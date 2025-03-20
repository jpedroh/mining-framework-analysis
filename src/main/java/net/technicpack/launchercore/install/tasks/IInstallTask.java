package net.technicpack.launchercore.install.tasks;
import net.technicpack.launchercore.install.InstallTasksQueue;
import java.io.IOException;

public interface IInstallTask {
  String getTaskDescription();

  float getTaskProgress();

  void runTask(InstallTasksQueue queue) throws IOException, InterruptedException;
}