package mikera.matrixx.algo;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.chol.ICholeskyResult;
import mikera.matrixx.algo.decompose.chol.impl.SimpleCholesky;

/**
 * Class implementing a standard Cholesky decomposition
 * 
 *    A = L.L*
 *    
 * Where: A  is a symmetric (actually Hermitian), positive-definite matrix 
 * and:   L  is a lower triangular matrix
 *        L* is the conjugate transpose of L (which is equal to its transpose, since A is real in Vectorz)
 * 
 * See: http://en.wikipedia.org/wiki/Cholesky_decomposition
 * 
 * @author Mike
 *
 */
public class Cholesky {
  /**
	 * Decompose a matrix according the the Cholesky decomposition A = L.L*
	 * 
	 * @param a Any symmetric, positive definite matrix
	 * @return The decomposition result
	 */
  public static final ICholeskyResult decompose(AMatrix a) {
    return SimpleCholesky.decompose(a.toMatrix());
  }

  public static final ICholeskyResult decompose(Matrix a) {
    return SimpleCholesky.decompose(a);
  }
}