package ninja;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.common.util.concurrent.ListenableFuture;
import ninja.bodyparser.BodyParserEngineJson;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;

public interface Context {
  enum HTTP_STATUS {
    notFound404,
    ok200,
    forbidden403,
    teapot418
  }

  /**
	 * Returns the uri as seen by the server.
	 * 
	 * http://example.com/index would return
	 * "/index".
	 * 
	 * @return the uri as seen by the server
	 */
  String getRequestUri();

  FlashCookie getFlashCookie();

  SessionCookie getSessionCookie();

  void redirect(String url);

  void setContentType(String contentType);

  Context template(String explicitTemplateName);

  HttpServletRequest getHttpServletRequest();

  HttpServletResponse getHttpServletResponse();

  Context status(HTTP_STATUS httpStatus);

  String getPathParameter(String key);

  String getTemplateName();

  void render();

  void render(Object object);

  void renderHtml();

  void renderHtml(Object object);

  void renderJson(Object object);

  <T extends java.lang.Object> T parseBody(Class<T> classOfT);

  /**
     * Indicate that this request will be handled asynchronously
     */
  void handleAsync();

  /**
     * Indicate that request processing of an async request is complete
     */
  void requestComplete();
}