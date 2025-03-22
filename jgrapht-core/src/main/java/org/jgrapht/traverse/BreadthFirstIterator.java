  package   org . jgrapht . traverse ;   import   org . jgrapht . Graph ;  import   java . util . ArrayDeque ;  import   java . util . Deque ;   public class BreadthFirstIterator  <  V ,  E >  extends  CrossComponentIterator  < V , E , Object >  {   private  Deque  < V >  queue =  new  ArrayDeque  < >  ( ) ;   public BreadthFirstIterator  (   Graph  < V , E > g )  {  this  ( g , 
<<<<<<<
 ( V ) null
=======
 g . vertexSet  ( )
>>>>>>>
 ) ; }   public BreadthFirstIterator  (   Graph  < V , E > g ,  V startVertex )  {  super  ( g , startVertex ) ; }   public BreadthFirstIterator  (   Graph  < V , E > g ,   Iterable  < V > startVertices )  {  super  ( g , startVertices ) ; }    @ Override protected boolean isConnectedComponentExhausted  ( )  {  return  queue . isEmpty  ( ) ; }    @ Override protected void encounterVertex  (  V vertex ,  E edge )  {   putSeenData  ( vertex , null ) ;   queue . add  ( vertex ) ; }    @ Override protected void encounterVertexAgain  (  V vertex ,  E edge )  { }    @ Override protected V provideNextVertex  ( )  {  return  queue . removeFirst  ( ) ; } }