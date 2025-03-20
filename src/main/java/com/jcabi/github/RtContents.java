  package   com . jcabi . github ;   import    com . jcabi . aspects . Immutable ;  import    com . jcabi . aspects . Loggable ;  import    com . rexsl . test . Request ;  import     com . rexsl . test . response . JsonResponse ;  import     com . rexsl . test . response . RestResponse ;  import   java . io . IOException ;  import   java . net . HttpURLConnection ;  import   javax . json . Json ;  import   javax . json . JsonStructure ;  import    javax . validation . constraints . NotNull ;  import  lombok . EqualsAndHashCode ;    @ Immutable  @ Loggable  (  Loggable . DEBUG )  @ EqualsAndHashCode  (  of =  { "entry" , "request" , } ) public final class RtContents  implements  Contents  {   private final transient Request  entry ;   private final transient Repo  owner ;   private final transient Request  request ;   public RtContents  (   final Request req ,   final Repo repo )  {    this . entry = req ;    this . owner = repo ;   final Coordinates  coords =  repo . coordinates  ( ) ;    this . request =      
<<<<<<<
 req . uri  ( )
=======
req
>>>>>>>
 . 
<<<<<<<
path
=======
uri
>>>>>>>
  ( "/repos" ) . path  ( 
<<<<<<<
  repo . coordinates  ( ) . user  ( )
=======
"/repos"
>>>>>>>
 ) . path  (  
<<<<<<<
 repo . coordinates  ( )
=======
coords
>>>>>>>
 . 
<<<<<<<
repo
=======
user
>>>>>>>
  ( ) ) . path  ( 
<<<<<<<
"/contents"
=======
 coords . repo  ( )
>>>>>>>
 ) . back  ( ) ; }    @ Override public Repo repo  ( )  {  return  this . owner ; }    @ Override public Content readme  ( )  {  return  new RtReadme  (  this . entry ,  this . 
<<<<<<<
owner
=======
request
>>>>>>>
 ) ; }    @ Override public Commit remove  (    @ NotNull  (  message = "path is never NULL" ) final String path ,    @ NotNull  (  message = "message is never NULL" ) final String message ,    @ NotNull  (  message = "sha is never NULL" ) final String sha )  throws IOException  {   final JsonStructure  json =     Json . createObjectBuilder  ( ) . add  ( "message" , message ) . add  ( "sha" , sha ) . build  ( ) ;  return  new RtCommit  (  this . entry ,  this . owner ,                 this . request . method  (  Request . DELETE ) . uri  ( ) . path  ( path ) . back  ( ) . body  ( ) . set  ( json ) . back  ( ) . fetch  ( ) . as  (  RestResponse . class ) . assertStatus  (  HttpURLConnection . HTTP_OK ) . as  (  JsonResponse . class ) . json  ( ) . readObject  ( ) . getJsonObject  ( "commit" ) . getString  ( "sha" ) ) ; } }