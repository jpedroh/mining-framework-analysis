  package     com . datastax . driver . core . policies ;   import     java . util . concurrent . atomic . AtomicInteger ;  import    java . util . concurrent . CopyOnWriteArrayList ;  import     com . google . common . collect . AbstractIterator ;  import   java . util . Collection ;  import   java . util . Iterator ;  import   java . util . List ;  import   java . util . Random ;  import     com . datastax . driver . core . Cluster ;  import     com . datastax . driver . core . Host ;  import     com . datastax . driver . core . HostDistance ;  import     com . datastax . driver . core . Query ;   public class RoundRobinPolicy  implements  LoadBalancingPolicy  {   private final  CopyOnWriteArrayList  < Host >  liveHosts =  new  CopyOnWriteArrayList  < Host >  ( ) ;   private final AtomicInteger  index =  new AtomicInteger  ( ) ;   public RoundRobinPolicy  ( )  { }    @ Override public void init  (  Cluster cluster ,   Collection  < Host > hosts )  {    this . liveHosts . addAll  ( hosts ) ;    this . index . set  (   new Random  ( ) . nextInt  (  Math . max  (  hosts . size  ( ) , 1 ) ) ) ; }    @ Override public HostDistance distance  (  Host host )  {  return  HostDistance . LOCAL ; }    @ Override public  Iterator  < Host > newQueryPlan  (  String loggedKeyspace ,  Statement statement )  {    @ SuppressWarnings  ( "unchecked" ) final  List  < Host >  hosts =  (  List  < Host > )  liveHosts . clone  ( ) ;   final  int  startIdx =  index . getAndIncrement  ( ) ;  if  (  startIdx >   Integer . MAX_VALUE - 10000 )   index . set  ( 0 ) ;  return  new  AbstractIterator  < Host >  ( )  {   private  int  idx = startIdx ;   private  int  remaining =  hosts . size  ( ) ;    @ Override protected Host computeNext  ( )  {  if  (  remaining <= 0 )  return  endOfData  ( ) ;   remaining -- ;   int  c =   idx ++ %  hosts . size  ( ) ;  if  (  c < 0 )   c +=  hosts . size  ( ) ;  return  hosts . get  ( c ) ; } } ; }    @ Override public void onUp  (  Host host )  {   liveHosts . addIfAbsent  ( host ) ; }    @ Override public void onDown  (  Host host )  {   liveHosts . remove  ( host ) ; }    @ Override public void onAdd  (  Host host )  {   onUp  ( host ) ; }    @ Override public void onRemove  (  Host host )  {   onDown  ( host ) ; } }