  package       org . keedio . flume . source . ftp . client . sources ;   import       org . keedio . flume . source . ftp . client . KeedioSource ;  import      org . apache . commons . net . ftp . FTPClient ;  import      org . apache . commons . net . ftp . FTPFile ;  import        org . keedio . flume . source . ftp . client . filters . KeedioFileFilter ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import   java . io . InputStream ;  import   java . util . ArrayList ;  import   java . util . List ;  import      org . apache . commons . net . ftp . FTPReply ;  import   java . io . IOException ;  import   java . util . Arrays ;  import      org . apache . commons . net . ftp . FTP ;   public class FTPSource  extends  KeedioSource  < FTPFile >  {   private static final Logger  LOGGER =  LoggerFactory . getLogger  (  FTPSource . class ) ;   private FTPClient  ftpClient =  new FTPClient  ( ) ;    @ Override public boolean connect  ( )  {   setConnected  ( true ) ;  try  {    getFtpClient  ( ) . connect  (  getServer  ( ) ,  getPort  ( ) ) ;   int  replyCode =   getFtpClient  ( ) . getReplyCode  ( ) ;  if  (  !  FTPReply . isPositiveCompletion  ( replyCode ) )  {    getFtpClient  ( ) . disconnect  ( ) ;   LOGGER . error  ( "Connect Failed due to FTP, server refused connection." ) ;   this . setConnected  ( false ) ; }  if  (  !  (  ftpClient . login  ( user , password ) ) )  {   LOGGER . error  ( "Could not login to the server" ) ;   this . setConnected  ( false ) ; }   ftpClient . enterLocalPassiveMode  ( ) ;   ftpClient . setControlKeepAliveTimeout  ( 300 ) ;  if  (   getWorkingDirectory  ( ) != null )  {    getFtpClient  ( ) . changeWorkingDirectory  (  getWorkingDirectory  ( ) ) ; }  if  (   getBufferSize  ( ) != null )  {    getFtpClient  ( ) . setBufferSize  (  getBufferSize  ( ) ) ; } }  catch (   IOException e )  {   this . setConnected  ( false ) ;   LOGGER . error  ( "" , e ) ; }  return  isConnected  ( ) ; }    @ Override public void disconnect  ( )  {  try  {    getFtpClient  ( ) . logout  ( ) ;    getFtpClient  ( ) . disconnect  ( ) ;   setConnected  ( false ) ; }  catch (   IOException e )  {   LOGGER . error  (   "Source " +   this . getClass  ( ) . getName  ( ) + " failed disconnect" , e ) ; } }    @ Override public void changeToDirectory  (  String dir )  throws IOException  {   ftpClient . changeWorkingDirectory  ( dir ) ; }    @ Override public  List  < FTPFile > listElements  (  String dir )  throws IOException  {   FTPFile  [ ]  subFiles =   getFtpClient  ( ) . listFiles  ( dir ) ;  return  Arrays . asList  ( subFiles ) ; }    @ Override public InputStream getInputStream  (  FTPFile file )  throws IOException  {  if  (  isFlushLines  ( ) )  {   this . setFileType  (  FTP . ASCII_FILE_TYPE ) ; } else  {   this . setFileType  (  FTP . BINARY_FILE_TYPE ) ; }  return   getFtpClient  ( ) . retrieveFileStream  (  file . getName  ( ) ) ; }    @ Override public String getObjectName  (  FTPFile file )  {  return  file . getName  ( ) ; }    @ Override public boolean isDirectory  (  FTPFile file )  {  return  file . isDirectory  ( ) ; }    @ Override public boolean isFile  (  FTPFile file )  {  return  file . isFile  ( ) ; }    @ Override public boolean particularCommand  ( )  {  boolean  success = true ;  try  {   success =   getFtpClient  ( ) . completePendingCommand  ( ) ; }  catch (   IOException e )  {   LOGGER . error  ( "Error on command completePendingCommand of FTPClient" , e ) ; }  return success ; }    @ Override public  long getObjectSize  (  FTPFile file )  {  return  file . getSize  ( ) ; }    @ Override public boolean isLink  (  FTPFile file )  {  return  file . isSymbolicLink  ( ) ; }    @ Override public String getLink  (  FTPFile file )  {  return  file . getLink  ( ) ; }    @ Override public String getDirectoryserver  ( )  throws IOException  {  return   getFtpClient  ( ) . printWorkingDirectory  ( ) ; }   public FTPClient getFtpClient  ( )  {  return ftpClient ; }   public void setFtpClient  (  FTPClient ftpClient )  {    this . ftpClient = ftpClient ; }    @ Override public Object getClientSource  ( )  {  return ftpClient ; }    @ Override public void setFileType  (   int fileType )  throws IOException  {   ftpClient . setFileType  ( fileType ) ; }    @ Override public  List  < FTPFile > listElements  (  String dirToList ,  KeedioFileFilter filter )  throws IOException  {   List  < FTPFile >  list =  new  ArrayList  < >  ( ) ;   FTPFile  [ ]  subFiles =   getFtpClient  ( ) . listFiles  ( dirToList , filter ) ;   list =  Arrays . asList  ( subFiles ) ;  return list ; }    @ Override public  long getModifiedTime  (  FTPFile file )  {  return   file . getTimestamp  ( ) . getTimeInMillis  ( ) ; } }