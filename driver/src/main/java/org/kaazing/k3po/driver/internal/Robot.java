package org.kaazing.k3po.driver.internal;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.channel.Channels.pipelineFactory;
import static org.jboss.netty.util.CharsetUtil.UTF_8;
import static org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory.newBootstrapFactory;
import static org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory.newChannelAddressFactory;
import static org.kaazing.k3po.lang.internal.RegionInfo.newSequential;
import java.io.ByteArrayInputStream;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.kaazing.k3po.driver.internal.behavior.Barrier;
import org.kaazing.k3po.driver.internal.behavior.Configuration;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgress;
import org.kaazing.k3po.driver.internal.behavior.ScriptProgressException;
import org.kaazing.k3po.driver.internal.behavior.handler.CompletionHandler;
import org.kaazing.k3po.driver.internal.behavior.parser.Parser;
import org.kaazing.k3po.driver.internal.behavior.parser.ScriptValidator;
import org.kaazing.k3po.driver.internal.behavior.visitor.GenerateConfigurationVisitor;
import org.kaazing.k3po.driver.internal.netty.bootstrap.BootstrapFactory;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ClientBootstrap;
import org.kaazing.k3po.driver.internal.netty.bootstrap.ServerBootstrap;
import org.kaazing.k3po.driver.internal.netty.channel.ChannelAddressFactory;
import org.kaazing.k3po.driver.internal.netty.channel.CompositeChannelFuture;
import org.kaazing.k3po.driver.internal.resolver.ClientBootstrapResolver;
import org.kaazing.k3po.driver.internal.resolver.ServerBootstrapResolver;
import org.kaazing.k3po.lang.internal.RegionInfo;
import org.kaazing.k3po.lang.internal.ast.AstScriptNode;
import org.kaazing.k3po.lang.internal.parser.ScriptParser;

public class Robot {
  private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(Robot.class);

  private final List<ChannelFuture> bindFutures = new ArrayList<>();

  private final List<ChannelFuture> connectFutures = new ArrayList<>();

  private final Channel channel = new DefaultLocalClientChannelFactory().newChannel(pipeline(new SimpleChannelHandler()));

  private final ChannelFuture startedFuture = Channels.future(channel);

  private final ChannelFuture abortedFuture = Channels.future(channel);

  private final ChannelFuture finishedFuture = Channels.future(channel);

  private final ChannelFuture disposedFuture = Channels.future(channel);

  private final DefaultChannelGroup serverChannels = new DefaultChannelGroup();

  private final DefaultChannelGroup clientChannels = new DefaultChannelGroup();

  private Configuration configuration;

  private ChannelFuture preparedFuture;

  private final ChannelAddressFactory addressFactory;

  private final BootstrapFactory bootstrapFactory;

  private ScriptProgress progress;

  private final ChannelHandler closeOnExceptionHandler = new CloseOnExceptionHandler();

  private final ConcurrentMap<String, Barrier> barriersByName = new ConcurrentHashMap<String, Barrier>();

  public Robot() {
    this.addressFactory = newChannelAddressFactory();
    this.bootstrapFactory = newBootstrapFactory(Collections.<Class<?>, Object>singletonMap(ChannelAddressFactory.class, addressFactory));
    ChannelFutureListener stopConfigurationListener = createStopConfigurationListener();
    this.abortedFuture.addListener(stopConfigurationListener);
    this.finishedFuture.addListener(stopConfigurationListener);
  }

  public ChannelFuture getPreparedFuture() {
    return preparedFuture;
  }

  public ChannelFuture getStartedFuture() {
    return startedFuture;
  }

