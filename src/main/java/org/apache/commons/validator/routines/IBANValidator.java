package org.apache.commons.validator.routines;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

/**
 * IBAN Validator.
 * @since 1.5.0
 */
public class IBANValidator {
  private final Map<String, Validator> formatValidators;

  public static class Validator {
    private static final int MIN_LEN = 8;

    private static final int MAX_LEN = 34;

    final String countryCode;

    final RegexValidator validator;

    final int lengthOfIBAN;

    /**
         * Creates the validator
         * @param cc the country code
         * @param len the length of the IBAN
         * @param format the regex to use to check the format
         */
    public Validator(String cc, int len, String format) {
      if (!(cc.length() == 2 && Character.isUpperCase(cc.charAt(0)) && Character.isUpperCase(cc.charAt(1)))) {
        throw new IllegalArgumentException("Invalid country Code; must be exactly 2 upper-case characters");
      }
      if (len > MAX_LEN || len < MIN_LEN) {
        throw new IllegalArgumentException("Invalid length parameter, must be in range " + MIN_LEN + " to " + MAX_LEN + " inclusive: " + len);
      }
      if (!format.startsWith(cc)) {
        throw new IllegalArgumentException("countryCode \'" + cc + "\' does not agree with format: " + format);
      }
      this.countryCode = cc;
      this.lengthOfIBAN = len;
      this.validator = new RegexValidator(format);
    }
  }

  private static final Validator[] DEFAULT_FORMATS = { new Validator("AD", 24, "AD\\d{10}[A-Z0-9]{12}"), new Validator("AE", 23, "AE\\d{21}"), new Validator("AL", 28, "AL\\d{10}[A-Z0-9]{16}"), new Validator("AT", 20, "AT\\d{18}"), new Validator("AZ", 28, "AZ\\d{2}[A-Z]{4}[A-Z0-9]{20}"), new Validator("BA", 20, "BA\\d{18}"), new Validator("BE", 16, "BE\\d{14}"), new Validator("BG", 22, "BG\\d{2}[A-Z]{4}\\d{6}[A-Z0-9]{8}"), new Validator("BH", 22, "BH\\d{2}[A-Z]{4}[A-Z0-9]{14}"), new Validator("BR", 29, "BR\\d{25}[A-Z]{1}[A-Z0-9]{1}"), new Validator("BY", 28, "BY\\d{2}[A-Z0-9]{4}\\d{4}[A-Z0-9]{16}"), new Validator("CH", 21, "CH\\d{7}[A-Z0-9]{12}"), new Validator("CR", 22, "CR\\d{20}"), new Validator("CY", 28, "CY\\d{10}[A-Z0-9]{16}"), new Validator("CZ", 24, "CZ\\d{22}"), new Validator("DE", 22, "DE\\d{20}"), new Validator("DK", 18, "DK\\d{16}"), new Validator("DO", 28, "DO\\d{2}[A-Z0-9]{4}\\d{20}"), new Validator("EE", 20, "EE\\d{18}"), new Validator("EG", 29, "EG\\d{27}"), new Validator("ES", 24, "ES\\d{22}"), new Validator("FI", 18, "FI\\d{16}"), new Validator("FO", 18, "FO\\d{16}"), new Validator("FR", 27, "FR\\d{12}[A-Z0-9]{11}\\d{2}"), new Validator("GB", 22, "GB\\d{2}[A-Z]{4}\\d{14}"), new Validator("GE", 22, "GE\\d{2}[A-Z]{2}\\d{16}"), new Validator("GI", 23, "GI\\d{2}[A-Z]{4}[A-Z0-9]{15}"), new Validator("GL", 18, "GL\\d{16}"), new Validator("GR", 27, "GR\\d{9}[A-Z0-9]{16}"), new Validator("GT", 28, "GT\\d{2}[A-Z0-9]{24}"), new Validator("HR", 21, "HR\\d{19}"), new Validator("HU", 28, "HU\\d{26}"), new Validator("IE", 22, "IE\\d{2}[A-Z]{4}\\d{14}"), new Validator("IL", 23, "IL\\d{21}"), new Validator("IQ", 23, "IQ\\d{2}[A-Z]{4}\\d{15}"), new Validator("IS", 26, "IS\\d{24}"), new Validator("IT", 27, "IT\\d{2}[A-Z]{1}\\d{10}[A-Z0-9]{12}"), new Validator("JO", 30, "JO\\d{2}[A-Z]{4}\\d{4}[A-Z0-9]{18}"), new Validator("KW", 30, "KW\\d{2}[A-Z]{4}[A-Z0-9]{22}"), new Validator("KZ", 20, "KZ\\d{5}[A-Z0-9]{13}"), new Validator("LB", 28, "LB\\d{6}[A-Z0-9]{20}"), new Validator("LC", 32, "LC\\d{2}[A-Z]{4}[A-Z0-9]{24}"), new Validator("LI", 21, "LI\\d{7}[A-Z0-9]{12}"), new Validator("LT", 20, "LT\\d{18}"), new Validator("LU", 20, "LU\\d{5}[A-Z0-9]{13}"), new Validator("LV", 21, "LV\\d{2}[A-Z]{4}[A-Z0-9]{13}"), new Validator("LY", 25, "LY\\d{23}"), new Validator("MC", 27, "MC\\d{12}[A-Z0-9]{11}\\d{2}"), new Validator("MD", 24, "MD\\d{2}[A-Z0-9]{20}"), new Validator("ME", 22, "ME\\d{20}"), new Validator("MK", 19, "MK\\d{5}[A-Z0-9]{10}\\d{2}"), new Validator("MR", 27, "MR\\d{25}"), new Validator("MT", 31, "MT\\d{2}[A-Z]{4}\\d{5}[A-Z0-9]{18}"), new Validator("MU", 30, "MU\\d{2}[A-Z]{4}\\d{19}[A-Z]{3}"), new Validator("NL", 18, "NL\\d{2}[A-Z]{4}\\d{10}"), new Validator("NO", 15, "NO\\d{13}"), new Validator("PK", 24, "PK\\d{2}[A-Z]{4}[A-Z0-9]{16}"), new Validator("PL", 28, "PL\\d{26}"), new Validator("PS", 29, "PS\\d{2}[A-Z]{4}[A-Z0-9]{21}"), new Validator("PT", 25, "PT\\d{23}"), new Validator("QA", 29, "QA\\d{2}[A-Z]{4}[A-Z0-9]{21}"), new Validator("RO", 24, "RO\\d{2}[A-Z]{4}[A-Z0-9]{16}"), new Validator("RS", 22, "RS\\d{20}"), new Validator("SA", 24, "SA\\d{4}[A-Z0-9]{18}"), new Validator("SC", 31, "SC\\d{2}[A-Z]{4}\\d{20}[A-Z]{3}"), new Validator("SE", 24, "SE\\d{22}"), new Validator("SI", 19, "SI\\d{17}"), new Validator("SK", 24, "SK\\d{22}"), new Validator("SM", 27, "SM\\d{2}[A-Z]{1}\\d{10}[A-Z0-9]{12}"), new Validator("ST", 25, "ST\\d{23}"), new Validator("SV", 28, "SV\\d{2}[A-Z]{4}\\d{20}"), new Validator("TL", 23, "TL\\d{21}"), new Validator("TN", 24, "TN\\d{22}"), new Validator("TR", 26, "TR\\d{8}[A-Z0-9]{16}"), new Validator("UA", 29, "UA\\d{8}[A-Z0-9]{19}"), new Validator("VA", 22, "VA\\d{20}"), new Validator("VG", 24, "VG\\d{2}[A-Z]{4}\\d{16}"), new Validator("XK", 20, "XK\\d{18}") };

