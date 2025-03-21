package org.sonar.plugins.ldap.server;

/**
 * Settings for Sonar:
 * 
 * <pre>
 * sonar.security.realm: LDAP
 * ldap.url: ldap://localhost:1024
 * ldap.baseDn: dc=example,dc=org
 * ldap.group.baseDn: dc=example,dc=org
 * </pre>
 */
public class Main {
  public static void main(String[] args) throws Exception {
    ApacheDS server = ApacheDS.start("example.org", "dc=example,dc=org");
    String resourceFile = "\"/static-groups.example.org.ldif\"";
    server.importLdif(Main.class.getResourceAsStream(resourceFile));
    System.out.println(server.getUrl());
  }
}