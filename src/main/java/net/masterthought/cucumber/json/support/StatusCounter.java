  package     net . masterthought . cucumber . json . support ;   import   java . util . EnumMap ;   public class StatusCounter  {   private  EnumMap  < Status , Integer >  counter =  new  EnumMap  < >  (  Status . class ) ;   private Status  finalStatus =  Status . PASSED ;   private  int  size = 0 ;   public StatusCounter  ( )  {  for ( Status status :  Status . values  ( ) )  {   counter . put  ( status , 0 ) ; } }   public void incrementFor  (  Status status )  {   final  int  statusCounter =   getValueFor  ( status ) + 1 ;    this . counter . put  ( status , statusCounter ) ;   size ++ ;  if  (   status . priority >  finalStatus . priority )  {   finalStatus = status ; } }   public  int getValueFor  (  Status status )  {  return   this . counter . get  ( status ) ; }   public  int size  ( )  {  return size ; }   public Status getFinalStatus  ( )  {  return finalStatus ; } }