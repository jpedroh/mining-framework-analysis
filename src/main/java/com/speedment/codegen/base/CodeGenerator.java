  package    com . speedment . codegen . base ;   import   java . util . Collection ;  import   java . util . List ;  import   java . util . Optional ;  import    java . util . stream . Stream ;   public interface CodeGenerator  {  DependencyManager getDependencyMgr  ( ) ;   List  < Object > getRenderStack  ( ) ;   <  M >  Stream  <  Code  < M > > codeOn  (  M model ) ;   default  <  M >  Stream  <  Code  < M > > codeOn  (   Collection  < M > models )  {  return    models . stream  ( ) . map  (  model ->  codeOn  ( model ) ) . flatMap  (  m -> m ) ; }   default  Optional  < String > on  (  Object model )  {  if  (  model instanceof 
<<<<<<<
 Optional  <  ? >
=======
Optional
>>>>>>>
 )  {  return   (  (  Optional  <  ? > ) model ) . flatMap  (  m ->    codeOn  ( m ) . findAny  ( ) . map  (  c ->  c . getText  ( ) ) ) ; } else  { 
<<<<<<<
 return    codeOn  ( model ) . findAny  ( ) . map  (  c ->  c . getText  ( ) ) ;
=======
  final Optional  result =  (  Optional  <  ? > ) model ;
>>>>>>>
  if  (  result . isPresent  ( ) )  {   model =  result . get  ( ) ; } else  {  return  Optional . empty  ( ) ; } } }   default  <  M >  Stream  < String > onEach  (   Collection  < M > models )  {  return   codeOn  ( models ) . map  (  c ->  c . getText  ( ) ) ; } }