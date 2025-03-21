package com.fasterxml.jackson.core;

/**
 * Unit tests for class {@link Version}.
<<<<<<< /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/left.java
 */
||||||| /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/base.java
 *
 * @date 2017-08-01
 * @see Version
 *
 **/
=======
 *
 **/
>>>>>>> /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/right.java
public class VersionTest extends BaseTest
{
  
  public void testCompareToOne() {
      Version version = Version.unknownVersion();
<<<<<<< /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/left.java
      Version versionTwo = new Version(0, -263, -1820, "", "", "");
||||||| /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/base.java
      Version versionTwo = new Version(0, (-263), (-1820), "");
=======
      Version versionTwo = new Version(0, (-263), (-1820), "",
              "", "");
>>>>>>> /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/right.java

      assertEquals(263, version.compareTo(versionTwo));
  }

  public void testCompareToReturningZero() {
      Version version = Version.unknownVersion();
      Version versionTwo = new Version(0, 0, 0, "",
              "", "");

      assertEquals(0, version.compareTo(versionTwo));
  }

  public void testCreatesVersionTaking6ArgumentsAndCallsCompareTo() {
      Version version = new Version(0, 0, 0, null, null, "");
      Version versionTwo = new Version(0, 0, 0, "", "", "//0.0.0");

      assertTrue(version.compareTo(versionTwo) < 0);
  }

  public void testCompareToTwo() {
      Version version = Version.unknownVersion();
<<<<<<< /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/left.java
      Version versionTwo = new Version(-1, 0, 0, "SNAPSHOT", "groupId", "artifactId");
||||||| /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/base.java
      Version versionTwo = new Version(-1, 0, 0, "SNAPSHOT", "groupId");
=======
      Version versionTwo = new Version(-1, 0, 0, "SNAPSHOT", "groupId",
              "", "");
>>>>>>> /usr/src/app/output/fasterxml/jackson-core/d708338d421df111ab3d4a36bb90b14900594ec0/src/test/java/com/fasterxml/jackson/core/VersionTest.java/right.java

      int diff = version.compareTo(versionTwo);
      assertTrue("Diff should be negative, was: "+diff, diff < 0);
  }

  public void testCompareToAndCreatesVersionTaking6ArgumentsAndUnknownVersion() {
      Version version = Version.unknownVersion();
      Version versionTwo = new Version(0, 0, 0, "SNAPSHOT", "groupId", "artifactId");

      assertTrue(version.compareTo(versionTwo) < 0);
  }
}
