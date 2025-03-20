package no.uib.cipr.matrix.sparse;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import no.uib.cipr.matrix.Vector;

/**
 * Outputs iteration information to an output stream.
 */
public class OutputIterationReporter implements IterationReporter {
  /**
     * Platform-dependent output
     */
  private PrintWriter out;

  /**
     * Constructor for OutputIterationReporter
     * 
     * @param out
     *            Writes iteration count and current residual here
     */
  public OutputIterationReporter(OutputStream out) {
    this.out = new PrintWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")), true);
  }

  /**
     * Constructor for OutputIterationReporter, using <code>System.err</code>.
     */
  public OutputIterationReporter() {
    this(System.err);
  }

  public void monitor(double r, int i) {
    out.format("%10d % .12e%n", i, r);
    out.flush();
  }

  public void monitor(double r, Vector x, int i) {
    monitor(r, i);
  }
}