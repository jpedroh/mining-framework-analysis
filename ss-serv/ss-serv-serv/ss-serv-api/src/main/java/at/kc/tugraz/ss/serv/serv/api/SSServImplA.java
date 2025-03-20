package at.kc.tugraz.ss.serv.serv.api;
import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.ss.adapter.socket.datatypes.SSSocketCon;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class SSServImplA {
  protected SSServConfA conf = null;

  protected abstract void finalizeImpl() throws Exception;

  public void handleClientOp(final Class clientInterfaceClass, final SSSocketCon sSCon, final SSServPar par) throws Exception {
    if (clientInterfaceClass == null) {
      SSServErrReg.regErr(new Exception("service op shouldnt be instantiated this way"));
      return;
    }
    clientInterfaceClass.getMethod(SSMethU.toStr(par.op), SSSocketCon.class, SSServPar.class).invoke(this, sSCon, par);
  }

  protected SSServImplA(final SSServConfA conf) {
    this.conf = conf;
  }

  public Object handleServerOp(final Class serverInterfaceClass, final SSServPar par) throws Exception {
    if (serverInterfaceClass == null) {
      SSServErrReg.regErr(new Exception("service op shouldnt be instantiated this way"));
      return null;
    }
    return serverInterfaceClass.getMethod(SSMethU.toStr(par.op), SSServPar.class).invoke(this, par);
  }

  public List<SSMethU> publishClientOps(final Class clientInterfaceClass) throws Exception {
    final List<SSMethU> clientOps = new ArrayList<SSMethU>();
    if (clientInterfaceClass == null) {
      return clientOps;
    }
    final Method[] methods = clientInterfaceClass.getMethods();
    for (Method method : methods) {
      clientOps.add(SSMethU.get(method.getName()));
    }
    return clientOps;
  }

  public List<SSMethU> publishServerOps(final Class serverInterfaceClass) throws Exception {
    final List<SSMethU> serverOps = new ArrayList<SSMethU>();
    if (serverInterfaceClass == null) {
      return serverOps;
    }
    final Method[] methods = serverInterfaceClass.getMethods();
    for (Method method : methods) {
      serverOps.add(SSMethU.get(method.getName()));
    }
    return serverOps;
  }
}