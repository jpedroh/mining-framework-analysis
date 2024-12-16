  package    de . deepamehta . core . impl ;   import     de . deepamehta . core . model . AssociationModel ;  import     de . deepamehta . core . model . IndexMode ;  import     de . deepamehta . core . model . RelatedAssociationModel ;  import     de . deepamehta . core . model . RelatedTopicModel ;  import     de . deepamehta . core . model . SimpleValue ;  import     de . deepamehta . core . model . TopicModel ;  import     de . deepamehta . core . service . ResultList ;  import      de . deepamehta . core . storage . spi . DeepaMehtaTransaction ;  import      de . deepamehta . core . storage . spi . DeepaMehtaStorage ;  import static    java . util . Arrays . asList ;  import   java . util . Iterator ;  import   java . util . List ;  import    java . util . logging . Logger ;   public class StorageDecorator  {   private DeepaMehtaStorage  storage ;   private final Logger  logger =  Logger . getLogger  (   getClass  ( ) . getName  ( ) ) ;   public StorageDecorator  (  DeepaMehtaStorage storage )  {    this . storage = storage ; }  TopicModel fetchTopic  (   long topicId )  {  return  storage . fetchTopic  ( topicId ) ; }  TopicModel fetchTopic  (  String key ,  SimpleValue value )  {  return  storage . fetchTopic  ( key ,  value . value  ( ) ) ; }   List  < TopicModel > fetchTopics  (  String key ,  SimpleValue value )  {  return  storage . fetchTopics  ( key ,  value . value  ( ) ) ; }   List  < TopicModel > queryTopics  (  String searchTerm ,  String fieldUri )  {  return  storage . queryTopics  ( fieldUri , searchTerm ) ; }   Iterator  < TopicModel > fetchAllTopics  ( )  {  return  storage . fetchAllTopics  ( ) ; }  void storeTopic  (  TopicModel model )  {   storage . storeTopic  ( model ) ; }  void storeTopicUri  (   long topicId ,  String uri )  {   storage . storeTopicUri  ( topicId , uri ) ; }  void storeTopicTypeUri  (   long topicId ,  String topicTypeUri )  {   storage . storeTopicTypeUri  ( topicId , topicTypeUri ) ; }  void storeTopicValue  (   long topicId ,  SimpleValue value )  {   storeTopicValue  ( topicId , value ,  asList  (  IndexMode . OFF ) , null , null ) ; }  void storeTopicValue  (   long topicId ,  SimpleValue value ,   List  < IndexMode > indexModes ,  String indexKey ,  SimpleValue indexValue )  {   storage . storeTopicValue  ( topicId , value , indexModes , indexKey , indexValue ) ; }  void indexTopicValue  (   long topicId ,  IndexMode indexMode ,  String indexKey ,  SimpleValue indexValue )  {   storage . indexTopicValue  ( topicId , indexMode , indexKey , indexValue ) ; }  void deleteTopic  (   long topicId )  {   storage . deleteTopic  ( topicId ) ; }  AssociationModel fetchAssociation  (   long assocId )  {  return  storage . fetchAssociation  ( assocId ) ; }  AssociationModel fetchAssociation  (  String assocTypeUri ,   long topicId1 ,   long topicId2 ,  String roleTypeUri1 ,  String roleTypeUri2 )  {   List  < AssociationModel >  assocs =  fetchAssociations  ( assocTypeUri , topicId1 , topicId2 , roleTypeUri1 , roleTypeUri2 ) ;  switch  (  assocs . size  ( ) )  {   case 0 :  return null ;   case 1 :  return  assocs . get  ( 0 ) ;   default :  throw  new RuntimeException  (              "Ambiguity: there are " +  assocs . size  ( ) + " \"" + assocTypeUri + "\" associations (topicId1=" + topicId1 + ", topicId2=" + topicId2 + ", " + "roleTypeUri1=\"" + roleTypeUri1 + "\", roleTypeUri2=\"" + roleTypeUri2 + "\")" ) ; } }   List  < AssociationModel > fetchAssociations  (  String assocTypeUri ,   long topicId1 ,   long topicId2 ,  String roleTypeUri1 ,  String roleTypeUri2 )  {  return  storage . fetchAssociations  ( assocTypeUri , topicId1 , topicId2 , roleTypeUri1 , roleTypeUri2 ) ; }  AssociationModel fetchAssociationBetweenTopicAndAssociation  (  String assocTypeUri ,   long topicId ,   long assocId ,  String topicRoleTypeUri ,  String assocRoleTypeUri )  {   List  < AssociationModel >  assocs =  fetchAssociationsBetweenTopicAndAssociation  ( assocTypeUri , topicId , assocId , topicRoleTypeUri , assocRoleTypeUri ) ;  switch  (  assocs . size  ( ) )  {   case 0 :  return null ;   case 1 :  return  assocs . get  ( 0 ) ;   default :  throw  new RuntimeException  (              "Ambiguity: there are " +  assocs . size  ( ) + " \"" + assocTypeUri + "\" associations (topicId=" + topicId + ", assocId=" + assocId + ", " + "topicRoleTypeUri=\"" + topicRoleTypeUri + "\", assocRoleTypeUri=\"" + assocRoleTypeUri + "\")" ) ; } }   List  < AssociationModel > fetchAssociationsBetweenTopicAndAssociation  (  String assocTypeUri ,   long topicId ,   long assocId ,  String topicRoleTypeUri ,  String assocRoleTypeUri )  {  return  storage . fetchAssociationsBetweenTopicAndAssociation  ( assocTypeUri , topicId , assocId , topicRoleTypeUri , assocRoleTypeUri ) ; }   Iterator  < AssociationModel > fetchAllAssociations  ( )  {  return  storage . fetchAllAssociations  ( ) ; }    long  [ ] fetchPlayerIds  (   long assocId )  {  return  storage . fetchPlayerIds  ( assocId ) ; }  void storeAssociationUri  (   long assocId ,  String uri )  {   storage . storeAssociationUri  ( assocId , uri ) ; }  void storeAssociationTypeUri  (   long assocId ,  String assocTypeUri )  {   storage . storeAssociationTypeUri  ( assocId , assocTypeUri ) ; }  void storeRoleTypeUri  (   long assocId ,   long playerId ,  String roleTypeUri )  {   storage . storeRoleTypeUri  ( assocId , playerId , roleTypeUri ) ; }  void storeAssociationValue  (   long assocId ,  SimpleValue value )  {   storeAssociationValue  ( assocId , value ,  asList  (  IndexMode . OFF ) , null , null ) ; }  void storeAssociationValue  (   long assocId ,  SimpleValue value ,   List  < IndexMode > indexModes ,  String indexKey ,  SimpleValue indexValue )  {   storage . storeAssociationValue  ( assocId , value , indexModes , indexKey , indexValue ) ; }  void indexAssociationValue  (   long assocId ,  IndexMode indexMode ,  String indexKey ,  SimpleValue indexValue )  {   storage . indexAssociationValue  ( assocId , indexMode , indexKey , indexValue ) ; }  void storeAssociation  (  AssociationModel model )  {   storage . storeAssociation  ( model ) ; }  void deleteAssociation  (   long assocId )  {   storage . deleteAssociation  ( assocId ) ; }   List  < AssociationModel > fetchTopicAssociations  (   long topicId )  {  return  storage . fetchTopicAssociations  ( topicId ) ; }   List  < AssociationModel > fetchAssociationAssociations  (   long assocId )  {  return  storage . fetchAssociationAssociations  ( assocId ) ; }  RelatedTopicModel fetchTopicRelatedTopic  (   long topicId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri )  {   ResultList  < RelatedTopicModel >  topics =  fetchTopicRelatedTopics  ( topicId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri , 0 ) ;  switch  (  topics . getSize  ( ) )  {   case 0 :  return null ;   case 1 :  return   topics . iterator  ( ) . next  ( ) ;   default :  throw  new RuntimeException  (              "Ambiguity: there are " +  topics . getSize  ( ) + " related topics (topicId=" + topicId + ", assocTypeUri=\"" + assocTypeUri + "\", myRoleTypeUri=\"" + myRoleTypeUri + "\", " + "othersRoleTypeUri=\"" + othersRoleTypeUri + "\", othersTopicTypeUri=\"" + othersTopicTypeUri + "\")" ) ; } }   ResultList  < RelatedTopicModel > fetchTopicRelatedTopics  (   long topicId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri ,   int maxResultSize )  {   List  < RelatedTopicModel >  relTopics =  storage . fetchTopicRelatedTopics  ( topicId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri ) ;  return  new ResultList  (  relTopics . size  ( ) , relTopics ) ; }   ResultList  < RelatedTopicModel > fetchTopicRelatedTopics  (   long topicId ,   List  < String > assocTypeUris ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri ,   int maxResultSize )  {   ResultList  < RelatedTopicModel >  result =  new ResultList  ( ) ;  for ( String assocTypeUri : assocTypeUris )  {   ResultList  < RelatedTopicModel >  res =  fetchTopicRelatedTopics  ( topicId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri , maxResultSize ) ;   result . addAll  ( res ) ; }  return result ; }  RelatedAssociationModel fetchTopicRelatedAssociation  (   long topicId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersAssocTypeUri )  {   ResultList  < RelatedAssociationModel >  assocs =  fetchTopicRelatedAssociations  ( topicId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersAssocTypeUri ) ;  switch  (  assocs . getSize  ( ) )  {   case 0 :  return null ;   case 1 :  return   assocs . iterator  ( ) . next  ( ) ;   default :  throw  new RuntimeException  (              "Ambiguity: there are " +  assocs . getSize  ( ) + " related associations (topicId=" + topicId + ", assocTypeUri=\"" + assocTypeUri + "\", myRoleTypeUri=\"" + myRoleTypeUri + "\", " + "othersRoleTypeUri=\"" + othersRoleTypeUri + "\", othersAssocTypeUri=\"" + othersAssocTypeUri + "\")" ) ; } }   ResultList  < RelatedAssociationModel > fetchTopicRelatedAssociations  (   long topicId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersAssocTypeUri )  {   List  < RelatedAssociationModel >  relAssocs =  storage . fetchTopicRelatedAssociations  ( topicId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersAssocTypeUri ) ;  return  new ResultList  (  relAssocs . size  ( ) , relAssocs ) ; }  RelatedTopicModel fetchAssociationRelatedTopic  (   long assocId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri )  {   ResultList  < RelatedTopicModel >  topics =  fetchAssociationRelatedTopics  ( assocId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri , 0 ) ;  switch  (  topics . getSize  ( ) )  {   case 0 :  return null ;   case 1 :  return   topics . iterator  ( ) . next  ( ) ;   default :  throw  new RuntimeException  (              "Ambiguity: there are " +  topics . getSize  ( ) + " related topics (assocId=" + assocId + ", assocTypeUri=\"" + assocTypeUri + "\", myRoleTypeUri=\"" + myRoleTypeUri + "\", " + "othersRoleTypeUri=\"" + othersRoleTypeUri + "\", othersTopicTypeUri=\"" + othersTopicTypeUri + "\")" ) ; } }   ResultList  < RelatedTopicModel > fetchAssociationRelatedTopics  (   long assocId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri ,   int maxResultSize )  {   List  < RelatedTopicModel >  relTopics =  storage . fetchAssociationRelatedTopics  ( assocId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri ) ;  return  new ResultList  (  relTopics . size  ( ) , relTopics ) ; }   ResultList  < RelatedTopicModel > fetchAssociationRelatedTopics  (   long assocId ,   List  < String > assocTypeUris ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri ,   int maxResultSize )  {   ResultList  < RelatedTopicModel >  result =  new ResultList  ( ) ;  for ( String assocTypeUri : assocTypeUris )  {   ResultList  < RelatedTopicModel >  res =  fetchAssociationRelatedTopics  ( assocId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri , maxResultSize ) ;   result . addAll  ( res ) ; }  return result ; }  RelatedAssociationModel fetchAssociationRelatedAssociation  (   long assocId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersAssocTypeUri )  {   ResultList  < RelatedAssociationModel >  assocs =  fetchAssociationRelatedAssociations  ( assocId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersAssocTypeUri ) ;  switch  (  assocs . getSize  ( ) )  {   case 0 :  return null ;   case 1 :  return   assocs . iterator  ( ) . next  ( ) ;   default :  throw  new RuntimeException  (               "Ambiguity: there are " +  assocs . getSize  ( ) + " related associations (assocId=" + assocId + ", assocTypeUri=\"" + assocTypeUri + "\", myRoleTypeUri=\"" + myRoleTypeUri + "\", " + "othersRoleTypeUri=\"" + othersRoleTypeUri + "\", othersAssocTypeUri=\"" + othersAssocTypeUri + "\"),\nresult=" + assocs ) ; } }   ResultList  < RelatedAssociationModel > fetchAssociationRelatedAssociations  (   long assocId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersAssocTypeUri )  {   List  < RelatedAssociationModel >  relAssocs =  storage . fetchAssociationRelatedAssociations  ( assocId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersAssocTypeUri ) ;  return  new ResultList  (  relAssocs . size  ( ) , relAssocs ) ; }  RelatedTopicModel fetchRelatedTopic  (   long objectId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri )  {   ResultList  < RelatedTopicModel >  topics =  fetchRelatedTopics  ( objectId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri ) ;  switch  (  topics . getSize  ( ) )  {   case 0 :  return null ;   case 1 :  return   topics . iterator  ( ) . next  ( ) ;   default :  throw  new RuntimeException  (              "Ambiguity: there are " +  topics . getSize  ( ) + " related topics (objectId=" + objectId + ", assocTypeUri=\"" + assocTypeUri + "\", myRoleTypeUri=\"" + myRoleTypeUri + "\", " + "othersRoleTypeUri=\"" + othersRoleTypeUri + "\", othersTopicTypeUri=\"" + othersTopicTypeUri + "\")" ) ; } }   ResultList  < RelatedTopicModel > fetchRelatedTopics  (   long objectId ,  String assocTypeUri ,  String myRoleTypeUri ,  String othersRoleTypeUri ,  String othersTopicTypeUri )  {   List  < RelatedTopicModel >  relTopics =  storage . fetchRelatedTopics  ( objectId , assocTypeUri , myRoleTypeUri , othersRoleTypeUri , othersTopicTypeUri ) ;  return  new ResultList  (  relTopics . size  ( ) , relTopics ) ; }  Object fetchProperty  (   long id ,  String propUri )  {  return  storage . fetchProperty  ( id , propUri ) ; }  boolean hasProperty  (   long id ,  String propUri )  {  return  storage . hasProperty  ( id , propUri ) ; }   List  < TopicModel > fetchTopicsByProperty  (  String propUri ,  Object propValue )  {  return  storage . fetchTopicsByProperty  ( propUri , propValue ) ; }   List  < TopicModel > fetchTopicsByPropertyRange  (  String propUri ,  Number from ,  Number to )  {  return  storage . fetchTopicsByPropertyRange  ( propUri , from , to ) ; }   List  < AssociationModel > fetchAssociationsByProperty  (  String propUri ,  Object propValue )  {  return  storage . fetchAssociationsByProperty  ( propUri , propValue ) ; }   List  < AssociationModel > fetchAssociationsByPropertyRange  (  String propUri ,  Number from ,  Number to )  {  return  storage . fetchAssociationsByPropertyRange  ( propUri , from , to ) ; }  void storeTopicProperty  (   long topicId ,  String propUri ,  Object propValue ,  boolean addToIndex )  {   storage . storeTopicProperty  ( topicId , propUri , propValue , addToIndex ) ; }  void storeAssociationProperty  (   long assocId ,  String propUri ,  Object propValue ,  boolean addToIndex )  {   storage . storeAssociationProperty  ( assocId , propUri , propValue , addToIndex ) ; }  void removeTopicProperty  (   long topicId ,  String propUri )  {   storage . deleteTopicProperty  ( topicId , propUri ) ; }  void removeAssociationProperty  (   long assocId ,  String propUri )  {   storage . deleteAssociationProperty  ( assocId , propUri ) ; }  DeepaMehtaTransaction beginTx  ( )  {  return  storage . beginTx  ( ) ; }  boolean init  ( )  {  boolean  isCleanInstall =  storage . setupRootNode  ( ) ;  if  ( isCleanInstall )  {   logger . info  ( "Starting with a fresh DB -- Setting migration number to 0" ) ;   storeMigrationNr  ( 0 ) ; }  return isCleanInstall ; }  void shutdown  ( )  {   storage . shutdown  ( ) ; }   int fetchMigrationNr  ( )  {  return  ( Integer )  storage . fetchProperty  ( 0 , "core_migration_nr" ) ; }  void storeMigrationNr  (   int migrationNr )  {   storage . storeTopicProperty  ( 0 , "core_migration_nr" , migrationNr , false ) ; }  Object getDatabaseVendorObject  ( )  {  return  storage . getDatabaseVendorObject  ( ) ; }  Object getDatabaseVendorObject  (   long objectId )  {  return  storage . getDatabaseVendorObject  ( objectId ) ; } }