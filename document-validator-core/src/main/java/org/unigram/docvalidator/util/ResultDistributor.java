package org.unigram.docvalidator.util;

/**
 * ResultDistributor flush the errors reported from Validators.
 */
public interface ResultDistributor {
  /**
   * Flush header block of semi-structured format.
   */
  void flushHeader();

  /**
   * Flush footer block of semi-structured format.
   */
  void flushFooter();

  /**
   * Flush given ValidationError.
   * @param err error reported from a Validator
   * @return 0 succeeded, otherwise 1
   */
  int flushResult(ValidationError err);

  /**
   * set Formatter object
   * @param formatter
   */
  void setFormatter(Formatter formatter);
}