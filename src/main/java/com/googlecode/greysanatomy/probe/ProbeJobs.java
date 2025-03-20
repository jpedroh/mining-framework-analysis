package com.googlecode.greysanatomy.probe;
import com.googlecode.greysanatomy.console.command.JavaScriptCommand.JLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class ProbeJobs {
  private static final Logger logger = LoggerFactory.getLogger("greysanatomy");

  private static final String REST_DIR = System.getProperty("java.io.tmpdir") + File.separator + "greysdata" + File.separator + UUID.randomUUID().toString() + File.separator;

  private static final String REST_FILE_EXT = ".ga";

  private static class Job {
    private final int id;

    private boolean isAlive;

    private boolean isKilled;

    private JobListener listener;

    private final File jobFile;

    private final Reader jobReader;

    private final Writer jobWriter;

    Job(int id) throws IOException {
      this.id = id;
      final File dir = new File(REST_DIR);
      if (!dir.exists()) {
        dir.mkdir();
      }
      jobFile = new File(REST_DIR + id + REST_FILE_EXT);
      jobFile.createNewFile();
      jobReader = new BufferedReader(new FileReader(jobFile));
      jobWriter = new BufferedWriter(new FileWriter(jobFile));
    }
  }

  private static final Map<Integer, Job> jobs = new ConcurrentHashMap<Integer, Job>();

  private static final AtomicInteger jobIdxSequencer = new AtomicInteger(1000);

  /**
     * ע��������
     *
     * @param listener
     */
  public static void register(Integer id, JobListener listener) {
    Job job = jobs.get(id);
    if (null != job) {
      job.listener = listener;
      listener.create();
    }
  }

  /**
     * ����һ��job
     *
     * @return
     */
  public static int createJob() throws IOException {
    final int id = jobIdxSequencer.getAndIncrement();
    Job job = new Job(id);
    job.isAlive = false;
    jobs.put(id, job);
    return id;
  }

  /**
     * ����һ��job
     *
     * @param id
     */
  public static void activeJob(int id) {
    Job job = jobs.get(id);
    if (null != job) {
      job.isAlive = true;
    }
  }

  /**
     * �ж�job�Ƿ񻹿��Լ�������
     *
     * @param id
     * @return
     */
  public static boolean isJobAlive(int id) {
    Job job = jobs.get(id);
    return null != job && job.isAlive;
  }

  /**
     * �ж�job�Ƿ��Ѿ���kill
     * @param id
     * @return
     */
  public static boolean isJobKilled(int id) {
    Job job = jobs.get(id);
    return null != job && job.isKilled;
  }

  /**
     * ɱ��һ��job
     *
     * @param id
     */
  public static void killJob(int id) {
    Job job = jobs.get(id);
    if (null != job) {
      job.isAlive = false;
      job.isKilled = true;
      try {
        job.listener.destroy();
      } catch (Throwable t) {
        logger.warn("destroy job listener failed, jobId={}", id, t);
      }
      try {
        job.jobReader.close();
        job.jobWriter.close();
        job.jobFile.deleteOnExit();
      } catch (IOException e) {
        logger.warn("close jobFile failed. jobId={}", id, e);
      }
      JLS.removeJob(id);
    }
  }

  /**
     * ���ش���jobId
     *
     * @return
     */
  public static List<Integer> listAliveJobIds() {
    final List<Integer> jobIds = new ArrayList<Integer>();
    for (Job job : jobs.values()) {
      if (job.isAlive) {
        jobIds.add(job.id);
      }
    }
    return jobIds;
  }

  public static Reader getJobReader(int id) {
    if (jobs.containsKey(id)) {
      return jobs.get(id).jobReader;
    } else {
      return null;
    }
  }

  public static Writer getJobWriter(int id) {
    if (jobs.containsKey(id)) {
      return jobs.get(id).jobWriter;
    } else {
      return null;
    }
  }

  /**
     * ���ص�ǰ��̽��������б�
     *
     * @param id
     * @return
     */
  public static JobListener getJobListeners(int id) {
    if (jobs.containsKey(id)) {
      return jobs.get(id).listener;
    } else {
      return null;
    }
  }

  /**
     * job�Ƿ�ʵ����ָ����listener
     *
     * @param id
     * @param classListener
     * @return
     */
  public static boolean isListener(int id, Class<? extends JobListener> classListener) {
    final JobListener jobListener = getJobListeners(id);
    return null != jobListener && classListener.isAssignableFrom(jobListener.getClass());
  }
}