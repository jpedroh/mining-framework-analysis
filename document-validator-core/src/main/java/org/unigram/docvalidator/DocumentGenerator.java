package org.unigram.docvalidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.parser.Parser;
import org.unigram.docvalidator.parser.DocumentParserFactory;
import org.unigram.docvalidator.model.DocumentCollection;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Generate DocumentCollection object loading input file.
 */
public final class DocumentGenerator {
  /**
   * Generate DocumentCollection from input file.
   *
   * @param inputFileNames input file name
   * @param resource       configuration resource
   * @param format         input file format
   * @return a generated DocumentCollection object
   */
  static DocumentCollection generate(String[] inputFileNames, DVResource resource, Parser.Type format) {
    Parser docparser;
    try {
      docparser = DocumentParserFactory.generate(format, resource);
    } catch (DocumentValidatorException e) {
      LOG.error("Failed to create documentCollection parser: " + e.getMessage());
      return null;
    }
    DocumentCollection documentCollection = new DocumentCollection();
    for (String inputFileName : inputFileNames) {
      try {
        documentCollection.addDocument(docparser.generateDocument(inputFileName));
      } catch (DocumentValidatorException e) {
        e.printStackTrace();
        return null;
      }
    }
    return documentCollection;
  }

  private static final Logger LOG = LoggerFactory.getLogger(DocumentGenerator.class);

  private DocumentGenerator() {
    super();
  }
}