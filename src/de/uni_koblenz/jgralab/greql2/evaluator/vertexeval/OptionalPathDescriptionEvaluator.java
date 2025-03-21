  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import    de . uni_koblenz . jgralab . EdgeDirection ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import       de . uni_koblenz . jgralab . greql2 . evaluator . fa . NFA ;  import      de . uni_koblenz . jgralab . greql2 . schema . OptionalPathDescription ;  import      de . uni_koblenz . jgralab . greql2 . schema . PathDescription ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class OptionalPathDescriptionEvaluator  extends  PathDescriptionEvaluator  < OptionalPathDescription >  {   public OptionalPathDescriptionEvaluator  (  OptionalPathDescription vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public NFA evaluate  (  InternalGreqlEvaluator evaluator )  {  PathDescription  p =   vertex . getFirstIsOptionalPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   PathDescriptionEvaluator  <  ? >  pathEval =  (  PathDescriptionEvaluator  <  ? > )  query . getVertexEvaluator  ( p ) ;  return  NFA . createOptionalPathDescriptionNFA  (  pathEval . getNFA  ( evaluator ) ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public NFA evaluate  ( )  {  PathDescription  p =  ( PathDescription )   vertex . getFirstIsOptionalPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;  PathDescriptionEvaluator  pathEval =  ( PathDescriptionEvaluator )  vertexEvalMarker . getMark  ( p ) ;  return  NFA . createOptionalPathDescriptionNFA  (  pathEval . getNFA  ( ) ) ; }
>>>>>>>
 }