  package    org . assertj . core . util ;   import static      org . assertj . core . api . Assertions . assertThat ;  import static      org . assertj . core . api . Assertions . assertThatThrownBy ;  import static       org . assertj . core . util . introspection . Introspection . getPropertyGetter ;  import    java . lang . reflect . Method ;  import      org . assertj . core . util . introspection . IntrospectionError ;  import   org . junit . Before ;  import   org . junit . Test ;  import static      org . assertj . core . test . ExpectedException . none ;  import     org . assertj . core . test . ExpectedException ;  import   org . junit . Rule ;   public class Introspection_getProperty_Test  {   private Employee  judy ;    @ Before public void initData  ( )  {   judy =  new Employee  ( 100000.0 , 31 ) ; }    @ Test public void get_getter_for_property  ( )  {  Method  getter =  getPropertyGetter  ( "age" , judy ) ;    assertThat  ( getter ) . isNotNull  ( ) ; }    @ Test public void should_raise_an_error_because_of_missing_getter  ( )  {   
<<<<<<<
  assertThatThrownBy  (   ( ) ->  getPropertyGetter  ( "salary" , judy ) ) . isInstanceOf  (  IntrospectionError . class )
=======
thrown
>>>>>>>
 . 
<<<<<<<
hasMessage
=======
expect
>>>>>>>
  (  IntrospectionError . class , "No getter for property 'salary' in org.assertj.core.util.Employee" ) ;   getPropertyGetter  ( "salary" , judy ) ; } 
<<<<<<<
   @ Test public void should_raise_an_error_because_of_non_public_getter  ( )  {     assertThatThrownBy  (   ( ) ->  getPropertyGetter  ( "firstJob" , judy ) ) . isInstanceOf  (  IntrospectionError . class ) . hasMessage  ( "No public getter for property 'firstJob' in org.assertj.core.util.Employee" ) ;     assertThatThrownBy  (   ( ) ->  getPropertyGetter  ( "company" , judy ) ) . isInstanceOf  (  IntrospectionError . class ) . hasMessage  ( "No public getter for property 'company' in org.assertj.core.util.Employee" ) ; }
=======
>>>>>>>
    @ Test public void should_raise_an_error_because_of_non_public_getter_when_getter_is_in_superclass  ( )  {   
<<<<<<<
  assertThatThrownBy  (   ( ) ->  getPropertyGetter  ( "name" ,  new Example  ( ) ) ) . isInstanceOf  (  IntrospectionError . class )
=======
thrown
>>>>>>>
 . 
<<<<<<<
hasMessage
=======
expect
>>>>>>>
  (  IntrospectionError . class , "No public getter for property 'name' in org.assertj.core.util.Introspection_getProperty_Test$Example" ) ;   getPropertyGetter  ( "name" ,  new Example  ( ) ) ; }   public static class Example  extends Super  { }   public static class Super  {    @ SuppressWarnings  ( "unused" ) private String getName  ( )  {  return "a" ; } }    @ Rule public ExpectedException  thrown =  none  ( ) ;    @ Test public void should_raise_an_error_because_of_non_public_getter_when_getter_does_not_exists  ( )  {   thrown . expect  (  IntrospectionError . class , "No public getter for property 'company' in org.assertj.core.util.Employee" ) ;   getPropertyGetter  ( "company" , judy ) ; }    @ Test public void should_raise_an_error_because_of_non_public_getter_when_getter_is_package_private  ( )  {   thrown . expect  (  IntrospectionError . class , "No public getter for property 'firstJob' in org.assertj.core.util.Employee" ) ;   getPropertyGetter  ( "firstJob" , judy ) ; } }