package org.openpnp.machine.reference.wizards;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.openpnp.Translations;
import org.openpnp.gui.components.ComponentDecorators;
import org.openpnp.gui.support.AbstractConfigurationWizard;
import org.openpnp.gui.support.IntegerConverter;
import org.openpnp.machine.reference.ReferencePnpJobProcessor;
import org.openpnp.machine.reference.ReferencePnpJobProcessor.JobOrderHint;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings(value = { "serial" }) public class ReferencePnpJobProcessorConfigurationWizard extends AbstractConfigurationWizard {
  private final ReferencePnpJobProcessor jobProcessor;

  private JComboBox comboBoxJobOrder;

  private JTextField maxVisionRetriesTextField;

  private JCheckBox steppingToNextMotion;

  public ReferencePnpJobProcessorConfigurationWizard(ReferencePnpJobProcessor jobProcessor) {
    this.jobProcessor = jobProcessor;
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    JPanel panelGeneral = new JPanel();
    panelGeneral.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    contentPanel.add(panelGeneral);
    panelGeneral.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC }, new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC }));
    JLabel lblJobOrder = new JLabel(Translations.getString("MachineSetup.JobProcessors.ReferencePnpJobProcessor.Label.JobOrder"));
    panelGeneral.add(lblJobOrder, "2, 2, right, default");
    comboBoxJobOrder = new JComboBox(JobOrderHint.values());
    panelGeneral.add(comboBoxJobOrder, "4, 2");
    JLabel lblMaxVisionRetries = new JLabel(Translations.getString("MachineSetup.JobProcessors.ReferencePnpJobProcessor.Label.MaxVisionRetries"));
    panelGeneral.add(lblMaxVisionRetries, "2, 4, right, default");
    maxVisionRetriesTextField = new JTextField();
    panelGeneral.add(maxVisionRetriesTextField, "4, 4");
    maxVisionRetriesTextField.setColumns(10);
    JLabel lblStepsMotion = new JLabel(Translations.getString("ReferencePnpJobProcessorConfigurationWizard.lblStepsMotion.text"));
    lblStepsMotion.setToolTipText(Translations.getString("ReferencePnpJobProcessorConfigurationWizard.lblStepsMotion.toolTipText"));
    panelGeneral.add(lblStepsMotion, "2, 6, right, default");
    steppingToNextMotion = new JCheckBox();
    panelGeneral.add(steppingToNextMotion, "4, 6");
  }

  @Override public void createBindings() {
    IntegerConverter intConverter = new IntegerConverter();
    addWrappedBinding(jobProcessor, "jobOrder", comboBoxJobOrder, "selectedItem");
    addWrappedBinding(jobProcessor, "maxVisionRetries", maxVisionRetriesTextField, "text", intConverter);
    addWrappedBinding(jobProcessor, "steppingToNextMotion", steppingToNextMotion, "selected");
    ComponentDecorators.decorateWithAutoSelect(maxVisionRetriesTextField);
  }
}