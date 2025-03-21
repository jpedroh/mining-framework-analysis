package mikera.matrixx.decompose.impl.lu;
import mikera.matrixx.AMatrix;
import static org.junit.Assert.*;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.decompose.ILUPResult;
import org.junit.Test;

public class TestAltLU {
  @Test public void testDecompose() {
    Matrix A = Matrix.create(new double[][] { { 5, 2, 3 }, { 1.5, -2, 8 }, { -3, 4.7, -0.5 } });

<<<<<<< /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/test/java/mikera/matrixx/decompose/impl/lu/TestAltLU.java/left.java
    AltLU
=======
    ILUPResult
>>>>>>> /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/test/java/mikera/matrixx/decompose/impl/lu/TestAltLU.java/right.java
     alg = new AltLU(A);
    LUPResult ans = alg.decompose(A);
    AMatrix L = ans.getL();
    AMatrix U = ans.getU();
    AMatrix P = alg.getP();
    Matrix expectL = Matrix.create(new double[][] { { 1, 0, 0 }, { -0.6, 1, 0 }, { 0.3, -0.44068, 1 } });
    Matrix expectU = Matrix.create(new double[][] { { 5, 2, 3 }, { 0, 5.9, 1.3 }, { 0, 0, 7.67288 } });
    assertTrue(P.isOrthogonal());
    assertArrayEquals(L.getElements(), expectL.data, 1e-5);
    assertArrayEquals(U.getElements(), expectU.data, 1e-5);
    assertFalse((alg).isSingular());
  }

  @Test public void testRandomDecompose() {
    AMatrix a = Matrixx.createRandomMatrix(4, 4);
    ILUPResult r = SimpleLUP.decompose(a);
    AMatrix lu = r.getL().innerProduct(r.getU());
    AMatrix pa = r.getP().innerProduct(a);
    if (!lu.epsilonEquals(pa)) {
      fail("L=" + r.getL() + "\n" + "U=" + r.getU() + "\n" + "LU=" + lu + "\n" + "PA=" + pa + "\n");
    }
  }
}