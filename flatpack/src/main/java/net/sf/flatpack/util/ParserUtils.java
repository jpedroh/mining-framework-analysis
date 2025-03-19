package net.sf.flatpack.util;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import net.sf.flatpack.Parser;
import net.sf.flatpack.converter.Converter;
import net.sf.flatpack.converter.FPConvertException;
import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.xml.MetaData;
import net.sf.flatpack.xml.XMLRecordElement;

/**
 *  Static utilities that are used to perform parsing in the
 *         DataSet class These can also be used for low level parsing, if not
 *         wishing to use the DataSet class.
 *
 * @author Paul Zepernick
 * @author Benoit Xhenseval
 */
public final class ParserUtils {
  private static final String DATASTRUCTURE_LENGTH = "DATASTRUCTURE_LENGTH";

  private ParserUtils() {
  }

  /**
     * Returns an ArrayList of items in a delimited string. If there is no
     * qualifier around the text, the qualifier parameter can be left null, or
     * empty. There should not be any line breaks in the string. Each line of
     * the file should be passed in individually.
     * Elements which are not qualified will have leading and trailing white
     * space removed.  This includes unqualified elements, which may be
     * contained in an unqualified parse: "data",  data  ,"data"
     *
     * Special thanks to Benoit for contributing this much improved speedy parser :0)
     *
     * @author Benoit Xhenseval
     * @param line -
     *            String of data to be parsed
     * @param delimiter -
     *            Delimiter separating each element
     * @param qualifier -
     *            qualifier which is surrounding the text
     * @param initialSize -
     *            initial capacity of the List size
     * @param preserveLeadingWhitespace
     *            Keep any leading spaces
     * @param preserveTrailingWhitespace
     *            Keep any trailing spaces
     * @return List
     */
  public static List<String> splitLine(final String line, final char delimiter, final char qualifier, final int initialSize, final boolean preserveLeadingWhitespace, final boolean preserveTrailingWhitespace) {
    final List<String> list = new ArrayList<>(initialSize);
    if (delimiter == 0) {
      list.add(line);
      return list;
    } else {
      if (line == null) {
        return list;
      }
    }
    String trimmedLine;
    if (delimiter == '\t' || delimiter == ' ') {
      trimmedLine = line;
    } else {
      trimmedLine = line;
      if (!preserveLeadingWhitespace) {
        trimmedLine = ParserUtils.lTrim(line);
      }
      if (!preserveTrailingWhitespace) {
        trimmedLine = ParserUtils.rTrim(line);
      }
    }
    final int size = trimmedLine.length();
    if (size == 0) {
      list.add("");
      return list;
    }
    boolean insideQualifier = false;
    char previousChar = 0;
    boolean blockWasInQualifier = false;
    final String doubleQualifier = "" + qualifier + qualifier;
    char[] newBlock = new char[size];
    int sizeSelected = 0;
    for (int i = 0; i < size; i++) {
      final char currentChar = trimmedLine.charAt(i);
      if (currentChar == '\ufeff') {
        continue;
      }
      if ((currentChar != delimiter || insideQualifier) && currentChar != qualifier) {
        previousChar = currentChar;
        newBlock[sizeSelected++] = currentChar;
        continue;
      }
      if (currentChar == delimiter) {
        if (!insideQualifier) {
          String trimmed = String.valueOf(newBlock, 0, sizeSelected);
          if (!blockWasInQualifier) {
            if (!preserveLeadingWhitespace) {
              trimmed = ParserUtils.lTrim(trimmed);
            }
            if (!preserveTrailingWhitespace) {
              trimmed = ParserUtils.rTrim(trimmed);
            }
          }
          if (trimmed == null || trimmed.length() == 1 && (trimmed.charAt(0) == delimiter || trimmed.charAt(0) == qualifier)) {
            list.add("");
          } else {
            list.add(replace(trimmed, doubleQualifier, String.valueOf(qualifier), -1));
          }
          blockWasInQualifier = false;
          sizeSelected = 0;
        }
      } else {
        if (currentChar == qualifier) {
          if (!insideQualifier && previousChar != qualifier) {
            if (previousChar == delimiter || previousChar == 0 || previousChar == ' ') {
              insideQualifier = true;
              sizeSelected = 0;
            } else {
              newBlock[sizeSelected++] = currentChar;
            }
          } else {
            if (i + 1 < size && delimiter != ' ') {
              int start = i + 1;
              char charToCheck = trimmedLine.charAt(start);
              while (charToCheck == ' ') {
                start++;
                if (start == size) {
                  break;
                }
                charToCheck = trimmedLine.charAt(start);
              }
              if (charToCheck != delimiter) {
                previousChar = currentChar;
                newBlock[sizeSelected++] = currentChar;
                continue;
              }
            }
            insideQualifier = false;
            blockWasInQualifier = true;
            if (i == size - 1) {
              String str = String.valueOf(newBlock, 0, sizeSelected);
              str = replace(str, doubleQualifier, String.valueOf(qualifier), -1);
              list.add(str);
              sizeSelected = 0;
            }
          }
        }
      }
      previousChar = currentChar;
    }
    if (sizeSelected > 0) {
      String str = String.valueOf(newBlock, 0, sizeSelected);
      str = replace(str, doubleQualifier, String.valueOf(qualifier), -1);
      if (blockWasInQualifier) {
        if (str.charAt(str.length() - 1) == qualifier) {
          list.add(str.substring(0, str.length() - 1));
        } else {
          list.add(str);
        }
      } else {
        String s = str;
        if (!preserveLeadingWhitespace) {
          s = ParserUtils.lTrim(s);
        }
        if (!preserveTrailingWhitespace) {
          s = ParserUtils.rTrim(s);
        }
        list.add(s);
      }
    } else {
      if (trimmedLine.charAt(size - 1) == delimiter) {
        list.add("");
      }
    }
    return list;
  }

