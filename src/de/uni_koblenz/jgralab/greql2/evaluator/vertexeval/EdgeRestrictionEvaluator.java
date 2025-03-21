  package      de . uni_koblenz . jgralab . greql2 . evaluator . vertexeval ;   import   java . util . HashSet ;  import   java . util . Set ;  import    de . uni_koblenz . jgralab . EdgeDirection ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . InternalGreqlEvaluator ;  import      de . uni_koblenz . jgralab . greql2 . evaluator . Query ;  import      de . uni_koblenz . jgralab . greql2 . schema . EdgeRestriction ;  import      de . uni_koblenz . jgralab . greql2 . schema . Expression ;  import      de . uni_koblenz . jgralab . greql2 . schema . IsBooleanPredicateOfEdgeRestriction ;  import      de . uni_koblenz . jgralab . greql2 . schema . IsRoleIdOf ;  import      de . uni_koblenz . jgralab . greql2 . schema . IsTypeIdOf ;  import      de . uni_koblenz . jgralab . greql2 . schema . RoleId ;  import      de . uni_koblenz . jgralab . greql2 . types . TypeCollection ;  import      de . uni_koblenz . jgralab . greql2 . schema . Greql2Vertex ;   public class EdgeRestrictionEvaluator  extends  VertexEvaluator  < EdgeRestriction >  {   private 
<<<<<<<
 VertexEvaluator  <  ? extends Expression >
=======
VertexEvaluator
>>>>>>>
  predicateEvaluator = null ;   public 
<<<<<<<
 VertexEvaluator  <  ? extends Expression >
=======
VertexEvaluator
>>>>>>>
 getPredicateEvaluator  ( )  {  return predicateEvaluator ; }   private TypeCollection  typeCollection = null ;   public TypeCollection getTypeCollection  (  InternalGreqlEvaluator evaluator )  {  if  (  typeCollection == null )  {   evaluate  ( evaluator ) ; }  return typeCollection ; }   private  Set  < String >  validRoles ;   public  Set  < String > getEdgeRoles  ( )  {  return validRoles ; }   public EdgeRestrictionEvaluator  (  EdgeRestriction vertex ,  Query query )  {  super  ( vertex , query ) ; }    @ Override public Object evaluate  (  InternalGreqlEvaluator evaluator )  {  if  (  typeCollection == null )  {   typeCollection =  new TypeCollection  ( ) ;  IsTypeIdOf  typeInc =  vertex . getFirstIsTypeIdOfIncidence  (  EdgeDirection . IN ) ;  while  (  typeInc != null )  {  TypeIdEvaluator  typeEval =  ( TypeIdEvaluator )  query . getVertexEvaluator  (  typeInc . getAlpha  ( ) ) ;   typeCollection . addTypes  (  ( TypeCollection )  typeEval . getResult  ( evaluator ) ) ;   typeInc =  typeInc . getNextIsTypeIdOfIncidence  (  EdgeDirection . IN ) ; } }  if  (   vertex . getFirstIsRoleIdOfIncidence  ( ) != null )  {   validRoles =  new  HashSet  < String >  ( ) ;  for ( IsRoleIdOf e :  vertex . getIsRoleIdOfIncidences  ( ) )  {  RoleId  role =  e . getAlpha  ( ) ;   validRoles . add  (  role . get_name  ( ) ) ; } }  IsBooleanPredicateOfEdgeRestriction  predInc =  vertex . getFirstIsBooleanPredicateOfEdgeRestrictionIncidence  (  EdgeDirection . IN ) ;  if  (  predInc != null )  {   predicateEvaluator =  query . getVertexEvaluator  (  predInc . getAlpha  ( ) ) ; }  return null ; } 
<<<<<<<
=======
   @ Override public Greql2Vertex getVertex  ( )  {  return vertex ; }
>>>>>>>
 
<<<<<<<
=======
  public TypeCollection getTypeCollection  ( )  {  if  (  typeCollection == null )  {   evaluate  ( ) ; }  return typeCollection ; }
>>>>>>>
 
<<<<<<<
=======
   @ Override public Object evaluate  ( )  {  if  (  typeCollection == null )  {   typeCollection =  new TypeCollection  ( ) ;  IsTypeIdOf  typeInc =  vertex . getFirstIsTypeIdOfIncidence  (  EdgeDirection . IN ) ;  while  (  typeInc != null )  {  TypeIdEvaluator  typeEval =  ( TypeIdEvaluator )  vertexEvalMarker . getMark  (  typeInc . getAlpha  ( ) ) ;   typeCollection . addTypes  (  ( TypeCollection )  typeEval . getResult  ( ) ) ;   typeInc =  typeInc . getNextIsTypeIdOfIncidence  (  EdgeDirection . IN ) ; } }  if  (   vertex . getFirstIsRoleIdOfIncidence  ( ) != null )  {   validRoles =  new  HashSet  < String >  ( ) ;  for ( IsRoleIdOf e :  vertex . getIsRoleIdOfIncidences  ( ) )  {  RoleId  role =  ( RoleId )  e . getAlpha  ( ) ;   validRoles . add  (  role . get_name  ( ) ) ; } }  IsBooleanPredicateOfEdgeRestriction  predInc =  vertex . getFirstIsBooleanPredicateOfEdgeRestrictionIncidence  (  EdgeDirection . IN ) ;  if  (  predInc != null )  {   predicateEvaluator =  vertexEvalMarker . getMark  (  predInc . getAlpha  ( ) ) ; }  return null ; }
>>>>>>>
 }