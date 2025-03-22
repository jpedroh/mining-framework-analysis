package org.docx4j.jaxb;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class NamespacePrefixMapperUtils {
  private static Logger log = LoggerFactory.getLogger(NamespacePrefixMapperUtils.class);

  private static JAXBContext testContext;

  private static Object prefixMapper;

  private static Object prefixMapperRels;

  private static boolean haveTried = false;

  public static synchronized Object getPrefixMapper() throws JAXBException {
    if (prefixMapper != null) {
      return prefixMapper;
    }
    if (haveTried) {
      return null;
    }
    haveTried = true;
    if (testContext == null) {
      java.lang.ClassLoader classLoader = NamespacePrefixMapperUtils.class.getClassLoader();
      testContext = JAXBContext.newInstance("org.docx4j.relationships", classLoader);
    }
    if (testContext == null) {
      throw new JAXBException("Couldn\'t create context for org.docx4j.relationships.  Everything is broken!");
    }
    if (testContext.getClass().getName().equals("org.eclipse.persistence.jaxb.JAXBContext")) {
      log.info("Using MOXy NamespacePrefixMapper");
      try {
        Class c = Class.forName("org.docx4j.jaxb.moxy.NamespacePrefixMapper");
        prefixMapper = c.newInstance();
        return prefixMapper;
      } catch (Exception e) {
        throw new JAXBException("Can\'t create org.docx4j.jaxb.moxy.NamespacePrefixMapper", e);
      }
    }
    if (testContext.getClass().getName().equals("com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl")) {
      log.info("Using com.sun.xml.internal NamespacePrefixMapper");
      try {
        Class c = Class.forName("org.docx4j.jaxb.suninternal.NamespacePrefixMapper");
        prefixMapper = c.newInstance();
        return prefixMapper;
      } catch (Exception e) {
        throw new JAXBException("Can\'t create internal NamespacePrefixMapper", e);
      }
    }
    Marshaller m = testContext.createMarshaller();
    return tryUsingRI(m);
  }

  private static Object tryUsingRI(Marshaller m) throws JAXBException {
    try {
      Class c = Class.forName("org.docx4j.jaxb.ri.NamespacePrefixMapper");
      prefixMapper = c.newInstance();
      m.setProperty("com.sun.xml.bind.namespacePrefixMapper", prefixMapper);
      log.info("Using ri.NamespacePrefixMapper, which is suitable for the JAXB RI");
      return prefixMapper;
    } catch (Exception e) {
      throw new JAXBException("JAXB: Can\'t instantiate JAXB Reference Implementation", e);
    }
  }

  public static synchronized Object getPrefixMapperRelationshipsPart() throws JAXBException {
    if (prefixMapperRels != null) {
      return prefixMapperRels;
    }
    if (testContext == null) {
      java.lang.ClassLoader classLoader = NamespacePrefixMapperUtils.class.getClassLoader();
      testContext = JAXBContext.newInstance("org.docx4j.relationships", classLoader);
    }
    if (testContext == null) {
      throw new JAXBException("Couldn\'t create context for org.docx4j.relationships.  Everything is broken!");
    }
    if (testContext.getClass().getName().equals("org.eclipse.persistence.jaxb.JAXBContext")) {
      log.info("Using MOXy NamespacePrefixMapper");
      try {
        Class c = Class.forName("org.docx4j.jaxb.moxy.NamespacePrefixMapperRelationshipsPart");
        prefixMapperRels = c.newInstance();
        return prefixMapperRels;
      } catch (Exception e) {
        throw new JAXBException("Can\'t create org.docx4j.jaxb.moxy.NamespacePrefixMapper", e);
      }
    }
    if (testContext.getClass().getName().equals("com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl")) {
      log.info("Using com.sun.xml.internal NamespacePrefixMapper");
      try {
        Class c = Class.forName("org.docx4j.jaxb.suninternal.NamespacePrefixMapperRelationshipsPart");
        prefixMapperRels = c.newInstance();
        return prefixMapperRels;
      } catch (Exception e) {
        throw new JAXBException("Can\'t create internal NamespacePrefixMapperRelationshipsPart", e);
      }
    }
    Marshaller m = testContext.createMarshaller();
    return tryRIforRelationshipsPart(m);
  }

  private static Object tryRIforRelationshipsPart(Marshaller m) throws JAXBException {
    try {
      Class c = Class.forName("org.docx4j.jaxb.ri.NamespacePrefixMapperRelationshipsPart");
      prefixMapperRels = c.newInstance();
      m.setProperty("com.sun.xml.bind.namespacePrefixMapper", prefixMapperRels);
      log.info("Using ri.NamespacePrefixMapperRelationshipsPart, which is suitable for the JAXB RI");
      return prefixMapperRels;
    } catch (Exception e) {
      throw new JAXBException("JAXB: Can\'t instantiate JAXB Reference Implementation", e);
    }
  }

  /**
	 * setProperty on 'com.sun.xml.bind.namespacePrefixMapper' or
	 * 'com.sun.xml.internal.bind.namespacePrefixMapper', as appropriate,
	 * depending on whether JAXB reference implementation, or Java 6 
	 * implementation is being used.
	 * 
	 * @param marshaller
	 * @param namespacePrefixMapper
	 * @throws JAXBException
	 */
  public static void setProperty(Marshaller marshaller, Object namespacePrefixMapper) throws JAXBException {
    if (namespacePrefixMapper == null) {
      throw new JAXBException("namespacePrefixMapper is null");
    }
    log.debug("attempting to setProperty on marshaller " + marshaller.getClass().getName());
    try {
      if (Context.getJaxbImplementation() == JAXBImplementation.ECLIPSELINK_MOXy) {
        marshaller.setProperty("eclipselink.namespace-prefix-mapper", namespacePrefixMapper);
        log.debug("setProperty: eclipselink.namespace-prefix-mapper");
      } else {
        if (Context.getJaxbImplementation() == JAXBImplementation.REFERENCE || Context.getJaxbImplementation() == JAXBImplementation.IBM_WEBSPHERE_XLXP) {
          marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", namespacePrefixMapper);
          log.debug("setProperty: com.sun.xml.bind.namespacePrefixMapper");
        } else {
          if (Context.getJaxbImplementation() == JAXBImplementation.ORACLE_JRE) {
            log.debug("attempting to setProperty: com.sun.xml.INTERNAL.bind.namespacePrefixMapper");
            marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", namespacePrefixMapper);
          } else {
            log.warn("Which NamespacePrefixMapper to use?");
          }
        }
      }
    } catch (javax.xml.bind.PropertyException e) {
      log.error("Couldn\'t setProperty on marshaller " + marshaller.getClass().getName());
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  public static String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) throws JAXBException {
    NamespacePrefixMapperInterface namespacePrefixMapper = (NamespacePrefixMapperInterface) getPrefixMapper();
    return namespacePrefixMapper.getPreferredPrefix(namespaceUri, suggestion, requirePrefix);
  }

  private static final String[] EMPTY_STRING = new String[0];

  public static String[] getPreDeclaredNamespaceUris(String mcIgnorable) {
    if (mcIgnorable == null) {
      return EMPTY_STRING;
    }
    Set<String> entries = new HashSet<String>();
    StringTokenizer st = new StringTokenizer(mcIgnorable, " ");
    while (st.hasMoreTokens()) {
      String prefix = (String) st.nextToken();
      String uri = NamespacePrefixMappings.getNamespaceURIStatic(prefix);
      if (uri == null || uri.contentEquals(XMLConstants.NULL_NS_URI)) {
        log.warn("No mapping for prefix \'" + prefix + "\'");
      } else {
        entries.add(uri);
      }
    }
    return entries.toArray(new String[entries.size()]);
  }

  public static Map<String, String> getPreDeclaredNamespaceMap(String mcIgnorable) {
    Map<String, String> entries = new HashMap<String, String>();
    if (mcIgnorable == null) {
      return entries;
    }
    StringTokenizer st = new StringTokenizer(mcIgnorable, " ");
    while (st.hasMoreTokens()) {
      String prefix = (String) st.nextToken();
      String uri = NamespacePrefixMappings.getNamespaceURIStatic(prefix);
      if (uri == null || uri.contentEquals(XMLConstants.NULL_NS_URI)) {
        log.warn("No mapping for prefix \'" + prefix + "\'");
      } else {
        entries.put(prefix, uri);
      }
    }
    return entries;
  }

  /**
	 * Word requires all mcIgnorable prefixes to be declared at the document level.
	 * 
	 * @param mcIgnorable
	 * @param doc
	 */
  public static void declareNamespaces(String mcIgnorable, Document doc) {
    if (mcIgnorable == null) {
      return;
    }
    StringTokenizer st = new StringTokenizer(mcIgnorable, " ");
    while (st.hasMoreTokens()) {
      String prefix = (String) st.nextToken();
      String uri = NamespacePrefixMappings.getNamespaceURIStatic(prefix);
      if (uri == null) {
        log.warn("No mapping for prefix \'" + prefix + "\'");
      } else {
        doc.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
      }
    }
  }
}