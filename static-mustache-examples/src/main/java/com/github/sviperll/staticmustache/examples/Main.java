  package     com . github . sviperll . staticmustache . examples ;   import     com . github . sviperll . staticmustache . Renderable ;  import     com . github . sviperll . staticmustache . Renderer ;  import   java . io . IOException ;   public class Main  {   public static void main  (   String  [ ] args )  throws IOException  {  User  user =  new User  ( "Victor" , 29 , null ,  new String  [ ]  { "aaa" , "bbb" , "ccc" } ,  new  int  [ ]  { 1 , 2 , 3 , 4 , 5 } , true ) ;   Renderable  < Text >  renderable =  new RenderableUserAdapter  ( user ) ;  Renderer  renderer =  renderable . createRenderer  (  System . out ) ;   renderer . render  ( ) ;  User  user1 =  new User  ( "Victor" , 29 , null ,  new String  [ ]  { } ,  new  int  [ ]  { 1 , 2 , 3 , 4 , 5 } , true ) ;  Renderable  renderable1 =  new RenderableUserAdapter  ( user1 ) ;  Renderer  renderer1 =  renderable1 . createRenderer  (  System . out ) ;   renderer1 . render  ( ) ; } }