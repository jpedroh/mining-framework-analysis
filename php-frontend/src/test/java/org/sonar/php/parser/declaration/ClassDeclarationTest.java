  package     org . sonar . php . parser . declaration ;   import   org . junit . Test ;  import     org . sonar . php . parser . PHPLexicalGrammar ;  import static      org . sonar . php . utils . Assertions . assertThat ;   public class ClassDeclarationTest  {    @ Test public void test  ( )  {           assertThat  (  PHPLexicalGrammar . CLASS_DECLARATION ) . matches  ( "class C {}" ) . matches  ( 
<<<<<<<
"class match {}"
=======
"abstract class C {}"
>>>>>>>
 ) . matches  ( "final class C {}" ) . matches  ( "class C extends A {}" ) . matches  ( "class C implements B {}" ) . matches  ( "class C extends A implements B {}" ) . matches  ( "#[A1(1)] class C {}" ) . notMatches  ( "class A extends B, C {}" ) ; } }