package org.apache.commons.validator.routines.checkdigit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;

/**
 * IBAN Check Digit Test.
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class IBANCheckDigitTest extends AbstractCheckDigitTest {
  /**
     * Constructor
     * @param name test name
     */
  public IBANCheckDigitTest(final String name) {
    super(name);
    checkDigitLth = 2;
  }

  /**
     * Set up routine & valid codes.
     */
  @Override protected void setUp() throws Exception {
    super.setUp();
    routine = IBANCheckDigit.IBAN_CHECK_DIGIT;
    valid = new String[] { "AD1200012030200359100100", "AE070331234567890123456", "AL47212110090000000235698741", "AT611904300234573201", "AZ21NABZ00000000137010001944", "BA391290079401028494", "BE62510007547061", "BE68539007547034", "BG80BNBG96611020345678", "BH67BMAG00001299123456", "BI4210000100010000332045181", "BR1800000000141455123924100C2", "BY13NBRB3600900000002Z00AB00", "CH3900700115201849173", "CH9300762011623852957", "CR05015202001026284066", "CY17002001280000001200527600", "CZ6508000000192000145399", "DE89370400440532013000", "DK5000400440116243", "DO28BAGR00000001212453611324", "EE382200221020145685", "ES8023100001180000012345", "FI2112345600000785", "FO6264600001631634", "FR1420041010050500013M02606", "GB29NWBK60161331926819", "GI75NWBK000000007099453", "GL8964710001000206", "GR1601101250000000012300695", "GT82TRAJ01020000001210029690", "HR1210010051863000160", "HU42117730161111101800000000", "IE29AIBK93115212345678", "IL620108000000099999999", "IQ98NBIQ850123456789012", "IS140159260076545510730339", "IT60X0542811101000000123456", "JO94CBJO0010000000000131000302", "KW81CBKU0000000000001234560101", "KZ86125KZT5004100100", "LB62099900000001001901229114", "LC55HEMM000100010012001200023015", "LI21088100002324013AA", "LT121000011101001000", "LU280019400644750000", "LV80BANK0000435195001", "MC5811222000010123456789030", "MD24AG000225100013104168", "ME25505000012345678951", "MK07250120000058984", "MR1300020001010000123456753", "MT84MALT011000012345MTLCAST001S", "MU17BOMM0101101030300200000MUR", "NL39RABO0300065264", "NL91ABNA0417164300", "NO9386011117947", "PK36SCBL0000001123456702", "PL27114020040000300201355387", "PL60102010260000042270201111", "PS92PALS000000000400123456702", "PT50000201231234567890154", "QA58DOHB00001234567890ABCDEFG", "RO49AAAA1B31007593840000", "RS35260005601001611379", "SA0380000000608010167519", "SC18SSCB11010000000000001497USD", "SE3550000000054910000003", "SD2129010501234001", "SI56191000000123438", "SK3112000000198742637541", "SM86U0322509800000000270100", "ST68000100010051845310112", "SV62CENR00000000000000700025", "TL380080012345678910157", "TN5910006035183598478831", "TR330006100519786457841326", "UA213223130000026007233566001", "VA59001123000012345678", "VG96VPVG0000012345678901", "XK051212012345678906", "AA0200000000053", "AA9700000000089", "AA9800000000071", "ZZ02ZZZZZZZZZZZZZZZZZZZZZZZZZ04", "ZZ97ZZZZZZZZZZZZZZZZZZZZZZZZZ40", "ZZ98ZZZZZZZZZZZZZZZZZZZZZZZZZ22" };
    invalid = new String[] { "510007+47061BE63", "IE01AIBK93118702569045", "AA0000000000089", "AA9900000000053" };
    zeroSum = null;
    missingMessage = "Invalid Code length=0";
  }

  /**
     * Test zero sum
     */
  @Override public void testZeroSum() {
  }

  /**
     * Returns an array of codes with invalid check digits.
     *
     * @param codes Codes with valid check digits
     * @return Codes with invalid check digits
     */
  @Override protected String[] createInvalidCodes(final String[] codes) {
    final List<String> list = new ArrayList<>();
    for (final String code2 : codes) {
      final String code = removeCheckDigit(code2);
      final String check = checkDigit(code2);
      for (int j = 2; j <= 98; j++) {
        final String curr = j > 9 ? "" + j : "0" + j;
        if (!curr.equals(check)) {
          list.add(code.substring(0, 2) + curr + code.substring(4));
        }
      }
    }
    return list.toArray(new String[list.size()]);
  }

  /**
     * Returns a code with the Check Digits (i.e. characters 3&4) set to "00".
     *
     * @param code The code
     * @return The code with the zeroed check digits
     */
  @Override protected String removeCheckDigit(final String code) {
    return code.substring(0, 2) + "00" + code.substring(4);
  }

  /**
     * Returns the check digit (i.e. last character) for a code.
     *
     * @param code The code
     * @return The check digit
     */
  @Override protected String checkDigit(final String code) {
    if (code == null || code.length() <= checkDigitLth) {
      return "";
    }
    return code.substring(2, 4);
  }

  public void testOther() throws Exception {
    try (BufferedReader rdr = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("IBANtests.txt"), "ASCII"))) {
      String line;
      while ((line = rdr.readLine()) != null) {
        if (!line.startsWith("#") && !line.isEmpty()) {
          if (line.startsWith("-")) {
            line = line.substring(1);
            Assert.assertFalse(line, routine.isValid(line.replace(" ", "")));
          } else {
            Assert.assertTrue(line, routine.isValid(line.replace(" ", "")));
          }
        }
      }
    }
  }
}