  /**
     * Using a much faster String Replace from Apache!
     * @see <a href="https://stackoverflow.com/questions/16228992/commons-lang-stringutils-replace-performance-vs-string-replace">StackOverflow</a>
     */
  private static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  /**
     * Using a much faster String Replace from Apache!
     * @see <a href="https://stackoverflow.com/questions/16228992/commons-lang-stringutils-replace-performance-vs-string-replace">StackOverflow</a>
     */
  public static String replace(String text, String searchString, String replacement, int max) {
    if (text.length() == 0 || isEmpty(searchString) || replacement == null || max == 0) {
      return text;
    }
    int start = 0;
    int end = text.indexOf(searchString, start);
    if (end == -1) {
      return text;
    }
    int replLength = searchString.length();
    int increase = replacement.length() - replLength;
    increase = (increase < 0 ? 0 : increase);
    int multiplier = max;
    if (max < 0) {
      multiplier = 16;
    } else {
      if (max > 64) {
        multiplier = 64;
      }
    }
    increase *= multiplier;
    final StringBuilder buf = new StringBuilder(text.length() + increase);
    while (end != -1) {
      buf.append(text.substring(start, end)).append(replacement);
      start = end + replLength;
      if (--max == 0) {
        break;
      }
      end = text.indexOf(searchString, start);
    }
    buf.append(text.substring(start));
    return buf.toString();
  }

  /**
     * reads from the specified point in the line and returns how many chars to
     * the specified delimiter
     *
     * @param line
     * @param start
     * @param delimiter
     * @return int
     */
  public static int getDelimiterOffset(final String line, final int start, final char delimiter) {
    int idx = line.indexOf(delimiter, start);
    if (idx >= 0) {
      idx -= start - 1;
    }
    return idx;
  }

  /**
     * Removes empty space from the beginning of a string
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
  public static String lTrim(final String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value;
    int offset = 0;
    final int maxLength = value.length();
    while (offset < maxLength && (value.charAt(offset) == ' ' || value.charAt(offset) == '\t')) {
      offset++;
    }
    if (offset > 0) {
      trimmed = value.substring(offset);
    }
    return trimmed;
  }

  /**
     * Removes empty space from the beginning of a string, except for tabs
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
  public static String lTrimKeepTabs(final String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value;
    int offset = 0;
    final int maxLength = value.length();
    while (offset < maxLength && value.charAt(offset) == ' ') {
      offset++;
    }
    if (offset > 0) {
      trimmed = value.substring(offset);
    }
    return trimmed;
  }

  /**
     * Removes empty space from the end of a string
     *
     * @param value -
     *            to be trimmed
     * @return String
     */
  public static String rTrim(final String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value;
    int offset = value.length() - 1;
    while (offset > -1 && (value.charAt(offset) == ' ' || value.charAt(offset) == '\t')) {
      offset--;
    }
    if (offset < value.length() - 1) {
      trimmed = value.substring(0, offset + 1);
    }
    return trimmed;
  }

