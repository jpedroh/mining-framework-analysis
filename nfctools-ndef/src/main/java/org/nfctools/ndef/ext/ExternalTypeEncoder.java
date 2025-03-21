  package    org . nfctools . ndef . ext ;   import    org . nfctools . ndef . NdefConstants ;  import    org . nfctools . ndef . NdefEncoderException ;  import    org . nfctools . ndef . NdefMessageEncoder ;  import    org . nfctools . ndef . NdefRecord ;  import    org . nfctools . ndef . Record ;  import      org . nfctools . ndef . wkt . encoder . RecordEncoder ;  import   java . util . HashMap ;  import   java . util . Map ;  import    org . nfctools . ndef . NdefException ;  import     org . nfctools . ndef . wkt . WellKnownRecordConfig ;   public class ExternalTypeEncoder  implements  RecordEncoder  {    @ Override public boolean canEncode  (  Record record )  {  return  record instanceof ExternalTypeRecord ; }    @ Override public NdefRecord encodeRecord  (  Record record ,  NdefMessageEncoder messageEncoder )  {  ExternalTypeRecord  externalType =  ( ExternalTypeRecord ) record ; 
<<<<<<<
 if  (  !  externalType . hasNamespace  ( ) )  {  throw  new NdefEncoderException  ( "Expected namespace" , record ) ; }
=======
 ExternalTypeRecordConfig  config =  externalRecordTypes . get  (  record . getClass  ( ) ) ;
>>>>>>>
    byte  [ ]  payload ;  if  ( 
<<<<<<<
 !  externalType . hasContent  ( )
=======
 config != null
>>>>>>>
 )  { 
<<<<<<<
 throw  new NdefEncoderException  ( "Expected content" , record ) ;
=======
  payload =    config . getContentEncoder  ( ) . encodeContent  ( externalType ) . getBytes  (  NdefConstants . DEFAULT_CHARSET ) ;
>>>>>>>
 } else  if  (  externalType instanceof UnsupportedExternalTypeRecord )  {  UnsupportedExternalTypeRecord  externalTypeUnsupportedRecord =  ( UnsupportedExternalTypeRecord ) externalType ;   payload =   externalTypeUnsupportedRecord . getContent  ( ) . getBytes  (  NdefConstants . DEFAULT_CHARSET ) ; } else  {  throw  new IllegalArgumentException  (  "Unable to encode external type " +   externalType . getClass  ( ) . getName  ( ) ) ; }    byte  [ ]  type =   externalType . getNamespace  ( ) . getBytes  (  NdefConstants . DEFAULT_CHARSET ) ;  return  new NdefRecord  (  NdefConstants . TNF_EXTERNAL_TYPE , type ,  record . getId  ( ) , payload ) ; }   private  Map  <  Class  <  ? > , ExternalTypeRecordConfig >  externalRecordTypes =  new  HashMap  <  Class  <  ? > , ExternalTypeRecordConfig >  ( ) ;   public void addRecordConfig  (  ExternalTypeRecordConfig config )  {   externalRecordTypes . put  (  config . getRecordClass  ( ) , config ) ; } }