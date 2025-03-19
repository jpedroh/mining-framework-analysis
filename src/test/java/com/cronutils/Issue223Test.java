  package  com . cronutils ;   import   java . time . ZonedDateTime ;  import   java . util . Optional ;  import   org . junit . Test ;  import    com . cronutils . model . Cron ;  import    com . cronutils . model . CronType ;  import     com . cronutils . model . definition . CronDefinition ;  import     com . cronutils . model . definition . CronDefinitionBuilder ;  import     com . cronutils . model . time . ExecutionTime ;  import    com . cronutils . parser . CronParser ;  import static    org . junit . Assert . assertEquals ;  import static    org . junit . Assert . fail ;   public class Issue223Test  {    @ Test public void testEveryWednesdayOfEveryDayNextExecution  ( )  {   final CronDefinition  cronDefinition =  CronDefinitionBuilder . instanceDefinitionFor  (  CronType . UNIX ) ;   final CronParser  parser =  new CronParser  ( cronDefinition ) ;   final Cron  myCron =  parser . parse  ( "* * * * 3" ) ;  ZonedDateTime  time =  ZonedDateTime . parse  ( "2017-09-05T11:31:55.407-05:00" ) ;   final  Optional  < ZonedDateTime > 
<<<<<<<
 nextExecution =   ExecutionTime . forCron  ( myCron ) . nextExecution  ( time )
=======
 onext =   ExecutionTime . forCron  ( myCron ) . nextExecution  ( time )
>>>>>>>
 ; 
<<<<<<<
 if  (  nextExecution . isPresent  ( ) )  {   assertEquals  (  ZonedDateTime . parse  ( "2017-09-06T00:00-05:00" ) ,  nextExecution . get  ( ) ) ; } else  {   fail  ( "next execution was not present" ) ; }
=======
 ZonedDateTime  next =  onext . orElse  ( null ) ;
>>>>>>>
 
<<<<<<<
=======
  assertEquals  (  ZonedDateTime . parse  ( "2017-09-06T00:00-05:00" ) , next ) ;
>>>>>>>
   final Cron  myCron2 =  parser . parse  ( "* * */1 * 3" ) ;   time =  ZonedDateTime . parse  ( "2017-09-05T11:31:55.407-05:00" ) ;   final  Optional  < ZonedDateTime > 
<<<<<<<
 nextExecution2 =   ExecutionTime . forCron  ( myCron2 ) . nextExecution  ( time )
=======
 onext2 =   ExecutionTime . forCron  ( myCron2 ) . nextExecution  ( time )
>>>>>>>
 ; 
<<<<<<<
 if  (  nextExecution2 . isPresent  ( ) )  {   assertEquals  (  ZonedDateTime . parse  ( "2017-09-06T00:00-05:00" ) ,  nextExecution2 . get  ( ) ) ; } else  {   fail  ( "next execution was not present" ) ; }
=======
 ZonedDateTime  next2 =  onext2 . orElse  ( null ) ;
>>>>>>>
 
<<<<<<<
=======
  assertEquals  (  ZonedDateTime . parse  ( "2017-09-06T00:00-05:00" ) , next2 ) ;
>>>>>>>
 } }