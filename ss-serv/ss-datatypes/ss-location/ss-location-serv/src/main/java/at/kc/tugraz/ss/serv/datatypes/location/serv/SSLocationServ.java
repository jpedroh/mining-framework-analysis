package at.kc.tugraz.ss.serv.datatypes.location.serv;
import at.kc.tugraz.ss.datatypes.datatypes.SSEntityEnum;
import at.kc.tugraz.ss.serv.datatypes.location.api.SSLocationClientI;
import at.kc.tugraz.ss.serv.datatypes.location.api.SSLocationServerI;
import at.kc.tugraz.ss.serv.db.api.SSDBGraphI;
import at.kc.tugraz.ss.serv.db.api.SSDBSQLI;
import at.kc.tugraz.ss.serv.db.serv.SSDBGraph;
import at.kc.tugraz.ss.serv.db.serv.SSDBSQL;
import at.kc.tugraz.ss.serv.datatypes.location.impl.SSLocationImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSLocationServ extends SSServA {
  public static final SSServA inst = new SSLocationServ(SSLocationClientI.class, SSLocationServerI.class);

  protected SSLocationServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSLocationImpl(servConf, (SSDBGraphI) SSDBGraph.inst.serv(), (SSDBSQLI) SSDBSQL.inst.serv());
  }

  @Override protected void initServSpecificStuff() throws Exception {
    regServForManagingEntities(SSEntityEnum.location);
  }
}