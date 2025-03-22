package com.mycila.maven.plugin.license.header;
import com.mycila.maven.plugin.license.util.FileContent;
import com.mycila.maven.plugin.license.util.StringUtils;

/**
 * The <code>HeaderParser</code> class is used to get header information about the current header defined in the given
 * file. The achieve this it will use the <code>HeaderDefinition</code> associated to the type of the given file.
 *
 * Important: is considered a license header a header which contains the word <em>copyright</em> (case insensitive)
 * within a section of the file which match the given <code>HeaderDefinition</code> associated to this
 * <code>HeaderParser</code>.
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @see com.mycila.maven.plugin.license.header.HeaderDefinition
 */
public final class HeaderParser {
  private final int beginPosition;

  private final int endPosition;

  private final boolean existingHeader;

  private final FileContent fileContent;

  private final String[] keywords;

  private HeaderDefinition headerDefinition;

  private String line;

  /**
     * Creates a <code>HeaderParser</code> object linked to the given file content and the associated header definition
     * based on the file type.
     *
     * @param fileContent      The file content.
     * @param headerDefinition The associated header definition to use.
     * @throws IllegalArgumentException If the file content is null or if the header definition is null.
     */
  public HeaderParser(FileContent fileContent, HeaderDefinition headerDefinition, String[] keywords) {
    if (fileContent == null) {
      throw new IllegalArgumentException("Cannot create a header parser for null file content");
    }
    if (headerDefinition == null) {
      throw new IllegalArgumentException("Cannot work on file header if the header definition is null");
    }
    this.keywords = keywords.clone();
    this.headerDefinition = headerDefinition;
    this.fileContent = fileContent;
    beginPosition = findBeginPosition();
    existingHeader = hasHeader();
    endPosition = existingHeader ? findEndPosition() : -1;
  }

  /**
     * Returns the index position in the content where the header effectively starts.
     *
     * @return The index in the content.
     */
  public int getBeginPosition() {
    return beginPosition;
  }

  /**
     * Returns the index position in the content where the header effectively ends.
     *
     * @return The index in the content.
     */
  public int getEndPosition() {
    return endPosition;
  }

  /**
     * Tells if the given file already contains a license header.
     *
     * @return true if a license header has been detect or false.
     */
  public boolean gotAnyHeader() {
    return existingHeader;
  }

  /**
     * Returns the file content.
     *
     * @return The content.
     */
  public FileContent getFileContent() {
    return fileContent;
  }

  /**
     * Returns the header definition associated to this header parser (itself bounded to a file).
     *
     * @return The associated header definition.
     */
  public HeaderDefinition getHeaderDefinition() {
    return headerDefinition;
  }

  private int findBeginPosition() {
    int beginPos = 0;
    line = fileContent.nextLine();
    if (headerDefinition.getSkipLinePattern() == null) {
      return beginPos;
    }
    while (line != null && !headerDefinition.isSkipLine(line)) {
      beginPos = fileContent.getPosition();
      line = fileContent.nextLine();
    }
    while (line != null && headerDefinition.isSkipLine(line)) {
      beginPos = fileContent.getPosition();
      line = fileContent.nextLine();
    }
    if (line == null) {
      beginPos = 0;
      fileContent.reset();
      line = fileContent.nextLine();
    }
    return beginPos;
  }

  private boolean hasHeader() {
    while (line != null && "".equals(line.trim())) {
      line = fileContent.nextLine();
    }
    boolean gotHeader = false;
    if (headerDefinition.isFirstHeaderLine(line)) {
      if (headerDefinition.allowBlankLines()) {
        do line = fileContent.nextLine(); while(line != null && "".equals(line.trim()));
      }
      if (line == null) {
        return false;
      }
      StringBuilder inPlaceHeader = new StringBuilder();
      String before = StringUtils.rtrim(headerDefinition.getBeforeEachLine());
      if ("".equals(before) && !headerDefinition.isMultiLine()) {
        before = headerDefinition.getBeforeEachLine();
      }
      boolean foundEnd = false;
      do {
        inPlaceHeader.append(line.toLowerCase());
        line = fileContent.nextLine();
        if (headerDefinition.isMultiLine() && headerDefinition.isLastHeaderLine(line)) {
          foundEnd = true;
          break;
        }
      } while(line != null && line.startsWith(before));
      if (headerDefinition.allowBlankLines()) {
        do line = fileContent.nextLine(); while(line != null && "".equals(line.trim()));
      }
      if (headerDefinition.allowBlankLines() || !foundEnd) {
        fileContent.rewind();
      }
      if (!headerDefinition.isMultiLine()) {
        int pos = fileContent.getPosition();
        while (line != null && !headerDefinition.isLastHeaderLine(line) && (headerDefinition.allowBlankLines() || !"".equals(line.trim())) && line.startsWith(before)) {
          line = fileContent.nextLine();
        }
        if (line == null) {
          fileContent.resetTo(pos);
        }
      }
      gotHeader = true;
      for (String keyword : keywords) {
        if (inPlaceHeader.indexOf(keyword.toLowerCase()) == -1) {
          gotHeader = false;
          break;
        }
      }
    }
    return gotHeader;
  }

  private int findEndPosition() {
    int end = fileContent.getPosition();
    line = fileContent.nextLine();
    if (line != null && "".equals(line.trim())) {
      end = fileContent.getPosition();
    }
    return end;
  }
}