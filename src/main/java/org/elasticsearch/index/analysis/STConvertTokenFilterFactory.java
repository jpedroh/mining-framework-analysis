package org.elasticsearch.index.analysis;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;

@Deprecated public class STConvertTokenFilterFactory extends AbstractTokenFilterFactory {
  private String delimiter = ",";

  private String type = "t2s";

  private Boolean keepBoth = false;

  @Inject public STConvertTokenFilterFactory(Index index, Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
    super(index, indexSettings, name, settings);
    type = settings.get("convert_type", "t2s");
    delimiter = settings.get("delimiter", ",");
    String keepBothStr = settings.get("keep_both", "false");
    if (keepBothStr.equals("true")) {
      keepBoth = true;
    }
  }

  @Override public TokenStream create(TokenStream tokenStream) {
    STConvertType convertType = STConvertType.TRADITIONAL_2_SIMPLE;
    if (type.equals("s2t")) {
      convertType = STConvertType.SIMPLE_2_TRADITIONAL;
    }
    return new STConvertTokenFilter(tokenStream, convertType, delimiter, keepBoth);
  }
}