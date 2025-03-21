  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import   org . pcollections . PCollection ;  import    de . uni_koblenz . jgralab . JGraLab ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . SetComprehension ;   public class SetComprehensionEvaluator  extends 
<<<<<<<
 ComprehensionEvaluator  < SetComprehension >
=======
ComprehensionEvaluator
>>>>>>>
  {   public SetComprehensionEvaluator  (  SetComprehension vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override protected  PCollection  < Object > getResultDatastructure  (  InternalGreqlEvaluator evaluator )  {  return  JGraLab . set  ( ) ; } 
<<<<<<<
=======
   @ Override public SetComprehension getVertex  ( )  {  return vertex ; }
>>>>>>>
    @ Override protected  PCollection  < Object > getResultDatastructure  ( )  {  return  JGraLab . set  ( ) ; } }