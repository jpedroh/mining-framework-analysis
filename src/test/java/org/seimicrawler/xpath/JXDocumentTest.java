package org.seimicrawler.xpath;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JXDocument Tester.
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @version 1.0
 */
@RunWith(value = DataProviderRunner.class) public class JXDocumentTest {
  private JXDocument underTest;

  private JXDocument doubanTest;

  private JXDocument custom;

  private ClassLoader loader = getClass().getClassLoader();

  private Logger logger = LoggerFactory.getLogger(JXDocumentTest.class);

  @Before public void before() throws Exception {
    String html = "<html><body><script>console.log(\'aaaaa\')</script><div class=\'test\'>some body</div><div class=\'xiao\'>Two</div></body></html>";
    underTest = JXDocument.create(html);
    if (doubanTest == null) {
      URL t = loader.getResource("d_test.html");
      assert t != null;
      File dBook = new File(t.toURI());
      String context = FileUtils.readFileToString(dBook, Charset.forName("utf8"));
      doubanTest = JXDocument.create(context);
    }
    custom = JXDocument.create("<li><b>\u6027\u522b\uff1a</b>\u7537</li>");
  }

  /**
     * Method: sel(String xpath)
     */
  @Test public void testSel() throws Exception {
    String xpath = "//script[1]/text()";
    JXNode res = underTest.selNOne(xpath);
    Assert.assertNotNull(res);
    Assert.assertEquals("console.log(\'aaaaa\')", res.asString());
  }

  @Test public void testNotMatchFilter() throws Exception {
    String xpath = "//div[contains(@class,\'xiao\')]/text()";
    JXNode node = underTest.selNOne(xpath);
    Assert.assertEquals("Two", node.asString());
  }

  @Test @DataProvider(value = { "//a/@href", "//div[@class=\'paginator\']/span[@class=\'next\']/a/@href" }) public void testXpath(String xpath) throws XpathSyntaxErrorException {
    logger.info("current xpath: {}", xpath);
    List<JXNode> rs = doubanTest.selN(xpath);
    for (JXNode n : rs) {
      if (!n.isString()) {
        int index = n.asElement().siblingIndex();
        logger.info("index = {}", index);
      }
      logger.info(n.toString());
    }
  }

  /**
     * d_test.html 来源于 https://book.douban.com/tag/%E4%BA%92%E8%81%94%E7%BD%91
     *
     * 为了测试各种可能情况，ul[@class='subject-list']节点以及其下内容被复制了一份出来，并修改部分书名前缀为'T2-'以便区分
     */
  @DataProvider public static Object[][] dataOfXpathAndexpect() {
    return new Object[][] { { "//ul[@class=\'subject-list\']/li[position()<3][last()]/div/h2/allText()", "\u9ed1\u5ba2\u4e0e\u753b\u5bb6 : \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6T2-\u9ed1\u5ba2\u4e0e\u753b\u5bb6 : \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6" }, { "//ul[@class=\'subject-list\']/li[2]/div/h2//text()", "\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6T2-\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6" }, { "//ul[@class=\'subject-list\']/li[first()]/div/h2/allText()", "\u5931\u63a7 : \u5168\u4eba\u7c7b\u7684\u6700\u7ec8\u547d\u8fd0\u548c\u7ed3\u5c40T2-\u5931\u63a7 : \u5168\u4eba\u7c7b\u7684\u6700\u7ec8\u547d\u8fd0\u548c\u7ed3\u5c40" }, { "//ul[@class=\'subject-list\']/li[./div/div/span[@class=\'pl\']/num()>(1000+90*(2*50))][last()][1]/div/h2/allText()", "\u957f\u5c3e\u7406\u8bba" }, { "//ul[@class=\'subject-list\']/li[self::li/div/div/span[@class=\'pl\']/num()>10000][-1]/div/h2/allText()", "\u957f\u5c3e\u7406\u8bba\u957f\u5c3e\u7406\u8bba" }, { "//ul[@class=\'subject-list\']/li[contains(self::li/div/div/span[@class=\'pl\']//text(),\'14582\')]/div/h2//text()", "\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6T2-\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6" }, { "//ul[@class=\'subject-list\']/li[contains(./div/div/span[@class=\'pl\']//text(),\'14582\')]/div/h2//text()", "\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6T2-\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6" }, { "//*[@id=\"subject_list\"]/ul/li[2]/div[2]/h2/a//text()", "\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6T2-\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6" }, { "//ul[@class]", 3L }, { "//a[@id]/@href", "https://www.douban.com/doumail/" }, { "//*[@id=\'subject_list\']/ul[1]/li[8]/div[2]/div[2]/span[3]/num()", "3734" }, { "//a[@id]/@href | //*[@id=\'subject_list\']/ul[1]/li[8]/div[2]/div[2]/span[3]/num()", "https://www.douban.com/doumail/3734" } };
  }

