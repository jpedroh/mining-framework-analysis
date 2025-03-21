  package     com . speedment . codegen . lang . interfaces ;   import      com . speedment . codegen . lang . models . ClassOrInterface ;  import   java . util . Collection ;  import   java . util . List ;   public interface HasClasses  <  T  extends  HasClasses  < T > >  {    @ SuppressWarnings  ( "unchecked" ) default T add  (   final  ClassOrInterface  <  ? > member )  {    getClasses  ( ) . add  (  member . copy  ( ) ) ;  return  ( T ) this ; }    @ SuppressWarnings  ( "unchecked" ) default T addAllClasses  (   final  Collection  <  ? extends  ClassOrInterface  <  ? > > members )  {   
<<<<<<<
 getClasses  ( )
=======
members
>>>>>>>
 . 
<<<<<<<
addAll
=======
forEach
>>>>>>>
  ( 
<<<<<<<
members
=======
 this :: add
>>>>>>>
 ) ;  return  ( T ) this ; }   List  <  ClassOrInterface  <  ? > > getClasses  ( ) ; }