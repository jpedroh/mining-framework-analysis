  package   com . jcabi . github ;   import    com . jcabi . aspects . Immutable ;  import    com . jcabi . aspects . Loggable ;  import    com . rexsl . test . Request ;  import   javax . json . JsonObject ;  import  lombok . EqualsAndHashCode ;  import     com . rexsl . test . response . JsonResponse ;  import     com . rexsl . test . response . RestResponse ;  import   java . io . IOException ;  import   java . net . HttpURLConnection ;  import   javax . json . Json ;  import   javax . json . JsonStructure ;    @ Immutable  @ Loggable  (  Loggable . DEBUG )  @ EqualsAndHashCode  (  of =  { "entry" , "owner" } ) public final class RtReleases  implements  Releases  {   private final transient Request  entry ;   private final transient Request  request ;   private final transient Repo  owner ;   public RtReleases  (   final Request req ,   final Repo repo )  {    this . entry = req ;    this . owner = repo ;   final Coordinates  coords =  repo . coordinates  ( ) ;    this . request =        this . entry . uri  ( ) . path  ( "/repos" ) . path  (  
<<<<<<<
 repo . coordinates  ( )
=======
coords
>>>>>>>
 . user  ( ) ) . path  (  
<<<<<<<
 repo . coordinates  ( )
=======
coords
>>>>>>>
 . repo  ( ) ) . path  ( "/releases" ) . back  ( ) ; }    @ Override public Repo repo  ( )  {  return  this . owner ; }    @ Override public  Iterable  < Release > iterate  ( )  {  return  new  RtPagination  < Release >  (  this . request ,  new   RtPagination . Mapping  < Release >  ( )  {    @ Override public Release map  (   final JsonObject object )  {  return  new RtRelease  (   RtReleases . this . entry ,    RtReleases . this . owner . coordinates  ( ) ,  object . getInt  ( "id" ) ) ; } } ) ; }    @ Override public Release get  (   final  int number )  {  return  new RtRelease  (  this . entry ,   this . owner . coordinates  ( ) , number ) ; }    @ Override public Release create  (   final String tag )  throws IOException  {   final JsonStructure  json =    Json . createObjectBuilder  ( ) . add  ( "tag_name" , tag ) . build  ( ) ;  return  this . get  (             this . request . method  (  Request . POST ) . body  ( ) . set  ( json ) . back  ( ) . fetch  ( ) . as  (  RestResponse . class ) . assertStatus  (  HttpURLConnection . HTTP_CREATED ) . as  (  JsonResponse . class ) . json  ( ) . readObject  ( ) . getInt  ( "id" ) ) ; } }