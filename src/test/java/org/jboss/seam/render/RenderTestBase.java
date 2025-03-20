  package    org . jboss . seam . render ;   import     org . jboss . arquillian . api . Deployment ;  import     org . jboss . arquillian . junit . Arquillian ;  import      org . jboss . seam . solder . el . Expressions ;  import     org . jboss . shrinkwrap . api . ArchivePaths ;  import     org . jboss . shrinkwrap . api . ShrinkWrap ;  import      org . jboss . shrinkwrap . api . asset . ByteArrayAsset ;  import      org . jboss . shrinkwrap . api . spec . JavaArchive ;  import    org . junit . runner . RunWith ;    @ RunWith  (  Arquillian . class ) public abstract class RenderTestBase  {    @ Deployment public static JavaArchive createTestArchive  ( )  {  JavaArchive  deployment =      ShrinkWrap . create  (  JavaArchive . class ) . addPackages  ( true ,   Root . class . getPackage  ( ) ) . addPackages  ( true ,   Expressions . class . getPackage  ( ) ) . addManifestResource  (  new ByteArrayAsset  (  "<beans/>" . getBytes  ( ) ) ,  ArchivePaths . create  ( "beans.xml" ) ) . addPackages  ( 
<<<<<<<
false
=======
"META-INF/services/org.jboss.seam.solder.beanManager.BeanManagerProvider"
>>>>>>>
 ,   Expressions . class . getPackage  ( ) ) ;  return deployment ; } }