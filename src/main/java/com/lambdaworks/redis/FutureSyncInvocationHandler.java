  package   com . lambdaworks . redis ;   import    java . lang . reflect . InvocationTargetException ;  import    java . lang . reflect . Method ;  import    java . util . concurrent . ExecutionException ;  import    java . util . concurrent . Future ;  import     com . google . common . cache . CacheBuilder ;  import     com . google . common . cache . CacheLoader ;  import     com . google . common . cache . LoadingCache ;  import     com . google . common . reflect . AbstractInvocationHandler ;  import     com . lambdaworks . redis . protocol . Command ;  import     com . lambdaworks . redis . api . StatefulConnection ;  import     com . lambdaworks . redis . api . StatefulRedisConnection ;  class FutureSyncInvocationHandler  <  K ,  V >  extends AbstractInvocationHandler  {   private final  StatefulConnection  <  ? ,  ? >  connection ;   private  LoadingCache  < Method , Method >  methodCache ; 
<<<<<<<
  public FutureSyncInvocationHandler  (   final  RedisChannelHandler  < K , V > connection )  {    this . connection = connection ;    this . timeout =  connection . timeout ;    this . unit =  connection . unit ;   methodCache =   CacheBuilder . newBuilder  ( ) . build  (  new  CacheLoader  < Method , Method >  ( )  {    @ Override public Method load  (  Method key )  throws Exception  {  return   connection . getClass  ( ) . getMethod  (  key . getName  ( ) ,  key . getParameterTypes  ( ) ) ; } } ) ; }
=======
>>>>>>>
    @ Override protected  @ SuppressWarnings  ( "unchecked" ) Object handleInvocation  (  Object proxy ,  Method method ,   Object  [ ] args )  throws Throwable  {  try  {  Method  targetMethod =  methodCache . get  ( method ) ;  Object  result =  targetMethod . invoke  ( asyncApi , args ) ;  if  (  result instanceof RedisFuture )  {   RedisFuture  <  ? > 
<<<<<<<
 redisCommand =  (  RedisCommand  <  ? ,  ? ,  ? > ) result
=======
 command =  (  RedisFuture  <  ? > ) result
>>>>>>>
 ;  if  (   !   method . getName  ( ) . equals  ( "exec" ) &&  !   method . getName  ( ) . equals  ( "multi" ) )  {  if  (   connection instanceof StatefulRedisConnection &&   (  ( StatefulRedisConnection ) connection ) . isMulti  ( ) )  {  return null ; } } 
<<<<<<<
 Object  awaitedResult =  LettuceFutures . awaitOrCancel  ( redisCommand , timeout , unit ) ;
=======
  LettuceFutures . awaitOrCancel  ( command ,  connection . getTimeout  ( ) ,  connection . getTimeoutUnit  ( ) ) ;
>>>>>>>
  if  (  redisCommand instanceof Command )  {   Command  <  ? ,  ? ,  ? >  command =  (  Command  <  ? ,  ? ,  ? > ) redisCommand ;  if  (   command . getException  ( ) != null )  {  throw  new RedisException  (  command . getException  ( ) ) ; } }  if  (  redisCommand instanceof  Future  <  ? > )  {  if  (  redisCommand . isDone  ( ) )  {  try  {   redisCommand . get  ( ) ; }  catch (   InterruptedException e )  {  throw e ; }  catch (   ExecutionException e )  {  throw  new RedisException  (  e . getCause  ( ) ) ; } } }  return 
<<<<<<<
awaitedResult
=======
 command . get  ( )
>>>>>>>
 ; } 
<<<<<<<
 if  (  result instanceof RedisClusterAsyncConnection )  {  return  AbstractRedisClient . syncHandler  (  (  RedisChannelHandler  <  ? ,  ? > ) result ,  RedisConnection . class ,  RedisClusterConnection . class ) ; }
=======
>>>>>>>
  return result ; }  catch (   InvocationTargetException e )  {  throw  e . getTargetException  ( ) ; } }   private final Object  asyncApi ;   public FutureSyncInvocationHandler  (   StatefulConnection  <  ? ,  ? > connection ,  Object asyncApi )  {    this . connection = connection ;    this . asyncApi = asyncApi ;   methodCache =   CacheBuilder . newBuilder  ( ) . build  (  new  CacheLoader  < Method , Method >  ( )  {    @ Override public Method load  (  Method key )  throws Exception  {  return   asyncApi . getClass  ( ) . getMethod  (  key . getName  ( ) ,  key . getParameterTypes  ( ) ) ; } } ) ; } }