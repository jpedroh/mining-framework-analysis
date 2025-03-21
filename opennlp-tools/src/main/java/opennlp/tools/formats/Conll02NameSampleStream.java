package opennlp.tools.formats;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import opennlp.common.util.Span;
import opennlp.common.util.StringUtil;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**
 * Parser for the dutch and spanish ner training files of the CONLL 2002 shared task.
 * <p>
 * The dutch data has a -DOCSTART- tag to mark article boundaries,
 * adaptive data in the feature generators will be cleared before every article.<br>
 * The spanish data does not contain article boundaries,
 * adaptive data will be cleared for every sentence.
 * <p>
 * The data contains four named entity types: Person, Organization, Location and Misc.<br>
 * <p>
 * Data can be found on this web site:<br>
 * http://www.cnts.ua.ac.be/conll2002/ner/
 * <p>
 * <b>Note:</b> Do not use this class, internal use only!
 */
public class Conll02NameSampleStream implements ObjectStream<NameSample> {
  public static final int GENERATE_PERSON_ENTITIES = 0x01;

  public enum LANGUAGE {
    NLD,
    SPA
  }

  public static final int GENERATE_ORGANIZATION_ENTITIES = 0x01 << 1;

  public static final int GENERATE_LOCATION_ENTITIES = 0x01 << 2;

  public static final int GENERATE_MISC_ENTITIES = 0x01 << 3;

  public static final String DOCSTART = "-DOCSTART-";

  private final LANGUAGE lang;

  private final ObjectStream<String> lineStream;

  private final int types;

  public Conll02NameSampleStream(LANGUAGE lang, ObjectStream<String> lineStream, int types) {
    this.lang = lang;
    this.lineStream = lineStream;
    this.types = types;
  }

  public Conll02NameSampleStream(LANGUAGE lang, InputStreamFactory in, int types) throws IOException {
    this.lang = lang;
    try {
      this.lineStream = new PlainTextByLineStream(in, StandardCharsets.UTF_8);
      System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
    this.types = types;
  }

  static Span extract(int begin, int end, String beginTag) throws InvalidFormatException {
    String type = beginTag.substring(2);
    switch (type) {
      case "PER":
      type = "person";
      break;
      case "LOC":
      type = "location";
      break;
      case "MISC":
      type = "misc";
      break;
      case "ORG":
      type = "organization";
      break;
      default:
      throw new InvalidFormatException("Unknown type: " + type);
    }
    return new Span(begin, end, type);
  }

  public NameSample read() throws IOException {
    List<String> sentence = new ArrayList<>();
    List<String> tags = new ArrayList<>();
    boolean isClearAdaptiveData = false;
    String line;
    while ((line = lineStream.read()) != null && !StringUtil.isEmpty(line)) {
      if (LANGUAGE.NLD.equals(lang) && line.startsWith(DOCSTART)) {
        isClearAdaptiveData = true;
        continue;
      }
      String[] fields = line.split(" ");
      if (fields.length == 3) {
        sentence.add(fields[0]);
        tags.add(fields[2]);
      } else {
        throw new IOException("Expected three fields per line in training data, got " + fields.length + " for line \'" + line + "\'!");
      }
    }
    if (LANGUAGE.SPA.equals(lang)) {
      isClearAdaptiveData = true;
    }
    if (sentence.size() > 0) {
      List<Span> names = new ArrayList<>();
      int beginIndex = -1;
      int endIndex = -1;
      for (int i = 0; i < tags.size(); i++) {
        String tag = tags.get(i);
        if (tag.endsWith("PER") && (types & GENERATE_PERSON_ENTITIES) == 0) {
          tag = "O";
        }
        if (tag.endsWith("ORG") && (types & GENERATE_ORGANIZATION_ENTITIES) == 0) {
          tag = "O";
        }
        if (tag.endsWith("LOC") && (types & GENERATE_LOCATION_ENTITIES) == 0) {
          tag = "O";
        }
        if (tag.endsWith("MISC") && (types & GENERATE_MISC_ENTITIES) == 0) {
          tag = "O";
        }
        if (tag.startsWith("B-")) {
          if (beginIndex != -1) {
            names.add(extract(beginIndex, endIndex, tags.get(beginIndex)));
            beginIndex = -1;
            endIndex = -1;
          }
          beginIndex = i;
          endIndex = i + 1;
        } else {
          if (tag.startsWith("I-")) {
            endIndex++;
          } else {
            if (tag.equals("O")) {
              if (beginIndex != -1) {
                names.add(extract(beginIndex, endIndex, tags.get(beginIndex)));
                beginIndex = -1;
                endIndex = -1;
              }
            } else {
              throw new IOException("Invalid tag: " + tag);
            }
          }
        }
      }
      if (beginIndex != -1) {
        names.add(extract(beginIndex, endIndex, tags.get(beginIndex)));
      }
      return new NameSample(sentence.toArray(new String[sentence.size()]), names.toArray(new Span[names.size()]), isClearAdaptiveData);
    } else {
      if (line != null) {
        return read();
      } else {
        return null;
      }
    }
  }

  public void reset() throws IOException, UnsupportedOperationException {
    lineStream.reset();
  }

  public void close() throws IOException {
    lineStream.close();
  }
}