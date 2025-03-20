  package     com . mitchellbosecke . pebble . node . expression ;   import     com . mitchellbosecke . pebble . node . Node ;  import     com . mitchellbosecke . pebble . template . EvaluationContextImpl ;  import     com . mitchellbosecke . pebble . template . PebbleTemplateImpl ;   public interface Expression  <  T >  extends  Node  {  T evaluate  (  PebbleTemplateImpl self ,  EvaluationContextImpl context )  throws PebbleException ;   int getLineNumber  ( ) ; 
<<<<<<<
=======
 T evaluate  (  PebbleTemplateImpl self ,  EvaluationContext context ) ;
>>>>>>>
 }