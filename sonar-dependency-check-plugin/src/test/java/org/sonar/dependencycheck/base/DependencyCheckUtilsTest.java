  package    org . sonar . dependencycheck . base ;   import   org . junit . Test ;  import    org . junit . runner . RunWith ;  import    org . junit . runners . Parameterized ;  import      org . sonar . api . batch . rule . Severity ;  import   java . util . Arrays ;  import   java . util . Collection ;  import static     org . fest . assertions . Assertions . assertThat ;    @ RunWith  (  Parameterized . class ) public class DependencyCheckUtilsTest  {   private final Float  cvssSeverity ;   private final Float  critical ;   private final Float  major ;   private final Severity  expectedSeverity ;   public DependencyCheckUtilsTest  (  Float cvssSeverity ,  Float critical ,  Float major ,  Severity expectedSeverity )  {    this . cvssSeverity = cvssSeverity ;    this . critical = critical ;    this . major = major ;    this . expectedSeverity = expectedSeverity ; }    @  Parameterized . Parameters public static  Collection  <  Object  [ ] > severities  ( )  {  return  Arrays . asList  (  new Object  [ ] [ ]  {  {  Float . valueOf  ( "10.0" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  {  Float . valueOf  ( "7.0" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  {  Float . valueOf  ( "6.9" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  {  Float . valueOf  ( "4.0" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  {  Float . valueOf  ( "3.9" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . MINOR } ,  {  Float . valueOf  ( "0.0" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . MINOR } ,  {  Float . valueOf  ( "10.0" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "1.0" ) ,  Severity . CRITICAL } ,  {  Float . valueOf  ( "7.0" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "1.0" ) ,  Severity . CRITICAL } ,  {  Float . valueOf  ( "6.9" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "1.0" ) ,  Severity . CRITICAL } 
<<<<<<<
=======
 { "4.0" ,  Double . valueOf  ( "5.0" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MAJOR }
>>>>>>>
 
<<<<<<<
=======
 { "3.9" ,  Double . valueOf  ( "5.0" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MAJOR }
>>>>>>>
 
<<<<<<<
=======
 { "1.9" ,  Double . valueOf  ( "5.0" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MINOR }
>>>>>>>
 
<<<<<<<
=======
 { "0.0" ,  Double . valueOf  ( "5.0" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . INFO }
>>>>>>>
 
<<<<<<<
=======
 { "10.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MAJOR }
>>>>>>>
 
<<<<<<<
=======
 { "7.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MAJOR }
>>>>>>>
 
<<<<<<<
=======
 { "6.9" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MAJOR }
>>>>>>>
 ,  {  Float . valueOf  ( "4.0" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "1.0" ) ,  Severity . MAJOR } ,  {  Float . valueOf  ( "3.9" ) ,  Float . valueOf  ( ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "1.0" ) ,  Severity . MAJOR } 
<<<<<<<
=======
 { "1.9" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MINOR }
>>>>>>>
 
<<<<<<<
=======
 { "0.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "2.0" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . INFO }
>>>>>>>
 ,  { "10.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MINOR } ,  { "7.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MINOR } ,  { "6.9" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MINOR } ,  { "4.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MINOR } ,  { "3.9" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . MINOR } ,  { "1.9" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"1.9"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "1.0" ) ,  Severity . MINOR } ,  { "0.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "1.0" ) ,  Severity . INFO } ,  { "10.0" ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "4.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  { "7.0" ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "4.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  { "6.9" ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "4.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  { "4.0" ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "4.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  { "3.9" ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "4.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  { "1.9" ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "4.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  { "0.0" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "4.0" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . CRITICAL } ,  { "10.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  { "7.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  { "6.9" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  { "4.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  { "3.9" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  { "1.9" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  { "0.0" ,  Double . valueOf  ( "-1" ) ,  Double . valueOf  ( "0.0" ) ,  Double . valueOf  ( "0.0" ) ,  Severity . MAJOR } ,  {  Float . valueOf  ( "10.0" ) ,  Float . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . } ,  {  Float . valueOf  ( "7.0" ) ,  Float . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . } ,  {  Float . valueOf  ( "6.9" ) ,  Float . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . } ,  {  Float . valueOf  ( "4.0" ) ,  Float . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . } ,  {  Float . valueOf  ( "3.9" ) ,  Float . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . } ,  {  Float . valueOf  ( "1.9" ) ,  Float . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . MINOR } ,  {  Float . valueOf  ( "0.0" ) ,  Float . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "0.0" ) ,  Severity . MINOR } ,  { "10.0" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"10.0"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  Severity . INFO } ,  { "7.0" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"7.0"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  Severity . INFO } ,  { "6.9" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"6.9"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  Severity . INFO } ,  { "4.0" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"4.0"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  Severity . INFO } ,  { "3.9" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"3.9"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  Severity . INFO } ,  { "1.9" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"1.9"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  Severity . INFO } ,  { "0.0" ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( 
<<<<<<<
"0.0"
=======
"-1"
>>>>>>>
 ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  
<<<<<<<
Float
=======
Double
>>>>>>>
 . valueOf  ( "-1" ) ,  Severity . INFO } } ) ; }    @ Test public void testCvssToSonarQubeSeverity  ( )  {    assertThat  (  DependencyCheckUtils . cvssToSonarQubeSeverity  (  this . cvssSeverity ,  this . critical ,  this . major ,  this . minor ) ) . isEqualTo  (  this . expectedSeverity ) ; }   private final Double  minor ;   public DependencyCheckUtilsTest  (  String cvssSeverity ,  Double critical ,  Double major ,  Double minor ,  Severity expectedSeverity )  {    this . cvssSeverity = cvssSeverity ;    this . critical = critical ;    this . major = major ;    this . minor = minor ;    this . expectedSeverity = expectedSeverity ; } }