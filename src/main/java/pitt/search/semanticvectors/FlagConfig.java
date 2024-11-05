package pitt.search.semanticvectors;
import pitt.search.semanticvectors.CompoundVectorBuilder.VectorLookupSyntax;
import pitt.search.semanticvectors.DocVectors.DocIndexingStrategy;
import pitt.search.semanticvectors.ElementalVectorStore.ElementalGenerationMethod;
import pitt.search.semanticvectors.LuceneUtils.TermWeight;
import pitt.search.semanticvectors.Search.SearchType;
import pitt.search.semanticvectors.TermTermVectorsFromLucene.PositionalMethod;
import pitt.search.semanticvectors.VectorStoreUtils.VectorStoreFormat;
import pitt.search.semanticvectors.utils.VerbatimLogger;
import pitt.search.semanticvectors.vectors.RealVector;
import pitt.search.semanticvectors.vectors.RealVector.RealBindMethod;
import pitt.search.semanticvectors.vectors.VectorType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Logger;
import pitt.search.semanticvectors.vectors.BinaryVector;
import pitt.search.semanticvectors.vectors.BinaryVector.BinaryNormalizationMethod;

public class FlagConfig {
  private static final Logger logger = Logger.getLogger(FlagConfig.class.getCanonicalName());

  private FlagConfig() {
    Field[] fields = FlagConfig.class.getDeclaredFields();
    for (int q = 0; q < fields.length; q++) {
      fields[q].setAccessible(true);
    }
  }

  public String[] remainingArgs;

  private int dimension = 200;

  public int dimension() {
    return dimension;
  }

  public void setDimension(int dimension) {
    this.dimension = dimension;
    this.makeFlagsCompatible();
  }

  private VectorType vectortype = VectorType.REAL;

  public VectorType vectortype() {
    return vectortype;
  }

  public void setVectortype(VectorType vectortype) {
    this.vectortype = vectortype;
    this.makeFlagsCompatible();
  }

  private RealBindMethod realbindmethod = RealBindMethod.CONVOLUTION;

  public RealBindMethod realbindmethod() {
    return realbindmethod;
  }

  private ElementalGenerationMethod elementalmethod = ElementalGenerationMethod.CONTENTHASH;

  public ElementalGenerationMethod elementalmethod() {
    return elementalmethod;
  }

  public int seedlength = 10;

  public int seedlength() {
    return seedlength;
  }

  private int minfrequency = 0;

  public int minfrequency() {
    return minfrequency;
  }

  private int maxfrequency = Integer.MAX_VALUE;

  public int maxfrequency() {
    return maxfrequency;
  }

  private int maxnonalphabetchars = Integer.MAX_VALUE;

  public int maxnonalphabetchars() {
    return maxnonalphabetchars;
  }

  private int mintermlength = 0;

  public int mintermlength() {
    return mintermlength;
  }

  private boolean filteroutnumbers = false;

  public boolean filteroutnumbers() {
    return filteroutnumbers;
  }

  private boolean bindnotreleasehack = false;

  public boolean bindnotreleasehack() {
    return bindnotreleasehack;
  }

  private boolean hybridvectors = false;

  public boolean hybridvectors() {
    return hybridvectors;
  }

  private int numsearchresults = 20;

  public int numsearchresults() {
    return numsearchresults;
  }

  private int treceval = -1;

  public int treceval() {
    return treceval;
  }

  private String jsonfile = "";

  public String jsonfile() {
    return jsonfile;
  }

  private int pathfinderQ = -1;

  public int pathfinderQ() {
    return pathfinderQ;
  }

  private double pathfinderR = Double.POSITIVE_INFINITY;

  public double pathfinderR() {
    return pathfinderR;
  }

  private double searchresultsminscore = -1.0;

  public double searchresultsminscore() {
    return searchresultsminscore;
  }

  private int numclusters = 10;

  public int numclusters() {
    return numclusters;
  }

  private int trainingcycles = 0;

  public int trainingcycles() {
    return trainingcycles;
  }

  private boolean rescaleintraining = false;

  public boolean rescaleintraining() {
    return rescaleintraining;
  }

  private int windowradius = 5;

  public int windowradius() {
    return windowradius;
  }

  private SearchType searchtype = SearchType.SUM;

  public SearchType searchtype() {
    return searchtype;
  }

  private boolean fieldweight = false;

  public boolean fieldweight() {
    return fieldweight;
  }

  private TermWeight termweight = TermWeight.IDF;

  public LuceneUtils.TermWeight termweight() {
    return termweight;
  }

  private boolean porterstemmer = false;

  public boolean porterstemmer() {
    return porterstemmer;
  }

  private boolean usetermweightsintermsearch = false;

  public boolean usetermweightsinsearch() {
    return usetermweightsintermsearch;
  }

  private boolean stdev = false;

