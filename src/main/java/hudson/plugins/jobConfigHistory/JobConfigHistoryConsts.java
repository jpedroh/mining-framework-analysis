package hudson.plugins.jobConfigHistory;

/**
 * Holder for constants.
 *
 * @author Stefan Brausch
 */
public final class JobConfigHistoryConsts {
  /**
	 * Holder for constants.
	 */
  private JobConfigHistoryConsts() {
  }

  /** Path to the jobConfigHistory base. */
  public static final String URLNAME = "jobConfigHistory";

  /** Path to the icon. */
  public static final String ICONFILENAME = "/plugin/jobConfigHistory/img/confighistory.png";

  /** Default root directory for storing history. */
  public static final String DEFAULT_HISTORY_DIR = "config-history";

  /** Default directory for storing job history. */
  public static final String JOBS_HISTORY_DIR = "jobs";

  /** Default directory for storing node history. */
  public static final String NODES_HISTORY_DIR = "nodes";

  /** name of history xml file. */
  public static final String HISTORY_FILE = "history.xml";

  /** Default regexp pattern of configuration files not to save. */
  public static final String DEFAULT_EXCLUDE = "queue\\.xml|nodeMonitors\\.xml|UpdateCenter\\.xml|global-build-stats|LockableResourcesManager\\.xml|MilestoneStep\\.xml";

  /** Format for timestamped dirs. */
  public static final String ID_FORMATTER = "yyyy-MM-dd_HH-mm-ss";

  /** Default maximum number of configuration history entries to keep. */
  public static final String DEFAULT_MAX_HISTORY_ENTRIES = "1000";

  /** Default maximum number of history entries per site to show. */
  public static final String DEFAULT_MAX_ENTRIES_PER_PAGE = "30";

  /** Default maximum number of days to keep entries. */
  public static final String DEFAULT_MAX_DAYS_TO_KEEP_ENTRIES = "20";
}