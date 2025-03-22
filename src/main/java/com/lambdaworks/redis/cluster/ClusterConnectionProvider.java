  package    com . lambdaworks . redis . cluster ;   import   java . io . Closeable ;  import    com . lambdaworks . redis . ReadFrom ;  import    com . lambdaworks . redis . RedisException ;  import       com . lambdaworks . redis . cluster . models . partitions . Partitions ;  import     com . lambdaworks . redis . api . StatefulRedisConnection ;  interface ClusterConnectionProvider  extends  Closeable  {   <  K ,  V >  StatefulRedisConnection  < K , V > getConnection  (  Intent intent ,   int slot ) ;   <  K ,  V >  StatefulRedisConnection  < K , V > getConnection  (  Intent intent ,  String host ,   int port ) ;   <  K ,  V >  
<<<<<<<
RedisAsyncConnectionImpl
=======
StatefulRedisConnection
>>>>>>>
  < K , V > getConnection  (  Intent intent ,  String nodeId ) ;    @ Override void close  ( ) ;  void reset  ( ) ;  void closeStaleConnections  ( ) ;  void setPartitions  (  Partitions partitions ) ;  void setAutoFlushCommands  (  boolean autoFlush ) ;  void flushCommands  ( ) ;  void setReadFrom  (  ReadFrom readFrom ) ;  ReadFrom getReadFrom  ( ) ;  enum Intent  {  READ ,  WRITE  ; } }