  package     com . xxl . job . service . handler ;   import    java . util . concurrent . TimeUnit ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import    org . springframework . stereotype . Service ;  import      com . xxl . job . client . handler . HandlerRepository ;  import      com . xxl . job . client . handler . IJobHandler ;    @ Service public class DemoJobHandler  extends IJobHandler  {   private static transient Logger  logger =  LoggerFactory . getLogger  (  DemoJobHandler . class ) ;   public DemoJobHandler  ( )  {   HandlerRepository . regist  ( "demoJobHandler" , this ) ; }    @ Override public JobHandleStatus handle  (  String ...  params )  throws Exception  {   logger . info  (  " ... params:" + params ) ;  for (   int  i = 0 ;  i < 
<<<<<<<
60
=======
10
>>>>>>>
 ;  i ++ )  {    TimeUnit . SECONDS . sleep  ( 1 ) ;   logger . info  ( "handler run:{}" , i ) ; }  return  JobHandleStatus . SUCCESS ; }   public static void main  (   String  [ ] args )  {    System . out . println  (   DemoJobHandler . class . getName  ( ) ) ;    System . out . println  (  DemoJobHandler . class ) ; } }