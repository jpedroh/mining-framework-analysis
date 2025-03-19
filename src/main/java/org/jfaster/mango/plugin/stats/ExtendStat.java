  package     org . jfaster . mango . plugin . stats ;   import     org . jfaster . mango . annotation . DB ;  import     org . jfaster . mango . annotation . Sharding ;  import     org . jfaster . mango . sharding . NotUseTableShardingStrategy ;  import     org . jfaster . mango . stat . OperatorStat ;  import     org . jfaster . mango . util . Joiner ;  import     org . jfaster . mango . util . Strings ;  import     org . jfaster . mango . util . ToStringHelper ;  import    java . lang . reflect . Method ;  import    java . lang . reflect . Type ;  import   java . util . ArrayList ;  import   java . util . Arrays ;  import   java . util . List ;   public class ExtendStat  {   private OperatorStat  operatorStat ;   private Method  method ;   public ExtendStat  (  OperatorStat operatorStat )  {    this . operatorStat = operatorStat ;    this . method =  operatorStat . getMethod  ( ) ; }   public String getSimpleClassName  ( )  {  return   operatorStat . getDaoClass  ( ) . getSimpleName  ( ) ; }   public String getSimpleMethodName  ( )  {  return     method . getName  ( ) + "(" +   method . getParameterTypes  ( ) . length + ")" ; }   public String getSql  ( )  {  String  sql =  
<<<<<<<
 Joiner . on  ( ' ' )
=======
operatorStat
>>>>>>>
 . 
<<<<<<<
join
=======
getSql
>>>>>>>
  (  Arrays . asList  (   method . getAnnotation  (  SQL . class ) . value  ( ) ) ) ;  DB  dbAnno =   operatorStat . getDaoClass  ( ) . getAnnotation  (  DB . class ) ;  String  table =  dbAnno . table  ( ) ;  if  (  Strings . isNotEmpty  ( table ) )  {  Sharding  shardingAnno =  method . getAnnotation  (  Sharding . class ) ;  if  (  shardingAnno == null )  {   shardingAnno =   operatorStat . getDaoClass  ( ) . getAnnotation  (  Sharding . class ) ; }  if  (   shardingAnno != null &&  !   NotUseTableShardingStrategy . class . equals  (  shardingAnno . tableShardingStrategy  ( ) ) )  {   table =  table + "_#" ; }   sql =  sql . replaceAll  ( "#table" , table ) ; }  return sql ; }   public  List  < String > getStrParameterTypes  ( )  {   List  < String >  r =  new  ArrayList  < String >  ( ) ;  for ( Type type :  method . getGenericParameterTypes  ( ) )  {   r . add  (  ToStringHelper . toString  ( type ) ) ; }  return r ; }   public String getType  ( )  {  return    operatorStat . getOperatorType  ( ) . name  ( ) . toLowerCase  ( ) ; } }