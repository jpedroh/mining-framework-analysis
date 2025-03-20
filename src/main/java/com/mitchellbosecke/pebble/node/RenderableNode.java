  package    com . mitchellbosecke . pebble . node ;   import   java . io . IOException ;  import   java . io . Writer ;  import     com . mitchellbosecke . pebble . template . EvaluationContextImpl ;  import     com . mitchellbosecke . pebble . template . PebbleTemplateImpl ;   public interface RenderableNode  extends  Node  {  void render  (  PebbleTemplateImpl self ,  Writer writer ,  EvaluationContextImpl context )  throws PebbleException , IOException ; 
<<<<<<<
=======
 void render  (  PebbleTemplateImpl self ,  Writer writer ,  EvaluationContext context )  throws IOException ;
>>>>>>>
 }