package org.openpnp.machine.reference.feeder;
import java.util.List;
import javax.swing.Action;
import org.apache.commons.io.IOUtils;
import org.opencv.core.RotatedRect;
import org.openpnp.gui.MainFrame;
import org.openpnp.gui.support.PropertySheetWizardAdapter;
import org.openpnp.gui.support.Wizard;
import org.openpnp.machine.reference.ReferenceFeeder;
import org.openpnp.machine.reference.feeder.wizards.AdvancedLoosePartFeederConfigurationWizard;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.spi.Camera;
import org.openpnp.spi.Nozzle;
import org.openpnp.spi.PropertySheetHolder;
import org.openpnp.util.MovableUtils;
import org.openpnp.util.OpenCvUtils;
import org.openpnp.util.Utils2D;
import org.openpnp.util.VisionUtils;
import org.openpnp.vision.pipeline.CvPipeline;
import org.pmw.tinylog.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.core.Commit;

public class AdvancedLoosePartFeeder extends ReferenceFeeder {
  @Element(required = false) private CvPipeline pipeline = createDefaultPipeline();

  @Element(required = false) private CvPipeline trainingPipeline = createDefaultTrainingPipeline();

  private Location pickLocation;

  @Commit public void commit() {
    if (rotationInFeeder == null) {
      Logger.trace(name + ": Old feeder format found, updating to new format...");
      rotationInFeeder = Utils2D.normalizeAngle180(-getLocation().getRotation());
      setLocation(getLocation().derive(null, null, null, 0.0));
    }
  }

  @Override public Location getPickLocation() throws Exception {
    return pickLocation == null ? getLocation().derive(null, null, null, 0.0) : convertToGlobalLocation(pickLocation);
  }

  public void setPickLocation(Location pickLocation) {
    this.pickLocation = convertToLocalLocation(pickLocation);
  }

  @Override public void feed(Nozzle nozzle) throws Exception {
    Camera camera = nozzle.getHead().getDefaultCamera();
    MovableUtils.moveToLocationAtSafeZ(camera, getLocation());
    for (int i = 0; i < 3; i++) {
      Location pickLocation = getPickLocation(camera, nozzle);
      camera.moveTo(pickLocation);
      setPickLocation(pickLocation);
    }
  }

  private Location getPickLocation(Camera camera, Nozzle nozzle) throws Exception {
    try (CvPipeline pipeline = getPipeline()) {
      pipeline.setProperty("camera", camera);
      pipeline.setProperty("nozzle", nozzle);
      pipeline.setProperty("feeder", this);
      pipeline.process();
      List<RotatedRect> results = (List<RotatedRect>) pipeline.getResult(VisionUtils.PIPELINE_RESULTS_NAME).model;
      if ((results == null) || results.isEmpty()) {
        throw new Exception("Feeder " + getName() + ": No parts found.");
      }
      results.sort((a, b) -> {
        Double da = VisionUtils.getPixelLocation(camera, a.center.x, a.center.y).getLinearDistanceTo(camera.getLocation());
        Double db = VisionUtils.getPixelLocation(camera, b.center.x, b.center.y).getLinearDistanceTo(camera.getLocation());
        return da.compareTo(db);
      });
      RotatedRect result = results.get(0);
      Location location = VisionUtils.getPixelLocation(camera, result.center.x, result.center.y);
      location = location.derive(null, null, null, rotationInFeeder - result.angle);
      location = location.derive(null, null, this.getLocation().convertToUnits(location.getUnits()).getZ() + part.getHeight().convertToUnits(location.getUnits()).getValue(), null);
      MainFrame.get().getCameraViews().getCameraView(camera).showFilteredImage(OpenCvUtils.toBufferedImage(pipeline.getWorkingImage()), 250);
      return location;
    }
  }

  public CvPipeline getPipeline() {
    return pipeline;
  }

  public void resetPipeline() {
    pipeline = createDefaultPipeline();
  }

  public CvPipeline getTrainingPipeline() {
    return trainingPipeline;
  }

  public void resetTrainingPipeline() {
    trainingPipeline = createDefaultTrainingPipeline();
  }

  @Override public Wizard getConfigurationWizard() {
    return new AdvancedLoosePartFeederConfigurationWizard(this);
  }

  @Override public String getPropertySheetHolderTitle() {
    return getClass().getSimpleName() + " " + getName();
  }

  @Override public PropertySheetHolder[] getChildPropertySheetHolders() {
    return null;
  }

  @Override public PropertySheet[] getPropertySheets() {
    return new PropertySheet[] { new PropertySheetWizardAdapter(getConfigurationWizard()) };
  }

  @Override public Action[] getPropertySheetHolderActions() {
    return null;
  }

  public static CvPipeline createDefaultPipeline() {
    try {
      String xml = IOUtils.toString(AdvancedLoosePartFeeder.class.getResource("AdvancedLoosePartFeeder-DefaultPipeline.xml"));
      return new CvPipeline(xml);
    } catch (Exception e) {
      throw new Error(e);
    }
  }

  public static CvPipeline createDefaultTrainingPipeline() {
    try {
      String xml = IOUtils.toString(AdvancedLoosePartFeeder.class.getResource("AdvancedLoosePartFeeder-DefaultTrainingPipeline.xml"));
      return new CvPipeline(xml);
    } catch (Exception e) {
      throw new Error(e);
    }
  }
}