  package   org . restheart . utils ;   import    org . restheart . handlers . RequestContext ;  import    io . undertow . server . HttpServerExchange ;  import   java . io . UnsupportedEncodingException ;  import   java . net . URLDecoder ;  import   java . util . Deque ;   public class URLUtilis  {   static public String removeTrailingSlashes  (  String s )  {  if  (   s == null ||   s . length  ( ) < 2 )  {  return s ; }  if  (    s . trim  ( ) . charAt  (   s . length  ( ) - 1 ) == '/' )  {  return  removeTrailingSlashes  (  s . substring  ( 0 ,   s . length  ( ) - 1 ) ) ; } else  {  return  s . trim  ( ) ; } }   static public String decodeQueryString  (  String qs )  {  try  {  return   URLDecoder . decode  (  qs . replace  ( "+" , "%2B" ) , "UTF-8" ) . replace  ( "%2B" , "+" ) ; }  catch (   UnsupportedEncodingException ex )  {  return null ; } }   static public String getParentPath  (  String path )  {  if  (    path == null ||  path . equals  ( "" ) ||  path . equals  ( "/" ) )  {  return path ; }   int  lastSlashPos =  path . lastIndexOf  ( '/' ) ;  if  (  lastSlashPos > 0 )  {  return  path . substring  ( 0 , lastSlashPos ) ; } else  if  (  lastSlashPos == 0 )  {  return "/" ; } else  {  return "" ; } }   static public String getPrefixUrl  (  HttpServerExchange exchange )  {  return   exchange . getRequestURL  ( ) . replaceAll  (  exchange . getRelativePath  ( ) , "" ) ; }   static public String getUriWithDocId  (  RequestContext context ,  String dbName ,  String collName ,  String documentId )  {  StringBuilder  sb =  new StringBuilder  ( ) ;        sb . append  ( "/" ) . append  ( dbName ) . append  ( "/" ) . append  ( collName ) . append  ( "/" ) . append  ( documentId ) ;  return  context . mapUri  (   sb . toString  ( ) . replaceAll  ( " " , "" ) ) ; }   static public String getUriWithFilterMany  (  RequestContext context ,  String dbName ,  String collName ,  String referenceField ,  String ids )  {  StringBuilder  sb =  new StringBuilder  ( ) ;                sb . append  ( "/" ) . append  ( dbName ) . append  ( "/" ) . append  ( collName ) . append  ( "?" ) . append  ( "filter={" ) . append  ( "'" ) . append  ( referenceField ) . append  ( "'" ) . append  ( ":" ) . append  ( "{'$in'" ) . append  ( ":" ) . append  ( ids ) . append  ( "}}" ) ;  return  context . mapUri  (   sb . toString  ( ) . replaceAll  ( " " , "" ) ) ; }   static public String getUriWithFilterOne  (  RequestContext context ,  String dbName ,  String collName ,  String referenceField ,  String ids )  {  StringBuilder  sb =  new StringBuilder  ( ) ;              sb . append  ( "/" ) . append  ( dbName ) . append  ( "/" ) . append  ( collName ) . append  ( "?" ) . append  ( "filter={" ) . append  ( "'" ) . append  ( referenceField ) . append  ( "'" ) . append  ( ":" ) . append  ( ids ) . append  ( "}" ) ;  return  context . mapUri  (   sb . toString  ( ) . replaceAll  ( " " , "" ) ) ; }   static public String getUriWithFilterManyInverse  (  RequestContext context ,  String dbName ,  String collName ,  String referenceField ,  String ids )  {  StringBuilder  sb =  new StringBuilder  ( ) ;             sb . append  ( "/" ) . append  ( dbName ) . append  ( "/" ) . append  ( collName ) . append  ( "?" ) . append  ( "filter={'" ) . append  ( referenceField ) . append  ( "':{" ) . append  ( "'$elemMatch':{'$eq':" ) . append  ( ids ) . append  ( "}}}" ) ;  return  context . mapUri  (   sb . toString  ( ) . replaceAll  ( " " , "" ) ) ; }   public static String getQueryStringRemovingParams  (  HttpServerExchange exchange ,  String ...  paramsToRemove )  {  String  ret =  exchange . getQueryString  ( ) ;  if  (    ret == null ||  ret . isEmpty  ( ) ||  paramsToRemove == null ||  ret . isEmpty  ( ) )  {  return ret ; }  for ( String key : paramsToRemove )  {   Deque  < String >  values =   exchange . getQueryParameters  ( ) . get  ( key ) ;  if  (  values != null )  {  for ( String value : values )  {   ret =  ret . replaceAll  (    key + "=" + value + "&" , "" ) ;   ret =  ret . replaceAll  (    key + "=" + value + "$" , "" ) ; } } }  return ret ; } }