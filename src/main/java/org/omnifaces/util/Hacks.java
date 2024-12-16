  package   org . omnifaces . util ;   import static     org . omnifaces . util . FacesLocal . getContextAttribute ;  import static     org . omnifaces . util . FacesLocal . getInitParameter ;  import static     org . omnifaces . util . FacesLocal . setContextAttribute ;  import    java . lang . reflect . Field ;  import    java . lang . reflect . Method ;  import   java . util . Arrays ;  import   java . util . Collection ;  import   java . util . Collections ;  import   java . util . HashMap ;  import   java . util . LinkedHashSet ;  import   java . util . Map ;  import   java . util . Set ;  import    javax . faces . context . FacesContext ;  import    javax . faces . context . PartialViewContext ;  import    javax . faces . context . PartialViewContextWrapper ;  import    org . omnifaces . resourcehandler . ResourceIdentifier ;   public final class Hacks  {   private static final boolean  RICHFACES_INSTALLED =  initRichFacesInstalled  ( ) ;   private static final String  RICHFACES_PVC_CLASS_NAME = "org.richfaces.context.ExtendedPartialViewContextImpl" ;   private static final String  RICHFACES_RLR_RENDERER_TYPE = "org.richfaces.renderkit.ResourceLibraryRenderer" ;   private static final String  RICHFACES_RLF_CLASS_NAME = "org.richfaces.resource.ResourceLibraryFactoryImpl" ;   private static final String  MYFACES_PACKAGE_PREFIX = "org.apache.myfaces." ;   private static final String  MYFACES_RENDERED_SCRIPT_RESOURCES_KEY = "org.apache.myfaces.RENDERED_SCRIPT_RESOURCES_SET" ;   private static final String  MYFACES_RENDERED_STYLESHEET_RESOURCES_KEY = "org.apache.myfaces.RENDERED_STYLESHEET_RESOURCES_SET" ;   private static final  Set  < String >  MOJARRA_MYFACES_RESOURCE_DEPENDENCY_KEYS =  Utils . unmodifiableSet  ( "com.sun.faces.PROCESSED_RESOURCE_DEPENDENCIES" , MYFACES_RENDERED_SCRIPT_RESOURCES_KEY , MYFACES_RENDERED_STYLESHEET_RESOURCES_KEY ) ;   private static final String  MOJARRA_DEFAULT_RESOURCE_MAX_AGE = "com.sun.faces.defaultResourceMaxAge" ;   private static final String  MYFACES_DEFAULT_RESOURCE_MAX_AGE = "org.apache.myfaces.RESOURCE_MAX_TIME_EXPIRES" ;   private static final  long  DEFAULT_RESOURCE_MAX_AGE = 604800000L ;   private static final  String  [ ]  PARAM_NAMES_RESOURCE_MAX_AGE =  { MOJARRA_DEFAULT_RESOURCE_MAX_AGE , MYFACES_DEFAULT_RESOURCE_MAX_AGE } ;   private static final String  ERROR_MAX_AGE = "The '%s' init param must be a number. Encountered an invalid value of '%s'." ;   private static final String  ERROR_CREATE_INSTANCE = "Cannot create instance of class '%s'." ;   private static final String  ERROR_ACCESS_FIELD = "Cannot access field '%s' of class '%s'." ;   private static final String  ERROR_INVOKE_METHOD = "Cannot invoke method '%s' of class '%s' with arguments %s." ;   private static Boolean  myFacesUsed ;   private static Long  defaultResourceMaxAge ;   private Hacks  ( )  { }   private static boolean initRichFacesInstalled  ( )  {  try  {   Class . forName  ( RICHFACES_PVC_CLASS_NAME ) ;  return true ; }  catch (   ClassNotFoundException ignore )  {  return false ; } }   public static boolean isRichFacesInstalled  ( )  {  return RICHFACES_INSTALLED ; }   public static PartialViewContext getRichFacesPartialViewContext  ( )  {  PartialViewContext  context =  Ajax . getContext  ( ) ;  while  (   !    context . getClass  ( ) . getName  ( ) . equals  ( RICHFACES_PVC_CLASS_NAME ) &&  context instanceof PartialViewContextWrapper )  {   context =   (  ( PartialViewContextWrapper ) context ) . getWrapped  ( ) ; }  if  (    context . getClass  ( ) . getName  ( ) . equals  ( RICHFACES_PVC_CLASS_NAME ) )  {  return context ; } else  {  return null ; } }   public static  Collection  < String > getRichFacesRenderIds  ( )  {  PartialViewContext  richFacesContext =  getRichFacesPartialViewContext  ( ) ;  if  (  richFacesContext != null )  {   Collection  < String >  renderIds =  accessField  ( richFacesContext , "componentRenderIds" ) ;  if  (  renderIds != null )  {  return renderIds ; } }  return  Collections . emptyList  ( ) ; }   public static PartialViewContext getRichFacesWrappedPartialViewContext  ( )  {  PartialViewContext  richFacesContext =  getRichFacesPartialViewContext  ( ) ;  if  (  richFacesContext != null )  {  return  accessField  ( richFacesContext , "wrappedViewContext" ) ; }  return null ; }   public static boolean isRichFacesResourceLibraryRenderer  (  String rendererType )  {  return  RICHFACES_RLR_RENDERER_TYPE . equals  ( rendererType ) ; }    @ SuppressWarnings  ( "rawtypes" ) public static  Set  < ResourceIdentifier > getRichFacesResourceLibraryResources  (  ResourceIdentifier id )  {  Object  resourceFactory =  createInstance  ( RICHFACES_RLF_CLASS_NAME ) ;  String  name =    id . getName  ( ) . split  ( "\\." ) [ 0 ] ;  Object  resourceLibrary =  invokeMethod  ( resourceFactory , "getResourceLibrary" , name ,  id . getLibrary  ( ) ) ;  Iterable  resources =  invokeMethod  ( resourceLibrary , "getResources" ) ;   Set  < ResourceIdentifier >  resourceIdentifiers =  new  LinkedHashSet  < ResourceIdentifier >  ( ) ;  for ( Object resource : resources )  {  String  libraryName =  invokeMethod  ( resource , "getLibraryName" ) ;  String  resourceName =  invokeMethod  ( resource , "getResourceName" ) ;   resourceIdentifiers . add  (  new ResourceIdentifier  ( libraryName , resourceName ) ) ; }  return resourceIdentifiers ; } 
<<<<<<<
  private static  List  < Integer > toVersionElements  (  String version )  {   List  < Integer >  versionElements =  new  ArrayList  < Integer >  ( ) ;  for ( String string :  version . split  ( "\\." ) )  {   versionElements . add  (  Integer . valueOf  ( string ) ) ; }  return versionElements ; }
=======
>>>>>>>
   public static boolean isMyFacesUsed  ( )  {  if  (  myFacesUsed == null )  {  FacesContext  context =  FacesContext . getCurrentInstance  ( ) ;  if  (  context != null )  {   myFacesUsed =     context . getClass  ( ) . getPackage  ( ) . getName  ( ) . startsWith  ( MYFACES_PACKAGE_PREFIX ) ; } else  {  return false ; } }  return myFacesUsed ; }   public static void setScriptResourceRendered  (  FacesContext context ,  ResourceIdentifier id )  {   setMojarraResourceRendered  ( context , id ) ;  if  (  isMyFacesUsed  ( ) )  {   setMyFacesResourceRendered  ( context , MYFACES_RENDERED_SCRIPT_RESOURCES_KEY , id ) ; } }   public static boolean isScriptResourceRendered  (  FacesContext context ,  ResourceIdentifier id )  {  boolean  rendered =  isMojarraResourceRendered  ( context , id ) ;  if  (   ! rendered &&  isMyFacesUsed  ( ) )  {  return  isMyFacesResourceRendered  ( context , MYFACES_RENDERED_SCRIPT_RESOURCES_KEY , id ) ; } else  {  return rendered ; } }   public static void setStylesheetResourceRendered  (  FacesContext context ,  ResourceIdentifier id )  {   setMojarraResourceRendered  ( context , id ) ;  if  (  isMyFacesUsed  ( ) )  {   setMyFacesResourceRendered  ( context , MYFACES_RENDERED_STYLESHEET_RESOURCES_KEY , id ) ; } }   private static void setMojarraResourceRendered  (  FacesContext context ,  ResourceIdentifier id )  {    context . getAttributes  ( ) . put  (   id . getName  ( ) +  id . getLibrary  ( ) , true ) ; }   private static boolean isMojarraResourceRendered  (  FacesContext context ,  ResourceIdentifier id )  {  return   context . getAttributes  ( ) . containsKey  (   id . getName  ( ) +  id . getLibrary  ( ) ) ; }   private static void setMyFacesResourceRendered  (  FacesContext context ,  String key ,  ResourceIdentifier id )  {    getMyFacesResourceMap  ( context , key ) . put  (  getMyFacesResourceKey  ( id ) , true ) ; }   private static boolean isMyFacesResourceRendered  (  FacesContext context ,  String key ,  ResourceIdentifier id )  {  return   getMyFacesResourceMap  ( context , key ) . containsKey  (  getMyFacesResourceKey  ( id ) ) ; }   private static  Map  < String , Boolean > getMyFacesResourceMap  (  FacesContext context ,  String key )  {   Map  < String , Boolean >  map =  getContextAttribute  ( context , key ) ;  if  (  map == null )  {   map =  new  HashMap  < String , Boolean >  ( ) ;   setContextAttribute  ( context , key , map ) ; }  return map ; }   private static String getMyFacesResourceKey  (  ResourceIdentifier id )  {  String  library =  id . getLibrary  ( ) ;  String  name =  id . getName  ( ) ;  return   (  library != null ) ?  (   library + '/' + name ) : name ; }   public static void removeResourceDependencyState  (  FacesContext context )  {     context . getAttributes  ( ) . keySet  ( ) . removeAll  ( MOJARRA_MYFACES_RESOURCE_DEPENDENCY_KEYS ) ;     context . getAttributes  ( ) . values  ( ) . removeAll  (  Collections . singleton  ( true ) ) ; }   public static  long getDefaultResourceMaxAge  ( )  {  if  (  defaultResourceMaxAge != null )  {  return defaultResourceMaxAge ; }  FacesContext  context =  FacesContext . getCurrentInstance  ( ) ;  if  (  context == null )  {  return DEFAULT_RESOURCE_MAX_AGE ; }  for ( String name : PARAM_NAMES_RESOURCE_MAX_AGE )  {  String  value =  getInitParameter  ( context , name ) ;  if  (  value != null )  {  try  {   defaultResourceMaxAge =  Long . valueOf  ( value ) ;  return defaultResourceMaxAge ; }  catch (   NumberFormatException e )  {  throw  new IllegalArgumentException  (  String . format  ( ERROR_MAX_AGE , name , value ) , e ) ; } } }   defaultResourceMaxAge = DEFAULT_RESOURCE_MAX_AGE ;  return defaultResourceMaxAge ; }   public static boolean isPrimeFacesDynamicResourceRequest  (  FacesContext context )  {   Map  < String , String >  params =   context . getExternalContext  ( ) . getRequestParameterMap  ( ) ;  return   "primefaces" . equals  (  params . get  ( "ln" ) ) &&   params . get  ( "pfdrid" ) != null ; }   private static Object createInstance  (  String className )  {  try  {  return   Class . forName  ( className ) . newInstance  ( ) ; }  catch (   Exception e )  {  throw  new IllegalArgumentException  (  String . format  ( ERROR_CREATE_INSTANCE , className ) , e ) ; } }    @ SuppressWarnings  ( "unchecked" ) private static  <  T > T accessField  (  Object instance ,  String fieldName )  {  try  {  Field  field =   instance . getClass  ( ) . getDeclaredField  ( fieldName ) ;   field . setAccessible  ( true ) ;  return  ( T )  field . get  ( instance ) ; }  catch (   Exception e )  {  throw  new IllegalArgumentException  (  String . format  ( ERROR_ACCESS_FIELD , fieldName ,  instance . getClass  ( ) ) , e ) ; } }    @ SuppressWarnings  (  { "rawtypes" , "unchecked" } ) private static  <  T > T invokeMethod  (  Object instance ,  String methodName ,  Object ...  parameters )  {   Class  [ ]  parameterTypes =  new Class  [  parameters . length ] ;  for (   int  i = 0 ;  i <  parameters . length ;  i ++ )  {    parameterTypes [ i ] =   parameters [ i ] . getClass  ( ) ; }  try  {  Method  method =   instance . getClass  ( ) . getMethod  ( methodName , parameterTypes ) ;   method . setAccessible  ( true ) ;  return  ( T )  method . invoke  ( instance , parameters ) ; }  catch (   Exception e )  {  throw  new IllegalArgumentException  (  String . format  ( ERROR_INVOKE_METHOD , methodName ,  instance . getClass  ( ) ,  Arrays . toString  ( parameters ) ) , e ) ; } } }