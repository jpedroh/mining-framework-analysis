  package     net . technicpack . launchercore . mirror . secure ;   import     net . technicpack . launchercore . exception . DownloadException ;  import       net . technicpack . launchercore . mirror . secure . rest . ISecureMirror ;  import   java . util . Date ;  import     net . technicpack . launchercore . auth . UserModel ;   public class SecureToken  {   private String  token ;   private Date  receivedTime ;   private UserModel  userModel ;   private ISecureMirror  mirror ;   private String  tokenUserName ;   private String  tokenAccessToken ;   public SecureToken  (  UserModel userModel ,  ISecureMirror mirror )  {    this . token = null ;    this . receivedTime = null ;    this . userModel = userModel ;    this . mirror = mirror ; }   public String getDownloadHost  ( )  {  return  mirror . getDownloadHost  ( ) ; }   public String queryForSecureToken  ( )  throws DownloadException  {  return null ; } }