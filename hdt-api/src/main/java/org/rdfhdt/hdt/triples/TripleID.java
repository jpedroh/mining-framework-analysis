package org.rdfhdt.hdt.triples;
import java.io.Serializable;
import org.rdfhdt.hdt.util.LongCompare;

/**
 * TripleID holds a triple using Long IDs
 *
 */
public final class TripleID implements Comparable<TripleID>, Serializable {
  private static final long serialVersionUID = -4685524566493494912L;

  private long subject;

  private long predicate;

  private long object;

  /**
	 * Basic constructor
	 */
  public TripleID() {
    super();
  }

  /**
	 * Constructor
	 *
	 * @param subject
	 *            The subject
	 * @param predicate
	 *            The predicate
	 * @param object
	 *            The object
	 */
  public TripleID(long subject, long predicate, long object) {
    super();
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  /**
	 * Build a TripleID as a copy of another one.
	 * @param other
	 */
  public TripleID(TripleID other) {
    super();
    this.subject = other.subject;
    this.predicate = other.predicate;
    this.object = other.object;
  }

  /**
	 * @return the subject
	 */
  public long getSubject() {
    return subject;
  }

  /**
	 * @param subject
	 *            the subject to set
	 */
  public void setSubject(long subject) {
    this.subject = subject;
  }

  /**
	 * @return the object
	 */
  public long getObject() {
    return object;
  }

  /**
	 * @param object
	 *            the object to set
	 */
  public void setObject(long object) {
    this.object = object;
  }

  /**
	 * @return the predicate
	 */
  public long getPredicate() {
    return predicate;
  }

  /**
	 * @param predicate
	 *            the predicate to set
	 */
  public void setPredicate(long predicate) {
    this.predicate = predicate;
  }

  /**
	 * Replace all components of a TripleID at once. Useful to reuse existing objects.
	 * @param subject
	 * @param predicate
	 * @param object
	 */
  public void setAll(long subject, long predicate, long object) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  public void assign(TripleID replacement) {
    subject = replacement.getSubject();
    object = replacement.getObject();
    predicate = replacement.getPredicate();
  }

  /**
	 * Set all components to zero.
	 */
  public void clear() {
    subject = predicate = object = 0;
  }

  @Override public String toString() {
    return Long.toString(subject) + " " + predicate + " " + object;
  }

  /**
	 * Compare TripleID to another one using SPO Order.
	 * To compare using other orders use @see org.rdfhdt.hdt.triples.TripleStringComparator
	 */
  @Override public int compareTo(TripleID other) {
    int result = LongCompare.compare(this.subject, other.subject);
    if (result == 0) {
      result = LongCompare.compare(this.predicate, other.predicate);
      if (result == 0) {
        return LongCompare.compare(this.object, other.object);
      } else {
        return result;
      }
    } else {
      return result;
    }
  }

  /**
	 * Check whether this triple matches a pattern of TripleID. 0 acts as a wildcard
	 *
	 * @param pattern
	 *            The pattern to match against
	 * @return boolean
	 */
  public boolean match(TripleID pattern) {
    long subjectPattern = pattern.getSubject();
    long predicatePattern = pattern.getPredicate();
    long objectPattern = pattern.getObject();
    if (subjectPattern == 0 || this.subject == subjectPattern) {
      if (predicatePattern == 0 || this.predicate == predicatePattern) {
        if (objectPattern == 0 || this.object == objectPattern) {
          return true;
        }
      }
    }
    return false;
  }

  /**
	 * Check whether all the components of the triple are empty (zero).
	 */
  public boolean isEmpty() {
    return !(subject != 0 || predicate != 0 || object != 0);
  }

  /**
	 * Check whether none of the components of the triple are empty.
	 */
  public boolean isValid() {
    return subject > 0 && predicate > 0 && object > 0;
  }

  /**
	 * Checks whether any of the components of the triple are "no match" (-1).
	 */
  public boolean isNoMatch() {
    return subject == -1 || predicate == -1 || object == -1;
  }

  /**
	 * Get the pattern of the triple as String, such as "SP?".
	 */
  public String getPatternString() {
    return "" + (subject == 0 ? '?' : 'S') + (predicate == 0 ? '?' : 'P') + (object == 0 ? '?' : 'O');
  }

  /** size of one TripleID in memory */
  public static int size() {
    return 48;
  }

  @Override public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TripleID)) {
      return false;
    }
    TripleID other = (TripleID) o;
    return !(subject != other.subject || predicate != other.predicate || object != other.object);
  }

  @Override public int hashCode() {
    return (int) (subject * 13 + predicate * 17 + object * 31);
  }
}