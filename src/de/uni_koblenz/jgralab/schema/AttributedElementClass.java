  package    de . uni_koblenz . jgralab . schema ;   import   java . util . Set ;  import   java . util . SortedSet ;  import    de . uni_koblenz . jgralab . AttributedElement ;  import      de . uni_koblenz . jgralab . schema . exception . DuplicateAttributeException ;  import      de . uni_koblenz . jgralab . schema . exception . SchemaClassAccessException ;  import    de . uni_koblenz . jgralab . NoSuchAttributeException ;   public interface AttributedElementClass  <  SC  extends  AttributedElementClass  < SC , IC > ,  IC  extends  AttributedElement  < SC , IC > >  extends  NamedElement  {   public void addAttribute  (  Attribute anAttribute ) ;   public void addAttribute  (  String name ,  Domain domain ,  String defaultValueAsString ) ;   public void addAttribute  (  String name ,  Domain domain ) ;   public void addConstraint  (  Constraint constraint ) ;   public boolean containsAttribute  (  String name ) ;   public  Set  < SC > getAllSubClasses  ( ) ;   public  Set  < SC > getAllSuperClasses  ( ) ;   public Attribute getAttribute  (  String name ) ;   public  int getAttributeCount  ( ) ;   public  SortedSet  < Attribute > getAttributeList  ( ) ;   public  Set  < Constraint > getConstraints  ( ) ;   public  Set  < SC > getDirectSubClasses  ( ) ;   public  Set  < SC > getDirectSuperClasses  ( ) ;   public  Class  < 
<<<<<<<
 ? extends AttributedElement
=======
IC
>>>>>>>
 > getSchemaClass  ( ) ;   public  Class  < 
<<<<<<<
 ? extends AttributedElement
=======
IC
>>>>>>>
 > getSchemaImplementationClass  ( ) ;   public Attribute getOwnAttribute  (  String name ) ;   public  int getOwnAttributeCount  ( ) ;   public  SortedSet  < Attribute > getOwnAttributeList  ( ) ;   public String getVariableName  ( ) ;   public boolean hasAttributes  ( ) ;   public boolean hasOwnAttributes  ( ) ;   public boolean isAbstract  ( ) ;   public boolean isInternal  ( ) ;   public void setAbstract  (  boolean isAbstract ) ;   public boolean isDirectSubClassOf  (  SC anAttributedElementClass ) ;   public boolean isDirectSuperClassOf  (  SC anAttributedElementClass ) ;   public boolean isSubClassOf  (  SC anAttributedElementClass ) ;   public boolean isSuperClassOf  (  SC anAttributedElementClass ) ;   public boolean isSuperClassOfOrEquals  (  SC anAttributedElementClass ) ;   public  int getAttributeIndex  (  String name )  throws NoSuchAttributeException ; }