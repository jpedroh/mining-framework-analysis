package at.kc.tugraz.ss.serv.datatypes;
import at.kc.tugraz.socialserver.utils.SSJSONU;
import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.socialserver.utils.SSVarU;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonParser;

public class SSServPar {
  public SSMethU op = null;

  public Map<String, Object> pars = null;

  public Map<String, String> clientPars = null;

  public SSUri user = null;

  public String key = null;

  public Boolean shouldCommit = true;

  public Boolean saveUE = true;

  public Boolean tryAgain = true;

  public SSServPar(final String jsonRequ) throws Exception {
    JsonParser jp;
    String jKey;
    String jValue;
    clientPars = new HashMap<String, String>();
    try {
      jp = SSJSONU.jsonParser(jsonRequ);
      jp.nextToken();
      while (jp.nextToken() != SSJSONU.jsonEnd) {
        jKey = jp.getCurrentName();
        jp.nextToken();
        jValue = jp.getText();
        if (SSStrU.equals(jKey, SSVarU.op)) {
          op = SSMethU.get(jValue);
          continue;
        }
        if (SSStrU.equals(jKey, SSVarU.user)) {
          user = SSUri.get(jValue);
          clientPars.put(jKey, jValue);
          continue;
        }
        if (SSStrU.equals(jKey, SSVarU.key)) {
          key = jValue;
          clientPars.put(jKey, jValue);
          continue;
        }
        if (SSStrU.isEmpty(jValue)) {
          clientPars.put(jKey, null);
        } else {
          clientPars.put(jKey, jValue);
        }
      }
      jp.close();
      if (this.op == null || this.user == null || SSStrU.isEmpty(this.key)) {
        SSServErrReg.regErr(new Exception("op, user or key is empty"));
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }

  public SSServPar(final SSMethU op, final Map<String, Object> pars) throws Exception {
    this.op = op;
    this.pars = pars;
    try {
      user = (SSUri) pars.get(SSVarU.user);
    } catch (Exception error1) {
    }
    try {
      key = (String) pars.get(SSVarU.key);
    } catch (Exception error2) {
    }
    try {
      shouldCommit = (Boolean) pars.get(SSVarU.shouldCommit);
    } catch (Exception error3) {
    }
    try {
      saveUE = (Boolean) pars.get(SSVarU.saveUE);
    } catch (Exception error4) {
    }
    if (this.op == null || this.pars == null) {
      SSServErrReg.regErrThrow(new Exception("op or pars is/are empty"));
    }
  }

  protected SSServPar(final SSServPar par) throws Exception {
    this.op = par.op;
    this.user = par.user;
    this.key = par.key;
    if (par.shouldCommit != null) {
      this.shouldCommit = par.shouldCommit;
    }
    if (par.tryAgain != null) {
      this.tryAgain = par.tryAgain;
    }
    if (par.saveUE != null) {
      this.saveUE = par.saveUE;
    }
    this.pars = par.pars;
    this.clientPars = par.clientPars;
  }
}