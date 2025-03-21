  package    net . iponweb . disthene . config ;   import   java . util . ArrayList ;  import   java . util . List ;  import   java . util . Objects ;   public class CarbonConfiguration  {   private String  bind ;   private  int  port ;   private  List  < Rollup >  rollups =  new  ArrayList  < >  ( ) ;   private Rollup  baseRollup ;   private  int  aggregatorDelay ;   private boolean  aggregateBaseRollup ;   public String getBind  ( )  {  return bind ; }   public void setBind  (  String bind )  {    this . bind = bind ; }   public  int getPort  ( )  {  return port ; }   public void setPort  (   int port )  {    this . port = port ; }   public  int getAggregatorDelay  ( )  {  return aggregatorDelay ; }   public void setAggregatorDelay  (   int aggregatorDelay )  {    this . aggregatorDelay = aggregatorDelay ; }   public boolean getAggregateBaseRollup  ( )  {  return aggregateBaseRollup ; }   public void setAggregateBaseRollup  (  boolean aggregateBaseRollup )  {    this . aggregateBaseRollup = aggregateBaseRollup ; }   public  List  < Rollup > getRollups  ( )  {  return rollups ; }   public void setRollups  (   List  < Rollup > rollups )  {   baseRollup =  rollups . get  ( 0 ) ;    this . rollups =  rollups . subList  ( 1 ,  rollups . size  ( ) ) ; }   public Rollup getBaseRollup  ( )  {  return baseRollup ; }    @ Override public String toString  ( )  {  return               
<<<<<<<
"CarbonConfiguration{"
=======
  "CarbonConfiguration{" + "bind='" + bind
>>>>>>>
 + 
<<<<<<<
"bind='"
=======
'\''
>>>>>>>
 + 
<<<<<<<
bind
=======
", port="
>>>>>>>
 + 
<<<<<<<
'\''
=======
port
>>>>>>>
 + 
<<<<<<<
", port="
=======
", rollups="
>>>>>>>
 + 
<<<<<<<
port
=======
rollups
>>>>>>>
 + 
<<<<<<<
", rollups="
=======
", baseRollup="
>>>>>>>
 + 
<<<<<<<
rollups
=======
baseRollup
>>>>>>>
 + 
<<<<<<<
", baseRollup="
=======
", aggregatorDelay="
>>>>>>>
 + 
<<<<<<<
baseRollup
=======
aggregatorDelay
>>>>>>>
 + 
<<<<<<<
", aggregatorDelay="
=======
", authorizedTenants="
>>>>>>>
 + 
<<<<<<<
aggregatorDelay
=======
authorizedTenants
>>>>>>>
 + 
<<<<<<<
", aggregateBaseRollup="
=======
", allowAll="
>>>>>>>
 + 
<<<<<<<
aggregateBaseRollup
=======
allowAll
>>>>>>>
 + '}' ; }   private  List  < String >  authorizedTenants =  new  ArrayList  < >  ( ) ;   private boolean  allowAll = true ;   public  List  < String > getAuthorizedTenants  ( )  {  return authorizedTenants ; }   public void setAuthorizedTenants  (   List  < String > authorizedTenants )  {    this . authorizedTenants =  Objects . requireNonNullElseGet  ( authorizedTenants ,  ArrayList :: new ) ; }   public boolean isAllowAll  ( )  {  return allowAll ; }   public void setAllowAll  (  boolean allowAll )  {    this . allowAll = allowAll ; } }