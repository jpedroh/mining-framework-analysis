package org.sonar.plugins.ldap;
import org.junit.Test;
import org.mockito.Mockito;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import java.net.UnknownHostException;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LdapAutodiscoveryTest {
  @Test public void testGetDnsDomain() throws UnknownHostException {
    assertThat(LdapAutodiscovery.getDnsDomainName("localhost")).isNull();
    assertThat(LdapAutodiscovery.getDnsDomainName("godin.example.org")).isEqualTo("example.org");
    assertThat(LdapAutodiscovery.getDnsDomainName("godin.usr.example.org")).isEqualTo("usr.example.org");
  }

  @Test public void testGetDnsDomainWithoutParameter() throws UnknownHostException {
    try {
      LdapAutodiscovery.getDnsDomainName();
    } catch (UnknownHostException e) {
      fail(e.getMessage());
    }
  }

  @Test public void testGetDnsDomainDn() {
    assertThat(LdapAutodiscovery.getDnsDomainDn("example.org")).isEqualTo("dc=example,dc=org");
  }

  @Test public void testGetLdapServer() throws NamingException {
    DirContext context = mock(DirContext.class);
    Attributes attributes = mock(Attributes.class);
    Attribute attribute = mock(Attribute.class);
    NamingEnumeration namingEnumeration = mock(NamingEnumeration.class);
    when(context.getAttributes(Mockito.anyString(), Mockito.<String[]>anyObject())).thenReturn(attributes);
    when(attributes.get(Mockito.eq("srv"))).thenReturn(attribute);
    when(attribute.getAll()).thenReturn(namingEnumeration);
    when(namingEnumeration.hasMore()).thenReturn(true, true, true, true, true, false);
    when(namingEnumeration.next()).thenReturn("10 40 389 ldap5.example.org.").thenReturn("0 10 389 ldap3.example.org").thenReturn("0 60 389 ldap1.example.org").thenReturn("0 30 389 ldap2.example.org").thenReturn("10 60 389 ldap4.example.org");
    assertThat(LdapAutodiscovery.getLdapServer(context, "example.org.")).isEqualTo("ldap://ldap1.example.org:389");
  }
}