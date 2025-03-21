package oshi.hardware.platform.mac;
import oshi.hardware.CentralProcessor;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.mac.MacFileSystem;

public class MacHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  /**
     * {@inheritDoc}
     */
  @Override public CentralProcessor getProcessor() {
    if (this.processor == null) {
      this.processor = new MacCentralProcessor();
    }
    return this.processor;
  }

  /**
     * {@inheritDoc}
     */
  @Override public GlobalMemory getMemory() {
    if (this.memory == null) {
      this.memory = new MacGlobalMemory();
    }
    return this.memory;
  }

  /**
     * {@inheritDoc}
     */
  @Override public PowerSource[] getPowerSources() {
    return MacPowerSource.getPowerSources();
  }

  /**
     * {@inheritDoc}
     */
  @Override public OSFileStore[] getFileStores() {
    return MacFileSystem.getFileStores();
  }

  /**
     * {@inheritDoc}
     */
  @Override public Display[] getDisplays() {
    return MacDisplay.getDisplays();
  }

  /**
     * {@inheritDoc}
     */
  @Override public Sensors getSensors() {
    if (this.sensors == null) {
      this.sensors = new MacSensors();
    }
    return this.sensors;
  }

  @Override public HWDiskStore[] getDisksStores() {
    return new MacDisks().getDisks();
  }
}