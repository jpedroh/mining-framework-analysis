package com.cronutils.model.field.constraint;
import com.cronutils.model.field.value.SpecialChar;
import com.cronutils.utils.Preconditions;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Holds information on valid values for a field and allows to perform mappings and validations. Example of information for valid field
 * values: int range, valid special characters, valid nominal values. Example for mappings: conversions from nominal values to integers and
 * integer-integer mappings if more than one integer represents the same concept.
 */
public class FieldConstraints implements Serializable {
  private final Map<String, Integer> stringMapping;

  private final Map<Integer, Integer> intMapping;

  private final Set<SpecialChar> specialChars;

  private final Integer startRange;

  private final Integer endRange;

  /**
	 * @param specialChars
	 *            - allowed special chars
	 * @param startRange
	 *            - lowest possible value
	 * @param endRange
	 *            - highest possible value
	 */
  public FieldConstraints(Map<String, Integer> stringMapping, Map<Integer, Integer> intMapping, Set<SpecialChar> specialChars, int startRange, int endRange) {
    this.stringMapping = Collections.unmodifiableMap(Preconditions.checkNotNull(stringMapping, "String mapping must not be null"));
    this.intMapping = Collections.unmodifiableMap(Preconditions.checkNotNull(intMapping, "Integer mapping must not be null"));
    this.specialChars = Collections.unmodifiableSet(Preconditions.checkNotNull(specialChars, "Special (non-standard) chars set must not be null"));
    this.startRange = startRange;
    this.endRange = endRange;
  }

  public int getStartRange() {
    return startRange;
  }

  public int getEndRange() {
    return endRange;
  }

  public Set<SpecialChar> getSpecialChars() {
    return specialChars;
  }

  /**
	 * Check if given number is greater or equal to start range and minor or equal to end range
	 * 
	 * @param value
	 *            - to be checked
	 */
  public boolean isInRange(int value) {
    return value >= getStartRange() && value <= getEndRange();
  }

  /**
     * Check if given period is compatible with the given range
     * 
     * @param period - to be checked
     * @return {@code true} if period is compatible, {@code false} otherwise.
     */
  public boolean isPeriodInRange(int period) {
    return period > 0 && period <= getEndRange() - getStartRange();
  }

  public Set<String> getStringMappingKeySet() {
    return stringMapping.keySet();
  }

  public Integer getStringMappingValue(String exp) {
    return stringMapping.get(exp);
  }

  public Integer getIntMappingValue(Integer exp) {
    return intMapping.get(exp);
  }
}