  @UseDataProvider(value = "dataOfXpathAndexpect") @Test public void testXpathAndAssert(String xpath, Object expect) throws XpathSyntaxErrorException {
    logger.info("current xpath: {}", xpath);
    List<JXNode> rs = doubanTest.selN(xpath);
    if (expect instanceof String) {
      String res = StringUtils.join(rs, "");
      logger.info(res);
      Assert.assertEquals(expect, res);
    } else {
      if (expect instanceof Number) {
        long size = (long) expect;
        Assert.assertEquals(size, rs.size());
      }
    }
  }

  @Test @DataProvider(value = { "//ul[@class=\'subject-list\']/li[position()<3]" }) public void testJXNode(String xpath) throws XpathSyntaxErrorException {
    logger.info("current xpath: {}", xpath);
    List<JXNode> jxNodeList = doubanTest.selN(xpath);
    Set<String> expect = new HashSet<>();
    expect.add("\u5931\u63a7: \u5168\u4eba\u7c7b\u7684\u6700\u7ec8\u547d\u8fd0\u548c\u7ed3\u5c40");
    expect.add("\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6");
    expect.add("T2-\u5931\u63a7: \u5168\u4eba\u7c7b\u7684\u6700\u7ec8\u547d\u8fd0\u548c\u7ed3\u5c40");
    expect.add("T2-\u9ed1\u5ba2\u4e0e\u753b\u5bb6: \u7845\u8c37\u521b\u4e1a\u4e4b\u7236Paul Graham\u6587\u96c6");
    Set<String> res = new HashSet<>();
    for (JXNode node : jxNodeList) {
      if (!node.isString()) {
        String currentRes = StringUtils.join(node.sel("/div/h2/a//text()"), "");
        logger.info(currentRes);
        res.add(currentRes);
      }
    }
    Assert.assertEquals(expect, res);
  }

  @Test @DataProvider(value = { "//ul[@class=\'subject-list\']" }) public void testRecursionNode(String xpath) throws XpathSyntaxErrorException {
    logger.info("current xpath: {}", xpath);
    List<JXNode> jxNodeList = doubanTest.selN(xpath);
    logger.info("size = {}", jxNodeList.size());
    Assert.assertEquals(2, jxNodeList.size());
  }

  @Test @DataProvider(value = { "//body/div/div/h1/text()", "/body/div/div/h1/text()" }) public void absolutePathTest(String xpath) throws XpathSyntaxErrorException {
    logger.info("current xpath: {}", xpath);
    List<JXNode> jxNodeList = doubanTest.selN(xpath);
    logger.info("size = {}\uff0cres ={}", jxNodeList.size(), jxNodeList);
  }

  @Test public void testAs() throws XpathSyntaxErrorException {
    List<JXNode> jxNodeList = custom.selN("//b[contains(text(),\'\u6027\u522b\')]/parent::*/text()");
    Assert.assertEquals("\u7537", StringUtils.join(jxNodeList, ""));
    for (JXNode jxNode : jxNodeList) {
      logger.info(jxNode.toString());
    }
  }

  /**
     * fix https://github.com/zhegexiaohuozi/JsoupXpath/issues/33
     */
  public void testNotObj() {
    JXDocument doc = JXDocument.createByUrl("https://www.gxwztv.com/61/61514/");
    List<JXNode> nodes = doc.selN("//*[@id=\"chapters-list\"]/li[not(@style)]");
    for (JXNode node : nodes) {
      logger.info("r = {}", node);
    }
  }

