package at.kc.tugraz.ss.service.solr.service;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;
import at.kc.tugraz.ss.service.filerepo.conf.SSFileRepoConf;
import at.kc.tugraz.ss.service.solr.api.SSSolrClientI;
import at.kc.tugraz.ss.service.solr.api.SSSolrServerI;
import at.kc.tugraz.ss.service.solr.impl.*;

public class SSSolrServ extends SSServA {
  public static final SSServA inst = new SSSolrServ(SSSolrClientI.class, SSSolrServerI.class);

  protected SSSolrServ(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSSolrImpl((SSFileRepoConf) servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}