  package   net . tridentsdk . config ;   import   net . tridentsdk . Impl ;  import    net . tridentsdk . util . Misc ;  import   org . junit . Test ;  import   org . mockito . Mockito ;  import   java . io . File ;  import    java . nio . file . Path ;  import    java . nio . file . Paths ;  import static    org . junit . Assert . assertNotNull ;   public class ConfigTest  {   private static final String  TEST_PATH =   Misc . HOME + "/kek/cfg.json" ;  static  {   Impl . setImpl  (  Mockito . mock  (   Impl . ImplementationProvider . class ) ) ;    Mockito . when  (   Impl . get  ( ) . newCfg  (  Paths . get  ( TEST_PATH ) ) ) . thenReturn  (  Mockito . mock  (  Config . class ) ) ; }    @ Test public void testPathString  ( )  {  Config  cfg =  Config . load  ( TEST_PATH ) ;   assertNotNull  ( cfg 
<<<<<<<
=======
  cfg . getPath  ( ) . toString  ( )
>>>>>>>
 ) ; }    @ Test public void testPath  ( )  {  Path  path =  Paths . get  ( TEST_PATH ) ;  Config  cfg =  Config . load  ( path ) ;   assertNotNull  ( cfg 
<<<<<<<
=======
 cfg . getPath  ( )
>>>>>>>
 ) ; }    @ Test public void testFile  ( )  {  File  file =  new File  ( TEST_PATH ) ;  Config  cfg =  Config . load  ( file ) ;   assertNotNull  ( cfg 
<<<<<<<
=======
 cfg . getFile  ( )
>>>>>>>
 ) ; } }