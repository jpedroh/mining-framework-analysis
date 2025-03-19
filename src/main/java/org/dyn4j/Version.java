package org.dyn4j;

/**
 * The version of the engine.
 * @author William Bittle
 * @version 4.1.0
 * @since 1.0.0
 */
public final class Version {
  /** The major version number; high impact changes; major API changes, major enhancements, etc. */
  private static final int MAJOR = 4;

  /** The minor version number; medium impact changes; minor API changes, minor enhancements, major bug fixes, etc. */
  private static final int MINOR = 1;

  /** The revision number; low impact changes; deprecating API changes, minor bug fixes, etc. */
  private static final int REVISION = 2;

  /**
	 * Hide the constructor.
	 */
  private Version() {
  }

  /**
	 * Returns the version as a string.
	 * @return String
	 */
  public static final String getVersion() {
    return MAJOR + "." + MINOR + "." + REVISION;
  }

  /**
	 * Returns the version numbers in an array of ints.
	 * <p>
	 * The array is of length 3 and has the major, minor, and
	 * revision numbers in that order.
	 * @return int[] the major, minor, and revision numbers
	 * @since 3.1.0
	 */
  public static final int[] getVersionNumbers() {
    return new int[] { MAJOR, MINOR, REVISION };
  }

  /**
	 * Returns the major version number.
	 * @return int
	 * @since 3.1.0
	 */
  public static final int getMajorNumber() {
    return MAJOR;
  }

  /**
	 * Returns the minor version number.
	 * @return int
	 * @since 3.1.0
	 */
  public static final int getMinorNumber() {
    return MINOR;
  }

  /**
	 * Returns the revision number.
	 * @return int
	 * @since 3.1.0
	 */
  public static final int getRevisionNumber() {
    return REVISION;
  }

  /**
	 * Main class to print the version to the console.
	 * @param args command line arguments (none accepted)
	 */
  public static final void main(String[] args) {
    System.out.println("dyn4j v" + Version.getVersion());
  }
}