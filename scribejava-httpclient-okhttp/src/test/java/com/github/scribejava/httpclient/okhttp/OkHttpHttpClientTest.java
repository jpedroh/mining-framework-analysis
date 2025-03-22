  package     com . github . scribejava . httpclient . okhttp ;   import      com . github . scribejava . core . httpclient . HttpClient ;  import  okhttp3 . OkHttpClient ;  import     com . github . scribejava . core . AbstractClientTest ;   public class OkHttpHttpClientTest  extends AbstractClientTest  { 
<<<<<<<
  private OAuthService  oAuthService ;
=======
>>>>>>>
    @ Override protected HttpClient createNewClient  ( )  {  return  new OkHttpHttpClient  (  new OkHttpClient  ( ) ) ; } }