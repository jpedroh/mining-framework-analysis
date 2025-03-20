package org.omnifaces.component.validator;
import static org.omnifaces.util.Utils.isEmpty;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.omnifaces.validator.MultiFieldValidator;

/**
 * <p>
 * The <code>&lt;o:validateOneOrMore&gt;</code> validates if at least ONE of the given {@link UIInput} components has
 * been filled out.
 * <p>
 * The default message is
 * <blockquote>{0}: Please fill out at least one of those fields</blockquote>
 * <p>
 * For general usage instructions, refer {@link ValidateMultipleFields} documentation.
 *
 * @author Bauke Scholtz
 */
@FacesComponent(value = ValidateOneOrMore.COMPONENT_TYPE) public class ValidateOneOrMore extends ValidateMultipleFields {
  /** The standard component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.validator.ValidateOneOrMore";

  /**
	 * Validate if at least one is filled out.
	 */
  @Override public boolean validateValues(FacesContext context, List<UIInput> inputs, List<Object> values) {
    for (Object value : values) {
      if (!isEmpty(value)) {
        return true;
      }
    }
    return false;
  }
}