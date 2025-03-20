package org.jboss.test.clusterbench.common.debug;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jboss.test.clusterbench.common.ClusterBenchConstants;
import org.jboss.test.clusterbench.common.SerialBean;

/**
 * Servlet which outputs debug information provided by the {@link #getContainerSpecificDebugInfo(HttpServletRequest)} method.
 *
 * @author Radoslav Husar
 * @version April 2012
 */
public abstract class AbstractCommonDebugServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(AbstractCommonDebugServlet.class.getName());

  public static final String KEY = AbstractCommonDebugServlet.class.getName();

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HttpSession session = req.getSession(true);
    resp.setContentType("text/plain");
    resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    PrintWriter out = resp.getWriter();
    if (session.isNew()) {
      log.log(Level.INFO, "New session created: {0}", session.getId());
      session.setAttribute(KEY, new SerialBean());
    } else {
      if (session.getAttribute(KEY) == null) {
        log.log(Level.INFO, "Session is not new, creating SerialBean: {0}", session.getId());
        session.setAttribute(KEY, new SerialBean());
      }
    }
    SerialBean bean = (SerialBean) session.getAttribute(KEY);
    resp.setContentType("text/plain");
    if (req.getParameter(ClusterBenchConstants.READONLY) != null) {
      out.print(bean.getSerial());
      out.println(this.getContainerSpecificDebugInfo(req));
      return;
    }
    int serial = bean.getSerial();
    bean.setSerial(serial + 1);
    session.setAttribute(KEY, bean);
    Enumeration<String> headers = req.getHeaderNames();
    while (headers.hasMoreElements()) {
      String header = headers.nextElement();
      out.println("Request header: " + header + "=" + req.getHeader(header));
    }
    out.println("Request URI: " + req.getRequestURI());
    out.println("Query string: " + req.getQueryString());
    out.println("Query string UTF-8 decoded: " + ((req.getQueryString() == null) ? "null" : URLDecoder.decode(req.getQueryString(), StandardCharsets.UTF_8)));
    out.println("Path info: " + req.getPathInfo());
    out.println("Serial: " + serial);
    out.println("Session ID: " + req.getSession().getId());
    out.println("Current time: " + new Date());
    out.println("ServletRequest.getServerPort(): " + req.getServerPort());
    out.println("ServletRequest.getLocalPort(): " + req.getLocalPort());
    out.println("Node name: " + System.getProperty("jboss.node.name"));
    out.println(printRequestParameters(req));
    out.println(this.getContainerSpecificDebugInfo(req));
    if (req.getParameter(ClusterBenchConstants.INVALIDATE) != null) {
      log.log(Level.INFO, "Invalidating: {0}", session.getId());
      session.invalidate();
    }
  }

  private String printRequestParameters(HttpServletRequest request) {
    final StringBuilder responseText = new StringBuilder();
    responseText.append("Parameters [key=value]: {");
    final Map<String, String[]> params = request.getParameterMap();
    final Iterator<String> i = params.keySet().iterator();
    while (i.hasNext()) {
      String key = i.next();
      String value = (params.get(key))[0];
      responseText.append("[");
      responseText.append(key);
      responseText.append("=");
      responseText.append(value);
      responseText.append("]");
      if (i.hasNext()) {
        responseText.append(" ");
      }
    }
    responseText.append("}");
    return responseText.toString();
  }

  @Override public String getServletInfo() {
    return "Debug servlet.";
  }

  /**
     * Implement this method to print out any debug info specific to the container or EE version.
     *
     * @param req HttpServletRequest
     * @return debug info String
     */
  abstract public String getContainerSpecificDebugInfo(HttpServletRequest req);
}