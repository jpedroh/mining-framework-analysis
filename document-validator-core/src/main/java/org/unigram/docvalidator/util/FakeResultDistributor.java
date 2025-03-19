package org.unigram.docvalidator.util;

/**
 * FakeResultDistributor does nothing. this class is just for testing.
 */
public class FakeResultDistributor implements ResultDistributor {
  /**
   * Constructor.
   */
  public FakeResultDistributor() {
    super();
  }

  @Override public int flushResult(ValidationError err) {
    return 0;
  }

  @Override public void setFormatter(Formatter formatter) {
  }

  @Override public void flushHeader() {
  }

  @Override public void flushFooter() {
  }
}