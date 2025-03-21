  package     it . geosolutions . geoserver . rest . encoder ;   import   org . jdom . Element ;  import       it . geosolutions . geoserver . rest . encoder . utils . PropertyXMLEncoder ;  import       it . geosolutions . geoserver . rest . encoder . utils . ElementUtils ;   public class GSWorkspaceEncoder  extends PropertyXMLEncoder  {   public GSWorkspaceEncoder  ( )  {  super  ( WORKSPACE ) ; }   public GSWorkspaceEncoder  (  String name )  {  super  ( 
<<<<<<<
"workspace"
=======
WORKSPACE
>>>>>>>
 ) ;   addName  ( name ) ; }   public void addName  (  String name )  {   final Element  el =  ElementUtils . contains  ( 
<<<<<<<
"name"
=======
 getRoot  ( )
>>>>>>>
 , NAME ) ;  if  (  el == null )   add  ( 
<<<<<<<
"name"
=======
NAME
>>>>>>>
 , name ) ; else  throw  new IllegalStateException  (  "Workspace name is already set: " +  el . getText  ( ) ) ; }   public void setName  (  String name )  {   final Element  el =  ElementUtils . contains  ( 
<<<<<<<
"name"
=======
 getRoot  ( )
>>>>>>>
 , NAME ) ;  if  (  el == null )   add  ( 
<<<<<<<
"name"
=======
NAME
>>>>>>>
 , name ) ; else   el . setText  ( name ) ; }   public String getName  ( )  {   final Element  el =  ElementUtils . contains  (  getRoot  ( ) , NAME ) ;  if  (  el != null )  return  el . getTextTrim  ( ) ; else  return null ; }   public final static String  WORKSPACE = "workspace" ;   public final static String  NAME = "name" ; }