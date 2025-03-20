package jsprit.core.algorithm.ruin;
import jsprit.core.problem.job.Job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Iterator;

/**
 * Created by schroeder on 07/01/15.
 */
class NearestNeighborhoodIterator implements Iterator<Job> {
  private static Logger log = LogManager.getLogger(NearestNeighborhoodIterator.class);

  private Iterator<ReferencedJob> jobIter;

  private int nJobs;

  private int jobCount = 0;

  public NearestNeighborhoodIterator(Iterator<ReferencedJob> jobIter, int nJobs) {
    super();
    this.jobIter = jobIter;
    this.nJobs = nJobs;
  }

  @Override public boolean hasNext() {
    if (jobCount < nJobs) {
      boolean hasNext = jobIter.hasNext();
      return hasNext;
    }
    return false;
  }

  @Override public Job next() {
    ReferencedJob next = jobIter.next();
    jobCount++;
    return next.getJob();
  }

  @Override public void remove() {
    jobIter.remove();
  }
}