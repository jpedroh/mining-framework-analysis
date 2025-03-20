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
import static org.omnifaces.util.Events.addBeforePhaseListener;
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

/**
 * <p>
 * The <code>&lt;o:validateBean&gt;</code> allows the developer to control bean validation on a per-{@link UICommand}
 * or {@link UIInput} component basis. The standard <code>&lt;f:validateBean&gt;</code> only allows that on a per-form
 * or a per-request basis (by using multiple tags and conditional EL expressions in its attributes) which may end up in
 * boilerplate code.
 *
 * <h3>Usage</h3>
 * <p>
 * Some examples:
 * <pre>
 * &lt;h:commandButton value="submit" action="#{bean.submit}"&gt;
 *     &lt;o:validateBean validationGroups="javax.validation.groups.Default,com.example.MyGroup"/&gt;
 * &lt;/h:commandButton&gt;
 * </pre>
 * <pre>
 * &lt;h:selectOneMenu value="#{bean.selectedItem}"&gt;
 *     &lt;f:selectItems value="#{bean.availableItems}"
 *     &lt;o:validateBean disabled="true" /&gt;
 *     &lt;f:ajax execute="@form" listener="#{bean.itemChanged}" render="@form" /&gt;
 * &lt;/h:commandButton&gt;
 * </pre>
 *
 * @author Bauke Scholtz
 */
public class ValidateBean extends TagHandler {
  private static final String ERROR_INVALID_PARENT = "o:validateBean parent must be an instance of UIInput or UICommand.";

  private static final Logger logger = Logger.getLogger(ValidateBean.class.getName());

  private static final String ERROR_MISSING_FORM = "o:validateBean must be nested in an UIForm.";

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

  /**
	 * The tag constructor.
	 * @param config The tag config.
	 */
  public ValidateBean(TagConfig config) {
    super(config);

<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/ValidateBean.java/left.java
    validationGroups = getAttribute("validationGroups");
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/ValidateBean.java/left.java
    disabled = getAttribute("disabled");
=======
>>>>>>> Unknown file: This is a bug in JDime.
  }

  /**
	 *
	 */
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

<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/ValidateBean.java/left.java
    addBeforePhaseListener(PhaseId.PROCESS_VALIDATIONS, new Callback.Void() {
      @Override public void invoke() {
        if (hasInvokedSubmit(parent)) {
          SystemEventListener listener = new BeanValidationEventListener(validationGroups, disabled);
          subscribeToViewEvent(PreValidateEvent.class, listener);
          subscribeToViewEvent(PostValidateEvent.class, listener);
        }
      }
    })
=======
    subscribeToRequestAfterPhase(RESTORE_VIEW, new Callback.Void() {
      private static final long serialVersionUID = 1L;

      @Override public void invoke() {
        processValidateBean(parent);
      }
    })
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/taghandler/ValidateBean.java/right.java
    ;
  }

  /**
	 * Check if the given component has participated in submitting the current form or action and if so, then perform
	 * the bean validation depending on the attributes set.
	 * @param component The involved component.
	 * @throws IllegalArgumentException When the parent form is missing.
	 */
  protected void processValidateBean(UIComponent component) {
    UIForm form = (component instanceof UIForm) ? ((UIForm) component) : getClosestParent(component, UIForm.class);
    if (form == null) {
      throw new IllegalArgumentException(ERROR_MISSING_FORM);
    }
    if (!form.equals(getCurrentForm()) || (component instanceof UICommand && !hasInvokedSubmit(component))) {
      return;
    }
    Object bean = (value != null) ? value.getValue(getELContext()) : null;
    if (bean != null) {
      if (!disabled) {
        switch (method) {
          case validateActual:
          validateActualBean(form, bean, groups);
          break;
          case validateCopy:
          validateCopiedBean(form, bean, copier, groups);
          break;
        }
      }
    } else {
      validateForm(groups, disabled);
    }
  }

  /**
	 * After update model values phase, validate actual bean. But don't proceed to render response on fail.
	 */
  private void validateActualBean(final UIForm form, final Object bean, final String groups) {
    ValidateBeanCallback validateActualBean = new ValidateBeanCallback() {
      private static final long serialVersionUID = 1L;

      @Override public void run() {
        FacesContext context = FacesContext.getCurrentInstance();
        validate(context, form, bean, groups, false);
      }
    };
    subscribeToRequestAfterPhase(UPDATE_MODEL_VALUES, validateActualBean);
  }

  /**
	 * Before validations phase of current request, collect all bean properties.
	 *
	 * After validations phase of current request, create a copy of the bean, set all collected properties there,
	 * then validate copied bean and proceed to render response on fail.
	 */
  private void validateCopiedBean(final UIForm form, final Object bean, final String copier, final String groups) {
    final Map<String, Object> properties = new HashMap<>();
    ValidateBeanCallback collectBeanProperties = new ValidateBeanCallback() {
      private static final long serialVersionUID = 1L;

      @Override public void run() {
        FacesContext context = FacesContext.getCurrentInstance();
        forEachInputWithMatchingBase(context, form, bean, new Operation() {
          private static final long serialVersionUID = 1L;

          @Override public void run(EditableValueHolder v, ValueReference vr) {
            addCollectingValidator(v, vr, properties);
          }
        });
      }
    };
    ValidateBeanCallback checkConstraints = new ValidateBeanCallback() {
      private static final long serialVersionUID = 1L;

      @Override public void run() {
        FacesContext context = FacesContext.getCurrentInstance();
        forEachInputWithMatchingBase(context, form, bean, new Operation() {
          private static final long serialVersionUID = 1L;

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

  /**
	 * Before validations phase of current request, subscribe the {@link BeanValidationEventListener} to validate the form based on groups.
	 */
  private void validateForm(final String validationGroups, final boolean disabled) {
    ValidateBeanCallback validateForm = new ValidateBeanCallback() {
      private static final long serialVersionUID = 1L;

      @Override public void run() {
        SystemEventListener listener = new BeanValidationEventListener(validationGroups, disabled);
        subscribeToViewEvent(PreValidateEvent.class, listener);
        subscribeToViewEvent(PostValidateEvent.class, listener);
      }
    };
    subscribeToRequestBeforePhase(PROCESS_VALIDATIONS, validateForm);
  }

  private static void forEachInputWithMatchingBase(final FacesContext context, UIComponent form, final Object base, final Operation operation) {
    forEachComponent(context).fromRoot(form).ofTypes(EditableValueHolder.class).invoke(new Callback.WithArgument<UIComponent>() {
      private static final long serialVersionUID = 1L;

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
    List<Class> groupClasses = new ArrayList<>();
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
    private static final long serialVersionUID = 1L;

    @Override public void invoke() {
      try {
        run();
      } catch (Exception e) {
        logger.log(SEVERE, "Exception occured while doing validation.", e);
        validationFailed();
        renderResponse();
        throw e;
      }
    }

    public abstract void run();
  }

  private abstract static class Operation implements Callback.WithArgument<Object[]> {
    private static final long serialVersionUID = 1L;

    @Override public void invoke(Object[] args) {
      run((EditableValueHolder) args[0], (ValueReference) args[1]);
    }

    public abstract void run(EditableValueHolder valueHolder, ValueReference valueReference);
  }
}