  package      com . speedment . codegen . lang . models . modifiers ;   import        com . speedment . codegen . lang . models . modifiers . Keyword . abstract_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . final_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . private_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . protected_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . public_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . static_ ;  import        com . speedment . codegen . lang . models . modifiers . Keyword . strictfp_ ;   public interface ClassModifier  <  T  extends  ClassModifier  < T > >  extends   public_  < T > ,  protected_  < T > ,  private_  < T > ,  abstract_  < T > ,  static_  < T > ,  final_  < T > ,  strictfp_  < T >  { 
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
   @ SuppressWarnings  ( "unchecked" ) default T abstract_  ( )  {    getModifiers  ( ) . add  ( ABSTRACT ) ;  return  ( T ) this ; }
>>>>>>>
 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T static_  ( )  {    getModifiers  ( ) . add  ( STATIC ) ;  return  ( T ) this ; }
>>>>>>>
 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T final_  ( )  {    getModifiers  ( ) . add  ( FINAL ) ;  return  ( T ) this ; }
>>>>>>>
 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T strictfp_  ( )  {    getModifiers  ( ) . add  ( STRICTFP ) ;  return  ( T ) this ; }
>>>>>>>
 }