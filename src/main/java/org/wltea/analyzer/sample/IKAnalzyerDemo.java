package org.wltea.analyzer.sample;
import org.apache.lucene.analysis.Analyzer;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import java.io.StringReader;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 使用IKAnalyzer进行分词的演示
 * 2012-10-22
 *
 */
public class IKAnalzyerDemo {
  public static ESLogger logger = Loggers.getLogger("ik-analyzer");

  public static void main(String[] args) {
    Analyzer analyzer = new IKAnalyzer(true, true);
    TokenStream ts = null;
    try {
      ts = analyzer.tokenStream("myfield", new StringReader("\u5218\u4e00\u6ce2WORLD ,.. html DATA</html>HELLO"));
      OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
      CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
      TypeAttribute type = ts.addAttribute(TypeAttribute.class);
      ts.reset();
      while (ts.incrementToken()) {
        logger.info(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | " + type.type());
      }
      ts.end();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (ts != null) {
        try {
          ts.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}