package org.unigram.docvalidator.util;

public interface Formatter {
  /**
   * Convert ValidationError into a string to flush a error message.
   *
   * @param error object containing file and line number information.
   * @return error message
   */
  String convertError(ValidationError error);

  /**
   * Return the header block of semi-structured format.
   *
   * @return header block
   */
  String header();

  /**
   * Return the footer block of semi-structured format.
   *
   * @return
   */
  String footer();
}