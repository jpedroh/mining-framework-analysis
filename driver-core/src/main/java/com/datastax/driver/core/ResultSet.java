  package    com . datastax . driver . core ;   import    java . util . concurrent . ExecutionException ;  import      com . google . common . util . concurrent . Futures ;  import      com . google . common . util . concurrent . ListenableFuture ;  import      com . google . common . util . concurrent . SettableFuture ;  import      com . google . common . util . concurrent . Uninterruptibles ;  import     com . datastax . driver . core . exceptions .  * ;  import   java . util . Iterator ;  import   java . util . List ;   public interface ResultSet  extends   Iterable  < Row >  {   public ColumnDefinitions getColumnDefinitions  ( ) ;   public boolean isExhausted  ( ) ;   public Row one  ( ) ;   public  List  < Row > all  ( ) ;    @ Override public  Iterator  < Row > iterator  ( ) ;   public  int getAvailableWithoutFetching  ( ) ;   public boolean isFullyFetched  ( ) ;   public  ListenableFuture  < Void > fetchMoreResults  ( ) ;   public ExecutionInfo getExecutionInfo  ( ) ;   public  List  < ExecutionInfo > getAllExecutionInfo  ( ) ; }