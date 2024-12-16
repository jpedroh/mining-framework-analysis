  package   org . restheart . db ;   import   com . mongodb . BasicDBObject ;  import   com . mongodb . CommandFailureException ;  import   com . mongodb . DB ;  import   com . mongodb . DBCollection ;  import   com . mongodb . DBObject ;  import   com . mongodb . MongoClient ;  import   com . mongodb . MongoException ;  import    org . restheart . handlers . RequestContext ;  import   java . time . Instant ;  import    org . bson . types . ObjectId ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;   public class PropsFixer  {   private final MongoClient  client =   MongoDBClientSingleton . getInstance  ( ) . getClient  ( ) ;   public boolean addCollectionProps  (  String dbName ,  String collName )  throws MongoException  {   final DbsDAO  dbsDAO =  new DbsDAO  ( ) ;  DBObject  dbmd =  dbsDAO . getDbProps  ( dbName ) ;  if  (  dbmd == null )  {  return false ; }   final CollectionDAO  collectionDAO =  new CollectionDAO  ( ) ;  DBObject  md =  collectionDAO . getCollectionProps  ( dbName , collName ) ;  if  (  md != null )  {  return false ; }  DB  db =  dbsDAO . getDB  ( dbName ) ;  if  (  !  db . collectionExists  ( collName ) )  {  return false ; }  DBObject  properties =  new BasicDBObject  ( ) ;  ObjectId  timestamp =  new ObjectId  ( ) ;  Instant  now =  Instant . ofEpochSecond  (  timestamp . getTimestamp  ( ) ) ;   properties . put  ( "_id" , "_properties" ) ;   properties . put  ( "_created_on" ,  now . toString  ( ) ) ;   properties . put  ( "_etag" , timestamp ) ;  DBCollection  coll =  collectionDAO . getCollection  ( dbName , collName ) ;   coll . insert  ( properties ) ;   LOGGER . info  ( "properties added to {}/{}" , dbName , collName ) ;  return true ; }   public boolean addDbProps  (  String dbName )  {   final DbsDAO  dbsDAO =  new DbsDAO  ( ) ;  if  (  !  dbsDAO . doesDbExists  ( dbName ) )  {  return false ; }  DBObject  dbmd =  dbsDAO . getDbProps  ( dbName ) ;  if  (  dbmd != null )  {  return false ; }  DBObject  properties =  new BasicDBObject  ( ) ;  ObjectId  timestamp =  new ObjectId  ( ) ;  Instant  now =  Instant . ofEpochSecond  (  timestamp . getTimestamp  ( ) ) ;   properties . put  ( "_id" , "_properties" ) ;   properties . put  ( "_created_on" ,  now . toString  ( ) ) ;   properties . put  ( "_etag" , timestamp ) ;   final CollectionDAO  collectionDAO =  new CollectionDAO  ( ) ;  DBCollection  coll =  collectionDAO . getCollection  ( dbName , "_properties" ) ;   coll . insert  ( properties ) ;   LOGGER . info  ( "properties added to {}" , dbName ) ;  return true ; }   public void fixAllMissingProps  ( )  {   final DbsDAO  dbsDAO =  new DbsDAO  ( ) ;  try  {       client . getDatabaseNames  ( ) . stream  ( ) . filter  (  dbName ->  !  RequestContext . isReservedResourceDb  ( dbName ) ) . map  (  dbName ->  {  try  {   addDbProps  ( dbName ) ; }  catch (   Throwable t )  {   LOGGER . error  ( "error fixing _properties of db {}" , dbName , t ) ; }  return dbName ; } ) . forEach  (  dbName ->  {  DB  db =  dbsDAO . getDB  ( dbName ) ;      dbsDAO . getDbCollections  ( db ) . stream  ( ) . filter  (  collectionName ->  !  RequestContext . isReservedResourceCollection  ( collectionName ) ) . forEach  (  collectionName ->  {  try  {   addCollectionProps  ( dbName , collectionName ) ; }  catch (   Throwable t )  {   LOGGER . error  ( "error checking the collection {}/{} for valid _properties. note that a request to it will result on NOT_FOUND" , dbName , collectionName , t ) ; } } ) ; } ) ; }  catch (   CommandFailureException cfe )  {  Object  errmsg =   cfe . getCommandResult  ( ) . get  ( "errmsg" ) ;  if  (    errmsg != null &&  errmsg instanceof String &&  (   "unauthorized" . equals  ( errmsg ) ||   (  ( String ) errmsg ) . contains  ( "not authorized" ) ) )  {   LOGGER . error  ( "error looking for dbs and collections with missing _properties due to insuffient mongo user privileges. note that requests to dbs and collections with no _properties result on NOT_FOUND" , cfe ) ; } else  {   LOGGER . error  ( "eorro looking for dbs and collections with missing _properties. note that requests to dbs and collections with no _properties result on NOT_FOUND" , cfe ) ; } }  catch (   MongoException mex )  {   LOGGER . error  ( "eorro looking for dbs and collections with missing _properties. note that requests to dbs and collections with no _properties result on NOT_FOUND" , mex ) ; } }   private static final Logger  LOGGER =  LoggerFactory . getLogger  (  PropsFixer . class ) ;   public PropsFixer  ( )  {   client =   MongoDBClientSingleton . getInstance  ( ) . getClient  ( ) ; } }