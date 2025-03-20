  package     com . mitchellbosecke . pebble . node . expression ;   import   java . util . List ;  import   java . util . Optional ;  import     com . mitchellbosecke . pebble . attributes . DefaultAttributeResolver ;  import     com . mitchellbosecke . pebble . attributes . ResolvedAttribute ;  import     com . mitchellbosecke . pebble . error . AttributeNotFoundException ;  import     com . mitchellbosecke . pebble . error . PebbleException ;  import     com . mitchellbosecke . pebble . error . RootAttributeNotFoundException ;  import     com . mitchellbosecke . pebble . extension . DynamicAttributeProvider ;  import     com . mitchellbosecke . pebble . extension . NodeVisitor ;  import     com . mitchellbosecke . pebble . node . ArgumentsNode ;  import     com . mitchellbosecke . pebble . node . PositionalArgumentNode ;  import     com . mitchellbosecke . pebble . template . EvaluationContextImpl ;  import     com . mitchellbosecke . pebble . template . PebbleTemplateImpl ;  import     com . mitchellbosecke . pebble . error . ClassAccessException ;   public class GetAttributeExpression  implements   Expression  < Object >  {   private final  Expression  <  ? >  node ;   private final  Expression  <  ? >  attributeNameExpression ;   private final ArgumentsNode  args ;   private final String  filename ;   private final  int  lineNumber ;   public GetAttributeExpression  (   Expression  <  ? > node ,   Expression  <  ? > attributeNameExpression ,  String filename ,   int lineNumber )  {  this  ( node , attributeNameExpression , null , filename , lineNumber ) ; }   public GetAttributeExpression  (   Expression  <  ? > node ,   Expression  <  ? > attributeNameExpression ,  ArgumentsNode args ,  String filename ,   int lineNumber )  {    this . node = node ;    this . attributeNameExpression = attributeNameExpression ;    this . args = args ;    this . filename = filename ;    this . lineNumber = lineNumber ; }    @ Override public Object evaluate  (  PebbleTemplateImpl self ,  EvaluationContextImpl context )  throws PebbleException  {   final Object  object =   this . node . evaluate  ( self , context ) ;   final Object  attributeNameValue =   this . attributeNameExpression . evaluate  ( self , context ) ;   final String  attributeName =  String . valueOf  ( attributeNameValue ) ;   final  Object  [ ]  argumentValues =  this . getArgumentValues  ( self , context ) ;  if  (   object == null &&  context . isStrictVariables  ( ) )  {  if  (  
<<<<<<<
 this . node
=======
object
>>>>>>>
 instanceof 
<<<<<<<
ContextVariableExpression
=======
DynamicAttributeProvider
>>>>>>>
 )  {  
<<<<<<<
 final
=======
DynamicAttributeProvider
>>>>>>>
 
<<<<<<<
String
=======
 dynamicAttributeProvider =  ( DynamicAttributeProvider ) object
>>>>>>>
  rootPropertyName =   (  ( ContextVariableExpression )  this . node ) . getName  ( ) ; 
<<<<<<<
 throw  new RootAttributeNotFoundException  ( null ,  String . format  ( "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true." , rootPropertyName ) , rootPropertyName ,  this . lineNumber ,  this . filename ) ;
=======
 if  (  dynamicAttributeProvider . canProvideDynamicAttribute  ( attributeName ) )  {  return  dynamicAttributeProvider . getDynamicAttribute  ( attributeNameValue , argumentValues ) ; }
>>>>>>>
 } else  {  throw  new RootAttributeNotFoundException  ( null , "Attempt to get attribute of null object and strict variables is set to true." , attributeName ,  this . lineNumber ,  this . filename ) ; } } 
<<<<<<<
  Optional  < ResolvedAttribute >  resolvedAttribute =  DefaultAttributeResolver . resolve  (   context . getExtensionRegistry  ( ) . getAttributeResolver  ( ) , object , attributeNameValue , argumentValues ,  context . isStrictVariables  ( ) , filename ,  this . lineNumber ) ;
=======
>>>>>>>
 
<<<<<<<
 if  (  resolvedAttribute . isPresent  ( ) )  {  return   resolvedAttribute . get  ( ) . evaluate  ( ) ; }
=======
>>>>>>>
  if  (  context . isStrictVariables  ( ) )  {  throw  new AttributeNotFoundException  ( null ,  String . format  ( "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true." , attributeName ,   object . getClass  ( ) . getName  ( ) ) , attributeName ,  this . lineNumber ,  this . filename ) ; } else  if  (  context . isStrictVariables  ( ) )  {  if  (  object == null )  {  if  (   this . node instanceof ContextVariableExpression )  {   final String  rootPropertyName =   (  ( ContextVariableExpression )  this . node ) . getName  ( ) ;  throw  new RootAttributeNotFoundException  ( null ,  String . format  ( "Root attribute [%s] does not exist or can not be accessed and strict variables is set to true." , rootPropertyName ) , rootPropertyName ,  this . lineNumber ,  this . filename ) ; } else  {  throw  new RootAttributeNotFoundException  ( null , "Attempt to get attribute of null object and strict variables is set to true." , attributeName ,  this . lineNumber ,  this . filename ) ; } } else  {  if  (   attributeName . equals  ( "class" ) ||  attributeName . equals  ( "getClass" ) )  {  throw  new ClassAccessException  (  this . lineNumber ,  this . filename ) ; } else  {  throw  new AttributeNotFoundException  ( null ,  String . format  ( "Attribute [%s] of [%s] does not exist or can not be accessed and strict variables is set to true." , attributeName ,   object . getClass  ( ) . getName  ( ) ) , attributeName ,  this . lineNumber ,  this . filename ) ; } } }  return null ; }   private  Object  [ ] getArgumentValues  (  PebbleTemplateImpl self ,  EvaluationContextImpl context )  throws PebbleException  {   Object  [ ]  argumentValues ;  if  (   this . args == null )  {   argumentValues = null ; } else  {   List  < PositionalArgumentNode >  args =   this . args . getPositionalArgs  ( ) ;   argumentValues =  new Object  [  args . size  ( ) ] ;   int  index = 0 ;  for ( PositionalArgumentNode arg : args )  {  Object  argumentValue =   arg . getValueExpression  ( ) . evaluate  ( self , context ) ;    argumentValues [ index ] = argumentValue ;   index ++ ; } }  return argumentValues ; }    @ Override public void accept  (  NodeVisitor visitor )  {   visitor . visit  ( this ) ; }   public  Expression  <  ? > getNode  ( )  {  return  this . node ; }   public  Expression  <  ? > getAttributeNameExpression  ( )  {  return  this . attributeNameExpression ; }   public ArgumentsNode getArgumentsNode  ( )  {  return  this . args ; }    @ Override public  int getLineNumber  ( )  {  return  this . lineNumber ; } 
<<<<<<<
=======
  private Object getObjectFromMap  (   Map  <  ? ,  ? > object ,  Object attributeNameValue )  throws PebbleException  {  if  (  object . isEmpty  ( ) )  {  return null ; }  if  (   attributeNameValue != null &&   Number . class . isAssignableFrom  (  attributeNameValue . getClass  ( ) ) )  {  Number  keyAsNumber =  ( Number ) attributeNameValue ;   Class  <  ? >  keyClass =     object . keySet  ( ) . iterator  ( ) . next  ( ) . getClass  ( ) ;  Object  key =  this . cast  ( keyAsNumber , keyClass ) ;  return  object . get  ( key ) ; }  return  object . get  ( attributeNameValue ) ; }
>>>>>>>
 
<<<<<<<
=======
  private Method findMethod  (   Class  <  ? > clazz ,  String name ,    Class  <  ? >  [ ] requiredTypes )  {  if  (  name . equals  ( "getClass" ) )  {  return null ; }  Method  result = null ;   Method  [ ]  candidates =  clazz . getMethods  ( ) ;  for ( Method candidate : candidates )  {  if  (  !   candidate . getName  ( ) . equalsIgnoreCase  ( name ) )  {  continue ; }    Class  <  ? >  [ ]  types =  candidate . getParameterTypes  ( ) ;  if  (   types . length !=  requiredTypes . length )  {  continue ; }  boolean  compatibleTypes = true ;  for (   int  i = 0 ;  i <  types . length ;  i ++ )  {  if  (    requiredTypes [ i ] != null &&  !   this . widen  (  types [ i ] ) . isAssignableFrom  (  requiredTypes [ i ] ) )  {   compatibleTypes = false ;  break ; } }  if  ( compatibleTypes )  {   result = candidate ;  break ; } }  return result ; }
>>>>>>>
 }