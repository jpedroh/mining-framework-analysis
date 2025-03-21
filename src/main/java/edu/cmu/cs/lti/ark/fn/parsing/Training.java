package edu.cmu.cs.lti.ark.fn.parsing;
import edu.cmu.cs.lti.ark.ml.optimization.Lbfgs;
import edu.cmu.cs.lti.ark.fn.utils.FNModelOptions;
import edu.cmu.cs.lti.ark.fn.utils.ThreadPool;
import edu.cmu.cs.lti.ark.util.FileUtil;
import edu.cmu.cs.lti.ark.util.ds.Pair;
import riso.numerical.LBFGS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import static edu.cmu.cs.lti.ark.util.SerializedObjects.readObject;
import static java.lang.Math.log;
import static java.lang.Math.min;

public class Training {
  private final String modelFile;

  private final ArrayList<FrameFeatures> frameFeaturesList;

  private final double[] weights;

  private final double[] gradients;

  private final double lambda;

  private final int numThreads;


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private List<String> mFrameLines;
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/right.java


  private final double[][] threadGradients;

  private final double[] threadObjectives;

  /**
	 * @param args command-line arguments as follows:
	 *   frameFeaturesCacheFile: path to file containing a serialized cache of all of the features
	 *       extracted from the training data
	 *   alphabetFile: path to file containing the alphabet
	 *   l2Strength: L2 regularization hyperparameter
	 *   numThreads: the number of parallel threads to run while optimizing
	 *   modelFile: path to output file to write resulting model to. intermediate models will be written to
	 *       modelFile + "_" + i
	 */
  public static void main(String[] args) throws Exception {
    final FNModelOptions opts = new FNModelOptions(args);
    final String modelFile = opts.modelFile.get();
    final String alphabetFile = opts.alphabetFile.get();
    final String frameFeaturesCacheFile = opts.frameFeaturesCacheFile.get();
    final double lambda = opts.l2Strength.get();
    final int numThreads = opts.numThreads.get();
    final ArrayList<FrameFeatures> frameFeaturesList = readObject(frameFeaturesCacheFile);
    final Training training = new Training(modelFile, alphabetFile, frameFeaturesList, lambda, numThreads);
    training.runCustomLBFGS();
  }

  public Training(String modelFile, String alphabetFile, ArrayList<FrameFeatures> frameFeaturesList, double lambda, int numThreads) {
    this.modelFile = modelFile;
    this.frameFeaturesList = frameFeaturesList;
    this.lambda = lambda;
    this.numThreads = numThreads;
    int numFeatures = readNumFeatures(alphabetFile);
    weights = new double[numFeatures];
    gradients = new double[numFeatures];
    threadGradients = new double[numThreads][numFeatures];
    threadObjectives = new double[numThreads];
  }

