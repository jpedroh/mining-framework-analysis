  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . BoolLiteral ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class BoolLiteralEvaluator  extends  VertexEvaluator  < BoolLiteral >  {   public BoolLiteralEvaluator  (  BoolLiteral vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public Boolean evaluate  (  InternalGreqlEvaluator evaluator )  {  return  vertex . is_boolValue  ( ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public Boolean evaluate  ( )  {  return  vertex . is_boolValue  ( ) ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public  double calculateEstimatedSelectivity  (  GraphSize graphSize )  {  if  (  vertex . is_boolValue  ( ) )  {  return 1 ; }  return 0 ; }
>>>>>>>
 }