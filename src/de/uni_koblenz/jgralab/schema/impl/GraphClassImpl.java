  package     de . uni_koblenz . jgralab . schema . impl ;   import   java . util . ArrayList ;  import   java . util . HashMap ;  import   java . util . List ;  import   java . util . Map ;  import   org . pcollections . PVector ;  import    de . uni_koblenz . jgralab . Graph ;  import     de . uni_koblenz . jgralab . schema . AggregationKind ;  import     de . uni_koblenz . jgralab . schema . Attribute ;  import     de . uni_koblenz . jgralab . schema . EdgeClass ;  import     de . uni_koblenz . jgralab . schema . GraphClass ;  import     de . uni_koblenz . jgralab . schema . GraphElementClass ;  import     de . uni_koblenz . jgralab . schema . VertexClass ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaException ;   public final class GraphClassImpl  extends  AttributedElementClassImpl  < GraphClass , Graph >  implements  GraphClass  {   Map  < String , VertexClass >  vertexClasses =  new  HashMap  < String , VertexClass >  ( ) ;   DirectedAcyclicGraph  < VertexClass >  vertexClassDag =  new  DirectedAcyclicGraph  < VertexClass >  ( true ) ;   Map  < String , EdgeClass >  edgeClasses =  new  HashMap  < String , EdgeClass >  ( ) ;   DirectedAcyclicGraph  < EdgeClass >  edgeClassDag =  new  DirectedAcyclicGraph  < EdgeClass >  ( true ) ;   private VertexClassImpl  defaultVertexClass ;   private EdgeClassImpl  defaultEdgeClass ;   private TemporaryVertexClassImpl  tempVertexClass ;   private TemporaryEdgeClassImpl  tempEdgeClass ;  GraphClassImpl  (  String gcName ,  SchemaImpl schema )  {  super  ( gcName ,  ( PackageImpl )  schema . getDefaultPackage  ( ) , schema ) ;   schema . setGraphClass  ( this ) ; }    @ Override public final VertexClass getDefaultVertexClass  ( )  {  return defaultVertexClass ; }   final void initializeDefaultVertexClass  ( )  {  VertexClassImpl  vc =  new VertexClassImpl  (  VertexClass . DEFAULTVERTEXCLASS_NAME ,  ( PackageImpl )  schema . getDefaultPackage  ( ) , this ) ;   vc . setAbstract  ( true ) ;   defaultVertexClass = vc ; }   final void initializeDefaultEdgeClass  ( )  {  assert   getDefaultVertexClass  ( ) != null : "Default VertexClass has not yet been created!" ;  assert   getDefaultEdgeClass  ( ) == null : "Default EdgeClass already created!" ;  EdgeClassImpl  ec =  new EdgeClassImpl  (  EdgeClass . DEFAULTEDGECLASS_NAME ,  ( PackageImpl )  schema . getDefaultPackage  ( ) , this , defaultVertexClass , 0 ,  Integer . MAX_VALUE , "" ,  AggregationKind . NONE , defaultVertexClass , 0 ,  Integer . MAX_VALUE , "" ,  AggregationKind . NONE ) ;   ec . setAbstract  ( true ) ;   defaultEdgeClass = ec ; }    @ Override public final VertexClass getTemporaryVertexClass  ( )  {  return tempVertexClass ; }   final void initializeTemporaryVertexClass  ( )  {  assert   getTemporaryVertexClass  ( ) == null : "TemporaryVertexClass already created!" ;   tempVertexClass =  new TemporaryVertexClassImpl  ( this ) ; }   final void initializeTemporaryEdgeClass  ( )  {  assert   getDefaultVertexClass  ( ) != null : "Default VertexClass has not yet been created!" ;  assert   getTemporaryEdgeClass  ( ) == null : "TemporaryEdgeClass already created!" ;    this . tempEdgeClass =  new TemporaryEdgeClassImpl  ( this ) ; }    @ Override public final EdgeClass getTemporaryEdgeClass  ( )  {  return tempEdgeClass ; }    @ Override public final EdgeClass getDefaultEdgeClass  ( )  {  return defaultEdgeClass ; }  void addEdgeClass  (  EdgeClass ec )  {  if  (  edgeClasses . containsKey  (  ec . getQualifiedName  ( ) ) )  {  throw  new SchemaException  (   "Duplicate edge class name '" +  ec . getQualifiedName  ( ) + "'" ) ; }  if  (  !   ec . getQualifiedName  ( ) . equals  (  EdgeClass . DEFAULTEDGECLASS_NAME ) )  {   edgeClasses . put  (  ec . getQualifiedName  ( ) , ec ) ; } }  void addVertexClass  (  VertexClass vc )  {  if  (  vertexClasses . containsKey  (  vc . getQualifiedName  ( ) ) )  {  throw  new SchemaException  (   "Duplicate vertex class name '" +  vc . getQualifiedName  ( ) + "'" ) ; }  if  (  !   vc . getQualifiedName  ( ) . equals  (  VertexClass . DEFAULTVERTEXCLASS_NAME ) )  {   vertexClasses . put  (  vc . getQualifiedName  ( ) , vc ) ; } }    @ Override public String getVariableName  ( )  {  return  "gc_" +   getQualifiedName  ( ) . replace  ( '.' , '_' ) ; }    @ Override public final EdgeClass createEdgeClass  (  String qualifiedName ,  VertexClass from ,   int fromMin ,   int fromMax ,  String fromRoleName ,  AggregationKind aggrFrom ,  VertexClass to ,   int toMin ,   int toMax ,  String toRoleName ,  AggregationKind aggrTo )  {   assertNotFinished  ( ) ;  if  (   from . isDefaultGraphElementClass  ( ) ||  to . isDefaultGraphElementClass  ( ) )  {  throw  new SchemaException  (  "EdgeClasses starting or ending at the default " + "VertexClass Vertex are forbidden." ) ; }  if  (   !  (  aggrFrom ==  AggregationKind . NONE ) &&  !  (  aggrTo ==  AggregationKind . NONE ) )  {  throw  new SchemaException  (  "At least one end of each class must be of AggregationKind NONE at EdgeClass " + qualifiedName ) ; }   String  [ ]  qn =  SchemaImpl . splitQualifiedName  ( qualifiedName ) ;  PackageImpl  parent =  schema . createPackageWithParents  (  qn [ 0 ] ) ;  EdgeClassImpl  ec =  new EdgeClassImpl  (  qn [ 1 ] , parent , this , from , fromMin , fromMax , fromRoleName , aggrFrom , to , toMin , toMax , toRoleName , aggrTo ) ;  if  (  defaultEdgeClass != null )  {   ec . addSuperClass  ( defaultEdgeClass ) ; }  return ec ; }    @ Override public final VertexClass createVertexClass  (  String qualifiedName )  {   assertNotFinished  ( ) ;   String  [ ]  qn =  SchemaImpl . splitQualifiedName  ( qualifiedName ) ;  PackageImpl  parent =   (  ( SchemaImpl )  getSchema  ( ) ) . createPackageWithParents  (  qn [ 0 ] ) ;  VertexClassImpl  vc =  new VertexClassImpl  (  qn [ 1 ] , parent , this ) ;  if  (  defaultVertexClass != null )  {   vc . addSuperClass  ( defaultVertexClass ) ; }  return vc ; }    @ Override public final  GraphElementClass  <  ? ,  ? > getGraphElementClass  (  String qn )  {   GraphElementClass  <  ? ,  ? >  gec =  vertexClasses . get  ( qn ) ;  if  (  gec != null )  {  return gec ; }  return  edgeClasses . get  ( qn ) ; }    @ Override public final  List  <  GraphElementClass  <  ? ,  ? > > getGraphElementClasses  ( )  {   List  <  GraphElementClass  <  ? ,  ? > >  l =  new  ArrayList  <  GraphElementClass  <  ? ,  ? > >  (  vertexClasses . values  ( ) ) ;   l . addAll  (  edgeClasses . values  ( ) ) ;  return l ; }    @ Override public final  List  < EdgeClass > getEdgeClasses  ( )  {   PVector  < EdgeClass >  vec =  edgeClassDag . getNodesInTopologicalOrder  ( ) ;  assert   vec . get  ( 0 ) == defaultEdgeClass ;  assert   vec . get  ( 1 ) == tempEdgeClass ;  return  vec . subList  ( 2 ,  vec . size  ( ) ) ; }    @ Override public final  List  < VertexClass > getVertexClasses  ( )  {   PVector  < VertexClass >  vec =  vertexClassDag . getNodesInTopologicalOrder  ( ) ;  assert   vec . get  ( 0 ) == defaultVertexClass ;  assert   vec . get  ( 1 ) == tempVertexClass ;  return  vec . subList  ( 2 ,  vec . size  ( ) ) ; }    @ Override public final VertexClass getVertexClass  (  String qn )  {  return  vertexClasses . get  ( qn ) ; }    @ Override public final EdgeClass getEdgeClass  (  String qn )  {  return  edgeClasses . get  ( qn ) ; }    @ Override public final  int getEdgeClassCount  ( )  {  return  edgeClasses . size  ( ) ; }    @ Override public final  int getVertexClassCount  ( )  {  return  vertexClasses . size  ( ) ; }    @ Override protected final void finish  ( )  {   assertNotFinished  ( ) ;   vertexClassDag . finish  ( ) ;   edgeClassDag . finish  ( ) ;  for ( VertexClass vc :  vertexClassDag . getNodesInTopologicalOrder  ( ) )  {    (  ( VertexClassImpl ) vc ) . finish  ( ) ; }  for ( EdgeClass ec :  edgeClassDag . getNodesInTopologicalOrder  ( ) )  {    (  ( EdgeClassImpl ) ec ) . finish  ( ) ; }   super . finish  ( ) ; }    @ Override public final boolean hasOwnAttributes  ( )  {  return  hasAttributes  ( ) ; }    @ Override public final Attribute getOwnAttribute  (  String name )  {  return  getAttribute  ( name ) ; }    @ Override public final  int getOwnAttributeCount  ( )  {  return  getAttributeCount  ( ) ; }    @ Override public final  List  < Attribute > getOwnAttributeList  ( )  {  return  getAttributeList  ( ) ; }    @ Override public void setQualifiedName  (  String newQName )  {  if  (  qualifiedName . equals  ( newQName ) )  {  return ; }  if  (  schema . knows  ( newQName ) )  {  throw  new SchemaException  (  newQName + " is already known to the schema." ) ; }  if  (  newQName . contains  ( "." ) )  {  throw  new SchemaException  (    "The GraphClass must be in the default package. " + "You tried to move it to '" + newQName + "'." ) ; }   unregister  ( ) ;   qualifiedName = newQName ;   simpleName = newQName ;   register  ( ) ; }    @ Override protected final void reopen  ( )  {  for ( VertexClass vc :  vertexClassDag . getNodesInTopologicalOrder  ( ) )  {    (  ( VertexClassImpl ) vc ) . reopen  ( ) ; }  for ( EdgeClass ec :  edgeClassDag . getNodesInTopologicalOrder  ( ) )  {    (  ( EdgeClassImpl ) ec ) . reopen  ( ) ; }   vertexClassDag . reopen  ( ) ;   edgeClassDag . reopen  ( ) ;   super . reopen  ( ) ; }    @ Override protected void deleteAttribute  (  AttributeImpl attr )  {   allAttributes =  allAttributes . minus  ( attr ) ; } }