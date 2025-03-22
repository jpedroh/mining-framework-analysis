  package   com . mpush . bootstrap ;   import    com . mpush . bootstrap . job .  * ;  import    com . mpush . core . server .  * ;  import     com . mpush . tools . config . CC ;  import static        com . mpush . tools . config . CC . mp . net . udpGateway ;  import static        com . mpush . tools . config . CC . mp . net . wsEnabled ;  import static     com . mpush . common . ServerNodes . CS ;  import static     com . mpush . common . ServerNodes . GS ;   public final class ServerLauncher  {   private final BootChain  chain =  BootChain . chain  ( ) ;   public ServerLauncher  ( )  {             chain . boot  ( ) . setNext  (  new ZKBoot  ( ) ) . setNext  ( 
<<<<<<<
 new RedisBoot  ( )
=======
 new ServiceRegistryBoot  ( )
>>>>>>>
 ) . setNext  (  new ServerBoot  (  ConnectionServer . I  ( ) , CS_NODE ) ) . setNext  ( 
<<<<<<<
  ( ) ->  new ServerBoot  (  WebSocketServer . I  ( ) , WS_NODE )
=======
 new ServerBoot  (  ConnectionServer . I  ( ) , CS )
>>>>>>>
 ,  wsEnabled  ( ) ) . setNext  (  new ServerBoot  (   udpGateway  ( ) ?  GatewayUDPConnector . I  ( ) :  GatewayServer . I  ( ) , GS ) ) . setNext  (  new ServerBoot  (  AdminServer . I  ( ) , null ) ) . setNext  (  new PushCenterBoot  ( ) ) . setNext  (  new HttpProxyBoot  ( ) ) . setNext  (  new MonitorBoot  ( ) ) . setNext  (  new LastBoot  ( ) ) ; }   public void start  ( )  {   chain . start  ( ) ; }   public void stop  ( )  {   chain . stop  ( ) ; } }