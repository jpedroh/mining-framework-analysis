  package     org . dspace . app . orcid . model ; 
<<<<<<<
  import   java . util . Arrays ;
=======
>>>>>>>
   public enum OrcidEntityType  {  PUBLICATION  ( "Publication" , "/work" ) ,  FUNDING  ( "Project" , "/funding" )  ;   private final String  entityType ;   private OrcidEntityType  (  String entityType ,  String path )  {    this . entityType = entityType ;    this . path = path ; }   public String getEntityType  ( )  {  return entityType ; }   public static boolean isValidEntityType  (  String entityType )  {  return   Arrays . stream  (  OrcidEntityType . values  ( ) ) . anyMatch  (  orcidEntityType ->   orcidEntityType . getEntityType  ( ) . equalsIgnoreCase  ( entityType ) ) ; }   public static OrcidEntityType fromEntityType  (  String entityType )  {  return     Arrays . stream  (  OrcidEntityType . values  ( ) ) . filter  (  orcidEntityType ->   orcidEntityType . getEntityType  ( ) . equalsIgnoreCase  ( entityType ) ) . findFirst  ( ) . orElse  ( null ) ; } }