  package     de . uni_koblenz . jgralab . schema . impl ;   import   java . util . Collections ;  import   java . util . HashMap ;  import   java . util . HashSet ;  import   java . util . Map ;  import   java . util . Set ;  import    de . uni_koblenz . jgralab . EdgeDirection ;  import    de . uni_koblenz . jgralab . Vertex ;  import     de . uni_koblenz . jgralab . schema . EdgeClass ;  import     de . uni_koblenz . jgralab . schema . IncidenceClass ;  import     de . uni_koblenz . jgralab . schema . Package ;  import     de . uni_koblenz . jgralab . schema . Schema ;  import     de . uni_koblenz . jgralab . schema . VertexClass ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaException ;  import     de . uni_koblenz . jgralab . schema . IncidenceDirection ;   public final class VertexClassImpl  extends  GraphElementClassImpl  < VertexClass , Vertex >  implements  VertexClass  {   private  Set  < IncidenceClass >  inIncidenceClasses =  new  HashSet  < IncidenceClass >  ( ) ;   private  Set  < IncidenceClass >  allInIncidenceClasses ;   private  Set  < IncidenceClass >  outIncidenceClasses =  new  HashSet  < IncidenceClass >  ( ) ;   private  Set  < IncidenceClass >  allOutIncidenceClasses ;   private  Set  < IncidenceClass >  validFromFarIncidenceClasses ;   private  Set  < EdgeClass >  validFromEdgeClasses ;   private  Set  < EdgeClass >  validToEdgeClasses ;   private  Set  < IncidenceClass >  validToFarIncidenceClasses ;   private  Map  < String , DirectedSchemaEdgeClass >  farRoleNameToEdgeClass ;   static VertexClass createDefaultVertexClass  (  Schema schema )  {  assert   schema . getDefaultGraphClass  ( ) != null : "DefaultGraphClass has not yet been created!" ;  assert   schema . getDefaultVertexClass  ( ) == null : "DefaultVertexClass already created!" ;  VertexClass  vc =   schema . getDefaultGraphClass  ( ) . createVertexClass  ( DEFAULTVERTEXCLASS_NAME ) ;   vc . setAbstract  ( true ) ;    (  ( VertexClassImpl ) vc ) . setInternal  ( true ) ;  return vc ; }   protected VertexClassImpl  (  String simpleName ,  Package pkg ,  GraphClass aGraphClass )  {  super  ( simpleName , pkg , aGraphClass ) ;   this . register  ( ) ; }    @ Override protected void register  ( )  {    (  ( PackageImpl )  this . parentPackage ) . addVertexClass  ( this ) ;    (  ( GraphClassImpl )  this . graphClass ) . addVertexClass  ( this ) ; }    @ Override public String getVariableName  ( )  {  return  "vc_" +   this . getQualifiedName  ( ) . replace  ( '.' , '_' ) ; }  void addInIncidenceClass  (  IncidenceClass incClass )  {  if  (   incClass . getVertexClass  ( ) != this )  {   this . throwSchemaException  ( incClass ) ; }   this . checkDuplicateRolenames  ( incClass ) ;   
<<<<<<<
 this . inIncidenceClasses
=======
inIncidenceClasses
>>>>>>>
 . add  ( incClass ) ; }  void addOutIncidenceClass  (  IncidenceClass incClass )  {  if  (   incClass . getVertexClass  ( ) != this )  {   this . throwSchemaException  ( incClass ) ; }   this . checkDuplicateRolenames  ( incClass ) ;   
<<<<<<<
 this . outIncidenceClasses
=======
outIncidenceClasses
>>>>>>>
 . add  ( incClass ) ; }   private void checkDuplicateRolenames  (  IncidenceClass incClass )  {  String  rolename =   incClass . getOpposite  ( ) . getRolename  ( ) ;  if  (  rolename . isEmpty  ( ) )  {  return ; }   this . checkDuplicatedRolenameForACyclicIncidence  ( incClass ) ;   this . checkDuplicatedRolenameForAllIncidences  ( incClass ,  this . getAllInIncidenceClasses  ( ) ) ;   this . checkDuplicatedRolenameForAllIncidences  ( incClass ,  this . getAllOutIncidenceClasses  ( ) ) ; }   private void checkDuplicatedRolenameForACyclicIncidence  (  IncidenceClass incClass )  {  String  rolename =   incClass . getOpposite  ( ) . getRolename  ( ) ;  VertexClass  oppositeVertexClass =   incClass . getOpposite  ( ) . getVertexClass  ( ) ;  boolean  equalRolenames =   incClass . getRolename  ( ) . equals  ( rolename ) ;  boolean  identicalClasses =  this == oppositeVertexClass ;  if  (  equalRolenames && identicalClasses )  { 
<<<<<<<
  this . throwSchemaException  ( incClass ) ;
=======
 throw  new SchemaException  (    "The rolename " +  incClass . getRolename  ( ) + " may be not used at both ends of the reflexive edge class " +   incClass . getEdgeClass  ( ) . getQualifiedName  ( ) ) ;
>>>>>>>
 } }   private void checkDuplicatedRolenameForAllIncidences  (  IncidenceClass incClass ,   Set  < IncidenceClass > incidenceSet )  {  String  rolename =   incClass . getOpposite  ( ) . getRolename  ( ) ;  if  (  rolename . isEmpty  ( ) )  {  return ; }  for ( IncidenceClass incidence : incidenceSet )  {  if  (  incidence == incClass )  {  continue ; }  if  (    incidence . getOpposite  ( ) . getRolename  ( ) . equals  ( rolename ) )  { 
<<<<<<<
  this . throwSchemaExceptionRolenameUsedTwice  ( incidence ) ;
=======
 throw  new SchemaException  (    "The rolename " +   incidence . getOpposite  ( ) . getRolename  ( ) + " is used twice at class " +  getQualifiedName  ( ) ) ;
>>>>>>>
 } } }   private void throwSchemaExceptionRolenameUsedTwice  (  IncidenceClass incidence )  {  throw  new SchemaException  (    "The rolename " +   incidence . getOpposite  ( ) . getRolename  ( ) + " is used twice at class " +  this . getQualifiedName  ( ) ) ; }   private void throwSchemaException  (  IncidenceClass 
<<<<<<<
incClass
=======
ic
>>>>>>>
 )  {  throw  new SchemaException  (    
<<<<<<<
"The rolename "
=======
 "Try to add IncidenceClass ending at '" +   ic . getVertexClass  ( ) . getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
 incClass . getRolename  ( )
=======
"' to VertexClass '"
>>>>>>>
 + 
<<<<<<<
" may be not used at both ends of the reflexive edge class "
=======
 getQualifiedName  ( )
>>>>>>>
 + 
<<<<<<<
  incClass . getEdgeClass  ( ) . getQualifiedName  ( )
=======
"'.IncidenceClasses may be added only to VertexClasses they are connected to."
>>>>>>>
 ) ; }   private void throwSchemaException  ( )  {  throw  new SchemaException  ( "IncidenceClasses may be added only to vertices they are connected to" ) ; }    @ Override public void addSuperClass  (  VertexClass superClass )  {   assertNotFinished  ( ) ;  if  (  
<<<<<<<
 (  superClass == this )
=======
superClass
>>>>>>>
 
<<<<<<<
||
=======
==
>>>>>>>
 
<<<<<<<
 (  superClass == null )
=======
this
>>>>>>>
 )  {  return ; }   this . checkDuplicateRolenames  ( superClass ) ;   super . addSuperClass  ( superClass ) ;  if  (  !  superClass . equals  (   this . getSchema  ( ) . getDefaultVertexClass  ( ) ) )  {     (  ( GraphClassImpl )   this . getSchema  ( ) . getGraphClass  ( ) ) . getVertexCsDag  ( ) . createEdge  ( superClass , this ) ; } }   private void checkDuplicateRolenames  (  VertexClass superClass )  {   this . checkDuplicatedRolenamesAgainstAllIncidences  (  superClass . getAllInIncidenceClasses  ( ) ) ;   this . checkDuplicatedRolenamesAgainstAllIncidences  (  superClass . getAllOutIncidenceClasses  ( ) ) ; }   private void checkDuplicatedRolenamesAgainstAllIncidences  (   Set  < IncidenceClass > incidences )  {  for ( IncidenceClass incidence : incidences )  {   this . checkDuplicateRolenames  ( incidence ) ; } }    @ Override public  Set  < IncidenceClass > getValidFromFarIncidenceClasses  ( )  {  if  (  this . isFinished  ( ) )  {  return 
<<<<<<<
 this . validFromFarIncidenceClasses
=======
validFromFarIncidenceClasses
>>>>>>>
 ; }   Set  < IncidenceClass >  validFromInc =  new  HashSet  < IncidenceClass >  ( ) ;  for ( IncidenceClass ic :  this . getAllOutIncidenceClasses  ( ) )  {  IncidenceClass  farInc =   ic . getEdgeClass  ( ) . getTo  ( ) ;   validFromInc . add  ( farInc ) ; }  for ( VertexClass aec :  this . getAllSuperClasses  ( ) )  {  VertexClass  vc = aec ;  if  (  vc . isInternal  ( ) )  {  continue ; }  for ( IncidenceClass ic :  vc . getAllOutIncidenceClasses  ( ) )  {  IncidenceClass  farInc =   ic . getEdgeClass  ( ) . getTo  ( ) ;   validFromInc . add  ( farInc ) ; } }   Set  < IncidenceClass >  temp =  new  HashSet  < IncidenceClass >  ( validFromInc ) ;  for ( IncidenceClass ic : temp )  {   validFromInc . removeAll  (  ic . getRedefinedIncidenceClasses  ( ) ) ; }  return validFromInc ; }    @ Override public  Set  < IncidenceClass > getValidToFarIncidenceClasses  ( )  {  if  (  this . isFinished  ( ) )  {  return 
<<<<<<<
 this . validToFarIncidenceClasses
=======
validToFarIncidenceClasses
>>>>>>>
 ; }   Set  < IncidenceClass >  validToInc =  new  HashSet  < IncidenceClass >  ( ) ;  for ( IncidenceClass ic :  this . getAllInIncidenceClasses  ( ) )  {  IncidenceClass  farInc =   ic . getEdgeClass  ( ) . getFrom  ( ) ;   validToInc . add  ( farInc ) ; }  for ( VertexClass aec :  this . getAllSuperClasses  ( ) )  {  VertexClass  vc = aec ;  if  (  vc . isInternal  ( ) )  {  continue ; }  for ( IncidenceClass ic :  vc . getAllInIncidenceClasses  ( ) )  {  IncidenceClass  farInc =   ic . getEdgeClass  ( ) . getFrom  ( ) ;   validToInc . add  ( farInc ) ; } }   Set  < IncidenceClass >  temp =  new  HashSet  < IncidenceClass >  ( validToInc ) ;  for ( IncidenceClass ic : temp )  {   validToInc . removeAll  (  ic . getRedefinedIncidenceClasses  ( ) ) ; }  return validToInc ; }    @ Override public  Set  < EdgeClass > getValidFromEdgeClasses  ( )  {  if  (  this . isFinished  ( ) )  {  return 
<<<<<<<
 this . validFromEdgeClasses
=======
validFromEdgeClasses
>>>>>>>
 ; }   Set  < EdgeClass >  validFrom =  new  HashSet  < EdgeClass >  ( ) ;  for ( IncidenceClass ic :  this . getValidFromFarIncidenceClasses  ( ) )  {  if  (  !   ic . getEdgeClass  ( ) . isInternal  ( ) )  {   validFrom . add  (  ic . getEdgeClass  ( ) ) ; } }  return validFrom ; }    @ Override public  Set  < EdgeClass > getValidToEdgeClasses  ( )  {  if  (  this . isFinished  ( ) )  {  return 
<<<<<<<
 this . validToEdgeClasses
=======
validToEdgeClasses
>>>>>>>
 ; }   Set  < EdgeClass >  validTo =  new  HashSet  < EdgeClass >  ( ) ;  for ( IncidenceClass ic :  this . getValidToFarIncidenceClasses  ( ) )  {  if  (  !   ic . getEdgeClass  ( ) . isInternal  ( ) )  {   validTo . add  (  ic . getEdgeClass  ( ) ) ; } }  return validTo ; }   public  Set  < IncidenceClass > getOwnInIncidenceClasses  ( )  {  return 
<<<<<<<
 this . inIncidenceClasses
=======
inIncidenceClasses
>>>>>>>
 ; }   public  Set  < IncidenceClass > getOwnOutIncidenceClasses  ( )  {  return 
<<<<<<<
 this . outIncidenceClasses
=======
outIncidenceClasses
>>>>>>>
 ; }    @ Override public  Set  < IncidenceClass > getAllInIncidenceClasses  ( )  {  if  (  this . isFinished  ( ) )  {  return 
<<<<<<<
 this . allInIncidenceClasses
=======
allInIncidenceClasses
>>>>>>>
 ; }   Set  < IncidenceClass >  incidenceClasses =  new  HashSet  < IncidenceClass >  ( ) ;   incidenceClasses . addAll  ( 
<<<<<<<
 this . inIncidenceClasses
=======
inIncidenceClasses
>>>>>>>
 ) ;  for ( VertexClass vc :  this . getDirectSuperClasses  ( ) )  {   incidenceClasses . addAll  (  vc . getAllInIncidenceClasses  ( ) ) ; }  return incidenceClasses ; }    @ Override public  Set  < IncidenceClass > getAllOutIncidenceClasses  ( )  {  if  (  this . isFinished  ( ) )  {  return 
<<<<<<<
 this . allOutIncidenceClasses
=======
allOutIncidenceClasses
>>>>>>>
 ; }   Set  < IncidenceClass >  incidenceClasses =  new  HashSet  < IncidenceClass >  ( ) ;   incidenceClasses . addAll  ( 
<<<<<<<
 this . outIncidenceClasses
=======
outIncidenceClasses
>>>>>>>
 ) ;  for ( VertexClass vc :  this . getDirectSuperClasses  ( ) )  {   incidenceClasses . addAll  (  vc . getAllOutIncidenceClasses  ( ) ) ; }  return incidenceClasses ; }    @ Override public  Set  < IncidenceClass > getOwnAndInheritedFarIncidenceClasses  ( )  {   Set  < IncidenceClass >  result =  new  HashSet  < IncidenceClass >  ( ) ;  for ( IncidenceClass ic :  this . getAllInIncidenceClasses  ( ) )  {   result . add  (   ic . getEdgeClass  ( ) . getFrom  ( ) ) ;  for ( IncidenceClass sup :  ic . getSubsettedIncidenceClasses  ( ) )  {   result . add  (   sup . getEdgeClass  ( ) . getFrom  ( ) ) ; } }  for ( IncidenceClass ic :  this . getAllOutIncidenceClasses  ( ) )  {   result . add  (   ic . getEdgeClass  ( ) . getTo  ( ) ) ;  for ( IncidenceClass sup :  ic . getSubsettedIncidenceClasses  ( ) )  {   result . add  (   sup . getEdgeClass  ( ) . getTo  ( ) ) ; } }  return result ; }    @ Override public  Set  < EdgeClass > getConnectedEdgeClasses  ( )  {   Set  < EdgeClass >  result =  new  HashSet  < EdgeClass >  ( ) ;  for ( IncidenceClass ic :  this . getAllInIncidenceClasses  ( ) )  {   result . add  (  ic . getEdgeClass  ( ) ) ; }  for ( IncidenceClass ic :  this . getAllOutIncidenceClasses  ( ) )  {   result . add  (  ic . getEdgeClass  ( ) ) ; }  return result ; }    @ Override public  Set  < EdgeClass > getOwnConnectedEdgeClasses  ( )  {   Set  < EdgeClass >  result =  new  HashSet  < EdgeClass >  ( ) ;  for ( IncidenceClass ic :  this . getOwnInIncidenceClasses  ( ) )  {   result . add  (  ic . getEdgeClass  ( ) ) ; }  for ( IncidenceClass ic :  this . getOwnOutIncidenceClasses  ( ) )  {   result . add  (  ic . getEdgeClass  ( ) ) ; }  return result ; }    @ Override protected void finish  ( )  {   
<<<<<<<
 this . allInIncidenceClasses
=======
allInIncidenceClasses
>>>>>>>
 =  new  HashSet  < IncidenceClass >  ( ) ;   
<<<<<<<
 this . allInIncidenceClasses
=======
allInIncidenceClasses
>>>>>>>
 . addAll  ( 
<<<<<<<
 this . inIncidenceClasses
=======
inIncidenceClasses
>>>>>>>
 ) ;   
<<<<<<<
 this . allOutIncidenceClasses
=======
allOutIncidenceClasses
>>>>>>>
 =  new  HashSet  < IncidenceClass >  ( ) ;   
<<<<<<<
 this . allOutIncidenceClasses
=======
allOutIncidenceClasses
>>>>>>>
 . addAll  ( 
<<<<<<<
 this . outIncidenceClasses
=======
outIncidenceClasses
>>>>>>>
 ) ;  for ( VertexClass vc :  this . getDirectSuperClasses  ( ) )  {   
<<<<<<<
 this . allInIncidenceClasses
=======
allInIncidenceClasses
>>>>>>>
 . addAll  (  vc . getAllInIncidenceClasses  ( ) ) ;   
<<<<<<<
 this . allOutIncidenceClasses
=======
allOutIncidenceClasses
>>>>>>>
 . addAll  (  vc . getAllOutIncidenceClasses  ( ) ) ; }   
<<<<<<<
 this . allInIncidenceClasses
=======
allInIncidenceClasses
>>>>>>>
 =  Collections . unmodifiableSet  ( 
<<<<<<<
 this . allInIncidenceClasses
=======
allInIncidenceClasses
>>>>>>>
 ) ;   
<<<<<<<
 this . allOutIncidenceClasses
=======
allOutIncidenceClasses
>>>>>>>
 =  Collections . unmodifiableSet  ( 
<<<<<<<
 this . allOutIncidenceClasses
=======
allOutIncidenceClasses
>>>>>>>
 ) ;   
<<<<<<<
 this . validFromFarIncidenceClasses
=======
validFromFarIncidenceClasses
>>>>>>>
 =  Collections . unmodifiableSet  (  this . getValidFromFarIncidenceClasses  ( ) ) ;   
<<<<<<<
 this . validToFarIncidenceClasses
=======
validToFarIncidenceClasses
>>>>>>>
 =  Collections . unmodifiableSet  (  this . getValidToFarIncidenceClasses  ( ) ) ;   
<<<<<<<
 this . validFromEdgeClasses
=======
validFromEdgeClasses
>>>>>>>
 =  Collections . unmodifiableSet  (  this . getValidFromEdgeClasses  ( ) ) ;   
<<<<<<<
 this . validToEdgeClasses
=======
validToEdgeClasses
>>>>>>>
 =  Collections . unmodifiableSet  (  this . getValidToEdgeClasses  ( ) ) ;   
<<<<<<<
 this . farRoleNameToEdgeClass
=======
farRoleNameToEdgeClass
>>>>>>>
 =  new  HashMap  < String , DirectedSchemaEdgeClass >  ( ) ;  for ( IncidenceClass ic :  this . getOwnAndInheritedFarIncidenceClasses  ( ) )  {  String  role =  ic . getRolename  ( ) ;  if  (   role == null ||   role . length  ( ) == 0 )  {  continue ; }   
<<<<<<<
 this . farRoleNameToEdgeClass
=======
farRoleNameToEdgeClass
>>>>>>>
 . put  ( 
<<<<<<<
 ic . getRolename  ( )
=======
role
>>>>>>>
 ,  this . getDirectedEdgeClassForFarEndRole  ( 
<<<<<<<
 ic . getRolename  ( )
=======
role
>>>>>>>
 ) ) ; }   
<<<<<<<
 this . farRoleNameToEdgeClass
=======
farRoleNameToEdgeClass
>>>>>>>
 =  Collections . unmodifiableMap  ( 
<<<<<<<
 this . farRoleNameToEdgeClass
=======
farRoleNameToEdgeClass
>>>>>>>
 ) ;   
<<<<<<<
 this . inIncidenceClasses
=======
inIncidenceClasses
>>>>>>>
 =  Collections . unmodifiableSet  ( 
<<<<<<<
 this . inIncidenceClasses
=======
inIncidenceClasses
>>>>>>>
 ) ;   
<<<<<<<
 this . outIncidenceClasses
=======
outIncidenceClasses
>>>>>>>
 =  Collections . unmodifiableSet  ( 
<<<<<<<
 this . outIncidenceClasses
=======
outIncidenceClasses
>>>>>>>
 ) ;  for ( IncidenceClass ic : 
<<<<<<<
 this . inIncidenceClasses
=======
inIncidenceClasses
>>>>>>>
 )  {    (  ( IncidenceClassImpl ) ic ) . finish  ( ) ; }  for ( IncidenceClass ic : 
<<<<<<<
 this . outIncidenceClasses
=======
outIncidenceClasses
>>>>>>>
 )  {    (  ( IncidenceClassImpl ) ic ) . finish  ( ) ; }   super . finish  ( ) ; }    @ Override public boolean isValidFromFor  (  EdgeClass ec )  {  return   this . getValidFromEdgeClasses  ( ) . contains  ( ec ) ; }    @ Override public boolean isValidToFor  (  EdgeClass ec )  {  return   this . getValidToEdgeClasses  ( ) . contains  ( ec ) ; }    @ Override protected void reopen  ( )  {    this . allInIncidenceClasses = null ;    this . allOutIncidenceClasses = null ;    this . validFromFarIncidenceClasses = null ;    this . validToFarIncidenceClasses = null ;    this . validFromEdgeClasses = null ;    this . validToEdgeClasses = null ;    this . inIncidenceClasses =  new  HashSet  < IncidenceClass >  (  this . inIncidenceClasses ) ;    this . outIncidenceClasses =  new  HashSet  < IncidenceClass >  (  this . outIncidenceClasses ) ;    this . farRoleNameToEdgeClass = null ;  for ( IncidenceClass ic :  this . inIncidenceClasses )  {    (  ( IncidenceClassImpl ) ic ) . reopen  ( ) ; }  for ( IncidenceClass ic :  this . outIncidenceClasses )  {    (  ( IncidenceClassImpl ) ic ) . reopen  ( ) ; }   super . reopen  ( ) ; }    @ Override public DirectedSchemaEdgeClass getDirectedEdgeClassForFarEndRole  (  String roleName )  {  if  (  this . isFinished  ( ) )  {  return  
<<<<<<<
 this . farRoleNameToEdgeClass
=======
farRoleNameToEdgeClass
>>>>>>>
 . get  ( roleName ) ; }  for ( IncidenceClass ic :  this . getOwnAndInheritedFarIncidenceClasses  ( ) )  {  String  role =  ic . getRolename  ( ) ;  if  (  roleName . equals  (  ic . getRolename  ( ) ) )  {  EdgeClass  ec =  ic . getEdgeClass  ( ) ;  return  new DirectedSchemaEdgeClass  ( ec ,  (  
<<<<<<<
  this . getValidFromEdgeClasses  ( ) . contains  ( ec )
=======
  ic . getDirection  ( ) ==  IncidenceDirection . IN
>>>>>>>
 ?  EdgeDirection . OUT :  EdgeDirection . IN ) ) ; } }  return null ; }   protected VertexClassImpl  (  String simpleName ,  PackageImpl pkg ,  GraphClassImpl gc )  {  super  ( simpleName , pkg , gc ,  gc . vertexClassDag ) ;   parentPackage . addVertexClass  ( this ) ;   graphClass . addVertexClass  ( this ) ; } }