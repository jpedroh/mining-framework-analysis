package com.farpost.ldt;

import org.testng.annotations.Test;

import static com.farpost.ldt.formatter.AbstractPlainResultFormatter.formatTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlainResultFormatterTest {

	@Test
	public void formatterCanFormatSeconds() throws InterruptedException {
		assertThat(formatTime(0), equalTo("<1mcs"));
		assertThat(formatTime(23), equalTo("~23mcs"));
		assertThat(formatTime(15000), equalTo("15ms"));
<<<<<<< /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/left.java
		assertThat(formatTime(1000000), equalTo("1.00s"));
		assertThat(formatTime(1120000), equalTo("1.12s"));
		assertThat(formatTime(1020000), equalTo("1.02s"));
		assertThat(formatTime(1235000), equalTo("1.24s"));
||||||| /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/base.java
		assertThat(formatTime(1000000), equalTo("1.0s"));
		assertThat(formatTime(1235000), equalTo("1.235s"));
=======
		assertThat(formatTime(1000000), equalTo(String.format("%.2fs", 1.0)));
		assertThat(formatTime(1120000), equalTo(String.format("%.2fs", 1.12)));
		assertThat(formatTime(1020000), equalTo(String.format("%.2fs", 1.02)));
		assertThat(formatTime(1235000), equalTo(String.format("%.2fs", 1.24)));
>>>>>>> /usr/src/app/output/bazhenov/load-test-tool/6593812db93b89deb415cd4e2728baf690b38487/ldt-core/src/test/java/com/farpost/ldt/PlainResultFormatterTest.java/right.java
		assertThat(formatTime(65235000l), equalTo("1m 5s"));
	}
}
