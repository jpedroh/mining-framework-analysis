package org.jasig.portlet.proxy.mvc.portlet.json;
import java.io.IOException;
import java.util.Map;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.service.IContentRequest;
import org.jasig.portlet.proxy.service.IContentResponse;
import org.jasig.portlet.proxy.service.IContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Controller @RequestMapping(value = "VIEW") public class JsonPortletController {
  protected static final String CONTENT_LOCATION_KEY = "location";

  protected static final String CONTENT_SERVICE_KEY = "contentService";

  protected static final String MAIN_VIEW_KEY = "mainView";

  protected static final String MOBILE_VIEW_KEY = "mobileView";

  protected final Logger log = LoggerFactory.getLogger(this.getClass());

  private ApplicationContext applicationContext;

  @Autowired(required = true) public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  private IViewSelector viewSelector;

  @Autowired(required = true) public void setViewSelector(IViewSelector viewSelector) {
    this.viewSelector = viewSelector;
  }

  @RequestMapping public ModelAndView showContent(PortletRequest request) {
    final ModelAndView mv = new ModelAndView();
    final PortletPreferences preferences = request.getPreferences();
    final String contentServiceKey = preferences.getValue(CONTENT_SERVICE_KEY, null);
    final IContentService contentService = applicationContext.getBean(contentServiceKey, IContentService.class);
    final IContentRequest proxyRequest = contentService.getRequest(request);
    final IContentResponse proxyResponse = contentService.getContent(proxyRequest, request);
    final ObjectMapper mapper = new ObjectMapper();
    final ObjectReader reader = mapper.reader(Map.class);
    try {
      final Map<String, Object> map = reader.readValue(proxyResponse.getContent());
      mv.addAllObjects(map);
    } catch (JsonProcessingException e) {
      log.error("Error parsing JSON content", e);
    } catch (IOException e) {
      log.error("IOException reading JSON content", e);
    } finally {
      if (proxyResponse != null) {
        proxyResponse.close();
      }
    }
    final String mainView = preferences.getValue(MAIN_VIEW_KEY, null);
    final String mobileView = preferences.getValue(MOBILE_VIEW_KEY, null);
    final String viewName;
    if (mobileView != null) {
      final boolean isMobile = viewSelector.isMobile(request);
      viewName = isMobile ? mobileView : mainView;
    } else {
      viewName = mainView;
    }
    mv.setViewName(viewName);
    return mv;
  }
}