  /**
     * Returns a list of ColumnMetaData objects. This is for use with delimited
     * files. The first line of the file which contains data will be used as the
     * column names
     *
     * @param line
     * @param delimiter
     * @param qualifier
     * @param p
     *          PZParser used to specify additional option when working with the ColumnMetaData. Can be null
     * @param addSuffixToDuplicateColumnNames
     * @return PZMetaData
     */
  public static MetaData getPZMetaDataFromFile(final String line, final char delimiter, final char qualifier, final Parser p, final boolean addSuffixToDuplicateColumnNames) {
    final List<ColumnMetaData> results = new ArrayList<>();
    final Set<String> dupCheck = new HashSet<>();
    final List<String> lineData = splitLine(line, delimiter, qualifier, FPConstants.SPLITLINE_SIZE_INIT, false, false);
    for (final String colName : lineData) {
      final ColumnMetaData cmd = new ColumnMetaData();
      String colNameToUse = colName;
      if (dupCheck.contains(colNameToUse)) {
        if (!addSuffixToDuplicateColumnNames) {
          throw new FPException("Duplicate Column Name In File: " + colNameToUse);
        } else {
          int count = 2;
          while (dupCheck.contains(colNameToUse + count)) {
            count++;
          }
          colNameToUse = colName + count;
        }
      }
      cmd.setColName(colNameToUse);
      results.add(cmd);
      dupCheck.add(cmd.getColName());
    }
    return new MetaData(results, buidColumnIndexMap(results, p));
  }

  /**
     * Determines if the given line is the first part of a multiline record.  It does this by verifying that the
     * qualifer on the last element is not closed
     *
     * @param chrArry -
     *            char data of the line
     * @param delimiter -
     *            delimiter being used
     * @param qualifier -
     *            qualifier being used
     * @return boolean
     */
  public static boolean isMultiLine(final char[] chrArry, final char delimiter, final char qualifier) {
    if (chrArry[chrArry.length - 1] != qualifier) {
      boolean qualiFound = false;
      for (int i = chrArry.length - 1; i >= 0; i--) {
        if (chrArry[i] == ' ') {
          continue;
        }
        if (qualiFound) {
          if (chrArry[i] == delimiter) {
            boolean qualifiedContent = chrArry[0] == qualifier;
            for (int index = 0; index < chrArry.length; index++) {
              final char currentChar = chrArry[index];
              qualifiedContent = currentChar == qualifier;
              if (qualifiedContent) {
                for ( ; index < chrArry.length; index++) {
                  if (chrArry[index] == delimiter && chrArry[++index] == qualifier) {
                    qualifiedContent = false;
                  }
                }
              }
            }
            return qualifiedContent;
          }
          qualiFound = chrArry[i] == qualifier;
        } else {
          if (chrArry[i] == delimiter) {
            for (int j = i - 1; j >= 0; j--) {
              if (chrArry[j] == ' ') {
                continue;
              } else {
                if (chrArry[j] == qualifier) {
                  return false;
                }
              }
              break;
            }
          } else {
            if (chrArry[i] == qualifier) {
              qualiFound = true;
            }
          }
        }
      }
    } else {
      for (int i = chrArry.length - 1; i >= 0; i--) {
        if (i == chrArry.length - 1 || chrArry[i] == ' ') {
          continue;
        }
        if (chrArry[i] == delimiter) {
          boolean qualifiedContent = chrArry[0] == qualifier;
          for (int index = 0; index < chrArry.length; index++) {
            final char currentChar = chrArry[index];
            qualifiedContent = currentChar == qualifier;
            if (qualifiedContent) {
              for ( ; index < chrArry.length; index++) {
                if (chrArry[index] == delimiter && chrArry[++index] == qualifier) {
                  qualifiedContent = false;
                }
              }
            }
          }
          return qualifiedContent;
        }
        break;
      }
    }
    return false;
  }

  public static Map<String, Integer> calculateRecordLengths(final MetaData columnMD) {
    final Map<String, Integer> recordLengths = new HashMap<>();
    int recordLength = 0;
    for (final ColumnMetaData cmd : columnMD.getColumnsNames()) {
      recordLength += cmd.getColLength();
    }
    recordLengths.put(FPConstants.DETAIL_ID, recordLength);
    final Iterator<Entry<String, XMLRecordElement>> columnMDIt = columnMD.xmlRecordIterator();
    while (columnMDIt.hasNext()) {
      final Entry<String, XMLRecordElement> entry = columnMDIt.next();
      final List<ColumnMetaData> cmds = entry.getValue().getColumns();
      recordLength = 0;
      for (final ColumnMetaData cmd : cmds) {
        recordLength += cmd.getColLength();
      }
      recordLengths.put(entry.getKey(), recordLength);
    }
    return recordLengths;
  }

