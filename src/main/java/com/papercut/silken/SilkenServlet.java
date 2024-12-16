  package   com . papercut . silken ;   import   java . io . IOException ;  import   java . io . PrintWriter ;  import   java . io . StringWriter ;  import   java . util . Arrays ;  import   java . util . Locale ;  import   javax . servlet . ServletConfig ;  import   javax . servlet . ServletException ;  import    javax . servlet . http . HttpServlet ;  import    javax . servlet . http . HttpServletRequest ;  import    javax . servlet . http . HttpServletResponse ;  import     com . google . common . base . Strings ;  import      com . google . template . soy . data . SoyMapData ;   public class SilkenServlet  extends HttpServlet  {   private static volatile SilkenServlet  s_instance ;   private static final  long  serialVersionUID = 1L ;   private static final String  HTML_CONTENT_TYPE = "text/html" ;   private static final String  JS_CONTENT_TYPE = "text/javascript" ;   private static final String  UTF8_ENCODING = "UTF-8" ;   private final Config  config =  new Config  ( ) ;   private final TemplateRenderer  templateRenderer =  new TemplateRenderer  ( config ) ;    @ Override public void init  (  ServletConfig servletConfig )  throws ServletException  {   super . init  ( servletConfig ) ;  String  disableCaching =  servletConfig . getInitParameter  ( "disableCaching" ) ;  if  (  disableCaching != null )  {   config . setDisableCaching  (  isValueTrue  ( disableCaching ) ) ; }  if  (   System . getProperty  ( "silken.disableCaching" ) != null )  {   config . setDisableCaching  ( true ) ; }  String  sharedNamespaces =  servletConfig . getInitParameter  ( "sharedNamespaces" ) ;  if  (  !  Strings . isNullOrEmpty  ( sharedNamespaces ) )  {   config . setSharedNameSpaces  (  Arrays . asList  (  sharedNamespaces . split  ( "[,;]" ) ) ) ; }  String  localeResolver =  servletConfig . getInitParameter  ( "localeResolver" ) ;  if  (  localeResolver != null )  {  try  {  Object  resolver =   Class . forName  ( localeResolver ) . newInstance  ( ) ;   config . setLocaleResolver  (  ( LocaleResolver ) resolver ) ; }  catch (   Exception e )  {  throw  new ServletException  ( "Unable to create localeResolver" , e ) ; } }  String  modelResolver =  servletConfig . getInitParameter  ( "modelResolver" ) ;  if  (  modelResolver != null )  {  try  {  Object  resolver =   Class . forName  ( modelResolver ) . newInstance  ( ) ;   config . setModelResolver  (  ( ModelResolver ) resolver ) ; }  catch (   Exception e )  {  throw  new ServletException  ( "Unable to create modelResolver" , e ) ; } }  String  fileSetResolver =  servletConfig . getInitParameter  ( "fileSetResolver" ) ;  if  (  fileSetResolver != null )  {  try  {  Object  resolver =   Class . forName  ( fileSetResolver ) . newInstance  ( ) ;   config . setFileSetResolver  (  ( FileSetResolver ) resolver ) ; }  catch (   Exception e )  {  throw  new ServletException  ( "Unable to create fileSetResolver" , e ) ; } } else  {   config . setFileSetResolver  (  new WebAppFileSetResolver  (  getServletContext  ( ) ) ) ; }  String  compileTimeGlobalsProvider =  servletConfig . getInitParameter  ( "compileTimeGlobalsProvider" ) ;  if  (  compileTimeGlobalsProvider != null )  {  try  {  Object  provider =   Class . forName  ( compileTimeGlobalsProvider ) . newInstance  ( ) ;   config . setCompileTimeGlobalsProvider  (  ( CompileTimeGlobalsProvider ) provider ) ; }  catch (   Exception e )  {  throw  new ServletException  ( "Unable to create compileTimeGlobalsProvider" , e ) ; } }  String  runtimeGlobalsProvider =  servletConfig . getInitParameter  ( "runtimeGlobalsResolver" ) ;  if  (  runtimeGlobalsProvider != null )  {  try  {  Object  provider =   Class . forName  ( runtimeGlobalsProvider ) . newInstance  ( ) ;   config . setRuntimeGlobalsResolver  (  ( RuntimeGlobalsResolver ) provider ) ; }  catch (   Exception e )  {  throw  new ServletException  ( "Unable to create runtimeGlobalsResolver" , e ) ; } }  String  stackTraces =  servletConfig . getInitParameter  ( "showStackTracesInErrors" ) ;  if  (  stackTraces != null )  {  if  (  isValueFalse  ( stackTraces ) )  {   config . setShowStackTracesInErrors  ( false ) ; } else  {   config . setShowStackTracesInErrors  ( true ) ; } }  String  searchPath =  servletConfig . getInitParameter  ( "searchPath" ) ;  if  (  searchPath != null )  {   config . setSearchPath  ( searchPath ) ; }    getServletContext  ( ) . setAttribute  ( "silken.config" , config ) ;    getServletContext  ( ) . setAttribute  ( "silken.templateRenderer" , templateRenderer ) ;  String  namespaces =  servletConfig . getInitParameter  ( "precompileNamespaces" ) ;  if  (  !  Strings . isNullOrEmpty  ( namespaces ) )  {  for ( String ns :  namespaces . split  ( "[,;]" ) )  {  try  {   templateRenderer . precompile  ( ns ) ; }  catch (   Exception e )  {    servletConfig . getServletContext  ( ) . log  (  "Unable to precompile namespace: " + ns , e ) ; } } }   s_instance = this ; }    @ Override protected void doGet  (  HttpServletRequest req ,  HttpServletResponse resp )  throws ServletException , IOException  {   doRequest  ( req , resp ) ; }    @ Override protected void doPost  (  HttpServletRequest req ,  HttpServletResponse resp )  throws ServletException , IOException  {   doRequest  ( req , resp ) ; }   private void doRequest  (  HttpServletRequest req ,  HttpServletResponse resp )  throws ServletException , IOException  {  try  {   final String  pathInfo =  req . getPathInfo  ( ) ;  if  (    pathInfo == null ||  pathInfo . isEmpty  ( ) ||  pathInfo . equals  ( "/" ) )  {   error  ( req , resp ,  new RuntimeException  ( "No valid soy template defined. Check the path." ) ) ;  return ; }   final String  path =  pathInfo . substring  ( 1 ) ;  if  (  path . startsWith  ( "_" ) )  {  if  (  path . startsWith  ( "_precompile/" ) )  {   templateRenderer . precompile  (  namespaceFromPath  ( path ) ) ;  return ; }  if  (  path . startsWith  ( "_flush/" ) )  {   templateRenderer . flush  (  namespaceFromPath  ( path ) ) ;  return ; }  if  (  path . startsWith  ( "_flushAll" ) )  {   templateRenderer . flushAll  ( ) ;  return ; } }  if  (  path . startsWith  ( "js/" ) )  {  Locale  locale = null ;  String  namespace =  namespaceFromPath  ( path ) ;  if  (  namespace . endsWith  ( ".js" ) )  {   namespace =  namespace . substring  ( 0 ,   namespace . length  ( ) - 3 ) ; }   String  [ ]  components =  path . split  ( "/" ) ;  if  (   components . length == 3 )  {   locale =   config . getLocaleResolver  ( ) . resolveLocale  ( req ) ; } else  if  (   components . length == 4 )  {   locale =  Utils . stringToLocale  (  components [ 2 ] ) ; } else  {  throw  new RuntimeException  ( "Request not in the format: /soy/js/[serial]/[optional:locale]/namespace.js" ) ; }   resp . setContentType  ( JS_CONTENT_TYPE ) ;   resp . setCharacterEncoding  ( UTF8_ENCODING ) ;  if  (  config . isDisableCaching  ( ) )  {   resp . setHeader  ( "Cache-Control" , "no-cache" ) ; } else  {   resp . setHeader  ( "Cache-Control" ,  "max-age=" +  Long . toString  (  config . getJavaScriptCacheMaxAge  ( ) ) ) ; }    resp . getWriter  ( ) . print  (  templateRenderer . provideAsJavaScript  ( namespace , locale ) ) ;  return ; }   final Locale  locale =   config . getLocaleResolver  ( ) . resolveLocale  ( req ) ;   final String  templateName = path ;  SoyMapData  model =   config . getModelResolver  ( ) . resolveModel  ( req ) ;  SoyMapData  globals = null ;  if  (   config . getRuntimeGlobalsResolver  ( ) != null )  {   globals =   config . getRuntimeGlobalsResolver  ( ) . resolveGlobals  ( req ) ; }   resp . setContentType  ( HTML_CONTENT_TYPE ) ;   resp . setCharacterEncoding  ( UTF8_ENCODING ) ;    resp . getWriter  ( ) . print  (  templateRenderer . render  ( templateName , model , globals , locale ) ) ;  return ; }  catch (   Exception e )  {   error  ( req , resp , e ) ; } }   private void error  (  HttpServletRequest req ,  HttpServletResponse resp ,  Exception ex )  throws ServletException , IOException  {  StringBuffer  html =  new StringBuffer  ( ) ;   html . append  ( "<html>" ) ;   html . append  ( "<title>Error</title>" ) ;   html . append  ( "<body bgcolor=\"#ffffff\">" ) ;   html . append  ( "<h2>SoyTemplateRenderer: Error rendering template</h2>" ) ;   html . append  ( "<pre style='white-space: pre-wrap;'>" ) ;  String  why =  ex . getMessage  ( ) ;  if  (   why != null &&    why . trim  ( ) . length  ( ) > 0 )  {   html . append  ( why ) ;   html . append  ( "<br>" ) ; }  if  (  config . isShowStackTracesInErrors  ( ) )  {   html . append  ( "<br>" ) ;  StringWriter  sw =  new StringWriter  ( ) ;   ex . printStackTrace  (  new PrintWriter  ( sw ) ) ;   html . append  (  sw . toString  ( ) ) ; }   html . append  ( "</pre>" ) ;   html . append  ( "</body>" ) ;   html . append  ( "</html>" ) ;    resp . getWriter  ( ) . append  (  html . toString  ( ) ) ; }   private String namespaceFromPath  (  String path )  {   final  int  slashPos =  path . lastIndexOf  ( '/' ) ;  return  path . substring  (  slashPos + 1 ) ; }   private boolean isValueTrue  (  String value )  {   value =    Strings . nullToEmpty  ( value ) . trim  ( ) . toLowerCase  ( ) ;  return  (    value . startsWith  ( "t" ) ||  value . equals  ( "1" ) ||  value . startsWith  ( "y" ) ) ; }   private boolean isValueFalse  (  String value )  {   value =    Strings . nullToEmpty  ( value ) . trim  ( ) . toLowerCase  ( ) ;  return  (    value . startsWith  ( "f" ) ||  value . equals  ( "0" ) ||  value . startsWith  ( "n" ) ) ; }   public static SilkenServlet getInstance  ( )  {  if  (  s_instance == null )  throw  new IllegalStateException  ( "The Silken Servlet is not yet initialized/loaded!" ) ;  return s_instance ; }   public static Config getConfig  ( )  {  return   getInstance  ( ) . config ; }   public static TemplateRenderer getTemplateRenderer  ( )  {  return   getInstance  ( ) . templateRenderer ; } }