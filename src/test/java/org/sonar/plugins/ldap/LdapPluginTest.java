package org.sonar.plugins.ldap;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class LdapPluginTest {
  @Test public void testGetExtensions() throws Exception {
    assertThat(new LdapPlugin().getExtensions().size()).isGreaterThan(0);
  }
}