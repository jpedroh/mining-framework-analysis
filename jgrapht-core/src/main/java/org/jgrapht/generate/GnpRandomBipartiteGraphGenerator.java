  package   org . jgrapht . generate ;   import   java . util . LinkedHashMap ;  import   java . util . LinkedHashSet ;  import   java . util . Map ;  import   java . util . Objects ;  import   java . util . Random ;  import   java . util . Set ;  import   org . jgrapht . Graph ;   public class GnpRandomBipartiteGraphGenerator  <  V ,  E >  implements   GraphGenerator  < V , E , V >  {   private final Random  rng ;   private final  int  n1 ;   private final  int  n2 ;   private final  double  p ;   private  Map  < Integer , V >  partitionA ;   private  Map  < Integer , V >  partitionB ;   public GnpRandomBipartiteGraphGenerator  (   int n1 ,   int n2 ,   double p )  {  this  ( n1 , n2 , p ,  new Random  ( ) ) ; }   public GnpRandomBipartiteGraphGenerator  (   int n1 ,   int n2 ,   double p ,   long seed )  {  this  ( n1 , n2 , p ,  new Random  ( seed ) ) ; }   public GnpRandomBipartiteGraphGenerator  (   int n1 ,   int n2 ,   double p ,  Random rng )  {  if  (  n1 < 0 )  {  throw  new IllegalArgumentException  ( "number of vertices must be non-negative" ) ; }    this . n1 = n1 ;  if  (  n2 < 0 )  {  throw  new IllegalArgumentException  ( "number of vertices must be non-negative" ) ; }    this . n2 = n2 ;  if  (   p < 0.0 ||  p > 1.0 )  {  throw  new IllegalArgumentException  ( "not valid probability of edge existence" ) ; }    this . p = p ;    this . rng =  Objects . requireNonNull  ( rng ) ; }    @ Override public void generateGraph  (   Graph  < V , E > target ,   Map  < String , V > resultMap )  {  if  (   n1 + n2 == 0 )  {  return ; }   int  previousVertexSetSize =   target . vertexSet  ( ) . size  ( ) ;   partitionA =  new  LinkedHashMap  < >  ( n1 ) ;  for (   int  i = 0 ;  i < n1 ;  i ++ )  {  V  v =  target . addVertex  ( ) ;   partitionA . put  ( i , v ) ; }   partitionB =  new  LinkedHashMap  < >  ( n2 ) ;  for (   int  i = 0 ;  i < n2 ;  i ++ )  {  V  v =  target . addVertex  ( ) ;   partitionB . put  ( i , v ) ; }  if  (    target . vertexSet  ( ) . size  ( ) !=   previousVertexSetSize + n1 + n2 )  {  throw  new IllegalArgumentException  (   "Vertex factory did not produce " +  (  n1 + n2 ) + " distinct vertices." ) ; }  boolean  isDirected =   target . getType  ( ) . isDirected  ( ) ;  for (   int  i = 0 ;  i < n1 ;  i ++ )  {  V  s =  partitionA . get  ( i ) ;  for (   int  j = 0 ;  j < n2 ;  j ++ )  {  V  t =  partitionB . get  ( j ) ;  if  (   rng . nextDouble  ( ) < p )  {   target . addEdge  ( s , t ) ; }  if  ( isDirected )  {  if  (   rng . nextDouble  ( ) < p )  {   target . addEdge  ( t , s ) ; } } } } }   public  Set  < V > getFirstPartition  ( )  {  if  (   partitionA . size  ( ) <=  partitionB . size  ( ) )  return  new  LinkedHashSet  < >  (  partitionA . values  ( ) ) ; else  return  new  LinkedHashSet  < >  (  partitionB . values  ( ) ) ; }   public  Set  < V > getSecondPartition  ( )  {  if  (   partitionB . size  ( ) >=  partitionA . size  ( ) )  return  new  LinkedHashSet  < >  (  partitionB . values  ( ) ) ; else  return  new  LinkedHashSet  < >  (  partitionA . values  ( ) ) ; } }