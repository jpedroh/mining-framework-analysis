package org.unigram.docvalidator.store;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Document class represent input document, which consists
 * of more than one files.
 */
public final class Document implements Block {
  public Document() {
    super();
    files = new ArrayList<FileContent>();
  }

  /**
   * Get files contained by Document.
   *
   * @return iterator of file
   */
  public Iterator<FileContent> getFiles() {
    return files.iterator();
  }

  /**
   * Add a file to Document.
   *
   * @param file a file to be added to Document
   */
  public void appendFile(FileContent file) {
    files.add(file);
  }

  /**
   * Get a file specifying with the file id.
   *
   * @param id id of file
   * @return a file
   */
  public FileContent getFile(int id) {
    return files.get(id);
  }

  /**
   * Get last file in Document.
   *
   * @return a file added in the last
   */
  public FileContent getLastFile() {
    FileContent fileContent = null;
    if (files.size() > 0) {
      fileContent = files.get(files.size() - 1);
    }
    return fileContent;
  }

  /**
   * Return the file number in Document
   *
   * @return the number of files
   */
  public int getNumberOfFiles() {
    return files.size();
  }

  public int getBlockID() {
    return BlockTypes.DOCUMENT;
  }

  private final List<FileContent> files;
}