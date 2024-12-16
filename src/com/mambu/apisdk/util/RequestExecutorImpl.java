  package    com . mambu . apisdk . util ;   import   java . io . BufferedReader ;  import   java . io . ByteArrayOutputStream ;  import   java . io . IOException ;  import   java . io . InputStream ;  import   java . io . InputStreamReader ;  import   java . io . UnsupportedEncodingException ;  import   java . net . HttpURLConnection ;  import   java . net . MalformedURLException ;  import    java . nio . charset . StandardCharsets ;  import   java . util . ArrayList ;  import   java . util . List ;  import   java . util . Map ;  import    java . util . logging . Level ;  import    java . util . logging . Logger ;  import      org . apache . commons . codec . binary . Base64 ;  import     org . apache . commons . io . IOUtils ;  import    org . apache . http . HttpEntity ;  import    org . apache . http . HttpResponse ;  import    org . apache . http . NameValuePair ;  import     org . apache . http . client . HttpClient ;  import      org . apache . http . client . config . CookieSpecs ;  import      org . apache . http . client . config . RequestConfig ;  import      org . apache . http . client . entity . UrlEncodedFormEntity ;  import      org . apache . http . client . methods . HttpDelete ;  import      org . apache . http . client . methods . HttpGet ;  import      org . apache . http . client . methods . HttpPatch ;  import      org . apache . http . client . methods . HttpPost ;  import      org . apache . http . conn . ssl . SSLConnectionSocketFactory ;  import      org . apache . http . conn . ssl . SSLContexts ;  import     org . apache . http . entity . StringEntity ;  import      org . apache . http . impl . client . HttpClients ;  import     org . apache . http . message . BasicNameValuePair ;  import    com . google . inject . Inject ;  import    com . google . inject . Singleton ;  import    com . mambu . apisdk . MambuAPIFactory ;  import     com . mambu . apisdk . exception . MambuApiException ;    @ Singleton public class RequestExecutorImpl  implements  RequestExecutor  {   private static final String  CONTENT_TYPE_HEADER_NAME = "Content-Type" ;   private static final String  AUTHORIZATION_HEADER_NAME = "Authorization" ;   private static final String  USER_AGENT_HEADER_NAME = "User-Agent" ;   private URLHelper  urlHelper ;   private String  encodedAuthorization ;   private final static String  APPLICATION_KEY =  APIData . APPLICATION_KEY ;   private final static Logger  LOGGER =  Logger . getLogger  (   RequestExecutorImpl . class . getName  ( ) ) ;    @ Inject public RequestExecutorImpl  (  URLHelper urlHelper )  {    this . urlHelper = urlHelper ; }    @ Override public String executeRequest  (  String urlString ,  Method method )  throws MambuApiException  {  return  executeRequest  ( urlString , null , method ,  ContentType . WWW_FORM ) ; }    @ Override public String executeRequest  (  String urlString ,  ParamsMap params ,  Method method )  throws MambuApiException  {  return  executeRequest  ( urlString , params , method ,  ContentType . WWW_FORM ) ; }    @ Override public String executeRequest  (  String urlString ,  Method method ,  ContentType contentTypeFormat )  throws MambuApiException  {  return  executeRequest  ( urlString , null , method , contentTypeFormat ) ; }    @ Override public String executeRequest  (  String urlString ,  ParamsMap params ,  Method method ,  ContentType contentTypeFormat )  throws MambuApiException  {   urlString =  urlHelper . addJsonPaginationParams  ( urlString , method , contentTypeFormat , params ) ;   logApiRequestDetails  ( urlString , params , method , contentTypeFormat ) ;   logCurlRequestDetails  ( urlString , params , method , contentTypeFormat ,  urlHelper . userAgentHeaderValue  ( ) ) ;   params =  addAppKeyToParams  ( params ) ;  HttpClient  httpClient =  createCustomHttpClient  ( ) ;  String  response = "" ;  HttpResponse  httpResponse = null ;  try  {   httpResponse =  executeRequestByMethod  ( urlString , params , method , contentTypeFormat , httpClient , httpResponse ) ;   response =  processResponse  ( httpResponse , method , contentTypeFormat , urlString , params ) ; }  catch (   MalformedURLException e )  {   LOGGER . severe  (  "MalformedURLException: " +  e . getMessage  ( ) ) ;  throw  new MambuApiException  ( e ) ; }  catch (   IOException e )  {   LOGGER . warning  (  "IOException: message= " +  e . getMessage  ( ) ) ;  throw  new MambuApiException  ( e ) ; }  finally  {    httpClient . getConnectionManager  ( ) . shutdown  ( ) ; }  return response ; }    @ Override public ByteArrayOutputStream executeRequest  (  String urlString ,  ParamsMap params ,  ApiDefinition apiDefinition )  throws MambuApiException  {  Method  method =  apiDefinition . getMethod  ( ) ;  ContentType  contentTypeFormat =  apiDefinition . getContentType  ( ) ;   logApiRequestDetails  ( urlString , params , method , contentTypeFormat ) ;   logCurlRequestDetails  ( urlString , params , method , contentTypeFormat ,  urlHelper . userAgentHeaderValue  ( ) ) ;   params =  addAppKeyToParams  ( params ) ;  HttpClient  httpClient =  createCustomHttpClient  ( ) ;  ByteArrayOutputStream  byteArrayOutputStreamResponse = null ;  HttpResponse  httpResponse = null ;  try  {   httpResponse =  executeRequestByMethod  ( urlString , params , method , contentTypeFormat , httpClient , httpResponse ) ;   byteArrayOutputStreamResponse =  processInputStreamResponse  ( httpResponse , method , contentTypeFormat , urlString , params ) ; }  catch (   MalformedURLException e )  {   LOGGER . severe  (  "MalformedURLException: " +  e . getMessage  ( ) ) ;  throw  new MambuApiException  ( e ) ; }  catch (   IOException e )  {   LOGGER . warning  (  "IOException: message= " +  e . getMessage  ( ) ) ;  throw  new MambuApiException  ( e ) ; }  finally  {    httpClient . getConnectionManager  ( ) . shutdown  ( ) ; }  return byteArrayOutputStreamResponse ; }   private HttpClient createCustomHttpClient  ( )  {  HttpClient  httpClient =     HttpClients . custom  ( ) . setDefaultRequestConfig  (    RequestConfig . custom  ( ) . setCookieSpec  (  CookieSpecs . IGNORE_COOKIES ) . build  ( ) ) . setSSLSocketFactory  (  createSslConnectionSocketFactory  ( ) ) . build  ( ) ;  return httpClient ; }   private SSLConnectionSocketFactory createSslConnectionSocketFactory  ( )  {  SSLConnectionSocketFactory  sslConnFactory =  new SSLConnectionSocketFactory  (  SSLContexts . createDefault  ( ) ,  new String  [ ]  { TLS_V1_2 } , null ,  SSLConnectionSocketFactory . ALLOW_ALL_HOSTNAME_VERIFIER ) ;  return sslConnFactory ; }   private ByteArrayOutputStream processInputStreamResponse  (  HttpResponse httpResponse ,  Method method ,  ContentType contentType ,  String urlString ,  ParamsMap params )  throws MambuApiException , UnsupportedOperationException , IOException  {   int  status =   httpResponse . getStatusLine  ( ) . getStatusCode  ( ) ;  ByteArrayOutputStream  response = null ;  String  responseMessage = "" ;  HttpEntity  entity =  httpResponse . getEntity  ( ) ;  if  (   entity != null &&  status ==  HttpURLConnection . HTTP_OK )  {   response =  getByteArrayOutputStream  (  entity . getContent  ( ) ) ;   responseMessage = "DB backup stream successfully obtained" ; } else  {  String  errorMessage = null ;   errorMessage =  processResponse  ( httpResponse , method , contentType , urlString , params ) ;   responseMessage = errorMessage ; }  if  (  LOGGER . isLoggable  ( RESPONSE_LOG_LEVEL ) )  {   logApiResponse  ( RESPONSE_LOG_LEVEL , urlString , status , responseMessage ) ; }  if  (   status ==  HttpURLConnection . HTTP_OK ||  status ==  HttpURLConnection . HTTP_CREATED )  {  return response ; }  Integer  errorCode = status ;   logExceptionForProcessingResponse  ( method , contentType , urlString , params , "" , errorCode ) ;  throw  new MambuApiException  ( errorCode , "Couldn`t obtain stream content" ) ; }   private ByteArrayOutputStream getByteArrayOutputStream  (  InputStream inputStream )  throws IOException  {    byte  [ ]  byteArray =  IOUtils . toByteArray  ( inputStream ) ;  ByteArrayOutputStream  baos =  new ByteArrayOutputStream  (  byteArray . length ) ;   baos . write  ( byteArray , 0 ,  byteArray . length ) ;  return baos ; }   private static void logExceptionForProcessingResponse  (  Method method ,  ContentType contentType ,  String urlString ,  ParamsMap params ,  String response ,  Integer errorCode )  {  if  (  LOGGER . isLoggable  ( EXCEPTION_LOG_LEVEL ) )  {  String  urlLogString = urlString ;  String  appKeyValue =  MambuAPIFactory . getApplicationKey  ( ) ;  if  (  appKeyValue != null )  {   urlLogString =  urlLogString . replace  ( appKeyValue , "..." ) ; }   LOGGER . log  ( EXCEPTION_LOG_LEVEL ,    "Creating exception, error code=" + errorCode + " for url=" + urlLogString ) ;  if  (  !  LOGGER . isLoggable  ( RESPONSE_LOG_LEVEL ) )  {   LOGGER . log  ( EXCEPTION_LOG_LEVEL ,  "Mambu Response: " + response ) ; }  if  (  !  LOGGER . isLoggable  ( REQUEST_LOG_LEVEL ) )  {   LOGGER . log  ( EXCEPTION_LOG_LEVEL , "Request causing Mambu exception:" ) ;   logApiRequest  ( EXCEPTION_LOG_LEVEL , method , contentType , urlLogString , params ) ; } } }   private ParamsMap addAppKeyToParams  (  ParamsMap paramsMap )  {  String  applicationKey =  MambuAPIFactory . getApplicationKey  ( ) ;  if  (  applicationKey != null )  {  if  (  paramsMap == null )  {   paramsMap =  new ParamsMap  ( ) ; }   paramsMap . addParam  ( APPLICATION_KEY , applicationKey ) ;   logAppKey  ( applicationKey ) ; }  return paramsMap ; }   private void logCurlRequestDetails  (  String urlString ,  ParamsMap params ,  Method method ,  ContentType contentTypeFormat ,  String userAgentHeaderValue )  {  if  (  LOGGER . isLoggable  ( curlRequestTemplateLogLevel ) )  {   logCurlCommandForRequest  ( method , contentTypeFormat , urlString , params , userAgentHeaderValue ) ; } }   private void logApiRequestDetails  (  String urlString ,  ParamsMap params ,  Method method ,  ContentType contentTypeFormat )  {  if  (  LOGGER . isLoggable  ( REQUEST_LOG_LEVEL ) )  {   logApiRequest  ( REQUEST_LOG_LEVEL , method , contentTypeFormat , urlString , params ) ; } }   private HttpResponse executeRequestByMethod  (  String urlString ,  ParamsMap params ,  Method method ,  ContentType contentTypeFormat ,  HttpClient httpClient ,  HttpResponse httpResponse )  throws MalformedURLException , IOException , MambuApiException  {  switch  ( method )  {   case GET :   httpResponse =  executeGetRequest  ( httpClient , urlString , params ) ;  break ;   case POST :   httpResponse =  executePostRequest  ( httpClient , urlString , params , contentTypeFormat ) ;  break ;   case PATCH :   httpResponse =  executePatchRequest  ( httpClient , urlString , params ) ;  break ;   case DELETE :   httpResponse =  executeDeleteRequest  ( httpClient , urlString , params ) ;  break ;   default :  throw  new IllegalArgumentException  (   "Only methods GET, POST PATCH and DELETE are supported, not " +  method . name  ( ) + "." ) ; }  return httpResponse ; }   private HttpResponse executePostRequest  (  HttpClient httpClient ,  String urlString ,  ParamsMap params ,  ContentType contentTypeFormat )  throws MalformedURLException , IOException , MambuApiException  {   final String  contentType =  getFormattedContentTypeString  ( contentTypeFormat ) ;  HttpPost  httpPost =  new HttpPost  ( urlString ) ;   httpPost . setHeader  ( CONTENT_TYPE_HEADER_NAME , contentType ) ;   httpPost . setHeader  ( AUTHORIZATION_HEADER_NAME ,  "Basic " + encodedAuthorization ) ;   httpPost . setHeader  ( USER_AGENT_HEADER_NAME ,  urlHelper . userAgentHeaderValue  ( ) ) ;  if  (   params != null &&   params . size  ( ) > 0 )  {  switch  ( contentTypeFormat )  {   case WWW_FORM :   List  < NameValuePair >  httpParams =  getListFromParams  ( params ) ;  HttpEntity  postEntity =  new UrlEncodedFormEntity  ( httpParams , UTF8_CHARSET ) ;   httpPost . setEntity  ( postEntity ) ;  break ;   case JSON :  StringEntity  jsonEntity =  makeJsonEntity  ( params ) ;   httpPost . setEntity  ( jsonEntity ) ;  break ; } }  HttpResponse  httpResponse =  httpClient . execute  ( httpPost ) ;  return httpResponse ; }   private HttpResponse executePatchRequest  (  HttpClient httpClient ,  String urlString ,  ParamsMap params )  throws MalformedURLException , IOException , MambuApiException  {   final String  contentType = JSON_CONTENT_TYPE ;  HttpPatch  httpPatch =  new HttpPatch  ( urlString ) ;   httpPatch . setHeader  ( CONTENT_TYPE_HEADER_NAME , contentType ) ;   httpPatch . setHeader  ( AUTHORIZATION_HEADER_NAME ,  "Basic " + encodedAuthorization ) ;   httpPatch . setHeader  ( USER_AGENT_HEADER_NAME ,  urlHelper . userAgentHeaderValue  ( ) ) ;  StringEntity  jsonEntity =  makeJsonEntity  ( params ) ;   httpPatch . setEntity  ( jsonEntity ) ;  HttpResponse  httpResponse =  httpClient . execute  ( httpPatch ) ;  return httpResponse ; }   private HttpResponse executeGetRequest  (  HttpClient httpClient ,  String urlString ,  ParamsMap params )  throws MalformedURLException , IOException , MambuApiException  {  if  (   params != null &&   params . size  ( ) > 0 )  {   urlString =  new String  (  (  URLHelper . makeUrlWithParams  ( urlString , params ) ) ) ; }  HttpGet  httpGet =  new HttpGet  ( urlString ) ;   httpGet . setHeader  ( AUTHORIZATION_HEADER_NAME ,  "Basic " + encodedAuthorization ) ;   httpGet . setHeader  ( USER_AGENT_HEADER_NAME ,  urlHelper . userAgentHeaderValue  ( ) ) ;  HttpResponse  httpResponse =  httpClient . execute  ( httpGet ) ;  return httpResponse ; }   private HttpResponse executeDeleteRequest  (  HttpClient httpClient ,  String urlString ,  ParamsMap params )  throws MalformedURLException , IOException , MambuApiException  {  if  (   params != null &&   params . size  ( ) > 0 )  {   urlString =  new String  (  (  URLHelper . makeUrlWithParams  ( urlString , params ) ) ) ; }  HttpDelete  httpDelete =  new HttpDelete  ( urlString ) ;   httpDelete . setHeader  ( AUTHORIZATION_HEADER_NAME ,  "Basic " + encodedAuthorization ) ;   httpDelete . setHeader  ( USER_AGENT_HEADER_NAME ,  urlHelper . userAgentHeaderValue  ( ) ) ;  HttpResponse  httpResponse =  httpClient . execute  ( httpDelete ) ;  return httpResponse ; }   private static StringEntity makeJsonEntity  (  ParamsMap params )  throws UnsupportedEncodingException  {  if  (  params == null )  {  throw  new IllegalArgumentException  ( "JSON requests require non NULL ParamsMap with JSON string" ) ; }  String  jsonString =  params . get  (  APIData . JSON_OBJECT ) ;  if  (  jsonString == null )  {  throw  new IllegalArgumentException  ( "JSON string cannot be NULL" ) ; }   jsonString =  addAppKeyToJson  ( jsonString , params ) ;  StringEntity  jsonEntity =  new StringEntity  ( jsonString , UTF8_CHARSET ) ;  return jsonEntity ; }   private static String processResponse  (  HttpResponse httpResponse ,  Method method ,  ContentType contentType ,  String urlString ,  ParamsMap params )  throws IOException , MambuApiException  {   int  status =   httpResponse . getStatusLine  ( ) . getStatusCode  ( ) ;  InputStream  content = null ;  String  response = "" ;  HttpEntity  entity =  httpResponse . getEntity  ( ) ;  if  (  entity != null )  {   content =  entity . getContent  ( ) ;  if  (  content != null )  {   response =  readStream  ( content ) ; } }  if  (  LOGGER . isLoggable  ( RESPONSE_LOG_LEVEL ) )  {   logApiResponse  ( RESPONSE_LOG_LEVEL , urlString , status , response ) ; }  if  (   status ==  HttpURLConnection . HTTP_OK ||  status ==  HttpURLConnection . HTTP_CREATED )  {  return response ; }  Integer  errorCode = status ;   logExceptionForProcessingResponse  ( method , contentType , urlString , params , response , errorCode ) ;  throw  new MambuApiException  ( errorCode , response ) ; }   private static String readStream  (  InputStream content )  throws IOException  {  String  response = "" ;  BufferedReader  in =  new BufferedReader  (  new InputStreamReader  ( content , UTF8_CHARSET ) ) ;  String  line ;  while  (   (  line =  in . readLine  ( ) ) != null )  {   response += line ; }  return response ; }    @ Override public void setAuthorization  (  String username ,  String password )  {  String  userNamePassword =   username + ":" + password ;   encodedAuthorization =  new String  (  Base64 . encodeBase64  (  userNamePassword . getBytes  ( ) ) ) ; }   private static  List  < NameValuePair > getListFromParams  (  ParamsMap params )  {   List  < NameValuePair >  nameValuePairs =  new  ArrayList  < NameValuePair >  (  params . size  ( ) ) ;  for (   Map . Entry  < String , String > entry :  params . entrySet  ( ) )  {  if  (   entry . getValue  ( ) != null )  {   nameValuePairs . add  (  new BasicNameValuePair  (  entry . getKey  ( ) ,  entry . getValue  ( ) ) ) ; } }  return nameValuePairs ; }   private static String getFormattedContentTypeString  (  ContentType contentTypeFormat )  {  switch  ( contentTypeFormat )  {   case WWW_FORM :  return WWW_FORM_URLENCODED_CONTENT_TYPE ;   case JSON :  return JSON_CONTENT_TYPE ;   default :  return WWW_FORM_URLENCODED_CONTENT_TYPE ; } }   private static String addAppKeyToJson  (  String jsonString ,  ParamsMap params )  {  if  (  params == null )  {  return jsonString ; }  String  appKey =  params . get  ( APPLICATION_KEY ) ;  return  ServiceHelper . addAppkeyValueToJson  ( appKey , jsonString ) ; }   private static void logApiRequest  (  Level logerLevel ,  Method method ,  ContentType contentType ,  String urlString ,  ParamsMap params )  {  if  (   !  LOGGER . isLoggable  ( logerLevel ) ||  method == null )  {  return ; }  String  requestDetails =   method . name  ( ) + " with URL=" ;  String  jsonString = null ;  String  urlWithParams = null ;  switch  ( method )  {   case GET :   urlWithParams =  new String  (  (  URLHelper . makeUrlWithParams  ( urlString , params ) ) ) ;   requestDetails =  requestDetails + urlWithParams ;  break ;   case POST :   case PATCH :  switch  ( contentType )  {   case WWW_FORM :   requestDetails =  requestDetails + urlString ;  if  (  params != null )  {  String  postParams =  params . getURLString  ( ) ;   requestDetails =   requestDetails + "\nParams=" + postParams ; }  break ;   case JSON :   requestDetails =  requestDetails + urlString ;  if  (  params != null )  {   jsonString =  params . get  (  APIData . JSON_OBJECT ) ; }  break ; }  break ;   case DELETE :   urlWithParams =  new String  (  (  URLHelper . makeUrlWithParams  ( urlString , params ) ) ) ;   requestDetails =  requestDetails + urlWithParams ;  break ;   default :  break ; }  if  (  contentType != null )  {   requestDetails =    requestDetails + " (contentType=" + contentType + ")" ; }  String  appKeyValue =  MambuAPIFactory . getApplicationKey  ( ) ;  if  (   requestDetails != null &&  appKeyValue != null )  {   requestDetails =  requestDetails . replace  ( appKeyValue , "..." ) ; }   LOGGER . log  ( logerLevel , requestDetails ) ;  if  (  jsonString != null )  {   logJsonInput  ( logerLevel , jsonString ) ; } }   private static void logCurlCommandForRequest  (  Method method ,  ContentType contentType ,  String urlString ,  ParamsMap params ,  String userAgentHeaderValue )  {  if  (  method == null )  {  return ; }  String  apiMethod = "" ;  switch  ( method )  {   case GET :   apiMethod = " -G" ;  break ;   case POST :   apiMethod = " -X POST" ;  break ;   case PATCH :   apiMethod = " -X PATCH" ;  break ;   case DELETE :   apiMethod = " -X DELETE" ;  break ; }  String  url = urlString ;   contentType =   (  contentType == null ) ?  ContentType . WWW_FORM : contentType ;  String  contentHeader =     " -H \"" + CONTENT_TYPE_HEADER_NAME + ": " +  getFormattedContentTypeString  ( contentType ) + "\"" ;  String  userAgentHeader =   (  userAgentHeaderValue != null ) ?     " -H \"" + USER_AGENT_HEADER_NAME + ": " + userAgentHeaderValue + "\"" : null ;  String  curlCommand =    "curl" + apiMethod + contentHeader + userAgentHeader ;  String  appKeyValue =  MambuAPIFactory . getApplicationKey  ( ) ;   final String  emptyAppKey = "..." ;  String  urlParams = "" ;  switch  ( contentType )  {   case WWW_FORM :  if  (  appKeyValue != null )  {   appKeyValue = emptyAppKey ;   urlParams =  "appkey=" + appKeyValue ; }  if  (   params != null &&   params . size  ( ) > 0 )  {  String  paramsString =  params . getURLString  ( ) ;  if  (   urlParams . length  ( ) > 0 )  {   urlParams =  urlParams + "&" ; }   urlParams =  urlParams + paramsString ; }  break ;   case JSON :  String  jsonString =   (  params == null ) ? "{}" :  params . get  (  APIData . JSON_OBJECT ) ;  if  (  appKeyValue != null )  {   jsonString =  ServiceHelper . addAppkeyValueToJson  ( appKeyValue , jsonString ) ;   final String  appKey =     "\"" +  APIData . APPLICATION_KEY + "\":\"" + appKeyValue + "\"," ;   final String  logAppKey =     "\"" +  APIData . APPLICATION_KEY + "\":\"" + emptyAppKey + "\"," ;   jsonString =  jsonString . replace  ( appKey , logAppKey ) ; }   curlCommand =    curlCommand + " -d '" + jsonString + "' " ;  break ; }   url =  url . replace  ( "://" , "://user:pwd@" ) ;  if  (   urlParams . length  ( ) > 0 )  {   url =   url + "?" + urlParams ; }   curlCommand =     "\n" + curlCommand + " '" + url + "'" ;   LOGGER . log  ( curlRequestTemplateLogLevel , curlCommand ) ; }   final static String  documentContentParam = "\"documentContent\":" ;   final static String  documentRoot = "\"document\":" ;   final static String  documentsApiEndpoint =   "/" +  APIData . DOCUMENTS + "/" ;   final static String  moreIndicator = "...\"" ;   final static  int  howManyEncodedCharsToShow = 20 ;   final static  int  howManyDocumentResponseCharsToShow = 50 ;   private static void logJsonInput  (  Level logerLevel ,  String jsonString )  {  if  (   !  LOGGER . isLoggable  ( logerLevel ) ||  jsonString == null )  {  return ; }  if  (  jsonString . contains  ( documentRoot ) )  {   int  contentStarts =  jsonString . indexOf  ( documentContentParam ) ;  if  (  contentStarts !=  - 1 )  {   final  int  encodedCharsToShow = 20 ;   jsonString =    jsonString . substring  ( 0 ,   contentStarts +  documentContentParam . length  ( ) + encodedCharsToShow ) + moreIndicator + "}" ; } }   LOGGER . log  ( logerLevel ,  "Input JsonString=" + jsonString ) ; }   private static void logApiResponse  (  Level logerLevel ,  String urlString ,   int status ,  String response )  {  if  (   status !=  HttpURLConnection . HTTP_OK &&  status !=  HttpURLConnection . HTTP_CREATED )  {   LOGGER . log  ( EXCEPTION_LOG_LEVEL ,    "Error status=" + status + " Error response=" + response ) ; } else  {  if  (  !  LOGGER . isLoggable  ( logerLevel ) )  {  return ; }   final String  encodedDataIndicator =  APIData . BASE64_ENCODING_INDICATOR ;   final  int  encodedDataStart =  response . indexOf  ( encodedDataIndicator ) ;   final boolean  isDocumentApiResponse =   (  urlString . contains  ( documentsApiEndpoint ) ) ? true : false ;  if  (  encodedDataStart !=  - 1 )  {   int  totalCharsToShow =   encodedDataStart +  encodedDataIndicator . length  ( ) + howManyEncodedCharsToShow ;   response =   response . substring  ( 0 , totalCharsToShow ) + moreIndicator ; } else  if  ( isDocumentApiResponse )  {  if  (   response . length  ( ) > howManyDocumentResponseCharsToShow )  {   response =   response . substring  ( 0 , howManyDocumentResponseCharsToShow ) + moreIndicator ; } }   LOGGER . log  ( logerLevel ,       "Response Status=" + status + "\tMessage length=" +  response . length  ( ) + "\nResponse message=" + response + "" ) ; } }   private static void logAppKey  (  String applicationKey )  {  if  (  !  LOGGER . isLoggable  (  Level . FINEST ) )  {  return ; }   final  int  keyLength =  applicationKey . length  ( ) ;   final  int  printLength = 3 ;  if  (  keyLength >= printLength )  {   LOGGER . finest  (    "Added Application key=" +  applicationKey . substring  ( 0 , printLength ) + "..." +  applicationKey . substring  (  keyLength - printLength , keyLength ) ) ; } }   private static final String  TLS_V1_2 = "TLSv1.2" ;   private final static String  UTF8_CHARSET =   StandardCharsets . UTF_8 . name  ( ) ;   private final static String  WWW_FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8" ;   private final static String  JSON_CONTENT_TYPE = "application/json; charset=UTF-8" ;   private final static Level  REQUEST_LOG_LEVEL =  Level . FINER ;   private final static Level  RESPONSE_LOG_LEVEL =  Level . FINER ;   private final static Level  EXCEPTION_LOG_LEVEL =  Level . WARNING ;   private final static Level  CURL_REQUEST_TEMPLATE_LOG_LEVEL =  Level . FINEST ; 
<<<<<<<
=======
  private void logCurlRequestDetails  (  String urlString ,  ParamsMap params ,  Method method ,  ContentType contentTypeFormat )  {  if  (  LOGGER . isLoggable  ( CURL_REQUEST_TEMPLATE_LOG_LEVEL ) )  {   logCurlCommandForRequest  ( method , contentTypeFormat , urlString , params ) ; } }
>>>>>>>
 
<<<<<<<
=======
  private static void logCurlCommandForRequest  (  Method method ,  ContentType contentType ,  String urlString ,  ParamsMap params )  {  if  (  method == null )  {  return ; }  String  apiMethod = "" ;  switch  ( method )  {   case GET :   apiMethod = " -G" ;  break ;   case POST :   apiMethod = " -X POST" ;  break ;   case PATCH :   apiMethod = " -X PATCH" ;  break ;   case DELETE :   apiMethod = " -X DELETE" ;  break ; }  String  url = urlString ;   contentType =   (  contentType == null ) ?  ContentType . WWW_FORM : contentType ;  String  contentHeader =   " -H \"Content-type: " +  getFormattedContentTypeString  ( contentType ) + "\"" ;  String  curlCommand =   "curl" + apiMethod + contentHeader ;  String  appKeyValue =  MambuAPIFactory . getApplicationKey  ( ) ;   final String  emptyAppKey = "..." ;  String  urlParams = "" ;  switch  ( contentType )  {   case WWW_FORM :  if  (  appKeyValue != null )  {   appKeyValue = emptyAppKey ;   urlParams =  "appkey=" + appKeyValue ; }  if  (   params != null &&   params . size  ( ) > 0 )  {  String  paramsString =  params . getURLString  ( ) ;  if  (   urlParams . length  ( ) > 0 )  {   urlParams =  urlParams + "&" ; }   urlParams =  urlParams + paramsString ; }  break ;   case JSON :  String  jsonString =   (  params == null ) ? "{}" :  params . get  (  APIData . JSON_OBJECT ) ;  if  (  appKeyValue != null )  {   jsonString =  ServiceHelper . addAppkeyValueToJson  ( appKeyValue , jsonString ) ;   final String  appKey =     "\"" +  APIData . APPLICATION_KEY + "\":\"" + appKeyValue + "\"," ;   final String  logAppKey =     "\"" +  APIData . APPLICATION_KEY + "\":\"" + emptyAppKey + "\"," ;   jsonString =  jsonString . replace  ( appKey , logAppKey ) ; }   curlCommand =    curlCommand + " -d '" + jsonString + "' " ;  break ; }   url =  url . replace  ( "://" , "://user:pwd@" ) ;  if  (   urlParams . length  ( ) > 0 )  {   url =   url + "?" + urlParams ; }   curlCommand =     "\n" + curlCommand + " '" + url + "'" ;   LOGGER . log  ( CURL_REQUEST_TEMPLATE_LOG_LEVEL , curlCommand ) ; }
>>>>>>>
 }