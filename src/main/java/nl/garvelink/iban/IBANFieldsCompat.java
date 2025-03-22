package nl.garvelink.iban;

/**
 * Extracts national encoded information from IBANs.
 *
 * <p>This class returns nulls to express fields that are not present in every country's IBAN format. If you're on JDK8,
 * you may prefer {@link IBANFields}, which uses the {@code Optional} API.</p>
 */
public class IBANFieldsCompat {
  private static final int BANK_IDENTIFIER_BEGIN_MASK = 0xFF;

  private static final int BANK_IDENTIFIER_END_SHIFT = 8;

  private static final int BANK_IDENTIFIER_END_MASK = 0xFF << BANK_IDENTIFIER_END_SHIFT;

  private static final int BRANCH_IDENTIFIER_BEGIN_SHIFT = 16;

  private static final int BRANCH_IDENTIFIER_BEGIN_MASK = 0xFF << BRANCH_IDENTIFIER_BEGIN_SHIFT;

  private static final int BRANCH_IDENTIFIER_END_SHIFT = 24;

  private static final int BRANCH_IDENTIFIER_END_MASK = 0xFF << BRANCH_IDENTIFIER_END_SHIFT;

  /**
     * Returns the bank identifier from the given IBAN, if available.
     * <p><strong>Finland (FI): </strong>
     * The spec mentions both "Not in use" and "Position 1-3 indicate the bank or banking group." I have taken "bank
     * or banking group" to be more or less synonymous with Bank Identifier and return it as such.</p>
     * <p><strong>Slovenia (SI): </strong>
     * The five digits following the checksum encode the financial institution and sub-encode the branch identifier
     * if applicable, depending on the type of financial institution. The library returns all five digits as the bank
     * identifier and never returns a branch identifier.</p>
     * <p><strong>Republic of Kosovo (XK): </strong>
     * The four digits following the checksum encode the Bank ID, and the last two of these four sub-encode the
     * branch ID. The library returns all four digits as the bank identifier. For example: if the IBAN has "1234" in
     * these positions, then the bank identifier is returned as "1234" and the branch identifier as "34".</p>
     * @param iban an iban to evaluate. Cannot be null.
     * @return the bank ID for this IBAN, or null if unknown.
     */
  public static String getBankIdentifier(IBAN iban) {
    int index = CountryCodes.indexOf(iban.getCountryCode());
    if (index > -1) {
      int data = BANK_CODE_BRANCH_CODE[index];
      int bankIdBegin = data & BANK_IDENTIFIER_BEGIN_MASK;
      int bankIdEnd = (data & BANK_IDENTIFIER_END_MASK) >>> BANK_IDENTIFIER_END_SHIFT;
      return bankIdBegin != 0 ? iban.toPlainString().substring(bankIdBegin, bankIdEnd) : null;
    }
    return null;
  }

  /**
     * Returns the branch identifier from the given IBAN, if available.
     * @param iban an iban to evaluate. Cannot be null.
     * @return the branch ID for this IBAN, or null if unknown.
     */
  public static String getBranchIdentifier(IBAN iban) {
    int index = CountryCodes.indexOf(iban.getCountryCode());
    if (index > -1) {
      int data = BANK_CODE_BRANCH_CODE[index];
      int branchIdBegin = (data & BRANCH_IDENTIFIER_BEGIN_MASK) >>> BRANCH_IDENTIFIER_BEGIN_SHIFT;
      int branchIdEnd = (data & BRANCH_IDENTIFIER_END_MASK) >>> BRANCH_IDENTIFIER_END_SHIFT;
      return branchIdBegin != 0 ? iban.toPlainString().substring(branchIdBegin, branchIdEnd) : null;
    }
    return null;
  }

  /**
     * Contains the start- and end-index (as per {@link String#substring(int, int)}) of the bank code and branch code
     * within a country's IBAN format. Mask:
     * <pre>
     * 0x000000FF <- begin offset bank id
     * 0x0000FF00 <- end offset bank id
     * 0x00FF0000 <- begin offset branch id
     * 0xFF000000 <- end offset branch id
     * </pre>
     */
  private static final int[] BANK_CODE_BRANCH_CODE = { 4 | 8 << 8 | 8 << 16 | 12 << 24, 4 | 7 << 8, 4 | 7 << 8 | 7 << 16 | 11 << 24, 0, 4 | 9 << 8, 4 | 8 << 8, 4 | 7 << 8 | 7 << 16 | 10 << 24, 4 | 7 << 8, 0, 4 | 8 << 8 | 8 << 16 | 12 << 24, 4 | 8 << 8, 0, 0, 4 | 12 << 8 | 12 << 16 | 17 << 24, 4 | 8 << 8, 0, 4 | 9 << 8, 0, 0, 4 | 8 << 8, 0, 4 | 7 << 8 | 7 << 16 | 12 << 24, 4 | 8 << 8, 4 | 12 << 8, 4 | 8 << 8, 4 | 8 << 8, 0, 4 | 6 << 8, 0, 4 | 8 << 8 | 8 << 16 | 12 << 24, 4 | 7 << 8, 4 | 8 << 8, 4 | 9 << 8, 0, 4 | 8 << 8 | 8 << 16 | 14 << 24, 4 | 6 << 8, 4 | 8 << 8, 4 | 8 << 8, 4 | 7 << 8 | 7 << 16 | 11 << 24, 4 | 8 << 8, 4 | 11 << 8, 4 | 7 << 8 | 7 << 16 | 11 << 24, 4 | 8 << 8 | 8 << 16 | 14 << 24, 4 | 7 << 8 | 7 << 16 | 10 << 24, 4 | 8 << 8 | 8 << 16 | 11 << 24, 0, 4 | 8 << 8, 5 | 10 << 8 | 10 << 16 | 15 << 24, 4 | 8 << 8, 4 | 8 << 8, 4 | 7 << 8, 4 | 8 << 8, 4 | 8 << 8, 4 | 9 << 8, 4 | 9 << 8, 4 | 7 << 8, 4 | 8 << 8, 4 | 9 << 8 | 9 << 16 | 14 << 24, 4 | 6 << 8, 4 | 7 << 8, 0, 4 | 7 << 8, 0, 4 | 9 << 8 | 9 << 16 | 14 << 24, 4 | 8 << 8 | 8 << 16 | 13 << 24, 4 | 10 << 8 | 10 << 16 | 12 << 24, 0, 4 | 8 << 8, 4 | 8 << 8, 4 | 8 << 8, 4 | 12 << 8, 4 | 8 << 8, 4 | 8 << 8, 4 | 8 << 8, 4 | 8 << 8, 4 | 7 << 8, 4 | 6 << 8, 4 | 12 << 8, 4 | 7 << 8, 4 | 9 << 8, 4 | 8 << 8, 5 | 10 << 8 | 10 << 16 | 15 << 24, 0, 4 | 8 << 8 | 8 << 16 | 12 << 24, 4 | 8 << 8, 4 | 7 << 8, 4 | 6 << 8 | 6 << 16 | 9 << 24, 4 | 9 << 8, 4 | 10 << 8, 4 | 8 << 8, 4 | 6 << 8 | 6 << 16 | 8 << 24 };
}