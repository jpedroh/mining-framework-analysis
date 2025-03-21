  package      it . geosolutions . geoserver . rest . encoder . metadata ;   import       it . geosolutions . geoserver . rest . encoder . utils . XmlElement ;  import   java . math . BigDecimal ;  import   org . jdom . Element ;   public class GSDimensionInfoEncoder  extends XmlElement  {   private boolean  enabled ;   public enum Presentation  {  LIST ,  CONTINUOUS_INTERVAL }   public enum PresentationDiscrete  {  DISCRETE_INTERVAL }   public GSDimensionInfoEncoder  (   final boolean enabled )  {  super  ( DIMENSIONINFO ) ;   add  ( "enabled" ,   ( enabled ) ? "true" : "false" ) ;    this . enabled = enabled ; }   public GSDimensionInfoEncoder  ( )  {  super  ( DIMENSIONINFO ) ;   add  ( "enabled" , "false" ) ;    this . enabled =  Boolean . FALSE ; }   public void addPresentation  (   final Presentation pres )  {  if  ( enabled )  {   add  ( 
<<<<<<<
"presentation"
=======
PRESENTATION
>>>>>>>
 ,  pres . toString  ( ) ) ; } }   public void addPresentation  (   final PresentationDiscrete pres ,   final BigDecimal interval )  {  if  ( enabled )  {   add  ( 
<<<<<<<
"presentation"
=======
PRESENTATION
>>>>>>>
 ,  pres . toString  ( ) ) ;   add  ( 
<<<<<<<
"resolution"
=======
RESOLUTION
>>>>>>>
 ,  String . valueOf  ( interval ) ) ; } }   public void add  (  String nodename ,  String nodetext )  {   final Element  el =  new Element  ( nodename ) ;   el . setText  ( nodetext ) ;   this . addContent  ( el ) ; }   public final static String  DIMENSIONINFO = "dimensionInfo" ;   public final static String  RESOLUTION = "resolution" ;   public final static String  PRESENTATION = "presentation" ;   public void setEnabled  (   final boolean enabled )  {   set  ( "enabled" , "true" ) ;    this . enabled =  Boolean . TRUE ; } 
<<<<<<<
=======
  public void setPresentation  (   final Presentation pres )  {  if  ( enabled )  {   set  ( PRESENTATION ,  pres . toString  ( ) ) ;   remove  ( RESOLUTION ) ; } }
>>>>>>>
   public void setPresentation  (   final PresentationDiscrete pres ,   final BigDecimal interval )  {  if  ( enabled )  {   set  ( PRESENTATION ,  pres . toString  ( ) ) ;   set  ( RESOLUTION ,  String . valueOf  ( interval ) ) ; } } }