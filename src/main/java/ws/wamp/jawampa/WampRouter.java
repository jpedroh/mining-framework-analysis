package ws.wamp.jawampa;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import ws.wamp.jawampa.WampMessages.*;
import ws.wamp.jawampa.internal.IdGenerator;
import ws.wamp.jawampa.internal.IdValidator;
import ws.wamp.jawampa.internal.RealmConfig;
import ws.wamp.jawampa.internal.UriValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The {@link WampRouter} provides Dealer and Broker functionality for the WAMP
 * protocol.<br>
 */
public class WampRouter {
  final static Set<WampRoles> SUPPORTED_CLIENT_ROLES;

  static {
    SUPPORTED_CLIENT_ROLES = new HashSet<WampRoles>();
    SUPPORTED_CLIENT_ROLES.add(WampRoles.Caller);
    SUPPORTED_CLIENT_ROLES.add(WampRoles.Callee);
    SUPPORTED_CLIENT_ROLES.add(WampRoles.Publisher);
    SUPPORTED_CLIENT_ROLES.add(WampRoles.Subscriber);
  }

  static class Realm {
    final RealmConfig config;

    final List<WampRouterHandler> channels = new ArrayList<WampRouterHandler>();

    final Map<String, Procedure> procedures = new HashMap<String, Procedure>();

    final Map<String, Subscription> subscriptionsByTopic = new HashMap<String, Subscription>();

    final Map<Long, Subscription> subscriptionsById = new HashMap<Long, Subscription>();

    long lastUsedSubscriptionId = IdValidator.MIN_VALID_ID;

    public Realm(RealmConfig config) {
      this.config = config;
    }

    void includeChannel(WampRouterHandler channel, long sessionId, Set<WampRoles> roles) {
      channels.add(channel);
      channel.realm = this;
      channel.sessionId = sessionId;
      channel.roles = roles;
    }

    void removeChannel(WampRouterHandler channel, boolean removeFromList) {
      if (channel.realm == null) {
        return;
      }
      if (channel.subscriptionsById != null) {
        for (Subscription sub : channel.subscriptionsById.values()) {
          sub.subscribers.remove(channel);
          if (sub.subscribers.isEmpty()) {
            subscriptionsByTopic.remove(sub.topic);
            subscriptionsById.remove(sub.subscriptionId);
          }
        }
        channel.subscriptionsById.clear();
        channel.subscriptionsById = null;
      }
      if (channel.providedProcedures != null) {
        for (Procedure proc : channel.providedProcedures.values()) {
          for (Invocation invoc : proc.pendingCalls) {
            if (invoc.caller.state != RouterHandlerState.Open) {
              continue;
            }
            ErrorMessage errMsg = new ErrorMessage(CallMessage.ID, invoc.callRequestId, null, ApplicationError.NO_SUCH_PROCEDURE, null, null);
            invoc.caller.ctx.writeAndFlush(errMsg);
          }
          proc.pendingCalls.clear();
          procedures.remove(proc.procName);
        }
        channel.providedProcedures = null;
        channel.pendingInvocations = null;
      }
      channel.realm = null;
      channel.roles.clear();
      channel.roles = null;
      channel.sessionId = 0;
      if (removeFromList) {
        channels.remove(channel);
      }
    }
  }

  static class Procedure {
    final String procName;

    final WampRouterHandler provider;

    final long registrationId;

    final List<Invocation> pendingCalls = new ArrayList<WampRouter.Invocation>();

    public Procedure(String name, WampRouterHandler provider, long registrationId) {
      this.procName = name;
      this.provider = provider;
      this.registrationId = registrationId;
    }
  }

  static class Invocation {
    Procedure procedure;

    long callRequestId;

    WampRouterHandler caller;

    long invocationRequestId;
  }

  static class Subscription {
    final String topic;

    final long subscriptionId;

    final Set<WampRouterHandler> subscribers;

    public Subscription(String topic, long subscriptionId) {
      this.topic = topic;
      this.subscriptionId = subscriptionId;
      this.subscribers = new HashSet<WampRouterHandler>();
    }
  }

