package org.hdiv.components.support;
import javax.faces.FacesException;
import javax.faces.component.UIOutcomeTarget;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.hdiv.util.UtilsJsf;

public class OutcomeTargetComponentProcessor extends AbstractComponentProcessor {
  private static final Log log = LogFactory.getLog(OutcomeTargetComponentProcessor.class);

  protected OutcomeTargetComponentHelper helper = new OutcomeTargetComponentHelper();

  public void processOutcomeTargetLinkComponent(final FacesContext context, final UIOutcomeTarget component) {
    try {
      ExternalContext externalContext = context.getExternalContext();
      HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
      String url = helper.getUrl(context, component);
      String hdivParameter = HDIVUtil.getHDIVParameter(request);
      UrlData urlData = linkUrlProcessor.createUrlData(url, Method.GET, hdivParameter, request);
      if (linkUrlProcessor.isHdivStateNecessary(urlData)) {
        boolean hasUIParams = UtilsJsf.hasUIParameterChild(component);
        if (!config.isValidationInUrlsWithoutParamsActivated() && !urlData.containsParams() && !hasUIParams) {
          return;
        }
        IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
        dataComposer.beginRequest(Method.GET, urlData.getUrlWithoutContextPath());
        String processedParams = dataComposer.composeParams(urlData.getUrlParams(), Method.GET, Constants.ENCODING_UTF_8);
        urlData.setUrlParams(processedParams);
        String stateParam = dataComposer.endRequest();

<<<<<<< Unknown file: This is a bug in JDime.
=======
        String hdivParameter = HDIVUtil.getHdivStateParameterName(request);
>>>>>>> /usr/src/app/output/hdiv/hdiv/2f9993190a5d9e153c693e8060ef8050f2fe2baa/hdiv-jsf/src/main/java/org/hdiv/components/support/OutcomeTargetComponentProcessor.java/right.java

        UIParameter paramComponent = (UIParameter) context.getApplication().createComponent(UIParameter.COMPONENT_TYPE);
        paramComponent.setName(hdivParameter);
        paramComponent.setValue(stateParam);
        component.getChildren().add(paramComponent);
      }
    } catch (FacesException e) {
      log.error("Error in OutcomeTargetComponentProcessor.processOutcomeTargetLinkComponent: " + e.getMessage());
      throw e;
    }
  }
}