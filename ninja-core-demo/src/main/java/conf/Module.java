  package conf ;   import  etc . GreetingService ;  import  etc . GreetingServiceImpl ;  import    com . google . inject . AbstractModule ;   public class Module  extends AbstractModule  { 
<<<<<<<
   @ Override protected ServletModule setupServlets  ( )  {    bind  (  NinjaServletDispatcher . class ) . asEagerSingleton  ( ) ;    bind  (  DemoServletFilter . class ) . asEagerSingleton  ( ) ;  return  new ServletModule  ( )  {    @ Override protected void configureServlets  ( )  {    filter  ( "/*" ) . through  (  DemoServletFilter . class ) ;    serve  ( "/*" ) . with  (  NinjaServletDispatcher . class ) ; } } ; }
=======
>>>>>>>
   public Module  ( )  {  super  ( ) ; }    @ Override protected void configure  ( )  {    bind  (  GreetingService . class ) . to  (  GreetingServiceImpl . class ) ; } }