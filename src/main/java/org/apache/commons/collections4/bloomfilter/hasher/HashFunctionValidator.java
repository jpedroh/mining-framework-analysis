package org.apache.commons.collections4.bloomfilter.hasher;

/**
 * Contains validation for hash functions.
 */
public final class HashFunctionValidator {
  /** Do not instantiate. */
  private HashFunctionValidator() {
  }

  /**
     * Compares the identity of the two hash functions. The functions are considered
     * equal if the signedness, process type and name are equal. The name is not
     * case specific.
     *
     * <p>A pair of functions that are equal would be expected to produce the same
     * hash output from the same input.
     *
     * @param a First hash function.
     * @param b Second hash function.
     * @return true, if successful
     * @see String#equalsIgnoreCase(String)
     */
  public static boolean areEqual(HashFunctionIdentity a, HashFunctionIdentity b) {
    return (a.getSignedness() == b.getSignedness() && a.getProcessType() == b.getProcessType() && a.getName().equalsIgnoreCase(b.getName()));
  }

  /**
     * Compares the identity of the two hash functions and throws an exception if they
     * are not equal.
     *
     * @param a First hash function.
     * @param b Second hash function.
     * @see #areEqual(HashFunctionIdentity, HashFunctionIdentity)
     * @throws IllegalArgumentException if the hash functions are not equal
     */
  public static void checkAreEqual(HashFunctionIdentity a, HashFunctionIdentity b) {
    if (!areEqual(a, b)) {
      throw new IllegalArgumentException(String.format("Hash functions are not equal: (%s) != (%s)", HashFunctionIdentity.asCommonString(a), HashFunctionIdentity.asCommonString(b)));
    }
  }
}