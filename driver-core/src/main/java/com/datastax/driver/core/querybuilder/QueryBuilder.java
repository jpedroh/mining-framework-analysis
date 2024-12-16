  package     com . datastax . driver . core . querybuilder ;   import  java . util .  * ;  import     com . datastax . driver . core . RegularStatement ;  import     com . datastax . driver . core . TableMetadata ;   public final class QueryBuilder  {   private QueryBuilder  ( )  { }   public static  Select . Builder select  (  String ...  columns )  {  return  new  Select . Builder  (  Arrays . asList  (  (  Object  [ ] ) columns ) ) ; }   public static  Select . Selection select  ( )  {  return  new  Select . SelectionOrAlias  ( ) ; }   public static Insert insertInto  (  String table )  {  return  new Insert  ( null , table ) ; }   public static Insert insertInto  (  String keyspace ,  String table )  {  return  new Insert  ( keyspace , table ) ; }   public static Insert insertInto  (  TableMetadata table )  {  return  new Insert  ( table ) ; }   public static Update update  (  String table )  {  return  new Update  ( null , table ) ; }   public static Update update  (  String keyspace ,  String table )  {  return  new Update  ( keyspace , table ) ; }   public static Update update  (  TableMetadata table )  {  return  new Update  ( table ) ; }   public static  Delete . Builder delete  (  String ...  columns )  {  return  new  Delete . Builder  (  Arrays . asList  (  (  Object  [ ] ) columns ) ) ; }   public static  Delete . Selection delete  ( )  {  return  new  Delete . Selection  ( ) ; }   public static Batch batch  (  RegularStatement ...  statements )  {  return  new Batch  ( statements , true ) ; }   public static Batch unloggedBatch  (  RegularStatement ...  statements )  {  return  new Batch  ( statements , false ) ; }   public static Truncate truncate  (  String table )  {  return  new Truncate  ( null , table ) ; }   public static Truncate truncate  (  String keyspace ,  String table )  {  return  new Truncate  ( keyspace , table ) ; }   public static Truncate truncate  (  TableMetadata table )  {  return  new Truncate  ( table ) ; }   public static String quote  (  String columnName )  {  StringBuilder  sb =  new StringBuilder  ( ) ;   sb . append  ( '"' ) ;   Utils . appendName  ( columnName , sb ) ;   sb . append  ( '"' ) ;  return  sb . toString  ( ) ; }   public static String token  (  String columnName )  {  StringBuilder  sb =  new StringBuilder  ( ) ;   sb . append  ( "token(" ) ;   Utils . appendName  ( columnName , sb ) ;   sb . append  ( ')' ) ;  return  sb . toString  ( ) ; }   public static String token  (  String ...  columnNames )  {  StringBuilder  sb =  new StringBuilder  ( ) ;   sb . append  ( "token(" ) ;   Utils . joinAndAppendNames  ( sb , "," ,  Arrays . asList  (  (  Object  [ ] ) columnNames ) ) ;   sb . append  ( ')' ) ;  return  sb . toString  ( ) ; }   public static Clause eq  (  String name ,  Object value )  {  return  new  Clause . SimpleClause  ( name , "=" , value ) ; }   public static Clause in  (  String name ,  Object ...  values )  {  return  new  Clause . InClause  ( name ,  Arrays . asList  ( values ) ) ; }   public static Clause lt  (  String name ,  Object value )  {  return  new  Clause . SimpleClause  ( name , "<" , value ) ; }   public static Clause lte  (  String name ,  Object value )  {  return  new  Clause . SimpleClause  ( name , "<=" , value ) ; }   public static Clause gt  (  String name ,  Object value )  {  return  new  Clause . SimpleClause  ( name , ">" , value ) ; }   public static Clause gte  (  String name ,  Object value )  {  return  new  Clause . SimpleClause  ( name , ">=" , value ) ; }   public static Ordering asc  (  String columnName )  {  return  new Ordering  ( columnName , false ) ; }   public static Ordering desc  (  String columnName )  {  return  new Ordering  ( columnName , true ) ; }   public static Using timestamp  (   long timestamp )  {  if  (  timestamp < 0 )  throw  new IllegalArgumentException  ( "Invalid timestamp, must be positive" ) ;  return  new  Using . WithValue  ( "TIMESTAMP" , timestamp ) ; }   public static Using timestamp  (  BindMarker marker )  {  return  new  Using . WithMarker  ( "TIMESTAMP" , marker ) ; }   public static Using ttl  (   int ttl )  {  if  (  ttl < 0 )  throw  new IllegalArgumentException  ( "Invalid ttl, must be positive" ) ;  return  new  Using . WithValue  ( "TTL" , ttl ) ; }   public static Using ttl  (  BindMarker marker )  {  return  new  Using . WithMarker  ( "TTL" , marker ) ; }   public static Assignment set  (  String name ,  Object value )  {  return  new  Assignment . SetAssignment  ( name , value ) ; }   public static Assignment incr  (  String name )  {  return  incr  ( name , 1L ) ; }   public static Assignment incr  (  String name ,   long value )  {  return  new  Assignment . CounterAssignment  ( name , value , true ) ; }   public static Assignment incr  (  String name ,  BindMarker value )  {  return  new  Assignment . CounterAssignment  ( name , value , true ) ; }   public static Assignment decr  (  String name )  {  return  decr  ( name , 1L ) ; }   public static Assignment decr  (  String name ,   long value )  {  return  new  Assignment . CounterAssignment  ( name , value , false ) ; }   public static Assignment decr  (  String name ,  BindMarker value )  {  return  new  Assignment . CounterAssignment  ( name , value , false ) ; }   public static Assignment prepend  (  String name ,  Object value )  {  Object  v =   value instanceof BindMarker ? value :  Collections . singletonList  ( value ) ;  return  new  Assignment . ListPrependAssignment  ( name , v ) ; }   public static Assignment prependAll  (  String name ,   List  <  ? > list )  {  return  new  Assignment . ListPrependAssignment  ( name , list ) ; }   public static Assignment prependAll  (  String name ,  BindMarker list )  {  return  new  Assignment . ListPrependAssignment  ( name , list ) ; }   public static Assignment append  (  String name ,  Object value )  {  Object  v =   value instanceof BindMarker ? value :  Collections . singletonList  ( value ) ;  return  new  Assignment . CollectionAssignment  ( name , v , true ) ; }   public static Assignment appendAll  (  String name ,   List  <  ? > list )  {  return  new  Assignment . CollectionAssignment  ( name , list , true ) ; }   public static Assignment appendAll  (  String name ,  BindMarker list )  {  return  new  Assignment . CollectionAssignment  ( name , list , true ) ; }   public static Assignment discard  (  String name ,  Object value )  {  Object  v =   value instanceof BindMarker ? value :  Collections . singletonList  ( value ) ;  return  new  Assignment . CollectionAssignment  ( name , v , false ) ; }   public static Assignment discardAll  (  String name ,   List  <  ? > list )  {  return  new  Assignment . CollectionAssignment  ( name , list , false ) ; }   public static Assignment discardAll  (  String name ,  BindMarker list )  {  return  new  Assignment . CollectionAssignment  ( name , list , false ) ; }   public static Assignment setIdx  (  String name ,   int idx ,  Object value )  {  return  new  Assignment . ListSetIdxAssignment  ( name , idx , value ) ; }   public static Assignment add  (  String name ,  Object value )  {  Object  v =   value instanceof BindMarker ? value :  Collections . singleton  ( value ) ;  return  new  Assignment . CollectionAssignment  ( name , v , true ) ; }   public static Assignment addAll  (  String name ,   Set  <  ? > set )  {  return  new  Assignment . CollectionAssignment  ( name , set , true ) ; }   public static Assignment addAll  (  String name ,  BindMarker set )  {  return  new  Assignment . CollectionAssignment  ( name , set , true ) ; }   public static Assignment remove  (  String name ,  Object value )  {  Object  v =   value instanceof BindMarker ? value :  Collections . singleton  ( value ) ;  return  new  Assignment . CollectionAssignment  ( name , v , false ) ; }   public static Assignment removeAll  (  String name ,   Set  <  ? > set )  {  return  new  Assignment . CollectionAssignment  ( name , set , false ) ; }   public static Assignment removeAll  (  String name ,  BindMarker set )  {  return  new  Assignment . CollectionAssignment  ( name , set , false ) ; }   public static Assignment put  (  String name ,  Object key ,  Object value )  {  return  new  Assignment . MapPutAssignment  ( name , key , value ) ; }   public static Assignment putAll  (  String name ,   Map  <  ? ,  ? > map )  {  return  new  Assignment . CollectionAssignment  ( name , map , true ) ; }   public static Assignment putAll  (  String name ,  BindMarker map )  {  return  new  Assignment . CollectionAssignment  ( name , map , true ) ; }   public static BindMarker bindMarker  ( )  {  return  BindMarker . ANONYMOUS ; }   public static BindMarker bindMarker  (  String name )  {  return  new BindMarker  ( name ) ; }   public static Object raw  (  String str )  {  return  new  Utils . RawString  ( str ) ; }   public static Object fcall  (  String name ,  Object ...  parameters )  {  return  new  Utils . FCall  ( name , parameters ) ; }   public static Object column  (  String name )  {  return  new  Utils . CName  ( name ) ; }   public static Clause in  (  String name ,   List  <  ? > values )  {  return  new  Clause . InClause  ( name , values ) ; }   public static Clause lt  (   List  < String > names ,   List  <  ? > values )  {  if  (   names . size  ( ) !=  values . size  ( ) )  throw  new IllegalArgumentException  (  String . format  ( "The number of names (%d) and values (%d) don't match" ,  names . size  ( ) ,  values . size  ( ) ) ) ;  return  new  Clause . CompoundClause  ( names , "<" , values ) ; }   public static Clause lte  (   List  < String > names ,   List  <  ? > values )  {  if  (   names . size  ( ) !=  values . size  ( ) )  throw  new IllegalArgumentException  (  String . format  ( "The number of names (%d) and values (%d) don't match" ,  names . size  ( ) ,  values . size  ( ) ) ) ;  return  new  Clause . CompoundClause  ( names , "<=" , values ) ; }   public static Clause gt  (   List  < String > names ,   List  <  ? > values )  {  if  (   names . size  ( ) !=  values . size  ( ) )  throw  new IllegalArgumentException  (  String . format  ( "The number of names (%d) and values (%d) don't match" ,  names . size  ( ) ,  values . size  ( ) ) ) ;  return  new  Clause . CompoundClause  ( names , ">" , values ) ; }   public static Clause gte  (   List  < String > names ,   List  <  ? > values )  {  if  (   names . size  ( ) !=  values . size  ( ) )  throw  new IllegalArgumentException  (  String . format  ( "The number of names (%d) and values (%d) don't match" ,  names . size  ( ) ,  values . size  ( ) ) ) ;  return  new  Clause . CompoundClause  ( names , ">=" , values ) ; } }