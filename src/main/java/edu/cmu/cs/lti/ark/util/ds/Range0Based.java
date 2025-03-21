package edu.cmu.cs.lti.ark.util.ds;
import com.google.common.collect.ComparisonChain;

/**
 * A range of values whose smallest normal index is 0. (Negative values may be used for "non-normal" indices.) 
 * This is to distinguish 0-based ranges from 1-based ranges when both are used.
 * @author Nathan Schneider (nschneid)
 * @since 2009-06-25
 */
public class Range0Based extends Range implements Comparable<Range0Based> {
  /**
	 * Converts a 1-based range to a 0-based range by subtracting 1 from the start and end indices
	 * @param r A 1-based range
	 */
  public Range0Based(Range1Based r) {
    this(r.start - 1, r.end - 1, r.isEndInclusive());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
	 * Creates a new range with start and end indices computed relative to those of an existing range.
	 * @param r Range serving as a point of reference
	 * @param deltaStart Amount to add to the start position in the provided range
	 * @param deltaEnd Amount to add to the end position in the provided range
	 */
  public Range0Based(Range r, int deltaStart, int deltaEnd) {
    this(r.start + deltaStart, r.end + deltaEnd, r.isEndInclusive());
  }
>>>>>>> /usr/src/app/output/sammthomson/semafor/1b62f9ce9b3c32ea3e1c737df5c9acf3c23d27e1/src/main/java/edu/cmu/cs/lti/ark/util/ds/Range0Based.java/right.java


  public Range0Based(int startPosition, int endPosition) {
    super(0, startPosition, endPosition);
  }

  public Range0Based(int startPosition, int endPosition, boolean isEndInclusive) {
    super(0, startPosition, endPosition, isEndInclusive);
  }

  private int endIdx() {
    return end + (isEndInclusive() ? 1 : 0);
  }

  /** Determines whether this and the other span overlap */
  public boolean overlaps(Range0Based other) {
    if (isEmpty() || other.isEmpty()) {
      return false;
    }
    if (start < other.start) {
      return endIdx() > other.start;
    } else {
      return other.endIdx() > start;
    }
  }

  public Range0Based clone() {
    return new Range0Based(this.start, this.end, this.endInclusive);
  }

  @Override public int compareTo(Range0Based other) {
    return ComparisonChain.start().compare(start, other.start).compare(length(), other.length()).result();
  }
}