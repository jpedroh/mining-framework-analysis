package com.willwinder.ugs.platform.surfacescanner;
import com.willwinder.ugs.nbm.visualizer.shared.RenderableUtils;
import com.willwinder.ugs.nbp.lib.Mode;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.ugs.nbp.lib.services.TopComponentLocalizer;
import com.willwinder.ugs.platform.surfacescanner.renderable.AutoLevelPreview;
import com.willwinder.ugs.platform.surfacescanner.ui.AutoLevelerPanel;
import com.willwinder.ugs.platform.surfacescanner.ui.AutoLevelerToolbar;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.events.*;
import com.willwinder.universalgcodesender.utils.GUIHelpers;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.modules.OnStart;
import org.openide.windows.TopComponent;
import java.awt.*;
import static com.willwinder.ugs.nbp.lib.services.LocalizingService.lang;

@TopComponent.Description(preferredID = "AutoLevelerTopComponent") @TopComponent.Registration(mode = Mode.OUTPUT, openAtStartup = false) @ActionID(category = AutoLevelerTopComponent.AutoLevelerCategory, id = AutoLevelerTopComponent.AutoLevelerActionId) @ActionReference(path = LocalizingService.MENU_WINDOW_PLUGIN) @TopComponent.OpenActionRegistration(displayName = "<Not localized:AutoLevelerTopComponent>", preferredID = "AutoLevelerTopComponent") public final class AutoLevelerTopComponent extends TopComponent implements UGSEventListener {
  public final static String AutoLevelerTitle = Localization.getString("platform.window.autoleveler", lang);

  private BackendAPI backend;

  public final static String AutoLevelerActionId = "com.willwinder.ugs.platform.surfacescanner.AutoLevelerTopComponent";

  private MeshLevelManager meshLevelManager;

  public final static String AutoLevelerCategory = LocalizingService.CATEGORY_WINDOW;

  private AutoLevelPreview autoLevelPreview;

  private SurfaceScanner scanner;

  private AutoLevelerPanel autoLevelerPanel;

  public AutoLevelerTopComponent() {
    setName(AutoLevelerTitle);
  }

  @OnStart public static class Localizer extends TopComponentLocalizer {
    public Localizer() {
      super(AutoLevelerCategory, AutoLevelerActionId, AutoLevelerTitle);
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private void updateSettings() {
    this.bulkChanges = true;
    boolean isSettingChange = false;
    AutoLevelSettings als = settings.getAutoLevelSettings();
    if (getValue(this.stepResolution) != als.stepResolution) {
      this.stepResolution.setValue(als.stepResolution);
      isSettingChange = true;
    }
    if (getValue(this.zRetract) != als.zRetract) {
      this.zRetract.setValue(als.zRetract);
      isSettingChange = true;
    }
    if (getValue(this.zSurface) != als.zSurface) {
      this.zSurface.setValue(als.zSurface);
      isSettingChange = true;
    }
    this.bulkChanges = false;
    if (isSettingChange) {
      this.stateChanged(null);
    }
  }
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


  @Override public void UGSEvent(UGSEvent evt) {
    if (evt instanceof ProbeEvent) {
      try {
        scanner.handleEvent((ProbeEvent) evt);
      } catch (Exception e) {
        e.printStackTrace();
        GUIHelpers.displayErrorDialog(Localization.getString("autoleveler.probe-failed"));
      } finally {
        updatePreview();
      }
    } else {
      if (evt instanceof SettingChangedEvent) {
        autoLevelerPanel.setSettings(backend.getSettings().getAutoLevelSettings());
        autoLevelerPanel.setUnits(backend.getSettings().getPreferredUnits());
      } else {
        if (evt instanceof FileStateEvent) {
          FileState fileState = ((FileStateEvent) evt).getFileState();
          if (fileState == FileState.OPENING_FILE) {
            updatePreview();
          }
        } else {
          if (evt instanceof ControllerStatusEvent) {
            boolean isIdle = (backend.isConnected() && backend.isIdle()) || !backend.isConnected();
            autoLevelerPanel.setEnabled(isIdle);
          }
        }
      }
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private void updateScanner() {
    if (scanner.isValid()) {
      int result = JOptionPane.showConfirmDialog(new Frame(), Localization.getString("autoleveler.panel.overwrite"), Localization.getString("AutoLevelerTitle"), JOptionPane.YES_NO_OPTION);
      if (result != JOptionPane.YES_OPTION) {
        return;
      }
    }
    Settings.AutoLevelSettings autoLevelerSettings = this.settings.getAutoLevelSettings();
    autoLevelerSettings.stepResolution = getValue(this.stepResolution);
    autoLevelerSettings.zRetract = getValue(this.zRetract);
    autoLevelerSettings.zSurface = getValue(this.zSurface);
    settings.setAutoLevelSettings(autoLevelerSettings);
    double xOff = autoLevelerSettings.autoLevelProbeOffset.x;
    double yOff = autoLevelerSettings.autoLevelProbeOffset.y;
    Position corner1 = new Position(getValue(xMin) + xOff, getValue(yMin) + yOff, getValue(zMin), backend.getSettings().getPreferredUnits());
    Position corner2 = new Position(getValue(xMax) + xOff, getValue(yMax) + yOff, getValue(zMax), backend.getSettings().getPreferredUnits());
    scanner.update(corner1, corner2);
    if (autoLevelPreview != null) {
      autoLevelPreview.updateSettings(scanner.getProbeStartPositions(), scanner.getProbePositionGrid());
    }
    updateMeshLeveler();
  }
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private void updateMeshLeveler() {
    if (this.bulkChanges) {
      return;
    }
    Settings.AutoLevelSettings autoLevelSettings = this.settings.getAutoLevelSettings();
    try {
      this.bulkChanges = true;
      for (CommandProcessor p : activeCommandProcessors) {
        backend.removeCommandProcessor(p);
      }
      if (!scanner.isValid() || !applyToGcode.isSelected()) {
        activeCommandProcessors = ImmutableList.of();
        activeLabel.setText("INACTIVE");
        activeLabel.setForeground(Color.RED);
        return;
      }
      ImmutableList.Builder<CommandProcessor> commandProcessors = ImmutableList.builder();
      commandProcessors.add(new ArcExpander(true, autoLevelSettings.autoLevelArcSliceLength, GcodePreprocessorUtils.getDecimalFormatter()));
      commandProcessors.add(new LineSplitter(getValue(stepResolution) / 4));
      commandProcessors.add(new MeshLeveler(getValue(this.zSurface), scanner.getProbePositionGrid()));
      activeCommandProcessors = commandProcessors.build();
      for (CommandProcessor p : activeCommandProcessors) {
        backend.applyCommandProcessor(p);
      }
      activeLabel.setForeground(Color.GREEN);
      activeLabel.setText("ACTIVE");
    } catch (Exception ex) {
      GUIHelpers.displayErrorDialog(ex.getMessage());
      Exceptions.printStackTrace(ex);
    } finally {
      this.bulkChanges = false;
    }
  }
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


  @Override public void componentOpened() {
    backend = CentralLookup.getDefault().lookup(BackendAPI.class);
    backend.addUGSEventListener(this);
    scanner = new SurfaceScanner(this.backend);
    scanner.addListener(this::updatePreview);
    meshLevelManager = new MeshLevelManager(scanner, backend);
    if (autoLevelPreview == null) {
      autoLevelPreview = new AutoLevelPreview(Localization.getString("platform.visualizer.renderable.autolevel-preview"));
      RenderableUtils.registerRenderable(autoLevelPreview);
    }
    initComponents();
    updateSettingsAndRefresh();
  }

  /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
  private void initComponents() {
    autoLevelerPanel = new AutoLevelerPanel(scanner, meshLevelManager, autoLevelPreview, backend.getSettings().getAutoLevelSettings(), backend.getSettings().getPreferredUnits());

<<<<<<< Unknown file: This is a bug in JDime.
=======
    visibleAutoLeveler.addActionListener(this::visibleAutoLevelerActionPerformed);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    useLoadedFile.addActionListener(this::useLoadedFileActionPerformed);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    dataViewer.addActionListener(this::dataViewerActionPerformed);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    settingsButton.addActionListener(this::settingsButtonActionPerformed);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    generateTestDataButton.addActionListener(this::generateTestDataButtonActionPerformed);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    scanSurfaceButton.addActionListener(this::scanSurfaceButtonActionPerformed);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    applyToGcode.addActionListener(this::applyToGcodeActionPerformed);
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(scanSurfaceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(applyToGcode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(activeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java

    autoLevelerPanel.
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/left.java
    addAutoLevelerPanelListener(this::updateSettingsAndRefresh)
=======
    setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(scanSurfaceButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(applyToGcode).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(activeLabel).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java
    ;
    this.setLayout(new BorderLayout());
    this.
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/left.java
    add(new AutoLevelerToolbar(scanner), BorderLayout.NORTH)
=======
>>>>>>> Unknown file: This is a bug in JDime.
    ;
    this.
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/left.java
    add(autoLevelerPanel, BorderLayout.CENTER)
=======
>>>>>>> Unknown file: This is a bug in JDime.
    ;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private void useLoadedFileActionPerformed(java.awt.event.ActionEvent evt) {
    if (backend.getProcessedGcodeFile() == null) {
      return;
    }
    FileStats fs = backend.getSettings().getFileStats();
    Position min = fs.minCoordinate.getPositionIn(backend.getSettings().getPreferredUnits());
    Position max = fs.maxCoordinate.getPositionIn(backend.getSettings().getPreferredUnits());
    this.xMin.setValue(min.x);
    this.yMin.setValue(min.y);
    this.zMin.setValue(min.z);
    this.xMax.setValue(max.x);
    this.yMax.setValue(max.y);
    this.zMax.setValue(max.z);
  }
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/03abfc1d5c21f20b8701e7ff9e64bac6bfc8fde2/ugs-platform/ugs-platform-surfacescanner/src/main/java/com/willwinder/ugs/platform/surfacescanner/AutoLevelerTopComponent.java/right.java


  @Override public void componentClosed() {
    RenderableUtils.removeRenderable(autoLevelPreview);
    autoLevelPreview = null;
    meshLevelManager.clear();
  }

  private void updatePreview() {
    if (autoLevelPreview != null) {
      autoLevelPreview.updateSettings(scanner.getProbeStartPositions(), scanner.getProbePositionGrid());
    }
    meshLevelManager.update();
  }

  private void updateSettingsAndRefresh() {
    backend.getSettings().getAutoLevelSettings().apply(autoLevelerPanel.getSettings());
    scanner.update(autoLevelerPanel.getMinPosition(), autoLevelerPanel.getMaxPosition());
    scanner.reset();
    updatePreview();
  }
}