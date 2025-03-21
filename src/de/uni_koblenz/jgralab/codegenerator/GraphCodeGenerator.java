  package    de . uni_koblenz . jgralab . codegenerator ;   import   java . util . HashSet ;  import   java . util . Set ;  import   java . util . TreeSet ;  import     de . uni_koblenz . jgralab . schema . EdgeClass ;  import     de . uni_koblenz . jgralab . schema . GraphClass ;  import     de . uni_koblenz . jgralab . schema . GraphElementClass ;  import     de . uni_koblenz . jgralab . schema . VertexClass ;   public class GraphCodeGenerator  extends AttributedElementCodeGenerator  {   public GraphCodeGenerator  (  GraphClass graphClass ,  String schemaPackageName ,  String schemaName ,  CodeGeneratorConfiguration config )  {  super  ( graphClass , schemaPackageName , config ) ;   rootBlock . setVariable  ( "graphElementClass" , "Graph" ) ;   rootBlock . setVariable  ( "schemaElementClass" , "GraphClass" ) ;   rootBlock . setVariable  ( "schemaName" , schemaName ) ;   rootBlock . setVariable  ( "theGraph" , "this" ) ; }    @ Override protected CodeBlock createHeader  ( )  {  return  super . createHeader  ( ) ; }    @ Override protected CodeBlock createBody  ( )  {  CodeList  code =  ( CodeList )  super . createBody  ( ) ;  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {  if  (  currentCycle . isStdImpl  ( ) )  {   addImports  ( "#jgImplStdPackage#.#baseClassName#" ) ; }  if  (  currentCycle . isTransImpl  ( ) )  {   addImports  ( "#jgImplTransPackage#.#baseClassName#" ) ; }  if  (  currentCycle . isDbImpl  ( ) )  {   addImports  ( "de.uni_koblenz.jgralab.GraphException" , "#jgImplDbPackage#.#baseClassName#" , "#jgImplDbPackage#.GraphDatabase" , "#jgImplDbPackage#.GraphDatabaseException" ) ; }   rootBlock . setVariable  ( "baseClassName" , "GraphImpl" ) ;   addImports  ( "org.pcollections.POrderedSet" ) ;   addImports  ( "#jgPackage#.Vertex" ) ;   addImports  ( "#jgPackage#.greql2.evaluator.GreqlEvaluator" ) ;   code . add  (  new CodeSnippet  ( "\n\tprotected GreqlEvaluator greqlEvaluator;\n" , "@Override" , "public synchronized <T extends Vertex> POrderedSet<T> reachableVertices(Vertex startVertex, String pathDescription, Class<T> vertexType) {" , "\tif (greqlEvaluator == null) {" , 
<<<<<<<
"public synchronized <T extends de.uni_koblenz.jgralab.Vertex> org.pcollections.POrderedSet<T> reachableVertices(de.uni_koblenz.jgralab.Vertex startVertex, String pathDescription, Class<T> vertexType) {"
=======
"\t\tgreqlEvaluator = new GreqlEvaluator((String) null, this, null);"
>>>>>>>
 , 
<<<<<<<
"\tde.uni_koblenz.jgralab.greql2.evaluator.Query q = new de.uni_koblenz.jgralab.greql2.evaluator.Query(\"using v: v \" + pathDescription);"
=======
"\t}"
>>>>>>>
 , 
<<<<<<<
"\tjava.util.HashMap<String, Object> variables = new java.util.HashMap<String, Object>();"
=======
"\tgreqlEvaluator.setVariable(\"v\", startVertex);"
>>>>>>>
 , 
<<<<<<<
"\tvariables.put(\"v\", startVertex);"
=======
"\tgreqlEvaluator.setQuery(\"using v: v \" + pathDescription);"
>>>>>>>
 , 
<<<<<<<
"\tde.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator eval = new de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator(q, this, variables, null);"
=======
"\tgreqlEvaluator.startEvaluation();"
>>>>>>>
 , 
<<<<<<<
"\treturn eval.getResultSet();"
=======
"\treturn greqlEvaluator.getResultSet();"
>>>>>>>
 , "}" ) ) ; }   code . add  (  createGraphElementClassMethods  ( ) ) ;   code . add  (  createEdgeIteratorMethods  ( ) ) ;   code . add  (  createVertexIteratorMethods  ( ) ) ;  return code ; }    @ Override protected CodeBlock createConstructor  ( )  {  CodeSnippet  code =  new CodeSnippet  ( true ) ;  if  (  currentCycle . isTransImpl  ( ) )  {   code . setVariable  ( "createSuffix" , 
<<<<<<<
"WithTransactionSupport"
=======
"TRANSACTION"
>>>>>>>
 ) ; }  if  (  currentCycle . isStdImpl  ( ) )  {   code . setVariable  ( "createSuffix" , 
<<<<<<<
""
=======
"STANDARD"
>>>>>>>
 ) ; }  if  (  currentCycle . isDbImpl  ( ) )  {   code . setVariable  ( "createSuffix" , 
<<<<<<<
"WithDatabaseSupport"
=======
"DATABASE"
>>>>>>>
 ) ; }  if  (  !  currentCycle . isDbImpl  ( ) )  {   code . add  ( 
<<<<<<<
"/* Constructors and create methods with values for initial vertex and edge count */"
=======
"/**"
>>>>>>>
 , "public #simpleClassName#Impl(int vMax, int eMax) {" , "\tthis(null, vMax, eMax);" , 
<<<<<<<
"}"
=======
" * DON'T USE THE CONSTRUCTOR"
>>>>>>>
 , 
<<<<<<<
""
=======
" * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>>
 , 
<<<<<<<
"public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax) {"
=======
"**/"
>>>>>>>
 , 
<<<<<<<
"\tsuper(id, #schemaName#.instance().#schemaVariableName#, vMax, eMax);"
=======
"public #simpleImplClassName#() {"
>>>>>>>
 , 
<<<<<<<
"\tinitializeAttributesWithDefaultValues();"
=======
"\tthis(null);"
>>>>>>>
 , "}" , "" , 
<<<<<<<
"public static #javaClassName# create(int vMax, int eMax) {"
=======
"/**"
>>>>>>>
 , 
<<<<<<<
"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null, vMax, eMax);"
=======
" * DON'T USE THE CONSTRUCTOR"
>>>>>>>
 , 
<<<<<<<
"}"
=======
" * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>>
 , 
<<<<<<<
""
=======
"**/"
>>>>>>>
 , 
<<<<<<<
"public static #javaClassName# create(String id, int vMax, int eMax) {"
=======
"public #simpleImplClassName#(int vMax, int eMax) {"
>>>>>>>
 , 
<<<<<<<
"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, vMax, eMax);"
=======
"\tthis(null, vMax, eMax);"
>>>>>>>
 , "}" , "" , "/* Constructors and create methods without values for initial vertex and edge count */" , 
<<<<<<<
"public #simpleClassName#Impl() {"
=======
"/**"
>>>>>>>
 , 
<<<<<<<
"\tthis(null);"
=======
" * DON'T USE THE CONSTRUCTOR"
>>>>>>>
 , 
<<<<<<<
"}"
=======
" * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>>
 , 
<<<<<<<
""
=======
"**/"
>>>>>>>
 , 
<<<<<<<
"public #simpleClassName#Impl(java.lang.String id) {"
=======
"public #simpleImplClassName#(java.lang.String id, int vMax, int eMax) {"
>>>>>>>
 , 
<<<<<<<
"\tsuper(id, #schemaName#.instance().#schemaVariableName#);"
=======
"\tsuper(id, #javaClassName#.GC, vMax, eMax);"
>>>>>>>
 , "\tinitializeAttributesWithDefaultValues();" , "}" , "" , "/**" , 
<<<<<<<
"public static #javaClassName# create() {"
=======
" * DON'T USE THE CONSTRUCTOR"
>>>>>>>
 , 
<<<<<<<
"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null);"
=======
" * For instantiating a Graph, use the Schema and a GraphFactory"
>>>>>>>
 , 
<<<<<<<
"}"
=======
"**/"
>>>>>>>
 , 
<<<<<<<
""
=======
"public #simpleImplClassName#(java.lang.String id) {"
>>>>>>>
 , 
<<<<<<<
"public static #javaClassName# create(String id) {"
=======
"\tsuper(id, #javaClassName#.GC);"
>>>>>>>
 , 
<<<<<<<
"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id);"
=======
"\tinitializeAttributesWithDefaultValues();"
>>>>>>>
 , "}" ) ; } else  {   code . add  ( 
<<<<<<<
"/* Constructors and create methods for database support */"
=======
"/**"
>>>>>>>
 , "" , "public #simpleClassName#Impl(java.lang.String id, GraphDatabase graphDatabase) {" , "\tsuper(id, #schemaName#.instance().#schemaVariableName#, graphDatabase);" , 
<<<<<<<
"\tinitializeAttributesWithDefaultValues();"
=======
" * DON'T USE THE CONSTRUCTOR"
>>>>>>>
 , 
<<<<<<<
"}"
=======
" * For instantiating a Graph, use a GraphFactory"
>>>>>>>
 , 
<<<<<<<
""
=======
"**/"
>>>>>>>
 , 
<<<<<<<
"public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax, GraphDatabase graphDatabase) {"
=======
"public #simpleImplClassName#(java.lang.String id, GraphDatabase graphDatabase) {"
>>>>>>>
 , 
<<<<<<<
"\tsuper(id, vMax, eMax, #schemaName#.instance().#schemaVariableName#, graphDatabase);"
=======
"\tsuper(id, #javaClassName#.GC, graphDatabase);"
>>>>>>>
 , "\tinitializeAttributesWithDefaultValues();" , "}" , "" , 
<<<<<<<
"public static #javaClassName# create(String id, GraphDatabase graphDatabase) {"
=======
"/**"
>>>>>>>
 , 
<<<<<<<
"\ttry{"
=======
" * DON'T USE THE CONSTRUCTOR"
>>>>>>>
 , 
<<<<<<<
"\t\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, graphDatabase);"
=======
" * For instantiating a Graph, use a GraphFactory"
>>>>>>>
 , 
<<<<<<<
"\t}"
=======
"**/"
>>>>>>>
 , 
<<<<<<<
"\tcatch(GraphDatabaseException exception){"
=======
"public #simpleImplClassName#(java.lang.String id, int vMax, int eMax, GraphDatabase graphDatabase) {"
>>>>>>>
 , 
<<<<<<<
"\t\tthrow new GraphException(\"Could not create graph.\", exception);"
=======
"\tsuper(id, vMax, eMax, #javaClassName#.GC, graphDatabase);"
>>>>>>>
 , 
<<<<<<<
"\t}"
=======
"\tinitializeAttributesWithDefaultValues();"
>>>>>>>
 , "}" ) ; }  return code ; }   private CodeBlock createGraphElementClassMethods  ( )  {  CodeList  code =  new CodeList  ( ) ;  GraphClass  gc =  ( GraphClass ) aec ;   TreeSet  < 
<<<<<<<
GraphElementClass
=======
 GraphElementClass  <  ? ,  ? >
>>>>>>>
 >  sortedClasses =  new  TreeSet  < 
<<<<<<<
GraphElementClass
=======
 GraphElementClass  <  ? ,  ? >
>>>>>>>
 >  ( ) ;   sortedClasses . addAll  (  gc . getGraphElementClasses  ( ) ) ;  for ( 
<<<<<<<
GraphElementClass
=======
 GraphElementClass  <  ? ,  ? >
>>>>>>>
 gec : sortedClasses )  { 
<<<<<<<
 if  (  !  gec . isInternal  ( ) )  {  CodeList  gecCode =  new CodeList  ( ) ;   code . addNoIndent  ( gecCode ) ;   gecCode . addNoIndent  (  new CodeSnippet  ( true , "// ------------------------ Code for #ecQualifiedName# ------------------------" ) ) ;   gecCode . setVariable  ( "ecSimpleName" ,  gec . getSimpleName  ( ) ) ;   gecCode . setVariable  ( "ecUniqueName" ,  gec . getUniqueName  ( ) ) ;   gecCode . setVariable  ( "ecQualifiedName" ,  gec . getQualifiedName  ( ) ) ;   gecCode . setVariable  ( "ecSchemaVariableName" ,  gec . getVariableName  ( ) ) ;   gecCode . setVariable  ( "ecJavaClassName" ,   schemaRootPackageName + "." +  gec . getQualifiedName  ( ) ) ;   gecCode . setVariable  ( "ecType" ,  (   gec instanceof VertexClass ? "Vertex" : "Edge" ) ) ;   gecCode . setVariable  ( "ecTypeInComment" ,  (   gec instanceof VertexClass ? "vertex" : "edge" ) ) ;   gecCode . setVariable  ( "ecCamelName" ,  camelCase  (  gec . getUniqueName  ( ) ) ) ;   gecCode . setVariable  ( "ecImplName" ,  (   gec . isAbstract  ( ) ? "**ERROR**" :   camelCase  (  gec . getQualifiedName  ( ) ) + "Impl" ) ) ;   gecCode . addNoIndent  (  createGetFirstMethods  ( gec ) ) ;   gecCode . addNoIndent  (  createFactoryMethods  ( gec ) ) ; }
=======
 CodeList  gecCode =  new CodeList  ( ) ;
>>>>>>>
   code . addNoIndent  ( gecCode ) ;   gecCode . addNoIndent  (  new CodeSnippet  ( true , "// ------------------------ Code for #ecQualifiedName# ------------------------" ) ) ;   gecCode . setVariable  ( "ecSimpleName" ,  gec . getSimpleName  ( ) ) ;   gecCode . setVariable  ( "ecUniqueName" ,  gec . getUniqueName  ( ) ) ;   gecCode . setVariable  ( "ecQualifiedName" ,  gec . getQualifiedName  ( ) ) ;   gecCode . setVariable  ( "ecSchemaVariableName" ,  gec . getVariableName  ( ) ) ;   gecCode . setVariable  ( "ecJavaClassName" ,   schemaRootPackageName + "." +  gec . getQualifiedName  ( ) ) ;   gecCode . setVariable  ( "ecType" ,  (   gec instanceof VertexClass ? "Vertex" : "Edge" ) ) ;   gecCode . setVariable  ( "ecTypeInComment" ,  (   gec instanceof VertexClass ? "vertex" : "edge" ) ) ;   gecCode . setVariable  ( "ecTypeAecConstant" ,  (   gec instanceof VertexClass ? "VC" : "EC" ) ) ;   gecCode . setVariable  ( "ecCamelName" ,  camelCase  (  gec . getUniqueName  ( ) ) ) ;   gecCode . setVariable  ( "ecImplName" ,  (   gec . isAbstract  ( ) ? "**ERROR**" :   camelCase  (  gec . getQualifiedName  ( ) ) + "Impl" ) ) ;   gecCode . addNoIndent  (  createGetFirstMethods  ( gec ) ) ;   gecCode . addNoIndent  (  createFactoryMethods  ( gec ) ) ; }  return code ; }   private CodeBlock createGetFirstMethods  (  GraphElementClass gec )  {  CodeList  code =  new CodeList  ( ) ;  if  (  config . hasTypeSpecificMethodsSupport  ( ) )  {   code . addNoIndent  (  createGetFirstMethod  ( gec ) ) ; }  return code ; }   private CodeBlock createGetFirstMethod  (  GraphElementClass gec )  {  CodeSnippet  code =  new CodeSnippet  ( true ) ;  if  (  currentCycle . isAbstract  ( ) )  {   code . add  ( "/**" , " * @return the first #ecSimpleName# #ecTypeInComment# in this graph" ) ;   code . add  ( " */" , "public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#);" ) ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   code . add  ( "public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#) {" , "\treturn (#ecJavaClassName#)getFirst#ecType#(#schemaName#.instance().#ecSchemaVariableName##actualParams#);" , "}" ) ; }   code . setVariable  ( "formalParams" , "" ) ;   code . setVariable  ( "actualParams" , "" ) ;  return code ; }   private CodeBlock createFactoryMethods  (  GraphElementClass gec )  {  if  (  gec . isAbstract  ( ) )  {  return null ; }  CodeList  code =  new CodeList  ( ) ;   code . addNoIndent  (  createFactoryMethod  ( gec , false ) ) ;  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   code . addNoIndent  (  createFactoryMethod  ( gec , true ) ) ; }  return code ; }   private CodeBlock createFactoryMethod  (  GraphElementClass gec ,  boolean withId )  {  CodeSnippet  code =  new CodeSnippet  ( true ) ;  if  (  currentCycle . isStdImpl  ( ) )  {   code . setVariable  ( "cycleSupportSuffix" , "" ) ; } else  if  (  currentCycle . isTransImpl  ( ) )  {   code . setVariable  ( "cycleSupportSuffix" , "WithTransactionSupport" ) ; } else  if  (  currentCycle . isDbImpl  ( ) )  {   code . setVariable  ( "cycleSupportSuffix" , "WithDatabaseSupport" ) ; }  if  (  currentCycle . isAbstract  ( ) )  {   code . add  ( "/**" , " * Creates a new #ecUniqueName# #ecTypeInComment# in this graph." , " *" ) ;  if  ( withId )  {   code . add  ( " * @param id the <code>id</code> of the #ecTypeInComment#" ) ; }  if  (  gec instanceof EdgeClass )  {   code . add  ( " * @param alpha the start vertex of the edge" , " * @param omega the target vertex of the edge" ) ; }   code . add  ( "*/" , "public #ecJavaClassName# create#ecCamelName#(#formalParams#);" ) ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   code . add  ( "public #ecJavaClassName# create#ecCamelName#(#formalParams#) {" , "\t#ecJavaClassName# new#ecType# = (#ecJavaClassName#) graphFactory.create#ecType##cycleSupportSuffix#(#ecJavaClassName#.class, #newActualParams#, this#additionalParams#);" , "\treturn new#ecType#;" , "}" ) ;   code . setVariable  ( "additionalParams" , "" ) ; }  if  (  gec instanceof EdgeClass )  {  EdgeClass  ec =  ( EdgeClass ) gec ;  String  fromClass =    ec . getFrom  ( ) . getVertexClass  ( ) . getQualifiedName  ( ) ;  String  toClass =    ec . getTo  ( ) . getVertexClass  ( ) . getQualifiedName  ( ) ;  if  (  fromClass . equals  ( "Vertex" ) )  {   code . setVariable  ( "fromClass" ,    rootBlock . getVariable  ( "jgPackage" ) + "." + "Vertex" ) ; } else  {   code . setVariable  ( "fromClass" ,   schemaRootPackageName + "." + fromClass ) ; }  if  (  toClass . equals  ( "Vertex" ) )  {   code . setVariable  ( "toClass" ,    rootBlock . getVariable  ( "jgPackage" ) + "." + "Vertex" ) ; } else  {   code . setVariable  ( "toClass" ,   schemaRootPackageName + "." + toClass ) ; }   code . setVariable  ( "formalParams" ,   (  withId ? "int id, " : "" ) + "#fromClass# alpha, #toClass# omega" ) ;   code . setVariable  ( "addActualParams" , ", alpha, omega" ) ;   code . setVariable  ( "additionalParams" , ", alpha, omega" ) ; } else  {   code . setVariable  ( "formalParams" ,  (  withId ? "int id" : "" ) ) ;   code . setVariable  ( "addActualParams" , "" ) ; }   code . setVariable  ( "newActualParams" ,  (  withId ? "id" : "0" ) ) ;  return code ; }   private CodeBlock createEdgeIteratorMethods  ( )  {  GraphClass  gc =  ( GraphClass ) aec ;  CodeList  code =  new CodeList  ( ) ;  if  (  !  config . hasTypeSpecificMethodsSupport  ( ) )  {  return code ; }   Set  < EdgeClass >  edgeClassSet =  new  HashSet  < EdgeClass >  ( ) ;   edgeClassSet . addAll  (  gc . getEdgeClasses  ( ) ) ;  for ( EdgeClass edge : edgeClassSet )  {  if  (  edge . isInternal  ( ) )  {  continue ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   addImports  ( "#jgImplPackage#.EdgeIterable" ) ; }  CodeSnippet  s =  new CodeSnippet  ( true ) ;   code . addNoIndent  ( s ) ;   s . setVariable  ( "edgeUniqueName" ,  camelCase  (  edge . getUniqueName  ( ) ) ) ;   s . setVariable  ( "edgeQualifiedName" ,  edge . getQualifiedName  ( ) ) ;   s . setVariable  ( "edgeJavaClassName" ,   schemaRootPackageName + "." +  edge . getQualifiedName  ( ) ) ;  if  (  currentCycle . isAbstract  ( ) )  {   s . add  ( "/**" ) ;   s . add  ( " * @return an Iterable for all edges of this graph that are of type #edgeQualifiedName# or subtypes." ) ;   s . add  ( " */" ) ;   s . add  ( "public Iterable<#edgeJavaClassName#> get#edgeUniqueName#Edges();" ) ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   s . add  ( "public Iterable<#edgeJavaClassName#> get#edgeUniqueName#Edges() {" ) ;   s . add  ( "\treturn new EdgeIterable<#edgeJavaClassName#>(this, #edgeJavaClassName#.class);" ) ;   s . add  ( "}" ) ; }   s . add  ( "" ) ; }  return code ; }   private CodeBlock createVertexIteratorMethods  ( )  {  GraphClass  gc =  ( GraphClass ) aec ;  CodeList  code =  new CodeList  ( ) ;  if  (  !  config . hasTypeSpecificMethodsSupport  ( ) )  {  return code ; }   Set  < VertexClass >  vertexClassSet =  new  HashSet  < VertexClass >  ( ) ;   vertexClassSet . addAll  (  gc . getVertexClasses  ( ) ) ;  for ( VertexClass vertex : vertexClassSet )  {  if  (  vertex . isInternal  ( ) )  {  continue ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   addImports  ( "#jgImplPackage#.VertexIterable" ) ; }  CodeSnippet  s =  new CodeSnippet  ( true ) ;   code . addNoIndent  ( s ) ;   s . setVariable  ( "vertexQualifiedName" ,  vertex . getQualifiedName  ( ) ) ;   s . setVariable  ( "vertexJavaClassName" ,  
<<<<<<<
 schemaRootPackageName + "."
=======
"#schemaPackage#."
>>>>>>>
 +  vertex . getQualifiedName  ( ) ) ;   s . setVariable  ( "vertexCamelName" ,  camelCase  (  vertex . getUniqueName  ( ) ) ) ;  if  (  currentCycle . isAbstract  ( ) )  {   s . add  ( "/**" ) ;   s . add  ( " * @return an Iterable for all vertices of this graph that are of type #vertexQualifiedName# or subtypes." ) ;   s . add  ( " */" ) ;   s . add  ( "public Iterable<#vertexJavaClassName#> get#vertexCamelName#Vertices();" ) ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   s . add  ( "public Iterable<#vertexJavaClassName#> get#vertexCamelName#Vertices() {" ) ;   s . add  ( "\treturn new VertexIterable<#vertexJavaClassName#>(this, #vertexJavaClassName#.class);" ) ;   s . add  ( "}" ) ; }   s . add  ( "" ) ; }  return code ; }    @ Override protected void addCheckValidityCode  (  CodeSnippet code )  { }   private CodeBlock createGetFirstMethods  (   GraphElementClass  <  ? ,  ? > gec )  {  CodeList  code =  new CodeList  ( ) ;  if  (  config . hasTypeSpecificMethodsSupport  ( ) )  {   code . addNoIndent  (  createGetFirstMethod  ( gec ) ) ; }  return code ; }   private CodeBlock createGetFirstMethod  (   GraphElementClass  <  ? ,  ? > gec )  {  CodeSnippet  code =  new CodeSnippet  ( true ) ;  if  (  currentCycle . isAbstract  ( ) )  {   code . add  ( "/**" , " * @return the first #ecSimpleName# #ecTypeInComment# in this graph" ) ;   code . add  ( " */" , "public #ecJavaClassName# getFirst#ecCamelName#();" ) ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   code . add  ( "public #ecJavaClassName# getFirst#ecCamelName#() {" , "\treturn (#ecJavaClassName#)getFirst#ecType#(#ecJavaClassName#.#ecTypeAecConstant#);" , "}" ) ; }  return code ; }   private CodeBlock createFactoryMethods  (   GraphElementClass  <  ? ,  ? > gec )  {  if  (  gec . isAbstract  ( ) )  {  return null ; }  CodeList  code =  new CodeList  ( ) ;   code . addNoIndent  (  createFactoryMethod  ( gec , false ) ) ;  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   code . addNoIndent  (  createFactoryMethod  ( gec , true ) ) ; }  return code ; }   private CodeBlock createFactoryMethod  (   GraphElementClass  <  ? ,  ? > gec ,  boolean withId )  {  CodeSnippet  code =  new CodeSnippet  ( true ) ;  if  (  currentCycle . isAbstract  ( ) )  {   code . add  ( "/**" , " * Creates a new #ecUniqueName# #ecTypeInComment# in this graph." , " *" ) ;  if  ( withId )  {   code . add  ( " * @param id the <code>id</code> of the #ecTypeInComment#" ) ; }  if  (  gec instanceof EdgeClass )  {   code . add  ( " * @param alpha the start vertex of the edge" , " * @param omega the target vertex of the edge" ) ; }   code . add  ( "*/" , "public #ecJavaClassName# create#ecCamelName#(#formalParams#);" ) ; }  if  (  currentCycle . isStdOrDbImplOrTransImpl  ( ) )  {   code . add  ( "public #ecJavaClassName# create#ecCamelName#(#formalParams#) {" , "\treturn graphFactory.<#ecJavaClassName#> create#ecType#(#ecJavaClassName#.#ecTypeAecConstant#, #newActualParams#, this#additionalParams#);" , "}" ) ;   code . setVariable  ( "additionalParams" , "" ) ; }  if  (  gec instanceof EdgeClass )  {  EdgeClass  ec =  ( EdgeClass ) gec ;  String  fromClass =    ec . getFrom  ( ) . getVertexClass  ( ) . getQualifiedName  ( ) ;  String  toClass =    ec . getTo  ( ) . getVertexClass  ( ) . getQualifiedName  ( ) ;  if  (  fromClass . equals  ( "Vertex" ) )  {   code . setVariable  ( "fromClass" , "#jgPackage#.Vertex" ) ; } else  {   code . setVariable  ( "fromClass" ,  "#schemaPackage#." + fromClass ) ; }  if  (  toClass . equals  ( "Vertex" ) )  {   code . setVariable  ( "toClass" , "#jgPackage#.Vertex" ) ; } else  {   code . setVariable  ( "toClass" ,  "#schemaPackage#." + toClass ) ; }   code . setVariable  ( "formalParams" ,   (  withId ? "int id, " : "" ) + "#fromClass# alpha, #toClass# omega" ) ;   code . setVariable  ( "addActualParams" , ", alpha, omega" ) ;   code . setVariable  ( "additionalParams" , ", alpha, omega" ) ; } else  {   code . setVariable  ( "formalParams" ,  (  withId ? "int id" : "" ) ) ;   code . setVariable  ( "addActualParams" , "" ) ; }   code . setVariable  ( "newActualParams" ,  (  withId ? "id" : "0" ) ) ;  return code ; }    @ Override protected CodeBlock createAttributedElementClassConstant  ( )  {  return  new CodeSnippet  ( true ,  "public static final #jgSchemaPackage#.#schemaElementClass# GC" + " = #schemaPackageName#.#schemaName#.instance().#schemaVariableName#;" ) ; }    @ Override protected CodeBlock createGetAttributedElementClassMethod  ( )  {  return  new CodeSnippet  ( true , "@Override" , "public final #jgSchemaPackage#.#schemaElementClass# getAttributedElementClass() {" , "\treturn #javaClassName#.GC;" , "}" ) ; } }