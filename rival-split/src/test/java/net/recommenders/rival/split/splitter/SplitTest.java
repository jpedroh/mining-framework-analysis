package net.recommenders.rival.split.splitter;
import java.util.Map;
import java.util.Map.Entry;
import net.recommenders.rival.core.DataModel;
import net.recommenders.rival.core.DataModelFactory;
import net.recommenders.rival.core.DataModelIF;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Main class that parses a data set and splits it according to a property file.
 * It tests implementations of {@link net.recommenders.rival.split.splitter.Splitter}.
 *
 * @author <a href="http://github.com/abellogin">Alejandro</a>
 */
public class SplitTest {
  /**
     * The number of users in the data model.
     */
  private static final int USERS = 10;

  /**
     * The number of items in the data model.
     */
  private static final int ITEMS = 10;

  @Test public void testCrossValidation() {
    DataModelIF<Long, Long> dm = DataModelFactory.getDefaultModel();
    for (long u = 1L; u <= USERS; u++) {
      for (long i = 1L; i <= ITEMS; i++) {
        dm.addPreference(u, i, 1.0 * u * i);
      }
    }
    int nFolds = 5;
    DataModelIF<Long, Long>[] splits = null;
    splits = new CrossValidationSplitter<Long, Long>(nFolds, false, 1L).split(dm);
    assertTrue(splits.length == 2 * nFolds);
    long userTest = -1;
    long itemTest = -1;
    for (Entry<Long, Map<Long, Double>> e : splits[1].getUserItemPreferences().entrySet()) {
      userTest = e.getKey();
      for (long i : e.getValue().keySet()) {
        itemTest = i;
        break;
      }
      break;
    }
    assertTrue(!splits[0].getUserItemPreferences().containsKey(userTest) || !splits[0].getUserItemPreferences().get(userTest).containsKey(itemTest));
    for (int i = 1; i < splits.length / 2; i++) {
      DataModelIF<Long, Long> training = splits[2 * i];
      DataModelIF<Long, Long> test = splits[2 * i + 1];
      assertTrue(!test.getUserItemPreferences().containsKey(userTest) || !test.getUserItemPreferences().get(userTest).containsKey(itemTest));
      assertTrue(training.getUserItemPreferences().get(userTest).containsKey(itemTest));
    }
  }

  @Test public void testRandom() {
    DataModelIF<Long, Long> dm = DataModelFactory.getDefaultModel();
    for (long u = 1L; u <= USERS; u++) {
      for (long i = 1L; i <= ITEMS; i++) {
        dm.addPreference(u, i, 1.0 * u * i);
      }
    }
    DataModelIF<Long, Long>[] splits = null;
    splits = new RandomSplitter<Long, Long>(0.8f, false, 1L, false).split(dm);
    assertTrue(splits.length == 2);
    long userTest = -1;
    long itemTest = -1;
    for (Entry<Long, Map<Long, Double>> e : splits[1].getUserItemPreferences().entrySet()) {
      userTest = e.getKey();
      for (long i : e.getValue().keySet()) {
        itemTest = i;
        break;
      }
      break;
    }
    assertTrue(!splits[0].getUserItemPreferences().containsKey(userTest) || !splits[0].getUserItemPreferences().get(userTest).containsKey(itemTest));
  }
}