  private int readNumFeatures(String alphabetFile) {
    final Scanner scanner = FileUtil.openInFile(alphabetFile);
    final int numFeatures = scanner.nextInt() + 1;
    scanner.close();
    return numFeatures;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void init(String modelFile, String alphabetFile, ArrayList<FrameFeatures> list, String frFile) throws IOException {
    mModelFile = modelFile;
    mAlphabetFile = alphabetFile;
    initModel();
    mFrameList = list;
    mFrameLines = ParsePreparation.readLines(frFile);
    rand = new Random(new Date().getTime());
    mLambda = 0.0;
    numDataPoints = mFrameList.size();
    mNumThreads = 1;
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/right.java


  private Pair<Double, double[]> getObjectiveAndGradient(FrameFeatures ffs) {
    final int modelSize = weights.length;
    final List<SpanAndFeatures[]> featsList = ffs.fElementSpansAndFeatures;
    final List<Integer> goldSpans = ffs.goldSpanIdxs;
    final double[] gradients = new double[modelSize];
    double value = 0.0;
    for (int i = 0; i < featsList.size(); i++) {
      SpanAndFeatures[] featureArray = featsList.get(i);
      int goldSpan = goldSpans.get(i);
      int featArrLen = featureArray.length;
      double weiFeatSum[] = new double[featArrLen];
      double exp[] = new double[featArrLen];
      double sumExp = 0.0;
      for (int j = 0; j < featArrLen; j++) {
        weiFeatSum[j] = weights[0];
        int[] feats = featureArray[j].features();
        for (int feat : feats) {
          if (feat == 0) {
            continue;
          }
          double weight = weights[feat];
          weiFeatSum[j] += weight;
        }
        exp[j] = Math.exp(weiFeatSum[j]);
        sumExp += exp[j];
      }
      value -= log(exp[goldSpan] / sumExp);
      double YMinusP[] = new double[featureArray.length];
      for (int j = 0; j < featArrLen; j++) {
        int Y = 0;
        if (j == goldSpan) {
          Y = 1;
        }
        int[] feats = featureArray[j].features();
        YMinusP[j] = Y - exp[j] / sumExp;
        gradients[0] -= YMinusP[j];
        for (int feat : feats) {
          gradients[feat] -= YMinusP[j];
        }
      }
    }
    return Pair.of(value, gradients);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void init(String modelFile, String alphabetFile, ArrayList<FrameFeatures> list, String frFile, String reg, double lambda, int numThreads) throws IOException {
    mModelFile = modelFile;
    mAlphabetFile = alphabetFile;
    initModel();
    mFrameList = list;
    mFrameLines = ParsePreparation.readLines(frFile);
    rand = new Random(new Date().getTime());
    mLambda = lambda;
    numDataPoints = mFrameList.size();
    mNumThreads = numThreads;
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  public void trainSGA(int TOTAL_PASSES, int batchsize) {
    int sizeOfData = mFrameList.size();
    int maxUpdates = (int) (((double) TOTAL_PASSES * (double) sizeOfData) / (double) batchsize);
    int totalUpdates = 0;
    int countPasses = 0;
    int countDataEncountered = 0;
    System.out.println("Max updates:" + maxUpdates);
    double[] sumDers = new double[W.length];
    while (totalUpdates < maxUpdates) {
      int[] arr = getRandArray(batchsize, sizeOfData, rand);
      Arrays.fill(sumDers, 0.0);
      for (int j = 0; j < arr.length; j++) {
        int sampleIndex = arr[j];
        System.out.println("Sample index:" + sampleIndex);
        Pair<Double, double[]> p = getDerivativesOfSample(sumDers, sampleIndex);
        sumDers = p.second;
      }
      countDataEncountered += batchsize;
      W = SGA.updateGradient(W, sumDers, 0.1);
      System.out.println("Performed update number:" + totalUpdates);
      totalUpdates++;
      if (countDataEncountered >= sizeOfData) {
        System.out.println("\nCompleted pass number:" + (countPasses + 1) + " total updates till now:" + totalUpdates);
        writeModel(mModelFile + "_" + countPasses);
        countPasses++;
        countDataEncountered = 0;
      }
    }
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/right.java


  public void processBatch(int taskID, int start, int end) {
    final int threadID = taskID % numThreads;
    System.out.println("Processing batch:" + taskID + " thread ID:" + threadID);
    final int safeEnd = min(end, frameFeaturesList.size());
    for (int index = start; index < safeEnd; index++) {
      final FrameFeatures ffs = frameFeaturesList.get(index);
      final Pair<Double, double[]> objAndGrad = getObjectiveAndGradient(ffs);

<<<<<<< /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/left.java
      final double objective = objAndGrad.first;
=======
      sumDers = p.second;
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/right.java

      final double[] gradient = objAndGrad.second;
      threadObjectives[threadID] += 
<<<<<<< /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/left.java
      objective
=======
      p.first
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/fn/parsing/Training.java/right.java
      ;
      for (int i = 0; i < weights.length; i++) {
        threadGradients[threadID][i] += gradient[i];
      }
    }
  }

  public Runnable createTask(final int count, final int start, final int end) {
    return new Runnable() {
      public void run() {
        System.out.println("Task " + count + " : start");
        processBatch(count, start, end);
        System.out.println("Task " + count + " : end");
      }
    };
  }

  /**
	 * @return the value of the function. fills out gradients as a side-effect
	 */
  private double getValuesAndGradients() {
    double value = 0.0;
    Arrays.fill(gradients, 0.0);
    for (int i = 0; i < numThreads; i++) {
      Arrays.fill(threadGradients[i], 0.0);
    }
    Arrays.fill(threadObjectives, 0.0);
    ThreadPool threadPool = new ThreadPool(numThreads);
    int batchSize = 10;
    int count = 0;
    for (int i = 0; i < frameFeaturesList.size(); i = i + batchSize) {
      threadPool.runTask(createTask(count, i, i + batchSize));
      count++;
    }
    threadPool.join();
    for (int i = 0; i < numThreads; i++) {
      value += threadObjectives[i];
      for (int j = 0; j < weights.length; j++) {
        gradients[j] += threadGradients[i][j];
      }
    }
    for (int i = 0; i < weights.length; i++) {
      gradients[i] += 2 * lambda * weights[i];
      value += lambda * weights[i] * weights[i];
    }
    System.out.println("Finished value and gradient computation.");
    return value;
  }

  public void runCustomLBFGS() throws Exception {
    int modelSize = weights.length;
    double[] diagco = new double[modelSize];
    int[] iprint = { Lbfgs.DEBUG ? 1 : -1, 0 };
    int[] iflag = { 0 };
    int iteration = 0;
    do {
      System.out.println("Starting iteration:" + iteration);
      double m_value = getValuesAndGradients();
      System.out.println("Function value:" + m_value);
      LBFGS.lbfgs(modelSize, Lbfgs.NUM_CORRECTIONS, weights, m_value, gradients, false, diagco, iprint, Lbfgs.STOPPING_THRESHOLD, Lbfgs.XTOL, iflag);
      System.out.println("Finished iteration:" + iteration);
      iteration++;
      if (iteration % Lbfgs.SAVE_EVERY_K == 0) {
        writeModel(modelFile + "_" + iteration);
      }
    } while(iteration <= Lbfgs.MAX_ITERATIONS && iflag[0] != 0);
    writeModel(modelFile);
  }

  public void writeModel(String modelFile) throws FileNotFoundException {
    final PrintStream ps = new PrintStream(new FileOutputStream(modelFile));
    System.out.println("Writing Model... ...");
    for (double w : weights) {
      ps.println(w);
    }
    System.out.println("Finished Writing Model");
    ps.close();
  }
}