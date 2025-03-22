  package   com . lambdaworks . redis ;   import static      org . assertj . core . api . Assertions . assertThat ;  import  java . io .  * ;  import   java . nio . ByteBuffer ;  import    java . nio . charset . Charset ;  import   java . util . List ;  import     com . lambdaworks . redis . codec . ByteArrayCodec ;  import     com . lambdaworks . redis . protocol . SetArgs ;  import   org . junit . Test ;  import     com . lambdaworks . redis . codec . ByteArrayCodec ;  import     com . lambdaworks . redis . codec . CompressionCodec ;  import     com . lambdaworks . redis . codec . RedisCodec ;  import    java . util . concurrent . TimeUnit ;  import     com . lambdaworks . redis . api . StatefulRedisConnection ;  import      com . lambdaworks . redis . api . sync . RedisCommands ;  import   rx . observers . TestSubscriber ;   public class CustomCodecTest  extends AbstractRedisClientTest  {    @ Test public void testJavaSerializer  ( )  throws Exception  {   StatefulRedisConnection  < String , Object >  redisConnection =  client . connect  (  new SerializedObjectCodec  ( ) ) ;   
<<<<<<<
RedisConnection
=======
RedisCommands
>>>>>>>
  < String , Object > 
<<<<<<<
 connection =  client . connect  (  new SerializedObjectCodec  ( ) )
=======
 sync =  redisConnection . sync  ( )
>>>>>>>
 ;   List  < String >  list =  list  ( "one" , "two" ) ;   
<<<<<<<
connection
=======
sync
>>>>>>>
 . set  ( key , list ) ;    assertThat  (  
<<<<<<<
connection
=======
sync
>>>>>>>
 . get  ( key ) ) . isEqualTo  ( list ) ;    assertThat  (  
<<<<<<<
connection
=======
sync
>>>>>>>
 . set  ( key , list ) ) . isEqualTo  ( "OK" ) ;    assertThat  (  
<<<<<<<
connection
=======
sync
>>>>>>>
 . set  ( key , list ,   SetArgs . Builder . ex  ( 1 ) ) ) . isEqualTo  ( "OK" ) ;   
<<<<<<<
connection
=======
redisConnection
>>>>>>>
 . close  ( ) ; }    @ Test public void testDeflateCompressedJavaSerializer  ( )  throws Exception  {   
<<<<<<<
RedisConnection
=======
RedisCommands
>>>>>>>
  < String , Object >  connection =  
<<<<<<<
client
=======
 client . connect  (  CompressionCodec . valueCompressor  (  new SerializedObjectCodec  ( ) ,   CompressionCodec . CompressionType . DEFLATE ) )
>>>>>>>
 . 
<<<<<<<
connect
=======
sync
>>>>>>>
  (  CompressionCodec . valueCompressor  (  new SerializedObjectCodec  ( ) ,   CompressionCodec . CompressionType . DEFLATE ) ) ;   List  < String >  list =  list  ( "one" , "two" ) ;   connection . set  ( key , list ) ;    assertThat  (  connection . get  ( key ) ) . isEqualTo  ( list ) ;   connection . close  ( ) ; }    @ Test public void testGzipompressedJavaSerializer  ( )  throws Exception  {   
<<<<<<<
RedisConnection
=======
RedisCommands
>>>>>>>
  < String , Object >  connection =  
<<<<<<<
client
=======
 client . connect  (  CompressionCodec . valueCompressor  (  new SerializedObjectCodec  ( ) ,   CompressionCodec . CompressionType . GZIP ) )
>>>>>>>
 . 
<<<<<<<
connect
=======
sync
>>>>>>>
  (  CompressionCodec . valueCompressor  (  new SerializedObjectCodec  ( ) ,   CompressionCodec . CompressionType . GZIP ) ) ;   List  < String >  list =  list  ( "one" , "two" ) ;   connection . set  ( key , list ) ;    assertThat  (  connection . get  ( key ) ) . isEqualTo  ( list ) ;   connection . close  ( ) ; }    @ Test public void testByteCodec  ( )  throws Exception  {   RedisConnection  <   byte  [ ] ,   byte  [ ] >  connection =   client . connect  (  new ByteArrayCodec  ( ) ) . sync  ( ) ;  String  value = "üöäü+#" ;   connection . set  (  key . getBytes  ( ) ,  value . getBytes  ( ) ) ;    assertThat  (  connection . get  (  key . getBytes  ( ) ) ) . isEqualTo  (  value . getBytes  ( ) ) ;   List  <   byte  [ ] >  keys =  connection . keys  (  key . getBytes  ( ) ) ;    assertThat  ( keys ) . contains  (  key . getBytes  ( ) ) ; }   public class SerializedObjectCodec  implements   RedisCodec  < String , Object >  {   private Charset  charset =  Charset . forName  ( "UTF-8" ) ;    @ Override public String decodeKey  (  ByteBuffer bytes )  {  return   charset . decode  ( bytes ) . toString  ( ) ; }    @ Override public Object decodeValue  (  ByteBuffer bytes )  {  try  {    byte  [ ]  array =  new  byte  [  bytes . remaining  ( ) ] ;   bytes . get  ( array ) ;  ObjectInputStream  is =  new ObjectInputStream  (  new ByteArrayInputStream  ( array ) ) ;  return  is . readObject  ( ) ; }  catch (   Exception e )  {  return null ; } }    @ Override public ByteBuffer encodeKey  (  String key )  {  return  charset . encode  ( key ) ; }    @ Override public ByteBuffer encodeValue  (  Object value )  {  try  {  ByteArrayOutputStream  bytes =  new ByteArrayOutputStream  ( ) ;  ObjectOutputStream  os =  new ObjectOutputStream  ( bytes ) ;   os . writeObject  ( value ) ;  return  ByteBuffer . wrap  (  bytes . toByteArray  ( ) ) ; }  catch (   IOException e )  {  return null ; } } }    @ Test public void testJavaSerializerRx  ( )  throws Exception  {   StatefulRedisConnection  < String , Object >  redisConnection =  client . connect  (  new SerializedObjectCodec  ( ) ) ;   List  < String >  list =  list  ( "one" , "two" ) ;   TestSubscriber  < String >  subscriber =  TestSubscriber . create  ( ) ;     redisConnection . reactive  ( ) . set  ( key , list ,   SetArgs . Builder . ex  ( 1 ) ) . subscribe  ( subscriber ) ;   subscriber . awaitTerminalEvent  ( 1 ,  TimeUnit . SECONDS ) ;   subscriber . assertCompleted  ( ) ;   subscriber . assertValue  ( "OK" ) ;   redisConnection . close  ( ) ; } }