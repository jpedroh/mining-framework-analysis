package org.hdiv.validators;
import java.util.Collection;
import java.util.Map;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.UIParameterExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.validation.ValidationContext;

/**
 * ComponentValidator that validates parameters of a component of type UICommand,
 * 
 * @author Gotzon Illarramendi
 */
public class UICommandValidator extends AbstractComponentValidator {
  private static final Log log = LogFactory.getLog(UICommandValidator.class);

  public UICommandValidator() {
    super(UICommand.class);
  }

  public void validate(final ValidationContext validationContext, final UIComponent component) {
    UICommand command = (UICommand) component;
    if (!wasClicked(validationContext.getFacesContext(), command)) {
      return;
    }
    validateUICommand(validationContext, command);
  }

  protected boolean wasClicked(final FacesContext facesContext, final UICommand command) {
    String clientId = command.getClientId(facesContext);
    String value = facesContext.getExternalContext().getRequestParameterMap().get(clientId);
    if (value != null && (value.equals(clientId) || value.equals(command.getValue()))) {
      return true;
    }
    PartialViewContext partialContext = facesContext.getPartialViewContext();
    if (partialContext != null && partialContext.isPartialRequest()) {
      Collection<String> execIds = partialContext.getExecuteIds();
      return execIds.contains(clientId);
    }
    return false;
  }

  protected void validateUICommand(final ValidationContext validationContext, final UICommand command) {
    validationContext.acceptParameter(command.getClientId(validationContext.getFacesContext()), command.getValue());
    for (UIComponent childComp : command.getChildren()) {
      if (childComp instanceof UIParameter) {
        UIParameter param = (UIParameter) childComp;
        processParam(validationContext, param);
      }
    }
  }

  /**
	 * Validates a parameter of component UICommand
	 * 
	 * @param context Request context
	 * @param parameter UIParameter component to validate
	 * @return validation result
	 */
  private void processParam(final ValidationContext validationContext, final UIParameter parameter) {
    FacesContext context = validationContext.getFacesContext();
    UIParameterExtension param = (UIParameterExtension) parameter;
    UIComponent parent = parameter.getParent();
    String parentClientId = parent.getClientId(context);
    Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
    String requestValue = requestMap.get(param.getName());
    String realValue = param.getValue(parentClientId).toString();
    if (log.isDebugEnabled()) {
      log.debug("UIParameter requestValue:" + requestValue);
      log.debug("UIParameter realValue:" + realValue);
    }

<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (requestValue == null) {
      ValidationError error = new ValidationError();
      error.setErrorKey(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
      error.setErrorParam(param.getId());
      error.setErrorValue(requestValue);
      error.setErrorComponent(param.getClientId(context));
      return error;
    }
>>>>>>> /usr/src/app/output/hdiv/hdiv/24a14715725c13330cb2da5e7846ba88e50557cf/hdiv-jsf/src/main/java/org/hdiv/validators/UICommandValidator.java/right.java

    if (requestValue != null && requestValue.equals(realValue)) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
      error.setErrorKey(HDIVErrorCodes.INVALID_PARAMETER_VALUE);
>>>>>>> /usr/src/app/output/hdiv/hdiv/24a14715725c13330cb2da5e7846ba88e50557cf/hdiv-jsf/src/main/java/org/hdiv/validators/UICommandValidator.java/right.java

      validationContext.acceptParameter(param.getName(), requestValue);
    } else {
      if (requestValue == null) {
        if (log.isDebugEnabled()) {
          log.debug("Parameter \'" + param.getName() + "\' rejected in component \'" + param.getClientId(context) + "\' in ComponentValidator \'" + this.getClass() + "\'");
        }
        validationContext.rejectParameter(param.getName(), requestValue, HDIVErrorCodes.REQUIRED_PARAMETERS);
      } else {
        if (log.isDebugEnabled()) {
          log.debug("Parameter \'" + param.getName() + "\' rejected in component \'" + param.getClientId(context) + "\' in ComponentValidator \'" + this.getClass() + "\'");
        }
        validationContext.rejectParameter(param.getName(), requestValue, HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
      }
    }
  }
}