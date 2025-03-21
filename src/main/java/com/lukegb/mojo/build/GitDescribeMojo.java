  package    com . lukegb . mojo . build ;   import   java . io . BufferedReader ;  import   java . io . File ;  import   java . io . InputStream ;  import   java . io . InputStreamReader ;  import    java . util . regex . Matcher ;  import    java . util . regex . Pattern ;  import   java . util . ArrayList ;  import   java . util . List ;  import     org . apache . maven . plugin . AbstractMojo ;  import     org . apache . maven . plugin . MojoExecutionException ;  import     org . apache . maven . project . MavenProject ;  import     org . apache . maven . scm . ScmException ;   public class GitDescribeMojo  extends AbstractMojo  {   private MavenProject  project ;   private File  scmDirectory ;    @ Deprecated private String  outputPostfix ;   private String  outputSuffix ;   private String  outputPrefix ;   private String  failOutput ;   private String  descriptionProperty ;   private String  commitCountProperty ;   private boolean  dirty ;   private String  dirtyMark ;   private boolean  tags ;   public void execute  ( )  throws MojoExecutionException  {  try  {  String  previousDescribe =  getDescribeProperty  ( ) ;  if  (  previousDescribe == null )  {  String  describe =  getDescriber  ( ) ;    getLog  ( ) . info  (  "Setting Git Describe: " + describe ) ;   setDescribeProperty  ( describe ) ;   setCommitCountProperty  (  getCommitCount  ( describe ) ) ; } }  catch (   ScmException e )  {  throw  new MojoExecutionException  ( "SCM Exception" , e ) ; } }   protected String getDescriber  ( )  throws ScmException , MojoExecutionException  {   outputPrefix =  firstNonNull  ( outputPrefix , "" ) ;   outputSuffix =  firstNonNull  ( outputSuffix , outputPostfix , "" ) ;  String  line =  commandExecutor  (  buildDescribeCommand  ( ) ) ;  if  (  line == null )  {  String  commandtwo  [ ] =  { "git" , "log" , "--pretty=format:\"%h\"" } ;   line =  commandExecutor  ( commandtwo ) ;  if  (  line == null )  {   line = failOutput ; } }  return   outputPrefix + line + outputSuffix ; }   private  String  [ ] buildDescribeCommand  ( )  {  
<<<<<<<
 List  < String >
=======
ArrayList
>>>>>>>
 
<<<<<<<
 args =  new  ArrayList  < String >  ( )
=======
 command =  new ArrayList  ( )
>>>>>>>
 ;   
<<<<<<<
args
=======
command
>>>>>>>
 . add  ( "git" ) ;   
<<<<<<<
args
=======
command
>>>>>>>
 . add  ( "describe" ) ;  if  ( dirty )  {   
<<<<<<<
args
=======
command
>>>>>>>
 . add  (  "--dirty=" + dirtyMark ) ; }  if  ( 
<<<<<<<
tags
=======
longFlag
>>>>>>>
 )  {   
<<<<<<<
args
=======
command
>>>>>>>
 . add  ( 
<<<<<<<
"--tags"
=======
"--long"
>>>>>>>
 ) ; }  return 
<<<<<<<
 args . toArray  (  new String  [  args . size  ( ) ] )
=======
 (  String  [ ] )  command . toArray  (  new String  [  command . size  ( ) ] )
>>>>>>>
 ; }   private String commandExecutor  (   String  [ ] command )  {  try  {  Process  p =    new ProcessBuilder  ( command ) . directory  ( scmDirectory ) . start  ( ) ;  InputStream  is =  p . getInputStream  ( ) ;  InputStreamReader  isr =  new InputStreamReader  ( is ) ;  BufferedReader  br =  new BufferedReader  ( isr ) ;  String  line ;   line =  br . readLine  ( ) ;  return line ; }  catch (   Exception e )  {  return null ; } }   private String getCommitCount  (  String describer )  {  Pattern  pattern =  Pattern . compile  ( "-(\\d+)-g[0-9a-f]{7}$" ) ;  Matcher  matcher =  pattern . matcher  ( describer ) ;  if  (  !  matcher . find  ( ) )  {  return failOutput ; }  String  count =  matcher . group  ( 1 ) ;  return count ; }   protected String getDescribeProperty  ( )  {  return  getProperty  ( descriptionProperty ) ; }   protected String getProperty  (  String property )  {  return   project . getProperties  ( ) . getProperty  ( property ) ; }   private void setDescribeProperty  (  String describer )  {   setProperty  ( descriptionProperty , describer ) ; }   private void setCommitCountProperty  (  String count )  {   setProperty  ( commitCountProperty , count ) ; }   private void setProperty  (  String property ,  String value )  {  if  (  value != null )  {    project . getProperties  ( ) . put  ( property , value ) ;  if  (  setReactorProjectsProperties &&  reactorProjects != null )  {  for ( Object reactorProject : reactorProjects )  {  MavenProject  nextProj =  ( MavenProject ) reactorProject ;    nextProj . getProperties  ( ) . put  ( property , value ) ; } } } }   private static String firstNonNull  (  String ...  strings )  {  for ( String string : strings )  {  if  (  string != null )  {  return string ; } }  return null ; }   private boolean  longFlag ;   private boolean  setReactorProjectsProperties ;   private List  reactorProjects ; }