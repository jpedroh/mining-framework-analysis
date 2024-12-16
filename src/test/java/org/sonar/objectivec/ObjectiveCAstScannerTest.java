  package   org . sonar . objectivec ;   import static    org . hamcrest . Matchers . hasItem ;  import static    org . hamcrest . Matchers . is ;  import static    org . junit . Assert . assertThat ;  import   java . io . File ;  import   org . junit . Test ;  import     org . sonar . objectivec . api . ObjectiveCMetric ;  import     org . sonar . squidbridge . api . SourceFile ;   public class ObjectiveCAstScannerTest  { 
<<<<<<<
   @ Test public void comments  ( )  {  SourceFile  file =  ObjectiveCAstScanner . scanSingleFile  (  new File  ( "src/test/resources/objcSample.h" ) ) ;   assertThat  (  file . getInt  (  ObjectiveCMetric . COMMENT_LINES ) ,  is  ( 4 ) ) ;   assertThat  (  file . getNoSonarTagLines  ( ) ,  hasItem  ( 10 ) ) ;   assertThat  (   file . getNoSonarTagLines  ( ) . size  ( ) ,  is  ( 1 ) ) ; }
=======
>>>>>>>
 }