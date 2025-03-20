package org.cts.parser.prj;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for PRJ / WKT (OGC & ESRI) String.
 *
 * This very simple parser works in tree steps: 1. it parses the PRJ String and
 * produces an abstract tree, without any assumptions on it being a valid OGC
 * WKT String. 2. it walks the tree looking for the values needed for
 * transformation into a proj4 description string. 3. the proj4 description
 * string is passed to the {@link org.jproj.parser.Proj4Parser } that builds the
 * CRS.
 *
 * @author Antoine Gourlay, Erwan Bocher
 */
public class PrjParser {
  /**
     * Creates a new parser.
     *
     */
  public PrjParser() {
  }

  /**
     * Parses a WKT PRJ String into a set of parameters.
     *
     * This is the main entry point of the parser.
     *
     * @param prjString a WKT string
     * @return a list of parameters
     * @throws PrjParserException if the PRJ cannot be parsed into a CRS for any
     * reason
     */
  public Map<String, String> getParameters(String prjString) {
    CharBuffer s = CharBuffer.wrap(prjString);
    PrjElement e;
    try {
      e = parseNode(s);
    } catch (BufferUnderflowException ex) {
      throw new PrjParserException("Failed to read PRJ.", ex);
    }
    Map<String, String> prjParameters = PrjMatcher.match(e);
    return prjParameters;
  }

  public PrjElement parseNode(CharBuffer s) {
    boolean complexNode = false;
    int start = s.position();
    int ll = 0;
    while (s.hasRemaining()) {
      char c = s.get();
      if (c == '[') {
        complexNode = true;
        break;
      } else {
        if (c == ']' || c == ',') {
          break;
        } else {
          ll++;
        }
      }
    }
    s.position(start);
    String name = s.subSequence(0, ll).toString();
    s.position(start + ll + 1);
    if (complexNode) {
      return new PrjNodeElement(name, parseNodeChildren(s));
    } else {
      s.position(s.position() - 1);
      return new PrjStringElement(name);
    }
  }

  private char next(CharBuffer s) {
    char next;
    do {
      next = s.get();
    } while(Character.isWhitespace(next));
    return next;
  }

  private List<PrjElement> parseNodeChildren(CharBuffer s) {
    List<PrjElement> elms = new ArrayList<PrjElement>();
    boolean finished = false;
    do {
      char next = next(s);
      if (next == '\"') {
        elms.add(parseString(s));
      } else {
        s.position(s.position() - 1);
        if (Character.isDigit(next) || next == '-') {
          elms.add(parseNumber(s));
        } else {
          elms.add(parseNode(s));
        }
      }
      next = next(s);
      switch (next) {
        case ',':
        break;
        case ']':
        finished = true;
        break;
        default:
        throw new PrjParserException("weird character: " + next);
      }
    } while(!finished);
    return elms;
  }

  private PrjStringElement parseString(CharBuffer s) {
    int start = s.position();
    int ll = 0;
    while (s.hasRemaining()) {
      char c = s.get();
      if (c == '\"') {
        break;
      } else {
        ll++;
      }
    }
    s.position(start);
    String str = s.subSequence(0, ll).toString();
    s.position(start + ll + 1);
    return new PrjStringElement(str);
  }

  private PrjNumberElement parseNumber(CharBuffer s) {
    int start = s.position();
    int ll = 0;
    while (s.hasRemaining()) {
      char c = s.get();
      if (c == ',' || c == ']' || Character.isWhitespace(c)) {
        break;
      } else {
        ll++;
      }
    }
    s.position(start);
    String str = s.subSequence(0, ll).toString();
    s.position(start + ll);
    return new PrjNumberElement(Double.parseDouble(str));
  }
}