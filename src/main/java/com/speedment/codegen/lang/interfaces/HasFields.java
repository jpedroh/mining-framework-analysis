  package     com . speedment . codegen . lang . interfaces ;   import      com . speedment . codegen . lang . models . Field ;  import   java . util . Collection ;  import   java . util . List ;  import    java . util . stream . Stream ;   public interface HasFields  <  T  extends  HasFields  < T > >  {    @ SuppressWarnings  ( "unchecked" ) default T add  (   final Field field )  {    getFields  ( ) . add  (  field . copy  ( ) ) ;  return  ( T ) this ; }    @ SuppressWarnings  ( "unchecked" ) default T addAllFields  (   final  Collection  <  ? extends Field > fields )  {   
<<<<<<<
 getFields  ( )
=======
fields
>>>>>>>
 . 
<<<<<<<
addAll
=======
forEach
>>>>>>>
  ( 
<<<<<<<
fields
=======
 this :: add
>>>>>>>
 ) ;  return  ( T ) this ; }   List  < Field > getFields  ( ) ; }