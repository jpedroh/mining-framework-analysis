package com.alibaba.easyexcel.test.demo.fill;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.easyexcel.test.util.TestFileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 写的填充写法
 *
 * @author Jiaju Zhuang
 * @since 2.1.1
 */
@Ignore public class FillTest {
  /**
     * 最简单的填充
     *
     * @since 2.1.1
     */
  @Test public void simpleFill() {
    String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "simple.xlsx";
    String fileName = TestFileUtil.getPath() + "simpleFill" + System.currentTimeMillis() + ".xlsx";
    FillData fillData = new FillData();
    fillData.setName("\u5f20\u4e09");
    fillData.setNumber(5.2);
    EasyExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(fillData);
    System.out.println(fileName);
  }

  /**
     * 填充列表
     *
     * @since 2.1.1
     */
  @Test public void listFill() {
    String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "list.xlsx";
    String fileName = TestFileUtil.getPath() + "listFill" + System.currentTimeMillis() + ".xlsx";
    Map<String, String> map = new HashMap<>();
    map.put("name", "\u5f20\u4e091");
    map.put("number", "\u5e74\u9f841");
    Map<String, String> map2 = new HashMap<>();
    map2.put("name", "\u5f20\u4e092");
    map2.put("number", "\u5e74\u9f842");
    map2.put("date", "20121212");
    List<Map<String, String>> list = new ArrayList<>();
    list.add(map);
    list.add(map2);
    EasyExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(list);
    System.out.println(fileName);
  }

  /**
     * 复杂的填充
     *
     * @since 2.1.1
     */
  @Test public void complexFill() {
    String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "complex.xlsx";
    String fileName = TestFileUtil.getPath() + "complexFill" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
      excelWriter.fill(data(), fillConfig, writeSheet);
      excelWriter.fill(data(), fillConfig, writeSheet);
      Map<String, Object> map = MapUtils.newHashMap();
      map.put("date", "2019\u5e7410\u67089\u65e513:28:28");
      map.put("total", 1000);
      excelWriter.fill(map, writeSheet);
    }
  }

  /**
     * 数据量大的复杂填充
     * <p>
     * 这里的解决方案是 确保模板list为最后一行，然后再拼接table.还有03版没救，只能刚正面加内存。
     *
     * @since 2.1.1
     */
  @Test public void complexFillWithTable() {
    String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "complexFillWithTable.xlsx";
    String fileName = TestFileUtil.getPath() + "complexFillWithTable" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      excelWriter.fill(data(), writeSheet);
      excelWriter.fill(data(), writeSheet);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("date", "2019\u5e7410\u67089\u65e513:28:28");
      excelWriter.fill(map, writeSheet);
      List<List<String>> totalListList = ListUtils.newArrayList();
      List<String> totalList = ListUtils.newArrayList();
      totalListList.add(totalList);
      totalList.add(null);
      totalList.add(null);
      totalList.add(null);
      totalList.add("\u7edf\u8ba1:1000");
      excelWriter.write(totalListList, writeSheet);
    }
  }

  /**
     * 横向的填充
     *
     * @since 2.1.1
     */
  @Test public void horizontalFill() {
    String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "horizontal.xlsx";
    String fileName = TestFileUtil.getPath() + "horizontalFill" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
      excelWriter.fill(data(), fillConfig, writeSheet);
      excelWriter.fill(data(), fillConfig, writeSheet);
      Map<String, Object> map = new HashMap<>();
      map.put("date", "2019\u5e7410\u67089\u65e513:28:28");
      excelWriter.fill(map, writeSheet);
    }
  }

  /**
     * 多列表组合填充填充
     *
     * @since 2.2.0-beta1
     */
  @Test public void compositeFill() {
    String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "composite.xlsx";
    String fileName = TestFileUtil.getPath() + "compositeFill" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet().build();
      FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
      excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
      excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
      excelWriter.fill(new FillWrapper("data2", data()), writeSheet);
      excelWriter.fill(new FillWrapper("data2", data()), writeSheet);
      excelWriter.fill(new FillWrapper("data3", data()), writeSheet);
      excelWriter.fill(new FillWrapper("data3", data()), writeSheet);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("date", new Date());
      excelWriter.fill(map, writeSheet);
    }
  }

  private List<FillData> data() {
    List<FillData> list = ListUtils.newArrayList();
    FillData firstData = new FillData();
    firstData.setName("\u674e\u56db");
    list.add(firstData);
    for (int i = 0; i < 10; i++) {
      FillData fillData = new FillData();
      list.add(fillData);
      fillData.setName("\u5f20\u4e09");
      fillData.setNumber(5.2);
      fillData.setDate(new Date());
    }
    return list;
  }

  @Test public void a() throws IOException, InvalidFormatException {
    File file = new File("/Users/gongxuanzhang/Desktop/listFill1675236421725.xlsx");
    FileInputStream fileInputStream = new FileInputStream(file);
    Workbook xssfWorkbook = WorkbookFactory.create(fileInputStream);
    fileInputStream.close();
    Sheet sheetAt = xssfWorkbook.getSheetAt(0);
    Row row = sheetAt.getRow(0);
    Cell cell = row.getCell(0);
    cell.setCellValue("bbbb");
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    xssfWorkbook.write(fileOutputStream);
    fileOutputStream.flush();
  }
}