  /**
     * fix https://github.com/zhegexiaohuozi/JsoupXpath/issues/34
     */
  @Test public void testAttrAtRoot() {
    String content = "<html>\n" + " <head></head>\n" + " <body>\n" + "  <a href=\"/124/124818/162585930.html\">\u7b2c2\u7ae0 \u795e\u5947\u4ea4\u6d41\u7fa4</a>\n" + " </body>\n" + "</html>";
    JXDocument doc = JXDocument.create(content);
    List<JXNode> nodes = doc.selN("//@href");
    for (JXNode node : nodes) {
      logger.info("r = {}", node);
    }
  }

  @Test public void testA() {
    String content = "<span style=\"color: #5191ce;\" >\u7f51\u9875\u8bbe\u8ba1\u5e08</span>";
    JXDocument doc = JXDocument.create(content);
    List<JXNode> nodes = doc.selN("//*[text()=\'\u7f51\u9875\u8bbe\u8ba1\u5e08\']");
    for (JXNode node : nodes) {
      logger.info("r = {}", node);
    }
  }

  /**
     * fix https://github.com/zhegexiaohuozi/JsoupXpath/issues/52
     */
  @Test public void fixTextBehaviorTest() {
    String html = "<p><span class=\"text-muted\">\u5206\u7c7b\uff1a</span>\u52a8\u6f2b<span class=\"split-line\"></span><span class=\"text-muted hidden-xs\">\u5730\u533a\uff1a</span>\u65e5\u672c<span class=\"split-line\"></span><span class=\"text-muted hidden-xs\">\u5e74\u4efd\uff1a</span>2010</p>";
    JXDocument jxDocument = JXDocument.create(html);
    List<JXNode> jxNodes = jxDocument.selN("//text()[3]");
    String actual = StringUtils.join(jxNodes, "");
    logger.info("actual = {}", actual);
    Assert.assertEquals("2010", actual);
    List<JXNode> nodes = jxDocument.selN("//text()");
    String allText = StringUtils.join(nodes, "");
    Assert.assertEquals("\u5206\u7c7b\uff1a\u52a8\u6f2b\u5730\u533a\uff1a\u65e5\u672c\u5e74\u4efd\uff1a2010", allText);
    logger.info("all = {}", allText);
  }

  /**
     * fix https://github.com/zhegexiaohuozi/JsoupXpath/issues/44
     */
  @Test public void fixTextElNoParentTest() {
    String test = "<div class=\'a\'> a <div>need</div> <div class=\'e\'> not need</div> c </div>";
    JXDocument j = JXDocument.create(test);
    List<JXNode> l = j.selN("//div[@class=\'a\']//text()[not(ancestor::div[@class=\'e\'])]");
    Set<String> finalRes = new HashSet<>();
    for (JXNode i : l) {
      logger.info("{}", i.toString());
      finalRes.add(i.asString());
    }
    Assert.assertFalse(finalRes.contains("not need"));
    Assert.assertTrue(finalRes.contains("need"));
    Assert.assertEquals(4, finalRes.size());
  }

