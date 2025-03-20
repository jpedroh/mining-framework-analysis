  package    org . assertj . core . api ;   import static      org . assertj . core . util . Sets . newLinkedHashSet ;  import static    org . junit . Assert . assertNotNull ;  import static    org . junit . Assert . assertSame ;  import   org . junit . Test ;   public class Assertions_assertThat_with_Iterable_Test  {    @ Test public void should_create_Assert  ( )  { 
<<<<<<<
=======
  AbstractIterableAssert  <  ? ,  ? extends  Iterable  <  ? > , Object >  assertions =  Assertions . assertThat  (  newLinkedHashSet  ( ) ) ;
>>>>>>>
   assertNotNull  (  Assertions . assertThat  (  newLinkedHashSet  ( ) ) ) ; }    @ Test public void should_pass_actual  ( )  {   Iterable  < String >  names =  newLinkedHashSet  ( "Luke" ) ; 
<<<<<<<
=======
  AbstractIterableAssert  <  ? ,  ? extends  Iterable  <  ? extends String > , String >  assertions =  Assertions . assertThat  ( names ) ;
>>>>>>>
   assertSame  ( names ,   Assertions . assertThat  ( names ) . actual ) ; } }