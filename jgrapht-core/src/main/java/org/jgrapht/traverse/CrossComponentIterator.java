  package   org . jgrapht . traverse ;   import   org . jgrapht . Graph ;  import   org . jgrapht . Graphs ;  import    org . jgrapht . event . ConnectedComponentTraversalEvent ;  import   java . util . HashMap ;  import   java . util . Iterator ;  import   java . util . Map ;  import   java . util . NoSuchElementException ;   public abstract class CrossComponentIterator  <  V ,  E ,  D >  extends  AbstractGraphIterator  < V , E >  {   private static final  int  CCS_BEFORE_COMPONENT = 1 ;   private static final  int  CCS_WITHIN_COMPONENT = 2 ;   private static final  int  CCS_AFTER_COMPONENT = 3 ;   private final ConnectedComponentTraversalEvent  ccFinishedEvent =  new ConnectedComponentTraversalEvent  ( this ,  ConnectedComponentTraversalEvent . CONNECTED_COMPONENT_FINISHED ) ;   private final ConnectedComponentTraversalEvent  ccStartedEvent =  new ConnectedComponentTraversalEvent  ( this ,  ConnectedComponentTraversalEvent . CONNECTED_COMPONENT_STARTED ) ;   private  Map  < V , D >  seen =  new  HashMap  < >  ( ) ;   private  Iterator  < V >  entireGraphVertexIterator = null ;   private  Iterator  < V >  startVertexIterator = null ;   private V  startVertex ;   private  int  state = CCS_BEFORE_COMPONENT ;   public CrossComponentIterator  (   Graph  < V , E > g )  {  this  ( g , 
<<<<<<<
 ( V ) null
=======
 g . vertexSet  ( )
>>>>>>>
 ) ; }   public CrossComponentIterator  (   Graph  < V , E > g ,  V startVertex )  {  this  ( g ,   startVertex == null ? null :  Collections . singletonList  ( startVertex ) ) ; }   public CrossComponentIterator  (   Graph  < V , E > g ,   Iterable  < V > startVertices )  {  super  ( g ) ;    this . 
<<<<<<<
entireGraphVertexIterator
=======
crossComponentTraversal
>>>>>>>
 = 
<<<<<<<
  graph . vertexSet  ( ) . iterator  ( )
=======
true
>>>>>>>
 ; 
<<<<<<<
 if  (  startVertices == null )  {    this . crossComponentTraversal = true ; } else  {    this . crossComponentTraversal = false ;    this . startVertexIterator =  startVertices . iterator  ( ) ; }
=======
  startVertexIterator =   startVertices != null ?  startVertices . iterator  ( ) :   graph . vertexSet  ( ) . iterator  ( ) ;
>>>>>>>
 
<<<<<<<
  Iterator  < V >  it =  crossComponentTraversal ? entireGraphVertexIterator : startVertexIterator ;
=======
  startVertex =   startVertexIterator . hasNext  ( ) ?  startVertexIterator . next  ( ) : null ;
>>>>>>>
  if  ( 
<<<<<<<
 it . hasNext  ( )
=======
 !  graph . containsVertex  ( startVertex )
>>>>>>>
 )  {    this . startVertex =  it . next  ( ) ;  if  (  !  graph . containsVertex  ( startVertex ) )  {  throw  new IllegalArgumentException  ( "graph must contain the start vertex" ) ; } } else  { 
<<<<<<<
   this . startVertex = null ;
=======
 throw  new IllegalArgumentException  ( "graph must contain the start vertex" ) ;
>>>>>>>
 } }    @ Override public boolean hasNext  ( )  {  if  (  startVertex != null )  {   encounterStartVertex  ( ) ; }  if  (  isConnectedComponentExhausted  ( ) )  {  if  (  state == CCS_WITHIN_COMPONENT )  {   state = CCS_AFTER_COMPONENT ;  if  (  nListeners != 0 )  {   fireConnectedComponentFinished  ( ccFinishedEvent ) ; } } 
<<<<<<<
  Iterator  < V >  it =   isCrossComponentTraversal  ( ) ? entireGraphVertexIterator : startVertexIterator ;
=======
 if  (  isCrossComponentTraversal  ( ) )  {  while  (  startVertexIterator . hasNext  ( ) )  {  V  v =  startVertexIterator . next  ( ) ;  if  (  !  graph . containsVertex  ( v ) )  {  throw  new IllegalArgumentException  ( "graph must contain the start vertex" ) ; }  if  (  !  isSeenVertex  ( v ) )  {   encounterVertex  ( v , null ) ;   state = CCS_BEFORE_COMPONENT ;  return true ; } }  return false ; } else  {  return false ; }
>>>>>>>
  while  (   it != null &&  it . hasNext  ( ) )  {  V  v =  it . next  ( ) ;  if  (  !  graph . containsVertex  ( v ) )  {  throw  new IllegalArgumentException  ( "graph must contain the start vertex" ) ; }  if  (  !  isSeenVertex  ( v ) )  {   encounterVertex  ( v , null ) ;   state = CCS_BEFORE_COMPONENT ;  return true ; } }  return false ; } else  {  return true ; } }    @ Override public V next  ( )  {  if  (  startVertex != null )  {   encounterStartVertex  ( ) ; }  if  (  hasNext  ( ) )  {  if  (  state == CCS_BEFORE_COMPONENT )  {   state = CCS_WITHIN_COMPONENT ;  if  (  nListeners != 0 )  {   fireConnectedComponentStarted  ( ccStartedEvent ) ; } }  V  nextVertex =  provideNextVertex  ( ) ;  if  (  nListeners != 0 )  {   fireVertexTraversed  (  createVertexTraversalEvent  ( nextVertex ) ) ; }   addUnseenChildrenOf  ( nextVertex ) ;  return nextVertex ; } else  {  throw  new NoSuchElementException  ( ) ; } }   protected abstract boolean isConnectedComponentExhausted  ( ) ;   protected abstract void encounterVertex  (  V vertex ,  E edge ) ;   protected abstract V provideNextVertex  ( ) ;   protected D getSeenData  (  V vertex )  {  return  seen . get  ( vertex ) ; }   protected boolean isSeenVertex  (  V vertex )  {  return  seen . containsKey  ( vertex ) ; }   protected abstract void encounterVertexAgain  (  V vertex ,  E edge ) ;   protected D putSeenData  (  V vertex ,  D data )  {  return  seen . put  ( vertex , data ) ; }   protected void finishVertex  (  V vertex )  {  if  (  nListeners != 0 )  {   fireVertexFinished  (  createVertexTraversalEvent  ( vertex ) ) ; } }   private void addUnseenChildrenOf  (  V vertex )  {  for ( E edge :  graph . outgoingEdgesOf  ( vertex ) )  {  if  (  nListeners != 0 )  {   fireEdgeTraversed  (  createEdgeTraversalEvent  ( edge ) ) ; }  V  oppositeV =  Graphs . getOppositeVertex  ( graph , edge , vertex ) ;  if  (  isSeenVertex  ( oppositeV ) )  {   encounterVertexAgain  ( oppositeV , edge ) ; } else  {   encounterVertex  ( oppositeV , edge ) ; } } }   private void encounterStartVertex  ( )  {   encounterVertex  ( startVertex , null ) ;   startVertex = null ; } }