  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import    de . uni_koblenz . jgralab . EdgeDirection ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import       de . uni_koblenz . jgralab . greql2 . evaluator . fa . NFA ;  import      de . uni_koblenz . jgralab . greql2 . schema . PathDescription ;  import      de . uni_koblenz . jgralab . greql2 . schema . TransposedPathDescription ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class TransposedPathDescriptionEvaluator  extends  PathDescriptionEvaluator  < TransposedPathDescription >  {   public TransposedPathDescriptionEvaluator  (  TransposedPathDescription vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public NFA evaluate  (  InternalGreqlEvaluator evaluator )  {  PathDescription  p =   vertex . getFirstIsTransposedPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   PathDescriptionEvaluator  <  ? >  pathEval =  (  PathDescriptionEvaluator  <  ? > )  query . getVertexEvaluator  ( p ) ;  return  NFA . createTransposedPathDescriptionNFA  (  pathEval . getNFA  ( evaluator ) ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public NFA evaluate  ( )  {  PathDescription  p =  ( PathDescription )   vertex . getFirstIsTransposedPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;  PathDescriptionEvaluator  pathEval =  ( PathDescriptionEvaluator )  vertexEvalMarker . getMark  ( p ) ;  return  NFA . createTransposedPathDescriptionNFA  (  pathEval . getNFA  ( ) ) ; }
>>>>>>>
 }