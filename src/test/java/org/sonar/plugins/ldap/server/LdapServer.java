package org.sonar.plugins.ldap.server;
import org.junit.rules.ExternalResource;

public class LdapServer extends ExternalResource {
  private ApacheDS server;

  private String ldif;

  private final String realm;

  private final String baseDn;

  public LdapServer(String ldifResourceName) {
    this(ldifResourceName, "example.org", "dc=example,dc=org");
  }

  public LdapServer(String ldifResourceName, String realm, String baseDn) {
    this.ldif = ldifResourceName;
    this.realm = realm;
    this.baseDn = baseDn;
  }

  @Override protected void before() throws Throwable {
    server = ApacheDS.start(realm, baseDn);
    server.importLdif(LdapServer.class.getResourceAsStream(ldif));
  }

  @Override protected void after() {
    try {
      server.stop();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String getUrl() {
    return server.getUrl();
  }

  public void disableAnonymousAccess() {
    server.disableAnonymousAccess();
  }

  public void enableAnonymousAccess() {
    server.enableAnonymousAccess();
  }
}