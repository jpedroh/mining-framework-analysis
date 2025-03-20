package org.omnifaces.component.validator;
import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.omnifaces.util.Callback;
import org.omnifaces.util.State;
import org.omnifaces.validator.MultiFieldValidator;

/**
 * <p>
 * The <code>&lt;o:validateOrder&gt;</code> validates if the values of the given {@link UIInput} components as specified
 * in the <code>components</code> attribute are in the order as specified by the <code>type</code> attribute which
 * accepts the following values:
 * <ul>
 * <li><code>lt</code> (default): from least to greatest, without duplicates.</li>
 * <li><code>lte</code>: from least to greatest, allowing duplicates (equal values next to each other).</li>
 * <li><code>gt</code>: from greatest to least, without duplicates.</li>
 * <li><code>gte</code>: from greatest to least, allowing duplicates (equal values next to each other).</li>
 * </ul>
 * <p>
 * This validator has the additional requirement that the to-be-validated values must implement {@link Comparable}.
 * This validator throws an {@link IllegalArgumentException} when one or more of the values do not implement it. Note
 * that when this validator is placed <em>before</em> all of the components, then it will only compare the raw
 * unconverted submitted string values, not the converted object values. If you need to compare by the converted object
 * values, then you need to place this validator <em>after</em> all of the components.
 * <p>
 * The default message is
 * <blockquote>{0}: Please fill out the values of all those fields in order</blockquote>
 * <p>
 * For general usage instructions, refer {@link ValidateMultipleFields} documentation.
 *
 * @author Bauke Scholtz
 */
@FacesComponent(value = ValidateOrder.COMPONENT_TYPE) @SuppressWarnings(value = { "unchecked", "rawtypes" }) public class ValidateOrder extends ValidateMultipleFields {
  /** The standard component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.validator.ValidateOrder";

  private enum Type {
    LT(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
      private static final long serialVersionUID = 1L;

      @Override public Boolean invoke(List<Comparable> values) {
        return new ArrayList<>(
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/validator/ValidateOrder.java/left.java
        new TreeSet<Comparable>(values)
=======
        new TreeSet<>(values)
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/validator/ValidateOrder.java/right.java
        ).equals(values);
      }
    }),
    LTE(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
      private static final long serialVersionUID = 1L;

      @Override public Boolean invoke(List<Comparable> values) {
        List<Comparable> sortedValues = new ArrayList<>(values);
        Collections.sort(sortedValues);
        return sortedValues.equals(values);
      }
    }),
    GT(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
      private static final long serialVersionUID = 1L;

      @Override public Boolean invoke(List<Comparable> values) {
        List<Comparable> sortedValues = new ArrayList<>(
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/validator/ValidateOrder.java/left.java
        new TreeSet<Comparable>(values)
=======
        new TreeSet<>(values)
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/validator/ValidateOrder.java/right.java
        );
        Collections.reverse(sortedValues);
        return sortedValues.equals(values);
      }
    }),
    GTE(new Callback.ReturningWithArgument<Boolean, List<Comparable>>() {
      private static final long serialVersionUID = 1L;

      @Override public Boolean invoke(List<Comparable> values) {
        List<Comparable> sortedValues = new ArrayList<>(values);
        Collections.sort(sortedValues, Collections.reverseOrder());
        return sortedValues.equals(values);
      }
    })
    ;

    private Callback.ReturningWithArgument<Boolean, List<Comparable>> callback;

    private Type(Callback.ReturningWithArgument<Boolean, List<Comparable>> callback) {
      this.callback = callback;
    }

    public boolean validateOrder(List<Comparable> values) {
      return callback.invoke(values);
    }
  }

  private static final String DEFAULT_TYPE = Type.LT.name();

  private static final String ERROR_INVALID_TYPE = "Invalid type \'%s\'. Only \'lt\', \'lte\', \'gt\' and \'gte\' are allowed.";

  private static final String ERROR_VALUES_NOT_COMPARABLE = "All values must implement java.lang.Comparable.";

  private enum PropertyKeys {
    type
  }

  private final State state = new State(getStateHelper());

  /**
	 * Validate if all values are in specified order.
	 */
  @Override public boolean validateValues(FacesContext context, List<UIInput> components, List<Object> values) {
    try {
      Object tmp = values;
      List<Comparable> comparableValues = 
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/validator/ValidateOrder.java/left.java
      new ArrayList<Comparable>((List<Comparable>) tmp)
=======
      new ArrayList<>((List<Comparable>) tmp)
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/validator/ValidateOrder.java/right.java
      ;
      comparableValues.removeAll(asList(null, ""));
      return Type.valueOf(getType().toUpperCase()).validateOrder(comparableValues);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException(ERROR_VALUES_NOT_COMPARABLE, e);
    }
  }

  /**
	 * Returns the ordering type to be used.
	 * @return The ordering type to be used.
	 */
  public String getType() {
    return state.get(PropertyKeys.type, DEFAULT_TYPE);
  }

  /**
	 * Sets the ordering type to be used.
	 * @param type The ordering type to be used.
	 */
  public void setType(String type) {
    try {
      Type.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(String.format(ERROR_INVALID_TYPE, type), e);
    }
    state.put(PropertyKeys.type, type);
  }
}