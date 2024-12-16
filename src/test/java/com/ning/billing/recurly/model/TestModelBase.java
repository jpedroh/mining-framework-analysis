  package     com . ning . billing . recurly . model ;   import      com . fasterxml . jackson . dataformat . xml . XmlMapper ;  import    org . testng . annotations . BeforeMethod ;  import      com . fasterxml . jackson . databind . introspect . AnnotationIntrospectorPair ;  import      com . fasterxml . jackson . databind . type . TypeFactory ;   public abstract class TestModelBase  {   protected XmlMapper  xmlMapper ;    @ BeforeMethod  (  alwaysRun = true ) public void setUp  ( )  throws Exception  {   xmlMapper =  RecurlyObject . newXmlMapper  ( ) ; 
<<<<<<<
=======
  final AnnotationIntrospector  secondary =  new JaxbAnnotationIntrospector  (  TypeFactory . defaultInstance  ( ) ) ;
>>>>>>>
 
<<<<<<<
=======
  final AnnotationIntrospector  pair =  new AnnotationIntrospectorPair  ( primary , secondary ) ;
>>>>>>>
 } }