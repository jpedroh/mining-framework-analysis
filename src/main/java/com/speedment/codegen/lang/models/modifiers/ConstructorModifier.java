  package      com . speedment . codegen . lang . models . modifiers ;   import        com . speedment . codegen . lang . models . modifiers . Keyword . private_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . protected_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . public_ ;   public interface ConstructorModifier  <  T  extends  ConstructorModifier  < T > >  extends   public_  < T > ,  protected_  < T > ,  private_  < T >  { 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T public_  ( )  {    getModifiers  ( ) . add  ( PUBLIC ) ;  return  ( T ) this ; }
>>>>>>>
 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T protected_  ( )  {    getModifiers  ( ) . add  ( PROTECTED ) ;  return  ( T ) this ; }
>>>>>>>
 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T private_  ( )  {    getModifiers  ( ) . add  ( PRIVATE ) ;  return  ( T ) this ; }
>>>>>>>
 }