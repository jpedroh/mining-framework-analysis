package oshi.hardware.platform.linux;
import oshi.hardware.CentralProcessor;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.linux.LinuxFileSystem;

public class LinuxHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  /**
     * {@inheritDoc}
     */
  @Override public GlobalMemory getMemory() {
    if (this.memory == null) {
      this.memory = new LinuxGlobalMemory();
    }
    return this.memory;
  }

  /**
     * {@inheritDoc}
     */
  @Override public CentralProcessor getProcessor() {
    if (this.processor == null) {
      this.processor = new LinuxCentralProcessor();
    }
    return this.processor;
  }

  /**
     * {@inheritDoc}
     */
  @Override public PowerSource[] getPowerSources() {
    return LinuxPowerSource.getPowerSources();
  }

  /**
     * {@inheritDoc}
     */
  @Override public OSFileStore[] getFileStores() {
    return LinuxFileSystem.getFileStores();
  }

  @Override public HWDiskStore[] getDisksStores() {
    return new LinuxDisks().getDisks();
  }

  /**
     * {@inheritDoc}
     */
  @Override public Display[] getDisplays() {
    return LinuxDisplay.getDisplays();
  }

  /**
     * {@inheritDoc}
     */
  @Override public Sensors getSensors() {
    if (this.sensors == null) {
      this.sensors = new LinuxSensors();
    }
    return this.sensors;
  }
}