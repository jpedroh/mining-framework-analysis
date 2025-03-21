  package  ninja . bodyparser ;   import static    org . hamcrest . CoreMatchers . equalTo ;  import static    org . junit . Assert . assertThat ;  import   java . util . Collections ;  import   java . util . List ;  import  ninja . Router ;  import  ninja . RouterImpl ;  import   ninja . i18n . Lang ;  import   ninja . i18n . LangImpl ;  import   ninja . params . ParamParser ;  import   ninja . utils . LoggerProvider ;  import   ninja . utils . NinjaMode ;  import   ninja . utils . NinjaProperties ;  import   ninja . utils . NinjaPropertiesImpl ;  import   org . junit . Test ;  import   org . slf4j . Logger ;  import     com . google . common . collect . Lists ;  import    com . google . inject . AbstractModule ;  import    com . google . inject . Guice ;  import    com . google . inject . Injector ;  import     com . google . inject . multibindings . Multibinder ;   public class BodyParserEngineManagerImplTest  {    @ Test public void testContentTypes  ( )  {   List  < String >  types =  Lists . newArrayList  (   createBodyParserEngineManager  ( ) . getContentTypes  ( ) ) ;   Collections . sort  ( types ) ;   assertThat  (  types . toString  ( ) ,  equalTo  ( "[application/json, application/x-www-form-urlencoded, application/xml]" ) ) ; }   private BodyParserEngineManager createBodyParserEngineManager  (   final  Class  <  ? > ...  toBind )  {  return   createInjector  ( toBind ) . getInstance  (  BodyParserEngineManager . class ) ; }   private Injector createInjector  (   final  Class  <  ? > ...  toBind )  {  return  Guice . createInjector  (  new AbstractModule  ( )  {    @ Override protected void configure  ( )  {    bind  (  Logger . class ) . toProvider  (  LoggerProvider . class ) ;    bind  (  Lang . class ) . to  (  LangImpl . class ) ;    bind  (  Router . class ) . to  (  RouterImpl . class ) ;   bind  (  BodyParserEnginePost . class ) ;   bind  (  BodyParserEngineJson . class ) ;   
<<<<<<<
Multibinder
=======
bind
>>>>>>>
 . newSetBinder  (  binder  ( ) ,  
<<<<<<<
ParamParser
=======
BodyParserEngineXml
>>>>>>>
 . class ) ;    bind  (  NinjaProperties . class ) . toInstance  (  new NinjaPropertiesImpl  (  NinjaMode . test ) ) ;  for (  Class  <  ? > clazz : toBind )  {   bind  ( clazz ) ; } } } ) ; } }