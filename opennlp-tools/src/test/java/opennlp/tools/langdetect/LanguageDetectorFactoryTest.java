  package   opennlp . tools . langdetect ;   import   java . io . ByteArrayInputStream ;  import   java . io . IOException ;  import    java . nio . charset . StandardCharsets ;  import   java . util . Arrays ;  import   java . util . HashSet ;  import   java . util . Set ;  import    opennlp . tools . formats . ResourceAsStreamFactory ;  import    opennlp . tools . util . PlainTextByLineStream ;  import    opennlp . tools . util . TrainingParameters ;  import     org . junit . jupiter . api . Assertions ;  import     org . junit . jupiter . api . BeforeAll ;  import     org . junit . jupiter . api . Test ;   public class LanguageDetectorFactoryTest  {   private static LanguageDetectorModel  model ;   static  @ BeforeAll void train  ( )  throws Exception  {  ResourceAsStreamFactory  streamFactory =  new ResourceAsStreamFactory  (  LanguageDetectorMETest . class , "/opennlp/tools/doccat/DoccatSample.txt" ) ;  PlainTextByLineStream  lineStream =  new PlainTextByLineStream  ( streamFactory ,  StandardCharsets . UTF_8 ) ;  LanguageDetectorSampleStream  sampleStream =  new LanguageDetectorSampleStream  ( lineStream ) ;  TrainingParameters  params =  new TrainingParameters  ( ) ;   params . put  (  TrainingParameters . ITERATIONS_PARAM , "100" ) ;   params . put  (  TrainingParameters . CUTOFF_PARAM , "5" ) ;   params . put  (  TrainingParameters . ALGORITHM_PARAM , "NAIVEBAYES" ) ;   model =  LanguageDetectorME . train  ( sampleStream , params ,  new DummyFactory  ( ) ) ; }    @ Test void testCorrectFactory  ( )  throws IOException  {    byte  [ ]  serialized =  LanguageDetectorMETest . serializeModel  ( model ) ;  LanguageDetectorModel  myModel =  new LanguageDetectorModel  (  new ByteArrayInputStream  ( serialized ) ) ;   Assertions . assertTrue  (   myModel . getFactory  ( ) instanceof DummyFactory ) ; }    @ Test void testDummyFactory  ( )  throws Exception  {    byte  [ ]  serialized =  LanguageDetectorMETest . serializeModel  ( model ) ;  LanguageDetectorModel  myModel =  new LanguageDetectorModel  (  new ByteArrayInputStream  ( serialized ) ) ;   Assertions . assertTrue  (   myModel . getFactory  ( ) instanceof DummyFactory ) ; }    @ Test void testDummyFactoryContextGenerator  ( )  {  LanguageDetectorContextGenerator  cg =   model . getFactory  ( ) . getContextGenerator  ( ) ;   String  [ ]  context =  cg . getContext  ( "a dummy text phrase to test if the context generator works!!!!!!!!!!!!" ) ;   Set  < String >  set =  new  HashSet  < >  (  Arrays . asList  ( context ) ) ;   Assertions . assertTrue  (  set . contains  ( "!!!!!" ) ) ;   Assertions . assertTrue  (  set . contains  ( "a dum" ) ) ;   Assertions . assertTrue  (  set . contains  ( "tg=[THE,CONTEXT,GENERATOR]" ) ) ; } }