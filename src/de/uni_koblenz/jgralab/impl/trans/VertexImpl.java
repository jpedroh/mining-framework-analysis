  package     de . uni_koblenz . jgralab . impl . trans ;   import   java . util . Comparator ;  import   java . util . HashMap ;  import   java . util . HashSet ;  import   java . util . Map ;  import   java . util . Set ;  import    de . uni_koblenz . jgralab . AttributedElement ;  import    de . uni_koblenz . jgralab . Edge ;  import    de . uni_koblenz . jgralab . EdgeDirection ;  import    de . uni_koblenz . jgralab . Graph ;  import    de . uni_koblenz . jgralab . GraphException ;  import    de . uni_koblenz . jgralab . GraphIOException ;  import    de . uni_koblenz . jgralab . Vertex ;  import     de . uni_koblenz . jgralab . impl . InternalEdge ;  import     de . uni_koblenz . jgralab . impl . IncidenceImpl ;  import     de . uni_koblenz . jgralab . impl . InternalVertex ;  import     de . uni_koblenz . jgralab . schema . Attribute ;  import     de . uni_koblenz . jgralab . schema . EdgeClass ;  import     de . uni_koblenz . jgralab . trans . ListPosition ;  import     de . uni_koblenz . jgralab . trans . Transaction ;  import     de . uni_koblenz . jgralab . trans . TransactionState ;  import     de . uni_koblenz . jgralab . trans . VersionedDataObject ;   public abstract class VertexImpl  extends     de . uni_koblenz . jgralab . impl . VertexBaseImpl  {   protected  VersionedReferenceImpl  < VertexImpl >  nextVertex ;   protected  VersionedReferenceImpl  < VertexImpl >  prevVertex ;   protected  VersionedReferenceImpl  < IncidenceImpl >  firstIncidence ;   protected  VersionedReferenceImpl  < IncidenceImpl >  lastIncidence ;   protected  VersionedReferenceImpl  < Long >  incidenceListVersion ;   protected VertexImpl  (   int anId ,  Graph graph )  {  super  ( anId , graph ) ;    (  ( GraphImpl ) graph ) . addVertex  ( this ) ; }    @ Override public  int getId  ( )  {  if  (  !  graph . isLoading  ( ) )  {  Transaction  transaction =  graph . getCurrentTransaction  ( ) ;  if  (  transaction == null )  {  throw  new GraphException  ( "Current transaction is null." ) ; }  if  (   (   transaction . getState  ( ) ==  TransactionState . RUNNING ) &&  !  isValid  ( ) )  {  return 0 ; } }  return id ; }    @ Override public InternalEdge getFirstIncidenceInISeq  ( )  {  if  (  firstIncidence == null )  {  return null ; }  return  firstIncidence . getValidValue  (  graph . getCurrentTransaction  ( ) ) ; }    @ Override public IncidenceImpl getLastIncidenceInISeq  ( )  {  if  (  lastIncidence == null )  {  return null ; }  return  lastIncidence . getValidValue  (  graph . getCurrentTransaction  ( ) ) ; }    @ Override public InternalVertex getNextVertexInVSeq  ( )  {  if  (  nextVertex == null )  {  return null ; }  return  nextVertex . getValidValue  (  graph . getCurrentTransaction  ( ) ) ; }    @ Override public InternalVertex getPrevVertexInVSeq  ( )  {  if  (  prevVertex == null )  {  return null ; }  return  prevVertex . getValidValue  (  graph . getCurrentTransaction  ( ) ) ; }    @ Override public void setId  (   int id )  {  if  (  graph . isLoading  ( ) )  {    this . id = id ; } else  {  Transaction  transaction =  graph . getCurrentTransaction  ( ) ;  if  (  transaction == null )  {  throw  new GraphException  ( "Current transaction is null." ) ; }  TransactionState  state =  transaction . getState  ( ) ;  if  (   (  state ==  TransactionState . RUNNING ) &&  (  id != 0 ) )  {    this . id = id ; } } }    @ Override public void setFirstIncidence  (  InternalEdge firstIncidence )  {  if  (  graph . isLoading  ( ) )  {    this . firstIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this ,  ( IncidenceImpl ) firstIncidence ) ; } else  {  if  (   this . firstIncidence == null )  {    this . firstIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this ) ; }    this . firstIncidence . setValidValue  (  ( IncidenceImpl ) firstIncidence ,  graph . getCurrentTransaction  ( ) ) ; } }    @ Override public void setLastIncidence  (  InternalEdge lastIncidence )  {  if  (  graph . isLoading  ( ) )  {    this . lastIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this ,  ( IncidenceImpl ) lastIncidence ) ; } else  {  if  (   this . lastIncidence == null )  {    this . lastIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this ) ; }    this . lastIncidence . setValidValue  (  ( IncidenceImpl ) lastIncidence ,  graph . getCurrentTransaction  ( ) ) ; } }    @ Override public void setNextVertex  (  Vertex nextVertex )  {  if  (  graph . isLoading  ( ) )  {    this . nextVertex =  new  VersionedReferenceImpl  < VertexImpl >  ( this ,  ( VertexImpl ) nextVertex , "$nextVertex" ) ; } else  {  TransactionImpl  transaction =  ( TransactionImpl )  graph . getCurrentTransaction  ( ) ;  if  (  transaction == null )  {  throw  new GraphException  ( "Current transaction is null." ) ; }  boolean  explicitChange = false ;  if  (   transaction . getState  ( ) ==  TransactionState . WRITING )  {  if  (   transaction . changedVseqVertices != null )  {   explicitChange =    transaction . changedVseqVertices . containsKey  ( this ) &&  (    transaction . changedVseqVertices . get  ( this ) . containsKey  (  ListPosition . NEXT ) ) ; } }  if  (   this . nextVertex == null )  {    this . nextVertex =  new  VersionedReferenceImpl  < VertexImpl >  ( this , null , "$nextVertex" ) ; }    this . nextVertex . setValidValue  (  ( VertexImpl ) nextVertex , transaction , explicitChange ) ; } }    @ Override public void setPrevVertex  (  Vertex prevVertex )  {  if  (  graph . isLoading  ( ) )  {    this . prevVertex =  new  VersionedReferenceImpl  < VertexImpl >  ( this ,  ( VertexImpl ) prevVertex , "$prevVertex" ) ; } else  {  TransactionImpl  transaction =  ( TransactionImpl )  graph . getCurrentTransaction  ( ) ;  if  (  transaction == null )  {  throw  new GraphException  ( "Current transaction is null." ) ; }  boolean  explicitChange = false ;  if  (   transaction . getState  ( ) ==  TransactionState . WRITING )  {  if  (   transaction . changedVseqVertices != null )  {   explicitChange =    transaction . changedVseqVertices . containsKey  ( this ) &&  (    transaction . changedVseqVertices . get  ( this ) . containsKey  (  ListPosition . PREV ) ) ; } }  if  (   this . prevVertex == null )  {    this . prevVertex =  new  VersionedReferenceImpl  < VertexImpl >  ( this , null , "$prevVertex" ) ; }    this . prevVertex . setValidValue  (  ( VertexImpl ) prevVertex , transaction , explicitChange ) ; } }    @ Override public void setIncidenceListVersion  (   long incidenceListVersion )  {  if  (   this . incidenceListVersion == null )  {    this . incidenceListVersion =  new  VersionedReferenceImpl  < Long >  ( this , null , "$incidenceListVersion" ) ; }    this . incidenceListVersion . setValidValue  ( incidenceListVersion ,  graph . getCurrentTransaction  ( ) ) ; }    @ Override public  long getIncidenceListVersion  ( )  {  if  (  incidenceListVersion == null )  {  return 0L ; }  Long  value =  incidenceListVersion . getValidValue  (  graph . getCurrentTransaction  ( ) ) ;  if  (  value == null )  {  return 0 ; }  return value ; }    @ Override public void putIncidenceBefore  (  InternalEdge targetIncidence ,  InternalEdge movedIncidence )  {  TransactionImpl  transaction =  ( TransactionImpl )  graph . getCurrentTransaction  ( ) ;  if  (  transaction == null )  {  throw  new GraphException  ( "Current transaction is null." ) ; }  if  (  !  isValid  ( ) )  {  throw  new GraphException  (   "Vertex " + this + " is not valid within the current transaction." ) ; }  if  (  !  targetIncidence . isValid  ( ) )  {  throw  new GraphException  (   "Incidence " + targetIncidence + " is not valid within the current transaction." ) ; }  if  (  !  movedIncidence . isValid  ( ) )  {  throw  new GraphException  (   "Incidence " + movedIncidence + " is not valid within the current transaction." ) ; }  synchronized  ( transaction )  {   super . putIncidenceBefore  ( targetIncidence , movedIncidence ) ;  assert  (     (  transaction != null ) &&  !  transaction . isReadOnly  ( ) &&  transaction . isValid  ( ) &&  (   transaction . getState  ( ) !=  TransactionState . NOTRUNNING ) ) ;  if  (   transaction . getState  ( ) ==  TransactionState . RUNNING )  {  if  (   transaction . changedIncidences == null )  {    transaction . changedIncidences =  new  HashMap  < VertexImpl ,  Map  < IncidenceImpl ,  Map  < ListPosition , Boolean > > >  ( 1 ,  TransactionManagerImpl . LOAD_FACTOR ) ; }   Map  < IncidenceImpl ,  Map  < ListPosition , Boolean > >  changedIncidences =   transaction . changedIncidences . get  ( this ) ;  if  (  changedIncidences == null )  {   changedIncidences =  new  HashMap  < IncidenceImpl ,  Map  < ListPosition , Boolean > >  ( 1 ,  TransactionManagerImpl . LOAD_FACTOR ) ;    transaction . changedIncidences . put  ( this , changedIncidences ) ; }   Map  < ListPosition , Boolean >  positionsMap =  changedIncidences . get  ( movedIncidence ) ;  if  (  positionsMap == null )  {   positionsMap =  new  HashMap  < ListPosition , Boolean >  ( 1 , 0.2f ) ;   changedIncidences . put  (  ( IncidenceImpl ) movedIncidence , positionsMap ) ; }   positionsMap . put  (  ListPosition . NEXT , true ) ;   positionsMap =  changedIncidences . get  ( targetIncidence ) ;  if  (  positionsMap == null )  {   positionsMap =  new  HashMap  < ListPosition , Boolean >  ( 1 , 0.2f ) ;   changedIncidences . put  (  ( IncidenceImpl ) targetIncidence , positionsMap ) ; }   positionsMap . put  (  ListPosition . PREV , false ) ; } } }    @ Override public void putIncidenceAfter  (  InternalEdge targetIncidence ,  InternalEdge movedIncidence )  {  TransactionImpl  transaction =  ( TransactionImpl )  graph . getCurrentTransaction  ( ) ;  if  (  transaction == null )  {  throw  new GraphException  ( "Current transaction is null." ) ; }  if  (  !  isValid  ( ) )  {  throw  new GraphException  (   "Vertex " + this + " is not valid within the current transaction." ) ; }  if  (  !  targetIncidence . isValid  ( ) )  {  throw  new GraphException  (   "Incidence " + targetIncidence + " is not valid within the current transaction." ) ; }  if  (  !  movedIncidence . isValid  ( ) )  {  throw  new GraphException  (   "Incidence " + movedIncidence + " is not valid within the current transaction." ) ; }  synchronized  ( transaction )  {   super . putIncidenceAfter  ( targetIncidence , movedIncidence ) ;  assert  (     (  transaction != null ) &&  !  transaction . isReadOnly  ( ) &&  transaction . isValid  ( ) &&  (   transaction . getState  ( ) !=  TransactionState . NOTRUNNING ) ) ;  if  (   transaction . getState  ( ) ==  TransactionState . RUNNING )  {  if  (   transaction . changedIncidences == null )  {    transaction . changedIncidences =  new  HashMap  < VertexImpl ,  Map  < IncidenceImpl ,  Map  < ListPosition , Boolean > > >  ( 1 ,  TransactionManagerImpl . LOAD_FACTOR ) ; }   Map  < IncidenceImpl ,  Map  < ListPosition , Boolean > >  changedIncidences =   transaction . changedIncidences . get  ( this ) ;  if  (  changedIncidences == null )  {   changedIncidences =  new  HashMap  < IncidenceImpl ,  Map  < ListPosition , Boolean > >  ( 1 ,  TransactionManagerImpl . LOAD_FACTOR ) ;    transaction . changedIncidences . put  ( this , changedIncidences ) ; }   Map  < ListPosition , Boolean >  positionsMap =  changedIncidences . get  ( movedIncidence ) ;  if  (  positionsMap == null )  {   positionsMap =  new  HashMap  < ListPosition , Boolean >  ( 1 , 0.2f ) ;   changedIncidences . put  (  ( IncidenceImpl ) movedIncidence , positionsMap ) ; }   positionsMap . put  (  ListPosition . PREV , true ) ;   positionsMap =  changedIncidences . get  ( targetIncidence ) ;  if  (  positionsMap == null )  {   positionsMap =  new  HashMap  < ListPosition , Boolean >  ( 1 , 0.2f ) ;   changedIncidences . put  (  ( IncidenceImpl ) targetIncidence , positionsMap ) ; }   positionsMap . put  (  ListPosition . NEXT , false ) ; } } }   protected void attributeChanged  (   VersionedDataObjectImpl  <  ? > versionedAttribute )  {  if  (  !  graph . isLoading  ( ) )  {  TransactionImpl  transaction =  ( TransactionImpl )  graph . getCurrentTransaction  ( ) ;  assert  (     (  transaction != null ) &&  (   transaction . getState  ( ) !=  TransactionState . NOTRUNNING ) &&  transaction . isValid  ( ) &&  !  transaction . isReadOnly  ( ) ) ;  if  (   transaction . getState  ( ) ==  TransactionState . RUNNING )  {  if  (  !  isValid  ( ) )  {  throw  new GraphException  (     "Trying to change the attribute '" + versionedAttribute + "' of Vertex " + this + ", that has been deleted within the current transaction." ) ; }  if  (   transaction . changedAttributes == null )  {    transaction . changedAttributes =  new  HashMap  < AttributedElement ,  Set  <  VersionedDataObject  <  ? > > >  ( 1 ,  TransactionManagerImpl . LOAD_FACTOR ) ; }   Set  <  VersionedDataObject  <  ? > >  attributes =   transaction . changedAttributes . get  ( this ) ;  if  (  attributes == null )  {   attributes =  new  HashSet  <  VersionedDataObject  <  ? > >  ( 1 ,  TransactionManagerImpl . LOAD_FACTOR ) ;    transaction . changedAttributes . put  ( this , attributes ) ; }   attributes . add  ( versionedAttribute ) ; } } }   abstract public  Set  <  VersionedDataObject  <  ? > > attributes  ( ) ;    @ Override public  Iterable  < Edge > incidences  ( )  {  return  new  AttributedElementIterable  < Edge >  (  super . incidences  ( ) , graph ) ; }    @ Override public  Iterable  < Edge > incidences  (  EdgeDirection dir )  {  return  new  AttributedElementIterable  < Edge >  (  super . incidences  ( dir ) , graph ) ; }    @ Override public  Iterable  < Edge > incidences  (  EdgeClass eclass ,  EdgeDirection dir )  {  return  new  AttributedElementIterable  < Edge >  (  super . incidences  ( eclass , dir ) , graph ) ; }    @ Override public  Iterable  < Edge > incidences  (   Class  <  ? extends Edge > eclass ,  EdgeDirection dir )  {  return  new  AttributedElementIterable  < Edge >  (  super . incidences  ( eclass , dir ) , graph ) ; }    @ Override public  Iterable  < Edge > incidences  (  EdgeClass eclass )  {  return  new  AttributedElementIterable  < Edge >  (  super . incidences  ( eclass ) , graph ) ; }    @ Override public  Iterable  < Edge > incidences  (   Class  <  ? extends Edge > eclass )  {  return  new  AttributedElementIterable  < Edge >  (  super . incidences  ( eclass ) , graph ) ; }    @ Override public boolean isValid  ( )  {      (  ( GraphImpl ) graph ) . vertexSync . readLock  ( ) . lock  ( ) ;  boolean  result =  super . isValid  ( ) ;      (  ( GraphImpl ) graph ) . vertexSync . readLock  ( ) . unlock  ( ) ;  return result ; }    @ Override public void sortIncidences  (   Comparator  < Edge > comp )  {  throw  new UnsupportedOperationException  ( ) ; }    @ Override public void internalSetDefaultValue  (  Attribute attr )  throws GraphIOException  {   attr . setDefaultTransactionValue  ( this ) ; } 
<<<<<<<
=======
   @ Override protected void setFirstIncidence  (  IncidenceImpl firstIncidence )  {  if  (  graph . isLoading  ( ) )  {    this . firstIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this , firstIncidence , "$firstIncidence" ) ; } else  {  if  (   this . firstIncidence == null )  {    this . firstIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this , null , "$firstIncidence" ) ; }    this . firstIncidence . setValidValue  ( firstIncidence ,  graph . getCurrentTransaction  ( ) ) ; } }
>>>>>>>
 
<<<<<<<
=======
   @ Override protected void setLastIncidence  (  IncidenceImpl lastIncidence )  {  if  (  graph . isLoading  ( ) )  {    this . lastIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this , lastIncidence , "$lastIncidence" ) ; } else  {  if  (   this . lastIncidence == null )  {    this . lastIncidence =  new  VersionedReferenceImpl  < IncidenceImpl >  ( this , null , "$lastIncidence" ) ; }    this . lastIncidence . setValidValue  ( lastIncidence ,  graph . getCurrentTransaction  ( ) ) ; } }
>>>>>>>
 }