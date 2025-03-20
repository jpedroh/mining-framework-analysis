package org.elasticsearch.index.analysis;

/**
 */
@Deprecated public class STConvertAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
  private static final String STCONVERT = "stconvert";

  @Override public void processAnalyzers(AnalyzersBindings analyzersBindings) {
    analyzersBindings.processAnalyzer(STCONVERT, STConvertAnalyzerProvider.class);
  }

  @Override public void processTokenizers(TokenizersBindings tokenizersBindings) {
    tokenizersBindings.processTokenizer(STCONVERT, STConvertTokenizerFactory.class);
  }

  @Override public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
    tokenFiltersBindings.processTokenFilter(STCONVERT, STConvertTokenFilterFactory.class);
  }
}