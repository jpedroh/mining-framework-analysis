  package    net . md_5 . bungee . connection ;   import     com . google . common . base . Preconditions ;  import   java . util . ArrayList ;  import   java . util . List ;  import    net . md_5 . bungee . BungeeCord ;  import    net . md_5 . bungee . UserConnection ;  import    net . md_5 . bungee . Util ;  import     net . md_5 . bungee . api . ProxyServer ;  import      net . md_5 . bungee . api . connection . ProxiedPlayer ;  import      net . md_5 . bungee . api . event . ChatEvent ;  import      net . md_5 . bungee . api . event . PlayerDisconnectEvent ;  import      net . md_5 . bungee . api . event . PluginMessageEvent ;  import      net . md_5 . bungee . api . event . TabCompleteEvent ;  import     net . md_5 . bungee . forge . ForgeConstants ;  import     net . md_5 . bungee . netty . ChannelWrapper ;  import     net . md_5 . bungee . netty . PacketHandler ;  import     net . md_5 . bungee . protocol . PacketWrapper ;  import     net . md_5 . bungee . protocol . ProtocolConstants ;  import      net . md_5 . bungee . protocol . packet . Chat ;  import      net . md_5 . bungee . protocol . packet . ClientSettings ;  import      net . md_5 . bungee . protocol . packet . KeepAlive ;  import      net . md_5 . bungee . protocol . packet . PlayerListItem ;  import      net . md_5 . bungee . protocol . packet . PluginMessage ;  import      net . md_5 . bungee . protocol . packet . TabCompleteRequest ;  import      net . md_5 . bungee . protocol . packet . TabCompleteResponse ;   public class UpstreamBridge  extends PacketHandler  {   private final ProxyServer  bungee ;   private final UserConnection  con ;   public UpstreamBridge  (  ProxyServer bungee ,  UserConnection con )  {    this . bungee = bungee ;    this . con = con ;    BungeeCord . getInstance  ( ) . addConnection  ( con ) ;    con . getTabListHandler  ( ) . onConnect  ( ) ;    con . unsafe  ( ) . sendPacket  (   BungeeCord . getInstance  ( ) . registerChannels  ( ) ) ; }    @ Override public void exception  (  Throwable t )  throws Exception  {   con . disconnect  (  Util . exception  ( t ) ) ; }    @ Override public void disconnected  (  ChannelWrapper channel )  throws Exception  {  PlayerDisconnectEvent  event =  new PlayerDisconnectEvent  ( con ) ;    bungee . getPluginManager  ( ) . callEvent  ( event ) ;    con . getTabListHandler  ( ) . onDisconnect  ( ) ;    BungeeCord . getInstance  ( ) . removeConnection  ( con ) ;  if  (   con . getServer  ( ) != null )  {  PlayerListItem  packet =  new PlayerListItem  ( ) ;   packet . setAction  (   PlayerListItem . Action . REMOVE_PLAYER ) ;   PlayerListItem . Item  item =  new  PlayerListItem . Item  ( ) ;   item . setUuid  (  con . getUniqueId  ( ) ) ;   packet . setItems  (  new  PlayerListItem . Item  [ ]  { item } ) ;  for ( ProxiedPlayer player :    con . getServer  ( ) . getInfo  ( ) . getPlayers  ( ) )  {    player . unsafe  ( ) . sendPacket  ( packet ) ; }    con . getServer  ( ) . disconnect  ( "Quitting" ) ; } }    @ Override public void writabilityChanged  (  ChannelWrapper channel )  throws Exception  {  if  (   con . getServer  ( ) != null )  {       con . getServer  ( ) . getCh  ( ) . getHandle  ( ) . config  ( ) . setAutoRead  (   channel . getHandle  ( ) . isWritable  ( ) ) ; } }    @ Override public boolean shouldHandle  (  PacketWrapper packet )  throws Exception  {  return    con . getServer  ( ) != null ||   packet . packet instanceof PluginMessage ; }    @ Override public void handle  (  PacketWrapper packet )  throws Exception  {  if  (   con . getServer  ( ) != null )  {     con . getServer  ( ) . getCh  ( ) . write  ( packet ) ; } }    @ Override public void handle  (  KeepAlive alive )  throws Exception  {  if  (   alive . getRandomId  ( ) ==   con . getServer  ( ) . getSentPingId  ( ) )  {   int  newPing =  (  int )  (   System . currentTimeMillis  ( ) -  con . getSentPingTime  ( ) ) ;    con . getTabListHandler  ( ) . onPingChange  ( newPing ) ;   con . setPing  ( newPing ) ; } else  {  throw  CancelSendSignal . INSTANCE ; } }    @ Override public void handle  (  Chat chat )  throws Exception  {   int  maxLength =   (    con . getPendingConnection  ( ) . getVersion  ( ) >=  ProtocolConstants . MINECRAFT_1_11 ) ? 256 : 100 ;   Preconditions . checkArgument  (    chat . getMessage  ( ) . length  ( ) <= maxLength , "Chat message too long" ) ;  ChatEvent  chatEvent =  new ChatEvent  ( con ,  con . getServer  ( ) ,  chat . getMessage  ( ) ) ;  if  (  !    bungee . getPluginManager  ( ) . callEvent  ( chatEvent ) . isCancelled  ( ) )  {   chat . setMessage  (  chatEvent . getMessage  ( ) ) ;  if  (   !  chatEvent . isCommand  ( ) ||  !   bungee . getPluginManager  ( ) . dispatchCommand  ( con ,   chat . getMessage  ( ) . substring  ( 1 ) ) )  {     con . getServer  ( ) . unsafe  ( ) . sendPacket  ( chat ) ; } }  throw  CancelSendSignal . INSTANCE ; }    @ Override public void handle  (  TabCompleteRequest tabComplete )  throws Exception  {   List  < String >  suggestions =  new  ArrayList  < >  ( ) ;  if  (   tabComplete . getCursor  ( ) . startsWith  ( "/" ) )  {    bungee . getPluginManager  ( ) . dispatchCommand  ( con ,   tabComplete . getCursor  ( ) . substring  ( 1 ) , suggestions ) ; }  TabCompleteEvent  tabCompleteEvent =  new TabCompleteEvent  ( con ,  con . getServer  ( ) ,  tabComplete . getCursor  ( ) , suggestions ) ;    bungee . getPluginManager  ( ) . callEvent  ( tabCompleteEvent ) ;  if  (  tabCompleteEvent . isCancelled  ( ) )  {  throw  CancelSendSignal . INSTANCE ; }   List  < String >  results =  tabCompleteEvent . getSuggestions  ( ) ;  if  (  !  results . isEmpty  ( ) )  {    con . unsafe  ( ) . sendPacket  (  new TabCompleteResponse  ( results ) ) ;  throw  CancelSendSignal . INSTANCE ; } }    @ Override public void handle  (  ClientSettings settings )  throws Exception  {   con . setSettings  ( settings ) ; }    @ Override public void handle  (  PluginMessage pluginMessage )  throws Exception  {  if  (   pluginMessage . getTag  ( ) . equals  ( "BungeeCord" ) )  {  throw  CancelSendSignal . INSTANCE ; }  if  (    pluginMessage . getTag  ( ) . equals  ( "FML" ) &&    pluginMessage . getStream  ( ) . readUnsignedByte  ( ) == 1 )  {  throw  CancelSendSignal . INSTANCE ; }  if  (   pluginMessage . getTag  ( ) . equals  (  ForgeConstants . FML_HANDSHAKE_TAG ) )  {    con . getForgeClientHandler  ( ) . handle  ( pluginMessage ) ;  throw  CancelSendSignal . INSTANCE ; }  if  (     con . getServer  ( ) != null &&  !   con . getServer  ( ) . isForgeServer  ( ) &&    pluginMessage . getData  ( ) . length >  Short . MAX_VALUE )  {  throw  CancelSendSignal . INSTANCE ; }  PluginMessageEvent  event =  new PluginMessageEvent  ( con ,  con . getServer  ( ) ,  pluginMessage . getTag  ( ) ,   pluginMessage . getData  ( ) . clone  ( ) ) ;  if  (    bungee . getPluginManager  ( ) . callEvent  ( event ) . isCancelled  ( ) )  {  throw  CancelSendSignal . INSTANCE ; }  if  (   PluginMessage . SHOULD_RELAY . apply  ( pluginMessage ) )  {     con . getPendingConnection  ( ) . getRelayMessages  ( ) . add  ( pluginMessage ) ; } }    @ Override public String toString  ( )  {  return   "[" +  con . getName  ( ) + "] -> UpstreamBridge" ; } }