  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import    de . uni_koblenz . jgralab . EdgeDirection ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . Expression ;  import      de . uni_koblenz . jgralab . greql2 . schema . RecordElement ;  import      de . uni_koblenz . jgralab . greql2 . schema . RecordId ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class RecordElementEvaluator  extends  VertexEvaluator  < RecordElement >  {   private String  id = null ;   private  VertexEvaluator  <  ? extends Expression >  expEval = null ;   public String getId  ( )  {  if  (  id == null )  {  RecordId  idVertex = 
<<<<<<<
  vertex . getFirstIsRecordIdOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( )
=======
 ( RecordId )   vertex . getFirstIsRecordIdOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( )
>>>>>>>
 ;   id =  idVertex . get_name  ( ) ; }  return id ; }   public RecordElementEvaluator  (  RecordElement vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public Object evaluate  (  InternalGreqlEvaluator evaluator )  {  if  (  expEval == null )  {  Expression  recordElementExp =   vertex . getFirstIsRecordExprOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   expEval =  query . getVertexEvaluator  ( recordElementExp ) ; }  return  expEval . getResult  ( evaluator ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public Object evaluate  ( )  {  if  (  expEval == null )  {  Expression  recordElementExp =  ( Expression )   vertex . getFirstIsRecordExprOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   expEval =  vertexEvalMarker . getMark  ( recordElementExp ) ; }  return  expEval . getResult  ( ) ; }
>>>>>>>
 }