package opennlp.tools.ml.maxent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import opennlp.tools.ml.model.DataIndexer;
import opennlp.tools.ml.model.EvalParameters;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MutableContext;
import opennlp.tools.ml.model.OnePassDataIndexer;
import opennlp.tools.ml.model.Prior;
import opennlp.tools.ml.model.UniformPrior;
import opennlp.tools.util.ObjectStream;

/**
 * An implementation of Generalized Iterative Scaling.  The reference paper
 * for this implementation was Adwait Ratnaparkhi's tech report at the
 * University of Pennsylvania's Institute for Research in Cognitive Science,
 * and is available at <a href ="ftp://ftp.cis.upenn.edu/pub/ircs/tr/97-08.ps.Z"><code>ftp://ftp.cis.upenn.edu/pub/ircs/tr/97-08.ps.Z</code></a>.
 *
 * The slack parameter used in the above implementation has been removed by default
 * from the computation and a method for updating with Gaussian smoothing has been
 * added per Investigating GIS and Smoothing for Maximum Entropy Taggers, Clark and Curran (2002).
 * <a href="http://acl.ldc.upenn.edu/E/E03/E03-1071.pdf"><code>http://acl.ldc.upenn.edu/E/E03/E03-1071.pdf</code></a>
 * The slack parameter can be used by setting <code>useSlackParameter</code> to true.
 * Gaussian smoothing can be used by setting <code>useGaussianSmoothing</code> to true.
 *
 * A prior can be used to train models which converge to the distribution which minimizes the
 * relative entropy between the distribution specified by the empirical constraints of the training
 * data and the specified prior.  By default, the uniform distribution is used as the prior.
 */
class GISTrainer {
  /**
   * Specifies whether unseen context/outcome pairs should be estimated as occur very infrequently.
   */
  private boolean useSimpleSmoothing = false;

  /**
   * Specified whether parameter updates should prefer a distribution of parameters which
   * is gaussian.
   */
  private boolean useGaussianSmoothing = false;

  private double sigma = 2.0;

  private double _smoothingObservation = 0.1;

  private final boolean printMessages;

  /**
   * Number of unique events which occured in the event set.
   */
  private int numUniqueEvents;

  /**
   * Number of predicates.
   */
  private int numPreds;

  /**
   * Number of outcomes.
   */
  private int numOutcomes;

  /**
   * Records the array of predicates seen in each event.
   */
  private int[][] contexts;

  /**
   * The value associated with each context. If null then context values are assumes to be 1.
   */
  private float[][] values;

  /**
   * List of outcomes for each event i, in context[i].
   */
  private int[] outcomeList;

  /**
   * Records the num of times an event has been seen for each event i, in context[i].
   */
  private int[] numTimesEventsSeen;

  /**
   * The number of times a predicate occured in the training data.
   */
  private int[] predicateCounts;

  private int cutoff;

  /**
   * Stores the String names of the outcomes. The GIS only tracks outcomes as
   * ints, and so this array is needed to save the model to disk and thereby
   * allow users to know what the outcome was in human understandable terms.
   */
  private String[] outcomeLabels;

  /**
   * Stores the String names of the predicates. The GIS only tracks predicates
   * as ints, and so this array is needed to save the model to disk and thereby
   * allow users to know what the outcome was in human understandable terms.
   */
  private String[] predLabels;

  /**
   * Stores the observed expected values of the features based on training data.
   */
  private MutableContext[] observedExpects;

  /**
   * Stores the estimated parameter value of each predicate during iteration
   */
  private MutableContext[] params;

  /**
   * Stores the expected values of the features based on the current models
   */
  private MutableContext[][] modelExpects;

  /**
   * This is the prior distribution that the model uses for training.
   */
  private Prior prior;

  private static final double LLThreshold = 0.0001;

  /**
   * Initial probability for all outcomes.
   */
  private EvalParameters evalParams;

  /**
   * Creates a new <code>GISTrainer</code> instance which does not print
   * progress messages about training to STDOUT.
   *
   */
  GISTrainer() {
    printMessages = false;
  }

  /**
   * Creates a new <code>GISTrainer</code> instance.
   *
   * @param printMessages sends progress messages about training to
   *                      STDOUT when true; trains silently otherwise.
   */
  GISTrainer(boolean printMessages) {
    this.printMessages = printMessages;
  }

  /**
   * Sets whether this trainer will use smoothing while training the model.
   * This can improve model accuracy, though training will potentially take
   * longer and use more memory.  Model size will also be larger.
   *
   * @param smooth true if smoothing is desired, false if not
   */
  public void setSmoothing(boolean smooth) {
    useSimpleSmoothing = smooth;
  }

