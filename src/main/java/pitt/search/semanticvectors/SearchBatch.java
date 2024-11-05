package pitt.search.semanticvectors;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import pitt.search.semanticvectors.ElementalVectorStore.ElementalGenerationMethod;
import pitt.search.semanticvectors.utils.VerbatimLogger;
import pitt.search.semanticvectors.vectors.Vector;
import pitt.search.semanticvectors.vectors.ZeroVectorException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

public class SearchBatch {
  private static final Logger logger = Logger.getLogger(SearchBatch.class.getCanonicalName());

  public enum SearchType {
    SUM,
    SUBSPACE,
    MAXSIM,
    MINSIM,
    PERMUTATION,
    BALANCEDPERMUTATION,
    BOUNDPRODUCT,
    LUCENE,
    BOUNDMINIMUM,
    BOUNDPRODUCTSUBSPACE,
    ANALOGY,
    PRINTQUERY
  }

  private static LuceneUtils luceneUtils;

  public static String usageMessage = "\nSearch class in package pitt.search.semanticvectors" + "\nUsage: java pitt.search.semanticvectors.Search [-queryvectorfile query_vector_file]" + "\n                                               [-searchvectorfile search_vector_file]" + "\n                                               [-luceneindexpath path_to_lucene_index]" + "\n                                               [-searchtype TYPE]" + "\n                                               <QUERYTERMS>" + "\nIf no query or search file is given, default will be" + "\n    termvectors.bin in local directory." + "\n-luceneindexpath argument is needed if to get term weights from" + "\n    term frequency, doc frequency, etc. in lucene index." + "\n-searchtype can be one of SUM, SUBSPACE, MAXSIM, MINSIM" + "\n    BALANCEDPERMUTATION, PERMUTATION, PRINTQUERY" + "\n<QUERYTERMS> should be a list of words, separated by spaces." + "\n    If the term NOT is used, terms after that will be negated.";

