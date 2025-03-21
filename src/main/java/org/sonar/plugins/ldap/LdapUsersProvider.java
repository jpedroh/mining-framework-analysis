package org.sonar.plugins.ldap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.UserDetails;
import org.sonar.api.utils.SonarException;
import javax.annotation.Nullable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.util.Map;

/**
 * @author Evgeny Mandrikov
 */
public class LdapUsersProvider extends ExternalUsersProvider {
  private static final Logger LOG = LoggerFactory.getLogger(LdapUsersProvider.class);

  private final Map<String, LdapContextFactory> contextFactories;

  private final Map<String, LdapUserMapping> userMappings;

  public LdapUsersProvider(Map<String, LdapContextFactory> contextFactories, Map<String, LdapUserMapping> userMappings) {
    this.contextFactories = contextFactories;
    this.userMappings = userMappings;
  }

  private static String getAttributeValue(@Nullable Attribute attribute) throws NamingException {
    if (attribute == null) {
      return "";
    }
    return (String) attribute.get();
  }

  /**
	 * @return details for specified user, or null if such user doesn't exist
	 * @throws SonarException
	 *             if unable to retrieve details
	 */
  public UserDetails doGetUserDetails(String username) {
    LOG.debug("Requesting details for user {}", username);
    if (userMappings.size() == 0) {
      throw new SonarException("Unable to retrieve details for user " + username);
    }
    for (String ldapIndex : userMappings.keySet()) {
      try {
        SearchResult searchResult = userMappings.get(ldapIndex).createSearch(contextFactories.get(ldapIndex), username).returns(userMappings.get(ldapIndex).getEmailAttribute(), userMappings.get(ldapIndex).getRealNameAttribute()).findUnique();
        if (searchResult == null) {
          LOG.debug("User {} not found", username);
          continue;
        }
        UserDetails details = new UserDetails();
        Attributes attributes = searchResult.getAttributes();
        details.setName(getAttributeValue(attributes.get(userMappings.get(ldapIndex).getRealNameAttribute())));
        details.setEmail(getAttributeValue(attributes.get(userMappings.get(ldapIndex).getEmailAttribute())));
        return details;
      } catch (NamingException e) {
        LOG.debug(e.getMessage(), e);
        throw new SonarException("Unable to retrieve details for user " + username, e);
      }
    }
    return null;
  }
}