  /**
     * fix https://github.com/zhegexiaohuozi/JsoupXpath/issues/53
     */
  @Test public void fixIssue53() {
    String content = "<li class=\"res-book-item\" data-bid=\"1018351389\" data-rid=\"1\"> \n" + " <div class=\"book-img-box\"> <a href=\"//book.qidian.com/info/1018351389\" target=\"_blank\" data-eid=\"qd_S04\" data-algrid=\"0.0.0\" data-bid=\"1018351389\"><img src=\"//bookcover.yuewen.com/qdbimg/349573/1018351389/150\"></a> \n" + " </div> \n" + " <div class=\"book-mid-info\"> \n" + "  <h4><a href=\"//book.qidian.com/info/1018351389\" target=\"_blank\" data-eid=\"qd_S05\" data-bid=\"1018351389\" data-algrid=\"0.0.0\"><cite class=\"red-kw\">\u6211\u4eec</cite>\u5e73\u51e1<cite class=\"red-kw\">\u6211\u4eec</cite>\u5fe0\u8bda</a></h4> \n" + "  <p class=\"author\"> <img src=\"//qidian.gtimg.com/qd/images/ico/user.f22d3.png\"><a class=\"name\" data-eid=\"qd_S06\" href=\"//my.qidian.com/author/403791004\" target=\"_blank\">\u5de1\u7483</a> <em>|</em><a href=\"//www.qidian.com/duanpian\" data-eid=\"qd_S07\" target=\"_blank\">\u77ed\u7bc7</a><em>|</em><span>\u8fde\u8f7d</span> </p> \n" + "  <p class=\"intro\"> \u8fd9\u662f\u4e00\u4f4d\u666e\u901a\u8001\u5175\u7684\u6545\u4e8b\uff0c\u8fd9\u4f4d\u8001\u5175\u6ca1\u6709\u8d70\u4e0a\u6218\u573a\uff0c\u4e5f\u6ca1\u6709\u4eba\u6b4c\u9882\u4ed6\uff0c\u4f46\u4ed6\u7684\u5de5\u4f5c\u5374\u662f\u9762\u5bf9\u751f\u4e0e\u6b7b\uff0c\u4ed6\u662f\u4e00\u540d\u666e\u901a\u7684\u519b\u8f6c\u5e72\u90e8\uff0c\u6ca1\u6709\u5f97\u5230\u4efb\u4f55\u8363\u8a89\uff0c\u5374\u4ecd\u65e7\u575a\u5b88\u7740\u4fe1\u4ef0\uff0c\u6c38\u8fdc\u5fe0\u8bda\u3002\u9664\u4e86\u4ed6\u7684\u5bb6\u4eba\uff0c\u4ed6\u7684\u6218\u53cb\uff0c\u4ed6\u7684\u6545\u4e8b\u4e0d\u88ab\u4efb\u4f55\u4eba\u6240\u77e5\uff0c\u4f46\u4ed6\u7684\u6545\u4e8b\u6b63\u662f\u4e00\u4ee3\u519b\u4eba\u3001\u4e00\u4ee3\u519b\u8f6c\u5e72\u90e8\u7684\u5199\u7167\u3002\u6240\u4ee5\uff0c\u6211\u6765\u6b4c\u9882\u4ed6\uff0c\u6b4c\u9882\u90a3\u4e00\u4ee3\u4eba\u3002 </p> \n" + "  <p class=\"update\"><a href=\"//read.qidian.com/chapter/YiObT_DmJpXu4xLcYRGW6w2/Ulsr6ThvJS5p4rPq4Fd4KQ2\" target=\"_blank\" data-eid=\"qd_S08\" data-bid=\"1018351389\" data-cid=\"//read.qidian.com/chapter/YiObT_DmJpXu4xLcYRGW6w2/Ulsr6ThvJS5p4rPq4Fd4KQ2\">\u6700\u65b0\u66f4\u65b0 \u7b2c\u4e00\u6b21\u89c1\u8bc6\u5230\u751f\u6b7b</a><em>\u00b7</em><span>2020-02-19</span> </p> \n" + " </div> \n" + " <div class=\"book-right-info\"> \n" + "  <div class=\"total\"> \n" + "   <p><span> 4497</span>\u603b\u5b57\u6570</p> \n" + "   <p><span> 0</span>\u603b\u63a8\u8350</p> \n" + "  </div> \n" + "  <p class=\"btn\"> <a class=\"red-btn\" href=\"//book.qidian.com/info/1018351389\" data-eid=\"qd_S02\" target=\"_blank\">\u4e66\u7c4d\u8be6\u60c5</a> <a class=\"blue-btn add-book\" href=\"javascript:\" data-eid=\"qd_S03\" data-bookid=\"1018351389\" data-bid=\"1018351389\">\u52a0\u5165\u4e66\u67b6</a> </p> \n" + " </div> </li>";
    JXDocument j = JXDocument.create(content);
    List<JXNode> l = j.selN("//*[text()=\'\u603b\u5b57\u6570\']//text()");
    Assert.assertEquals(2, l.size());
    Assert.assertEquals("4497", l.get(0).asString());
    Assert.assertEquals("\u603b\u5b57\u6570", l.get(1).asString());
  }

