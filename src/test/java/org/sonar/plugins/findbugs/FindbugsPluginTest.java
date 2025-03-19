  package    org . sonar . plugins . findbugs ;   import    org . sonar . api . Plugin ;  import    org . sonar . api . SonarRuntime ;  import static      org . junit . jupiter . api . Assertions . assertEquals ;  import static    org . mockito . Mockito . mock ;  import     org . junit . jupiter . api . Test ;  class FindbugsPluginTest  {    @ Test void testGetExtensions  ( )  {   Plugin . Context  ctx =  new  Plugin . Context  (  mock  (  SonarRuntime . class ) ) ;  FindbugsPlugin  plugin =  new FindbugsPlugin  ( ) ;   plugin . define  ( ctx ) ;   assertEquals  ( 
<<<<<<<
24
=======
22
>>>>>>>
 ,   ctx . getExtensions  ( ) . size  ( ) , "extension count" ) ; } }