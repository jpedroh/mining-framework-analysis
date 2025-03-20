  package   com . jcabi . http ;   import     com . jcabi . http . request . ApacheRequest ;  import     com . jcabi . http . request . JdkRequest ;  import     com . jcabi . http . response . RestResponse ;  import     com . jcabi . http . response . XmlResponse ;  import   java . io . IOException ;  import   java . net . HttpURLConnection ;  import   java . net . URI ;  import   java . util . Arrays ;  import   java . util . Collection ;  import   org . junit . Test ;  import    org . junit . runner . RunWith ;  import    org . junit . runners . Parameterized ;    @ RunWith  (  Parameterized . class ) public final class RequestITCase  {   private final transient  Class  <  ? extends Request >  type ;   public RequestITCase  (   final  Class  <  ? extends Request > req )  {    this . type = req ; }    @  Parameterized . Parameters public static  Collection  <  Object  [ ] > primeNumbers  ( )  {  return  Arrays . asList  (  new Object  [ ]  {  ApacheRequest . class } ,  new Object  [ ]  {  JdkRequest . class } ) ; }    @ Test public void sendsHttpRequestAndProcessesHttpResponse  ( )  throws Exception  {        this . request  (  new URI  ( 
<<<<<<<
"https://http.jcabi.com"
=======
"http://www.jare.io"
>>>>>>>
 ) ) . fetch  ( ) . as  (  RestResponse . class ) . assertStatus  (  HttpURLConnection . HTTP_OK ) . as  (  XmlResponse . class ) . assertXPath  ( "/xhtml:html" ) ; }    @ Test public void processesNotOkHttpResponse  ( )  throws Exception  {      this . request  (  new URI  ( 
<<<<<<<
"https://http.jcabi.com/file-not-found.txt"
=======
"http://www.jare.io/file-not-found.txt"
>>>>>>>
 ) ) . fetch  ( ) . as  (  RestResponse . class ) . assertStatus  (  HttpURLConnection . HTTP_NOT_FOUND ) ; }    @ Test  (  expected =  IOException . class ) public void continuesOnConnectionError  ( )  throws Exception  {     this . request  (  new URI  ( "http://localhost:6868/" ) ) . method  (  Request . GET ) . fetch  ( ) ; }   private Request request  (   final URI uri )  throws Exception  {  return    this . type . getDeclaredConstructor  (  URI . class ) . newInstance  ( uri ) ; } }