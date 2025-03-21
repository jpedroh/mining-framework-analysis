  package      net . masterthought . cucumber . generators . integrations . helpers ;   import static      org . assertj . core . api . Assertions . assertThat ;   public class OutputAssertion  extends ReportAssertion  {   public void hasMessages  (   String  [ ] messages )  {   WebAssertion  [ ]  outputMessages =  allBySelector  ( 
<<<<<<<
"span"
=======
"p"
>>>>>>>
 ,  WebAssertion . class ) ;    assertThat  ( outputMessages ) . hasSameSizeAs  ( messages ) ;  for (   int  i = 0 ;  i <  messages . length ;  i ++ )  {    assertThat  (   outputMessages [ i ] . text  ( ) ) . isEqualTo  (  messages [ i ] ) ; } } }