  final EventLoopGroup eventLoop;

  final Scheduler scheduler;

  final ObjectMapper objectMapper = new ObjectMapper();

  boolean isDisposed = false;

  final Map<String, Realm> realms;

  final ChannelGroup idleChannels;

  /**
     * Returns the (singlethreaded) EventLoop on which this router is running.<br>
     * This is required by other Netty ChannelHandlers that want to forward messages
     * to the router.
     */
  public EventLoopGroup eventLoop() {
    return eventLoop;
  }

  /**
     * Returns the Jackson {@link ObjectMapper} that is used for JSON serialization,
     * deserialization and object mapping by this router.
     */
  public ObjectMapper objectMapper() {
    return objectMapper;
  }

  WampRouter(Map<String, RealmConfig> realms) {
    this.realms = new HashMap<String, Realm>();
    for (Map.Entry<String, RealmConfig> e : realms.entrySet()) {
      Realm info = new Realm(e.getValue());
      this.realms.put(e.getKey(), info);
    }
    this.eventLoop = new NioEventLoopGroup(1, new ThreadFactory() {
      @Override public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "WampRouterEventLoop");
        t.setDaemon(true);
        return t;
      }
    });
    this.scheduler = Schedulers.from(eventLoop);
    idleChannels = new DefaultChannelGroup(eventLoop.next());
  }

  /**
     * Closes the router.<br>
     * This will shut down all realm that are registered to the router.
     * All connections to clients on the realm will be closed.<br>
     * However pending calls will be completed through an error message
     * as far as possible.
     */
  public void close() {
    if (eventLoop.isShuttingDown() || eventLoop.isShutdown()) {
      return;
    }
    eventLoop.execute(new Runnable() {
      @Override public void run() {
        if (isDisposed) {
          return;
        }
        isDisposed = true;
        idleChannels.close();
        idleChannels.clear();
        for (Realm ri : realms.values()) {
          for (WampRouterHandler channel : ri.channels) {
            ri.removeChannel(channel, false);
            channel.markAsClosed();
            GoodbyeMessage goodbye = new GoodbyeMessage(null, ApplicationError.SYSTEM_SHUTDOWN);
            channel.ctx.writeAndFlush(goodbye).addListener(ChannelFutureListener.CLOSE);
          }
          ri.channels.clear();
        }
        eventLoop.shutdownGracefully();
      }
    });
  }

  public ChannelHandler createRouterHandler() {
    return new WampRouterHandler();
  }

  enum RouterHandlerState {
    Open,
    Closed
  }

  class WampRouterHandler extends SimpleChannelInboundHandler<WampMessage> {
    public RouterHandlerState state = RouterHandlerState.Open;

    ChannelHandlerContext ctx;

    long sessionId;

    Realm realm;

    Set<WampRoles> roles;

    /**
         * Procedures that this channel provides.<br>
         * Key is the registration ID, Value is the procedure
         */
    Map<Long, Procedure> providedProcedures;

    Map<Long, Invocation> pendingInvocations;

    /** The Set of subscriptions to which this channel is subscribed */
    Map<Long, Subscription> subscriptionsById;

    long lastUsedId = IdValidator.MIN_VALID_ID;

    void markAsClosed() {
      state = RouterHandlerState.Closed;
    }

    @Override public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
      if (state != RouterHandlerState.Open) {
        return;
      }
      if (isDisposed) {
        state = RouterHandlerState.Closed;
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
      } else {
        idleChannels.add(ctx.channel());
      }
    }

    @Override public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      if (isDisposed || state != RouterHandlerState.Open) {
        return;
      }
      markAsClosed();
      if (realm != null) {
        realm.removeChannel(this, true);
      } else {
        idleChannels.remove(ctx.channel());
      }
    }

    @Override protected void channelRead0(ChannelHandlerContext ctx, WampMessage msg) throws Exception {
      if (isDisposed || state != RouterHandlerState.Open) {
        return;
      }
      if (realm == null) {
        onMessageFromUnregisteredChannel(this, msg);
      } else {
        onMessageFromRegisteredChannel(this, msg);
      }
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if (isDisposed || state != RouterHandlerState.Open) {
        return;
      }
      if (realm != null) {
        closeActiveChannel(this, null);
      } else {
        closePassiveChannel(this);
      }
    }
  }

  private void onMessageFromRegisteredChannel(WampRouterHandler handler, WampMessage msg) {
    if (msg instanceof HelloMessage || msg instanceof WelcomeMessage) {
      closeActiveChannel(handler, new GoodbyeMessage(null, ApplicationError.INVALID_ARGUMENT));
    } else {
      if (msg instanceof AbortMessage || msg instanceof GoodbyeMessage) {
        handler.realm.removeChannel(handler, true);
        idleChannels.add(handler.ctx.channel());
        if (msg instanceof GoodbyeMessage) {
          GoodbyeMessage reply = new GoodbyeMessage(null, ApplicationError.GOODBYE_AND_OUT);
          handler.ctx.writeAndFlush(reply);
        }
      } else {
        if (msg instanceof CallMessage) {
          CallMessage call = (CallMessage) msg;
          String err = null;
          if (!UriValidator.tryValidate(call.procedure, handler.realm.config.useStrictUriValidation)) {
            err = ApplicationError.INVALID_URI;
          }
          if (err == null && !(IdValidator.isValidId(call.requestId))) {
            err = ApplicationError.INVALID_ARGUMENT;
          }
          Procedure proc = null;
          if (err == null) {
            proc = handler.realm.procedures.get(call.procedure);
            if (proc == null) {
              err = ApplicationError.NO_SUCH_PROCEDURE;
            }
          }
          if (err != null) {
            ErrorMessage errMsg = new ErrorMessage(CallMessage.ID, call.requestId, null, err, null, null);
            handler.ctx.writeAndFlush(errMsg);
            return;
          }
          Invocation invoc = new Invocation();
          invoc.callRequestId = call.requestId;
          invoc.caller = handler;
          invoc.procedure = proc;
          invoc.invocationRequestId = IdGenerator.newLinearId(proc.provider.lastUsedId, proc.provider.pendingInvocations);
          proc.provider.lastUsedId = invoc.invocationRequestId;
          proc.provider.pendingInvocations.put(invoc.invocationRequestId, invoc);
          proc.pendingCalls.add(invoc);
          InvocationMessage imsg = new InvocationMessage(invoc.invocationRequestId, proc.registrationId, null, call.arguments, call.argumentsKw);
          proc.provider.ctx.writeAndFlush(imsg);
        } else {
          if (msg instanceof YieldMessage) {
            YieldMessage yield = (YieldMessage) msg;
            if (!(IdValidator.isValidId(yield.requestId))) {
              return;
            }
            if (handler.pendingInvocations == null) {
              return;
            }
            Invocation invoc = handler.pendingInvocations.get(yield.requestId);
            if (invoc == null) {
              return;
            }
            handler.pendingInvocations.remove(yield.requestId);
            invoc.procedure.pendingCalls.remove(invoc);
            ResultMessage result = new ResultMessage(invoc.callRequestId, null, yield.arguments, yield.argumentsKw);
            invoc.caller.ctx.writeAndFlush(result);
          } else {
            if (msg instanceof ErrorMessage) {
              ErrorMessage err = (ErrorMessage) msg;
              if (!(IdValidator.isValidId(err.requestId))) {
                return;
              }
              if (err.requestType == InvocationMessage.ID) {
                if (!UriValidator.tryValidate(err.error, handler.realm.config.useStrictUriValidation)) {
                  closeActiveChannel(handler, new GoodbyeMessage(null, ApplicationError.INVALID_ARGUMENT));
                  return;
                }
                if (handler.pendingInvocations == null) {
                  return;
                }
                Invocation invoc = handler.pendingInvocations.get(err.requestId);
                if (invoc == null) {
                  return;
                }
                handler.pendingInvocations.remove(err.requestId);
                invoc.procedure.pendingCalls.remove(invoc);
                ErrorMessage fwdError = new ErrorMessage(CallMessage.ID, invoc.callRequestId, null, err.error, err.arguments, err.argumentsKw);
                invoc.caller.ctx.writeAndFlush(fwdError);
              }
            } else {
              if (msg instanceof RegisterMessage) {
                RegisterMessage reg = (RegisterMessage) msg;
                String err = null;
                if (!UriValidator.tryValidate(reg.procedure, handler.realm.config.useStrictUriValidation)) {
                  err = ApplicationError.INVALID_URI;
                }
                if (err == null && !(IdValidator.isValidId(reg.requestId))) {
                  err = ApplicationError.INVALID_ARGUMENT;
                }
                Procedure proc = null;
                if (err == null) {
                  proc = handler.realm.procedures.get(reg.procedure);
                  if (proc != null) {
                    err = ApplicationError.PROCEDURE_ALREADY_EXISTS;
                  }
                }
                if (err != null) {
                  ErrorMessage errMsg = new ErrorMessage(RegisterMessage.ID, reg.requestId, null, err, null, null);
                  handler.ctx.writeAndFlush(errMsg);
                  return;
                }
                long registrationId = IdGenerator.newLinearId(handler.lastUsedId, handler.providedProcedures);
                handler.lastUsedId = registrationId;
                Procedure procInfo = new Procedure(reg.procedure, handler, registrationId);
                handler.realm.procedures.put(reg.procedure, procInfo);
                if (handler.providedProcedures == null) {
                  handler.providedProcedures = new HashMap<Long, WampRouter.Procedure>();
                  handler.pendingInvocations = new HashMap<Long, WampRouter.Invocation>();
                }
                handler.providedProcedures.put(procInfo.registrationId, procInfo);
                RegisteredMessage response = new RegisteredMessage(reg.requestId, procInfo.registrationId);
                handler.ctx.writeAndFlush(response);
              } else {
                if (msg instanceof UnregisterMessage) {
                  UnregisterMessage unreg = (UnregisterMessage) msg;
                  String err = null;
                  if (!(IdValidator.isValidId(unreg.requestId)) || !(IdValidator.isValidId(unreg.registrationId))) {
                    err = ApplicationError.INVALID_ARGUMENT;
                  }
                  Procedure proc = null;
                  if (err == null) {
                    if (handler.providedProcedures != null) {
                      proc = handler.providedProcedures.get(unreg.registrationId);
                    }
                    if (proc == null) {
                      err = ApplicationError.NO_SUCH_REGISTRATION;
                    }
                  }
                  if (err != null) {
                    ErrorMessage errMsg = new ErrorMessage(UnregisterMessage.ID, unreg.requestId, null, err, null, null);
                    handler.ctx.writeAndFlush(errMsg);
                    return;
                  }
                  for (Invocation invoc : proc.pendingCalls) {
                    handler.pendingInvocations.remove(invoc.invocationRequestId);
                    if (invoc.caller.state == RouterHandlerState.Open) {
                      ErrorMessage errMsg = new ErrorMessage(CallMessage.ID, invoc.callRequestId, null, ApplicationError.NO_SUCH_PROCEDURE, null, null);
                      invoc.caller.ctx.writeAndFlush(errMsg);
                    }
                  }
                  proc.pendingCalls.clear();
                  handler.realm.procedures.remove(proc.procName);
                  handler.providedProcedures.remove(proc.registrationId);
                  if (handler.providedProcedures.size() == 0) {
                    handler.providedProcedures = null;
                    handler.pendingInvocations = null;
                  }
                  UnregisteredMessage response = new UnregisteredMessage(unreg.requestId);
                  handler.ctx.writeAndFlush(response);
                } else {
                  if (msg instanceof SubscribeMessage) {
                    SubscribeMessage sub = (SubscribeMessage) msg;
                    String err = null;
                    if (!UriValidator.tryValidate(sub.topic, handler.realm.config.useStrictUriValidation)) {
                      err = ApplicationError.INVALID_URI;
                    }
                    if (err == null && !(IdValidator.isValidId(sub.requestId))) {
                      err = ApplicationError.INVALID_ARGUMENT;
                    }
                    if (err != null) {
                      ErrorMessage errMsg = new ErrorMessage(SubscribeMessage.ID, sub.requestId, null, err, null, null);
                      handler.ctx.writeAndFlush(errMsg);
                      return;
                    }
                    if (handler.subscriptionsById == null) {
                      handler.subscriptionsById = new HashMap<Long, WampRouter.Subscription>();
                    }
                    Subscription subscription = handler.realm.subscriptionsByTopic.get(sub.topic);
                    if (subscription == null) {
                      long subscriptionId = IdGenerator.newLinearId(handler.realm.lastUsedSubscriptionId, handler.realm.subscriptionsById);
                      handler.realm.lastUsedSubscriptionId = subscriptionId;
                      subscription = new Subscription(sub.topic, subscriptionId);
                      handler.realm.subscriptionsByTopic.put(sub.topic, subscription);
                      handler.realm.subscriptionsById.put(subscriptionId, subscription);
                    }
                    if (subscription.subscribers.add(handler)) {
                      handler.subscriptionsById.put(subscription.subscriptionId, subscription);
                    }
                    SubscribedMessage response = new SubscribedMessage(sub.requestId, subscription.subscriptionId);
                    handler.ctx.writeAndFlush(response);
                  } else {
                    if (msg instanceof UnsubscribeMessage) {
                      UnsubscribeMessage unsub = (UnsubscribeMessage) msg;
                      String err = null;
                      if (!(IdValidator.isValidId(unsub.requestId)) || !(IdValidator.isValidId(unsub.subscriptionId))) {
                        err = ApplicationError.INVALID_ARGUMENT;
                      }
                      Subscription s = null;
                      if (err == null) {
                        if (handler.subscriptionsById != null) {
                          s = handler.subscriptionsById.get(unsub.subscriptionId);
                        }
                        if (s == null) {
                          err = ApplicationError.NO_SUCH_SUBSCRIPTION;
                        }
                      }
                      if (err != null) {
                        ErrorMessage errMsg = new ErrorMessage(UnsubscribeMessage.ID, unsub.requestId, null, err, null, null);
                        handler.ctx.writeAndFlush(errMsg);
                        return;
                      }
                      s.subscribers.remove(handler);
                      handler.subscriptionsById.remove(s.subscriptionId);
                      if (handler.subscriptionsById.isEmpty()) {
                        handler.subscriptionsById = null;
                      }
                      if (s.subscribers.isEmpty()) {
                        handler.realm.subscriptionsByTopic.remove(s.topic);
                        handler.realm.subscriptionsById.remove(s.subscriptionId);
                      }
                      UnsubscribedMessage response = new UnsubscribedMessage(unsub.requestId);
                      handler.ctx.writeAndFlush(response);
                    } else {
                      if (msg instanceof PublishMessage) {
                        PublishMessage pub = (PublishMessage) msg;
                        boolean sendAcknowledge = false;
                        JsonNode ackOption = pub.options.get("acknowledge");
                        if (ackOption != null && ackOption.asBoolean() == true) {
                          sendAcknowledge = true;
                        }
                        String err = null;
                        if (!UriValidator.tryValidate(pub.topic, handler.realm.config.useStrictUriValidation)) {
                          err = ApplicationError.INVALID_URI;
                        }
                        if (err == null && !(IdValidator.isValidId(pub.requestId))) {
                          err = ApplicationError.INVALID_ARGUMENT;
                        }
                        if (err != null) {
                          ErrorMessage errMsg = new ErrorMessage(PublishMessage.ID, pub.requestId, null, err, null, null);
                          if (sendAcknowledge) {
                            handler.ctx.writeAndFlush(errMsg);
                          }
                          return;
                        }
                        long publicationId = IdGenerator.newRandomId(null);
                        Subscription subscription = handler.realm.subscriptionsByTopic.get(pub.topic);
                        if (subscription != null) {
                          for (WampRouterHandler receiver : subscription.subscribers) {
                            if (receiver == handler) {
                              boolean skipPublisher = true;
                              if (pub.options != null) {
                                JsonNode excludeMeNode = pub.options.get("exclude_me");
                                if (excludeMeNode != null) {
                                  skipPublisher = excludeMeNode.asBoolean();
                                }
                              }
                              if (skipPublisher) {
                                continue;
                              }
                            }
                            EventMessage ev = new EventMessage(subscription.subscriptionId, publicationId, null, pub.arguments, pub.argumentsKw);
                            receiver.ctx.writeAndFlush(ev);
                          }
                        }
                        if (sendAcknowledge) {
                          PublishedMessage response = new PublishedMessage(pub.requestId, publicationId);
                          handler.ctx.writeAndFlush(response);
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void onMessageFromUnregisteredChannel(WampRouterHandler channelHandler, WampMessage msg) {
    if (!(msg instanceof HelloMessage)) {
      closePassiveChannel(channelHandler);
      return;
    }
    HelloMessage hello = (HelloMessage) msg;
    String errorMsg = null;
    Realm realm = null;
    if (!UriValidator.tryValidate(hello.realm, false)) {
      errorMsg = ApplicationError.INVALID_URI;
    } else {
      realm = realms.get(hello.realm);
      if (realm == null) {
        errorMsg = ApplicationError.NO_SUCH_REALM;
      }
    }
    if (errorMsg != null) {
      AbortMessage abort = new AbortMessage(null, errorMsg);
      channelHandler.ctx.writeAndFlush(abort);
      return;
    }
    Set<WampRoles> roles = new HashSet<WampRoles>();
    boolean hasUnsupportedRoles = false;
    JsonNode n = hello.details.get("roles");
    if (n != null && n.isObject()) {
      ObjectNode rolesNode = (ObjectNode) n;
      Iterator<String> roleKeys = rolesNode.fieldNames();
      while (roleKeys.hasNext()) {
        WampRoles role = WampRoles.fromString(roleKeys.next());
        if (!SUPPORTED_CLIENT_ROLES.contains(role)) {
          hasUnsupportedRoles = true;
        }
        if (role != null) {
          roles.add(role);
        }
      }
    }
    if (roles.size() == 0 || hasUnsupportedRoles) {
      AbortMessage abort = new AbortMessage(null, ApplicationError.NO_SUCH_ROLE);
      channelHandler.ctx.writeAndFlush(abort);
      return;
    }
    long sessionId = IdGenerator.newRandomId(null);
    realm.includeChannel(channelHandler, sessionId, roles);
    idleChannels.remove(channelHandler.ctx.channel());
    ObjectNode welcomeDetails = objectMapper.createObjectNode();
    ObjectNode routerRoles = welcomeDetails.putObject("roles");
    for (WampRoles role : realm.config.roles) {
      ObjectNode roleNode = routerRoles.putObject(role.toString());
      if (role == WampRoles.Publisher) {
        ObjectNode featuresNode = roleNode.putObject("features");
        featuresNode.put("publisher_exclusion", true);
      }
    }
    WelcomeMessage welcome = new WelcomeMessage(channelHandler.sessionId, welcomeDetails);
    channelHandler.ctx.writeAndFlush(welcome);
  }

  private void closeActiveChannel(WampRouterHandler channel, WampMessage closeMessage) {
    if (channel == null) {
      return;
    }
    channel.realm.removeChannel(channel, true);
    channel.markAsClosed();
    if (channel.ctx != null) {
      Object m = (closeMessage == null) ? Unpooled.EMPTY_BUFFER : closeMessage;
      channel.ctx.writeAndFlush(m).addListener(ChannelFutureListener.CLOSE);
    }
  }

  private void closePassiveChannel(WampRouterHandler channelHandler) {
    idleChannels.remove(channelHandler.ctx.channel());
    channelHandler.markAsClosed();
    channelHandler.ctx.close();
  }
}