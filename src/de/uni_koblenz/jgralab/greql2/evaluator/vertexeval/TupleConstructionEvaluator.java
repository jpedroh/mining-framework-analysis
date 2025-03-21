  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import   org . pcollections . PCollection ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . TupleConstruction ;  import      de . uni_koblenz . jgralab . greql2 . types . Tuple ;   public class TupleConstructionEvaluator  extends  ValueConstructionEvaluator  < TupleConstruction >  {   public TupleConstructionEvaluator  (  TupleConstruction vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public  PCollection  < Object > evaluate  (  InternalGreqlEvaluator evaluator )  {  return  createValue  (  Tuple . empty  ( ) , evaluator ) ; } 
<<<<<<<
=======
   @ Override public  PCollection  < Object > evaluate  ( )  {  return  createValue  (  Tuple . empty  ( ) ) ; }
>>>>>>>
 }