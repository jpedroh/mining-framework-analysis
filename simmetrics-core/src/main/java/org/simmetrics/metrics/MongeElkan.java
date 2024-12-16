  package   org . simmetrics . metrics ;   import static      com . google . common . base . Preconditions . checkArgument ;  import static    java . lang . Math . max ;  import static    java . lang . Math . sqrt ;  import   java . util . List ;  import   org . simmetrics . ListMetric ;  import   org . simmetrics . StringMetric ;   public final class MongeElkan  implements   ListMetric  < String >  {   private final StringMetric  metric ;   public MongeElkan  (   final StringMetric metric )  {    this . metric = metric ; }    @ Override public  float compare  (   List  < String > a ,   List  < String > b )  {   checkArgument  (  !  a . contains  ( null ) , "a may not not contain null" ) ;   checkArgument  (  !  b . contains  ( null ) , "b may not not contain null" ) ;  if  (   a . isEmpty  ( ) &&  b . isEmpty  ( ) )  {  return 1.0f ; }  if  (   a . isEmpty  ( ) ||  b . isEmpty  ( ) )  {  return 0.0f ; }  return  (  float )  sqrt  (   mongeElkan  ( a , b ) *  mongeElkan  ( b , a ) ) ; }   private  float mongeElkan  (   List  < String > a ,   List  < String > b )  {   float  sum = 0.0f ;  for ( String s : a )  {   float  max = 0.0f ;  for ( String q : b )  {   max =  max  ( max ,  metric . compare  ( s , q ) ) ; }   sum += max ; }  return  sum /  a . size  ( ) ; }    @ Override public String toString  ( )  {  return   "MongeElkan [metric=" + metric + "]" ; } }