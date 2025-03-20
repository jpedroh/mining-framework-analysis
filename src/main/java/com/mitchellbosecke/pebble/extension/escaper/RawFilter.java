  package     com . mitchellbosecke . pebble . extension . escaper ;   import   java . util . List ;  import   java . util . Map ;  import     com . mitchellbosecke . pebble . extension . Filter ;  import     com . mitchellbosecke . pebble . template . PebbleTemplateImpl ;   public class RawFilter  implements  Filter  {   public  List  < String > getArgumentNames  ( )  {  return null ; } 
<<<<<<<
  public Object apply  (  Object inputObject ,   Map  < String , Object > args )  {  return   inputObject == null ? null :  new SafeString  (  inputObject . toString  ( ) ) ; }
=======
>>>>>>>
    @ Override public Object apply  (  Object inputObject ,   Map  < String , Object > args ,  PebbleTemplateImpl self ,   int lineNumber )  {  if  (  inputObject instanceof String )  {  return  new SafeString  (  ( String ) inputObject ) ; }  return inputObject ; } }