package org.hdiv.validators;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.HtmlInputHiddenExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
import org.hdiv.validation.ValidationContext;

/**
 * Validates component of type HtmlInputHiddenExtension.
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlInputHiddenValidator extends AbstractComponentValidator {
  private static final Log log = LogFactory.getLog(HtmlInputHiddenValidator.class);

  public HtmlInputHiddenValidator() {
    super(HtmlInputHidden.class);
  }

  public void validate(final ValidationContext context, final UIComponent component) {
    HtmlInputHiddenExtension inputHidden = (HtmlInputHiddenExtension) component;
    validateHiddenComponent(context, inputHidden);
  }

  /**
	 * Validates Hidden component received as input
	 * 
	 * @param validationContext Validation context
	 * @param inputHidden component to validate
	 */
  protected void validateHiddenComponent(final ValidationContext validationContext, final HtmlInputHiddenExtension inputHidden) {
    FacesContext context = validationContext.getFacesContext();
    UIData uiDataComp = UtilsJsf.findParentUIData(inputHidden);
    int rowIndex = 0;
    if (uiDataComp != null) {
      rowIndex = uiDataComp.getRowIndex();
    }
    Object hiddenValue;
    Object hiddenRealValue;
    Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
    if (rowIndex >= 0) {
      hiddenValue = parameters.get(inputHidden.getClientId(context));
      hiddenRealValue = inputHidden.getRealValue(inputHidden.getClientId(context));
      if (log.isDebugEnabled()) {
        log.debug("Hidden\'s value received:" + hiddenValue);
        log.debug("Hidden\'s value sent to the client:" + hiddenRealValue);
      }
      if (hiddenValue == null) {
        if (log.isDebugEnabled()) {
          log.debug("Parameter \'" + inputHidden.getId() + "\' rejected in component \'" + inputHidden.getId() + "\' in ComponentValidator \'" + this.getClass() + "\'");
        }

<<<<<<< Unknown file: This is a bug in JDime.
=======
        error.setErrorKey(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
>>>>>>> /usr/src/app/output/hdiv/hdiv/24a14715725c13330cb2da5e7846ba88e50557cf/hdiv-jsf/src/main/java/org/hdiv/validators/HtmlInputHiddenValidator.java/right.java

        validationContext.rejectParameter(inputHidden.getId(), null, HDIVErrorCodes.REQUIRED_PARAMETERS);
      }
      boolean correct = hasEqualValue(hiddenValue, hiddenRealValue);
      if (!correct) {
        if (log.isDebugEnabled()) {
          log.debug("Parameter \'" + inputHidden.getId() + "\' rejected in component \'" + inputHidden.getId() + "\' in ComponentValidator \'" + this.getClass() + "\'");
        }

<<<<<<< Unknown file: This is a bug in JDime.
=======
        error.setErrorKey(HDIVErrorCodes.INVALID_PARAMETER_VALUE);
>>>>>>> /usr/src/app/output/hdiv/hdiv/24a14715725c13330cb2da5e7846ba88e50557cf/hdiv-jsf/src/main/java/org/hdiv/validators/HtmlInputHiddenValidator.java/right.java

        validationContext.rejectParameter(inputHidden.getId(), hiddenRealValue.toString(), HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
      } else {
        validationContext.acceptParameter(inputHidden.getId(), hiddenRealValue.toString());
      }
    } else {
      List<String> clientIds = inputHidden.getClientIds();
      for (int i = 0; i < clientIds.size(); i++) {
        String clientId = clientIds.get(i);
        hiddenValue = parameters.get(clientId);
        hiddenRealValue = inputHidden.getRealValue(clientId);
        if (log.isDebugEnabled()) {
          log.debug("Hidden\'s value received:" + hiddenValue);
          log.debug("Hidden\'s value sent to the client:" + hiddenRealValue);
        }
        if (hiddenValue == null) {
          if (log.isDebugEnabled()) {
            log.debug("Parameter \'" + inputHidden.getId() + "\' rejected in component \'" + inputHidden.getId() + "\' in ComponentValidator \'" + this.getClass() + "\'");
          }

<<<<<<< Unknown file: This is a bug in JDime.
=======
          error.setErrorKey(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
>>>>>>> /usr/src/app/output/hdiv/hdiv/24a14715725c13330cb2da5e7846ba88e50557cf/hdiv-jsf/src/main/java/org/hdiv/validators/HtmlInputHiddenValidator.java/right.java

          validationContext.rejectParameter(inputHidden.getId(), null, HDIVErrorCodes.REQUIRED_PARAMETERS);
        }
        boolean correct = hiddenValue.equals(hiddenRealValue);
        if (!correct) {
          if (log.isDebugEnabled()) {
            log.debug("Parameter \'" + inputHidden.getId() + "\' rejected in component \'" + inputHidden.getId() + "\' in ComponentValidator \'" + this.getClass() + "\'");
          }

<<<<<<< Unknown file: This is a bug in JDime.
=======
          error.setErrorKey(HDIVErrorCodes.INVALID_PARAMETER_VALUE);
>>>>>>> /usr/src/app/output/hdiv/hdiv/24a14715725c13330cb2da5e7846ba88e50557cf/hdiv-jsf/src/main/java/org/hdiv/validators/HtmlInputHiddenValidator.java/right.java

          validationContext.rejectParameter(inputHidden.getId(), hiddenRealValue.toString(), HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
        } else {
          validationContext.acceptParameter(inputHidden.getId(), hiddenRealValue.toString());
        }
      }
    }
  }

  /**
	 * Return true only if the two objects have the same value.
	 * 
	 * @param hiddenValue component value
	 * @param realValue request value
	 * @return result
	 */
  protected boolean hasEqualValue(Object hiddenValue, Object realValue) {
    if (!(hiddenValue instanceof String)) {
      hiddenValue = hiddenValue.toString();
    }
    if (!(realValue instanceof String)) {
      realValue = realValue.toString();
    }
    return hiddenValue.equals(realValue);
  }
}