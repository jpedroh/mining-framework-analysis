  package       de . uniluebeck . itm . spitfire . nCoap . communication . callback ;   import     com . google . common . collect . HashBasedTable ;  import       de . uniluebeck . itm . spitfire . nCoap . toolbox . Tools ;  import       de . uniluebeck . itm . spitfire . nCoap . message . CoapRequest ;  import       de . uniluebeck . itm . spitfire . nCoap . message . CoapResponse ;  import        de . uniluebeck . itm . spitfire . nCoap . message . options . InvalidOptionException ;  import        de . uniluebeck . itm . spitfire . nCoap . message . options . ToManyOptionsException ;  import     org . jboss . netty . channel . ExceptionEvent ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import   java . net . InetSocketAddress ;  import   java . util . Arrays ;  import        de . uniluebeck . itm . spitfire . nCoap . communication . internal . InternalAcknowledgementMessage ;  import        de . uniluebeck . itm . spitfire . nCoap . communication . internal . InternalErrorMessage ;  import        de . uniluebeck . itm . spitfire . nCoap . message . header . Code ;  import        de . uniluebeck . itm . spitfire . nCoap . message . header . MsgType ;  import       de . uniluebeck . itm . spitfire . nCoap . toolbox . ByteArrayWrapper ;  import    org . jboss . netty . channel .  * ;   public class ResponseCallbackHandler  extends SimpleChannelHandler  {   private static Logger  log =  LoggerFactory . getLogger  (   ResponseCallbackHandler . class . getName  ( ) ) ;   private static ResponseCallbackHandler  instance =  new ResponseCallbackHandler  ( ) ;   HashBasedTable  < ByteArrayWrapper , InetSocketAddress , ResponseCallback >  callbacks =  HashBasedTable . create  ( ) ;   private ResponseCallbackHandler  ( )  { }   public static ResponseCallbackHandler getInstance  ( )  {  return instance ; }    @ Override public void writeRequested  (  ChannelHandlerContext ctx ,  MessageEvent me )  {  if  (   me . getMessage  ( ) instanceof CoapRequest )  {   log . debug  ( 
<<<<<<<
" Handling downstream event!"
=======
"CoapRequest received on downstream"
>>>>>>>
 ) ;  CoapRequest  coapRequest =  ( CoapRequest )  me . getMessage  ( ) ;  if  (   coapRequest . getResponseCallback  ( ) != null )  {  try  {   coapRequest . setToken  (   TokenFactory . getInstance  ( ) . getNextToken  ( ) ) ; }  catch (   InvalidOptionException e )  {  String  errorMessage =  "Internal CoAP error while setting token: " +  e . getCause  ( ) ;   log . error  ( 
<<<<<<<
" Error while setting token.\n"
=======
errorMessage
>>>>>>>
 ) ;  UpstreamMessageEvent  ume =  new UpstreamMessageEvent  (  ctx . getChannel  ( ) ,  new InternalErrorMessage  ( errorMessage ,  coapRequest . getToken  ( ) ) ,  me . getRemoteAddress  ( ) ) ;   ctx . sendUpstream  ( ume ) ;  return ; }  catch (   ToManyOptionsException e )  {  String  errorMessage =  "Internal CoAP error while setting token: " +  e . getCause  ( ) ;   log . error  ( 
<<<<<<<
" Error while setting token.\n"
=======
errorMessage
>>>>>>>
 ) ;  UpstreamMessageEvent  ume =  new UpstreamMessageEvent  (  ctx . getChannel  ( ) ,  new InternalErrorMessage  ( errorMessage ,  coapRequest . getToken  ( ) ) ,  me . getRemoteAddress  ( ) ) ;   ctx . sendUpstream  ( ume ) ;  return ; } 
<<<<<<<
  log . debug  (      " New Confirmable Request added: \n" + "\tRemote Address: " +  me . getRemoteAddress  ( ) + "\n" + "\tToken: " +  Tools . toHexString  (  coapRequest . getToken  ( ) ) ) ;
=======
>>>>>>>
   callbacks . put  (  new ByteArrayWrapper  (  coapRequest . getToken  ( ) ) ,  ( InetSocketAddress )  me . getRemoteAddress  ( ) ,  coapRequest . getResponseCallback  ( ) ) ;   log . info  (    "New confirmable Request added (Remote Address: " +  me . getRemoteAddress  ( ) + ", Token: " +  Tools . toHexString  (  coapRequest . getToken  ( ) ) ) ;   log . debug  (  
<<<<<<<
" Number of registered callbacks: "
=======
"Number of registered callbacks: "
>>>>>>>
 +  callbacks . size  ( ) ) ; } }   ctx . sendDownstream  ( me ) ; }    @ Override public void messageReceived  (  ChannelHandlerContext ctx ,  MessageEvent me )  {   log . debug  ( " Handle Upstream Message Event." ) ;   log . debug  (      " Received message is a response: \n" + "\tRemote Address: " +  me . getRemoteAddress  ( ) + "\n" + "\tToken: " +  Tools . toHexString  (  coapResponse . getToken  ( ) ) ) ;  if  (   me . getMessage  ( ) instanceof CoapResponse )  {  CoapResponse  coapResponse =  ( CoapResponse )  me . getMessage  ( ) ;   log . debug  (  
<<<<<<<
" Response callback found. "
=======
      " Received message (" +  coapResponse . getMessageType  ( ) + ", " +  coapResponse . getCode  ( ) + ") is a response (Remote Address: " +  me . getRemoteAddress  ( ) + ", Token: "
>>>>>>>
 + 
<<<<<<<
"Invoking method receiveCoapResponse"
=======
 Tools . toHexString  (  coapResponse . getToken  ( ) )
>>>>>>>
 ) ;  ResponseCallback  callback =  callbacks . remove  (  new ByteArrayWrapper  (  coapResponse . getToken  ( ) ) ,  me . getRemoteAddress  ( ) ) ;  if  (  callback != null )  {   log . debug  (  " Received response for request with token " +  Tools . toHexString  (  coapResponse . getToken  ( ) ) ) ;   callback . receiveResponse  ( coapResponse ) ; } } else  if  (   me . getMessage  ( ) instanceof InternalAcknowledgementMessage )  {  ByteArrayWrapper  token =   (  ( InternalAcknowledgementMessage )  me . getMessage  ( ) ) . getContent  ( ) ;  ResponseCallback  callback =  callbacks . get  ( token ,  me . getRemoteAddress  ( ) ) ;  if  (  callback != null )  {   log . debug  (  "Received empty acknowledgement for request with token " +  token . toHexString  ( ) ) ;   callback . receiveEmptyACK  ( ) ; } } else  if  (   me . getMessage  ( ) instanceof InternalErrorMessage )  {  InternalErrorMessage  errorMessage =  ( InternalErrorMessage )  me . getMessage  ( ) ;  ByteArrayWrapper  token =  new ByteArrayWrapper  (  errorMessage . getToken  ( ) ) ;  ResponseCallback  callback =  callbacks . get  ( token ,  me . getRemoteAddress  ( ) ) ;  if  (  callback != null )  {  String  error =    "Received internal error message for request with token " +  token . toHexString  ( ) + ":\n" +  errorMessage . getContent  ( ) ;   log . debug  ( error ) ;   callback . receiveInternalError  ( error ) ; } } else  {   ctx . sendUpstream  ( me ) ; } }    @ Override public void exceptionCaught  (  ChannelHandlerContext ctx ,  ExceptionEvent e )  throws Exception  {   log . debug  ( 
<<<<<<<
" Exception caught:\n"
=======
" Exception caught:"
>>>>>>>
 , 
<<<<<<<
e
=======
 e . getCause  ( )
>>>>>>>
 ) ; } }