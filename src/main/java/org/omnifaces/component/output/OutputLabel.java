package org.omnifaces.component.output;
import static org.omnifaces.util.Components.findComponentRelatively;
import static org.omnifaces.util.Components.getOptionalLabel;
import static org.omnifaces.util.Utils.isEmpty;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PostRestoreStateEvent;

/**
 * <p>
 * The <code>&lt;o:outputLabel&gt;</code> is a component that extends the standard <code>&lt;h:outputLabel&gt;</code>
 * with support for automatically setting its value as the label of the component identified by its <code>for</code>
 * attribute (if any). This way there's no need to duplicate the very same label into the <code>label</code> attribute
 * of the input component. After submitting the form without having entered a value, a validation message is posted that
 * should contain the label printed before the input instead of some generated ID.
 * <p>
 * You can use it the same way as <code>&lt;h:outputLabel&gt;</code>, you only need to change <code>h:</code> into
 * <code>o:</code>.
 *
 * @author Arjan Tijms
 */
@FacesComponent(value = OutputLabel.COMPONENT_TYPE) public class OutputLabel extends HtmlOutputLabel {
  public static final String COMPONENT_TYPE = "org.omnifaces.component.output.OutputLabel";

  private static final String ERROR_FOR_COMPONENT_NOT_FOUND = "A component with Id \'%s\' as specified by the for attribute of the OutputLabel with Id \'%s\' could not be found.";

  @Override public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
    if (event instanceof PostRestoreStateEvent) {
      String forValue = (String) getAttributes().get("for");
      if (!isEmpty(forValue)) {
        UIComponent forComponent = findComponentRelatively(this, forValue);
        if (forComponent == null) {
          throw new IllegalArgumentException(String.format(ERROR_FOR_COMPONENT_NOT_FOUND, forValue, getId()));
        }
        if (getOptionalLabel(forComponent) == null) {
          ValueExpression valueExpression = getValueExpression("value");
          if (valueExpression != null) {
            forComponent.setValueExpression("label", valueExpression);
          } else {
            forComponent.getAttributes().put("label", getValue());
          }
        }
      }
    }
  }
}