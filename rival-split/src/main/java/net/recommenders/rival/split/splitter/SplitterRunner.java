package net.recommenders.rival.split.splitter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import net.recommenders.rival.core.DataModelUtils;
import net.recommenders.rival.core.TemporalDataModelIF;

/**
 * Class that splits a dataset according to some properties.
 *
 * @author <a href="http://github.com/abellogin">Alejandro</a>
 */
public final class SplitterRunner {
  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String DATASET_SPLITTER = "dataset.splitter";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_PERUSER = "split.peruser";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_PERITEMS = "split.peritems";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_SEED = "split.seed";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_CV_NFOLDS = "split.cv.nfolds";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_RANDOM_PERCENTAGE = "split.random.percentage";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_OUTPUT_FOLDER = "split.output.folder";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_OUTPUT_OVERWRITE = "split.output.overwrite";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_TRAINING_PREFIX = "split.training.prefix";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_TRAINING_SUFFIX = "split.training.suffix";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_TEST_PREFIX = "split.test.prefix";

  /**
     * Variable that represent the name of a property in the file.
     */
  public static final String SPLIT_TEST_SUFFIX = "split.test.suffix";

  /**
     * Utility classes should not have a public or default constructor.
     */
  private SplitterRunner() {
  }

  /**
     * Runs a Splitter instance based on the properties.
     *
     * @param properties property file
     * @param data the data to be split
     * @param doDataClear flag to clear the memory used for the data before
     * saving the splits
     * @throws FileNotFoundException see
     * {@link net.recommenders.rival.core.DataModelUtils#saveDataModel(net.recommenders.rival.core.DataModel, java.lang.String, boolean)}
     * @throws UnsupportedEncodingException see
     * {@link net.recommenders.rival.core.DataModelUtils#saveDataModel(net.recommenders.rival.core.DataModel, java.lang.String, boolean)}
     */
  public static <U extends java.lang.Object, I extends java.lang.Object> void run(final Properties properties, final TemporalDataModelIF<U, I> data, final boolean doDataClear) throws FileNotFoundException, UnsupportedEncodingException {
    System.out.println("Start splitting");
    TemporalDataModelIF<U, I>[] splits;
    String outputFolder = properties.getProperty(SPLIT_OUTPUT_FOLDER);
    Boolean overwrite = Boolean.parseBoolean(properties.getProperty(SPLIT_OUTPUT_OVERWRITE, "false"));
    String splitTrainingPrefix = properties.getProperty(SPLIT_TRAINING_PREFIX);
    String splitTrainingSuffix = properties.getProperty(SPLIT_TRAINING_SUFFIX);
    String splitTestPrefix = properties.getProperty(SPLIT_TEST_PREFIX);
    String splitTestSuffix = properties.getProperty(SPLIT_TEST_SUFFIX);
    Splitter<U, I> splitter = instantiateSplitter(properties);
    splits = splitter.split(data);
    if (doDataClear) {
      data.clear();
    }
    System.out.println("Saving splits");
    for (int i = 0; i < splits.length / 2; i++) {
      TemporalDataModelIF<U, I> training = splits[2 * i];
      TemporalDataModelIF<U, I> test = splits[2 * i + 1];
      String trainingFile = outputFolder + splitTrainingPrefix + i + splitTrainingSuffix;
      String testFile = outputFolder + splitTestPrefix + i + splitTestSuffix;
      DataModelUtils.saveDataModel(training, trainingFile, overwrite);
      DataModelUtils.saveDataModel(test, testFile, overwrite);
    }
  }


<<<<<<< /usr/src/app/output/recommenders/rival/aefae91d1ce70013519cfa0bc0132c5279c0fb2c/rival-split/src/main/java/net/recommenders/rival/split/splitter/SplitterRunner.java/left.java
  /**
     *
     * Instantiates a splitter based on the properties.
     *
     * @param properties the properties to be used.
     * @return a splitter according to the properties mapping provided.
     */
  public static Splitter<Long, Long> instantiateSplitter(final Properties properties) {
    String splitterClassName = properties.getProperty(DATASET_SPLITTER);
    Boolean perUser = Boolean.parseBoolean(properties.getProperty(SPLIT_PERUSER));
    Boolean doSplitPerItems = Boolean.parseBoolean(properties.getProperty(SPLIT_PERITEMS, "true"));
    Splitter<Long, Long> splitter = null;
    if (splitterClassName.contains("CrossValidation")) {
      Long seed = Long.parseLong(properties.getProperty(SPLIT_SEED));
      Integer nFolds = Integer.parseInt(properties.getProperty(SPLIT_CV_NFOLDS));
      splitter = new CrossValidationSplitter<Long, Long>(nFolds, perUser, seed);
    } else {
      if (splitterClassName.contains("Random")) {
        Long seed = Long.parseLong(properties.getProperty(SPLIT_SEED));
        Float percentage = Float.parseFloat(properties.getProperty(SPLIT_RANDOM_PERCENTAGE));
        splitter = new RandomSplitter(percentage, perUser, seed, doSplitPerItems);
      } else {
        if (splitterClassName.contains("Temporal")) {
          Float percentage = Float.parseFloat(properties.getProperty(SPLIT_RANDOM_PERCENTAGE));
          splitter = new TemporalSplitter(percentage, perUser, doSplitPerItems);
        }
      }
    }
    return splitter;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  /**
     *
     * Instantiates a splitter based on the properties.
     *
     * @param properties the properties to be used.
     * @return a splitter according to the properties mapping provided.
     */
  public static <U extends java.lang.Object, I extends java.lang.Object> Splitter<U, I> instantiateSplitter(final Properties properties) {
    String splitterClassName = properties.getProperty(DATASET_SPLITTER);
    Boolean perUser = Boolean.parseBoolean(properties.getProperty(SPLIT_PERUSER));
    Boolean doSplitPerItems = Boolean.parseBoolean(properties.getProperty(SPLIT_PERITEMS, "true"));
    Splitter<U, I> splitter = null;
    if (splitterClassName.contains("CrossValidation")) {
      Long seed = Long.parseLong(properties.getProperty(SPLIT_SEED));
      Integer nFolds = Integer.parseInt(properties.getProperty(SPLIT_CV_NFOLDS));
      splitter = new CrossValidationSplitter<>(nFolds, perUser, seed);
    } else {
      if (splitterClassName.contains("Random")) {
        Long seed = Long.parseLong(properties.getProperty(SPLIT_SEED));
        Float percentage = Float.parseFloat(properties.getProperty(SPLIT_RANDOM_PERCENTAGE));
        splitter = new RandomSplitter<>(percentage, perUser, seed, doSplitPerItems);
      } else {
        if (splitterClassName.contains("Temporal")) {
          Float percentage = Float.parseFloat(properties.getProperty(SPLIT_RANDOM_PERCENTAGE));
          splitter = new TemporalSplitter<>(percentage, perUser, doSplitPerItems);
        }
      }
    }
    return splitter;
  }
}