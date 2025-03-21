  package     it . geosolutions . geoserver . rest . encoder ;   import       it . geosolutions . geoserver . rest . encoder . utils . PropertyXMLEncoder ;   public class GSLayerEncoder  extends PropertyXMLEncoder  {   public GSLayerEncoder  ( )  {  super  ( "layer" ) ;   addEnabled  ( ) ; }   public void addDefaultStyle  (  String defaultStyle )  {   add  ( "defaultStyle" , defaultStyle ) ; }   protected void addEnabled  ( )  {   add  ( "enabled" , "true" ) ; }   public void setEnabled  (  boolean enable )  {  if  ( enable )   set  ( "enabled" , "true" ) ; else   set  ( "enabled" , "false" ) ; } 
<<<<<<<
=======
  public void setDefaultStyle  (  String defaultStyle )  {   set  ( "defaultStyle" , defaultStyle ) ; }
>>>>>>>
 }