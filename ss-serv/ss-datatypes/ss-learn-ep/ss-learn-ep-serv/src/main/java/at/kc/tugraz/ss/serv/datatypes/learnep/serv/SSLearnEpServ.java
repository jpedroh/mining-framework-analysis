package at.kc.tugraz.ss.serv.datatypes.learnep.serv;
import at.kc.tugraz.ss.serv.db.api.SSDBGraphI;
import at.kc.tugraz.ss.serv.db.api.SSDBSQLI;
import at.kc.tugraz.ss.serv.db.serv.SSDBGraph;
import at.kc.tugraz.ss.serv.db.serv.SSDBSQL;
import at.kc.tugraz.ss.datatypes.datatypes.SSEntityEnum;
import at.kc.tugraz.ss.serv.datatypes.learnep.api.SSLearnEpClientI;
import at.kc.tugraz.ss.serv.datatypes.learnep.api.SSLearnEpServerI;
import at.kc.tugraz.ss.serv.datatypes.learnep.impl.SSLearnEpImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSLearnEpServ extends SSServA {
  public static final SSServA inst = new SSLearnEpServ(SSLearnEpClientI.class, SSLearnEpServerI.class);

  protected SSLearnEpServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSLearnEpImpl(servConf, (SSDBGraphI) SSDBGraph.inst.serv(), (SSDBSQLI) SSDBSQL.inst.serv());
  }

  @Override protected void initServSpecificStuff() throws Exception {
    regServForManagingEntities(SSEntityEnum.learnEp);
    regServForManagingEntities(SSEntityEnum.learnEpTimelineState);
    regServForManagingEntities(SSEntityEnum.learnEpVersion);
    regServForManagingEntities(SSEntityEnum.learnEpCircle);
    regServForManagingEntities(SSEntityEnum.learnEpEntity);
  }
}