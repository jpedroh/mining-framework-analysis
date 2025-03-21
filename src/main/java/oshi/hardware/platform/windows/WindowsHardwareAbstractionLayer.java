package oshi.hardware.platform.windows;
import oshi.hardware.CentralProcessor;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.windows.WindowsFileSystem;

public class WindowsHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  /**
     * {@inheritDoc}
     */
  @Override public GlobalMemory getMemory() {
    if (this.memory == null) {
      this.memory = new WindowsGlobalMemory();
    }
    return this.memory;
  }

  /**
     * {@inheritDoc}
     */
  @Override public CentralProcessor getProcessor() {
    if (this.processor == null) {
      processor = new WindowsCentralProcessor();
    }
    return this.processor;
  }

  /**
     * {@inheritDoc}
     */
  @Override public PowerSource[] getPowerSources() {
    return WindowsPowerSource.getPowerSources();
  }

  /**
     * {@inheritDoc}
     */
  @Override public OSFileStore[] getFileStores() {
    return WindowsFileSystem.getFileStores();
  }

  @Override public HWDiskStore[] getDisksStores() {
    return new WindowsDisks().getDisks();
  }

  /**
     * {@inheritDoc}
     */
  @Override public Display[] getDisplays() {
    return WindowsDisplay.getDisplays();
  }

  /**
     * {@inheritDoc}
     */
  @Override public Sensors getSensors() {
    if (this.sensors == null) {
      this.sensors = new WindowsSensors();
    }
    return this.sensors;
  }
}