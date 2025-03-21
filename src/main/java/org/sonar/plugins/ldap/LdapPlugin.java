package org.sonar.plugins.ldap;
import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public class LdapPlugin extends SonarPlugin {
  public List getExtensions() {
    return ImmutableList.of(LdapRealm.class);
  }
}