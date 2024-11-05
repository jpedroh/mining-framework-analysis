package org.omnifaces.taghandler;
import static java.util.logging.Level.SEVERE;
import static javax.faces.event.PhaseId.PROCESS_VALIDATIONS;
import static javax.faces.event.PhaseId.RESTORE_VIEW;
import static javax.faces.event.PhaseId.UPDATE_MODEL_VALUES;
import static org.omnifaces.el.ExpressionInspector.getValueReference;
import static org.omnifaces.util.Components.forEachComponent;
import static org.omnifaces.util.Components.getClosestParent;
import static org.omnifaces.util.Components.getCurrentForm;
import static org.omnifaces.util.Components.hasInvokedSubmit;
import static org.omnifaces.util.Events.subscribeToRequestAfterPhase;
import static org.omnifaces.util.Events.subscribeToRequestBeforePhase;
import static org.omnifaces.util.Events.subscribeToViewEvent;
import static org.omnifaces.util.Facelets.getBoolean;
import static org.omnifaces.util.Facelets.getString;
import static org.omnifaces.util.Facelets.getValueExpression;
import static org.omnifaces.util.Faces.getELContext;
import static org.omnifaces.util.Faces.renderResponse;
import static org.omnifaces.util.Faces.validationFailed;
import static org.omnifaces.util.FacesLocal.evaluateExpressionGet;
import static org.omnifaces.util.Messages.createError;
import static org.omnifaces.util.Reflection.instance;
import static org.omnifaces.util.Reflection.setProperties;
import static org.omnifaces.util.Reflection.toClass;
import static org.omnifaces.util.Utils.csvToList;
import static org.omnifaces.util.Utils.isEmpty;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import javax.validation.ConstraintViolation;
import org.omnifaces.eventlistener.BeanValidationEventListener;
import org.omnifaces.util.Callback;
import org.omnifaces.util.Platform;
import org.omnifaces.util.copier.CloneCopier;
import org.omnifaces.util.copier.Copier;
import org.omnifaces.util.copier.CopyCtorCopier;
import org.omnifaces.util.copier.MultiStrategyCopier;
import org.omnifaces.util.copier.NewInstanceCopier;
import org.omnifaces.util.copier.SerializationCopier;
import static javax.faces.component.visit.VisitHint.SKIP_UNRENDERED;

public class ValidateBean extends TagHandler {
  private static final Logger logger = Logger.getLogger(ValidateBean.class.getName());

  private static final String ERROR_MISSING_FORM = "o:validateBean must be nested in an UIForm.";

  private static final String ERROR_INVALID_PARENT = "o:validateBean parent must be an instance of UIInput or UICommand.";

  private static enum ValidateMethod {
    validateCopy,
    validateActual
    ;

    public static ValidateMethod of(String name) {
      if (isEmpty(name)) {
        return validateCopy;
      }
      return valueOf(name);
    }
  }

  private ValueExpression value;

  private boolean disabled;

  private ValidateMethod method;

  private String groups;

  private String copier;

  public ValidateBean(TagConfig config) {
    super(config);
  }

  @Override public void apply(FaceletContext context, final UIComponent parent) throws IOException {
    if (getAttribute("value") == null && (!(parent instanceof UICommand || parent instanceof UIInput))) {
      throw new IllegalArgumentException(ERROR_INVALID_PARENT);
    }
    FacesContext facesContext = context.getFacesContext();
    if (!(ComponentHandler.isNew(parent) && facesContext.isPostback() && facesContext.getCurrentPhaseId() == RESTORE_VIEW)) {
      return;
    }
    value = getValueExpression(context, getAttribute("value"), Object.class);
    disabled = getBoolean(context, getAttribute("disabled"));
    method = ValidateMethod.of(getString(context, getAttribute("method")));
    groups = getString(context, getAttribute("validationGroups"));
    copier = getString(context, getAttribute("copier"));
    subscribeToRequestAfterPhase(RESTORE_VIEW, new Callback.Void() {
      @Override public void invoke() {
        processValidateBean(parent);
      }
    });
  }

  protected void processValidateBean(UIComponent component) {
    UIForm form = (component instanceof UIForm) ? ((UIForm) component) : getClosestParent(component, UIForm.class);
    if (form == null) {
      throw new IllegalArgumentException(ERROR_MISSING_FORM);
    }
    if (!form.equals(getCurrentForm()) || (component instanceof UICommand && !hasInvokedSubmit(component))) {
      return;
    }
    Object bean = (value != null) ? value.getValue(getELContext()) : null;
    if (bean == null) {
      validateForm(groups, disabled);
      return;
    }
    if (disabled) {
      return;
    }
    switch (method) {
      case validateActual:
      validateActualBean(form, bean, groups);
      break;
      case validateCopy:
      validateCopiedBean(form, bean, copier, groups);
      break;
    }
  }

  private void validateActualBean(final UIForm form, final Object bean, final String groups) {
    ValidateBeanCallback validateActualBean = new ValidateBeanCallback() {
      @Override public void run() {
        FacesContext context = FacesContext.getCurrentInstance();
        validate(context, form, bean, groups, false);
      }
    };
    subscribeToRequestAfterPhase(UPDATE_MODEL_VALUES, validateActualBean);
  }