  @Test public void fixIssue53B() {
    String content = "<li class=\"res-book-item\" data-bid=\"1018351389\" data-rid=\"1\"> \n" + " <div class=\"book-img-box\"> <a href=\"//book.qidian.com/info/1018351389\" target=\"_blank\" data-eid=\"qd_S04\" data-algrid=\"0.0.0\" data-bid=\"1018351389\"><img src=\"//bookcover.yuewen.com/qdbimg/349573/1018351389/150\"></a> \n" + " </div> \n" + " <div class=\"book-mid-info\"> \n" + "  <h4><a href=\"//book.qidian.com/info/1018351389\" target=\"_blank\" data-eid=\"qd_S05\" data-bid=\"1018351389\" data-algrid=\"0.0.0\"><cite class=\"red-kw\">\u6211\u4eec</cite>\u5e73\u51e1<cite class=\"red-kw\">\u6211\u4eec</cite>\u5fe0\u8bda</a></h4> \n" + "  <p class=\"author\"> <img src=\"//qidian.gtimg.com/qd/images/ico/user.f22d3.png\"><a class=\"name\" data-eid=\"qd_S06\" href=\"//my.qidian.com/author/403791004\" target=\"_blank\">\u5de1\u7483</a> <em>|</em><a href=\"//www.qidian.com/duanpian\" data-eid=\"qd_S07\" target=\"_blank\">\u77ed\u7bc7</a><em>|</em><span>\u8fde\u8f7d</span> </p> \n" + "  <p class=\"intro\"> \u8fd9\u662f\u4e00\u4f4d\u666e\u901a\u8001\u5175\u7684\u6545\u4e8b\uff0c\u8fd9\u4f4d\u8001\u5175\u6ca1\u6709\u8d70\u4e0a\u6218\u573a\uff0c\u4e5f\u6ca1\u6709\u4eba\u6b4c\u9882\u4ed6\uff0c\u4f46\u4ed6\u7684\u5de5\u4f5c\u5374\u662f\u9762\u5bf9\u751f\u4e0e\u6b7b\uff0c\u4ed6\u662f\u4e00\u540d\u666e\u901a\u7684\u519b\u8f6c\u5e72\u90e8\uff0c\u6ca1\u6709\u5f97\u5230\u4efb\u4f55\u8363\u8a89\uff0c\u5374\u4ecd\u65e7\u575a\u5b88\u7740\u4fe1\u4ef0\uff0c\u6c38\u8fdc\u5fe0\u8bda\u3002\u9664\u4e86\u4ed6\u7684\u5bb6\u4eba\uff0c\u4ed6\u7684\u6218\u53cb\uff0c\u4ed6\u7684\u6545\u4e8b\u4e0d\u88ab\u4efb\u4f55\u4eba\u6240\u77e5\uff0c\u4f46\u4ed6\u7684\u6545\u4e8b\u6b63\u662f\u4e00\u4ee3\u519b\u4eba\u3001\u4e00\u4ee3\u519b\u8f6c\u5e72\u90e8\u7684\u5199\u7167\u3002\u6240\u4ee5\uff0c\u6211\u6765\u6b4c\u9882\u4ed6\uff0c\u6b4c\u9882\u90a3\u4e00\u4ee3\u4eba\u3002 </p> \n" + "  <p class=\"update\"><a href=\"//read.qidian.com/chapter/YiObT_DmJpXu4xLcYRGW6w2/Ulsr6ThvJS5p4rPq4Fd4KQ2\" target=\"_blank\" data-eid=\"qd_S08\" data-bid=\"1018351389\" data-cid=\"//read.qidian.com/chapter/YiObT_DmJpXu4xLcYRGW6w2/Ulsr6ThvJS5p4rPq4Fd4KQ2\">\u6700\u65b0\u66f4\u65b0 \u7b2c\u4e00\u6b21\u89c1\u8bc6\u5230\u751f\u6b7b</a><em>\u00b7</em><span>2020-02-19</span> </p> \n" + " </div> \n" + " <div class=\"book-right-info\"> \n" + "  <div class=\"total\"> \n" + "   <p><span> 4497</span>\u603b\u5b57\u6570</p> \n" + "   <p><span> 0</span>\u603b\u63a8\u8350</p> \n" + "  </div> \n" + "  <p class=\"btn\"> <a class=\"red-btn\" href=\"//book.qidian.com/info/1018351389\" data-eid=\"qd_S02\" target=\"_blank\">\u4e66\u7c4d\u8be6\u60c5</a> <a class=\"blue-btn add-book\" href=\"javascript:\" data-eid=\"qd_S03\" data-bookid=\"1018351389\" data-bid=\"1018351389\">\u52a0\u5165\u4e66\u67b6</a> </p> \n" + " </div> </li>";
    JXDocument j = JXDocument.create(content);
    List<JXNode> l = j.selN("//*[text()=\'\u603b\u5b57\u6570\']//text()[1]");
    logger.info("{}", l);
    Assert.assertEquals(2, l.size());
    List<JXNode> l2 = j.selN("//*[text()=\'\u603b\u5b57\u6570\']//text()[0]");
    Assert.assertEquals(0, l2.size());
    List<JXNode> l3 = j.selN("//*[text()=\'\u603b\u5b57\u6570\']//text()[2]");
    Assert.assertEquals(0, l3.size());
  }

