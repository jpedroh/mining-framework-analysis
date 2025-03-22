  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import   java . util . ArrayList ;  import   java . util . List ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . GreqlEvaluator ;  import       de . uni_koblenz . jgralab . greql2 . evaluator . costmodel . GraphSize ;  import       de . uni_koblenz . jgralab . greql2 . evaluator . costmodel . VertexCosts ;  import      de . uni_koblenz . jgralab . greql2 . exception . UnknownTypeException ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;  import      de . uni_koblenz . jgralab . greql2 . schema . TypeId ;  import      de . uni_koblenz . jgralab . greql2 . types . TypeCollection ;  import     de . uni_koblenz . jgralab . schema . Schema ;   public class TypeIdEvaluator  extends VertexEvaluator  {    @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }   private TypeId  vertex ;   public TypeIdEvaluator  (  TypeId vertex ,  GreqlEvaluator eval )  {  super  ( eval ) ;    this . vertex = vertex ; }   protected  List  <  
<<<<<<<
AttributedElementClass
=======
GraphElementClass
>>>>>>>
  <  ? ,  ? > > createTypeList  (  Schema schema )  {   ArrayList  <  
<<<<<<<
AttributedElementClass
=======
GraphElementClass
>>>>>>>
  <  ? ,  ? > >  returnTypes =  new  ArrayList  <  
<<<<<<<
AttributedElementClass
=======
GraphElementClass
>>>>>>>
  <  ? ,  ? > >  ( ) ;   
<<<<<<<
AttributedElementClass
=======
GraphElementClass
>>>>>>>
  <  ? ,  ? >  elemClass =  
<<<<<<<
schema
=======
 schema . getGraphClass  ( )
>>>>>>>
 . 
<<<<<<<
getAttributedElementClass
=======
getGraphElementClass
>>>>>>>
  (  vertex . get_name  ( ) ) ;  if  (  elemClass == null )  {   elemClass =  greqlEvaluator . getKnownType  (  vertex . get_name  ( ) ) ;  if  (  elemClass == null )  {  throw  new UnknownTypeException  (  vertex . get_name  ( ) ,  createPossibleSourcePositions  ( ) ) ; } else  {   vertex . set_name  (  elemClass . getQualifiedName  ( ) ) ; } }   returnTypes . add  ( elemClass ) ;  if  (  !  vertex . is_type  ( ) )  {   returnTypes . addAll  (  elemClass . getAllSubClasses  ( ) ) ; }  return returnTypes ; }    @ Override public Object evaluate  ( )  {   List  <  
<<<<<<<
AttributedElementClass
=======
GraphElementClass
>>>>>>>
  <  ? ,  ? > >  typeList =  createTypeList  (   greqlEvaluator . getDatagraph  ( ) . getSchema  ( ) ) ;  return  new TypeCollection  ( typeList ,  vertex . is_excluded  ( ) ) ; }    @ Override public VertexCosts calculateSubtreeEvaluationCosts  (  GraphSize graphSize )  {  return   greqlEvaluator . getCostModel  ( ) . calculateCostsTypeId  ( this , graphSize ) ; }    @ Override public  double calculateEstimatedSelectivity  (  GraphSize graphSize )  {  return   greqlEvaluator . getCostModel  ( ) . calculateSelectivityTypeId  ( this , graphSize ) ; }    @ Override public String getLoggingName  ( )  {  StringBuilder  name =  new StringBuilder  ( ) ;   name . append  (   vertex . getAttributedElementClass  ( ) . getQualifiedName  ( ) ) ;  if  (  vertex . is_type  ( ) )  {   name . append  ( "-type" ) ; }  if  (  vertex . is_excluded  ( ) )  {   name . append  ( "-excluded" ) ; }  return  name . toString  ( ) ; } }