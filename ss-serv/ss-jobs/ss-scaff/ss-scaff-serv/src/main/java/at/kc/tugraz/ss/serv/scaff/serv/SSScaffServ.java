package at.kc.tugraz.ss.serv.scaff.serv;
import at.kc.tugraz.ss.serv.scaff.api.SSScaffClientI;
import at.kc.tugraz.ss.serv.scaff.api.SSScaffServerI;
import at.kc.tugraz.ss.serv.scaff.impl.SSScaffImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSScaffServ extends SSServA {
  public static final SSServA inst = new SSScaffServ(SSScaffClientI.class, SSScaffServerI.class);

  protected SSScaffServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSScaffImpl(servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}