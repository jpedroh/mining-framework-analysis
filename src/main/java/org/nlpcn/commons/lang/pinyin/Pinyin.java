  package     org . nlpcn . commons . lang . pinyin ;   import   java . util . List ;   public class Pinyin  {   public static  List  < String > pinyin  (  String str )  {  return   PinyinUtil . INSTANCE . convert  ( str ,  PinyinFormat . TONELESS_PINYIN_FORMAT ) ; }   public static  List  < String > firstChar  (  String str )  {  return   PinyinUtil . INSTANCE . convert  ( str ,  PinyinFormat . ABBR_PINYIN_FORMAT ) ; }   public static  List  < String > unicodePinyin  (  String str )  {  return   PinyinUtil . INSTANCE . convert  ( str ,  PinyinFormat . UNICODE_PINYIN_FORMAT ) ; }   public static  List  < String > tonePinyin  (  String str )  {  return   PinyinUtil . INSTANCE . convert  ( str ,  PinyinFormat . DEFAULT_PINYIN_FORMAT ) ; }   public static String list2String  (   List  < String > list ,  String spearator )  {  StringBuilder  sb =  new StringBuilder  ( ) ;  boolean  flag = true ;  for ( String string : list )  {  if  (  string == null )  {   string = "NULL" ; }  if  ( 
<<<<<<<
  sb . length  ( ) > 0
=======
flag
>>>>>>>
 )  {   sb . append  ( string ) ;   flag = false ; } else  {   sb . append  ( spearator ) ;   sb . append  ( string ) ; }   sb . append  (  String . valueOf  ( string ) ) ; }  return  sb . toString  ( ) ; }   public static String list2String  (   List  < String > list )  {  return  list2String  ( list , " " ) ; }   public static void insertPinyin  (  String word ,   String  [ ] pinyins )  {    PinyinUtil . INSTANCE . insertPinyin  ( word , pinyins ) ; }   public static String list2StringSkipNull  (   List  < String > list )  {  return  list2StringSkipNull  ( list , " " ) ; }   public static String list2StringSkipNull  (   List  < String > list ,  String spearator )  {  StringBuilder  sb =  new StringBuilder  ( ) ;  boolean  flag = true ;  for ( String string : list )  {  if  (  string == null )  {  continue ; }  if  ( 
<<<<<<<
  sb . length  ( ) > 0
=======
flag
>>>>>>>
 )  {   sb . append  ( string ) ;   flag = false ; } else  {   sb . append  ( spearator ) ;   sb . append  ( string ) ; }  if  (  string == null )  {  continue ; }   sb . append  (  String . valueOf  ( string ) ) ; }  return  sb . toString  ( ) ; } }