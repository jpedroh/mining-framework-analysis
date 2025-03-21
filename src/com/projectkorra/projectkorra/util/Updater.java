  package    com . projectkorra . projectkorra . util ;   import    org . bukkit . plugin . Plugin ;  import   java . io . BufferedReader ;  import   java . io . IOException ;  import   java . io . InputStreamReader ;  import   java . net . URL ;  import   java . net . URLConnection ;   public class Updater  {   private URL  url ;   private URLConnection  urlc ;   private String  updateVersion ;   private String  currentVersion ;   private Plugin  plugin ;   private boolean  checkUpdate ;   private String  pluginName ;   public Updater  (  Plugin plugin ,  String URL ,  boolean checkForUpdate )  {    this . plugin = plugin ;    this . checkUpdate = checkForUpdate ;  if  ( checkUpdate )  {   runAsync  ( plugin ,   ( ) ->  {  try  {    plugin . getLogger  ( ) . info  ( "Checking for updates...!" ) ;   url =  new URL  ( URL ) ;   urlc =  url . openConnection  ( ) ;   urlc . setRequestProperty  ( "User-Agent" , "Mozilla/5.0" ) ;   urlc . setConnectTimeout  ( 30000 ) ;  try  (  BufferedReader reader =  new BufferedReader  (  new InputStreamReader  (  urlc . getInputStream  ( ) ) ) )  {  String  line ;  while  (   (  line =  reader . readLine  ( ) ) != null )  {  if  (  
<<<<<<<
line
=======
 line . toLowerCase  ( )
>>>>>>>
 . 
<<<<<<<
contains
=======
matches
>>>>>>>
  ( 
<<<<<<<
"<h3>Version "
=======
".*<span class=\"u-muted\">[0-9\\. formcr+]{1,23}<\\/span>.*"
>>>>>>>
 ) )  {   line =  line . trim  ( ) ;   updateVersion =   
<<<<<<<
line
=======
  line . split  ( "<span class=\"u-muted\">" ) [ 1 ]
>>>>>>>
 . split  ( 
<<<<<<<
"<h3>Version "
=======
"<\\/span>"
>>>>>>>
 ) [ 
<<<<<<<
1
=======
0
>>>>>>>
 ] ; 
<<<<<<<
  updateVersion =  updateVersion . substring  ( 0 ,   updateVersion . length  ( ) - 5 ) ;
=======
 break ;
>>>>>>>
 } } }  catch (   IOException e )  {   e . printStackTrace  ( ) ; } }  catch (   IOException e )  {    plugin . getLogger  ( ) . info  ( 
<<<<<<<
"Could not connect to ProjectKorra.com"
=======
"Could not connect to projectkorra.com"
>>>>>>>
 ) ; }   checkUpdate  ( ) ; } ) ; }    this . currentVersion =   plugin . getDescription  ( ) . getVersion  ( ) ;    this . pluginName =   plugin . getDescription  ( ) . getName  ( ) ; }   private void runAsync  (  Plugin plugin ,  Runnable run )  {     plugin . getServer  ( ) . getScheduler  ( ) . runTaskAsynchronously  ( plugin , run ) ; }   public void checkUpdate  ( )  {  if  (  !  isEnabled  ( ) )  return ;  if  (   getUpdateVersion  ( ) == null )  {    plugin . getLogger  ( ) . info  ( "Something went wrong while trying to retrieve the latest version." ) ;  return ; }  if  (  updateAvailable  ( ) )  {    plugin . getLogger  ( ) . info  ( "===================[Update Available]===================" ) ;    plugin . getLogger  ( ) . info  (  "You are running version " +  getCurrentVersion  ( ) ) ;    plugin . getLogger  ( ) . info  (  "The latest version available is " +  getUpdateVersion  ( ) ) ; } else  {    plugin . getLogger  ( ) . info  (  "You are running the latest version of " + pluginName ) ; } }   public String getUpdateVersion  ( )  {  return updateVersion ; }   public boolean updateAvailable  ( )  {  String  updateVersion =  getUpdateVersion  ( ) ;  if  (  updateVersion == null )  return false ;  String  numericUpdateVersion =   updateVersion . split  ( " " ) [ 0 ] ;  String  numericCurrentVersion =   currentVersion . split  ( " " ) [ 0 ] ;   int  currentNumber =  Integer . parseInt  (  
<<<<<<<
currentVersion
=======
numericCurrentVersion
>>>>>>>
 . replaceAll  ( "[^\\d]" , "" ) ) ;   int  updateNumber =  Integer . parseInt  (  
<<<<<<<
updateVersion
=======
numericUpdateVersion
>>>>>>>
 . replaceAll  ( "[^\\d]" , "" ) ) ;  return  
<<<<<<<
currentNumber
=======
 currentNumber < updateNumber
>>>>>>>
 
<<<<<<<
<
=======
||
>>>>>>>
 
<<<<<<<
updateNumber
=======
  currentVersion . hashCode  ( ) !=  updateVersion . hashCode  ( )
>>>>>>>
 ; }   public URL getUrl  ( )  {  return url ; }   public String getCurrentVersion  ( )  {  return currentVersion ; }   public boolean isEnabled  ( )  {  return checkUpdate ; } }