package com.fasterxml.jackson.dataformat.xml.stream;
import java.io.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlTestBase;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

public class XmlParserTest extends XmlTestBase {
  protected JsonFactory _jsonFactory;

  protected XmlFactory _xmlFactory;

  protected XmlMapper _xmlMapper;

  @Override public void setUp() throws Exception {
    super.setUp();
    _jsonFactory = new JsonFactory();
    _xmlFactory = new XmlFactory();
    _xmlMapper = new XmlMapper();
  }

  public void testSimplest() throws Exception {
    assertEquals("{\"leaf\":\"abc\"}", _readXmlWriteJson("<root><leaf>abc</leaf></root>"));
  }

  public void testSimpleWithEmpty() throws Exception {
    assertEquals("{\"leaf\":null}", _readXmlWriteJson("<root><leaf /></root>"));
  }

  public void testSimpleNested() throws Exception {
    assertEquals("{\"a\":{\"b\":{\"c\":\"xyz\"}}}", _readXmlWriteJson("<root><a><b><c>xyz</c></b></a></root>"));
  }

  /**
     * Unit test that verifies that we can write sample document from JSON
     * specification as XML, and read it back in "as JSON", with
     * expected transformation.
     */
  public void testRoundTripWithSample() throws Exception {
    JsonNode root = new ObjectMapper().readTree(SAMPLE_DOC_JSON_SPEC);
    String xml = _xmlMapper.writeValueAsString(root);
    JsonParser p = _xmlMapper.getFactory().createParser(xml);
    assertToken(JsonToken.START_OBJECT, p.nextToken());
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Image");
    assertToken(JsonToken.START_OBJECT, p.nextToken());
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Width");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(String.valueOf(SAMPLE_SPEC_VALUE_WIDTH), p.getText());
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Height");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(String.valueOf(SAMPLE_SPEC_VALUE_HEIGHT), p.getText());
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Title");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(SAMPLE_SPEC_VALUE_TITLE, getAndVerifyText(p));
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Thumbnail");
    assertToken(JsonToken.START_OBJECT, p.nextToken());
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Url");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(SAMPLE_SPEC_VALUE_TN_URL, getAndVerifyText(p));
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Height");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(String.valueOf(SAMPLE_SPEC_VALUE_TN_HEIGHT), p.getText());
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "Width");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(SAMPLE_SPEC_VALUE_TN_WIDTH, getAndVerifyText(p));
    assertToken(JsonToken.END_OBJECT, p.nextToken());
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "IDs");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(String.valueOf(SAMPLE_SPEC_VALUE_TN_ID1), getAndVerifyText(p));
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "IDs");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(String.valueOf(SAMPLE_SPEC_VALUE_TN_ID2), getAndVerifyText(p));
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "IDs");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(String.valueOf(SAMPLE_SPEC_VALUE_TN_ID3), getAndVerifyText(p));
    assertToken(JsonToken.FIELD_NAME, p.nextToken());
    verifyFieldName(p, "IDs");
    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals(String.valueOf(SAMPLE_SPEC_VALUE_TN_ID4), getAndVerifyText(p));
    assertToken(JsonToken.END_OBJECT, p.nextToken());
    assertToken(JsonToken.END_OBJECT, p.nextToken());
    p.close();
  }

  /**
     * Test to ensure functionality used to force an element to be reported
     * as "JSON" Array, instead of default Object.
     */
  public void testForceElementAsArray() throws Exception {
    final String XML = "<array><elem>value</elem><elem><property>123</property></elem><elem>1</elem></array>";
    FromXmlParser xp = (FromXmlParser) _xmlFactory.createParser(new StringReader(XML));
    assertToken(JsonToken.START_OBJECT, xp.nextToken());
    assertToken(JsonToken.FIELD_NAME, xp.nextToken());
    assertEquals("elem", xp.getCurrentName());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertEquals("value", xp.getText());
    assertToken(JsonToken.FIELD_NAME, xp.nextToken());
    assertEquals("elem", xp.getCurrentName());
    assertToken(JsonToken.START_OBJECT, xp.nextToken());
    assertToken(JsonToken.FIELD_NAME, xp.nextToken());
    assertEquals("property", xp.getCurrentName());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertEquals("123", xp.getText());
    assertToken(JsonToken.END_OBJECT, xp.nextToken());
    assertToken(JsonToken.FIELD_NAME, xp.nextToken());
    assertEquals("elem", xp.getCurrentName());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertEquals("1", xp.getText());
    assertToken(JsonToken.END_OBJECT, xp.nextToken());
    xp.close();
    xp = (FromXmlParser) _xmlFactory.createParser(new StringReader(XML));
    assertTrue(xp.getParsingContext().inRoot());
    assertToken(JsonToken.START_OBJECT, xp.nextToken());
    assertTrue(xp.getParsingContext().inObject());
    assertTrue("Should \'convert\' START_OBJECT to START_ARRAY", xp.isExpectedStartArrayToken());
    assertToken(JsonToken.START_ARRAY, xp.getCurrentToken());
    assertTrue(xp.getParsingContext().inArray());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertTrue(xp.getParsingContext().inArray());
    assertEquals("value", xp.getText());
    assertToken(JsonToken.START_OBJECT, xp.nextToken());
    assertTrue(xp.getParsingContext().inObject());
    assertToken(JsonToken.FIELD_NAME, xp.nextToken());
    assertEquals("property", xp.getCurrentName());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertEquals("123", xp.getText());
    StringWriter w = new StringWriter();
    assertEquals(3, xp.getText(w));
    assertEquals("123", w.toString());
    assertTrue(xp.getParsingContext().inObject());
    assertToken(JsonToken.END_OBJECT, xp.nextToken());
    assertTrue(xp.getParsingContext().inArray());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertTrue(xp.getParsingContext().inArray());
    assertEquals("1", xp.getText());
    assertToken(JsonToken.END_ARRAY, xp.nextToken());
    assertTrue(xp.getParsingContext().inRoot());
    xp.close();
  }

  public void testXmlAttributes() throws Exception {
    final String XML = "<data max=\"7\" offset=\"9\"/>";
    FromXmlParser xp = (FromXmlParser) _xmlFactory.createParser(new StringReader(XML));
    assertToken(JsonToken.START_OBJECT, xp.nextToken());
    assertToken(JsonToken.FIELD_NAME, xp.nextToken());
    assertEquals("max", xp.getCurrentName());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertEquals("7", xp.getText());
    assertToken(JsonToken.FIELD_NAME, xp.nextToken());
    assertEquals("offset", xp.getCurrentName());
    StringWriter w = new StringWriter();
    assertEquals(6, xp.getText(w));
    assertEquals("offset", w.toString());
    assertToken(JsonToken.VALUE_STRING, xp.nextToken());
    assertEquals("9", xp.getText());
    w = new StringWriter();
    assertEquals(1, xp.getText(w));
    assertEquals("9", w.toString());
    assertToken(JsonToken.END_OBJECT, xp.nextToken());
    xp.close();
  }

  private String _readXmlWriteJson(String xml) throws IOException {
    StringWriter w = new StringWriter();
    JsonParser p = _xmlFactory.createParser(xml);
    JsonGenerator jg = _jsonFactory.createGenerator(w);
    while (p.nextToken() != null) {
      jg.copyCurrentEvent(p);
    }
    p.close();
    jg.close();
    return w.toString();
  }
}