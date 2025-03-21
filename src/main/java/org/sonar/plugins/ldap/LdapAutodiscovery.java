package org.sonar.plugins.ldap;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Evgeny Mandrikov
 */
public final class LdapAutodiscovery {
  private static final Logger LOG = LoggerFactory.getLogger(LdapAutodiscovery.class);

  private LdapAutodiscovery() {
  }

  /**
	 * Get the DNS domain name (eg: example.org).
	 * 
	 * @return DNS domain
	 * @throws java.net.UnknownHostException
	 *             if unable to determine DNS domain
	 */
  public static String getDnsDomainName() throws UnknownHostException {
    return getDnsDomainName(InetAddress.getLocalHost().getCanonicalHostName());
  }

  /**
	 * Extracts DNS domain name from Fully Qualified Domain Name.
	 * 
	 * @param fqdn
	 *            Fully Qualified Domain Name
	 * @return DNS domain name or null, if can't be extracted
	 */
  public static String getDnsDomainName(String fqdn) {
    if (fqdn.indexOf('.') == -1) {
      return null;
    }
    return fqdn.substring(fqdn.indexOf('.') + 1);
  }

  /**
	 * Get the DNS DN domain (eg: dc=example,dc=org).
	 * 
	 * @param domain
	 *            DNS domain
	 * @return DNS DN domain
	 */
  public static String getDnsDomainDn(String domain) {
    StringBuilder result = new StringBuilder();
    String[] domainPart = domain.split("[.]");
    for (int i = 0; i < domainPart.length; i++) {
      result.append(i > 0 ? "," : "").append("dc=").append(domainPart[i]);
    }
    return result.toString();
  }

  /**
	 * Get LDAP exampleServer from DNS.
	 * 
	 * @param domain
	 *            DNS domain
	 * @return LDAP exampleServer or null if unable to determine
	 */
  public static String getLdapServer(String domain) {
    try {
      return getLdapServer(new InitialDirContext(), domain);
    } catch (NamingException e) {
      LOG.error("Unable to determine LDAP exampleServer from DNS", e);
      return null;
    }
  }

  static String getLdapServer(DirContext context, String domain) throws NamingException {
    Attributes lSrvAttrs = context.getAttributes("dns:/_ldap._tcp." + domain, new String[] { "srv" });
    Attribute serversAttribute = lSrvAttrs.get("srv");
    NamingEnumeration lEnum = serversAttribute.getAll();
    String server = null;
    int currentPriority = 0;
    int currentWeight = 0;
    while (lEnum.hasMore()) {
      String srvRecord = (String) lEnum.next();
      String[] srvData = srvRecord.split(" ");
      int priority = NumberUtils.toInt(srvData[0]);
      int weight = NumberUtils.toInt(srvData[1]);
      String port = srvData[2];
      String target = srvData[3];
      if (target.endsWith(".")) {
        target = target.substring(0, target.length() - 1);
      }
      if ((server == null) || (priority < currentPriority)) {
        server = "ldap://" + target + ":" + port;
        currentPriority = priority;
        currentWeight = weight;
      } else {
        if ((priority == currentPriority) && (weight > currentWeight)) {
          server = "ldap://" + target + ":" + port;
          currentWeight = weight;
        }
      }
    }
    return server;
  }
}