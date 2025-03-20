package com.premiumminds.billy.portugal.util;
import com.premiumminds.billy.core.util.FinancialValidator;

public class PTFinancialValidator extends FinancialValidator {
  public static final String PT_COUNTRY_CODE = "PT";

  public PTFinancialValidator(String financialID) {
    super(financialID);
  }

  @Override public boolean isValid() {
    if (this.financialID.length() != 9 || !this.financialID.matches("\\d+")) {
      return false;
    }
    char firstDigit = this.financialID.charAt(0);
    String validChars = "125689";
    if (validChars.indexOf(firstDigit) == -1) {
      return false;
    }
    int checkSum = 0;
    for (int i = 1; i < this.financialID.length(); i++) {
      int digit = Character.getNumericValue(this.financialID.charAt(i - 1));
      checkSum += (10 - i) * digit;
    }
    int lastDigit = Character.getNumericValue(this.financialID.charAt(8));
    int val = (checkSum / 11) * 11;
    checkSum -= val;
    if (checkSum == 0 || checkSum == 1) {
      checkSum = 0;
    } else {
      checkSum = 11 - checkSum;
    }
    if (checkSum == lastDigit) {
      return true;
    } else {
      return false;
    }
  }
}