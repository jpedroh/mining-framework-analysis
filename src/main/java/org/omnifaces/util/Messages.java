package org.omnifaces.util;
import static org.omnifaces.util.Faces.getContext;
import static org.omnifaces.util.Faces.getFlash;
import java.text.MessageFormat;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.convert.ConverterException;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContextListener;

public final class Messages {
  private static final String ERROR_RESOLVER_ALREADY_SET = "The resolver can be set only once.";

  public interface Resolver {
    String getMessage(String message, Object... params);
  }

  private static final Resolver DEFAULT_RESOLVER = new Resolver() {
    @Override public String getMessage(String message, Object... params) {
      return MessageFormat.format(message, params);
    }
  };

  private static Resolver resolver = DEFAULT_RESOLVER;

  public static void setResolver(Resolver resolver) {
    if (Messages.resolver == DEFAULT_RESOLVER) {
      Messages.resolver = resolver;
    } else {
      throw new IllegalStateException(ERROR_RESOLVER_ALREADY_SET);
    }
  }

  private Messages() {
  }

  public static Message create(String message, Object... params) {
    return new Message(createInfo(message, params));
  }

  public static final class Message {
    private FacesMessage message;

    private Message(FacesMessage message) {
      this.message = message;
    }

    public Message detail(String detail, Object... params) {
      message.setDetail(resolver.getMessage(detail, params));
      return this;
    }

    public Message warn() {
      message.setSeverity(FacesMessage.SEVERITY_WARN);
      return this;
    }

    public Message error() {
      message.setSeverity(FacesMessage.SEVERITY_ERROR);
      return this;
    }

    public Message fatal() {
      message.setSeverity(FacesMessage.SEVERITY_FATAL);
      return this;
    }

    public Message flash() {
      getFlash().setKeepMessages(true);
      return this;
    }

    public void add(String clientId) {
      Messages.add(clientId, message);
    }

    public void add() {
      Messages.addGlobal(message);
    }

    public FacesMessage get() {
      return message;
    }
  }

  public static FacesMessage create(FacesMessage.Severity severity, String message, Object... params) {
    return new FacesMessage(severity, resolver.getMessage(message, params), null);
  }

  public static FacesMessage createInfo(String message, Object... params) {
    return create(FacesMessage.SEVERITY_INFO, message, params);
  }

  public static FacesMessage createWarn(String message, Object... params) {
    return create(FacesMessage.SEVERITY_WARN, message, params);
  }

  public static FacesMessage createError(String message, Object... params) {
    return create(FacesMessage.SEVERITY_ERROR, message, params);
  }

  public static FacesMessage createFatal(String message, Object... params) {
    return create(FacesMessage.SEVERITY_FATAL, message, params);
  }

  public static void add(String clientId, FacesMessage message) {
    getContext().addMessage(clientId, message);
  }

  public static void add(FacesMessage.Severity severity, String clientId, String message, Object... params) {
    add(clientId, create(severity, message, params));
  }

  public static void addInfo(String clientId, String message, Object... params) {
    add(clientId, createInfo(message, params));
  }

  public static void addWarn(String clientId, String message, Object... params) {
    add(clientId, createWarn(message, params));
  }

  public static void addError(String clientId, String message, Object... params) {
    add(clientId, createError(message, params));
  }

  public static void addFatal(String clientId, String message, Object... params) {
    add(clientId, createFatal(message, params));
  }

  public static void addGlobal(FacesMessage message) {
    add(null, message);
  }

  public static void addGlobal(FacesMessage.Severity severity, String message, Object... params) {
    addGlobal(create(severity, message, params));
  }

  public static void addGlobalInfo(String message, Object... params) {
    addGlobal(createInfo(message, params));
  }

  public static void addGlobalWarn(String message, Object... params) {
    addGlobal(createWarn(message, params));
  }

  public static void addGlobalError(String message, Object... params) {
    addGlobal(createError(message, params));
  }

  public static void addGlobalFatal(String message, Object... params) {
    addGlobal(createFatal(message, params));
  }

  public static void addFlash(String clientId, FacesMessage message) {
    getFlash().setKeepMessages(true);
    add(clientId, message);
  }

  public static void addFlash(FacesMessage.Severity severity, String clientId, String message, Object... params) {
    addFlash(clientId, create(severity, message, params));
  }

  public static void addFlashInfo(String clientId, String message, Object... params) {
    addFlash(clientId, createInfo(message, params));
  }

  public static void addFlashWarn(String clientId, String message, Object... params) {
    addFlash(clientId, createWarn(message, params));
  }

  public static void addFlashError(String clientId, String message, Object... params) {
    addFlash(clientId, createError(message, params));
  }

  public static void addFlashFatal(String clientId, String message, Object... params) {
    addFlash(clientId, createFatal(message, params));
  }

  public static void addFlashGlobal(FacesMessage message) {
    addFlash(null, message);
  }

  public static void addFlashGlobalInfo(String message, Object... params) {
    addFlashGlobal(createInfo(message, params));
  }

  public static void addFlashGlobalWarn(String message, Object... params) {
    addFlashGlobal(createWarn(message, params));
  }

  public static void addFlashGlobalError(String message, Object... params) {
    addFlashGlobal(createError(message, params));
  }

  public static void addFlashGlobalFatal(String message, Object... params) {
    addFlashGlobal(createFatal(message, params));
  }

  public static boolean isEmpty() {
    return getContext().getMessageList().isEmpty();
  }

  public static boolean isEmpty(String clientId) {
    return getContext().getMessageList(clientId).isEmpty();
  }

  public static boolean isGlobalEmpty() {
    return isEmpty(null);
  }
}