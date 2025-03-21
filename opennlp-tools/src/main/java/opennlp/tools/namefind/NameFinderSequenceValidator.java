package opennlp.tools.namefind;
import opennlp.tools.util.SequenceValidator;

/**
 * This class is created by the {@link BioCodec}.
 */
public class NameFinderSequenceValidator implements SequenceValidator<String> {
  public boolean validSequence(int i, String[] inputSequence, String[] outcomesSequence, String outcome) {
    if (outcome.endsWith(BioCodec.CONTINUE)) {
      int li = outcomesSequence.length - 1;
      if (li == -1) {
        return false;
      } else {
        if (outcomesSequence[li].endsWith(BioCodec.OTHER)) {
          return false;
        } else {
          if (outcomesSequence[li].endsWith(BioCodec.CONTINUE) || outcomesSequence[li].endsWith(BioCodec.START)) {
            String previousNameType = NameFinderME.extractNameType(outcomesSequence[li]);
            String nameType = NameFinderME.extractNameType(outcome);
            if (previousNameType != null || nameType != null) {
              if (nameType != null) {
                return nameType.equals(previousNameType);
              }
              return false;
            }
          }
        }
      }
    }
    return true;
  }
}