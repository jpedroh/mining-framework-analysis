  package     de . uni_koblenz . jgralab . schema . impl ;   import    java . lang . reflect . Field ;  import   java . util . Collections ;  import   java . util . HashMap ;  import   java . util . Set ;  import   java . util . TreeSet ;  import    de . uni_koblenz . jgralab . AttributedElement ;  import    de . uni_koblenz . jgralab . NoSuchAttributeException ;  import     de . uni_koblenz . jgralab . schema . Attribute ;  import     de . uni_koblenz . jgralab . schema . AttributedElementClass ;  import     de . uni_koblenz . jgralab . schema . Constraint ;  import     de . uni_koblenz . jgralab . schema . Domain ;  import      de . uni_koblenz . jgralab . schema . exception . DuplicateAttributeException ;  import      de . uni_koblenz . jgralab . schema . exception . InheritanceException ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaClassAccessException ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaException ;  import       de . uni_koblenz . jgralab . schema . impl . compilation . SchemaClassManager ;  import   java . util . List ;  import   org . pcollections . ArrayPSet ;  import   org . pcollections . ArrayPVector ;  import   org . pcollections . PSet ;  import   org . pcollections . PVector ;   public abstract class AttributedElementClassImpl  <  SC  extends  AttributedElementClass  < SC , IC > ,  IC  extends  AttributedElement  < SC , IC > >  extends NamedElementImpl  implements   AttributedElementClass  < SC , IC >  { 
<<<<<<<
  private final  TreeSet  < Attribute >  attributeList =  new  TreeSet  < Attribute >  ( ) ;
=======
>>>>>>>
   private  SortedSet  < Attribute >  allAttributeList ;   protected  
<<<<<<<
HashSet
=======
PSet
>>>>>>>
  < Constraint >  constraints =  new  HashSet  < Constraint >  ( 1 ) ; 
<<<<<<<
  protected  Set  < SC >  directSubClasses =  new  HashSet  < SC >  ( ) ;
=======
>>>>>>>
   protected  Set  < SC >  allSubClasses ; 
<<<<<<<
  protected  Set  < SC >  directSuperClasses =  new  HashSet  < SC >  ( ) ;
=======
>>>>>>>
   protected  HashMap  < String , Integer >  attributeIndex ;   protected  Set  < SC >  allSuperClasses ;   private protected boolean  finished = false ;   private boolean  isAbstract = false ;   private boolean  internal = false ;   private  Class  < IC >  schemaClass ;   private  Class  < IC >  schemaImplementationClass ;   protected AttributedElementClassImpl  (  String simpleName ,  Package pkg ,  Schema schema )  {  super  ( simpleName , pkg , schema ) ; }    @ Override public void addAttribute  (  Attribute anAttribute )  { 
<<<<<<<
 if  ( finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }
=======
  assertNotFinished  ( ) ;
>>>>>>>
  if  (  containsAttribute  (  anAttribute . getName  ( ) ) )  {  throw 
<<<<<<<
 new DuplicateAttributeException  (  anAttribute . getName  ( ) ,  getQualifiedName  ( ) )
=======
 new SchemaException  (     "Duplicate attribute '" +  anAttribute . getName  ( ) + "' in AttributedElementClass '" +  getQualifiedName  ( ) + "'" )
>>>>>>>
 ; } 
<<<<<<<
 if  (  subclassContainsAttribute  (  anAttribute . getName  ( ) ) )  {  throw  new DuplicateAttributeException  (      "Duplicate Attribute '" +  anAttribute . getName  ( ) + "' in AttributedElementClass '" +  getQualifiedName  ( ) + "'. " + "A derived AttributedElementClass already contains this Attribute." ) ; }
=======
  TreeSet  < Attribute >  s =  new  TreeSet  < Attribute >  ( allAttributes ) ;
>>>>>>>
   s . add  ( anAttribute ) ;   allAttributes =   ArrayPVector .  < Attribute > empty  ( ) . plusAll  ( s ) ; }    @ Override public void addAttribute  (  String name ,  Domain domain ,  String defaultValueAsString )  {   addAttribute  (  new AttributeImpl  ( name , domain , this , defaultValueAsString ) ) ; }    @ Override public void addAttribute  (  String name ,  Domain domain )  {   addAttribute  (  new AttributeImpl  ( name , domain , this , null ) ) ; }    @ Override public void addConstraint  (  Constraint constraint )  {  if  ( finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }   
<<<<<<<
constraints
=======
assertNotFinished
>>>>>>>
 . add  ( constraint ) ;   constraints =  constraints . plus  ( constraint ) ; }    @ SuppressWarnings  ( "unchecked" ) protected void addSuperClass  (  SC superClass )  {  if  ( finished )  {  throw  new SchemaException  ( "No changes to finished schema!" ) ; }  if  (   (  superClass == this ) ||  (  superClass == null ) )  {  return ; }   directSuperClasses . remove  (   getSchema  ( ) . getDefaultGraphClass  ( ) ) ;   directSuperClasses . remove  (   getSchema  ( ) . getDefaultEdgeClass  ( ) ) ;   directSuperClasses . remove  (   getSchema  ( ) . getDefaultVertexClass  ( ) ) ;  for ( Attribute a :  superClass . getAttributeList  ( ) )  {  if  (   getOwnAttribute  (  a . getName  ( ) ) != null )  {  throw  new InheritanceException  (       "Cannot add " +  superClass . getQualifiedName  ( ) + " as superclass of " +  getQualifiedName  ( ) + ", cause: Attribute " +  a . getName  ( ) + " is declared in both classes" ) ; } }  if  (  superClass . isSubClassOf  (  ( SC ) this ) )  {  throw  new InheritanceException  (    "Cycle in class hierarchie for classes: " +  getQualifiedName  ( ) + " and " +  superClass . getQualifiedName  ( ) ) ; }   directSuperClasses . add  ( superClass ) ;     (  (  AttributedElementClassImpl  < SC , IC > ) superClass ) . directSubClasses . add  (  ( SC ) this ) ; }   protected String attributesToString  ( )  {  StringBuilder  output =  new StringBuilder  ( 
<<<<<<<
"\nSelf Attributes:\n"
=======
"Attributes:\n"
>>>>>>>
 ) ;  for ( Attribute a :  getAttributeList  ( ) )  {   output . append  (   "\t" +  a . toString  ( ) + "\n" ) ; } 
<<<<<<<
 while  (  it . hasNext  ( ) )  {   a =  it . next  ( ) ;   output . append  (   a . toString  ( ) + "\n" ) ; }
=======
>>>>>>>
 
<<<<<<<
  output . append  ( "\nSelf + Inherited Attributes:\n" ) ;
=======
>>>>>>>
 
<<<<<<<
 while  (  it . hasNext  ( ) )  {   a =  it . next  ( ) ;   output . append  (   a . toString  ( ) + "\n" ) ; }
=======
>>>>>>>
  return  output . toString  ( ) ; }    @ Override public boolean containsAttribute  (  String name )  {  if  ( finished )  {  return  attributeIndex . containsKey  ( name ) ; }  return  (   getAttribute  ( name ) != null ) ; } 
<<<<<<<
   @ Override public  Set  < SC > getAllSubClasses  ( )  {  if  ( finished )  {  return allSubClasses ; }   Set  < SC >  returnSet =  new  HashSet  < SC >  ( ) ;  for ( SC subclass : directSubClasses )  {   returnSet . add  ( subclass ) ;   returnSet . addAll  (  subclass . getAllSubClasses  ( ) ) ; }  return returnSet ; }
=======
>>>>>>>
 
<<<<<<<
   @ Override public  Set  < SC > getAllSuperClasses  ( )  {  if  ( finished )  {  return allSuperClasses ; }   HashSet  < SC >  allSuperClasses =  new  HashSet  < SC >  ( ) ;   allSuperClasses . addAll  ( directSuperClasses ) ;  for ( SC superClass : directSuperClasses )  {   allSuperClasses . addAll  (  superClass . getAllSuperClasses  ( ) ) ; }  return allSuperClasses ; }
=======
>>>>>>>
    @ Override public Attribute getAttribute  (  String name )  {  if  ( finished )  {   Iterator  < Attribute >  it =  allAttributeList . iterator  ( ) ;  Attribute  a ;  while  (  it . hasNext  ( ) )  {   a =  it . next  ( ) ;  if  (   a . getName  ( ) . equals  ( name ) )  {  return a ; } } } 
<<<<<<<
 if  (  ownAttr != null )  {  return ownAttr ; }
=======
>>>>>>>
  for ( 
<<<<<<<
SC
=======
Attribute
>>>>>>>
 a : allAttributes )  {  if  (   a . getName  ( ) . equals  ( name ) )  {  return 
<<<<<<<
inheritedAttr
=======
a
>>>>>>>
 ; } }  return null ; }    @ Override public  int getAttributeCount  ( )  {  if  ( finished )  {  return  allAttributeList . size  ( ) ; } 
<<<<<<<
 for ( SC superClass : directSuperClasses )  {   attrCount +=  superClass . getAttributeCount  ( ) ; }
=======
>>>>>>>
  return  allAttributes . size  ( ) ; }    @ Override public  List  < Attribute > getAttributeList  ( )  {  if  ( finished )  {  return allAttributeList ; } 
<<<<<<<
 for ( SC superClass : directSuperClasses )  {   attrList . addAll  (  superClass . getAttributeList  ( ) ) ; }
=======
>>>>>>>
  return allAttributes ; }    @ Override public  Set  < Constraint > getConstraints  ( )  {  return constraints ; } 
<<<<<<<
   @ Override public  Set  < SC > getDirectSubClasses  ( )  {  return directSubClasses ; }
=======
>>>>>>>
 
<<<<<<<
   @ Override public  Set  < SC > getDirectSuperClasses  ( )  {  return directSuperClasses ; }
=======
>>>>>>>
    @ SuppressWarnings  ( "unchecked" )  @ Override public  Class  < IC > getSchemaClass  ( )  {  if  (  schemaClass == null )  {  String  schemaClassName =    
<<<<<<<
 getSchema  ( )
=======
schema
>>>>>>>
 . getPackagePrefix  ( ) + "." +  getQualifiedName  ( ) ;  try  {   schemaClass =  (  Class  < IC > )  Class . forName  ( schemaClassName , true ,  SchemaClassManager . instance  (  
<<<<<<<
 getSchema  ( )
=======
schema
>>>>>>>
 . getQualifiedName  ( ) ) ) ; }  catch (   ClassNotFoundException e )  {  throw  new SchemaClassAccessException  (   "Can't load (generated) schema class for AttributedElementClass '" +  getQualifiedName  ( ) + "'" , e ) ; } }  return schemaClass ; }    @ SuppressWarnings  ( "unchecked" )  @ Override public  Class  < IC > getSchemaImplementationClass  ( )  {  if  (  isAbstract  ( ) )  {  throw  new SchemaClassAccessException  (   "Can't get (generated) schema implementation class. AttributedElementClass '" +  getQualifiedName  ( ) + "' is abstract!" ) ; }  if  (  schemaImplementationClass == null )  {  try  {  Field  f =   getSchemaClass  ( ) . getField  ( "IMPLEMENTATION_CLASS" ) ;   schemaImplementationClass =  (  Class  < IC > )  f . get  ( schemaClass ) ; }  catch (   SecurityException e )  {  throw  new SchemaClassAccessException  ( e ) ; }  catch (   NoSuchFieldException e )  {  throw  new SchemaClassAccessException  ( e ) ; }  catch (   IllegalArgumentException e )  {  throw  new SchemaClassAccessException  ( e ) ; }  catch (   IllegalAccessException e )  {  throw  new SchemaClassAccessException  ( e ) ; } }  return schemaImplementationClass ; } 
<<<<<<<
   @ Override public Attribute getOwnAttribute  (  String name )  {   Iterator  < Attribute >  it =  attributeList . iterator  ( ) ;  Attribute  a ;  while  (  it . hasNext  ( ) )  {   a =  it . next  ( ) ;  if  (   a . getName  ( ) . equals  ( name ) )  {  return a ; } }  return null ; }
=======
>>>>>>>
    @ Override public boolean hasAttributes  ( )  {  return  !   getAttributeList  ( ) . isEmpty  ( ) ; }    @ Override public boolean isAbstract  ( )  {  return isAbstract ; }    @ Override public boolean isDirectSubClassOf  (  SC anAttributedElementClass )  {  return  directSuperClasses . contains  ( anAttributedElementClass ) ; }    @ Override public boolean isDirectSuperClassOf  (  SC anAttributedElementClass )  {  return    (  (  AttributedElementClassImpl  < SC , IC > ) anAttributedElementClass ) . directSuperClasses . contains  ( this ) ; }    @ Override public boolean isInternal  ( )  {  return internal ; }  void setInternal  (  Boolean b )  {   internal = b ; }    @ Override public boolean isSubClassOf  (  SC anAttributedElementClass )  {  return   getAllSuperClasses  ( ) . contains  ( anAttributedElementClass ) ; }    @ Override public boolean isSuperClassOf  (  SC anAttributedElementClass )  {  return   anAttributedElementClass . getAllSuperClasses  ( ) . contains  ( this ) ; }    @ Override public boolean isSuperClassOfOrEquals  (  SC anAttributedElementClass )  {  return  (   (  this == anAttributedElementClass ) ||  (  isSuperClassOf  ( anAttributedElementClass ) ) ) ; }    @ Override public void setAbstract  (  boolean isAbstract )  {    this . isAbstract = isAbstract ; }   protected boolean subclassContainsAttribute  (  String name )  {  for ( SC subClass :  getAllSubClasses  ( ) )  {  Attribute  subclassAttr =  subClass . getAttribute  ( name ) ;  if  (  subclassAttr != null )  {  return true ; } }  return false ; }   protected void finish  ( )  { 
<<<<<<<
  allSuperClasses =  new  HashSet  < SC >  ( ) ;
=======
 assert  allAttributes != null ;
>>>>>>>
   allSuperClasses . addAll  ( directSuperClasses ) ;  for ( SC superClass : directSuperClasses )  {   allSuperClasses . addAll  (  superClass . getAllSuperClasses  ( ) ) ; }   allSubClasses =  new  HashSet  < SC >  ( ) ;   allSubClasses . addAll  ( directSubClasses ) ;  for ( SC subClass : directSubClasses )  {   allSubClasses . addAll  (  subClass . getAllSubClasses  ( ) ) ; }   allAttributeList =  new  TreeSet  < Attribute >  ( ) ;   allAttributeList . addAll  ( attributeList ) ;  for ( SC superClass : directSuperClasses )  {   allAttributeList . addAll  (  superClass . getAttributeList  ( ) ) ; }   directSubClasses =  Collections . unmodifiableSet  ( directSubClasses ) ;   directSuperClasses =  Collections . unmodifiableSet  ( directSuperClasses ) ;   allSuperClasses =  Collections . unmodifiableSet  ( allSuperClasses ) ;   allSubClasses =  Collections . unmodifiableSet  ( allSubClasses ) ;   allAttributeList =  Collections . unmodifiableSortedSet  ( allAttributeList ) ;   attributeIndex =  new  HashMap  < String , Integer >  ( ) ;   int  i = 0 ;  for ( Attribute a : 
<<<<<<<
allAttributeList
=======
allAttributes
>>>>>>>
 )  {   attributeIndex . put  (  a . getName  ( ) , i ) ;   ++ i ; }   finished = true ; }   protected void reopen  ( )  {   directSubClasses =  new  HashSet  < SC >  ( directSubClasses ) ;   directSuperClasses =  new  HashSet  < SC >  ( directSuperClasses ) ;   allSuperClasses = null ;   allSubClasses = null ;   allAttributeList = null ;   finished = false ; }   protected boolean isFinished  ( )  {  return finished ; }    @ Override public  int getAttributeIndex  (  String name )  {  Integer  i ;  if  ( 
<<<<<<<
 isFinished  ( )
=======
finished
>>>>>>>
 )  { 
<<<<<<<
  i =  attributeIndex . get  ( name ) ;
=======
 Integer  i =  attributeIndex . get  ( name ) ;
>>>>>>>
  if  (  i != null )  {  return i ; } } else  {   int 
<<<<<<<
 j = 0
=======
 i = 0
>>>>>>>
 ;  for ( Attribute a :  getAttributeList  ( ) )  {  if  (   a . getName  ( ) . equals  ( name ) )  { 
<<<<<<<
 break ;
=======
 return i ;
>>>>>>>
 }   ++ 
<<<<<<<
j
=======
i
>>>>>>>
 ; }   i =  Integer . valueOf  ( j ) ; } 
<<<<<<<
 if  (   i != null &&  i <  allAttributeList . size  ( ) )  {  return  i . intValue  ( ) ; } else  {  throw  new NoSuchAttributeException  (    this . getSimpleName  ( ) + " doesn't contain an attribute " + name ) ; }
=======
 throw  new NoSuchAttributeException  (     getQualifiedName  ( ) + " doesn't contain an attribute '" + name + "'" ) ;
>>>>>>>
 }   protected  PVector  < Attribute >  allAttributes ;   protected AttributedElementClassImpl  (  String simpleName ,  PackageImpl pkg ,  SchemaImpl schema )  {  super  ( simpleName , pkg , schema ) ;   allAttributes =  ArrayPVector . empty  ( ) ;   constraints =  ArrayPSet . empty  ( ) ; }   protected void assertNotFinished  ( )  {  if  ( finished )  {  throw  new SchemaException  ( "No changes allowed in a finished Schema." ) ; } } }