package edu.cmu.cs.lti.ark.util.ds;
import com.google.common.collect.ComparisonChain;

/**
 * A range of values whose smallest legal index is 1. Setting the start or end position to 0 is an error. 
 * (Negative values may be used for "non-normal" indices.) 
 * This is to distinguish 1-based ranges from 0-based ranges when both are used.
 * @author Nathan Schneider (nschneid)
 * @since 2009-06-25
 */
public class Range1Based extends Range implements Comparable<Range1Based> {
  /**
	 * Converts a 0-based range to a 1-based range by adding 1 to the start and end indices
	 * @param r A 0-based range
	 */
  public Range1Based(Range0Based r) {
    this(r.start + 1, r.end + 1, r.isEndInclusive());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * Creates a new range with start and end indices computed relative to those of an existing range.
	 * @param r Range serving as a point of reference
	 * @param deltaStart Amount to add to the start position in the provided range
	 * @param deltaEnd Amount to add to the end position in the provided range
	 */
  public Range1Based(Range r, int deltaStart, int deltaEnd) {
    this(r.start + deltaStart, r.end + deltaEnd, r.isEndInclusive());
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/util/ds/Range1Based.java/right.java


  public Range1Based(int startPosition, int endPosition) {
    super(1, startPosition, endPosition);
  }

  public Range1Based(int startPosition, int endPosition, boolean isEndInclusive) {
    super(1, startPosition, endPosition, isEndInclusive);
  }

  public Range1Based clone() {
    return new Range1Based(this.start, this.end, this.endInclusive);
  }

  @Override public int compareTo(Range1Based other) {
    return ComparisonChain.start().compare(start, other.start).compare(length(), other.length()).result();
  }
}