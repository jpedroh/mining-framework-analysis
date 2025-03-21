  package    com . speedment . codegen . base ;   import   java . util . Collection ;  import   java . util . Optional ;  import    java . util . stream . Stream ;   public interface Generator  {  DependencyManager getDependencyMgr  ( ) ;  RenderStack getRenderStack  ( ) ;   <  A ,  B >  Stream  <  Meta  < A , B > > metaOn  (  A from ,   Class  < B > to ) ;   default  <  A ,  B >  Stream  <  Meta  < A , B > > metaOn  (  A from ,   Class  < B > to ,   Class  <  ? extends  Transform  < A , B > > transform )  {  return   metaOn  ( from , to ) . filter  (  meta ->  transform . equals  (   meta . getTransform  ( ) . getClass  ( ) ) ) ; }   default  <  M >  Stream  <  Meta  < M , String > > metaOn  (  M model )  {  return  metaOn  ( model ,  String . class ) ; }   default  <  A >  Stream  <  Meta  < A , String > > metaOn  (   Collection  < A > models )  {  return    models . stream  ( ) . map  (  model ->  metaOn  ( model ) ) . flatMap  (  m -> m ) ; }   default  <  A ,  B >  Stream  <  Meta  < A , B > > metaOn  (   Collection  < A > models ,   Class  < B > to )  {  return    models . stream  ( ) . map  (  model ->  metaOn  ( model , to ) ) . flatMap  (  m -> m ) ; }   default  <  A ,  B >  Stream  <  Meta  < A , B > > metaOn  (   Collection  < A > models ,   Class  < B > to ,   Class  <  ? extends  Transform  < A , B > > transform )  {  return   metaOn  ( models , to ) . filter  (  meta ->  
<<<<<<<
 meta . getTransform  ( )
=======
transform
>>>>>>>
 . 
<<<<<<<
is
=======
equals
>>>>>>>
  ( 
<<<<<<<
transform
=======
  meta . getTransform  ( ) . getClass  ( )
>>>>>>>
 ) ) ; }   default  Optional  < String > on  (  Object model )  {  if  (  model instanceof Optional )  {   final  Optional  <  ? >  result =  (  Optional  <  ? > ) model ;  if  (  result . isPresent  ( ) )  {   model =  result . get  ( ) ; } else  {  return  Optional . empty  ( ) ; } }  return    metaOn  ( model ) . map  (  c ->  c . getResult  ( ) ) . findAny  ( ) ; }   default  <  M >  Stream  < String > onEach  (   Collection  < M > models )  {  return   metaOn  ( models ) . map  (  c ->  c . getResult  ( ) ) ; }   <  A ,  B >  Optional  <  Meta  < A , B > > transform  (   Transform  < A , B > transform ,  A model ,  TransformFactory factory ) ; }