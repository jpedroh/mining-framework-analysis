  package   com . googlecode . javaewah32 ;   public final class IteratingBufferedRunningLengthWord32  implements  IteratingRLW32 , Cloneable  {   public IteratingBufferedRunningLengthWord32  (   final EWAHIterator32 iterator )  {    this . iterator = iterator ;    this . brlw =  new BufferedRunningLengthWord32  (   this . iterator . next  ( ) ) ;    this . literalWordStartPosition =    this . iterator . literalWords  ( ) +   this . brlw . literalwordoffset ;    this . buffer =   this . iterator . buffer  ( ) ; }   public IteratingBufferedRunningLengthWord32  (   final EWAHCompressedBitmap32 bitmap )  {  this  (  EWAHIterator32 . getEWAHIterator  ( bitmap ) ) ; }   public void discardFirstWords  (   int x )  {  while  (  x > 0 )  {  if  (    this . brlw . RunningLength > x )  {     this . brlw . RunningLength -= x ;  return ; }   x -=   this . brlw . RunningLength ;     this . brlw . RunningLength = 0 ;   int  toDiscard =   x >   this . brlw . NumberOfLiteralWords ?   this . brlw . NumberOfLiteralWords : x ;    this . literalWordStartPosition += toDiscard ;     this . brlw . NumberOfLiteralWords -= toDiscard ;   x -= toDiscard ;  if  (   (  x > 0 ) ||  (    this . brlw . size  ( ) == 0 ) )  {  if  (  !   this . iterator . hasNext  ( ) )  {  break ; }    this . brlw . reset  (   this . iterator . next  ( ) ) ;    this . literalWordStartPosition =   this . iterator . literalWords  ( ) ; } } }   public  int discharge  (  BitmapStorage32 container ,   int max )  {   int  index = 0 ;  while  (   (  index < max ) &&  (   size  ( ) > 0 ) )  {   int  pl =  getRunningLength  ( ) ;  if  (   index + pl > max )  {   pl =  max - index ; }   container . addStreamOfEmptyWords  (  getRunningBit  ( ) , pl ) ;   index += pl ;   int  pd =  getNumberOfLiteralWords  ( ) ;  if  (   pd + index > max )  {   pd =  max - index ; }   writeLiteralWords  ( pd , container ) ;   discardFirstWords  (  pl + pd ) ;   index += pd ; }  return index ; }   public  int dischargeNegated  (  BitmapStorage32 container ,   int max )  {   int  index = 0 ;  while  (   (  index < max ) &&  (   size  ( ) > 0 ) )  {   int  pl =  getRunningLength  ( ) ;  if  (   index + pl > max )  {   pl =  max - index ; }   container . addStreamOfEmptyWords  (  !  getRunningBit  ( ) , pl ) ;   index += pl ;   int  pd =  getNumberOfLiteralWords  ( ) ;  if  (   pd + index > max )  {   pd =  max - index ; }   writeNegatedLiteralWords  ( pd , container ) ;   discardFirstWords  (  pl + pd ) ;   index += pd ; }  return index ; }   public boolean next  ( )  {  if  (  !   this . iterator . hasNext  ( ) )  {     this . brlw . NumberOfLiteralWords = 0 ;     this . brlw . RunningLength = 0 ;  return false ; }    this . brlw . reset  (   this . iterator . next  ( ) ) ;    this . literalWordStartPosition =   this . iterator . literalWords  ( ) ;  return true ; }   public void dischargeAsEmpty  (  BitmapStorage32 container )  {  while  (   size  ( ) > 0 )  {   container . addStreamOfEmptyWords  ( false ,  size  ( ) ) ;   discardFirstWords  (  size  ( ) ) ; } }   public void discharge  (  BitmapStorage32 container )  {     this . brlw . literalwordoffset =   this . literalWordStartPosition -   this . iterator . literalWords  ( ) ;   discharge  (  this . brlw ,  this . iterator , container ) ; }   public  int getLiteralWordAt  (   int index )  {  return   this . buffer [   this . literalWordStartPosition + index ] ; }   public  int getNumberOfLiteralWords  ( )  {  return   this . brlw . NumberOfLiteralWords ; }   public boolean getRunningBit  ( )  {  return   this . brlw . RunningBit ; }   public  int getRunningLength  ( )  {  return   this . brlw . RunningLength ; }   public  int size  ( )  {  return   this . brlw . size  ( ) ; }   public void writeLiteralWords  (   int numWords ,  BitmapStorage32 container )  {   container . addStreamOfLiteralWords  (  this . buffer ,  this . literalWordStartPosition , numWords ) ; }   public void writeNegatedLiteralWords  (   int numWords ,  BitmapStorage32 container )  {   container . addStreamOfNegatedLiteralWords  (  this . buffer ,  this . literalWordStartPosition , numWords ) ; }   protected static void discharge  (   final BufferedRunningLengthWord32 initialWord ,   final EWAHIterator32 iterator ,   final BitmapStorage32 container )  {  BufferedRunningLengthWord32  runningLengthWord = initialWord ;  for ( ; ; )  {   final  int  runningLength =  runningLengthWord . getRunningLength  ( ) ;   container . addStreamOfEmptyWords  (  runningLengthWord . getRunningBit  ( ) , runningLength ) ;   container . addStreamOfLiteralWords  (  iterator . buffer  ( ) ,   iterator . literalWords  ( ) +  runningLengthWord . literalwordoffset ,  runningLengthWord . getNumberOfLiteralWords  ( ) ) ;  if  (  !  iterator . hasNext  ( ) )  break ;   runningLengthWord =  new BufferedRunningLengthWord32  (  iterator . next  ( ) ) ; } }   public IteratingBufferedRunningLengthWord32 clone  ( )  throws CloneNotSupportedException  {  IteratingBufferedRunningLengthWord32  answer =  ( IteratingBufferedRunningLengthWord32 )  super . clone  ( ) ;    answer . brlw =   this . brlw . clone  ( ) ;    answer . buffer =  this . buffer ;    answer . iterator =   this . iterator . clone  ( ) ;    answer . literalWordStartPosition =  this . literalWordStartPosition ;  return answer ; }   private BufferedRunningLengthWord32  brlw ;   private   int  [ ]  buffer ;   private  int  literalWordStartPosition ;   private EWAHIterator32  iterator ; }