  package     be . ugent . mmlab . rml . core ;   import      be . ugent . mmlab . rml . model . GraphMap ;  import      be . ugent . mmlab . rml . model . JoinCondition ;  import      be . ugent . mmlab . rml . model . LogicalSource ;  import      be . ugent . mmlab . rml . model . ObjectMap ;  import      be . ugent . mmlab . rml . model . PredicateMap ;  import      be . ugent . mmlab . rml . model . PredicateObjectMap ;  import      be . ugent . mmlab . rml . model . RMLMapping ;  import      be . ugent . mmlab . rml . model . ReferencingObjectMap ;  import      be . ugent . mmlab . rml . model . StdGraphMap ;  import      be . ugent . mmlab . rml . model . StdJoinCondition ;  import      be . ugent . mmlab . rml . model . StdLogicalSource ;  import      be . ugent . mmlab . rml . model . StdObjectMap ;  import      be . ugent . mmlab . rml . model . StdPredicateMap ;  import      be . ugent . mmlab . rml . model . StdPredicateObjectMap ;  import      be . ugent . mmlab . rml . model . StdReferencingObjectMap ;  import      be . ugent . mmlab . rml . model . StdSubjectMap ;  import      be . ugent . mmlab . rml . model . StdTriplesMap ;  import      be . ugent . mmlab . rml . model . SubjectMap ;  import      be . ugent . mmlab . rml . model . TriplesMap ;  import       be . ugent . mmlab . rml . model . reference . ReferenceIdentifier ;  import       be . ugent . mmlab . rml . model . reference . ReferenceIdentifierImpl ;  import      be . ugent . mmlab . rml . vocabulary . Vocab ;  import       be . ugent . mmlab . rml . vocabulary . Vocab . R2RMLTerm ;  import       be . ugent . mmlab . rml . vocabulary . Vocab . RMLTerm ;  import   java . io . IOException ;  import   java . net . HttpURLConnection ;  import   java . net . URL ;  import   java . util . HashMap ;  import   java . util . HashSet ;  import   java . util . List ;  import   java . util . Map ;  import   java . util . Set ;  import    java . util . logging . Level ;  import    java . util . logging . Logger ;  import        net . antidot . semantic . rdf . model . impl . sesame . SesameDataSet ;  import        net . antidot . semantic . rdf . rdb2rdf . r2rml . exception . InvalidR2RMLStructureException ;  import        net . antidot . semantic . rdf . rdb2rdf . r2rml . exception . InvalidR2RMLSyntaxException ;  import        net . antidot . semantic . rdf . rdb2rdf . r2rml . exception . R2RMLDataError ;  import     org . apache . commons . logging . Log ;  import     org . apache . commons . logging . LogFactory ;  import    org . openrdf . model . BNode ;  import    org . openrdf . model . Resource ;  import    org . openrdf . model . Statement ;  import    org . openrdf . model . URI ;  import    org . openrdf . model . Value ;  import    org . openrdf . model . ValueFactory ;  import     org . openrdf . model . impl . ValueFactoryImpl ;  import    org . openrdf . repository . RepositoryException ;  import    org . openrdf . rio . RDFFormat ;  import    org . openrdf . rio . RDFParseException ;   public abstract class RMLMappingFactory  {   private static Log  log =  LogFactory . getLog  (  RMLMappingFactory . class ) ;   private static ValueFactory  vf =  new ValueFactoryImpl  ( ) ;   public static RMLMapping extractRMLMapping  (  String fileToRMLFile )  throws InvalidR2RMLStructureException , InvalidR2RMLSyntaxException , R2RMLDataError , RepositoryException , RDFParseException , IOException  {  SesameDataSet  r2rmlMappingGraph =  new SesameDataSet  ( ) ;  if  (  !  RMLEngine . isLocalFile  ( fileToRMLFile ) )  {  HttpURLConnection  con =  ( HttpURLConnection )   new URL  ( fileToRMLFile ) . openConnection  ( ) ;   con . setRequestMethod  ( "HEAD" ) ;  if  (   con . getResponseCode  ( ) ==  HttpURLConnection . HTTP_OK )   r2rmlMappingGraph . addURI  ( fileToRMLFile ,  RDFFormat . TURTLE ) ; } else  {   r2rmlMappingGraph . loadDataFromFile  ( fileToRMLFile ,  RDFFormat . TURTLE ) ; }   log . debug  (    "[RMLMappingFactory:extractRMLMapping] Number of R2RML triples in file " + fileToRMLFile + " : " +  r2rmlMappingGraph . getSize  ( ) ) ;   replaceShortcuts  ( r2rmlMappingGraph ) ;   launchPreChecks  ( r2rmlMappingGraph ) ;   Map  < Resource , TriplesMap >  triplesMapResources =  extractTripleMapResources  ( r2rmlMappingGraph ) ;   log . debug  (       "[RMLMappingFactory:extractRMLMapping] Number of RML triples with " + " type " +  R2RMLTerm . TRIPLES_MAP_CLASS + " in file " + fileToRMLFile + " : " +  triplesMapResources . size  ( ) ) ;  for ( Resource triplesMapResource :  triplesMapResources . keySet  ( ) )  {   extractTriplesMap  ( r2rmlMappingGraph , triplesMapResource , triplesMapResources ) ; }  RMLMapping  result =  new RMLMapping  (  triplesMapResources . values  ( ) ) ;  return result ; }   private static void replaceShortcuts  (  SesameDataSet r2rmlMappingGraph )  {   Map  < URI , URI >  shortcutPredicates =  new  HashMap  < URI , URI >  ( ) ;   shortcutPredicates . put  (  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . SUBJECT ) ,  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . SUBJECT_MAP ) ) ;   shortcutPredicates . put  (  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . PREDICATE ) ,  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . PREDICATE_MAP ) ) ;   shortcutPredicates . put  (  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . OBJECT ) ,  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . OBJECT_MAP ) ) ;   shortcutPredicates . put  (  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . GRAPH ) ,  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . GRAPH_MAP ) ) ;  for ( URI u :  shortcutPredicates . keySet  ( ) )  {   List  < Statement >  shortcutTriples =  r2rmlMappingGraph . tuplePattern  ( null , u , null ) ;   log . debug  (     "[RMLMappingFactory:replaceShortcuts] Number of R2RML shortcuts found " + "for " +  u . getLocalName  ( ) + " : " +  shortcutTriples . size  ( ) ) ;  for ( Statement shortcutTriple : shortcutTriples )  {   r2rmlMappingGraph . remove  (  shortcutTriple . getSubject  ( ) ,  shortcutTriple . getPredicate  ( ) ,  shortcutTriple . getObject  ( ) ) ;  BNode  blankMap =  vf . createBNode  ( ) ;  URI  pMap =  vf . createURI  (   shortcutPredicates . get  ( u ) . toString  ( ) ) ;  URI  pConstant =  vf . createURI  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . CONSTANT ) ;   r2rmlMappingGraph . add  (  shortcutTriple . getSubject  ( ) , pMap , blankMap ) ;   r2rmlMappingGraph . add  ( blankMap , pConstant ,  shortcutTriple . getObject  ( ) ) ; } } }   private static  Map  < Resource , TriplesMap > extractTripleMapResources  (  SesameDataSet r2rmlMappingGraph )  throws InvalidR2RMLStructureException  {   Map  < Resource , TriplesMap >  triplesMapResources =  new  HashMap  < Resource , TriplesMap >  ( ) ;  URI  p =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +   Vocab . R2RMLTerm . SUBJECT_MAP ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( null , p , null ) ;  if  (  statements . isEmpty  ( ) )  {   log . warn  ( "[RMLMappingFactory:extractRMLMapping] No subject statement found. Exit..." ) ; } else  {  for ( Statement s : statements )  {   List  < Statement >  otherStatements =  r2rmlMappingGraph . tuplePattern  (  s . getSubject  ( ) , p , null ) ;  if  (   otherStatements . size  ( ) > 1 )  {  throw  new InvalidR2RMLStructureException  (    "[RMLMappingFactory:extractRMLMapping] " +  s . getSubject  ( ) + " has many subjectMap " + "(or subject) but only one is required." ) ; } else  {   triplesMapResources . put  (  s . getSubject  ( ) ,  new StdTriplesMap  ( null , null , null ,   s . getSubject  ( ) . stringValue  ( ) ) ) ; } } }  return triplesMapResources ; }   private static void launchPreChecks  (  SesameDataSet r2rmlMappingGraph )  throws InvalidR2RMLStructureException  {  URI  p =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . PREDICATE_OBJECT_MAP ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( null , p , null ) ;  for ( Statement s : statements )  {   p =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . SUBJECT_MAP ) ;   List  < Statement >  otherStatements =  r2rmlMappingGraph . tuplePattern  (  s . getSubject  ( ) , p , null ) ;  if  (  otherStatements . isEmpty  ( ) )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:launchPreChecks] You have a triples map without subject map : " +   s . getSubject  ( ) . stringValue  ( ) + "." ) ; } } }   private static void extractTriplesMap  (  SesameDataSet r2rmlMappingGraph ,  Resource triplesMapSubject ,   Map  < Resource , TriplesMap > triplesMapResources )  throws InvalidR2RMLStructureException , InvalidR2RMLSyntaxException , R2RMLDataError  {  if  (  log . isDebugEnabled  ( ) )  {   log . debug  (  "[RMLMappingFactory:extractTriplesMap] Extract TriplesMap subject : " +  triplesMapSubject . stringValue  ( ) ) ; }  TriplesMap  result =  triplesMapResources . get  ( triplesMapSubject ) ;  LogicalSource  logicalSource =  extractLogicalSource  ( r2rmlMappingGraph , triplesMapSubject ) ;   Set  < GraphMap >  graphMaps =  new  HashSet  < GraphMap >  ( ) ;  SubjectMap  subjectMap =  extractSubjectMap  ( r2rmlMappingGraph , triplesMapSubject , graphMaps , result ) ;   Set  < PredicateObjectMap >  predicateObjectMaps =  extractPredicateObjectMaps  ( r2rmlMappingGraph , triplesMapSubject , graphMaps , result , triplesMapResources ) ;   log . debug  (  "[RMLMappingFactory:extractTriplesMap] Current number of created graphMaps : " +  graphMaps . size  ( ) ) ;  for ( PredicateObjectMap predicateObjectMap : predicateObjectMaps )  {   result . addPredicateObjectMap  ( predicateObjectMap ) ; }   result . setLogicalSource  ( logicalSource ) ;   result . setSubjectMap  ( subjectMap ) ;   log . debug  (   "[RMLMappingFactory:extractTriplesMap] Extract of TriplesMap subject : " +  triplesMapSubject . stringValue  ( ) + " done." ) ; }   private static  Set  < PredicateObjectMap > extractPredicateObjectMaps  (  SesameDataSet r2rmlMappingGraph ,  Resource triplesMapSubject ,   Set  < GraphMap > graphMaps ,  TriplesMap result ,   Map  < Resource , TriplesMap > triplesMapResources )  throws InvalidR2RMLStructureException , R2RMLDataError , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractPredicateObjectMaps] Extract predicate-object maps..." ) ;  URI  p =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . PREDICATE_OBJECT_MAP ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( triplesMapSubject , p , null ) ;   Set  < PredicateObjectMap >  predicateObjectMaps =  new  HashSet  < PredicateObjectMap >  ( ) ;  try  {  for ( Statement statement : statements )  {  PredicateObjectMap  predicateObjectMap =  extractPredicateObjectMap  ( r2rmlMappingGraph , triplesMapSubject ,  ( Resource )  statement . getObject  ( ) , graphMaps , triplesMapResources ) ;   predicateObjectMap . setOwnTriplesMap  ( result ) ;   predicateObjectMaps . add  ( predicateObjectMap ) ; } }  catch (   ClassCastException e )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractPredicateObjectMaps] " + "A resource was expected in object of predicateObjectMap of " +  triplesMapSubject . stringValue  ( ) ) ; }   log . debug  (  "[RMLMappingFactory:extractPredicateObjectMaps] Number of extracted predicate-object maps : " +  predicateObjectMaps . size  ( ) ) ;  return predicateObjectMaps ; }   private static PredicateObjectMap extractPredicateObjectMap  (  SesameDataSet r2rmlMappingGraph ,  Resource triplesMapSubject ,  Resource predicateObject ,   Set  < GraphMap > savedGraphMaps ,   Map  < Resource , TriplesMap > triplesMapResources )  throws InvalidR2RMLStructureException , R2RMLDataError , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractPredicateObjectMap] Extract predicate-object map.." ) ;  URI  p =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . PREDICATE_MAP ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( predicateObject , p , null ) ;  if  (   statements . size  ( ) < 1 )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractSubjectMap] " +  predicateObject . stringValue  ( ) + " has no predicate map defined : one or more is required." ) ; }   Set  < PredicateMap >  predicateMaps =  new  HashSet  < PredicateMap >  ( ) ;  try  {  for ( Statement statement : statements )  {   log . info  (  "[RMLMappingFactory] saved Graphs " + savedGraphMaps ) ;  PredicateMap  predicateMap =  extractPredicateMap  ( r2rmlMappingGraph ,  ( Resource )  statement . getObject  ( ) , savedGraphMaps ) ;   predicateMaps . add  ( predicateMap ) ; } }  catch (   ClassCastException e )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractPredicateObjectMaps] " + "A resource was expected in object of predicateMap of " +  predicateObject . stringValue  ( ) ) ; }  URI  o =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . OBJECT_MAP ) ;   statements =  r2rmlMappingGraph . tuplePattern  ( predicateObject , o , null ) ;  if  (   statements . size  ( ) < 1 )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractPredicateObjectMap] " +  predicateObject . stringValue  ( ) + " has no object map defined : one or more is required." ) ; }   Set  < ObjectMap >  objectMaps =  new  HashSet  < ObjectMap >  ( ) ;   Set  < ReferencingObjectMap >  refObjectMaps =  new  HashSet  < ReferencingObjectMap >  ( ) ;  try  {  for ( Statement statement : statements )  {   log . debug  ( "[RMLMappingFactory:extractPredicateObjectMap] Try to extract object map.." ) ;  ReferencingObjectMap  refObjectMap =  extractReferencingObjectMap  ( r2rmlMappingGraph ,  ( Resource )  statement . getObject  ( ) , savedGraphMaps , triplesMapResources ) ;  if  (  refObjectMap != null )  {   refObjectMaps . add  ( refObjectMap ) ;  continue ; }  ObjectMap  objectMap =  extractObjectMap  ( r2rmlMappingGraph ,  ( Resource )  statement . getObject  ( ) , savedGraphMaps , triplesMapResources ) ;   objectMap . setOwnTriplesMap  (  triplesMapResources . get  ( triplesMapSubject ) ) ;   log . debug  (    "[RMLMappingFactory:extractPredicateObjectMap] ownTriplesMap attempted " +  triplesMapResources . get  (  statement . getContext  ( ) ) + " for object " +   statement . getObject  ( ) . stringValue  ( ) ) ;   objectMaps . add  ( objectMap ) ; } }  catch (   ClassCastException e )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractPredicateObjectMaps] " + "A resource was expected in object of objectMap of " +  predicateObject . stringValue  ( ) ) ; }  PredicateObjectMap  predicateObjectMap =  new StdPredicateObjectMap  ( predicateMaps , objectMaps , refObjectMaps ) ;   Set  < GraphMap >  graphMaps =  new  HashSet  < GraphMap >  ( ) ;   Set  < Value >  graphMapValues =  extractValuesFromResource  ( r2rmlMappingGraph , predicateObject ,  R2RMLTerm . GRAPH_MAP ) ;  if  (  graphMapValues != null )  {   graphMaps =  extractGraphMapValues  ( r2rmlMappingGraph , graphMapValues , savedGraphMaps ) ;   log . info  (  "[RMLMappingFactory] graph Maps returned " + graphMaps ) ; }   predicateObjectMap . setGraphMaps  ( graphMaps ) ;   log . debug  ( "[RMLMappingFactory:extractPredicateObjectMap] Extract predicate-object map done." ) ;  return predicateObjectMap ; }   private static ReferencingObjectMap extractReferencingObjectMap  (  SesameDataSet r2rmlMappingGraph ,  Resource object ,   Set  < GraphMap > graphMaps ,   Map  < Resource , TriplesMap > triplesMapResources )  throws InvalidR2RMLStructureException , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractReferencingObjectMap] Extract referencing object map.." ) ;  URI  parentTriplesMap =  ( URI )  extractValueFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . PARENT_TRIPLES_MAP ) ;   Set  < JoinCondition >  joinConditions =  extractJoinConditions  ( r2rmlMappingGraph , object ) ;  if  (   parentTriplesMap == null &&  !  joinConditions . isEmpty  ( ) )  {  throw  new InvalidR2RMLStructureException  (    "[RMLMappingFactory:extractReferencingObjectMap] " +  object . stringValue  ( ) + " has no parentTriplesMap map defined whereas one or more joinConditions exist" + " : exactly one parentTripleMap is required." ) ; }  if  (   parentTriplesMap == null &&  joinConditions . isEmpty  ( ) )  {   log . debug  ( "[RMLMappingFactory:extractReferencingObjectMap] This object map is not a referencing object map." ) ;  return null ; }  boolean  contains = false ;  TriplesMap  parent = null ;  for ( Resource triplesMapResource :  triplesMapResources . keySet  ( ) )  {  if  (   triplesMapResource . stringValue  ( ) . equals  (  parentTriplesMap . stringValue  ( ) ) )  {   contains = true ;   parent =  triplesMapResources . get  ( triplesMapResource ) ;   log . debug  (  "[RMLMappingFactory:extractReferencingObjectMap] Parent triples map found : " +  triplesMapResource . stringValue  ( ) ) ;  break ; } }  if  (  ! contains )  {  throw  new InvalidR2RMLStructureException  (     "[RMLMappingFactory:extractReferencingObjectMap] " +  object . stringValue  ( ) + " reference to parent triples maps is broken : " +  parentTriplesMap . stringValue  ( ) + " not found." ) ; }  ReferencingObjectMap  refObjectMap =  new StdReferencingObjectMap  ( null , parent , joinConditions ) ;   log . debug  ( "[RMLMappingFactory:extractReferencingObjectMap] Extract referencing object map done." ) ;  return refObjectMap ; }   private static  Set  < JoinCondition > extractJoinConditions  (  SesameDataSet r2rmlMappingGraph ,  Resource object )  throws InvalidR2RMLStructureException , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractJoinConditions] Extract join conditions.." ) ;   Set  < JoinCondition >  result =  new  HashSet  < JoinCondition >  ( ) ;  URI  p =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . JOIN_CONDITION ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( object , p , null ) ;  try  {  for ( Statement statement : statements )  {  Resource  jc =  ( Resource )  statement . getObject  ( ) ;  String  child =  extractLiteralFromTermMap  ( r2rmlMappingGraph , jc ,  R2RMLTerm . CHILD ) ;  String  parent =  extractLiteralFromTermMap  ( r2rmlMappingGraph , jc ,  R2RMLTerm . PARENT ) ;  if  (   parent == null ||  child == null )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractReferencingObjectMap] " +  object . stringValue  ( ) + " must have exactly two properties child and parent. " ) ; }   result . add  (  new StdJoinCondition  ( child , parent ) ) ; } }  catch (   ClassCastException e )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractJoinConditions] " + "A resource was expected in object of predicateMap of " +  object . stringValue  ( ) ) ; }   log . debug  ( "[RMLMappingFactory:extractJoinConditions] Extract join conditions done." ) ;  return result ; }   private static ObjectMap extractObjectMap  (  SesameDataSet r2rmlMappingGraph ,  Resource object ,   Set  < GraphMap > graphMaps ,   Map  < Resource , TriplesMap > triplesMapResources )  throws InvalidR2RMLStructureException , R2RMLDataError , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractObjectMap] Extract object map.." ) ;  Value  constantValue =  extractValueFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . CONSTANT ) ;  String  stringTemplate =  extractLiteralFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . TEMPLATE ) ;  String  languageTag =  extractLiteralFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . LANGUAGE ) ;  URI  termType =  ( URI )  extractValueFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . TERM_TYPE ) ;  URI  dataType =  ( URI )  extractValueFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . DATATYPE ) ;  String  inverseExpression =  extractLiteralFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . INVERSE_EXPRESSION ) ;  ReferenceIdentifier  referenceValue =  extractReferenceIdentifier  ( r2rmlMappingGraph , object ) ;  StdObjectMap  result =  new StdObjectMap  ( null , constantValue , dataType , languageTag , stringTemplate , termType , inverseExpression , referenceValue ) ;   log . debug  ( "[RMLMappingFactory:extractObjectMap] Extract object map done." ) ;  return result ; }   private static ReferenceIdentifier extractReferenceIdentifier  (  SesameDataSet r2rmlMappingGraph ,  Resource resource )  throws InvalidR2RMLStructureException  {  String  columnValueStr =  extractLiteralFromTermMap  ( r2rmlMappingGraph , resource ,  R2RMLTerm . COLUMN ) ;  String  referenceValueStr =  extractLiteralFromTermMap  ( r2rmlMappingGraph , resource ,  RMLTerm . REFERENCE ) ;  if  (   columnValueStr != null &&  referenceValueStr != null )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractReferenceIdentifier] " + resource + " has a reference and column defined." ) ; }  if  (  columnValueStr != null )  {  return  ReferenceIdentifierImpl . buildFromR2RMLConfigFile  ( columnValueStr ) ; }  return  ReferenceIdentifierImpl . buildFromR2RMLConfigFile  ( referenceValueStr ) ; }   private static PredicateMap extractPredicateMap  (  SesameDataSet r2rmlMappingGraph ,  Resource object ,   Set  < GraphMap > graphMaps )  throws InvalidR2RMLStructureException , R2RMLDataError , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractPredicateMap] Extract predicate map.." ) ;  Value  constantValue =  extractValueFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . CONSTANT ) ;  String  stringTemplate =  extractLiteralFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . TEMPLATE ) ;  URI  termType =  ( URI )  extractValueFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . TERM_TYPE ) ;  String  inverseExpression =  extractLiteralFromTermMap  ( r2rmlMappingGraph , object ,  R2RMLTerm . INVERSE_EXPRESSION ) ;  ReferenceIdentifier  referenceValue =  extractReferenceIdentifier  ( r2rmlMappingGraph , object ) ;  PredicateMap  result =  new StdPredicateMap  ( null , constantValue , stringTemplate , inverseExpression , referenceValue , termType ) ;   log . debug  ( "[RMLMappingFactory:extractPredicateMap] Extract predicate map done." ) ;  return result ; }   private static SubjectMap extractSubjectMap  (  SesameDataSet r2rmlMappingGraph ,  Resource triplesMapSubject ,   Set  < GraphMap > savedGraphMaps ,  TriplesMap ownTriplesMap )  throws InvalidR2RMLStructureException , R2RMLDataError , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractPredicateObjectMaps] Extract subject map..." ) ;  URI  p =  r2rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +  R2RMLTerm . SUBJECT_MAP ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( triplesMapSubject , p , null ) ;  if  (  statements . isEmpty  ( ) )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractSubjectMap] " + triplesMapSubject + " has no subject map defined." ) ; }  if  (   statements . size  ( ) > 1 )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractSubjectMap] " + triplesMapSubject + " has too many subject map defined." ) ; }  Resource  subjectMap =  ( Resource )   statements . get  ( 0 ) . getObject  ( ) ;   log . debug  (  "[RMLMappingFactory:extractTriplesMap] Found subject map : " +  subjectMap . stringValue  ( ) ) ;  Value  constantValue =  extractValueFromTermMap  ( r2rmlMappingGraph , subjectMap ,  R2RMLTerm . CONSTANT ) ;  String  stringTemplate =  extractLiteralFromTermMap  ( r2rmlMappingGraph , subjectMap ,  R2RMLTerm . TEMPLATE ) ;  URI  termType =  ( URI )  extractValueFromTermMap  ( r2rmlMappingGraph , subjectMap ,  R2RMLTerm . TERM_TYPE ) ;  String  inverseExpression =  extractLiteralFromTermMap  ( r2rmlMappingGraph , subjectMap ,  R2RMLTerm . INVERSE_EXPRESSION ) ;  ReferenceIdentifier  referenceValue =  extractReferenceIdentifier  ( r2rmlMappingGraph , subjectMap ) ;   Set  < URI >  classIRIs =  extractURIsFromTermMap  ( r2rmlMappingGraph , subjectMap ,  R2RMLTerm . CLASS ) ;   Set  < GraphMap >  graphMaps =  new  HashSet  < GraphMap >  ( ) ;   Set  < Value >  graphMapValues =  extractValuesFromResource  ( r2rmlMappingGraph , subjectMap ,  R2RMLTerm . GRAPH_MAP ) ;  if  (  graphMapValues != null )  {   graphMaps =  extractGraphMapValues  ( r2rmlMappingGraph , graphMapValues , savedGraphMaps ) ;   log . info  (  "[RMLMappingFactory] graph Maps returned " + graphMaps ) ; }  SubjectMap  result =  new StdSubjectMap  ( ownTriplesMap , constantValue , stringTemplate , termType , inverseExpression , referenceValue , classIRIs , graphMaps ) ;   log . debug  ( "[RMLMappingFactory:extractSubjectMap] Subject map extracted." ) ;  return result ; }   private static  Set  < GraphMap > extractGraphMapValues  (  SesameDataSet r2rmlMappingGraph ,   Set  < Value > graphMapValues ,   Set  < GraphMap > savedGraphMaps )  throws InvalidR2RMLStructureException  {   Set  < GraphMap >  graphMaps =  new  HashSet  < GraphMap >  ( ) ;  for ( Value graphMap : graphMapValues )  {  boolean  found = false ;  GraphMap  graphMapFound = null ;  if  ( found )  {   graphMaps . add  ( graphMapFound ) ; } else  {  GraphMap  newGraphMap = null ;  try  {   newGraphMap =  extractGraphMap  ( r2rmlMappingGraph ,  ( Resource ) graphMap ) ; }  catch (   R2RMLDataError ex )  {    Logger . getLogger  (   RMLMappingFactory . class . getName  ( ) ) . log  (  Level . SEVERE , null , ex ) ; }  catch (   InvalidR2RMLSyntaxException ex )  {    Logger . getLogger  (   RMLMappingFactory . class . getName  ( ) ) . log  (  Level . SEVERE , null , ex ) ; }   savedGraphMaps . add  ( newGraphMap ) ;   graphMaps . add  ( newGraphMap ) ; } }  return graphMaps ; }   private static GraphMap extractGraphMap  (  SesameDataSet r2rmlMappingGraph ,  Resource graphMap )  throws InvalidR2RMLStructureException , R2RMLDataError , InvalidR2RMLSyntaxException  {   log . debug  ( "[RMLMappingFactory:extractPredicateObjectMaps] Extract graph map..." ) ;  Value  constantValue =  extractValueFromTermMap  ( r2rmlMappingGraph , graphMap ,  R2RMLTerm . CONSTANT ) ;  String  stringTemplate =  extractLiteralFromTermMap  ( r2rmlMappingGraph , graphMap ,  R2RMLTerm . TEMPLATE ) ;  String  inverseExpression =  extractLiteralFromTermMap  ( r2rmlMappingGraph , graphMap ,  R2RMLTerm . INVERSE_EXPRESSION ) ;  ReferenceIdentifier  referenceValue =  extractReferenceIdentifier  ( r2rmlMappingGraph , graphMap ) ;  URI  termType =  ( URI )  extractValueFromTermMap  ( r2rmlMappingGraph , graphMap ,  R2RMLTerm . TERM_TYPE ) ;  GraphMap  result =  new StdGraphMap  ( constantValue , stringTemplate , inverseExpression , referenceValue , termType ) ;   log . debug  ( "[RMLMappingFactory:extractPredicateObjectMaps] Graph map extracted." ) ;  return result ; }   private static String extractLiteralFromTermMap  (  SesameDataSet r2rmlMappingGraph ,  Resource termType ,  Enum term )  throws InvalidR2RMLStructureException  {  URI  p =  getTermURI  ( r2rmlMappingGraph , term ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( termType , p , null ) ;  if  (  statements . isEmpty  ( ) )  {  return null ; }  if  (   statements . size  ( ) > 1 )  {  throw  new InvalidR2RMLStructureException  (     "[RMLMappingFactory:extractValueFromTermMap] " + termType + " has too many " + term + " predicate defined." ) ; }  String  result =    statements . get  ( 0 ) . getObject  ( ) . stringValue  ( ) ;  if  (  log . isDebugEnabled  ( ) )  {   log . debug  (    "[RMLMappingFactory:extractLiteralFromTermMap] Extracted " + term + " : " + result ) ; }  return result ; }   private static Value extractValueFromTermMap  (  SesameDataSet r2rmlMappingGraph ,  Resource termType ,  Enum term )  throws InvalidR2RMLStructureException  {  URI  p =  getTermURI  ( r2rmlMappingGraph , term ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( termType , p , null ) ;  if  (  statements . isEmpty  ( ) )  {  return null ; }  if  (   statements . size  ( ) > 1 )  {  throw  new InvalidR2RMLStructureException  (     "[RMLMappingFactory:extractValueFromTermMap] " + termType + " has too many " + term + " predicate defined." ) ; }  Value  result =   statements . get  ( 0 ) . getObject  ( ) ;   log . debug  (    "[RMLMappingFactory:extractValueFromTermMap] Extracted " + term + " : " +  result . stringValue  ( ) ) ;  return result ; }   private static  Set  < Value > extractValuesFromResource  (  SesameDataSet r2rmlMappingGraph ,  Resource termType ,  Enum term )  throws InvalidR2RMLStructureException  {  URI  p =  getTermURI  ( r2rmlMappingGraph , term ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( termType , p , null ) ;  if  (  statements . isEmpty  ( ) )  {  return null ; }   Set  < Value >  values =  new  HashSet  < Value >  ( ) ;  for ( Statement statement : statements )  {  Value  value =  statement . getObject  ( ) ;   log . debug  (    "[RMLMappingFactory:extractURIsFromTermMap] Extracted " + term + " : " +  value . stringValue  ( ) ) ;   values . add  ( value ) ; }  return values ; }   private static  Set  < URI > extractURIsFromTermMap  (  SesameDataSet r2rmlMappingGraph ,  Resource termType ,  R2RMLTerm term )  throws InvalidR2RMLStructureException  {  URI  p =  getTermURI  ( r2rmlMappingGraph , term ) ;   List  < Statement >  statements =  r2rmlMappingGraph . tuplePattern  ( termType , p , null ) ;  if  (  statements . isEmpty  ( ) )  {  return null ; }   Set  < URI >  uris =  new  HashSet  < URI >  ( ) ;  for ( Statement statement : statements )  {  URI  uri =  ( URI )  statement . getObject  ( ) ;   log . debug  (    "[RMLMappingFactory:extractURIsFromTermMap] Extracted " + term + " : " +  uri . stringValue  ( ) ) ;   uris . add  ( uri ) ; }  return uris ; }   private static URI getTermURI  (  SesameDataSet r2rmlMappingGraph ,  Enum term )  throws InvalidR2RMLStructureException  {  String  namespace =  Vocab . R2RML_NAMESPACE ;  if  (  term instanceof  Vocab . RMLTerm )  {   namespace =  Vocab . RML_NAMESPACE ; } else  if  (  !  (  term instanceof R2RMLTerm ) )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractValueFromTermMap] " + term + " is not valid." ) ; }  return  r2rmlMappingGraph . URIref  (  namespace + term ) ; }   private static LogicalSource extractLogicalSource  (  SesameDataSet rmlMappingGraph ,  Resource triplesMapSubject )  throws InvalidR2RMLStructureException , InvalidR2RMLSyntaxException , R2RMLDataError  {   Vocab . QLTerm  referenceFormulation = null ;  URI  pTable =  rmlMappingGraph . URIref  (   Vocab . R2RML_NAMESPACE +   Vocab . R2RMLTerm . LOGICAL_TABLE ) ;  URI  pSource =  rmlMappingGraph . URIref  (   Vocab . RML_NAMESPACE +   Vocab . RMLTerm . LOGICAL_SOURCE ) ;   List  < Statement >  sTable =  rmlMappingGraph . tuplePattern  ( triplesMapSubject , pTable , null ) ;   List  < Statement >  sSource =  rmlMappingGraph . tuplePattern  ( triplesMapSubject , pSource , null ) ;  if  (   !  sTable . isEmpty  ( ) &&  !  sSource . isEmpty  ( ) )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractLogicalSource] " + triplesMapSubject + " has both a source and table defined." ) ; }  if  (  !  sTable . isEmpty  ( ) )  {   extractLogicalTable  ( ) ; }   List  < Statement >  statements = sSource ;  if  (  statements . isEmpty  ( ) )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractLogicalSource] " + triplesMapSubject + " has no logical source defined." ) ; }  if  (   statements . size  ( ) > 1 )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractLogicalSource] " + triplesMapSubject + " has too many logical source defined." ) ; }  Resource  blankLogicalSource =  ( Resource )   statements . get  ( 0 ) . getObject  ( ) ;  if  (  referenceFormulation == null )   referenceFormulation =  getReferenceFormulation  ( rmlMappingGraph , blankLogicalSource ) ;  if  (  referenceFormulation == null )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractLogicalSource] " + triplesMapSubject + " has an unknown query language." ) ; }  URI  pName =  rmlMappingGraph . URIref  (   Vocab . RML_NAMESPACE +   Vocab . RMLTerm . SOURCE ) ;   List  < Statement >  statementsName =  rmlMappingGraph . tuplePattern  ( blankLogicalSource , pName , null ) ;  URI  pView =  rmlMappingGraph . URIref  (   Vocab . RML_NAMESPACE +   Vocab . RMLTerm . ITERATOR ) ;   List  < Statement >  statementsView =  rmlMappingGraph . tuplePattern  ( blankLogicalSource , pView , null ) ;  LogicalSource  logicalSource = null ;  if  (  !  statementsName . isEmpty  ( ) )  {  if  (   statementsName . size  ( ) > 1 )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractLogicalSource] " + triplesMapSubject + " has too many logical source name defined." ) ; }  String  file =    statementsName . get  ( 0 ) . getObject  ( ) . stringValue  ( ) ;  String  iterator = null ;  if  (  !  statementsView . isEmpty  ( ) )  {   iterator =    statementsView . get  ( 0 ) . getObject  ( ) . stringValue  ( ) ; }   logicalSource =  new StdLogicalSource  ( iterator , file , referenceFormulation ) ; } else  { }   log . debug  (  "[RMLMappingFactory:extractLogicalSource] Logical source extracted : " + logicalSource ) ;  return logicalSource ; }   private static  Vocab . QLTerm getReferenceFormulation  (  SesameDataSet rmlMappingGraph ,  Resource subject )  throws InvalidR2RMLStructureException  {  URI  pReferenceFormulation =  rmlMappingGraph . URIref  (   Vocab . RML_NAMESPACE +   Vocab . RMLTerm . REFERENCE_FORMULATION ) ;   List  < Statement >  statements =  rmlMappingGraph . tuplePattern  ( subject , pReferenceFormulation , null ) ;  if  (   statements . size  ( ) > 1 )  {  throw  new InvalidR2RMLStructureException  (   "[RMLMappingFactory:extractLogicalSource] " + subject + " has too many query language defined." ) ; }  if  (  statements . isEmpty  ( ) )  {  return   Vocab . QLTerm . SQL_CLASS ; }  Resource  object =  ( Resource )   statements . get  ( 0 ) . getObject  ( ) ;  return  Vocab . getQLTerms  (  object . stringValue  ( ) ) ; }   private static void extractLogicalTable  ( )  { } }