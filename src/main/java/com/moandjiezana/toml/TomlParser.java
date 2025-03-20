  package   com . moandjiezana . toml ;   import static     com . moandjiezana . toml . ValueConverterUtils . INVALID ;  import static     com . moandjiezana . toml . IdentifierConverter . IDENTIFIER_CONVERTER ;  import     java . util . concurrent . atomic . AtomicInteger ;  import     com . moandjiezana . toml . ValueConverterUtils . Unterminated ;  class TomlParser  {  Results run  (  String tomlString )  {   final Results  results =  new Results  ( ) ;  if  (  tomlString . isEmpty  ( ) )  {  return results ; }    char  [ ]  chars =  tomlString . toCharArray  ( ) ;  
<<<<<<<
 int
=======
AtomicInteger
>>>>>>>
 
<<<<<<<
 lastKeyLine = 1
=======
 index =  new AtomicInteger  ( )
>>>>>>>
 ;  boolean  inComment = false ;  AtomicInteger  line =  new AtomicInteger  ( 1 ) ;  Identifier  identifier = null ;  Object  value = null ;  for (   int  i =  index . get  ( ) ;  i <  chars . length ;  i =  index . incrementAndGet  ( ) )  { 
<<<<<<<
 if  (  isTableArray  ( line ) )  {  String  tableName =  Keys . getTableArrayName  ( line ) ;  if  (  tableName != null )  {   results . startTableArray  ( tableName ) ; } else  {    results . errors . invalidTableArray  ( line ,  i + 1 ) ; }  continue ; }
=======
>>>>>>>
 
<<<<<<<
 if  (   multiline . isNotMultiline  ( ) &&  isTable  ( line ) )  {  String  tableName =  Keys . getTableName  ( line ) ;  if  (  tableName != null )  {   results . startTables  ( tableName ) ; } else  {    results . errors . invalidTable  (  line . trim  ( ) ,  i + 1 ) ; }  continue ; }
=======
>>>>>>>
 
<<<<<<<
 if  (   multiline . isNotMultiline  ( ) &&  !  line . contains  ( "=" ) )  {    results . errors . invalidKey  ( line ,  i + 1 ) ;  continue ; }
=======
>>>>>>>
   char  c =  chars [ i ] ;  if  (   c == '#' &&  ! inComment )  {   inComment = true ; } else  if  (    !  Character . isWhitespace  ( c ) &&  ! inComment &&  identifier == null )  {  Identifier  id =  IDENTIFIER_CONVERTER . convert  ( chars , index ) ;  if  (  id . isValid  ( ) )  {   char  next =  chars [  index . get  ( ) ] ;  if  (    index . get  ( ) <   chars . length - 1 &&  !  id . acceptsNext  ( next ) )  {    results . errors . invalidTextAfterIdentifier  ( id , next ,  line . get  ( ) ) ; } else  if  (  id . isKey  ( ) )  {   identifier = id ; } else  if  (  id . isTable  ( ) )  {   results . startTables  (  Keys . getTableName  (  id . getName  ( ) ) ) ; } else  if  (  id . isTableArray  ( ) )  {   results . startTableArray  (  Keys . getTableArrayName  (  id . getName  ( ) ) ) ; }   inComment =  next == '#' ; } else  {    results . errors . invalidIdentifier  ( id ,  line . get  ( ) ) ; } } else  if  (  c == '\n' )  {   inComment = false ; 
<<<<<<<
 if  (  key == null )  {    results . errors . invalidKey  (  pair [ 0 ] ,  i + 1 ) ;  continue ; }
=======
  identifier = null ;
>>>>>>>
   value = null ;   line . incrementAndGet  ( ) ; } else  if  (      ! inComment &&  identifier != null &&  identifier . isKey  ( ) &&  value == null &&  !  Character . isWhitespace  ( c ) )  {   int  startIndex =  index . get  ( ) ;  Object  converted =   ValueConverters . CONVERTERS . convert  ( tomlString , index ) ;   value = converted ;  if  (  converted == INVALID )  {    results . errors . invalidValue  (  identifier . getName  ( ) ,  tomlString . substring  ( startIndex ,  Math . min  (  index . get  ( ) ,   tomlString . length  ( ) - 1 ) ) ,  line . get  ( ) ) ; } else  if  (  converted instanceof Unterminated )  {    results . errors . unterminated  (  identifier . getName  ( ) ,   (  ( Unterminated ) converted ) . payload ,  line . get  ( ) ) ; } else  {   results . addValue  (  identifier . getName  ( ) , converted ) ; } } else  if  (    value != null &&  ! inComment &&  !  Character . isWhitespace  ( c ) )  {    results . errors . invalidTextAfterIdentifier  ( identifier , c ,  line . get  ( ) ) ; }   lastKeyLine =  i + 1 ; 
<<<<<<<
 if  (  convertedValue != INVALID )  {   results . addValue  ( key , convertedValue ) ; } else  {    results . errors . invalidValue  ( key , value ,  i + 1 ) ; }
=======
>>>>>>>
 } 
<<<<<<<
 if  (  multiline !=  Multiline . NONE )  {    results . errors . unterminated  ( key ,   multilineBuilder . toString  ( ) . trim  ( ) , lastKeyLine ) ; }
=======
>>>>>>>
  return results ; } }