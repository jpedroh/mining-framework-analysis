  package  com . cronutils ;   import   java . time . LocalDateTime ;  import   java . time . ZoneId ;  import   java . time . ZonedDateTime ;  import   java . util . Optional ;  import   org . junit . Assert ;  import   org . junit . Before ;  import   org . junit . Ignore ;  import   org . junit . Test ;  import    com . cronutils . model . CronType ;  import     com . cronutils . model . definition . CronDefinitionBuilder ;  import     com . cronutils . model . time . ExecutionTime ;  import    com . cronutils . parser . CronParser ;  import static    org . junit . Assert . fail ;   public class Issue143Test  {   private static final String  LAST_EXECUTION_NOT_PRESENT_ERROR = "last execution was not present" ;   private CronParser  parser ;   private ZonedDateTime  currentDateTime ;    @ Before public void setUp  ( )  {   currentDateTime =  ZonedDateTime . of  (  LocalDateTime . of  ( 2016 , 12 , 20 , 12 , 0 ) ,  ZoneId . systemDefault  ( ) ) ;   parser =  new CronParser  (  CronDefinitionBuilder . instanceDefinitionFor  (  CronType . QUARTZ ) ) ; }    @ Test public void testCase1  ( )  {   final ExecutionTime  et =  ExecutionTime . forCron  (  parser . parse  ( "0 0 12 31 12 ? *" ) ) ;   final  Optional  < ZonedDateTime > 
<<<<<<<
 lastExecution =  et . lastExecution  ( currentDateTime )
=======
 olast =  et . lastExecution  ( currentDateTime )
>>>>>>>
 ; 
<<<<<<<
 if  (  lastExecution . isPresent  ( ) )  {   final ZonedDateTime  actual =  lastExecution . get  ( ) ;   final ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2015 , 12 , 31 , 12 , 00 ) ,  ZoneId . systemDefault  ( ) ) ;   Assert . assertEquals  ( expected , actual ) ; }
=======
 ZonedDateTime  last =  olast . orElse  ( null ) ;
>>>>>>>
 
<<<<<<<
=======
 ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2015 , 12 , 31 , 12 , 0 ) ,  ZoneId . systemDefault  ( ) ) ;
>>>>>>>
 
<<<<<<<
=======
  Assert . assertEquals  ( expected , last ) ;
>>>>>>>
 }    @ Test public void testCase2  ( )  {   final ExecutionTime  et =  ExecutionTime . forCron  (  parser . parse  ( "0 0 12 ? 12 SAT#5 *" ) ) ;   final  Optional  < ZonedDateTime > 
<<<<<<<
 lastExecution =  et . lastExecution  ( currentDateTime )
=======
 olast =  et . lastExecution  ( currentDateTime )
>>>>>>>
 ; 
<<<<<<<
 if  (  lastExecution . isPresent  ( ) )  {   final ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2012 , 12 , 29 , 12 , 00 ) ,  ZoneId . systemDefault  ( ) ) ;   Assert . assertEquals  ( expected ,  lastExecution . get  ( ) ) ; } else  {   fail  ( LAST_EXECUTION_NOT_PRESENT_ERROR ) ; }
=======
 ZonedDateTime  last =  olast . orElse  ( null ) ;
>>>>>>>
 
<<<<<<<
=======
 ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2012 , 12 , 29 , 12 , 0 ) ,  ZoneId . systemDefault  ( ) ) ;
>>>>>>>
 
<<<<<<<
=======
  Assert . assertEquals  ( expected , last ) ;
>>>>>>>
 }    @ Test  @ Ignore public void testCase3  ( )  {   final ExecutionTime  et =  ExecutionTime . forCron  (  parser . parse  ( "0 0 12 31 1/1 ? *" ) ) ;   final  Optional  < ZonedDateTime > 
<<<<<<<
 lastExecution =  et . lastExecution  ( currentDateTime )
=======
 olast =  et . lastExecution  ( currentDateTime )
>>>>>>>
 ; 
<<<<<<<
 if  (  lastExecution . isPresent  ( ) )  {   final ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2015 , 12 , 31 , 12 , 00 ) ,  ZoneId . systemDefault  ( ) ) ;   Assert . assertEquals  ( expected ,  lastExecution . get  ( ) ) ; } else  {   fail  ( LAST_EXECUTION_NOT_PRESENT_ERROR ) ; }
=======
 ZonedDateTime  last =  olast . orElse  ( null ) ;
>>>>>>>
 
<<<<<<<
=======
 ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2015 , 12 , 31 , 12 , 0 ) ,  ZoneId . systemDefault  ( ) ) ;
>>>>>>>
 
<<<<<<<
=======
  Assert . assertEquals  ( expected , last ) ;
>>>>>>>
 }    @ Test public void testCase4  ( )  {   final ExecutionTime  et =  ExecutionTime . forCron  (  parser . parse  ( "0 0 12 ? 1/1 SAT#5 *" ) ) ;   final  Optional  < ZonedDateTime > 
<<<<<<<
 lastExecution =  et . lastExecution  ( currentDateTime )
=======
 olast =  et . lastExecution  ( currentDateTime )
>>>>>>>
 ; 
<<<<<<<
 if  (  lastExecution . isPresent  ( ) )  {   final ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2016 , 10 , 29 , 12 , 00 ) ,  ZoneId . systemDefault  ( ) ) ;   Assert . assertEquals  ( expected ,  lastExecution . get  ( ) ) ; } else  {   fail  ( LAST_EXECUTION_NOT_PRESENT_ERROR ) ; }
=======
 ZonedDateTime  last =  olast . orElse  ( null ) ;
>>>>>>>
 
<<<<<<<
=======
 ZonedDateTime  expected =  ZonedDateTime . of  (  LocalDateTime . of  ( 2016 , 10 , 29 , 12 , 0 ) ,  ZoneId . systemDefault  ( ) ) ;
>>>>>>>
 
<<<<<<<
=======
  Assert . assertEquals  ( expected , last ) ;
>>>>>>>
 } }