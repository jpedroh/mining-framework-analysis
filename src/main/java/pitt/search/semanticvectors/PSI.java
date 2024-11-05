package pitt.search.semanticvectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import pitt.search.semanticvectors.utils.VerbatimLogger;
import pitt.search.semanticvectors.vectors.Vector;
import pitt.search.semanticvectors.vectors.VectorFactory;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Logger;
import pitt.search.semanticvectors.LuceneUtils.TermWeight;

public class PSI {
  private static final Logger logger = Logger.getLogger(PSI.class.getCanonicalName());

  private FlagConfig flagConfig;

  private VectorStore elementalItemVectors, elementalPredicateVectors;

  private VectorStoreRAM semanticItemVectors, semanticPredicateVectors;

  private static final String SUBJECT_FIELD = "subject";

  private static final String PREDICATE_FIELD = "predicate";

  private static final String OBJECT_FIELD = "object";

  private static final String PREDICATION_FIELD = "predication";

  private String[] itemFields = { SUBJECT_FIELD, OBJECT_FIELD };

  private LuceneUtils luceneUtils;

  private PSI() {
  }



  public static void createIncrementalPSIVectors(FlagConfig flagConfig) throws IOException {
    PSI incrementalPSIVectors = new PSI();
    incrementalPSIVectors.flagConfig = flagConfig;
    incrementalPSIVectors.initialize();
    VectorStoreWriter.writeVectors(flagConfig.elementalvectorfile(), flagConfig, incrementalPSIVectors.elementalItemVectors);
    VectorStoreWriter.writeVectors(flagConfig.elementalpredicatevectorfile(), flagConfig, incrementalPSIVectors.elementalPredicateVectors);
    VerbatimLogger.info("Performing first round of PSI training ...");
    incrementalPSIVectors.trainIncrementalPSIVectors("");
    VerbatimLogger.info("Performing next round of PSI training ...");
    incrementalPSIVectors.elementalItemVectors = incrementalPSIVectors.semanticItemVectors;
    incrementalPSIVectors.elementalPredicateVectors = incrementalPSIVectors.semanticPredicateVectors;
    incrementalPSIVectors.trainIncrementalPSIVectors("1");
    VerbatimLogger.info("Done with createIncrementalPSIVectors.");
  }

  private void initialize() throws IOException {
    if (this.luceneUtils == null) {
      this.luceneUtils = new LuceneUtils(flagConfig);
    }
    elementalItemVectors = new ElementalVectorStore(flagConfig);
    semanticItemVectors = new VectorStoreRAM(flagConfig);
    elementalPredicateVectors = new ElementalVectorStore(flagConfig);
    semanticPredicateVectors = new VectorStoreRAM(flagConfig);
    flagConfig.setContentsfields(itemFields);
    HashSet<String> addedConcepts = new HashSet<String>();
    for (String fieldName : itemFields) {
      Terms terms = luceneUtils.getTermsForField(fieldName);
      if (terms == null) {
        throw new NullPointerException(String.format("No terms for field \'%s\'. Please check that index at \'%s\' was built correctly for use with PSI.", fieldName, flagConfig.luceneindexpath()));
      }
      TermsEnum termsEnum = terms.iterator(null);
      BytesRef bytes;
      while ((bytes = termsEnum.next()) != null) {
        Term term = new Term(fieldName, bytes);
        if (!luceneUtils.termFilter(term)) {
          VerbatimLogger.fine("Filtering out term: " + term + "\n");
          continue;
        }
        if (!addedConcepts.contains(term.text())) {
          addedConcepts.add(term.text());
          elementalItemVectors.getVector(term.text());
          semanticItemVectors.putVector(term.text(), VectorFactory.createZeroVector(flagConfig.vectortype(), flagConfig.dimension()));
        }
      }
    }
    Terms predicateTerms = luceneUtils.getTermsForField(PREDICATE_FIELD);
    String[] dummyArray = new String[] { PREDICATE_FIELD };
    TermsEnum termsEnum = predicateTerms.iterator(null);
    BytesRef bytes;
    while ((bytes = termsEnum.next()) != null) {
      Term term = new Term(PREDICATE_FIELD, bytes);
      if (!luceneUtils.termFilter(term, dummyArray, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, 1)) {
        continue;
      }
      elementalPredicateVectors.getVector(term.text().trim());
      semanticPredicateVectors.putVector(term.text().trim(), VectorFactory.createZeroVector(flagConfig.vectortype(), flagConfig.dimension()));
      elementalPredicateVectors.getVector(term.text().trim() + "-INV");
      semanticPredicateVectors.putVector(term.text().trim() + "-INV", VectorFactory.createZeroVector(flagConfig.vectortype(), flagConfig.dimension()));
    }
  }

