  package    de . slackspace . openkeepass . domain ;   import   java . util . ArrayList ;  import   java . util . List ;  import   java . util . UUID ;  import     javax . xml . bind . annotation . XmlAccessType ;  import     javax . xml . bind . annotation . XmlAccessorType ;  import     javax . xml . bind . annotation . XmlElement ;  import     javax . xml . bind . annotation . XmlRootElement ;  import      de . slackspace . openkeepass . domain . filter . Filter ;  import      de . slackspace . openkeepass . domain . filter . ListFilter ;    @ XmlRootElement  (  name = "KeePassFile" )  @ XmlAccessorType  (  XmlAccessType . FIELD ) public class KeePassFile  implements  KeePassFileElement  {    @ XmlElement  (  name = "Meta" ) private Meta  meta ;    @ XmlElement  (  name = "Root" ) private Group  root ;  KeePassFile  ( )  { }   public Meta getMeta  ( )  {  return meta ; }   public Group getRoot  ( )  {  return root ; }   public  List  < Group > getTopGroups  ( )  {  if  (    root != null &&   root . getGroups  ( ) != null &&    root . getGroups  ( ) . size  ( ) == 1 )  {  return    root . getGroups  ( ) . get  ( 0 ) . getGroups  ( ) ; }  return  new  ArrayList  < Group >  ( ) ; }   public  List  < Entry > getTopEntries  ( )  {  if  (    root != null &&   root . getGroups  ( ) != null &&    root . getGroups  ( ) . size  ( ) == 1 )  {  return    root . getGroups  ( ) . get  ( 0 ) . getEntries  ( ) ; }  return  new  ArrayList  < Entry >  ( ) ; }   public Entry getEntryByTitle  (  String title )  {   List  < Entry >  entries =  getEntriesByTitle  ( title , true ) ;  if  (  !  entries . isEmpty  ( ) )  {  return  entries . get  ( 0 ) ; }  return null ; }   public  List  < Entry > getEntriesByTitle  (   final String title ,   final boolean matchExactly )  {   List  < Entry >  allEntries =  new  ArrayList  < Entry >  ( ) ;  if  (  root != null )  {   getEntries  ( root , allEntries ) ; }  return  ListFilter . filter  ( allEntries ,  new  Filter  < Entry >  ( )  {    @ Override public boolean matches  (  Entry item )  {  if  ( matchExactly )  {  if  (    item . getTitle  ( ) != null &&   item . getTitle  ( ) . equalsIgnoreCase  ( title ) )  {  return true ; } } else  {  if  (    item . getTitle  ( ) != null &&    item . getTitle  ( ) . toLowerCase  ( ) . contains  (  title . toLowerCase  ( ) ) )  {  return true ; } }  return false ; } } ) ; }   public  List  < Group > getGroupsByName  (   final String name ,   final boolean matchExactly )  {   List  < Group >  allGroups =  new  ArrayList  < Group >  ( ) ;  if  (  root != null )  {   getGroups  ( root , allGroups ) ; }  return  ListFilter . filter  ( allGroups ,  new  Filter  < Group >  ( )  {    @ Override public boolean matches  (  Group item )  {  if  ( matchExactly )  {  if  (    item . getName  ( ) != null &&   item . getName  ( ) . equalsIgnoreCase  ( name ) )  {  return true ; } } else  {  if  (    item . getName  ( ) != null &&    item . getName  ( ) . toLowerCase  ( ) . contains  (  name . toLowerCase  ( ) ) )  {  return true ; } }  return false ; } } ) ; }   public  List  < Entry > getEntries  ( )  {   List  < Entry >  allEntries =  new  ArrayList  < Entry >  ( ) ;  if  (  root != null )  {   getEntries  ( root , allEntries ) ; }  return allEntries ; }   public  List  < Group > getGroups  ( )  {   List  < Group >  allGroups =  new  ArrayList  < Group >  ( ) ;  if  (  root != null )  {   getGroups  ( root , allGroups ) ; }  return allGroups ; }   public Group getGroupByName  (  String name )  {   List  < Group >  groups =  getGroupsByName  ( name , true ) ;  if  (  !  groups . isEmpty  ( ) )  {  return  groups . get  ( 0 ) ; }  return null ; }   private static void getEntries  (  Group parentGroup ,   List  < Entry > entries )  {   List  < Group >  groups =  parentGroup . getGroups  ( ) ;   entries . addAll  (  parentGroup . getEntries  ( ) ) ;  if  (  !  groups . isEmpty  ( ) )  {  for ( Group group : groups )  {   getEntries  ( group , entries ) ; } }  return ; }   private static void getGroups  (  Group parentGroup ,   List  < Group > groups )  {   List  < Group >  parentGroups =  parentGroup . getGroups  ( ) ;   groups . addAll  ( parentGroups ) ;  if  (  !  parentGroups . isEmpty  ( ) )  {  for ( Group group : parentGroups )  {   getGroups  ( group , groups ) ; } }  return ; }    @ Deprecated public Entry getEntryByUUID  (   final UUID UUID )  {  return  getEntryByUuid  ( UUID ) ; }   public Entry getEntryByUuid  (   final UUID uuid )  {   List  < Entry >  allEntries =  getEntries  ( ) ;   List  < Entry >  entries =  ListFilter . filter  ( allEntries ,  new  Filter  < Entry >  ( )  {    @ Override public boolean matches  (  Entry item )  {  if  (    item . getUuid  ( ) != null &&    item . getUuid  ( ) . compareTo  ( uuid ) == 0 )  {  return true ; } else  {  return false ; } } } ) ;  if  (   entries . size  ( ) == 1 )  {  return  entries . get  ( 0 ) ; } else  {  return null ; } }    @ Deprecated public Group getGroupByUUID  (   final UUID UUID )  {   List  < Group >  allGroups =  getGroups  ( ) ;   List  < Group >  groups =  ListFilter . filter  ( allGroups ,  new  Filter  < Group >  ( )  {    @ Override public boolean matches  (  Group item )  {  if  (    item . getUuid  ( ) != null &&    item . getUuid  ( ) . compareTo  ( UUID ) == 0 )  {  return true ; } else  {  return false ; } } } ) ;  if  (   groups . size  ( ) == 1 )  {  return  groups . get  ( 0 ) ; } else  {  return null ; } }   public Group getGroupByUuid  (   final UUID uuid )  {   List  < Group >  allGroups =  getGroups  ( ) ;   List  < Group >  groups =  ListFilter . filter  ( allGroups ,  new  Filter  < Group >  ( )  {    @ Override public boolean matches  (  Group item )  {  if  (    item . getUuid  ( ) != null &&    item . getUuid  ( ) . compareTo  ( uuid ) == 0 )  {  return true ; } else  {  return false ; } } } ) ;  if  (   groups . size  ( ) == 1 )  {  return  groups . get  ( 0 ) ; } else  {  return null ; } }   public KeePassFile  (  KeePassFileContract keePassFileContract )  {    this . meta =  keePassFileContract . getMeta  ( ) ;    this . root =  keePassFileContract . getRoot  ( ) ; } }