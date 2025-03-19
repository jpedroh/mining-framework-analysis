package cc.redpen.formatter;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cc.redpen.DocumentValidatorException;
import cc.redpen.ValidationError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * XML Output formatter.
 */
public class XMLFormatter implements Formatter {
  /**
   * Constructor.
   *
   * @throws cc.redpen.DocumentValidatorException when failed to create Formatter
   */
  public XMLFormatter() throws DocumentValidatorException {
    super();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      this.db = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new DocumentValidatorException(e.getMessage());
    }
  }

  @Override public String convertError(ValidationError error) {
    Document doc = db.newDocument();
    Element errorElement = doc.createElement("error");
    doc.appendChild(errorElement);
    Element validatorElement = doc.createElement("validator");
    errorElement.appendChild(validatorElement);
    Text validator = doc.createTextNode(error.getValidatorName());
    validatorElement.appendChild(validator);
    Element contentElement = doc.createElement("message");
    errorElement.appendChild(contentElement);
    Text content = doc.createTextNode(error.getMessage());
    contentElement.appendChild(content);
    error.getFileName().ifPresent((e) -> {
      Element fileNameElement = doc.createElement("file");
      errorElement.appendChild(fileNameElement);
      Text fileName = doc.createTextNode(e);
      fileNameElement.appendChild(fileName);
    });
    Element lineNumberElement = doc.createElement("lineNum");
    errorElement.appendChild(lineNumberElement);
    Text lineNum = doc.createTextNode(Integer.toString(error.getLineNumber()));
    lineNumberElement.appendChild(lineNum);

<<<<<<< /usr/src/app/output/recruit-tech/redpen/9af5140829baa1e9dd5094879ad0681606574646/redpen-core/src/main/java/cc/redpen/formatter/XMLFormatter.java/left.java
    error.getSentence().ifPresent((e) -> {
      Element sentenceElement = doc.createElement("sentence");
      errorElement.appendChild(sentenceElement);
      sentenceElement.appendChild(doc.createTextNode(e.content));
    });
=======
    if (error.getSentence() != null && !error.getSentence().content.equals("")) {
      Element sentenceElement = doc.createElement("sentence");
      errorElement.appendChild(sentenceElement);
      Text content = doc.createTextNode(error.getSentence().content);
      sentenceElement.appendChild(content);
    }
>>>>>>> /usr/src/app/output/recruit-tech/redpen/9af5140829baa1e9dd5094879ad0681606574646/redpen-core/src/main/java/cc/redpen/formatter/XMLFormatter.java/right.java

    Transformer transformer = createTransformer();
    if (transformer == null) {
      throw new IllegalStateException("Failed to create XML Transformer");
    }
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    DOMSource source = new DOMSource(doc);
    try {
      transformer.transform(source, result);
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Transformer createTransformer() {
    TransformerFactory tf;
    tf = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = tf.newTransformer();
    } catch (TransformerConfigurationException e) {
      LOG.error(e.getMessage());
      return null;
    }
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    return transformer;
  }

  @Override public String header() {
    return "<validation-result>";
  }

  @Override public String footer() {
    return "</validation-result>";
  }

  private DocumentBuilder db;

  private static final Logger LOG = LoggerFactory.getLogger(XMLFormatter.class);
}