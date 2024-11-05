package org.omnifaces.filter;
import static org.omnifaces.util.Utils.UTF_8;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.faces.context.ExternalContext;
import javax.faces.context.PartialViewContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CharacterEncodingFilter extends HttpFilter {
  private static final String INIT_PARAM_ENCODING = "encoding";

  private static final Charset DEFAULT_ENCODING = UTF_8;

  private static final String ERROR_ENCODING = "The \'encoding\' init param must represent a valid charset. Encountered an invalid charset of \'%s\'.";

  private Charset encoding = DEFAULT_ENCODING;

  @Override public void init() throws ServletException {
    String encodingParam = getInitParameter(INIT_PARAM_ENCODING);
    if (encodingParam != null) {
      try {
        encoding = Charset.forName(encodingParam);
      } catch (Exception e) {
        throw new ServletException(String.format(ERROR_ENCODING, encodingParam), e);
      }
    }
  }

  @Override public void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {
    if (request.getCharacterEncoding() == null) {
      request.setCharacterEncoding(encoding.name());
    }
    chain.doFilter(request, response);
  }
}