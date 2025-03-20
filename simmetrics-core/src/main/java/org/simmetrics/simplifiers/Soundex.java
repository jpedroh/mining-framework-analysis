package org.simmetrics.simplifiers;
import static org.apache.commons.codec.language.Soundex.US_ENGLISH;

/**
 * Encodes a string into a Soundex value. Soundex is an encoding used to relate
 * similar names, but can also be used as a general purpose scheme to find word
 * with similar phonemes.
 *
 * This class is thread-safe and immutable.
 * 
 * @see org.apache.commons.codec.language.Soundex
 *
 */
public class Soundex implements Simplifier {
  @Override public String toString() {
    return 
<<<<<<< /usr/src/app/output/simmetrics/simmetrics/5f973be314a72f132d668ea0500013139b0481de/simmetrics-core/src/main/java/org/simmetrics/simplifiers/Soundex.java/left.java
    "Soundex"
=======
    "SoundexSimplifier"
>>>>>>> /usr/src/app/output/simmetrics/simmetrics/5f973be314a72f132d668ea0500013139b0481de/simmetrics-core/src/main/java/org/simmetrics/simplifiers/Soundex.java/right.java
    ;
  }

  @Override public String simplify(String input) {
    return US_ENGLISH.soundex(input);
  }
}