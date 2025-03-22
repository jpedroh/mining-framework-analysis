  package    com . keybox . manage . db ;   import     com . keybox . manage . model . PublicKey ;  import     com . keybox . manage . model . SortedSet ;  import     com . keybox . manage . util . DBUtils ;  import     com . keybox . manage . util . EncryptionUtil ;  import   java . sql . Connection ;  import   java . sql . PreparedStatement ;  import   java . sql . ResultSet ;  import   java . sql . Types ;  import   java . util . ArrayList ;  import   java . util . List ;  import     com . keybox . manage . util . SSHUtil ;  import     org . apache . commons . lang3 . StringUtils ;   public class PublicKeyDB  {   public static final String  SORT_BY_KEY_NM = "key_nm" ;   public static final String  SORT_BY_PROFILE = "profile_id" ;   public static final String  SORT_BY_KEY_TP = "key_tp" ;   public static final String  SORT_BY_KEY_FP = "key_fp" ;   public static SortedSet getPublicKeySet  (  SortedSet sortedSet )  {   ArrayList  < PublicKey >  publicKeysList =  new  ArrayList  < PublicKey >  ( ) ;  String  orderBy = "" ;  if  (    sortedSet . getOrderByField  ( ) != null &&  !    sortedSet . getOrderByField  ( ) . trim  ( ) . equals  ( "" ) )  {   orderBy =    " order by " +  sortedSet . getOrderByField  ( ) + " " +  sortedSet . getOrderByDirection  ( ) ; }  String  sql = "select p.*, u.username from public_keys p, users u where u.id=p.user_id  " ;   sql +=   StringUtils . isNotEmpty  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_USER_ID ) ) ? " and p.user_id=? " : "" ;   sql +=   StringUtils . isNotEmpty  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_PROFILE_ID ) ) ? " and p.profile_id=? " : "" ;   sql +=   StringUtils . isNotEmpty  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_ENABLED ) ) ? " and p.enabled=? " : " and p.enabled=true" ;   sql =  sql + orderBy ;  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( sql ) ;   int  i = 1 ;  if  (  StringUtils . isNotEmpty  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_USER_ID ) ) )  {   stmt . setLong  (  i ++ ,  Long . valueOf  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_USER_ID ) ) ) ; }  if  (  StringUtils . isNotEmpty  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_PROFILE_ID ) ) )  {   stmt . setLong  (  i ++ ,  Long . valueOf  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_PROFILE_ID ) ) ) ; }  if  (  StringUtils . isNotEmpty  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_ENABLED ) ) )  {   stmt . setBoolean  (  i ++ ,  Boolean . valueOf  (   sortedSet . getFilterMap  ( ) . get  ( FILTER_BY_ENABLED ) ) ) ; }  ResultSet  rs =  stmt . executeQuery  ( ) ;  while  (  rs . next  ( ) )  {  String  strTempPublicKey =  rs . getString  ( "public_key" ) ;  PublicKey  publicKey =  new PublicKey  ( ) ;   publicKey . setId  (  rs . getLong  ( "id" ) ) ;   publicKey . setKeyNm  (  rs . getString  ( "key_nm" ) ) ;   publicKey . setPublicKey  ( strTempPublicKey ) ;   publicKey . setKeyTp  (  EncryptionUtil . generateKeyType  ( strTempPublicKey ) ) ;   publicKey . 
<<<<<<<
setKeyFp
=======
setType
>>>>>>>
  (  
<<<<<<<
EncryptionUtil
=======
SSHUtil
>>>>>>>
 . 
<<<<<<<
generateFingerprint
=======
getKeyType
>>>>>>>
  ( 
<<<<<<<
strTempPublicKey
=======
 publicKey . getPublicKey  ( )
>>>>>>>
 ) ) ;   publicKey . setFingerprint  (  SSHUtil . getFingerprint  (  publicKey . getPublicKey  ( ) ) ) ;   publicKey . setCreateDt  (  rs . getTimestamp  ( "create_dt" ) ) ;   publicKey . setUsername  (  rs . getString  ( "username" ) ) ;   publicKey . setUserId  (  rs . getLong  ( "user_id" ) ) ;   publicKey . setEnabled  (  rs . getBoolean  ( "enabled" ) ) ;   publicKeysList . add  ( publicKey ) ; }   DBUtils . closeRs  ( rs ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ;   sortedSet . setItemList  ( publicKeysList ) ;  return sortedSet ; }   public static SortedSet getPublicKeySet  (  SortedSet sortedSet ,  Long userId )  {   ArrayList  < PublicKey >  publicKeysList =  new  ArrayList  < PublicKey >  ( ) ;  String  orderBy = "" ;  if  (    sortedSet . getOrderByField  ( ) != null &&  !    sortedSet . getOrderByField  ( ) . trim  ( ) . equals  ( "" ) )  {   orderBy =    "order by " +  sortedSet . getOrderByField  ( ) + " " +  sortedSet . getOrderByDirection  ( ) ; }  String  sql =  "select * from public_keys where user_id = ? and enabled=true " + orderBy ;  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( sql ) ;   stmt . setLong  ( 1 , userId ) ;  ResultSet  rs =  stmt . executeQuery  ( ) ;  while  (  rs . next  ( ) )  {  String  strTempPublicKey =  rs . getString  ( "public_key" ) ;  PublicKey  publicKey =  new PublicKey  ( ) ;   publicKey . setId  (  rs . getLong  ( "id" ) ) ;   publicKey . setKeyNm  (  rs . getString  ( "key_nm" ) ) ;   publicKey . setPublicKey  ( strTempPublicKey ) ;   publicKey . setKeyTp  (  EncryptionUtil . generateKeyType  ( strTempPublicKey ) ) ;   publicKey . 
<<<<<<<
setKeyFp
=======
setType
>>>>>>>
  (  
<<<<<<<
EncryptionUtil
=======
SSHUtil
>>>>>>>
 . 
<<<<<<<
generateFingerprint
=======
getKeyType
>>>>>>>
  ( 
<<<<<<<
strTempPublicKey
=======
 publicKey . getPublicKey  ( )
>>>>>>>
 ) ) ;   publicKey . setFingerprint  (  SSHUtil . getFingerprint  (  publicKey . getPublicKey  ( ) ) ) ;   publicKey . setCreateDt  (  rs . getTimestamp  ( "create_dt" ) ) ;   publicKeysList . add  ( publicKey ) ; }   DBUtils . closeRs  ( rs ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ;   sortedSet . setItemList  ( publicKeysList ) ;  return sortedSet ; }   public static PublicKey getPublicKey  (  Long publicKeyId )  {  PublicKey  publicKey = null ;  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;   publicKey =  getPublicKey  ( con , publicKeyId ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ;  return publicKey ; }   public static PublicKey getPublicKey  (  Connection con ,  Long publicKeyId )  {  PublicKey  publicKey = null ;  try  {  PreparedStatement  stmt =  con . prepareStatement  ( "select * from  public_keys where id=?" ) ;   stmt . setLong  ( 1 , publicKeyId ) ;  ResultSet  rs =  stmt . executeQuery  ( ) ;  while  (  rs . next  ( ) )  {  String  strTempPublicKey =  rs . getString  ( "public_key" ) ;   publicKey =  new PublicKey  ( ) ;   publicKey . setId  (  rs . getLong  ( "id" ) ) ;   publicKey . setKeyNm  (  rs . getString  ( "key_nm" ) ) ;   publicKey . setPublicKey  (  rs . getString  ( strTempPublicKey ) ) ;   publicKey . 
<<<<<<<
setKeyTp
=======
setType
>>>>>>>
  (  
<<<<<<<
EncryptionUtil
=======
rs
>>>>>>>
 . 
<<<<<<<
generateKeyType
=======
getString
>>>>>>>
  ( 
<<<<<<<
strTempPublicKey
=======
"type"
>>>>>>>
 ) ) ;   publicKey . 
<<<<<<<
setKeyFp
=======
setFingerprint
>>>>>>>
  (  
<<<<<<<
EncryptionUtil
=======
rs
>>>>>>>
 . 
<<<<<<<
generateFingerprint
=======
getString
>>>>>>>
  ( 
<<<<<<<
strTempPublicKey
=======
"fingerprint"
>>>>>>>
 ) ) ;   publicKey . setCreateDt  (  rs . getTimestamp  ( "create_dt" ) ) ; }   DBUtils . closeRs  ( rs ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }  return publicKey ; }   public static void insertPublicKey  (  PublicKey publicKey )  {  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( 
<<<<<<<
"insert into public_keys(key_nm, public_key, key_tp, key_fp, profile_id, user_id) values (?,?,?,?,?,?)"
=======
"insert into public_keys(key_nm, type, fingerprint, public_key, profile_id, user_id) values (?,?,?,?,?,?)"
>>>>>>>
 ) ;   stmt . setString  ( 1 ,  publicKey . getKeyNm  ( ) ) ;   stmt . setString  ( 2 ,  SSHUtil . getKeyType  (  publicKey . getPublicKey  ( ) ) ) ;   stmt . setString  ( 3 ,  
<<<<<<<
publicKey
=======
SSHUtil
>>>>>>>
 . 
<<<<<<<
getKeyTp
=======
getFingerprint
>>>>>>>
  (  publicKey . getPublicKey  ( ) ) ) ;   stmt . setString  ( 4 ,  
<<<<<<<
publicKey
=======
 publicKey . getPublicKey  ( )
>>>>>>>
 . 
<<<<<<<
getKeyFp
=======
trim
>>>>>>>
  ( ) ) ;  if  (    publicKey . getProfile  ( ) == null ||    publicKey . getProfile  ( ) . getId  ( ) == null )  {   stmt . setNull  ( 5 ,  Types . NULL ) ; } else  {   stmt . setLong  ( 5 ,   publicKey . getProfile  ( ) . getId  ( ) ) ; }   stmt . setLong  ( 6 ,  publicKey . getUserId  ( ) ) ;   stmt . execute  ( ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ; }   public static void updatePublicKey  (  PublicKey publicKey )  {  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( 
<<<<<<<
"update public_keys set key_nm=?, public_key=?, key_tp=?, key_fp=?, profile_id=? where id=? and user_id=?"
=======
"update public_keys set key_nm=?, type=?, fingerprint=?, public_key=?, profile_id=? where id=? and user_id=? and enabled=true"
>>>>>>>
 ) ;   stmt . setString  ( 1 ,  publicKey . getKeyNm  ( ) ) ;   stmt . setString  ( 2 ,  SSHUtil . getKeyType  (  publicKey . getPublicKey  ( ) ) ) ;   stmt . setString  ( 3 ,  
<<<<<<<
publicKey
=======
SSHUtil
>>>>>>>
 . 
<<<<<<<
getKeyTp
=======
getFingerprint
>>>>>>>
  (  publicKey . getPublicKey  ( ) ) ) ;   stmt . setString  ( 4 ,  
<<<<<<<
publicKey
=======
 publicKey . getPublicKey  ( )
>>>>>>>
 . 
<<<<<<<
getKeyFp
=======
trim
>>>>>>>
  ( ) ) ;  if  (    publicKey . getProfile  ( ) == null ||    publicKey . getProfile  ( ) . getId  ( ) == null )  {   stmt . setNull  ( 5 ,  Types . NULL ) ; } else  {   stmt . setLong  ( 5 ,   publicKey . getProfile  ( ) . getId  ( ) ) ; }   stmt . setLong  ( 6 ,  publicKey . getId  ( ) ) ;   stmt . setLong  ( 7 ,  publicKey . getUserId  ( ) ) ;   stmt . execute  ( ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ; }   public static void deletePublicKey  (  Long publicKeyId ,  Long userId )  {  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( "delete from public_keys where id=? and user_id=? and enabled=true" ) ;   stmt . setLong  ( 1 , publicKeyId ) ;   stmt . setLong  ( 2 , userId ) ;   stmt . execute  ( ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ; }   public static void deleteUserPublicKeys  (  Long userId )  {  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( "delete from public_keys where user_id=? and enabled=true" ) ;   stmt . setLong  ( 1 , userId ) ;   stmt . execute  ( ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ; }   public static void deleteProfilePublicKeys  (  Long profileId )  {  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( "delete from public_keys where profile_id=? and enabled=true" ) ;   stmt . setLong  ( 1 , profileId ) ;   stmt . execute  ( ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ; }   public static  List  < String > getPublicKeysForSystem  (  Long systemId )  {  Connection  con = null ;   List  < String >  publicKeyList =  new  ArrayList  < String >  ( ) ;  try  {   con =  DBUtils . getConn  ( ) ;   publicKeyList =  getPublicKeysForSystem  ( con , systemId ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ;  return publicKeyList ; }   public static  List  < String > getPublicKeysForSystem  (  Connection con ,  Long systemId )  {   List  < String >  publicKeyList =  new  ArrayList  < String >  ( ) ;  if  (  systemId == null )  {   systemId =  - 99L ; }  try  {  PreparedStatement  stmt =  con . prepareStatement  ( "select * from public_keys where (profile_id is null or profile_id in (select profile_id from system_map where system_id=?)) and enabled=true" ) ;   stmt . setLong  ( 1 , systemId ) ;  ResultSet  rs =  stmt . executeQuery  ( ) ;  while  (  rs . next  ( ) )  {   publicKeyList . add  (  rs . getString  ( "public_key" ) ) ; }   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }  return publicKeyList ; }   public static final String  FILTER_BY_USER_ID = "user_id" ;   public static final String  FILTER_BY_PROFILE_ID = "profile_id" ;   public static final String  FILTER_BY_ENABLED = "enabled" ;   public static final String  SORT_BY_TYPE = "type" ;   public static final String  SORT_BY_FINGERPRINT = "fingerprint" ;   public static final String  SORT_BY_CREATE_DT = "create_dt" ;   public static final String  SORT_BY_USERNAME = "username" ;   public static void disableKey  (  Long id )  {  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( "update public_keys set enabled=false where id=?" ) ;   stmt . setLong  ( 1 , id ) ;   stmt . execute  ( ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ; }   public static void enableKey  (  Long id )  {  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( "update public_keys set enabled=true where id=?" ) ;   stmt . setLong  ( 1 , id ) ;   stmt . execute  ( ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ; }   public static boolean isKeyDisabled  (  String fingerprint )  {  boolean  isDisabled = false ;  Connection  con = null ;  try  {   con =  DBUtils . getConn  ( ) ;  PreparedStatement  stmt =  con . prepareStatement  ( "select * from  public_keys where fingerprint like ? and enabled=false" ) ;   stmt . setString  ( 1 , fingerprint ) ;  ResultSet  rs =  stmt . executeQuery  ( ) ;  if  (  rs . next  ( ) )  {   isDisabled = true ; }   DBUtils . closeRs  ( rs ) ;   DBUtils . closeStmt  ( stmt ) ; }  catch (   Exception e )  {   e . printStackTrace  ( ) ; }   DBUtils . closeConn  ( con ) ;  return isDisabled ; } }