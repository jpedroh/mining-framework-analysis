  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import   org . pcollections . PVector ;  import    de . uni_koblenz . jgralab . EdgeDirection ;  import    de . uni_koblenz . jgralab . JGraLab ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . Expression ;  import      de . uni_koblenz . jgralab . greql2 . schema . ListRangeConstruction ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class ListRangeConstructionEvaluator  extends  VertexEvaluator  < ListRangeConstruction >  {   public ListRangeConstructionEvaluator  (  ListRangeConstruction vertex ,  Query query )  {  super  ( vertex , query ) ; }   private 
<<<<<<<
 VertexEvaluator  <  ? extends Expression >
=======
VertexEvaluator
>>>>>>>
  firstElementEvaluator = null ;   private 
<<<<<<<
 VertexEvaluator  <  ? extends Expression >
=======
VertexEvaluator
>>>>>>>
  lastElementEvaluator = null ;   private void getEvals  ( )  {  Expression  firstElementExpression = 
<<<<<<<
  vertex . getFirstIsFirstValueOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( )
=======
 ( Expression )   vertex . getFirstIsFirstValueOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( )
>>>>>>>
 ;  Expression  lastElementExpression = 
<<<<<<<
  vertex . getFirstIsLastValueOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( )
=======
 ( Expression )   vertex . getFirstIsLastValueOfIncidence  (  EdgeDirection . IN ) . getAlpha  ( )
>>>>>>>
 ;   firstElementEvaluator =  
<<<<<<<
query
=======
vertexEvalMarker
>>>>>>>
 . 
<<<<<<<
getVertexEvaluator
=======
getMark
>>>>>>>
  ( firstElementExpression ) ;   lastElementEvaluator =  
<<<<<<<
query
=======
vertexEvalMarker
>>>>>>>
 . 
<<<<<<<
getVertexEvaluator
=======
getMark
>>>>>>>
  ( lastElementExpression ) ; }    @ Override public  PVector  < Integer > evaluate  (  InternalGreqlEvaluator evaluator )  {   PVector  < Integer >  resultList =  JGraLab . vector  ( ) ;  if  (  firstElementEvaluator == null )  {   getEvals  ( ) ; }  Object  firstElement =  firstElementEvaluator . getResult  ( evaluator ) ;  Object  lastElement =  lastElementEvaluator . getResult  ( evaluator ) ;  if  (   firstElement instanceof Integer &&  lastElement instanceof Integer )  {  if  (   ( Integer ) firstElement <  ( Integer ) lastElement )  {  for (   int  i =  ( Integer ) firstElement ;  i <   ( Integer ) lastElement + 1 ;  i ++ )  {   resultList =  resultList . plus  ( i ) ; } } else  {  for (   int  i =  ( Integer ) lastElement ;  i <   ( Integer ) firstElement + 1 ;  i ++ )  {   resultList =  resultList . plus  ( i ) ; } } }  return resultList ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public  PVector  < Integer > evaluate  ( )  {   PVector  < Integer >  resultList =  JGraLab . vector  ( ) ;  if  (  firstElementEvaluator == null )  {   getEvals  ( ) ; }  Object  firstElement =  firstElementEvaluator . getResult  ( ) ;  Object  lastElement =  lastElementEvaluator . getResult  ( ) ;  if  (   firstElement instanceof Integer &&  lastElement instanceof Integer )  {  if  (   ( Integer ) firstElement <  ( Integer ) lastElement )  {  for (   int  i =  ( Integer ) firstElement ;  i <   ( Integer ) lastElement + 1 ;  i ++ )  {   resultList =  resultList . plus  ( i ) ; } } else  {  for (   int  i =  ( Integer ) lastElement ;  i <   ( Integer ) firstElement + 1 ;  i ++ )  {   resultList =  resultList . plus  ( i ) ; } } }  return resultList ; }
>>>>>>>
 }