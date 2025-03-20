package at.kc.tugraz.ss.service.search.service;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;
import at.kc.tugraz.ss.service.search.api.SSSearchClientI;
import at.kc.tugraz.ss.service.search.api.SSSearchServerI;
import at.kc.tugraz.ss.service.search.impl.SSSearchImpl;

public class SSSearchServ extends SSServA {
  public static final SSServA inst = new SSSearchServ(SSSearchClientI.class, SSSearchServerI.class);

  protected SSSearchServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSSearchImpl(servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}