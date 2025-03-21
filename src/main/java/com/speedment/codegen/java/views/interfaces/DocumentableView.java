  package      com . speedment . codegen . java . views . interfaces ;   import static     com . speedment . codegen . Formatting . EMPTY ;  import static     com . speedment . codegen . Formatting . nl ;  import     com . speedment . codegen . base . CodeGenerator ;  import     com . speedment . codegen . base . CodeView ;  import      com . speedment . codegen . lang . interfaces . Documentable ;   public interface DocumentableView  <  M  extends  Documentable  < M > >  extends   CodeView  < M >  {   default String renderJavadoc  (  CodeGenerator cg ,  M model )  {  return    cg . on  (  model . getJavadoc  ( ) ) . map  (  
<<<<<<<
jd
=======
j
>>>>>>>
 ->  
<<<<<<<
jd
=======
j
>>>>>>>
 +  nl  ( ) ) . orElse  ( EMPTY ) ; } }