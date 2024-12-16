  package   org . jgrapht . generate ;   import  java . util .  * ;  import  org . jgrapht .  * ;   public class BarabasiAlbertGraphGenerator  <  V ,  E >  implements   GraphGenerator  < V , E , V >  {   private final Random  rng ;   private final  int  m0 ;   private final  int  m ;   private final  int  n ;   public BarabasiAlbertGraphGenerator  (   int m0 ,   int m ,   int n )  {  this  ( m0 , m , n ,  new Random  ( ) ) ; }   public BarabasiAlbertGraphGenerator  (   int m0 ,   int m ,   int n ,   long seed )  {  this  ( m0 , m , n ,  new Random  ( seed ) ) ; }   public BarabasiAlbertGraphGenerator  (   int m0 ,   int m ,   int n ,  Random rng )  {  if  (  m0 < 1 )  {  throw  new IllegalArgumentException  (   "invalid initial nodes (" + m0 + " < 1)" ) ; }    this . m0 = m0 ;  if  (  m <= 0 )  {  throw  new IllegalArgumentException  (   "invalid edges per node (" + m + " <= 0" ) ; }  if  (  m > m0 )  {  throw  new IllegalArgumentException  (     "invalid edges per node (" + m + " > " + m0 + ")" ) ; }    this . m = m ;  if  (  n < m0 )  {  throw  new IllegalArgumentException  ( "total number of nodes must be at least equal to the initial set" ) ; }    this . n = n ;    this . rng =  Objects . requireNonNull  ( rng , "Random number generator cannot be null" ) ; }    @ Override public void generateGraph  (   Graph  < V , E > target ,   Map  < String , V > resultMap )  {   Set  < V >  oldNodes =  new  HashSet  < >  (  target . vertexSet  ( ) ) ;   Set  < V >  newNodes =  new  HashSet  < >  ( ) ;    new  CompleteGraphGenerator  < V , E >  ( m0 ) . generateGraph  ( target , resultMap ) ;      target . vertexSet  ( ) . stream  ( ) . filter  (  v ->  !  oldNodes . contains  ( v ) ) . forEach  (  newNodes :: add ) ;   List  < V >  nodes =  new  ArrayList  < >  (  n * m ) ;   nodes . addAll  ( newNodes ) ;  for (   int  i = 0 ;  i <  m0 - 2 ;  i ++ )  {   nodes . addAll  ( newNodes ) ; }  for (   int  i = m0 ;  i < n ;  i ++ )  {  V  v =  target . addVertex  ( ) ;  if  (  v == null )  {  throw  new IllegalArgumentException  ( "Invalid vertex supplier (does not return unique vertices on each call)." ) ; }   List  < V >  newEndpoints =  new  ArrayList  < >  ( ) ;   int  added = 0 ;  while  (  added < m )  {  V  u =  nodes . get  (  rng . nextInt  (  nodes . size  ( ) ) ) ;  if  (  !  target . containsEdge  ( v , u ) )  {   target . addEdge  ( v , u ) ;   added ++ ;   newEndpoints . add  ( v ) ;  if  (  i > 1 )  {   newEndpoints . add  ( u ) ; } } }   nodes . addAll  ( newEndpoints ) ; } } }