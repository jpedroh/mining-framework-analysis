package com.alibaba.easyexcel.test.core.annotation;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;

/**
 * @author Jiaju Zhuang
 */
public class AnnotationIndexAndNameDataListener extends AnalysisEventListener<AnnotationIndexAndNameData> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationIndexAndNameDataListener.class);

  private final List<AnnotationIndexAndNameData> list = new ArrayList<>();

  @Override public void invoke(AnnotationIndexAndNameData data, AnalysisContext context) {
    list.add(data);
  }

  @Override public void doAfterAllAnalysed(AnalysisContext context) {
    Assert.assertEquals(list.size(), 1);
    AnnotationIndexAndNameData data = list.get(0);
    Assert.assertEquals(data.getIndex0(), "\u7b2c0\u4e2a");
    Assert.assertEquals(data.getIndex1(), "\u7b2c1\u4e2a");
    Assert.assertEquals(data.getIndex2(), "\u7b2c2\u4e2a");
    Assert.assertEquals(data.getIndex4(), "\u7b2c4\u4e2a");
    LOGGER.debug("First row:{}", JSON.toJSONString(list.get(0)));
  }
}