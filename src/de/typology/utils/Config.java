package de.typology.utils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * This is an interface class to the Config file for this project. For each
 * class field one java property must be defined in config.txt. The fields will
 * be automatically filled!
 * 
 * Allowed Types are String, int, boolean, String[] and long[] where arrays are
 * defined by semicolon-separated Strings like "array=a;b;c" boolen fields are
 * initialized with true or false
 * 
 * lines starting with # will be ignored and can serve as comments
 * 
 * @author Jonas Kunze, Rene Pickhardt
 * 
 */
public class Config extends Properties {
  public String dbUser;

  public String dbName;

  public String trainedOnDataSet;

  public String trainedOnLang;

  public String testedOnDataSet;

  public String testedOnLang;

  public String weight;

  public boolean parseData;

  public boolean sampleSplitData;

  public boolean useWeights;

  public boolean loadIndexToRAM;

  public boolean weightedPredictions;

  public String dgttmInputDirectory;

  public String enronInputDirectory;

  public String googleInputDirectory;

  public String reutersInputDirectory;

  public String wikiInputDirectory;

  public String DGTTMLanguages;

  public String outputDirectory;

  public int memoryLimitForWritingFiles;

  public int nGramLength;

  public int sampleRate;

  public int splitDataRatio;

  public int splitTestRatio;

  public String trainingPath;

  public String testingPath;

  public String learningPath;

  public boolean createNGramChunks;

  public boolean createSecondLevelNGramChunks;

  public boolean aggregateNGramChunks;

  public boolean sortNGrams;

  public boolean generateNGramDistribution;

  public boolean normalizeNGrams;

  public boolean createTypologyEdgeChunks;

  public boolean createSecondLevelTypologyEdgeChunks;

  public boolean aggregateTypologyEdgeChunks;

  public boolean sortTypologyEdges;

  public boolean generateTypologyEdgeDistribution;

  public boolean normalizeEdges;

  public int fileChunkThreashhold;

  public String nGramKeyFile;

  public String nGramsNotAggregatedPath;

  public String typologyEdgesPathNotAggregated;

  public String wikiLinksOutputPath;

  public String wikiLinksHead;

  public String nGramsAggregatedPath;

  public String edgeInput;

  public String normalizedEdges;

  public String nGramsInput;

  public String normalizedNGrams;

  public String indexPath;

  public String nGramIndexPath;

  public String wordCountInput;

  public String wordCountStats;

  public String lineCountInput;

  public String lineCountStats;

  public String dataSet;

  private static final long serialVersionUID = -4439565094382127683L;

  static Config instance = null;

  public static String ngramDownloadPath;

  public static String ngramDownloadOutputPath;

  public Config() {
    String file = "config.txt";
    try {
      BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
      this.load(stream);
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      this.initialize();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  /**
	 * Fills all fields with the data defined in the config file.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
  private void initialize() throws IllegalArgumentException, IllegalAccessException {
    Field[] fields = this.getClass().getFields();
    for (Field f : fields) {
      if (this.getProperty(f.getName()) == null) {
        System.err.print("Property \'" + f.getName() + "\' not defined in config file");
      }
      if (f.getType().equals(String.class)) {
        f.set(this, this.getProperty(f.getName()));
      } else {
        if (f.getType().equals(long.class)) {
          f.setLong(this, Long.valueOf(this.getProperty(f.getName())));
        } else {
          if (f.getType().equals(int.class)) {
            f.setInt(this, Integer.valueOf(this.getProperty(f.getName())));
          } else {
            if (f.getType().equals(boolean.class)) {
              f.setBoolean(this, Boolean.valueOf(this.getProperty(f.getName())));
            } else {
              if (f.getType().equals(String[].class)) {
                f.set(this, this.getProperty(f.getName()).split(";"));
              } else {
                if (f.getType().equals(int[].class)) {
                  String[] tmp = this.getProperty(f.getName()).split(";");
                  int[] ints = new int[tmp.length];
                  for (int i = 0; i < tmp.length; i++) {
                    ints[i] = Integer.parseInt(tmp[i]);
                  }
                  f.set(this, ints);
                } else {
                  if (f.getType().equals(long[].class)) {
                    String[] tmp = this.getProperty(f.getName()).split(";");
                    long[] longs = new long[tmp.length];
                    for (int i = 0; i < tmp.length; i++) {
                      longs[i] = Long.parseLong(tmp[i]);
                    }
                    f.set(this, longs);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public static Config get() {
    if (instance == null) {
      instance = new Config();
    }
    return instance;
  }
}