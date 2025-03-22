package de.uni_koblenz.jgralab.impl;
import java.io.PrintStream;

/**
 * This deprecated class prevents breaking old code - you should replace usages
 * by ConsoleProgressFunction.
 * 
 * @author ist@uni-koblenz.de
 */
@Deprecated public final class ProgressFunctionImpl extends ConsoleProgressFunction {
  @Deprecated public ProgressFunctionImpl() {
    super();
  }

  @Deprecated public ProgressFunctionImpl(int length) {
    super(length);
  }

  @Deprecated public ProgressFunctionImpl(PrintStream printStream) {
    super(printStream);
  }

  @Deprecated public ProgressFunctionImpl(PrintStream printStream, int length) {
    super(printStream, length);
  }
}