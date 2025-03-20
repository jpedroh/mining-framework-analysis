package org.assertj.core.test;

/**
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author Nicolas François
 */
public final class ErrorMessages {
  public static String arrayIsNull() {
    return "The given array should not be null";
  }

  public static String arrayIsEmpty() {
    return "The given array should not be empty";
  }

  public static String iterableIsNull() {
    return "The given iterable should not be null";
  }

  public static String iterableIsEmpty() {
    return "The given iterable should not be empty";
  }

  public static String descriptionIsNull() {
    return "The description to set should not be null";
  }

  public static String keysToLookForIsEmpty() {
    return "The array of keys to look for should not be empty";
  }

  public static String keysToLookForIsNull() {
    return "The array of keys to look for should not be null";
  }

  public static String entriesToLookForIsEmpty() {
    return "The array of entries to look for should not be empty";
  }

  public static String entriesToLookForIsNull() {
    return "The array of entries to look for should not be null";
  }

  public static String entryToLookForIsNull() {
    return "Entries to look for should not be null";
  }

  public static String isNotArray(Object o) {
    return String.format("The object <%s> should be an array", o);
  }

  public static String iterableToLookForIsNull() {
    return "The iterable to look for should not be null";
  }

  public static String offsetIsNull() {
    return "The given offset should not be null";
  }

  public static String offsetValueIsNotPositive() {
    return "The value of the offset should be greater than zero";
  }

  public static String regexPatternIsNull() {
    return "The regular expression pattern to match should not be null";
  }

  public static String charSequenceToLookForIsNull() {
    return "The char sequence to look for should not be null";
  }

  public static String valuesToLookForIsEmpty() {
    return "The array of values to look for should not be empty";
  }

  public static String valuesToLookForIsNull() {
    return "The array of values to look for should not be null";
  }

  public static String iterableValuesToLookForIsEmpty() {
    return "The iterable of values to look for should not be empty";
  }

  public static String iterableValuesToLookForIsNull() {
    return "The iterable of values to look for should not be null";
  }

  public static String dateToCompareActualWithIsNull() {
    return "The date to compare actual with should not be null";
  }

  public static String startDateToCompareActualWithIsNull() {
    return "The start date of period to compare actual with should not be null";
  }

  public static String endDateToCompareActualWithIsNull() {
    return "The end date of period to compare actual with should not be null";
  }

  public static String arrayOfValuesToLookForIsNull() {
    return "The array of values to look for should not be null";
  }

  public static String arrayOfValuesToLookForIsEmpty() {
    return "The array of values to look for should not be empty";
  }

  public static String predicateIsNull() {
    return "The predicate must not be null";
  }

  private ErrorMessages() {
  }
}