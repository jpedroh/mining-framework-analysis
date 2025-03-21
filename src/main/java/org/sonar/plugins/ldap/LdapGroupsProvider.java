package org.sonar.plugins.ldap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.security.ExternalGroupsProvider;
import org.sonar.api.utils.SonarException;

/**
 * @author Evgeny Mandrikov
 */
public class LdapGroupsProvider extends ExternalGroupsProvider {
  private static final Logger LOG = LoggerFactory.getLogger(LdapGroupsProvider.class);

  private final Map<String, LdapContextFactory> contextFactories;

  private final Map<String, LdapUserMapping> userMappings;

  private final Map<String, LdapGroupMapping> groupMappings;

  public LdapGroupsProvider(Map<String, LdapContextFactory> contextFactories, Map<String, LdapUserMapping> userMappings, Map<String, LdapGroupMapping> groupMapping) {
    this.contextFactories = contextFactories;
    this.userMappings = userMappings;
    this.groupMappings = groupMapping;
  }

  /**
   * @throws SonarException if unable to retrieve groups
   */
  public Collection<String> doGetGroups(String username) {
    checkPrerequisites(username);
    Set<String> groups = Sets.newHashSet();
    List<SonarException> sonarExceptions = new ArrayList<SonarException>();
    for (String serverKey : userMappings.keySet()) {
      if (!groupMappings.containsKey(serverKey)) {
        LOG.debug(" No group mapping for this ldap instance {}", serverKey);
        continue;
      }
      SearchResult searchResult = searchUserGroups(username, sonarExceptions, serverKey);
      if (searchResult != null) {
        try {
          String[] serverKeysForGroup = groupMappings.get(serverKey).getGroupRequestServersOverride();
          if (serverKeysForGroup == null) {
            serverKeysForGroup = new String[] { serverKey };
          }
          for (String serverKeyForGroup : serverKeysForGroup) {
            NamingEnumeration<SearchResult> result = groupMappings.get(serverKeyForGroup).createSearch(contextFactories.get(serverKeyForGroup), searchResult).find();
            groups.addAll(mapGroups(serverKey, result));
          }
          break;
        } catch (NamingException e) {
          LOG.debug(e.getMessage(), e);
          sonarExceptions.add(new SonarException("Unable to retrieve groups for user " + username + " in " + serverKey, e));
        }
      } else {
        LOG.debug("user not found on server {}", serverKey);
        continue;
      }
    }
    checkResults(groups, sonarExceptions);
    return groups;
  }

  private void checkResults(Set<String> groups, List<SonarException> sonarExceptions) {
    if (groups.isEmpty() && !sonarExceptions.isEmpty()) {
      throw sonarExceptions.iterator().next();
    }
  }

  private void checkPrerequisites(String username) {
    if (userMappings.isEmpty() || groupMappings.isEmpty()) {
      throw new SonarException("Unable to retrieve details for user " + username + ": No user or group mapping found.");
    }
  }

  private SearchResult searchUserGroups(String username, List<SonarException> sonarExceptions, String serverKey) {
    SearchResult searchResult = null;
    try {
      LOG.debug("Requesting groups for user {} on Server {}", username, serverKey);
      searchResult = userMappings.get(serverKey).createSearch(contextFactories.get(serverKey), username).returns(groupMappings.get(serverKey).getRequiredUserAttributes()).findUnique();
    } catch (NamingException e) {
      LOG.debug(e.getMessage(), e);
      sonarExceptions.add(new SonarException("Unable to retrieve groups for user " + username + " in " + serverKey, e));
    }
    return searchResult;
  }

  /**
   * Map all the groups.
   *
   * @param serverKey The index we use to choose the correct {@link LdapGroupMapping}.
   * @param searchResult The {@link SearchResult} from the search for the user.
   * @return A {@link Collection} of groups the user is member of.
   * @throws NamingException
   */
  private Collection<String> mapGroups(String serverKey, NamingEnumeration<SearchResult> searchResult) throws NamingException {
    Set<String> groups = new HashSet<String>();
    while (searchResult.hasMoreElements()) {
      SearchResult obj = (SearchResult) searchResult.nextElement();
      Attributes attributes = obj.getAttributes();
      String groupId = (String) attributes.get(groupMappings.get(serverKey).getIdAttribute()).get();
      LOG.debug("found group {}", groupId);
      groups.add(groupId);
    }
    return groups;
  }
}