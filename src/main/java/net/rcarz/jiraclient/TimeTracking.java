  package   net . rcarz . jiraclient ;   import   java . util . Map ;  import    net . sf . json . JSONObject ;   public class TimeTracking  {   private String  originalEstimate = null ;   private String  remainingEstimate = null ;   private String  timeSpent = null ;   private Integer  originalEstimateSeconds = null ;   private Integer  remainingEstimateSeconds = null ;   private 
<<<<<<<
Integer
=======
 int
>>>>>>>
  timeSpentSeconds = 
<<<<<<<
null
=======
0
>>>>>>>
 ;   protected TimeTracking  (  JSONObject json )  {   Map  <  ? ,  ? >  map = json ;   originalEstimate =  Field . getString  (  map . get  ( "originalEstimate" ) ) ;   remainingEstimate =  Field . getString  (  map . get  ( "remainingEstimate" ) ) ;   timeSpent =  Field . getString  (  map . get  ( "timeSpent" ) ) ;   originalEstimateSeconds =  Field . getInteger  (  map . get  ( "originalEstimateSeconds" ) ) ;   remainingEstimateSeconds =  Field . getInteger  (  map . get  ( "remainingEstimateSeconds" ) ) ;   timeSpentSeconds =  Field . getInteger  (  map . get  ( "timeSpentSeconds" ) ) ; }   public String getOriginalEstimate  ( )  {  return originalEstimate ; }   public String getRemainingEstimate  ( )  {  return remainingEstimate ; }   public String getTimeSpent  ( )  {  return timeSpent ; }   public  int getOriginalEstimateSeconds  ( )  {  return originalEstimateSeconds ; }   public  int getRemainingEstimateSeconds  ( )  {  return remainingEstimateSeconds ; }   public  int getTimeSpentSeconds  ( )  {  return timeSpentSeconds ; } }