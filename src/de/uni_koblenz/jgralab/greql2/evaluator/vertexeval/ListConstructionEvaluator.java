  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import   org . pcollections . PCollection ;  import    de . uni_koblenz . jgralab . JGraLab ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . ListConstruction ;   public class ListConstructionEvaluator  extends  ValueConstructionEvaluator  < ListConstruction >  {   public ListConstructionEvaluator  (  ListConstruction vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public  PCollection  < Object > evaluate  (  InternalGreqlEvaluator evaluator )  {  return  createValue  (  JGraLab . vector  ( ) , evaluator ) ; } 
<<<<<<<
=======
   @ Override public  PCollection  < Object > evaluate  ( )  {  return  createValue  (  JGraLab . vector  ( ) ) ; }
>>>>>>>
 }