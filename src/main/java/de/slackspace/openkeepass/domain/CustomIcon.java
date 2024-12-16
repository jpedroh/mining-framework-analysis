  package    de . slackspace . openkeepass . domain ;   import   java . util . Arrays ;  import   java . util . UUID ;  import     javax . xml . bind . annotation . XmlAccessType ;  import     javax . xml . bind . annotation . XmlAccessorType ;  import     javax . xml . bind . annotation . XmlElement ;  import     javax . xml . bind . annotation . XmlRootElement ;  import      javax . xml . bind . annotation . adapters . XmlJavaTypeAdapter ;  import       de . slackspace . openkeepass . domain . xml . adapter . UUIDXmlAdapter ;    @ XmlRootElement  @ XmlAccessorType  (  XmlAccessType . FIELD ) public class CustomIcon  {    @ XmlElement  (  name = "UUID" )  @ XmlJavaTypeAdapter  (  UUIDXmlAdapter . class ) private UUID  uuid ;    @ XmlElement  (  name = "Data" ) private   byte  [ ]  data ;  CustomIcon  ( )  { }   public UUID getUuid  ( )  {  return uuid ; }   public   byte  [ ] getData  ( )  {  return data ; }    @ Override public final  int hashCode  ( )  {   final  int  prime = 31 ;   int  result = 1 ;   result =   prime * result +  Arrays . hashCode  ( data ) ;   result =   prime * result +  (   (  uuid == null ) ? 0 :  uuid . hashCode  ( ) ) ;  return result ; }    @ Override public final boolean equals  (  Object obj )  {  if  (  this == obj )  return true ;  if  (  obj == null )  return false ;  if  (  !  (  obj instanceof CustomIcon ) )  return false ;  CustomIcon  other =  ( CustomIcon ) obj ;  if  (  !  Arrays . equals  ( data ,  other . data ) )  return false ;  if  (  uuid == null )  {  if  (   other . uuid != null )  return false ; } else  if  (  !  uuid . equals  (  other . uuid ) )  return false ;  return true ; }   public CustomIcon  (  CustomIconContract customIconContract )  {    this . uuid =  customIconContract . getUuid  ( ) ;    this . data =  customIconContract . getData  ( ) ; } }