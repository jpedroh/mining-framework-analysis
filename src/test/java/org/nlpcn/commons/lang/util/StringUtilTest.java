  package     org . nlpcn . commons . lang . util ;   import   org . junit . Test ;   public class StringUtilTest  {    @ Test public void test  ( )  {    System . out . println  (  StringUtil . isBlank  ( " \t" ) ) ;    System . out . println  (  StringUtil . rmHtmlTag  ( 
<<<<<<<
"<a>hello ansj</a>my name is "
=======
"hello ansj hello kk "
>>>>>>>
 ) ) ;    System . out . println  (  StringUtil . makeSqlInString  ( "ansj,2134,123,123,123" ) ) ; } }