  package    org . xhtmlrenderer . simple . extend ;   import   java . io . IOException ;  import   java . io . InputStream ;  import   java . io . InputStreamReader ;  import   java . util . ArrayList ;  import   java . util . Arrays ;  import   java . util . HashMap ;  import   java . util . List ;  import   java . util . Map ;  import    org . w3c . dom . CharacterData ;  import    org . w3c . dom . Element ;  import    org . w3c . dom . Node ;  import    org . w3c . dom . NodeList ;  import    org . w3c . dom . Text ;  import     org . xhtmlrenderer . css . extend . StylesheetFactory ;  import     org . xhtmlrenderer . css . sheet . Stylesheet ;  import     org . xhtmlrenderer . css . sheet . StylesheetInfo ;  import    org . xhtmlrenderer . simple . NoNamespaceHandler ;  import    org . xhtmlrenderer . util . Configuration ;  import    org . xhtmlrenderer . util . XRLog ;   public class XhtmlCssOnlyNamespaceHandler  extends NoNamespaceHandler  {   final static String  _namespace = "http://www.w3.org/1999/xhtml" ;   private static StylesheetInfo  _defaultStylesheet ;   private static boolean  _defaultStylesheetError = false ;   private final Map  _metadata = null ;   public String getNamespace  ( )  {  return _namespace ; }   public String getClass  (     org . w3c . dom . Element e )  {  return  e . getAttribute  ( "class" ) ; }   public String getID  (     org . w3c . dom . Element e )  {  String  result =   e . getAttribute  ( "id" ) . trim  ( ) ;  return    result . length  ( ) == 0 ? null : result ; }   protected String convertToLength  (  String value )  {  if  (  isInteger  ( value ) )  {  return  value + "px" ; } else  {  return value ; } }   protected boolean isInteger  (  String value )  {  for (   int  i = 0 ;  i <  value . length  ( ) ;  i ++ )  {   char  c =  value . charAt  ( i ) ;  if  (  !  (   c >= '0' &&  c <= '9' ) )  {  return false ; } }  return true ; }   protected String getAttribute  (  Element e ,  String attrName )  {  String  result =  e . getAttribute  ( attrName ) ;   result =  result . trim  ( ) ;  return    result . length  ( ) == 0 ? null : result ; }   public String getElementStyling  (     org . w3c . dom . Element e )  {  StringBuffer  style =  new StringBuffer  ( ) ;  if  (    e . getNodeName  ( ) . equals  ( "td" ) ||   e . getNodeName  ( ) . equals  ( "th" ) )  {  String  s ;   s =  getAttribute  ( e , "colspan" ) ;  if  (  s != null )  {   style . append  ( "-fs-table-cell-colspan: " ) ;   style . append  ( s ) ;   style . append  ( ";" ) ; }   s =  getAttribute  ( e , "rowspan" ) ;  if  (  s != null )  {   style . append  ( "-fs-table-cell-rowspan: " ) ;   style . append  ( s ) ;   style . append  ( ";" ) ; } } else  if  (   e . getNodeName  ( ) . equals  ( "img" ) )  {  String  s ;   s =  getAttribute  ( e , "width" ) ;  if  (  s != null )  {   style . append  ( "width: " ) ;   style . append  (  convertToLength  ( s ) ) ;   style . append  ( ";" ) ; }   s =  getAttribute  ( e , "height" ) ;  if  (  s != null )  {   style . append  ( "height: " ) ;   style . append  (  convertToLength  ( s ) ) ;   style . append  ( ";" ) ; } } else  if  (    e . getNodeName  ( ) . equals  ( "colgroup" ) ||   e . getNodeName  ( ) . equals  ( "col" ) )  {  String  s ;   s =  getAttribute  ( e , "span" ) ;  if  (  s != null )  {   style . append  ( "-fs-table-cell-colspan: " ) ;   style . append  ( s ) ;   style . append  ( ";" ) ; }   s =  getAttribute  ( e , "width" ) ;  if  (  s != null )  {   style . append  ( "width: " ) ;   style . append  (  convertToLength  ( s ) ) ;   style . append  ( ";" ) ; } }   style . append  (  e . getAttribute  ( "style" ) ) ;  return  style . toString  ( ) ; }   public String getLinkUri  (     org . w3c . dom . Element e )  {  String  href = null ;  if  (    e . getNodeName  ( ) . equalsIgnoreCase  ( "a" ) &&  e . hasAttribute  ( "href" ) )  {   href =  e . getAttribute  ( "href" ) ; }  return href ; }   public String getLinkTitle  (     org . w3c . dom . Element e )  {  String  title = null ;  if  (    e . getNodeName  ( ) . equalsIgnoreCase  ( "a" ) &&  e . hasAttribute  ( "title" ) )  {   title =  e . getAttribute  ( "title" ) ; }  return title ; }   public String getAnchorName  (  Element e )  {  if  (    e != null &&   e . getNodeName  ( ) . equalsIgnoreCase  ( "a" ) &&  e . hasAttribute  ( "name" ) )  {  return  e . getAttribute  ( "name" ) ; }  return null ; }   private static String readTextContent  (  Element element )  {  StringBuffer  result =  new StringBuffer  ( ) ;  Node  current =  element . getFirstChild  ( ) ;  while  (  current != null )  {   short  nodeType =  current . getNodeType  ( ) ;  if  (   nodeType ==  Node . TEXT_NODE ||  nodeType ==  Node . CDATA_SECTION_NODE )  {  Text  t =  ( Text ) current ;   result . append  (  t . getData  ( ) ) ; }   current =  current . getNextSibling  ( ) ; }  return  result . toString  ( ) ; }   private static String collapseWhiteSpace  (  String text )  {  StringBuffer  result =  new StringBuffer  ( ) ;   int  l =  text . length  ( ) ;  for (   int  i = 0 ;  i < l ;  i ++ )  {   char  c =  text . charAt  ( i ) ;  if  (  Character . isWhitespace  ( c ) )  {   result . append  ( ' ' ) ;  while  (   ++ i < l )  {   c =  text . charAt  ( i ) ;  if  (  !  Character . isWhitespace  ( c ) )  {   i -- ;  break ; } } } else  {   result . append  ( c ) ; } }  return  result . toString  ( ) ; }   public String getDocumentTitle  (     org . w3c . dom . Document doc )  {  String  title = "" ;  Element  html =  doc . getDocumentElement  ( ) ;  Element  head =  findFirstChild  ( html , "head" ) ;  if  (  head != null )  {  Element  titleElem =  findFirstChild  ( head , "title" ) ;  if  (  titleElem != null )  {   title =  collapseWhiteSpace  (   readTextContent  ( titleElem ) . trim  ( ) ) ; } }  return title ; }   private Element findFirstChild  (  Element parent ,  String targetName )  {  NodeList  children =  parent . getChildNodes  ( ) ;  for (   int  i = 0 ;  i <  children . getLength  ( ) ;  i ++ )  {  Node  n =  children . item  ( i ) ;  if  (    n . getNodeType  ( ) ==  Node . ELEMENT_NODE &&   n . getNodeName  ( ) . equals  ( targetName ) )  {  return  ( Element ) n ; } }  return null ; }   protected StylesheetInfo readStyleElement  (  Element style )  {  String  media =  style . getAttribute  ( "media" ) ;  if  (  "" . equals  ( media ) )  {   media = "all" ; }  StylesheetInfo  info =  new StylesheetInfo  ( ) ;   info . setMedia  ( media ) ;   info . setType  (  style . getAttribute  ( "type" ) ) ;   info . setTitle  (  style . getAttribute  ( "title" ) ) ;   info . setOrigin  (  StylesheetInfo . AUTHOR ) ;  StringBuffer  buf =  new StringBuffer  ( ) ;  Node  current =  style . getFirstChild  ( ) ;  while  (  current != null )  {  if  (  current instanceof CharacterData )  {   buf . append  (   (  ( CharacterData ) current ) . getData  ( ) ) ; }   current =  current . getNextSibling  ( ) ; }  String  css =   buf . toString  ( ) . trim  ( ) ;  if  (   css . length  ( ) > 0 )  {   info . setContent  (  css . toString  ( ) ) ;  return info ; } else  {  return null ; } }   protected StylesheetInfo readLinkElement  (  Element link )  {  String  rel =   link . getAttribute  ( "rel" ) . toLowerCase  ( ) ;  if  (   rel . indexOf  ( "alternate" ) !=  - 1 )  {  return null ; }  if  (   rel . indexOf  ( "stylesheet" ) ==  - 1 )  {  return null ; }  String  type =  link . getAttribute  ( "type" ) ;  if  (  !  (   type . equals  ( "" ) ||  type . equals  ( "text/css" ) ) )  {  return null ; }  StylesheetInfo  info =  new StylesheetInfo  ( ) ;  if  (  type . equals  ( "" ) )  {   type = "text/css" ; }   info . setType  ( type ) ;   info . setOrigin  (  StylesheetInfo . AUTHOR ) ;   info . setUri  (  link . getAttribute  ( "href" ) ) ;  String  media =  link . getAttribute  ( "media" ) ;  if  (  "" . equals  ( media ) )  {   media = "all" ; }   info . setMedia  ( media ) ;  String  title =  link . getAttribute  ( "title" ) ;   info . setTitle  ( title ) ;  return info ; }   public  StylesheetInfo  [ ] getStylesheets  (     org . w3c . dom . Document doc )  {  List  result =  new ArrayList  ( ) ;   result . addAll  (  Arrays . asList  (  super . getStylesheets  ( doc ) ) ) ;  Element  html =  doc . getDocumentElement  ( ) ;  Element  head =  findFirstChild  ( html , "head" ) ;  if  (  head != null )  {  Node  current =  head . getFirstChild  ( ) ;  while  (  current != null )  {  if  (   current . getNodeType  ( ) ==  Node . ELEMENT_NODE )  {  Element  elem =  ( Element ) current ;  StylesheetInfo  info = null ;  String  elemName =  elem . getLocalName  ( ) ;  if  (  elemName == null )  {   elemName =  elem . getTagName  ( ) ; }  if  (  elemName . equals  ( "link" ) )  {   info =  readLinkElement  ( elem ) ; } else  if  (  elemName . equals  ( "style" ) )  {   info =  readStyleElement  ( elem ) ; }  if  (  info != null )  {   result . add  ( info ) ; } }   current =  current . getNextSibling  ( ) ; } }  return  (  StylesheetInfo  [ ] )  result . toArray  (  new StylesheetInfo  [  result . size  ( ) ] ) ; }   public StylesheetInfo getDefaultStylesheet  (  StylesheetFactory factory )  {  synchronized  (  XhtmlCssOnlyNamespaceHandler . class )  {  if  (  _defaultStylesheet != null )  {  return _defaultStylesheet ; }  if  ( _defaultStylesheetError )  {  return null ; }  StylesheetInfo  info =  new StylesheetInfo  ( ) ;   info . setUri  (  getNamespace  ( ) ) ;   info . setOrigin  (  StylesheetInfo . USER_AGENT ) ;   info . setMedia  ( "all" ) ;   info . setType  ( "text/css" ) ;  InputStream  is = null ;  try  {   is =  getDefaultStylesheetStream  ( ) ;  if  ( _defaultStylesheetError )  {  return null ; }  Stylesheet  sheet =  factory . parse  (  new InputStreamReader  ( is ) , info ) ;   info . setStylesheet  ( sheet ) ;   is . close  ( ) ;   is = null ; }  catch (   Exception e )  {   _defaultStylesheetError = true ;   XRLog . exception  ( "Could not parse default stylesheet" , e ) ; }  finally  {  if  (  is != null )  {  try  {   is . close  ( ) ; }  catch (   IOException e )  { } } }   _defaultStylesheet = info ;  return _defaultStylesheet ; } }   private InputStream getDefaultStylesheetStream  ( )  {  InputStream  stream = null ;  String  defaultStyleSheet =   Configuration . valueFor  ( "xr.css.user-agent-default-css" ) + "XhtmlNamespaceHandler.css" ;   stream =   this . getClass  ( ) . getResourceAsStream  ( defaultStyleSheet ) ;  if  (  stream == null )  {   XRLog . exception  (    "Can't load default CSS from " + defaultStyleSheet + "." + "This file must be on your CLASSPATH. Please check before continuing." ) ;   _defaultStylesheetError = true ; }  return stream ; }   private Map getMetaInfo  (     org . w3c . dom . Document doc )  {  if  (   this . _metadata != null )  {  return  this . _metadata ; }  Map  metadata =  new HashMap  ( ) ;  Element  html =  doc . getDocumentElement  ( ) ;  Element  head =  findFirstChild  ( html , "head" ) ;  if  (  head != null )  {  Node  current =  head . getFirstChild  ( ) ;  while  (  current != null )  {  if  (   current . getNodeType  ( ) ==  Node . ELEMENT_NODE )  {  Element  elem =  ( Element ) current ;  String  elemName =  elem . getLocalName  ( ) ;  if  (  elemName == null )  {   elemName =  elem . getTagName  ( ) ; }  if  (  elemName . equals  ( "meta" ) )  {  String  http_equiv =  elem . getAttribute  ( "http-equiv" ) ;  String  content =  elem . getAttribute  ( "content" ) ;  if  (   !  http_equiv . equals  ( "" ) &&  !  content . equals  ( "" ) )  {   metadata . put  ( http_equiv , content ) ; } } }   current =  current . getNextSibling  ( ) ; } }  return metadata ; }   public String getLang  (     org . w3c . dom . Element e )  {  if  (  e == null )  {  return "" ; }  String  lang =  e . getAttribute  ( "lang" ) ;  if  (  "" . equals  ( lang ) )  {   lang =  ( String )   this . getMetaInfo  (  e . getOwnerDocument  ( ) ) . get  ( "Content-Language" ) ;  if  (  lang == null )  {   lang = "" ; } }  return lang ; } }