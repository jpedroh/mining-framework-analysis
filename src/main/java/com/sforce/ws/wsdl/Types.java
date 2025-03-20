package com.sforce.ws.wsdl;
import java.util.HashMap;
import javax.xml.namespace.QName;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.parser.XmlInputStream;

/**
 * This class represents Definitions->types.
 *
 * @author http://cheenath.com
 * @version 1.0
 * @since 1.0   Nov 5, 2005
 */
public class Types extends WsdlNode {
  private HashMap<String, Schema> schemas = new HashMap<String, Schema>();

  @Override public String toString() {
    return "Types{" + "schemas=" + schemas + '}';
  }

  public void read(WsdlParser parser) throws WsdlParseException {
    int eventType = parser.getEventType();
    while (true) {
      if (eventType == XmlInputStream.START_TAG) {
        String name = parser.getName();
        String namespace = parser.getNamespace();
        if (SCHEMA.equals(name)) {
          if (!SCHEMA_NS.equals(namespace)) {
            throw new WsdlParseException("Unsupport schema version: " + namespace + ". It must be: " + SCHEMA_NS);
          }
          Schema schema = new Schema(this);
          schema.read(parser);
          schemas.put(schema.getTargetNamespace(), schema);
        }
      } else {
        if (eventType == XmlInputStream.END_TAG) {
          String name = parser.getName();
          String namespace = parser.getNamespace();
          if (TYPES.equals(name) && WSDL_NS.equals(namespace)) {
            break;
          }
        } else {
          if (eventType == XmlInputStream.END_DOCUMENT) {
            throw new WsdlParseException("Failed to find end tag for \'types\'");
          }
        }
      }
      eventType = parser.next();
    }
  }

  public java.util.Collection<Schema> getSchemas() {
    return schemas.values();
  }

  public Element getElement(QName element) throws ConnectionException {
    Schema schema = getSchema(element);
    Element el = schema.getGlobalElement(element.getLocalPart());
    if (el == null) {
      throw new ConnectionException("Unable to find element for " + element);
    }
    return el;
  }

  public Attribute getAttribute(QName element) throws ConnectionException {
    Schema schema = getSchema(element);
    Attribute el = schema.getGlobalAttribute(element.getLocalPart());
    if (el == null) {
      throw new ConnectionException("Unable to find attribute for " + element);
    }
    return el;
  }

  public AttributeGroup getAttributeGroup(QName element) throws ConnectionException {
    Schema schema = getSchema(element);
    AttributeGroup el = schema.getGlobalAttributeGroup(element.getLocalPart());
    if (el == null) {
      throw new ConnectionException("Unable to find attribute group for " + element);
    }
    return el;
  }

  private Schema getSchema(QName element) throws ConnectionException {
    Schema schema = schemas.get(element.getNamespaceURI());
    if (schema == null) {
      throw new ConnectionException("Unable to find schema for element; " + element);
    }
    return schema;
  }

  public SimpleType getSimpleTypeAllowNull(QName type) {
    Schema schema = schemas.get(type.getNamespaceURI());
    if (schema == null) {
      return null;
    }
    return schema.getSimpleType(type.getLocalPart());
  }

  public ComplexType getComplexType(QName type) throws ConnectionException {
    Schema schema = getSchema(type);
    ComplexType ct = schema.getComplexType(type.getLocalPart());
    if (ct == null) {
      throw new ConnectionException("Unable to find complexType for " + type);
    }
    return ct;
  }
}