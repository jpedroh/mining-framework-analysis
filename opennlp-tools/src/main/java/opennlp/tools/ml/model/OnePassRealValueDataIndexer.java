package opennlp.tools.ml.model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import opennlp.tools.util.InsufficientTrainingDataException;
import opennlp.tools.util.ObjectStream;

/**
 * An indexer for maxent model data which handles cutoffs for uncommon
 * contextual predicates and provides a unique integer index for each of the
 * predicates and maintains event values.
 */
public class OnePassRealValueDataIndexer extends OnePassDataIndexer {
  float[][] values;

  public OnePassRealValueDataIndexer() {
  }

  public float[][] getValues() {
    return values;
  }

  @Deprecated public OnePassRealValueDataIndexer(ObjectStream<Event> eventStream, int cutoff, boolean sort) throws IOException {
    super(eventStream, cutoff, sort);
  }

  protected int sortAndMerge(List<ComparableEvent> eventsToCompare, boolean sort) throws InsufficientTrainingDataException {
    int numUniqueEvents = super.sortAndMerge(eventsToCompare, sort);
    values = new float[numUniqueEvents][];
    int numEvents = eventsToCompare.size();
    for (int i = 0, j = 0; i < numEvents; i++) {
      ComparableEvent evt = eventsToCompare.get(i);
      if (null == evt) {
        continue;
      }
      values[j++] = evt.values;
    }
    return numUniqueEvents;
  }

  /**
   * Two argument constructor for DataIndexer.
   * @param eventStream An Event[] which contains the a list of all the Events
   *               seen in the training data.
   * @param cutoff The minimum number of times a predicate must have been
   *               observed in order to be included in the model.
   */
  @Deprecated public OnePassRealValueDataIndexer(ObjectStream<Event> eventStream, int cutoff) throws IOException {
    super(eventStream, cutoff);
  }

  @Override protected List<ComparableEvent> index(List<Event> events, Map<String, Integer> predicateIndex) {
    Map<String, Integer> omap = new HashMap<>();
    int numEvents = events.size();
    int outcomeCount = 0;
    List<ComparableEvent> eventsToCompare = new ArrayList<>(numEvents);
    List<Integer> indexedContext = new ArrayList<>();
    for (Event ev : events) {
      String[] econtext = ev.getContext();
      ComparableEvent ce;
      int ocID;
      String oc = ev.getOutcome();
      if (omap.containsKey(oc)) {
        ocID = omap.get(oc);
      } else {
        ocID = outcomeCount++;
        omap.put(oc, ocID);
      }
      for (String pred : econtext) {
        if (predicateIndex.containsKey(pred)) {
          indexedContext.add(predicateIndex.get(pred));
        }
      }
      if (indexedContext.size() > 0) {
        int[] cons = new int[indexedContext.size()];
        for (int ci = 0; ci < cons.length; ci++) {
          cons[ci] = indexedContext.get(ci);
        }
        ce = new ComparableEvent(ocID, cons, ev.getValues());
        eventsToCompare.add(ce);
      } else {
        System.err.println("Dropped event " + ev.getOutcome() + ":" + Arrays.asList(ev.getContext()));
      }
      indexedContext.clear();
    }
    outcomeLabels = toIndexedStringArray(omap);
    predLabels = toIndexedStringArray(predicateIndex);
    return eventsToCompare;
  }
}