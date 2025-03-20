package org.omnifaces.component.output;
import static org.omnifaces.util.Components.validateHasNoChildren;
import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.FacesComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.omnifaces.servlet.BufferedHttpServletResponse;
import org.omnifaces.servlet.HttpServletResponseOutputWrapper;

/**
 * <p>
 * The <code>&lt;o:resourceInclude&gt;</code> component can be used to catch the output from a JSP or Servlet
 * resource and render it as output to the JSF writer. In effect, this allows you to include both Servlets and
 * JSP pages in e.g. Facelets.
 * <p>
 * Note that this isn't recommended as a lasting solution, but it might ease a migration from legacy JSP with
 * smelly scriptlets and all on them to a more sane and modern Facelets application.
 *
 * @author Arjan Tijms
 * @author Bauke Scholtz
 */
@FacesComponent(value = ResourceInclude.COMPONENT_TYPE) public class ResourceInclude extends OutputFamily {
  /** The component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.output.ResourceInclude";

  /**
	 * Create a dispatcher for the resource given by the component's path attribute, catch its output and write it to
	 * the JSF response writer.
	 */
  @Override public void encodeBegin(FacesContext context) throws IOException {
    validateHasNoChildren(this);
    ExternalContext externalContext = context.getExternalContext();
    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
    BufferedHttpServletResponse bufferedResponse = new BufferedHttpServletResponse(response);
    try {
      request.getRequestDispatcher((String) getAttributes().get("path")).include(request, bufferedResponse);
    } catch (ServletException e) {
      throw new FacesException(e);
    }
    context.getResponseWriter().write(bufferedResponse.getBufferAsString());
  }
}