  /**
   * Sets whether this trainer will use smoothing while training the model.
   * This can improve model accuracy, though training will potentially take
   * longer and use more memory.  Model size will also be larger.
   *
   * @param timesSeen the "number" of times we want the trainer to imagine
   *                  it saw a feature that it actually didn't see
   */
  public void setSmoothingObservation(double timesSeen) {
    _smoothingObservation = timesSeen;
  }

  /**
   * Sets whether this trainer will use smoothing while training the model.
   * This can improve model accuracy, though training will potentially take
   * longer and use more memory.  Model size will also be larger.
   *
   */
  public void setGaussianSigma(double sigmaValue) {
    useGaussianSmoothing = true;
    sigma = sigmaValue;
  }

  /**
   * Trains a GIS model on the event in the specified event stream, using the specified number
   * of iterations and the specified count cutoff.
   * @param eventStream A stream of all events.
   * @param iterations The number of iterations to use for GIS.
   * @param cutoff The number of times a feature must occur to be included.
   * @return A GIS model trained with specified
   */
  public GISModel trainModel(ObjectStream<Event> eventStream, int iterations, int cutoff) throws IOException {
    DataIndexer indexer = new OnePassDataIndexer();
    Map<String, String> params = new HashMap<String, String>();
    params.put(GIS.ITERATIONS_PARAM, Integer.toString(iterations));
    params.put(GIS.CUTOFF_PARAM, Integer.toString(cutoff));
    indexer.init(params, new HashMap<String, String>());
    return trainModel(iterations, indexer, cutoff);
  }

  /**
   * Train a model using the GIS algorithm.
   *
   * @param iterations  The number of GIS iterations to perform.
   * @param di The data indexer used to compress events in memory.
   * @return The newly trained model, which can be used immediately or saved
   *         to disk using an opennlp.tools.ml.maxent.io.GISModelWriter object.
   */
  public GISModel trainModel(int iterations, DataIndexer di, int cutoff) {
    return trainModel(iterations, di, new UniformPrior(), cutoff, 1);
  }

  /**
   * Train a model using the GIS algorithm.
   *
   * @param iterations  The number of GIS iterations to perform.
   * @param di The data indexer used to compress events in memory.
   * @param modelPrior The prior distribution used to train this model.
   * @return The newly trained model, which can be used immediately or saved
   *         to disk using an opennlp.tools.ml.maxent.io.GISModelWriter object.
   */
  public GISModel trainModel(int iterations, DataIndexer di, Prior modelPrior, int cutoff, int threads) {
    if (threads <= 0) {
      throw new IllegalArgumentException("threads must be at least one or greater but is " + threads + "!");
    }
    modelExpects = new MutableContext[threads][];
    display("Incorporating indexed data for training...  \n");
    contexts = di.getContexts();
    values = di.getValues();
    this.cutoff = cutoff;
    predicateCounts = di.getPredCounts();
    numTimesEventsSeen = di.getNumTimesEventsSeen();
    numUniqueEvents = contexts.length;
    this.prior = modelPrior;
    double correctionConstant = 0;
    for (int ci = 0; ci < contexts.length; ci++) {
      if (values == null || values[ci] == null) {
        if (contexts[ci].length > correctionConstant) {
          correctionConstant = contexts[ci].length;
        }
      } else {
        float cl = values[ci][0];
        for (int vi = 1; vi < values[ci].length; vi++) {
          cl += values[ci][vi];
        }
        if (cl > correctionConstant) {
          correctionConstant = cl;
        }
      }
    }
    display("done.\n");
    outcomeLabels = di.getOutcomeLabels();
    outcomeList = di.getOutcomeList();
    numOutcomes = outcomeLabels.length;
    predLabels = di.getPredLabels();
    prior.setLabels(outcomeLabels, predLabels);
    numPreds = predLabels.length;
    display("\tNumber of Event Tokens: " + numUniqueEvents + "\n");
    display("\t    Number of Outcomes: " + numOutcomes + "\n");
    display("\t  Number of Predicates: " + numPreds + "\n");
    float[][] predCount = new float[numPreds][numOutcomes];
    for (int ti = 0; ti < numUniqueEvents; ti++) {
      for (int j = 0; j < contexts[ti].length; j++) {
        if (values != null && values[ti] != null) {
          predCount[contexts[ti][j]][outcomeList[ti]] += numTimesEventsSeen[ti] * values[ti][j];
        } else {
          predCount[contexts[ti][j]][outcomeList[ti]] += numTimesEventsSeen[ti];
        }
      }
    }
    di = null;
    final double smoothingObservation = _smoothingObservation;
    params = new MutableContext[numPreds];
    for (int i = 0; i < modelExpects.length; i++) {
      modelExpects[i] = new MutableContext[numPreds];
    }
    observedExpects = new MutableContext[numPreds];
    evalParams = new EvalParameters(params, 0, 1, numOutcomes);
    int[] activeOutcomes = new int[numOutcomes];
    int[] outcomePattern;
    int[] allOutcomesPattern = new int[numOutcomes];
    for (int oi = 0; oi < numOutcomes; oi++) {
      allOutcomesPattern[oi] = oi;
    }
    int numActiveOutcomes;
    for (int pi = 0; pi < numPreds; pi++) {
      numActiveOutcomes = 0;
      if (useSimpleSmoothing) {
        numActiveOutcomes = numOutcomes;
        outcomePattern = allOutcomesPattern;
      } else {
        for (int oi = 0; oi < numOutcomes; oi++) {
          if (predCount[pi][oi] > 0 && predicateCounts[pi] >= cutoff) {
            activeOutcomes[numActiveOutcomes] = oi;
            numActiveOutcomes++;
          }
        }
        if (numActiveOutcomes == numOutcomes) {
          outcomePattern = allOutcomesPattern;
        } else {
          outcomePattern = new int[numActiveOutcomes];
          System.arraycopy(activeOutcomes, 0, outcomePattern, 0, numActiveOutcomes);
        }
      }
      params[pi] = new MutableContext(outcomePattern, new double[numActiveOutcomes]);
      for (int i = 0; i < modelExpects.length; i++) {
        modelExpects[i][pi] = new MutableContext(outcomePattern, new double[numActiveOutcomes]);
      }
      observedExpects[pi] = new MutableContext(outcomePattern, new double[numActiveOutcomes]);
      for (int aoi = 0; aoi < numActiveOutcomes; aoi++) {
        int oi = outcomePattern[aoi];
        params[pi].setParameter(aoi, 0.0);
        for (MutableContext[] modelExpect : modelExpects) {
          modelExpect[pi].setParameter(aoi, 0.0);
        }
        if (predCount[pi][oi] > 0) {
          observedExpects[pi].setParameter(aoi, predCount[pi][oi]);
        } else {
          if (useSimpleSmoothing) {
            observedExpects[pi].setParameter(aoi, smoothingObservation);
          }
        }
      }
    }
    predCount = null;
    display("...done.\n");
    if (threads == 1) {
      display("Computing model parameters ...\n");
    } else {
      display("Computing model parameters in " + threads + " threads...\n");
    }
    findParameters(iterations, correctionConstant);
    return new GISModel(params, predLabels, outcomeLabels, 1, evalParams.getCorrectionParam());
  }