  public boolean stdev() {
    return stdev;
  }

  private boolean expandsearchspace = false;

  public boolean expandsearchspace() {
    return expandsearchspace;
  }

  private VectorStoreFormat indexfileformat = VectorStoreFormat.LUCENE;

  public VectorStoreFormat indexfileformat() {
    return indexfileformat;
  }

  private String termvectorsfile = "termvectors";

  public String termvectorsfile() {
    return termvectorsfile;
  }

  private String docvectorsfile = "docvectors";

  public String docvectorsfile() {
    return docvectorsfile;
  }

  private String termtermvectorsfile = "termtermvectors";

  public String termtermvectorsfile() {
    return termtermvectorsfile;
  }

  private String queryvectorfile = "termvectors";

  public String queryvectorfile() {
    return queryvectorfile;
  }

  private String searchvectorfile = "";

  public String searchvectorfile() {
    return searchvectorfile;
  }

  private String boundvectorfile = "";

  public String boundvectorfile() {
    return boundvectorfile;
  }

  private String elementalvectorfile = "elementalvectors";

  public String elementalvectorfile() {
    return elementalvectorfile;
  }

  private String semanticvectorfile = "semanticvectors";

  public String semanticvectorfile() {
    return semanticvectorfile;
  }

  private String elementalpredicatevectorfile = "predicatevectors";

  public String elementalpredicatevectorfile() {
    return elementalpredicatevectorfile;
  }

  private String semanticpredicatevectorfile = "semanticpredicatevectors";

  public String semanticpredicatevectorfile() {
    return semanticpredicatevectorfile;
  }

  private String permutedvectorfile = "permtermvectors";

  public String permutedvectorfile() {
    return permutedvectorfile;
  }

  private String proximityvectorfile = "proxtermvectors";

  public String proximityvectorfile() {
    return proximityvectorfile;
  }

  private String directionalvectorfile = "drxntermvectors";

  public String directionalvectorfile() {
    return directionalvectorfile;
  }

  private String permplustermvectorfile = "permplustermvectors";

  public String permplustermvectorfile() {
    return permplustermvectorfile;
  }

  private PositionalMethod positionalmethod = PositionalMethod.BASIC;

  public PositionalMethod positionalmethod() {
    return positionalmethod;
  }

  private String stoplistfile = "";

  public String stoplistfile() {
    return stoplistfile;
  }

  private String startlistfile = "";

  public String startlistfile() {
    return startlistfile;
  }

  private String luceneindexpath = "";

  public String luceneindexpath() {
    return luceneindexpath;
  }

  private String initialtermvectors = "";

  public String initialtermvectors() {
    return initialtermvectors;
  }

  private String initialdocumentvectors = "";

  public String initialdocumentvectors() {
    return initialdocumentvectors;
  }

  private DocIndexingStrategy docindexing = DocIndexingStrategy.INCREMENTAL;

  public DocIndexingStrategy docindexing() {
    return docindexing;
  }

  private VectorLookupSyntax vectorlookupsyntax = VectorLookupSyntax.EXACTMATCH;

  public VectorLookupSyntax vectorlookupsyntax() {
    return vectorlookupsyntax;
  }

  private boolean matchcase = false;

  public boolean matchcase() {
    return matchcase;
  }

  private String batchcompareseparator = "\\|";

  public String batchcompareseparator() {
    return batchcompareseparator;
  }

  private boolean suppressnegatedqueries = false;

  public boolean suppressnegatedqueries() {
    return suppressnegatedqueries;
  }

  private String[] contentsfields = { "contents" };

  public String[] contentsfields() {
    return contentsfields;
  }

  public void setContentsfields(String[] contentsfields) {
    this.contentsfields = contentsfields;
  }

  private String docidfield = "path";

  public String docidfield() {
    return docidfield;
  }

  public static FlagConfig parseFlagsFromString(String header) {
    String[] args = header.split("\\s");
    return getFlagConfig(args);
  }

