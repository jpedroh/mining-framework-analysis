package org.nlpcn.commons.lang.util;
import org.junit.Test;

public class StringUtilTest {
  @Test public void test() {
    System.out.println(StringUtil.isBlank(" \t"));
    System.out.println(StringUtil.rmHtmlTag(
<<<<<<< /usr/src/app/output/nlpchina/nlp-lang/cec0dd677bf90156ba3adaea1ffd6c69f64df9a1/src/test/java/org/nlpcn/commons/lang/util/StringUtilTest.java/left.java
    "<a>hello ansj</a>my name is "
=======
    "hello ansj hello kk "
>>>>>>> /usr/src/app/output/nlpchina/nlp-lang/cec0dd677bf90156ba3adaea1ffd6c69f64df9a1/src/test/java/org/nlpcn/commons/lang/util/StringUtilTest.java/right.java
    ));
    System.out.println(StringUtil.makeSqlInString("ansj,2134,123,123,123"));
  }
}