package opennlp.tools.namefind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import opennlp.common.util.Span;
import opennlp.tools.util.SequenceCodec;

public class BioCodec implements SequenceCodec<String> {
  public static final String START = "start";

  public static final String CONTINUE = "cont";

  public static final String OTHER = "other";

  private static final Pattern typedOutcomePattern = Pattern.compile("(.+)-\\w+");

  static String extractNameType(String outcome) {
    Matcher matcher = typedOutcomePattern.matcher(outcome);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }

  public Span[] decode(List<String> c) {
    int start = -1;
    int end = -1;
    List<Span> spans = new ArrayList<>(c.size());
    for (int li = 0; li < c.size(); li++) {
      String chunkTag = c.get(li);
      if (chunkTag.endsWith(BioCodec.START)) {
        if (start != -1) {
          spans.add(new Span(start, end, extractNameType(c.get(li - 1))));
        }
        start = li;
        end = li + 1;
      } else {
        if (chunkTag.endsWith(BioCodec.CONTINUE)) {
          end = li + 1;
        } else {
          if (chunkTag.endsWith(BioCodec.OTHER)) {
            if (start != -1) {
              spans.add(new Span(start, end, extractNameType(c.get(li - 1))));
              start = -1;
              end = -1;
            }
          }
        }
      }
    }
    if (start != -1) {
      spans.add(new Span(start, end, extractNameType(c.get(c.size() - 1))));
    }
    return spans.toArray(new Span[spans.size()]);
  }

  public String[] encode(Span[] names, int length) {
    String[] outcomes = new String[length];
    Arrays.fill(outcomes, BioCodec.OTHER);
    for (Span name : names) {
      if (name.getType() == null) {
        outcomes[name.getStart()] = "default" + "-" + BioCodec.START;
      } else {
        outcomes[name.getStart()] = name.getType() + "-" + BioCodec.START;
      }
      for (int i = name.getStart() + 1; i < name.getEnd(); i++) {
        if (name.getType() == null) {
          outcomes[i] = "default" + "-" + BioCodec.CONTINUE;
        } else {
          outcomes[i] = name.getType() + "-" + BioCodec.CONTINUE;
        }
      }
    }
    return outcomes;
  }

  public NameFinderSequenceValidator createSequenceValidator() {
    return new NameFinderSequenceValidator();
  }

  @Override public boolean areOutcomesCompatible(String[] outcomes) {
    List<String> start = new ArrayList<>();
    List<String> cont = new ArrayList<>();
    for (String outcome : outcomes) {
      if (outcome.endsWith(BioCodec.START)) {
        start.add(outcome.substring(0, outcome.length() - BioCodec.START.length()));
      } else {
        if (outcome.endsWith(BioCodec.CONTINUE)) {
          cont.add(outcome.substring(0, outcome.length() - BioCodec.CONTINUE.length()));
        } else {
          if (!outcome.equals(BioCodec.OTHER)) {
            return false;
          }
        }
      }
    }
    if (start.size() == 0) {
      return false;
    } else {
      for (String contPreffix : cont) {
        if (!start.contains(contPreffix)) {
          return false;
        }
      }
    }
    return true;
  }
}