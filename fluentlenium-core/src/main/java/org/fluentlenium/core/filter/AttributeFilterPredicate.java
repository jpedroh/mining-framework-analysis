  package    org . fluentlenium . core . filter ;   import     org . fluentlenium . core . domain . FluentWebElement ;  import    java . util . function . Predicate ;   public class AttributeFilterPredicate  implements   Predicate  < FluentWebElement >  {   private final AttributeFilter  filter ;   public AttributeFilterPredicate  (  AttributeFilter filter )  {    this . filter = filter ; }    @ Override public boolean test  (  FluentWebElement element )  {  String  attribute =  getAttributeValue  ( element ) ;  return   filter != null &&   filter . getMatcher  ( ) . isSatisfiedBy  ( attribute ) ; }   private String getAttributeValue  (  FluentWebElement element )  { 
<<<<<<<
 return   "text" . equalsIgnoreCase  (  filter . getAttribute  ( ) ) ?  element . text  ( ) :  element . attribute  (  filter . getAttribute  ( ) ) ;
=======
 if  (  "text" . equalsIgnoreCase  (  filter . getAttribut  ( ) ) )  {  return  element . text  ( ) ; } else  if  (  "textContent" . equalsIgnoreCase  (  filter . getAttribut  ( ) ) )  {  return  element . textContent  ( ) ; } else  {  return  element . attribute  (  filter . getAttribut  ( ) ) ; }
>>>>>>>
 } }