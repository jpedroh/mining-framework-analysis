package at.kc.tugraz.ss.service.coll.datatypes.pars;
import at.kc.tugraz.socialserver.utils.SSVarU;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;

public class SSCollUserWithEntriesPar extends SSServPar {
  public SSUri coll = null;

  public SSCollUserWithEntriesPar(SSServPar par) throws Exception {
    super(par);
    try {
      if (pars != null) {
        coll = (SSUri) pars.get(SSVarU.coll);
      }
      if (clientPars != null) {
        coll = SSUri.get((String) clientPars.get(SSVarU.coll));
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }
}