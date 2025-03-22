package org.docx4j;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.IOUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.JAXBAssociation;
import org.docx4j.jaxb.JAXBImplementation;
import org.docx4j.jaxb.JaxbValidationEventHandler;
import org.docx4j.jaxb.McIgnorableNamespaceDeclarator;
import org.docx4j.jaxb.NamespacePrefixMapperUtils;
import org.docx4j.jaxb.NamespacePrefixMappings;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.parts.JaxbXmlPart;
import org.docx4j.org.apache.xml.security.Init;
import org.docx4j.org.apache.xml.security.c14n.CanonicalizationException;
import org.docx4j.org.apache.xml.security.c14n.Canonicalizer;
import org.docx4j.org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.docx4j.utils.XPathFactoryUtil;
import org.docx4j.utils.XmlSerializerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils {
  private static Logger log = LoggerFactory.getLogger(XmlUtils.class);

  public static String TRANSFORMER_FACTORY_PROCESSOR_XALAN = "org.apache.xalan.processor.TransformerFactoryImpl";

  private static javax.xml.transform.TransformerFactory transformerFactory;

  /**
	 * @since 2.8.1
	 */
  public static TransformerFactory getTransformerFactory() {
    return transformerFactory;
  }

  final private static DocumentBuilderFactory documentBuilderFactory;

  /**
	 * @since 2.8.1
	 * 
	 * TODO replace the various DocumentBuilderFactory.newInstance()
	 * throughout docx4j with a call to this.
	 */
  @Deprecated public static DocumentBuilderFactory getDocumentBuilderFactory() {
    return documentBuilderFactory;
  }

  /**
	 * Use the suitably configured DocumentBuilderFactory to provide
	 * a new instance of DocumentBuilder. Remember that DocumentBuilder is not thread-safe!
	 * @return
	 * @since 3.2.0
	 */
  public static DocumentBuilder getNewDocumentBuilder() {
    synchronized (documentBuilderFactory) {
      try {
        return documentBuilderFactory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        log.error(e.getMessage(), e);
        return null;
      }
    }
  }

  static {
    instantiateTransformerFactory();
    log.debug(System.getProperty("java.vendor"));
    log.debug(System.getProperty("java.version"));
    String sp = Docx4jProperties.getProperty("javax.xml.parsers.SAXParserFactory");
    if (sp != null) {
      System.setProperty("javax.xml.parsers.SAXParserFactory", sp);
      log.info("setProperty " + sp + " (from docx4j.properties)");
    } else {
      if (Docx4jProperties.getProperty("docx4j.javax.xml.parsers.SAXParserFactory.donotset", false)) {
        log.info("Not setting docx4j.javax.xml.parsers.SAXParserFactory");
      } else {
        if ((System.getProperty("java.version").startsWith("1.6") && System.getProperty("java.vendor").startsWith("Sun")) || (System.getProperty("java.version").startsWith("1.7") && System.getProperty("java.vendor").startsWith("Oracle")) || (System.getProperty("java.version").startsWith("1.8") && System.getProperty("java.vendor").startsWith("Oracle")) || (System.getProperty("java.version").startsWith("1.9") && System.getProperty("java.vendor").startsWith("Oracle")) || (System.getProperty("java.version").startsWith("1.7") && System.getProperty("java.vendor").startsWith("Jeroen"))) {
          System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
          log.info("setProperty com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
        } else {
          log.warn("default SAXParserFactory property : " + System.getProperty("javax.xml.parsers.SAXParserFactory") + "\n Please consider using Xerces.");
        }
      }
    }
    log.info("actual: " + SAXParserFactory.newInstance().getClass().getName());
    String dbf = Docx4jProperties.getProperty("javax.xml.parsers.DocumentBuilderFactory");
    if (dbf != null) {
      System.setProperty("javax.xml.parsers.DocumentBuilderFactory", dbf);
      log.info("setProperty " + dbf + " (from docx4j.properties)");
    } else {
      if (Docx4jProperties.getProperty("docx4j.javax.xml.parsers.DocumentBuilderFactory.donotset", false)) {
        log.info("Not setting docx4j.javax.xml.parsers.DocumentBuilderFactory");
      } else {
        if ((System.getProperty("java.version").startsWith("1.6") && System.getProperty("java.vendor").startsWith("Sun")) || (System.getProperty("java.version").startsWith("1.7") && System.getProperty("java.vendor").startsWith("Oracle")) || (System.getProperty("java.version").startsWith("1.8") && System.getProperty("java.vendor").startsWith("Oracle")) || (System.getProperty("java.version").startsWith("1.9") && System.getProperty("java.vendor").startsWith("Oracle")) || (System.getProperty("java.version").startsWith("1.7") && System.getProperty("java.vendor").startsWith("Jeroen"))) {
          System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
          log.info("setProperty com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        } else {
          log.warn("default DocumentBuilderFactory property: " + System.getProperty("javax.xml.parsers.DocumentBuilderFactory") + "\n Please consider using Xerces.");
        }
      }
    }
    documentBuilderFactory = DocumentBuilderFactory.newInstance();
    log.info("actual: " + documentBuilderFactory.getClass().getName());
    documentBuilderFactory.setNamespaceAware(true);
    try {
      documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    } catch (ParserConfigurationException e) {
      log.warn(e.getMessage());
      StackTraceElement[] elements = e.getStackTrace();
      if (elements.length > 0) {
        log.warn(elements[0].toString());
      }
    }
    try {
      documentBuilderFactory.setXIncludeAware(false);
    } catch (Exception e) {
      log.warn(e.getMessage());
      StackTraceElement[] elements = e.getStackTrace();
      if (elements.length > 0) {
        log.warn(elements[0].toString());
      }
    }
    try {
      documentBuilderFactory.setExpandEntityReferences(false);
    } catch (Exception e) {
      log.warn(e.getMessage());
      StackTraceElement[] elements = e.getStackTrace();
      if (elements.length > 0) {
        log.warn(elements[0].toString());
      }
    }
    try {
      documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    } catch (ParserConfigurationException e) {
      log.warn(e.getMessage());
      StackTraceElement[] elements = e.getStackTrace();
      if (elements.length > 0) {
        log.warn(elements[0].toString());
      }
    }
    try {
      documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    } catch (ParserConfigurationException e) {
      log.warn(e.getMessage());
      StackTraceElement[] elements = e.getStackTrace();
      if (elements.length > 0) {
        log.warn(elements[0].toString());
      }
    }
  }

  private static void instantiateTransformerFactory() {
    String originalSystemProperty = System.getProperty("javax.xml.transform.TransformerFactory");
    try {
      System.setProperty("javax.xml.transform.TransformerFactory", TRANSFORMER_FACTORY_PROCESSOR_XALAN);
      transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
      if (originalSystemProperty == null) {
        System.clearProperty("javax.xml.transform.TransformerFactory");
      } else {
        System.setProperty("javax.xml.transform.TransformerFactory", originalSystemProperty);
      }
    } catch (javax.xml.transform.TransformerFactoryConfigurationError e) {
      log.warn("Xalan jar missing from classpath; xslt not supported");
      if (originalSystemProperty == null) {
        System.clearProperty("javax.xml.transform.TransformerFactory");
      } else {
        System.setProperty("javax.xml.transform.TransformerFactory", originalSystemProperty);
      }
      transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
    }
    LoggingErrorListener errorListener = new LoggingErrorListener(false);
    transformerFactory.setErrorListener(errorListener);
  }

  /**
	 * If an object is wrapped in a JAXBElement, return the object.
	 * Warning: be careful with this. If you are copying objects
	 * into your document (rather than just reading them), you'll
	 * probably want the object to remain wrapped (JAXB usually wraps them
	 * for a reason; without the wrapper, you'll (probably?) need an 
	 * @XmlRootElement annotation in order to be able to marshall ie save your
	 * document).
	 * 
	 * @param o
	 * @return
	 */
  public static Object unwrap(Object o) {
    if (o == null) {
      return null;
    }
    if (o instanceof javax.xml.bind.JAXBElement) {
      log.debug("Unwrapped " + ((JAXBElement) o).getDeclaredType().getName());
      log.debug("name: " + ((JAXBElement) o).getName());
      return ((JAXBElement) o).getValue();
    } else {
      return o;
    }
  }

  public static String JAXBElementDebug(javax.xml.bind.JAXBElement o) {
    String prefix = null;
    if (o.getName().getNamespaceURI() != null) {
      try {
        prefix = NamespacePrefixMapperUtils.getPreferredPrefix(o.getName().getNamespaceURI(), null, false);
      } catch (JAXBException e) {
        e.printStackTrace();
      }
    }
    if (prefix != null) {
      return prefix + ':' + o.getName().getLocalPart() + " is a javax.xml.bind.JAXBElement; it has declared type " + o.getDeclaredType().getName();
    } else {
      return o.getName() + " is a javax.xml.bind.JAXBElement; it has declared type " + o.getDeclaredType().getName();
    }
  }

  /**
	 * @param list
	 * @param name
	 * @return
	 * @since 3.2.0
	 */
  public static JAXBElement<?> getListItemByQName(List<JAXBElement<?>> list, QName name) {
    for (JAXBElement<?> el : list) {
      if (el.getName().equals(name)) {
        return el;
      }
    }
    return null;
  }

  /** Unmarshal an InputStream as an object in the package org.docx4j.jaxb.document.
	 *  Note: you should ensure you include a namespace declaration for w: and
	 *  any other namespace in the xml string.
	 *  Also, the object you are attempting to unmarshall to might need to
	 *  have an @XmlRootElement annotation for things to work.  */
  public static Object unmarshal(InputStream is) throws JAXBException {
    return unmarshal(is, Context.jc);
  }

  public static Object unmarshal(InputStream is, JAXBContext jc) throws JAXBException {
    XMLInputFactory xif = XMLInputFactory.newInstance();
    xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    XMLStreamReader xsr = null;
    try {
      xsr = xif.createXMLStreamReader(is);
    } catch (XMLStreamException e) {
      throw new JAXBException(e);
    }
    Object o = null;
    Unmarshaller u = jc.createUnmarshaller();
    JaxbValidationEventHandler eventHandler = new JaxbValidationEventHandler();
    u.setEventHandler(eventHandler);
    try {
      o = u.unmarshal(xsr);
      return o;
    } catch (UnmarshalException ue) {
      if (ue.getLinkedException() != null && ue.getLinkedException().getMessage().contains("entity")) {
        log.error(ue.getMessage(), ue);
        throw ue;
      }
      if (is.markSupported()) {
        log.info("encountered unexpected content; pre-processing");
        eventHandler.setContinue(true);
        try {
          Templates mcPreprocessorXslt = JaxbValidationEventHandler.getMcPreprocessor();
          is.reset();
          JAXBResult result = XmlUtils.prepareJAXBResult(jc);
          XmlUtils.transform(new StreamSource(is), mcPreprocessorXslt, null, result);
          return result.getResult();
        } catch (Exception e) {
          throw new JAXBException("Preprocessing exception", e);
        }
      } else {
        log.error(ue.getMessage(), ue);
        log.error(".. and mark not supported");
        throw ue;
      }
    }
  }

  /** Unmarshal a String as an object in the package org.docx4j.jaxb.document.
	 *  Note: you should ensure you include a namespace declaration for w: and
	 *  any other namespace in the xml string.
	 *  Also, the object you are attempting to unmarshall to might need to
	 *  have an @XmlRootElement annotation for things to work.  */
  public static Object unmarshalString(String str) throws JAXBException {
    return unmarshalString(str, Context.jc);
  }

  public static Object unmarshalString(String str, JAXBContext jc, Class declaredType) throws JAXBException {
    Unmarshaller u = jc.createUnmarshaller();
    u.setEventHandler(new org.docx4j.jaxb.JaxbValidationEventHandler());
    Object o = u.unmarshal(new javax.xml.transform.stream.StreamSource(new java.io.StringReader(str)), declaredType);
    if (o instanceof JAXBElement) {
      return ((JAXBElement) o).getValue();
    } else {
      return o;
    }
  }

  public static Object unmarshalString(String str, JAXBContext jc) throws JAXBException {
    log.debug("Unmarshalling \'" + str + "\'");
    str = str.trim().replaceFirst("^([\\W]+)<", "<");
    Unmarshaller u = jc.createUnmarshaller();
    JaxbValidationEventHandler eventHandler = new JaxbValidationEventHandler();
    u.setEventHandler(eventHandler);
    try {
      return u.unmarshal(new javax.xml.transform.stream.StreamSource(new java.io.StringReader(str)));
    } catch (UnmarshalException ue) {
      if (ue.getLinkedException() != null && ue.getLinkedException().getMessage().contains("entity")) {
        log.error(ue.getMessage(), ue);
        throw ue;
      }
      log.info("encountered unexpected content; pre-processing");
      eventHandler.setContinue(true);
      try {
        Templates mcPreprocessorXslt = JaxbValidationEventHandler.getMcPreprocessor();
        JAXBResult result = XmlUtils.prepareJAXBResult(jc);
        XmlUtils.transform(new StreamSource(new java.io.StringReader(str)), mcPreprocessorXslt, null, result);
        return result.getResult();
      } catch (Exception e) {
        throw new JAXBException("Preprocessing exception", e);
      }
    }
  }

  public static Object unmarshal(Node n) throws JAXBException {
    Unmarshaller u = Context.jc.createUnmarshaller();
    JaxbValidationEventHandler veh = new org.docx4j.jaxb.JaxbValidationEventHandler();
    veh.setContinue(true);
    u.setEventHandler(veh);
    return u.unmarshal(n);
  }

  public static Object unmarshal(Node n, JAXBContext jc, Class declaredType) throws JAXBException {
    Unmarshaller u = jc.createUnmarshaller();
    u.setEventHandler(new org.docx4j.jaxb.JaxbValidationEventHandler());
    Object o = u.unmarshal(n, declaredType);
    if (o instanceof javax.xml.bind.JAXBElement) {
      return ((JAXBElement) o).getValue();
    } else {
      return o;
    }
  }

  /**
	 * Give a string of wml containing ${key1}, ${key2}, return a suitable
	 * object.  
	 * 
	 * @param wmlTemplateString
	 * @param mappings
	 * @return
	 * @see JaxbXmlPart.variableReplace which can invoke this more efficiently
	 */
  public static Object unmarshallFromTemplate(String wmlTemplateString, java.util.Map<String, ?> mappings) throws JAXBException {
    return unmarshallFromTemplate(wmlTemplateString, mappings, Context.jc);
  }

  public static Object unmarshallFromTemplate(String wmlTemplateString, java.util.Map<String, ?> mappings, JAXBContext jc) throws JAXBException {
    String wmlString = replace(wmlTemplateString, 0, new StringBuilder(), mappings).toString();
    log.debug("Results of substitution: " + wmlString);
    return unmarshalString(wmlString, jc);
  }

  public static Object unmarshallFromTemplate(String wmlTemplateString, java.util.Map<String, ?> mappings, JAXBContext jc, Class<?> declaredType) throws JAXBException {
    String wmlString = replace(wmlTemplateString, 0, new StringBuilder(), mappings).toString();
    return unmarshalString(wmlString, jc, declaredType);
  }

  private static StringBuilder replace(String wmlTemplateString, int offset, StringBuilder strB, java.util.Map<String, ?> mappings) {
    int startKey = wmlTemplateString.indexOf("${", offset);
    if (startKey == -1) {
      return strB.append(wmlTemplateString.substring(offset));
    } else {
      strB.append(wmlTemplateString.substring(offset, startKey));
      int keyEnd = wmlTemplateString.indexOf('}', startKey);
      String key = wmlTemplateString.substring(startKey + 2, keyEnd);
      Object val = mappings.get(key);
      if (val == null) {
        log.warn("Invalid key \'" + key + "\' or key not mapped to a value");
        strB.append(key);
      } else {
        strB.append(val.toString());
      }
      return replace(wmlTemplateString, keyEnd + 1, strB, mappings);
    }
  }

  /**
	 * Marshal this object to a String, pretty printed, and without an XML declaration.
	 * Useful for debugging.
	 * 
	 * @param o
	 * @return
	 * @since 3.0.1
	 */
  public static String marshaltoString(Object o) {
    JAXBContext jc = Context.jc;
    return marshaltoString(o, true, true, jc);
  }

  /**
	 * Use the specified JAXBContext to marshal this object to a String, pretty printed, and without an XML declaration.
	 * Useful for debugging.
	 * 
	 * @param o
	 * @return
	 * @since 3.0.1
	 */
  public static String marshaltoString(Object o, JAXBContext jc) {
    return marshaltoString(o, true, true, jc);
  }

  /** Marshal to a String */
  @Deprecated public static String marshaltoString(Object o, boolean suppressDeclaration) {
    JAXBContext jc = Context.jc;
    return marshaltoString(o, suppressDeclaration, false, jc);
  }

  /** Marshal to a String */
  @Deprecated public static String marshaltoString(Object o, boolean suppressDeclaration, JAXBContext jc) {
    return marshaltoString(o, suppressDeclaration, false, jc);
  }

  /** Marshal to a String */
  public static String marshaltoString(Object o, boolean suppressDeclaration, boolean prettyprint) {
    JAXBContext jc = Context.jc;
    return marshaltoString(o, suppressDeclaration, prettyprint, jc);
  }

  /**
	 * @param prefixMapper
	 * @param o
	 * @since 3.1.1
	 */
  private static String setMcIgnorable(McIgnorableNamespaceDeclarator prefixMapper, Object o) {
    if (o instanceof org.docx4j.wml.Document) {
      String ignorables = ((org.docx4j.wml.Document) o).getIgnorable();
      if (ignorables != null) {
        prefixMapper.setMcIgnorable(ignorables);
      }
      return ignorables;
    }
    return null;
  }

  /** Removes superflouous namespaces.
	 * 
	 * It makes things neater, at the cost of some extra processing.
	 *  
	 * @throws InvalidCanonicalizerException 
	 * @throws CanonicalizationException 
	 */
  private static byte[] trimNamespaces(org.w3c.dom.Document doc, String ignorables) throws InvalidCanonicalizerException, CanonicalizationException {
    log.debug("Input to Canonicalizer: " + XmlUtils.w3CDomNodeToString(doc));
    Init.init();
    Canonicalizer c = Canonicalizer.getInstance(CanonicalizationMethod.EXCLUSIVE);
    return c.canonicalizeSubtree(doc, ignorables);
  }

  /** Marshal to a String */
  public static String marshaltoString(Object o, boolean suppressDeclaration, boolean prettyprint, JAXBContext jc) {
    if (o == null) {
      return null;
    }
    try {
      Marshaller m = jc.createMarshaller();
      NamespacePrefixMapperUtils.setProperty(m, NamespacePrefixMapperUtils.getPrefixMapper());
      String ignorables = setMcIgnorable(((McIgnorableNamespaceDeclarator) NamespacePrefixMapperUtils.getPrefixMapper()), o);
      if (prettyprint) {
        m.setProperty("jaxb.formatted.output", true);
      }
      if (suppressDeclaration) {
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
      }
      if (Docx4jProperties.getProperty("docx4j.jaxb.marshal.canonicalize", false)) {
        org.w3c.dom.Document doc = marshaltoW3CDomDocument(o, jc);
        byte[] bytes = trimNamespaces(doc, ignorables);
        return new String(bytes, "UTF-8");
      } else {
        StringWriter sWriter = new StringWriter();
        m.marshal(o, sWriter);
        return sWriter.toString();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Marshal to a String, for object
	 *  missing an @XmlRootElement annotation.  */
  public static String marshaltoString(Object o, boolean suppressDeclaration, boolean prettyprint, JAXBContext jc, String uri, String local, Class declaredType) {
    try {
      Marshaller m = jc.createMarshaller();
      NamespacePrefixMapperUtils.setProperty(m, NamespacePrefixMapperUtils.getPrefixMapper());
      String ignorables = setMcIgnorable(((McIgnorableNamespaceDeclarator) NamespacePrefixMapperUtils.getPrefixMapper()), o);
      if (prettyprint) {
        m.setProperty("jaxb.formatted.output", true);
      }
      if (suppressDeclaration) {
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
      }
      if (Docx4jProperties.getProperty("docx4j.jaxb.marshal.canonicalize", false)) {
        org.w3c.dom.Document doc = marshaltoW3CDomDocument(o, jc, uri, local, declaredType);
        byte[] bytes = trimNamespaces(doc, ignorables);
        return new String(bytes, "UTF-8");
      } else {
        StringWriter sWriter = new StringWriter();
        m.marshal(new JAXBElement(new QName(uri, local), declaredType, o), sWriter);
        return sWriter.toString();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static java.io.InputStream marshaltoInputStream(Object o, boolean suppressDeclaration, JAXBContext jc) {
    try {
      Marshaller m = jc.createMarshaller();
      NamespacePrefixMapperUtils.setProperty(m, NamespacePrefixMapperUtils.getPrefixMapper());
      String ignorables = setMcIgnorable(((McIgnorableNamespaceDeclarator) NamespacePrefixMapperUtils.getPrefixMapper()), o);
      if (suppressDeclaration) {
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
      }
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      m.marshal(o, os);
      if (Docx4jProperties.getProperty("docx4j.jaxb.marshal.canonicalize", false)) {
        org.w3c.dom.Document doc = marshaltoW3CDomDocument(o, jc);
        byte[] bytes = trimNamespaces(doc, ignorables);
        return new java.io.ByteArrayInputStream(bytes);
      } else {
        return new java.io.ByteArrayInputStream(os.toByteArray());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Marshal to a W3C document */
  public static org.w3c.dom.Document marshaltoW3CDomDocument(Object o) {
    return marshaltoW3CDomDocument(o, Context.jc);
  }

  /** Marshal to a W3C document */
  public static org.w3c.dom.Document marshaltoW3CDomDocument(Object o, JAXBContext jc) {
    try {
      Marshaller marshaller = jc.createMarshaller();
      NamespacePrefixMapperUtils.setProperty(marshaller, NamespacePrefixMapperUtils.getPrefixMapper());
      String ignorables = setMcIgnorable(((McIgnorableNamespaceDeclarator) NamespacePrefixMapperUtils.getPrefixMapper()), o);
      org.w3c.dom.Document doc = XmlUtils.getNewDocumentBuilder().newDocument();
      marshaller.marshal(o, doc);
      if (Docx4jProperties.getProperty("docx4j.jaxb.marshal.canonicalize", false)) {
        byte[] bytes = trimNamespaces(doc, ignorables);
        DocumentBuilder builder = XmlUtils.getDocumentBuilderFactory().newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(bytes));
      } else {
        return doc;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Marshal to a W3C document, for object
	 *  missing an @XmlRootElement annotation.  */
  public static org.w3c.dom.Document marshaltoW3CDomDocument(Object o, JAXBContext jc, String uri, String local, Class declaredType) {
    try {
      Marshaller marshaller = jc.createMarshaller();
      NamespacePrefixMapperUtils.setProperty(marshaller, NamespacePrefixMapperUtils.getPrefixMapper());
      String ignorables = setMcIgnorable(((McIgnorableNamespaceDeclarator) NamespacePrefixMapperUtils.getPrefixMapper()), o);
      org.w3c.dom.Document doc = XmlUtils.getNewDocumentBuilder().newDocument();
      marshaller.marshal(new JAXBElement(new QName(uri, local), declaredType, o), doc);
      if (Docx4jProperties.getProperty("docx4j.jaxb.marshal.canonicalize", false)) {
        byte[] bytes = trimNamespaces(doc, ignorables);
        DocumentBuilder builder = XmlUtils.getDocumentBuilderFactory().newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(bytes));
      } else {
        return doc;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Clone this JAXB object, using default JAXBContext. */
  public static <T extends java.lang.Object> T deepCopy(T value) {
    return deepCopy(value, Context.jc);
  }

  /** Clone this JAXB object
	 * @param value
	 * @param jc
	 * @return
	 */
  public static <T extends java.lang.Object> T deepCopy(T value, JAXBContext jc) {
    if (value == null) {
      throw new IllegalArgumentException("Can\'t clone a null argument");
    }
    JAXBElement<T> elem;
    try {
      if (value instanceof JAXBElement<?>) {
        Object wrapped = ((JAXBElement) value).getValue();
        @SuppressWarnings(value = { "unchecked" }) Class clazz = wrapped.getClass();
        JAXBElement contentObject = new JAXBElement(new QName(clazz.getSimpleName()), clazz, wrapped);
        JAXBSource source = new JAXBSource(jc, contentObject);
        elem = jc.createUnmarshaller().unmarshal(source, clazz);
      } else {
        @SuppressWarnings(value = { "unchecked" }) Class<T> clazz = (Class<T>) value.getClass();
        JAXBElement<T> contentObject = new JAXBElement<T>(new QName(clazz.getSimpleName()), clazz, value);
        JAXBSource source = new JAXBSource(jc, contentObject);
        elem = jc.createUnmarshaller().unmarshal(source, clazz);
      }
      T res;
      if (value instanceof JAXBElement<?>) {
        @SuppressWarnings(value = { "unchecked" }) T resT = (T) elem;
        res = resT;
      } else {
        @SuppressWarnings(value = { "unchecked" }) T resT = (T) elem.getValue();
        res = resT;
      }
      return res;
    } catch (JAXBException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  public static String w3CDomNodeToString(Node n) {
    StringWriter sw = new StringWriter();
    try {
      XmlSerializerUtil.serialize(new DOMSource(n), new StreamResult(sw), true, true);
      return sw.toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  /**
     * @param n
     * @param os
     * @throws Docx4JException 
     * 
     * @Since 3.3.0
     */
  public static void w3CDomNodeToOutputStream(Node n, OutputStream os) throws Docx4JException {
    XmlSerializerUtil.serialize(new DOMSource(n), new StreamResult(os), true, true);
  }

  /** Use DocumentBuilderFactory to create and return a new w3c dom Document. */
  public static org.w3c.dom.Document neww3cDomDocument() {
    return XmlUtils.getNewDocumentBuilder().newDocument();
  }

  /**
	   * @param docBuilder
	   *          the parser
	   * @param parent
	   *          node to add fragment to
	   * @param fragment
	   *          a well formed XML fragment
	 * @throws ParserConfigurationException 
	   */
  public static void appendXmlFragment(Document document, Node parent, String fragment) throws IOException, SAXException, ParserConfigurationException {
    Node fragmentNode = XmlUtils.getNewDocumentBuilder().parse(new InputSource(new StringReader(fragment))).getDocumentElement();
    fragmentNode = document.importNode(fragmentNode, true);
    parent.appendChild(fragmentNode);
  }

  /**
	 * Prepare a JAXB transformation result for some given context.
	 * @param context The JAXB context.
	 * @return The result data structure created.
	 * @throws Docx4JException In case of configuration errors.
	 */
  public static JAXBResult prepareJAXBResult(final JAXBContext context) throws Docx4JException {
    final JAXBResult result;
    try {
      final Unmarshaller unmarshaller = context.createUnmarshaller();
      unmarshaller.setEventHandler(new JaxbValidationEventHandler());
      result = new JAXBResult(unmarshaller);
    } catch (JAXBException e) {
      throw new Docx4JException("Error preparing empty JAXB result", e);
    }
    return result;
  }

  public static void transform(org.w3c.dom.Document doc, javax.xml.transform.Templates template, Map<String, Object> transformParameters, javax.xml.transform.Result result) throws Docx4JException {
    if (doc == null) {
      Throwable t = new Throwable();
      throw new Docx4JException("Null DOM Doc", t);
    }
    javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(doc);
    transform(domSource, template, transformParameters, result);
  }

  public static Templates getTransformerTemplate(javax.xml.transform.Source xsltSource) throws TransformerConfigurationException {
    return transformerFactory.newTemplates(xsltSource);
  }

  private static final String S_BUILTIN_EXTENSIONS_URL = "http://xml.apache.org/xalan";

  private static final String S_BUILTIN_EXTENSIONS_UNIVERSAL = "{" + S_BUILTIN_EXTENSIONS_URL + "}";

  private static final String S_KEY_CONTENT_HANDLER = S_BUILTIN_EXTENSIONS_UNIVERSAL + "content-handler";

  /**
     * 
     * Transform an input document using XSLT
     * 
     * @param doc
     * @param xslt
     * @param transformParameters
     * @param result
     * @throws Docx4JException In case serious transformation errors occur
     */
  public static void transform(javax.xml.transform.Source source, javax.xml.transform.Templates template, Map<String, Object> transformParameters, javax.xml.transform.Result result) throws Docx4JException {
    if (source == null) {
      Throwable t = new Throwable();
      throw new Docx4JException("Null Source doc", t);
    }
    javax.xml.transform.Transformer xformer;
    try {
      xformer = template.newTransformer();
    } catch (TransformerConfigurationException e) {
      throw new Docx4JException("The Transformer is ill-configured", e);
    }
    log.info("Using " + xformer.getClass().getName());
    if (xformer.getClass().getName().equals("org.apache.xalan.transformer.TransformerImpl")) {
      if (Docx4jProperties.getProperty("docx4j.xalan.XALANJ-2419.workaround", false)) {
        log.info("Working around https://issues.apache.org/jira/browse/XALANJ-2419");
        Properties p = xformer.getOutputProperties();
        String method = p.getProperty("method");
        System.out.println("method: " + method);
        if (method == null || method.equals("xml")) {
          ((org.apache.xalan.transformer.TransformerImpl) xformer).setOutputProperty(S_KEY_CONTENT_HANDLER, "org.docx4j.org.apache.xml.serializer.ToXMLStream");
        } else {
          if (method.equals("html")) {
            ((org.apache.xalan.transformer.TransformerImpl) xformer).setOutputProperty(S_KEY_CONTENT_HANDLER, "org.docx4j.org.apache.xml.serializer.ToHTMLStream");
          } else {
            if (method.equals("text")) {
              ((org.apache.xalan.transformer.TransformerImpl) xformer).setOutputProperty(S_KEY_CONTENT_HANDLER, "org.docx4j.org.apache.xml.serializer.ToTextStream");
            } else {
              log.warn("fallback for method: " + method);
              ((org.apache.xalan.transformer.TransformerImpl) xformer).setOutputProperty(S_KEY_CONTENT_HANDLER, "org.docx4j.org.apache.xml.serializer.ToUnknownStream");
            }
          }
        }
      }
    } else {
      log.error("Detected " + xformer.getClass().getName() + ", but require org.apache.xalan.transformer.TransformerImpl. " + "Ensure Xalan 2.7.x is on your classpath!");
    }
    LoggingErrorListener errorListener = new LoggingErrorListener(false);
    xformer.setErrorListener(errorListener);
    if (transformParameters != null) {
      Iterator parameterIterator = transformParameters.entrySet().iterator();
      while (parameterIterator.hasNext()) {
        Map.Entry pairs = (Map.Entry) parameterIterator.next();
        if (pairs.getKey() == null) {
          log.info("Skipped null key");
          continue;
        }
        if (pairs.getKey().equals("customXsltTemplates")) {
          continue;
        }
        if (pairs.getValue() == null) {
          log.warn("parameter \'" + pairs.getKey() + "\' was null.");
        } else {
          xformer.setParameter((String) pairs.getKey(), pairs.getValue());
        }
      }
    }
    try {
      xformer.transform(source, result);
    } catch (TransformerException e) {
      throw new Docx4JException("Cannot perform the transformation", e);
    } finally {
    }
  }

  /**
	 * Fetch JAXB Nodes matching an XPath (for example "//w:p").
	 * 
	 * In JAXB, this association is partial; not all XML elements have associated JAXB objects, 
	 * and not all JAXB objects have associated XML elements.  
	 * If the XPath returns an element which isn't associated
	 * with a JAXB object, since 3.0, this method will throw 
	 * XPathBinderAssociationIsPartialException, to distinguish
	 * from no matching elements.
	 * 
	 * If you have modified your JAXB objects (eg added or changed a 
	 * w:p paragraph), you need to update the association. The problem
	 * is that this can only be done ONCE, owing to a bug in JAXB:
	 * see https://jaxb.dev.java.net/issues/show_bug.cgi?id=459
	 * 
	 * So this is left for you to choose to do via the refreshXmlFirst parameter.   
	 * 
	 * @param binder
	 * @param jaxbElement
	 * @param refreshXmlFirst
	 * @param xpathExpr
	 * @return
	 * @throws JAXBException
	 * @throws XPathBinderAssociationIsPartialException 
	 */
  public static List<Object> getJAXBNodesViaXPath(Binder<Node> binder, Object jaxbElement, String xpathExpr, boolean refreshXmlFirst) throws JAXBException, XPathBinderAssociationIsPartialException {
    List<JAXBAssociation> associations = getJAXBAssociationsForXPath(binder, jaxbElement, xpathExpr, refreshXmlFirst);
    List<Object> resultList = new ArrayList<Object>();
    for (JAXBAssociation association : associations) {
      if (association.getJaxbObject() == null) {
        throw new XPathBinderAssociationIsPartialException("no object association for xpath result: " + association.getDomNode().getNodeName());
      } else {
        resultList.add(association.getJaxbObject());
      }
    }
    return resultList;
  }

  /**
	 * Fetch DOM node / JAXB object pairs matching an XPath (for example "//w:p").
	 * 
	 * In JAXB, this association is partial; not all XML elements have associated JAXB objects, 
	 * and not all JAXB objects have associated XML elements.  
	 * 
	 * If the XPath returns an element which isn't associated
	 * with a JAXB object, the element's pair will be null.
	 * 
	 * If you have modified your JAXB objects (eg added or changed a 
	 * w:p paragraph), you need to update the association. The problem
	 * is that this can only be done ONCE, owing to a bug in JAXB:
	 * see https://jaxb.dev.java.net/issues/show_bug.cgi?id=459
	 * 
	 * So this is left for you to choose to do via the refreshXmlFirst parameter.   
	 * 
	 * @param binder
	 * @param jaxbElement
	 * @param xpathExpr
	 * @param refreshXmlFirst
	 * @return
	 * @throws JAXBException
	 * @throws XPathBinderAssociationIsPartialException
	 * @since 3.0.0
	 */
  public static List<JAXBAssociation> getJAXBAssociationsForXPath(Binder<Node> binder, Object jaxbElement, String xpathExpr, boolean refreshXmlFirst) throws JAXBException, XPathBinderAssociationIsPartialException {
    if (binder == null) {
      log.warn("null binder");
    }
    if (jaxbElement == null) {
      log.warn("null jaxbElement");
    }
    Node node;
    if (refreshXmlFirst) {
      node = binder.updateXML(jaxbElement);
    }
    node = binder.getXMLNode(jaxbElement);
    if (node == null) {
      throw new XPathBinderAssociationIsPartialException("binder.getXMLNode returned null");
    }
    List<JAXBAssociation> resultList = new ArrayList<JAXBAssociation>();
    for (Node n : xpath(node, xpathExpr)) {
      resultList.add(new JAXBAssociation(n, binder.getJAXBNode(n)));
    }
    return resultList;
  }

  public static List<Node> xpath(Node node, String xpathExpression) {
    NamespaceContext nsContext = new NamespacePrefixMappings();
    return xpath(node, xpathExpression, nsContext);
  }

  public static List<Node> xpath(Node node, String xpathExpression, NamespaceContext nsContext) {
    if (log.isDebugEnabled()) {
      log.debug(w3CDomNodeToString(node));
    }
    XPath xpath = XPathFactoryUtil.newXPath();
    try {
      List<Node> result = new ArrayList<Node>();
      xpath.setNamespaceContext(nsContext);
      NodeList nl = (NodeList) xpath.evaluate(xpathExpression, node, XPathConstants.NODESET);
      if (log.isDebugEnabled()) {
        log.debug("evaluate returned " + nl.getLength());
      }
      if (nl.getLength() == 0) {
        log.info("no results for xpath " + xpathExpression);
      }
      for (int i = 0; i < nl.getLength(); i++) {
        result.add(nl.item(i));
      }
      return result;
    } catch (XPathExpressionException e) {
      log.error("Problem with \'" + xpathExpression + "\'", e);
      throw new RuntimeException(e);
    }
  }

  static class LoggingErrorListener implements ErrorListener {
    boolean strict;

    public LoggingErrorListener(boolean strict) {
    }

    public void warning(TransformerException exception) {
      log.warn(exception.getMessage(), exception);
    }

    public void error(TransformerException exception) throws TransformerException {
      log.error(exception.getMessage(), exception);
      if (strict) {
        throw exception;
      }
    }

    public void fatalError(TransformerException exception) throws TransformerException {
      if (Docx4jProperties.getProperty("docx4j.openpackaging.exceptions.LogBeforeThrow", true)) {
        log.error(exception.getMessage(), exception);
      }
      throw exception;
    }
  }

  public static void treeCopy(NodeList sourceNodes, Node destParent) {
    for (int i = 0; i < sourceNodes.getLength(); i++) {
      treeCopy((Node) sourceNodes.item(i), destParent);
    }
  }

  /**
	 * Copy a node from one DOM document to another.  Used
	 * to avoid relying on an underlying implementation which might 
	 * not support importNode 
	 * (eg Xalan's org.apache.xml.dtm.ref.DTMNodeProxy).
	 * 
	 * WARNING: doesn't fully support namespaces!
	 * 
	 * @param sourceNode
	 * @param destParent
	 */
  public static void treeCopy(Node sourceNode, Node destParent) {
    log.debug("node type" + sourceNode.getNodeType());
    switch (sourceNode.getNodeType()) {
      case Node.DOCUMENT_NODE:
      case Node.DOCUMENT_FRAGMENT_NODE:
      NodeList nodes = sourceNode.getChildNodes();
      if (nodes != null) {
        for (int i = 0; i < nodes.getLength(); i++) {
          log.debug("child " + i + "of DOCUMENT_NODE");
          treeCopy((Node) nodes.item(i), destParent);
        }
      }
      break;
      case Node.ELEMENT_NODE:
      log.debug("copying: " + sourceNode.getNodeName());
      Node newChild;
      if (destParent instanceof Document) {
        newChild = ((Document) destParent).createElementNS(sourceNode.getNamespaceURI(), sourceNode.getLocalName());
      } else {
        if (sourceNode.getNamespaceURI() != null) {
          newChild = destParent.getOwnerDocument().createElementNS(sourceNode.getNamespaceURI(), sourceNode.getLocalName());
        } else {
          newChild = destParent.getOwnerDocument().createElement(sourceNode.getNodeName());
        }
      }
      destParent.appendChild(newChild);
      NamedNodeMap atts = sourceNode.getAttributes();
      for (int i = 0; i < atts.getLength(); i++) {
        Attr attr = (Attr) atts.item(i);
        if (attr.getNodeName().startsWith("xmlns:")) {
          ;
        } else {
          if (attr.getNamespaceURI() == null) {
            ((org.w3c.dom.Element) newChild).setAttribute(attr.getName(), attr.getValue());
          } else {
            if (attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) {
              ;
            } else {
              if (attr.getNodeName() != null) {
                ((org.w3c.dom.Element) newChild).setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), attr.getValue());
              } else {
                ((org.w3c.dom.Element) newChild).setAttributeNS(attr.getNamespaceURI(), attr.getLocalName(), attr.getValue());
              }
            }
          }
        }
      }
      NodeList children = sourceNode.getChildNodes();
      if (children != null) {
        for (int i = 0; i < children.getLength(); i++) {
          treeCopy((Node) children.item(i), newChild);
        }
      }
      break;
      case Node.TEXT_NODE:
      if (destParent.getOwnerDocument() == null && destParent.getNodeName().equals("#document")) {
        Node textNode = ((Document) destParent).createTextNode(sourceNode.getNodeValue());
        destParent.appendChild(textNode);
      } else {
        Node textNode = destParent.getOwnerDocument().createTextNode(sourceNode.getNodeValue());
        Node appended = destParent.appendChild(textNode);
      }
      break;
    }
  }
}