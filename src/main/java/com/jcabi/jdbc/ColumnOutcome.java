  package   com . jcabi . jdbc ;   import    com . jcabi . aspects . Immutable ;  import    com . jcabi . aspects . Loggable ;  import   java . sql . ResultSet ;  import   java . sql . SQLException ;  import   java . sql . Statement ;  import   java . util . Collection ;  import   java . util . Date ;  import   java . util . LinkedList ;  import    javax . validation . constraints . NotNull ;  import  lombok . EqualsAndHashCode ;  import  lombok . ToString ;    @ Immutable  @ ToString  @ EqualsAndHashCode  (  of = "type" ) public final class ColumnOutcome  <  T >  implements   Outcome  <  Collection  < T > >  {   private final transient String  type ;   public ColumnOutcome  (    @ NotNull  (  message = "type can't be NULL" ) final  Class  < T > tpe )  {  if  (        tpe . equals  (  String . class ) ||  tpe . equals  (  Long . class ) ||  tpe . equals  (  Boolean . class ) ||  tpe . equals  (  Byte . class ) ||  tpe . equals  (  Date . class ) ||  tpe . equals  (  Utc . class ) ||     byte  [ ] . class . equals  ( 
<<<<<<<
tpe
=======
   byte  [ ] . class
>>>>>>>
 ) )  {    this . type =  tpe . getName  ( ) ; } else  {  throw  new IllegalArgumentException  (  String . format  ( "type %s is not supported" ,  tpe . getName  ( ) ) ) ; } }    @ Override  @ Loggable  (  Loggable . DEBUG ) public  Collection  < T > handle  (   final ResultSet rset ,   final Statement stmt )  throws SQLException  {   final  Collection  < T >  result =  new  LinkedList  < T >  ( ) ;  while  (  rset . next  ( ) )  {   result . add  (  this . fetch  ( rset ) ) ; }  return result ; }    @ SuppressWarnings  ( "unchecked" ) private T fetch  (   final ResultSet rset )  throws SQLException  {   final Object  result ;   Class  < T >  tpe ;  try  {   tpe =  (  Class  < T > )  Class . forName  (  this . type ) ;  if  (  tpe . equals  (  String . class ) )  {   result =  rset . getString  ( 1 ) ; } else  if  (  tpe . equals  (  Long . class ) )  {   result =  rset . getLong  ( 1 ) ; } else  if  (  tpe . equals  (  Boolean . class ) )  {   result =  rset . getBoolean  ( 1 ) ; } else  if  (  tpe . equals  (  Byte . class ) )  {   result =  rset . getByte  ( 1 ) ; } else  if  (  tpe . equals  (  Date . class ) )  {   result =  rset . getDate  ( 1 ) ; } else  if  (  tpe . equals  (  Utc . class ) )  {   result =  new Utc  (  Utc . getTimestamp  ( rset , 1 ) ) ; } else  if  (  
<<<<<<<
   byte  [ ] . class
=======
tpe
>>>>>>>
 . equals  ( 
<<<<<<<
tpe
=======
   byte  [ ] . class
>>>>>>>
 ) )  {   result =  rset . getBytes  ( 1 ) ; } else  {  throw  new IllegalStateException  (  String . format  ( "type %s is not allowed" ,  tpe . getName  ( ) ) ) ; } }  catch (   final  ClassNotFoundException ex )  {  throw  new IllegalArgumentException  (  String . format  ( "Unknown type: %s" ,  this . type ) , ex ) ; }  return  tpe . cast  ( result ) ; } }