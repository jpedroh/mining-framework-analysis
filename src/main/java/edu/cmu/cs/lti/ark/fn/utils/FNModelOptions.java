package edu.cmu.cs.lti.ark.fn.utils;
import edu.cmu.cs.lti.ark.util.CommandLineOptions;

/**
 * A hodgepodge of command line options for various models used in training/testing the frame structure parser. 
 * Should probably be refactored into several separate classes
 * @author Nathan Schneider (nschneid)
 * @since 2009-09-25
 */
public final class FNModelOptions extends CommandLineOptions {
  public FNModelOptions(String[] args) {
    this(args, false);
  }

  public FNModelOptions(String[] args, boolean ignoreUnknownOptions) {
    super();
    init(args, ignoreUnknownOptions);
  }

  public StringOption alphabetFile = new StringOption("alphabetfile");

  public StringOption warmStartModelFile = new StringOption("warm-start-model");

  public BoolOption train = new BoolOption("train");

  public IntOption saveEveryKBatches = new IntOption("save-every-k-batches");

  public BoolOption dev = new BoolOption("dev");

  public BoolOption test = new BoolOption("test");

  public StringOption trainParseFile = new StringOption("train-parsefile");

  public StringOption trainFrameFile = new StringOption("train-framefile");

  public StringOption trainFrameElementFile = new StringOption("train-fefile");

  public StringOption reg = new StringOption("regularization");

  public DoubleOption l1Strength = new DoubleOption("l1-strength");

  public DoubleOption l2Strength = new DoubleOption("l2-strength");

  public StringOption frameNetMapFile = new StringOption("framenet-mapfile");

  public ExistingPathOption wnConfigFile = new ExistingPathOption("wordnet-configfile");

  public ExistingPathOption stopWordsFile = new ExistingPathOption("stopwords-file");

  public StringOption testFrameFile = new StringOption("test-framefile");

  public StringOption testParseFile = new StringOption("test-parsefile");

  public StringOption modelFile = new StringOption("model");

  public IntOption numModelsToSave = new IntOption("num-models-to-save");

  public IntOption memory = new IntOption("memory");

  public IntOption startIndex = new IntOption("startindex");

  public IntOption endIndex = new IntOption("endindex");

  public ExistingPathOption testTokenizedFile = new ExistingPathOption("testtokenizedfile");

  public StringOption allRelatedWordsFile = new StringOption("allrelatedwordsfile");

  public StringOption wnRelatedWordsForWordsFile = new StringOption("wnrelatedwordsforwordsfile");

  public StringOption wnMapFile = new StringOption("wnmapfile");

  public StringOption hvCorrespondenceFile = new StringOption("hvcorrespondencefile");

  public StringOption fnIdReqDataFile = new StringOption("fnidreqdatafile");

  public ExistingPathOption idParamFile = new ExistingPathOption("idmodelfile");

  public ExistingPathOption luXmlDir = new ExistingPathOption("luxmldir");

  public NewFilePathOption frameElementsOutputFile = new NewFilePathOption("frameelementsoutputfile");

  public NewFilePathOption logOutputFile = new NewFilePathOption("logoutputfile");

  public IntOption minimumCount = new IntOption("minimum-count");

  public IntOption numThreads = new IntOption("numthreads");

  public IntOption batchSize = new PositiveIntOption("batch-size");

  public BoolOption usePartialCredit = new BoolOption("use-partial-credit");

  public DoubleOption costMultiple = new DoubleOption("cost-multiple");

  public ExistingPathOption inputFile = new ExistingPathOption("input-file");

  public NewFilePathOption outputFile = new NewFilePathOption("output-file");

  public StringOption idFeatureExtractorType = new StringOption("id-feature-extractor-type");

  public StringOption eventsFile = new StringOption("eventsfile");

  public StringOption spansFile = new StringOption("spansfile");

  public StringOption frameFeaturesCacheFile = new StringOption("localfeaturescache");

  public StringOption restartFile = new StringOption("restartfile");

  public StringOption lemmaCacheFile = new StringOption("lemmacachefile");

  public StringOption revisedMapFile = new StringOption("revisedmapfile");

  public StringOption useGraph = new StringOption("useGraph");

  public IntOption port = new IntOption("port");

  public StringOption modelDirectory = new StringOption("model-dir");

  public PositiveIntOption kBestOutput = new PositiveIntOption("k-best-output");
}