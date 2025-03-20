  package     com . mitchellbosecke . pebble . node . expression ;   import     com . mitchellbosecke . pebble . extension . NodeVisitor ;  import     com . mitchellbosecke . pebble . template . EvaluationContextImpl ;  import     com . mitchellbosecke . pebble . template . PebbleTemplateImpl ;   public class LiteralNullExpression  implements   Expression  < Object >  {   private final  int  lineNumber ;   public LiteralNullExpression  (   int lineNumber )  {    this . lineNumber = lineNumber ; }    @ Override public void accept  (  NodeVisitor visitor )  {   visitor . visit  ( this ) ; }    @ Override public Object evaluate  (  PebbleTemplateImpl self ,  EvaluationContextImpl context )  throws PebbleException  {  return null ; }    @ Override public  int getLineNumber  ( )  {  return  this . lineNumber ; } 
<<<<<<<
=======
   @ Override public Object evaluate  (  PebbleTemplateImpl self ,  EvaluationContext context )  {  return null ; }
>>>>>>>
 }