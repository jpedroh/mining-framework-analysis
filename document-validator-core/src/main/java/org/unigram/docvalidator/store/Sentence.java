package org.unigram.docvalidator.store;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentence block in a Document.
 */
public final class Sentence implements Block {
  /**
   * Constructor.
   *
   * @param sentenceContent  content of sentence
   * @param sentencePosition sentence position
   */
  public Sentence(String sentenceContent, int sentencePosition) {
    super();
    this.content = sentenceContent;
    this.position = sentencePosition;
    this.isFirstSentence = false;
    this.links = new ArrayList<String>();
  }

  public int getBlockID() {
    return 0;
  }

  /**
   * Content of string.
   */
  public String content;

  /**
   * Sentence position in a file.
   */
  public int position;

  /**
   * First sentence in a paragraph.
   */
  public boolean isFirstSentence;

  /**
   * Links (including internal and external ones)
   */
  public final List<String> links;
}