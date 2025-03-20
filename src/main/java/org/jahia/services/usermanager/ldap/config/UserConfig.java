package org.jahia.services.usermanager.ldap.config;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

/**
 * User specific config provide by the ldap config file
 * @author kevan
 */
public class UserConfig extends AbstractConfig {
  private String uidSearchName;

  private String uidSearchAttribute = "cn";

  private boolean shareable = false;

  public UserConfig() {
  }

  public void handleDefaults() {
    if (StringUtils.isEmpty(getSearchObjectclass())) {
      setSearchObjectclass("person");
    }
    if (getSearchWildcardsAttributes().isEmpty()) {
      setSearchWildcardsAttributes(Sets.newHashSet("ou", "cn", "o", "c", "mail", "uid", "uniqueIdentifier", "givenName", "sn", "dn"));
    }
    if (getAttributesMapper().isEmpty()) {
      getAttributesMapper().put("username", uidSearchAttribute);
      getAttributesMapper().put("j:firstName", "givenName");
      getAttributesMapper().put("j:lastName", "sn");
      getAttributesMapper().put("j:email", "mail");
      getAttributesMapper().put("j:organization", "o");
    }
  }

  public boolean isMinimalSettingsOk() {
    return StringUtils.isNotEmpty(getUrl()) && StringUtils.isNotEmpty(getUidSearchName());
  }

  public String getUidSearchName() {
    return uidSearchName;
  }

  public void setUidSearchName(String uidSearchName) {
    this.uidSearchName = uidSearchName;
  }

  public String getUidSearchAttribute() {
    return uidSearchAttribute;
  }

  public void setUidSearchAttribute(String uidSearchAttribute) {
    this.uidSearchAttribute = uidSearchAttribute;
  }

  public boolean isShareable() {
    return shareable;
  }

  public void setShareable(boolean shareable) {
    this.shareable = shareable;
  }
}