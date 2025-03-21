  package     org . sonar . php . parser . statement ;   import   org . junit . Test ;  import     org . sonar . php . parser . PHPLexicalGrammar ;  import static      org . sonar . php . utils . Assertions . assertThat ;   public class StatementTest  {    @ Test public void test  ( )  {                                  
<<<<<<<
  assertThat  (  PHPLexicalGrammar . STATEMENT ) . matches  ( "{}" )
=======
assertThat
>>>>>>>
 . matches  ( 
<<<<<<<
"label:"
=======
 PHPLexicalGrammar . STATEMENT
>>>>>>>
 ) . matches  ( 
<<<<<<<
"if ($a): endif;"
=======
"{}"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"while($a) {}"
=======
"label:"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"for ($i = 1; $i <= 10; $i++) {}"
=======
"if ($a): endif;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"switch ($a) {}"
=======
"while($a) {}"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"break;"
=======
"for ($i = 1; $i <= 10; $i++) {}"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"continue;"
=======
"switch ($a) {}"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"return;"
=======
"break;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
";"
=======
"continue;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"yield $a;"
=======
"return;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"[$a, &$b] = $array;"
=======
";"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"list($a, &$b) = $array;"
=======
"yield $a;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"foreach ($array as list(&$a, $b)) { $a = 7; }"
=======
"[$a, &$b] = $array;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"global $a;"
=======
"list($a, &$b) = $array;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"echo \"Hi\";"
=======
"foreach ($array as list(&$a, $b)) { $a = 7; }"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"$a = b'hello';"
=======
"global $a;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"unset($a);"
=======
"echo \"Hi\";"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"yield yield;"
=======
"$a = b'hello';"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"die(yield $foo);"
=======
"unset($a);"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"yield from [yield];"
=======
"yield yield;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"list($value) = yield;"
=======
"die(yield $foo);"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"var_dump(yield * -1);"
=======
"yield from [yield];"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"var_dump([yield \"k\" => \"a\" . \"b\"]);"
=======
"list($value) = yield;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"$$varName = yield;"
=======
"var_dump(yield * -1);"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"$gen = yield;"
=======
"var_dump([yield \"k\" => \"a\" . \"b\"]);"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"$var = function () {};"
=======
"$$varName = yield;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"foo();"
=======
"$gen = yield;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"Foo::bar();"
=======
"$var = function () {};"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"'Foo::bar'();"
=======
"foo();"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"['Foo','bar']();"
=======
"Foo::bar();"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"[A::class, $method_name]();"
=======
"'Foo::bar'();"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"null();"
=======
"['Foo','bar']();"
>>>>>>>
 ) ; }    @ Test public void optional_semicolon  ( )  {    assertThat  (  PHPLexicalGrammar . STATEMENT ) . matches  ( "continue ?>" ) ; }    @ Test public void top_statement  ( )  {    assertThat  (  PHPLexicalGrammar . TOP_STATEMENT ) . matches  ( "__halt_compiler();" ) ; } }