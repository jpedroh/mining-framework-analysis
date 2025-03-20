package org.elasticsearch.indices.analysis.pl;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.Lucene;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalyzerScope;
import org.elasticsearch.index.analysis.PreBuiltAnalyzerProviderFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenFilterFactoryFactory;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import morfologik.stemming.*;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.morfologik.*;
import java.io.IOException;

/**
 * Registers indices level analysis components so, if not explicitly configured, will be shared
 * among all indices.
 */
public class MorfologikIndicesAnalysis extends AbstractComponent {
  @Inject public MorfologikIndicesAnalysis(Settings settings, IndicesAnalysisService indicesAnalysisService) {
    super(settings);
    indicesAnalysisService.analyzerProviderFactories().put("morfologik", new PreBuiltAnalyzerProviderFactory("morfologik", AnalyzerScope.INDICES, new MorfologikAnalyzer(Lucene.ANALYZER_VERSION)));
    indicesAnalysisService.tokenFilterFactories().put("morfologik_stem", new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
      @Override public String name() {
        return "morfologik_stem";
      }

      @Override public TokenStream create(TokenStream tokenStream) {
        return new MorfologikFilter(tokenStream, Lucene.ANALYZER_VERSION);
      }
    }));
  }
}