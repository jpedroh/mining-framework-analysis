package at.kc.tugraz.ss.test.serv.coll;
import at.kc.tugraz.socialserver.utils.SSLogU;
import at.kc.tugraz.ss.datatypes.datatypes.SSLabelStr;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.coll.conf.SSCollConf;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;
import at.kc.tugraz.ss.serv.test.api.SSServOverallTestCaseA;
import at.kc.tugraz.ss.service.coll.datatypes.SSColl;
import java.util.List;

public class SSCollOverallTest extends SSServOverallTestCaseA {
  public SSCollOverallTest(SSCollConf conf) throws Exception {
    super(conf);
  }

  @Override public void test() throws Exception {
    SSLogU.info("SSCollOverallTest start");
    SSColl rootColl = SSServCaller.collUserRootGet(userUri);
    SSUri collFirstUri = SSServCaller.collUserEntryAdd(userUri, rootColl.uri, null, SSLabelStr.get("firstColl"), true, false, true);
    SSUri collSecondUri = SSServCaller.collUserEntryAdd(userUri, collFirstUri, null, SSLabelStr.get("secondColl"), true, false, true);
    List<SSColl> collHierarchy = SSServCaller.collUserHierarchyGet(userUri, collSecondUri);
    SSLogU.info(SSCollOverallTest.class.getName() + " end");
  }
}