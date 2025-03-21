package com.willwinder.ugs.nbp.designer.entities.controls;
import com.willwinder.ugs.nbp.designer.entities.AbstractEntity;
import com.willwinder.ugs.nbp.designer.entities.selection.SelectionManager;
import com.willwinder.ugs.nbp.designer.model.Size;
import java.awt.*;

/**
 * @author Joacim Breiler
 */
public abstract class AbstractControl extends AbstractEntity implements Control {
  private final SelectionManager selectionManager;

  protected AbstractControl(SelectionManager selectionManager) {
    this.selectionManager = selectionManager;
    addListener(this);
  }

  @Override public void setSize(Size size) {
  }

  @Override public Shape getShape() {
    return selectionManager.getShape();
  }

  @Override public Shape getRelativeShape() {
    return selectionManager.getRelativeShape();
  }

  @Override public SelectionManager getSelectionManager() {
    return selectionManager;
  }

  @Override public void destroy() {
    removeListener(this);
    super.destroy();
  }
}