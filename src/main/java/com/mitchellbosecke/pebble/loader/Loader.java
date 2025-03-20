  package    com . mitchellbosecke . pebble . loader ;   import    com . mitchellbosecke . pebble . PebbleEngine ;  import   java . io . Reader ;   public interface Loader  <  T >  {  Reader getReader  (  String templateName )  throws LoaderException ;  void setCharset  (  String charset ) ;  void setPrefix  (  String prefix ) ;  void setSuffix  (  String suffix ) ;  String resolveRelativePath  (  String relativePath ,  String anchorPath ) ;  T createCacheKey  (  String templateName ) ; 
<<<<<<<
=======
 Reader getReader  (  T cacheKey ) ;
>>>>>>>
 }