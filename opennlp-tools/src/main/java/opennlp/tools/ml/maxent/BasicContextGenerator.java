package opennlp.tools.ml.maxent;

/**
 * Generate contexts for maxent decisions, assuming that the input
 * given to the getContext() method is a String containing contextual
 * predicates separated by spaces.
 * e.g:
 * <p>
 * cp_1 cp_2 ... cp_n
 * </p>
 */
public class BasicContextGenerator implements ContextGenerator<String> {
  private String separator = " ";

  public BasicContextGenerator() {
  }

  public BasicContextGenerator(String sep) {
    separator = sep;
  }

  /**
   * Builds up the list of contextual predicates given a String.
   */
  public String[] getContext(String o) {
    return o.split(separator);
  }
}