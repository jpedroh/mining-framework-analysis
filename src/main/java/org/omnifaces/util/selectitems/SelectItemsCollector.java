package org.omnifaces.util.selectitems;
import static org.omnifaces.util.Utils.isEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import org.omnifaces.el.ScopedRunner;
import org.omnifaces.model.ExtendedSelectItem;
import org.omnifaces.util.Callback;

/**
 * Collection of utility methods for collecting {@link SelectItem} instances from various sources.
 *
 * @author Arjan Tijms
 *
 */
public final class SelectItemsCollector {
  private SelectItemsCollector() {
  }

  private static final String ERROR_UNKNOWN_SELECT_TYPE = "A value expression of type \'%s\' is disallowed for a select item";

  /**
	 * This method gets all select items that are expressed via {@link UISelectItem} or {@link UISelectItems}
	 * children of the given parent component.
	 * <p>
	 * Note that if {@link SelectItemGroup} instances are present then those will be inserted directly in the returned list
	 * and the using code still has to iterate over its children recursively to obtain all separate {@link SelectItem} instances.
	 *
	 * @param parent the parent whose children are scanned
	 * @param context The involved faces context.
	 * @return list of select items obtained from parent's children.
	 */
  public static List<SelectItem> collectFromParent(FacesContext context, UIComponent parent) {
    List<SelectItem> selectItems = new ArrayList<>();
    for (UIComponent child : parent.getChildren()) {
      if (child instanceof UISelectItem) {
        UISelectItem uiSelectItem = (UISelectItem) child;
        Object value = uiSelectItem.getValue();
        if (value instanceof SelectItem) {
          selectItems.add((SelectItem) value);
        } else {
          if (uiSelectItem.getValue() == null) {
            selectItems.add(new ExtendedSelectItem(uiSelectItem));
          } else {
            throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_SELECT_TYPE, value.getClass().toString()));
          }
        }
      } else {
        if (child instanceof UISelectItems) {
          UISelectItems uiSelectItems = (UISelectItems) child;
          Object value = uiSelectItems.getValue();
          if (value instanceof SelectItem) {
            selectItems.add((SelectItem) value);
          } else {
            if (value instanceof Object[]) {
              selectItems.addAll(collectFromUISelectItemsIterator(context, uiSelectItems, Arrays.asList((Object[]) value)));
            } else {
              if (value instanceof Iterable) {
                selectItems.addAll(collectFromUISelectItemsIterator(context, uiSelectItems, (Iterable<?>) value));
              } else {
                if (value instanceof Map) {
                  selectItems.addAll(SelectItemsBuilder.fromMap((Map<?, ?>) value));
                } else {
                  throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_SELECT_TYPE, value.getClass().toString()));
                }
              }
            }
          }
        }
      }
    }
    return selectItems;
  }

  /**
	 * This method runs the algorithm expressed by a <code>UISelectItems</code> component that uses the <code>var</code> iterator construct to generate
	 * a list of <code>SelectItem</code>s.
	 *
	 * @param uiSelectItems The involved select items component.
	 * @param items The available select items.
	 * @param facesContext The involved faces context.
	 * @return list of <code>SelectItem</code> obtained from the given parameters
	 */
  public static List<SelectItem> collectFromUISelectItemsIterator(FacesContext facesContext, UISelectItems uiSelectItems, Iterable<?> items) {
    final List<SelectItem> selectItems = new ArrayList<>();
    final Map<String, Object> attributes = uiSelectItems.getAttributes();
    String var = (String) attributes.get("var");
    ScopedRunner scopedRunner = new ScopedRunner(facesContext);
    for (final Object item : items) {
      if (item instanceof SelectItem) {
        selectItems.add((SelectItem) item);
        continue;
      }
      if (!isEmpty(var)) {
        scopedRunner.with(var, item);
      }
      scopedRunner.invoke(new Callback.Void() {
        private static final long serialVersionUID = 1L;

        @Override public void invoke() {
          Object itemValue = getItemValue(attributes, item);
          Object noSelectionValue = attributes.get("noSelectionValue");
          boolean itemValueIsNoSelectionValue = noSelectionValue != null && noSelectionValue.equals(itemValue);
          selectItems.add(new SelectItem(itemValue, getItemLabel(attributes, itemValue), getItemDescription(attributes), getBooleanAttribute(attributes, "itemDisabled", false), getBooleanAttribute(attributes, "itemLabelEscaped", true), getBooleanAttribute(attributes, "noSelectionOption", false) || itemValueIsNoSelectionValue));
        }
      });
    }
    return selectItems;
  }

  /**
	 * Gets the optional value. It defaults to the item itself if not specified.
	 *
	 * @param attributes the attributes from which the label is fetched.
	 * @param item default value if no item value present
	 * @return the value, or the item if none is present
	 */
  private static Object getItemValue(Map<String, Object> attributes, Object item) {
    Object itemValue = attributes.get("itemValue");
    if (itemValue == null) {
      itemValue = item;
    }
    return itemValue;
  }

  /**
	 * Gets the optional label. It defaults to the item value if not specified.
	 *
	 * @param attributes the attributes from which the label is fetched.
	 * @param itemValue default value if no item value present
	 * @return the label, or the item value if none present
	 */
  private static String getItemLabel(Map<String, Object> attributes, Object itemValue) {
    Object itemLabelObj = attributes.get("itemLabel");
    String itemLabel = null;
    if (itemLabelObj != null) {
      itemLabel = itemLabelObj.toString();
    } else {
      itemLabel = itemValue.toString();
    }
    return itemLabel;
  }

  /**
	 * Gets the optional description.
	 *
	 * @param attributes the attributes from which the description is fetched.
	 * @return the description, or null if none present.
	 */
  private static String getItemDescription(Map<String, Object> attributes) {
    Object itemDescriptionObj = attributes.get("itemDescription");
    String itemDescription = null;
    if (itemDescriptionObj != null) {
      itemDescription = itemDescriptionObj.toString();
    }
    return itemDescription;
  }

  /**
	 * Gets the name boolean attribute. It defaults to <code>false</code> if not specified.
	 * @param attributes the attributes from which the attribute is fetched.
	 * @param key name of the attribute
	 * @return the boolean represented by the attribute or false if there's no such attribute
	 */
  private static boolean getBooleanAttribute(Map<String, Object> attributes, String key, boolean defaultValue) {
    Object valueObj = attributes.get(key);
    boolean value = defaultValue;
    if (valueObj != null) {
      value = Boolean.parseBoolean(valueObj.toString());
    }
    return value;
  }
}