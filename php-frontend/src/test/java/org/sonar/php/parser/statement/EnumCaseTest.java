  package     org . sonar . php . parser . statement ;   import   org . junit . Test ;  import     org . sonar . php . parser . PHPLexicalGrammar ;  import static      org . sonar . php . utils . Assertions . assertThat ;   public class EnumCaseTest  {    @ Test public void test  ( )  {       
<<<<<<<
assertThat
=======
  assertThat  (  PHPLexicalGrammar . ENUM_CASE ) . matches  ( "case A;" )
>>>>>>>
 . matches  ( 
<<<<<<<
 PHPLexicalGrammar . ENUM_CASE
=======
"#[A1(1)] case A;"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"case A;"
=======
"case A = 'A';"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"#[A1(1)] case A;"
=======
"case A = 'A' . 'B';"
>>>>>>>
 ) . matches  ( 
<<<<<<<
"case Enum;"
=======
"case A = MyClass::CONSTANT;"
>>>>>>>
 ) . notMatches  ( "case A" ) ; } }