  package    de . uni_koblenz . jgralab . impl ;   import   java . util . ConcurrentModificationException ;  import   java . util . Iterator ;  import   java . util . NoSuchElementException ;  import    de . uni_koblenz . jgralab . Edge ;  import    de . uni_koblenz . jgralab . EdgeDirection ;  import    de . uni_koblenz . jgralab . Vertex ;   public class IncidenceIterable  <  E  extends Edge >  implements   Iterable  < E >  {   public IncidenceIterable  (  Vertex v )  {  this  ( v , 
<<<<<<<
null
=======
 ( EdgeClass ) null
>>>>>>>
 ,  EdgeDirection . INOUT ) ; }   public IncidenceIterable  (  Vertex v ,  EdgeDirection orientation )  {  this  ( v , 
<<<<<<<
null
=======
 ( EdgeClass ) null
>>>>>>>
 , orientation ) ; }   public IncidenceIterable  (  Vertex v ,   Class  <  ? extends Edge > ec )  {  this  ( v , ec ,  EdgeDirection . INOUT ) ; }   public IncidenceIterable  (  Vertex v ,   Class  <  ? extends Edge > ec ,  EdgeDirection orientation )  {  assert   v != null &&  v . isValid  ( ) ;   iter =  new IncidenceIterator  (  ( InternalVertex ) v , ec , orientation ) ; }  class IncidenceIterator  implements   Iterator  < E >  {   protected E  current = null ;   protected InternalVertex  vertex = null ;   protected  Class  <  ? extends Edge >  ec ;   protected EdgeDirection  dir ;   protected  long  incidenceListVersion ;    @ SuppressWarnings  ( "unchecked" ) public IncidenceIterator  (  InternalVertex vertex ,   Class  <  ? extends Edge > ec ,  EdgeDirection dir )  {    this . vertex = vertex ;    this . ec = ec ;    this . dir = dir ;   incidenceListVersion =  vertex . getIncidenceListVersion  ( ) ;   current =  ( E )  (   (  ec == null ) ?  vertex . getFirstIncidence  ( dir ) :  vertex . getFirstIncidence  ( ec , dir ) ) ; }    @ SuppressWarnings  ( "unchecked" ) public  @ Override E next  ( )  {  if  (  vertex . isIncidenceListModified  ( incidenceListVersion ) )  {  throw  new ConcurrentModificationException  ( "The incidence list of the vertex has been modified - the iterator is not longer valid" ) ; }  if  (  current == null )  {  throw  new NoSuchElementException  ( ) ; }  E  result = current ;   current =  ( E )  (  
<<<<<<<
 (  ec == null )
=======
  ec == null &&  schemaEc == null
>>>>>>>
 ?  current . getNextIncidence  ( dir ) : 
<<<<<<<
 current . getNextIncidence  ( ec , dir )
=======
  schemaEc == null ?  current . getNextIncidence  ( ec , dir ) :  current . getNextIncidence  ( schemaEc , dir )
>>>>>>>
 ) ;  return result ; }   public  @ Override boolean hasNext  ( )  {  if  (  vertex . isIncidenceListModified  ( incidenceListVersion ) )  {  throw  new ConcurrentModificationException  ( "The incidence list of the vertex has been modified - the iterator is not longer valid" ) ; }  return  current != null ; }   public  @ Override void remove  ( )  {  throw  new UnsupportedOperationException  ( "Cannot remove Edges using Iterator" ) ; }   protected EdgeClass  schemaEc ;    @ SuppressWarnings  ( "unchecked" ) public IncidenceIterator  (  InternalVertex vertex ,  EdgeClass ec ,  EdgeDirection dir )  {    this . vertex = vertex ;    this . schemaEc = ec ;    this . dir = dir ;   incidenceListVersion =  vertex . getIncidenceListVersion  ( ) ;   current =  ( E )  (   (  ec == null ) ?  vertex . getFirstIncidence  ( dir ) :  vertex . getFirstIncidence  ( ec , dir ) ) ; } }   private IncidenceIterator  iter = null ;   public  @ Override  Iterator  < E > iterator  ( )  {  return iter ; } 
<<<<<<<
=======
  public IncidenceIterable  (  Vertex v ,  EdgeClass ec )  {  this  ( v , ec ,  EdgeDirection . INOUT ) ; }
>>>>>>>
 
<<<<<<<
=======
  public IncidenceIterable  (  Vertex v ,  EdgeClass ec ,  EdgeDirection orientation )  {  assert   v != null &&  v . isValid  ( ) ;   iter =  new IncidenceIterator  (  ( InternalVertex ) v , ec , orientation ) ; }
>>>>>>>
 }