package de.bitzeche.video.transcoding.zencoder;
import org.w3c.dom.Document;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderNotificationJobState;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;
import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;

public interface IZencoderClient {
  /**
	 * Submits a new Zencoder Job
	 * 
	 * @param job
	 * @return XML Response from zencoder
	 */
  public Document createJob(ZencoderJob job) throws ZencoderErrorResponseException;

  /**
	 * Send a jobProgress request for a job.
	 * @param jobId ID for the requested job.
	 * @return State of job, or null if unable to parse response.
	 */
  public ZencoderNotificationJobState jobProgress(int jobId);

  /**
	 * Send a jobProgress request for a job.
	 * @param job
	 * @return State of job, or null if unable to parse response.
	 */
  public ZencoderNotificationJobState jobProgress(ZencoderJob job);

  public boolean resubmitJob(int jobId);

  public boolean resubmitJob(ZencoderJob job);

  public boolean cancelJob(int jobId);

  public boolean cancelJob(ZencoderJob job);

  @Deprecated public boolean deleteJob(int jobId);

  @Deprecated public boolean deleteJob(ZencoderJob job);
}