  package    io . tracee . contextlogger . integrationtest ;   import     io . tracee . contextlogger . api . CustomImplicitContextData ;  import     io . tracee . contextlogger . api . TraceeContextProvider ;  import     io . tracee . contextlogger . api . TraceeContextProviderMethod ;    @ TraceeContextProvider  (  displayName = "testBrokenImplicitContextData" ,  order = 200 ) public class TestBrokenImplicitContextDataProvider  implements  CustomImplicitContextData  {   public static final String  PROPERTY_NAME = "TestBrokenImplicitContextDataProvider.testOutputPropertyName" ;    @ SuppressWarnings  ( "unused" )  @ TraceeContextProviderMethod  (  displayName = "output" ,  propertyName =  TestImplicitContextDataProvider . PROPERTY_NAME ,  order = 10 ) public final String getOutput  ( )  {  throw  new NullPointerException  ( "Whoops!!!" ) ; } }