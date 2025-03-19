package org.n0pe.asadmin.commands;
import java.util.ArrayList;
import java.util.List;
import org.n0pe.asadmin.AbstractAsAdminCmd;

/**
 * @author Paul Merlin
 * @author Christophe Souvignier
 */
public class Database extends AbstractAsAdminCmd {
  public static final String START = "start-database";

  public static final String STOP = "stop-database";

  public static final String DB_HOST = "--dbhost";

  public static final String DB_PORT = "--dbport";

  private Boolean start = null;

  private String dbHost;

  private String dbPort;

  /**
     * Database CTOR.
     */
  public Database() {
  }

  public Database(String dbHost, String dbPort) {
    this.dbHost = dbHost;
    this.dbPort = dbPort;
  }

  public Database setDbHost(String dbHost) {
    this.dbHost = dbHost;
    return this;
  }

  public Database setDbPort(String dbPort) {
    this.dbPort = dbPort;
    return this;
  }

  public Database start() {
    start = Boolean.TRUE;
    return this;
  }

  public Database stop() {
    start = Boolean.FALSE;
    return this;
  }

  public boolean needCredentials() {
    return false;
  }

  public String getActionCommand() {
    if (start == null) {
      throw new IllegalStateException();
    }
    return start.booleanValue() ? START : STOP;
  }

  public String[] getParameters() {
    final List<String> params = new ArrayList<String>();
    if (isSet(dbHost)) {
      params.add(DB_HOST);
      params.add(dbHost);
    }
    if (isSet(dbPort)) {
      params.add(DB_PORT);
      params.add(dbPort);
    }
    return params.toArray(new String[0]);
  }

  private final boolean isSet(String str) {
    return str != null && str.trim().length() > 0;
  }
}