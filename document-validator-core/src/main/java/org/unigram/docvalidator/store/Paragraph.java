package org.unigram.docvalidator.store;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represent a paragraph of text.
 */
public final class Paragraph implements Block {
  /**
   * Constructor.
   */
  public Paragraph() {
    super();
    sentences = new ArrayList<Sentence>();
  }

  /**
   * Get the iterator of sentences.
   *
   * @return sentences in the paragraph
   */
  public Iterator<Sentence> getSentences() {
    return sentences.iterator();
  }

  /**
   * Get the specified sentence.
   *
   * @param sentenceNumber sentence number
   * @return sentence with specified id
   */
  public Sentence getSentence(int sentenceNumber) {
    return sentences.get(sentenceNumber);
  }

  /**
   * Append a sentence to a paragraph.
   *
   * @param content sentence
   * @param lineNum line number of sentence
   */
  public void appendSentence(String content, int lineNum) {
    sentences.add(new Sentence(content, lineNum));
  }

  /**
   * Append a sentence.
   *
   * @param sentence a sentence to be added to paragraph
   */
  public void appendSentence(Sentence sentence) {
    sentences.add(sentence);
  }

  /**
   * Get the number of sentences in the paragraph.
   *
   * @return sentences number
   */
  public int getNumberOfSentences() {
    return sentences.size();
  }

  public int getBlockID() {
    return BlockTypes.PARAGRAPH;
  }

  private final List<Sentence> sentences;
}