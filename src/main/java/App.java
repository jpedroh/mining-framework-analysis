   import    com . google . gson . JsonObject ;  import    com . google . gson . JsonParser ;  import    com . tumblr . jumblr . JumblrClient ;  import     com . tumblr . jumblr . types . Blog ;  import   java . io . BufferedReader ;  import   java . io . FileNotFoundException ;  import   java . io . FileReader ;  import   java . io . IOException ;  import   java . util . List ;   public class App  {   public static void main  (   String  [ ] args )  throws FileNotFoundException , IOException , InstantiationException , IllegalAccessException  {  FileReader  fr =  new FileReader  ( "credentials.json" ) ;  BufferedReader  br =  new BufferedReader  ( fr ) ;  StringBuilder  json =  new StringBuilder  ( ) ;  try  {  while  (  br . ready  ( ) )  {   json . append  (  br . readLine  ( ) ) ; } }  finally  {   br . close  ( ) ; }   br . close  ( ) ;  JsonParser  parser =  new JsonParser  ( ) ;  JsonObject  obj =  ( JsonObject )  parser . parse  (  json . toString  ( ) ) ;  JumblrClient  client =  new JumblrClient  (   obj . getAsJsonPrimitive  ( "consumer_key" ) . getAsString  ( ) ,   obj . getAsJsonPrimitive  ( "consumer_secret" ) . getAsString  ( ) ) ;  boolean  b =  client . authenticate  ( ) ;  if  (  ! b )  {    System . out . println  ( "Failed to authenticate." ) ; }  User  user =  client . user  ( ) ;    System . out . printf  ( "User %s has these blogs:%n" ,  user . getName  ( ) ) ;  for ( Blog blog :  user . getBlogs  ( ) )  {    System . out . printf  ( "\t%s (%s)%n" ,  blog . getName  ( ) ,  blog . getTitle  ( ) ) ; } 
<<<<<<<
=======
   System . out . println  ( "They are following these blogs:" ) ;
>>>>>>>
   List  < Blog > 
<<<<<<<
 posts =  client . blogPosts  ( "seejohnrun" )
=======
 blogs =  client . userFollowing  ( )
>>>>>>>
 ;  for ( 
<<<<<<<
Post
=======
Blog
>>>>>>>
 
<<<<<<<
post
=======
blog
>>>>>>>
 : 
<<<<<<<
posts
=======
blogs
>>>>>>>
 )  {    System . out . 
<<<<<<<
println
=======
printf
>>>>>>>
  ( "\t%s (%s)%n" ,  blog . getName  ( ) ,  
<<<<<<<
post
=======
blog
>>>>>>>
 . 
<<<<<<<
getShortUrl
=======
getTitle
>>>>>>>
  ( ) ) ; } } }