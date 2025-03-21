package org.apache.commons.math4.complex;
import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.IntegerSequence;
import org.apache.commons.math4.util.IntegerSequence.Range;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test cases for {@link ComplexUtils}.
 */
public class ComplexUtilsTest {
  private final double inf = Double.POSITIVE_INFINITY;

  private final double negInf = Double.NEGATIVE_INFINITY;

  private final double nan = Double.NaN;

  private final double pi = FastMath.PI;

  private final Complex negInfInf = new Complex(negInf, inf);

  private final Complex infNegInf = new Complex(inf, negInf);

  private final Complex infInf = new Complex(inf, inf);

  private final Complex negInfNegInf = new Complex(negInf, negInf);

  private final Complex infNaN = new Complex(inf, nan);

  private static Complex c[];

  private static Complex cr[];

  private static Complex ci[];

  private static double d[];

  private static double di[];

  private static float f[];

  private static float fi[];

  private static double sr[];

  private static double si[];

  private static float sfr[];

  private static float sfi[];

  static Complex ans1, ans2;

  static Range range1, range2;

  static Complex[] ansArrayc1r, ansArrayc1i, ansArrayc2r, ansArrayc2i, ansArrayc3, ansArrayc4;

  static double[] ansArrayd1r, ansArrayd2r, ansArrayd1i, ansArrayd2i, ansArraydi1, ansArraydi2;

  static float[] ansArrayf1r, ansArrayf2r, ansArrayf1i, ansArrayf2i, ansArrayfi1, ansArrayfi2;

  static String msg;

  static Complex[][] c2d, cr2d, ci2d;

  static Complex[][][] c3d, cr3d, ci3d;

  static double[][] d2d, di2d, sr2d, si2d;

  static double[][][] d3d, di3d, sr3d, si3d;

  static float[][] f2d, fi2d, sfr2d, sfi2d;

  static float[][][] f3d, fi3d, sfr3d, sfi3d;

