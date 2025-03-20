package at.kc.tugraz.ss.serv.db.serv;
import at.kc.tugraz.ss.serv.db.api.SSDBGraphI;
import at.kc.tugraz.ss.serv.db.impl.SSDBGraphVirtuosoImpl;
import at.kc.tugraz.ss.serv.db.conf.SSDBGraphConf;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSDBGraph extends SSServA {
  public static final SSServA inst = new SSDBGraph(null, SSDBGraphI.class);

  protected SSDBGraph(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSDBGraphVirtuosoImpl((SSDBGraphConf) servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}