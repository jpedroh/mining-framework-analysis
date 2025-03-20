  package    de . devland . esperandro . generation ;   import    com . squareup . javapoet . MethodSpec ;  import    com . squareup . javapoet . TypeSpec ;  import     javax . lang . model . element . Modifier ;  import     de . devland . esperandro . annotations . Cached ;  import      de . devland . esperandro . base . preferences . EsperandroType ;  import      de . devland . esperandro . base . preferences . MethodInformation ;  import      de . devland . esperandro . base . preferences . TypeInformation ;  import      de . devland . esperandro . base . processing . Environment ;  import   java . util . Collection ;  import   java . util . HashSet ;  import   java . util . List ;  import   java . util . Set ;  import    de . devland . esperandro . Constants ;  import    de . devland . esperandro . Utils ;  import      de . devland . esperandro . base . preferences . MethodOperation ;   public class CollectionActionGenerator  implements  MethodGenerator  {   private final String  action ;   public CollectionActionGenerator  (  String action )  {    this . action = action ; }    @ Override public void generateMethod  (   TypeSpec . Builder type ,  MethodInformation methodInformation ,  Cached cacheAnnotation )  {  String  prefName =  methodInformation . associatedPreference ;  String  setterName = null ;  String  getterName = null ;   List  < MethodInformation >  methods =   Environment . currentPreferenceInterface . getMethodsForPreference  ( prefName ) ;  for ( MethodInformation method : methods )  {  if  (   method . operation ==  MethodOperation . GET )  {   getterName =  method . methodName ; }  if  (   method . operation ==  MethodOperation . PUT )  {   setterName =  method . methodName ; } }  TypeInformation  preferenceType =   Environment . currentPreferenceInterface . getTypeOfPreference  ( prefName ) ;   MethodSpec . Builder 
<<<<<<<
 adder =       MethodSpec . methodBuilder  (  methodInformation . getMethodName  ( ) ) . addAnnotation  (  Override . class ) . addModifiers  (  Modifier . PUBLIC ) . returns  (  void . class ) . addParameter  (   methodInformation . parameterType . getType  ( ) , "value" ) . addStatement  ( "$T __pref = this.$L()" ,  preferenceType . getObjectType  ( ) , prefName )
=======
 action =       MethodSpec . methodBuilder  (  methodInformation . getMethodName  ( ) ) . addAnnotation  (  Override . class ) . addModifiers  (  Modifier . PUBLIC ) . returns  (   methodInformation . returnType . getType  ( ) ) . addParameter  (   methodInformation . parameterType . getType  ( ) , "value" ) . addStatement  ( "$T __pref = this.$L()" ,  preferenceType . getObjectType  ( ) , getterName )
>>>>>>>
 ;  if  (   preferenceType . getEsperandroType  ( ) ==  EsperandroType . STRINGSET )  {   
<<<<<<<
adder
=======
action
>>>>>>>
 . addStatement  ( "__pref = new java.util.HashSet<String>(__pref)" ) ; }    
<<<<<<<
adder
=======
action
>>>>>>>
 . addStatement  ( 
<<<<<<<
"__pref.$L(value)"
=======
"boolean result = __pref.$L(value)"
>>>>>>>
 , 
<<<<<<<
action
=======
 this . action
>>>>>>>
 ) . addStatement  ( "this.$L(__pref)" , 
<<<<<<<
prefName
=======
setterName
>>>>>>>
 ) ;  if  (    methodInformation . returnType . getEsperandroType  ( ) ==  EsperandroType . BOOLEAN )  {   action . addStatement  ( "return result" ) ; }   type . addMethod  (  action . build  ( ) ) ; } }