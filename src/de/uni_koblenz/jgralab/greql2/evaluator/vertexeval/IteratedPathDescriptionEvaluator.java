  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import    de . uni_koblenz . jgralab . EdgeDirection ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import       de . uni_koblenz . jgralab . greql2 . evaluator . fa . NFA ;  import      de . uni_koblenz . jgralab . greql2 . schema . IteratedPathDescription ;  import      de . uni_koblenz . jgralab . greql2 . schema . IterationType ;  import      de . uni_koblenz . jgralab . greql2 . schema . PathDescription ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class IteratedPathDescriptionEvaluator  extends  PathDescriptionEvaluator  < IteratedPathDescription >  {   public IteratedPathDescriptionEvaluator  (  IteratedPathDescription vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public NFA evaluate  (  InternalGreqlEvaluator evaluator )  {  PathDescription  p =   vertex . getFirstIsIteratedPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   PathDescriptionEvaluator  <  ? >  pathEval =  (  PathDescriptionEvaluator  <  ? > )  query . getVertexEvaluator  ( p ) ;  NFA  createdNFA =  NFA . createIteratedPathDescriptionNFA  (  pathEval . getNFA  ( evaluator ) ,   vertex . get_times  ( ) ==  IterationType . STAR ) ;  return createdNFA ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public NFA evaluate  ( )  {  PathDescription  p =  ( PathDescription )   vertex . getFirstIsIteratedPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;  PathDescriptionEvaluator  pathEval =  ( PathDescriptionEvaluator )  vertexEvalMarker . getMark  ( p ) ;  NFA  createdNFA =  NFA . createIteratedPathDescriptionNFA  (  pathEval . getNFA  ( ) ,   vertex . get_times  ( ) ==  IterationType . STAR ) ;  return createdNFA ; }
>>>>>>>
 }