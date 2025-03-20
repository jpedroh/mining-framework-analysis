package at.kc.tugraz.ss.service.broadcast.service;
import at.kc.tugraz.socialserver.service.broadcast.api.SSBroadcasterClientI;
import at.kc.tugraz.socialserver.service.broadcast.api.SSBroadcasterServerI;
import at.kc.tugraz.ss.serv.broadcast.impl.SSBroadcasterImpl;
import at.kc.tugraz.socialserver.service.broadcast.conf.SSBroadcasterConf;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSBroadcasterServ extends SSServA {
  public static final SSServA inst = new SSBroadcasterServ(SSBroadcasterClientI.class, SSBroadcasterServerI.class);

  protected SSBroadcasterServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSBroadcasterImpl((SSBroadcasterConf) servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}