  private void trainIncrementalPSIVectors(String iterationTag) throws IOException {
    String fieldName = PREDICATION_FIELD;
    Terms allTerms = luceneUtils.getTermsForField(fieldName);
    TermsEnum termsEnum = allTerms.iterator(null);
    BytesRef bytes;
    while ((bytes = termsEnum.next()) != null) {
      int pc = 0;
      Term term = new Term(fieldName, bytes);
      pc++;
      if ((pc > 0) && ((pc % 10000 == 0) || (pc < 10000 && pc % 1000 == 0))) {
        VerbatimLogger.info("Processed " + pc + " unique predications ... ");
      }
      DocsEnum termDocs = luceneUtils.getDocsForTerm(term);
      termDocs.nextDoc();
      Document document = luceneUtils.getDoc(termDocs.docID());
      String subject = document.get(SUBJECT_FIELD);
      String predicate = document.get(PREDICATE_FIELD);
      String object = document.get(OBJECT_FIELD);
      if (!(elementalItemVectors.containsVector(object) && elementalItemVectors.containsVector(subject) && elementalPredicateVectors.containsVector(predicate))) {
        logger.info("skipping predication " + subject + " " + predicate + " " + object);
        continue;
      }
      float sWeight = 1;
      float oWeight = 1;
      float pWeight = 1;
      sWeight = luceneUtils.getGlobalTermWeight(new Term(SUBJECT_FIELD, subject));
      oWeight = luceneUtils.getGlobalTermWeight(new Term(OBJECT_FIELD, object));
      pWeight = luceneUtils.getLocalTermWeight(luceneUtils.getGlobalTermFreq(term));
      Vector subjectSemanticVector = semanticItemVectors.getVector(subject);
      Vector objectSemanticVector = semanticItemVectors.getVector(object);
      Vector subjectElementalVector = elementalItemVectors.getVector(subject);
      Vector objectElementalVector = elementalItemVectors.getVector(object);
      Vector predicateElementalVector = elementalPredicateVectors.getVector(predicate);
      Vector predicateElementalVectorInv = elementalPredicateVectors.getVector(predicate + "-INV");
      Vector predicateSemanticVector = semanticPredicateVectors.getVector(predicate);
      Vector predicateSemanticVectorInv = semanticPredicateVectors.getVector(predicate + "-INV");
      Vector objToAdd = objectElementalVector.copy();
      objToAdd.bind(predicateElementalVector);
      subjectSemanticVector.superpose(objToAdd, pWeight * oWeight, null);
      Vector subjToAdd = subjectElementalVector.copy();
      subjToAdd.bind(predicateElementalVectorInv);
      objectSemanticVector.superpose(subjToAdd, pWeight * sWeight, null);
      Vector predToAdd = subjectElementalVector.copy();
      predToAdd.bind(objectElementalVector);
      predicateSemanticVector.superpose(predToAdd, sWeight * oWeight, null);
      Vector predToAddInv = objectElementalVector.copy();
      predToAddInv.bind(subjectElementalVector);
      predicateSemanticVectorInv.superpose(predToAddInv, oWeight * sWeight, null);
    }
    Enumeration<ObjectVector> e = semanticItemVectors.getAllVectors();
    while (e.hasMoreElements()) {
      e.nextElement().getVector().normalize();
    }
    e = semanticPredicateVectors.getAllVectors();
    while (e.hasMoreElements()) {
      e.nextElement().getVector().normalize();
    }
    VectorStoreWriter.writeVectors(flagConfig.semanticvectorfile() + iterationTag, flagConfig, semanticItemVectors);
    VectorStoreWriter.writeVectors(flagConfig.semanticpredicatevectorfile() + iterationTag, flagConfig, semanticPredicateVectors);
    VerbatimLogger.info("Finished writing this round of semantic item and predicate vectors.\n");
  }

  public static void main(String[] args) throws IllegalArgumentException, IOException {
    FlagConfig flagConfig = FlagConfig.getFlagConfig(args);
    args = flagConfig.remainingArgs;
    if (flagConfig.luceneindexpath().isEmpty()) {
      throw (new IllegalArgumentException("-luceneindexpath argument must be provided."));
    }
    VerbatimLogger.info("Building PSI model from index in: " + flagConfig.luceneindexpath() + "\n");
    VerbatimLogger.info("Minimum frequency = " + flagConfig.minfrequency() + "\n");
    VerbatimLogger.info("Maximum frequency = " + flagConfig.maxfrequency() + "\n");
    VerbatimLogger.info("Number non-alphabet characters = " + flagConfig.maxnonalphabetchars() + "\n");
    createIncrementalPSIVectors(flagConfig);
  }
}