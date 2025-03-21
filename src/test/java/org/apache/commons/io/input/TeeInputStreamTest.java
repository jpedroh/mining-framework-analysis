  package     org . apache . commons . io . input ;   import static    org . junit . Assert . assertEquals ;  import   java . io . ByteArrayInputStream ;  import   java . io . ByteArrayOutputStream ;  import   java . io . IOException ;  import   java . io . InputStream ;  import   org . junit . Assert ;  import   org . junit . Before ;  import   org . junit . Test ;  import static    org . mockito . Mockito . mock ;  import static    org . mockito . Mockito . never ;  import static    org . mockito . Mockito . times ;  import static    org . mockito . Mockito . verify ;  import   java . io . OutputStream ;  import      org . apache . commons . io . testtools . YellOnCloseInputStream ;  import      org . apache . commons . io . testtools . YellOnCloseOutputStream ;   public class TeeInputStreamTest  {   private static class ExceptionOnCloseByteArrayInputStream  extends ByteArrayInputStream  {   public ExceptionOnCloseByteArrayInputStream  ( )  {  super  (  new  byte  [ 0 ] ) ; }    @ Override public void close  ( )  throws IOException  {  throw  new IOException  ( ) ; } }   private static class RecordCloseByteArrayInputStream  extends ByteArrayInputStream  {  boolean  closed ;   public RecordCloseByteArrayInputStream  ( )  {  super  (  new  byte  [ 0 ] ) ; }    @ Override public void close  ( )  throws IOException  {   super . close  ( ) ;   closed = true ; } }   private static class ExceptionOnCloseByteArrayOutputStream  extends ByteArrayOutputStream  {    @ Override public void close  ( )  throws IOException  {  throw  new IOException  ( ) ; } }   private static class RecordCloseByteArrayOutputStream  extends ByteArrayOutputStream  {  boolean  closed ;    @ Override public void close  ( )  throws IOException  {   super . close  ( ) ;   closed = true ; } }   private final String  ASCII = "US-ASCII" ;   private InputStream  tee ;   private ByteArrayOutputStream  output ;    @ Before public void setUp  ( )  throws Exception  {   final InputStream  input =  new ByteArrayInputStream  (  "abc" . getBytes  ( ASCII ) ) ;   output =  new ByteArrayOutputStream  ( ) ;   tee =  new TeeInputStream  ( input , output ) ; }    @ Test public void testReadNothing  ( )  throws Exception  {   assertEquals  ( "" ,  new String  (  output . toString  ( ASCII ) ) ) ; }    @ Test public void testReadOneByte  ( )  throws Exception  {   assertEquals  ( 'a' ,  tee . read  ( ) ) ;   assertEquals  ( "a" ,  new String  (  output . toString  ( ASCII ) ) ) ; }    @ Test public void testReadEverything  ( )  throws Exception  {   assertEquals  ( 'a' ,  tee . read  ( ) ) ;   assertEquals  ( 'b' ,  tee . read  ( ) ) ;   assertEquals  ( 'c' ,  tee . read  ( ) ) ;   assertEquals  (  - 1 ,  tee . read  ( ) ) ;   assertEquals  ( "abc" ,  new String  (  output . toString  ( ASCII ) ) ) ; }    @ Test public void testReadToArray  ( )  throws Exception  {   final   byte  [ ]  buffer =  new  byte  [ 8 ] ;   assertEquals  ( 3 ,  tee . read  ( buffer ) ) ;   assertEquals  ( 'a' ,  buffer [ 0 ] ) ;   assertEquals  ( 'b' ,  buffer [ 1 ] ) ;   assertEquals  ( 'c' ,  buffer [ 2 ] ) ;   assertEquals  (  - 1 ,  tee . read  ( buffer ) ) ;   assertEquals  ( "abc" ,  new String  (  output . toString  ( ASCII ) ) ) ; }    @ Test public void testReadToArrayWithOffset  ( )  throws Exception  {   final   byte  [ ]  buffer =  new  byte  [ 8 ] ;   assertEquals  ( 3 ,  tee . read  ( buffer , 4 , 4 ) ) ;   assertEquals  ( 'a' ,  buffer [ 4 ] ) ;   assertEquals  ( 'b' ,  buffer [ 5 ] ) ;   assertEquals  ( 'c' ,  buffer [ 6 ] ) ;   assertEquals  (  - 1 ,  tee . read  ( buffer , 4 , 4 ) ) ;   assertEquals  ( "abc" ,  new String  (  output . toString  ( ASCII ) ) ) ; }    @ Test public void testSkip  ( )  throws Exception  {   assertEquals  ( 'a' ,  tee . read  ( ) ) ;   assertEquals  ( 1 ,  tee . skip  ( 1 ) ) ;   assertEquals  ( 'c' ,  tee . read  ( ) ) ;   assertEquals  (  - 1 ,  tee . read  ( ) ) ;   assertEquals  ( "ac" ,  new String  (  output . toString  ( ASCII ) ) ) ; }    @ Test public void testMarkReset  ( )  throws Exception  {   assertEquals  ( 'a' ,  tee . read  ( ) ) ;   tee . mark  ( 1 ) ;   assertEquals  ( 'b' ,  tee . read  ( ) ) ;   tee . reset  ( ) ;   assertEquals  ( 'b' ,  tee . read  ( ) ) ;   assertEquals  ( 'c' ,  tee . read  ( ) ) ;   assertEquals  (  - 1 ,  tee . read  ( ) ) ;   assertEquals  ( "abbc" ,  new String  (  output . toString  ( ASCII ) ) ) ; }    @ Test public void testCloseBranchIOException  ( )  throws Exception  {   final 
<<<<<<<
RecordCloseByteArrayInputStream
=======
ByteArrayInputStream
>>>>>>>
  goodIs = 
<<<<<<<
 new RecordCloseByteArrayInputStream  ( )
=======
 mock  (  ByteArrayInputStream . class )
>>>>>>>
 ;   final 
<<<<<<<
ByteArrayOutputStream
=======
OutputStream
>>>>>>>
  badOs = 
<<<<<<<
 new ExceptionOnCloseByteArrayOutputStream  ( )
=======
 new YellOnCloseOutputStream  ( )
>>>>>>>
 ;   final TeeInputStream  nonClosingTis =  new TeeInputStream  ( goodIs , badOs , false ) ;   nonClosingTis . close  ( ) ;   
<<<<<<<
Assert
=======
 verify  ( goodIs )
>>>>>>>
 . 
<<<<<<<
assertTrue
=======
close
>>>>>>>
  (  goodIs . closed ) ;   final TeeInputStream  closingTis =  new TeeInputStream  ( goodIs , badOs , true ) ;  try  {   closingTis . close  ( ) ;   Assert . fail  (  "Expected " +   IOException . class . getName  ( ) ) ; }  catch (   final  IOException e )  {   
<<<<<<<
Assert
=======
 verify  ( goodIs ,  times  ( 2 ) )
>>>>>>>
 . 
<<<<<<<
assertTrue
=======
close
>>>>>>>
  (  goodIs . closed ) ; } }    @ Test public void testCloseMainIOException  ( )  throws IOException  {   final 
<<<<<<<
ByteArrayInputStream
=======
InputStream
>>>>>>>
  badIs = 
<<<<<<<
 new ExceptionOnCloseByteArrayInputStream  ( )
=======
 new YellOnCloseInputStream  ( )
>>>>>>>
 ;   final 
<<<<<<<
RecordCloseByteArrayOutputStream
=======
ByteArrayOutputStream
>>>>>>>
  goodOs = 
<<<<<<<
 new RecordCloseByteArrayOutputStream  ( )
=======
 mock  (  ByteArrayOutputStream . class )
>>>>>>>
 ;   final TeeInputStream  nonClosingTis =  new TeeInputStream  ( badIs , goodOs , false ) ;  try  {   nonClosingTis . close  ( ) ;   Assert . fail  (  "Expected " +   IOException . class . getName  ( ) ) ; }  catch (   final  IOException e )  {   
<<<<<<<
Assert
=======
 verify  ( goodOs ,  never  ( ) )
>>>>>>>
 . 
<<<<<<<
assertFalse
=======
close
>>>>>>>
  (  goodOs . closed ) ; }   final TeeInputStream  closingTis =  new TeeInputStream  ( badIs , goodOs , true ) ;  try  {   closingTis . close  ( ) ;   Assert . fail  (  "Expected " +   IOException . class . getName  ( ) ) ; }  catch (   final  IOException e )  {   
<<<<<<<
Assert
=======
 verify  ( goodOs )
>>>>>>>
 . 
<<<<<<<
assertTrue
=======
close
>>>>>>>
  (  goodOs . closed ) ; } } }