  public static void runSearch(FlagConfig flagConfig) throws IllegalArgumentException {
    if (flagConfig == null) {
      throw new NullPointerException("flagConfig cannot be null");
    }
    if (flagConfig.remainingArgs == null) {
      throw new IllegalArgumentException("No query terms left after flag parsing!");
    }
    String[] queryArgs = flagConfig.remainingArgs;
    VectorStore queryVecReader = null;
    VectorStore boundVecReader = null;
    VectorStore elementalVecReader = null, semanticVecReader = null, predicateVecReader = null;
    VectorStore searchVecReader = null;
    try {
      if (!flagConfig.elementalvectorfile().equals("elementalvectors") && !flagConfig.semanticvectorfile().equals("semanticvectors") && !flagConfig.elementalpredicatevectorfile().equals("predicatevectors")) {
        VerbatimLogger.info("Opening elemental query vector store from file: " + flagConfig.elementalvectorfile() + "\n");
        VerbatimLogger.info("Opening semantic query vector store from file: " + flagConfig.semanticvectorfile() + "\n");
        VerbatimLogger.info("Opening predicate query vector store from file: " + flagConfig.elementalpredicatevectorfile() + "\n");
        if (flagConfig.elementalvectorfile().equals("deterministic")) {
          if (flagConfig.elementalmethod().equals(ElementalGenerationMethod.ORTHOGRAPHIC)) {
            elementalVecReader = new VectorStoreOrthographical(flagConfig);
          } else {
            if (flagConfig.elementalmethod().equals(ElementalGenerationMethod.CONTENTHASH)) {
              elementalVecReader = new VectorStoreDeterministic(flagConfig);
            } else {
              VerbatimLogger.info("Please select either -elementalmethod orthographic OR -elementalmethod contenthash depending upon the deterministic approach you would like used.");
            }
          }
        } else {
          elementalVecReader = new VectorStoreRAM(flagConfig);
          ((VectorStoreRAM) elementalVecReader).initFromFile(flagConfig.elementalvectorfile());
        }
        semanticVecReader = new VectorStoreRAM(flagConfig);
        ((VectorStoreRAM) semanticVecReader).initFromFile(flagConfig.semanticvectorfile());
        predicateVecReader = new VectorStoreRAM(flagConfig);
        ((VectorStoreRAM) predicateVecReader).initFromFile(flagConfig.elementalpredicatevectorfile());
      } else {
        VerbatimLogger.info("Opening query vector store from file: " + flagConfig.queryvectorfile() + "\n");
        if (flagConfig.queryvectorfile().equals("deterministic")) {
          if (flagConfig.elementalmethod().equals(ElementalGenerationMethod.ORTHOGRAPHIC)) {
            queryVecReader = new VectorStoreOrthographical(flagConfig);
          } else {
            if (flagConfig.elementalmethod().equals(ElementalGenerationMethod.CONTENTHASH)) {
              queryVecReader = new VectorStoreDeterministic(flagConfig);
            } else {
              VerbatimLogger.info("Please select either -elementalmethod orthographic OR -elementalmethod contenthash depending upon the deterministic approach you would like used.");
            }
          }
        } else {
          queryVecReader = new VectorStoreRAM(flagConfig);
          ((VectorStoreRAM) queryVecReader).initFromFile(flagConfig.queryvectorfile());
        }
      }
      if (flagConfig.boundvectorfile().length() > 0) {
        VerbatimLogger.info("Opening second query vector store from file: " + flagConfig.boundvectorfile() + "\n");
        boundVecReader = new VectorStoreRAM(flagConfig);
        ((VectorStoreRAM) boundVecReader).initFromFile(flagConfig.boundvectorfile());
      }
      if (flagConfig.queryvectorfile().equals(flagConfig.searchvectorfile()) || flagConfig.searchvectorfile().isEmpty()) {
        searchVecReader = queryVecReader;
      } else {
        VerbatimLogger.info("Opening search vector store from file: " + flagConfig.searchvectorfile() + "\n");
        searchVecReader = new VectorStoreRAM(flagConfig);
        ((VectorStoreRAM) searchVecReader).initFromFile(flagConfig.searchvectorfile());
      }
      if (!flagConfig.luceneindexpath().isEmpty()) {
        try {
          luceneUtils = new LuceneUtils(flagConfig);
        } catch (IOException e) {
          logger.warning("Couldn\'t open Lucene index at " + flagConfig.luceneindexpath() + ". Will continue without term weighting.");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      BufferedReader queryReader = new BufferedReader(new FileReader(new File(queryArgs[0])));
      String queryString = queryReader.readLine();
      int qcnt = 0;
      while (queryString != null) {
        ArrayList<String> queryTerms = new ArrayList<String>();
        qcnt++;
        Analyzer analyzer = null;
        if (!flagConfig.matchcase()) {
          analyzer = new StandardAnalyzer(new CharArraySet(new ArrayList<String>(), true));
        } else {
          analyzer = new WhitespaceAnalyzer();
        }
        TokenStream stream = analyzer.tokenStream(null, new StringReader(queryString));
        CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
          String term = cattr.toString();
          if (luceneUtils == null || !luceneUtils.stoplistContains(term)) {
            if (!flagConfig.matchcase()) {
              term = term.toLowerCase();
            }
            queryTerms.add(term);
          }
        }
        stream.end();
        stream.close();
        analyzer.close();
        queryArgs = queryTerms.toArray(new String[0]);
        VectorSearcher vecSearcher = null;
        LinkedList<SearchResult> results;
        VerbatimLogger.info("Searching term vectors, searchtype " + flagConfig.searchtype() + "\n");
        try {
          switch (flagConfig.searchtype()) {
            case SUM:
            vecSearcher = new VectorSearcher.VectorSearcherCosine(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs);
            break;
            case SUBSPACE:
            vecSearcher = new VectorSearcher.VectorSearcherSubspaceSim(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs);
            break;
            case MAXSIM:
            vecSearcher = new VectorSearcher.VectorSearcherMaxSim(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs);
            break;
            case MINSIM:
            vecSearcher = new VectorSearcher.VectorSearcherMinSim(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs);
            break;
            case BOUNDPRODUCT:
            if (queryArgs.length == 2) {
              vecSearcher = new VectorSearcher.VectorSearcherBoundProduct(queryVecReader, boundVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs[0], queryArgs[1]);
            } else {
              vecSearcher = new VectorSearcher.VectorSearcherBoundProduct(elementalVecReader, semanticVecReader, predicateVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs[0]);
            }
            break;
            case BOUNDPRODUCTSUBSPACE:
            if (queryArgs.length == 2) {
              vecSearcher = new VectorSearcher.VectorSearcherBoundProductSubSpace(queryVecReader, boundVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs[0], queryArgs[1]);
            } else {
              vecSearcher = new VectorSearcher.VectorSearcherBoundProductSubSpace(elementalVecReader, semanticVecReader, predicateVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs[0]);
            }
            break;
            case BOUNDMINIMUM:
            if (queryArgs.length == 2) {
              vecSearcher = new VectorSearcher.VectorSearcherBoundMinimum(queryVecReader, boundVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs[0], queryArgs[1]);
            } else {
              vecSearcher = new VectorSearcher.VectorSearcherBoundMinimum(elementalVecReader, semanticVecReader, predicateVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs[0]);
            }
            break;
            case PERMUTATION:
            vecSearcher = new VectorSearcher.VectorSearcherPerm(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs);
            break;
            case BALANCEDPERMUTATION:
            vecSearcher = new VectorSearcher.BalancedVectorSearcherPerm(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs);
            break;
            case ANALOGY:
            vecSearcher = new VectorSearcher.AnalogySearcher(queryVecReader, searchVecReader, luceneUtils, flagConfig, queryArgs);
            break;
            case LUCENE:
            vecSearcher = new VectorSearcher.VectorSearcherLucene(luceneUtils, flagConfig, queryArgs);
            break;
            case PRINTQUERY:
            Vector queryVector = CompoundVectorBuilder.getQueryVector(queryVecReader, luceneUtils, flagConfig, queryArgs);
            System.out.println(queryVector.toString());
            default:
            throw new IllegalArgumentException("Unknown search type: " + flagConfig.searchtype());
          }
        } catch (ZeroVectorException zve) {
          logger.info(zve.getMessage());
        }
        results = new LinkedList<SearchResult>();
        try {
          results = vecSearcher.getNearestNeighbors(flagConfig.numsearchresults());
        } catch (Exception e) {
        }
        int cnt = 0;
        if (results.size() > 0) {
          VerbatimLogger.info("Search output follows ...\n");
          for (SearchResult result : results) {
            if (flagConfig.treceval() != -1) {
              System.out.println(String.format("%s\t%s\t%s\t%s\t%f\t%s", qcnt, "Q0", result.getObjectVector().getObject().toString(), ++cnt, result.getScore(), "DEFAULT"));
            } else {
              System.out.println(String.format("%f:%s", result.getScore(), result.getObjectVector().getObject().toString()));
            }
          }
        }
        queryString = queryReader.readLine();
      }
      queryReader.close();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IllegalArgumentException, IOException {
    FlagConfig flagConfig;
    try {
      flagConfig = FlagConfig.getFlagConfig(args);
      runSearch(flagConfig);
    } catch (IllegalArgumentException e) {
      System.err.println(usageMessage);
      throw e;
    }
  }
}