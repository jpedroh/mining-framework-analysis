  package    org . omnifaces . component . output ;   import static     org . omnifaces . util . Utils . isEmpty ;  import   java . io . IOException ;  import    javax . faces . component . FacesComponent ;  import    javax . faces . context . FacesContext ;  import    javax . faces . context . ResponseWriter ;  import    org . omnifaces . util . State ;    @ FacesComponent  (  ConditionalComment . COMPONENT_TYPE ) public class ConditionalComment  extends OutputFamily  {   public static final String  COMPONENT_TYPE = "org.omnifaces.component.output.ConditionalComment" ;   private static final String  ERROR_MISSING_IF = "ConditionalComment attribute 'if' must be specified." ;   private enum PropertyKeys  {  IF  ( "if" )  ;    @ Override public String toString  ( )  {  return   name  ( ) . toLowerCase  ( ) ; } }   private final State  state =  new State  (  getStateHelper  ( ) ) ;    @ Override public void encodeBegin  (  FacesContext context )  throws IOException  {  String 
<<<<<<<
 _if =  getIf  ( )
=======
 condition =  getIf  ( )
>>>>>>>
 ;  if  (  isEmpty  ( 
<<<<<<<
_if
=======
condition
>>>>>>>
 ) )  {  throw  new IllegalArgumentException  ( ERROR_MISSING_IF ) ; }  ResponseWriter  writer =  context . getResponseWriter  ( ) ;   writer . write  ( "<!--[if " ) ;   writer . write  ( 
<<<<<<<
_if
=======
condition
>>>>>>>
 ) ;   writer . write  ( "]>" ) ; }    @ Override public void encodeEnd  (  FacesContext context )  throws IOException  {  ResponseWriter  writer =  context . getResponseWriter  ( ) ;   writer . write  ( "<![endif]-->" ) ; }   public String getIf  ( )  {  return  state . get  (  PropertyKeys . IF ) ; }   public void setIf  (  String condition )  {   state . put  (  PropertyKeys . IF , condition ) ; } }