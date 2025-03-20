package org.omnifaces.component.validator;
import java.util.HashSet;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.omnifaces.validator.MultiFieldValidator;

/**
 * <p>
 * The <code>&lt;o:validateUnique&gt;</code> validates if ALL of the given {@link UIInput} components have an unique
 * value.
 * <p>
 * The default message is
 * <blockquote>{0}: Please fill out an unique value for all of those fields</blockquote>
 * <p>
 * For general usage instructions, refer {@link ValidateMultipleFields} documentation.
 *
 * @author Bauke Scholtz
 */
@FacesComponent(value = ValidateUnique.COMPONENT_TYPE) public class ValidateUnique extends ValidateMultipleFields {
  /** The standard component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.validator.ValidateUnique";

  /**
	 * Validate if all values are unique.
	 */
  @Override public boolean validateValues(FacesContext context, List<UIInput> inputs, List<Object> values) {
    return (new HashSet<>(values).size() == inputs.size());
  }
}