package at.kc.tugraz.ss.serv.jobs.evernote.serv;
import at.kc.tugraz.ss.datatypes.datatypes.SSEntityEnum;
import at.kc.tugraz.ss.serv.jobs.evernote.api.SSEvernoteClientI;
import at.kc.tugraz.ss.serv.jobs.evernote.api.SSEvernoteServerI;
import at.kc.tugraz.ss.serv.jobs.evernote.impl.SSEvernoteImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSEvernoteServ extends SSServA {
  public static final SSServA inst = new SSEvernoteServ(SSEvernoteClientI.class, SSEvernoteServerI.class);

  protected SSEvernoteServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSEvernoteImpl(servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
    regServForManagingEntities(SSEntityEnum.evernoteNotebook);
    regServForManagingEntities(SSEntityEnum.evernoteNote);
    regServForManagingEntities(SSEntityEnum.evernoteResource);
  }
}