package de.uni_koblenz.jgralab;

/**
 * can be implemented by a user to build his/her own progress function, example:
 * jgralab.impl.ProgressFunctionImpl.java
 * 
 * @author ist@uni-koblenz.de
 */
public interface ProgressFunction {
  /**
	 * method for initialization of progress function
	 * 
	 * @param totalElements
	 *            total number of elements which are to be processed
	 */
  public void init(long totalElements);

  /**
	 * Called during processing elements.
	 * 
	 * @param processedElements
	 *            the current number of elements which have been processed
	 */
  public void progress(long processedElements);

  /**
	 * Called after completion of all elements.
	 */
  public void finished();

  /**
	 * Specifies the number of processed elements after which a call to
	 * progress() occurs.
	 * 
	 * @return the interval (number of processed elements) which specifies how
	 *         often the progress bar is updated
	 */
  public long getUpdateInterval();
}