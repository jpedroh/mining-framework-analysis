  package   org . jgrapht . graph ;   import  java . util .  * ;  import  org . jgrapht .  * ;  import   org . jgrapht . util .  * ;  import    org . jgrapht . util . PrefetchIterator .  * ;  class MaskEdgeSet  <  V ,  E >  extends  AbstractSet  < E >  {   private  Set  < E >  edgeSet ;   private  Graph  < V , E >  graph ;   private  MaskFunctor  < V , E >  mask ;   private transient  TypeUtil  < E >  edgeTypeDecl = null ;   public MaskEdgeSet  (   Graph  < V , E > graph ,   Set  < E > edgeSet ,   MaskFunctor  < V , E > mask )  {    this . graph = graph ;    this . edgeSet = edgeSet ;    this . mask = mask ; }    @ Override public boolean contains  (  Object o )  {  E  e =  ( E ) o ;  return     edgeSet . contains  ( e ) &&  !  mask . isEdgeMasked  ( e ) &&  !  mask . isVertexMasked  (  graph . getEdgeSource  ( e ) ) &&  !  mask . isVertexMasked  (  graph . getEdgeTarget  ( e ) ) ; }    @ Override public  Iterator  < E > iterator  ( )  {  return  new  PrefetchIterator  < E >  (  new MaskEdgeSetNextElementFunctor  ( ) ) ; }    @ Override public  int size  ( )  { 
<<<<<<<
 if  (   this . size ==  - 1 )  {    this . size = 0 ;  for (   Iterator  < E >  iter =  iterator  ( ) ;  iter . hasNext  ( ) ;  iter . next  ( ) )  {    this . size ++ ; } }
=======
>>>>>>>
  return  (  int )    edgeSet . stream  ( ) . filter  (  e ->  contains  ( e ) ) . count  ( ) ; }   private class MaskEdgeSetNextElementFunctor  implements   NextElementFunctor  < E >  {   private  Iterator  < E >  iter ;   public MaskEdgeSetNextElementFunctor  ( )  {    this . iter =    MaskEdgeSet . this . edgeSet . iterator  ( ) ; }    @ Override public E nextElement  ( )  throws NoSuchElementException  {  E  edge =   this . iter . next  ( ) ;  while  (  isMasked  ( edge ) )  {   edge =   this . iter . next  ( ) ; }  return edge ; }   private boolean isMasked  (  E edge )  {  return      MaskEdgeSet . this . mask . isEdgeMasked  ( edge ) ||    MaskEdgeSet . this . mask . isVertexMasked  (    MaskEdgeSet . this . graph . getEdgeSource  ( edge ) ) ||    MaskEdgeSet . this . mask . isVertexMasked  (    MaskEdgeSet . this . graph . getEdgeTarget  ( edge ) ) ; } } }