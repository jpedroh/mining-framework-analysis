package com.sforce.ws.wsdl;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.parser.PullParserException;
import com.sforce.ws.parser.XmlInputStream;

/**
 * WsdlParser
 * 
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0 Jan 20, 2006
 */
public class WsdlParser {
  public interface PostParseProcessor {
    public void postParse() throws ConnectionException;
  }

  private XmlInputStream in;

  private List<PostParseProcessor> postParseBlocks = new ArrayList<PostParseProcessor>();

  public WsdlParser(XmlInputStream in) {
    this.in = in;
  }

  public void setInput(InputStream inputStream, String inputEncoding) throws WsdlParseException {
    try {
      in.setInput(inputStream, inputEncoding);
    } catch (PullParserException e) {
      throw new WsdlParseException("Failed to set input", e);
    }
  }

  public String getNamespace(String prefix) {
    return in.getNamespace(prefix);
  }

  public String getPositionDescription() {
    return in.getLineNumber() + ":" + in.getColumnNumber();
  }

  public String getNamespace() {
    return in.getNamespace();
  }

  public String getName() {
    return in.getName();
  }

  public String getAttributeValue(String namespace, String name) {
    return in.getAttributeValue(namespace, name);
  }

  public int getEventType() throws WsdlParseException {
    try {
      return in.getEventType();
    } catch (ConnectionException e) {
      throw new WsdlParseException(e);
    }
  }

  public int next() throws WsdlParseException {
    try {
      return in.next();
    } catch (IOException e) {
      throw new WsdlParseException(e);
    } catch (ConnectionException e) {
      throw new WsdlParseException(e);
    }
  }

  @Override public String toString() {
    return "WsdlParser: " + in.toString();
  }

  public String nextText() throws WsdlParseException {
    try {
      return in.nextText();
    } catch (IOException e) {
      throw new WsdlParseException(e);
    } catch (ConnectionException e) {
      throw new WsdlParseException(e);
    }
  }

  public int nextTag() throws WsdlParseException {
    try {
      return in.nextTag();
    } catch (IOException e) {
      throw new WsdlParseException(e);
    } catch (ConnectionException e) {
      throw new WsdlParseException(e);
    }
  }

  public int peekTag() throws WsdlParseException {
    try {
      return in.peekTag();
    } catch (ConnectionException e) {
      throw new WsdlParseException(e);
    } catch (IOException e) {
      throw new WsdlParseException(e);
    }
  }

  public QName parseRef(Schema schema) throws WsdlParseException {
    String r = getAttributeValue(null, Constants.REF);
    QName ref = null;
    if (r != null) {
      if ("".equals(r)) {
        throw new WsdlParseException("Element ref can not be empty, at: " + this.getPositionDescription());
      }
      ref = ParserUtil.toQName(r, this);
      if (ref.getNamespaceURI() == null || "".equals(ref.getNamespaceURI())) {
        ref = new QName(schema.getTargetNamespace(), ref.getLocalPart());
      }
    }
    return ref;
  }

  public void addPostParseProcessor(PostParseProcessor process) {
    postParseBlocks.add(process);
  }

  public Definitions parse(InputStream stream) throws WsdlParseException {
    Definitions definitions = new Definitions();
    setInput(stream, "UTF-8");
    definitions.read(this);
    try {
      for (PostParseProcessor process : postParseBlocks) {
        process.postParse();
      }
    } catch (ConnectionException e) {
      throw new WsdlParseException(e.getMessage(), e);
    }
    return definitions;
  }
}