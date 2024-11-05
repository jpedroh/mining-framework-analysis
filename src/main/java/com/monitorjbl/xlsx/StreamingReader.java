package com.monitorjbl.xlsx;
import com.monitorjbl.xlsx.exceptions.MissingSheetException;
import com.monitorjbl.xlsx.exceptions.OpenException;
import com.monitorjbl.xlsx.exceptions.ReadException;
import com.monitorjbl.xlsx.sst.BufferedStringsTable;
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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StaxHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.Objects;
import static com.monitorjbl.xlsx.XmlUtils.document;
import static com.monitorjbl.xlsx.XmlUtils.searchForNodeList;
import static com.monitorjbl.xlsx.impl.TempFileUtil.writeInputStreamToFile;

public class StreamingReader implements Iterable<Row>, AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(StreamingReader.class);

  private File tmp;

  private final StreamingWorkbookReader workbook;

  public StreamingReader(StreamingWorkbookReader workbook) {
    this.workbook = workbook;
  }

  @Override public Iterator<Row> iterator() {
    return workbook.first().iterator();
  }

  @Override public void close() throws IOException {
    try {
      workbook.close();
    }  finally {
      if (tmp != null) {
        if (log.isDebugEnabled()) {
          log.debug("Deleting tmp file [" + tmp.getAbsolutePath() + "]");
        }
        tmp.delete();
      }
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private int rowCacheSize = 10;

    private int bufferSize = 1024;

    private int sheetIndex = 0;

    private int sstCacheSize = -1;

    private String sheetName;

    private String password;

    private boolean loadComments = false;

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

    public int getSstCacheSize() {
      return sstCacheSize;
    }

    public boolean shouldLoadCellComments() {
      return loadComments;
    }

    public Builder rowCacheSize(int rowCacheSize) {
      this.rowCacheSize = rowCacheSize;
      return this;
    }

    public Builder bufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
      return this;
    }

    public Builder sheetIndex(int sheetIndex) {
      this.sheetIndex = sheetIndex;
      return this;
    }

    public Builder sheetName(String sheetName) {
      this.sheetName = sheetName;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder sstCacheSize(int sstCacheSize) {
      this.sstCacheSize = sstCacheSize;
      return this;
    }

    public Builder readComments() {
      this.loadComments = true;
      return this;
    }

    public Workbook open(InputStream is) {
      StreamingWorkbookReader workbook = new StreamingWorkbookReader(this);
      workbook.init(is);
      return new StreamingWorkbook(workbook);
    }

    public Workbook open(File file) {
      StreamingWorkbookReader workbook = new StreamingWorkbookReader(this);
      workbook.init(file);
      return new StreamingWorkbook(workbook);
    }

    public StreamingReader read(InputStream is) {
      File f = null;
      try {
        f = writeInputStreamToFile(is, bufferSize);
        log.debug("Created temp file [" + f.getAbsolutePath() + "]");
        StreamingReader r = read(f);
        r.tmp = f;
        return r;
      } catch (IOException e) {
        throw new ReadException("Unable to read input stream", e);
      } catch (RuntimeException e) {
        f.delete();
        throw e;
      }
    }

    public StreamingReader read(File f) {
      try {
        OPCPackage pkg;
        if (password != null) {
          POIFSFileSystem poifs = new POIFSFileSystem(f);
          EncryptionInfo info = new EncryptionInfo(poifs);
          Decryptor d = Decryptor.getInstance(info);
          d.verifyPassword(password);
          pkg = OPCPackage.open(d.getDataStream(poifs));
        } else {
          pkg = OPCPackage.open(f);
        }
        boolean use1904Dates = false;
        XSSFReader reader = new XSSFReader(pkg);
        SharedStringsTable sst;
        File sstCache = null;
        if (sstCacheSize > 0) {
          sstCache = Files.createTempFile("", "").toFile();
          log.debug("Created sst cache file [" + sstCache.getAbsolutePath() + "]");
          sst = BufferedStringsTable.getSharedStringsTable(sstCache, sstCacheSize, pkg);
        } else {
          sst = reader.getSharedStringsTable();
        }
        StylesTable styles = reader.getStylesTable();
        NodeList workbookPr = searchForNodeList(document(reader.getWorkbookData()), "/workbook/workbookPr");
        if (workbookPr.getLength() == 1) {
          final Node date1904 = workbookPr.item(0).getAttributes().getNamedItem("date1904");
          if (date1904 != null) {
            use1904Dates = ("1".equals(date1904.getTextContent()));
          }
        }
        InputStream sheet = findSheet(reader);
        if (sheet == null) {
          throw new MissingSheetException("Unable to find sheet at index [" + sheetIndex + "]");
        }
        XMLEventReader parser = StaxHelper.newXMLInputFactory().createXMLEventReader(sheet);
        return new StreamingReader(new StreamingWorkbookReader(sst, sstCache, pkg, new StreamingSheetReader(sst, styles, parser, use1904Dates, rowCacheSize), this));
      } catch (IOException e) {
        throw new OpenException("Failed to open file", e);
      } catch (OpenXML4JException | XMLStreamException e) {
        throw new ReadException("Unable to read workbook", e);
      } catch (GeneralSecurityException e) {
        throw new ReadException("Unable to read workbook - Decryption failed", e);
      }
    }

    private InputStream findSheet(XSSFReader reader) throws IOException, InvalidFormatException {
      int index = sheetIndex;
      if (sheetName != null) {
        index = -1;
        NodeList nl = searchForNodeList(document(reader.getWorkbookData()), "/workbook/sheets/sheet");
        for (int i = 0; i < nl.getLength(); i++) {
          if (Objects.equals(nl.item(i).getAttributes().getNamedItem("name").getTextContent(), sheetName)) {
            index = i;
          }
        }
        if (index < 0) {
          return null;
        }
      }
      Iterator<InputStream> iter = reader.getSheetsData();
      InputStream sheet = null;
      int i = 0;
      while (iter.hasNext()) {
        InputStream is = iter.next();
        if (i++ == index) {
          sheet = is;
          log.debug("Found sheet at index [" + sheetIndex + "]");
          break;
        }
      }
      return sheet;
    }
  }
}