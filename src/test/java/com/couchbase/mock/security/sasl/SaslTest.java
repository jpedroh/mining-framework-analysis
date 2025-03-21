package com.couchbase.mock.security.sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import org.junit.Assert;
import org.junit.Test;
import com.couchbase.mock.memcached.SaslCallbackHandler;

/**
 * @author Senol Ozer / Amadeus IT Group
 */
public class SaslTest {
  /**
     * SunSASL provider doesn't support the server mechanism PLAIN
     * https://docs.oracle.com/javase/8/docs/technotes/guides/security/sasl/sasl-refguide.html#SUN
     * 
     * @throws SaslException
     */
  @Test(expected = SaslException.class) public void testSaslPlain() throws SaslException {
    Sasl.createSaslServer("PLAIN", "couchbase", "localhost", null, new SaslCallbackHandler("foo", "bar"));
  }

  @Test public void testSaslCramMd5() throws SaslException {
    SaslServer saslServer = Sasl.createSaslServer("CRAM-MD5", "couchbase", "localhost", null, new SaslCallbackHandler("foo", "bar"));
    Assert.assertEquals("CRAM-MD5", saslServer.getMechanismName());
    Assert.assertEquals(false, saslServer.isComplete());
    Assert.assertFalse(saslServer instanceof ShaSaslServer);
  }

  @Test public void testSaslScramSha512() throws SaslException {
    SaslServer saslServer = Sasl.createSaslServer("SCRAM-SHA512", "couchbase", "localhost", null, new SaslCallbackHandler("foo", "bar"));
    Assert.assertEquals("SCRAM-SHA512", saslServer.getMechanismName());
    Assert.assertEquals(false, saslServer.isComplete());
    Assert.assertTrue(saslServer instanceof ShaSaslServer);
  }
}