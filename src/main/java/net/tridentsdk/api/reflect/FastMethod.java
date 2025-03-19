  package    net . tridentsdk . api . reflect ;   import    com . esotericsoftware . reflectasm . MethodAccess ;  import     net . tridentsdk . api . docs . InternalUseOnly ;   public class FastMethod  {   private final MethodAccess  access ;   private final String  name ;   private final Object  instance ;    @ InternalUseOnly public FastMethod  (  Object instance ,  MethodAccess access ,  String name )  {    this . access = access ;    this . name = name ;    this . instance = instance ; } 
<<<<<<<
  public Object invoke  (  Object instance )  {  return   this . access . invoke  ( instance ,  this . name ) ; }
=======
>>>>>>>
   public Object getInstance  ( )  {  return instance ; }   public Object invoke  (  Object ...  args )  {  return   this . access . invoke  (  this . instance ,  this . name , args ) ; }   public Object invoke  ( )  {  return   this . access . invoke  (  this . instance ,  this . name ) ; } }