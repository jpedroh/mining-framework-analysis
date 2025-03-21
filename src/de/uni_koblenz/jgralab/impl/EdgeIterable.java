  package    de . uni_koblenz . jgralab . impl ;   import   java . util . ConcurrentModificationException ;  import   java . util . Iterator ;  import   java . util . NoSuchElementException ;  import    de . uni_koblenz . jgralab . Edge ;  import    de . uni_koblenz . jgralab . Graph ;   public class EdgeIterable  <  E  extends Edge >  implements   Iterable  < E >  {  class EdgeIterator  implements   Iterator  < E >  {   protected E  current = null ;   protected InternalGraph  graph = null ;   protected  Class  <  ? extends Edge >  ec ;   protected  long  edgeListVersion ;    @ SuppressWarnings  ( "unchecked" ) EdgeIterator  (  InternalGraph g ,   Class  <  ? extends Edge > ec )  {   graph = g ;    this . ec = ec ;   edgeListVersion =  g . getEdgeListVersion  ( ) ;   current =  ( E )  (   ec == null ?  graph . getFirstEdge  ( ) :  graph . getFirstEdge  ( ec ) ) ; }    @ SuppressWarnings  ( "unchecked" ) public E next  ( )  {  if  (  graph . isEdgeListModified  ( edgeListVersion ) )  {  throw  new ConcurrentModificationException  ( "The edge list of the graph has been modified - the iterator is not longer valid" ) ; }  if  (  current == null )  {  throw  new NoSuchElementException  ( ) ; }  E  result = current ;   current =  ( E )  (   
<<<<<<<
ec
=======
 ec == null
>>>>>>>
 
<<<<<<<
==
=======
&&
>>>>>>>
 
<<<<<<<
null
=======
 schemaEc == null
>>>>>>>
 ?  current . getNextEdge  ( ) : 
<<<<<<<
 current . getNextEdge  ( ec )
=======
  schemaEc == null ?  current . getNextEdge  ( ec ) :  current . getNextEdge  ( schemaEc )
>>>>>>>
 ) ;  return result ; }   public boolean hasNext  ( )  {  return  current != null ; }   public void remove  ( )  {  throw  new UnsupportedOperationException  ( "It is not allowed to remove edges during iteration." ) ; }   protected EdgeClass  schemaEc ;    @ SuppressWarnings  ( "unchecked" ) EdgeIterator  (  InternalGraph g ,  EdgeClass ec )  {   graph = g ;   schemaEc = ec ;   edgeListVersion =  g . getEdgeListVersion  ( ) ;   current =  ( E )  (   ec == null ?  graph . getFirstEdge  ( ) :  graph . getFirstEdge  ( ec ) ) ; } }   private EdgeIterator  iter ;   public EdgeIterable  (  Graph g )  {  this  ( g , 
<<<<<<<
null
=======
 (  Class  <  ? extends Edge > ) null
>>>>>>>
 ) ; }   public EdgeIterable  (  Graph g ,   Class  <  ? extends Edge > ec )  {  assert  g != null ;   iter =  new EdgeIterator  (  ( InternalGraph ) g , ec ) ; }   public  Iterator  < E > iterator  ( )  {  return iter ; } 
<<<<<<<
=======
  public EdgeIterable  (  Graph g ,  EdgeClass ec )  {  assert  g != null ;   iter =  new EdgeIterator  (  ( InternalGraph ) g , ec ) ; }
>>>>>>>
 }