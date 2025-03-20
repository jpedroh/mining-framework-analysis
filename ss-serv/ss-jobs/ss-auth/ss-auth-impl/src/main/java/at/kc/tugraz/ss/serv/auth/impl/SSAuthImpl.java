package at.kc.tugraz.ss.serv.auth.impl;
import at.kc.tugraz.ss.adapter.socket.datatypes.SSSocketCon;
import at.kc.tugraz.ss.serv.auth.api.SSAuthClientI;
import at.kc.tugraz.ss.serv.auth.api.SSAuthServerI;
import at.kc.tugraz.ss.serv.auth.conf.SSAuthConf;
import at.kc.tugraz.ss.serv.auth.impl.fct.csv.SSAuthFct;
import at.kc.tugraz.ss.serv.datatypes.SSServPar;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import at.kc.tugraz.ss.serv.serv.api.SSServImplMiscA;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.pars.SSAuthCheckCredPar;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.pars.SSAuthUsersFromCSVFileAddPar;
import at.kc.tugraz.ss.serv.ss.auth.datatypes.ret.SSAuthCheckCredRet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSAuthImpl extends SSServImplMiscA implements SSAuthClientI, SSAuthServerI {
  private static final List<String> keys = new ArrayList<String>();

  private static final Map<String, String> keyPerUser = new HashMap<String, String>();

  private static final Map<String, String> passwordPerUser = new HashMap<String, String>();

  public SSAuthImpl(final SSAuthConf conf) throws Exception {
    super(conf);
    switch (conf.authType) {
      case noAuth:
      if (keys.isEmpty()) {
        keys.add("FischersFritzFischtFrischeFische");
        keys.add("681V454J1P3H4W3B367BB79615U184N22356I3E");
        keys.add("d4ed2b76cfcf9bad374ef96c9c7ab3b");
      }
      break;
    }
  }

  @Override public void authCheckCred(SSSocketCon sSCon, SSServPar par) throws Exception {
    sSCon.writeRetFullToClient(SSAuthCheckCredRet.get(authCheckCred(par), par.op));
  }

  @Override public void authUsersFromCSVFileAdd(final SSServPar parA) throws Exception {
    final SSAuthUsersFromCSVFileAddPar par = new SSAuthUsersFromCSVFileAddPar(parA);
    String key;
    try {
      passwordPerUser.putAll(SSServCaller.dataImportSSSUsersFromCSVFile(((SSAuthConf) conf).fileName));
      for (Map.Entry<String, String> passwordForUser : passwordPerUser.entrySet()) {
        key = SSAuthFct.generateKey(passwordForUser.getKey() + passwordForUser.getValue());
        keyPerUser.put(passwordForUser.getKey(), key);
        keys.add(key);
      }
    } catch (Exception error) {
      SSServErrReg.regErrThrow(error);
    }
  }

  @Override public String authCheckCred(final SSServPar parA) throws Exception {
    final SSAuthCheckCredPar par = new SSAuthCheckCredPar(parA);
    switch (((SSAuthConf) conf).authType) {
      case noAuth:
      return keys.get(1);
      case csvFileAuth:
      return SSAuthFct.checkPasswordAndGetUserKey(passwordPerUser, keyPerUser, par.userLabel, par.pass);
      default:
      throw new UnsupportedOperationException();
    }
  }

  @Override public void authCheckKey(final SSServPar parA) throws Exception {
    SSAuthFct.checkKey(keys, parA.key);
  }
}