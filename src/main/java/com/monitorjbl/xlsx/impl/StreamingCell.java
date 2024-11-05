package com.monitorjbl.xlsx.impl;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import com.monitorjbl.xlsx.exceptions.NotSupportedException;

public class StreamingCell implements Cell {
  private static final Supplier NULL_SUPPLIER = new Supplier() {
    @Override public Object getContent() {
      return null;
    }
  };

  private static final Supplier NOT_SUPPORTED_SUPPLIER = new Supplier() {
    @Override public Object getContent() {
      throw new NotSupportedException();
    }
  };

  private static final String FALSE_AS_STRING = "0";

  private static final String TRUE_AS_STRING = "1";

  private int columnIndex;

  private int rowIndex;

  private final boolean use1904Dates;

  private Supplier commentsTableSupplier = NOT_SUPPORTED_SUPPLIER;

  private Supplier contentsSupplier = NULL_SUPPLIER;

  private Object rawContents;

  private String formula;

  private String numericFormat;

  private Short numericFormatIndex;

  private String type;

  private Row row;

  private CellStyle cellStyle;

  public StreamingCell(int columnIndex, int rowIndex, boolean use1904Dates) {
    this.columnIndex = columnIndex;
    this.rowIndex = rowIndex;
    this.use1904Dates = use1904Dates;
  }

  public void setCommentsTableSupplier(Supplier commentsTableSupplier) {
    this.commentsTableSupplier = commentsTableSupplier;
  }

  public void setContentSupplier(Supplier contentsSupplier) {
    this.contentsSupplier = contentsSupplier;
  }

  public void setRawContents(Object rawContents) {
    this.rawContents = rawContents;
  }

  public String getNumericFormat() {
    return numericFormat;
  }

  public void setNumericFormat(String numericFormat) {
    this.numericFormat = numericFormat;
  }

  public Short getNumericFormatIndex() {
    return numericFormatIndex;
  }

