package org.unigram.docvalidator.parser;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;

/**
 * Factory class of DocumentParser.
 */
public final class DocumentParserFactory {
  /**
   * Create DocumentParser object following specified input file type.
   *
   * @param parserType type of parser (plain or wiki etc.)
   * @param resource   configuration settings
   * @return Parser implementation object
   * @throws DocumentValidatorException when failed to generate Parser instance
   *                                    or no specified parser implementation.
   */
  public static Parser generate(String parserType, DVResource resource) throws DocumentValidatorException {
    Parser docparser;
    if (parserType.equals("wiki")) {
      docparser = new WikiParser();
    } else {
      if (parserType.equals("plain")) {
        docparser = new PlainTextParser();
      } else {
        if (parserType.equals("markdown")) {
          docparser = new MarkdownParser();
        } else {
          throw new DocumentValidatorException("Specified parser type not exist: " + parserType);
        }
      }
    }
    docparser.initialize(resource);
    return docparser;
  }

  private DocumentParserFactory() {
    super();
  }
}