  public ChannelFuture prepare(String expectedScript) throws Exception {
    if (preparedFuture != null) {
      throw new IllegalStateException("Script already prepared");
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Expected script:\n" + expectedScript);
    }
    final ScriptParser parser = new Parser();
    AstScriptNode scriptAST = parser.parse(new ByteArrayInputStream(expectedScript.getBytes(UTF_8)));
    final ScriptValidator validator = new ScriptValidator();
    validator.validate(scriptAST);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Parsed script:\n" + scriptAST);
    }
    RegionInfo scriptInfo = scriptAST.getRegionInfo();
    progress = new ScriptProgress(scriptInfo, expectedScript);
    final GenerateConfigurationVisitor visitor = new GenerateConfigurationVisitor(bootstrapFactory, addressFactory);
    configuration = scriptAST.accept(visitor, new GenerateConfigurationVisitor.State(barriersByName));
    preparedFuture = prepareConfiguration();
    return preparedFuture;
  }

  ChannelFuture prepareAndStart(String script) throws Exception {
    ChannelFuture preparedFuture = prepare(script);
    preparedFuture.addListener(new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture future) throws Exception {
        start();
      }
    });
    return startedFuture;
  }

  public ChannelFuture start() throws Exception {
    if (preparedFuture == null || !preparedFuture.isDone()) {
      throw new IllegalStateException("Script has not been prepared or is still preparing");
    } else {
      if (startedFuture.isDone()) {
        throw new IllegalStateException("Script has already been started");
      }
    }
    preparedFuture.addListener(new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture future) throws Exception {
        try {
          startConfiguration();
          startedFuture.setSuccess();
        } catch (Exception ex) {
          startedFuture.setFailure(ex);
        }
      }
    });
    return startedFuture;
  }

  public ChannelFuture abort() {
    abortedFuture.setSuccess();
    return finishedFuture;
  }

  public ChannelFuture finish() {
    return finishedFuture;
  }

  public String getObservedScript() {
    return (progress != null) ? progress.getObservedScript() : null;
  }

  public ChannelFuture dispose() {
    if (preparedFuture == null) {
      disposedFuture.setSuccess();
    } else {
      if (!disposedFuture.isDone()) {
        ChannelFuture future = abort();
        future.addListener(new ChannelFutureListener() {
          @Override public void operationComplete(ChannelFuture future) throws Exception {
            serverChannels.close().addListener(new ChannelGroupFutureListener() {
              @Override public void operationComplete(ChannelGroupFuture future) throws Exception {
                clientChannels.close();
                try {
                  bootstrapFactory.shutdown();
                  bootstrapFactory.releaseExternalResources();
                } catch (Exception e) {
                  if (LOGGER.isDebugEnabled()) {
                    LOGGER.error("Caught exception releasing resources", e);
                  }
                } finally {
                  disposedFuture.setSuccess();
                }
              }
            });
          }
        });
      }
    }
    return disposedFuture;
  }

  private ChannelFuture prepareConfiguration() throws Exception {
    List<ChannelFuture> completionFutures = new ArrayList<>();
    ChannelFutureListener streamCompletionListener = createStreamCompletionListener();
    for (ChannelPipeline pipeline : configuration.getClientAndServerPipelines()) {
      CompletionHandler completionHandler = pipeline.get(CompletionHandler.class);
      ChannelFuture completionFuture = completionHandler.getHandlerFuture();
      completionFutures.add(completionFuture);
      completionFuture.addListener(streamCompletionListener);
    }
    ChannelFuture executionFuture = new CompositeChannelFuture<>(channel, completionFutures);
    ChannelFutureListener executionListener = createScriptCompletionListener();
    executionFuture.addListener(executionListener);
    return prepareServers();
  }

  private ChannelFuture prepareServers() throws Exception {
    for (ServerBootstrapResolver serverResolver : configuration.getServerResolvers()) {
      ServerBootstrap server = serverResolver.resolve();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Binding to address " + server.getOption("localAddress"));
      }
      server.setParentHandler(new SimpleChannelHandler() {
        @Override public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
          clientChannels.add(e.getChildChannel());
        }

        @Override public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
          Channel channel = ctx.getChannel();
          channel.close();
        }
      });
      ChannelFuture bindFuture = server.bindAsync();
      serverChannels.add(bindFuture.getChannel());
      bindFutures.add(bindFuture);
      RegionInfo regionInfo = (RegionInfo) server.getOption("regionInfo");
      bindFuture.addListener(createBindCompleteListener(regionInfo, serverResolver.getNotifyBarrier()));
    }
    return new CompositeChannelFuture<>(channel, bindFutures);
  }

  private void startConfiguration() throws Exception {
    for (final ClientBootstrapResolver clientResolver : configuration.getClientResolvers()) {
      Barrier awaitBarrier = clientResolver.getAwaitBarrier();
      if (awaitBarrier != null) {
        awaitBarrier.getFuture().addListener(new ChannelFutureListener() {
          @Override public void operationComplete(ChannelFuture future) throws Exception {
            connectClient(clientResolver);
          }
        });
      } else {
        connectClient(clientResolver);
      }
    }
  }

  private void connectClient(ClientBootstrapResolver clientResolver) throws Exception {
    final RegionInfo regionInfo = clientResolver.getRegionInfo();
    ClientBootstrap client = clientResolver.resolve();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("[id:           ] connect " + client.getOption("remoteAddress"));
    }
    ChannelFuture connectFuture = client.connect();
    connectFutures.add(connectFuture);
    clientChannels.add(connectFuture.getChannel());
    connectFuture.addListener(createConnectCompleteListener(regionInfo));
  }

  private void stopConfiguration() throws Exception {
    if (configuration == null) {
      if (progress == null) {
        progress = new ScriptProgress(newSequential(0, 0), "");
      }
      RegionInfo scriptInfo = progress.getScriptInfo();
      progress.addScriptFailure(scriptInfo);
    } else {
      for (ServerBootstrapResolver serverResolver : configuration.getServerResolvers()) {
        ServerBootstrap server = serverResolver.resolve();
        server.setPipelineFactory(pipelineFactory(pipeline(closeOnExceptionHandler)));
      }
      for (ClientBootstrapResolver clientResolver : configuration.getClientResolvers()) {
        ClientBootstrap client = clientResolver.resolve();
        client.setPipelineFactory(pipelineFactory(pipeline(closeOnExceptionHandler)));
      }
      for (ChannelPipeline pipeline : configuration.getClientAndServerPipelines()) {
        stopStream(pipeline);
      }
      for (ChannelFuture bindFuture : bindFutures) {
        bindFuture.cancel();
      }
      for (ChannelFuture connectFuture : connectFutures) {
        connectFuture.cancel();
      }
      for (AutoCloseable resource : configuration.getResources()) {
        try {
          resource.close();
        } catch (Exception e) {
        }
      }
    }
  }

  private void stopStream(final ChannelPipeline pipeline) {
    if (pipeline.isAttached()) {
      pipeline.execute(new Runnable() {
        @Override public void run() {
          stopStreamAligned(pipeline);
        }
      });
    } else {
      stopStreamAligned(pipeline);
    }
  }

  private void stopStreamAligned(final ChannelPipeline pipeline) {
    for (ChannelHandler handler : pipeline.toMap().values()) {
      pipeline.remove(handler);
    }
    if (pipeline.getContext(closeOnExceptionHandler) == null) {
      pipeline.addLast("closeOnException", closeOnExceptionHandler);
    }
  }

  private ChannelFutureListener createBindCompleteListener(final RegionInfo regionInfo, final Barrier notifyBarrier) {
    return new ChannelFutureListener() {
      @Override public void operationComplete(final ChannelFuture bindFuture) throws Exception {
        Channel boundChannel = bindFuture.getChannel();
        SocketAddress localAddress = boundChannel.getLocalAddress();
        if (bindFuture.isSuccess()) {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Successfully bound to " + localAddress);
          }
          if (notifyBarrier != null) {
            ChannelFuture barrierFuture = notifyBarrier.getFuture();
            barrierFuture.setSuccess();
          }
        } else {
          LOGGER.error("Failed to bind to " + localAddress);
          Throwable cause = bindFuture.getCause();
          String message = format("accept failed: %s", cause.getMessage());
          progress.addScriptFailure(regionInfo, message);
          List<ChannelPipeline> acceptedPipelines = configuration.getServerPipelines(regionInfo);
          for (ChannelPipeline acceptedPipeline : acceptedPipelines) {
            stopStream(acceptedPipeline);
          }
        }
      }
    };
  }

  private ChannelFutureListener createConnectCompleteListener(final RegionInfo regionInfo) {
    return new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture connectFuture) throws Exception {
        if (connectFuture.isCancelled()) {
          progress.addScriptFailure(regionInfo, "");
        } else {
          if (!connectFuture.isSuccess()) {
            Throwable cause = connectFuture.getCause();
            String message = format("connect failed: %s", cause.getMessage());
            progress.addScriptFailure(regionInfo, message);
          }
        }
      }
    };
  }

  private ChannelFutureListener createStreamCompletionListener() {
    return new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture completionFuture) throws Exception {
        if (!completionFuture.isSuccess()) {
          Throwable cause = completionFuture.getCause();
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Unexpected exception: " + cause + " " + cause.getMessage());
          }
          if (cause instanceof ScriptProgressException) {
            ScriptProgressException exception = (ScriptProgressException) cause;
            progress.addScriptFailure(exception.getRegionInfo(), exception.getMessage());
          } else {
            LOGGER.warn("Unexpected exception", cause);
          }
        }
      }
    };
  }

  private ChannelFutureListener createScriptCompletionListener() {
    ChannelFutureListener executionListener = new ChannelFutureListener() {
      @Override public void operationComplete(final ChannelFuture future) throws Exception {
        if (LOGGER.isDebugEnabled()) {
          String observedScript = progress.getObservedScript();
          LOGGER.debug("Observed script:\n" + observedScript);
        }
        if (abortedFuture.isDone()) {
          finishedFuture.setSuccess();
        } else {
          finishedFuture.setSuccess();
        }
      }
    };
    return executionListener;
  }

  private ChannelFutureListener createStopConfigurationListener() {
    return new ChannelFutureListener() {
      @Override public void operationComplete(ChannelFuture future) throws Exception {
        stopConfiguration();
      }
    };
  }

  @Sharable private static final class CloseOnExceptionHandler extends SimpleChannelHandler {
    @Override public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
      if (TRUE != ctx.getAttachment()) {
        ctx.setAttachment(TRUE);
        Channel channel = ctx.getChannel();
        channel.close();
      } else {
        super.exceptionCaught(ctx, e);
      }
    }
  }

  public Map<String, Barrier> getBarriersByName() {
    return barriersByName;
  }

  public void notifyBarrier(String barrierName) throws Exception {
    final Barrier barrier = barriersByName.get(barrierName);
    if (barrier == null) {
      throw new Exception("Can not notify a barrier that does not exist in the script: " + barrierName);
    }
    barrier.getFuture().setSuccess();
  }

  public ChannelFuture awaitBarrier(String barrierName) throws Exception {
    final Barrier barrier = barriersByName.get(barrierName);
    if (barrier == null) {
      throw new Exception("Can not notify a barrier that does not exist in the script: " + barrierName);
    }
    return barrier.getFuture();
  }
}