  public void setNumericFormatIndex(Short numericFormatIndex) {
    this.numericFormatIndex = numericFormatIndex;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setRow(Row row) {
    this.row = row;
  }

  @Override public void setCellStyle(CellStyle cellStyle) {
    this.cellStyle = cellStyle;
  }

  @Override public int getColumnIndex() {
    return columnIndex;
  }

  @Override public int getRowIndex() {
    return rowIndex;
  }

  @Override public Row getRow() {
    return row;
  }

  @Override public CellType getCellType() {
    if (formulaType) {
      return CellType.FORMULA;
    } else {
      if (contentsSupplier.getContent() == null || type == null) {
        return CellType.BLANK;
      } else {
        if ("n".equals(type)) {
          return CellType.NUMERIC;
        } else {
          if ("s".equals(type) || "inlineStr".equals(type) || "str".equals(type)) {
            return CellType.STRING;
          } else {
            if ("str".equals(type)) {
              return CellType.FORMULA;
            } else {
              if ("b".equals(type)) {
                return CellType.BOOLEAN;
              } else {
                if ("e".equals(type)) {
                  return CellType.ERROR;
                } else {
                  throw new UnsupportedOperationException("Unsupported cell type \'" + type + "\'");
                }
              }
            }
          }
        }
      }
    }
  }

  @Override public @Deprecated CellType getCellTypeEnum() {
    return getCellType();
  }

  @Override public String getStringCellValue() {
    Object c = contentsSupplier.getContent();
    return c == null ? "" : c.toString();
  }

  @Override public double getNumericCellValue() {
    return rawContents == null ? 0.0 : Double.parseDouble((String) rawContents);
  }

  @Override public Date getDateCellValue() {
    if (getCellType() == CellType.STRING) {
      throw new IllegalStateException("Cell type cannot be CELL_TYPE_STRING");
    }
    return rawContents == null ? null : HSSFDateUtil.getJavaDate(getNumericCellValue(), use1904Dates);
  }

  @Override public boolean getBooleanCellValue() {
    CellType cellType = getCellType();
    switch (cellType) {
      case BLANK:
      return false;
      case BOOLEAN:
      return rawContents != null && TRUE_AS_STRING.equals(rawContents);
      case FORMULA:
      throw new NotSupportedException();
      default:
      throw typeMismatch(CellType.BOOLEAN, cellType, false);
    }
  }

  @Override public CellStyle getCellStyle() {
    return this.cellStyle;
  }

  @Override public String getCellFormula() {
    if (!formulaType) {
      throw new IllegalStateException("This cell does not have a formula");
    }
    return formula;
  }

  @Override public CellType getCachedFormulaResultType() {
    if (formulaType) {
      if (contentsSupplier.getContent() == null || type == null) {
        return CellType.BLANK;
      } else {
        if ("n".equals(type)) {
          return CellType.NUMERIC;
        } else {
          if ("s".equals(type) || "inlineStr".equals(type) || "str".equals(type)) {
            return CellType.STRING;
          } else {
            if ("b".equals(type)) {
              return CellType.BOOLEAN;
            } else {
              if ("e".equals(type)) {
                return CellType.ERROR;
              } else {
                throw new UnsupportedOperationException("Unsupported cell type \'" + type + "\'");
              }
            }
          }
        }
      }
    } else {
      throw new IllegalStateException("Only formula cells have cached results");
    }
  }

  @Override public @Deprecated CellType getCachedFormulaResultTypeEnum() {
    return getCachedFormulaResultType();
  }

  @Override public Comment getCellComment() {
    CellAddress ref = new CellAddress(rowIndex, columnIndex);
    CommentsTable commentsTable = (CommentsTable) this.commentsTableSupplier.getContent();
    CTComment ctComment = commentsTable.getCTComment(ref);
    if (ctComment == null) {
      return null;
    } else {
      return new XSSFComment(commentsTable, ctComment, null);
    }
  }

  @Override public CellAddress getAddress() {
    return new CellAddress(rowIndex, columnIndex);
  }

  @Override public void setCellType(CellType cellType) {
    throw new NotSupportedException();
  }

  @Override public Sheet getSheet() {
    throw new NotSupportedException();
  }

  @Override public void setCellValue(double value) {
    throw new NotSupportedException();
  }

  @Override public void setCellValue(Date value) {
    throw new NotSupportedException();
  }

  @Override public void setCellValue(Calendar value) {
    throw new NotSupportedException();
  }

  @Override public void setCellValue(RichTextString value) {
    throw new NotSupportedException();
  }

  @Override public void setCellValue(String value) {
    throw new NotSupportedException();
  }

  @Override public void setCellFormula(String formula) throws FormulaParseException {
    throw new NotSupportedException();
  }

  @Override public XSSFRichTextString getRichStringCellValue() {
    CellType cellType = getCellType();
    XSSFRichTextString rt;
    switch (cellType) {
      case BLANK:
      rt = new XSSFRichTextString("");
      break;
      case STRING:
      rt = new XSSFRichTextString(getStringCellValue());
      break;
      default:
      throw new NotSupportedException();
    }
    return rt;
  }

  @Override public void setCellValue(boolean value) {
    throw new NotSupportedException();
  }

  @Override public void setCellErrorValue(byte value) {
    throw new NotSupportedException();
  }

  @Override public byte getErrorCellValue() {
    throw new NotSupportedException();
  }

  @Override public void setAsActiveCell() {
    throw new NotSupportedException();
  }

  @Override public void setCellComment(Comment comment) {
    throw new NotSupportedException();
  }

  @Override public void removeCellComment() {
    throw new NotSupportedException();
  }

  @Override public Hyperlink getHyperlink() {
    throw new NotSupportedException();
  }

  @Override public void setHyperlink(Hyperlink link) {
    throw new NotSupportedException();
  }

  @Override public void removeHyperlink() {
    throw new NotSupportedException();
  }

  @Override public CellRangeAddress getArrayFormulaRange() {
    throw new NotSupportedException();
  }

  @Override public boolean isPartOfArrayFormulaGroup() {
    throw new NotSupportedException();
  }

  private boolean formulaType;

  public boolean isFormulaType() {
    return formulaType;
  }

  public void setFormulaType(boolean formulaType) {
    this.formulaType = formulaType;
  }

  private static RuntimeException typeMismatch(CellType expectedType, CellType actualType, boolean isFormulaCell) {
    String msg = "Cannot get a " + getCellTypeName(expectedType) + " value from a " + getCellTypeName(actualType) + " " + (isFormulaCell ? "formula " : "") + "cell";
    return new IllegalStateException(msg);
  }

  private static String getCellTypeName(CellType cellType) {
    switch (cellType) {
      case BLANK:
      return "blank";
      case STRING:
      return "text";
      case BOOLEAN:
      return "boolean";
      case ERROR:
      return "error";
      case NUMERIC:
      return "numeric";
      case FORMULA:
      return "formula";
    }
    return "#unknown cell type (" + cellType + ")#";
  }
}