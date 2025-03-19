package net.technicpack.launchercore.install.tasks;
import net.technicpack.launchercore.install.InstallTasksQueue;
import net.technicpack.utilslib.IZipFileFilter;
import net.technicpack.utilslib.ZipUtils;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

public class UnzipFileTask extends ListenerTask {
  private File zipFile;

  private File destination;

  private IZipFileFilter filter;

  public UnzipFileTask(File zipFile, File destination, IZipFileFilter filter) {
    this.zipFile = zipFile;
    this.destination = destination;
    this.filter = filter;
  }

  @Override public String getTaskDescription() {
    return "Unzipping " + this.zipFile.getName();
  }

  @Override public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
    super.runTask(queue);
    if (!zipFile.exists()) {
      throw new ZipException("Attempting to extract file " + zipFile.getName() + ", but it did not exist.");
    }
    if (!destination.exists()) {
      destination.mkdirs();
    }
    ZipUtils.unzipFile(zipFile, destination, filter, this);
  }
}