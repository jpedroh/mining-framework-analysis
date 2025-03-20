package at.kc.tugraz.ss.serv.job.dataexport.serv;
import at.kc.tugraz.ss.serv.job.dataexport.api.SSDataExportClientI;
import at.kc.tugraz.ss.serv.job.dataexport.api.SSDataExportServerI;
import at.kc.tugraz.ss.serv.job.dataexport.impl.SSDataExportImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSDataExportServ extends SSServA {
  public static final SSServA inst = new SSDataExportServ(SSDataExportClientI.class, SSDataExportServerI.class);

  protected SSDataExportServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSDataExportImpl(servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}