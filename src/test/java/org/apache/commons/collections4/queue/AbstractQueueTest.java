  package     org . apache . commons . collections4 . queue ;   import static      org . junit . jupiter . api . Assertions . assertNull ;  import static      org . junit . jupiter . api . Assertions . assertThrows ;  import static      org . junit . jupiter . api . Assertions . assertTrue ;  import   java . io . IOException ;  import   java . io . Serializable ;  import   java . util . ArrayList ;  import   java . util . Arrays ;  import   java . util . Collection ;  import   java . util . Iterator ;  import   java . util . NoSuchElementException ;  import   java . util . Queue ;  import      org . apache . commons . collections4 . collection . AbstractCollectionTest ;   public abstract class AbstractQueueTest  <  E >  extends  AbstractCollectionTest  < E >  {   public AbstractQueueTest  (   final String testName )  {  super  ( testName ) ; }   public boolean isSetSupported  ( )  {  return true ; }    @ Override public void verify  ( )  {   super . verify  ( ) ;   final  Iterator  < E >  iterator1 =   getCollection  ( ) . iterator  ( ) ;  for (  final E e :  getConfirmed  ( ) )  {   assertTrue  (  iterator1 . hasNext  ( ) ) ;   final Object  o1 =  iterator1 . next  ( ) ;   final Object  o2 = e ;   assertEquals  ( o1 , o2 ) ; } }    @ Override public  Collection  < E > makeConfirmedCollection  ( )  {   final  ArrayList  < E >  list =  new  ArrayList  < >  ( ) ;  return list ; }    @ Override public  Collection  < E > makeConfirmedFullCollection  ( )  {   final  ArrayList  < E >  list =  new  ArrayList  < >  ( ) ;   list . addAll  (  Arrays . asList  (  getFullElements  ( ) ) ) ;  return list ; }    @ Override public abstract  Queue  < E > makeObject  ( ) ;    @ Override public  Queue  < E > makeFullCollection  ( )  {   final  Queue  < E >  queue =  makeObject  ( ) ;   queue . addAll  (  Arrays . asList  (  getFullElements  ( ) ) ) ;  return queue ; }    @ Override public  Queue  < E > getCollection  ( )  {  return  (  Queue  < E > )  super . getCollection  ( ) ; }   public void testQueueOffer  ( )  {  if  (  !  isAddSupported  ( ) )  {  return ; }   final  E  [ ]  elements =  getFullElements  ( ) ;  for (  final E element : elements )  {   resetEmpty  ( ) ;   final boolean  r =   getCollection  ( ) . offer  ( element ) ;    getConfirmed  ( ) . add  ( element ) ;   verify  ( ) ;   assertTrue  ( "Empty queue changed after add" , r ) ;   assertEquals  ( "Queue size is 1 after first add" , 1 ,   getCollection  ( ) . size  ( ) ) ; }   resetEmpty  ( ) ;   int  size = 0 ;  for (  final E element : elements )  {   final boolean  r =   getCollection  ( ) . offer  ( element ) ;    getConfirmed  ( ) . add  ( element ) ;   verify  ( ) ;  if  ( r )  {   size ++ ; }   assertEquals  ( "Queue size should grow after add" , size ,   getCollection  ( ) . size  ( ) ) ;   assertTrue  ( "Queue should contain added element" ,   getCollection  ( ) . contains  ( element ) ) ; } }   public void testQueueElement  ( )  {   resetEmpty  ( ) ;  Exception  exception =  assertThrows  (  NoSuchElementException . class ,   ( ) ->  {    getCollection  ( ) . element  ( ) ; } ) ;  if  (  null !=  exception . getMessage  ( ) )  {   assertTrue  (   exception . getMessage  ( ) . contains  ( "queue is empty" ) ) ; }   resetFull  ( ) ;   assertTrue  (   getConfirmed  ( ) . contains  (   getCollection  ( ) . element  ( ) ) ) ;  if  (  !  isRemoveSupported  ( ) )  {  return ; }   final  int  max =   getFullElements  ( ) . length ;  for (   int  i = 0 ;  i < max ;  i ++ )  {   final E  element =   getCollection  ( ) . element  ( ) ;  if  (  !  isNullSupported  ( ) )  {   assertNotNull  ( element ) ; }   assertTrue  (   getConfirmed  ( ) . contains  ( element ) ) ;    getCollection  ( ) . remove  ( element ) ;    getConfirmed  ( ) . remove  ( element ) ;   verify  ( ) ; }   exception =  assertThrows  (  NoSuchElementException . class ,   ( ) ->  {    getCollection  ( ) . element  ( ) ; } ) ;  if  (  null !=  exception . getMessage  ( ) )  {   assertTrue  (   exception . getMessage  ( ) . contains  ( "queue is empty" ) ) ; } }   public void testQueuePeek  ( )  {  if  (  !  isRemoveSupported  ( ) )  {  return ; }   resetEmpty  ( ) ;  E  element =   getCollection  ( ) . peek  ( ) ;   assertNull  ( element ) ;   resetFull  ( ) ;   final  int  max =   getFullElements  ( ) . length ;  for (   int  i = 0 ;  i < max ;  i ++ )  {   element =   getCollection  ( ) . peek  ( ) ;  if  (  !  isNullSupported  ( ) )  {   assertNotNull  ( element ) ; }   assertTrue  (   getConfirmed  ( ) . contains  ( element ) ) ;    getCollection  ( ) . remove  ( element ) ;    getConfirmed  ( ) . remove  ( element ) ;   verify  ( ) ; }   element =   getCollection  ( ) . peek  ( ) ;   assertNull  ( element ) ; }   public void testQueueRemove  ( )  {  if  (  !  isRemoveSupported  ( ) )  {  return ; }   resetEmpty  ( ) ;  Exception  exception =  assertThrows  (  NoSuchElementException . class ,   ( ) ->  {    getCollection  ( ) . remove  ( ) ; } ) ;  if  (  null !=  exception . getMessage  ( ) )  {   assertTrue  (   exception . getMessage  ( ) . contains  ( "queue is empty" ) ) ; }   resetFull  ( ) ;   final  int  max =   getFullElements  ( ) . length ;  for (   int  i = 0 ;  i < max ;  i ++ )  {   final E  element =   getCollection  ( ) . remove  ( ) ;   final boolean  success =   getConfirmed  ( ) . remove  ( element ) ;   assertTrue  ( "remove should return correct element" , success ) ;   verify  ( ) ; }   exception =  assertThrows  (  NoSuchElementException . class ,   ( ) ->  {    getCollection  ( ) . element  ( ) ; } ) ;  if  (  null !=  exception . getMessage  ( ) )  {   assertTrue  (   exception . getMessage  ( ) . contains  ( "queue is empty" ) ) ; } }   public void testQueuePoll  ( )  {  if  (  !  isRemoveSupported  ( ) )  {  return ; }   resetEmpty  ( ) ;  E  element =   getCollection  ( ) . poll  ( ) ;   assertNull  ( element ) ;   resetFull  ( ) ;   final  int  max =   getFullElements  ( ) . length ;  for (   int  i = 0 ;  i < max ;  i ++ )  {   element =   getCollection  ( ) . poll  ( ) ;   final boolean  success =   getConfirmed  ( ) . remove  ( element ) ;   assertTrue  ( "poll should return correct element" , success ) ;   verify  ( ) ; }   element =   getCollection  ( ) . poll  ( ) ;   assertNull  ( element ) ; }    @ SuppressWarnings  ( "unchecked" ) public void testEmptyQueueSerialization  ( )  throws IOException , ClassNotFoundException  {   final  Queue  < E >  queue =  makeObject  ( ) ;  if  (  !  (   queue instanceof Serializable &&  isTestSerialization  ( ) ) )  {  return ; }   final   byte  [ ]  objekt =  writeExternalFormToBytes  (  ( Serializable ) queue ) ;   final  Queue  < E >  queue2 =  (  Queue  < E > )  readExternalFormFromBytes  ( objekt ) ;   assertEquals  ( "Both queues are empty" , 0 ,  queue . size  ( ) ) ;   assertEquals  ( "Both queues are empty" , 0 ,  queue2 . size  ( ) ) ; }    @ SuppressWarnings  ( "unchecked" ) public void testFullQueueSerialization  ( )  throws IOException , ClassNotFoundException  {   final  Queue  < E >  queue =  makeFullCollection  ( ) ;   final  int  size =   getFullElements  ( ) . length ;  if  (  !  (   queue instanceof Serializable &&  isTestSerialization  ( ) ) )  {  return ; }   final   byte  [ ]  objekt =  writeExternalFormToBytes  (  ( Serializable ) queue ) ;   final  Queue  < E >  queue2 =  (  Queue  < E > )  readExternalFormFromBytes  ( objekt ) ;   assertEquals  ( "Both queues are same size" , size ,  queue . size  ( ) ) ;   assertEquals  ( "Both queues are same size" , size ,  queue2 . size  ( ) ) ; }    @ SuppressWarnings  ( "unchecked" ) public void testEmptyQueueCompatibility  ( )  throws IOException , ClassNotFoundException  {   final  Queue  < E >  queue =  makeObject  ( ) ;  if  (    queue instanceof Serializable &&  !  skipSerializedCanonicalTests  ( ) &&  isTestSerialization  ( ) )  {   final  Queue  < E >  queue2 =  (  Queue  < E > )  readExternalFormFromDisk  (  getCanonicalEmptyCollectionName  ( queue ) ) ;   assertEquals  ( "Queue is empty" , 0 ,  queue2 . size  ( ) ) ; } }    @ SuppressWarnings  ( "unchecked" ) public void testFullQueueCompatibility  ( )  throws IOException , ClassNotFoundException  {   final  Queue  < E >  queue =  makeFullCollection  ( ) ;  if  (    queue instanceof Serializable &&  !  skipSerializedCanonicalTests  ( ) &&  isTestSerialization  ( ) )  {   final  Queue  < E >  queue2 =  (  Queue  < E > )  readExternalFormFromDisk  (  getCanonicalFullCollectionName  ( queue ) ) ;   assertEquals  ( "Queues are not the right size" ,  queue . size  ( ) ,  queue2 . size  ( ) ) ; } } }