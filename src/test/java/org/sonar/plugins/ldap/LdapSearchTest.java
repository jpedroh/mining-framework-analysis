package org.sonar.plugins.ldap;
import com.google.common.collect.Iterators;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.plugins.ldap.server.LdapServer;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import java.util.Map;
import static org.fest.assertions.Assertions.assertThat;

public class LdapSearchTest {
  @ClassRule public static LdapServer server = new LdapServer("/users.example.org.ldif");

  @Rule public ExpectedException thrown = ExpectedException.none();

  private static Map<String, LdapContextFactory> contextFactories;

  @BeforeClass public static void init() {
    contextFactories = new LdapSettingsManager(LdapSettingsFactory.generateSimpleAnonymousAccessSettings(server, null)).getContextFactories();
  }

  @Test public void subtreeSearch() throws Exception {
    LdapSearch search = new LdapSearch(contextFactories.get(LdapSettingsFactory.LDAP)).setBaseDn("dc=example,dc=org").setRequest("(objectClass={0})").setParameters("inetOrgPerson").returns("objectClass");
    assertThat(search.getBaseDn()).isEqualTo("dc=example,dc=org");
    assertThat(search.getScope()).isEqualTo(SearchControls.SUBTREE_SCOPE);
    assertThat(search.getRequest()).isEqualTo("(objectClass={0})");
    assertThat(search.getParameters()).isEqualTo(new String[] { "inetOrgPerson" });
    assertThat(search.getReturningAttributes()).isEqualTo(new String[] { "objectClass" });
    assertThat(search.toString()).isEqualTo("LdapSearch{baseDn=dc=example,dc=org, scope=subtree, request=(objectClass={0}), parameters=[inetOrgPerson], attributes=[objectClass]}");
    assertThat(Iterators.size(Iterators.forEnumeration(search.find()))).isEqualTo(3);
    thrown.expect(NamingException.class);
    thrown.expectMessage("Non unique result for " + search.toString());
    search.findUnique();
  }

  @Test public void oneLevelSearch() throws Exception {
    LdapSearch search = new LdapSearch(contextFactories.get(LdapSettingsFactory.LDAP)).setBaseDn("dc=example,dc=org").setScope(SearchControls.ONELEVEL_SCOPE).setRequest("(objectClass={0})").setParameters("inetOrgPerson").returns("cn");
    assertThat(search.getBaseDn()).isEqualTo("dc=example,dc=org");
    assertThat(search.getScope()).isEqualTo(SearchControls.ONELEVEL_SCOPE);
    assertThat(search.getRequest()).isEqualTo("(objectClass={0})");
    assertThat(search.getParameters()).isEqualTo(new String[] { "inetOrgPerson" });
    assertThat(search.getReturningAttributes()).isEqualTo(new String[] { "cn" });
    assertThat(search.toString()).isEqualTo("LdapSearch{baseDn=dc=example,dc=org, scope=onelevel, request=(objectClass={0}), parameters=[inetOrgPerson], attributes=[cn]}");
    assertThat(Iterators.size(Iterators.forEnumeration(search.find()))).isEqualTo(0);
    assertThat(search.findUnique()).isNull();
  }

  @Test public void objectSearch() throws Exception {
    LdapSearch search = new LdapSearch(contextFactories.get(LdapSettingsFactory.LDAP)).setBaseDn("cn=bind,ou=users,dc=example,dc=org").setScope(SearchControls.OBJECT_SCOPE).setRequest("(objectClass={0})").setParameters("uidObject").returns("uid");
    assertThat(search.getBaseDn()).isEqualTo("cn=bind,ou=users,dc=example,dc=org");
    assertThat(search.getScope()).isEqualTo(SearchControls.OBJECT_SCOPE);
    assertThat(search.getRequest()).isEqualTo("(objectClass={0})");
    assertThat(search.getParameters()).isEqualTo(new String[] { "uidObject" });
    assertThat(search.getReturningAttributes()).isEqualTo(new String[] { "uid" });
    assertThat(search.toString()).isEqualTo("LdapSearch{baseDn=cn=bind,ou=users,dc=example,dc=org, scope=object, request=(objectClass={0}), parameters=[uidObject], attributes=[uid]}");
    assertThat(Iterators.size(Iterators.forEnumeration(search.find()))).isEqualTo(1);
    assertThat(search.findUnique()).isNotNull();
  }
}