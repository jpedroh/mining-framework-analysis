package com.alibaba.easyexcel.test.demo.write;
import com.alibaba.easyexcel.test.util.TestFileUtil;
import java.io.File;
import com.alibaba.excel.EasyExcel;
import java.io.InputStream;
import com.alibaba.excel.ExcelWriter;
import java.net.URL;
import com.alibaba.excel.annotation.ExcelProperty;
import java.util.ArrayList;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.util.Date;
import com.alibaba.excel.annotation.format.NumberFormat;
import java.util.HashSet;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import java.util.List;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import java.util.Set;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.CommentData;
import com.alibaba.excel.metadata.data.FormulaData;
import com.alibaba.excel.metadata.data.HyperlinkData;
import com.alibaba.excel.metadata.data.HyperlinkData.HyperlinkType;
import com.alibaba.excel.metadata.data.ImageData;
import com.alibaba.excel.metadata.data.ImageData.ImageType;
import com.alibaba.excel.metadata.data.RichTextStringData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.util.FileUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.merge.LoopMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 写的常见写法
 *
 * @author Jiaju Zhuang
 */
@Ignore public class WriteTest {
  /**
     * 最简单的写
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 直接写即可
     */
  @Test public void simpleWrite() {
    String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).sheet("\u6a21\u677f").doWrite(() -> {
      return data();
    });
    fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).sheet("\u6a21\u677f").doWrite(data());
    fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet("\u6a21\u677f").build();
      excelWriter.write(data(), writeSheet);
    }
  }

  /**
     * 根据参数只导出指定列
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 根据自己或者排除自己需要的列
     * <p>
     * 3. 直接写即可
     *
     * @since 2.1.1
     */
  @Test public void excludeOrIncludeWrite() {
    String fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
    Set<String> excludeColumnFieldNames = new HashSet<>();
    excludeColumnFieldNames.add("date");
    EasyExcel.write(fileName, DemoData.class).excludeColumnFieldNames(excludeColumnFieldNames).sheet("\u6a21\u677f").doWrite(data());
    fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
    Set<String> includeColumnFieldNames = new HashSet<>();
    includeColumnFieldNames.add("date");
    EasyExcel.write(fileName, DemoData.class).includeColumnFieldNames(includeColumnFieldNames).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 指定写入的列
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link IndexData}
     * <p>
     * 2. 使用{@link ExcelProperty}注解指定写入的列
     * <p>
     * 3. 直接写即可
     */
  @Test public void indexWrite() {
    String fileName = TestFileUtil.getPath() + "indexWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, IndexData.class).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 复杂头写入
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ComplexHeadData}
     * <p>
     * 2. 使用{@link ExcelProperty}注解指定复杂的头
     * <p>
     * 3. 直接写即可
     */
  @Test public void complexHeadWrite() {
    String fileName = TestFileUtil.getPath() + "complexHeadWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, ComplexHeadData.class).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 重复多次写入
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ComplexHeadData}
     * <p>
     * 2. 使用{@link ExcelProperty}注解指定复杂的头
     * <p>
     * 3. 直接调用二次写入即可
     */
  @Test public void repeatedWrite() {
    String fileName = TestFileUtil.getPath() + "repeatedWrite" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet("\u6a21\u677f").build();
      for (int i = 0; i < 5; i++) {
        List<DemoData> data = data();
        excelWriter.write(data, writeSheet);
      }
    }
    fileName = TestFileUtil.getPath() + "repeatedWrite" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build()) {
      for (int i = 0; i < 5; i++) {
        WriteSheet writeSheet = EasyExcel.writerSheet(i, "\u6a21\u677f" + i).build();
        List<DemoData> data = data();
        excelWriter.write(data, writeSheet);
      }
    }
    fileName = TestFileUtil.getPath() + "repeatedWrite" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName).build()) {
      for (int i = 0; i < 5; i++) {
        WriteSheet writeSheet = EasyExcel.writerSheet(i, "\u6a21\u677f" + i).head(DemoData.class).build();
        List<DemoData> data = data();
        excelWriter.write(data, writeSheet);
      }
    }
  }

  /**
     * 日期、数字或者自定义格式转换
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ConverterData}
     * <p>
     * 2. 使用{@link ExcelProperty}配合使用注解{@link DateTimeFormat}、{@link NumberFormat}或者自定义注解
     * <p>
     * 3. 直接写即可
     */
  @Test public void converterWrite() {
    String fileName = TestFileUtil.getPath() + "converterWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, ConverterData.class).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 图片导出
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ImageDemoData}
     * <p>
     * 2. 直接写即可
     */
  @Test public void imageWrite() throws Exception {
    String fileName = TestFileUtil.getPath() + "imageWrite" + System.currentTimeMillis() + ".xlsx";
    String imagePath = TestFileUtil.getPath() + "converter" + File.separator + "img.jpg";
    try (InputStream inputStream = FileUtils.openInputStream(new File(imagePath))) {
      List<ImageDemoData> list = ListUtils.newArrayList();
      ImageDemoData imageDemoData = new ImageDemoData();
      list.add(imageDemoData);
      imageDemoData.setByteArray(FileUtils.readFileToByteArray(new File(imagePath)));
      imageDemoData.setFile(new File(imagePath));
      imageDemoData.setString(imagePath);
      imageDemoData.setInputStream(inputStream);
      imageDemoData.setUrl(new URL("https://raw.githubusercontent.com/alibaba/easyexcel/master/src/test/resources/converter/img.jpg"));
      WriteCellData<Void> writeCellData = new WriteCellData<>();
      imageDemoData.setWriteCellDataFile(writeCellData);
      writeCellData.setType(CellDataTypeEnum.STRING);
      writeCellData.setStringValue("\u989d\u5916\u7684\u653e\u4e00\u4e9b\u6587\u5b57");
      List<ImageData> imageDataList = new ArrayList<>();
      ImageData imageData = new ImageData();
      imageDataList.add(imageData);
      writeCellData.setImageDataList(imageDataList);
      imageData.setImage(FileUtils.readFileToByteArray(new File(imagePath)));
      imageData.setImageType(ImageType.PICTURE_TYPE_PNG);
      imageData.setTop(5);
      imageData.setRight(40);
      imageData.setBottom(5);
      imageData.setLeft(5);
      imageData = new ImageData();
      imageDataList.add(imageData);
      writeCellData.setImageDataList(imageDataList);
      imageData.setImage(FileUtils.readFileToByteArray(new File(imagePath)));
      imageData.setImageType(ImageType.PICTURE_TYPE_PNG);
      imageData.setTop(5);
      imageData.setRight(5);
      imageData.setBottom(5);
      imageData.setLeft(50);
      imageData.setRelativeFirstRowIndex(0);
      imageData.setRelativeFirstColumnIndex(0);
      imageData.setRelativeLastRowIndex(0);
      imageData.setRelativeLastColumnIndex(1);
      EasyExcel.write(fileName, ImageDemoData.class).sheet().doWrite(list);
    }
  }

  /**
     * 超链接、备注、公式、指定单个单元格的样式、单个单元格多种样式
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link WriteCellDemoData}
     * <p>
     * 2. 直接写即可
     *
     * @since 3.0.0-beta1
     */
  @Test public void writeCellDataWrite() {
    String fileName = TestFileUtil.getPath() + "writeCellDataWrite" + System.currentTimeMillis() + ".xlsx";
    WriteCellDemoData writeCellDemoData = new WriteCellDemoData();
    WriteCellData<String> hyperlink = new WriteCellData<>("\u5b98\u65b9\u7f51\u7ad9");
    writeCellDemoData.setHyperlink(hyperlink);
    HyperlinkData hyperlinkData = new HyperlinkData();
    hyperlink.setHyperlinkData(hyperlinkData);
    hyperlinkData.setAddress("https://github.com/alibaba/easyexcel");
    hyperlinkData.setHyperlinkType(HyperlinkType.URL);
    WriteCellData<String> comment = new WriteCellData<>("\u5907\u6ce8\u7684\u5355\u5143\u683c\u4fe1\u606f");
    writeCellDemoData.setCommentData(comment);
    CommentData commentData = new CommentData();
    comment.setCommentData(commentData);
    commentData.setAuthor("Jiaju Zhuang");
    commentData.setRichTextStringData(new RichTextStringData("\u8fd9\u662f\u4e00\u4e2a\u5907\u6ce8"));
    commentData.setRelativeLastColumnIndex(1);
    commentData.setRelativeLastRowIndex(1);
    WriteCellData<String> formula = new WriteCellData<>();
    writeCellDemoData.setFormulaData(formula);
    FormulaData formulaData = new FormulaData();
    formula.setFormulaData(formulaData);
    formulaData.setFormulaValue("REPLACE(123456789,1,1,2)");
    WriteCellData<String> writeCellStyle = new WriteCellData<>("\u5355\u5143\u683c\u6837\u5f0f");
    writeCellStyle.setType(CellDataTypeEnum.STRING);
    writeCellDemoData.setWriteCellStyle(writeCellStyle);
    WriteCellStyle writeCellStyleData = new WriteCellStyle();
    writeCellStyle.setWriteCellStyle(writeCellStyleData);
    writeCellStyleData.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
    writeCellStyleData.setFillForegroundColor(IndexedColors.GREEN.getIndex());
    WriteCellData<String> richTest = new WriteCellData<>();
    richTest.setType(CellDataTypeEnum.RICH_TEXT_STRING);
    writeCellDemoData.setRichText(richTest);
    RichTextStringData richTextStringData = new RichTextStringData();
    richTest.setRichTextStringDataValue(richTextStringData);
    richTextStringData.setTextString("\u7ea2\u8272\u7eff\u8272\u9ed8\u8ba4");
    WriteFont writeFont = new WriteFont();
    writeFont.setColor(IndexedColors.RED.getIndex());
    richTextStringData.applyFont(0, 2, writeFont);
    writeFont = new WriteFont();
    writeFont.setColor(IndexedColors.GREEN.getIndex());
    richTextStringData.applyFont(2, 4, writeFont);
    List<WriteCellDemoData> data = new ArrayList<>();
    data.add(writeCellDemoData);
    EasyExcel.write(fileName, WriteCellDemoData.class).inMemory(true).sheet("\u6a21\u677f").doWrite(data);
  }

  /**
     * 根据模板写入
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link IndexData}
     * <p>
     * 2. 使用{@link ExcelProperty}注解指定写入的列
     * <p>
     * 3. 使用withTemplate 写取模板
     * <p>
     * 4. 直接写即可
     */
  @Test public void templateWrite() {
    String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    String fileName = TestFileUtil.getPath() + "templateWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).withTemplate(templateFileName).sheet().doWrite(data());
  }

  /**
     * 列宽、行高
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link WidthAndHeightData}
     * <p>
     * 2. 使用注解{@link ColumnWidth}、{@link HeadRowHeight}、{@link ContentRowHeight}指定宽度或高度
     * <p>
     * 3. 直接写即可
     */
  @Test public void widthAndHeightWrite() {
    String fileName = TestFileUtil.getPath() + "widthAndHeightWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, WidthAndHeightData.class).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 注解形式自定义样式
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoStyleData}
     * <p>
     * 3. 直接写即可
     *
     * @since 2.2.0-beta1
     */
  @Test public void annotationStyleWrite() {
    String fileName = TestFileUtil.getPath() + "annotationStyleWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoStyleData.class).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 拦截器形式自定义样式
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 创建一个style策略 并注册
     * <p>
     * 3. 直接写即可
     */
  @Test public void handlerStyleWrite() {
    String fileName = TestFileUtil.getPath() + "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";
    WriteCellStyle headWriteCellStyle = new WriteCellStyle();
    headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
    WriteFont headWriteFont = new WriteFont();
    headWriteFont.setFontHeightInPoints((short) 20);
    headWriteCellStyle.setWriteFont(headWriteFont);
    WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
    contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
    contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
    WriteFont contentWriteFont = new WriteFont();
    contentWriteFont.setFontHeightInPoints((short) 20);
    contentWriteCellStyle.setWriteFont(contentWriteFont);
    HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    EasyExcel.write(fileName, DemoData.class).registerWriteHandler(horizontalCellStyleStrategy).sheet("\u6a21\u677f").doWrite(data());
    fileName = TestFileUtil.getPath() + "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).registerWriteHandler(new CellWriteHandler() {
      @Override public void afterCellDispose(CellWriteHandlerContext context) {
        if (BooleanUtils.isNotTrue(context.getHead())) {
          WriteCellData<?> cellData = context.getFirstCellData();
          WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
          writeCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
          writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        }
      }
    }).sheet("\u6a21\u677f").doWrite(data());
    fileName = TestFileUtil.getPath() + "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).registerWriteHandler(new CellWriteHandler() {
      @Override public void afterCellDispose(CellWriteHandlerContext context) {
        if (BooleanUtils.isNotTrue(context.getHead())) {
          Cell cell = context.getCell();
          Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
          CellStyle cellStyle = workbook.createCellStyle();
          cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
          cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
          cell.setCellStyle(cellStyle);
          context.getFirstCellData().setWriteCellStyle(null);
        }
      }
    }).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 合并单元格
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData} {@link DemoMergeData}
     * <p>
     * 2. 创建一个merge策略 并注册
     * <p>
     * 3. 直接写即可
     *
     * @since 2.2.0-beta1
     */
  @Test public void mergeWrite() {
    String fileName = TestFileUtil.getPath() + "mergeWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoMergeData.class).sheet("\u6a21\u677f").doWrite(data());
    fileName = TestFileUtil.getPath() + "mergeWrite" + System.currentTimeMillis() + ".xlsx";
    LoopMergeStrategy loopMergeStrategy = new LoopMergeStrategy(2, 0);
    EasyExcel.write(fileName, DemoData.class).registerWriteHandler(loopMergeStrategy).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 自定义合并单元格
     **/
  @Test public void customWrite() {
    String fileName = TestFileUtil.getPath() + "mergeWrite" + System.currentTimeMillis() + ".xlsx";
    LoopMergeStrategy loopMergeStrategy = new LoopMergeStrategy(2, 0);
    loopMergeStrategy.setValueSetting((mergedCell, values) -> {
      StringBuilder mergedValue = new StringBuilder();
      for (Object[] rowValues : values) {
        for (Object cellValue : rowValues) {
          if (cellValue != null) {
            mergedValue.append(cellValue);
          }
        }
      }
      mergedCell.setCellValue(mergedValue.toString());
    });
    EasyExcel.write(fileName, DemoData.class).registerWriteHandler(loopMergeStrategy).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 使用table去写入
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 然后写入table即可
     */
  @Test public void tableWrite() {
    String fileName = TestFileUtil.getPath() + "tableWrite" + System.currentTimeMillis() + ".xlsx";
    try (ExcelWriter excelWriter = EasyExcel.write(fileName, DemoData.class).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet("\u6a21\u677f").needHead(Boolean.FALSE).build();
      WriteTable writeTable0 = EasyExcel.writerTable(0).needHead(Boolean.TRUE).build();
      WriteTable writeTable1 = EasyExcel.writerTable(1).needHead(Boolean.TRUE).build();
      excelWriter.write(data(), writeSheet, writeTable0);
      excelWriter.write(data(), writeSheet, writeTable1);
    }
  }

  /**
     * 动态头，实时生成头写入
     * <p>
     * 思路是这样子的，先创建List<String>头格式的sheet仅仅写入头,然后通过table 不写入头的方式 去写入数据
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 然后写入table即可
     */
  @Test public void dynamicHeadWrite() {
    String fileName = TestFileUtil.getPath() + "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName).head(head()).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 自动列宽(不太精确)
     * <p>
     * 这个目前不是很好用，比如有数字就会导致换行。而且长度也不是刚好和实际长度一致。 所以需要精确到刚好列宽的慎用。 当然也可以自己参照 {@link LongestMatchColumnWidthStyleStrategy}
     * 重新实现.
     * <p>
     * poi 自带{@link SXSSFSheet#autoSizeColumn(int)} 对中文支持也不太好。目前没找到很好的算法。 有的话可以推荐下。
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link LongestMatchColumnWidthData}
     * <p>
     * 2. 注册策略{@link LongestMatchColumnWidthStyleStrategy}
     * <p>
     * 3. 直接写即可
     */
  @Test public void longestMatchColumnWidthWrite() {
    String fileName = TestFileUtil.getPath() + "longestMatchColumnWidthWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, LongestMatchColumnWidthData.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("\u6a21\u677f").doWrite(dataLong());
  }

  /**
     * 下拉，超链接等自定义拦截器（上面几点都不符合但是要对单元格进行操作的参照这个）
     * <p>
     * demo这里实现2点。1. 对第一行第一列的头超链接到:https://github.com/alibaba/easyexcel 2. 对第一列第一行和第二行的数据新增下拉框，显示 测试1 测试2
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 注册拦截器 {@link CustomCellWriteHandler} {@link CustomSheetWriteHandler}
     * <p>
     * 2. 直接写即可
     */
  @Test public void customHandlerWrite() {
    String fileName = TestFileUtil.getPath() + "customHandlerWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).registerWriteHandler(new CustomSheetWriteHandler()).registerWriteHandler(new CustomCellWriteHandler()).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 插入批注
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 注册拦截器 {@link CommentWriteHandler}
     * <p>
     * 2. 直接写即可
     */
  @Test public void commentWrite() {
    String fileName = TestFileUtil.getPath() + "commentWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).inMemory(Boolean.TRUE).registerWriteHandler(new CommentWriteHandler()).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 可变标题处理(包括标题国际化等)
     * <p>
     * 简单的说用List<List<String>>的标题 但是还支持注解
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ConverterData}
     * <p>
     * 2. 直接写即可
     */
  @Test public void variableTitleWrite() {
    String fileName = TestFileUtil.getPath() + "variableTitleWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, ConverterData.class).head(variableTitleHead()).sheet("\u6a21\u677f").doWrite(data());
  }

  /**
     * 不创建对象的写
     */
  @Test public void noModelWrite() {
    String fileName = TestFileUtil.getPath() + "noModelWrite" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName).head(head()).sheet("\u6a21\u677f").doWrite(dataList());
  }

  private List<LongestMatchColumnWidthData> dataLong() {
    List<LongestMatchColumnWidthData> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
      LongestMatchColumnWidthData data = new LongestMatchColumnWidthData();
      data.setString("\u6d4b\u8bd5\u5f88\u957f\u7684\u5b57\u7b26\u4e32\u6d4b\u8bd5\u5f88\u957f\u7684\u5b57\u7b26\u4e32\u6d4b\u8bd5\u5f88\u957f\u7684\u5b57\u7b26\u4e32" + i);
      data.setDate(new Date());
      data.setDoubleData(1000000000000.0);
      list.add(data);
    }
    return list;
  }

  private List<List<String>> variableTitleHead() {
    List<List<String>> list = ListUtils.newArrayList();
    List<String> head0 = ListUtils.newArrayList();
    head0.add("string" + System.currentTimeMillis());
    List<String> head1 = ListUtils.newArrayList();
    head1.add("number" + System.currentTimeMillis());
    List<String> head2 = ListUtils.newArrayList();
    head2.add("date" + System.currentTimeMillis());
    list.add(head0);
    list.add(head1);
    list.add(head2);
    return list;
  }

  private List<List<String>> head() {
    List<List<String>> list = ListUtils.newArrayList();
    List<String> head0 = ListUtils.newArrayList();
    head0.add("\u5b57\u7b26\u4e32" + System.currentTimeMillis());
    List<String> head1 = ListUtils.newArrayList();
    head1.add("\u6570\u5b57" + System.currentTimeMillis());
    List<String> head2 = ListUtils.newArrayList();
    head2.add("\u65e5\u671f" + System.currentTimeMillis());
    list.add(head0);
    list.add(head1);
    list.add(head2);
    return list;
  }

  private List<List<Object>> dataList() {
    List<List<Object>> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
      List<Object> data = ListUtils.newArrayList();
      data.add("\u5b57\u7b26\u4e32" + i);
      data.add(0.56);
      data.add(new Date());
      list.add(data);
    }
    return list;
  }

  private List<DemoData> data() {
    List<DemoData> list = ListUtils.newArrayList();
    for (int i = 0; i < 10; i++) {
      DemoData data = new DemoData();
      data.setString("\u5b57\u7b26\u4e32" + i);
      data.setDate(new Date());
      data.setDoubleData(0.56);
      list.add(data);
    }
    return list;
  }
}