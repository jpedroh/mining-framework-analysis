package ezvcard.io.json;
import static ezvcard.util.StringUtils.NEWLINE;
import static ezvcard.util.TestUtils.assertValidate;
import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ezvcard.VCard;
import ezvcard.VCardDataType;
import ezvcard.VCardVersion;
import ezvcard.io.scribe.SkipMeScribe;
import ezvcard.io.scribe.VCardPropertyScribe;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.parameter.VCardParameters;
import ezvcard.property.Address;
import ezvcard.property.Anniversary;
import ezvcard.property.Birthday;
import ezvcard.property.Gender;
import ezvcard.property.Geo;
import ezvcard.property.Key;
import ezvcard.property.SkipMeProperty;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.Timezone;
import ezvcard.property.VCardProperty;
import ezvcard.util.IOUtils;
import ezvcard.util.PartialDate;
import ezvcard.util.TelUri;
import ezvcard.util.UtcOffset;

/**
 * @author Michael Angstadt
 */
public class JCardWriterTest {
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test public void write_single_vcard() throws Throwable {
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw);
    writer.setAddProdId(false);
    VCard vcard = new VCard();
    vcard.setFormattedName("John Doe");
    writer.write(vcard);
    writer.close();
    String expected = "[\"vcard\"," + "[" + "[\"version\",{},\"text\",\"4.0\"]," + "[\"fn\",{},\"text\",\"John Doe\"]" + "]" + "]";
    assertEquals(expected, sw.toString());
  }

  @Test public void write_multiple_vcards() throws Throwable {
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw, true);
    writer.setAddProdId(false);
    VCard vcard = new VCard();
    vcard.setFormattedName("John Doe");
    writer.write(vcard);
    vcard = new VCard();
    vcard.setFormattedName("Jane Doe");
    writer.write(vcard);
    writer.close();
    String expected = "[" + "[\"vcard\"," + "[" + "[\"version\",{},\"text\",\"4.0\"]," + "[\"fn\",{},\"text\",\"John Doe\"]" + "]" + "]," + "[\"vcard\"," + "[" + "[\"version\",{},\"text\",\"4.0\"]," + "[\"fn\",{},\"text\",\"Jane Doe\"]" + "]" + "]" + "]";
    assertEquals(expected, sw.toString());
  }

  @Test public void setIndent() throws Throwable {
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw, true);
    writer.setAddProdId(false);
    writer.setIndent(true);
    VCard vcard = new VCard();
    vcard.setFormattedName("John Doe");
    writer.write(vcard);
    vcard = new VCard();
    vcard.setFormattedName("John Doe");
    writer.write(vcard);
    writer.close();
    String expected = "[" + NEWLINE + "[" + NEWLINE + "\"vcard\",[[" + NEWLINE + "  \"version\",{},\"text\",\"4.0\"],[" + NEWLINE + "  \"fn\",{},\"text\",\"John Doe\"]]],[" + NEWLINE + "\"vcard\",[[" + NEWLINE + "  \"version\",{},\"text\",\"4.0\"],[" + NEWLINE + "  \"fn\",{},\"text\",\"John Doe\"]]]" + NEWLINE + "]";
    assertEquals(expected, sw.toString());
  }

  @Test public void write_no_vcards() throws Throwable {
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw);
    writer.close();
    assertEquals("", sw.toString());
  }

  @Test public void write_raw_property() throws Throwable {
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw);
    writer.setAddProdId(false);
    VCard vcard = new VCard();
    vcard.setFormattedName("John Doe");
    vcard.addExtendedProperty("x-type", "value");
    writer.write(vcard);
    writer.close();
    String expected = "[\"vcard\"," + "[" + "[\"version\",{},\"text\",\"4.0\"]," + "[\"fn\",{},\"text\",\"John Doe\"]," + "[\"x-type\",{},\"unknown\",\"value\"]" + "]" + "]";
    assertEquals(expected, sw.toString());
  }

  @Test public void write_extended_property() throws Throwable {
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw);
    writer.registerScribe(new TestScribe());
    writer.setAddProdId(false);
    VCard vcard = new VCard();
    vcard.setFormattedName("John Doe");
    vcard.addProperty(new TestProperty(JCardValue.single("value")));
    writer.write(vcard);
    writer.close();
    String expected = "[\"vcard\"," + "[" + "[\"version\",{},\"text\",\"4.0\"]," + "[\"fn\",{},\"text\",\"John Doe\"]," + "[\"x-type\",{},\"text\",\"value\"]" + "]" + "]";
    assertEquals(expected, sw.toString());
  }

  @Test public void skipMeException() throws Throwable {
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw);
    writer.registerScribe(new SkipMeScribe());
    writer.setAddProdId(false);
    VCard vcard = new VCard();
    vcard.setFormattedName("John Doe");
    vcard.addProperty(new SkipMeProperty());
    writer.write(vcard);
    writer.close();
    String expected = "[\"vcard\"," + "[" + "[\"version\",{},\"text\",\"4.0\"]," + "[\"fn\",{},\"text\",\"John Doe\"]" + "]" + "]";
    assertEquals(expected, sw.toString());
  }

  @Test public void utf8() throws Throwable {
    VCard vcard = new VCard();
    vcard.addNote("\u019dote");
    File file = tempFolder.newFile();
    JCardWriter writer = new JCardWriter(file);
    writer.setAddProdId(false);
    writer.write(vcard);
    writer.close();
    String expected = "[\"vcard\"," + "[" + "[\"version\",{},\"text\",\"4.0\"]," + "[\"note\",{},\"text\",\"\u019dote\"]" + "]" + "]";
    String actual = IOUtils.getFileContents(file, "UTF-8");
    assertEquals(expected, actual);
  }

  @Test public void jcard_example() throws Throwable {
    VCard vcard = createExample();
    assertValidate(vcard).
<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    getTypes()
=======
    versions(VCardVersion.V4_0)
>>>>>>> /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/right.java
    .
<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    add(AddressType.WORK)
=======
    run()
>>>>>>> /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/right.java
    ;
    StringWriter sw = new StringWriter();
    JCardWriter writer = new JCardWriter(sw);

<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    tel.getTypes().add(TelephoneType.WORK);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    tel.getTypes().add(TelephoneType.VOICE);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    tel.getTypes().add(TelephoneType.WORK);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    tel.getTypes().add(TelephoneType.CELL);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    tel.getTypes().add(TelephoneType.VOICE);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    tel.getTypes().add(TelephoneType.VIDEO);
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mangstadt/ez-vcard/63ab6b841a7aa31372315a902b98fc5b2cc3b661/src/test/java/ezvcard/io/json/JCardWriterTest.java/left.java
    tel.getTypes().add(TelephoneType.TEXT);
=======
>>>>>>> Unknown file: This is a bug in JDime.

    writer.setAddProdId(false);
    writer.write(vcard);
    writer.close();
    String actual = sw.toString();
    assertExample(actual, "jcard-example.json");
  }

  public static VCard createExample() {
    VCard vcard = new VCard();
    vcard.setFormattedName("SimonPerreault");
    StructuredName n = new StructuredName();
    n.setFamily("Perreault");
    n.setGiven("Simon");
    n.addSuffix("ing.jr");
    n.addSuffix("M.Sc.");
    vcard.setStructuredName(n);
    Birthday bday = new Birthday(PartialDate.builder().month(2).date(3).build());
    vcard.setBirthday(bday);
    Anniversary anniversary = new Anniversary(PartialDate.builder().year(2009).month(8).date(8).hour(14).minute(30).second(0).offset(new UtcOffset(false, -5, 0)).build());
    vcard.setAnniversary(anniversary);
    vcard.setGender(Gender.male());
    vcard.addLanguage("fr").setPref(1);
    vcard.addLanguage("en").setPref(2);
    vcard.setOrganization("Viagenie").setType("work");
    Address adr = new Address();
    adr.setExtendedAddress("SuiteD2-630");
    adr.setStreetAddress("2875Laurier");
    adr.setLocality("Quebec");
    adr.setRegion("QC");
    adr.setPostalCode("G1V2M2");
    adr.setCountry("Canada");
    adr.addType(AddressType.WORK);
    vcard.addAddress(adr);
    TelUri telUri = new TelUri.Builder("+1-418-656-9254").extension("102").build();
    Telephone tel = new Telephone(telUri);
    tel.addType(TelephoneType.WORK);
    tel.addType(TelephoneType.VOICE);
    tel.setPref(1);
    vcard.addTelephoneNumber(tel);
    tel = new Telephone(new TelUri.Builder("+1-418-262-6501").build());
    tel.addType(TelephoneType.WORK);
    tel.addType(TelephoneType.CELL);
    tel.addType(TelephoneType.VOICE);
    tel.addType(TelephoneType.VIDEO);
    tel.addType(TelephoneType.TEXT);
    vcard.addTelephoneNumber(tel);
    vcard.addEmail("simon.perreault@viagenie.ca", EmailType.WORK);
    Geo geo = new Geo(46.772673, -71.282945);
    geo.setType("work");
    vcard.setGeo(geo);
    Key key = new Key("http://www.viagenie.ca/simon.perreault/simon.asc", null);
    key.setType("work");
    vcard.addKey(key);
    vcard.setTimezone(new Timezone(new UtcOffset(false, -5, 0)));
    vcard.addUrl("http://nomis80.org").setType("home");
    return vcard;
  }

  public static void assertExample(String actual, String exampleFileName) throws IOException {
    Filter filter = new Filter() {
      public String filter(String json) {
        json = json.replaceAll("\"bday\",\\{\\},\"date-and-or-time\"", "\"bday\",{},\"date\"");
        json = json.replaceAll("\"anniversary\",\\{\\},\"date-and-or-time\"", "\"anniversary\",{},\"date-time\"");
        return json;
      }
    };
    String expected = new String(IOUtils.toByteArray(JCardWriterTest.class.getResourceAsStream(exampleFileName)));
    expected = expected.replaceAll("\\s", "");
    if (filter != null) {
      expected = filter.filter(expected);
    }
    assertEquals(expected, actual);
  }

  private interface Filter {
    String filter(String json);
  }

  private static class TestProperty extends VCardProperty {
    public JCardValue value;

    public TestProperty(JCardValue value) {
      this.value = value;
    }

    @Override public TestProperty copy() {
      throw new UnsupportedOperationException("Copy method should not be used.");
    }
  }

  private static class TestScribe extends VCardPropertyScribe<TestProperty> {
    public TestScribe() {
      super(TestProperty.class, "X-TYPE");
    }

    @Override protected VCardDataType _defaultDataType(VCardVersion version) {
      return VCardDataType.TEXT;
    }

    @Override protected String _writeText(TestProperty property, VCardVersion version) {
      return null;
    }

    @Override protected TestProperty _parseText(String value, VCardDataType dataType, VCardVersion version, VCardParameters parameters, List<String> warnings) {
      return null;
    }

    @Override protected JCardValue _writeJson(TestProperty property) {
      return property.value;
    }
  }
}