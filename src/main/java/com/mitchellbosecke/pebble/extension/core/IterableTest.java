  package     com . mitchellbosecke . pebble . extension . core ;   import     com . mitchellbosecke . pebble . extension . Test ;  import     com . mitchellbosecke . pebble . template . EvaluationContext ;  import     com . mitchellbosecke . pebble . template . PebbleTemplate ;  import   java . util . List ;  import   java . util . Map ;   public class IterableTest  implements  Test  {    @ Override public  List  < String > getArgumentNames  ( )  {  return null ; }    @ Override public boolean apply  (  Object input ,   Map  < String , Object > args ,  PebbleTemplate self ,  EvaluationContext context ,   int lineNumber )  {  return  input instanceof Iterable ; } 
<<<<<<<
=======
   @ Override public boolean apply  (  Object input ,   Map  < String , Object > args )  {  return   input instanceof Iterable ||  input instanceof  Object  [ ] ; }
>>>>>>>
 }