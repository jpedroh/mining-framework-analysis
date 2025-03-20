package org.codehaus.mojo.jspc.compiler;
import java.io.File;

/**
 * Interface to provide plugable JSP compilation.
 *
 * @version $Id$
 */
public interface JspCompiler {
  void setWebappDirectory(String webappDir);

  void setOutputDirectory(File outputDirectory);

  void setEncoding(String encoding);

  void setShowSuccess(boolean showSuccesses);

  void setListErrors(boolean listErrors);

  void setWebFragmentFile(File webFragmentFile);

  void setPackageName(String packageName);

  void setClasspath(Iterable<String> classpathElements);

  void setSmapDumped(boolean setSmapDumped);

  void setSmapSuppressed(boolean setSmapSuppressed);

  void setCompile(boolean setCompile);

  void setValidateXml(boolean validateXml);

  void setTrimSpaces(boolean trimSpaces);

  void setErrorOnUseBeanInvalidClassAttribute(boolean error);

  void setVerbose(int verbose);

  void setCompilerSourceVM(String source);

  void setCompilerTargetVM(String target);

  void setCompileThreads(int threads);

  void setCaching(boolean caching);

  void setCompileTimeout(long timeout);

  void setGenStringAsCharArray(boolean genStringAsCharArray);

  void setPoolingEnabled(boolean poolingEnabled);

  void setClassDebugInfo(boolean classDebugInfo);

  void compile(Iterable<File> jspFiles) throws Exception;
}