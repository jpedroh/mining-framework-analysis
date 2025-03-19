package org.unigram.docvalidator.store;
import java.util.Iterator;
import java.util.List;

/**
 * Element of List in semi-structured text format such as wiki.
 */
public final class ListElement {
  /**
   * Constructor.
   *
   * @param listLevel    indentation level
   * @param listContents content of list element
   */
  public ListElement(int listLevel, List<Sentence> listContents) {
    super();
    this.level = listLevel;
    this.contents = listContents;
  }

  /**
   * Get content of list element.
   *
   * @return all contents of list element
   */
  public Iterator<Sentence> getSentences() {
    return contents.iterator();
  }

  /**
   * Get specified content
   *
   * @return content of list element
   */
  public Sentence getSentence(int id) {
    return contents.get(id);
  }

  /**
   * Get number of content sentence
   *
   * @return number of sentences in the list item
   */
  public int getNumberOfSentences() {
    return contents.size();
  }

  /**
   * Get indentation level.
   *
   * @return indentation level
   */
  public int getLevel() {
    return level;
  }

  private final List<Sentence> contents;

  private final int level;
}