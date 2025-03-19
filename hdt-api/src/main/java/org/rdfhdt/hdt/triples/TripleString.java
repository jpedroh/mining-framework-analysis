package org.rdfhdt.hdt.triples;
import java.io.IOException;
import org.rdfhdt.hdt.exceptions.ParserException;
import org.rdfhdt.hdt.util.UnicodeEscape;

/**
 * TripleString holds a triple as Strings
 */
public class TripleString {
  private CharSequence subject;

  private CharSequence predicate;

  private CharSequence object;

  public TripleString() {
    this.subject = this.predicate = this.object = null;
  }

  /**
	 * Basic constructor
	 * 
	 * @param subject
	 *            The subject
	 * @param predicate
	 *            The predicate
	 * @param object
	 *            The object
	 */
  public TripleString(CharSequence subject, CharSequence predicate, CharSequence object) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  /**
	 * Copy constructor
	 */
  public TripleString(TripleString other) {
    this.subject = other.subject;
    this.predicate = other.predicate;
    this.object = other.object;
  }

  /**
	 * @return the subject
	 */
  public CharSequence getSubject() {
    return subject;
  }

  /**
	 * @param subject
	 *            the subject to set
	 */
  public void setSubject(CharSequence subject) {
    this.subject = subject;
  }

  /**
	 * @return the predicate
	 */
  public CharSequence getPredicate() {
    return predicate;
  }

  /**
	 * @param predicate
	 *            the predicate to set
	 */
  public void setPredicate(CharSequence predicate) {
    this.predicate = predicate;
  }

  /**
	 * @return the object
	 */
  public CharSequence getObject() {
    return object;
  }

  /**
	 * @param object
	 *            the object to set
	 */
  public void setObject(CharSequence object) {
    this.object = object;
  }

  /**
	 * Sets all components at once. Useful to reuse existing object instead of creating new ones for performance.
	 * @param subject
	 * @param predicate
	 * @param object
	 */
  public void setAll(CharSequence subject, CharSequence predicate, CharSequence object) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  public boolean equals(TripleString other) {
    return !(!subject.equals(other.subject) || !predicate.equals(other.predicate) || !object.equals(other.object));
  }

  /**
	 * Check whether this triple matches a pattern. A pattern is just a TripleString where each empty component means <em>any</em>.
	 * @param pattern
	 * @return
	 */
  public boolean match(TripleString pattern) {
    if (pattern.getSubject() == "" || pattern.getSubject().equals(this.subject)) {
      if (pattern.getPredicate() == "" || pattern.getPredicate().equals(this.predicate)) {
        if (pattern.getObject() == "" || pattern.getObject().equals(this.object)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
	 * Set all components to ""
	 */
  public void clear() {
    subject = predicate = object = "";
  }

  /**
	 * Checks whether all components are empty.
	 * @return
	 */
  public boolean isEmpty() {
    return subject.length() == 0 && predicate.length() == 0 && object.length() == 0;
  }

  /**
	 * Checks whether any component is empty.
	 * @return
	 */
  public boolean hasEmpty() {
    return subject.length() == 0 || predicate.length() == 0 || object.length() == 0;
  }

  /**
	 * Read from a line, where each component is separated by space.
	 * @param line
	 */
  public void read(String line) throws ParserException {
    int split, posa, posb;
    this.clear();
    line = line.replace("\\t", " ");
    posa = 0;
    posb = split = line.indexOf(' ', posa);
    if (posb == -1) {
      return;
    }
    if (line.charAt(posa) == '<') {
      posa++;
    }
    if (line.charAt(posb - 1) == '>') {
      posb--;
    }
    this.setSubject(UnicodeEscape.unescapeString(line.substring(posa, posb)));
    posa = split + 1;
    posb = split = line.indexOf(' ', posa);
    if (posb == -1) {
      return;
    }
    if (line.charAt(posa) == '<') {
      posa++;
    }
    if (posb > posa && line.charAt(posb - 1) == '>') {
      posb--;
    }
    this.setPredicate(UnicodeEscape.unescapeString(line.substring(posa, posb)));
    posa = split + 1;
    posb = line.length();
    if (line.charAt(posb - 1) == '.') {
      posb--;
    }
    if (line.charAt(posb - 1) == ' ') {
      posb--;
    }
    if (line.charAt(posa) == '<') {
      posa++;
      if (posb > posa && line.charAt(posb - 1) == '>') {
        posb--;
      }
    }
    this.setObject(UnicodeEscape.unescapeString(line.substring(posa, posb)));
  }

  @Override public String toString() {
    return subject + " " + predicate + " " + object;
  }

  /** Convert TripleString to NTriple */
  public CharSequence asNtriple() throws IOException {
    StringBuilder str = new StringBuilder();
    this.dumpNtriple(str);
    return str;
  }

  public final void dumpNtriple(Appendable out) throws IOException {
    char s0 = subject.charAt(0);
    if (s0 == '_' || s0 == '<') {
      out.append(subject);
    } else {
      out.append('<').append(subject).append('>');
    }
    char p0 = predicate.charAt(0);
    if (p0 == '<') {
      out.append(' ').append(predicate).append(' ');
    } else {
      out.append(" <").append(predicate).append("> ");
    }
    char o0 = object.charAt(0);
    if (o0 == '\"') {
      UnicodeEscape.escapeString(object.toString(), out);
      out.append(" .\n");
    } else {
      if (o0 == '_' || o0 == '<') {
        out.append(object).append(" .\n");
      } else {
        out.append('<').append(object).append("> .\n");
      }
    }
  }
}