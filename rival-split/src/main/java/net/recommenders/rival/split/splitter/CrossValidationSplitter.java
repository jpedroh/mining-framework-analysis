package net.recommenders.rival.split.splitter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.recommenders.rival.core.DataModelFactory;
import net.recommenders.rival.core.DataModelIF;
import net.recommenders.rival.core.TemporalDataModel;
import net.recommenders.rival.core.TemporalDataModelIF;

/**
 * Class that splits a dataset using a cross validation technique (every
 * interaction in the data only appears once in each test split).
 *
 * @author <a href="http://github.com/abellogin">Alejandro</a>
 */
public class CrossValidationSplitter<U extends java.lang.Object, I extends java.lang.Object> implements Splitter<U, I> {
  /**
     * The number of folds that the data will be split into.
     */
  private int nFolds;

  /**
     * The flag that indicates if the split should be done in a per user basis.
     */
  private boolean perUser;

  /**
     * An instance of a Random class.
     */
  private Random rnd;

  /**
     * Constructor.
     *
     * @param nFold number of folds that the data will be split into
     * @param perUsers flag to do the split in a per user basis
     * @param seed value to initialize a Random class
     */
  public CrossValidationSplitter(final int nFold, final boolean perUsers, final long seed) {
    this.nFolds = nFold;
    this.perUser = perUsers;
    rnd = new Random(seed);
  }

  /**
     * {@inheritDoc}
     */
  @Override public DataModelIF<U, I>[] split(final DataModelIF<U, I> data) {
    @SuppressWarnings(value = { "unchecked" }) final DataModelIF<U, I>[] splits = new DataModelIF[2 * nFolds];
    for (int i = 0; i < nFolds; i++) {
      splits[2 * i] = DataModelFactory.getDefaultModel();
      splits[2 * i + 1] = DataModelFactory.getDefaultModel();
    }
    if (perUser) {
      int n = 0;
      for (U user : data.getUsers()) {
        List<I> items = new ArrayList<>(data.getUserItemPreferences().get(user).keySet());
        Collections.shuffle(items, rnd);
        for (I item : items) {
          Double pref = data.getUserItemPreferences().get(user).get(item);
          int curFold = n % nFolds;
          for (int i = 0; i < nFolds; i++) {
            DataModelIF<U, I> datamodel = splits[2 * i];
            if (i == curFold) {
              datamodel = splits[2 * i + 1];
            }
            if (pref != null) {
              datamodel.addPreference(user, item, pref);
            }
          }
          n++;
        }
      }
    } else {
      List<U> users = new ArrayList<>(data.getUsers());
      Collections.shuffle(users, rnd);
      int n = 0;
      for (U user : users) {
        List<I> items = new ArrayList<>(data.getUserItemPreferences().get(user).keySet());
        Collections.shuffle(items, rnd);
        for (I item : items) {
          Double pref = data.getUserItemPreferences().get(user).get(item);
          int curFold = n % nFolds;
          for (int i = 0; i < nFolds; i++) {
            DataModelIF<U, I> datamodel = splits[2 * i];
            if (i == curFold) {
              datamodel = splits[2 * i + 1];
            }
            if (pref != null) {
              datamodel.addPreference(user, item, pref);
            }
          }
          n++;
        }
      }
    }
    return splits;
  }

  /**
     * {@inheritDoc}
     */
  @Override public 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
  DataModel
=======
  TemporalDataModelIF
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
  <U, I>[] split(final 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
  DataModel
=======
  TemporalDataModelIF
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
  <U, I> data) {
    @SuppressWarnings(value = { "unchecked" }) final 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
    DataModel
=======
    TemporalDataModelIF
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
    <U, I>[] splits = new 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
    DataModel
=======
    TemporalDataModelIF
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
    [2 * nFolds];
    for (int i = 0; i < nFolds; i++) {
      splits[2 * i] = 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
      new DataModel<U, I>()
=======
      new TemporalDataModel<>()
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
      ;
      splits[2 * i + 1] = 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
      new DataModel<U, I>()
=======
      new TemporalDataModel<>()
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
      ;
    }
    if (perUser) {
      int n = 0;
      for (U user : data.getUsers()) {
        List<I> items = 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
        new ArrayList<I>(data.getUserItemPreferences().get(user).keySet())
=======
        new ArrayList<>(data.getUserItemPreferences().get(user).keySet())
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
        ;
        Collections.shuffle(items, rnd);
        for (I item : items) {
          Double pref = data.getUserItemPreferences().get(user).get(item);
          Set<Long> time = null;
          if (data.getUserItemTimestamps().containsKey(user) && data.getUserItemTimestamps().get(user).containsKey(item)) {
            time = data.getUserItemTimestamps().get(user).get(item);
          }
          int curFold = n % nFolds;
          for (int i = 0; i < nFolds; i++) {

<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
            DataModel
=======
            TemporalDataModelIF
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
            <U, I> datamodel = splits[2 * i];
            if (i == curFold) {
              datamodel = splits[2 * i + 1];
            }
            if (pref != null) {
              datamodel.addPreference(user, item, pref);
            }
            if (time != null) {
              for (Long t : time) {
                datamodel.addTimestamp(user, item, t);
              }
            }
          }
          n++;
        }
      }
    } else {
      List<U> users = 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
      new ArrayList<U>(data.getUsers())
=======
      new ArrayList<>(data.getUsers())
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
      ;
      Collections.shuffle(users, rnd);
      int n = 0;
      for (U user : users) {
        List<I> items = 
<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
        new ArrayList<I>(data.getUserItemPreferences().get(user).keySet())
=======
        new ArrayList<>(data.getUserItemPreferences().get(user).keySet())
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
        ;
        Collections.shuffle(items, rnd);
        for (I item : items) {
          Double pref = data.getUserItemPreferences().get(user).get(item);
          Set<Long> time = null;
          if (data.getUserItemTimestamps().containsKey(user) && data.getUserItemTimestamps().get(user).containsKey(item)) {
            time = data.getUserItemTimestamps().get(user).get(item);
          }
          int curFold = n % nFolds;
          for (int i = 0; i < nFolds; i++) {

<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/left.java
            DataModel
=======
            TemporalDataModelIF
>>>>>>> /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/CrossValidationSplitter.java/right.java
            <U, I> datamodel = splits[2 * i];
            if (i == curFold) {
              datamodel = splits[2 * i + 1];
            }
            if (pref != null) {
              datamodel.addPreference(user, item, pref);
            }
            if (time != null) {
              for (Long t : time) {
                datamodel.addTimestamp(user, item, t);
              }
            }
          }
          n++;
        }
      }
    }
    return splits;
  }
}