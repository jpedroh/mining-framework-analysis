  package       de . uniluebeck . itm . spitfire . nCoap . communication . reliability ;   import     com . google . common . collect . HashBasedTable ;  import        de . uniluebeck . itm . spitfire . nCoap . communication . core . CoapClientDatagramChannelFactory ;  import       de . uniluebeck . itm . spitfire . nCoap . message . CoapMessage ;  import       de . uniluebeck . itm . spitfire . nCoap . message . CoapRequest ;  import       de . uniluebeck . itm . spitfire . nCoap . message . CoapResponse ;  import        de . uniluebeck . itm . spitfire . nCoap . message . header . Code ;  import        de . uniluebeck . itm . spitfire . nCoap . message . header . InvalidHeaderException ;  import        de . uniluebeck . itm . spitfire . nCoap . message . header . MsgType ;  import        de . uniluebeck . itm . spitfire . nCoap . message . options . ToManyOptionsException ;  import    org . jboss . netty . channel .  * ;  import      org . jboss . netty . channel . socket . DatagramChannel ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import   java . net . InetSocketAddress ;  import    java . util . concurrent . Executors ;  import    java . util . concurrent . ScheduledExecutorService ;  import    java . util . concurrent . TimeUnit ;   public class IncomingMessageReliabilityHandler  extends SimpleChannelHandler  {   private static Logger  log =  LoggerFactory . getLogger  (   IncomingMessageReliabilityHandler . class . getName  ( ) ) ;   private final  HashBasedTable  < InetSocketAddress , Integer , Boolean >  incomingMessagesToBeConfirmed =  HashBasedTable . create  ( ) ;   private Object  monitor =  new Object  ( ) ;   private ScheduledExecutorService  executorService =  Executors . newScheduledThreadPool  ( 10 ) ;   private static IncomingMessageReliabilityHandler  instance =  new IncomingMessageReliabilityHandler  ( ) ;   public static IncomingMessageReliabilityHandler getInstance  ( )  {  return instance ; }   private IncomingMessageReliabilityHandler  ( )  { }    @ Override public void messageReceived  (  ChannelHandlerContext ctx ,  MessageEvent me )  throws Exception  {  if  (  !  (   me . getMessage  ( ) instanceof CoapMessage ) )  {   ctx . sendUpstream  ( me ) ;  return ; }  CoapMessage  coapMessage =  ( CoapMessage )  me . getMessage  ( ) ; 
<<<<<<<
 if  (    coapMessage . getMessageType  ( ) !=  MsgType . CON ||  !  coapMessage . isRequest  ( ) )  {   ctx . sendUpstream  ( me ) ;  return ; }
=======
>>>>>>>
  DatagramChannel  datagramChannel =  ( DatagramChannel )  ctx . getChannel  ( ) ;  if  (   coapMessage . getMessageType  ( ) ==  MsgType . CON )  {  DatagramChannel 
<<<<<<<
 inserted = false
=======
 datagramChannel =  ( DatagramChannel )  ctx . getChannel  ( )
>>>>>>>
 ; 
<<<<<<<
 synchronized  ( monitor )  {  if  (  !  incomingMessagesToBeConfirmed . contains  (  me . getRemoteAddress  ( ) ,  coapMessage . getMessageID  ( ) ) )  {   incomingMessagesToBeConfirmed . put  (  ( InetSocketAddress )  me . getRemoteAddress  ( ) ,  coapMessage . getMessageID  ( ) , false ) ;   inserted = true ;   monitor . notifyAll  ( ) ; } }
=======
 EmptyACKSender  emptyACKSender =  new EmptyACKSender  (  ( InetSocketAddress )  me . getRemoteAddress  ( ) ,  coapMessage . getMessageID  ( ) , datagramChannel ,  coapMessage . isRequest  ( ) ) ;
>>>>>>>
   log . debug  (       "New confirmable request with message ID " +  coapMessage . getMessageID  ( ) + " from " +  me . getRemoteAddress  ( ) + " received (duplicate = " +  ! inserted + ")" ) ;  if  ( 
<<<<<<<
inserted
=======
>>>>>>>
 )  {  boolean 
<<<<<<<
 emptyACKSender =  new EmptyACKSender  (  ( InetSocketAddress )  me . getRemoteAddress  ( ) ,  coapMessage . getMessageID  ( ) , datagramChannel )
=======
 inserted = false
>>>>>>>
 ;  synchronized  ( monitor )  {  if  (  !  incomingMessagesToBeConfirmed . contains  (  me . getRemoteAddress  ( ) ,  coapMessage . getMessageID  ( ) ) )  {   incomingMessagesToBeConfirmed . put  (  ( InetSocketAddress )  me . getRemoteAddress  ( ) ,  coapMessage . getMessageID  ( ) , false ) ;   inserted = true ;   monitor . notifyAll  ( ) ; } }   .  ( 
<<<<<<<
emptyACKSender
=======
>>>>>>>
 ) ;  if  ( inserted )  {   executorService . schedule  ( emptyACKSender , 2000 ,  TimeUnit . MILLISECONDS ) ; } } else  { 
<<<<<<<
 EmptyACKSender  emptyACKSender =  new EmptyACKSender  (  ( InetSocketAddress )  me . getRemoteAddress  ( ) ,  coapMessage . getMessageID  ( ) , datagramChannel ) ;
=======
  log . debug  (     "New confirmable response with message ID " +  coapMessage . getMessageID  ( ) + " from " +  me . getRemoteAddress  ( ) + " received. Send empty ACK immediately." ) ;
>>>>>>>
   executorService . schedule  ( emptyACKSender , 0 ,  TimeUnit . MILLISECONDS ) ; } }   ctx . sendUpstream  ( me ) ; }    @ Override public void writeRequested  (  ChannelHandlerContext ctx ,  MessageEvent me )  throws Exception  {   log . debug  ( "[IncomingMessageReliablityHandler] Handle Downstream Message Event." ) ;  if  (   me . getMessage  ( ) instanceof CoapResponse )  {  CoapResponse  coapResponse =  ( CoapResponse )  me . getMessage  ( ) ;   log . debug  (    "Handle downstream event for message with ID " +  coapResponse . getMessageID  ( ) + " for " +  me . getRemoteAddress  ( ) ) ;  Boolean  alreadyConfirmed ;  synchronized  ( monitor )  {   alreadyConfirmed =  incomingMessagesToBeConfirmed . remove  (  me . getRemoteAddress  ( ) ,  coapResponse . getMessageID  ( ) ) ; }  if  (  alreadyConfirmed == null )  {    coapResponse . getHeader  ( ) . setMsgType  (  MsgType . NON ) ; } else  {  if  ( alreadyConfirmed )  {    coapResponse . getHeader  ( ) . setMsgType  (  MsgType . CON ) ; } else  {    coapResponse . getHeader  ( ) . setMsgType  (  MsgType . ACK ) ; } } }   ctx . sendDownstream  ( me ) ; }   private class EmptyACKSender  implements  Runnable  {   private InetSocketAddress  rcptAddress ;   private  int  messageID ;   private DatagramChannel  datagramChannel ;   public EmptyACKSender  (  InetSocketAddress rcptAddress ,   int messageID ,  DatagramChannel datagramChannel )  {    this . rcptAddress = rcptAddress ;    this . messageID = messageID ;    this . datagramChannel = datagramChannel ; }    @ Override public void run  ( )  {   log . debug  ( "Start!" ) ;  boolean  confirmed = false ;  if  ( receivedMessageIsRequest )  {  synchronized  ( monitor )  {  if  (  incomingMessagesToBeConfirmed . contains  ( rcptAddress , messageID ) )  {   confirmed = true ;   incomingMessagesToBeConfirmed . put  ( rcptAddress , messageID , true ) ; } } }  if  (  confirmed ||  ! receivedMessageIsRequest )  {  CoapMessage  coapMessage = null ;  try  {   coapMessage =  new CoapResponse  (  MsgType . ACK ,  Code . EMPTY , messageID ) ; }  catch (   ToManyOptionsException e )  {   log . error  (  "Exception while creating empty ACK. This should " + " never happen!" , e ) ; }  catch (   InvalidHeaderException e )  {   log . error  (  "Exception while creating empty ACK. This should " + " never happen!" , e ) ; }  try  {   coapMessage . setMessageID  ( messageID ) ; }  catch (   InvalidHeaderException e )  {   log . error  (  
<<<<<<<
"This should never happen! Exception while setting message ID for "
=======
"Exception while setting message ID for "
>>>>>>>
 + "empty ACK. This should never happen!" , e ) ; }  ChannelFuture  future =  Channels . future  ( datagramChannel ) ;   Channels . write  (   datagramChannel . getPipeline  ( ) . getContext  ( "OutgoingMessageReliabilityHandler" ) , future , coapMessage , rcptAddress ) ;   future . addListener  (  new ChannelFutureListener  ( )  {    @ Override public void operationComplete  (  ChannelFuture future )  throws Exception  {   log . debug  (    "Sent empty ACK for message with ID " + messageID + " to recipient " + rcptAddress ) ; } } ) ; } }   private boolean  receivedMessageIsRequest ;   public EmptyACKSender  (  InetSocketAddress rcptAddress ,   int messageID ,  DatagramChannel datagramChannel ,  boolean receivedMessageIsRequest )  {    this . rcptAddress = rcptAddress ;    this . messageID = messageID ;    this . datagramChannel = datagramChannel ;    this . receivedMessageIsRequest = receivedMessageIsRequest ; } } }