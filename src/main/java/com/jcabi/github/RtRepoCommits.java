  package   com . jcabi . github ;   import    com . jcabi . aspects . Immutable ;  import    com . jcabi . aspects . Loggable ;  import   java . io . IOException ;  import   javax . json . JsonObject ;  import  lombok . EqualsAndHashCode ;  import    com . jcabi . http . Request ;    @ Immutable  @ Loggable  (  Loggable . DEBUG )  @ EqualsAndHashCode  (  of = "request" ) final class RtRepoCommits  implements  RepoCommits  {   private final transient Request  request ;   private final transient Request  entry ;   private final transient Github  github ;   private final transient Repo  repo ; 
<<<<<<<
 RtRepoCommits  (   final Request req ,   final Coordinates repo )  {    this . entry = req ;    this . request =       req . uri  ( ) . path  ( "/repos" ) . path  (  repo . user  ( ) ) . path  (  repo . repo  ( ) ) . path  ( "/commits" ) . back  ( ) ;    this . github =  new RtGithub  (  this . request ) ;    this . repo =  new RtRepo  (  this . github ,  this . request , repo ) ; }
=======
>>>>>>>
    @ Override public  Iterable  < Commit > iterate  ( )  {  return  new  RtPagination  < Commit >  (  this . request ,  new   RtPagination . Mapping  < Commit >  ( )  {    @ Override public Commit map  (   final JsonObject object )  {  return  get  (  object . getString  ( "sha" ) ) ; } } ) ; }    @ Override public Commit get  (   final String sha )  {  return  new RtCommit  (  this . entry ,  this . 
<<<<<<<
repo
=======
owner
>>>>>>>
 , sha ) ; }    @ Override public String toString  ( )  {  return     this . request . uri  ( ) . get  ( ) . toString  ( ) ; }    @ Override public JsonObject json  ( )  throws IOException  {  return   new RtJson  (  this . request ) . fetch  ( ) ; }   private final transient Repo  owner ;  RtRepoCommits  (   final Request req ,   final Repo repo )  {    this . entry = req ;    this . owner = repo ;    this . request =       req . uri  ( ) . path  ( "/repos" ) . path  (   repo . coordinates  ( ) . user  ( ) ) . path  (   repo . coordinates  ( ) . repo  ( ) ) . path  ( "/commits" ) . back  ( ) ; } }