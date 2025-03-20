package at.kc.tugraz.ss.serv.lomextractor.serv;
import at.kc.tugraz.ss.serv.lomextractor.api.SSLOMExtractorClientI;
import at.kc.tugraz.ss.serv.lomextractor.api.SSLOMExtractorServerI;
import at.kc.tugraz.ss.serv.lomextractor.conf.SSLOMExtractorConf;
import at.kc.tugraz.ss.serv.lomextractor.impl.SSLOMExtractorImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSLOMExtractorServ extends SSServA {
  public static final SSServA inst = new SSLOMExtractorServ(SSLOMExtractorClientI.class, SSLOMExtractorServerI.class);

  protected SSLOMExtractorServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSLOMExtractorImpl((SSLOMExtractorConf) servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}