  /** The singleton instance which uses the default formats */
  public static final IBANValidator DEFAULT_IBAN_VALIDATOR = new IBANValidator();

  /**
     * Return a singleton instance of the IBAN validator using the default formats
     *
     * @return A singleton instance of the IBAN validator
     */
  public static IBANValidator getInstance() {
    return DEFAULT_IBAN_VALIDATOR;
  }

  /**
     * Create a default IBAN validator.
     */
  public IBANValidator() {
    this(DEFAULT_FORMATS);
  }

  /**
     * Create an IBAN validator from the specified map of IBAN formats.
     *
     * @param formatMap map of IBAN formats
     */
  public IBANValidator(Validator[] formatMap) {
    this.formatValidators = createValidators(formatMap);
  }

  private Map<String, Validator> createValidators(Validator[] formatMap) {
    Map<String, Validator> m = new ConcurrentHashMap<>();
    for (Validator v : formatMap) {
      m.put(v.countryCode, v);
    }
    return m;
  }

  /**
     * Validate an IBAN Code
     *
     * @param code The value validation is being performed on
     * @return <code>true</code> if the value is valid
     */
  public boolean isValid(String code) {
    Validator formatValidator = getValidator(code);
    if (formatValidator == null || code.length() != formatValidator.lengthOfIBAN || !formatValidator.validator.isValid(code)) {
      return false;
    }
    return IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(code);
  }

  /**
     * Does the class have the required validator?
     *
     * @param code the code to check
     * @return true if there is a validator
     */
  public boolean hasValidator(String code) {
    return getValidator(code) != null;
  }

  /**
     * Gets a copy of the default Validators.
     * 
     * @return a copy of the default Validator array
     */
  public Validator[] getDefaultValidators() {
    return Arrays.copyOf(DEFAULT_FORMATS, DEFAULT_FORMATS.length);
  }

  /**
     * Get the Validator for a given IBAN
     * 
     * @param code a string starting with the ISO country code (e.g. an IBAN)
     * 
     * @return the validator or {@code null} if there is not one registered.
     */
  public Validator getValidator(String code) {
    if (code == null || code.length() < 2) {
      return null;
    }
    String key = code.substring(0, 2);
    return formatValidators.get(key);
  }

  /**
     * Installs a validator.
     * Will replace any existing entry which has the same countryCode
     * 
     * @param validator the instance to install.
     * @return the previous Validator, or {@code null} if there was none
     * @throws IllegalStateException if an attempt is made to modify the singleton validator
     */
  public Validator setValidator(Validator validator) {
    if (this == DEFAULT_IBAN_VALIDATOR) {
      throw new IllegalStateException("The singleton validator cannot be modified");
    }
    return formatValidators.put(validator.countryCode, validator);
  }

  /**
     * Installs a validator.
     * Will replace any existing entry which has the same countryCode.
     * 
     * @param countryCode the country code
     * @param length the length of the IBAN. Must be &ge; 8 and &le; 32.
     * If the length is &lt; 0, the validator is removed, and the format is not used.
     * @param format the format of the IBAN (as a regular expression)
     * @return the previous Validator, or {@code null} if there was none
     * @throws IllegalArgumentException if there is a problem
     * @throws IllegalStateException if an attempt is made to modify the singleton validator
     */
  public Validator setValidator(String countryCode, int length, String format) {
    if (this == DEFAULT_IBAN_VALIDATOR) {
      throw new IllegalStateException("The singleton validator cannot be modified");
    }
    if (length < 0) {
      return formatValidators.remove(countryCode);
    }
    return setValidator(new Validator(countryCode, length, format));
  }
}