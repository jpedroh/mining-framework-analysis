package org.unigram.docvalidator.util;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class of ResultDistributor.
 */
public final class ResultDistributorFactory {
  /**
   * Create ResultDistributor object.
   *
   * @param outputFormat syntax of output
   * @param output       output stream
   * @return ResultDistributor object when succeeded to create, null otherwise
   */
  static public ResultDistributor createDistributor(String outputFormat, OutputStream output) {
    if (outputFormat == null) {
      LOG.error("Specified output format is null...");
      return null;
    }
    if (output == null) {
      LOG.error("Output stream is null...");
      return null;
    }
    ResultDistributor distributor = new DefaultResultDistributor(output);
    LOG.info("Creating Distributor...");
    try {
      if (outputFormat.equals("plain")) {
        distributor.setFormatter(new PlainFormatter());
      } else {
        if (outputFormat.equals("xml")) {
          distributor.setFormatter(new XMLFormatter());
        } else {
          LOG.error("No specified distributor...");
          return null;
        }
      }
    } catch (DocumentValidatorException e) {
      LOG.error(e.getMessage());
      return null;
    }
    return distributor;
  }

  private ResultDistributorFactory() {
  }

  private static final Logger LOG = LoggerFactory.getLogger(ResultDistributor.class);
}