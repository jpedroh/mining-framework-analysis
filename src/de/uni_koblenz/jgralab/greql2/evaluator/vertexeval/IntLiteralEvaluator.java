  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . IntLiteral ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . GreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class IntLiteralEvaluator  extends  VertexEvaluator  < IntLiteral >  {   public IntLiteralEvaluator  (  IntLiteral vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public Integer evaluate  (  InternalGreqlEvaluator evaluator )  {  return  vertex . get_intValue  ( ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public Integer evaluate  ( )  {  return  vertex . get_intValue  ( ) ; }
>>>>>>>
 }