  public static String getCMDKeyForDelimitedFile(final MetaData columnMD, final List<String> lineElements) {
    if (!columnMD.isAnyRecordFormatSpecified()) {
      return FPConstants.DETAIL_ID;
    }
    final Iterator<Entry<String, XMLRecordElement>> mapEntries = columnMD.xmlRecordIterator();
    while (mapEntries.hasNext()) {
      final Entry<String, XMLRecordElement> entry = mapEntries.next();
      final XMLRecordElement recordXMLElement = entry.getValue();
      if (recordXMLElement.getElementCount() > 0 && recordXMLElement.getElementCount() == lineElements.size()) {
        return entry.getKey();
      } else {
        if (recordXMLElement.getElementNumber() > lineElements.size()) {
          continue;
        }
      }
      final String lineElement = lineElements.get(recordXMLElement.getElementNumber() - 1);
      if (lineElement.equals(recordXMLElement.getIndicator())) {
        return entry.getKey();
      }
    }
    return FPConstants.DETAIL_ID;
  }

  public static List<ColumnMetaData> getColumnMetaData(final String key, final MetaData columnMD) {
    if (key == null || key.equals(FPConstants.DETAIL_ID) || key.equals(FPConstants.COL_IDX)) {
      return columnMD.getColumnsNames();
    }
    return columnMD.getListColumnsForRecord(key);
  }

  public static int getColumnIndex(final String key, final MetaData columnMD, final String colName, final boolean columNameCaseSensitive) {
    int idx = -1;
    String column = colName;
    if (!columNameCaseSensitive) {
      column = colName.toLowerCase(Locale.getDefault());
    }
    idx = columnMD.getColumnIndex(key, column);
    if (idx < 0) {
      throw new NoSuchElementException("Column [" + column + "] does not exist, check case/spelling." + (key != null ? " key:[" + key + "]" : ""));
    }
    return idx;
  }

  /**
     * <p>
     * Returns padding using the specified delimiter repeated to a given length.
     * </p>
     *
     * <pre>
     *                PZStringUtils.padding(0, 'e')  = &quot;&quot;
     *                PZStringUtils.padding(3, 'e')  = &quot;eee&quot;
     *                PZStringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * <p>
     * Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode
     * Supplementary Characters</a> as they require a pair of <code>char</code>s
     * to be represented.
     * </p>
     *
     * @param repeat
     *            number of times to repeat delim
     * @param padChar
     *            character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException
     */
  public static String padding(final int repeat, final char padChar) {
    if (repeat < 0) {
      throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
    }
    final char[] buf = new char[repeat];
    for (int i = 0; i < buf.length; i++) {
      buf[i] = padChar;
    }
    return new String(buf);
  }

  /**
     * Build a map of name/position based on a list of ColumnMetaData.
     *
     * @author Benoit Xhenseval
     * @author Paul Zepernick
     * @param columns
     * @param p
     *         Reference to Parser which can provide additional options on how the
     *         map should be build.  This can be NULL.
     * @return a new Map
     */
  public static Map<String, Integer> buidColumnIndexMap(final List<ColumnMetaData> columns, final Parser p) {
    Map<String, Integer> map = null;
    if (columns != null && !columns.isEmpty()) {
      map = new HashMap<>();
      int idx = 0;
      for (final ColumnMetaData meta : columns) {
        String colName = meta.getColName();
        if (p != null && !p.isColumnNamesCaseSensitive()) {
          colName = colName.toLowerCase(Locale.getDefault());
        }
        map.put(colName, idx++);
      }
    }
    return map;
  }

