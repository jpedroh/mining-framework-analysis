  package   com . moandjiezana . toml ;   import static     com . moandjiezana . toml . MapValueWriter . MAP_VALUE_WRITER ;  import static     com . moandjiezana . toml . ObjectValueWriter . OBJECT_VALUE_WRITER ;  import static     com . moandjiezana . toml . ValueWriters . WRITERS ;  import   java . io . File ;  import   java . io . FileWriter ;  import   java . io . IOException ;  import   java . io . OutputStream ;  import   java . io . OutputStreamWriter ;  import   java . io . StringWriter ;  import   java . io . Writer ;  import   java . util . List ;  import   java . util . Map ;  import   java . util . TimeZone ;  import static    com . moandjiezana . toml . MapValueWriter .  * ;  import static    com . moandjiezana . toml . ObjectValueWriter .  * ;   public class TomlWriter  {   public static class Builder  {   private  int  keyIndentation ;   private  int  tableIndentation ;   private  int  arrayDelimiterPadding = 0 ;   private TimeZone  timeZone =  TimeZone . getTimeZone  ( "UTC" ) ;   private boolean  showFractionalSeconds = false ;   public  TomlWriter . Builder indentValuesBy  (   int spaces )  {    this . keyIndentation = spaces ;  return this ; }   public  TomlWriter . Builder indentTablesBy  (   int spaces )  {    this . tableIndentation = spaces ;  return this ; }   public  TomlWriter . Builder timeZone  (  TimeZone timeZone )  {    this . timeZone = timeZone ;  return this ; }   public  TomlWriter . Builder padArrayDelimitersBy  (   int spaces )  {    this . arrayDelimiterPadding = spaces ;  return this ; }   public TomlWriter build  ( )  {  return  new TomlWriter  ( keyIndentation , tableIndentation , arrayDelimiterPadding , timeZone , showFractionalSeconds ) ; }   public  TomlWriter . Builder showFractionalSeconds  ( )  {    this . showFractionalSeconds = true ;  return this ; } }   private final IndentationPolicy  indentationPolicy ;   private final DatePolicy  datePolicy ;   public TomlWriter  ( )  {  this  ( 0 , 0 , 0 ,  TimeZone . getTimeZone  ( "UTC" ) , false ) ; }   private TomlWriter  (   int keyIndentation ,   int tableIndentation ,   int arrayDelimiterPadding ,  TimeZone timeZone ,  boolean showFractionalSeconds )  {    this . indentationPolicy =  new IndentationPolicy  ( keyIndentation , tableIndentation , arrayDelimiterPadding ) ;    this . datePolicy =  new DatePolicy  ( timeZone , showFractionalSeconds ) ; }   public String write  (  Object from )  {  try  {  StringWriter  output =  new StringWriter  ( ) ;   write  ( from , output ) ;  return  output . toString  ( ) ; }  catch (   IOException e )  {  throw  new RuntimeException  ( e ) ; } }   public void write  (  Object from ,  OutputStream target )  throws IOException  {  OutputStreamWriter  writer =  new OutputStreamWriter  ( target ) ;   write  ( from , writer ) ;   writer . flush  ( ) ; }   public void write  (  Object from ,  File target )  throws IOException  {  FileWriter  writer =  new FileWriter  ( target ) ;  try  {   write  ( from , writer ) ; }  finally  {   writer . close  ( ) ; } }   public void write  (  Object from ,  Writer target )  throws IOException  {  ValueWriter 
<<<<<<<
 valueWriter =  WRITERS . findWriterFor  ( from )
=======
 writer =  WRITERS . findWriterFor  ( from )
>>>>>>>
 ;  if  (   
<<<<<<<
valueWriter
=======
writer
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 MAP_VALUE_WRITER 
<<<<<<<
&&
=======
||
>>>>>>>
  
<<<<<<<
valueWriter
=======
writer
>>>>>>>
 
<<<<<<<
!=
=======
==
>>>>>>>
 OBJECT_VALUE_WRITER )  {    WRITERS . findWriterFor  ( from ) . write  ( from , context ) ; } else  {  throw 
<<<<<<<
 new IllegalArgumentException  (   "An object of type " +   from . getClass  ( ) . getSimpleName  ( ) + " cannot produce valid TOML." )
=======
 new IllegalStateException  ( "Top-level value must not be a primitive or array." )
>>>>>>>
 ; } 
<<<<<<<
  valueWriter . write  ( from , context ) ;
=======
>>>>>>>
 } }