  package  com . lambdaworks ;   import static       com . google . code . tempusfugit . temporal . Duration . seconds ;  import static     com . lambdaworks . redis . TestSettings . host ;  import static     com . lambdaworks . redis . TestSettings . sslPort ;  import static      org . assertj . core . api . Assertions . assertThat ;  import static      org . assertj . core . api . Assertions . fail ;  import static    org . junit . Assume . assumeTrue ;  import   java . io . File ;  import    java . security . cert . CertificateException ;  import   java . util . List ;  import    java . util . concurrent . ExecutionException ;  import   org . junit . Before ;  import   org . junit . Test ;  import  rx . Subscription ;  import   rx . observers . TestSubscriber ;  import      com . google . code . tempusfugit . temporal . Condition ;  import      com . google . code . tempusfugit . temporal . Timeout ;  import      com . google . code . tempusfugit . temporal . WaitFor ;  import     com . lambdaworks . redis . event . Event ;  import     com . lambdaworks . redis . event . EventBus ;  import      com . lambdaworks . redis . event . connection . ConnectedEvent ;  import      com . lambdaworks . redis . event . connection . ConnectionActivatedEvent ;  import      com . lambdaworks . redis . event . connection . ConnectionDeactivatedEvent ;  import      com . lambdaworks . redis . event . connection . DisconnectedEvent ;  import     io . netty . handler . codec . DecoderException ;  import    com . lambdaworks . redis . AbstractTest ;  import    com . lambdaworks . redis . ClientOptions ;  import    com . lambdaworks . redis . FastShutdown ;  import    com . lambdaworks . redis . RedisClient ;  import    com . lambdaworks . redis . RedisConnection ;  import    com . lambdaworks . redis . RedisConnectionException ;  import    com . lambdaworks . redis . RedisFuture ;  import    com . lambdaworks . redis . RedisURI ;  import       com . lambdaworks . redis . pubsub . api . async . RedisPubSubAsyncCommands ;  import       com . lambdaworks . redis . pubsub . api . sync . RedisPubSubCommands ;   public class SslTest  extends 
