package org.beastmachine.util;
import static java.lang.Math.max;

public class NumberFormatter {
  private static final int ZEROS_LIMIT = 5;

  public static String format(double d) {
    String naive = String.valueOf(d);
    System.out.println(naive);
    String 
<<<<<<< /usr/src/app/output/beastmachine/ggplot/e2841b05bab86e238732649229f3d0a2834badf7/src/main/java/org/beastmachine/util/NumberFormatter.java/left.java
    postFix = ""
=======
    decimal = naive.indexOf(".")
>>>>>>> /usr/src/app/output/beastmachine/ggplot/e2841b05bab86e238732649229f3d0a2834badf7/src/main/java/org/beastmachine/util/NumberFormatter.java/right.java
    ;

<<<<<<< /usr/src/app/output/beastmachine/ggplot/e2841b05bab86e238732649229f3d0a2834badf7/src/main/java/org/beastmachine/util/NumberFormatter.java/left.java
    if (naive.contains("E")) {
      postFix = naive.subSequence(naive.indexOf("E"), naive.length()).toString();
      naive = naive.subSequence(0, naive.indexOf("E")).toString();
    }
=======
    if (decimal == -1) {
      decimal = naive.length();
    }
>>>>>>> /usr/src/app/output/beastmachine/ggplot/e2841b05bab86e238732649229f3d0a2834badf7/src/main/java/org/beastmachine/util/NumberFormatter.java/right.java

    boolean previousNonzero = false;
    int firstZero = -1;
    int zeroCount = 0;
    for (int ii = 0; ii < naive.length(); ii++) {
      char c = naive.charAt(ii);
      System.out.println("car " + c);
      if (c == '.' || c == '-') {
        continue;
      } else {
        if (c == '0') {
          if (previousNonzero) {
            if (zeroCount == 0) {
              firstZero = ii;
            }
            zeroCount++;
          }
        } else {
          System.out.println(c);
          previousNonzero = true;
          zeroCount = 0;
          firstZero = -1;
        }
      }
      if (zeroCount >= ZEROS_LIMIT) {
        naive = naive.substring(0, max(firstZero, decimal));
        if (naive.endsWith(".")) {
          naive = naive.substring(0, naive.length() - 1);
        }
        return naive + postFix;
      }
    }
    if (zeroCount >= ZEROS_LIMIT) {
      naive = naive.substring(0, max(firstZero, decimal));
      if (naive.endsWith(".")) {
        naive = naive.substring(0, naive.length() - 1);
      }
      return naive;
    }
    return naive + postFix;
  }

  public static void main(String[] args) {
    System.out.println(format(
<<<<<<< /usr/src/app/output/beastmachine/ggplot/e2841b05bab86e238732649229f3d0a2834badf7/src/main/java/org/beastmachine/util/NumberFormatter.java/left.java
    -100.00001
=======
    1
>>>>>>> /usr/src/app/output/beastmachine/ggplot/e2841b05bab86e238732649229f3d0a2834badf7/src/main/java/org/beastmachine/util/NumberFormatter.java/right.java
    ));
  }
}