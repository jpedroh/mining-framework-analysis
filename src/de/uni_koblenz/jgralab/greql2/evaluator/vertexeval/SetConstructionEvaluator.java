  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import    de . uni_koblenz . jgralab . JGraLab ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . SetConstruction ;   public class SetConstructionEvaluator  extends  ValueConstructionEvaluator  < SetConstruction >  {   public SetConstructionEvaluator  (  SetConstruction vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public Object evaluate  (  InternalGreqlEvaluator evaluator )  {  return  createValue  (  JGraLab . set  ( ) , evaluator ) ; } 
<<<<<<<
=======
   @ Override public Object evaluate  ( )  {  return  createValue  (  JGraLab . set  ( ) ) ; }
>>>>>>>
 }