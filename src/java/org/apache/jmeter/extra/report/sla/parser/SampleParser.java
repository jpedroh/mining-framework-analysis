package org.apache.jmeter.extra.report.sla.parser;
import org.apache.jmeter.extra.report.sla.JMeterReportModel;
import org.apache.jmeter.extra.report.sla.element.SampleElement;
import org.apache.jmeter.extra.report.sla.stax.ComponentParser;
import javax.xml.stream.XMLStreamReader;
import java.util.Date;
import java.util.Properties;
import java.util.Stack;

/**
 * Parses a "sample" element (including httpSample).
 */
public class SampleParser extends AbstractModelParser implements ComponentParser {
  private final int LABEL_LENGTH = 70;

  private final int RESPONSE_MESSAGE_LENGTH = 120;

  public SampleParser(JMeterReportModel model) {
    super(model);
  }

  /**
     * <httpSample t="4" lt="4" ts="1301400114405" s="true" lb="Reporting.EnumGroupReport" rc="200" rm="OK" tn="CRM 1-2" dt="text" by="2469"/>
     *
     * @param streamReader the Stax stream reader
     * @param elementStack the current element stack
     */
  public Object startElement(XMLStreamReader streamReader, Stack<Object> elementStack) {
    final Properties attributes = new Properties();
    for (int i = 0; i < streamReader.getAttributeCount(); i++) {
      attributes.put(streamReader.getAttributeName(i).getLocalPart(), streamReader.getAttributeValue(i));
    }
    final int duration = Integer.parseInt(attributes.getProperty("t"));
    final Date timestamp = new Date(Long.parseLong(attributes.getProperty("ts")));
    final String label = trim(attributes.getProperty("lb", ""), LABEL_LENGTH);
    final String resultCode = attributes.getProperty("rc");
    final String responseMessage = trim(attributes.getProperty("rm"), RESPONSE_MESSAGE_LENGTH);
    final boolean success = Boolean.valueOf(attributes.getProperty("s"));
    final SampleElement sampleElement = new SampleElement();
    sampleElement.setDuration(duration);
    sampleElement.setTimestamp(timestamp);
    sampleElement.setLabel(label);
    sampleElement.setResultCode(resultCode);
    sampleElement.setResponseMessage(responseMessage);
    sampleElement.setSuccess(success);
    return sampleElement;
  }

  public void endElement(XMLStreamReader streamReader, Stack<Object> elementStack) {
    final SampleElement sampleElement = (SampleElement) elementStack.peek();
    addElement(sampleElement);
  }

  private String trim(String input, int maxLength) {
    if (input == null || input.length() <= maxLength) {
      return input;
    }
    return input.substring(0, maxLength - 2) + "..";
  }
}