  @Test public void textChildOrderTest() {
    String content = "<p> one <span> two</span> three </p>";
    JXDocument j = JXDocument.create(content);
    Assert.assertEquals(StringUtils.join(j.selN("//text()[2]"), ""), "three");
  }

  @Test public void issue64And65() {
    String content = "<div class=\'a\'>1</div>\n" + "<div>2</div>\n" + "<div class=\'a\'>3</div>\n" + "<div>4</div>\n" + "<div>5</div>";
    JXDocument j = JXDocument.create(content);
    Assert.assertEquals("2", j.selNOne("//div[text()=\'3\']/preceding-sibling-one::div/text()").asString());
    Assert.assertEquals("4", j.selNOne("//div[text()=\'3\']/following-sibling-one::div/text()").asString());
  }

  @Test public void issue66() throws Exception {
    JXDocument j = JXDocument.create(FileUtils.readFileToString(new File(loader.getResource("issue66.html").toURI()), Charset.forName("utf8")));
    logger.info("{}", j.selN("count(//bookstore/book)"));
    logger.info("{}", j.selN("//bookstore/book[position()<count(//bookstore/book)]/price"));
    logger.info("{}", j.selN("//bookstore/book[position()<count(//bookstore/book)-1]/price"));
    logger.info("{}", j.selN("sum(//bookstore/book/year[num()<2005])"));
    logger.info("{}", j.selN("sum(//bookstore/book/price)"));
    logger.info("{}", j.selN("sum(//bookstore/book/title)"));
    Assert.assertEquals(4, j.selNOne("count(//bookstore/book)").asLong().longValue());
    Assert.assertEquals(3, j.selN("//bookstore/book[position()<count(//bookstore/book)]/price").size());
    Assert.assertEquals(2, j.selN("//bookstore/book[position()<count(//bookstore/book)-1]/price").size());
    Assert.assertEquals(4006, j.selNOne("sum(//bookstore/book/year[num()<2005])").asLong().longValue());
    Assert.assertEquals("", j.selNOne("sum(//bookstore/book/title)").asString());
  }

  @Test public void i42() throws Exception {
    JXDocument j = JXDocument.create(FileUtils.readFileToString(new File(loader.getResource("issue66.html").toURI()), Charset.forName("utf8")));
    logger.info("{}", j.selNOne("//bookstore/book[last()]/price"));
    Assert.assertEquals(39.95, j.selNOne("//bookstore/book[last()]/price/num()").asDouble().doubleValue(), 10);
  }
}