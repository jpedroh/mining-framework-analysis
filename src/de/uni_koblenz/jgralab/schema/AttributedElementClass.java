  package    de . uni_koblenz . jgralab . schema ;   import   java . util . Set ;  import    de . uni_koblenz . jgralab . AttributedElement ;  import    de . uni_koblenz . jgralab . NoSuchAttributeException ;  import      de . uni_koblenz . jgralab . schema . exception . DuplicateAttributeException ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaClassAccessException ;  import   java . util . List ;  import   org . pcollections . PVector ;   public interface AttributedElementClass  <  SC  extends  AttributedElementClass  < SC , IC > ,  IC  extends  AttributedElement  < SC , IC > >  extends  NamedElement  {   public void addAttribute  (  Attribute anAttribute ) ;   public void addAttribute  (  String name ,  Domain domain ,  String defaultValueAsString ) ;   public void addAttribute  (  String name ,  Domain domain ) ;   public void addConstraint  (  Constraint constraint ) ;   public boolean containsAttribute  (  String name ) ; 
<<<<<<<
  public  Set  < SC > getAllSubClasses  ( ) ;
=======
>>>>>>>
 
<<<<<<<
  public  Set  < SC > getAllSuperClasses  ( ) ;
=======
>>>>>>>
   public Attribute getAttribute  (  String name ) ;   public  int getAttributeCount  ( ) ;   public  List  < Attribute > getAttributeList  ( ) ;   public  Set  < Constraint > getConstraints  ( ) ; 
<<<<<<<
  public  Set  < SC > getDirectSubClasses  ( ) ;
=======
>>>>>>>
 
<<<<<<<
  public  Set  < SC > getDirectSuperClasses  ( ) ;
=======
>>>>>>>
   public  Class  < IC > getSchemaClass  ( ) ;   public  Class  < IC > getSchemaImplementationClass  ( ) ;   public Attribute getOwnAttribute  (  String name ) ;   public  int getOwnAttributeCount  ( ) ;   public  List  < Attribute > getOwnAttributeList  ( ) ;   public String getVariableName  ( ) ;   public boolean hasAttributes  ( ) ;   public boolean hasOwnAttributes  ( ) ;   public boolean isAbstract  ( ) ;   public boolean isDirectSubClassOf  (  SC anAttributedElementClass ) ;   public boolean isDirectSuperClassOf  (  SC anAttributedElementClass ) ;   public boolean isInternal  ( ) ;   public boolean isSubClassOf  (  SC anAttributedElementClass ) ;   public boolean isSuperClassOf  (  SC anAttributedElementClass ) ;   public boolean isSuperClassOfOrEquals  (  SC anAttributedElementClass ) ;   public void setAbstract  (  boolean isAbstract ) ;   public  int getAttributeIndex  (  String name )  throws NoSuchAttributeException ; }