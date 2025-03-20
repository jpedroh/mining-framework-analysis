package org.simmetrics.simplifiers;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.codec.language.RefinedSoundex.US_ENGLISH;

/**
 * Encodes a string into a Refined Soundex value. A refined soundex code is
 * optimized for spell checking words. Soundex method originally developed by
 * <cite>Margaret Odell</cite> and <cite>Robert Russell</cite>.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @see org.apache.commons.codec.language.RefinedSoundex
 * 
 * @deprecated will be removed due to a lack of a good use case
 */
@Deprecated public final class RefinedSoundex implements Simplifier {
  @Override public String simplify(String input) {
    checkNotNull(input);
    return US_ENGLISH.soundex(input);
  }

  @Override public String toString() {
    return "RefinedSoundex";
  }
}