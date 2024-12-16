  package     com . datastax . driver . core . policies ;   import     com . datastax . driver . core . ConsistencyLevel ;  import     com . datastax . driver . core . Query ;  import     com . datastax . driver . core . WriteType ;   public interface RetryPolicy  {   public static class RetryDecision  {   public static enum Type  {  RETRY ,  RETHROW ,  IGNORE } ;   private final Type  type ;   private final ConsistencyLevel  retryCL ;   private RetryDecision  (  Type type ,  ConsistencyLevel retryCL )  {    this . type = type ;    this . retryCL = retryCL ; }   public Type getType  ( )  {  return type ; }   public ConsistencyLevel getRetryConsistencyLevel  ( )  {  return retryCL ; }   public static RetryDecision rethrow  ( )  {  return  new RetryDecision  (  Type . RETHROW , null ) ; }   public static RetryDecision retry  (  ConsistencyLevel consistency )  {  return  new RetryDecision  (  Type . RETRY , consistency ) ; }   public static RetryDecision ignore  ( )  {  return  new RetryDecision  (  Type . IGNORE , null ) ; }    @ Override public String toString  ( )  {  switch  ( type )  {   case RETRY :  return  "Retry at " + retryCL ;   case RETHROW :  return "Rethrow" ;   case IGNORE :  return "Ignore" ; }  throw  new AssertionError  ( ) ; } }   public RetryDecision onReadTimeout  (  Statement statement ,  ConsistencyLevel cl ,   int requiredResponses ,   int receivedResponses ,  boolean dataRetrieved ,   int nbRetry ) ;   public RetryDecision onWriteTimeout  (  Statement statement ,  ConsistencyLevel cl ,  WriteType writeType ,   int requiredAcks ,   int receivedAcks ,   int nbRetry ) ;   public RetryDecision onUnavailable  (  Statement statement ,  ConsistencyLevel cl ,   int requiredReplica ,   int aliveReplica ,   int nbRetry ) ; }