package at.kc.tugraz.ss.service.coll.datatypes.pars;
import at.kc.tugraz.socialserver.utils.SSVarU;
import at.kc.tugraz.ss.datatypes.datatypes.SSLabelStr;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;

public class SSCollUserEntryAddPar extends SSServPar {
  public SSUri coll = null;

  public SSUri collEntry = null;

  public SSLabelStr collEntryLabel = null;

  public SSUri circleUri = null;

  public Boolean addNewColl = null;

  public SSCollUserEntryAddPar(final SSServPar par) throws Exception {
    super(par);
    try {
      if (pars != null) {
        coll = (SSUri) pars.get(SSVarU.coll);
        collEntry = (SSUri) pars.get(SSVarU.collEntry);
        collEntryLabel = (SSLabelStr) pars.get(SSVarU.collEntryLabel);
        addNewColl = (Boolean) pars.get(SSVarU.addNewColl);
        circleUri = (SSUri) pars.get(SSVarU.circleUri);
      }
      if (clientPars != null) {
        coll = SSUri.get(clientPars.get(SSVarU.coll));
        collEntryLabel = SSLabelStr.get(clientPars.get(SSVarU.collEntryLabel));
        try {
          addNewColl = Boolean.valueOf(clientPars.get(SSVarU.addNewColl));
        } catch (Exception error) {
        }
        try {
          collEntry = SSUri.get(clientPars.get(SSVarU.collEntry));
        } catch (Exception error) {
        }
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }
}