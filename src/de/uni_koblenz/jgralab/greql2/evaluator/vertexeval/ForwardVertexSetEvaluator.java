  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import    de . uni_koblenz . jgralab . EdgeDirection ;  import    de . uni_koblenz . jgralab . Vertex ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import       de . uni_koblenz . jgralab . greql2 . evaluator . fa . DFA ;  import       de . uni_koblenz . jgralab . greql2 . funlib . graph . ReachableVertices ;  import      de . uni_koblenz . jgralab . greql2 . schema . Expression ;  import      de . uni_koblenz . jgralab . greql2 . schema . ForwardVertexSet ;  import      de . uni_koblenz . jgralab . greql2 . schema . PathDescription ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . GreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class ForwardVertexSetEvaluator  extends  PathSearchEvaluator  < ForwardVertexSet >  {   public ForwardVertexSetEvaluator  (  ForwardVertexSet vertex ,  Query query )  {  super  ( vertex , query ) ; }   private boolean  initialized = false ;   private 
<<<<<<<
 VertexEvaluator  <  ? extends Expression >
=======
VertexEvaluator
>>>>>>>
  startEval = null ;   private final void initialize  (  InternalGreqlEvaluator evaluator )  {  PathDescription  p =  ( PathDescription )   vertex . getFirstIsPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   PathDescriptionEvaluator  <  ? >  pathDescEval =  (  PathDescriptionEvaluator  <  ? > )  query . getVertexEvaluator  ( p ) ;  Expression  startExpression =   vertex . getFirstIsStartExprOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   startEval =  query . getVertexEvaluator  ( startExpression ) ;   searchAutomaton =  new DFA  (  pathDescEval . getNFA  ( evaluator ) ) ;   initialized = true ; }    @ Override public Object evaluate  (  InternalGreqlEvaluator evaluator )  {  if  (  ! initialized )  {   initialize  ( evaluator ) ; }  Vertex  startVertex = null ;   startVertex =  ( Vertex )  startEval . getResult  ( evaluator ) ;  return  ReachableVertices . search  ( startVertex , searchAutomaton ) ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
   private final void initialize  ( )  {  PathDescription  p =  ( PathDescription )   vertex . getFirstIsPathOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;  PathDescriptionEvaluator  pathDescEval =  ( PathDescriptionEvaluator )  vertexEvalMarker . getMark  ( p ) ;  Expression  startExpression =  ( Expression )   vertex . getFirstIsStartExprOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( ) ;   startEval =  vertexEvalMarker . getMark  ( startExpression ) ;   searchAutomaton =  new DFA  (  pathDescEval . getNFA  ( ) ) ;   initialized = true ; } 
<<<<<<<
=======
   @ Override public Object evaluate  ( )  {  if  (  ! initialized )  {   initialize  ( ) ; }  Vertex  startVertex = null ;   startVertex =  ( Vertex )  startEval . getResult  ( ) ;  return  ReachableVertices . search  ( startVertex , searchAutomaton ) ; }
>>>>>>>
 }