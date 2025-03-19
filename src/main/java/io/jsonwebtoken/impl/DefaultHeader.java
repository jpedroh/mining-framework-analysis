  package   io . jsonwebtoken . impl ;   import   io . jsonwebtoken . Header ;  import    io . jsonwebtoken . lang . Strings ;  import   java . util . Map ;    @ SuppressWarnings  ( "unchecked" ) public class DefaultHeader  <  T  extends  Header  < T > >  extends JwtMap  implements   Header  < T >  {   public DefaultHeader  ( )  {  super  ( ) ; }   public DefaultHeader  (   Map  < String , Object > map )  {  super  ( map ) ; }    @ Override public String getType  ( )  {  return  getString  ( TYPE ) ; }    @ Override public T setType  (  String typ )  {   setValue  ( TYPE , typ ) ;  return  ( T ) this ; }    @ Override public String getContentType  ( )  {  return  getString  ( CONTENT_TYPE ) ; }    @ Override public T setContentType  (  String cty )  {   setValue  ( CONTENT_TYPE , cty ) ;  return  ( T ) this ; }    @ SuppressWarnings  ( "deprecation" )  @ Override public String getCompressionAlgorithm  ( )  {  String 
<<<<<<<
 s =  getString  ( COMPRESSION_ALGORITHM )
=======
 alg =  getString  ( COMPRESSION_ALGORITHM )
>>>>>>>
 ;  if  (  !  Strings . hasText  ( 
<<<<<<<
s
=======
alg
>>>>>>>
 ) )  {   
<<<<<<<
s
=======
alg
>>>>>>>
 =  getString  ( DEPRECATED_COMPRESSION_ALGORITHM ) ; }  return 
<<<<<<<
s
=======
alg
>>>>>>>
 ; }    @ Override public T setCompressionAlgorithm  (  String compressionAlgorithm )  {   setValue  ( COMPRESSION_ALGORITHM , compressionAlgorithm ) ;  return  ( T ) this ; } }