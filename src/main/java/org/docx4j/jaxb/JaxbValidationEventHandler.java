package org.docx4j.jaxb;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.docx4j.XmlUtils;
import org.docx4j.utils.ResourceUtils;

public class JaxbValidationEventHandler implements ValidationEventHandler {
  private static Logger log = LoggerFactory.getLogger(JaxbValidationEventHandler.class);

  private boolean shouldContinue = false;

  public void setContinue(boolean val) {
    shouldContinue = val;
  }

  public final static String UNEXPECTED_MC_ALTERNATE_CONTENT = "unexpected element (uri:\"http://schemas.openxmlformats.org/markup-compatibility/2006\", local:\"AlternateContent\")";

  static Templates mcPreprocessorXslt;

  public static Templates getMcPreprocessor() throws IOException, TransformerConfigurationException {
    if (mcPreprocessorXslt == null) {
      Source xsltSource = new StreamSource(ResourceUtils.getResourceViaProperty("docx4j.jaxb.JaxbValidationEventHandler", "org/docx4j/jaxb/mc-preprocessor.xslt"));
      mcPreprocessorXslt = XmlUtils.getTransformerTemplate(xsltSource);
    }
    return mcPreprocessorXslt;
  }

  public boolean handleEvent(ValidationEvent ve) {
    if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
      ValidationEventLocator locator = ve.getLocator();
      if (log.isDebugEnabled() || ve.getMessage().length() < 120) {
        log.warn(printSeverity(ve) + ": " + ve.getMessage());
      } else {
        log.warn(printSeverity(ve) + ": " + ve.getMessage().substring(0, 120));
      }
      if (ve.getLinkedException() != null && log.isDebugEnabled()) {
        ve.getLinkedException().printStackTrace();
      }
      if (locator.getColumnNumber() > -1) {
        log.warn("Column is " + locator.getColumnNumber() + " at line number " + locator.getLineNumber());
      }
      if (locator.getNode() != null) {
        Node node = locator.getNode();
        log.warn("troublesome node: " + XmlUtils.w3CDomNodeToString(node));
        if (node.getParentNode() != null) {
          log.warn("in parent node: " + XmlUtils.w3CDomNodeToString(node.getParentNode()));
          Node parent = node.getParentNode();
          String path = "";
          while (parent != null) {
            path = getLocalName(parent) + "/" + path;
            parent = parent.getParentNode();
          }
          log.warn(path + getLocalName(node));
        }
      }
      if (locator.getOffset() > -1) {
        log.warn("At offset " + locator.getOffset());
      }
      if (locator.getObject() != null) {
        log.warn(locator.getObject().getClass().getName());
      }
    } else {
      if (ve.getSeverity() == ve.WARNING) {
        log.warn(printSeverity(ve) + "Message is " + ve.getMessage());
        if (ve.getMessage().startsWith("Errors limit exceeded")) {
          try {
            log.warn("Resetting error counter to work around https://github.com/gf-metro/jaxb/issues/22");
            Field field = null;
            if (Context.getJaxbImplementation() == JAXBImplementation.ORACLE_JRE) {
              field = Class.forName("com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext").getDeclaredField("errorsCounter");
            } else {
              if (Context.getJaxbImplementation() == JAXBImplementation.IBM_WEBSPHERE_XLXP) {
                log.warn("with IBM unmarshaller");
                field = Class.forName("com.ibm.jtc.jax.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext").getDeclaredField("errorsCounter");
              } else {
                try {
                  field = Class.forName("com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext").getDeclaredField("errorsCounter");
                } catch (Exception e) {
                  log.error("Trying to reset error counter, but not using JAXB RI:- ");
                  log.error(e.getMessage());
                }
              }
            }
            if (field == null) {
              log.error("Unable to reset error counter. See https://github.com/plutext/docx4j/issues/164");
            } else {
              field.setAccessible(true);
              field.set(null, 10);
              log.warn(".. reset successful");
            }
          } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Unable to reset error counter. See https://github.com/plutext/docx4j/issues/164");
          }
        }
      }
    }
    if (shouldContinue) {
      log.info("continuing (with possible element/attribute loss)");
    } else {
      if (log.isDebugEnabled()) {
        log.debug("shouldContinue is set to false", new Throwable());
      } else {
        log.info("shouldContinue is set to false");
      }
    }
    return shouldContinue;
  }

  private String getLocalName(Node sourceNode) {
    if (sourceNode.getLocalName() == null) {
      return sourceNode.getNodeName();
    } else {
      return sourceNode.getLocalName();
    }
  }

  public String printSeverity(ValidationEvent ve) {
    String errorLevel;
    switch (ve.getSeverity()) {
      case ValidationEvent.FATAL_ERROR:
      {
        errorLevel = "(non)FATAL_ERROR";
        break;
      }
      case ValidationEvent.ERROR:
      {
        errorLevel = "ERROR";
        break;
      }
      case ValidationEvent.WARNING:
      {
        errorLevel = "WARNING";
        break;
      }
      default:
      errorLevel = new Integer(ve.getSeverity()).toString();
    }
    return "[" + errorLevel + "] ";
  }
}