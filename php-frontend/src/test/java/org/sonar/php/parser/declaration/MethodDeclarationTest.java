  package     org . sonar . php . parser . declaration ;   import   org . junit . Test ;  import     org . sonar . php . parser . PHPLexicalGrammar ;  import static      org . sonar . php . utils . Assertions . assertThat ;   public class MethodDeclarationTest  {    @ Test public void test  ( )  throws Exception  {              assertThat  (  PHPLexicalGrammar . METHOD_DECLARATION ) . matches  ( "function f ();" ) . matches  ( "function f () {}" ) . matches  ( "function &f () {}" ) . matches  ( "private function f () {}" ) . matches  ( "protected abstract function f () {}" ) . matches  ( "public static function f () {}" ) . matches  ( "final function f () {}" ) . matches  ( "function f () : bool {}" ) . matches  ( "function f () : ?bool {}" ) . matches  ( "function if() {}" ) . matches  ( 
<<<<<<<
"function match() {}"
=======
"#[A1(4)] public function f() {}"
>>>>>>>
 ) ; }    @ Test public void optional_semicolon  ( )  {     assertThat  (  PHPLexicalGrammar . METHOD_DECLARATION ) . matches  ( "function fun() ?>" ) . notMatches  ( "function fun() ?> <?php {}" ) ; } }