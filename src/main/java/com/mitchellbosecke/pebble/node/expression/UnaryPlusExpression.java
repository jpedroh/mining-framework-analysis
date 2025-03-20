  package     com . mitchellbosecke . pebble . node . expression ;   import     com . mitchellbosecke . pebble . template . EvaluationContextImpl ;  import     com . mitchellbosecke . pebble . template . PebbleTemplateImpl ;  import     com . mitchellbosecke . pebble . utils . OperatorUtils ;   public class UnaryPlusExpression  extends UnaryExpression  {    @ Override public Object evaluate  (  PebbleTemplateImpl self ,  EvaluationContextImpl context )  throws PebbleException  {  return  OperatorUtils . unaryPlus  (   getChildExpression  ( ) . evaluate  ( self , context ) ) ; } 
<<<<<<<
=======
   @ Override public Object evaluate  (  PebbleTemplateImpl self ,  EvaluationContext context )  {  return  OperatorUtils . unaryPlus  (   getChildExpression  ( ) . evaluate  ( self , context ) ) ; }
>>>>>>>
 }