package mikera.matrixx.decompose.impl.lu;
import java.util.Arrays;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.vectorz.util.IntArrays;

/**
 * <p>
 * An LU decomposition algorithm that originally came from Jama. In general this
 * is faster than what is in NR since it creates a cache of a column, which
 * makes a big difference in larger matrices.
 * </p>
 * 
 * @author Peter Abeles
 */
public class AltLU {
  protected int maxWidth = -1;

  protected int m, n;

  protected double dataLU[];

  protected double vv[];

  protected int pivot[];

  protected double pivsign;

  public final static double EPS = Math.pow(2, -52);

  protected Matrix LU;

  protected Matrix L;

  protected Matrix U;

  protected PermutationMatrix P;

  protected boolean singular;

  public AltLU(AMatrix a) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    decompose(a.toMatrix());
>>>>>>> /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/main/java/mikera/matrixx/decompose/impl/lu/AltLU.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    L = computeL();
>>>>>>> /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/main/java/mikera/matrixx/decompose/impl/lu/AltLU.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    U = computeU();
>>>>>>> /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/main/java/mikera/matrixx/decompose/impl/lu/AltLU.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    P = computeP();
>>>>>>> /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/main/java/mikera/matrixx/decompose/impl/lu/AltLU.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    singular = computeSingular();
>>>>>>> /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/main/java/mikera/matrixx/decompose/impl/lu/AltLU.java/right.java
  }

  public Matrix getLU() {
    return LU;
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public PermutationMatrix getP() {
    return P;
  }
>>>>>>> /usr/src/app/output/mikera/vectorz/889e8999395fb5f60266d32bec790fc2ad3daeb8/src/main/java/mikera/matrixx/decompose/impl/lu/AltLU.java/right.java


  /**
	 * Writes the lower triangular matrix into the specified matrix.
	 */
  public Matrix computeL() {
    int numRows = LU.rowCount();
    int numCols = Math.min(LU.rowCount(), LU.columnCount());
    Matrix lower = Matrix.create(numRows, numCols);
    for (int i = 0; i < numCols; i++) {
      lower.set(i, i, 1.0);
      for (int j = 0; j < i; j++) {
        lower.set(i, j, LU.get(i, j));
      }
    }
    if (numRows > numCols) {
      for (int i = numCols; i < numRows; i++) {
        for (int j = 0; j < numCols; j++) {
          lower.set(i, j, LU.get(i, j));
        }
      }
    }
    return lower;
  }

  /**
	 * Writes the upper triangular matrix into the specified matrix.
	 */
  public Matrix computeU() {
    int numRows = Math.min(LU.rowCount(), LU.columnCount());
    int numCols = LU.columnCount();
    Matrix upper = Matrix.create(numRows, numCols);
    for (int i = 0; i < numRows; i++) {
      for (int j = i; j < numCols; j++) {
        upper.set(i, j, LU.get(i, j));
      }
    }
    return upper;
  }

  public PermutationMatrix getPivotMatrix() {
    int numPivots = LU.rowCount();
    return PermutationMatrix.create(Index.wrap(Arrays.copyOf(pivot, numPivots)));
  }

  protected void decomposeCommonInit(Matrix A) {
    m = A.rowCount();
    n = A.columnCount();
    LU = A;
    dataLU = LU.data;
    maxWidth = Math.max(m, n);
    vv = new double[maxWidth];
    pivot = new int[maxWidth];
    for (int i = 0; i < m; i++) {
      pivot[i] = i;
    }
    pivsign = 1;
  }

  /**
	 * Determines if the decomposed matrix is singular. This function can return
	 * false and the matrix be almost singular, which is still bad.
	 * 
	 * @return true if singular false otherwise.
	 */
  public boolean isSingular() {
    if (m != n) {
      throw new IllegalArgumentException("Must be a square matrix.");
    }
    return computeSingular();
  }

  protected boolean computeSingular() {
    for (int i = 0; i < m; i++) {
      if (Math.abs(dataLU[i * n + i]) < EPS) {
        return true;
      }
    }
    return false;
  }

  /**
	 * Computes the determinant from the LU decomposition.
	 * 
	 * @return The matrix's determinant.
	 */
  public double computeDeterminant() {
    if (m != n) {
      throw new IllegalArgumentException("Must be a square matrix.");
    }
    double ret = pivsign;
    int total = m * n;
    for (int i = 0; i < total; i += n + 1) {
      ret *= dataLU[i];
    }
    return ret;
  }

  /**
	 * This is a modified version of what was found in the JAMA package. The
	 * order that it performs its permutations in is the primary difference from
	 * NR
	 * 
	 * @param A The matrix that is to be decomposed. Not modified.
	 * @return An LUPResult object that contains L, U and P matrices
	 */
  public LUPResult decompose(AMatrix _A) {
    Matrix A = _A.toMatrix();
    decomposeCommonInit(A);
    double LUcolj[] = vv;
    for (int j = 0; j < n; j++) {
      for (int i = 0; i < m; i++) {
        LUcolj[i] = dataLU[i * n + j];
      }
      for (int i = 0; i < m; i++) {
        int rowIndex = i * n;
        int kmax = i < j ? i : j;
        double s = 0.0;
        for (int k = 0; k < kmax; k++) {
          s += dataLU[rowIndex + k] * LUcolj[k];
        }
        dataLU[rowIndex + j] = LUcolj[i] -= s;
      }
      int p = j;
      double max = Math.abs(LUcolj[p]);
      for (int i = j + 1; i < m; i++) {
        double v = Math.abs(LUcolj[i]);
        if (v > max) {
          p = i;
          max = v;
        }
      }
      if (p != j) {
        int rowP = p * n;
        int rowJ = j * n;
        int endP = rowP + n;
        for ( ; rowP < endP; rowP++, rowJ++) {
          double t = dataLU[rowP];
          dataLU[rowP] = dataLU[rowJ];
          dataLU[rowJ] = t;
        }
        IntArrays.swap(pivot, p, j);
        pivsign = -pivsign;
      }
      if (j < m) {
        double lujj = dataLU[j * n + j];
        if (lujj != 0) {
          for (int i = j + 1; i < m; i++) {
            dataLU[i * n + j] /= lujj;
          }
        }
      }
    }
    L = computeL();
    U = computeU();
    return new LUPResult(L, U, getPivotMatrix());
  }

  private PermutationMatrix computeP() {
    return PermutationMatrix.wrap(Index.wrap(pivot).invert());
  }
}