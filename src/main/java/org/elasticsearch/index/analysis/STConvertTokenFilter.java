package org.elasticsearch.index.analysis;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.IOException;

/**
 */
public class STConvertTokenFilter extends TokenFilter {
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

  private String delimiter = ",";

  private STConvertType convertType = STConvertType.SIMPLE_2_TRADITIONAL;

  private Boolean keepBoth = false;

  @Override public final boolean incrementToken() throws IOException {
    if (!input.incrementToken()) {
      return false;
    }
    StringBuilder stringBuilder = new StringBuilder();
    String str = termAtt.toString();
    termAtt.setEmpty();
    String converted = STConverter.getInstance().convert(str, convertType);
    if (!converted.isEmpty()) {
      stringBuilder.append(converted);
      if (keepBoth) {
        stringBuilder.append(delimiter);
        stringBuilder.append(str);
      }
    } else {
      stringBuilder.append(str);
    }
    termAtt.resizeBuffer(stringBuilder.length());
    termAtt.append(stringBuilder.toString());
    termAtt.setLength(stringBuilder.length());
    return true;
  }

  public STConvertTokenFilter(TokenStream in, STConvertType convertType, String delimiter, Boolean keepBoth) {
    super(in);
    this.delimiter = delimiter;
    this.convertType = convertType;
    this.keepBoth = keepBoth;
  }

  @Override public final void end() throws IOException {
    super.end();
  }

  @Override public void reset() throws IOException {
    super.reset();
  }
}