  package     com . github . alexcojocaru . mojo . elasticsearch ;   import   java . io . File ;  import     org . apache . maven . plugin . MojoExecutionException ;  import    org . elasticsearch . client . Client ;  import     org . elasticsearch . common . settings . Settings ;  import    org . elasticsearch . node . Node ;  import    org . elasticsearch . node . NodeBuilder ;   public class ElasticsearchNode  {   private Node  node ;   private  int  httpPort ;   public ElasticsearchNode  (  Settings settings )  throws MojoExecutionException  {   settings =          Settings . settingsBuilder  ( ) . put  ( "index.number_of_shards" , 1 ) . put  ( "index.number_of_replicas" , 0 ) . put  ( "network.host" , "127.0.0.1" ) . put  ( "discovery.zen.ping.timeout" , "3ms" ) . put  ( "discovery.zen.ping.multicast.enabled" , false ) . put  ( "http.cors.enabled" , true ) . put  ( settings ) . build  ( ) ;   httpPort =  settings . getAsInt  ( "http.port" , 9200 ) ;   node =    NodeBuilder . nodeBuilder  ( ) . settings  ( settings ) . node  ( ) ; }   public static ElasticsearchNode start  (  String dataPath )  throws MojoExecutionException  {  return  start  ( dataPath , 9200 , 9300 ) ; }   public static ElasticsearchNode start  (  String dataPath ,   int httpPort ,   int tcpPort )  throws MojoExecutionException  {  String  homePath =   new File  ( dataPath ) . getParent  ( ) ;  Settings  settings =        
<<<<<<<
 Settings . settingsBuilder  ( )
=======
ImmutableSettings
>>>>>>>
 . 
<<<<<<<
put
=======
settingsBuilder
>>>>>>>
  ( "cluster.name" , "test" ) . put  ( 
<<<<<<<
"action.auto_create_index"
=======
"cluster.name"
>>>>>>>
 , 
<<<<<<<
false
=======
"test"
>>>>>>>
 ) . put  ( 
<<<<<<<
"transport.tcp.port"
=======
"action.auto_create_index"
>>>>>>>
 , 
<<<<<<<
tcpPort
=======
false
>>>>>>>
 ) . put  ( 
<<<<<<<
"http.port"
=======
"transport.tcp.port"
>>>>>>>
 , 
<<<<<<<
httpPort
=======
tcpPort
>>>>>>>
 ) . put  ( 
<<<<<<<
"path.data"
=======
"http.port"
>>>>>>>
 , 
<<<<<<<
dataPath
=======
httpPort
>>>>>>>
 ) . put  ( 
<<<<<<<
"path.home"
=======
"path.data"
>>>>>>>
 , 
<<<<<<<
homePath
=======
dataPath
>>>>>>>
 ) . build  ( ) ;  return  new ElasticsearchNode  ( settings ) ; }   public Client getClient  ( )  {  return  node . client  ( ) ; }   public void stop  ( )  {  if  (  node != null )  {   node . close  ( ) ;   node = null ; } }   public boolean isClosed  ( )  {  return  (   node == null ||   node != null &&  node . isClosed  ( ) ) ; }   public  int getHttpPort  ( )  {  return httpPort ; } }