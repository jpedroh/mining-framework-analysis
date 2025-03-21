  package     cz . startnet . utils . pgdiff . loader ;   import     cz . startnet . utils . pgdiff . Resources ;  import      cz . startnet . utils . pgdiff . parsers . AlterSequenceParser ;  import      cz . startnet . utils . pgdiff . parsers . AlterRelationParser ;  import      cz . startnet . utils . pgdiff . parsers . CommentParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateExtensionParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateFunctionParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateTypeParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateIndexParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateSchemaParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateSequenceParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateTableParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateTriggerParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateViewParser ;  import      cz . startnet . utils . pgdiff . parsers . GrantRevokeParser ;  import      cz . startnet . utils . pgdiff . parsers . CreatePolicyParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateProcedureParser ;  import      cz . startnet . utils . pgdiff . parsers . CreateRuleParser ;  import      cz . startnet . utils . pgdiff . schema . PgDatabase ;  import   java . io . BufferedReader ;  import   java . io . FileInputStream ;  import   java . io . FileNotFoundException ;  import   java . io . IOException ;  import   java . io . InputStream ;  import   java . io . InputStreamReader ;  import   java . io . UnsupportedEncodingException ;  import   java . text . MessageFormat ;  import    java . util . regex . Matcher ;  import    java . util . regex . Pattern ;   public class PgDumpLoader  {   private static final Pattern  PATTERN_CREATE_SCHEMA =  Pattern . compile  ( "^CREATE[\\s]+SCHEMA[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_DEFAULT_SCHEMA =  Pattern . compile  (  "^SET[\\s]+search_path[\\s]*=[\\s]*\"?([^,\\s\"]+)\"?" + "(?:,[\\s]+.*)?;$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_TABLE =  Pattern . compile  ( "^CREATE[\\s]+(UNLOGGED\\s|FOREIGN\\s)*TABLE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_VIEW =  Pattern . compile  ( "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?(?:MATERIALIZED[\\s]+)?VIEW[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_ALTER_TABLE =  Pattern . compile  ( "^ALTER[\\s](FOREIGN)*TABLE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_SEQUENCE =  Pattern . compile  ( "^CREATE[\\s]+SEQUENCE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_ALTER_SEQUENCE =  Pattern . compile  ( "^ALTER[\\s]+SEQUENCE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_INDEX =  Pattern . compile  ( "^CREATE[\\s]+(?:UNIQUE[\\s]+)?INDEX[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_SELECT =  Pattern . compile  ( "^SELECT[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_INSERT_INTO =  Pattern . compile  ( "^INSERT[\\s]+INTO[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_UPDATE =  Pattern . compile  ( "^UPDATE[\\s].*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_DELETE_FROM =  Pattern . compile  ( "^DELETE[\\s]+FROM[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_TRIGGER =  Pattern . compile  ( "^CREATE[\\s]+TRIGGER[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_FUNCTION =  Pattern . compile  ( "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?FUNCTION[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_PROCEDURE =  Pattern . compile  ( "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?PROCEDURE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_ALTER_VIEW =  Pattern . compile  ( "^ALTER[\\s]+(?:MATERIALIZED[\\s]+)?VIEW[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_COMMENT =  Pattern . compile  ( "^COMMENT[\\s]+ON[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_TYPE =  Pattern . compile  ( "^CREATE[\\s]+TYPE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_GRANT =  Pattern . compile  ( "^GRANT[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_REVOKE =  Pattern . compile  ( "^REVOKE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_DOLLAR_TAG =  Pattern . compile  ( "[\"\\s]" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_EXTENSION =  Pattern . compile  ( "^CREATE[\\s]+EXTENSION[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_POLICY =  Pattern . compile  ( "^CREATE[\\s]+POLICY[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_DISABLE_TRIGGER =  Pattern . compile  ( "ALTER\\s+TABLE+\\s+\\w+.+\\w+\\s+DISABLE+\\s+TRIGGER+\\s+\\w+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static final Pattern  PATTERN_CREATE_RULE =  Pattern . compile  ( "^CREATE[\\s]+RULE[\\s]+.*$" ,   Pattern . CASE_INSENSITIVE |  Pattern . DOTALL ) ;   private static String  lineBuffer ;   public static PgDatabase loadDatabaseSchema  (   final InputStream inputStream ,   final String charsetName ,   final boolean outputIgnoredStatements ,   final boolean ignoreSlonyTriggers ,   final boolean ignoreSchemaCreation )  {   final PgDatabase  database =  new PgDatabase  ( ) ;  BufferedReader  reader = null ;  try  {   reader =  new BufferedReader  (  new InputStreamReader  ( inputStream , charsetName ) ) ; }  catch (   final  UnsupportedEncodingException ex )  {  throw  new UnsupportedOperationException  (    Resources . getString  ( "UnsupportedEncoding" ) + ": " + charsetName , ex ) ; }  String  statement =  getWholeStatement  ( reader ) ;  while  (  statement != null )  {  if  (   PATTERN_CREATE_SCHEMA . matcher  ( statement ) . matches  ( ) )  {   CreateSchemaParser . parse  ( database , statement ) ; } else  if  (   PATTERN_CREATE_EXTENSION . matcher  ( statement ) . matches  ( ) )  {   CreateExtensionParser . parse  ( database , statement ) ; } else  if  (   PATTERN_DEFAULT_SCHEMA . matcher  ( statement ) . matches  ( ) )  {   final Matcher  matcher =  PATTERN_DEFAULT_SCHEMA . matcher  ( statement ) ;   matcher . matches  ( ) ;   database . setDefaultSchema  (  matcher . group  ( 1 ) ) ; } else  if  (   PATTERN_CREATE_TABLE . matcher  ( statement ) . matches  ( ) )  {   CreateTableParser . parse  ( database , statement , ignoreSchemaCreation ) ; } else  if  (   (    PATTERN_ALTER_TABLE . matcher  ( statement ) . matches  ( ) ||   PATTERN_ALTER_VIEW . matcher  ( statement ) . matches  ( ) ) &&  !   PATTERN_DISABLE_TRIGGER . matcher  ( statement ) . matches  ( ) )  {   AlterRelationParser . parse  ( database , statement , outputIgnoredStatements ) ; } else  if  (   PATTERN_CREATE_SEQUENCE . matcher  ( statement ) . matches  ( ) )  {   CreateSequenceParser . parse  ( database , statement ) ; } else  if  (   PATTERN_ALTER_SEQUENCE . matcher  ( statement ) . matches  ( ) )  {   AlterSequenceParser . parse  ( database , statement , outputIgnoredStatements ) ; } else  if  (   PATTERN_CREATE_INDEX . matcher  ( statement ) . matches  ( ) )  {   CreateIndexParser . parse  ( database , statement ) ; } else  if  (   PATTERN_CREATE_VIEW . matcher  ( statement ) . matches  ( ) )  {   CreateViewParser . parse  ( database , statement ) ; } else  if  (   PATTERN_CREATE_TRIGGER . matcher  ( statement ) . matches  ( ) )  {   CreateTriggerParser . parse  ( database , statement , ignoreSlonyTriggers ) ; } else  if  (   PATTERN_DISABLE_TRIGGER . matcher  ( statement ) . matches  ( ) )  {   CreateTriggerParser . parseDisable  ( database , statement ) ; } else  if  (   PATTERN_CREATE_FUNCTION . matcher  ( statement ) . matches  ( ) )  {   CreateFunctionParser . parse  ( database , statement ) ; } else  if  (   
<<<<<<<
PATTERN_CREATE_PROCEDURE
=======
PATTERN_CREATE_TYPE
>>>>>>>
 . matcher  ( statement ) . matches  ( ) )  {   
<<<<<<<
CreateProcedureParser
=======
CreateTypeParser
>>>>>>>
 . parse  ( database , statement ) ; } else  if  (   
<<<<<<<
PATTERN_CREATE_TYPE
=======
PATTERN_COMMENT
>>>>>>>
 . matcher  ( statement ) . matches  ( ) )  {   
<<<<<<<
CreateTypeParser
=======
CommentParser
>>>>>>>
 . parse  ( database , statement , outputIgnoredStatements ) ; } else  if  ( 
<<<<<<<
  PATTERN_COMMENT . matcher  ( statement ) . matches  ( )
=======
     PATTERN_SELECT . matcher  ( statement ) . matches  ( ) ||   PATTERN_INSERT_INTO . matcher  ( statement ) . matches  ( ) ||   PATTERN_UPDATE . matcher  ( statement ) . matches  ( ) ||   PATTERN_DELETE_FROM . matcher  ( statement ) . matches  ( )
>>>>>>>
 )  { 
<<<<<<<
  CommentParser . parse  ( database , statement , outputIgnoredStatements ) ;
=======
>>>>>>>
 } else  if  ( 
<<<<<<<
     PATTERN_SELECT . matcher  ( statement ) . matches  ( ) ||   PATTERN_INSERT_INTO . matcher  ( statement ) . matches  ( ) ||   PATTERN_UPDATE . matcher  ( statement ) . matches  ( ) ||   PATTERN_DELETE_FROM . matcher  ( statement ) . matches  ( )
=======
  PATTERN_GRANT . matcher  ( statement ) . matches  ( )
>>>>>>>
 )  { } else  if  (   
<<<<<<<
PATTERN_GRANT
=======
PATTERN_REVOKE
>>>>>>>
 . matcher  ( statement ) . matches  ( ) )  {   GrantRevokeParser . parse  ( database , statement , outputIgnoredStatements ) ; } else  if  (   
<<<<<<<
PATTERN_REVOKE
=======
PATTERN_CREATE_POLICY
>>>>>>>
 . matcher  ( statement ) . matches  ( ) )  {   
<<<<<<<
GrantRevokeParser
=======
CreatePolicyParser
>>>>>>>
 . parse  ( database , statement , outputIgnoredStatements ) ; } else  if  (   
<<<<<<<
PATTERN_CREATE_POLICY
=======
PATTERN_CREATE_RULE
>>>>>>>
 . matcher  ( statement ) . matches  ( ) )  {   
<<<<<<<
CreatePolicyParser
=======
CreateRuleParser
>>>>>>>
 . parse  ( database , statement ) ; } else  if  ( 
<<<<<<<
  PATTERN_CREATE_RULE . matcher  ( statement ) . matches  ( )
=======
outputIgnoredStatements
>>>>>>>
 )  {   
<<<<<<<
CreateRuleParser
=======
database
>>>>>>>
 . 
<<<<<<<
parse
=======
addIgnoredStatement
>>>>>>>
  ( database , statement ) ; } else 
<<<<<<<
 if  ( outputIgnoredStatements )  {   database . addIgnoredStatement  ( statement ) ; } else  { }
=======
 { }
>>>>>>>
   statement =  getWholeStatement  ( reader ) ; }  return database ; }   public static PgDatabase loadDatabaseSchema  (   final String file ,   final String charsetName ,   final boolean outputIgnoredStatements ,   final boolean ignoreSlonyTriggers ,   final boolean ignoreSchemaCreation )  {  if  (  file . equals  ( "-" ) )  return  loadDatabaseSchema  (  System . in , charsetName , outputIgnoredStatements , ignoreSlonyTriggers , ignoreSchemaCreation ) ;  FileInputStream  fis = null ;  try  {   fis =  new FileInputStream  ( file ) ;  return  loadDatabaseSchema  ( fis , charsetName , outputIgnoredStatements , ignoreSlonyTriggers , ignoreSchemaCreation ) ; }  catch (   final  FileNotFoundException ex )  {  throw  new FileException  (  MessageFormat . format  (  Resources . getString  ( "FileNotFound" ) , file ) , ex ) ; }  finally  {  if  (  fis != null )  {  try  {   fis . close  ( ) ; }  catch (   IOException ex )  { } } } }   private static String getWholeStatement  (   final BufferedReader reader )  {   final StringBuilder  sbStatement =  new StringBuilder  ( 1024 ) ;  if  (  lineBuffer != null )  {   sbStatement . append  ( lineBuffer ) ;   lineBuffer = null ;   stripComment  ( sbStatement ) ; }   int  pos =  sbStatement . indexOf  ( ";" ) ;  while  ( true )  {  if  (  pos ==  - 1 )  {   final String  newLine ;  try  {   newLine =  reader . readLine  ( ) ; }  catch (   IOException ex )  {  throw  new FileException  (  Resources . getString  ( "CannotReadFile" ) , ex ) ; }  if  (  newLine == null )  {  if  (     sbStatement . toString  ( ) . trim  ( ) . length  ( ) == 0 )  {  return null ; } else  {  throw  new RuntimeException  (  MessageFormat . format  (  Resources . getString  ( "EndOfStatementNotFound" ) ,  sbStatement . toString  ( ) ) ) ; } }  if  (   sbStatement . length  ( ) > 0 )  {   sbStatement . append  (  System . getProperty  ( "line.separator" ) ) ; }   pos =  sbStatement . length  ( ) ;   sbStatement . append  ( newLine ) ;   stripComment  ( sbStatement ) ;   pos =  sbStatement . indexOf  ( ";" , pos ) ; } else  {  if  (  !  isQuoted  ( sbStatement , pos ) )  {  if  (  pos ==   sbStatement . length  ( ) - 1 )  {   lineBuffer = null ; } else  {   lineBuffer =  sbStatement . substring  (  pos + 1 ) ;   sbStatement . setLength  (  pos + 1 ) ; }  return   sbStatement . toString  ( ) . trim  ( ) ; }   pos =  sbStatement . indexOf  ( ";" ,  pos + 1 ) ; } } }   private static void stripComment  (   final StringBuilder sbStatement )  {   int  pos =  sbStatement . indexOf  ( "--" ) ;  while  (  pos >= 0 )  {  if  (  pos == 0 )  {   sbStatement . setLength  ( 0 ) ;  return ; } else  {  if  (  !  isQuoted  ( sbStatement , pos ) )  {   sbStatement . setLength  ( pos ) ;  return ; } }   pos =  sbStatement . indexOf  ( "--" ,  pos + 1 ) ; }   int  endPos =  sbStatement . indexOf  ( "*/" ) ;  while  (  endPos >= 0 )  {  if  (  !  isQuoted  ( sbStatement , endPos ) )  {   int  startPos =  sbStatement . lastIndexOf  ( "/*" , endPos ) ;  if  (   startPos < endPos &&  !  isQuoted  ( sbStatement , startPos ) )  {   sbStatement . replace  ( startPos ,  endPos + 2 , "" ) ; } }   endPos =  sbStatement . indexOf  ( "*/" ,  endPos + 2 ) ; } }    @ SuppressWarnings  ( "AssignmentToForLoopParameter" ) private static boolean isQuoted  (   final StringBuilder sbString ,   final  int pos )  {  boolean  isQuoted = false ;  boolean  insideDoubleQuotes = false ;  boolean  insideSingeQuote = false ;  for (   int  curPos = 0 ;  curPos < pos ;  curPos ++ )  {  if  (    sbString . charAt  ( curPos ) == '\"' &&  ! insideSingeQuote )  {   insideDoubleQuotes =  ! insideDoubleQuotes ; }  if  (    sbString . charAt  ( curPos ) == '\'' &&  ! insideDoubleQuotes )  {   insideSingeQuote =  ! insideSingeQuote ; }  if  (  ! insideDoubleQuotes )  {  if  (   sbString . charAt  ( curPos ) == '\'' )  {   isQuoted =  ! isQuoted ;  if  (   pos > 0 &&   sbString . charAt  (  pos - 1 ) == '\\' )  {   isQuoted =  ! isQuoted ; } } else  if  (    sbString . charAt  ( curPos ) == '$' &&  ! isQuoted )  {   final  int  endPos =  sbString . indexOf  ( "$" ,  curPos + 1 ) ;  if  (  endPos ==  - 1 )  {  return false ; }   final String  tag =  sbString . substring  ( curPos ,  endPos + 1 ) ;  if  (  !  isCorrectTag  ( tag ) )  {  return false ; }   final  int  endTagPos =  sbString . indexOf  ( tag ,  endPos + 1 ) ;  if  (   endTagPos ==  - 1 ||  endTagPos > pos )  {  return true ; }   curPos =   endTagPos +  tag . length  ( ) - 1 ; } } }  return isQuoted ; }   private static boolean isCorrectTag  (   final String tag )  {  return  !   PATTERN_DOLLAR_TAG . matcher  ( tag ) . find  ( ) ; }   private PgDumpLoader  ( )  { } }