  package   org . sqlproc . engine ;   public interface SqlFeature  {   public static final String  ORACLE = "ORACLE" ;   public static final String  HSQLDB = "HSQLDB" ;   public static final String  MYSQL = "MYSQL" ;   public static final String  INFORMIX = "INFORMIX" ;   public static final String  WILDCARD_CHARACTER = "WILDCARD_CHARACTER" ;   public static final String  DEFAULT_WILDCARD_CHARACTER = "%" ;   public static final String  SURROUND_QUERY_LIKE = "SURROUND_QUERY_LIKE" ;   public static final String  SURROUND_QUERY_MIN_LEN = "SURROUND_QUERY_MIN_LEN" ;   public static final Integer  DEFAULT_SURROUND_QUERY_MIN_LEN = 2 ;   public static final String  LIKE_STRING = "LIKE_STRING" ;   public static final String  DEFAULT_LIKE_STRING = "like" ;   public static final String  METHODS_ENUM_IN = "METHODS_ENUM_IN" ;   public static final  String  [ ]  DEFAULT_METHODS_ENUM_IN =  new String  [ ]  { "getCode" , "getValue" , "getName" , "name" } ;   public static final String  METHODS_ENUM_OUT = "METHODS_ENUM_OUT" ;   public static final  String  [ ]  DEFAULT_METHODS_ENUM_OUT =  new String  [ ]  { "fromCode" , "fromValue" , "valueOf" } ;   public static final String  ID = "ID" ;   public static final String  IGNORE_INPROPER_IN = "IGNORE_INPROPER_IN" ;   public static final String  IGNORE_INPROPER_OUT = "IGNORE_INPROPER_OUT" ;   public static final String  JDBC = "JDBC" ;   public static final String  LIMIT_FROM_TO = "LIMIT_FROM_TO" ;   public static final String  LIMIT_TO = "LIMIT_TO" ;   public static final String  HSQLDB_DEFAULT_LIMIT_FROM_TO = "select limit $F $M $s" ;   public static final String  HSQLDB_DEFAULT_LIMIT_TO = "select top $M $s" ;   public static final String  ORACLE_DEFAULT_LIMIT_FROM_TO = "select * from ( select row_.*, rownum rownum_ from ($S) row_ where rownum <= $m) where rownum_ > $F" ;   public static final String  ORACLE_DEFAULT_LIMIT_TO = "select * from ($S) where rownum <= $m" ;   public static final String  MYSQL_DEFAULT_LIMIT_FROM_TO = "$S limit $F, $M" ;   public static final String  MYSQL_DEFAULT_LIMIT_TO = "$S limit $M" ;   public static final String  POSTGRESQL_DEFAULT_LIMIT_FROM_TO = "$S limit $M offset $F" ;   public static final String  POSTGRESQL_DEFAULT_LIMIT_TO = "$S limit $M" ;   public static final String  INFORMIX_DEFAULT_LIMIT_FROM_TO = "select skip $F first $M $s" ;   public static final String  INFORMIX_DEFAULT_LIMIT_TO = "select first $M $s" ;   public static final String 
<<<<<<<
 DB2_DEFAULT_LIMIT_FROM_TO = "select * from (select row_.*, rownumber() over() rownum_ from ($S) row_) where rownum_ <= $m and rownum_ > $F"
=======
 MSSQL = "MSSQL"
>>>>>>>
 ;   public static final String 
<<<<<<<
 DB2_DEFAULT_LIMIT_TO = "select * from (select row_.*, rownumber() over() rownum_ from ($S) row_) where rownum_ <= $m"
=======
 MSSQL_DEFAULT_LIMIT_TO = "select top ($M) $s"
>>>>>>>
 ;   public static final String  SEQ = "SEQ" ;   public static final String  HSQLDB_DEFAULT_SEQ = "call next value for $n" ;   public static final String  ORACLE_DEFAULT_SEQ = "select $n.nextval from dual" ;   public static final String  POSTGRESQL_DEFAULT_SEQ = "select nextval('$n')" ;   public static final String  INFORMIX_DEFAULT_SEQ = "SELECT FIRST 1 $n.NEXTVAL FROM systables" ;   public static final String 
<<<<<<<
 DB2_DEFAULT_SEQ = "values nextval for $n"
=======
 IDSEL_JDBC = "JDBC"
>>>>>>>
 ;   public static final String  DEFAULT_SEQ_NAME = "SQLPROC_SEQUENCE" ;   public static final String  IDSEL = "IDSEL" ;   public static final String  HSQLDB_DEFAULT_IDSEL = "call identity()" ;   public static final String  MYSQL_DEFAULT_IDSEL = "select last_insert_id()" ;   public static final String  INFORMIX_DEFAULT_IDSEL = "SELECT FIRST 1 dbinfo('bigserial') FROM systables" ;   public static final String 
<<<<<<<
 DB2_DEFAULT_IDSEL = "SELECT identity_val_local() FROM SYSIBM.DUAL"
=======
 MSSQL_DEFAULT_IDSEL = IDSEL_JDBC
>>>>>>>
 ;   public static final String  DEFAULT_VERSION_COLUMN = "version" ; }