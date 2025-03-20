  package    org . sonar . plugins . objectivec ;   import   java . util . List ;  import    org . sonar . api . Extension ;  import    org . sonar . api . Properties ;  import    org . sonar . api . Property ;  import    org . sonar . api . SonarPlugin ;  import      org . sonar . plugins . objectivec . colorizer . ObjectiveCColorizerFormat ;  import      org . sonar . plugins . objectivec . core . ObjectiveC ;  import      org . sonar . plugins . objectivec . core . ObjectiveCSourceImporter ;  import      org . sonar . plugins . objectivec . cpd . ObjectiveCCpdMapping ;  import     com . google . common . collect . ImmutableList ;    @ Properties  (  {  @ Property  (  key =  
<<<<<<<
OCLintSensor
=======
ObjectiveCCoverageSensor
>>>>>>>
 . 
<<<<<<<
REPORT_PATH_KEY
=======
REPORT_PATTERN_KEY
>>>>>>>
 ,  defaultValue =  
<<<<<<<
OCLintSensor
=======
ObjectiveCCoverageSensor
>>>>>>>
 . 
<<<<<<<
DEFAULT_REPORT_PATH
=======
DEFAULT_REPORT_PATTERN
>>>>>>>
 ,  name = 
<<<<<<<
"Path to oclint pmd formatted report"
=======
"Path to unit test coverage report(s)"
>>>>>>>
 ,  description = 
<<<<<<<
"Relative to projects' root."
=======
"Relative to projects' root. Ant patterns are accepted"
>>>>>>>
 ,  global = false ,  project = true ) , } ) public class ObjectiveCPlugin  extends SonarPlugin  {   public  List  <  Class  <  ? extends Extension > > getExtensions  ( )  {  return  ImmutableList . of  (  ObjectiveC . class ,  ObjectiveCSourceImporter . class ,  ObjectiveCColorizerFormat . class ,  ObjectiveCCpdMapping . class ,  ObjectiveCSquidSensor . class ,  ObjectiveCProfile . class ,  OCLintRuleRepository . class ,  OCLintSensor . class ,  OCLintProfile . class ,  
<<<<<<<
OCLintProfileImporter
=======
ObjectiveCCoverageSensor
>>>>>>>
 . class ) ; }   public static final String  FALSE = "false" ;   public static final String  FILE_SUFFIXES_KEY = "sonar.objectivec.file.suffixes" ;   public static final String  FILE_SUFFIXES_DEFVALUE = "h,m" ;   public static final String  PROPERTY_PREFIX = "sonar.objectivec" ;   public static final String  TEST_FRAMEWORK_KEY =  PROPERTY_PREFIX + ".testframework" ;   public static final String  TEST_FRAMEWORK_DEFAULT = "ghunit" ; }