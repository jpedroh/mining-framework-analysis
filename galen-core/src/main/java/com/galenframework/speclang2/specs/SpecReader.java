package com.galenframework.speclang2.specs;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.*;
import com.galenframework.parser.StringCharReader;
import java.util.HashMap;
import java.util.Map;

public class SpecReader {
  public static Map<String, SpecProcessor> specProcessors = initSpecProcessors();

  public SpecReader() {
    initSpecProcessors();
  }

  private static Map<String, SpecProcessor> initSpecProcessors() {
    return new HashMap<String, SpecProcessor>() {
      {
        put("inside", new SpecInsideProcessor());
        put("contains", new SpecContainsProcessor());
        put("near", new SpecNearProcessor());
        put("aligned", new SpecAlignedProcessor());
        put("absent", new SingleWordSpecProcessor() {
          @Override public Spec createSpec() {
            return new SpecAbsent();
          }
        });
        put("visible", new SingleWordSpecProcessor() {
          @Override public Spec createSpec() {
            return new SpecVisible();
          }
        });
        put("width", new SpecWithRangeProcessor() {
          @Override public Spec createSpec(Range range) {
            return new SpecWidth(range);
          }
        });
        put("height", new SpecWithRangeProcessor() {
          @Override public Spec createSpec(Range range) {
            return new SpecHeight(range);
          }
        });
        put("text", new SpecTextProcessor());
        put("css", new SpecCssProcessor());
        put("above", new SpecWithObjectAndRangeProcessor() {
          @Override public Spec createSpec(String objectName, Range range) {
            return new SpecAbove(objectName, range);
          }
        });
        put("below", new SpecWithObjectAndRangeProcessor() {
          @Override public Spec createSpec(String objectName, Range range) {
            return new SpecBelow(objectName, range);
          }
        });
        put("left-of", new SpecWithObjectAndRangeProcessor() {
          @Override public Spec createSpec(String objectName, Range range) {
            return new SpecLeftOf(objectName, range);
          }
        });
        put("right-of", new SpecWithObjectAndRangeProcessor() {
          @Override public Spec createSpec(String objectName, Range range) {
            return new SpecRightOf(objectName, range);
          }
        });
        put("centered", new SpecCenteredProcessor());
        put("on", new SpecOnProcessor());
        put("color-scheme", new SpecColorSchemeProcessor());
        put("image", new SpecImageProcessor());
        put("component", new SpecComponentProcessor());
        put("count", new SpecCountProcessor());
        put("ocr", new SpecOcrProcessor());
      }
    };
  }

  public Spec read(String specText, String contextPath) {
    if (specText == null) {
      throw new IllegalArgumentException("specText argument should not be null");
    }
    specText = specText.trim();
    if (specText.isEmpty()) {
      throw new IllegalArgumentException("specText should not be empty");
    }
    StringCharReader reader = new StringCharReader(specText);
    String firstWord = reader.readWord();
    SpecProcessor specProcessor = specProcessors.get(firstWord);
    if (specProcessor != null) {
      Spec spec = specProcessor.process(reader, contextPath);
      spec.setOriginalText(specText);
      if (reader.hasMoreNormalSymbols()) {
        throw new SyntaxException("Couldn\'t process: " + reader.getTheRest());
      }
      return spec;
    } else {
      throw new SyntaxException("Unknown spec: " + firstWord);
    }
  }

  public Spec read(String specText) {
    return read(specText, null);
  }
}