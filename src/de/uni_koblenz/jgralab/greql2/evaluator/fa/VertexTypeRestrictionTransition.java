  package      de . uni_koblenz . jgralab . greql2 . evaluator . fa ;   import    de . uni_koblenz . jgralab . Edge ;  import    de . uni_koblenz . jgralab . Vertex ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . types . TypeCollection ;  import     de . uni_koblenz . jgralab . schema . AttributedElementClass ;  import     de . uni_koblenz . jgralab . schema . VertexClass ;   public class VertexTypeRestrictionTransition  extends Transition  {   private final TypeCollection  typeCollection ;    @ Override public boolean equalSymbol  (  Transition t )  {  if  (  !  (  t instanceof VertexTypeRestrictionTransition ) )  {  return false ; }  VertexTypeRestrictionTransition  vt =  ( VertexTypeRestrictionTransition ) t ;  if  (  !  typeCollection . equals  (  vt . typeCollection ) )  {  return false ; }  return true ; }   public VertexTypeRestrictionTransition  (  State start ,  State end ,  TypeCollection typeCollection )  {  super  ( start , end ) ;    this . typeCollection = typeCollection ; }   protected VertexTypeRestrictionTransition  (  VertexTypeRestrictionTransition t ,  boolean addToStates )  {  super  ( t , addToStates ) ;   typeCollection =  new TypeCollection  (  t . typeCollection ) ; }    @ Override public Transition copy  (  boolean addToStates )  {  return  new VertexTypeRestrictionTransition  ( this , addToStates ) ; }    @ Override public boolean isEpsilon  ( )  {  return false ; }    @ Override public String edgeString  ( )  {  String  desc = "VertexRestrictinTransition" ;  return desc ; }    @ Override public boolean accepts  (  Vertex v ,  Edge e ,  InternalGreqlEvaluator evaluator )  {  VertexClass  vertexClass =  v . getAttributedElementClass  ( ) ;  if  (  !  typeCollection . acceptsType  ( vertexClass ) )  {  return false ; }  return true ; }    @ Override public Vertex getNextVertex  (  Vertex v ,  Edge e )  {  return v ; }    @ Override public String prettyPrint  ( )  {  StringBuilder  b =  new StringBuilder  ( ) ;  String  delim = "" ;  for (  AttributedElementClass  <  ? ,  ? > c :  typeCollection . getAllowedTypes  ( ) )  {   b . append  ( delim ) ;   b . append  (  c . getSimpleName  ( ) ) ;   delim = "," ; }  return   "&{" + b + "}" ; }    @ Override public boolean consumesEdge  ( )  {  return false ; }   public TypeCollection getAcceptedVertexTypes  ( )  {  return typeCollection ; } }