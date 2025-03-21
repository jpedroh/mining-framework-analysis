  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import    de . uni_koblenz . jgralab . EdgeDirection ;  import    de . uni_koblenz . jgralab . Record ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . IsRecordElementOf ;  import      de . uni_koblenz . jgralab . greql2 . schema . RecordConstruction ;  import      de . uni_koblenz . jgralab . greql2 . schema . RecordElement ;  import     de . uni_koblenz . jgralab . impl . RecordImpl ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class RecordConstructionEvaluator  extends  VertexEvaluator  < RecordConstruction >  {   public RecordConstructionEvaluator  (  RecordConstruction vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public Record evaluate  (  InternalGreqlEvaluator evaluator )  {  RecordImpl  resultRecord =  RecordImpl . empty  ( ) ;  IsRecordElementOf  inc =  vertex . getFirstIsRecordElementOfIncidence  (  EdgeDirection . IN ) ;  while  (  inc != null )  {  RecordElement  currentElement =  inc . getAlpha  ( ) ;  RecordElementEvaluator  vertexEval =  ( RecordElementEvaluator )  query . getVertexEvaluator  ( currentElement ) ;   resultRecord =  resultRecord . plus  (  vertexEval . getId  ( ) ,  vertexEval . getResult  ( evaluator ) ) ;   inc =  inc . getNextIsRecordElementOfIncidence  (  EdgeDirection . IN ) ; }  return resultRecord ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public Record evaluate  ( )  {  RecordImpl  resultRecord =  RecordImpl . empty  ( ) ;  IsRecordElementOf  inc =  vertex . getFirstIsRecordElementOfIncidence  (  EdgeDirection . IN ) ;  while  (  inc != null )  {  RecordElement  currentElement =  inc . getAlpha  ( ) ;  RecordElementEvaluator  vertexEval =  ( RecordElementEvaluator )  vertexEvalMarker . getMark  ( currentElement ) ;   resultRecord =  resultRecord . plus  (  vertexEval . getId  ( ) ,  vertexEval . getResult  ( ) ) ;   inc =  inc . getNextIsRecordElementOfIncidence  (  EdgeDirection . IN ) ; }  return resultRecord ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public VertexCosts calculateSubtreeEvaluationCosts  (  GraphSize graphSize )  {  return   greqlEvaluator . getCostModel  ( ) . calculateCostsRecordConstruction  ( this , graphSize ) ; }
>>>>>>>
 }