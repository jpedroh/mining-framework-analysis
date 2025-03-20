package org.openpnp.gui.components;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.openpnp.ConfigurationListener;
import org.openpnp.gui.support.CameraItem;
import org.openpnp.model.Configuration;
import org.openpnp.spi.Camera;

/**
 * Shows a square grid of cameras or a blown up image from a single camera.
 */
@SuppressWarnings(value = { "serial" }) public class CameraPanel extends JPanel {
  private static int maximumFps = 15;

  private static final String SHOW_NONE_ITEM = "Show None";

  private static final String SHOW_ALL_ITEM = "Show All";

  private Map<Camera, CameraView> cameraViews = new LinkedHashMap<>();

  private JComboBox camerasCombo;

  private JPanel camerasPanel;

  private CameraView selectedCameraView;

  private static final String PREF_SELECTED_CAMERA_VIEW = "JobPanel.dividerPosition";

  private Preferences prefs = Preferences.userNodeForPackage(CameraPanel.class);

  public CameraPanel() {
    createUi();
    Configuration.get().addListener(new ConfigurationListener.Adapter() {
      @Override public void configurationComplete(Configuration configuration) throws Exception {
        String selectedCameraView = prefs.get(PREF_SELECTED_CAMERA_VIEW, null);
        if (selectedCameraView != null) {
          System.out.println("Loaded " + selectedCameraView);
          for (int i = 0; i < camerasCombo.getItemCount(); i++) {
            Object o = camerasCombo.getItemAt(i);
            if (o.toString().equals(selectedCameraView)) {
              camerasCombo.setSelectedItem(o);
            }
          }
        }
        camerasCombo.addActionListener((event) -> {
          try {
            prefs.put(PREF_SELECTED_CAMERA_VIEW, camerasCombo.getSelectedItem().toString());
            prefs.flush();
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
      }
    });
  }

  public void addCamera(Camera camera) {
    CameraView cameraView = new CameraView(maximumFps / Math.max(cameraViews.size(), 1));
    cameraView.setCamera(camera);
    cameraViews.put(camera, cameraView);
    camerasCombo.addItem(new CameraItem(camera));
    if (cameraViews.size() == 1) {
      camerasCombo.setSelectedIndex(1);
    } else {
      if (cameraViews.size() == 2) {
        camerasCombo.insertItemAt(SHOW_ALL_ITEM, 1);
      }
    }
  }

  private void createUi() {
    camerasPanel = new JPanel();
    camerasCombo = new JComboBox();
    camerasCombo.addActionListener(cameraSelectedAction);
    setLayout(new BorderLayout());
    camerasCombo.addItem(SHOW_NONE_ITEM);
    add(camerasCombo, BorderLayout.NORTH);
    add(camerasPanel);
  }

  /**
	 * Make sure the given Camera is visible in the UI. If All Cameras is
	 * selected we do nothing, otherwise we select the specified Camera.
	 * @param camera
	 * @return
	 */
  public void ensureCameraVisible(Camera camera) {
    if (camerasCombo.getSelectedItem().equals(SHOW_ALL_ITEM)) {
      return;
    }
    setSelectedCamera(camera);
  }

  public CameraView setSelectedCamera(Camera camera) {
    if (selectedCameraView != null && selectedCameraView.getCamera() == camera) {
      return selectedCameraView;
    }
    for (int i = 0; i < camerasCombo.getItemCount(); i++) {
      Object o = camerasCombo.getItemAt(i);
      if (o instanceof CameraItem) {
        Camera c = ((CameraItem) o).getCamera();
        if (c == camera) {
          camerasCombo.setSelectedIndex(i);
          return selectedCameraView;
        }
      }
    }
    return null;
  }

  public CameraView getCameraView(Camera camera) {
    return cameraViews.get(camera);
  }

  private AbstractAction cameraSelectedAction = new AbstractAction("") {
    @Override public void actionPerformed(ActionEvent ev) {
      selectedCameraView = null;
      camerasPanel.removeAll();
      if (camerasCombo.getSelectedItem().equals(SHOW_NONE_ITEM)) {
        camerasPanel.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setBackground(Color.black);
        camerasPanel.add(panel);
        selectedCameraView = null;
      } else {
        if (camerasCombo.getSelectedItem().equals(SHOW_ALL_ITEM)) {
          int columns = (int) Math.ceil(Math.sqrt(cameraViews.size()));
          if (columns == 0) {
            columns = 1;
          }
          camerasPanel.setLayout(new GridLayout(0, columns, 1, 1));
          for (CameraView cameraView : cameraViews.values()) {
            cameraView.setMaximumFps(maximumFps / Math.max(cameraViews.size(), 1));
            camerasPanel.add(cameraView);
            if (cameraViews.size() == 1) {
              selectedCameraView = cameraView;
            }
          }
          if (cameraViews.size() > 2) {
            for (int i = 0; i < (columns * columns) - cameraViews.size(); i++) {
              JPanel panel = new JPanel();
              panel.setBackground(Color.black);
              camerasPanel.add(panel);
            }
          }
          selectedCameraView = null;
        } else {
          camerasPanel.setLayout(new BorderLayout());
          Camera camera = ((CameraItem) camerasCombo.getSelectedItem()).getCamera();
          CameraView cameraView = getCameraView(camera);
          cameraView.setMaximumFps(maximumFps);
          camerasPanel.add(cameraView);
          selectedCameraView = cameraView;
        }
      }
      revalidate();
      repaint();
    }
  };
}