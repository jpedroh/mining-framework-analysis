  package    com . mitchellbosecke . pebble . template ;   import   java . util . Locale ; 
<<<<<<<
  public interface EvaluationContext  {  boolean isStrictVariables  ( ) ;  Locale getLocale  ( ) ;  Object getVariable  (  String key ) ; }
=======
  public class EvaluationContext  {   private final boolean  strictVariables ;   private final Hierarchy  hierarchy ;   private final ScopeChain  scopeChain ;   private final Locale  locale ;   private final ExtensionRegistry  extensionRegistry ;   private final  Cache  < CacheKey , Object >  tagCache ;   private final ExecutorService  executorService ;   private final  List  < PebbleTemplateImpl >  importedTemplates ;   private final boolean  allowGetClass ;   public EvaluationContext  (  PebbleTemplateImpl self ,  boolean strictVariables ,  Locale locale ,  ExtensionRegistry extensionRegistry ,   Cache  < CacheKey , Object > tagCache ,  ExecutorService executorService ,   List  < PebbleTemplateImpl > importedTemplates ,  ScopeChain scopeChain ,  Hierarchy hierarchy ,  boolean allowGetClass )  {  if  (  hierarchy == null )  {   hierarchy =  new Hierarchy  ( self ) ; }    this . strictVariables = strictVariables ;    this . locale = locale ;    this . extensionRegistry = extensionRegistry ;    this . tagCache = tagCache ;    this . executorService = executorService ;    this . importedTemplates = importedTemplates ;    this . scopeChain = scopeChain ;    this . hierarchy = hierarchy ;    this . allowGetClass = allowGetClass ; }   public EvaluationContext shallowCopyWithoutInheritanceChain  (  PebbleTemplateImpl self )  {  EvaluationContext  result =  new EvaluationContext  ( self , strictVariables , locale , extensionRegistry , tagCache , executorService , importedTemplates , scopeChain , null , allowGetClass ) ;  return result ; }   public EvaluationContext threadSafeCopy  (  PebbleTemplateImpl self )  {  EvaluationContext  result =  new EvaluationContext  ( self , strictVariables , locale , extensionRegistry , tagCache , executorService ,  new  ArrayList  < >  ( importedTemplates ) ,  scopeChain . deepCopy  ( ) , hierarchy , allowGetClass ) ;  return result ; }   public boolean isStrictVariables  ( )  {  return strictVariables ; }   public Locale getLocale  ( )  {  return locale ; }   public ExtensionRegistry getExtensionRegistry  ( )  {  return extensionRegistry ; }   public ExecutorService getExecutorService  ( )  {  return executorService ; }   public  List  < PebbleTemplateImpl > getImportedTemplates  ( )  {  return  this . importedTemplates ; }   public  Cache  < CacheKey , Object > getTagCache  ( )  {  return tagCache ; }   public ScopeChain getScopeChain  ( )  {  return scopeChain ; }   public Hierarchy getHierarchy  ( )  {  return hierarchy ; }   public boolean isAllowGetClass  ( )  {  return  this . allowGetClass ; } }
>>>>>>>
