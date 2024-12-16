  package    com . datastax . driver . core ;   import   java . math . BigInteger ;  import   java . nio . ByteBuffer ;  import   java . security . MessageDigest ;  import   java . security . NoSuchAlgorithmException ;  import   java . util . Arrays ;  import   java . util . List ;  import     com . google . common . annotations . VisibleForTesting ;  import     com . google . common . collect . Lists ;  import     com . google . common . primitives . UnsignedBytes ;  import      com . datastax . driver . core . utils . Bytes ;   public abstract class Token  implements   Comparable  < Token >  {   public abstract DataType getType  ( ) ;   public abstract Object getValue  ( ) ;   static  Token . Factory getFactory  (  String partitionerName )  {  if  (  partitionerName . endsWith  ( "Murmur3Partitioner" ) )  return  M3PToken . FACTORY ; else  if  (  partitionerName . endsWith  ( "RandomPartitioner" ) )  return  RPToken . FACTORY ; else  if  (  partitionerName . endsWith  ( "OrderedPartitioner" ) )  return  OPPToken . FACTORY ; else  return null ; }   static abstract class Factory  {   abstract Token fromString  (  String tokenStr ) ;   abstract DataType getTokenType  ( ) ;   abstract Token deserialize  (  ByteBuffer buffer ,  ProtocolVersion protocolVersion ) ;   abstract Token minToken  ( ) ;   abstract Token hash  (  ByteBuffer partitionKey ) ;   abstract  List  < Token > split  (  Token startToken ,  Token endToken ,   int numberOfSplits ) ;   protected  List  < BigInteger > split  (  BigInteger start ,  BigInteger range ,  BigInteger ringEnd ,  BigInteger ringLength ,   int numberOfSplits )  {   BigInteger  [ ]  tmp =  range . divideAndRemainder  (  BigInteger . valueOf  ( numberOfSplits ) ) ;  BigInteger  divider =  tmp [ 0 ] ;   int  remainder =   tmp [ 1 ] . intValue  ( ) ;   List  < BigInteger >  results =  Lists . newArrayListWithExpectedSize  (  numberOfSplits - 1 ) ;  BigInteger  current = start ;  BigInteger  dividerPlusOne =   (  remainder == 0 ) ? null :  divider . add  (  BigInteger . ONE ) ;  for (   int  i = 1 ;  i < numberOfSplits ;  i ++ )  {   current =  current . add  (    remainder -- > 0 ? dividerPlusOne : divider ) ;  if  (   ringEnd != null &&   current . compareTo  ( ringEnd ) > 0 )   current =  current . subtract  ( ringLength ) ;   results . add  ( current ) ; }  return results ; } }   static class M3PToken  extends Token  {   private final  long  value ;   public static final Factory  FACTORY =  new M3PTokenFactory  ( ) ;   private static class M3PTokenFactory  extends Factory  {   private static final BigInteger  RING_END =  BigInteger . valueOf  (  Long . MAX_VALUE ) ;   private static final BigInteger  RING_LENGTH =  RING_END . subtract  (  BigInteger . valueOf  (  Long . MIN_VALUE ) ) ;   static final M3PToken  MIN_TOKEN =  new M3PToken  (  Long . MIN_VALUE ) ;   static final M3PToken  MAX_TOKEN =  new M3PToken  (  Long . MAX_VALUE ) ;   private  long getblock  (  ByteBuffer key ,   int offset ,   int index )  {   int  i_8 =  index << 3 ;   int  blockOffset =  offset + i_8 ;  return         (   (  long )  key . get  (  blockOffset + 0 ) & 0xff ) +  (   (   (  long )  key . get  (  blockOffset + 1 ) & 0xff ) << 8 ) +  (   (   (  long )  key . get  (  blockOffset + 2 ) & 0xff ) << 16 ) +  (   (   (  long )  key . get  (  blockOffset + 3 ) & 0xff ) << 24 ) +  (   (   (  long )  key . get  (  blockOffset + 4 ) & 0xff ) << 32 ) +  (   (   (  long )  key . get  (  blockOffset + 5 ) & 0xff ) << 40 ) +  (   (   (  long )  key . get  (  blockOffset + 6 ) & 0xff ) << 48 ) +  (   (   (  long )  key . get  (  blockOffset + 7 ) & 0xff ) << 56 ) ; }   private  long rotl64  (   long v ,   int n )  {  return  (   (  v << n ) |  (  v >>>  (  64 - n ) ) ) ; }   private  long fmix  (   long k )  {   k ^=  k >>> 33 ;   k *= 0xff51afd7ed558ccdL ;   k ^=  k >>> 33 ;   k *= 0xc4ceb9fe1a85ec53L ;   k ^=  k >>> 33 ;  return k ; }    @ SuppressWarnings  ( "fallthrough" ) private  long murmur  (  ByteBuffer data )  {   int  offset =  data . position  ( ) ;   int  length =  data . remaining  ( ) ;   int  nblocks =  length >> 4 ;   long  h1 = 0 ;   long  h2 = 0 ;   long  c1 = 0x87c37b91114253d5L ;   long  c2 = 0x4cf5ad432745937fL ;  for (   int  i = 0 ;  i < nblocks ;  i ++ )  {   long  k1 =  getblock  ( data , offset ,   i * 2 + 0 ) ;   long  k2 =  getblock  ( data , offset ,   i * 2 + 1 ) ;   k1 *= c1 ;   k1 =  rotl64  ( k1 , 31 ) ;   k1 *= c2 ;   h1 ^= k1 ;   h1 =  rotl64  ( h1 , 27 ) ;   h1 += h2 ;   h1 =   h1 * 5 + 0x52dce729 ;   k2 *= c2 ;   k2 =  rotl64  ( k2 , 33 ) ;   k2 *= c1 ;   h2 ^= k2 ;   h2 =  rotl64  ( h2 , 31 ) ;   h2 += h1 ;   h2 =   h2 * 5 + 0x38495ab5 ; }   offset +=  nblocks * 16 ;   long  k1 = 0 ;   long  k2 = 0 ;  switch  (  length & 15 )  {   case 15 :   k2 ^=   (  (  long )  data . get  (  offset + 14 ) ) << 48 ;   case 14 :   k2 ^=   (  (  long )  data . get  (  offset + 13 ) ) << 40 ;   case 13 :   k2 ^=   (  (  long )  data . get  (  offset + 12 ) ) << 32 ;   case 12 :   k2 ^=   (  (  long )  data . get  (  offset + 11 ) ) << 24 ;   case 11 :   k2 ^=   (  (  long )  data . get  (  offset + 10 ) ) << 16 ;   case 10 :   k2 ^=   (  (  long )  data . get  (  offset + 9 ) ) << 8 ;   case 9 :   k2 ^=   (  (  long )  data . get  (  offset + 8 ) ) << 0 ;   k2 *= c2 ;   k2 =  rotl64  ( k2 , 33 ) ;   k2 *= c1 ;   h2 ^= k2 ;   case 8 :   k1 ^=   (  (  long )  data . get  (  offset + 7 ) ) << 56 ;   case 7 :   k1 ^=   (  (  long )  data . get  (  offset + 6 ) ) << 48 ;   case 6 :   k1 ^=   (  (  long )  data . get  (  offset + 5 ) ) << 40 ;   case 5 :   k1 ^=   (  (  long )  data . get  (  offset + 4 ) ) << 32 ;   case 4 :   k1 ^=   (  (  long )  data . get  (  offset + 3 ) ) << 24 ;   case 3 :   k1 ^=   (  (  long )  data . get  (  offset + 2 ) ) << 16 ;   case 2 :   k1 ^=   (  (  long )  data . get  (  offset + 1 ) ) << 8 ;   case 1 :   k1 ^=  (  (  long )  data . get  ( offset ) ) ;   k1 *= c1 ;   k1 =  rotl64  ( k1 , 31 ) ;   k1 *= c2 ;   h1 ^= k1 ; } ;   h1 ^= length ;   h2 ^= length ;   h1 += h2 ;   h2 += h1 ;   h1 =  fmix  ( h1 ) ;   h2 =  fmix  ( h2 ) ;   h1 += h2 ;   h2 += h1 ;  return h1 ; }    @ Override M3PToken fromString  (  String tokenStr )  {  return  new M3PToken  (  Long . parseLong  ( tokenStr ) ) ; }    @ Override DataType getTokenType  ( )  {  return  DataType . bigint  ( ) ; }    @ Override Token deserialize  (  ByteBuffer buffer ,  ProtocolVersion protocolVersion )  {  return  new M3PToken  (  ( Long )   getTokenType  ( ) . deserialize  ( buffer , protocolVersion ) ) ; }    @ Override Token minToken  ( )  {  return MIN_TOKEN ; }    @ Override M3PToken hash  (  ByteBuffer partitionKey )  {   long  v =  murmur  ( partitionKey ) ;  return  new M3PToken  (   v ==  Long . MIN_VALUE ?  Long . MAX_VALUE : v ) ; }    @ Override  List  < Token > split  (  Token startToken ,  Token endToken ,   int numberOfSplits )  {  if  (   startToken . equals  ( endToken ) &&  startToken . equals  ( MIN_TOKEN ) )   endToken = MAX_TOKEN ;  BigInteger  start =  BigInteger . valueOf  (   (  ( M3PToken ) startToken ) . value ) ;  BigInteger  end =  BigInteger . valueOf  (   (  ( M3PToken ) endToken ) . value ) ;  BigInteger  range =  end . subtract  ( start ) ;  if  (   range . compareTo  (  BigInteger . ZERO ) < 0 )   range =  range . add  ( RING_LENGTH ) ;   List  < BigInteger >  values =  super . split  ( start , range , RING_END , RING_LENGTH , numberOfSplits ) ;   List  < Token >  tokens =  Lists . newArrayListWithExpectedSize  (  values . size  ( ) ) ;  for ( BigInteger value : values )   tokens . add  (  new M3PToken  (  value . longValue  ( ) ) ) ;  return tokens ; } }   private M3PToken  (   long value )  {    this . value = value ; }    @ Override public DataType getType  ( )  {  return  FACTORY . getTokenType  ( ) ; }    @ Override public Object getValue  ( )  {  return value ; }    @ Override public  int compareTo  (  Token other )  {  assert  other instanceof M3PToken ;   long  otherValue =   (  ( M3PToken ) other ) . value ;  return   value < otherValue ?  - 1 :   (  value == otherValue ) ? 0 : 1 ; }    @ Override public boolean equals  (  Object obj )  {  if  (  this == obj )  return true ;  if  (   obj == null ||   this . getClass  ( ) !=  obj . getClass  ( ) )  return false ;  return  value ==   (  ( M3PToken ) obj ) . value ; }    @ Override public  int hashCode  ( )  {  return  (  int )  (  value ^  (  value >>> 32 ) ) ; }    @ Override public String toString  ( )  {  return  Long . toString  ( value ) ; } }   static class OPPToken  extends Token  {   private final ByteBuffer  value ;   public static final Factory  FACTORY =  new OPPTokenFactory  ( ) ;   private static class OPPTokenFactory  extends Factory  {   private static final BigInteger  TWO =  BigInteger . valueOf  ( 2 ) ;   private static final Token  MIN_TOKEN =  new OPPToken  (  ByteBuffer . allocate  ( 0 ) ) ;    @ Override public OPPToken fromString  (  String tokenStr )  {  if  (  !  tokenStr . startsWith  ( "0x" ) )  {  String  prefix =   (    tokenStr . length  ( ) % 2 == 0 ) ? "0x" : "0x0" ;   tokenStr =  prefix + tokenStr ; }  ByteBuffer  value =  Bytes . fromHexString  ( tokenStr ) ;  return  new OPPToken  ( value ) ; }    @ Override DataType getTokenType  ( )  {  return  DataType . blob  ( ) ; }    @ Override Token deserialize  (  ByteBuffer buffer ,  ProtocolVersion protocolVersion )  {  return  new OPPToken  ( buffer ) ; }    @ Override Token minToken  ( )  {  return MIN_TOKEN ; }    @ Override OPPToken hash  (  ByteBuffer partitionKey )  {  return  new OPPToken  ( partitionKey ) ; }    @ Override  List  < Token > split  (  Token startToken ,  Token endToken ,   int numberOfSplits )  {   int  tokenOrder =  startToken . compareTo  ( endToken ) ;  if  (   tokenOrder == 0 &&  startToken . equals  ( MIN_TOKEN ) )  throw  new IllegalArgumentException  ( "Cannot split whole ring with ordered partitioner" ) ;  OPPToken  oppStartToken =  ( OPPToken ) startToken ;  OPPToken  oppEndToken =  ( OPPToken ) endToken ;   int  significantBytes ;  BigInteger  start ,  end ,  range ,  ringEnd ,  ringLength ;  BigInteger  bigNumberOfSplits =  BigInteger . valueOf  ( numberOfSplits ) ;  if  (  tokenOrder < 0 )  {   significantBytes =  Math . max  (   oppStartToken . value . capacity  ( ) ,   oppEndToken . value . capacity  ( ) ) ;   int  addedBytes = 0 ;  while  ( true )  {   start =  toBigInteger  (  oppStartToken . value , significantBytes ) ;   end =  toBigInteger  (  oppEndToken . value , significantBytes ) ;   range =  end . subtract  ( start ) ;  if  (   addedBytes == 4 ||   range . compareTo  ( bigNumberOfSplits ) >= 0 )  break ;   significantBytes += 1 ;   addedBytes += 1 ; }   ringEnd =  ringLength = null ; } else  {   significantBytes =  Math . max  (   oppStartToken . value . capacity  ( ) ,   oppEndToken . value . capacity  ( ) ) ;   int  addedBytes = 0 ;  while  ( true )  {   start =  toBigInteger  (  oppStartToken . value , significantBytes ) ;   end =  toBigInteger  (  oppEndToken . value , significantBytes ) ;   ringLength =  TWO . pow  (  significantBytes * 8 ) ;   ringEnd =  ringLength . subtract  (  BigInteger . ONE ) ;   range =   end . subtract  ( start ) . add  ( ringLength ) ;  if  (   addedBytes == 4 ||   range . compareTo  ( bigNumberOfSplits ) >= 0 )  break ;   significantBytes += 1 ;   addedBytes += 1 ; } }   List  < BigInteger >  values =  super . split  ( start , range , ringEnd , ringLength , numberOfSplits ) ;   List  < Token >  tokens =  Lists . newArrayListWithExpectedSize  (  values . size  ( ) ) ;  for ( BigInteger value : values )   tokens . add  (  new OPPToken  (  toBytes  ( value , significantBytes ) ) ) ;  return tokens ; }   private BigInteger toBigInteger  (  ByteBuffer bb ,   int significantBytes )  {    byte  [ ]  bytes =  Bytes . getArray  ( bb ) ;    byte  [ ]  target ;  if  (  significantBytes !=  bytes . length )  {   target =  new  byte  [ significantBytes ] ;   System . arraycopy  ( bytes , 0 , target , 0 ,  bytes . length ) ; } else   target = bytes ;  return  new BigInteger  ( 1 , target ) ; }   protected ByteBuffer toBytes  (  BigInteger value ,   int significantBytes )  {    byte  [ ]  rawBytes =  value . toByteArray  ( ) ;    byte  [ ]  result ;  if  (   rawBytes . length == significantBytes )   result = rawBytes ; else  {   result =  new  byte  [ significantBytes ] ;   int  start ,  length ;  if  (   rawBytes [ 0 ] == 0 )  {   start = 1 ;   length =   rawBytes . length - 1 ; } else  {   start = 0 ;   length =  rawBytes . length ; }   System . arraycopy  ( rawBytes , start , result ,  significantBytes - length , length ) ; }  return  ByteBuffer . wrap  ( result ) ; } }    @ VisibleForTesting OPPToken  (  ByteBuffer value )  {    this . value =  stripTrailingZeroBytes  ( value ) ; }   private static ByteBuffer stripTrailingZeroBytes  (  ByteBuffer b )  {   byte  result  [ ] =  Bytes . getArray  ( b ) ;   int  zeroIndex =  result . length ;  for (   int  i =   result . length - 1 ;  i > 0 ;  i -- )  {  if  (   result [ i ] == 0 )  {   zeroIndex = i ; } else  {  break ; } }  return  ByteBuffer . wrap  ( result , 0 , zeroIndex ) ; }    @ Override public DataType getType  ( )  {  return  FACTORY . getTokenType  ( ) ; }    @ Override public Object getValue  ( )  {  return value ; }    @ Override public  int compareTo  (  Token other )  {  assert  other instanceof OPPToken ;  return   UnsignedBytes . lexicographicalComparator  ( ) . compare  (  Bytes . getArray  ( value ) ,  Bytes . getArray  (   (  ( OPPToken ) other ) . value ) ) ; }    @ Override public boolean equals  (  Object obj )  {  if  (  this == obj )  return true ;  if  (   obj == null ||   this . getClass  ( ) !=  obj . getClass  ( ) )  return false ;  return  value . equals  (   (  ( OPPToken ) obj ) . value ) ; }    @ Override public  int hashCode  ( )  {  return  value . hashCode  ( ) ; }    @ Override public String toString  ( )  {  return  Bytes . toHexString  ( value ) ; } }   static class RPToken  extends Token  {   private final BigInteger  value ;   public static final Factory  FACTORY =  new RPTokenFactory  ( ) ;   private static class RPTokenFactory  extends Factory  {   private static final BigInteger  MIN_VALUE =   BigInteger . ONE . negate  ( ) ;   private static final BigInteger  MAX_VALUE =   BigInteger . valueOf  ( 2 ) . pow  ( 127 ) ;   private static final BigInteger  RING_LENGTH =  MAX_VALUE . add  (  BigInteger . ONE ) ;   private static final Token  MIN_TOKEN =  new RPToken  ( MIN_VALUE ) ;   private static final Token  MAX_TOKEN =  new RPToken  ( MAX_VALUE ) ;   private BigInteger md5  (  ByteBuffer data )  {  try  {  MessageDigest  digest =  MessageDigest . getInstance  ( "MD5" ) ;   digest . update  (  data . duplicate  ( ) ) ;  return   new BigInteger  (  digest . digest  ( ) ) . abs  ( ) ; }  catch (   NoSuchAlgorithmException e )  {  throw  new RuntimeException  ( "MD5 doesn't seem to be available on this JVM" , e ) ; } }    @ Override RPToken fromString  (  String tokenStr )  {  return  new RPToken  (  new BigInteger  ( tokenStr ) ) ; }    @ Override DataType getTokenType  ( )  {  return  DataType . varint  ( ) ; }    @ Override Token deserialize  (  ByteBuffer buffer ,  ProtocolVersion protocolVersion )  {  return  new RPToken  (  ( BigInteger )   getTokenType  ( ) . deserialize  ( buffer , protocolVersion ) ) ; }    @ Override Token minToken  ( )  {  return MIN_TOKEN ; }    @ Override RPToken hash  (  ByteBuffer partitionKey )  {  return  new RPToken  (  md5  ( partitionKey ) ) ; }    @ Override  List  < Token > split  (  Token startToken ,  Token endToken ,   int numberOfSplits )  {  if  (   startToken . equals  ( endToken ) &&  startToken . equals  ( MIN_TOKEN ) )   endToken = MAX_TOKEN ;  BigInteger  start =   (  ( RPToken ) startToken ) . value ;  BigInteger  end =   (  ( RPToken ) endToken ) . value ;  BigInteger  range =  end . subtract  ( start ) ;  if  (   range . compareTo  (  BigInteger . ZERO ) < 0 )   range =  range . add  ( RING_LENGTH ) ;   List  < BigInteger >  values =  super . split  ( start , range , MAX_VALUE , RING_LENGTH , numberOfSplits ) ;   List  < Token >  tokens =  Lists . newArrayListWithExpectedSize  (  values . size  ( ) ) ;  for ( BigInteger value : values )   tokens . add  (  new RPToken  ( value ) ) ;  return tokens ; } }   private RPToken  (  BigInteger value )  {    this . value = value ; }    @ Override public DataType getType  ( )  {  return  FACTORY . getTokenType  ( ) ; }    @ Override public Object getValue  ( )  {  return value ; }    @ Override public  int compareTo  (  Token other )  {  assert  other instanceof RPToken ;  return  value . compareTo  (   (  ( RPToken ) other ) . value ) ; }    @ Override public boolean equals  (  Object obj )  {  if  (  this == obj )  return true ;  if  (   obj == null ||   this . getClass  ( ) !=  obj . getClass  ( ) )  return false ;  return  value . equals  (   (  ( RPToken ) obj ) . value ) ; }    @ Override public  int hashCode  ( )  {  return  value . hashCode  ( ) ; }    @ Override public String toString  ( )  {  return  value . toString  ( ) ; } } }