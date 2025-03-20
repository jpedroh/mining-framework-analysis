package at.kc.tugraz.ss.serv.localwork.serv;
import at.kc.tugraz.ss.serv.localwork.impl.SSLocalWorkImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSLocalWorkServ extends SSServA {
  public static final SSLocalWorkServ inst = new SSLocalWorkServ(null, null);

  protected SSLocalWorkServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSLocalWorkImpl(servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}