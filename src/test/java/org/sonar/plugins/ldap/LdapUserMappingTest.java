package org.sonar.plugins.ldap;
import org.junit.Test;
import org.sonar.api.config.Settings;
import static org.fest.assertions.Assertions.assertThat;

public class LdapUserMappingTest {
  @Test public void defaults() {
    LdapUserMapping userMapping = new LdapUserMapping(new Settings(), "ldap");
    assertThat(userMapping.getBaseDn()).isNull();
    assertThat(userMapping.getRequest()).isEqualTo("(&(objectClass=inetOrgPerson)(uid={0}))");
    assertThat(userMapping.getRealNameAttribute()).isEqualTo("cn");
    assertThat(userMapping.getEmailAttribute()).isEqualTo("mail");
    assertThat(userMapping.toString()).isEqualTo("LdapUserMapping{" + "baseDn=null," + " request=(&(objectClass=inetOrgPerson)(uid={0}))," + " realNameAttribute=cn," + " emailAttribute=mail}");
  }

  @Test public void activeDirectory() {
    Settings settings = new Settings().setProperty("ldap.user.baseDn", "cn=users").setProperty("ldap.user.objectClass", "user").setProperty("ldap.user.loginAttribute", "sAMAccountName");
    LdapUserMapping userMapping = new LdapUserMapping(settings, "ldap");
    LdapSearch search = userMapping.createSearch(null, "tester");
    assertThat(search.getBaseDn()).isEqualTo("cn=users");
    assertThat(search.getRequest()).isEqualTo("(&(objectClass=user)(sAMAccountName={0}))");
    assertThat(search.getParameters()).isEqualTo(new String[] { "tester" });
    assertThat(search.getReturningAttributes()).isNull();
    assertThat(userMapping.toString()).isEqualTo("LdapUserMapping{" + "baseDn=cn=users," + " request=(&(objectClass=user)(sAMAccountName={0}))," + " realNameAttribute=cn," + " emailAttribute=mail}");
  }

  @Test public void realm() {
    Settings settings = new Settings().setProperty("ldap.realm", "example.org").setProperty("ldap.userObjectClass", "user").setProperty("ldap.loginAttribute", "sAMAccountName");
    LdapUserMapping userMapping = new LdapUserMapping(settings, "ldap");
    assertThat(userMapping.getBaseDn()).isEqualTo("dc=example,dc=org");
  }
}