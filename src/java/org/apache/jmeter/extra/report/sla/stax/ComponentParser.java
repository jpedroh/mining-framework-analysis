package org.apache.jmeter.extra.report.sla.stax;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Stack;

/**
 * Helper interface to simplify Stax parsing.
 */
public interface ComponentParser {
  Object startElement(XMLStreamReader staxXmlReader, Stack<Object> elementStack) throws XMLStreamException;

  void endElement(XMLStreamReader staxXmlReader, Stack<Object> elementStack) throws XMLStreamException;
}