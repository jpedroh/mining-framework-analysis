  package    org . jgrapht . alg . shortestpath ;   import  java . util .  * ;  import  org . jgrapht .  * ;  import    org . jgrapht . alg . util .  * ;   public class BhandariKDisjointShortestPaths  <  V ,  E >  extends  BaseKDisjointShortestPathsAlgorithm  < V , E >  {   public BhandariKDisjointShortestPaths  (   Graph  < V , E > graph )  {  super  ( graph ) ; }    @ Override protected void prepare  (   List  < E > previousPath )  {  V  source ,  target ;  E  reversedEdge ;  for ( E originalEdge : previousPath )  {   source =  workingGraph . getEdgeSource  ( originalEdge ) ;   target =  workingGraph . getEdgeTarget  ( originalEdge ) ;   workingGraph . removeEdge  ( originalEdge ) ;   reversedEdge =  workingGraph . addEdge  ( target , source ) ;  if  (  reversedEdge != null )  {   workingGraph . setEdgeWeight  ( reversedEdge ,  -  workingGraph . getEdgeWeight  ( originalEdge ) ) ; } } }    @ Override protected  GraphPath  < V , E > calculateShortestPath  (  V startVertex ,  V endVertex )  {  return   new  BellmanFordShortestPath  < >  (  this . workingGraph ) . getPath  ( startVertex , endVertex ) ; } 
<<<<<<<
=======
  private void findOverlappingEdges  ( )  {   Map  <  UnorderedPair  < V , V > , Integer >  edgeOccurrenceCount =  new  HashMap  < >  ( ) ;  for (  List  < E > path : pathList )  {  for ( E e : path )  {  V  v =   this . workingGraph . getEdgeSource  ( e ) ;  V  u =   this . workingGraph . getEdgeTarget  ( e ) ;   UnorderedPair  < V , V >  edgePair =  new  UnorderedPair  < >  ( v , u ) ;  if  (  edgeOccurrenceCount . containsKey  ( edgePair ) )  {   edgeOccurrenceCount . put  ( edgePair , 2 ) ; } else  {   edgeOccurrenceCount . put  ( edgePair , 1 ) ; } } }    this . overlappingEdges =     pathList . stream  ( ) . flatMap  (  List :: stream ) . filter  (  e ->   edgeOccurrenceCount . get  (  new  UnorderedPair  < >  (   this . workingGraph . getEdgeSource  ( e ) ,   this . workingGraph . getEdgeTarget  ( e ) ) ) > 1 ) . collect  (  Collectors . toSet  ( ) ) ; }
>>>>>>>
 }