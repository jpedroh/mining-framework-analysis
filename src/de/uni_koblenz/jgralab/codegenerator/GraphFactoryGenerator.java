  package    de . uni_koblenz . jgralab . codegenerator ;   import     de . uni_koblenz . jgralab . schema . EdgeClass ;  import     de . uni_koblenz . jgralab . schema . GraphClass ;  import     de . uni_koblenz . jgralab . schema . Schema ;  import     de . uni_koblenz . jgralab . schema . VertexClass ;   public class GraphFactoryGenerator  extends CodeGenerator  {   private final Schema  schema ;   public GraphFactoryGenerator  (  Schema schema ,  String schemaPackageName ,  CodeGeneratorConfiguration config )  {  super  ( schemaPackageName , "" , config ) ;    this . schema = schema ;   rootBlock . setVariable  ( "schemaName" ,  schema . getQualifiedName  ( ) ) ;   rootBlock . setVariable  ( 
<<<<<<<
"className"
=======
"simpleClassName"
>>>>>>>
 ,   
<<<<<<<
schema
=======
 schema . getGraphClass  ( )
>>>>>>>
 . 
<<<<<<<
getName
=======
getSimpleName
>>>>>>>
  ( ) + "Factory" ) ;   rootBlock . setVariable  ( 
<<<<<<<
"simpleClassName"
=======
"simpleImplClassName"
>>>>>>>
 ,   
<<<<<<<
schema
=======
 schema . getGraphClass  ( )
>>>>>>>
 . 
<<<<<<<
getName
=======
getSimpleName
>>>>>>>
  ( ) + 
<<<<<<<
"Factory"
=======
"FactoryImpl"
>>>>>>>
 ) ;   rootBlock . setVariable  ( "isClassOnly" , 
<<<<<<<
"true"
=======
"false"
>>>>>>>
 ) ; }    @ Override protected CodeBlock createHeader  ( )  {   addImports  ( "#jgImplPackage#.GraphFactoryImpl" ) ;  CodeSnippet  code =  new CodeSnippet  ( true ) ; 
<<<<<<<
  code . setVariable  ( "className" ,   schema . getName  ( ) + "Factory" ) ;
=======
 if  (  currentCycle . isAbstract  ( ) )  {   addImports  ( "#jgPackage#.GraphFactory" ) ;   code . add  ( "public interface #simpleClassName# extends GraphFactory {" ) ; } else  {   addImports  ( "#schemaPackage#.#simpleClassName#" ) ;   addImports  ( "#jgImplPackage#.GraphFactoryImpl" ) ;   addImports  ( "#jgPackage#.ImplementationType" ) ;   code . add  ( "public class #simpleImplClassName# extends GraphFactoryImpl implements #simpleClassName# {" ) ; }
>>>>>>>
   code . add  ( "public class #className# extends GraphFactoryImpl {" ) ;  return code ; }    @ Override protected CodeBlock createBody  ( )  {  CodeList  code =  new CodeList  ( ) ;  if  (  currentCycle . 
<<<<<<<
isClassOnly
=======
isStdOrDbImplOrTransImpl
>>>>>>>
  ( ) )  {   code . add  (  createConstructor  ( ) ) ;   code . add  (  createFillTableMethod  ( ) ) ; }  return code ; }   protected CodeBlock createConstructor  ( )  {  CodeList  code =  new CodeList  ( ) ; 
<<<<<<<
  code . setVariable  ( "className" ,   schema . getName  ( ) + "Factory" ) ;
=======
 if  (  currentCycle . isStdImpl  ( ) )  {   code . setVariable  ( "implTypeInfix" , "STANDARD" ) ; }
>>>>>>>
  if  (  currentCycle . isTransImpl  ( ) )  {   code . setVariable  ( "implTypeInfix" , "TRANSACTION" ) ; }  if  (  currentCycle . isDbImpl  ( ) )  {   code . setVariable  ( "implTypeInfix" , "DATABASE" ) ; }  CodeSnippet  s =  new CodeSnippet  ( true ) ;   s . add  ( "public #simpleImplClassName#() {" , "\tsuper(#schemaName#.instance(), ImplementationType.#implTypeInfix#);" , "\tcreateMaps();" ) ;   code . addNoIndent  ( s ) ;   code . add  (  createFillTableMethod  ( ) ) ;   code . addNoIndent  (  new CodeSnippet  ( "}" ) ) ;  return code ; }   protected CodeBlock createFillTableMethod  ( )  {  if  (  currentCycle . isAbstract  ( ) )  {  return null ; }  CodeList  code =  new CodeList  ( ) ;  GraphClass  graphClass =  schema . getGraphClass  ( ) ;   code . add  (  createFillTableForGraph  ( graphClass ) ) ;  for ( VertexClass vertexClass :  graphClass . getVertexClasses  ( ) )  {   code . 
<<<<<<<
add
=======
addNoIndent
>>>>>>>
  (  createFillTableForVertex  ( vertexClass ) ) ; }  for ( EdgeClass edgeClass :  graphClass . getEdgeClasses  ( ) )  {   code . addNoIndent  (  createFillTableForEdge  ( edgeClass ) ) ; }  return code ; }   protected CodeBlock createFillTableForGraph  (  GraphClass graphClass )  {  if  (  graphClass . isAbstract  ( ) )  {  return null ; }  CodeSnippet  code =  new CodeSnippet  ( false ) ;   code . setVariable  ( "graphName" ,   graphClass . getQualifiedName  ( ) + ".GC" ) ;   code . setVariable  ( "graphImplName" ,   "#schemaImplStdPackage#." + 
<<<<<<<
".impl.std."
=======
 graphClass . getQualifiedName  ( )
>>>>>>>
 + "Impl" ) ;   code . setVariable  ( "graphTransactionImplName" ,   
<<<<<<<
schemaRootPackageName
=======
"#schemaImplTransPackage#."
>>>>>>>
 + 
<<<<<<<
".impl.trans."
=======
 graphClass . getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
 graphClass . getQualifiedName  ( )
=======
"Impl"
>>>>>>>
 ) ;   code . setVariable  ( "graphDatabaseImplName" ,   
<<<<<<<
schemaRootPackageName
=======
"#schemaImplDbPackage#."
>>>>>>>
 + 
<<<<<<<
".impl.db."
=======
 graphClass . getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
 graphClass . getQualifiedName  ( )
=======
"Impl"
>>>>>>>
 ) ;  if  (  !  graphClass . isAbstract  ( ) )  {  if  ( 
<<<<<<<
 config . hasStandardSupport  ( )
=======
  currentCycle . isStdImpl  ( ) &&  config . hasStandardSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setGraphImplementationClass(#graphName#.class, #graphImplName#Impl.class);"
=======
"setGraphImplementationClass(#schemaPackage#.#graphName#, #graphImplName#.class);"
>>>>>>>
 ) ; }  if  ( 
<<<<<<<
 config . hasTransactionSupport  ( )
=======
  currentCycle . isTransImpl  ( ) &&  config . hasTransactionSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setGraphTransactionImplementationClass(#graphName#.class, #graphTransactionImplName#Impl.class);"
=======
"setGraphImplementationClass(#schemaPackage#.#graphName#, #graphTransactionImplName#.class);"
>>>>>>>
 ) ; }  if  ( 
<<<<<<<
 config . hasDatabaseSupport  ( )
=======
  currentCycle . isDbImpl  ( ) &&  config . hasDatabaseSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setGraphDatabaseImplementationClass(#graphName#.class, #graphDatabaseImplName#Impl.class);"
=======
"setGraphImplementationClass(#schemaPackage#.#graphName#, #graphDatabaseImplName#.class);"
>>>>>>>
 ) ; } }  return code ; }   protected CodeBlock createFillTableForVertex  (  VertexClass vertexClass )  {  if  (  vertexClass . isAbstract  ( ) )  {  return null ; }  CodeSnippet  code =  new CodeSnippet  ( false ) ;   code . setVariable  ( "vertexName" ,   vertexClass . getQualifiedName  ( ) + ".VC" ) ;   code . setVariable  ( "vertexImplName" ,   "#schemaImplStdPackage#." + 
<<<<<<<
".impl.std."
=======
 vertexClass . getQualifiedName  ( )
>>>>>>>
 + "Impl" ) ;   code . setVariable  ( "vertexTransactionImplName" ,   
<<<<<<<
schemaRootPackageName
=======
"#schemaImplTransPackage#."
>>>>>>>
 + 
<<<<<<<
".impl.trans."
=======
 vertexClass . getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
 vertexClass . getQualifiedName  ( )
=======
"Impl"
>>>>>>>
 ) ;   code . setVariable  ( "vertexDatabaseImplName" ,   
<<<<<<<
schemaRootPackageName
=======
"#schemaImplDbPackage#."
>>>>>>>
 + 
<<<<<<<
".impl.db."
=======
 vertexClass . getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
 vertexClass . getQualifiedName  ( )
=======
"Impl"
>>>>>>>
 ) ;  if  (  !  vertexClass . isAbstract  ( ) )  {  if  ( 
<<<<<<<
 config . hasStandardSupport  ( )
=======
  currentCycle . isStdImpl  ( ) &&  config . hasStandardSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setVertexImplementationClass(#vertexName#.class, #vertexImplName#Impl.class);"
=======
"setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexImplName#.class);"
>>>>>>>
 ) ; }  if  ( 
<<<<<<<
 config . hasTransactionSupport  ( )
=======
  currentCycle . isTransImpl  ( ) &&  config . hasTransactionSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setVertexTransactionImplementationClass(#vertexName#.class, #vertexTransactionImplName#Impl.class);"
=======
"setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexTransactionImplName#.class);"
>>>>>>>
 ) ; }  if  ( 
<<<<<<<
 config . hasDatabaseSupport  ( )
=======
  currentCycle . isDbImpl  ( ) &&  config . hasDatabaseSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setVertexDatabaseImplementationClass(#vertexName#.class, #vertexDatabaseImplName#Impl.class);"
=======
"setVertexImplementationClass(#schemaPackage#.#vertexName#, #vertexDatabaseImplName#.class);"
>>>>>>>
 ) ; } }  return code ; }   protected CodeBlock createFillTableForEdge  (  EdgeClass edgeClass )  {  CodeSnippet  code =  new CodeSnippet  ( false ) ;   code . setVariable  ( "edgeName" ,   edgeClass . getQualifiedName  ( ) + ".EC" ) ;   code . setVariable  ( "edgeImplName" ,   "#schemaImplStdPackage#." + 
<<<<<<<
".impl.std."
=======
 edgeClass . getQualifiedName  ( )
>>>>>>>
 + "Impl" ) ;   code . setVariable  ( "edgeTransactionImplName" ,   
<<<<<<<
schemaRootPackageName
=======
"#schemaImplTransPackage#."
>>>>>>>
 + 
<<<<<<<
".impl.trans."
=======
 edgeClass . getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
 edgeClass . getQualifiedName  ( )
=======
"Impl"
>>>>>>>
 ) ;   code . setVariable  ( "edgeDatabaseImplName" ,   
<<<<<<<
schemaRootPackageName
=======
"#schemaImplDbPackage#."
>>>>>>>
 + 
<<<<<<<
".impl.db."
=======
 edgeClass . getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
 edgeClass . getQualifiedName  ( )
=======
"Impl"
>>>>>>>
 ) ;  if  (  !  edgeClass . isAbstract  ( ) )  {  if  ( 
<<<<<<<
 config . hasStandardSupport  ( )
=======
  currentCycle . isStdImpl  ( ) &&  config . hasStandardSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setEdgeImplementationClass(#edgeName#.class, #edgeImplName#Impl.class);"
=======
"setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeImplName#.class);"
>>>>>>>
 ) ; }  if  ( 
<<<<<<<
 config . hasTransactionSupport  ( )
=======
  currentCycle . isTransImpl  ( ) &&  config . hasTransactionSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setEdgeTransactionImplementationClass(#edgeName#.class, #edgeTransactionImplName#Impl.class);"
=======
"setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeTransactionImplName#.class);"
>>>>>>>
 ) ; }  if  ( 
<<<<<<<
 config . hasDatabaseSupport  ( )
=======
  currentCycle . isDbImpl  ( ) &&  config . hasDatabaseSupport  ( )
>>>>>>>
 )  {   code . add  ( 
<<<<<<<
"setEdgeDatabaseImplementationClass(#edgeName#.class, #edgeDatabaseImplName#Impl.class);"
=======
"setEdgeImplementationClass(#schemaPackage#.#edgeName#, #edgeDatabaseImplName#.class);"
>>>>>>>
 ) ; } }  return code ; } }