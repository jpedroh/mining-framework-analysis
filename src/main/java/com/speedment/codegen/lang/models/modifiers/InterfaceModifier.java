  package      com . speedment . codegen . lang . models . modifiers ;   import        com . speedment . codegen . lang . models . modifiers . Keyword . public_ ;   public interface InterfaceModifier  <  T  extends  InterfaceModifier  < T > >  extends   public_  < T >  { 
<<<<<<<
=======
   @ SuppressWarnings  ( "unchecked" ) default T public_  ( )  {    getModifiers  ( ) . add  ( PUBLIC ) ;  return  ( T ) this ; }
>>>>>>>
 }