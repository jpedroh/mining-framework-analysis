package org.movsim.utilities;
import java.util.Random;

/**
 * The Class MyRandom.
 */
public final class MyRandom {
  private static Random rand = new Random();

  private MyRandom() {
    throw new IllegalStateException("do not instanciate");
  }

  public static void initializeWithSeed(long randomSeed) {
    rand = new Random(randomSeed);
  }

  public static boolean isInitialized() {
    return rand != null;
  }

  /**
     * Next int.
     * @return the int
     */
  public static int nextInt() {
    return rand.nextInt();
  }

  public static int nextInt(int n) {
    return rand.nextInt(n);
  }

  /**
     * Next double.
     * @return the double
     */
  public static double nextDouble() {
    return rand.nextDouble();
  }

  /**
     * returns a realization of a uniformly distributed random variable in [-1, 1]
     * @return a uniformly distributed realization in [-1, 1]
     */
  public static double getUniformDistribution() {
    return 2 * MyRandom.nextDouble() - 1;
  }

  public static double getUniformlyDistributedRandomizedFactor(double randomizationStrength) {
    return 1 + randomizationStrength * getUniformDistribution();
  }

  public static double getGaussiansDistributedRandomizedFactor(double sigma, double nSigmaCutoff) {
    return 1 + Math.max(-nSigmaCutoff * sigma, Math.min(nSigmaCutoff, sigma * rand.nextGaussian()));
  }
}