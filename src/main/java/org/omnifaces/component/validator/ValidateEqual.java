package org.omnifaces.component.validator;
import java.util.HashSet;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.omnifaces.validator.MultiFieldValidator;

/**
 * <p>
 * The <code>&lt;o:validateEqual&gt;</code> validates if ALL of the given {@link UIInput} components have the same
 * value.
 * <p>
 * The default message is
 * <blockquote>{0}: Please fill out the same value for all of those fields</blockquote>
 * <p>
 * For general usage instructions, refer {@link ValidateMultipleFields} documentation.
 *
 * @author Bauke Scholtz
 */
@FacesComponent(value = ValidateEqual.COMPONENT_TYPE) public class ValidateEqual extends ValidateMultipleFields {
  /** The standard component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.validator.ValidateEqual";

  /**
	 * Validate if all values are equal.
	 */
  @Override public boolean validateValues(FacesContext context, List<UIInput> inputs, List<Object> values) {
    return (new HashSet<>(values).size() == 1);
  }
}