package org.omnifaces.renderkit;
import static org.omnifaces.util.Components.getCurrentComponent;
import static org.omnifaces.util.Faces.getInitParameter;
import static org.omnifaces.util.Utils.isEmpty;
import static org.omnifaces.util.Utils.isOneInstanceOf;
import static org.omnifaces.util.Utils.unmodifiableSet;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitWrapper;

public @Deprecated class Html5RenderKit extends RenderKitWrapper {
  public static final String PARAM_NAME_PASSTHROUGH_ATTRIBUTES = "org.omnifaces.HTML5_RENDER_KIT_PASSTHROUGH_ATTRIBUTES";

  private static final Set<String> HTML5_UIFORM_ATTRIBUTES = unmodifiableSet("autocomplete");

  private static final Set<String> HTML5_SELECT_ATTRIBUTES = unmodifiableSet("autofocus");

  private static final Set<String> HTML5_TEXTAREA_ATTRIBUTES = unmodifiableSet("autofocus", "maxlength", "placeholder", "spellcheck", "wrap");

  private static final Set<String> HTML5_INPUT_ATTRIBUTES = unmodifiableSet("autofocus", "list", "pattern", "placeholder", "spellcheck");

  private static final Set<String> HTML5_INPUT_PASSWORD_ATTRIBUTES = unmodifiableSet("autofocus", "pattern", "placeholder");

  private static final Set<String> HTML5_INPUT_RANGE_ATTRIBUTES = unmodifiableSet("max", "min", "step");

  private static final Set<String> HTML5_INPUT_RANGE_TYPES = unmodifiableSet("range", "number", "date");

  private static final Set<String> HTML5_INPUT_TYPES = unmodifiableSet("text", "search", "email", "url", "tel", HTML5_INPUT_RANGE_TYPES);

  private static final Set<String> HTML5_BUTTON_ATTRIBUTES = unmodifiableSet("autofocus");

  private static final String ERROR_INVALID_INIT_PARAM = "Context parameter \'" + PARAM_NAME_PASSTHROUGH_ATTRIBUTES + "\' is in invalid syntax.";

  private static final String ERROR_INVALID_INIT_PARAM_CLASS = "Context parameter \'" + PARAM_NAME_PASSTHROUGH_ATTRIBUTES + "\'" + " references a class which is not found in runtime classpath: \'%s\'";

  private static final String ERROR_UNSUPPORTED_HTML5_INPUT_TYPE = "HtmlInputText type \'%s\' is not supported. Supported types are " + HTML5_INPUT_TYPES + ".";

  private RenderKit wrapped;

  private Map<Class<UIComponent>, Set<String>> passthroughAttributes;

  public Html5RenderKit(RenderKit wrapped) {
    this.wrapped = wrapped;
    passthroughAttributes = initPassthroughAttributes();
  }

  @Override public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
    return new Html5ResponseWriter(super.createResponseWriter(writer, contentTypeList, characterEncoding));
  }

  @Override public RenderKit getWrapped() {
    return wrapped;
  }

  @SuppressWarnings(value = { "unchecked" }) private static Map<Class<UIComponent>, Set<String>> initPassthroughAttributes() {
    String passthroughAttributesParam = getInitParameter(PARAM_NAME_PASSTHROUGH_ATTRIBUTES);
    if (isEmpty(passthroughAttributesParam)) {
      return null;
    }
    Map<Class<UIComponent>, Set<String>> passthroughAttributes = new HashMap<Class<UIComponent>, Set<String>>();
    for (String passthroughAttribute : passthroughAttributesParam.split("\\s*;\\s*")) {
      String[] classAndAttributeNames = passthroughAttribute.split("\\s*=\\s*", 2);
      if (classAndAttributeNames.length != 2) {
        throw new IllegalArgumentException(ERROR_INVALID_INIT_PARAM);
      }
      String className = classAndAttributeNames[0];
      Object[] attributeNames = classAndAttributeNames[1].split("\\s*,\\s*");
      Set<String> attributeNameSet = unmodifiableSet(attributeNames);
      try {
        passthroughAttributes.put((Class<UIComponent>) Class.forName(className), attributeNameSet);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(String.format(ERROR_INVALID_INIT_PARAM_CLASS, className), e);
      }
    }
    return passthroughAttributes;
  }

  class Html5ResponseWriter extends ResponseWriterWrapper {
    private ResponseWriter wrapped;

    public Html5ResponseWriter(ResponseWriter wrapped) {
      this.wrapped = wrapped;
    }

    @Override public ResponseWriter cloneWithWriter(Writer writer) {
      return new Html5ResponseWriter(super.cloneWithWriter(writer));
    }

    @Override public void startElement(String name, UIComponent component) throws IOException {
      super.startElement(name, component);
      if (component == null) {
        return;
      }
      if (component instanceof UIForm && "form".equals(name)) {
        writeHtml5AttributesIfNecessary(component.getAttributes(), HTML5_UIFORM_ATTRIBUTES);
      } else {
        if (component instanceof UIInput) {
          writeHtml5AttributesIfNecessary((UIInput) component, name);
        } else {
          if (component instanceof UICommand && "input".equals(name)) {
            writeHtml5AttributesIfNecessary(component.getAttributes(), HTML5_BUTTON_ATTRIBUTES);
          }
        }
      }
      if (passthroughAttributes != null) {
        for (Entry<Class<UIComponent>, Set<String>> entry : passthroughAttributes.entrySet()) {
          if (entry.getKey().isInstance(component)) {
            writeHtml5AttributesIfNecessary(component.getAttributes(), entry.getValue());
          }
        }
      }
    }

    @Override public void writeAttribute(String name, Object value, String property) throws IOException {
      if ("type".equals(name) && "text".equals(value)) {
        UIComponent component = getCurrentComponent();
        if (component instanceof HtmlInputText) {
          Object type = component.getAttributes().get("type");
          if (type != null) {
            if (HTML5_INPUT_TYPES.contains(type)) {
              super.writeAttribute(name, type, null);
              return;
            } else {
              throw new IllegalArgumentException(String.format(ERROR_UNSUPPORTED_HTML5_INPUT_TYPE, type));
            }
          }
        }
      }
      super.writeAttribute(name, value, property);
    }

    @Override public ResponseWriter getWrapped() {
      return wrapped;
    }

    private void writeHtml5AttributesIfNecessary(UIInput component, String name) throws IOException {
      if (isInput(component, name)) {
        Map<String, Object> attributes = component.getAttributes();
        writeHtml5AttributesIfNecessary(attributes, HTML5_INPUT_ATTRIBUTES);
        if (HTML5_INPUT_RANGE_TYPES.contains(attributes.get("type"))) {
          writeHtml5AttributesIfNecessary(attributes, HTML5_INPUT_RANGE_ATTRIBUTES);
        }
      } else {
        if (isInputPassword(component, name)) {
          writeHtml5AttributesIfNecessary(component.getAttributes(), HTML5_INPUT_PASSWORD_ATTRIBUTES);
        } else {
          if (isTextarea(component, name)) {
            writeHtml5AttributesIfNecessary(component.getAttributes(), HTML5_TEXTAREA_ATTRIBUTES);
          } else {
            if (isSelect(component, name)) {
              writeHtml5AttributesIfNecessary(component.getAttributes(), HTML5_SELECT_ATTRIBUTES);
            }
          }
        }
      }
    }

    private void writeHtml5AttributesIfNecessary(Map<String, Object> attributes, Set<String> names) throws IOException {
      for (String name : names) {
        Object value = attributes.get(name);
        if (value != null) {
          super.writeAttribute(name, value, null);
        }
      }
    }

    private boolean isInput(UIInput component, String name) {
      return component instanceof HtmlInputText && "input".equals(name);
    }

    private boolean isInputPassword(UIInput component, String name) {
      return component instanceof HtmlInputSecret && "input".equals(name);
    }

    private boolean isTextarea(UIInput component, String name) {
      return component instanceof HtmlInputTextarea && "textarea".equals(name);
    }

    private boolean isSelect(UIInput component, String name) {
      return isOneInstanceOf(component.getClass(), UISelectBoolean.class, UISelectOne.class, UISelectMany.class) && ("input".equals(name) || "select".equals(name));
    }
  }
}