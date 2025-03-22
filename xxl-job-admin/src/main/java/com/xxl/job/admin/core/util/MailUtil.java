  package      com . xxl . job . admin . core . util ;   import       com . xxl . job . admin . core . conf . XxlJobAdminConfig ;  import     org . apache . commons . mail . DefaultAuthenticator ;  import     org . apache . commons . mail . EmailException ;  import     org . apache . commons . mail . HtmlEmail ;  import   org . slf4j . Logger ;  import   org . slf4j . LoggerFactory ;  import    java . nio . charset . Charset ;   public class MailUtil  {   private static Logger  logger =  LoggerFactory . getLogger  (  MailUtil . class ) ;   public static boolean sendMail  (  String toAddress ,  String mailSubject ,  String mailBody )  {  try  {  HtmlEmail  email =  new HtmlEmail  ( ) ;   email . setSSLOnConnect  ( true ) ;   email . setHostName  (   XxlJobAdminConfig . getAdminConfig  ( ) . getMailHost  ( ) ) ; 
<<<<<<<
 if  (   XxlJobAdminConfig . getAdminConfig  ( ) . isMailSSL  ( ) )  {   email . setSslSmtpPort  (   XxlJobAdminConfig . getAdminConfig  ( ) . getMailPort  ( ) ) ;   email . setSSLOnConnect  ( true ) ; } else  {   email . setSmtpPort  (  Integer . valueOf  (   XxlJobAdminConfig . getAdminConfig  ( ) . getMailPort  ( ) ) ) ; }
=======
  int  port =  Integer . valueOf  (   XxlJobAdminConfig . getAdminConfig  ( ) . getMailPort  ( ) ) ;
>>>>>>>
 
<<<<<<<
=======
  email . setSslSmtpPort  (  port + "" ) ;
>>>>>>>
   email . setAuthenticator  (  new DefaultAuthenticator  (   XxlJobAdminConfig . getAdminConfig  ( ) . getMailUsername  ( ) ,   XxlJobAdminConfig . getAdminConfig  ( ) . getMailPassword  ( ) ) ) ;   email . setCharset  ( "UTF-8" ) ;   email . setFrom  (   XxlJobAdminConfig . getAdminConfig  ( ) . getMailUsername  ( ) ,   XxlJobAdminConfig . getAdminConfig  ( ) . getMailSendNick  ( ) ) ;   email . addTo  ( toAddress ) ;   email . setSubject  ( mailSubject ) ;   email . setMsg  ( mailBody ) ;   email . setSocketTimeout  ( 180000 ) ;   email . setSocketConnectionTimeout  ( 180000 ) ;   email . send  ( ) ;  return true ; }  catch (   EmailException e )  {   logger . error  (  e . getMessage  ( ) , e ) ; }  return false ; } }