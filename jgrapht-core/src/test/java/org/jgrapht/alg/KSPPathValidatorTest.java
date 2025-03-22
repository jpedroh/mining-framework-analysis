  package   org . jgrapht . alg ;   import  java . util .  * ;  import  org . jgrapht .  * ;  import   org . jgrapht . generate .  * ;  import   org . jgrapht . graph .  * ;  import  junit . framework .  * ;   public class KSPPathValidatorTest  extends TestCase  {   public void testBlockAll  ( )  {   int  size = 5 ;   SimpleGraph  < String , DefaultEdge >  clique =  buildCliqueGraph  ( size ) ;  for (   int  i = 0 ;  i < size ;  i ++ )  {   KShortestPaths  < String , DefaultEdge >  ksp =  new  KShortestPaths  < String , DefaultEdge >  ( clique ,  String . valueOf  ( i ) , 1 ,  Integer . MAX_VALUE ,  new  PathValidator  < String , DefaultEdge >  ( )  {    @ Override public boolean isValidPath  (   GraphPath  < String , DefaultEdge > 
<<<<<<<
partialPath
=======
prevPath
>>>>>>>
 ,  DefaultEdge edge )  {  return false ; } } ) ;  for (   int  j = 0 ;  j < size ;  j ++ )  {  if  (  j == i )  {  continue ; }   List  <  GraphPath  < String , DefaultEdge > >  paths =  ksp . getPaths  (  String . valueOf  ( j ) ) ;   assertNull  ( paths ) ; } } }   public void testAllowAll  ( )  {   int  size = 5 ;   SimpleGraph  < String , DefaultEdge >  clique =  buildCliqueGraph  ( size ) ;  for (   int  i = 0 ;  i < size ;  i ++ )  {   KShortestPaths  < String , DefaultEdge >  ksp =  new  KShortestPaths  < String , DefaultEdge >  ( clique ,  String . valueOf  ( i ) , 30 ,  Integer . MAX_VALUE ,  new  PathValidator  < String , DefaultEdge >  ( )  {    @ Override public boolean isValidPath  (   GraphPath  < String , DefaultEdge > 
<<<<<<<
partialPath
=======
prevPath
>>>>>>>
 ,  DefaultEdge edge )  {  return true ; } } ) ;  for (   int  j = 0 ;  j < size ;  j ++ )  {  if  (  j == i )  {  continue ; }   List  <  GraphPath  < String , DefaultEdge > >  paths =  ksp . getPaths  (  String . valueOf  ( j ) ) ;   assertNotNull  ( paths ) ;   assertEquals  ( 16 ,  paths . size  ( ) ) ; } } }   public void testRing  ( )  {   int  size = 10 ;   SimpleGraph  < Integer , DefaultEdge >  ring =  buildRingGraph  ( size ) ;  for (   int  i = 0 ;  i < size ;  i ++ )  {   KShortestPaths  < Integer , DefaultEdge >  ksp =  new  KShortestPaths  < Integer , DefaultEdge >  ( ring , i , 2 ,  Integer . MAX_VALUE ,  new  PathValidator  < Integer , DefaultEdge >  ( )  {    @ Override public boolean isValidPath  (   GraphPath  < Integer , DefaultEdge > 
<<<<<<<
partialPath
=======
prevPath
>>>>>>>
 ,  DefaultEdge edge )  {  if  (  
<<<<<<<
partialPath
=======
prevPath
>>>>>>>
 == null )  {  return true ; }  return   Math . abs  (   
<<<<<<<
partialPath
=======
prevPath
>>>>>>>
 . getEndVertex  ( ) -  Graphs . getOppositeVertex  ( ring , edge ,  
<<<<<<<
partialPath
=======
prevPath
>>>>>>>
 . getEndVertex  ( ) ) ) == 1 ; } } ) ;  for (   int  j = 0 ;  j < size ;  j ++ )  {  if  (  j == i )  {  continue ; }   List  <  GraphPath  < Integer , DefaultEdge > >  paths =  ksp . getPaths  ( j ) ;   assertNotNull  ( paths ) ;   assertEquals  ( 1 ,  paths . size  ( ) ) ; } } }   public void testDisconnected  ( )  {   int  cliqueSize = 5 ;   SimpleGraph  < Integer , DefaultEdge >  graph =  buildGraphForTestDisconnected  ( cliqueSize ) ;  for (   int  i = 0 ;  i <   graph . vertexSet  ( ) . size  ( ) ;  i ++ )  {   KShortestPaths  < Integer , DefaultEdge >  ksp =  new  KShortestPaths  < Integer , DefaultEdge >  ( graph , i , 100 ,  Integer . MAX_VALUE ,  new  PathValidator  < Integer , DefaultEdge >  ( )  {    @ Override public boolean isValidPath  (   GraphPath  < Integer , DefaultEdge > 
<<<<<<<
partialPath
=======
prevPath
>>>>>>>
 ,  DefaultEdge edge )  {  DefaultEdge  connectingEdge =  graph . getEdge  (  cliqueSize - 1 , cliqueSize ) ;  return  connectingEdge != edge ; } } ) ;  for (   int  j = 0 ;  j <   graph . vertexSet  ( ) . size  ( ) ;  j ++ )  {  if  (  j == i )  {  continue ; }   List  <  GraphPath  < Integer , DefaultEdge > >  paths =  ksp . getPaths  ( j ) ;  if  (   (   i < cliqueSize &&  j < cliqueSize ) ||  (   i >= cliqueSize &&  j >= cliqueSize ) )  {   assertNotNull  ( paths ) ;   assertTrue  (   paths . size  ( ) > 0 ) ; } else  {   assertNull  ( paths ) ; } } } }   public void testGraphPath  ( )  {   SimpleDirectedGraph  < Integer , DefaultEdge >  line =  buildLineGraph  ( 10 ) ;   KShortestPaths  < Integer , DefaultEdge >  ksp =  new  KShortestPaths  < Integer , DefaultEdge >  ( line , 0 ,  Integer . MAX_VALUE ,  new  PathValidator  < Integer , DefaultEdge >  ( )  {   int  index = 0 ;    @ Override public boolean isValidPath  (   GraphPath  < Integer , DefaultEdge > partialPath ,  DefaultEdge edge )  {   assertNotNull  ( edge ) ;   assertEquals  (  line . getEdgeSource  ( edge ) , index ,  index + 1 ) ;   List  < Integer >  expectedVertices =  new  ArrayList  < >  ( ) ;  if  (  index > 0 )  {  for (   int  i = 0 ;  i <  index + 1 ;  i ++ )  {   expectedVertices . add  ( i ) ; } }   List  < DefaultEdge >  expectedEdges =  new  ArrayList  < >  ( ) ;  for (   int  i = 0 ;  i < index ;  i ++ )  {   expectedEdges . add  (  line . getEdge  ( i ,  i + 1 ) ) ; }   assertNotNull  ( partialPath ) ;   assertEquals  ( index ,   partialPath . getEdgeList  ( ) . size  ( ) ) ;   assertEquals  ( expectedEdges ,  partialPath . getEdgeList  ( ) ) ;   assertEquals  ( index ,   partialPath . getEndVertex  ( ) . intValue  ( ) ) ;   assertEquals  ( line ,  partialPath . getGraph  ( ) ) ;   assertEquals  ( index ,  partialPath . getLength  ( ) ) ;   assertEquals  ( 0 ,   partialPath . getStartVertex  ( ) . intValue  ( ) ) ;   assertEquals  (  (   index == 0 ? 0 :  index + 1 ) ,   partialPath . getVertexList  ( ) . size  ( ) ) ;   assertEquals  ( expectedVertices ,  partialPath . getVertexList  ( ) ) ;   assertEquals  (  (  double ) index ,  partialPath . getWeight  ( ) ) ;   index ++ ;  return true ; } } ) ;   ksp . getPaths  ( 9 ) ; }   private  SimpleGraph  < String , DefaultEdge > buildCliqueGraph  (   int size )  {   SimpleGraph  < String , DefaultEdge >  clique =  new  SimpleGraph  < >  (  DefaultEdge . class ) ;   CompleteGraphGenerator  < String , DefaultEdge >  graphGenerator =  new  CompleteGraphGenerator  < >  ( size ) ;   graphGenerator . generateGraph  ( clique ,  new  VertexFactory  < String >  ( )  {   private  int  index = 0 ;    @ Override public String createVertex  ( )  {  return  String . valueOf  (  index ++ ) ; } } , null ) ;  return clique ; }   private  SimpleGraph  < Integer , DefaultEdge > buildGraphForTestDisconnected  (   int size )  {   SimpleGraph  < Integer , DefaultEdge >  graph =  new  SimpleGraph  < >  (  DefaultEdge . class ) ;   VertexFactory  < Integer >  vertexFactory =  new IntegerVertexFactory  ( ) ;   CompleteGraphGenerator  < Integer , DefaultEdge >  completeGraphGenerator =  new  CompleteGraphGenerator  < >  ( size ) ;   SimpleGraph  < Integer , DefaultEdge >  east =  new  SimpleGraph  < >  (  DefaultEdge . class ) ;   completeGraphGenerator . generateGraph  ( east , vertexFactory , null ) ;   SimpleGraph  < Integer , DefaultEdge >  west =  new  SimpleGraph  < >  (  DefaultEdge . class ) ;   completeGraphGenerator . generateGraph  ( west , vertexFactory , null ) ;   Graphs . addGraph  ( graph , east ) ;   Graphs . addGraph  ( graph , west ) ;   graph . addEdge  (  size - 1 , size ) ;  return graph ; }   private  SimpleGraph  < Integer , DefaultEdge > buildRingGraph  (   int size )  {   SimpleGraph  < Integer , DefaultEdge >  clique =  new  SimpleGraph  < >  (  DefaultEdge . class ) ;   RingGraphGenerator  < Integer , DefaultEdge >  graphGenerator =  new  RingGraphGenerator  < >  ( size ) ;   graphGenerator . generateGraph  ( clique ,  new IntegerVertexFactory  ( ) , null ) ;  return clique ; }   private  SimpleDirectedGraph  < Integer , DefaultEdge > buildLineGraph  (   int size )  {   SimpleDirectedGraph  < Integer , DefaultEdge >  line =  new  SimpleDirectedGraph  < >  (  DefaultEdge . class ) ;   LinearGraphGenerator  < Integer , DefaultEdge >  graphGenerator =  new  LinearGraphGenerator  < >  ( size ) ;   graphGenerator . generateGraph  ( line ,  new IntegerVertexFactory  ( ) , null ) ;  return line ; } }