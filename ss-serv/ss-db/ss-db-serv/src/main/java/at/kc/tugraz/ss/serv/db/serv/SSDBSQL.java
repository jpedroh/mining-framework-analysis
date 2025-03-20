package at.kc.tugraz.ss.serv.db.serv;
import at.kc.tugraz.ss.serv.db.api.SSDBSQLI;
import at.kc.tugraz.ss.serv.db.conf.SSDBSQLConf;
import at.kc.tugraz.ss.serv.db.impl.SSDBSQLMySQLImpl;
import at.kc.tugraz.ss.serv.serv.api.SSServA;
import at.kc.tugraz.ss.serv.serv.api.SSServImplA;

public class SSDBSQL extends SSServA {
  public static final SSServA inst = new SSDBSQL(null, SSDBSQLI.class);

  protected SSDBSQL(final Class servImplClientInteraceClass, final Class servImplServerInteraceClass) {
    super(servImplClientInteraceClass, servImplServerInteraceClass);
  }

  @Override protected SSServImplA createServImplForThread() throws Exception {
    return new SSDBSQLMySQLImpl((SSDBSQLConf) servConf);
  }

  @Override protected void initServSpecificStuff() throws Exception {
  }
}