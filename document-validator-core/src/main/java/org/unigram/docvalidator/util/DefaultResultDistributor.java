  package    org . unigram . docvalidator . util ;   import   java . io . OutputStream ;  import   java . io . PrintStream ;  import   java . io . UnsupportedEncodingException ;   public class DefaultResultDistributor  implements  ResultDistributor  {  DefaultResultDistributor  (  OutputStream os )  {  super  ( ) ;  if  (  os == null )  {  throw  new IllegalArgumentException  ( "argument OutputStream is null" ) ; }  try  {   writer =  new PrintStream  ( os , true , "UTF-8" ) ; }  catch (   UnsupportedEncodingException e )  {  throw  new IllegalStateException  (  "Specified output stream is illegal: " +  e . getMessage  ( ) ) ; }   myFormatter =  new PlainFormatter  ( ) ; }   public DefaultResultDistributor  (  PrintStream ps )  {  if  (  ps == null )  {  throw  new IllegalArgumentException  ( "argument PrintStream is null" ) ; }  try  {   writer =  new PrintStream  ( ps , true , "UTF-8" ) ; }  catch (   UnsupportedEncodingException e )  {  throw  new IllegalStateException  (  "Specified output stream is illegal: " +  e . getMessage  ( ) ) ; }   myFormatter =  new PlainFormatter  ( ) ; }   public  int flushResult  (  ValidationError err )  {  if  (  err == null )  {  throw  new IllegalArgumentException  ( "argument ValidationError is null" ) ; }   writer . println  (  myFormatter . convertError  ( err ) ) ;   writer . flush  ( ) ;  return 0 ; }    @ Override public void flushHeader  ( )  {  String  header =  myFormatter . header  ( ) ;  if  (  header != null )  {   writer . println  ( header ) ; } }    @ Override public void flushFooter  ( )  {  String  footer =  myFormatter . footer  ( ) ;  if  (  footer != null )  {   writer . println  ( footer ) ;   writer . flush  ( ) ; } }    @ Override public void setFormatter  (  Formatter formatter )  {  if  (  formatter == null )  {  throw  new IllegalArgumentException  ( "argument formatter is null" ) ; }    this . myFormatter = formatter ; }   private Formatter  myFormatter ;   private PrintStream  writer ; }