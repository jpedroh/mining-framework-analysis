package org.omnifaces.component.validator;
import static org.omnifaces.util.Utils.isEmpty;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.omnifaces.validator.MultiFieldValidator;

/**
 * <p>
 * The <code>&lt;o:validateOneOrNone&gt;</code> validates if ONLY ONE of the given {@link UIInput} components has been
 * filled out or that NONE of the given {@link UIInput} components have been filled out.
 * <p>
 * The default message is
 * <blockquote>{0}: Please fill out only one or none of those fields</blockquote>
 * <p>
 * For general usage instructions, refer {@link ValidateMultipleFields} documentation.
 *
 * @author Bauke Scholtz
 * @since 1.2
 */
@FacesComponent(value = ValidateOneOrNone.COMPONENT_TYPE) public class ValidateOneOrNone extends ValidateMultipleFields {
  /** The standard component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.validator.ValidateOneOrNone";

  /**
	 * Validate if only one or none is filled out.
	 */
  @Override public boolean validateValues(FacesContext context, List<UIInput> inputs, List<Object> values) {
    boolean hasValue = false;
    for (Object value : values) {
      if (!isEmpty(value)) {
        if (hasValue) {
          return false;
        }
        hasValue = true;
      }
    }
    return true;
  }
}