  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . ElementSetExpression ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public abstract class ElementSetExpressionEvaluator  <  V  extends ElementSetExpression >  extends  AbstractGraphElementCollectionEvaluator  < V >  {   public ElementSetExpressionEvaluator  (  V vertex ,  Query query )  {  super  ( vertex , query ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 }