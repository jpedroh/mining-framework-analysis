  package     com . xxl . job . core . executor ;   import      com . xxl . job . core . biz . AdminBiz ;  import       com . xxl . job . core . biz . client . AdminBizClient ;  import      com . xxl . job . core . handler . IJobHandler ;  import      com . xxl . job . core . log . XxlJobFileAppender ;  import      com . xxl . job . core . thread . JobLogFileCleanThread ;  import      com . xxl . job . core . thread . JobThread ;  import      com . xxl . job . core . thread . TriggerCallbackThread ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import    java . util . concurrent . ConcurrentHashMap ;  import    java . util . concurrent . ConcurrentMap ;  import      com . xxl . job . core . server . EmbedServer ;  import      com . xxl . job . core . util . IpUtil ;  import      com . xxl . job . core . util . NetUtil ;  import   java . util . ArrayList ;  import   java . util . List ;  import   java . util . Map ;   public class XxlJobExecutor  {   private static final Logger  logger =  LoggerFactory . getLogger  (  XxlJobExecutor . class ) ;   private String  adminAddresses ;   private String  accessToken ;   private String  address ;   private String  ip ;   private  int  port ;   private String  logPath ;   private  int  logRetentionDays ;   public void setAdminAddresses  (  String adminAddresses )  {    this . adminAddresses = adminAddresses ; }   public void setAccessToken  (  String accessToken )  {    this . accessToken = accessToken ; }   public void setAddress  (  String address )  {    this . address = address ; }   public void setIp  (  String ip )  {    this . ip = ip ; }   public void setPort  (   int port )  {    this . port = port ; }   public void setLogPath  (  String logPath )  {    this . logPath = logPath ; }   public void setLogRetentionDays  (   int logRetentionDays )  {    this . logRetentionDays = logRetentionDays ; }   public void start  ( )  throws Exception  {   XxlJobFileAppender . initLogPath  ( logPath ) ;   initAdminBizList  ( adminAddresses , accessToken ) ;    JobLogFileCleanThread . getInstance  ( ) . start  ( logRetentionDays ) ;    TriggerCallbackThread . getInstance  ( ) . start  ( ) ;   initEmbedServer  ( address , ip , port , appname , accessToken ) ; }   public void destroy  ( )  {   stopEmbedServer  ( ) ;  if  (   jobThreadRepository . size  ( ) > 0 )  {  for (   Map . Entry  < Long , JobThread > item :  jobThreadRepository . entrySet  ( ) )  {  JobThread  oldJobThread =  removeJobThread  (  item . getKey  ( ) , "web container destroy and kill the job." ) ;  if  (  oldJobThread != null )  {  try  {   oldJobThread . join  ( ) ; }  catch (   InterruptedException e )  {   logger . error  ( ">>>>>>>>>>> xxl-job, JobThread destroy(join) error, jobId:{}" ,  item . getKey  ( ) , e ) ; } } }   jobThreadRepository . clear  ( ) ; }   jobHandlerRepository . clear  ( ) ;    JobLogFileCleanThread . getInstance  ( ) . toStop  ( ) ;    TriggerCallbackThread . getInstance  ( ) . toStop  ( ) ; }   private static  List  < AdminBiz >  adminBizList ;   private void initAdminBizList  (  String adminAddresses ,  String accessToken )  throws Exception  {  if  (   adminAddresses != null &&    adminAddresses . trim  ( ) . length  ( ) > 0 )  {  for ( String address :   adminAddresses . trim  ( ) . split  ( "," ) )  {  if  (   address != null &&    address . trim  ( ) . length  ( ) > 0 )  {  AdminBiz  adminBiz =  new AdminBizClient  (  address . trim  ( ) , accessToken ) ;  if  (  adminBizList == null )  {   adminBizList =  new  ArrayList  < AdminBiz >  ( ) ; }   adminBizList . add  ( adminBiz ) ; } } } }   public static  List  < AdminBiz > getAdminBizList  ( )  {  return adminBizList ; }   private static  ConcurrentMap  < String , IJobHandler >  jobHandlerRepository =  new  ConcurrentHashMap  < String , IJobHandler >  ( ) ;   public static IJobHandler registJobHandler  (  String name ,  IJobHandler jobHandler )  {   logger . info  ( ">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}" , name , jobHandler ) ;  return  jobHandlerRepository . put  ( name , jobHandler ) ; }   public static IJobHandler loadJobHandler  (  String name )  {  return  jobHandlerRepository . get  ( name ) ; }   private static  ConcurrentMap  < Long , JobThread >  jobThreadRepository =  new  ConcurrentHashMap  < Long , JobThread >  ( ) ;   public static JobThread registJobThread  (   long jobId ,  IJobHandler handler ,  String removeOldReason )  {  JobThread  newJobThread =  new JobThread  ( jobId , handler ) ;   newJobThread . start  ( ) ;   logger . info  ( ">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}" ,  new Object  [ ]  { jobId , handler } ) ;  JobThread  oldJobThread =  jobThreadRepository . put  ( jobId , newJobThread ) ;  if  (  oldJobThread != null )  {   oldJobThread . toStop  ( removeOldReason ) ;   oldJobThread . interrupt  ( ) ; }  return newJobThread ; }   public static JobThread removeJobThread  (   long jobId ,  String removeOldReason )  {  JobThread  oldJobThread =  jobThreadRepository . remove  ( jobId ) ;  if  (  oldJobThread != null )  {   oldJobThread . toStop  ( removeOldReason ) ;   oldJobThread . interrupt  ( ) ;  return oldJobThread ; }  return null ; }   public static JobThread loadJobThread  (   long jobId )  {  JobThread  jobThread =  jobThreadRepository . get  ( jobId ) ;  return jobThread ; }   private String  appname ;   public void setAppname  (  String appname )  {    this . appname = appname ; }   private EmbedServer  embedServer = null ;   private void initEmbedServer  (  String address ,  String ip ,   int port ,  String appname ,  String accessToken )  throws Exception  {   port =   port > 0 ? port :  NetUtil . findAvailablePort  ( 9999 ) ;   ip =   (   ip != null &&    ip . trim  ( ) . length  ( ) > 0 ) ? ip :  IpUtil . getIp  ( ) ;  if  (   address == null ||    address . trim  ( ) . length  ( ) == 0 )  {  String  ip_port_address =  IpUtil . getIpPort  ( ip , port ) ;   address =  "http://{ip_port}/" . replace  ( "{ip_port}" , ip_port_address ) ; }   embedServer =  new EmbedServer  ( ) ;   embedServer . start  ( address , port , appname , accessToken ) ; }   private void stopEmbedServer  ( )  {  try  {   embedServer . stop  ( ) ; }  catch (   Exception e )  {   logger . error  (  e . getMessage  ( ) , e ) ; } } }