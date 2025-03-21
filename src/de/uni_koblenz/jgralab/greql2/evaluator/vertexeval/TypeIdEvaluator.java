  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import   java . util . ArrayList ;  import   java . util . List ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . exception . UnknownTypeException ;  import      de . uni_koblenz . jgralab . greql2 . schema . TypeId ;  import      de . uni_koblenz . jgralab . greql2 . types . TypeCollection ;  import     de . uni_koblenz . jgralab . schema . AttributedElementClass ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class TypeIdEvaluator  extends  VertexEvaluator  < TypeId >  {   public TypeIdEvaluator  (  TypeId vertex ,  Query query )  {  super  ( vertex , query ) ; }   protected  List  < AttributedElementClass > createTypeList  (  InternalGreqlEvaluator evaluator )  {   ArrayList  < AttributedElementClass >  returnTypes =  new  ArrayList  < AttributedElementClass >  ( ) ;  AttributedElementClass  elemClass =  evaluator . getAttributedElementClass  (  vertex . get_name  ( ) ) ;  if  (  elemClass == null )  {   elemClass =  evaluator . getKnownType  (  vertex . get_name  ( ) ) ;  if  (  elemClass == null )  {  throw  new UnknownTypeException  (  vertex . get_name  ( ) ,  createPossibleSourcePositions  ( ) ) ; } else  {   vertex . set_name  (  elemClass . getQualifiedName  ( ) ) ; } }   returnTypes . add  ( elemClass ) ;  if  (  !  vertex . is_type  ( ) )  {   returnTypes . addAll  (  elemClass . getAllSubClasses  ( ) ) ; }  return returnTypes ; }    @ Override public TypeCollection evaluate  (  InternalGreqlEvaluator evaluator )  {   List  < AttributedElementClass >  typeList =  createTypeList  ( evaluator ) ;  return  new TypeCollection  ( typeList ,  vertex . is_excluded  ( ) ) ; }    @ Override public String getLoggingName  ( )  {  StringBuilder  name =  new StringBuilder  ( ) ;   name . append  (   vertex . getAttributedElementClass  ( ) . getQualifiedName  ( ) ) ;  if  (  vertex . is_type  ( ) )  {   name . append  ( "-type" ) ; }  if  (  vertex . is_excluded  ( ) )  {   name . append  ( "-excluded" ) ; }  return  name . toString  ( ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
  protected  List  <  AttributedElementClass  <  ? ,  ? > > createTypeList  (  Schema schema )  {   ArrayList  <  AttributedElementClass  <  ? ,  ? > >  returnTypes =  new  ArrayList  <  AttributedElementClass  <  ? ,  ? > >  ( ) ;   AttributedElementClass  <  ? ,  ? >  elemClass =  schema . getAttributedElementClass  (  vertex . get_name  ( ) ) ;  if  (  elemClass == null )  {   elemClass =  greqlEvaluator . getKnownType  (  vertex . get_name  ( ) ) ;  if  (  elemClass == null )  {  throw  new UnknownTypeException  (  vertex . get_name  ( ) ,  createPossibleSourcePositions  ( ) ) ; } else  {   vertex . set_name  (  elemClass . getQualifiedName  ( ) ) ; } }   returnTypes . add  ( elemClass ) ;  if  (  !  vertex . is_type  ( ) )  {   returnTypes . addAll  (  elemClass . getAllSubClasses  ( ) ) ; }  return returnTypes ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public Object evaluate  ( )  {   List  <  AttributedElementClass  <  ? ,  ? > >  typeList =  createTypeList  (   greqlEvaluator . getDatagraph  ( ) . getSchema  ( ) ) ;  return  new TypeCollection  ( typeList ,  vertex . is_excluded  ( ) ) ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public VertexCosts calculateSubtreeEvaluationCosts  (  GraphSize graphSize )  {  return   greqlEvaluator . getCostModel  ( ) . calculateCostsTypeId  ( this , graphSize ) ; }
>>>>>>>
 }