  package     de . uni_koblenz . jgralab . schema . impl ;   import   java . io . ByteArrayOutputStream ;  import   java . io . DataOutputStream ;  import   java . io . File ;  import   java . io . IOException ;  import    java . lang . reflect . Method ;  import   java . util . ArrayList ;  import   java . util . Collection ;  import   java . util . HashMap ;  import   java . util . LinkedList ;  import   java . util . List ;  import   java . util . Map ;  import   java . util . TreeMap ;  import   java . util . Vector ;  import    java . util . regex . Pattern ;  import   javax . tools . JavaCompiler ;  import   javax . tools . JavaFileObject ;  import   javax . tools . StandardJavaFileManager ;  import   javax . tools . ToolProvider ;  import    de . uni_koblenz . jgralab . Graph ;  import    de . uni_koblenz . jgralab . GraphFactory ;  import    de . uni_koblenz . jgralab . GraphIO ;  import    de . uni_koblenz . jgralab . GraphIOException ;  import    de . uni_koblenz . jgralab . ImplementationType ;  import    de . uni_koblenz . jgralab . ProgressFunction ;  import    de . uni_koblenz . jgralab . Vertex ;  import     de . uni_koblenz . jgralab . codegenerator . CodeGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . CodeGeneratorConfiguration ;  import     de . uni_koblenz . jgralab . codegenerator . EdgeCodeGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . EnumCodeGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . GraphCodeGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . GraphFactoryGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . RecordCodeGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . ReversedEdgeCodeGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . SchemaCodeGenerator ;  import     de . uni_koblenz . jgralab . codegenerator . VertexCodeGenerator ;  import     de . uni_koblenz . jgralab . impl . ConsoleProgressFunction ;  import      de . uni_koblenz . jgralab . impl . generic . GenericGraphFactoryImpl ;  import     de . uni_koblenz . jgralab . schema . Attribute ;  import     de . uni_koblenz . jgralab . schema . AttributedElementClass ;  import     de . uni_koblenz . jgralab . schema . BooleanDomain ;  import     de . uni_koblenz . jgralab . schema . CompositeDomain ;  import     de . uni_koblenz . jgralab . schema . Domain ;  import     de . uni_koblenz . jgralab . schema . DoubleDomain ;  import     de . uni_koblenz . jgralab . schema . EdgeClass ;  import     de . uni_koblenz . jgralab . schema . EnumDomain ;  import     de . uni_koblenz . jgralab . schema . GraphClass ;  import     de . uni_koblenz . jgralab . schema . IntegerDomain ;  import     de . uni_koblenz . jgralab . schema . ListDomain ;  import     de . uni_koblenz . jgralab . schema . LongDomain ;  import     de . uni_koblenz . jgralab . schema . MapDomain ;  import     de . uni_koblenz . jgralab . schema . NamedElement ;  import     de . uni_koblenz . jgralab . schema . Package ;  import     de . uni_koblenz . jgralab . schema . RecordDomain ;  import      de . uni_koblenz . jgralab . schema . RecordDomain . RecordComponent ;  import     de . uni_koblenz . jgralab . schema . Schema ;  import     de . uni_koblenz . jgralab . schema . SetDomain ;  import     de . uni_koblenz . jgralab . schema . StringDomain ;  import     de . uni_koblenz . jgralab . schema . VertexClass ;  import      de . uni_koblenz . jgralab . schema . exception . InvalidNameException ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaClassAccessException ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaException ;  import       de . uni_koblenz . jgralab . schema . impl . compilation . ClassFileManager ;  import       de . uni_koblenz . jgralab . schema . impl . compilation . InMemoryJavaSourceFile ;  import       de . uni_koblenz . jgralab . schema . impl . compilation . SchemaClassManager ;   public class SchemaImpl  implements  Schema  {   private SchemaClassManager  schemaClassManager = null ;   public SchemaClassManager getSchemaClassManager  ( )  {  return 
<<<<<<<
 this . schemaClassManager
=======
schemaClassManager
>>>>>>>
 ; }   static final   Class  <  ? >  [ ]  GRAPHCLASS_CREATE_SIGNATURE =  {  ImplementationType . class ,  String . class ,   int . class ,   int . class } ;   public static final String  IMPL_PACKAGE_NAME = "impl" ;   public static final String  IMPLSTDPACKAGENAME = "impl.std" ;   public static final String  IMPLTRANSPACKAGENAME = "impl.trans" ;   public static final String  IMPLDATABASEPACKAGENAME = "impl.db" ;   static final   Class  <  ? >  [ ]  VERTEX_CLASS_CREATE_SIGNATURE =  {   int . class } ;   private boolean  allowLowercaseEnumConstants = true ;   private PackageImpl  defaultPackage ;   protected CodeGeneratorConfiguration  config ;   private  Map  < String , Domain >  domains =  new  HashMap  < String , Domain >  ( ) ;   private  DirectedAcyclicGraph  < Domain >  domainsDag =  new  DirectedAcyclicGraph  < Domain >  ( ) ;   private boolean  finished = false ;   private 
<<<<<<<
GraphClass
=======
GraphClassImpl
>>>>>>>
  graphClass ;   private String  name ;   private String  packagePrefix ;   private  Map  < String ,  AttributedElementClass  <  ? ,  ? > >  duplicateSimpleNames =  new  HashMap  < String ,  AttributedElementClass  <  ? ,  ? > >  ( ) ;   private  Map  < String , PackageImpl >  packages =  new  TreeMap  < String , 
<<<<<<<
Package
=======
PackageImpl
>>>>>>>
 >  ( ) ;   private String  qualifiedName ;   private  Map  < String , NamedElement >  namedElements =  new  TreeMap  < String , NamedElement >  ( ) ;   private BooleanDomain  booleanDomain ;   private DoubleDomain  doubleDomain ;   private IntegerDomain  integerDomain ;   private LongDomain  longDomain ;   private StringDomain  stringDomain ;   private static final Pattern  SCHEMA_NAME_PATTERN =  Pattern . compile  ( "^\\p{Upper}(\\p{Alnum}|[_])*\\p{Alnum}$" ) ;   private static final Pattern  PACKAGE_PREFIX_PATTERN =  Pattern . compile  ( "^\\p{Lower}\\w*(\\.\\p{Lower}\\w*)*$" ) ;   public SchemaImpl  (  String name ,  String packagePrefix )  {  if  (  !   SCHEMA_NAME_PATTERN . matcher  ( name ) . matches  ( ) )  { 
<<<<<<<
  this . throwInvalidSchemaNameException  ( ) ;
=======
 throw  new SchemaException  (       "Invalid schema name '" + name + "'.\n" + "The name must not be empty.\n" + "The name must start with a capital letter.\n" + "Any following character must be alphanumeric and/or a '_' character.\n" + "The name must end with an alphanumeric character." ) ;
>>>>>>>
 }  if  (  !   PACKAGE_PREFIX_PATTERN . matcher  ( packagePrefix ) . matches  ( ) )  { 
<<<<<<<
  this . throwInvalidPackagePrefixNameException  ( ) ;
=======
 throw  new SchemaException  (        "Invalid schema package prefix '" + packagePrefix + "'.\n" + "The packagePrefix must not be empty.\n" + "The package prefix must start with a small letter.\n" + "The first character after each '.' must be a small letter.\n" + "Following characters may be alphanumeric and/or '_' characters.\n" + "The last character before a '.' and the end of the line must be an alphanumeric character." ) ;
>>>>>>>
 }    this . name = name ;    this . packagePrefix = packagePrefix ;   
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 =   packagePrefix + "." + name ;   
<<<<<<<
 this . schemaClassManager
=======
schemaClassManager
>>>>>>>
 =  SchemaClassManager . instance  ( 
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 ) ;   
<<<<<<<
 this . defaultPackage
=======
defaultPackage
>>>>>>>
 =  
<<<<<<<
this
=======
PackageImpl
>>>>>>>
 . createDefaultPackage  ( this ) ;    this . booleanDomain =  this . createBooleanDomain  ( ) ;    this . doubleDomain =  this . createDoubleDomain  ( ) ;    this . integerDomain =  this . createIntegerDomain  ( ) ;    this . longDomain =  this . createLongDomain  ( ) ;  
<<<<<<<
  this . stringDomain =  this . createStringDomain  ( )
=======
 createBooleanDomain  ( )
>>>>>>>
 ;  
<<<<<<<
  this . defaultGraphClass =  GraphClassImpl . createDefaultGraphClass  ( this )
=======
 createDoubleDomain  ( )
>>>>>>>
 ;  
<<<<<<<
  this . defaultVertexClass =  VertexClassImpl . createDefaultVertexClass  ( this )
=======
 createIntegerDomain  ( )
>>>>>>>
 ;  
<<<<<<<
  this . defaultEdgeClass =  EdgeClassImpl . createDefaultEdgeClass  ( this )
=======
 createLongDomain  ( )
>>>>>>>
 ;  
<<<<<<<
  this . config =  this . createDefaultConfig  ( )
=======
 createStringDomain  ( )
>>>>>>>
 ; }   protected Package createDefaultPackage  ( )  {  return  PackageImpl . createDefaultPackage  ( this ) ; }   private void throwInvalidSchemaNameException  ( )  {  throw  new InvalidNameException  (       "Invalid schema name '" +  this . name + "'.\n" + "The name must not be empty.\n" + "The name must start with a capital letter.\n" + "Any following character must be alphanumeric and/or a '_' character.\n" + "The name must end with an alphanumeric character." ) ; }   private void throwInvalidPackagePrefixNameException  ( )  {  throw  new InvalidNameException  (        "Invalid schema package prefix '" +  this . packagePrefix + "'.\n" + "The packagePrefix must not be empty.\n" + "The package prefix must start with a small letter.\n" + "The first character after each '.' must be a small letter.\n" + "Following characters may be alphanumeric and/or '_' characters.\n" + "The last character before a '.' and the end of the line must be an alphanumeric character." ) ; }   private CodeGeneratorConfiguration createDefaultConfig  ( )  {  CodeGeneratorConfiguration  out =  new CodeGeneratorConfiguration  ( ) ;  if  (     java . lang . Package . getPackage  (    this . packagePrefix + ". " + IMPLSTDPACKAGENAME ) == null )  {   out . setStandardSupport  ( false ) ; }  if  (     java . lang . Package . getPackage  (    this . packagePrefix + ". " + IMPLTRANSPACKAGENAME ) != null )  {   out . setTransactionSupport  ( true ) ; }  if  (     java . lang . Package . getPackage  (    this . packagePrefix + ". " + IMPLDATABASEPACKAGENAME ) != null )  {   out . setDatabaseSupport  ( true ) ; }  return out ; }  void addDomain  (  Domain dom )  { 
<<<<<<<
 assert  !   this . domains . containsKey  (  dom . getQualifiedName  ( ) ) :   "There already is a Domain with the qualified name: " +  dom . getQualifiedName  ( ) + " in the Schema!" ;
=======
 if  (  domains . containsKey  (  dom . getQualifiedName  ( ) ) )  {  throw  new SchemaException  (   "Duplicate Domain '" +  dom . getQualifiedName  ( ) + "'" ) ; }
>>>>>>>
    this . domains . put  (  dom . getQualifiedName  ( ) , dom ) ; 
<<<<<<<
=======
  domainsDag . createNode  ( dom ) ;
>>>>>>>
 }  void addPackage  (  PackageImpl pkg )  { 
<<<<<<<
 assert  !   this . packages . containsKey  (  pkg . getQualifiedName  ( ) ) :   "There already is a Package with the qualified name '" +  pkg . getQualifiedName  ( ) + "' in the Schema!" ;
=======
 if  (  packages . containsKey  (  pkg . getQualifiedName  ( ) ) )  {  throw  new SchemaException  (   "Duplicate Package '" +  pkg . getQualifiedName  ( ) + "'" ) ; }
>>>>>>>
   
<<<<<<<
 this . packages
=======
packages
>>>>>>>
 . put  (  pkg . getQualifiedName  ( ) , pkg ) ; }  void addNamedElement  (  NamedElement namedElement )  { 
<<<<<<<
 assert  !   this . namedElements . containsKey  (  namedElement . getQualifiedName  ( ) ) :   "You are trying to add the NamedElement '" +  namedElement . getQualifiedName  ( ) + "' to this Schema, but that does already exist!" ;
=======
 if  (  namedElements . containsKey  (  namedElement . getQualifiedName  ( ) ) )  {  throw  new SchemaException  (   "Duplicate NamedElement '" +  namedElement . getQualifiedName  ( ) + "'" ) ; }
>>>>>>>
   
<<<<<<<
 this . namedElements
=======
namedElements
>>>>>>>
 . put  (  namedElement . getQualifiedName  ( ) , namedElement ) ;  if  (  !  (  namedElement instanceof AttributedElementClass ) )  {  return ; }   AttributedElementClass  <  ? ,  ? >  aec =  (  AttributedElementClass  <  ? ,  ? > ) namedElement ;  if  (  
<<<<<<<
 this . duplicateSimpleNames
=======
duplicateSimpleNames
>>>>>>>
 . containsKey  (  aec . getSimpleName  ( ) ) )  {   AttributedElementClass  <  ? ,  ? >  other =  
<<<<<<<
 this . duplicateSimpleNames
=======
duplicateSimpleNames
>>>>>>>
 . get  (  aec . getSimpleName  ( ) ) ;  if  (  other != null )  {    (  ( NamedElementImpl ) other ) . changeUniqueName  ( ) ;   
<<<<<<<
 this . duplicateSimpleNames
=======
duplicateSimpleNames
>>>>>>>
 . put  (  aec . getSimpleName  ( ) , null ) ; }    (  ( NamedElementImpl ) aec ) . changeUniqueName  ( ) ; } else  {   
<<<<<<<
 this . duplicateSimpleNames
=======
duplicateSimpleNames
>>>>>>>
 . put  (  aec . getSimpleName  ( ) , aec ) ; } }    @ Override public NamedElement getNamedElement  (  String qualifiedName )  {  return  
<<<<<<<
 this . namedElements
=======
namedElements
>>>>>>>
 . get  ( qualifiedName ) ; }    @ Override public boolean allowsLowercaseEnumConstants  ( )  {  return  this . allowLowercaseEnumConstants ; }   private  Vector  < InMemoryJavaSourceFile > createClasses  (  CodeGeneratorConfiguration config )  {   Vector  < InMemoryJavaSourceFile >  javaSources =  new  Vector  < InMemoryJavaSourceFile >  ( ) ;  GraphCodeGenerator  graphCodeGenerator =  new GraphCodeGenerator  ( 
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , 
<<<<<<<
 this . name
=======
name
>>>>>>>
 , config ) ;   javaSources . addAll  (  graphCodeGenerator . createJavaSources  ( ) ) ;  for ( VertexClass vertexClass :  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getVertexClasses  ( ) )  {  if  (  vertexClass . isInternal  ( ) )  {  continue ; }  VertexCodeGenerator  codeGen =  new VertexCodeGenerator  ( vertexClass , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   javaSources . addAll  (  codeGen . createJavaSources  ( ) ) ; }  for ( EdgeClass edgeClass :  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getEdgeClasses  ( ) )  {  if  (  edgeClass . isInternal  ( ) )  {  continue ; }  CodeGenerator  codeGen =  new EdgeCodeGenerator  ( edgeClass , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   javaSources . addAll  (  codeGen . createJavaSources  ( ) ) ;  if  (  !  edgeClass . isAbstract  ( ) )  {   codeGen =  new ReversedEdgeCodeGenerator  ( edgeClass , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   javaSources . addAll  (  codeGen . createJavaSources  ( ) ) ; } }  for ( Domain domain :  this . getRecordDomains  ( ) )  {  CodeGenerator  rcode =  new RecordCodeGenerator  (  ( RecordDomain ) domain , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   javaSources . addAll  (  rcode . createJavaSources  ( ) ) ; }  for ( Domain domain :  this . getEnumDomains  ( ) )  {  CodeGenerator  ecode =  new EnumCodeGenerator  (  ( EnumDomain ) domain , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 ) ;   javaSources . addAll  (  ecode . createJavaSources  ( ) ) ; }  return javaSources ; }    @ Override public void createJAR  (  CodeGeneratorConfiguration config ,  String jarFileName )  throws IOException , GraphIOException  {   assertFinished  ( ) ;  File  tmpFile =  File . createTempFile  ( "jar-creation" , "tmp" ) ;   tmpFile . deleteOnExit  ( ) ;  File  tmpDir =  new File  (  tmpFile . getParent  ( ) ) ;  File  schemaDir =  new File  (   tmpDir +  File . separator +  this . getName  ( ) ) ;  if  (  !  schemaDir . mkdir  ( ) )  {    System . err . println  (  "Couldn't create " + schemaDir ) ;  return ; }    System . out . println  (  "Committing schema classes to " + schemaDir ) ;   this . commit  (  schemaDir . getAbsolutePath  ( ) , config ,  new ConsoleProgressFunction  ( "Committing" ) ) ;   this . compileClasses  ( schemaDir ) ;  Process  proc =   Runtime . getRuntime  ( ) . exec  (     "jar cf " + jarFileName + " -C " +  schemaDir . getAbsolutePath  ( ) + " ." ) ;  try  {   proc . waitFor  ( ) ; }  catch (   InterruptedException e )  {   e . printStackTrace  ( ) ; }   this . deleteRecursively  ( schemaDir ) ; }   private void deleteRecursively  (  File file )  {  if  (  file . isDirectory  ( ) )  {  for ( File f :  file . listFiles  ( ) )  {   this . deleteRecursively  ( f ) ; }   file . delete  ( ) ; } else  {   file . delete  ( ) ; } }   private void compileClasses  (  File schemaDir )  throws IOException  {  JavaCompiler  c =  ToolProvider . getSystemJavaCompiler  ( ) ;  StandardJavaFileManager  fileManager =  c . getStandardFileManager  ( null , null , null ) ;   Iterable  <  ? extends JavaFileObject >  compilationUnits =  fileManager . getJavaFileObjectsFromFiles  (  this . getJavaFiles  ( schemaDir ) ) ;    c . getTask  ( null , fileManager , null , null , null , compilationUnits ) . call  ( ) ;   fileManager . close  ( ) ; }   private  List  < File > getJavaFiles  (  File schemaDir )  {   LinkedList  < File >  sources =  new  LinkedList  < File >  ( ) ;  for ( File f :  schemaDir . listFiles  ( ) )  {  if  (  f . isDirectory  ( ) )  {   sources . addAll  (  this . getJavaFiles  ( f ) ) ; } else  if  (   f . getName  ( ) . endsWith  ( ".java" ) )  {   sources . add  ( f ) ; } else  {    System . out . println  (   "Skipping " + f + "..." ) ; } }  return sources ; }    @ Override public  Vector  < InMemoryJavaSourceFile > commit  (  CodeGeneratorConfiguration config )  { 
<<<<<<<
 if  (  !  this . finished )  {  throw  new SchemaException  (  "Schema must be finish before committing is allowed. " + "Call finish() to finish the schema." ) ; }
=======
  assertFinished  ( ) ;
>>>>>>>
   Vector  < InMemoryJavaSourceFile >  javaSources =  new  Vector  < InMemoryJavaSourceFile >  ( ) ;  CodeGenerator  schemaCodeGenerator =  new SchemaCodeGenerator  ( this , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   javaSources . addAll  (  schemaCodeGenerator . createJavaSources  ( ) ) ;  CodeGenerator  factoryCodeGenerator =  new GraphFactoryGenerator  ( this , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   javaSources . addAll  (  factoryCodeGenerator . createJavaSources  ( ) ) ;  if  (    this . graphClass . getQualifiedName  ( ) . equals  ( "Graph" ) )  {  throw  new SchemaException  ( "The defined GraphClass must not be named Graph!" ) ; }   javaSources . addAll  (  this . createClasses  ( config ) ) ;  return javaSources ; }   private void createFiles  (  CodeGeneratorConfiguration config ,  String pathPrefix ,  ProgressFunction progressFunction ,   long schemaElements ,   long currentCount ,   long interval )  throws GraphIOException  {  GraphCodeGenerator  graphCodeGenerator =  new GraphCodeGenerator  ( 
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , 
<<<<<<<
 this . name
=======
name
>>>>>>>
 , config ) ;   graphCodeGenerator . createFiles  ( pathPrefix ) ;  for ( VertexClass vertexClass :  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getVertexClasses  ( ) )  {  if  (  vertexClass . isInternal  ( ) )  {  continue ; }  VertexCodeGenerator  codeGen =  new VertexCodeGenerator  ( vertexClass , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   codeGen . createFiles  ( pathPrefix ) ;  if  (  progressFunction != null )  {   schemaElements ++ ;   currentCount ++ ;  if  (  currentCount == interval )  {   progressFunction . progress  ( schemaElements ) ;   currentCount = 0 ; } } }  for ( EdgeClass edgeClass :  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getEdgeClasses  ( ) )  {  if  (  edgeClass . isInternal  ( ) )  {  continue ; }  CodeGenerator  codeGen =  new EdgeCodeGenerator  ( edgeClass , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   codeGen . createFiles  ( pathPrefix ) ;  if  (  !  edgeClass . isAbstract  ( ) )  {   codeGen =  new ReversedEdgeCodeGenerator  ( edgeClass , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   codeGen . createFiles  ( pathPrefix ) ; }  if  (  progressFunction != null )  {   schemaElements ++ ;   currentCount ++ ;  if  (  currentCount == interval )  {   progressFunction . progress  ( schemaElements ) ;   currentCount = 0 ; } } }  for ( Domain domain :  this . getRecordDomains  ( ) )  {  CodeGenerator  rcode =  new RecordCodeGenerator  (  ( RecordDomain ) domain , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   rcode . createFiles  ( pathPrefix ) ;  if  (  progressFunction != null )  {   schemaElements ++ ;   currentCount ++ ;  if  (  currentCount == interval )  {   progressFunction . progress  ( schemaElements ) ;   currentCount = 0 ; } } }  for ( Domain domain :  this . getEnumDomains  ( ) )  {  CodeGenerator  ecode =  new EnumCodeGenerator  (  ( EnumDomain ) domain , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 ) ;   ecode . createFiles  ( pathPrefix ) ; }  if  (  progressFunction != null )  {   schemaElements ++ ;   currentCount ++ ;  if  (  currentCount == interval )  {   progressFunction . progress  ( schemaElements ) ;   currentCount = 0 ; } } }    @ Override public void commit  (  String pathPrefix ,  CodeGeneratorConfiguration config )  throws GraphIOException  {   assertFinished  ( ) ;   this . commit  ( pathPrefix , config , null ) ; }    @ Override public void commit  (  String pathPrefix ,  CodeGeneratorConfiguration config ,  ProgressFunction progressFunction )  throws GraphIOException  { 
<<<<<<<
 if  (  !  this . finished )  {  throw  new SchemaException  (  "Schema must be finish before committing is allowed. " + "Call finish() to finish the schema." ) ; }
=======
  assertFinished  ( ) ;
>>>>>>>
   long  schemaElements = 0 ,  currentCount = 0 ,  interval = 1 ;  if  (  progressFunction != null )  {   int  elements =  this . getNumberOfElements  ( ) ;  if  (  config . hasTransactionSupport  ( ) )  {   elements *= 2 ; }   progressFunction . init  ( elements ) ;   interval =  progressFunction . getUpdateInterval  ( ) ; }  if  (  !  pathPrefix . endsWith  (  File . separator ) )  {   pathPrefix +=  File . separator ; }  CodeGenerator  schemaCodeGenerator =  new SchemaCodeGenerator  ( this , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   schemaCodeGenerator . createFiles  ( pathPrefix ) ;  CodeGenerator  factoryCodeGenerator =  new GraphFactoryGenerator  ( this , 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 , config ) ;   factoryCodeGenerator . createFiles  ( pathPrefix ) ;  if  (   
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getQualifiedName  ( ) . equals  ( "Graph" ) )  {  throw  new SchemaException  ( "The defined GraphClass must not be named Graph!" ) ; }   this . createFiles  ( config , pathPrefix , progressFunction , schemaElements , currentCount , interval ) ;  if  (  progressFunction != null )  {   progressFunction . finished  ( ) ; } }    @ Override public  int compareTo  (  Schema other )  {  return  
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 . compareTo  (  other . getQualifiedName  ( ) ) ; }    @ Override public void compile  (  CodeGeneratorConfiguration config )  {   
<<<<<<<
this
=======
assertFinished
>>>>>>>
 . finish  ( ) ;  JavaCompiler  compiler =  ToolProvider . getSystemJavaCompiler  ( ) ;  if  (  compiler == null )  {  throw  new SchemaException  (    "Cannot compile schema " + 
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 + ". Most probably you use a JRE instead of a JDK. " + "The JRE does not provide a compiler." ) ; }  StandardJavaFileManager  jfm =  compiler . getStandardFileManager  ( null , null , null ) ;  ClassFileManager  manager =  new ClassFileManager  ( this , jfm ) ;   Vector  < InMemoryJavaSourceFile >  javaSources =  this . commit  ( config ) ;    compiler . getTask  ( null , manager , null , null , null , javaSources ) . call  ( ) ; }    @ Override public Attribute createAttribute  (  String name ,  Domain dom ,   AttributedElementClass  <  ? ,  ? > aec ,  String defaultValueAsString )  { 
<<<<<<<
 if  (  this . finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
  return  new AttributeImpl  ( name , dom , aec , defaultValueAsString ) ; }    @ Override public EnumDomain createEnumDomain  (  String qualifiedName )  {  return  this . createEnumDomain  ( qualifiedName ,  new  ArrayList  < String >  ( ) ) ; }    @ Override public EnumDomain createEnumDomain  (  String qualifiedName ,   List  < String > enumComponents )  { 
<<<<<<<
 if  (  this . finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
   String  [ ]  components =  splitQualifiedName  ( qualifiedName ) ;  PackageImpl  parent = 
<<<<<<<
 ( PackageImpl )  this . createPackageWithParents  (  components [ 0 ] )
=======
 createPackageWithParents  (  components [ 0 ] )
>>>>>>>
 ;  String  simpleName =  components [ 1 ] ;  EnumDomain  ed =  new EnumDomainImpl  ( simpleName , parent , enumComponents ) ;  return ed ; }    @ Override public GraphClass createGraphClass  (  String simpleName )  { 
<<<<<<<
 if  (  this . finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
  if  (  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 != null )  {  throw  new SchemaException  (   "Only one GraphClass (except DefaultGraphClass) is allowed in a Schema! '" +  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getQualifiedName  ( ) + "' is already there." ) ; }  if  (  simpleName . equals  (  GraphClass . DEFAULTGRAPHCLASS_NAME ) )  {  throw  new InvalidNameException  (   "A GraphClass must not be named like the default GraphClass (" +  GraphClass . DEFAULTGRAPHCLASS_NAME + ")" ) ; }  if  (  simpleName . contains  ( "." ) )  {  throw 
<<<<<<<
 new InvalidNameException  ( "A GraphClass must always be in the default package!" )
=======
 new SchemaException  ( "A GraphClass must always be in the default package!" )
>>>>>>>
 ; }  GraphClassImpl  gc =  new GraphClassImpl  ( simpleName , this ) ;   gc . addSuperClass  (  this . defaultGraphClass ) ;  return 
<<<<<<<
gc
=======
 new GraphClassImpl  ( simpleName , this )
>>>>>>>
 ; }   protected private BooleanDomain createBooleanDomain  ( )  {   assertNotFinished  ( ) ;  if  (  
<<<<<<<
 this . booleanDomain
=======
booleanDomain
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 null )  { 
<<<<<<<
 throw  new SchemaException  ( "The BooleanDomain for this Schema was already created!" ) ;
=======
  booleanDomain =  new BooleanDomainImpl  ( this ) ;
>>>>>>>
 }    this . booleanDomain =  new BooleanDomainImpl  ( this ) ;  return 
<<<<<<<
 this . booleanDomain
=======
booleanDomain
>>>>>>>
 ; }   protected private DoubleDomain createDoubleDomain  ( )  {   assertNotFinished  ( ) ;  if  (  
<<<<<<<
 this . doubleDomain
=======
doubleDomain
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 null )  { 
<<<<<<<
 throw  new SchemaException  ( "The DoubleDomain for this Schema was already created!" ) ;
=======
  doubleDomain =  new DoubleDomainImpl  ( this ) ;
>>>>>>>
 }    this . doubleDomain =  new DoubleDomainImpl  ( this ) ;  return 
<<<<<<<
 this . doubleDomain
=======
doubleDomain
>>>>>>>
 ; }   protected private IntegerDomain createIntegerDomain  ( )  {   assertNotFinished  ( ) ;  if  (  
<<<<<<<
 this . integerDomain
=======
integerDomain
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 null )  { 
<<<<<<<
 throw  new SchemaException  ( "The IntegerDomain for this Schema was already created!" ) ;
=======
  integerDomain =  new IntegerDomainImpl  ( this ) ;
>>>>>>>
 }    this . integerDomain =  new IntegerDomainImpl  ( this ) ;  return 
<<<<<<<
 this . integerDomain
=======
integerDomain
>>>>>>>
 ; }   protected private LongDomain createLongDomain  ( )  {   assertNotFinished  ( ) ;  if  (  
<<<<<<<
 this . longDomain
=======
longDomain
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 null )  { 
<<<<<<<
 throw  new SchemaException  ( "The LongDomain for this Schema was already created!" ) ;
=======
  longDomain =  new LongDomainImpl  ( this ) ;
>>>>>>>
 }    this . longDomain =  new LongDomainImpl  ( this ) ;  return 
<<<<<<<
 this . longDomain
=======
longDomain
>>>>>>>
 ; }   protected private StringDomain createStringDomain  ( )  {   assertNotFinished  ( ) ;  if  (  
<<<<<<<
 this . stringDomain
=======
stringDomain
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 null )  { 
<<<<<<<
 throw  new SchemaException  ( "The StringDomain for this Schema was already created!" ) ;
=======
  stringDomain =  new StringDomainImpl  ( this ) ;
>>>>>>>
 }    this . stringDomain =  new StringDomainImpl  ( this ) ;  return 
<<<<<<<
 this . stringDomain
=======
stringDomain
>>>>>>>
 ; }    @ Override public ListDomain createListDomain  (  Domain baseDomain )  { 
<<<<<<<
 if  (  this . finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
  String  qn =   "List<" +  baseDomain . getQualifiedName  ( ) + ">" ;  if  (  
<<<<<<<
 this . domains
=======
domains
>>>>>>>
 . containsKey  ( qn ) )  {  return  ( ListDomain )  
<<<<<<<
 this . domains
=======
domains
>>>>>>>
 . get  ( qn ) ; }  return  new ListDomainImpl  ( this , baseDomain ) ; }    @ Override public MapDomain createMapDomain  (  Domain keyDomain ,  Domain valueDomain )  { 
<<<<<<<
 if  (  this . finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
  String  qn =     "Map<" +  keyDomain . getQualifiedName  ( ) + ", " +  valueDomain . getQualifiedName  ( ) + ">" ;  if  (  
<<<<<<<
 this . domains
=======
domains
>>>>>>>
 . containsKey  ( qn ) )  {  return  ( MapDomain )  
<<<<<<<
 this . domains
=======
domains
>>>>>>>
 . get  ( qn ) ; }  return  new MapDomainImpl  ( this , keyDomain , valueDomain ) ; }   protected Package createPackage  (  String sn ,  Package parentPkg )  {  return  new PackageImpl  ( sn , parentPkg , this ) ; }  
<<<<<<<
 protected
=======
PackageImpl
>>>>>>>
 createPackageWithParents  (  String qn )  {   assertNotFinished  ( ) ;  if  (  
<<<<<<<
 this . packages
=======
packages
>>>>>>>
 . containsKey  ( qn ) )  {  return  
<<<<<<<
 this . packages
=======
packages
>>>>>>>
 . get  ( qn ) ; }   String  [ ]  components =  splitQualifiedName  ( qn ) ;  String  parent =  components [ 0 ] ;  String  pkgSimpleName =  components [ 1 ] ;  assert  !  pkgSimpleName . contains  ( "." ) :   "The package simple name '" + pkgSimpleName + "' must not contain a dot!" ;  PackageImpl  currentParent = 
<<<<<<<
 this . defaultPackage
=======
defaultPackage
>>>>>>>
 ;  String  currentPkgQName = "" ;  if  (  !  
<<<<<<<
 this . packages
=======
packages
>>>>>>>
 . containsKey  ( parent ) )  {  for ( String component :  parent . split  ( "\\." ) )  {  if  (  currentParent != 
<<<<<<<
 this . defaultPackage
=======
defaultPackage
>>>>>>>
 )  {   currentPkgQName =    currentParent . getQualifiedName  ( ) + "." + component ; } else  {   currentPkgQName = component ; }  if  (  
<<<<<<<
 this . packages
=======
packages
>>>>>>>
 . containsKey  ( currentPkgQName ) )  {   currentParent =  
<<<<<<<
 this . packages
=======
packages
>>>>>>>
 . get  ( currentPkgQName ) ;  continue ; }   currentParent =  this . createPackage  ( component , currentParent ) ; } } else  {   currentParent =  
<<<<<<<
 this . packages
=======
packages
>>>>>>>
 . get  ( parent ) ; }  assert   currentParent . getQualifiedName  ( ) . equals  ( parent ) :      "Something went wrong when creating a package with parents: " + "parent should be \"" + parent + "\" but created was \"" +  currentParent . getQualifiedName  ( ) + "\"." ;  assert  (    currentParent . getQualifiedName  ( ) . isEmpty  ( ) ?  currentParent == 
<<<<<<<
 this . defaultPackage
=======
defaultPackage
>>>>>>>
 : true ) :   "The parent package of package '" + pkgSimpleName + "' is empty, but not the default package." ;  return  this . createPackage  ( pkgSimpleName , currentParent ) ; }   public static  String  [ ] splitQualifiedName  (  String qualifiedName )  {   int  lastIndex =  qualifiedName . lastIndexOf  ( '.' ) ;   String  [ ]  components =  new String  [ 2 ] ;  if  (  lastIndex ==  - 1 )  {    components [ 0 ] = "" ;    components [ 1 ] = qualifiedName ; } else  {    components [ 0 ] =  qualifiedName . substring  ( 0 , lastIndex ) ;  if  (   (    components [ 0 ] . length  ( ) >= 1 ) &&  (    components [ 0 ] . charAt  ( 0 ) == '.' ) )  {    components [ 0 ] =   components [ 0 ] . substring  ( 1 ) ; }    components [ 1 ] =  qualifiedName . substring  (  lastIndex + 1 ) ; }  return components ; }    @ Override public RecordDomain createRecordDomain  (  String qualifiedName )  {  return  this . createRecordDomain  ( qualifiedName , null ) ; }    @ Override public RecordDomain createRecordDomain  (  String qualifiedName ,   Collection  < RecordComponent > recordComponents )  { 
<<<<<<<
 if  (  this . finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
   String  [ ]  components =  splitQualifiedName  ( qualifiedName ) ;  PackageImpl  parent = 
<<<<<<<
 ( PackageImpl )  this . createPackageWithParents  (  components [ 0 ] )
=======
 createPackageWithParents  (  components [ 0 ] )
>>>>>>>
 ;  String  simpleName =  components [ 1 ] ;  RecordDomain  rd =  new RecordDomainImpl  ( simpleName , parent , recordComponents ) ;  return rd ; }    @ Override public SetDomain createSetDomain  (  Domain baseDomain )  { 
<<<<<<<
 if  (  this . finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
  String  qn =   "Set<" +  baseDomain . getQualifiedName  ( ) + ">" ;  if  (  
<<<<<<<
 this . domains
=======
domains
>>>>>>>
 . containsKey  ( qn ) )  {  return  ( SetDomain )  
<<<<<<<
 this . domains
=======
domains
>>>>>>>
 . get  ( qn ) ; }  return  new SetDomainImpl  ( this , baseDomain ) ; }    @ Override public boolean equals  (  Object other )  {  if  (   (  other == null ) ||  !  (  other instanceof Schema ) )  {  return false ; }  return  
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 . equals  ( 
<<<<<<<
  (  ( Schema ) other ) . getQualifiedName  ( )
=======
  (  ( SchemaImpl ) other ) . qualifiedName
>>>>>>>
 ) ; }    @ Override public  int hashCode  ( )  {  return  
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 . hashCode  ( ) ; }    @ SuppressWarnings  ( "unchecked" )  @ Override public  <  T  extends  AttributedElementClass  <  ? ,  ? > > T getAttributedElementClass  (  String qualifiedName )  {  if  (  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 == null )  {  return null ; } else  if  (   
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getQualifiedName  ( ) . equals  ( qualifiedName ) )  {  return  ( T ) 
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 ; } else  {  return  ( T )  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getGraphElementClass  ( qualifiedName ) ; } }    @ Override public  List  < CompositeDomain > getCompositeDomains  ( )  {   ArrayList  < CompositeDomain >  topologicalOrderList =  new  ArrayList  < CompositeDomain >  ( ) ;  for ( Domain dom :  
<<<<<<<
 this . domainsDag
=======
domainsDag
>>>>>>>
 . getNodesInTopologicalOrder  ( ) )  {  if  (  dom instanceof CompositeDomain )  {   topologicalOrderList . add  (  ( CompositeDomain ) dom ) ; } }  return topologicalOrderList ; }   private Method getCreateMethod  (  String className ,  String graphClassName ,    Class  <  ? >  [ ] signature ,  ImplementationType implementationType )  {   Class  <  ? extends Graph >  schemaClass = null ;   AttributedElementClass  <  ? ,  ? >  aec = null ;  try  {   schemaClass =  this . getGraphClassImpl  ( implementationType ) ;  if  (  className . equals  ( graphClassName ) )  {  if  (  implementationType !=  ImplementationType . GENERIC )  {  return   this . getClass  ( ) . getMethod  (  "create" + graphClassName , signature ) ; } else  {  return  schemaClass . getMethod  ( "createGraph" , signature ) ; } } else  {   aec =  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getVertexClass  ( className ) ;  if  (  aec == null )  {   aec =  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getEdgeClass  ( className ) ;  if  (  aec == null )  {  throw  new SchemaClassAccessException  (   "class " + className + " does not exist in schema" ) ; } }  if  (  implementationType !=  ImplementationType . GENERIC )  {  return  schemaClass . getMethod  (  "create" +  CodeGenerator . camelCase  (  aec . getUniqueName  ( ) ) , signature ) ; } else  {  if  (   signature [ 0 ] . equals  (  VertexClass . class ) )  {  return  schemaClass . getMethod  ( "createVertex" , signature ) ; } else  {  return  schemaClass . getMethod  ( "createEdge" , signature ) ; } } } }  catch (   SecurityException e )  {  throw  new SchemaClassAccessException  (     "can't find create method in '" +  schemaClass . getName  ( ) + "' for '" +  aec . getUniqueName  ( ) + "'" , e ) ; }  catch (   NoSuchMethodException e )  {  throw  new SchemaClassAccessException  (     "can't find create method in '" +  schemaClass . getName  ( ) + "' for '" +  aec . getUniqueName  ( ) + "'" , e ) ; } } 
<<<<<<<
   @ Override public EdgeClass getDefaultEdgeClass  ( )  {  return  this . defaultEdgeClass ; }
=======
>>>>>>>
 
<<<<<<<
   @ Override public GraphClass getDefaultGraphClass  ( )  {  return  this . defaultGraphClass ; }
=======
>>>>>>>
    @ Override public Package getDefaultPackage  ( )  {  return  this . defaultPackage ; } 
<<<<<<<
   @ Override public VertexClass getDefaultVertexClass  ( )  {  return  this . defaultVertexClass ; }
=======
>>>>>>>
    @ Override public Domain getDomain  (  String domainName )  {  return   this . domains . get  ( domainName ) ; }    @ Override public  Map  < String , Domain > getDomains  ( )  {  return  this . domains ; }   protected  DirectedAcyclicGraph  < Domain > getDomainsDag  ( )  {  return  this . domainsDag ; }    @ Override public  List  < EdgeClass > getEdgeClasses  ( )  {   List  < EdgeClass >  ec_top =  new  ArrayList  < EdgeClass >  ( ) ;   ec_top . add  (  this . defaultEdgeClass ) ;  for ( EdgeClass ec :   this . graphClass . getEdgeClasses  ( ) )  {   ec_top . add  ( ec ) ; }  return 
<<<<<<<
ec_top
=======
 graphClass . getEdgeClasses  ( )
>>>>>>>
 ; }    @ Override public Method getEdgeCreateMethod  (  String edgeClassName ,  ImplementationType implementationType )  {   AttributedElementClass  <  ? ,  ? >  aec =  this . getAttributedElementClass  ( edgeClassName ) ;  if  (   (  aec == null ) ||  !  (  aec instanceof EdgeClass ) )  {  throw  new SchemaException  (   "There's no EdgeClass with qualified name " + edgeClassName + "!" ) ; }  EdgeClass  ec =  ( EdgeClass ) aec ;  String  methodName =  "create" +  CodeGenerator . camelCase  (  ec . getUniqueName  ( ) ) ;   Class  <  ? >  schemaClass =  this . getGraphClassImpl  ( implementationType ) ;  if  (  implementationType !=  ImplementationType . GENERIC )  {  for ( Method m :  schemaClass . getMethods  ( ) )  {  if  (    m . getName  ( ) . equals  ( methodName ) &&  (    m . getParameterTypes  ( ) . length == 3 ) )  {  return m ; } } } else  {  try  {  return  schemaClass . getMethod  ( "createEdge" ,  new Class  [ ]  {  EdgeClass . class ,   int . class ,  Vertex . class ,  Vertex . class } ) ; }  catch (   NoSuchMethodException e )  {   e . printStackTrace  ( ) ; }  catch (   SecurityException e )  {   e . printStackTrace  ( ) ; } }  throw  new SchemaClassAccessException  (       "can't find create method '" + methodName + "' in '" +  schemaClass . getName  ( ) + "' for '" +  ec . getUniqueName  ( ) + "'" ) ; }    @ Override public  List  < EnumDomain > getEnumDomains  ( )  {   ArrayList  < EnumDomain >  enumList =  new  ArrayList  < EnumDomain >  ( ) ;  for ( Domain dl :   this . domains . values  ( ) )  {  if  (  dl instanceof EnumDomain )  {   enumList . add  (  ( EnumDomain ) dl ) ; } }  return enumList ; }    @ Override public BooleanDomain getBooleanDomain  ( )  {  return 
<<<<<<<
 this . booleanDomain
=======
booleanDomain
>>>>>>>
 ; }    @ Override public DoubleDomain getDoubleDomain  ( )  {  return 
<<<<<<<
 this . doubleDomain
=======
doubleDomain
>>>>>>>
 ; }    @ Override public IntegerDomain getIntegerDomain  ( )  {  return 
<<<<<<<
 this . integerDomain
=======
integerDomain
>>>>>>>
 ; }    @ Override public LongDomain getLongDomain  ( )  {  return 
<<<<<<<
 this . longDomain
=======
longDomain
>>>>>>>
 ; }    @ Override public StringDomain getStringDomain  ( )  {  return 
<<<<<<<
 this . stringDomain
=======
stringDomain
>>>>>>>
 ; }    @ Override public GraphClass getGraphClass  ( )  {  return 
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 ; }    @ SuppressWarnings  ( "unchecked" ) private  Class  <  ? extends Graph > getGraphClassImpl  (  ImplementationType implementationType )  {  String  implClassName =  
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 + "." ;  switch  ( implementationType )  {   case STANDARD :   implClassName += IMPLSTDPACKAGENAME ;  break ;   case TRANSACTION :   implClassName += IMPLTRANSPACKAGENAME ;  break ;   case DATABASE :   implClassName += IMPLDATABASEPACKAGENAME ;   case GENERIC :   implClassName = "de.uni_koblenz.jgralab.impl.generic" ;  break ;   default :  throw  new SchemaException  (   "Implementation type " + implementationType + " not supported yet." ) ; }   Class  <  ? extends Graph >  schemaClass ;  if  (  implementationType !=  ImplementationType . GENERIC )  {   implClassName =    implClassName + "." +  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getSimpleName  ( ) + "Impl" ;  try  {   schemaClass =  (  Class  <  ? extends Graph > )  Class . forName  ( implClassName , true ,  SchemaClassManager . instance  ( 
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 ) ) ; }  catch (   ClassNotFoundException e )  {  throw  new SchemaClassAccessException  (   "can't load implementation class '" + implClassName + "'" , e ) ; }  return schemaClass ; } else  {   implClassName +=  "." + "GenericGraphImpl" ;  try  {  return  (  Class  <  ? extends Graph > )  Class . forName  ( implClassName ) ; }  catch (   ClassNotFoundException e )  {  throw  new SchemaClassAccessException  (   "can't load implementation class '" + implClassName + "'" , e ) ; } } }    @ Override public String getName  ( )  {  return 
<<<<<<<
 this . name
=======
name
>>>>>>>
 ; }   private  int getNumberOfElements  ( )  {  return    
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getGraphElementClasses  ( ) . size  ( ) + 1 ; }    @ Override public Package getPackage  (  String packageName )  {  return   this . packages . get  ( packageName ) ; }    @ Override public String getPackagePrefix  ( )  {  return 
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 ; } 
<<<<<<<
   @ Override public  Map  < String , Package > getPackages  ( )  {  return  this . packages ; }
=======
>>>>>>>
    @ Override public String getQualifiedName  ( )  {  return 
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 ; }    @ Override public  List  < RecordDomain > getRecordDomains  ( )  {   ArrayList  < RecordDomain >  recordList =  new  ArrayList  < RecordDomain >  ( ) ;  for ( Domain dl :   this . domains . values  ( ) )  {  if  (  dl instanceof RecordDomain )  {   recordList . add  (  ( RecordDomain ) dl ) ; } }  return recordList ; }    @ Override public  List  < VertexClass > getVertexClasses  ( )  {   List  < VertexClass >  vc_top =  new  ArrayList  < VertexClass >  ( ) ;   vc_top . add  (  this . defaultVertexClass ) ;  for ( VertexClass vc :   this . graphClass . getVertexClasses  ( ) )  {   vc_top . add  ( vc ) ; }  return 
<<<<<<<
vc_top
=======
 graphClass . getVertexClasses  ( )
>>>>>>>
 ; }    @ Override public Method getVertexCreateMethod  (  String vertexClassName ,  ImplementationType implementationType )  {  if  (  implementationType !=  ImplementationType . GENERIC )  {  return  this . getCreateMethod  ( vertexClassName ,  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getSimpleName  ( ) , VERTEX_CLASS_CREATE_SIGNATURE , implementationType ) ; } else  {  return  this . getCreateMethod  ( vertexClassName ,  
<<<<<<<
 this . graphClass
=======
graphClass
>>>>>>>
 . getSimpleName  ( ) ,  new Class  [ ]  {  VertexClass . class ,   int . class } , implementationType ) ; } }    @ Override public boolean isValidEnumConstant  (  String name )  {  if  (  name . isEmpty  ( ) )  {  return false ; }  if  (   ! 
<<<<<<<
 this . allowLowercaseEnumConstants
=======
allowLowercaseEnumConstants
>>>>>>>
 &&  !  name . equals  (  name . toUpperCase  ( ) ) )  {  return false ; }  if  (  RESERVED_JAVA_WORDS . contains  ( name ) )  {  return false ; }  if  (  !  Character . isJavaIdentifierStart  (  name . charAt  ( 0 ) ) )  {  return false ; }  for (  char c :  name . toCharArray  ( ) )  {  if  (  !  Character . isJavaIdentifierPart  ( c ) )  {  return false ; } }  return true ; }    @ Override public boolean knows  (  String qn )  {  return  (   
<<<<<<<
 this . namedElements
=======
namedElements
>>>>>>>
 . containsKey  ( qn ) ||   this . getQualifiedName  ( ) . equals  ( qn ) ) ; }    @ Override public void setAllowLowercaseEnumConstants  (  boolean allowLowercaseEnumConstants )  {    this . allowLowercaseEnumConstants = allowLowercaseEnumConstants ; }  void setGraphClass  (  GraphClass gc )  {  if  (   this . graphClass != null )  {  throw  new SchemaException  (   "There already is a GraphClass named: " +   this . graphClass . getQualifiedName  ( ) + "in the Schema!" ) ; }    this . graphClass = gc ; }   public String getDescriptionString  ( )  {  return    "GraphClass of schema '" + 
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 + "':\n\n\n" +  
<<<<<<<
 (  ( GraphClassImpl )  this . graphClass )
=======
graphClass
>>>>>>>
 . getDescriptionString  ( ) ; }    @ Override public String toString  ( )  {  return  this . getQualifiedName  ( ) ; }    @ Override public String toTGString  ( )  {  String  schemaDefinition = null ;  ByteArrayOutputStream  byteOut =  new ByteArrayOutputStream  ( ) ;  DataOutputStream  out =  new DataOutputStream  ( byteOut ) ;  try  {   GraphIO . saveSchemaToStream  ( this , out ) ;   out . close  ( ) ;   byteOut . close  ( ) ;   schemaDefinition =  new String  (  byteOut . toByteArray  ( ) ) ; }  catch (   GraphIOException e )  {  throw 
<<<<<<<
 new RuntimeException  ( e )
=======
 new SchemaException  ( e )
>>>>>>>
 ; }  catch (   IOException e )  {  throw 
<<<<<<<
 new RuntimeException  ( e )
=======
 new SchemaException  ( e )
>>>>>>>
 ; }  return schemaDefinition ; }    @ Override public String getFileName  ( )  {  return  
<<<<<<<
 this . qualifiedName
=======
qualifiedName
>>>>>>>
 . replace  ( '.' ,  File . separatorChar ) ; }    @ Override public String getPathName  ( )  {  return  
<<<<<<<
 this . packagePrefix
=======
packagePrefix
>>>>>>>
 . replace  ( '.' ,  File . separatorChar ) ; }    @ Override public GraphFactory createDefaultGraphFactory  (  ImplementationType implementationType )  {   assertFinished  ( ) ;  if  (  implementationType !=  ImplementationType . GENERIC )  {  throw 
<<<<<<<
 new IllegalArgumentException  (   "Base implementation can't create a GraphFactory for implementation type " + implementationType + ". Only GENERIC is supported." )
=======
 new SchemaException  (   "Base implementation can't create a GraphFactory for implementation type " + implementationType + ". Only GENERIC is supported." )
>>>>>>>
 ; }  return  new GenericGraphFactoryImpl  ( this ) ; }    @ Override public Graph createGraph  (  ImplementationType implementationType )  {  return  this . createGraph  ( implementationType , null , 100 , 100 ) ; }    @ Override public Graph createGraph  (  ImplementationType implementationType ,  String id ,   int vMax ,   int eMax )  {   assertFinished  ( ) ;  GraphFactory  factory =  this . createDefaultGraphFactory  ( implementationType ) ;  return  factory . createGraph  (  this . getGraphClass  ( ) , id , vMax , eMax ) ; }    @ Override public boolean isFinished  ( )  {  return 
<<<<<<<
 this . finished
=======
finished
>>>>>>>
 ; }    @ Override public void finish  ( )  {  if  ( 
<<<<<<<
 this . finished
=======
finished
>>>>>>>
 )  {  return ; }  if  (  graphClass == null )  {  throw  new SchemaException  ( "Can't finish a schema without a GraphClass. Create a GraphClass first!" ) ; }   domainsDag . finish  ( ) ;   
<<<<<<<
 (  ( GraphClassImpl )  this . graphClass )
=======
graphClass
>>>>>>>
 . finish  ( ) ;   
<<<<<<<
 this . finished
=======
finished
>>>>>>>
 = true ; }    @ Override public void reopen  ( )  { 
<<<<<<<
 if  (  !  this . finished )  {  return ; }
=======
 throw  new UnsupportedOperationException  ( ) ;
>>>>>>>
    (  ( GraphClassImpl )  this . graphClass ) . finish  ( ) ;    this . finished = false ; }    @ Override public void save  (  String filename )  throws GraphIOException  {   GraphIO . saveSchemaToFile  ( this , filename ) ; }    @ Override public void save  (  DataOutputStream out )  throws GraphIOException  {   GraphIO . saveSchemaToStream  ( this , out ) ; }  PackageImpl createPackage  (  String sn ,  PackageImpl parentPkg )  {   assertNotFinished  ( ) ;  return  new PackageImpl  ( sn , parentPkg , this ) ; }  void addDomainDependency  (  Domain composite ,  Domain base )  {   domainsDag . createEdge  ( base , composite ) ; }  void setGraphClass  (  GraphClassImpl gc )  {  if  (  graphClass != null )  {  throw  new SchemaException  (   "A GraphClass named '" +  graphClass . getQualifiedName  ( ) + "' already exists in this Schema!" ) ; }   graphClass = gc ; }   protected void assertFinished  ( )  {  if  (  ! finished )  {  throw  new SchemaException  ( "Schema must be finished." ) ; } }   protected void assertNotFinished  ( )  {  if  ( finished )  {  throw  new SchemaException  ( "No changes allowed in a finished Schema." ) ; } } }