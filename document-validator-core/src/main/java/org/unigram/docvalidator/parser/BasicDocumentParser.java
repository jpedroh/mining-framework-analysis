  package    org . unigram . docvalidator . parser ;   import   java . io . BufferedReader ;  import   java . io . FileInputStream ;  import   java . io . FileNotFoundException ;  import   java . io . InputStream ;  import   java . io . InputStreamReader ;  import   java . io . UnsupportedEncodingException ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import     org . unigram . docvalidator . symbol . DefaultSymbols ;  import     org . unigram . docvalidator . util . CharacterTable ;  import     org . unigram . docvalidator . util . DVResource ;  import   java . util . ArrayList ;  import   java . util . List ;   public abstract class BasicDocumentParser  implements  Parser  {   public final boolean initialize  (  DVResource resource )  {  if  (  resource == null )  {   LOG . error  ( "Given resource is null" ) ;  return false ; }  if  (   resource . getCharacterTable  ( ) == null )  {    this . periods . add  (   characterTable . getCharacter  ( "FULL_STOP" ) . getValue  ( ) ) ; } else  {   
<<<<<<<
LOG
=======
 this . periods
>>>>>>>
 . 
<<<<<<<
error
=======
add
>>>>>>>
  ( 
<<<<<<<
"Character table in the given resource is null"
=======
  DefaultSymbols . get  ( "FULL_STOP" ) . getValue  ( )
>>>>>>>
 ) ;  return false ; }  if  (  characterTable . isContainCharacter  ( "QUESTION_MARK" ) )  {    this . periods . add  (   characterTable . getCharacter  ( "QUESTION_MARK" ) . getValue  ( ) ) ; } else  {    this . periods . add  (   DefaultSymbols . get  ( "QUESTION_MARK" ) . getValue  ( ) ) ; } 
<<<<<<<
   this . period =    DefaultSymbols . getInstance  ( ) . get  ( "FULL_STOP" ) . getValue  ( ) ;
=======
>>>>>>>
  if  (  characterTable . isContainCharacter  ( "EXCLAMATION_MARK" ) )  {    this . periods . add  ( 
<<<<<<<
  "Full stop is set to \"" +  this . period + "\""
=======
  characterTable . getCharacter  ( "EXCLAMATION_MARK" ) . getValue  ( )
>>>>>>>
 ) ; } else  {   LOG . warn  ( "FULL_STOP does not exist in the configuration" ) ;   
<<<<<<<
LOG
=======
 this . periods
>>>>>>>
 . 
<<<<<<<
info
=======
add
>>>>>>>
  ( 
<<<<<<<
  "Set FULL_STOP as \"" +  this . period + "\""
=======
  DefaultSymbols . get  ( "EXCLAMATION_MARK" ) . getValue  ( )
>>>>>>>
 ) ; }  for ( String period :  this . periods )  {   LOG . info  (   "\"" + period + "\" is added as a end of sentence character" ) ; }    this . sentenceExtractor =  new SentenceExtractor  (  this . periods ) ;  return true ; }   protected BufferedReader createReader  (  InputStream is )  {  BufferedReader  br ;  try  {   br =  new BufferedReader  (  new InputStreamReader  ( is , "UTF-8" ) ) ; }  catch (   UnsupportedEncodingException e )  {   LOG . error  (  e . getMessage  ( ) ) ;  return null ; }  return br ; }   protected final InputStream loadStream  (  String fileName )  {  InputStream  inputStream = null ;  if  (   fileName == null ||  fileName . equals  ( "" ) )  {   LOG . error  ( "input file was not specified." ) ;  return null ; } else  {  try  {   inputStream =  new FileInputStream  ( fileName ) ; }  catch (   FileNotFoundException e )  {   LOG . error  (  "Input file is not found: " +  e . getMessage  ( ) ) ; } }  return inputStream ; }   private static final Logger  LOG =  LoggerFactory . getLogger  (  BasicDocumentParser . class ) ;   protected SentenceExtractor getSentenceExtractor  ( )  {  return sentenceExtractor ; }   private SentenceExtractor  sentenceExtractor ;   private  List  < String >  periods =  new  ArrayList  < String >  ( ) ; }