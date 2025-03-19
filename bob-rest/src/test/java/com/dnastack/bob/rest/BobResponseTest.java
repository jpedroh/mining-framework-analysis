package com.dnastack.bob.rest;
import com.dnastack.bob.service.dto.BeaconResponseTo;
import com.dnastack.bob.service.dto.ReferenceTo;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import static com.dnastack.bob.rest.util.BeaconResponseTestUtils.beaconsMatch;
import static com.dnastack.bob.rest.util.BeaconResponseTestUtils.getNonMachingFields;
import static com.dnastack.bob.rest.util.BeaconResponseTestUtils.queriesMatch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test of Bob service.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
@RunWith(value = Arquillian.class) @RunAsClient public class BobResponseTest extends AbstractResponseTest {
  private static final String BEACON = "bob";

  @Test @Override public void testAllRefsFound(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "13", "32888798", "G", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testSpecificRefFound(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "13", "32888798", "G", "hg19" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testSpecificRefNotFound(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "13", "32888798", "G", "hg38" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertFalse(br.getResponse());
  }

  @Test @Override public void testInvalidRef(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "13", "32888798", "G", "hg100" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertEquals(br.getQuery().getReference(), null);
    assertNull(br.getResponse());
  }

  @Test @Override public void testRefConversion(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "13", "32888798", "G", "grch38" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertEquals(br.getQuery().getReference(), ReferenceTo.HG38);
    assertNotNull(br.getResponse());
  }

  @Test @Override public void testStringAllele(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "1", "46402", "TGT", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testDel(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "1", "73976147", "D", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testIns(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "1", "46402", "I", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertNotNull(br.getResponse());
  }

  @Test @Override public void testAllRefsNotFound(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "41087869", "C", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertFalse(br.getResponse());
  }

  @Test @Override public void testInvalidAllele(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "41087869", "DC", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertNull(br.getQuery().getAllele());
    assertNull(br.getResponse());
  }

  @Test @Override public void testAlleleConversion(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "1", "1002921", "DEL", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertEquals(query[2].substring(0, 1), br.getQuery().getAllele());
    assertNotNull(br.getResponse());
  }

  @Test @Override public void testChromConversion(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "chrom14", "106833421", "D", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue((getNonMachingFields(br.getQuery(), query).size() == 1) && (getNonMachingFields(br.getQuery(), query).contains(0)));
    assertNotNull(br.getResponse());
  }

  @Test @Override public void testInvalidChrom(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "30", "41087869", "A", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertNull(br.getQuery().getChromosome());
    assertNull(br.getResponse());
  }

  @Test @Override public void testChromX(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "X", "41087869", "A", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertNotNull(br.getResponse());
  }

  @Test @Override public void testChromMT(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "MT", "41087869", "A", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertNotNull(br.getResponse());
  }

  @Override public void testDifferentGenome(URL url) throws JAXBException, MalformedURLException {
  }
}