  private void findParameters(int iterations, double correctionConstant) {
    int threads = modelExpects.length;
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CompletionService<ModelExpactationComputeTask> completionService = new ExecutorCompletionService<>(executor);
    double prevLL = 0.0;
    double currLL;
    display("Performing " + iterations + " iterations.\n");
    for (int i = 1; i <= iterations; i++) {
      if (i < 10) {
        display("  " + i + ":  ");
      } else {
        if (i < 100) {
          display(" " + i + ":  ");
        } else {
          display(i + ":  ");
        }
      }
      currLL = nextIteration(correctionConstant, completionService);
      if (i > 1) {
        if (prevLL > currLL) {
          System.err.println("Model Diverging: loglikelihood decreased");
          break;
        }
        if (currLL - prevLL < LLThreshold) {
          break;
        }
      }
      prevLL = currLL;
    }
    observedExpects = null;
    modelExpects = null;
    numTimesEventsSeen = null;
    contexts = null;
    executor.shutdown();
  }

  private double gaussianUpdate(int predicate, int oid, int n, double correctionConstant) {
    double param = params[predicate].getParameters()[oid];
    double x0 = 0.0;
    double modelValue = modelExpects[0][predicate].getParameters()[oid];
    double observedValue = observedExpects[predicate].getParameters()[oid];
    for (int i = 0; i < 50; i++) {
      double tmp = modelValue * Math.exp(correctionConstant * x0);
      double f = tmp + (param + x0) / sigma - observedValue;
      double fp = tmp * correctionConstant + 1 / sigma;
      if (fp == 0) {
        break;
      }
      double x = x0 - f / fp;
      if (Math.abs(x - x0) < 0.000001) {
        x0 = x;
        break;
      }
      x0 = x;
    }
    return x0;
  }

  private class ModelExpactationComputeTask implements Callable<ModelExpactationComputeTask> {
    private final int startIndex;

    private final int length;

    private double loglikelihood = 0;

    private int numEvents = 0;

    private int numCorrect = 0;

    final private int threadIndex;

    ModelExpactationComputeTask(int threadIndex, int startIndex, int length) {
      this.startIndex = startIndex;
      this.length = length;
      this.threadIndex = threadIndex;
    }

