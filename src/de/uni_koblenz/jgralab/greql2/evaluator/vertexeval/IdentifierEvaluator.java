  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . Identifier ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . GreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class IdentifierEvaluator  extends  VertexEvaluator  < Identifier >  {   public IdentifierEvaluator  (  Identifier vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public String evaluate  (  InternalGreqlEvaluator evaluator )  {  return  vertex . get_name  ( ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public String evaluate  ( )  {  return  vertex . get_name  ( ) ; }
>>>>>>>
 }