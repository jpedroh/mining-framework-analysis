  package   com . cronutils . parser ;   import    com . cronutils . descriptor . CronDescriptor ;  import    com . cronutils . model . Cron ;  import    com . cronutils . model . CronType ;  import     com . cronutils . model . definition . CronDefinition ;  import     com . cronutils . model . definition . CronDefinitionBuilder ;  import   org . junit . Before ;  import   org . junit . Test ;  import   java . util . Locale ;   public class CronParserQuartzIntegrationTest  {   private CronParser  parser ;    @ Before public void setUp  ( )  throws Exception  {   parser =  new CronParser  (  CronDefinitionBuilder . instanceDefinitionFor  (  CronType . QUARTZ ) ) ; }    @ Test  (  expected =  IllegalArgumentException . class ) public void testInvalidCharsDetected  ( )  throws Exception  {   parser . parse  ( "* * * * $ ?" ) ; }    @ Test  (  expected =  IllegalArgumentException . class ) public void testInvalidCharsDetectedWithSingleSpecialChar  ( )  throws Exception  {   parser . parse  ( "* * * * $W ?" ) ; }    @ Test  (  expected =  IllegalArgumentException . class ) public void testInvalidCharsDetectedWithHashExpression1  ( )  throws Exception  {   parser . parse  ( "* * * * $#3 ?" ) ; }    @ Test  (  expected =  IllegalArgumentException . class ) public void testInvalidCharsDetectedWithHashExpression2  ( )  throws Exception  {   parser . parse  ( "* * * * 3#$ ?" ) ; }    @ Test public void testLSupportedInDoMRange  ( )  throws Exception  {   parser . parse  ( "* * * L-3 * ?" ) ; }    @ Test  (  expected =  IllegalArgumentException . class ) public void testLSupportedInRange  ( )  throws Exception  {   parser . parse  ( "* * * W-3 * ?" ) ; }    @ Test public void testNLSupported  ( )  throws Exception  {   parser . parse  ( "* * * 3L * ?" ) ; }    @ Test public void testLSupportedInDoM  ( )  throws Exception  {   parser . parse  ( "0 0/10 22 L * ?" ) ; }    @ Test public void testMonthRangeStringMapping  ( )  {   parser . parse  ( "0 0 0 * JUL-AUG ? *" ) ;   parser . parse  ( "0 0 0 * JAN-FEB ? *" ) ; }    @ Test public void testSingleMonthStringMapping  ( )  {   parser . parse  ( "0 0 0 * JAN ? *" ) ; }    @ Test public void testDoWRangeStringMapping  ( )  {   parser . parse  ( "0 0 0 ? * MON-FRI *" ) ; }    @ Test public void testSingleDoWStringMapping  ( )  {   parser . parse  ( "0 0 0 ? * MON *" ) ; }    @ Test public void testJulyMonthAsStringConsideredSpecialChar  ( )  {   parser . parse  ( "0 0 0 * JUL ? *" ) ; }   public  @ Test void testSunToSat  ( )  {   parser . parse  ( "0 0 12 ? * SUN-SAT" ) ; }    @ Test public void testParseExpressionWithQuestionMarkAndWeekdays  ( )  {   parser . parse  ( "0 0 0 ? * MON,TUE *" ) ; }    @ Test public void testDescribeExpressionWithQuestionMarkAndWeekdays  ( )  {  Cron  quartzCron =  parser . parse  ( "0 0 0 ? * MON,TUE *" ) ;  CronDescriptor  descriptor =  CronDescriptor . instance  (  Locale . ENGLISH ) ;   descriptor . describe  ( quartzCron ) ; }    @ Test public void testDescribeExpression  ( )  {  String  expression = "0 * * ? * 1,5" ;  CronDefinition  definition =  CronDefinitionBuilder . instanceDefinitionFor  (  CronType . QUARTZ ) ;  CronParser  parser =  new CronParser  ( definition ) ;  Cron  c =  parser . parse  ( expression ) ;    CronDescriptor . instance  (  Locale . GERMAN ) . describe  ( c ) ; }    @ Test public void testIntervalSeconds  ( )  {   parser . parse  ( "0/2 * * * * ?" ) ; }    @ Test  (  expected =  IllegalArgumentException . class ) public void testDoMAndDoWParametersInvalidForQuartz  ( )  {   parser . parse  ( "0 30 17 4 1 * 2016" ) ; } }