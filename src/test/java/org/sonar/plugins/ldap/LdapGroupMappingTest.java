package org.sonar.plugins.ldap;
import org.junit.Test;
import org.sonar.api.config.Settings;
import static org.fest.assertions.Assertions.assertThat;

public class LdapGroupMappingTest {
  @Test public void defaults() {
    LdapGroupMapping groupMapping = new LdapGroupMapping(new Settings(), "ldap");
    assertThat(groupMapping.getBaseDn()).isNull();
    assertThat(groupMapping.getIdAttribute()).isEqualTo("cn");
    assertThat(groupMapping.getRequest()).isEqualTo("(&(objectClass=groupOfUniqueNames)(uniqueMember={0}))");
    assertThat(groupMapping.getRequiredUserAttributes()).isEqualTo(new String[] { "dn" });
    assertThat(groupMapping.toString()).isEqualTo("LdapGroupMapping{" + "baseDn=null," + " idAttribute=cn," + " requiredUserAttributes=[dn]," + " request=(&(objectClass=groupOfUniqueNames)(uniqueMember={0}))}");
  }

  @Test public void backward_compatibility() {
    Settings settings = new Settings().setProperty("ldap.group.objectClass", "group").setProperty("ldap.group.memberAttribute", "member");
    LdapGroupMapping groupMapping = new LdapGroupMapping(settings, "ldap");
    assertThat(groupMapping.getRequest()).isEqualTo("(&(objectClass=group)(member={0}))");
  }

  @Test public void custom_request() {
    Settings settings = new Settings().setProperty("ldap.group.request", "(&(|(objectClass=posixGroup)(objectClass=groupOfUniqueNames))(|(memberUid={uid})(uniqueMember={dn})))");
    LdapGroupMapping groupMapping = new LdapGroupMapping(settings, "ldap");
    assertThat(groupMapping.getRequest()).isEqualTo("(&(|(objectClass=posixGroup)(objectClass=groupOfUniqueNames))(|(memberUid={0})(uniqueMember={1})))");
    assertThat(groupMapping.getRequiredUserAttributes()).isEqualTo(new String[] { "uid", "dn" });
  }
}