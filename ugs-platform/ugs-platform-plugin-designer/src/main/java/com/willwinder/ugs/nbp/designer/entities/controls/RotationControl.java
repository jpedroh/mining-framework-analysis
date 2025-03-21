package com.willwinder.ugs.nbp.designer.entities.controls;
import com.willwinder.ugs.nbp.designer.Utils;
import com.willwinder.ugs.nbp.designer.actions.RotateAction;
import com.willwinder.ugs.nbp.designer.actions.UndoManager;
import com.willwinder.ugs.nbp.designer.entities.Entity;
import com.willwinder.ugs.nbp.designer.entities.EntityEvent;
import com.willwinder.ugs.nbp.designer.entities.EventType;
import com.willwinder.ugs.nbp.designer.entities.selection.SelectionManager;
import com.willwinder.ugs.nbp.designer.gui.Colors;
import com.willwinder.ugs.nbp.designer.gui.MouseEntityEvent;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joacim Breiler
 */
public class RotationControl extends AbstractControl {
  public static final int SIZE = 6;

  public static final int MARGIN = 12;

  private final Shape shape;

  private boolean isHovered;

  private Point2D startPosition = new Point2D.Double();

  private double startRotation = 0d;

  private Point2D center;

  public RotationControl(SelectionManager selectionManager) {
    super(selectionManager);
    shape = new Ellipse2D.Double(0, 0, SIZE, SIZE);
  }

  private void updatePosition() {
    AffineTransform transform = getSelectionManager().getTransform();
    Rectangle2D bounds = getSelectionManager().getRelativeShape().getBounds2D();
    transform.translate(bounds.getX(), bounds.getY() + bounds.getHeight());
    transform.translate(bounds.getWidth() / 2 - (SIZE / 2d), MARGIN);
    Point2D result = new Point2D.Double();
    transform.transform(new Point2D.Double(0, 0), result);
    transform = new AffineTransform();
    transform.translate(result.getX(), result.getY());
    setTransform(transform);
  }

  @Override public Shape getShape() {
    return getTransform().createTransformedShape(shape);
  }

  @Override public Shape getRelativeShape() {
    return shape;
  }

  @Override public void render(Graphics2D graphics) {
    updatePosition();
    graphics.setStroke(new BasicStroke(1f));
    graphics.setColor(Colors.CONTROL_HANDLE);
    graphics.fill(getShape());

<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/059969c5f7faecead3cb81b38f7bd055a9162b0b/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/entities/controls/RotationControl.java/left.java
    if (isHovered) {
      graphics.setColor(Colors.CONTROL_BORDER);
      graphics.draw(getShape());
      double centerX = getSelectionManager().getCenter().getX();
      double centerY = getSelectionManager().getCenter().getY();
      graphics.draw(new Line2D.Double(centerX - (SIZE / 2d), centerY, centerX + (SIZE / 2d), centerY));
      graphics.draw(new Line2D.Double(centerX, centerY - (SIZE / 2d), centerX, centerY + (SIZE / 2d)));
    }
=======
    double centerX = getSelectionManager().getCenter().getX();
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/059969c5f7faecead3cb81b38f7bd055a9162b0b/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/entities/controls/RotationControl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    double centerY = getSelectionManager().getCenter().getY();
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/059969c5f7faecead3cb81b38f7bd055a9162b0b/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/entities/controls/RotationControl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    graphics.setStroke(new BasicStroke(0.8f));
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/059969c5f7faecead3cb81b38f7bd055a9162b0b/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/entities/controls/RotationControl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    graphics.draw(new Line2D.Double(centerX - (SIZE / 2d), centerY, centerX + (SIZE / 2d), centerY));
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/059969c5f7faecead3cb81b38f7bd055a9162b0b/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/entities/controls/RotationControl.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    graphics.draw(new Line2D.Double(centerX, centerY - (SIZE / 2d), centerX, centerY + (SIZE / 2d)));
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/059969c5f7faecead3cb81b38f7bd055a9162b0b/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/entities/controls/RotationControl.java/right.java
  }

  @Override public void onEvent(EntityEvent entityEvent) {
    if (entityEvent instanceof MouseEntityEvent && entityEvent.getTarget() == this) {
      MouseEntityEvent mouseShapeEvent = (MouseEntityEvent) entityEvent;
      Point2D mousePosition = mouseShapeEvent.getCurrentMousePosition();
      Entity target = getSelectionManager();
      if (mouseShapeEvent.getType() == EventType.MOUSE_PRESSED) {
        startPosition = mousePosition;
        startRotation = target.getRotation();
        center = target.getCenter();
      } else {
        if (mouseShapeEvent.getType() == EventType.MOUSE_DRAGGED) {
          int decimals = 0;
          if (mouseShapeEvent.isAltPressed()) {
            decimals = 1;
          }
          double deltaAngle = Utils.calcRotationAngleInDegrees(target.getCenter(), startPosition) - Utils.calcRotationAngleInDegrees(target.getCenter(), mousePosition);
          double fractionToRound = deltaAngle + target.getRotation() - Utils.roundToDecimals(deltaAngle + target.getRotation(), decimals);
          deltaAngle = deltaAngle - fractionToRound;
          target.rotate(center, deltaAngle);
          startPosition = mousePosition;
        } else {
          if (mouseShapeEvent.getType() == EventType.MOUSE_RELEASED) {
            double totalRotation = (startRotation + target.getRotation());
            addUndoAction(center, totalRotation, target);
          } else {
            if (mouseShapeEvent.getType() == EventType.MOUSE_IN) {
              isHovered = true;
            } else {
              if (mouseShapeEvent.getType() == EventType.MOUSE_OUT) {
                isHovered = false;
              }
            }
          }
        }
      }
    }
  }

  private void addUndoAction(Point2D center, double rotation, Entity target) {
    UndoManager undoManager = CentralLookup.getDefault().lookup(UndoManager.class);
    if (undoManager != null) {
      List<Entity> entityList = new ArrayList<>();
      if (target instanceof SelectionManager) {
        entityList.addAll(((SelectionManager) target).getSelection());
      } else {
        entityList.add(target);
      }
      undoManager.addAction(new RotateAction(entityList, center, rotation));
    }
  }
}