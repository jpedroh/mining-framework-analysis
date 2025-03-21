  package       de . uniluebeck . itm . spitfire . nCoap . communication . core ;   import       de . uniluebeck . itm . spitfire . nCoap . application . CoapServerApplication ;  import       de . uniluebeck . itm . spitfire . nCoap . configuration . Configuration ;  import    org . apache . log4j . Logger ;  import     org . jboss . netty . bootstrap . ConnectionlessBootstrap ;  import     org . jboss . netty . channel . ChannelFactory ;  import      org . jboss . netty . channel . socket . DatagramChannel ;  import       org . jboss . netty . channel . socket . nio . NioDatagramChannelFactory ;  import  java . net .  * ;  import    java . util . concurrent . Executors ;   public class CoapServerDatagramChannelFactory  {   private static Logger  log =  Logger . getLogger  (   CoapServerDatagramChannelFactory . class . getName  ( ) ) ;   public static  int  COAP_SERVER_PORT =   Configuration . getInstance  ( ) . getInt  ( "server.port" , 5683 ) ;   private DatagramChannel  channel ;   public CoapServerDatagramChannelFactory  (  CoapServerApplication 
<<<<<<<
coapServerApplication
=======
serverApp
>>>>>>>
 )  {  ChannelFactory  channelFactory =  new NioDatagramChannelFactory  (  Executors . newCachedThreadPool  ( ) ) ;  ConnectionlessBootstrap  bootstrap =  new ConnectionlessBootstrap  ( channelFactory ) ;   bootstrap . setPipelineFactory  (  new CoapServerPipelineFactory  ( 
<<<<<<<
coapServerApplication
=======
serverApp
>>>>>>>
 ) ) ;  InetAddress  localAddress = null ;  try  {   localAddress =    NetworkInterface . getByName  ( "eth4" ) . getInetAddresses  ( ) . nextElement  ( ) ; }  catch (   SocketException e )  {   log . fatal  (    "[" +   this . getClass  ( ) . getName  ( ) + "] " +   e . getClass  ( ) . getName  ( ) , e ) ; }   channel =  ( DatagramChannel )  bootstrap . bind  (  new InetSocketAddress  ( localAddress , COAP_SERVER_PORT ) ) ; }   public DatagramChannel getChannel  ( )  {  return channel ; } }