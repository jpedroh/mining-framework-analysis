package org.apache.commons.collections4.bloomfilter.hasher;
import java.util.Locale;
import java.util.Objects;

/**
 * Contains validation for hash functions.
 */
public final class HashFunctionValidator {
  /** Do not instantiate. */
  private HashFunctionValidator() {
  }

  /**
     * Generates a hash code for the identity of the hash function. The hash code is
     * generated using the same properties as those tested in
     * {@link #areEqual(HashFunctionIdentity, HashFunctionIdentity)}, that is the
     * signedness, process type and name. The name is not case specific and is converted
     * to lower-case using the {@link Locale#ROOT root locale}.
     *
     * <p>The generated value is suitable for use in generation of a hash code that satisfies
     * the contract of {@link Object#hashCode()} if the {@link Object#equals(Object)} method
     * is implemented using {@link #areEqual(HashFunctionIdentity, HashFunctionIdentity)}. That
     * is two objects considered equal will have the same hash code.
     *
     * <p>If the hash function identity is a field within a larger object the generated hash code
     * should be incorporated into the entire hash, for example using
     * {@link Objects#hash(Object...)}.
     *
     * @param a hash function.
     * @return hash code
     * @see String#toLowerCase(Locale)
     * @see Locale#ROOT
     */
  static int hash(HashFunctionIdentity a) {
    return Objects.hash(a.getSignedness(), a.getProcessType(), a.getName().toLowerCase(Locale.ROOT));
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