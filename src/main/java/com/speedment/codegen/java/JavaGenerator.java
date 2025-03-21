package com.speedment.codegen.java;
import com.speedment.codegen.base.DefaultDependencyManager;
import com.speedment.codegen.base.Installer;
import com.speedment.codegen.base.MultiGenerator;

/**
 *
 * @author Emil Forslund
 */
public class JavaGenerator extends MultiGenerator {
  private final static String[] types = new String[] { "void", "byte", "short", "char", "int", "long", "float", "double", "boolean" };

  public JavaGenerator() {
    this(new JavaInstaller());
  }

  public JavaGenerator(Installer... installers) {
    super(new DefaultDependencyManager("java.lang", types), installers);
  }
}