package com.alibaba.easyexcel.test.demo.read;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

/**
 * 读取单元格的批注
 *
 * @author Jiaju Zhuang
 **/
@Slf4j public class DemoExtraListener implements ReadListener<DemoExtraData> {
  @Override public void invoke(DemoExtraData data, AnalysisContext context) {
  }

  @Override public void doAfterAllAnalysed(AnalysisContext context) {
  }

  @Override public void extra(CellExtra extra, AnalysisContext context) {
    log.info("\u8bfb\u53d6\u5230\u4e86\u4e00\u6761\u989d\u5916\u4fe1\u606f:{}", JSON.toJSONString(extra));
    switch (extra.getType()) {
      case COMMENT:
      log.info("\u989d\u5916\u4fe1\u606f\u662f\u6279\u6ce8,\u5728rowIndex:{},columnIndex;{},\u5185\u5bb9\u662f:{}", extra.getRowIndex(), extra.getColumnIndex(), extra.getText());
      break;
      case HYPERLINK:
      if ("Sheet1!A1".equals(extra.getText())) {
        log.info("\u989d\u5916\u4fe1\u606f\u662f\u8d85\u94fe\u63a5,\u5728rowIndex:{},columnIndex;{},\u5185\u5bb9\u662f:{}", extra.getRowIndex(), extra.getColumnIndex(), extra.getText());
      } else {
        if ("Sheet2!A1".equals(extra.getText())) {
          log.info("\u989d\u5916\u4fe1\u606f\u662f\u8d85\u94fe\u63a5,\u800c\u4e14\u8986\u76d6\u4e86\u4e00\u4e2a\u533a\u95f4,\u5728firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{}," + "\u5185\u5bb9\u662f:{}", extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(), extra.getLastColumnIndex(), extra.getText());
        } else {
          Assert.fail("Unknown hyperlink!");
        }
      }
      break;
      case MERGE:
      log.info("\u989d\u5916\u4fe1\u606f\u662f\u5408\u5e76\u5355\u5143\u683c,\u800c\u4e14\u8986\u76d6\u4e86\u4e00\u4e2a\u533a\u95f4,\u5728firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{}", extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(), extra.getLastColumnIndex());
      break;
      default:
    }
  }
}