  package     com . speedment . codegen . java . views ;   import     com . speedment . codegen . base . CodeView ;  import      com . speedment . codegen . lang . models . AnnotationUsage ;  import   java . util . Optional ;  import static    com . speedment . codegen . Formatting .  * ;  import     com . speedment . codegen . base . CodeGenerator ;  import    com . speedment . util . CodeCombiner ;  import    java . util . stream . Stream ;   public class AnnotationUsageView  implements   CodeView  < AnnotationUsage >  {   private final static String  PSTART = "(" ,  EQUALS = " = " ;    @ Override public  Optional  < String > render  (  CodeGenerator cg ,  AnnotationUsage model )  {   final  Optional  < String >  value =  cg . on  (  model . getValue  ( ) ) ;   final  Stream  < String >  valueStream =   value . isPresent  ( ) ?  Stream . of  (  value . get  ( ) ) :  Stream . empty  ( ) ;  return  Optional . of  (   AT +   cg . on  (  model . getType  ( ) ) . get  ( ) +   
<<<<<<<
cg
=======
 Stream . of  (    model . getValues  ( ) . stream  ( ) . map  (  e ->   e . getKey  ( ) +    cg . on  (  e . getValue  ( ) ) . map  (  s ->  EQUALS + s ) . orElse  ( EMPTY ) ) , valueStream )
>>>>>>>
 . 
<<<<<<<
on
=======
flatMap
>>>>>>>
  ( 
<<<<<<<
 model . getValue  ( )
=======
 s -> s
>>>>>>>
 ) . 
<<<<<<<
orElse
=======
collect
>>>>>>>
  ( 
<<<<<<<
EMPTY
=======
>>>>>>>
 ) ) ; } }