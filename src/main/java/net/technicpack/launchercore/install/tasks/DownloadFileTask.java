  package     net . technicpack . launchercore . install . tasks ;   import     net . technicpack . launchercore . exception . DownloadException ;  import   java . io . File ;  import   java . io . IOException ;  import     net . technicpack . launchercore . install . InstallTasksQueue ;  import      net . technicpack . launchercore . install . verifiers . IFileVerifier ;   public class DownloadFileTask  extends ListenerTask  {   private String  url ;   private File  destination ;   private String  taskDescription ;   private IFileVerifier  fileVerifier ;   public DownloadFileTask  (  String url ,  File destination ,  IFileVerifier verifier )  {  this  ( url , destination , verifier ,  destination . getName  ( ) ) ; }   public DownloadFileTask  (  String url ,  File destination ,  IFileVerifier verifier ,  String taskDescription )  {    this . url = url ;    this . destination = destination ;    this . taskDescription = taskDescription ;    this . fileVerifier = verifier ; }    @ Override public String getTaskDescription  ( )  {  return taskDescription ; }    @ Override public void runTask  (  InstallTasksQueue queue )  throws IOException , InterruptedException  {   super . runTask  ( queue ) ;    queue . getMirrorStore  ( ) . downloadFile  ( url ,   this . destination . getName  ( ) ,   this . destination . getAbsolutePath  ( ) , null , fileVerifier , this ) ;  if  (  !   this . destination . exists  ( ) )  {  throw  new DownloadException  (   "Failed to download " +   this . destination . getName  ( ) + "." ) ; } } }