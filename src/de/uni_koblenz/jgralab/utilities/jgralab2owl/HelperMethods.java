package de.uni_koblenz.jgralab.utilities.jgralab2owl;

public final class HelperMethods {
  /**
	 * Changes the first character of {@code string} to lower case and returns
	 * the resulting String.
	 * 
	 * @param string
	 *            String whose first character shall be changed to lower case.
	 * @return The given String with its first character changed to lower case.
	 */
  static String firstToLowerCase(String string) {
    char first = Character.toLowerCase(string.charAt(0));
    return first + string.substring(1);
  }

  /**
	 * Changes the first character of {@code string} to upper case and returns
	 * the resulting String.
	 * 
	 * @param string
	 *            String whose first character shall be changed to upper case.
	 * @return The given String with its first character changed to upper case.
	 */
  static String firstToUpperCase(String string) {
    char first = Character.toUpperCase(string.charAt(0));
    return first + string.substring(1);
  }
}