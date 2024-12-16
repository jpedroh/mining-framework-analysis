  package   pitt . search . semanticvectors ;   import   java . io . File ;  import   java . io . IOException ;  import    java . nio . file . FileSystems ;  import   java . util . ArrayList ;  import   java . util . Collections ;  import   java . util . Enumeration ;  import   java . util . Hashtable ;  import   java . util . Random ;  import    java . util . concurrent . ConcurrentHashMap ;  import    java . util . concurrent . ConcurrentLinkedQueue ;  import    java . util . concurrent . ConcurrentSkipListMap ;  import    java . util . concurrent . ExecutorService ;  import    java . util . concurrent . Executors ;  import     java . util . concurrent . atomic . AtomicBoolean ;  import     java . util . concurrent . atomic . AtomicInteger ;  import     org . apache . lucene . index . FieldInfos ;  import     org . apache . lucene . index . PostingsEnum ;  import     org . apache . lucene . index . Term ;  import     org . apache . lucene . index . Terms ;  import     org . apache . lucene . index . TermsEnum ;  import     org . apache . lucene . store . FSDirectory ;  import     org . apache . lucene . store . IOContext ;  import     org . apache . lucene . store . IndexOutput ;  import     org . apache . lucene . util . BytesRef ;  import    org . netlib . blas . BLAS ;  import     pitt . search . semanticvectors . DocVectors . DocIndexingStrategy ;  import     pitt . search . semanticvectors . orthography . NumberRepresentation ;  import     pitt . search . semanticvectors . utils . SigmoidTable ;  import     pitt . search . semanticvectors . utils . VerbatimLogger ;  import     pitt . search . semanticvectors . vectors . PermutationUtils ;  import     pitt . search . semanticvectors . vectors . PermutationVector ;  import     pitt . search . semanticvectors . vectors . Vector ;  import     pitt . search . semanticvectors . vectors . VectorFactory ;  import     pitt . search . semanticvectors . vectors . VectorType ;  import     pitt . search . semanticvectors . vectors . VectorUtils ;  import     pitt . search . semanticvectors . vectors . ZeroVectorException ;   public class TermTermVectorsFromLucene  {   public enum PositionalMethod  {  BASIC ,  DIRECTIONAL ,  PERMUTATION ,  PERMUTATIONPLUSBASIC ,  PROXIMITY }   public enum EncodingMethod  {  RANDOM_INDEXING ,  EMBEDDINGS }   private static final  int  MAX_EXP = 6 ;   private FlagConfig  flagConfig ;   private AtomicBoolean  exhaustedQ =  new AtomicBoolean  ( ) ;   private  int  qsize = 100000 ;   private boolean  retraining = false ;   private volatile VectorStoreRAM  semanticTermVectors ;   private volatile VectorStore  elementalTermVectors ;   private volatile VectorStoreRAM  embeddingDocVectors ;   private volatile CompressedVectorStoreRAM  subwordEmbeddingVectors ;   private LuceneUtils  luceneUtils ;   private VectorStoreRAM  positionalNumberVectors ;   private Random  random ;   private  ConcurrentSkipListMap  < Double , String >  termDic ;   private  ConcurrentHashMap  < String , Double >  subsamplingProbabilities ;   private  ConcurrentLinkedQueue  < DocIdTerms >  theQ ;   private  double  totalPool = 0 ;   private  long  totalCount = 0 ;   private  double  initial_alpha = 0.05 ;   private  double  alpha = initial_alpha ;   private  double  minimum_alpha =  0.0001 * initial_alpha ;   private AtomicInteger  totalDocCount =  new AtomicInteger  ( ) ;   private AtomicInteger  totalQueueCount =  new AtomicInteger  ( ) ;   private SigmoidTable  sigmoidTable =  new SigmoidTable  ( MAX_EXP , 1000 ) ;   private  long  tpd_average ;   private class DocIdTerms  {   int  docID ;  Terms  terms ;   public DocIdTerms  (   int docID ,  Terms terms )  {    this . docID = docID ;    this . terms = terms ; } }   private VectorStoreRAM  permutationCache ;   private  ConcurrentLinkedQueue  < Integer >  randomStartpoints ;   public VectorStore getSemanticTermVectors  ( )  {  return  this . semanticTermVectors ; }   private void initializeRandomizationStartpoints  (   int incrementSize )  {    this . randomStartpoints =  new  ConcurrentLinkedQueue  < Integer >  ( ) ;   int  increments =   luceneUtils . getNumDocs  ( ) / incrementSize ;  boolean  remainder =    luceneUtils . getNumDocs  ( ) % incrementSize > 0 ;  if  ( remainder )   increments ++ ;   ArrayList  < Integer >  toRandomize =  new  ArrayList  < Integer >  ( ) ;  for (   int  x = 0 ;  x < increments ;  x ++ )   toRandomize . add  (  x * incrementSize ) ;   Collections . shuffle  ( toRandomize ) ;   randomStartpoints . addAll  ( toRandomize ) ; }   public  ArrayList  < String > getComponentNgrams  (  String incomingString )  {   ArrayList  < String >  outgoingNgrams =  new  ArrayList  < String >  ( ) ;  String  toDecompose =   "<" + incomingString + ">" ;  for (   int  ngram_length =  flagConfig . minimum_ngram_length  ( ) ;  ngram_length <=  flagConfig . maximum_ngram_length  ( ) ;  ngram_length ++ )  for (   int  j = 0 ;  j <=  (   toDecompose . length  ( ) - ngram_length ) ;  j ++ )  {  String  toAdd =  toDecompose . substring  ( j ,  j + ngram_length ) ;  if  (  !  toAdd . equals  ( toDecompose ) )   outgoingNgrams . add  ( toAdd ) ; }  return outgoingNgrams ; }   public TermTermVectorsFromLucene  (  FlagConfig flagConfig ,  VectorStore elementalTermVectors )  throws IOException  {    this . flagConfig = flagConfig ;    this . random =  new Random  ( ) ;    this . initial_alpha =  flagConfig . initial_alpha  ( ) ;    this . alpha = initial_alpha ;  if  (  flagConfig . subword_embeddings  ( ) )  {   VerbatimLogger . info  ( "Using subword embeddings\n" ) ;    this . subwordEmbeddingVectors =  new CompressedVectorStoreRAM  ( flagConfig ) ; }  if  (  elementalTermVectors != null )  {   retraining = true ;    this . elementalTermVectors = elementalTermVectors ;   VerbatimLogger . info  (   "Reusing basic term vectors; number of terms: " +  elementalTermVectors . getNumVectors  ( ) + "\n" ) ;  if  (   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) )  {    this . semanticTermVectors =  new VectorStoreRAM  ( flagConfig ) ;    this . semanticTermVectors . initFromFile  (   flagConfig . initialtermvectors  ( ) . replaceAll  ( "elemental" , "embedding" ) ) ;  if  (    flagConfig . positionalmethod  ( ) !=  PositionalMethod . BASIC &&   flagConfig . vectortype  ( ) . equals  (  VectorType . REAL ) )  {  VectorType  repType =  flagConfig . vectortype  ( ) ;   flagConfig . setVectortype  (  VectorType . PERMUTATION ) ;    this . permutationCache =  new VectorStoreRAM  ( flagConfig ) ;    this . permutationCache . initFromFile  (  flagConfig . permutationcachefile  ( ) ) ;   flagConfig . setVectortype  ( repType ) ; } } } else  {    this . elementalTermVectors =  new ElementalVectorStore  ( flagConfig ) ; }  if  (   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) )  {  if  (  !   flagConfig . vectortype  ( ) . equals  (  VectorType . BINARY ) )  {    flagConfig . seedlength =  flagConfig . dimension  ( ) ;   VerbatimLogger . info  ( "Setting seedlength=dimensionality, to initialize embedding weights" ) ; } else  {   VerbatimLogger . info  ( "Warning: binary vector embeddings are in the experimental phase" ) ; } }  if  (  permutationCache == null )  {  if  (    flagConfig . positionalmethod  ( ) ==  PositionalMethod . PERMUTATION ||   flagConfig . positionalmethod  ( ) ==  PositionalMethod . PERMUTATIONPLUSBASIC )   initializePermutations  ( ) ; else  if  (   flagConfig . positionalmethod  ( ) ==  PositionalMethod . DIRECTIONAL )   initializeDirectionalPermutations  ( ) ; else  if  (   flagConfig . positionalmethod  ( ) ==  PositionalMethod . PROXIMITY )   initializeNumberRepresentations  ( ) ; } else  if  (   flagConfig . positionalmethod  ( ) ==  PositionalMethod . DIRECTIONAL )   initializeDirectionalPermutations  ( ) ; else  if  (   flagConfig . positionalmethod  ( ) ==  PositionalMethod . PROXIMITY )   initializeNumberRepresentations  ( ) ;   trainTermTermVectors  ( ) ; }   private void initializePermutations  ( )  {  VectorType  typeA =  flagConfig . vectortype  ( ) ;   flagConfig . setVectortype  (  VectorType . PERMUTATION ) ;   permutationCache =  new VectorStoreRAM  ( flagConfig ) ;   flagConfig . setVectortype  ( typeA ) ;  for (   int  i =   - 1 *  flagConfig . windowradius  ( ) ;  i <=  flagConfig . windowradius  ( ) ;  ++ i )  {  {  if  (  i == 0 )  {    int  [ ]  noPerm =  new  int  [  flagConfig . dimension  ( ) ] ;  for (   int  q = 0 ;  q <  flagConfig . dimension  ( ) ;  q ++ )    noPerm [ q ] = q ;   permutationCache . putVector  (  "" + 0 ,  new PermutationVector  ( noPerm ) ) ; } else  {   permutationCache . putVector  (  "" + i ,  new PermutationVector  (  PermutationUtils . getRandomPermutation  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ) ) ;   permutationCache . putVector  (  "_" + i ,  new PermutationVector  (  PermutationUtils . getInversePermutation  (   (  ( PermutationVector )  permutationCache . getVector  (  "" + i ) ) . getCoordinates  ( ) ) ) ) ; } } } }   private synchronized void populateQueue  ( )  {  if  (     this . totalQueueCount . get  ( ) >=  luceneUtils . getNumDocs  ( ) ||  randomStartpoints . isEmpty  ( ) )  {  if  (   theQ . size  ( ) == 0 )   exhaustedQ . set  ( true ) ;  return ; }   int  added = 0 ;   int  startdoc =  randomStartpoints . poll  ( ) ;   int  stopdoc =  Math . min  (  startdoc + qsize ,  luceneUtils . getNumDocs  ( ) ) ;  for (   int  a = startdoc ;  a < stopdoc ;  a ++ )  {  for ( String field :  flagConfig . contentsfields  ( ) )  try  {   int  docID = a ;  Terms  incomingTermVector =  luceneUtils . getTermVector  ( a , field ) ;   totalQueueCount . incrementAndGet  ( ) ;  if  (  incomingTermVector != null )  {   theQ . add  (  new DocIdTerms  ( docID , incomingTermVector ) ) ;   added ++ ; } }  catch (   IOException e )  {   e . printStackTrace  ( ) ; } }  if  (  added > 0 )    System . err . println  (   "Initialized TermVector Queue with " + added + " documents" ) ; }   private synchronized DocIdTerms drawFromQueue  ( )  {  if  (  theQ . isEmpty  ( ) )   populateQueue  ( ) ;  DocIdTerms  toReturn =  theQ . poll  ( ) ;  return toReturn ; }   private boolean queueExhausted  ( )  {  return  exhaustedQ . get  ( ) ; }   private void initializeNumberRepresentations  ( )  {  if  (   flagConfig . vectortype  ( ) . equals  (  VectorType . REAL ) )   initializeProximityPermutations  ( ) ; else  {  NumberRepresentation  numberRepresentation =  new NumberRepresentation  ( flagConfig ) ;   positionalNumberVectors =  numberRepresentation . getNumberVectors  ( 1 ,   2 *  flagConfig . windowradius  ( ) + 2 ) ;  try  {   VectorStoreWriter . writeVectorsInLuceneFormat  ( "numbervectors.bin" , flagConfig , positionalNumberVectors ) ; }  catch (   IOException e )  {   e . printStackTrace  ( ) ; } } }   private void initializeProximityPermutations  ( )  {  VectorType  typeA =  flagConfig . vectortype  ( ) ;   flagConfig . setVectortype  (  VectorType . PERMUTATION ) ;   permutationCache =  new VectorStoreRAM  ( flagConfig ) ;   flagConfig . setVectortype  ( typeA ) ;    int  [ ]  noPerm =  new  int  [  flagConfig . dimension  ( ) ] ;  for (   int  q = 0 ;  q <  flagConfig . dimension  ( ) ;  q ++ )    noPerm [ q ] = q ;   permutationCache . putVector  (  "" + 0 ,  new PermutationVector  ( noPerm ) ) ;   permutationCache . putVector  (  "" + 1 ,  new PermutationVector  (  PermutationUtils . getRandomPermutation  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ) ) ;   permutationCache . putVector  (  "_" + 1 ,  new PermutationVector  (  PermutationUtils . getInversePermutation  (   (  ( PermutationVector )  permutationCache . getVector  (  "" + 1 ) ) . getCoordinates  ( ) ) ) ) ;   permutationCache . putVector  (  "" +  - 1 ,  new PermutationVector  (  PermutationUtils . getRandomPermutation  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ) ) ;   permutationCache . putVector  (  "_" +  - 1 ,  new PermutationVector  (  PermutationUtils . getInversePermutation  (   (  ( PermutationVector )  permutationCache . getVector  (  "" +  - 1 ) ) . getCoordinates  ( ) ) ) ) ;  for (   int  i =  - 2 ;  i >=   - 1 *  flagConfig . windowradius  ( ) ;  -- i )  {    int  [ ]  toAdd =  PermutationUtils . getSwapPermutation  (  flagConfig . vectortype  ( ) ,   (  ( PermutationVector )  permutationCache . getVector  (   "" + i + 1 ) ) . getCoordinates  ( ) , .25 ) ;   permutationCache . putVector  (  "" + i ,  new PermutationVector  ( toAdd ) ) ;   permutationCache . putVector  (  "_" + i ,  new PermutationVector  (  PermutationUtils . getInversePermutation  ( toAdd ) ) ) ; }  for (   int  i = 2 ;  i <=  flagConfig . windowradius  ( ) ;  ++ i )  {    int  [ ]  toAdd =  PermutationUtils . getSwapPermutation  (  flagConfig . vectortype  ( ) ,   (  ( PermutationVector )  permutationCache . getVector  (  "" +  (  i - 1 ) ) ) . getCoordinates  ( ) , .25 ) ;   permutationCache . putVector  (  "" + i ,  new PermutationVector  ( toAdd ) ) ;   permutationCache . putVector  (  "_" + i ,  new PermutationVector  (  PermutationUtils . getInversePermutation  ( toAdd ) ) ) ; } }   private void initializeDirectionalPermutations  ( )  {  VectorType  typeA =  flagConfig . vectortype  ( ) ;   flagConfig . setVectortype  (  VectorType . PERMUTATION ) ;   permutationCache =  new VectorStoreRAM  ( flagConfig ) ;   flagConfig . setVectortype  ( typeA ) ;   permutationCache . putVector  (  "" +  - 1 ,  new PermutationVector  (  PermutationUtils . getRandomPermutation  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ) ) ;   permutationCache . putVector  (  "_" +  - 1 ,  new PermutationVector  (  PermutationUtils . getInversePermutation  (   (  ( PermutationVector )  permutationCache . getVector  (  "" +  - 1 ) ) . getCoordinates  ( ) ) ) ) ;    int  [ ]  noPerm =  new  int  [  flagConfig . dimension  ( ) ] ;  for (   int  q = 0 ;  q <  flagConfig . dimension  ( ) ;  q ++ )    noPerm [ q ] = q ;   permutationCache . putVector  (  "" + 0 ,  new PermutationVector  ( noPerm ) ) ;   permutationCache . putVector  (  "" + 1 ,  new PermutationVector  (  PermutationUtils . getRandomPermutation  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ) ) ;   permutationCache . putVector  (  "_" + 1 ,  new PermutationVector  (  PermutationUtils . getInversePermutation  (   (  ( PermutationVector )  permutationCache . getVector  (  "" + 1 ) ) . getCoordinates  ( ) ) ) ) ; }   private class TrainTermVectorThread  implements  Runnable  {   int  dcnt = 0 ;   int  threadno = 0 ;   double  time = 0 ;  BLAS  blas = null ;   public TrainTermVectorThread  (   int threadno )  {    this . threadno = threadno ;    this . blas =  BLAS . getInstance  ( ) ;    this . time =  System . currentTimeMillis  ( ) ; }    @ Override public void run  ( )  {  while  (  !  queueExhausted  ( ) )  {  for ( String field :  flagConfig . contentsfields  ( ) )  {  try  {  DocIdTerms  terms =  drawFromQueue  ( ) ;  if  (  terms != null )  {   processTermPositionVector  ( terms , field , blas ) ; } }  catch (   ArrayIndexOutOfBoundsException | IOException e )  {   e . printStackTrace  ( ) ; } }   dcnt ++ ;  if  (   (   dcnt % 10000 == 0 ) ||  (   dcnt < 10000 &&   dcnt % 1000 == 0 ) )  {   VerbatimLogger . info  (        "[T" + threadno + "]" + " processed " + dcnt + " documents in " +   (  "" +  (   (   System . currentTimeMillis  ( ) - time ) /  (  1000 * 60 ) ) ) . replaceAll  ( "\\..*" , "" ) + " min.." ) ; }  if  (    threadno == 0 &&   dcnt % tpd_average == 0 &&   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) )  {   double  proportionComplete =   totalDocCount . get  ( ) /  (  double )  (   (  1 +  flagConfig . trainingcycles  ( ) ) *  (  luceneUtils . getNumDocs  ( ) ) ) ;   alpha =  initial_alpha *  (  1 - proportionComplete ) ;  if  (  alpha < minimum_alpha )   alpha = minimum_alpha ;  if  (   (   dcnt % 10000 == 0 ) ||  (   dcnt < 10000 &&   dcnt % 1000 == 0 ) )   VerbatimLogger . info  (   "..Updated alpha to " + alpha + ".." ) ; } } } }   private void trainTermTermVectors  ( )  throws IOException , RuntimeException  {   luceneUtils =  new LuceneUtils  ( flagConfig ) ;   termDic =  new  ConcurrentSkipListMap  < Double , String >  ( ) ;  if  (    flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) &&   flagConfig . docindexing  ( ) . equals  (  DocIndexingStrategy . INMEMORY ) )   embeddingDocVectors =  new VectorStoreRAM  ( flagConfig ) ;   totalPool = 0 ;  FieldInfos  fieldsWithPositions =  luceneUtils . getFieldInfos  ( ) ;  if  (  !  fieldsWithPositions . hasVectors  ( ) )  {  throw  new IOException  (  "Term-term indexing requires a Lucene index containing TermPositionVectors." + "\nTry rebuilding Lucene index using pitt.search.lucene.IndexFilePositions" ) ; }  if  (   this . semanticTermVectors == null )    this . semanticTermVectors =  new VectorStoreRAM  ( flagConfig ) ;   int  tc = 0 ;  for ( String fieldName :  flagConfig . contentsfields  ( ) )  {  TermsEnum  terms =    this . luceneUtils . getTermsForField  ( fieldName ) . iterator  ( ) ;  BytesRef  bytes ;  while  (   (  bytes =  terms . next  ( ) ) != null )  {  Term  term =  new Term  ( fieldName , bytes ) ;  if  (  !  luceneUtils . termFilter  ( term ) )  continue ;   tc ++ ;   totalCount +=  luceneUtils . getGlobalTermFreq  ( term ) ;  if  (    flagConfig . samplingthreshold  ( ) <= 0 ||   flagConfig . samplingthreshold  ( ) >= 1 )  {   totalPool +=  Math . pow  (  luceneUtils . getGlobalTermFreq  ( term ) , .5 ) ;   termDic . put  ( totalPool ,  term . text  ( ) ) ; }  Vector  termVector = null ;  if  (   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) )  {   termVector =  VectorFactory . generateRandomVector  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ,  flagConfig . seedlength  ( ) , random ) ; } else   termVector =  VectorFactory . createZeroVector  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ;  if  (  !   this . semanticTermVectors . containsVector  (  term . text  ( ) ) )    this . semanticTermVectors . putVector  (  term . text  ( ) , termVector ) ;  if  (  ! retraining )  {    this . elementalTermVectors . getVector  (  term . text  ( ) ) ; } else  if  (   retraining &&   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) &&  !  elementalTermVectors . containsVector  (  term . text  ( ) ) )  {    (  ( VectorStoreRAM )  this . elementalTermVectors ) . putVector  (  term . text  ( ) ,  VectorFactory . generateRandomVector  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ,  flagConfig . seedlength , random ) ) ; } } }   VerbatimLogger . info  (    "\nNumber term vectors " +  semanticTermVectors . getNumVectors  ( ) + "\t" +  elementalTermVectors . getNumVectors  ( ) ) ;   tpd_average =  totalCount /  luceneUtils . getNumDocs  ( ) ;  if  (    flagConfig . samplingthreshold  ( ) > 0 &&   flagConfig . samplingthreshold  ( ) < 1 )  {   subsamplingProbabilities =  new  ConcurrentHashMap  < String , Double >  ( ) ;   VerbatimLogger . info  (     "Populating subsampling probabilities - total term count = " + totalCount + " which is " + tpd_average + " per doc on average" ) ;   int  count = 0 ;  for ( String fieldName :  flagConfig . contentsfields  ( ) )  {  TermsEnum  terms =    this . luceneUtils . getTermsForField  ( fieldName ) . iterator  ( ) ;  BytesRef  bytes ;  while  (   (  bytes =  terms . next  ( ) ) != null )  {  Term  term =  new Term  ( fieldName , bytes ) ;  if  (    ++ count % 10000 == 0 )   VerbatimLogger . info  ( "." ) ;  if  (  !  semanticTermVectors . containsVector  (  term . text  ( ) ) )  continue ;   double  globalFreq =   (  double )  luceneUtils . getGlobalTermFreq  ( term ) /  (  double ) totalCount ;   double  subdiscount = 1 ;  if  (  globalFreq >  flagConfig . samplingthreshold  ( ) )  {   double  subsample_probability =  1 -  (  Math . sqrt  (   flagConfig . samplingthreshold  ( ) / globalFreq ) ) ;  if  (  flagConfig . aggressivesubsampling  ( ) )   subsample_probability =  1 -  (   Math . sqrt  (   flagConfig . samplingthreshold  ( ) / globalFreq ) +  (   flagConfig . samplingthreshold  ( ) / globalFreq ) ) ;   subsamplingProbabilities . put  (   fieldName + ":" +  bytes . utf8ToString  ( ) , subsample_probability ) ;  if  (  flagConfig . discountnegativesampling  ( ) )   subdiscount =  1 - subsample_probability ; }   totalPool +=  Math . pow  (  subdiscount *  luceneUtils . getGlobalTermFreq  ( term ) , .5 ) ;   termDic . put  ( totalPool ,  term . text  ( ) ) ; } }   VerbatimLogger . info  ( "\n" ) ;  if  (   subsamplingProbabilities != null &&   subsamplingProbabilities . size  ( ) > 0 )   VerbatimLogger . info  (   "Selected for subsampling: " +  subsamplingProbabilities . size  ( ) + " terms.\n" ) ; }   VerbatimLogger . info  (     "There are now elemental term vectors for " + tc + " terms (and " +  luceneUtils . getNumDocs  ( ) + " docs).\n" ) ;   totalDocCount . set  ( 0 ) ;  if  (  qsize >  luceneUtils . getNumDocs  ( ) )   qsize =   luceneUtils . getNumDocs  ( ) / 10 ;  for (   int  trainingcycle = 0 ;  trainingcycle <=  flagConfig . trainingcycles  ( ) ;  trainingcycle ++ )  {   initializeRandomizationStartpoints  ( qsize ) ;   exhaustedQ . set  ( false ) ;   theQ =  new  ConcurrentLinkedQueue  < >  ( ) ;   totalQueueCount . set  ( 0 ) ;   populateQueue  ( ) ;   double  cycleStart =  System . currentTimeMillis  ( ) ;   int  numthreads =  flagConfig . numthreads  ( ) ;  ExecutorService  executor =  Executors . newFixedThreadPool  ( numthreads ) ;  for (   int  q = 0 ;  q < numthreads ;  q ++ )  {   executor . execute  (  new TrainTermVectorThread  ( q ) ) ;   VerbatimLogger . info  (   "Started thread " + q + "\n" ) ; }   executor . shutdown  ( ) ;  while  (  !  executor . isTerminated  ( ) )  {  if  (   theQ . size  ( ) <  qsize / 2 )  {   populateQueue  ( ) ; } }   VerbatimLogger . info  (   "\nTime for training cycle " +  (   System . currentTimeMillis  ( ) - cycleStart ) + "ms \n" ) ;   VerbatimLogger . info  (   "\nProcessed " +  totalQueueCount . get  ( ) + " documents" ) ; }   VerbatimLogger . info  (   "\nCreated " +  semanticTermVectors . getNumVectors  ( ) + " term vectors ...\n" ) ;  if  (    flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) &&  (   !  flagConfig . notnormalized ||  flagConfig . subword_embeddings  ( ) ) )  {   Enumeration  < ObjectVector >  g =  semanticTermVectors . getAllVectors  ( ) ;  while  (  g . hasMoreElements  ( ) )  {  ObjectVector  nextObjectVector =  g . nextElement  ( ) ;  if  (  flagConfig . subword_embeddings  ( ) )  {   ArrayList  < String >  subwordStrings =  getComponentNgrams  (   nextObjectVector . getObject  ( ) . toString  ( ) ) ;   float  weightReduction =  1 /  (   (  float )  subwordStrings . size  ( ) + 1 ) ;  Vector  wordVec =  VectorFactory . createZeroVector  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ;  if  (  flagConfig . balanced_subwords  ( ) )   weightReduction = 1 ;   wordVec . superpose  (  nextObjectVector . getVector  ( ) , weightReduction , null ) ;  if  (  flagConfig . balanced_subwords  ( ) )   weightReduction =  1 /  (  (  float )  subwordStrings . size  ( ) ) ;  for ( String subword : subwordStrings )  {  Vector  subwordVector =  subwordEmbeddingVectors . getVector  ( subword , false ) ;   wordVec . superpose  ( subwordVector , weightReduction , null ) ; }   nextObjectVector . setVector  ( wordVec ) ; }  if  (  !  flagConfig . notnormalized  ( ) )    nextObjectVector . getVector  ( ) . normalize  ( ) ; } }  if  (    (   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) ) ||   flagConfig . positionalmethod  ( ) ==  PositionalMethod . PERMUTATION ||    flagConfig . positionalmethod  ( ) ==  PositionalMethod . PERMUTATIONPLUSBASIC &&  (   ! retraining ||   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) ) )  {   VerbatimLogger . info  (   "Normalizing and writing elemental vectors to " +  flagConfig . elementalvectorfile  ( ) + "\n" ) ;   Enumeration  < ObjectVector >  f =  elementalTermVectors . getAllVectors  ( ) ;  if  (  !  flagConfig . notnormalized  ( ) )  while  (  f . hasMoreElements  ( ) )  {     f . nextElement  ( ) . getVector  ( ) . normalize  ( ) ; } }   VectorStoreWriter . writeVectorsInLuceneFormat  (   flagConfig . elementalvectorfile  ( ) + ".bin" , flagConfig ,  this . elementalTermVectors ) ;  if  (  permutationCache != null )  {  VectorType  typeA =  flagConfig . vectortype  ( ) ;   flagConfig . setVectortype  (  VectorType . PERMUTATION ) ;   VectorStoreWriter . writeVectorsInLuceneFormat  (   flagConfig . permutationcachefile  ( ) + ".bin" , flagConfig ,  this . permutationCache ) ;   flagConfig . setVectortype  ( typeA ) ; }  if  (    flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) &&   flagConfig . docindexing  ( ) . equals  (  DocIndexingStrategy . INMEMORY ) )  {   Enumeration  < ObjectVector >  f =  embeddingDocVectors . getAllVectors  ( ) ;  File  vectorFile =  new File  (  VectorStoreUtils . getStoreFileName  (  flagConfig . docvectorsfile  ( ) , flagConfig ) ) ;  String  parentPath =  vectorFile . getParent  ( ) ;  if  (  parentPath == null )   parentPath = "" ;  FSDirectory  fsDirectory =  FSDirectory . open  (   FileSystems . getDefault  ( ) . getPath  ( parentPath ) ) ;  IndexOutput  outputStream =  fsDirectory . createOutput  (  vectorFile . getName  ( ) ,  IOContext . DEFAULT ) ;   VerbatimLogger . info  (   "Writing vectors incrementally to file " + vectorFile + " ... " ) ;   outputStream . writeString  (  VectorStoreWriter . generateHeaderString  ( flagConfig ) ) ;  while  (  f . hasMoreElements  ( ) )  {  ObjectVector  nextObjectVector =  f . nextElement  ( ) ;  Vector  nextVector =  nextObjectVector . getVector  ( ) ;  if  (  !  flagConfig . notnormalized  ( ) )   nextVector . normalize  ( ) ;   int  docID =  ( Integer )  nextObjectVector . getObject  ( ) ;  String  docName =  "" +  luceneUtils . getExternalDocId  ( docID ) ;   outputStream . writeString  ( docName ) ;   nextVector . writeToLuceneStream  ( outputStream ) ; }   VerbatimLogger . info  ( "Finished writing vectors.\n" ) ;   outputStream . close  ( ) ;   fsDirectory . close  ( ) ; } }   private void processEmbeddings  (  Vector embeddingVector ,   ArrayList  < Vector > contextVectors ,   ArrayList  < Integer > contextLabels ,   double learningRate ,  BLAS blas ,    int  [ ] permutation ,    int  [ ] inversePermutation )  {   double  scalarProduct = 0 ;   double  error = 0 ;   int  counter = 0 ;  for ( Vector contextVec : contextVectors )  {  Vector  duplicateContextVec =  contextVec . copy  ( ) ;   scalarProduct =  VectorUtils . scalarProduct  ( embeddingVector , duplicateContextVec , flagConfig , blas , permutation ) ;  if  (  !   flagConfig . vectortype  ( ) . equals  (  VectorType . BINARY ) )  {  if  (  scalarProduct > MAX_EXP )   error =   contextLabels . get  (  counter ++ ) - 1 ; else  if  (  scalarProduct <  - MAX_EXP )   error =  contextLabels . get  (  counter ++ ) ; else   error =   contextLabels . get  (  counter ++ ) -  sigmoidTable . sigmoid  ( scalarProduct ) ; } else  {   scalarProduct =  Math . max  ( scalarProduct , 0 ) ;   error =   contextLabels . get  (  counter ++ ) - scalarProduct ;   error =  Math . round  (  error * 100 ) ; }  if  (  error != 0 )  {   VectorUtils . superposeInPlace  ( embeddingVector , contextVec , flagConfig , blas ,  learningRate * error , inversePermutation ) ;   VectorUtils . superposeInPlace  ( duplicateContextVec , embeddingVector , flagConfig , blas ,  learningRate * error , permutation ) ; } } }   private void processEmbeddings  (   ArrayList  < Vector > embeddingVectors ,   ArrayList  < Vector > contextVectors ,   ArrayList  < Integer > contextLabels ,   double learningRate ,  BLAS blas ,    int  [ ] permutation ,    int  [ ] inversePermutation )  {   double  scalarProduct = 0 ;   double  error = 0 ;   int  counter = 0 ;  Vector  embeddingVector =  VectorFactory . createZeroVector  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ) ;   float  weightReduction = 1 ;  for (   int  v = 0 ;  v <  embeddingVectors . size  ( ) ;  v ++ )  {  if  (  flagConfig . balanced_subwords )  {  if  (  v > 0 )   weightReduction =  1 /  (  (  float )  embeddingVectors . size  ( ) ) ; } else   weightReduction =  1 /  (  (  float )  embeddingVectors . size  ( ) ) ;   embeddingVector . superpose  (  embeddingVectors . get  ( v ) , weightReduction , null ) ; }  for ( Vector contextVec : contextVectors )  {  Vector  duplicateContextVec =  contextVec . copy  ( ) ;   scalarProduct =  VectorUtils . scalarProduct  ( embeddingVector , duplicateContextVec , flagConfig , blas , permutation ) ;  if  (  !   flagConfig . vectortype  ( ) . equals  (  VectorType . BINARY ) )  {  if  (  scalarProduct > MAX_EXP )   error =   contextLabels . get  (  counter ++ ) - 1 ; else  if  (  scalarProduct <  - MAX_EXP )   error =  contextLabels . get  (  counter ++ ) ; else   error =  (  float )  (   contextLabels . get  (  counter ++ ) -  sigmoidTable . sigmoid  ( scalarProduct ) ) ; } else  {   scalarProduct =  Math . max  ( scalarProduct , 0 ) ;   error =   contextLabels . get  (  counter ++ ) - scalarProduct ;   error =  Math . round  (  error * 100 ) ; }  if  (  error != 0 )  {   VectorUtils . superposeInPlace  ( embeddingVector , contextVec , flagConfig , blas ,  learningRate * error , inversePermutation ) ;   weightReduction = 1 ;  for (   int  v = 0 ;  v <  embeddingVectors . size  ( ) ;  v ++ )  {  if  (  flagConfig . balanced_subwords )  {  if  (  v > 0 )   weightReduction =  1 /  (  (  float )  embeddingVectors . size  ( ) ) ; }   VectorUtils . superposeInPlace  ( duplicateContextVec ,  embeddingVectors . get  ( v ) , flagConfig , blas ,   weightReduction * learningRate * error , permutation ) ; } } } }   private void processTermPositionVector  (  DocIdTerms terms ,  String field ,  BLAS blas )  throws ArrayIndexOutOfBoundsException , IOException  {  if  (  terms == null )  return ;   Hashtable  < Integer , String >  localTermPositions =  new  Hashtable  < Integer , String >  ( ) ;   ArrayList  < Integer >  thePositions =  new  ArrayList  < Integer >  ( ) ;  TermsEnum  termsEnum =   terms . terms . iterator  ( ) ;  BytesRef  text ;  Integer  docID =  terms . docID ;  while  (   (  text =  termsEnum . next  ( ) ) != null )  {  String  theTerm =  text . utf8ToString  ( ) ;  if  (  !  semanticTermVectors . containsVector  ( theTerm ) )  continue ;  PostingsEnum  docsAndPositions =  termsEnum . postings  ( null ) ;  if  (  docsAndPositions == null )  continue ;   docsAndPositions . nextDoc  ( ) ;   int  freq =  docsAndPositions . freq  ( ) ;  for (   int  x = 0 ;  x < freq ;  x ++ )  {   int  thePosition =  docsAndPositions . nextPosition  ( ) ;  if  (    subsamplingProbabilities != null &&  subsamplingProbabilities . containsKey  (   field + ":" + theTerm ) &&   random . nextDouble  ( ) <=  subsamplingProbabilities . get  (   field + ":" + theTerm ) )  {  if  (  flagConfig . exactwindowpositions  ( ) )  {   localTermPositions . put  ( thePosition , "_BLANK_" ) ;   thePositions . add  ( thePosition ) ; }  continue ; }   localTermPositions . put  ( thePosition , theTerm ) ;   thePositions . add  ( thePosition ) ; } }   Collections . sort  ( thePositions ) ;  for (   int  occupiedPositionNumber = 0 ;  occupiedPositionNumber <  thePositions . size  ( ) ;  occupiedPositionNumber ++ )  {   int  focusposn =  thePositions . get  ( occupiedPositionNumber ) ;  String  focusterm =  localTermPositions . get  ( focusposn ) ;  if  (   flagConfig . exactwindowpositions  ( ) &&  focusterm . equals  ( "_BLANK_" ) )  continue ;   int  effectiveWindowRadius =  flagConfig . windowradius  ( ) ;  if  (  flagConfig . subsampleinwindow )   effectiveWindowRadius =   random . nextInt  (  flagConfig . windowradius  ( ) ) + 1 ;   int  windowstart =  Math . max  ( 0 ,  occupiedPositionNumber - effectiveWindowRadius ) ;  if  (   flagConfig . truncatedleftradius  ( ) > 0 )  {   int  truncatedLeftRadius =  Math . min  (  flagConfig . truncatedleftradius  ( ) , effectiveWindowRadius ) ;   windowstart =  Math . max  ( 0 ,  occupiedPositionNumber - truncatedLeftRadius ) ; }   int  windowend =  Math . min  (  occupiedPositionNumber + effectiveWindowRadius ,   thePositions . size  ( ) - 1 ) ;  for (   int  cursorPositionNumber = windowstart ;  cursorPositionNumber <= windowend ;  cursorPositionNumber ++ )  {  if  (   cursorPositionNumber == occupiedPositionNumber &&  !  (    flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) &&   flagConfig . docindexing  ( ) . equals  (  DocIndexingStrategy . INMEMORY ) ) )  continue ;  String  coterm =  localTermPositions . get  (  thePositions . get  ( cursorPositionNumber ) ) ;  if  (   flagConfig . exactwindowpositions  ( ) &&  coterm . equals  ( "_BLANK_" ) )  continue ;    int  [ ]  permutation = null ;    int  [ ]  inversePermutation = null ;   int  desiredPermutation =  cursorPositionNumber - occupiedPositionNumber ;  if  (    flagConfig . positionalmethod  ( ) . equals  (  PositionalMethod . PERMUTATION ) ||   flagConfig . positionalmethod  ( ) . equals  (  PositionalMethod . PROXIMITY ) )  {   permutation =   (  ( PermutationVector )  permutationCache . getVector  (  "" + desiredPermutation ) ) . getCoordinates  ( ) ;   inversePermutation =   (  ( PermutationVector )  permutationCache . getVector  (  "_" + desiredPermutation ) ) . getCoordinates  ( ) ;  if  (  permutation == null )   VerbatimLogger . info  ( "null permutation" ) ;  if  (  inversePermutation == null )   VerbatimLogger . info  ( "null inverse permutation" ) ; } else  if  (   flagConfig . positionalmethod  ( ) . equals  (  PositionalMethod . DIRECTIONAL ) )  {   permutation =   (  ( PermutationVector )  permutationCache . getVector  (  (  int )  Math . signum  ( desiredPermutation ) ) ) . getCoordinates  ( ) ;   inversePermutation =   (  ( PermutationVector )  permutationCache . getVector  (  "_" +  (  int )  Math . signum  ( desiredPermutation ) ) ) . getCoordinates  ( ) ;  if  (  permutation == null )   VerbatimLogger . info  ( "null permutation" ) ;  if  (  inversePermutation == null )   VerbatimLogger . info  ( "null inverse permutation" ) ; }  Vector  toSuperpose =  elementalTermVectors . getVector  ( coterm ) ;  if  (   flagConfig . encodingmethod  ( ) . equals  (  EncodingMethod . EMBEDDINGS ) )  {   ArrayList  < Vector >  contextVectors =  new  ArrayList  < Vector >  ( ) ;   ArrayList  < Integer >  contextLabels =  new  ArrayList  < Integer >  ( ) ;   contextVectors . add  ( toSuperpose ) ;   contextLabels . add  ( 1 ) ;  while  (   contextVectors . size  ( ) <=  flagConfig . negsamples )  {  Vector  randomTerm = null ;   double  max = totalPool ;  while  (  randomTerm == null )  {   double  test =   random . nextDouble  ( ) * max ;  if  (   termDic . ceilingEntry  ( test ) != null )  {  String  testTerm =   termDic . ceilingEntry  ( test ) . getValue  ( ) ;  if  (  !  testTerm . equals  ( coterm ) )   randomTerm =  elementalTermVectors . getVector  ( testTerm ) ; } }   contextVectors . add  ( randomTerm ) ;   contextLabels . add  ( 0 ) ; }  if  (  cursorPositionNumber != occupiedPositionNumber )  {  if  (  flagConfig . subword_embeddings  ( ) )  {   ArrayList  < String >  subWords =  getComponentNgrams  ( focusterm ) ;   ArrayList  < Vector >  subWordVectors =  new  ArrayList  < Vector >  ( ) ;   subWordVectors . add  (  semanticTermVectors . getVector  ( focusterm ) ) ;  for ( String subword : subWords )   subWordVectors . add  (  subwordEmbeddingVectors . getVector  ( subword , false ) ) ;   this . processEmbeddings  ( subWordVectors , contextVectors , contextLabels , alpha , blas , permutation , inversePermutation ) ; } else   processEmbeddings  (  semanticTermVectors . getVector  ( focusterm ) , contextVectors , contextLabels , alpha , blas , permutation , inversePermutation ) ; }  if  (   flagConfig . docindexing  ( ) . equals  (  DocIndexingStrategy . INMEMORY ) )  {  if  (  !  embeddingDocVectors . containsVector  ( docID ) )   embeddingDocVectors . putVector  ( docID ,  VectorFactory . generateRandomVector  (  flagConfig . vectortype  ( ) ,  flagConfig . dimension  ( ) ,  flagConfig . seedlength , random ) ) ;   this . processEmbeddings  (  embeddingDocVectors . getVector  ( docID ) , contextVectors , contextLabels , alpha , blas , permutation , inversePermutation ) ; } } else  {   float  globalweight =  luceneUtils . getGlobalTermWeight  (  new Term  ( field , coterm ) ) ;  if  (    flagConfig . positionalmethod  ( ) ==  PositionalMethod . BASIC ||   flagConfig . positionalmethod  ( ) ==  PositionalMethod . PERMUTATIONPLUSBASIC )  {    semanticTermVectors . getVector  ( focusterm ) . superpose  ( toSuperpose , globalweight , null ) ; }  if  (     flagConfig . positionalmethod  ( ) ==  PositionalMethod . PERMUTATION ||   flagConfig . positionalmethod  ( ) ==  PositionalMethod . PERMUTATIONPLUSBASIC ||   flagConfig . positionalmethod  ( ) ==  PositionalMethod . PROXIMITY )  {   Enumeration  < ObjectVector >  theVecs =  permutationCache . getAllVectors  ( ) ;   permutation =   (  ( PermutationVector )  permutationCache . getVector  ( 
<<<<<<<
 "" +  (  cursorPositionNumber - focusposn )
=======
 (  int )  (  cursorPositionNumber - occupiedPositionNumber )
>>>>>>>
 ) ) . getCoordinates  ( ) ;    semanticTermVectors . getVector  ( focusterm ) . superpose  ( toSuperpose , globalweight , permutation ) ; } else  if  (   flagConfig . positionalmethod  ( ) ==  PositionalMethod . DIRECTIONAL )  {   permutation =   (  ( PermutationVector )  permutationCache . getVector  ( 
<<<<<<<
 "" +  (  Math . signum  (  cursorPositionNumber - focusposn ) )
=======
 (  int )  Math . signum  (  cursorPositionNumber - occupiedPositionNumber )
>>>>>>>
 ) ) . getCoordinates  ( ) ;    semanticTermVectors . getVector  ( focusterm ) . superpose  ( toSuperpose , globalweight , permutation ) ; } } } }   totalDocCount . incrementAndGet  ( ) ; } }