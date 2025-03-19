package com.smartsheet.api.sample;
import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetBuilder;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.models.Column;
import com.smartsheet.api.models.PagedResult;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;
import java.util.List;

/**
 *
 */
public class Sample {


  public static void main(String[] args) {
    try {
      Smartsheet smartsheet = new SmartsheetBuilder().build();
      PagedResult<Sheet> sheets = smartsheet.sheetResources().listSheets(null, null, null);
      System.out.println("\nFound " + sheets.getTotalCount() + " sheets\n");
      Long sheetId = sheets.getData().get(0).getId();
      Sheet sheet = smartsheet.sheetResources().getSheet(sheetId, null, null, null, null, null, null, null);
      List<Row> rows = sheet.getRows();
      System.out.println("\nLoaded sheet id " + sheetId + " with " + rows.size() + " rows, title: " + sheet.getName());
      for (int rowNumber = 0; rowNumber < rows.size() && rowNumber < 5; rowNumber++) {
        DumpRow(rows.get(rowNumber), sheet.getColumns());
      }
    } catch (SmartsheetException sx) {
      sx.printStackTrace();
    }
    System.out.println("done.");
  }

  static void DumpRow(Row row, List<Column> columns) {
    System.out.println("Row # " + row.getRowNumber() + ":");
    for (int columnNumber = 0; columnNumber < columns.size() && columnNumber < 5; columnNumber++) {
      System.out.println("    " + columns.get(columnNumber).getTitle() + ": " + row.getCells().get(columnNumber).getValue());
    }
  }
}