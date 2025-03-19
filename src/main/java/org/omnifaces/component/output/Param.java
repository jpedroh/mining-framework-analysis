package org.omnifaces.component.output;
import static org.omnifaces.util.FacesLocal.createConverter;
import java.io.IOException;
import java.io.StringWriter;
import javax.faces.FacesException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import org.omnifaces.component.ParamHolder;


<<<<<<< /usr/src/app/output/omnifaces/omnifaces/05abae8be2b4e35b6c7a849b68ad6af6b07d4310/src/main/java/org/omnifaces/component/output/Param.java/left.java
/**
 * <p>
 * The	<code>&lt;o:param&gt;</code> is a component that extends the standard {@link UIParameter} to implement {@link ValueHolder}
 * and thus support a {@link Converter} to convert the supplied value to string, if necessary.
 * <p>
 * You can use it the same way as <code>&lt;f:param&gt;</code>, you only need to change <code>f:</code> into
 * <code>o:</code> to get the extra support for a {@link Converter} by usual means via the <code>converter</code>
 * attribute of the tag, or the nested <code>&lt;f:converter&gt;</code> tag, or just automatically if a converter is
 * already registered for the target class via <code>@FacesConverter(forClass)</code>.
 * <p>
 * Also, if no value is specified, but children are present, then the encoded output of children will be returned as
 * param value. This is useful when you want to supply JSF components or HTML as parameter of an unescaped
 * <code>&lt;h:outputFormat&gt;</code>. For example,
 * <pre>
 * &lt;h:outputFormat value="#{bundle.paragraph}" escape="false"&gt;
 *     &lt;o:param&gt;&lt;h:link outcome="contact" value="#{bundle.contact}" /&gt;&lt;/o:param&gt;
 * &lt;/h:outputFormat&gt;
 * </pre>
 * <p>with this bundle
 * <pre>
 * paragraph = Please {0} for more information.
 * contact = contact us
 * </pre>
 * <p>will result in the link being actually encoded as output format parameter value.
 *
 * @author Bauke Scholtz
 * @param <T> The type of the value.
 * @since 1.4
 * @see ParamHolder
 */
@FacesComponent(value = Param.COMPONENT_TYPE) public class Param<T extends java.lang.Object> extends UIParameter implements ParamHolder<T> {
  public static final String COMPONENT_TYPE = "org.omnifaces.component.output.Param";

  private enum PropertyKeys {
    converter
  }

  private Converter<T> localConverter;

  @Override @SuppressWarnings(value = { "unchecked" }) public Converter<T> getConverter() {
    return localConverter != null ? localConverter : (Converter<T>) getStateHelper().eval(PropertyKeys.converter);
  }

  @Override @SuppressWarnings(value = { "unchecked", "rawtypes" }) public void setConverter(Converter converter) {
    localConverter = converter;
  }

  /**
	 * @throws ClassCastException When actual value is not <code>T</code>.
	 */
  @Override @SuppressWarnings(value = { "unchecked" }) public T getLocalValue() {
    return (T) super.getValue();
  }

  @Override @SuppressWarnings(value = { "unchecked" }) public String getValue() {
    FacesContext context = getFacesContext();
    Converter<T> converter = getConverter();
    Object value = getLocalValue();
    if (value == null && getChildCount() > 0) {
      ResponseWriter originalResponseWriter = context.getResponseWriter();
      StringWriter output = new StringWriter();
      context.setResponseWriter(originalResponseWriter.cloneWithWriter(output));
      try {
        super.encodeChildren(context);
      } catch (IOException e) {
        throw new FacesException(e);
      } finally {
        context.setResponseWriter(originalResponseWriter);
      }
      value = output.toString();
    }
    if (converter == null && value != null) {
      converter = createConverter(context, value.getClass());
    }
    if (converter != null) {
      return converter.getAsString(context, this, (T) value);
    } else {
      return value != null ? value.toString() : null;
    }
  }

  @Override public boolean getRendersChildren() {
    return true;
  }

  @Override public void encodeChildren(FacesContext context) throws IOException {
  }
}
=======
/**
 * <p>
 * The	<code>&lt;o:param&gt;</code> is a component that extends the standard {@link UIParameter} to implement {@link ValueHolder}
 * and thus support a {@link Converter} to convert the supplied value to string, if necessary.
 * <p>
 * You can use it the same way as <code>&lt;f:param&gt;</code>, you only need to change <code>f:</code> into
 * <code>o:</code> to get the extra support for a {@link Converter} by usual means via the <code>converter</code>
 * attribute of the tag, or the nested <code>&lt;f:converter&gt;</code> tag, or just automatically if a converter is
 * already registered for the target class via <code>@FacesConverter(forClass)</code>.
 * <p>
 * Also, if no value is specified, but children are present, then the encoded output of children will be returned as
 * param value. This is useful when you want to supply JSF components or HTML as parameter of an unescaped
 * <code>&lt;h:outputFormat&gt;</code>. For example,
 * <pre>
 * &lt;h:outputFormat value="#{bundle.paragraph}" escape="false"&gt;
 *     &lt;o:param&gt;&lt;h:link outcome="contact" value="#{bundle.contact}" /&gt;&lt;/o:param&gt;
 * &lt;/h:outputFormat&gt;
 * </pre>
 * <p>with this bundle
 * <pre>
 * paragraph = Please {0} for more information.
 * contact = contact us
 * </pre>
 * <p>will result in the link being actually encoded as output format parameter value.
 *
 * @author Bauke Scholtz
 * @since 1.4
 * @see ParamHolder
 */
@FacesComponent(value = Param.COMPONENT_TYPE) public class Param extends UIParameter implements ParamHolder {
  public static final String COMPONENT_TYPE = "org.omnifaces.component.output.Param";

  private enum PropertyKeys {
    converter
  }

  @Override public Converter getConverter() {
    return (Converter) getStateHelper().eval(PropertyKeys.converter);
  }

  @Override public void setConverter(Converter converter) {
    getStateHelper().put(PropertyKeys.converter, converter);
  }

  @Override public Object getLocalValue() {
    return super.getValue();
  }

  @Override public Object getValue() {
    FacesContext context = getFacesContext();
    Converter converter = getConverter();
    Object value = getLocalValue();
    if (value == null && getChildCount() > 0) {
      ResponseWriter originalResponseWriter = context.getResponseWriter();
      StringWriter output = new StringWriter();
      context.setResponseWriter(originalResponseWriter.cloneWithWriter(output));
      try {
        super.encodeChildren(context);
      } catch (IOException e) {
        throw new FacesException(e);
      } finally {
        context.setResponseWriter(originalResponseWriter);
      }
      value = output.toString();
    }
    if (converter == null && value != null) {
      converter = context.getApplication().createConverter(value.getClass());
    }
    if (converter != null) {
      return converter.getAsString(context, this, value);
    } else {
      return value;
    }
  }

  @Override public boolean getRendersChildren() {
    return true;
  }

  @Override public void encodeChildren(FacesContext context) throws IOException {
  }
}
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/05abae8be2b4e35b6c7a849b68ad6af6b07d4310/src/main/java/org/omnifaces/component/output/Param.java/right.java
