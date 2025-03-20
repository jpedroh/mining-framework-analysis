package at.kc.tugraz.ss.test.load;
import at.kc.tugraz.socialserver.utils.SSLogU;
import at.kc.tugraz.socialserver.utils.SSStrU;
import at.kc.tugraz.ss.serv.serv.api.SSServConfA;
import at.kc.tugraz.ss.datatypes.datatypes.SSUEEnum;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.err.reg.SSServErrReg;
import at.kc.tugraz.ss.serv.serv.api.SSServImplStartA;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;

public class SSLoadTestUEAdder extends SSServImplStartA {
  private final SSUri userUri;

  public SSLoadTestUEAdder(SSServConfA conf, SSUri userUri) throws Exception {
    super(conf);
    this.userUri = userUri;
  }

  @Override public void run() {
    try {
      SSServCaller.ueAdd(userUri, SSLoadTest.collUri, SSUEEnum.createPrivateCollection, SSStrU.empty, true);
    } catch (Exception error1) {
      SSServErrReg.regErr(error1);
    } finally {
      try {
        finalizeImpl();
      } catch (Exception error2) {
        SSLogU.err(error2);
      }
    }
  }

  @Override protected void finalizeImpl() throws Exception {
    finalizeThread();
  }
}