package org.docx4j.openpackaging.parts;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.docx4j.Docx4jProperties;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.JaxbValidationEventHandler;
import org.docx4j.jaxb.McIgnorableNamespaceDeclarator;
import org.docx4j.jaxb.NamespacePrefixMapperUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.exceptions.LocationAwareXMLStreamException;
import org.docx4j.openpackaging.io3.stores.PartStore;
import org.docx4j.openpackaging.io3.stores.ZipPartStore;
import org.docx4j.openpackaging.io3.stores.ZipPartStore.ByteArray;
import org.docx4j.org.apache.xml.security.Init;
import org.docx4j.org.apache.xml.security.c14n.Canonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/** OPC Parts are either XML, or binary (or text) documents.
 * 
 *  Most are XML documents.
 *  
 *  docx4j aims to represent XML parts using JAXB.  
 *  
 *  Any XML Part for which we have a JAXB representation (eg the main
 *  document part) should extend this Part.  
 *  
 *  This class provides only one of the methods for serializing (marshalling) the 
 *  Java content tree back into XML data found in 
 *  javax.xml.bind.Marshaller interface.  You can always use 
 *  any of the others by getting the jaxbElement required by those
 *  methods.
 *  
 *  Insofar as unmarshalling is concerned, at present it doesn't 
 *  contain all the methods in javax.xml.bind.unmarshaller interface.
 *  This is because the content always comes from the same place
 *  (ie from a zip file or JCR via org.docx4j.io.*).  
 *  TODO - what is the best thing to unmarshall from?
 *  
 *  @param <E> type of the content tree object
 * */
public abstract class JaxbXmlPart<E extends java.lang.Object> extends Part {
  protected static Logger log = LoggerFactory.getLogger(JaxbXmlPart.class);

  public JaxbXmlPart(PartName partName) throws InvalidFormatException {
    super(partName);
    setJAXBContext(Context.jc);
  }

  public JaxbXmlPart(PartName partName, JAXBContext jc) throws InvalidFormatException {
    super(partName);
    setJAXBContext(jc);
  }

  protected JAXBContext jc;

  public void setJAXBContext(JAXBContext jc) {
    this.jc = jc;
  }

  /**
	 * @since 2.7
	 */
  public JAXBContext getJAXBContext() {
    return jc;
  }

  /** The content tree (ie JAXB representation of the Part) */
  protected E jaxbElement = null;

  /**
	 * Get the live contents of this part (the JAXB object model).
	 * (An alias/synonym for older getJaxbElement(), but now throws exception)
	 * @throws Docx4JException
	 * @return
	 * @since 3.0
	 */
  public E getContents() throws Docx4JException {
    InputStream is = null;
    if (jaxbElement == null) {
      if (this.getPackage() == null) {
        log.warn("This part not added to a package, so its contents can\'t be retrieved. ");
        return null;
      }
      PartStore partStore = this.getPackage().getSourcePartStore();
      if (partStore == null) {
        log.info("No PartStore defined for this package (it was probably created, not loaded). ");
        log.info(this.getPartName().getName() + ": did you initialise its contents to something?");
        return null;
      }
      try {
        String name = this.getPartName().getName();
        try {
          if (partStore != null) {
            this.setContentLengthAsLoaded(partStore.getPartSize(name.substring(1)));
          }
        } catch (UnsupportedOperationException uoe) {
        }
        is = partStore.loadPart(name.substring(1));
        if (is == null) {
          log.warn(name + " missing from part store");
        } else {
          log.debug("Lazily unmarshalling " + name);
          unmarshal(is);
        }
      } catch (JAXBException e) {
        log.error("Problem with part " + this.getPartName());
        throw new Docx4JException(e.getMessage(), e);
      } finally {
        IOUtils.closeQuietly(is);
      }
    }
    return jaxbElement;
  }

