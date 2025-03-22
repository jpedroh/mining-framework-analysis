package nl.garvelink.iban;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Contains information about IBAN country codes.
 */
public abstract class CountryCodes {
  private static final int SEPA = 1 << 8;

  private static final int REMOVE_SEPA_MASK = ~SEPA;

  /**
     * Known country codes, this list must be sorted to allow binary search.
     */
  private static final String[] COUNTRY_CODES = { "AD", "AE", "AL", "AO", "AT", "AZ", "BA", "BE", "BF", "BG", "BH", "BI", "BJ", "BR", "BY", "CG", "CH", "CI", "CM", "CR", "CV", "CY", "CZ", "DE", "DK", "DO", "DZ", "EE", "EG", "ES", "FI", "FO", "FR", "GA", "GB", "GE", "GI", "GL", "GR", "GT", "HR", "HU", "IE", "IL", "IQ", "IR", "IS", "IT", "JO", "KW", "KZ", "LB", "LC", "LI", "LT", "LU", "LV", "MC", "MD", "ME", "MG", "MK", "ML", "MR", "MT", "MU", "MZ", "NL", "NO", "PK", "PL", "PS", "PT", "QA", "RO", "RS", "SA", "SC", "SE", "SI", "SK", "SM", "SN", "ST", "SV", "TL", "TN", "TR", "UA", "VG", "XK" };

  /**
     * Lengths for each country's IBAN. The indices match the indices of {@link #COUNTRY_CODES}, the values are the expected length.
     */
  private static final int[] COUNTRY_IBAN_LENGTHS = { 24, 23, 28, 25, 20 | SEPA, 28, 20, 16 | SEPA, 27, 22 | SEPA, 22, 16, 28, 29, 28, 27, 21 | SEPA, 28, 27, 22, 25, 28 | SEPA, 24 | SEPA, 22 | SEPA, 18 | SEPA, 28, 24, 20 | SEPA, 27, 24 | SEPA, 18 | SEPA, 18, 27 | SEPA, 27, 22 | SEPA, 22, 23 | SEPA, 18, 27 | SEPA, 28, 21 | SEPA, 28 | SEPA, 22 | SEPA, 23, 23, 26, 26 | SEPA, 27 | SEPA, 30, 30, 20, 28, 32, 21 | SEPA, 20 | SEPA, 20 | SEPA, 21 | SEPA, 27 | SEPA, 24, 22, 27, 19, 28, 27, 31 | SEPA, 30, 25, 18 | SEPA, 15 | SEPA, 24, 28 | SEPA, 29, 25 | SEPA, 29, 24 | SEPA, 22, 24, 31, 24 | SEPA, 19 | SEPA, 24 | SEPA, 27 | SEPA, 28, 25, 28, 23, 24, 26, 29, 24, 20 };

  /**
     * The shortest valid IBAN according to {@link #COUNTRY_IBAN_LENGTHS}
     */
  public static final int SHORTEST_IBAN_LENGTH;

  /**
     * The longest valid IBAN according to {@link #COUNTRY_IBAN_LENGTHS}
     */
  public static final int LONGEST_IBAN_LENGTH;

  static {
    int min = Integer.MAX_VALUE;
    int max = 0;
    for (int countryIbanLength : COUNTRY_IBAN_LENGTHS) {
      final int length = REMOVE_SEPA_MASK & countryIbanLength;
      if (length > max) {
        max = length;
      }
      if (length < min) {
        min = length;
      }
    }
    SHORTEST_IBAN_LENGTH = min;
    LONGEST_IBAN_LENGTH = max;
  }

  /**
     * Returns the index of the given country code in {@link #COUNTRY_CODES} by binary search.
     * @param countryCode a country code.
     * @return the array index, or -1.
     */
  static int indexOf(String countryCode) {
    return Arrays.binarySearch(CountryCodes.COUNTRY_CODES, countryCode);
  }

  /**
     * Returns the IBAN length for a given country code.
     * @param countryCode a non-null, uppercase, two-character country code.
     * @return the IBAN length for the given country, or -1 if the input is not a known, two-character country code.
     * @throws NullPointerException if the input is null.
     */
  public static int getLengthForCountryCode(String countryCode) {
    int index = indexOf(countryCode);
    if (index > -1) {
      return CountryCodes.COUNTRY_IBAN_LENGTHS[index] & REMOVE_SEPA_MASK;
    }
    return -1;
  }

  /**
     * Returns whether the given country code is in SEPA.
     * @param countryCode a non-null, uppercase, two-character country code.
     * @return true if SEPA, false if not.
     * @throws NullPointerException if the input is null.
     */
  public static boolean isSEPACountry(String countryCode) {
    int index = indexOf(countryCode);
    if (index > -1) {
      return (CountryCodes.COUNTRY_IBAN_LENGTHS[index] & SEPA) == SEPA;
    }
    return false;
  }

  /**
     * Returns the known country codes.
     * @return the collection of known country codes, upper case, in alphabetical order.
     */
  public static Collection<String> getKnownCountryCodes() {
    return Collections.unmodifiableList(Arrays.asList(COUNTRY_CODES));
  }

  /**
     * Returns whether the given string is a known country code.
     * @param aCountryCode the string to evaluate.
     * @return {@code true} if {@code aCountryCode} is a two-letter, uppercase String present in {@link #getKnownCountryCodes()}.
     */
  public static boolean isKnownCountryCode(String aCountryCode) {
    if (aCountryCode == null || aCountryCode.length() != 2) {
      return false;
    }
    return indexOf(aCountryCode) >= 0;
  }

  /** Prevent instantiation of static utility class. */
  private CountryCodes() {
  }
}