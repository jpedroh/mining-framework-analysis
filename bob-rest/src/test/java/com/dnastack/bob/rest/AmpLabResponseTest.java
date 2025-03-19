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
 * Test of AMPLab service.
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
@RunWith(value = Arquillian.class) @RunAsClient public class AmpLabResponseTest extends AbstractResponseTest {
  private static final String BEACON = "amplab";

  @Test @Override public void testAllRefsFound(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "41087869", "A", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testSpecificRefFound(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "41087869", "A", "hg19" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testSpecificRefNotFound(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "41087869", "A", "hg38" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertFalse(br.getResponse());
  }

  @Test @Override public void testInvalidRef(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "41087869", "A", "hg100" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertEquals(br.getQuery().getReference(), null);
    assertNull(br.getResponse());
  }

  @Test @Override public void testRefConversion(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "41087869", "A", "grch38" };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertEquals(br.getQuery().getReference(), ReferenceTo.HG38);
    assertNotNull(br.getResponse());
  }

  @Test @Override public void testDifferentGenome(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "15", "47481776", "G", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testStringAllele(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "22", "17213589", "TGTTA", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Test @Override public void testDel(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "14", "106833420", "D", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(queriesMatch(br.getQuery(), query));
    assertTrue(br.getResponse());
  }

  @Override public void testIns(URL url) throws JAXBException, MalformedURLException {
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
    String[] query = { "14", "106833420", "DEL", null };
    BeaconResponseTo br = readBeaconResponse(url.toExternalForm() + getUrl(BEACON, query));
    assertNotNull(br);
    assertTrue(beaconsMatch(br.getBeacon(), BEACON));
    assertTrue(getNonMachingFields(br.getQuery(), query).size() == 1);
    assertEquals(query[2].substring(0, 1), br.getQuery().getAllele());
    assertTrue(br.getResponse());
  }

  @Test @Override public void testChromConversion(@ArquillianResource URL url) throws JAXBException, MalformedURLException {
    String[] query = { "chrom14", "106833420", "D", null };
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
}