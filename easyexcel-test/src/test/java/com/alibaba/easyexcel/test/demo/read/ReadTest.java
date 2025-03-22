package com.alibaba.easyexcel.test.demo.read;
import java.io.File;
import java.util.List;
import java.util.Map;
import com.alibaba.easyexcel.test.util.TestFileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.DefaultConverterLoader;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 读的常见写法
 *
 * @author Jiaju Zhuang
 */
@Ignore @Slf4j public class ReadTest {
  /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoDataListener}
     * <p>
     * 3. 直接读即可
     */
  @Test public void simpleRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>((dataList) -> {
      for (DemoData demoData : dataList) {
        log.info("\u8bfb\u53d6\u5230\u4e00\u6761\u6570\u636e{}", JSON.toJSONString(demoData));
      }
    })).sheet().doRead();
    fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, DemoData.class, new ReadListener<DemoData>() {
      /**
             * 单次缓存的数据量
             */
      public static final int BATCH_COUNT = 100;

      /**
             *临时存储
             */
      private List<DemoData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

      @Override public void invoke(DemoData data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
          saveData();
          cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
      }

      @Override public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
      }

      /**
             * 加上存储数据库
             */
      private void saveData() {
        log.info("{}\u6761\u6570\u636e\uff0c\u5f00\u59cb\u5b58\u50a8\u6570\u636e\u5e93\uff01", cachedDataList.size());
        log.info("\u5b58\u50a8\u6570\u636e\u5e93\u6210\u529f\uff01");
      }
    }).sheet().doRead();
    fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
    fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    try (ExcelReader excelReader = EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).build()) {
      ReadSheet readSheet = EasyExcel.readSheet(0).build();
      excelReader.read(readSheet);
    }
  }

  /**
     * 指定列的下标或者列名
     *
     * <p>
     * 1. 创建excel对应的实体对象,并使用{@link ExcelProperty}注解. 参照{@link IndexOrNameData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link IndexOrNameDataListener}
     * <p>
     * 3. 直接读即可
     */
  @Test public void indexOrNameRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, IndexOrNameData.class, new IndexOrNameDataListener()).sheet().doRead();
  }

  /**
     * 读多个或者全部sheet,这里注意一个sheet不能读取多次，多次读取需要重新读取文件
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoDataListener}
     * <p>
     * 3. 直接读即可
     */
  @Test public void repeatedRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();
    fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    try (ExcelReader excelReader = EasyExcel.read(fileName).build()) {
      ReadSheet readSheet1 = EasyExcel.readSheet(0).head(DemoData.class).registerReadListener(new DemoDataListener()).build();
      ReadSheet readSheet2 = EasyExcel.readSheet(1).head(DemoData.class).registerReadListener(new DemoDataListener()).build();
      excelReader.read(readSheet1, readSheet2);
    }
  }

  /**
     * 日期、数字或者自定义格式转换
     * <p>
     * 默认读的转换器{@link DefaultConverterLoader#loadDefaultReadConverter()}
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ConverterData}.里面可以使用注解{@link DateTimeFormat}、{@link NumberFormat}或者自定义注解
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link ConverterDataListener}
     * <p>
     * 3. 直接读即可
     */
  @Test public void converterRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, ConverterData.class, new ConverterDataListener()).sheet().doRead();
  }

  /**
     * 多行头
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoDataListener}
     * <p>
     * 3. 设置headRowNumber参数，然后读。 这里要注意headRowNumber如果不指定， 会根据你传入的class的{@link ExcelProperty#value()}里面的表头的数量来决定行数，
     * 如果不传入class则默认为1.当然你指定了headRowNumber不管是否传入class都是以你传入的为准。
     */
  @Test public void complexHeaderRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().headRowNumber(1).doRead();
  }

  /**
     * 读取表头数据
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoHeadDataListener}
     * <p>
     * 3. 直接读即可
     */
  @Test public void headerRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, DemoData.class, new DemoHeadDataListener()).sheet().doRead();
  }

  /**
     * 额外信息（批注、超链接、合并单元格信息读取）
     * <p>
     * 由于是流式读取，没法在读取到单元格数据的时候直接读取到额外信息，所以只能最后通知哪些单元格有哪些额外信息
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoExtraData}
     * <p>
     * 2. 由于默认异步读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoExtraListener}
     * <p>
     * 3. 直接读即可
     *
     * @since 2.2.0-beat1
     */
  @Test public void extraRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "extra.xlsx";
    EasyExcel.read(fileName, DemoExtraData.class, new DemoExtraListener()).extraRead(CellExtraTypeEnum.COMMENT).extraRead(CellExtraTypeEnum.HYPERLINK).extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
  }

  /**
     * 读取公式和单元格类型
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link CellDataReadDemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoHeadDataListener}
     * <p>
     * 3. 直接读即可
     *
     * @since 2.2.0-beat1
     */
  @Test public void cellDataRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "cellDataDemo.xlsx";
    EasyExcel.read(fileName, CellDataReadDemoData.class, new CellDataDemoHeadDataListener()).sheet().doRead();
  }

  /**
     * 数据转换等异常处理
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link ExceptionDemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoExceptionListener}
     * <p>
     * 3. 直接读即可
     */
  @Test public void exceptionRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, ExceptionDemoData.class, new DemoExceptionListener()).sheet().doRead();
  }

  /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
  @Test public void synchronousRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    List<DemoData> list = EasyExcel.read(fileName).head(DemoData.class).sheet().doReadSync();
    for (DemoData data : list) {
      log.info("\u8bfb\u53d6\u5230\u6570\u636e:{}", JSON.toJSONString(data));
    }
    List<Map<Integer, String>> listMap = EasyExcel.read(fileName).sheet().doReadSync();
    for (Map<Integer, String> data : listMap) {
      log.info("\u8bfb\u53d6\u5230\u6570\u636e:{}", JSON.toJSONString(data));
    }
  }

  /**
     * 不创建对象的读
     */
  @Test public void noModelRead() {
    String fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
    EasyExcel.read(fileName, new NoModelDataListener()).sheet().doRead();
  }
}