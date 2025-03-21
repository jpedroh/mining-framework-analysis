  package     org . dspace . statistics . export . service ;   import static    org . hamcrest . CoreMatchers . is ;  import static    org . hamcrest . MatcherAssert . assertThat ;  import static    org . mockito . ArgumentMatchers . any ;  import static    org . mockito . ArgumentMatchers . anyString ;  import static    org . mockito . Mockito . doNothing ;  import static    org . mockito . Mockito . doReturn ;  import static    org . mockito . Mockito . mock ;  import static    org . mockito . Mockito . times ;  import static    org . mockito . Mockito . verify ;  import static    org . mockito . Mockito . when ;  import   java . io . IOException ;  import   java . net . HttpURLConnection ;  import   java . sql . SQLException ;  import   java . util . List ;  import      org . apache . http . client . config . RequestConfig ;  import    org . dspace . core . Context ;  import     org . dspace . statistics . export . OpenURLTracker ;  import   org . junit . Test ;  import    org . junit . runner . RunWith ;  import   org . mockito . Mock ;  import   org . mockito . Mockito ;  import    org . mockito . junit . MockitoJUnitRunner ;  import static    org . hamcrest . Matchers . closeTo ;  import static    org . mockito . ArgumentMatchers . eq ;  import   java . math . BigDecimal ;  import   java . util . Date ;  import    org . apache . http . HttpResponse ;  import    org . apache . http . StatusLine ;  import     org . apache . http . client . HttpClient ;  import   org . junit . Before ;  import   org . mockito . ArgumentCaptor ;    @ RunWith  (  MockitoJUnitRunner . class ) public class OpenUrlServiceImplTest  {   private OpenUrlServiceImpl  openUrlService ;    @ Mock private FailedOpenURLTrackerService  failedOpenURLTrackerService ;    @ Test public void testProcessUrl  ( )  throws IOException , SQLException  {  Context  context =  mock  (  Context . class ) ;     doReturn  (  createMockHttpResponse  (  HttpURLConnection . HTTP_OK ) ) . when  ( httpClient ) . execute  (  any  ( ) ) ;   openUrlService . processUrl  ( context , "test-url" ) ;    verify  ( openUrlService ,  times  ( 0 ) ) . logfailed  ( context , "test-url" ) ; }    @ Test public void testProcessUrlOnFail  ( )  throws IOException , SQLException  {  Context  context =  mock  (  Context . class ) ;     doReturn  (  createMockHttpResponse  (  HttpURLConnection . HTTP_INTERNAL_ERROR ) ) . when  ( httpClient ) . execute  (  any  ( ) ) ;     doNothing  ( ) . when  ( openUrlService ) . logfailed  (  any  (  Context . class ) ,  anyString  ( ) ) ;   openUrlService . processUrl  ( context , "test-url" ) ;    verify  ( openUrlService ,  times  ( 1 ) ) . logfailed  ( context , "test-url" ) ; }    @ Test public void testReprocessFailedQueue  ( )  throws IOException , SQLException  {  Context  context =  mock  (  Context . class ) ;   List  < OpenURLTracker >  trackers =  List . of  (  createMockTracker  ( "tacker1" ) ,  createMockTracker  ( "tacker2" ) ,  createMockTracker  ( "tacker3" ) ) ;    when  (  failedOpenURLTrackerService . findAll  (  any  (  Context . class ) ) ) . thenReturn  ( trackers ) ;     doReturn  (  createMockHttpResponse  (  HttpURLConnection . HTTP_INTERNAL_ERROR ) ,  createMockHttpResponse  (  HttpURLConnection . HTTP_NOT_FOUND ) ,  createMockHttpResponse  (  HttpURLConnection . HTTP_OK ) ) . when  ( httpClient ) . execute  (  any  ( ) ) ;   openUrlService . reprocessFailedQueue  ( context ) ;    verify  ( openUrlService ,  times  ( 3 ) ) . tryReprocessFailed  (  any  (  Context . class ) ,  any  (  OpenURLTracker . class ) ) ;    verify  ( failedOpenURLTrackerService ,  times  ( 0 ) ) . remove  (  any  (  Context . class ) ,  eq  (  trackers . get  ( 0 ) ) ) ;    verify  ( failedOpenURLTrackerService ,  times  ( 0 ) ) . remove  (  any  (  Context . class ) ,  eq  (  trackers . get  ( 1 ) ) ) ;    verify  ( failedOpenURLTrackerService ,  times  ( 1 ) ) . remove  (  any  (  Context . class ) ,  eq  (  trackers . get  ( 2 ) ) ) ; }    @ Test public void testLogfailed  ( )  throws SQLException  {  Context  context =  mock  (  Context . class ) ;  OpenURLTracker  tracker1 =  mock  (  OpenURLTracker . class ) ;    when  (  failedOpenURLTrackerService . create  (  any  (  Context . class ) ) ) . thenReturn  ( tracker1 ) ;  String  failedUrl = "failed-url" ;   openUrlService . logfailed  ( context , failedUrl ) ;    verify  ( tracker1 ) . setUrl  ( failedUrl ) ;   ArgumentCaptor  < Date >  dateArgCaptor =  ArgumentCaptor . forClass  (  Date . class ) ;    verify  ( tracker1 ) . setUploadDate  (  dateArgCaptor . capture  ( ) ) ;   assertThat  (  new BigDecimal  (   dateArgCaptor . getValue  ( ) . getTime  ( ) ) ,  closeTo  (  new BigDecimal  (   new Date  ( ) . getTime  ( ) ) ,  new BigDecimal  ( 5000 ) ) ) ; }    @ Test public void testTimeout  ( )  throws IOException , SQLException  {  Context  context =  mock  (  Context . class ) ;  String  URL = "http://bla.com" ;   RequestConfig . Builder  requestConfig =  mock  (   RequestConfig . Builder . class ) ;     doReturn  ( 
<<<<<<<
requestConfig
=======
 createMockHttpResponse  (  HttpURLConnection . HTTP_OK )
>>>>>>>
 ) . when  ( 
<<<<<<<
openUrlService
=======
httpClient
>>>>>>>
 ) . 
<<<<<<<
getRequestConfigBuilder
=======
execute
>>>>>>>
  (  any  ( ) ) ;   
<<<<<<<
  doReturn  ( requestConfig ) . when  ( requestConfig )
=======
openUrlService
>>>>>>>
 . 
<<<<<<<
setConnectTimeout
=======
processUrl
>>>>>>>
  ( 
<<<<<<<
 10 * 1000
=======
context
>>>>>>>
 , "test-url" ) ;    
<<<<<<<
 doReturn  (   RequestConfig . custom  ( ) . build  ( ) )
=======
verify
>>>>>>>
 . when  ( 
<<<<<<<
requestConfig
=======
openUrlService
>>>>>>>
 ) . 
<<<<<<<
build
=======
getHttpClient
>>>>>>>
  (  any  ( ) ) ;   
<<<<<<<
openUrlService
=======
 verify  ( openUrlService )
>>>>>>>
 . 
<<<<<<<
processUrl
=======
getHttpClientRequestConfig
>>>>>>>
  ( context , URL ) ;   
<<<<<<<
 Mockito . verify  ( requestConfig )
=======
assertThat
>>>>>>>
 . setConnectTimeout  ( 
<<<<<<<
 10 * 1000
=======
  openUrlService . getHttpClientRequestConfig  ( ) . getConnectTimeout  ( )
>>>>>>>
 ,  is  (  10 * 1000 ) ) ; }    @ Mock private HttpClient  httpClient ;    @ Before public void setUp  ( )  throws Exception  {   openUrlService =  Mockito . spy  (  OpenUrlServiceImpl . class ) ;    openUrlService . failedOpenUrlTrackerService = failedOpenURLTrackerService ;     doReturn  ( httpClient ) . when  ( openUrlService ) . getHttpClient  (  any  ( ) ) ; }   protected HttpResponse createMockHttpResponse  (   int statusCode )  {  StatusLine  statusLine =  mock  (  StatusLine . class ) ;    when  (  statusLine . getStatusCode  ( ) ) . thenReturn  ( statusCode ) ;  HttpResponse  httpResponse =  mock  (  HttpResponse . class ) ;    when  (  httpResponse . getStatusLine  ( ) ) . thenReturn  ( statusLine ) ;  return httpResponse ; }   protected OpenURLTracker createMockTracker  (  String url )  {  OpenURLTracker  tracker =  mock  (  OpenURLTracker . class ) ;    when  (  tracker . getUrl  ( ) ) . thenReturn  ( url ) ;  return tracker ; } }