  private static void setArrays() {
    c = new Complex[10];
    cr = new Complex[10];
    ci = new Complex[10];
    d = new double[10];
    f = new float[10];
    di = new double[20];
    fi = new float[20];
    sr = new double[10];
    si = new double[10];
    sfr = new float[10];
    sfi = new float[10];
    c2d = new Complex[10][10];
    cr2d = new Complex[10][10];
    ci2d = new Complex[10][10];
    c3d = new Complex[10][10][10];
    cr3d = new Complex[10][10][10];
    ci3d = new Complex[10][10][10];
    d2d = new double[10][10];
    d3d = new double[10][10][10];
    f2d = new float[10][10];
    f3d = new float[10][10][10];
    sr2d = new double[10][10];
    sr3d = new double[10][10][10];
    si2d = new double[10][10];
    si3d = new double[10][10][10];
    sfr2d = new float[10][10];
    sfr3d = new float[10][10][10];
    sfi2d = new float[10][10];
    sfi3d = new float[10][10][10];
    di2d = new double[10][20];
    di3d = new double[10][10][20];
    fi2d = new float[10][20];
    fi3d = new float[10][10][20];
    range1 = IntegerSequence.range(3, 7);
    range2 = IntegerSequence.range(3, 7, 2);
    for (int i = 0; i < 20; i += 2) {
      d[i / 2] = i / 2;
      f[i / 2] = i / 2;
      di[i] = i;
      di[i + 1] = i + 1;
      fi[i] = i;
      fi[i + 1] = i + 1;
      c[i / 2] = new Complex(i, i + 1);
      cr[i / 2] = new Complex(i / 2);
      ci[i / 2] = new Complex(0, i / 2);
      sr[i / 2] = i;
      si[i / 2] = i + 1;
      sfr[i / 2] = i;
      sfi[i / 2] = i + 1;
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 20; j += 2) {
        d2d[i][j / 2] = 10 * i + j / 2;
        f2d[i][j / 2] = 10 * i + j / 2;
        sr2d[i][j / 2] = 10 * i + j;
        si2d[i][j / 2] = 10 * i + j + 1;
        sfr2d[i][j / 2] = 10 * i + j;
        sfi2d[i][j / 2] = 10 * i + j + 1;
        di2d[i][j] = 10 * i + j;
        di2d[i][j + 1] = 10 * i + j + 1;
        fi2d[i][j] = 10 * i + j;
        fi2d[i][j + 1] = 10 * i + j + 1;
        c2d[i][j / 2] = new Complex(10 * i + j, 10 * i + j + 1);
        cr2d[i][j / 2] = new Complex(10 * i + j / 2);
        ci2d[i][j / 2] = new Complex(0, 10 * i + j / 2);
      }
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        for (int k = 0; k < 20; k += 2) {
          d3d[i][j][k / 2] = 100 * i + 10 * j + k / 2;
          f3d[i][j][k / 2] = 100 * i + 10 * j + k / 2;
          sr3d[i][j][k / 2] = 100 * i + 10 * j + k;
          si3d[i][j][k / 2] = 100 * i + 10 * j + k + 1;
          sfr3d[i][j][k / 2] = 100 * i + 10 * j + k;
          sfi3d[i][j][k / 2] = 100 * i + 10 * j + k + 1;
          di3d[i][j][k] = 100 * i + 10 * j + k;
          di3d[i][j][k + 1] = 100 * i + 10 * j + k + 1;
          fi3d[i][j][k] = 100 * i + 10 * j + k;
          fi3d[i][j][k + 1] = 100 * i + 10 * j + k + 1;
          c3d[i][j][k / 2] = new Complex(100 * i + 10 * j + k, 100 * i + 10 * j + k + 1);
          cr3d[i][j][k / 2] = new Complex(100 * i + 10 * j + k / 2);
          ci3d[i][j][k / 2] = new Complex(0, 100 * i + 10 * j + k / 2);
        }
      }
    }
    ansArrayc1r = new Complex[] { new Complex(3), new Complex(4), new Complex(5), new Complex(6), new Complex(7) };
    ansArrayc2r = new Complex[] { new Complex(3), new Complex(5), new Complex(7) };
    ansArrayc1i = new Complex[] { new Complex(0, 3), new Complex(0, 4), new Complex(0, 5), new Complex(0, 6), new Complex(0, 7) };
    ansArrayc2i = new Complex[] { new Complex(0, 3), new Complex(0, 5), new Complex(0, 7) };
    ansArrayc3 = new Complex[] { new Complex(6, 7), new Complex(8, 9), new Complex(10, 11), new Complex(12, 13), new Complex(14, 15) };
    ansArrayc4 = new Complex[] { new Complex(6, 7), new Complex(10, 11), new Complex(14, 15) };
    ansArrayd1r = new double[] { 6, 8, 10, 12, 14 };
    ansArrayd1i = new double[] { 7, 9, 11, 13, 15 };
    ansArrayd2r = new double[] { 6, 10, 14 };
    ansArrayd2i = new double[] { 7, 11, 15 };
    ansArrayf1r = new float[] { 6, 8, 10, 12, 14 };
    ansArrayf1i = new float[] { 7, 9, 11, 13, 15 };
    ansArrayf2r = new float[] { 6, 10, 14 };
    ansArrayf2i = new float[] { 7, 11, 15 };
    ansArraydi1 = new double[] { 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    ansArrayfi1 = new float[] { 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    ansArraydi2 = new double[] { 6, 7, 10, 11, 14, 15 };
    ansArrayfi2 = new float[] { 6, 7, 10, 11, 14, 15 };
    msg = "";
  }

  @Test public void testPolar2Complex() {
    TestUtils.assertEquals(Complex.ONE, ComplexUtils.polar2Complex(1, 0), 10e-12);
    TestUtils.assertEquals(Complex.ZERO, ComplexUtils.polar2Complex(0, 1), 10e-12);
    TestUtils.assertEquals(Complex.ZERO, ComplexUtils.polar2Complex(0, -1), 10e-12);
    TestUtils.assertEquals(Complex.I, ComplexUtils.polar2Complex(1, pi / 2), 10e-12);
    TestUtils.assertEquals(Complex.I.negate(), ComplexUtils.polar2Complex(1, -pi / 2), 10e-12);
    double r = 0;
    for (int i = 0; i < 5; i++) {
      r += i;
      double theta = 0;
      for (int j = 0; j < 20; j++) {
        theta += pi / 6;
        TestUtils.assertEquals(altPolar(r, theta), ComplexUtils.polar2Complex(r, theta), 10e-12);
      }
      theta = -2 * pi;
      for (int j = 0; j < 20; j++) {
        theta -= pi / 6;
        TestUtils.assertEquals(altPolar(r, theta), ComplexUtils.polar2Complex(r, theta), 10e-12);
      }
    }
  }

  protected Complex altPolar(double r, double theta) {
    return Complex.I.multiply(new Complex(theta, 0)).exp().multiply(new Complex(r, 0));
  }

  @Test(expected = MathIllegalArgumentException.class) public void testPolar2ComplexIllegalModulus() {
    ComplexUtils.polar2Complex(-1, 0);
  }

  @Test public void testPolar2ComplexNaN() {
    TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(nan, 1));
    TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(1, nan));
    TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(nan, nan));
  }

  @Test public void testPolar2ComplexInf() {
    TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(1, inf));
    TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(1, negInf));
    TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(inf, inf));
    TestUtils.assertSame(Complex.NaN, ComplexUtils.polar2Complex(inf, negInf));
    TestUtils.assertSame(infInf, ComplexUtils.polar2Complex(inf, pi / 4));
    TestUtils.assertSame(infNaN, ComplexUtils.polar2Complex(inf, 0));
    TestUtils.assertSame(infNegInf, ComplexUtils.polar2Complex(inf, -pi / 4));
    TestUtils.assertSame(negInfInf, ComplexUtils.polar2Complex(inf, 3 * pi / 4));
    TestUtils.assertSame(negInfNegInf, ComplexUtils.polar2Complex(inf, 5 * pi / 4));
  }

  @Test public void testCExtract() {
    final double[] real = new double[] { negInf, -123.45, 0, 1, 234.56, pi, inf };
    final Complex[] complex = ComplexUtils.real2Complex(real);
    for (int i = 0; i < real.length; i++) {
      Assert.assertEquals(real[i], complex[i].getReal(), 0d);
    }
  }

  @Test public void testExtractionMethods() {
    setArrays();
    TestUtils.assertSame(new Complex(3), ComplexUtils.extractComplexFromRealArray(d, 3));
    TestUtils.assertSame(new Complex(3), ComplexUtils.extractComplexFromRealArray(f, 3));
    TestUtils.assertSame(6, ComplexUtils.extractRealFromComplexArray(c, 3));
    TestUtils.assertSame(6, ComplexUtils.extractRealFloatFromComplexArray(c, 3));
    TestUtils.assertSame(new Complex(6, 7), ComplexUtils.extractComplexFromInterleavedArray(d, 3));
    TestUtils.assertSame(new Complex(6, 7), ComplexUtils.extractComplexFromInterleavedArray(f, 3));
    TestUtils.assertEquals(msg, new double[] { 6, 7 }, ComplexUtils.extractInterleavedFromComplexArray(c, 3), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, new double[] { 6, 7 }, ComplexUtils.extractInterleavedFromComplexArray(c, 3), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testRealToComplex() {
    setArrays();
    TestUtils.assertEquals(msg, ansArrayc1r, ComplexUtils.real2Complex(d, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc1r, ComplexUtils.real2Complex(f, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2r, ComplexUtils.real2Complex(d, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2r, ComplexUtils.real2Complex(f, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc1r, ComplexUtils.real2Complex(d, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc1r, ComplexUtils.real2Complex(f, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2r, ComplexUtils.real2Complex(d, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2r, ComplexUtils.real2Complex(f, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, cr, ComplexUtils.real2Complex(d), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, cr, ComplexUtils.real2Complex(f), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    for (int i = 0; i < 10; i++) {
      TestUtils.assertEquals(msg, cr2d[i], ComplexUtils.real2Complex(d2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
      TestUtils.assertEquals(msg, cr2d[i], ComplexUtils.real2Complex(f2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        TestUtils.assertEquals(msg, cr3d[i][j], ComplexUtils.real2Complex(d3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
        TestUtils.assertEquals(msg, cr3d[i][j], ComplexUtils.real2Complex(f3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
      }
    }
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testComplexToReal() {
    setArrays();
    TestUtils.assertEquals(msg, ansArrayd1r, ComplexUtils.complex2Real(c, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf1r, ComplexUtils.complex2RealFloat(c, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayd2r, ComplexUtils.complex2Real(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf2r, ComplexUtils.complex2RealFloat(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayd1r, ComplexUtils.complex2Real(c, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf1r, ComplexUtils.complex2RealFloat(c, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayd2r, ComplexUtils.complex2Real(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf2r, ComplexUtils.complex2RealFloat(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, sr, ComplexUtils.complex2Real(c), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, sfr, ComplexUtils.complex2RealFloat(c), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    for (int i = 0; i < 10; i++) {
      TestUtils.assertEquals(msg, sr2d[i], ComplexUtils.complex2Real(c2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
      TestUtils.assertEquals(msg, sfr2d[i], ComplexUtils.complex2RealFloat(c2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        TestUtils.assertEquals(msg, sr3d[i][j], ComplexUtils.complex2Real(c3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
        TestUtils.assertEquals(msg, sfr3d[i][j], ComplexUtils.complex2RealFloat(c3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
      }
    }
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testImaginaryToComplex() {
    setArrays();
    TestUtils.assertEquals(msg, ansArrayc1i, ComplexUtils.imaginary2Complex(d, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc1i, ComplexUtils.imaginary2Complex(f, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2i, ComplexUtils.imaginary2Complex(d, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2i, ComplexUtils.imaginary2Complex(f, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc1i, ComplexUtils.imaginary2Complex(d, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc1i, ComplexUtils.imaginary2Complex(f, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2i, ComplexUtils.imaginary2Complex(d, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc2i, ComplexUtils.imaginary2Complex(f, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ci, ComplexUtils.imaginary2Complex(d), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ci, ComplexUtils.imaginary2Complex(f), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    for (int i = 0; i < 10; i++) {
      TestUtils.assertEquals(msg, ci2d[i], ComplexUtils.imaginary2Complex(d2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
      TestUtils.assertEquals(msg, ci2d[i], ComplexUtils.imaginary2Complex(f2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        TestUtils.assertEquals(msg, ci3d[i][j], ComplexUtils.imaginary2Complex(d3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
        TestUtils.assertEquals(msg, ci3d[i][j], ComplexUtils.imaginary2Complex(f3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
      }
    }
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testComplexToImaginary() {
    setArrays();
    TestUtils.assertEquals(msg, ansArrayd1i, ComplexUtils.complex2Imaginary(c, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf1i, ComplexUtils.complex2ImaginaryFloat(c, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayd2i, ComplexUtils.complex2Imaginary(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf2i, ComplexUtils.complex2ImaginaryFloat(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayd1i, ComplexUtils.complex2Imaginary(c, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf1i, ComplexUtils.complex2ImaginaryFloat(c, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayd2i, ComplexUtils.complex2Imaginary(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayf2i, ComplexUtils.complex2ImaginaryFloat(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, si, ComplexUtils.complex2Imaginary(c), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, sfi, ComplexUtils.complex2ImaginaryFloat(c), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    for (int i = 0; i < 10; i++) {
      TestUtils.assertEquals(msg, si2d[i], ComplexUtils.complex2Imaginary(c2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
      TestUtils.assertEquals(msg, sfi2d[i], ComplexUtils.complex2ImaginaryFloat(c2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        TestUtils.assertEquals(msg, si3d[i][j], ComplexUtils.complex2Imaginary(c3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
        TestUtils.assertEquals(msg, sfi3d[i][j], ComplexUtils.complex2ImaginaryFloat(c3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
      }
    }
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testInterleavedToComplex() {
    setArrays();
    TestUtils.assertEquals(msg, ansArrayc3, ComplexUtils.interleaved2Complex(di, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc3, ComplexUtils.interleaved2Complex(fi, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc4, ComplexUtils.interleaved2Complex(di, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc4, ComplexUtils.interleaved2Complex(fi, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc3, ComplexUtils.interleaved2Complex(di, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc3, ComplexUtils.interleaved2Complex(fi, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc4, ComplexUtils.interleaved2Complex(di, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayc4, ComplexUtils.interleaved2Complex(fi, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, c, ComplexUtils.interleaved2Complex(di), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, c, ComplexUtils.interleaved2Complex(fi), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    for (int i = 0; i < 10; i++) {
      TestUtils.assertEquals(msg, c2d[i], ComplexUtils.interleaved2Complex(di2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
      TestUtils.assertEquals(msg, c2d[i], ComplexUtils.interleaved2Complex(fi2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        TestUtils.assertEquals(msg, c3d[i][j], ComplexUtils.interleaved2Complex(di3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
        TestUtils.assertEquals(msg, c3d[i][j], ComplexUtils.interleaved2Complex(fi3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
      }
    }
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testComplexToInterleaved() {
    setArrays();
    TestUtils.assertEquals(msg, ansArraydi1, ComplexUtils.complex2Interleaved(c, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayfi1, ComplexUtils.complex2InterleavedFloat(c, 3, 7), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArraydi2, ComplexUtils.complex2Interleaved(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayfi2, ComplexUtils.complex2InterleavedFloat(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArraydi1, ComplexUtils.complex2Interleaved(c, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayfi1, ComplexUtils.complex2InterleavedFloat(c, range1), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArraydi2, ComplexUtils.complex2Interleaved(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, ansArrayfi2, ComplexUtils.complex2InterleavedFloat(c, 3, 7, 2), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, di, ComplexUtils.complex2Interleaved(c), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    TestUtils.assertEquals(msg, fi, ComplexUtils.complex2InterleavedFloat(c), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    for (int i = 0; i < 10; i++) {
      TestUtils.assertEquals(msg, di2d[i], ComplexUtils.complex2Interleaved(c2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
      TestUtils.assertEquals(msg, fi2d[i], ComplexUtils.complex2InterleavedFloat(c2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        TestUtils.assertEquals(msg, di3d[i][j], ComplexUtils.complex2Interleaved(c3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
        TestUtils.assertEquals(msg, fi3d[i][j], ComplexUtils.complex2InterleavedFloat(c3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0f
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
      }
    }
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testSplit2Complex() {
    setArrays();
    TestUtils.assertEquals(msg, c, ComplexUtils.split2Complex(sr, si), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
    1
=======
    1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
    ));
    for (int i = 0; i < 10; i++) {
      TestUtils.assertEquals(msg, c2d[i], ComplexUtils.split2Complex(sr2d[i], si2d[i]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
      1
=======
      1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
      ));
    }
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        TestUtils.assertEquals(msg, c3d[i][j], ComplexUtils.split2Complex(sr3d[i][j], si3d[i][j]), Math.ulp(
<<<<<<< /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/left.java
        1
=======
        1.0
>>>>>>> /usr/src/app/output/apache/commons-math/2df243026fcab4010afc10468bb6ab8ce42aa2ad/src/test/java/org/apache/commons/math4/complex/ComplexUtilsTest.java/right.java
        ));
      }
    }
    if (!msg.equals("")) {
      throw new RuntimeException(msg);
    }
  }

  @Test public void testInitialize() {
    Complex[] c = new Complex[10];
    ComplexUtils.initialize(c);
    for (Complex cc : c) {
      TestUtils.assertEquals(new Complex(0, 0), cc, Math.ulp(0));
    }
  }
}