  package      org . movsim . input . model . output . impl ;   import   org . jdom . Element ;  import      org . movsim . input . model . output . TrajectoriesInput ;   public class TrajectoriesInputImpl  implements  TrajectoriesInput  {   private  double  dt ;   private  double  startTime ;   private  double  endTime ;   private  double  startPosition ;   private  double  endPosition ;   private boolean  isInitialized ;   public TrajectoriesInputImpl  (  Element elem )  {  if  (  elem == null )  {   isInitialized = false ;  return ; }   dt =  Double . parseDouble  (  elem . getAttributeValue  ( "dt" ) ) ;   startTime =  Double . parseDouble  (  elem . getAttributeValue  ( "start_time" ) ) ;   endTime = 
<<<<<<<
 Double . parseDouble  (  elem . getAttributeValue  ( "end_time" ) )
=======
 startTime +  Double . parseDouble  (  elem . getAttributeValue  ( "duration" ) )
>>>>>>>
 ;   startPosition =  Double . parseDouble  (  elem . getAttributeValue  ( 
<<<<<<<
"start_x"
=======
"x"
>>>>>>>
 ) ) ;   endPosition = 
<<<<<<<
 Double . parseDouble  (  elem . getAttributeValue  ( "end_x" ) )
=======
 startPosition +  Double . parseDouble  (  elem . getAttributeValue  ( "length" ) )
>>>>>>>
 ;   isInitialized = true ; }   public  double getDt  ( )  {  return dt ; }   public  double getStartTime  ( )  {  return startTime ; }   public  double getEndTime  ( )  {  return endTime ; }   public  double getStartPosition  ( )  {  return startPosition ; }   public  double getEndPosition  ( )  {  return endPosition ; }   public boolean isInitialized  ( )  {  return isInitialized ; } }