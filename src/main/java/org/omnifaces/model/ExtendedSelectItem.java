package org.omnifaces.model;
import javax.faces.component.UISelectItem;
import javax.faces.model.SelectItem;

/**
 * This class extends the default {@link SelectItem} with several convenience methods.
 *
 * @author Arjan Tijms
 *
 */
public class ExtendedSelectItem extends SelectItem {
  private static final long serialVersionUID = -3266902732567582732L;

  /**
     * <p>Construct a <code>SelectItem</code> with no initialized property
     * values.</p>
     */
  public ExtendedSelectItem() {
  }

  /**
     * <p>Construct a <code>SelectItem</code> with property values initialized from the corresponding
     * properties on the <code>UISelectItem</code>.
     * </p>
     * @param uiSelectItem The UI select item.
     */
  public ExtendedSelectItem(UISelectItem uiSelectItem) {
    super(uiSelectItem.getItemValue(), uiSelectItem.getItemLabel() != null ? uiSelectItem.getItemLabel() : uiSelectItem.getItemValue().toString(), uiSelectItem.getItemDescription(), uiSelectItem.isItemDisabled(), uiSelectItem.isItemEscaped(), uiSelectItem.isNoSelectionOption());
  }
}