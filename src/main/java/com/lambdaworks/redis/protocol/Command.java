  package    com . lambdaworks . redis . protocol ;   import     com . lambdaworks . redis . output . CommandOutput ;  import    io . netty . buffer . ByteBuf ;   public class Command  <  K ,  V ,  T >  implements   RedisCommand  < K , V , T >  {   private static final   byte  [ ]  CRLF =  "\r\n" . getBytes  (  LettuceCharsets . ASCII ) ;   private final ProtocolKeyword  type ;   protected  CommandArgs  < K , V >  args ;   protected  CommandOutput  < K , V , T >  output ;   protected Throwable  exception ;   protected boolean  cancelled = false ;   protected boolean  completed = false ;   public Command  (  ProtocolKeyword type ,   CommandOutput  < K , V , T > output ,   CommandArgs  < K , V > args )  {    this . type = type ;    this . output = output ;    this . args = args ; }    @ Override public  CommandOutput  < K , V , T > getOutput  ( )  {  return output ; }    @ Override public boolean completeExceptionally  (  Throwable 
<<<<<<<
throwable
=======
ex
>>>>>>>
 )  {  boolean  result = false ;  if  (  
<<<<<<<
output
=======
 latch . getCount  ( )
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 
<<<<<<<
null
=======
1
>>>>>>>
 )  {  
<<<<<<<
 output . setError  (  throwable . getMessage  ( ) )
=======
 result =  super . completeExceptionally  ( ex )
>>>>>>>
 ; }  
<<<<<<<
 exception = throwable
=======
 latch . countDown  ( )
>>>>>>>
 ;  return 
<<<<<<<
true
=======
result
>>>>>>>
 ; }    @ Override public void complete  ( )  { 
<<<<<<<
=======
 if  (   latch . getCount  ( ) == 1 )  {  if  (  output == null )  {   complete  ( null ) ; } else  if  (  output . hasError  ( ) )  {   completeExceptionally  (  new RedisCommandExecutionException  (  output . getError  ( ) ) ) ; } else  {   complete  (  output . get  ( ) ) ; } }
>>>>>>>
  
<<<<<<<
 completed = true
=======
 latch . countDown  ( )
>>>>>>>
 ; }    @ Override public void cancel  ( )  {   cancelled = true ; }   public void encode  (  ByteBuf buf )  {   buf . writeByte  ( '*' ) ;   writeInt  ( buf ,  1 +  (   args != null ?  args . count  ( ) : 0 ) ) ;   buf . writeBytes  ( CRLF ) ;   buf . writeByte  ( '$' ) ;   writeInt  ( buf ,   type . getBytes  ( ) . length ) ;   buf . writeBytes  ( CRLF ) ;   buf . writeBytes  (  type . getBytes  ( ) ) ;   buf . writeBytes  ( CRLF ) ;  if  (  args != null )  {   buf . writeBytes  (  args . buffer  ( ) ) ; } }   protected static void writeInt  (  ByteBuf buf ,   int value )  {  if  (  value < 10 )  {   buf . writeByte  (  '0' + value ) ;  return ; }  StringBuilder  sb =  new StringBuilder  ( 8 ) ;  while  (  value > 0 )  {   int  digit =  value % 10 ;   sb . append  (  (  char )  (  '0' + digit ) ) ;   value /= 10 ; }  for (   int  i =   sb . length  ( ) - 1 ;  i >= 0 ;  i -- )  {   buf . writeByte  (  sb . charAt  ( i ) ) ; } }   public String getError  ( )  {  return  output . getError  ( ) ; }    @ Override public  CommandArgs  < K , V > getArgs  ( )  {  return args ; }   public T get  ( )  {  if  (  output != null )  {  return  output . get  ( ) ; }  return null ; }    @ Override public String toString  ( )  {   final StringBuilder  sb =  new StringBuilder  ( ) ;   sb . append  (   getClass  ( ) . getSimpleName  ( ) ) ;    sb . append  ( " [type=" ) . append  ( type ) ;    sb . append  ( ", output=" ) . append  ( output ) ;   sb . append  ( ']' ) ;  return  sb . toString  ( ) ; }   public void setOutput  (   CommandOutput  < K , V , T > output )  {  if  (   isCancelled  ( ) || completed )  {  throw  new IllegalStateException  ( "Command is completed/cancelled. Cannot set a new output" ) ; }    this . output = output ; }    @ Override public ProtocolKeyword getType  ( )  {  return type ; }    @ Override public boolean isCancelled  ( )  {  return cancelled ; } }