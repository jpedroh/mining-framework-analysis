package at.kc.tugraz.ss.serv.serv.api;
import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.ss.adapter.socket.datatypes.SSSocketCon;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import java.util.ArrayList;
import java.util.List;

public abstract class SSServImplDBA extends SSServImplA {
  public SSServImplDBA(final SSServConfA conf) throws Exception {
    super(conf);
  }

  @Override public List<SSMethU> publishClientOps(final Class servImplClientInteraceClass) throws Exception {
    return new ArrayList<SSMethU>();
  }

  @Override public List<SSMethU> publishServerOps(final Class servImplServerInteraceClass) throws Exception {
    return new ArrayList<SSMethU>();
  }

  @Override public void handleClientOp(final Class servImplClientInteraceClass, final SSSocketCon sSCon, final SSServPar par) throws Exception {
    throw new UnsupportedOperationException(SSStrU.empty);
  }

  @Override public Object handleServerOp(final Class servImplServerInteraceClass, final SSServPar par) throws Exception {
    throw new UnsupportedOperationException(SSStrU.empty);
  }
}