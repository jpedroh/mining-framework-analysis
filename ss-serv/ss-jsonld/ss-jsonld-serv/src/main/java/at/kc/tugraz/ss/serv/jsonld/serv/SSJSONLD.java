package at.kc.tugraz.ss.serv.jsonld.serv;
import at.kc.tugraz.ss.serv.jsonld.api.SSJSONLDClientI;
import at.kc.tugraz.ss.serv.jsonld.api.SSJSONLDServerI;
import at.kc.tugraz.ss.serv.jsonld.conf.SSJSONLDConf;
import at.kc.tugraz.ss.serv.jsonld.impl.SSJSONLDImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSJSONLD extends SSServA {
  public static final SSServA inst = new SSJSONLD(SSJSONLDClientI.class, SSJSONLDServerI.class);

  protected SSJSONLD(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSJSONLDImpl((SSJSONLDConf) servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}