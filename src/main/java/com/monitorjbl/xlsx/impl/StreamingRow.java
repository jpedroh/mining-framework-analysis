package com.monitorjbl.xlsx.impl;
import com.monitorjbl.xlsx.exceptions.NotSupportedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class StreamingRow implements Row {
  private int rowIndex;

  private boolean isHidden;

  private final float rowHeight;

  private final CellStyle rowStyle;

  private TreeMap<Integer, Cell> cellMap = new TreeMap<>();

  public StreamingRow(int rowIndex, boolean isHidden) {
    this(rowIndex, 0, isHidden, null);
  }

  public StreamingRow(int rowIndex, float rowHeight, boolean isHidden, CellStyle rowStyle) {
    this.rowIndex = rowIndex;
    this.rowHeight = rowHeight;
    this.isHidden = isHidden;
    this.rowStyle = rowStyle;
  }

  public Map<Integer, Cell> getCellMap() {
    return cellMap;
  }

  public void setCellMap(TreeMap<Integer, Cell> cellMap) {
    this.cellMap = cellMap;
  }

  @Override public int getRowNum() {
    return rowIndex;
  }

  @Override public Iterator<Cell> cellIterator() {
    return cellMap.values().iterator();
  }

  @Override public Iterator<Cell> iterator() {
    return cellMap.values().iterator();
  }

  @Override public Cell getCell(int cellnum) {
    return cellMap.get(cellnum);
  }

  @Override public short getLastCellNum() {
    return (short) (cellMap.size() == 0 ? -1 : cellMap.lastEntry().getValue().getColumnIndex() + 1);
  }

  @Override public boolean getZeroHeight() {
    return isHidden;
  }

  @Override public int getPhysicalNumberOfCells() {
    return cellMap.size();
  }

  @Override public short getFirstCellNum() {
    if (cellMap.size() == 0) {
      return -1;
    }
    return cellMap.firstKey().shortValue();
  }

  @Override public Cell getCell(int cellnum, MissingCellPolicy policy) {
    StreamingCell cell = (StreamingCell) cellMap.get(cellnum);
    if (policy == MissingCellPolicy.CREATE_NULL_AS_BLANK) {
      if (cell == null) {
        return new StreamingCell(cellnum, rowIndex, false);
      }
    } else {
      if (policy == MissingCellPolicy.RETURN_BLANK_AS_NULL) {
        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
          return null;
        }
      }
    }
    return cell;
  }

  @Override public short getHeight() {
    return (short) (getHeightInPoints() * 20);
  }

  @Override public float getHeightInPoints() {
    return rowHeight;
  }

  @Override public boolean isFormatted() {
    return rowStyle != null;
  }

  @Override public CellStyle getRowStyle() {
    return rowStyle;
  }

  @Override public Cell createCell(int column) {
    throw new NotSupportedException();
  }

  @Override public Cell createCell(int i, CellType cellType) {
    throw new NotSupportedException();
  }

  @Override public void removeCell(Cell cell) {
    throw new NotSupportedException();
  }

  @Override public void setRowNum(int rowNum) {
    throw new NotSupportedException();
  }

  @Override public void setHeight(short height) {
    throw new NotSupportedException();
  }

  @Override public void setZeroHeight(boolean zHeight) {
    throw new NotSupportedException();
  }

  @Override public void setHeightInPoints(float height) {
    throw new NotSupportedException();
  }

  @Override public void setRowStyle(CellStyle style) {
    throw new NotSupportedException();
  }

  @Override public Sheet getSheet() {
    throw new NotSupportedException();
  }

  @Override public int getOutlineLevel() {
    throw new NotSupportedException();
  }

  @Override public void shiftCellsRight(int firstShiftColumnIndex, int lastShiftColumnIndex, int step) {
    throw new NotSupportedException();
  }

  @Override public void shiftCellsLeft(int firstShiftColumnIndex, int lastShiftColumnIndex, int step) {
    throw new NotSupportedException();
  }
}