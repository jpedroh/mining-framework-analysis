  package      de . uniluebeck . itm . spitfire . nCoap . application ;   import       de . uniluebeck . itm . spitfire . nCoap . message . CoapRequest ;  import       de . uniluebeck . itm . spitfire . nCoap . message . CoapResponse ;  import       de . uniluebeck . itm . spitfire . nCoap . message . MessageDoesNotAllowPayloadException ;  import        de . uniluebeck . itm . spitfire . nCoap . message . header . Code ;  import    org . apache . log4j . Logger ;  import    java . nio . charset . Charset ;  import        de . uniluebeck . itm . spitfire . nCoap . message . options . InvalidOptionException ;  import        de . uniluebeck . itm . spitfire . nCoap . message . options . OptionRegistry ;  import        de . uniluebeck . itm . spitfire . nCoap . message . options . ToManyOptionsException ;   public class SimpleCoapServerApplication  extends CoapServerApplication  {   private static Logger  log =  Logger . getLogger  (   SimpleCoapServerApplication . class . getName  ( ) ) ;    @ Override public CoapResponse receiveCoapRequest  (  CoapRequest coapRequest )  { 
<<<<<<<
  log . debug  (  "[SimpleCoapServerApplication] Received request for " +  coapRequest . getTargetUri  ( ) ) ;
=======
 CoapResponse  coapResponse =  new CoapResponse  (  Code . CONTENT_205 ) ;
>>>>>>>
  try  {   
<<<<<<<
Thread
=======
coapResponse
>>>>>>>
 . 
<<<<<<<
sleep
=======
setContentType
>>>>>>>
  (   OptionRegistry . MediaType . APP_LINK_FORMAT ) ; }  catch (   InvalidOptionException e )  {   log . fatal  (    "[" +   this . getClass  ( ) . getName  ( ) + "] " +   e . getClass  ( ) . getName  ( ) , e ) ; }  catch (   ToManyOptionsException e )  {   log . fatal  (    "[" +   this . getClass  ( ) . getName  ( ) + "] " +   e . getClass  ( ) . getName  ( ) , e ) ; }  try  {   coapResponse . setPayload  (  (   new String  ( "</simple>" ) . getBytes  (  Charset . forName  ( "UTF-8" ) ) ) ) ; }  catch (   MessageDoesNotAllowPayloadException e )  {   log . fatal  ( "[SimpleCoapServerApplication] Error while setting payload for response." ) ; }  return coapResponse ; }   public static void main  (   String  [ ] args )  {  SimpleCoapServerApplication  serverApplication =  new SimpleCoapServerApplication  ( ) ; } }