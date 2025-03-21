package com.farpost.ldt;
import org.testng.annotations.Test;
import static com.farpost.ldt.formatter.AbstractPlainResultFormatter.formatTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlainResultFormatterTest {
  @Test public void formatterCanFormatSeconds() throws InterruptedException {
    assertThat(formatTime(0), equalTo("<1mcs"));
    assertThat(formatTime(23), equalTo("~23mcs"));
    assertThat(formatTime(15000), equalTo("15ms"));
    assertThat(formatTime(1000000), equalTo(
<<<<<<< /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/left.java
    "1.00s"
=======
    String.format("%.2fs", 1.0)
>>>>>>> /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/right.java
    ));
    assertThat(formatTime(1120000), equalTo(
<<<<<<< /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/left.java
    "1.12s"
=======
    String.format("%.2fs", 1.12)
>>>>>>> /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/right.java
    ));
    assertThat(formatTime(1020000), equalTo(
<<<<<<< /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/left.java
    "1.02s"
=======
    String.format("%.2fs", 1.02)
>>>>>>> /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/right.java
    ));
    assertThat(formatTime(1235000), equalTo(
<<<<<<< /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/left.java
    "1.24s"
=======
    String.format("%.2fs", 1.24)
>>>>>>> /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/right.java
    ));
    assertThat(formatTime(65235000l), equalTo("1m 5s"));
  }
}