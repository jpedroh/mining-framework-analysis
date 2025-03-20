  package   org . simmetrics . simplifiers ;   import static       org . apache . commons . codec . language . Soundex . US_ENGLISH ;   public class Soundex  implements  Simplifier  {    @ Override public String toString  ( )  {  return 
<<<<<<<
"Soundex"
=======
"SoundexSimplifier"
>>>>>>>
 ; }    @ Override public String simplify  (  String input )  {  return  US_ENGLISH . soundex  ( input ) ; } }