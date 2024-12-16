  package     com . sun . syndication . feed . atom ;   import   java . io . Serializable ;  import      com . sun . syndication . feed . impl . ObjectBean ;   public class Link  implements  Cloneable , Serializable  {   private static final  long  serialVersionUID = 670365139518027828L ;   private final ObjectBean  objBean ;   private String  href ;   private String  hrefResolved ;   private String  rel = "alternate" ;   private String  type ;   private String  hreflang ;   private String  title ;   private  long  length ;   public Link  ( )  {   objBean =  new ObjectBean  (  this . getClass  ( ) , this ) ; }    @ Override public Object clone  ( )  throws CloneNotSupportedException  {  return  objBean . clone  ( ) ; }    @ Override public boolean equals  (   final Object other )  {  return  objBean . equals  ( other ) ; }    @ Override public  int hashCode  ( )  {  return  objBean . hashCode  ( ) ; }    @ Override public String toString  ( )  {  return  objBean . toString  ( ) ; }   public String getRel  ( )  {  return rel ; }   public void setRel  (   final String rel )  {    this . rel = rel ; }   public String getType  ( )  {  return type ; }   public void setType  (   final String type )  {    this . type = type ; }   public String getHref  ( )  {  return href ; }   public void setHref  (   final String href )  {    this . href = href ; }   public void setHrefResolved  (   final String hrefResolved )  {    this . hrefResolved = hrefResolved ; }   public String getHrefResolved  ( )  {  if  (  hrefResolved != null )  {  return hrefResolved ; } else  {  return href ; } }   public String getTitle  ( )  {  return title ; }   public void setTitle  (   final String title )  {    this . title = title ; }   public String getHreflang  ( )  {  return hreflang ; }   public void setHreflang  (   final String hreflang )  {    this . hreflang = hreflang ; }   public  long getLength  ( )  {  return length ; }   public void setLength  (   final  long length )  {    this . length = length ; } }