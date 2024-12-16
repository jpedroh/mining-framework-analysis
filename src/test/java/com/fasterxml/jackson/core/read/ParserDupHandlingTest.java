package com.fasterxml.jackson.core.read;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.JsonFactory;


public class ParserDupHandlingTest extends BaseTest {
    private final String[] DUP_DOCS = new String[]{ "{ 'a':1, 'a':2 }", "[{ 'a':1, 'a':2 }]", "{ 'a':1, 'b':2, 'c':3,'a':true,'e':false }", "{ 'foo': { 'bar': [ [ { 'x':3, 'a':1 } ]], 'x':0, 'a':'y', 'b':3,'a':13 } }", "[{\'b\':1},{\'b\":3},[{\'a\':3}], {\'a\':1,\'a\':2}]", "{'b':1,'array':[{'b':3}],'ob':{'b':4,'x':0,'y':3,'a':true,'a':false }}" };

    {
        for (int i = 0; i < DUP_DOCS.length; ++i) {
            DUP_DOCS[i] = aposToQuotes(DUP_DOCS[i]);
        }
    }

    public void testSimpleDupCheckDisabled() throws Exception {
        // first: verify no problems if detection NOT enabled
        final JsonFactory f = new JsonFactory();
        assertFalse(f.isEnabled(StreamReadFeature.STRICT_DUPLICATE_DETECTION));
        for (String doc : DUP_DOCS) {
            _testSimpleDupsOk(doc, f, MODE_INPUT_STREAM);
            _testSimpleDupsOk(doc, f, MODE_INPUT_STREAM_THROTTLED);
            _testSimpleDupsOk(doc, f, MODE_READER);
            _testSimpleDupsOk(doc, f, MODE_DATA_INPUT);
        }
    }

    public void testSimpleDupsBytes() throws Exception {
<<<<<<< LEFT
        JsonFactory dupF = JsonFactory.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
=======
        JsonFactory nonDupF = new JsonFactory();
        JsonFactory dupF = JsonFactory.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .build();
>>>>>>> RIGHT
        for (String doc : DUP_DOCS) {
            _testSimpleDupsFail(doc, dupF, MODE_INPUT_STREAM, "a");

            _testSimpleDupsFail(doc, dupF, MODE_INPUT_STREAM_THROTTLED, "a");
        }
    }

    public void testSimpleDupsDataInput() throws Exception {
<<<<<<< LEFT
        JsonFactory dupF = JsonFactory.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
=======
        JsonFactory nonDupF = new JsonFactory();
        JsonFactory dupF = JsonFactory.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .build();
>>>>>>> RIGHT
        for (String doc : DUP_DOCS) {
            _testSimpleDupsFail(doc, dupF, MODE_DATA_INPUT, "a");
        }
    }

    public void testSimpleDupsChars() throws Exception {
        JsonFactory dupF = JsonFactory.builder().enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
        for (String doc : DUP_DOCS) {
            _testSimpleDupsFail(doc, dupF, MODE_READER, "a");
        }
    }

    private void _testSimpleDupsOk(final String doc, JsonFactory f,
            int mode) throws Exception
    {
        JsonParser p = createParser(f, mode, doc);
        JsonToken t = p.nextToken();
        assertNotNull(t);
        assertTrue(t.isStructStart());

        int depth = 1;

        while (depth > 0) {
            switch (p.nextToken()) {
            case START_ARRAY:
            case START_OBJECT:
                ++depth;
                break;
            case END_ARRAY:
            case END_OBJECT:
                --depth;
                break;
            default:
            }
        }
        if (mode != MODE_DATA_INPUT) {
            assertNull(p.nextToken());
        }
        p.close();
    }

    private void _testSimpleDupsFail(final String doc, JsonFactory f, int mode, String name) throws Exception {
        JsonParser p = createParser(f, mode, doc);
        JsonToken t = p.nextToken();
        assertNotNull(t);
        assertTrue(t.isStructStart());
        int depth = 1;
        JsonParseException e = null;
        while (depth > 0) {
            try {
                switch (p.nextToken()) {
                    case START_ARRAY :
                    case START_OBJECT :
                        ++depth;
                        break;
                    case END_ARRAY :
                    case END_OBJECT :
                        --depth;
                        break;
                    default :
                }
            } catch (JsonParseException e0) {
                e = e0;
                break;
            }
        } 
        p.close();
        if (e == null) {
            fail("Should have caught exception for dup");
        }
        verifyException(e, ("duplicate field '" + name) + "'");
    }
}