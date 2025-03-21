  package     com . speedment . codegen . lang . interfaces ;   import      com . speedment . codegen . lang . models . Initalizer ;  import   java . util . List ;  import   java . util . Collection ;   public interface HasInitalizers  <  T  extends  HasInitalizers  < T > >  {    @ SuppressWarnings  ( "unchecked" ) default T add  (   final Initalizer 
<<<<<<<
init
=======
initalizer
>>>>>>>
 )  {    getInitalizers  ( ) . add  ( 
<<<<<<<
init
=======
 initalizer . copy  ( )
>>>>>>>
 ) ;  return  ( T ) this ; }   List  < Initalizer > getInitalizers  ( ) ;    @ SuppressWarnings  ( "unchecked" ) default T addAllInitalizers  (   final  Collection  <  ? extends Initalizer > initalizers )  {   initalizers . forEach  (  this :: add ) ;  return  ( T ) this ; } }