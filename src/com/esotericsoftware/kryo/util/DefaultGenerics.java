  package    com . esotericsoftware . kryo . util ;   import    com . esotericsoftware . kryo . Kryo ;  import    java . lang . reflect . Type ;  import    java . lang . reflect . TypeVariable ;   public final class DefaultGenerics  extends BaseGenerics  {   private  int  argumentsSize ;   private  Type  [ ]  arguments =  new Type  [ 16 ] ;   public DefaultGenerics  (  Kryo kryo )  {  super  ( kryo ) ; }    @ Override public  int pushTypeVariables  (  GenericsHierarchy hierarchy ,   GenericType  [ ] args )  {  if  (     hierarchy . total == 0 ||   hierarchy . rootTotal >  args . length ||   args . length >   hierarchy . counts . length )  return 0 ;   int  startSize =  this . argumentsSize ;   int  sizeNeeded =  startSize +  hierarchy . total ;  if  (  sizeNeeded >  arguments . length )  {   Type  [ ]  newArray =  new Type  [  Math . max  ( sizeNeeded ,   arguments . length << 1 ) ] ;   System . arraycopy  ( arguments , 0 , newArray , 0 , startSize ) ;   arguments = newArray ; }    int  [ ]  counts =  hierarchy . counts ;   TypeVariable  [ ]  params =  hierarchy . parameters ;  for (   int  i = 0 ,  p = 0 ,  n =  args . length ;  i < n ;  i ++ )  {  GenericType  arg =  args [ i ] ;  Class  resolved =  arg . resolve  ( this ) ;  if  (  resolved == null )  continue ;   int  count =  counts [ i ] ;  if  (  arg == null )   p += count ; else  {  for (   int  nn =  p + count ;  p < nn ;  p ++ )  {    arguments [ argumentsSize ] =  params [ p ] ;    arguments [  argumentsSize + 1 ] = resolved ;   argumentsSize += 2 ; } } }  return  argumentsSize - startSize ; }    @ Override public void popTypeVariables  (   int count )  {   int  n = argumentsSize ,  i =  n - count ;   argumentsSize = i ;  while  (  i < n )    arguments [  i ++ ] = null ; }    @ Override public Class resolveTypeVariable  (  TypeVariable typeVariable )  {  for (   int  i =  argumentsSize - 2 ;  i >= 0 ;  i -= 2 )  {   final Type  arg =  arguments [ i ] ;  if  (   arg == typeVariable ||  arg . equals  ( typeVariable ) )  return  ( Class )  arguments [  i + 1 ] ; }  return null ; }   public String toString  ( )  {  StringBuilder  buffer =  new StringBuilder  ( ) ;  for (   int  i = 0 ;  i < argumentsSize ;  i += 2 )  {  if  (  i != 0 )   buffer . append  ( ", " ) ;   buffer . append  (   (  ( TypeVariable )  arguments [ i ] ) . getName  ( ) ) ;   buffer . append  ( "=" ) ;   buffer . append  (   (  ( Class )  arguments [  i + 1 ] ) . getSimpleName  ( ) ) ; }  return  buffer . toString  ( ) ; } }