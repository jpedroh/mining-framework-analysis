package org.omnifaces.renderer;
import static org.omnifaces.resourcehandler.DefaultResource.RES_NOT_FOUND;
import static org.omnifaces.resourcehandler.DefaultResourceHandler.RES_NOT_FOUND;
import static org.omnifaces.util.Utils.isEmpty;
import java.io.IOException;
import java.util.Map;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.omnifaces.component.script.DeferredScript;
import org.omnifaces.component.script.ScriptFamily;
import org.omnifaces.resourcehandler.CombinedResourceHandler;

/**
 * This renderer is the default renderer of {@link DeferredScript}. The rendering is extracted from the component so
 * that it can be reused by {@link CombinedResourceHandler}.
 *
 * @author Bauke Scholtz
 * @since 1.8
 */
@FacesRenderer(componentFamily = ScriptFamily.COMPONENT_FAMILY, rendererType = DeferredScriptRenderer.RENDERER_TYPE) public class DeferredScriptRenderer extends Renderer {
  /** The standard renderer type. */
  public static final String RENDERER_TYPE = "org.omnifaces.DeferredScript";

  /**
	 * Writes a <code>&lt;script&gt;</code> element which calls <code>OmniFaces.DeferredScript.add</code> with as
	 * arguments the script URL and, if any, the onbegin, onsuccess and/or onerror callbacks. If the script resource is
	 * not resolvable, then a <code>RES_NOT_FOUND</code> will be written to <code>src</code> attribute instead.
	 */
  @Override public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    Map<String, Object> attributes = component.getAttributes();
    String library = (String) attributes.get("library");
    String name = (String) attributes.get("name");
    Resource resource = context.getApplication().getResourceHandler().createResource(name, library);
    ResponseWriter writer = context.getResponseWriter();
    writer.startElement("script", component);
    writer.writeAttribute("type", "text/javascript", "type");
    if (resource != null) {
      writer.write("OmniFaces.DeferredScript.add(\'");
      writer.write(resource.getRequestPath());
      writer.write('\'');
      String onbegin = (String) attributes.get("onbegin");
      String onsuccess = (String) attributes.get("onsuccess");
      String onerror = (String) attributes.get("onerror");
      boolean hasOnbegin = !isEmpty(onbegin);
      boolean hasOnsuccess = !isEmpty(onsuccess);
      boolean hasOnerror = !isEmpty(onerror);
      if (hasOnbegin || hasOnsuccess || hasOnerror) {
        encodeFunctionArgument(writer, onbegin, hasOnbegin);
      }
      if (hasOnsuccess || hasOnerror) {
        encodeFunctionArgument(writer, onsuccess, hasOnsuccess);
      }
      if (hasOnerror) {
        encodeFunctionArgument(writer, onerror, true);
      }
      writer.write(");");
    } else {
      writer.writeURIAttribute("src", RES_NOT_FOUND, "src");
    }
  }

  private void encodeFunctionArgument(ResponseWriter writer, String function, boolean hasFunction) throws IOException {
    if (hasFunction) {
      writer.write(",function() {");
      writer.write(function);
      writer.write('}');
    } else {
      writer.write(",null");
    }
  }

  @Override public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    ResponseWriter writer = context.getResponseWriter();
    writer.endElement("script");
  }
}