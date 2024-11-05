package com.fasterxml.jackson.core.json;
import com.fasterxml.jackson.core.*;

public class TestParserSymbols extends com.fasterxml.jackson.core.BaseTest {
  public void testSymbolsWithNullBytes() throws Exception {
    JsonFactory f = new JsonFactory();
    _testSymbolsWithNull(f, true);
    _testSymbolsWithNull(f, true);
  }

  public void testSymbolsWithNullChars() throws Exception {
    JsonFactory f = new JsonFactory();
    _testSymbolsWithNull(f, false);
    _testSymbolsWithNull(f, false);
  }

  private void _testSymbolsWithNull(JsonFactory f, boolean useBytes) throws Exception {
    final String INPUT = "{\"\\u0000abc\" : 1, \"abc\":2}";
    JsonParser parser = useBytes ? f.createParser(INPUT.getBytes("UTF-8")) : f.createParser(INPUT);
    assertToken(JsonToken.START_OBJECT, parser.nextToken());
    assertToken(JsonToken.FIELD_NAME, parser.nextToken());
    String currName = parser.getCurrentName();
    if (!"\u0000abc".equals(currName)) {
      fail("Expected \\0abc (4 bytes), \'" + currName + "\' (" + currName.length() + ")");
    }
    assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
    assertEquals(1, parser.getIntValue());
    assertToken(JsonToken.FIELD_NAME, parser.nextToken());
    currName = parser.getCurrentName();
    if (!"abc".equals(currName)) {
      fail("Expected \'abc\' (3 bytes), \'" + currName + "\' (" + currName.length() + ")");
    }
    assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
    assertEquals(2, parser.getIntValue());
    assertToken(JsonToken.END_OBJECT, parser.nextToken());
    parser.close();
  }
}