package com.monitorjbl.xlsx;

import com.monitorjbl.xlsx.exceptions.MissingSheetException;
import com.monitorjbl.xlsx.exceptions.OpenException;
import com.monitorjbl.xlsx.exceptions.ReadException;
import com.monitorjbl.xlsx.impl.StreamingSheetReader;
import com.monitorjbl.xlsx.impl.StreamingWorkbook;
import com.monitorjbl.xlsx.impl.StreamingWorkbookReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.Objects;

import static com.monitorjbl.xlsx.XmlUtils.document;
import static com.monitorjbl.xlsx.XmlUtils.searchForNodeList;

/**
 * Streaming Excel workbook implementation. Most advanced features of POI are not supported.
 * Use this only if your application can handle iterating through an entire workbook, row by
 * row.
 */
public class StreamingReader implements Iterable<Row>, AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(StreamingReader.class);

  private File tmp;
<<<<<<< /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/left.java
  private OPCPackage pkg;
||||||| /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/base.java
=======
  private final StreamingWorkbookReader workbook;
>>>>>>> /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/right.java

<<<<<<< /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/left.java
  private StreamingReader(OPCPackage pkg, SharedStringsTable sst, StylesTable stylesTable, XMLEventReader parser, int rowCacheSize) {
    this.pkg = pkg;
    this.sst = sst;
    this.stylesTable = stylesTable;
    this.parser = parser;
    this.rowCacheSize = rowCacheSize;
  }

  /**
   * Read through a number of rows equal to the rowCacheSize field or until there is no more data to read
   *
   * @return true if data was read
   */
  private boolean getRow() {
    try {
      rowCache.clear();
      while(rowCache.size() < rowCacheSize && parser.hasNext()) {
        handleEvent(parser.nextEvent());
      }
      rowCacheIterator = rowCache.iterator();
      return rowCacheIterator.hasNext();
    } catch(XMLStreamException | SAXException e) {
      log.debug("End of stream");
    }
    return false;
  }

  /**
   * Handles a SAX event.
   *
   * @param event
   * @throws SAXException
   */
  private void handleEvent(XMLEvent event) throws SAXException {
    if(event.getEventType() == XMLStreamConstants.CHARACTERS) {
      Characters c = event.asCharacters();
      lastContents += c.getData();
    } else if(event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      StartElement startElement = event.asStartElement();
      String tagLocalName = startElement.getName().getLocalPart();

      if("row".equals(tagLocalName)) {
        Attribute rowIndex = startElement.getAttributeByName(new QName("r"));
        currentRow = new StreamingRow(Integer.parseInt(rowIndex.getValue()) - 1);
      } else if("c".equals(tagLocalName)) {
        Attribute ref = startElement.getAttributeByName(new QName("r"));

        String[] coord = ref.getValue().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        currentCell = new StreamingCell(CellReference.convertColStringToIndex(coord[0]), Integer.parseInt(coord[1]) - 1);
        setFormatString(startElement, currentCell);

        Attribute type = startElement.getAttributeByName(new QName("t"));
        if(type != null) {
          currentCell.setType(type.getValue());
        } else {
          currentCell.setType("n");
        }

        Attribute style = startElement.getAttributeByName(new QName("s"));

        if(style != null) {
          String indexStr = style.getValue();
          try {
            int index = Integer.parseInt(indexStr);
            currentCell.setCellStyle(stylesTable.getStyleAt(index));
          } catch(NumberFormatException nfe) {
            log.warn("Ignoring invalid style index {}", indexStr);
          }
        }
      }

      // Clear contents cache
      lastContents = "";
    } else if(event.getEventType() == XMLStreamConstants.END_ELEMENT) {
      EndElement endElement = event.asEndElement();
      String tagLocalName = endElement.getName().getLocalPart();

      if("v".equals(tagLocalName) || "t".equals(tagLocalName)) {
        currentCell.setRawContents(unformattedContents());
        currentCell.setContents(formattedContents());
      } else if("row".equals(tagLocalName) && currentRow != null) {
        rowCache.add(currentRow);
      } else if("c".equals(tagLocalName)) {
        currentRow.getCellMap().put(currentCell.getColumnIndex(), currentCell);
      }

    }
  }

  /**
   * Read the numeric format string out of the styles table for this cell. Stores
   * the result in the Cell.
   *
   * @param startElement
   * @param cell
   */
  void setFormatString(StartElement startElement, StreamingCell cell) {
    Attribute cellStyle = startElement.getAttributeByName(new QName("s"));
    String cellStyleString = (cellStyle != null) ? cellStyle.getValue() : null;
    XSSFCellStyle style = null;

    if(cellStyleString != null) {
      style = stylesTable.getStyleAt(Integer.parseInt(cellStyleString));
    } else if(stylesTable.getNumCellStyles() > 0) {
      style = stylesTable.getStyleAt(0);
    }

    if(style != null) {
      cell.setNumericFormatIndex(style.getDataFormat());
      String formatString = style.getDataFormatString();

      if(formatString != null) {
        cell.setNumericFormat(formatString);
      } else {
        cell.setNumericFormat(BuiltinFormats.getBuiltinFormat(cell.getNumericFormatIndex()));
      }
    } else {
      cell.setNumericFormatIndex(null);
      cell.setNumericFormat(null);
    }
  }

  /**
   * Tries to format the contents of the last contents appropriately based on
   * the type of cell and the discovered numeric format.
   *
   * @return
   */
  String formattedContents() {
    switch(currentCell.getType()) {
      case "s":           //string stored in shared table
        int idx = Integer.parseInt(lastContents);
        return new XSSFRichTextString(sst.getEntryAt(idx)).toString();
      case "inlineStr":   //inline string (not in sst)
        return new XSSFRichTextString(lastContents).toString();
      case "str":         //forumla type
        return '"' + lastContents + '"';
      case "e":           //error type
        return "ERROR:  " + lastContents;
      case "n":           //numeric type
        if(currentCell.getNumericFormat() != null && lastContents.length() > 0) {
          return dataFormatter.formatRawCellContents(
              Double.parseDouble(lastContents),
              currentCell.getNumericFormatIndex(),
              currentCell.getNumericFormat());
        } else {
          return lastContents;
        }
      default:
        return lastContents;
    }
  }

  /**
   * Returns the contents of the cell, with no formatting applied
   *
   * @return
   */
  String unformattedContents() {
    switch(currentCell.getType()) {
      case "s":           //string stored in shared table
        int idx = Integer.parseInt(lastContents);
        return new XSSFRichTextString(sst.getEntryAt(idx)).toString();
      case "inlineStr":   //inline string (not in sst)
        return new XSSFRichTextString(lastContents).toString();
      default:
        return lastContents;
    }
||||||| /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/base.java
  private StreamingReader(SharedStringsTable sst, StylesTable stylesTable, XMLEventReader parser, int rowCacheSize) {
    this.sst = sst;
    this.stylesTable = stylesTable;
    this.parser = parser;
    this.rowCacheSize = rowCacheSize;
  }

  /**
   * Read through a number of rows equal to the rowCacheSize field or until there is no more data to read
   *
   * @return true if data was read
   */
  private boolean getRow() {
    try {
      rowCache.clear();
      while(rowCache.size() < rowCacheSize && parser.hasNext()) {
        handleEvent(parser.nextEvent());
      }
      rowCacheIterator = rowCache.iterator();
      return rowCacheIterator.hasNext();
    } catch(XMLStreamException | SAXException e) {
      log.debug("End of stream");
    }
    return false;
  }

  /**
   * Handles a SAX event.
   *
   * @param event
   * @throws SAXException
   */
  private void handleEvent(XMLEvent event) throws SAXException {
    if(event.getEventType() == XMLStreamConstants.CHARACTERS) {
      Characters c = event.asCharacters();
      lastContents += c.getData();
    } else if(event.getEventType() == XMLStreamConstants.START_ELEMENT) {
      StartElement startElement = event.asStartElement();
      String tagLocalName = startElement.getName().getLocalPart();

      if("row".equals(tagLocalName)) {
        Attribute rowIndex = startElement.getAttributeByName(new QName("r"));
        currentRow = new StreamingRow(Integer.parseInt(rowIndex.getValue()) - 1);
      } else if("c".equals(tagLocalName)) {
        Attribute ref = startElement.getAttributeByName(new QName("r"));

        String[] coord = ref.getValue().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        currentCell = new StreamingCell(CellReference.convertColStringToIndex(coord[0]), Integer.parseInt(coord[1]) - 1);
        setFormatString(startElement, currentCell);

        Attribute type = startElement.getAttributeByName(new QName("t"));
        if(type != null) {
          currentCell.setType(type.getValue());
        } else {
          currentCell.setType("n");
        }

        Attribute style = startElement.getAttributeByName(new QName("s"));

        if(style != null){
          String indexStr = style.getValue();
          try{
            int index = Integer.parseInt(indexStr);
            currentCell.setCellStyle(stylesTable.getStyleAt(index));
          } catch(NumberFormatException nfe) {
            log.warn("Ignoring invalid style index {}", indexStr);
          }
        }
      }

      // Clear contents cache
      lastContents = "";
    } else if(event.getEventType() == XMLStreamConstants.END_ELEMENT) {
      EndElement endElement = event.asEndElement();
      String tagLocalName = endElement.getName().getLocalPart();

      if("v".equals(tagLocalName)) {
        currentCell.setRawContents(unformattedContents());
        currentCell.setContents(formattedContents());
      } else if("row".equals(tagLocalName) && currentRow != null) {
        rowCache.add(currentRow);
      } else if("c".equals(tagLocalName)) {
        currentRow.getCellMap().put(currentCell.getColumnIndex(), currentCell);
      }

    }
  }

  /**
   * Read the numeric format string out of the styles table for this cell. Stores
   * the result in the Cell.
   *
   * @param startElement
   * @param cell
   */
  void setFormatString(StartElement startElement, StreamingCell cell) {
    Attribute cellStyle = startElement.getAttributeByName(new QName("s"));
    String cellStyleString = (cellStyle != null) ? cellStyle.getValue() : null;
    XSSFCellStyle style = null;

    if(cellStyleString != null) {
      style = stylesTable.getStyleAt(Integer.parseInt(cellStyleString));
    } else if(stylesTable.getNumCellStyles() > 0) {
      style = stylesTable.getStyleAt(0);
    }

    if(style != null) {
      cell.setNumericFormatIndex(style.getDataFormat());
      String formatString = style.getDataFormatString();

      if(formatString != null) {
        cell.setNumericFormat(formatString);
      } else {
        cell.setNumericFormat(BuiltinFormats.getBuiltinFormat(cell.getNumericFormatIndex()));
      }
    } else {
      cell.setNumericFormatIndex(null);
      cell.setNumericFormat(null);
    }
  }

  /**
   * Tries to format the contents of the last contents appropriately based on
   * the type of cell and the discovered numeric format.
   *
   * @return
   */
  String formattedContents() {
    switch(currentCell.getType()) {
      case "s":           //string stored in shared table
        int idx = Integer.parseInt(lastContents);
        return new XSSFRichTextString(sst.getEntryAt(idx)).toString();
      case "inlineStr":   //inline string (not in sst)
        return new XSSFRichTextString(lastContents).toString();
      case "str":         //forumla type
        return '"' + lastContents + '"';
      case "e":           //error type
        return "ERROR:  " + lastContents;
      case "n":           //numeric type
        if(currentCell.getNumericFormat() != null && lastContents.length() > 0) {
          return dataFormatter.formatRawCellContents(
              Double.parseDouble(lastContents),
              currentCell.getNumericFormatIndex(),
              currentCell.getNumericFormat());
        } else {
          return lastContents;
        }
      default:
        return lastContents;
    }
  }

  /**
   * Returns the contents of the cell, with no formatting applied
   *
   * @return
   */
  String unformattedContents() {
    switch(currentCell.getType()) {
      case "s":           //string stored in shared table
        int idx = Integer.parseInt(lastContents);
        return new XSSFRichTextString(sst.getEntryAt(idx)).toString();
      case "inlineStr":   //inline string (not in sst)
        return new XSSFRichTextString(lastContents).toString();
      default:
        return lastContents;
    }
=======
  public StreamingReader(StreamingWorkbookReader workbook) {
    this.workbook = workbook;
>>>>>>> /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/right.java
  }

  /**
   * Returns a new streaming iterator to loop through rows. This iterator is not
   * guaranteed to have all rows in memory, and any particular iteration may
   * trigger a load from disk to read in new data.
   *
   * @return the streaming iterator
   * @deprecated StreamingReader is equivalent to the POI Workbook object rather
   * than the Sheet object. This method will be removed in a future release.
   */
  @Override
  public Iterator<Row> iterator() {
    return workbook.first().iterator();
  }

  /**
   * Closes the streaming resource, attempting to clean up any temporary files created.
   *
   * @throws com.monitorjbl.xlsx.exceptions.CloseException if there is an issue closing the stream
   */
  @Override
  public void close() {
    try {
<<<<<<< /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/left.java
      parser.close();
      pkg.revert();
    } catch(XMLStreamException e) {
      throw new CloseException(e);
    }

    if(tmp != null) {
      log.debug("Deleting tmp file [" + tmp.getAbsolutePath() + "]");
      tmp.delete();
||||||| /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/base.java
      parser.close();
    } catch(XMLStreamException e) {
      throw new CloseException(e);
    }

    if(tmp != null) {
      log.debug("Deleting tmp file [" + tmp.getAbsolutePath() + "]");
      tmp.delete();
=======
      workbook.close();
    } finally {
      if(tmp != null) {
        log.debug("Deleting tmp file [" + tmp.getAbsolutePath() + "]");
        tmp.delete();
      }
>>>>>>> /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/right.java
    }
  }

  static File writeInputStreamToFile(InputStream is, int bufferSize) throws IOException {
    File f = Files.createTempFile("tmp-", ".xlsx").toFile();
    try(FileOutputStream fos = new FileOutputStream(f)) {
      int read;
      byte[] bytes = new byte[bufferSize];
      while((read = is.read(bytes)) != -1) {
        fos.write(bytes, 0, read);
      }
      is.close();
      fos.close();
      return f;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private int rowCacheSize = 10;
    private int bufferSize = 1024;
    private int sheetIndex = 0;
    private String sheetName;
    private String password;

    public int getRowCacheSize() {
      return rowCacheSize;
    }

    public int getBufferSize() {
      return bufferSize;
    }

    public int getSheetIndex() {
      return sheetIndex;
    }

    public String getSheetName() {
      return sheetName;
    }

    public String getPassword() {
      return password;
    }

    /**
     * The number of rows to keep in memory at any given point.
     * <p>
     * Defaults to 10
     * </p>
     *
     * @param rowCacheSize number of rows
     * @return reference to current {@code Builder}
     */
    public Builder rowCacheSize(int rowCacheSize) {
      this.rowCacheSize = rowCacheSize;
      return this;
    }

    /**
     * The number of bytes to read into memory from the input
     * resource.
     * <p>
     * Defaults to 1024
     * </p>
     *
     * @param bufferSize buffer size in bytes
     * @return reference to current {@code Builder}
     */
    public Builder bufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
      return this;
    }

    /**
     * Which sheet to open. There can only be one sheet open
     * for a single instance of {@code StreamingReader}. If
     * more sheets need to be read, a new instance must be
     * created.
     * <p>
     * Defaults to 0
     * </p>
     *
     * @param sheetIndex index of sheet
     * @return reference to current {@code Builder}
     */
    public Builder sheetIndex(int sheetIndex) {
      this.sheetIndex = sheetIndex;
      return this;
    }

    /**
     * Which sheet to open. There can only be one sheet open
     * for a single instance of {@code StreamingReader}. If
     * more sheets need to be read, a new instance must be
     * created.
     *
     * @param sheetName name of sheet
     * @return reference to current {@code Builder}
     */
    public Builder sheetName(String sheetName) {
      this.sheetName = sheetName;
      return this;
    }

    /**
     * For password protected files specify password to open file.
     * If the password is incorrect a {@code ReadException} is thrown on
     * {@code read}.
     * <p>NULL indicates that no password should be used, this is the
     * default value.</p>
     *
     * @param password to use when opening file
     * @return reference to current {@code Builder}
     */
    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public StreamingWorkbook open(InputStream is) {
      StreamingWorkbookReader workbook = new StreamingWorkbookReader(this);
      workbook.init(is);
      return new StreamingWorkbook(workbook);
    }

    public StreamingWorkbook open(File file) {
      StreamingWorkbookReader workbook = new StreamingWorkbookReader(this);
      workbook.init(file);
      return new StreamingWorkbook(workbook);
    }

    /**
     * Reads a given {@code InputStream} and returns a new
     * instance of {@code StreamingReader}. Due to Apache POI
     * limitations, a temporary file must be written in order
     * to create a streaming iterator. This process will use
     * the same buffer size as specified in {@link #bufferSize(int)}.
     *
     * @param is input stream to read in
     * @return built streaming reader instance
     * @throws com.monitorjbl.xlsx.exceptions.ReadException if there is an issue reading the stream
     * @deprecated This method will be removed in a future release. Use {@link Builder#open(InputStream)} instead
     */
    public StreamingReader read(InputStream is) {
      File f = null;
      try {
        f = writeInputStreamToFile(is, bufferSize);
        log.debug("Created temp file [" + f.getAbsolutePath() + "]");

        StreamingReader r = read(f);
        r.tmp = f;
        return r;
      } catch(IOException e) {
        throw new ReadException("Unable to read input stream", e);
      } catch(RuntimeException e) {
        f.delete();
        throw e;
      }
    }

    /**
     * Reads a given {@code File} and returns a new instance
     * of {@code StreamingReader}.
     *
     * @param f file to read in
     * @return built streaming reader instance
     * @throws com.monitorjbl.xlsx.exceptions.OpenException if there is an issue opening the file
     * @throws com.monitorjbl.xlsx.exceptions.ReadException if there is an issue reading the file
     * @deprecated This method will be removed in a future release. Use {@link Builder#open(File)} instead
     */
    public StreamingReader read(File f) {
      try {
        OPCPackage pkg;
        if(password != null) {
          // Based on: https://poi.apache.org/encryption.html
          POIFSFileSystem poifs = new POIFSFileSystem(f);
          EncryptionInfo info = new EncryptionInfo(poifs);
          Decryptor d = Decryptor.getInstance(info);
          d.verifyPassword(password);
          pkg = OPCPackage.open(d.getDataStream(poifs));
        } else {
          pkg = OPCPackage.open(f);
        }

        XSSFReader reader = new XSSFReader(pkg);
        SharedStringsTable sst = reader.getSharedStringsTable();
        StylesTable styles = reader.getStylesTable();

        InputStream sheet = findSheet(reader);
        if(sheet == null) {
          throw new MissingSheetException("Unable to find sheet at index [" + sheetIndex + "]");
        }

        XMLEventReader parser = XMLInputFactory.newInstance().createXMLEventReader(sheet);
<<<<<<< /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/left.java
        return new StreamingReader(pkg, sst, styles, parser, rowCacheSize);
||||||| /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/base.java
        return new StreamingReader(sst, styles, parser, rowCacheSize);
=======

        return new StreamingReader(new StreamingWorkbookReader(pkg, new StreamingSheetReader(sst, styles, parser, rowCacheSize), this));
>>>>>>> /usr/src/app/output/monitorjbl/excel-streaming-reader/f80a8d72621dab651e6defecb377f38f2dead38e/src/main/java/com/monitorjbl/xlsx/StreamingReader.java/right.java
      } catch(IOException e) {
        throw new OpenException("Failed to open file", e);
      } catch(OpenXML4JException | XMLStreamException e) {
        throw new ReadException("Unable to read workbook", e);
      } catch(GeneralSecurityException e) {
        throw new ReadException("Unable to read workbook - Decryption failed", e);
      }
    }

    InputStream findSheet(XSSFReader reader) throws IOException, InvalidFormatException {
      int index = sheetIndex;
      if(sheetName != null) {
        index = -1;
        //This file is separate from the worksheet data, and should be fairly small
        NodeList nl = searchForNodeList(document(reader.getWorkbookData()), "/workbook/sheets/sheet");
        for(int i = 0; i < nl.getLength(); i++) {
          if(Objects.equals(nl.item(i).getAttributes().getNamedItem("name").getTextContent(), sheetName)) {
            index = i;
          }
        }
        if(index < 0) {
          return null;
        }
      }
      Iterator<InputStream> iter = reader.getSheetsData();
      InputStream sheet = null;

      int i = 0;
      while(iter.hasNext()) {
        InputStream is = iter.next();
        if(i++ == index) {
          sheet = is;
          log.debug("Found sheet at index [" + sheetIndex + "]");
          break;
        }
      }
      return sheet;
    }
  }

}
