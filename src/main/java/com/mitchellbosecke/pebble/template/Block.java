  package    com . mitchellbosecke . pebble . template ;   import   java . io . IOException ;  import   java . io . Writer ;   public interface Block  {  String getName  ( ) ;  void evaluate  (  PebbleTemplateImpl self ,  Writer writer ,  EvaluationContextImpl context )  throws PebbleException , IOException ; 
<<<<<<<
=======
 void evaluate  (  PebbleTemplateImpl self ,  Writer writer ,  EvaluationContext context )  throws IOException ;
>>>>>>>
 }