  package    com . datastax . driver . core ;   import    java . util . concurrent . ConcurrentHashMap ;  import   java . util . Collection ;  import   java . util . Collections ;  import   java . util . HashMap ;  import   java . util . Map ;   public class KeyspaceMetadata  {   public static final String  KS_NAME = "keyspace_name" ;   private static final String  DURABLE_WRITES = "durable_writes" ;   private static final String  STRATEGY_CLASS = "strategy_class" ;   private static final String  STRATEGY_OPTIONS = "strategy_options" ;   private final String  name ;   private final boolean  durableWrites ;   private final ReplicationStrategy  strategy ;   private final  Map  < String , String >  replication =  new  HashMap  < String , String >  ( ) ;   private final  Map  < String , TableMetadata >  tables =  new  ConcurrentHashMap  < String , TableMetadata >  ( ) ;   private KeyspaceMetadata  (  String name ,  boolean durableWrites ,   Map  < String , String > replication )  {    this . name = name ;    this . durableWrites = durableWrites ;    this . replication = replication ;    this . strategy =  ReplicationStrategy . create  ( replication ) ; }   static KeyspaceMetadata build  (  Row row )  {  String  name =  row . getString  ( KS_NAME ) ;  boolean  durableWrites =  row . getBool  ( DURABLE_WRITES ) ;   Map  < String , String >  replicationOptions =  new  HashMap  < String , String >  ( ) ;   replicationOptions . put  ( "class" ,  row . getString  ( STRATEGY_CLASS ) ) ;   replicationOptions . putAll  (  TableMetadata . fromJsonMap  (  row . getString  ( STRATEGY_OPTIONS ) ) ) ;  return  new KeyspaceMetadata  ( name , durableWrites , replicationOptions ) ; }   public String getName  ( )  {  return name ; }   public boolean isDurableWrites  ( )  {  return durableWrites ; }   public  Map  < String , String > getReplication  ( )  {  return  Collections .  < String , String > unmodifiableMap  ( replication ) ; }   public TableMetadata getTable  (  String name )  {  return  tables . get  ( name ) ; }   public  Collection  < TableMetadata > getTables  ( )  {  return  Collections .  < TableMetadata > unmodifiableCollection  (  tables . values  ( ) ) ; }   public String exportAsString  ( )  {  StringBuilder  sb =  new StringBuilder  ( ) ;    sb . append  (  asCQLQuery  ( ) ) . append  ( "\n" ) ;  for ( TableMetadata tm :  tables . values  ( ) )     sb . append  ( "\n" ) . append  (  tm . exportAsString  ( ) ) . append  ( "\n" ) ;  return  sb . toString  ( ) ; }   public String asCQLQuery  ( )  {  StringBuilder  sb =  new StringBuilder  ( ) ;     sb . append  ( "CREATE KEYSPACE " ) . append  ( name ) . append  ( " WITH " ) ;     sb . append  ( "REPLICATION = { 'class' : '" ) . append  (  replication . get  ( "class" ) ) . append  ( "'" ) ;  for (   Map . Entry  < String , String > entry :  replication . entrySet  ( ) )  {  if  (   entry . getKey  ( ) . equals  ( "class" ) )  continue ;       sb . append  ( ", '" ) . append  (  entry . getKey  ( ) ) . append  ( "': '" ) . append  (  entry . getValue  ( ) ) . append  ( "'" ) ; }    sb . append  ( " } AND DURABLE_WRITES = " ) . append  ( durableWrites ) ;   sb . append  ( ";" ) ;  return  sb . toString  ( ) ; }  void add  (  TableMetadata tm )  {   tables . put  (  tm . getName  ( ) , tm ) ; }  ReplicationStrategy replicationStrategy  ( )  {  return strategy ; } }