  package      com . speedment . codegen . lang . models . modifiers ;   import        com . speedment . codegen . lang . models . modifiers . Keyword . private_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . protected_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . public_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . static_ ;   public interface EnumModifier  <  T  extends  EnumModifier  < T > >  extends   public_  < T > ,  protected_  < T > ,  private_  < T > ,  static_  < T >  { 
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
 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T static_  ( )  {    getModifiers  ( ) . add  ( STATIC ) ;  return  ( T ) this ; }
>>>>>>>
 }