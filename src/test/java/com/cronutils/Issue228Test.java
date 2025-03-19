  package  com . cronutils ;   import   java . time . ZonedDateTime ;  import   java . util . Optional ;  import   org . junit . Test ;  import    com . cronutils . model . Cron ;  import     com . cronutils . model . definition . CronDefinition ;  import     com . cronutils . model . definition . CronDefinitionBuilder ;  import     com . cronutils . model . time . ExecutionTime ;  import    com . cronutils . parser . CronParser ;  import static    org . junit . Assert . assertEquals ;   public class Issue228Test  {   private static final String  TEST_DATE = "2017-09-29T14:46:01.166-07:00" ;   private final CronDefinition  cronDefinition =                  CronDefinitionBuilder . defineCron  ( ) . withMinutes  ( ) . and  ( ) . withHours  ( ) . and  ( ) . withDayOfMonth  ( ) . and  ( ) . withMonth  ( ) . and  ( ) . withDayOfWeek  ( ) . withValidRange  ( 0 , 7 ) . withMondayDoWValue  ( 1 ) . withIntMapping  ( 7 , 0 ) . and  ( ) . enforceStrictRanges  ( ) . matchDayOfWeekAndDayOfMonth  ( ) . instance  ( ) ;    @ Test public void testFirstMondayOfTheMonthNextExecution  ( )  {   final CronParser  parser =  new CronParser  ( cronDefinition ) ;   final Cron  myCron =  parser . parse  ( "0 9 1-7 * 1" ) ;   final ZonedDateTime  time =  ZonedDateTime . parse  ( TEST_DATE ) ;   Optional  < ZonedDateTime >  onext =   ExecutionTime . forCron  ( myCron ) . nextExecution  ( time ) ;  ZonedDateTime  next =  onext . orElse  ( null ) ;   assertEquals  (  ZonedDateTime . parse  ( "2017-10-02T09:00-07:00" ) , 
<<<<<<<
 getNextExecutionTime  ( myCron , time )
=======
next
>>>>>>>
 ) ; }    @ Test public void testEveryWeekdayFirstWeekOfMonthNextExecution  ( )  {   final CronParser  parser =  new CronParser  ( cronDefinition ) ;   final Cron  myCron =  parser . parse  ( "0 9 1-7 * 1-5" ) ;   final ZonedDateTime  time =  ZonedDateTime . parse  ( TEST_DATE ) ;   Optional  < ZonedDateTime >  onext =   ExecutionTime . forCron  ( myCron ) . nextExecution  ( time ) ;  ZonedDateTime  next =  onext . orElse  ( null ) ;   assertEquals  (  ZonedDateTime . parse  ( "2017-10-02T09:00-07:00" ) , 
<<<<<<<
 getNextExecutionTime  ( myCron , time )
=======
next
>>>>>>>
 ) ; }    @ Test public void testEveryWeekendFirstWeekOfMonthNextExecution  ( )  {   final CronParser  parser =  new CronParser  ( cronDefinition ) ;   final Cron  myCron =  parser . parse  ( "0 9 1-7 * 6-7" ) ;   final ZonedDateTime  time =  ZonedDateTime . parse  ( TEST_DATE ) ;   Optional  < ZonedDateTime >  onext =   ExecutionTime . forCron  ( myCron ) . nextExecution  ( time ) ;  ZonedDateTime  next =  onext . orElse  ( null ) ;   assertEquals  (  ZonedDateTime . parse  ( "2017-10-01T09:00-07:00" ) , 
<<<<<<<
 getNextExecutionTime  ( myCron , time )
=======
next
>>>>>>>
 ) ; }    @ Test public void testEveryWeekdaySecondWeekOfMonthNextExecution  ( )  {   final CronParser  parser =  new CronParser  ( cronDefinition ) ;   final Cron  myCron =  parser . parse  ( "0 9 8-14 * 1-5" ) ;   final ZonedDateTime  time =  ZonedDateTime . parse  ( TEST_DATE ) ;   Optional  < ZonedDateTime >  onext =   ExecutionTime . forCron  ( myCron ) . nextExecution  ( time ) ;  ZonedDateTime  next =  onext . orElse  ( null ) ;   assertEquals  (  ZonedDateTime . parse  ( "2017-10-09T09:00-07:00" ) , 
<<<<<<<
 getNextExecutionTime  ( myCron , time )
=======
next
>>>>>>>
 ) ; }    @ Test public void testEveryWeekendForthWeekOfMonthNextExecution  ( )  {   final CronParser  parser =  new CronParser  ( cronDefinition ) ;   final Cron  myCron =  parser . parse  ( "0 9 22-28 * 6-7" ) ;   final ZonedDateTime  time =  ZonedDateTime . parse  ( TEST_DATE ) ;   Optional  < ZonedDateTime >  onext =   ExecutionTime . forCron  ( myCron ) . nextExecution  ( time ) ;  ZonedDateTime  next =  onext . orElse  ( null ) ;   assertEquals  (  ZonedDateTime . parse  ( "2017-10-22T09:00-07:00" ) , 
<<<<<<<
 getNextExecutionTime  ( myCron , time )
=======
next
>>>>>>>
 ) ; }   private ZonedDateTime getNextExecutionTime  (   final Cron cron ,   final ZonedDateTime time )  {   final  Optional  < ZonedDateTime >  nextExecution =   ExecutionTime . forCron  ( cron ) . nextExecution  ( time ) ;  if  (  nextExecution . isPresent  ( ) )  {  return  nextExecution . get  ( ) ; } else  {  throw  new NullPointerException  ( "next execution was not present" ) ; } } }