  private void validateCopiedBean(final UIForm form, final Object bean, final String copier, final String groups) {
    final Map<String, Object> properties = new HashMap<String, Object>();
    ValidateBeanCallback collectBeanProperties = new ValidateBeanCallback() {
      @Override public void run() {
        FacesContext context = FacesContext.getCurrentInstance();
        forEachInputWithMatchingBase(context, form, bean, new Operation() {
          @Override public void run(EditableValueHolder v, ValueReference vr) {
            addCollectingValidator(v, vr, properties);
          }
        });
      }
    };
    ValidateBeanCallback checkConstraints = new ValidateBeanCallback() {
      @Override public void run() {
        FacesContext context = FacesContext.getCurrentInstance();
        forEachInputWithMatchingBase(context, form, bean, new Operation() {
          @Override public void run(EditableValueHolder v, ValueReference vr) {
            removeCollectingValidator(v);
          }
        });
        Object copiedBean = getCopier(context, copier).copy(bean);
        setProperties(copiedBean, properties);
        validate(context, form, copiedBean, groups, true);
      }
    };
    subscribeToRequestBeforePhase(PROCESS_VALIDATIONS, collectBeanProperties);
    subscribeToRequestAfterPhase(PROCESS_VALIDATIONS, checkConstraints);
  }

  private void validateForm(final String validationGroups, final boolean disabled) {
    ValidateBeanCallback validateForm = new ValidateBeanCallback() {
      @Override public void run() {
        SystemEventListener listener = new BeanValidationEventListener(validationGroups, disabled);
        subscribeToViewEvent(PreValidateEvent.class, listener);
        subscribeToViewEvent(PostValidateEvent.class, listener);
      }
    };
    subscribeToRequestBeforePhase(PROCESS_VALIDATIONS, validateForm);
  }

  private static void forEachInputWithMatchingBase(final FacesContext context, UIComponent form, final Object base, final Operation operation) {
    forEachComponent(context).fromRoot(form).ofTypes(EditableValueHolder.class).withHints(SKIP_UNRENDERED).invoke(new Callback.WithArgument<UIComponent>() {
      @Override public void invoke(UIComponent component) {
        ValueExpression valueExpression = component.getValueExpression("value");
        if (valueExpression != null) {
          ValueReference valueReference = getValueReference(context.getELContext(), valueExpression);
          if (valueReference.getBase().equals(base)) {
            operation.run((EditableValueHolder) component, valueReference);
          }
        }
      }
    });
  }

  private static void addCollectingValidator(EditableValueHolder valueHolder, ValueReference valueReference, Map<String, Object> propertyValues) {
    valueHolder.addValidator(new CollectingValidator(propertyValues, valueReference.getProperty().toString()));
  }

  private static void removeCollectingValidator(EditableValueHolder valueHolder) {
    Validator collectingValidator = null;
    for (Validator validator : valueHolder.getValidators()) {
      if (validator instanceof CollectingValidator) {
        collectingValidator = validator;
        break;
      }
    }
    if (collectingValidator != null) {
      valueHolder.removeValidator(collectingValidator);
    }
  }

  private static Copier getCopier(FacesContext context, String copierName) {
    Copier copier = null;
    if (!isEmpty(copierName)) {
      Object expressionResult = evaluateExpressionGet(context, copierName);
      if (expressionResult instanceof Copier) {
        copier = (Copier) expressionResult;
      } else {
        if (expressionResult instanceof String) {
          copier = instance((String) expressionResult);
        }
      }
    }
    if (copier == null) {
      copier = new MultiStrategyCopier();
    }
    return copier;
  }

  @SuppressWarnings(value = { "unchecked", "rawtypes" }) private static void validate(FacesContext context, UIForm form, Object bean, String groups, boolean renderResponseOnFail) {
    List<Class> groupClasses = new ArrayList<Class>();
    for (String group : csvToList(groups)) {
      groupClasses.add(toClass(group));
    }
    Set violationsRaw = Platform.getBeanValidator().validate(bean, groupClasses.toArray(new Class[groupClasses.size()]));
    Set<ConstraintViolation<?>> violations = violationsRaw;
    if (!violations.isEmpty()) {
      context.validationFailed();
      String formId = form.getClientId(context);
      for (ConstraintViolation<?> violation : violations) {
        context.addMessage(formId, createError(violation.getMessage()));
      }
      if (renderResponseOnFail) {
        context.renderResponse();
      }
    }
  }

  public static final class CollectingValidator implements Validator {
    private final Map<String, Object> propertyValues;

    private final String property;

    public CollectingValidator(Map<String, Object> propertyValues, String property) {
      this.propertyValues = propertyValues;
      this.property = property;
    }

    @Override public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
      propertyValues.put(property, value);
    }
  }

  private abstract static class ValidateBeanCallback implements Callback.Void {
    @Override public void invoke() {
      try {
        run();
      } catch (Exception e) {
        logger.log(SEVERE, "Exception occured while doing validation.", e);
        validationFailed();
        renderResponse();
        throw new FacesException(e);
      }
    }

    public abstract void run();
  }

  private abstract static class Operation implements Callback.WithArgument<Object[]> {
    @Override public void invoke(Object[] args) {
      run((EditableValueHolder) args[0], (ValueReference) args[1]);
    }

    public abstract void run(EditableValueHolder valueHolder, ValueReference valueReference);
  }
}