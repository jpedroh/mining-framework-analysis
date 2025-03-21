package org.atmosphere.spring.bean;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequestImpl;
import org.atmosphere.cpr.AtmosphereResponseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Atmosphere's Servlet.
 *
 * @author Evgeny Konovalov
 */
public class AtmosphereSpringServlet extends HttpServlet {
  private static final long serialVersionUID = 6755906261738522768L;

  @Autowired private AtmosphereFramework framework;

  @Autowired private AtmosphereSpringContext atmosphereSpringContext;

  @Override public void init(ServletConfig config) throws ServletException {
    super.init(config);
    WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()).getAutowireCapableBeanFactory().autowireBean(this);
    atmosphereSpringContext.setServletContext(config.getServletContext());
    framework.init(atmosphereSpringContext, false);
  }

  @Override public void doHead(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    doPost(req, res);
  }

  @Override public void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    doPost(req, res);
  }

  @Override public void doTrace(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    doPost(req, res);
  }

  @Override public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    doPost(req, res);
  }

  @Override public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    doPost(req, res);
  }

  @Override public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    doPost(req, res);
  }

  @Override public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    framework.doCometSupport(AtmosphereRequestImpl.wrap(req), AtmosphereResponseImpl.wrap(res));
  }
}