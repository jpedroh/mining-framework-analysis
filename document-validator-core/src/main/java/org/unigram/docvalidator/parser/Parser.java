package org.unigram.docvalidator.parser;
import java.io.InputStream;
import org.unigram.docvalidator.model.Document;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Parser generates Document from input.
 */
public interface Parser {
  /**
   * Given input stream, return Document instance from a stream.
   *
   * @param io input stream containing input content
   * @return a generated file content
   * @throws DocumentValidatorException if Parser failed to parse input.
   */
  Document generateDocument(InputStream io) throws DocumentValidatorException;

  /**
   * Given input file name, return Document instance for the specified file.
   *
   * @param fileName input file name
   * @return a generated file content
   * @throws DocumentValidatorException if Parser failed to parse input.
   */
  Document generateDocument(String fileName) throws DocumentValidatorException;

  /**
   * Initialize parser.
   *
   * @param resource configuration resources
   * @throws DocumentValidatorException if the configurations loading failed
   */
  void initialize(DVResource resource) throws DocumentValidatorException;

  enum Type {
    PLAIN,
    WIKI,
    MARKDOWN
  }
}