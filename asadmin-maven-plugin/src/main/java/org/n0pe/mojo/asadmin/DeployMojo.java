  package    org . n0pe . mojo . asadmin ;   import     org . apache . commons . lang . StringUtils ;  import    org . n0pe . asadmin . AsAdminCmdList ;  import     org . n0pe . asadmin . commands . Deployment ;   public class DeployMojo  extends AbstractAsadminMojo  {   private String  target ;   private boolean  force ;    @ Override protected AsAdminCmdList getAsCommandList  ( )  {    getLog  ( ) . info  (  "Deploying application archive: " + appArchive ) ;   final AsAdminCmdList  list =  new AsAdminCmdList  ( ) ;   final Deployment  d =    new Deployment  ( ) . archive  ( appArchive ) . target  ( target ) ;  if  (   "war" . equalsIgnoreCase  (  mavenProject . getPackaging  ( ) ) &&  !  StringUtils . isEmpty  ( contextRoot ) )  {   d . withContextRoot  ( contextRoot ) ; }  if  (  !  StringUtils . isEmpty  ( appName ) )  {   d . appName  ( appName ) ; }   list . add  (   d . 
<<<<<<<
force
=======
availability
>>>>>>>
  ( 
<<<<<<<
force
=======
availabilityenabled
>>>>>>>
 ) . deploy  ( ) ) ;   setPatterns  ( d ) ;  return list ; }   private Boolean  availabilityenabled = null ; }