  /**
	 * Get the live contents of this part.
	 * (getContents() is preferred, this is the older/less friendly method name)
	 * @return
	 */
  @Deprecated public E getJaxbElement() {
    try {
      return getContents();
    } catch (Docx4JException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public void setJaxbElement(E jaxbElement) {
    this.jaxbElement = jaxbElement;
  }

  /**
	 * Set the  contents of this part.
	 * (Just an alias/synonym for setJaxbElement())
	 * @param jaxbElement
	 * @since 3.0
	 */
  public void setContents(E jaxbElement) {
    this.jaxbElement = jaxbElement;
  }

  public void setJaxbElement(JAXBResult result) throws JAXBException {
    setJaxbElement((E) result.getResult());
  }

  /**
	 * See your content as XML.  An easy way to invoke XmlUtils.marshaltoString 
	 *  
	 * @return
	 * @since 3.0.0
	 */
  public String getXML() {
    return XmlUtils.marshaltoString(getJaxbElement(), true, true, jc);
  }

  public boolean isUnmarshalled() {
    return jaxbElement != null;
  }

  /**
	 * unmarshallFromTemplate.  Where jaxbElement has not been
	 * unmarshalled yet, this is more efficient (3 times
	 * faster, in some testing) than calling
	 * XmlUtils.marshaltoString directly, since it avoids
	 * some JAXB processing.  
	 * 
	 * @param mappings
	 * @throws JAXBException
	 * @throws Docx4JException
	 * 
	 * @since 3.0.0
	 */
  public void variableReplace(java.util.Map<String, String> mappings) throws JAXBException, Docx4JException {
    String wmlTemplateString = null;
    if (jaxbElement == null) {
      PartStore partStore = this.getPackage().getSourcePartStore();
      String name = this.getPartName().getName();
      InputStream is = partStore.loadPart(name.substring(1));
      if (is == null) {
        log.warn(name + " missing from part store");
        throw new Docx4JException(name + " missing from part store");
      } else {
        log.info("Lazily unmarshalling " + name);
        try {
          wmlTemplateString = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
          throw new Docx4JException(e.getMessage(), e);
        }
      }
    } else {
      wmlTemplateString = XmlUtils.marshaltoString(jaxbElement, true, false, jc);
    }
    jaxbElement = (E) XmlUtils.unwrap(XmlUtils.unmarshallFromTemplate(wmlTemplateString, mappings, jc));
  }


<<<<<<< /usr/src/app/output/plutext/docx4j/aa42a5b3f77c350916c1b790b26cb2633e9aa31c/src/main/java/org/docx4j/openpackaging/parts/JaxbXmlPart.java/left.java
  /**
     * Use an XSLT to alter the contents of this part.
     * If you want to replace the content, next call setContents
     * 
     * @param xslt
     * @param transformParameters
     * @throws Exception
	 * @since 3.3.6
     */
  public E transform(Templates xslt, Map<String, Object> transformParameters) throws Docx4JException {
    JAXBResult result = XmlUtils.prepareJAXBResult(jc);
    if (jaxbElement == null) {
      PartStore partStore = this.getPackage().getSourcePartStore();
      String name = this.getPartName().getName();
      InputStream is = partStore.loadPart(name.substring(1));
      if (is == null) {
        log.warn(name + " missing from part store");
        throw new Docx4JException(name + " missing from part store");
      }
      XmlUtils.transform(new StreamSource(is), xslt, transformParameters, result);
    } else {
      org.w3c.dom.Document doc = org.docx4j.XmlUtils.neww3cDomDocument();
      try {
        this.marshal(doc);
      } catch (JAXBException e) {
        throw new Docx4JException("Marshalling exception preparing content for transform", e);
      }
      org.docx4j.XmlUtils.transform(doc, xslt, transformParameters, result);
    }
    try {
      return (E) XmlUtils.unwrap(result.getResult());
    } catch (JAXBException e) {
      throw new Docx4JException("Problem with transform result", e);
    }
  }
=======
  /**
     * Use an XSLT to alter the contents of this part.  You can transform to whatever
     * you like (ie it doesn't have to be WordML content), which is why the API design
     * is that you provide the Result object. 
     *  
     * If you do want to replace the content in this part, convert your result to
     * and element or input stream, then invoke unmarshal on it, then setContents. 
     * (Unmarshal takes care of any unexpected content, sidestepping the issue of 
     *  whether to do that before the transform (where reading the part directly),
     *  or after).
     * 
     * @param xslt
     * @param transformParameters
     * @throws Exception
	 * @since 3.3.6
     */
  public void transform(Templates xslt, Map<String, Object> transformParameters, Result result) throws Docx4JException {
    if (jaxbElement == null) {
      PartStore partStore = this.getPackage().getSourcePartStore();
      String name = this.getPartName().getName();
      InputStream is = partStore.loadPart(name.substring(1));
      if (is == null) {
        log.warn(name + " missing from part store");
        throw new Docx4JException(name + " missing from part store");
      }
      XmlUtils.transform(new StreamSource(is), xslt, transformParameters, result);
    } else {
      org.w3c.dom.Document doc = org.docx4j.XmlUtils.neww3cDomDocument();
      try {
        this.marshal(doc);
      } catch (JAXBException e) {
        throw new Docx4JException("Marshalling exception preparing content for transform", e);
      }
      org.docx4j.XmlUtils.transform(doc, xslt, transformParameters, result);
    }
  }
>>>>>>> /usr/src/app/output/plutext/docx4j/aa42a5b3f77c350916c1b790b26cb2633e9aa31c/src/main/java/org/docx4j/openpackaging/parts/JaxbXmlPart.java/right.java


  /**
	 * Replace the contents of this part with the output of passing it through your SAXHandler. 
	 * This is offered as an alternative to the similar methods which use StAX.  If you are
	 * unsure which to use, you should probably use the StAX approach.
	 * 
	 * This is most efficient in the case where there has been no need for JAXB to unmarshal
	 * the contents.  In this case, it is possible to process then save the contents without
	 * incurring JAXB overhead (you may see 1/4 heap usage).
	 * 
	 * @param saxHandler
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws Docx4JException
	 * @throws IOException
	 * @throws JAXBException
	 */
  public void pipe(SAXHandler saxHandler) throws ParserConfigurationException, SAXException, Docx4JException, IOException, JAXBException {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = spf.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();
    xmlReader.setContentHandler(saxHandler);
    PartStore partStore = null;
    if (jaxbElement == null) {
      partStore = this.getPackage().getSourcePartStore();
      String name = this.getPartName().getName();
      InputStream is = partStore.loadPart(name.substring(1));
      if (is == null) {
        log.warn(name + " missing from part store");
        throw new Docx4JException(name + " missing from part store");
      } else {
        log.info("Fetching from part store " + name);
        xmlReader.parse(new InputSource(is));
      }
    } else {
      xmlReader.parse(new InputSource(XmlUtils.marshaltoInputStream(jaxbElement, true, this.jc)));
    }
    if (jaxbElement == null && partStore instanceof ZipPartStore) {
      log.debug("Just update the entry in the ZipPartStore");
      ByteArray byteArray = ((ZipPartStore) partStore).getByteArray(this.getPartName().getName().substring(1));
      byteArray.setBytes(saxHandler.getOutputStream().toByteArray());
    } else {
      if (jaxbElement == null && log.isInfoEnabled()) {
        log.info(partStore.getClass().getName() + ": can\'t update in place, so unmarshalling.");
      } else {
        log.debug("unmarshalling");
      }
      jaxbElement = this.unmarshal(new ByteArrayInputStream(saxHandler.getOutputStream().toByteArray()));
    }
  }

  /**
	 * Replace the contents of this part with the output of passing it through your StAXHandler. 
	 * 
	 * This is most efficient in the case where there has been no need for JAXB to unmarshal
	 * the contents.  In this case, it is possible to process then save the contents without
	 * incurring JAXB overhead (you may see 1/4 heap usage).
	 * 
	 * @param handler
	 * @throws XMLStreamException
	 * @throws Docx4JException
	 * @throws JAXBException
	 */
  public void pipe(StAXHandlerInterface handler) throws XMLStreamException, Docx4JException, JAXBException {
    pipe(handler, null);
  }

  /**
	 * Replace the contents of this part with the output of passing it through your StAXHandler. 
	 * 
	 * This is most efficient in the case where there has been no need for JAXB to unmarshal
	 * the contents.  In this case, it is possible to process then save the contents without
	 * incurring JAXB overhead (you may see 1/4 heap usage).
	 * 
	 * @param handler
	 * @param filter
	 * @throws XMLStreamException
	 * @throws Docx4JException
	 * @throws JAXBException
	 */
  public void pipe(StAXHandlerInterface handler, StreamFilter filter) throws XMLStreamException, Docx4JException, JAXBException {
    XMLInputFactory xmlif = null;
    xmlif = XMLInputFactory.newInstance();
    xmlif.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
    xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
    xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
    xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
    xmlif.setXMLReporter((new XMLReporter() {
      @Override public void report(String message, String errorType, Object relatedInformation, Location location) throws XMLStreamException {
        log.warn("Error:" + errorType + ", " + message + " at line " + location.getLineNumber() + ", col " + location.getColumnNumber());
      }
    }));
    XMLStreamReader xmlr = null;
    PartStore partStore = null;
    if (jaxbElement == null) {
      partStore = this.getPackage().getSourcePartStore();
      String name = this.getPartName().getName();
      InputStream is = partStore.loadPart(name.substring(1));
      if (is == null) {
        log.warn(name + " missing from part store");
        throw new Docx4JException(name + " missing from part store");
      } else {
        log.info("Fetching from part store " + name);
        if (filter == null) {
          xmlr = xmlif.createXMLStreamReader(is);
        } else {
          xmlr = xmlif.createFilteredReader(xmlif.createXMLStreamReader(is), filter);
        }
      }
    } else {
      if (filter == null) {
        xmlr = xmlif.createXMLStreamReader(XmlUtils.marshaltoInputStream(jaxbElement, true, this.jc));
      } else {
        xmlr = xmlif.createFilteredReader(xmlif.createXMLStreamReader(XmlUtils.marshaltoInputStream(jaxbElement, true, this.jc)), filter);
      }
    }
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    XMLStreamWriter xmlWriter = null;
    ByteArrayOutputStream baos = null;
    if (jaxbElement == null) {
      baos = new ByteArrayOutputStream();
      xmlWriter = outputFactory.createXMLStreamWriter(baos, "UTF-8");
    } else {
      baos = new ByteArrayOutputStream();
      xmlWriter = outputFactory.createXMLStreamWriter(baos, "UTF-8");
    }
    try {
      log.debug("StAX implementation details:");
      log.debug(xmlr.getClass().getName());
      log.debug(xmlWriter.getClass().getName());
      handler.handle(xmlr, xmlWriter);
    } catch (LocationAwareXMLStreamException e) {
      log.error(e.getMessage() + " at line " + e.getLocation().getLineNumber() + ", col " + e.getLocation().getColumnNumber());
      e.getCause().printStackTrace();
      InputStream is = null;
      if (jaxbElement == null) {
        partStore = this.getPackage().getSourcePartStore();
        String name = this.getPartName().getName();
        is = partStore.loadPart(name.substring(1));
        if (is == null) {
          log.warn(name + " missing from part store");
          throw new Docx4JException(name + " missing from part store");
        }
      } else {
        is = XmlUtils.marshaltoInputStream(jaxbElement, true, this.jc);
      }
      if (is != null) {
        try {
          List<String> lines = IOUtils.readLines(is);
          String line = lines.get(e.getLocation().getLineNumber() - 1);
          int PRIOR_CHARS = 100;
          int start = 0;
          if (e.getLocation().getColumnNumber() > PRIOR_CHARS) {
            start = e.getLocation().getColumnNumber() - PRIOR_CHARS;
          }
          int end = e.getLocation().getColumnNumber() + PRIOR_CHARS;
          if (end > line.length() - 1) {
            end = line.length() - 1;
          }
          log.error("error is at pos " + PRIOR_CHARS + " in " + line.substring(start, end));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      throw (XMLStreamException) e.getCause();
    }
    xmlr.close();
    xmlWriter.flush();
    xmlWriter.close();
    if (jaxbElement == null && partStore instanceof ZipPartStore) {
      log.debug("Just update the entry in the ZipPartStore");
      ByteArray byteArray = ((ZipPartStore) partStore).getByteArray(this.getPartName().getName().substring(1));
      byteArray.setBytes(baos.toByteArray());
    } else {
      if (jaxbElement == null && log.isInfoEnabled()) {
        log.info(partStore.getClass().getName() + ": can\'t update in place, so unmarshalling.");
      } else {
        log.debug("unmarshalling");
      }
      jaxbElement = this.unmarshal(new ByteArrayInputStream(baos.toByteArray()));
    }
  }

  /**
     * Marshal the content tree rooted at <tt>jaxbElement</tt> into a DOM tree.
     * 
     * @param node
     *      DOM nodes will be added as children of this node.
     *      This parameter must be a Node that accepts children
     *      ({@link org.w3c.dom.Document},
     *      {@link  org.w3c.dom.DocumentFragment}, or
     *      {@link  org.w3c.dom.Element})
     * 
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
     */
  public void marshal(org.w3c.dom.Node node) throws JAXBException {
    marshal(node, NamespacePrefixMapperUtils.getPrefixMapper());
  }

  /**
     * Marshal the content tree rooted at <tt>jaxbElement</tt> into a DOM tree.
     * 
     * @param node
     *      DOM nodes will be added as children of this node.
     *      This parameter must be a Node that accepts children
     *      ({@link org.w3c.dom.Document},
     *      {@link  org.w3c.dom.DocumentFragment}, or
     *      {@link  org.w3c.dom.Element})
     * 
     * @throws JAXBException
     *      If any unexpected problem occurs during the marshalling.
     */
  public void marshal(org.w3c.dom.Node node, Object namespacePrefixMapper) throws JAXBException {
    try {
      Marshaller marshaller = jc.createMarshaller();
      NamespacePrefixMapperUtils.setProperty(marshaller, namespacePrefixMapper);
      getContents();
      setMceIgnorable((McIgnorableNamespaceDeclarator) namespacePrefixMapper);
      marshaller.marshal(jaxbElement, node);
      ((McIgnorableNamespaceDeclarator) namespacePrefixMapper).setMcIgnorable(null);
    } catch (Docx4JException e) {
      log.error(e.getMessage(), e);
      throw new JAXBException(e);
    } catch (JAXBException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  /**
	 * Marshal the content tree rooted at <tt>jaxbElement</tt> into an output
	 * stream, using org.docx4j.jaxb.NamespacePrefixMapper.
	 * 
	 * @param os
	 *            XML will be added to this stream.
	 * 
	 * @throws JAXBException
	 *             If any unexpected problem occurs during the marshalling.
	 */
  public void marshal(java.io.OutputStream os) throws JAXBException {
    marshal(os, NamespacePrefixMapperUtils.getPrefixMapper());
  }

  /**
	 * Marshal the content tree rooted at <tt>jaxbElement</tt> into an output
	 * stream
	 * 
	 * @param os
	 *            XML will be added to this stream.
	 * @param namespacePrefixMapper
	 *            namespacePrefixMapper
	 * 
	 * @throws JAXBException
	 *             If any unexpected problem occurs during the marshalling.
	 */
  public void marshal(java.io.OutputStream os, Object namespacePrefixMapper) throws JAXBException {
    try {
      Marshaller marshaller = jc.createMarshaller();
      if (Docx4jProperties.getProperty("docx4j.jaxb.formatted.output", true)) {
        marshaller.setProperty("jaxb.formatted.output", true);
      }
      NamespacePrefixMapperUtils.setProperty(marshaller, namespacePrefixMapper);
      log.debug("marshalling " + this.getClass().getName());
      getContents();
      setMceIgnorable((McIgnorableNamespaceDeclarator) namespacePrefixMapper);
      if (Docx4jProperties.getProperty("docx4j.jaxb.marshal.canonicalize", false)) {
        Document doc = XmlUtils.marshaltoW3CDomDocument(jaxbElement, jc);
        NamespacePrefixMapperUtils.declareNamespaces(this.getMceIgnorable(), doc);
        log.warn("Input to Canonicalizer: " + XmlUtils.w3CDomNodeToString(doc));
        Init.init();
        Canonicalizer c = Canonicalizer.getInstance(CanonicalizationMethod.EXCLUSIVE);
        log.debug("canonicalizeSubtree with inclusiveNamespaces " + this.getMceIgnorable());
        byte[] bytes = c.canonicalizeSubtree(doc, this.getMceIgnorable());
        IOUtils.write(bytes, os);
      } else {
        marshaller.marshal(jaxbElement, os);
      }
      ((McIgnorableNamespaceDeclarator) namespacePrefixMapper).setMcIgnorable(null);
    } catch (Docx4JException e) {
      log.error(e.getMessage(), e);
      throw new JAXBException(e);
    } catch (JAXBException e) {
      log.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new JAXBException(e);
    }
  }

  protected String getMceIgnorable() {
    return null;
  }

  /**
     * Where the mc:Ignorable attribute is present,
     * ensure its contents matches the ignorable namespaces
     * actually present.
     */
  protected void setMceIgnorable(McIgnorableNamespaceDeclarator namespacePrefixMapper) {
  }

  /**
	 * Unmarshal XML data from the specified InputStream and return the
	 * resulting content tree. Validation event location information may be
	 * incomplete when using this form of the unmarshal API.
	 * 
	 * <p>
	 * Implements <a href="#unmarshalGlobal">Unmarshal Global Root Element</a>.
	 * 
	 * @param is
	 *            the InputStream to unmarshal XML data from
	 * @return the newly created root object of the java content tree
	 * 
	 * @throws JAXBException
	 *             If any unexpected errors occur while unmarshalling
	 */
  public E unmarshal(java.io.InputStream is) throws JAXBException {
    try {
      XMLInputFactory xif = XMLInputFactory.newInstance();
      xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
      xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
      XMLStreamReader xsr = xif.createXMLStreamReader(is);
      Unmarshaller u = jc.createUnmarshaller();
      JaxbValidationEventHandler eventHandler = new JaxbValidationEventHandler();
      u.setEventHandler(eventHandler);
      try {
        jaxbElement = (E) XmlUtils.unwrap(u.unmarshal(xsr));
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
            jaxbElement = (E) XmlUtils.unwrap(result.getResult());
          } catch (Exception e) {
            throw new JAXBException("Preprocessing exception", e);
          }
        } else {
          log.error(ue.getMessage(), ue);
          log.error(".. and mark not supported");
          throw ue;
        }
      }
    } catch (XMLStreamException e1) {
      log.error(e1.getMessage(), e1);
      throw new JAXBException(e1);
    }
    return jaxbElement;
  }

  public E unmarshal(org.w3c.dom.Element el) throws JAXBException {
    try {
      Unmarshaller u = jc.createUnmarshaller();
      JaxbValidationEventHandler eventHandler = new JaxbValidationEventHandler();
      eventHandler.setContinue(false);
      u.setEventHandler(eventHandler);
      try {
        jaxbElement = (E) XmlUtils.unwrap(u.unmarshal(el));
      } catch (UnmarshalException ue) {
        log.info("encountered unexpected content; pre-processing");
        try {
          org.w3c.dom.Document doc;
          if (el instanceof org.w3c.dom.Document) {
            doc = (org.w3c.dom.Document) el;
          } else {
            doc = el.getOwnerDocument();
          }
          eventHandler.setContinue(true);
          JAXBResult result = XmlUtils.prepareJAXBResult(jc);
          Templates mcPreprocessorXslt = JaxbValidationEventHandler.getMcPreprocessor();
          XmlUtils.transform(doc, mcPreprocessorXslt, null, result);
          jaxbElement = (E) XmlUtils.unwrap(result.getResult());
        } catch (Exception e) {
          throw new JAXBException("Preprocessing exception", e);
        }
      }
      return jaxbElement;
    } catch (JAXBException e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  public boolean isContentEqual(Part other) throws Docx4JException {
    log.debug("Comparing " + getPartName().getName() + " : " + other.getPartName().getName());
    if (!(other instanceof JaxbXmlPart)) {
      log.debug(other.getPartName().getName() + " is not a JaxbXmlPart");
      return false;
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
    try {
      marshal(baos);
      ((JaxbXmlPart) other).marshal(baos2);
    } catch (JAXBException e) {
      throw new Docx4JException("Error marshalling parts", e);
    }
    return java.util.Arrays.equals(baos.toByteArray(), baos2.toByteArray());
  }
}