<<<<<<<
AbstractCommandTest
=======
AbstractTest
>>>>>>>
  {   public static final String  KEYSTORE = "work/keystore.jks" ;    @ Before public void before  ( )  throws Exception  {   assumeTrue  ( "Assume that stunnel runs on port 6443" ,  Sockets . isOpen  (  host  ( ) ,  sslPort  ( ) ) ) ;    assertThat  (  new File  ( KEYSTORE ) ) . exists  ( ) ;   System . setProperty  ( "javax.net.ssl.trustStore" , KEYSTORE ) ; }    @ Test public void regularSsl  ( )  throws Exception  {  RedisURI  redisUri =      RedisURI . Builder . redis  (  host  ( ) ,  sslPort  ( ) ) . withSsl  ( true ) . withVerifyPeer  ( false ) . build  ( ) ;   RedisConnection  < String , String >  connection =  
<<<<<<<
client
=======
 redisClient . connect  ( redisUri )
>>>>>>>
 . sync  ( ) ;   connection . set  ( "key" , "value" ) ;    assertThat  (  connection . get  ( "key" ) ) . isEqualTo  ( "value" ) ;   connection . close  ( ) ; }    @ Test public void pingBeforeActivate  ( )  throws Exception  {  RedisURI  redisUri =      RedisURI . Builder . redis  (  host  ( ) ,  sslPort  ( ) ) . withSsl  ( true ) . withVerifyPeer  ( false ) . build  ( ) ;   client . setOptions  (    new  ClientOptions . Builder  ( ) . pingBeforeActivateConnection  ( true ) . build  ( ) ) ;   RedisConnection  < String , String >  connection =  
<<<<<<<
client
=======
 redisClient . connect  ( redisUri )
>>>>>>>
 . sync  ( ) ;   connection . set  ( "key" , "value" ) ;    assertThat  (  connection . get  ( "key" ) ) . isEqualTo  ( "value" ) ;   connection . close  ( ) ; }    @ Test public void regularSslWithReconnect  ( )  throws Exception  {  RedisURI  redisUri =      RedisURI . Builder . redis  (  host  ( ) ,  sslPort  ( ) ) . withSsl  ( true ) . withVerifyPeer  ( false ) . build  ( ) ;   RedisConnection  < String , String >  connection =  
<<<<<<<
client
=======
 redisClient . connect  ( redisUri )
>>>>>>>
 . sync  ( ) ;   connection . set  ( "key" , "value" ) ;   Thread . sleep  ( 200 ) ;    assertThat  (  connection . get  ( "key" ) ) . isEqualTo  ( "value" ) ;   connection . close  ( ) ; }    @ Test  (  expected =  RedisConnectionException . class ) public void sslWithVerificationWillFail  ( )  throws Exception  {  RedisURI  redisUri =  RedisURI . create  (    "rediss://" +  host  ( ) + ":" +  sslPort  ( ) ) ;   RedisConnection  < String , String >  connection =  
<<<<<<<
client
=======
 redisClient . connect  ( redisUri )
>>>>>>>
 . sync  ( ) ; }    @ Test public void pubSubSsl  ( )  throws Exception  {  RedisURI  redisUri =      RedisURI . Builder . redis  (  host  ( ) ,  sslPort  ( ) ) . withSsl  ( true ) . withVerifyPeer  ( false ) . build  ( ) ;   RedisPubSubCommands  < String , String >  connection =  
<<<<<<<
client
=======
 redisClient . connectPubSub  ( redisUri )
>>>>>>>
 . sync  ( ) ;   connection . subscribe  ( "c1" ) ;   connection . subscribe  ( "c2" ) ;   Thread . sleep  ( 200 ) ;   RedisPubSubCommands  < String , String >  connection2 =  
<<<<<<<
client
=======
 redisClient . connectPubSub  ( redisUri )
>>>>>>>
 . sync  ( ) ;    assertThat  (  connection2 . pubsubChannels  ( ) ) . contains  ( "c1" , "c2" ) ;   connection . quit  ( ) ;   Thread . sleep  ( 200 ) ;    Wait . untilTrue  (  connection :: isOpen ) . waitOrTimeout  ( ) ;    assertThat  (  connection2 . pubsubChannels  ( ) ) . contains  ( "c1" , "c2" ) ;   connection . close  ( ) ;   connection2 . close  ( ) ; }    @ Test public void pubSubSslAndBreakConnection  ( )  throws Exception  {  RedisURI  redisUri =      RedisURI . Builder . redis  (  host  ( ) ,  sslPort  ( ) ) . withSsl  ( true ) . withVerifyPeer  ( false ) . build  ( ) ;   
<<<<<<<
client
=======
redisClient
>>>>>>>
 . setOptions  (    new  ClientOptions . Builder  ( ) . suspendReconnectOnProtocolFailure  ( true ) . build  ( ) ) ;   RedisPubSubAsyncCommands  < String , String >  connection =  
<<<<<<<
client
=======
 redisClient . connectPubSub  ( redisUri )
>>>>>>>
 . async  ( ) ;    connection . subscribe  ( "c1" ) . get  ( ) ;    connection . subscribe  ( "c2" ) . get  ( ) ;   Thread . sleep  ( 200 ) ;   RedisPubSubAsyncCommands  < String , String >  connection2 =  
<<<<<<<
client
=======
 redisClient . connectPubSub  ( redisUri )
>>>>>>>
 . async  ( ) ;    assertThat  (   connection2 . pubsubChannels  ( ) . get  ( ) ) . contains  ( "c1" , "c2" ) ;   redisUri . setVerifyPeer  ( true ) ;   connection . quit  ( ) ;   Thread . sleep  ( 500 ) ;   RedisFuture  <  List  < String > >  future =  connection2 . pubsubChannels  ( ) ;    assertThat  (  future . get  ( ) ) . doesNotContain  ( "c1" , "c2" ) ;    assertThat  (  future . isDone  ( ) ) . isEqualTo  ( true ) ;   RedisFuture  <  List  < String > >  defectFuture =  connection . pubsubChannels  ( ) ;  try  {    assertThat  (  defectFuture . get  ( ) ) . doesNotContain  ( "c1" , "c2" ) ;   fail  ( "Missing ExecutionException with nested SSLHandshakeException" ) ; }  catch (   InterruptedException e )  {   fail  ( "Missing ExecutionException with nested SSLHandshakeException" ) ; }  catch (   ExecutionException e )  {    assertThat  ( e ) . hasCauseInstanceOf  (  DecoderException . class ) ;    assertThat  ( e ) . hasRootCauseInstanceOf  (  CertificateException . class ) ; }    assertThat  (  defectFuture . isDone  ( ) ) . isEqualTo  ( true ) ;   connection . close  ( ) ;   connection2 . close  ( ) ; }    @ Test public void clientEvents  ( )  throws Exception  {  RedisURI  redisUri =      RedisURI . Builder . redis  (  host  ( ) ,  sslPort  ( ) ) . withSsl  ( true ) . withVerifyPeer  ( false ) . build  ( ) ;  RedisClient  myClient =  RedisClient . create  ( resources , redisUri ) ;  EventBus  eventBus =   client . getResources  ( ) . eventBus  ( ) ;   final  TestSubscriber  < Event >  eventTestSubscriber =  new  TestSubscriber  < Event >  ( ) ;  Subscription  subscribe =   eventBus . get  ( ) . subscribe  ( eventTestSubscriber ) ;   RedisAsyncConnection  < String , String >  async =  client . connectAsync  ( ) ;    async . set  ( key , value ) . get  ( ) ;   async . close  ( ) ;   WaitFor . waitOrTimeout  (  new Condition  ( )  {    @ Override public boolean isSatisfied  ( )  {  return    eventTestSubscriber . getOnNextEvents  ( ) . size  ( ) >= 4 ; } } ,  Timeout . timeout  (  seconds  ( 5 ) ) ) ;   subscribe . unsubscribe  ( ) ;   List  < Event >  events =  eventTestSubscriber . getOnNextEvents  ( ) ;    assertThat  ( events ) . hasSize  ( 4 ) ;    assertThat  (  events . get  ( 0 ) ) . isInstanceOf  (  ConnectedEvent . class ) ;    assertThat  (  events . get  ( 1 ) ) . isInstanceOf  (  ConnectionActivatedEvent . class ) ;    assertThat  (  events . get  ( 2 ) ) . isInstanceOf  (  DisconnectedEvent . class ) ;    assertThat  (  events . get  ( 3 ) ) . isInstanceOf  (  ConnectionDeactivatedEvent . class ) ;     assertThat  (   events . get  ( 3 ) . toString  ( ) ) . contains  ( "ConnectionDeactivatedEvent" ) . contains  ( " -> " ) ;   myClient . shutdown  ( ) ; } 
<<<<<<<
=======
  public static RedisClient  redisClient =  RedisClient . create  ( ) ;
>>>>>>>
 
<<<<<<<
=======
   @ AfterClass public static void afterClass  ( )  {   FastShutdown . shutdown  ( redisClient ) ; }
>>>>>>>
 }