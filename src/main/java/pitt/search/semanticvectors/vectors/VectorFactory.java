package pitt.search.semanticvectors.vectors;
import java.util.Random;
import pitt.search.semanticvectors.vectors.ComplexVector.Mode;

/**
 * Class for building vectors, designed to be used externally.
 * 
 * @author Dominic Widdows
 */
public class VectorFactory {
  private static final BinaryVector binaryInstance = new BinaryVector(0);

  private static final RealVector realInstance = new RealVector(0);

  private static final BipolarVector bipolarInstance = new BipolarVector(0);

  private static final ComplexVector complexInstance = new ComplexVector(0, ComplexVector.Mode.POLAR_SPARSE);

  private static final ComplexVector complexFlatInstance = new ComplexVector(0, ComplexVector.Mode.CARTESIAN);

  /**
   * createZeroVector returns a vector set to zero. It can be used externally, but
   * be careful particularly with Complex Vectors to make sure that polar / cartesian / hermitian
   * modes are set correctly especially if you try to set coordinates manually after that.
   */
  public static Vector createZeroVector(VectorType type, int dimension) {
    switch (type) {
      case BINARY:
      return new BinaryVector(dimension);
      case REAL:
      return new RealVector(dimension);
      case COMPLEX:
      return new ComplexVector(dimension, Mode.POLAR_SPARSE);
      case COMPLEXFLAT:
      return new ComplexVector(dimension, Mode.POLAR_SPARSE);
      case PERMUTATION:
      return new PermutationVector(dimension);
      default:
      throw new IllegalArgumentException("Unrecognized VectorType: " + type);
    }
  }

  /**
   * Generates an appropriate random vector.
   * 
   * @param type one of the recognized vector types
   * @param dimension number of dimensions in the generated vector
   * @param numEntries total number of non-zero entries; must be no greater than half of dimension
   * @param random random number generator; passed in to enable deterministic testing
   * @return vector generated with appropriate type, dimension and number of nonzero entries
   */
  public static Vector generateRandomVector(VectorType type, int dimension, int numEntries, Random random) {
    if (2 * numEntries > dimension && !type.equals(VectorType.COMPLEX) && !(numEntries == dimension)) {
      throw new RuntimeException("Requested " + numEntries + " to be filled in sparse " + "vector of dimension " + dimension + ". This is not sparse and may cause problems.");
    }
    switch (type) {
      case BINARY:
      return binaryInstance.generateRandomVector(dimension, numEntries, random);
      case BIPOLAR:
      return bipolarInstance.generateRandomVector(dimension, numEntries, random);
      case REAL:
      return realInstance.generateRandomVector(dimension, numEntries, random);
      case PERMUTATION:
      return new PermutationVector(PermutationUtils.getRandomPermutation(VectorType.REAL, dimension));
      case COMPLEX:
      if (!ComplexVector.getDominantMode().equals(Mode.HERMITIAN)) {
        ComplexVector.setDominantMode(Mode.POLAR_DENSE);
      }
      return complexInstance.generateRandomVector(dimension, numEntries, random);
      case COMPLEXFLAT:
      ComplexVector.setDominantMode(Mode.CARTESIAN);
      return complexInstance.generateRandomVector(dimension, numEntries, random);
      default:
      throw new IllegalArgumentException("Unrecognized VectorType: " + type);
    }
  }

  /**
   * Returns the size in bytes expected to be taken up by the serialization
   * of this vector in Lucene format.
   */
  public static int getLuceneByteSize(VectorType vectorType, int dimension) {
    switch (vectorType) {
      case BINARY:
      return 8 * ((dimension / 64));
      case REAL:
      return 4 * dimension;
      case COMPLEX:
      case COMPLEXFLAT:
      return 8 * dimension;
      default:
      throw new IllegalArgumentException("Unrecognized VectorType: " + vectorType);
    }
  }
}