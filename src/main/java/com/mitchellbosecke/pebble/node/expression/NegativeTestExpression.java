  package     com . mitchellbosecke . pebble . node . expression ;   import     com . mitchellbosecke . pebble . template . EvaluationContextImpl ;  import     com . mitchellbosecke . pebble . template . PebbleTemplateImpl ;   public class NegativeTestExpression  extends PositiveTestExpression  {    @ Override public Object evaluate  (  PebbleTemplateImpl self ,  EvaluationContextImpl context )  throws PebbleException  {  return  !  (  ( Boolean )  super . evaluate  ( self , context ) ) ; } 
<<<<<<<
=======
   @ Override public Object evaluate  (  PebbleTemplateImpl self ,  EvaluationContext context )  {  return  !  (  ( Boolean )  super . evaluate  ( self , context ) ) ; }
>>>>>>>
 }