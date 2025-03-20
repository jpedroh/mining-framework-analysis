  package    org . cts . parser . proj ;   import   java . io . BufferedReader ;  import   java . io . IOException ;  import   java . io . InputStream ;  import   java . io . InputStreamReader ;  import   java . util . HashMap ;  import   java . util . Map ;  import    org . cts . registry . Registry ;   public class ProjParser  {   private final Registry  registry ;   public ProjParser  (  Registry registry )  {    this . registry = registry ; }   public  Map  < String , String > readParameters  (  String crsCode ,  String regexPattern )  throws IOException  {  InputStream  inStr =   Registry . class . getResourceAsStream  (  registry . getRegistryName  ( ) ) ;  if  (  inStr == null )  {  throw  new IllegalStateException  (  "Unable to access CRS file: " +  registry . getRegistryName  ( ) ) ; }  BufferedReader  reader =  new BufferedReader  (  new InputStreamReader  ( inStr ) ) ;   Map  < String , String >  args ;  try  {   args =  readRegistry  ( reader , crsCode , regexPattern ) ; }  finally  {   reader . close  ( ) ; }  return args ; }   private  Map  < String , String > readRegistry  (  BufferedReader br ,  String nameOfCRS ,  String regex )  throws IOException  {  String  line ;  String  crsName = null ;  while  (  null !=  (  line =  br . readLine  ( ) ) )  {  if  (  line . startsWith  ( "#" ) )  {   crsName =   line . substring  ( 1 ) . trim  ( ) ; } else  if  (   line . startsWith  ( "<" ) &&  line . endsWith  ( ">" ) )  {   String  [ ]  tokens =  line . split  ( regex ) ;   Map  < String , String >  v =  new  HashMap  < String , String >  ( ) ;  String  crsID = null ;  boolean  crsFounded = true ;  for ( String token : tokens )  {  if  (    token . startsWith  ( "<" ) &&  token . endsWith  ( ">" ) &&   token . length  ( ) > 2 )  {   crsID =  token . substring  ( 1 ,   token . length  ( ) - 1 ) ;  if  (  !   crsID . toLowerCase  ( ) . equals  (  nameOfCRS . toLowerCase  ( ) ) )  {   
<<<<<<<
crsName
=======
crsFounded
>>>>>>>
 = 
<<<<<<<
null
=======
false
>>>>>>>
 ;  break ; } } else  if  (  token . equals  ( "<>" ) )  {  break ; } else  {   String  [ ]  keyValue =  token . split  ( "=" ) ;  if  (   keyValue . length == 2 )  {  String  key =  formatKey  (  keyValue [ 0 ] ) ;   ProjKeyParameters . checkUnsupported  ( key ) ;   v . put  ( key ,  keyValue [ 1 ] ) ; } else  {  String  key =  formatKey  ( token ) ;   ProjKeyParameters . checkUnsupported  ( key ) ;   v . put  ( key , null ) ; } } }  if  ( 
<<<<<<<
 nameOfCRS . equals  ( crsID )
=======
crsFounded
>>>>>>>
 )  {  if  (   !  v . containsKey  (  ProjKeyParameters . title ) &&  crsName != null )  {   v . put  (  ProjKeyParameters . title , crsName ) ; }  return v ; } } }  return null ; }   private static String formatKey  (  String key )  {  String  formatKey = key ;  if  (  key . startsWith  ( "+" ) )  {   formatKey =  key . substring  ( 1 ) ; }  return formatKey ; } }