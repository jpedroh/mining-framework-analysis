  package    com . jcabi . http . mock ;   import   java . net . URI ;  import   org . hamcrest . Matcher ;  import   org . hamcrest . Matchers ;    @ SuppressWarnings  ( "PMD.ProhibitPublicStaticMethods" ) public final class MkQueryMatchers  {   private MkQueryMatchers  ( )  { }   public static  Matcher  < MkQuery > hasBody  (   final  Matcher  < String > matcher )  {  return  new MkQueryBodyMatcher  ( matcher ) ; }   public static  Matcher  < MkQuery > hasHeader  (   final String header ,   final  Matcher  <  Iterable  <  ? extends String > > matcher )  {  return  new MkQueryHeaderMatcher  ( header , matcher ) ; }   public static  Matcher  < MkQuery > hasPath  (   final  Matcher  < String > path )  {  return  new MkQueryUriMatcher  (  Matchers .  < URI > hasProperty  ( "rawPath" , path ) ) ; }   public static  Matcher  < MkQuery > hasQuery  (   final  Matcher  < String > 
<<<<<<<
path
=======
query
>>>>>>>
 )  {  return 
<<<<<<<
 new MkQueryUriMatcher  (  Matchers .  < URI > hasProperty  ( "rawQuery" , path ) )
=======
 new MkQueryQueryMatcher  ( query )
>>>>>>>
 ; } }