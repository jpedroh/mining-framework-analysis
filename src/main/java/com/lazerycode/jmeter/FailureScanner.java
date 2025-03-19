  package   com . lazerycode . jmeter ;   import   java . io . File ;  import   java . io . IOException ;  import   java . util . Scanner ;  import    java . util . regex . Pattern ;  class FailureScanner  {   private static final String  REQUEST_SUCCESS_PATTERN = "s=\"true\"" ;   private final boolean  ignoreFailures ;   private  int  failureCount ;   private  int  successCount ;   public FailureScanner  (  boolean ignoreFailures )  {    this . ignoreFailures = ignoreFailures ; }   public boolean hasTestFailed  ( )  {  return   !  this . ignoreFailures &&   this . failureCount > 0 ; }   public void parseResults  (  File file )  throws IOException  {   failureCount = 0 ;   successCount = 0 ;  Scanner  resultFileScanner ;  Pattern  errorPattern =  Pattern . compile  ( REQUEST_FAILURE_PATTERN ) ;  Pattern  successPattern =  Pattern . compile  ( REQUEST_SUCCESS_PATTERN ) ;   resultFileScanner =  new Scanner  ( file ) ;  while  (   resultFileScanner . findWithinHorizon  ( errorPattern , 0 ) != null )  {   failureCount ++ ; }   resultFileScanner . close  ( ) ;   resultFileScanner =  new Scanner  ( file ) ;  while  ( 
<<<<<<<
  resultFileScanner . findWithinHorizon  ( successPattern , 0 ) != null
=======
 resultFileScanner . hasNextLine  ( )
>>>>>>>
 )  { 
<<<<<<<
  successCount ++ ;
=======
 String  line =  resultFileScanner . nextLine  ( ) ;
>>>>>>>
  if  (   SUCCESS_PATTERN . matcher  ( line ) . find  ( ) )  {   successCount ++ ; } else  if  (   ERROR_PATTERN . matcher  ( line ) . find  ( ) )  {   failureCount ++ ; } }   resultFileScanner . close  ( ) ; }   public  int getFailureCount  ( )  {  if  (  this . ignoreFailures )  {  return 0 ; } else  {  return  this . failureCount ; } }   public  int getRequestCount  ( )  {  return   this . failureCount +  this . successCount ; }   private static final String  REQUEST_FAILURE = "s=\"false\"" ;   private static final Pattern  ERROR_PATTERN =  Pattern . compile  ( REQUEST_FAILURE ) ;   private static final String  REQUEST_SUCCESS = "s=\"true\"" ;   private static final Pattern  SUCCESS_PATTERN =  Pattern . compile  ( REQUEST_SUCCESS ) ; }