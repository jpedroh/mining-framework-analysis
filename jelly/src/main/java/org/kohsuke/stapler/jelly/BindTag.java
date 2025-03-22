package org.kohsuke.stapler.jelly;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.jvnet.maven.jellydoc.annotation.NoContent;
import org.jvnet.maven.jellydoc.annotation.Required;
import org.kohsuke.stapler.WebApp;
import org.kohsuke.stapler.bind.Bound;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Binds a server-side object to client side so that JavaScript can call into server.
 * This tag evaluates to a {@code <script>} tag.
 * 
 * @author Kohsuke Kawaguchi
 */
@NoContent public class BindTag extends AbstractStaplerTag {
  private String varName;

  private Object javaObject;

  /**
     * JavaScript variable name to set the proxy to.
     * <p>
     * This name can be arbitrary left hand side expression,
     * such as "a[0]" or "a.b.c".
     *
     * If this value is unspecified, the tag generates a JavaScript expression to create a proxy.
     */
  public void setVar(String varName) {
    this.varName = varName;
  }

  @Required public void setValue(Object o) {
    this.javaObject = o;
  }

  @Override public void doTag(XMLOutput out) throws JellyTagException {
    AdjunctTag a = new AdjunctTag();
    a.setContext(getContext());
    a.setIncludes("org.kohsuke.stapler.bind");
    a.doTag(out);
    try {
      if (javaObject == null) {
        if (varName == null) {
          out.write("null");
        } else {
          writeScriptTag(out, null);
        }
      } else {
        Bound h = WebApp.getCurrent().boundObjectTable.bind(javaObject);
        if (varName == null) {
          out.write(h.getProxyScript());
        } else {
          writeScriptTag(out, h);
        }
      }
    } catch (SAXException e) {
      throw new JellyTagException(e);
    }
  }

  private void writeScriptTag(XMLOutput out, @CheckForNull Bound bound) throws SAXException {
    final AttributesImpl attributes = new AttributesImpl();
    if (bound == null) {
      attributes.addAttribute("", "src", "src", "", Bound.getProxyScriptUrl(varName, null));
    } else {
      attributes.addAttribute("", "src", "src", "", bound.getProxyScriptURL(varName));
    }
    attributes.addAttribute("", "type", "type", "", "application/javascript");
    out.startElement("script", attributes);
    out.endElement("script");
  }
}