package de.uni_koblenz.jgralab;
import java.util.Random;

/**
 * RandonIdGenerator creates 128 bit random IDs as string consisting of 4
 * hexadecimal numbers.
 * 
 * @author ist@uni-koblenz.de
 */
public class RandomIdGenerator {
  private static Random rand = new Random();

  /**
	 * Creates 128 random id, encoded in hexadecimal string representation.
	 * 
	 * @return a random id
	 */
  public static String generateId() {
    return Integer.toHexString(rand.nextInt()) + "-" + Integer.toHexString(rand.nextInt()) + "-" + Integer.toHexString(rand.nextInt()) + "-" + Integer.toHexString(rand.nextInt());
  }
}