  /**
     * Removes chars from the String that could not
     * be parsed into a Long value
     *
     *      PZStringUtils.stripNonLongChars("1000.25") = "1000"
     *
     * Method will truncate everything to the right of the decimal
     * place when encountered.
     *
     * @param value
     * @return String
     */
  public static String stripNonLongChars(final String value) {
    final StringBuilder newString = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      final char c = value.charAt(i);
      if (c == '.') {
        break;
      } else {
        if (c >= '0' && c <= '9' || c == '-') {
          newString.append(c);
        }
      }
    }
    final int sLen = newString.length();
    final String s = newString.toString();
    if (sLen == 0 || sLen == 1 && "-".equals(s)) {
      return "0";
    }
    return newString.toString();
  }

  /**
     * Removes chars from the String that could not
     * be parsed into a Double value
     *
     * @param value
     * @return String
     */
  public static String stripNonDoubleChars(final String value) {
    final StringBuilder newString = new StringBuilder();
    for (int i = 0; i < value.length(); i++) {
      final char c = value.charAt(i);
      if (c >= '0' && c <= '9' || c == '-' || c == '.') {
        newString.append(c);
      }
    }
    final int sLen = newString.length();
    final String s = newString.toString();
    if (sLen == 0 || sLen == 1 && (".".equals(s) || "-".equals(s))) {
      return "0";
    }
    return newString.toString();
  }

  /**
     * Retrieves the conversion table for use with the getObject()
     * method in IDataSet
     *
     * @throws IOException
     * @return Properties
     *              Properties contained in the pzconvert.properties file
     */
  public static Properties loadConvertProperties() throws IOException {
    final Properties pzConvertProps = new Properties();
    final URL url = ParserUtils.class.getClassLoader().getResource("fpconvert.properties");
    pzConvertProps.load(url.openStream());
    return pzConvertProps;
  }

  /**
     * Checks a list of &lt;String&gt; elements to see if every element
     * in the list is empty.
     *
     * @param l
     *          List of &lt;String&gt;
     * @return boolean
     *              true when all elements are empty
     */
  public static boolean isListElementsEmpty(final List<String> l) {
    for (final String s : l) {
      if (s != null && s.trim().length() > 0) {
        return false;
      }
    }
    return true;
  }

  /**
     * Converts a String value to the appropriate Object via
     * the correct net.sf.flatpack.converter.PZConverter implementation
     *
     * @param classXref
     *             Properties holding class cross reference
     * @param value
     *             Value to be converted to the Object
     * @param typeToReturn
     *             Type of object to be returned
     * @throws FPConvertException
     * @return Object
     */
  public static Object runPzConverter(final Properties classXref, final String value, final Class<?> typeToReturn) {
    final String sConverter = classXref.getProperty(typeToReturn.getName());
    if (sConverter == null) {
      throw new FPConvertException(typeToReturn.getName() + " is not registered in pzconvert.properties");
    }
    try {
      final Converter pzconverter = (Converter) Class.forName(sConverter).newInstance();
      return pzconverter.convertValue(value);
    } catch (final IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
      throw new FPConvertException(ex);
    }
  }

  /**
     * Returns a definition of pz column metadata from a given
     * pz datastructure held in an SQL database
     *
     * @param con
     *          Database connection containing the Datafile and Datastructure
     *          tables
     * @param dataDefinition
     *          Name of the data definition stored in the Datafile table
     * @param parser
     * 			Instance of the parser being used for the file.  It will be checked to get the table names
     * 			for the DATASTRUCTURE table and DATAFILE table.
     * @throws SQLException
     * @return List
     */
  public static List<ColumnMetaData> buildMDFromSQLTable(final Connection con, final String dataDefinition, final Parser parser) throws SQLException {
    final List<ColumnMetaData> cmds = new ArrayList<>();
    final String dfTbl = parser != null ? parser.getDataFileTable() : "DATAFILE";
    final String dsTbl = parser != null ? parser.getDataStructureTable() : "DATASTRUCTURE";
    final StringBuilder sqlSb = new StringBuilder();
    sqlSb.append("SELECT * FROM ").append(dfTbl).append(" INNER JOIN ").append(dsTbl).append(" ON ").append(dfTbl).append(".DATAFILE_NO = ").append(dsTbl).append(".DATAFILE_NO " + "WHERE DATAFILE_DESC = ? ORDER BY DATASTRUCTURE_COL_ORDER");
    try (PreparedStatement stmt = con.prepareStatement(sqlSb.toString())) {
      stmt.setString(1, dataDefinition);
      try (ResultSet rs = stmt.executeQuery()) {
        int recPosition = 1;
        while (rs.next()) {
          final ColumnMetaData column = new ColumnMetaData();
          column.setColName(rs.getString("DATASTRUCTURE_COLUMN"));
          column.setColLength(rs.getInt(DATASTRUCTURE_LENGTH));
          column.setStartPosition(recPosition);
          column.setEndPosition(recPosition + rs.getInt(DATASTRUCTURE_LENGTH) - 1);
          recPosition += rs.getInt(DATASTRUCTURE_LENGTH);
          cmds.add(column);
        }
      }
      if (cmds.isEmpty()) {
        throw new FPException("Data File Key [" + dataDefinition + "] Is Not In The database OR No Columns Specified In Table");
      }
    }
    return cmds;
  }
}