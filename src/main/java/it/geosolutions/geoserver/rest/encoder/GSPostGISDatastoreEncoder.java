  package     it . geosolutions . geoserver . rest . encoder ;   import       it . geosolutions . geoserver . rest . encoder . utils . NestedElementEncoder ;  import       it . geosolutions . geoserver . rest . encoder . utils . PropertyXMLEncoder ;   public class GSPostGISDatastoreEncoder  extends PropertyXMLEncoder  {   private NestedElementEncoder  connectionParameters =  new NestedElementEncoder  ( "connectionParameters" ) ;   public GSPostGISDatastoreEncoder  ( )  {  super  ( "dataStore" ) ;   addType  ( "PostGIS" ) ;   addDatabaseType  ( "postgis" ) ;   addContent  (  connectionParameters . getRoot  ( ) ) ; }   public void defaultInit  ( )  {   addMinConnections  ( 1 ) ;   addMaxConnections  ( 10 ) ;   addFetchSize  ( 1000 ) ;   addConnectionTimeout  ( 20 ) ;   addLooseBBox  ( true ) ;   addPreparedStatements  ( false ) ;   addMaxOpenPreparedStatements  ( 50 ) ; }   public void addName  (  String name )  {   add  ( "name" , name ) ; }   public void addDescription  (  String description )  {   add  ( "description" , description ) ; }   public void addType  (  String type )  {   add  ( "type" , type ) ; }   public void addEnabled  (  boolean enabled )  {   add  ( "enabled" ,  Boolean . toString  ( enabled ) ) ; }   public void addNamespace  (  String namespace )  {   connectionParameters . add  ( "namespace" , namespace ) ; }   public void addHost  (  String host )  {   connectionParameters . add  ( "host" , host ) ; }   public void addPort  (   int port )  {   connectionParameters . add  ( "port" ,  Integer . toString  ( port ) ) ; }   public void addDatabase  (  String database )  {   connectionParameters . add  ( "database" , database ) ; }   public void addSchema  (  String schema )  {   connectionParameters . add  ( "schema" , schema ) ; }   public void addUser  (  String user )  {   connectionParameters . add  ( "user" , user ) ; }   public void addPassword  (  String password )  {   connectionParameters . add  ( "passwd" , password ) ; }   public void addDatabaseType  (  String dbtype )  {   connectionParameters . add  ( "dbtype" , dbtype ) ; }   public void addJndiReferenceName  (  String jndiReferenceName )  {   connectionParameters . add  ( "jndiReferenceName" , jndiReferenceName ) ; }   public void addExposePrimaryKeys  (  boolean exposePrimaryKeys )  {   connectionParameters . add  ( "Expose primary keys" ,  Boolean . toString  ( exposePrimaryKeys ) ) ; }   public void addMaxConnections  (   int maxConnections )  {   connectionParameters . add  ( "max connections" ,  Integer . toString  ( maxConnections ) ) ; }   public void addMinConnections  (   int minConnections )  {   connectionParameters . add  ( "min connections" ,  Integer . toString  ( minConnections ) ) ; }   public void addFetchSize  (   int fetchSize )  {   connectionParameters . add  ( "fetch size" ,  Integer . toString  ( fetchSize ) ) ; }   public void addConnectionTimeout  (   int seconds )  {   connectionParameters . add  ( "Connection timeout" ,  Integer . toString  ( seconds ) ) ; }   public void addValidateConnections  (  boolean validateConnections )  {   connectionParameters . add  ( "validate connections" ,  Boolean . toString  ( validateConnections ) ) ; }   public void addPrimaryKeyMetadataTable  (  String primaryKeyMetadataTable )  {   connectionParameters . add  ( "Primary key metadata table" , primaryKeyMetadataTable ) ; }   public void addLooseBBox  (  boolean looseBBox )  {   connectionParameters . add  ( "Loose bbox" ,  Boolean . toString  ( looseBBox ) ) ; }   public void addPreparedStatements  (  boolean preparedStatements )  {   connectionParameters . add  ( "preparedStatements" ,  Boolean . toString  ( preparedStatements ) ) ; }   public void addMaxOpenPreparedStatements  (   int maxOpenPreparedStatements )  {   connectionParameters . add  ( "Max open prepared statements" ,  Integer . toString  ( maxOpenPreparedStatements ) ) ; } 
<<<<<<<
=======
  public void setName  (  String name )  {   set  ( "name" , name ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setDescription  (  String description )  {   set  ( "description" , description ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setType  (  String type )  {   set  ( "type" , type ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setEnabled  (  boolean enabled )  {   set  ( "enabled" ,  Boolean . toString  ( enabled ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setNamespace  (  String namespace )  {   connectionParameters . set  ( "namespace" , namespace ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setHost  (  String host )  {   connectionParameters . set  ( "host" , host ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setPort  (   int port )  {   connectionParameters . set  ( "port" ,  Integer . toString  ( port ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setDatabase  (  String database )  {   connectionParameters . set  ( "database" , database ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setSchema  (  String schema )  {   connectionParameters . set  ( "schema" , schema ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setUser  (  String user )  {   connectionParameters . set  ( "user" , user ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setPassword  (  String password )  {   connectionParameters . set  ( "passwd" , password ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setDatabaseType  (  String dbtype )  {   connectionParameters . set  ( "dbtype" , dbtype ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setJndiReferenceName  (  String jndiReferenceName )  {   connectionParameters . set  ( "jndiReferenceName" , jndiReferenceName ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setExposePrimaryKeys  (  boolean exposePrimaryKeys )  {   connectionParameters . set  ( "Expose primary keys" ,  Boolean . toString  ( exposePrimaryKeys ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setMaxConnections  (   int maxConnections )  {   connectionParameters . set  ( "max connections" ,  Integer . toString  ( maxConnections ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setMinConnections  (   int minConnections )  {   connectionParameters . set  ( "min connections" ,  Integer . toString  ( minConnections ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setFetchSize  (   int fetchSize )  {   connectionParameters . set  ( "fetch size" ,  Integer . toString  ( fetchSize ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setConnectionTimeout  (   int seconds )  {   connectionParameters . set  ( "Connection timeout" ,  Integer . toString  ( seconds ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setValidateConnections  (  boolean validateConnections )  {   connectionParameters . set  ( "validate connections" ,  Boolean . toString  ( validateConnections ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setPrimaryKeyMetadataTable  (  String primaryKeyMetadataTable )  {   connectionParameters . set  ( "Primary key metadata table" , primaryKeyMetadataTable ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setLooseBBox  (  boolean looseBBox )  {   connectionParameters . set  ( "Loose bbox" ,  Boolean . toString  ( looseBBox ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setPreparedStatements  (  boolean preparedStatements )  {   connectionParameters . set  ( "preparedStatements" ,  Boolean . toString  ( preparedStatements ) ) ; }
>>>>>>>
 
<<<<<<<
=======
  public void setMaxOpenPreparedStatements  (   int maxOpenPreparedStatements )  {   connectionParameters . set  ( "Max open prepared statements" ,  Integer . toString  ( maxOpenPreparedStatements ) ) ; }
>>>>>>>
 }