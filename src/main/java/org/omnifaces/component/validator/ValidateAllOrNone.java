package org.omnifaces.component.validator;
import static org.omnifaces.util.Utils.isEmpty;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.omnifaces.validator.MultiFieldValidator;

/**
 * <p>
 * The <code>&lt;o:validateAllOrNone&gt;</code> validates if at least ALL of the given {@link UIInput} components have
 * been filled out or that NONE of the given <code>UIInput</code> components have been filled out.
 * <p>
 * The default message is
 * <blockquote>{0}: Please fill out all or none of those fields</blockquote>
 * <p>
 * For general usage instructions, refer {@link ValidateMultipleFields} documentation.
 *
 * @author Bauke Scholtz
 */
@FacesComponent(value = ValidateAllOrNone.COMPONENT_TYPE) public class ValidateAllOrNone extends ValidateMultipleFields {
  /** The standard component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.validator.ValidateAllOrNone";

  /**
	 * Validate if all or none is filled out.
	 */
  @Override public boolean validateValues(FacesContext context, List<UIInput> inputs, List<Object> values) {
    boolean hasValue = false;
    boolean hasNoValue = false;
    for (Object value : values) {
      boolean currentHasValue = !isEmpty(value);
      hasValue |= currentHasValue;
      hasNoValue |= !currentHasValue;
    }
    return (hasValue != hasNoValue);
  }

  /**
	 * In an invalidating case, invalidate only those inputs which have an empty value.
	 */
  @Override protected boolean shouldInvalidateInput(FacesContext context, UIInput input, Object value) {
    return isEmpty(value);
  }
}