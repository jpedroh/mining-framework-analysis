  package    com . jcabi . github . mock ;   import    com . jcabi . aspects . Immutable ;  import    com . jcabi . aspects . Loggable ;  import    com . jcabi . github . Content ;  import    com . jcabi . github . Contents ;  import    com . jcabi . github . Coordinates ;  import    com . jcabi . github . Repo ;  import    com . jcabi . github . RepoCommit ;  import   java . io . IOException ;  import   javax . json . JsonObject ;  import  lombok . EqualsAndHashCode ;  import  lombok . ToString ;  import     org . apache . commons . lang3 . RandomStringUtils ;  import   org . xembly . Directives ;  import    javax . validation . constraints . NotNull ;    @ Immutable  @ Loggable  (  Loggable . DEBUG )  @ ToString  @ EqualsAndHashCode  (  of =  { "storage" , "self" , "coords" } )  @ SuppressWarnings  ( "PMD.AvoidDuplicateLiterals" ) public final class MkContents  implements  Contents  {   private final transient MkStorage  storage ;   private final transient String  self ;   private final transient Coordinates  coords ;   public MkContents  (   final  @ NotNull  (  message = "stg can't be NULL" ) MkStorage stg ,   final  @ NotNull  (  message = "login can't be NULL" ) String login ,   final  @ NotNull  (  message = "rep can't be NULL" ) Coordinates rep )  throws IOException  {    this . storage = stg ;    this . self = login ;    this . coords = rep ;    this . storage . apply  (      new Directives  ( ) . xpath  (  String . format  ( "/github/repos/repo[@coords='%s']" ,  this . coords ) ) . addIf  ( "contents" ) . up  ( ) . addIf  ( "commits" ) ) ; }    @ Override public  @ NotNull  (  message = "Repo is never NULL" ) Repo repo  ( )  {  return  new MkRepo  (  this . storage ,  this . self ,  this . coords ) ; }    @ Override public  @ NotNull  (  message = "the content is never NULL" ) Content readme  ( )  throws IOException  {  return  new MkContent  (  this . storage ,  this . self ,  this . coords , "README.md" ) ; }    @ Override public  @ NotNull  (  message = "content is never NULL" ) Content readme  (   final  @ NotNull  (  message = "branch can't be NULL" ) String branch )  {  throw  new UnsupportedOperationException  ( "Readme not yet implemented." ) ; }    @ Override public  @ NotNull  (  message = "created content is never NULL" ) Content create  (   final  @ NotNull  (  message = "json can't be NULL" ) JsonObject json )  throws IOException  {    this . storage . lock  ( ) ;   final String  path =  json . getString  ( "path" ) ;  try  {    this . storage . apply  (                               new Directives  ( ) . xpath  (  this . xpath  ( ) ) . add  ( "content" ) . add  ( "name" ) . set  ( path ) . up  ( ) . add  ( "path" ) . set  ( path ) . up  ( ) . add  ( "content" ) . set  (  json . getString  ( "content" ) ) . up  ( ) . add  ( "type" ) . set  ( "file" ) . up  ( ) . add  ( "encoding" ) . set  ( "base64" ) . up  ( ) . add  ( "sha" ) . set  (  fakeSha  ( ) ) . up  ( ) . add  ( "url" ) . set  ( "http://localhost/1" ) . up  ( ) . add  ( "git_url" ) . set  ( "http://localhost/2" ) . up  ( ) . add  ( "html_url" ) . set  ( "http://localhost/3" ) . up  ( ) ) ;   this . commit  ( json ) ; }  finally  {    this . storage . unlock  ( ) ; }  return  new MkContent  (  this . storage ,  this . self ,  this . coords , path ) ; }    @ Override public  @ NotNull  (  message = "retrieved content is never NULL" ) Content get  (   final  @ NotNull  (  message = "path can't be NULL" ) String path ,   final  @ NotNull  (  message = "ref can't be NULL" ) String ref )  throws IOException  {  return  new MkContent  (  this . storage ,  this . self ,  this . coords , path ) ; }    @ Override public  @ NotNull  (  message = "updated commit is never NULL" ) RepoCommit update  (   final  @ NotNull  (  message = "path cannot be NULL" ) String path ,   final  @ NotNull  (  message = "json should not be NULL" ) JsonObject json )  throws IOException  {  try  {    new JsonPatch  (  this . storage ) . patch  ( path , json ) ;  return  this . commit  ( json ) ; }  finally  {    this . storage . unlock  ( ) ; } }   private  @ NotNull  (  message = "Xpath is never NULL" ) String xpath  ( )  {  return  String . format  ( "/github/repos/repo[@coords='%s']/contents" ,  this . coords ) ; }   private  @ NotNull  (  message = "commit xpath is never NULL" ) String commitXpath  ( )  {  return  String . format  ( "/github/repos/repo[@coords='%s']/commits" ,  this . coords ) ; }   private  @ NotNull  (  message = "MkRepoCommit is never NULL" ) MkRepoCommit commit  (   final  @ NotNull  (  message = "json can't be NULL" ) JsonObject json )  throws IOException  {   final String  sha =  fakeSha  ( ) ;   final Directives  commit =                new Directives  ( ) . xpath  (  this . commitXpath  ( ) ) . add  ( "commit" ) . add  ( "sha" ) . set  ( sha ) . up  ( ) . add  ( "url" ) . set  ( "http://localhost/4" ) . up  ( ) . add  ( "html_url" ) . set  ( "http://localhost/5" ) . up  ( ) . add  ( "message" ) . set  (  json . getString  ( "message" ) ) . up  ( ) ;  if  (  json . containsKey  ( "committer" ) )  {   final JsonObject  committer =  json . getJsonObject  ( "committer" ) ;         commit . add  ( "committer" ) . add  ( "email" ) . set  (  committer . getString  ( "email" ) ) . up  ( ) . add  ( "name" ) . set  (  committer . getString  ( "name" ) ) . up  ( ) ; }  if  (  json . containsKey  ( "author" ) )  {   final JsonObject  author =  json . getJsonObject  ( "author" ) ;         commit . add  ( "author" ) . add  ( "email" ) . set  (  author . getString  ( "email" ) ) . up  ( ) . add  ( "name" ) . set  (  author . getString  ( "name" ) ) . up  ( ) ; }    this . storage . apply  ( commit ) ;  return  new MkRepoCommit  (  this . storage ,  this . repo  ( ) , sha ) ; }   private static  @ NotNull  (  message = "fake sha can't be NULL" ) String fakeSha  ( )  {  return  RandomStringUtils . random  ( 40 , "0123456789abcdef" ) ; }    @ Override  @ NotNull  (  message = "commit is never NULL" ) public RepoCommit remove  (    @ NotNull  (  message = "content should not be NULL" ) final JsonObject content )  throws IOException  {  throw  new UnsupportedOperationException  ( "Remove not yet implemented." ) ; } }