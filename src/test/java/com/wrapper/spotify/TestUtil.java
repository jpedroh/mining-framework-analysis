  package   com . wrapper . spotify ;   import    com . wrapper . spotify . exceptions .  * ;  import  java . io .  * ;  import static    org . mockito . Matchers . any ;  import static    org . mockito . Mockito . mock ;  import static    org . mockito . Mockito . when ;  import   java . io . BufferedReader ;  import   java . io . FileInputStream ;  import   java . io . InputStreamReader ;   public class TestUtil  {   private static final String  TEST_DATA_DIR = "src/test/fixtures/" ;   private static final  int  MAX_TEST_DATA_FILE_SIZE = 65536 ;   public static String readTestData  (  String fileName )  throws IOException  {  return  readFromFile  (  new File  ( TEST_DATA_DIR , fileName ) ) ; }   private static String readFromFile  (  File file )  throws IOException  {  
<<<<<<<
BufferedReader
=======
String
>>>>>>>
 
<<<<<<<
 in =  new BufferedReader  (  new InputStreamReader  (  new FileInputStream  ( file ) , "UTF8" ) )
=======
 currentLine
>>>>>>>
 ;  StringBuilder 
<<<<<<<
 out =  new StringBuilder  ( )
=======
 result =  new StringBuilder  ( )
>>>>>>>
 ; 
<<<<<<<
=======
 InputStreamReader  reader =  new InputStreamReader  (  new FileInputStream  ( file ) , "UTF-8" ) ;
>>>>>>>
  
<<<<<<<
String
=======
BufferedReader
>>>>>>>
 
<<<<<<<
 line
=======
 bufReader =  new BufferedReader  ( reader )
>>>>>>>
 ;  while  (   (  
<<<<<<<
line
=======
currentLine
>>>>>>>
 =  
<<<<<<<
in
=======
bufReader
>>>>>>>
 . readLine  ( ) ) != null )  {   
<<<<<<<
out
=======
result
>>>>>>>
 . append  ( 
<<<<<<<
line
=======
currentLine
>>>>>>>
 ) ; } 
<<<<<<<
  in . close  ( ) ;
=======
>>>>>>>
  return  
<<<<<<<
out
=======
result
>>>>>>>
 . toString  ( ) ; }   public static class MockedHttpManager  {   public static HttpManager returningJson  (  String jsonFixture )  throws Exception  {   final HttpManager  mockedHttpManager =  mock  (  HttpManager . class ) ;   final String  fixture =  readTestData  ( jsonFixture ) ;    when  (  mockedHttpManager . get  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( fixture ) ;    when  (  mockedHttpManager . post  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( fixture ) ;    when  (  mockedHttpManager . put  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( fixture ) ;    when  (  mockedHttpManager . delete  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( fixture ) ;  return mockedHttpManager ; }   public static HttpManager returningString  (  String returnedString )  throws IOException , NoContentException , BadRequestException , UnauthorizedException , ForbiddenException , NotFoundException , TooManyRequestsException , InternalServerErrorException , BadGatewayException , ServiceUnavailableException  {   final HttpManager  mockedHttpManager =  mock  (  HttpManager . class ) ;    when  (  mockedHttpManager . get  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( returnedString ) ;    when  (  mockedHttpManager . post  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( returnedString ) ;    when  (  mockedHttpManager . put  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( returnedString ) ;    when  (  mockedHttpManager . delete  (  (  UtilProtos . Url )  any  ( ) ) ) . thenReturn  ( returnedString ) ;  return mockedHttpManager ; } } }