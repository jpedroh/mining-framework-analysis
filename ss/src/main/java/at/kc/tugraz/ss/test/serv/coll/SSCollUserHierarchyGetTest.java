package at.kc.tugraz.ss.test.serv.coll;
import at.kc.tugraz.socialserver.utils.SSMethU;
import at.kc.tugraz.ss.datatypes.datatypes.SSLabelStr;
import at.kc.tugraz.ss.datatypes.datatypes.SSUri;
import at.kc.tugraz.ss.serv.coll.conf.SSCollConf;
import at.kc.tugraz.ss.serv.serv.caller.SSServCaller;
import at.kc.tugraz.ss.serv.test.api.SSServOpTestCaseA;
import at.kc.tugraz.ss.service.coll.datatypes.SSColl;
import java.util.List;

public class SSCollUserHierarchyGetTest extends SSServOpTestCaseA {
  public SSCollUserHierarchyGetTest(SSCollConf collConf) throws Exception {
    super(collConf, SSMethU.collUserHierarchyGet);
  }

  @Override protected void test() throws Exception {
    SSColl rootColl = SSServCaller.collUserRootGet(userUri);
    SSUri collFirstUri = SSServCaller.collUserEntryAdd(userUri, rootColl.uri, null, SSLabelStr.get("firstColl"), true, false, true);
    SSUri collSecondUri = SSServCaller.collUserEntryAdd(userUri, collFirstUri, null, SSLabelStr.get("secondColl"), true, false, true);
    List<SSColl> collHierarchy = SSServCaller.collUserHierarchyGet(userUri, collSecondUri);
    System.out.println(op + " Test end");
  }

  @Override protected void testFromClient() throws Exception {
  }

  @Override protected void setUp() throws Exception {
    userUri = SSServCaller.logUserIn(SSLabelStr.get("dt"), true);
  }
}