    public ModelExpactationComputeTask call() {
      final double[] modelDistribution = new double[numOutcomes];
      for (int ei = startIndex; ei < startIndex + length; ei++) {
        if (values != null) {
          prior.logPrior(modelDistribution, contexts[ei], values[ei]);
          GISModel.eval(contexts[ei], values[ei], modelDistribution, evalParams);
        } else {
          prior.logPrior(modelDistribution, contexts[ei]);
          GISModel.eval(contexts[ei], modelDistribution, evalParams);
        }
        for (int j = 0; j < contexts[ei].length; j++) {
          int pi = contexts[ei][j];
          if (predicateCounts[pi] >= cutoff) {
            int[] activeOutcomes = modelExpects[threadIndex][pi].getOutcomes();
            for (int aoi = 0; aoi < activeOutcomes.length; aoi++) {
              int oi = activeOutcomes[aoi];
              if (values != null && values[ei] != null) {
                modelExpects[threadIndex][pi].updateParameter(aoi, modelDistribution[oi] * values[ei][j] * numTimesEventsSeen[ei]);
              } else {
                modelExpects[threadIndex][pi].updateParameter(aoi, modelDistribution[oi] * numTimesEventsSeen[ei]);
              }
            }
          }
        }
        loglikelihood += Math.log(modelDistribution[outcomeList[ei]]) * numTimesEventsSeen[ei];
        numEvents += numTimesEventsSeen[ei];
        if (printMessages) {
          int max = 0;
          for (int oi = 1; oi < numOutcomes; oi++) {
            if (modelDistribution[oi] > modelDistribution[max]) {
              max = oi;
            }
          }
          if (max == outcomeList[ei]) {
            numCorrect += numTimesEventsSeen[ei];
          }
        }
      }
      return this;
    }

    synchronized int getNumEvents() {
      return numEvents;
    }

    synchronized int getNumCorrect() {
      return numCorrect;
    }

    synchronized double getLoglikelihood() {
      return loglikelihood;
    }
  }

  private double nextIteration(double correctionConstant, CompletionService<ModelExpactationComputeTask> completionService) {
    double loglikelihood = 0.0;
    int numEvents = 0;
    int numCorrect = 0;
    int numberOfThreads = modelExpects.length;
    int taskSize = numUniqueEvents / numberOfThreads;
    int leftOver = numUniqueEvents % numberOfThreads;
    for (int i = 0; i < numberOfThreads; i++) {
      if (i < leftOver) {
        completionService.submit(new ModelExpactationComputeTask(i, i * taskSize + i, taskSize + 1));
      } else {
        completionService.submit(new ModelExpactationComputeTask(i, i * taskSize + leftOver, taskSize));
      }
    }
    for (int i = 0; i < numberOfThreads; i++) {
      ModelExpactationComputeTask finishedTask;
      try {
        finishedTask = completionService.take().get();
      } catch (InterruptedException e) {
        e.printStackTrace();
        throw new IllegalStateException("Interruption is not supported!", e);
      } catch (ExecutionException e) {
        throw new RuntimeException("Exception during training: " + e.getMessage(), e);
      }
      numEvents += finishedTask.getNumEvents();
      numCorrect += finishedTask.getNumCorrect();
      loglikelihood += finishedTask.getLoglikelihood();
    }
    display(".");
    for (int pi = 0; pi < numPreds; pi++) {
      int[] activeOutcomes = params[pi].getOutcomes();
      for (int aoi = 0; aoi < activeOutcomes.length; aoi++) {
        for (int i = 1; i < modelExpects.length; i++) {
          modelExpects[0][pi].updateParameter(aoi, modelExpects[i][pi].getParameters()[aoi]);
        }
      }
    }
    display(".");
    for (int pi = 0; pi < numPreds; pi++) {
      double[] observed = observedExpects[pi].getParameters();
      double[] model = modelExpects[0][pi].getParameters();
      int[] activeOutcomes = params[pi].getOutcomes();
      for (int aoi = 0; aoi < activeOutcomes.length; aoi++) {
        if (useGaussianSmoothing) {
          params[pi].updateParameter(aoi, gaussianUpdate(pi, aoi, numEvents, correctionConstant));
        } else {
          if (model[aoi] == 0) {
            System.err.println("Model expects == 0 for " + predLabels[pi] + " " + outcomeLabels[aoi]);
          }
          params[pi].updateParameter(aoi, ((Math.log(observed[aoi]) - Math.log(model[aoi])) / correctionConstant));
        }
        for (MutableContext[] modelExpect : modelExpects) {
          modelExpect[pi].setParameter(aoi, 0.0);
        }
      }
    }
    display(". loglikelihood=" + loglikelihood + "\t" + ((double) numCorrect / numEvents) + "\n");
    return loglikelihood;
  }

  private void display(String s) {
    if (printMessages) {
      System.out.print(s);
    }
  }
}