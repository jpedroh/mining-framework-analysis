  package    com . mitchellbosecke . pebble . template ;   import   java . util . Map ;  import     com . mitchellbosecke . pebble . extension . NamedArguments ;   public interface Macro  extends  NamedArguments  {  String getName  ( ) ;  String call  (  PebbleTemplateImpl self ,  EvaluationContextImpl context ,   Map  < String , Object > args )  throws PebbleException ; 
<<<<<<<
=======
 String call  (  PebbleTemplateImpl self ,  EvaluationContext context ,   Map  < String , Object > args ) ;
>>>>>>>
 }