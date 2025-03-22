package com.alibaba.easyexcel.test.temp.simple;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.alibaba.easyexcel.test.core.large.LargeData;
import com.alibaba.easyexcel.test.demo.write.DemoData;
import com.alibaba.easyexcel.test.util.TestFileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.BeanMapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试poi
 *
 * @author Jiaju Zhuang
 **/
@Ignore @Slf4j public class Write {
  private static final Logger LOGGER = LoggerFactory.getLogger(Write.class);

  @Test public void simpleWrite1() {
    LargeData ss = new LargeData();
    ss.setStr23("ttt");
    Map map = BeanMapUtils.create(ss);
    System.out.println(map.containsKey("str23"));
    System.out.println(map.containsKey("str22"));
    System.out.println(map.get("str23"));
    System.out.println(map.get("str22"));
  }

  @Test public void simpleWrite() {
    log.info("t5");
    String fileName = TestFileUtil.getPath() + "t22" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, DemoData.class).relativeHeadRowIndex(10).sheet("\u6a21\u677f").doWrite(data());
  }

  @Test public void simpleWrite2() {
    String fileName = TestFileUtil.getPath() + "t22" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName, WriteData.class).sheet("\u6a21\u677f").registerWriteHandler(new WriteHandler()).doWrite(data1());
  }

  @Test public void simpleWrite3() {
    String fileName = TestFileUtil.getPath() + "t33" + System.currentTimeMillis() + ".xlsx";
    EasyExcel.write(fileName).head(head()).inMemory(true).sheet("\u6a21\u677f").registerWriteHandler(new WriteCellHandler()).doWrite(data1());
  }

  @Test public void json() {
    JsonData jsonData = new JsonData();
    jsonData.setSS1("11");
    jsonData.setSS2("22");
    jsonData.setSs3("33");
    System.out.println(JSON.toJSONString(jsonData));
  }

  @Test public void json3() {
    String json = "{\"SS1\":\"11\",\"sS2\":\"22\",\"ss3\":\"33\"}";
    JsonData jsonData = JSON.parseObject(json, JsonData.class);
    System.out.println(JSON.toJSONString(jsonData));
  }

  @Test public void tableWrite() {
    String fileName = TestFileUtil.getPath() + "tableWrite" + System.currentTimeMillis() + ".xlsx";
    ExcelWriter excelWriter = EasyExcel.write(fileName).build();
    WriteSheet writeSheet = EasyExcel.writerSheet("\u6a21\u677f").build();
    WriteTable writeTable0 = EasyExcel.writerTable(0).head(DemoData1.class).build();
    excelWriter.write(data(), writeSheet, writeTable0);
    excelWriter.finish();
  }

  private List<List<String>> head() {
    List<List<String>> list = new ArrayList<List<String>>();
    List<String> head0 = new ArrayList<String>();
    head0.add("\u5b57\u7b26\u4e32" + System.currentTimeMillis());
    List<String> head1 = new ArrayList<String>();
    head1.add("\u6570\u5b57" + System.currentTimeMillis());
    List<String> head2 = new ArrayList<String>();
    head2.add("\u65e5\u671f" + System.currentTimeMillis());
    list.add(head0);
    list.add(head1);
    list.add(head2);
    return list;
  }

  private List<DemoData> data() {
    List<DemoData> list = new ArrayList<DemoData>();
    for (int i = 0; i < 10; i++) {
      DemoData data = new DemoData();
      data.setString("640121807369666560" + i);
      data.setDate(new Date());
      data.setDoubleData(null);
      list.add(data);
    }
    return list;
  }

  private List<WriteData> data1() {
    List<WriteData> list = new ArrayList<WriteData>();
    for (int i = 0; i < 10; i++) {
      WriteData data = new WriteData();
      data.setDd(new Date());
      data.setF1(33f);
      list.add(data);
    }
    return list;
  }

  @Test public void read() {
    List<Map<Integer, Object>> list = new ArrayList<>();
    String name = "/Users/gongxuanzhang/Downloads/test.xls";
    EasyExcel.read(name, new I(list)).sheet().headRowNumber(3).doRead();
  }

  public static class I implements ReadListener<Map<Integer, Object>> {
    private final List<Map<Integer, Object>> list;

    I(List<Map<Integer, Object>> list) {
      this.list = list;
    }

    @Override public void invoke(Map<Integer, Object> data, AnalysisContext context) {
      System.out.println(data);
      list.add(data);
    }

    @Override public void doAfterAllAnalysed(AnalysisContext context) {
    }
  }
}