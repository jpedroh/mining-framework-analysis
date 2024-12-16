  package    com . mercadopago . client . customer ;   import static    com . mercadopago . MercadoPagoConfig . getStreamHandler ;  import    com . google . gson . JsonObject ;  import   com . mercadopago . MercadoPagoConfig ;  import    com . mercadopago . client . MercadoPagoClient ;  import    com . mercadopago . core . MPRequestOptions ;  import    com . mercadopago . exceptions . MPException ;  import    com . mercadopago . net . HttpMethod ;  import    com . mercadopago . net . MPHttpClient ;  import    com . mercadopago . net . MPRequest ;  import    com . mercadopago . net . MPResourceList ;  import    com . mercadopago . net . MPResponse ;  import     com . mercadopago . resources . customer . CustomerCard ;  import    com . mercadopago . serialization . Serializer ;  import    java . util . logging . Logger ;  import    java . util . logging . StreamHandler ;  import    com . mercadopago . exceptions . MPApiException ;   public class CustomerCardClient  extends MercadoPagoClient  {   private static final Logger  LOGGER =  Logger . getLogger  (   CustomerCardClient . class . getName  ( ) ) ;   public CustomerCardClient  ( )  {  this  (  MercadoPagoConfig . getHttpClient  ( ) ) ; }   public CustomerCardClient  (  MPHttpClient httpClient )  {  super  ( httpClient ) ;  StreamHandler  streamHandler =  getStreamHandler  ( ) ;   streamHandler . setLevel  (  MercadoPagoConfig . getLoggingLevel  ( ) ) ;   LOGGER . addHandler  ( streamHandler ) ;   LOGGER . setLevel  (  MercadoPagoConfig . getLoggingLevel  ( ) ) ; }   public CustomerCard get  (  String customerId ,  String cardId )  throws MPException , MPApiException  {  return  this . get  ( customerId , cardId , null ) ; }   public CustomerCard get  (  String customerId ,  String cardId ,  MPRequestOptions requestOptions )  throws MPException , MPApiException  {   LOGGER . info  ( "Sending get customer card request" ) ;  MPResponse  response =  send  (  String . format  ( "/v1/customers/%s/cards/%s" , customerId , cardId ) ,  HttpMethod . GET , null , null , requestOptions ) ;  CustomerCard  card =  Serializer . deserializeFromJson  (  CustomerCard . class ,  response . getContent  ( ) ) ;   card . setResponse  ( response ) ;  return card ; }   public CustomerCard create  (  String customerId ,  CustomerCardCreateRequest request )  throws MPException , MPApiException  {  return  this . create  ( customerId , request , null ) ; }   public CustomerCard create  (  String customerId ,  CustomerCardCreateRequest request ,  MPRequestOptions requestOptions )  throws MPException , MPApiException  {   LOGGER . info  ( "Sending create customer card request" ) ;  JsonObject  payload =  Serializer . serializeToJson  ( request ) ;  MPRequest  mpRequest =  MPRequest . buildRequest  (  String . format  ( "/v1/customers/%s/cards" , customerId ) ,  HttpMethod . POST , payload , null , requestOptions ) ;  MPResponse  response =  send  ( mpRequest ) ;  CustomerCard  card =  Serializer . deserializeFromJson  (  CustomerCard . class ,  response . getContent  ( ) ) ;   card . setResponse  ( response ) ;  return card ; }   public CustomerCard delete  (  String customerId ,  String cardId )  throws MPException , MPApiException  {  return  this . delete  ( customerId , cardId , null ) ; }   public CustomerCard delete  (  String customerId ,  String cardId ,  MPRequestOptions requestOptions )  throws MPException , MPApiException  {   LOGGER . info  ( "Sending delete customer card request" ) ;  MPResponse  response =  send  (  String . format  ( "/v1/customers/%s/cards/%s" , customerId , cardId ) ,  HttpMethod . DELETE , null , null , requestOptions ) ;  CustomerCard  card =  Serializer . deserializeFromJson  (  CustomerCard . class ,  response . getContent  ( ) ) ;   card . setResponse  ( response ) ;  return card ; }   public  MPResourceList  < CustomerCard > listAll  (  String customerId )  throws MPException , MPApiException  {  return  this . listAll  ( customerId , null ) ; }   public  MPResourceList  < CustomerCard > listAll  (  String customerId ,  MPRequestOptions requestOptions )  throws MPException , MPApiException  {   LOGGER . info  ( "Sending list all customer cards request" ) ;  MPResponse  response =  list  (  String . format  ( "/v1/customers/%s/cards" , customerId ) ,  HttpMethod . GET , null , null , requestOptions ) ;   MPResourceList  < CustomerCard >  cards =  Serializer . deserializeListFromJson  (  CustomerCard . class ,  response . getContent  ( ) ) ;   cards . setResponse  ( response ) ;  return cards ; } }