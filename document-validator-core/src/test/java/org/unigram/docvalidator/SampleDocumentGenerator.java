package org.unigram.docvalidator;
import org.apache.commons.io.IOUtils;
import org.unigram.docvalidator.parser.DocumentParserFactory;
import org.unigram.docvalidator.parser.Parser;
import org.unigram.docvalidator.model.DocumentCollection;
import org.unigram.docvalidator.util.CharacterTable;
import org.unigram.docvalidator.util.DVResource;
import org.unigram.docvalidator.util.DocumentValidatorException;
import org.unigram.docvalidator.util.ValidatorConfiguration;
import java.io.InputStream;

/**
 * Generate DocumentCollection objects from String. This class are applied
 * only for testing purpose.
 */
public class SampleDocumentGenerator {
  /**
   * Given a string and the syntax type, build a DocumentCollection object.
   * This build method is made to write test easily, but this generator
   * class does not supports the configurations if the configurations are
   * needed please use DocumentGenerator class.
   *
   * @param docString input document string
   * @param type document syntax: wiki, markdown or plain
   * @return DocumentCollection object
   */
  public static DocumentCollection generateOneFileDocument(String docString, Parser.Type type) throws DocumentValidatorException {
    DVResource resource = new DVResource(new ValidatorConfiguration("dummy"), new CharacterTable());
    Parser parser = DocumentParserFactory.generate(type, resource);
    InputStream stream = IOUtils.toInputStream(docString);
    DocumentCollection documentCollection = new DocumentCollection();
    documentCollection.addDocument(parser.generateDocument(stream));
    return documentCollection;
  }
}