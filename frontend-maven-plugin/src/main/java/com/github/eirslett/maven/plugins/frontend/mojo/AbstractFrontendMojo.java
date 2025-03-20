  package       com . github . eirslett . maven . plugins . frontend . mojo ;   import   java . io . File ;  import   java . util . Map ;  import     org . apache . maven . plugin . AbstractMojo ;  import     org . apache . maven . plugin . MojoExecution ;  import     org . apache . maven . plugin . MojoFailureException ;  import      org . apache . maven . plugins . annotations . Component ;  import      org . apache . maven . plugins . annotations . Parameter ;  import     org . apache . maven . project . MavenProject ;  import    org . eclipse . aether . RepositorySystemSession ;  import        com . github . eirslett . maven . plugins . frontend . lib . FrontendException ;  import        com . github . eirslett . maven . plugins . frontend . lib . FrontendPluginFactory ;  import        com . github . eirslett . maven . plugins . frontend . lib . TaskRunnerException ;   public abstract class AbstractFrontendMojo  extends AbstractMojo  {    @ Component protected MojoExecution  execution ;    @ Parameter  (  property = "skipTests" ,  required = false ,  defaultValue = "false" ) protected Boolean  skipTests ;    @ Parameter  (  property = 
<<<<<<<
"maven.frontend.failOnError"
=======
"failOnError"
>>>>>>>
 ,  required = false ,  defaultValue = "true" ) protected 
<<<<<<<
boolean
=======
Boolean
>>>>>>>
  failOnError ;    @ Parameter  (  property = "maven.test.failure.ignore" ,  required = false ,  defaultValue = "false" ) protected boolean  testFailureIgnore ;    @ Parameter  (  defaultValue = "${basedir}" ,  property = "workingDirectory" ,  required = false ) protected File  workingDirectory ;    @ Parameter  (  property = "installDirectory" ,  required = false ) protected File  installDirectory ;    @ Parameter protected  Map  < String , String >  environmentVariables ;    @ Parameter  (  defaultValue = "${project}" ,  readonly = true ) private MavenProject  project ;    @ Parameter  (  defaultValue = "${repositorySystemSession}" ,  readonly = true ) private RepositorySystemSession  repositorySystemSession ;   private boolean skipTestPhase  ( )  {  return  skipTests &&  isTestingPhase  ( ) ; }   private boolean isTestingPhase  ( )  {  String  phase =  execution . getLifecyclePhase  ( ) ;  return   "test" . equals  ( phase ) ||  "integration-test" . equals  ( phase ) ; }   protected abstract void execute  (  FrontendPluginFactory factory )  throws FrontendException ;   protected abstract boolean skipExecution  ( ) ;    @ Override public void execute  ( )  throws MojoFailureException  {  if  (  testFailureIgnore &&  !  isTestingPhase  ( ) )  {    
<<<<<<<
LoggerFactory
=======
getLog
>>>>>>>
 . getLogger  (  AbstractFrontendMojo . class ) . 
<<<<<<<
warn
=======
info
>>>>>>>
  ( "testFailureIgnore property is ignored in non test phases" ) ; }  if  (  !  (   skipTestPhase  ( ) ||  skipExecution  ( ) ) )  {  if  (  installDirectory == null )  {   installDirectory = workingDirectory ; }  try  {   execute  (  new FrontendPluginFactory  ( workingDirectory , installDirectory ,  new RepositoryCacheResolver  ( repositorySystemSession ) ) ) ; }  catch (   TaskRunnerException e )  { 
<<<<<<<
  failOnError  ( "Failed to run task" , e ) ;
=======
 if  (   !  isFailOnError  ( ) ||  testFailureIgnore &&  isTestingPhase  ( ) )  {    getLog  ( ) . error  (  "There are test failures.\nFailed to run task: " +  e . getMessage  ( ) , e ) ; } else  {  throw  new MojoFailureException  ( "Failed to run task" , e ) ; }
>>>>>>>
 }  catch (   FrontendException e )  {  throw  MojoUtils . toMojoFailureException  ( e ) ; } } else  {    getLog  ( ) . info  ( "Skipping execution." ) ; } }   protected void failOnError  (  String prefix ,  Exception e )  throws MojoFailureException  {  if  (   ! failOnError ||  (  testFailureIgnore &&  isTestingPhase  ( ) ) )  {  if  (  (  testFailureIgnore &&  isTestingPhase  ( ) ) )  {    LoggerFactory . getLogger  (  AbstractFrontendMojo . class ) . warn  (  "There are ignored test failures/errors for: " + workingDirectory ) ; }    LoggerFactory . getLogger  (  AbstractFrontendMojo . class ) . error  (   prefix + ": " +  e . getMessage  ( ) , e ) ; } else  {  if  (  e instanceof RuntimeException )  {  throw  ( RuntimeException ) e ; }  throw  new MojoFailureException  (   prefix + ": " +  e . getMessage  ( ) , e ) ; } }   protected boolean isFailOnError  ( )  {  if  (  failOnError == null )  {   failOnError =  !  isTestingPhase  ( ) ; }  return failOnError ; } }