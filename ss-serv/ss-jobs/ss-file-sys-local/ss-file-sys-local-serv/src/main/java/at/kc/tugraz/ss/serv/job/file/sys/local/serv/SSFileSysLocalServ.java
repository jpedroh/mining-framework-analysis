package at.kc.tugraz.ss.serv.job.file.sys.local.serv;
import at.kc.tugraz.ss.serv.job.file.sys.local.api.SSFileSysLocalClientI;
import at.kc.tugraz.ss.serv.job.file.sys.local.api.SSFileSysLocalServerI;
import at.kc.tugraz.ss.serv.job.file.sys.local.impl.SSFileSysLocalImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSFileSysLocalServ extends SSServA {
  public static final SSServA inst = new SSFileSysLocalServ(SSFileSysLocalClientI.class, SSFileSysLocalServerI.class);

  protected SSFileSysLocalServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSFileSysLocalImpl(servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}