  public static FlagConfig getFlagConfig(String[] args) throws IllegalArgumentException {
    FlagConfig flagConfig = new FlagConfig();
    if (args == null || args.length == 0) {
      flagConfig.remainingArgs = new String[0];
      return flagConfig;
    }
    int argc = 0;
    while (args[argc].charAt(0) == '-') {
      String flagName = args[argc];
      if (flagName.equals("-")) {
        continue;
      }
      while (flagName.charAt(0) == '-') {
        flagName = flagName.substring(1, flagName.length());
      }
      try {
        Field field = FlagConfig.class.getDeclaredField(flagName);
        if (field.getType().getName().equals("java.lang.String")) {
          String flagValue;
          try {
            flagValue = args[argc + 1];
          } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("option -" + flagName + " requires an argument");
          }
          field.set(flagConfig, flagValue);
          argc += 2;
        } else {
          if (field.getType().getName().equals("[Ljava.lang.String;")) {
            String flagValue;
            try {
              flagValue = args[argc + 1].toLowerCase();
            } catch (ArrayIndexOutOfBoundsException e) {
              throw new IllegalArgumentException("option -" + flagName + " requires an argument");
            }
            field.set(flagConfig, flagValue.split(","));
            argc += 2;
          } else {
            if (field.getType().getName().equals("int")) {
              try {
                field.setInt(flagConfig, Integer.parseInt(args[argc + 1]));
              } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("option -" + flagName + " requires an argument");
              }
              argc += 2;
            } else {
              if (field.getType().getName().equals("double")) {
                try {
                  field.setDouble(flagConfig, Double.parseDouble(args[argc + 1]));
                } catch (ArrayIndexOutOfBoundsException e) {
                  throw new IllegalArgumentException("option -" + flagName + " requires an argument");
                }
                argc += 2;
              } else {
                if (field.getType().isEnum()) {
                  try {
                    @SuppressWarnings(value = { "rawtypes", "unchecked" }) Class<Enum> className = (Class<Enum>) field.getType();
                    try {
                      field.set(flagConfig, Enum.valueOf(className, args[argc + 1].toUpperCase()));
                    } catch (IllegalArgumentException e) {
                      throw new IllegalArgumentException(String.format(e.getMessage() + "\nAccepted values for \'-%s\' are:\n%s%n", field.getName(), Arrays.asList(className.getEnumConstants()), e));
                    }
                  } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("option -" + flagName + " requires an argument");
                  }
                  argc += 2;
                } else {
                  if (field.getType().getName().equals("boolean")) {
                    field.setBoolean(flagConfig, true);
                    ++argc;
                  } else {
                    logger.warning("No support for fields of type: " + field.getType().getName());
                    argc += 2;
                  }
                }
              }
            }
          }
        }
      } catch (NoSuchFieldException e) {
        throw new IllegalArgumentException("Command line flag not defined: " + flagName);
      } catch (IllegalAccessException e) {
        logger.warning("Must be able to access all fields publicly, including: " + flagName);
        e.printStackTrace();
      }
      if (argc >= args.length) {
        logger.fine("Consumed all command line input while parsing flags");
        flagConfig.makeFlagsCompatible();
        return flagConfig;
      }
    }
    flagConfig.makeFlagsCompatible();
    flagConfig.remainingArgs = new String[args.length - argc];
    for (int i = 0; i < args.length - argc; ++i) {
      flagConfig.remainingArgs[i] = args[argc + i];
    }
    return flagConfig;
  }

  public static void mergeWriteableFlagsFromString(String source, FlagConfig target) {
    FlagConfig sourceConfig = FlagConfig.parseFlagsFromString(source);
    mergeWriteableFlags(sourceConfig, target);
  }

  public static void mergeWriteableFlags(FlagConfig source, FlagConfig target) {
    if (target.dimension != source.dimension) {
      VerbatimLogger.info("Setting dimension of target config to: " + source.dimension + "\n");
      target.dimension = source.dimension;
    }
    if (target.vectortype != source.vectortype) {
      VerbatimLogger.info("Setting vectortype of target config to: " + source.vectortype + "\n");
      target.vectortype = source.vectortype;
    }
    target.makeFlagsCompatible();
  }

  private void makeFlagsCompatible() {
    if (vectortype == VectorType.BINARY) {
      if (dimension % 64 != 0) {
        dimension = (1 + (dimension / 64)) * 64;
        logger.fine("For performance reasons, dimensions for binary vectors must be a mutliple " + "of 64. Flags.dimension set to: " + dimension + ".");
      }
      if (seedlength != dimension / 2) {
        seedlength = dimension / 2;
        logger.fine("Binary vectors must be generated with a balanced number of zeros and ones." + " FlagConfig.seedlength set to: " + seedlength + ".");
      }
    }
    if (searchvectorfile.isEmpty()) {
      searchvectorfile = queryvectorfile;
    }
    if (vectortype == VectorType.REAL && realbindmethod == RealVector.RealBindMethod.PERMUTATION) {
      RealVector.setBindType(RealVector.RealBindMethod.PERMUTATION);
    }
    if (vectortype == VectorType.BINARY && binarynormalizemethod == BinaryVector.BinaryNormalizationMethod.PROBABILISTIC) {
      BinaryVector.setNormalizationMethod(BinaryVector.BinaryNormalizationMethod.PROBABILISTIC);
    }
  }

  public void setExpandsearchspace(boolean b) {
    this.expandsearchspace = b;
  }

  private BinaryNormalizationMethod binarynormalizemethod = BinaryNormalizationMethod.SPATTERCODE;

  public BinaryNormalizationMethod binarynormalizemethod() {
    return binarynormalizemethod;
  }
}