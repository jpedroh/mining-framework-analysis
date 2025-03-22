  package     com . salesmanager . core . business . configuration ;   import     org . springframework . boot . autoconfigure . EnableAutoConfiguration ;  import     org . springframework . context . annotation . ComponentScan ;  import     org . springframework . context . annotation . Configuration ;  import     org . springframework . context . annotation . ImportResource ;    @ Configuration  @ EnableAutoConfiguration  @ ComponentScan  (  { "com.salesmanager.core.business" } )  @ ImportResource  ( "classpath:/spring/shopizer-core-context.xml" ) 
<<<<<<<
 @ Import  (  {  DroolsConfiguration . class ,  DataConfiguration . class } )
=======
>>>>>>>
 public class CoreApplicationConfiguration  { }