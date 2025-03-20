package com.github.javaparser.printer.lexicalpreservation.transformations.ast.body;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.printer.lexicalpreservation.AbstractLexicalPreservingTest;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import org.junit.Ignore;
import org.junit.Test;
import static com.github.javaparser.ast.Modifier.Keyword.PROTECTED;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.createModifierList;
import static com.github.javaparser.utils.TestUtils.assertEqualsNoEol;
import static com.github.javaparser.utils.Utils.EOL;

/**
 * Transforming MethodDeclaration and verifying the LexicalPreservation works as expected.
 */
public class MethodDeclarationTransformationsTest extends AbstractLexicalPreservingTest {
  protected MethodDeclaration consider(String code) {
    considerCode("class A { " + code + " }");
    return cu.getType(0).getMembers().get(0).asMethodDeclaration();
  }

  @Test public void settingName() {
    MethodDeclaration it = consider("void A(){}");
    it.setName("B");
    assertTransformedToString("void B(){}", it);
  }

  @Ignore(value = "Indentation not correct yet") @Test public void removingDuplicateJavaDocComment() {
    considerCode("public class MyClass {" + EOL + EOL + "  /**" + EOL + "   * Comment A" + EOL + "   */" + EOL + "  public void oneMethod() {" + EOL + "  }" + EOL + EOL + "  /**" + EOL + "   * Comment A" + EOL + "   */" + EOL + "  public void anotherMethod() {" + EOL + "  }" + EOL + "}" + EOL);
    MethodDeclaration methodDeclaration = cu.findAll(MethodDeclaration.class).get(1);
    methodDeclaration.removeComment();
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("public class MyClass {\n" + "\n" + "  /**\n" + "   * Comment A\n" + "   */\n" + "  public void oneMethod() {\n" + "  }\n" + "\n" + "  public void anotherMethod() {\n" + "  }\n" + "}\n", result);
  }

  @Ignore(value = "Indentation not correct yet") @Test public void replacingDuplicateJavaDocComment() {
    considerCode("public class MyClass {" + EOL + EOL + "  /**" + EOL + "   * Comment A" + EOL + "   */" + EOL + "  public void oneMethod() {" + EOL + "  }" + EOL + EOL + "  /**" + EOL + "   * Comment A" + EOL + "   */" + EOL + "  public void anotherMethod() {" + EOL + "  }" + EOL + "}" + EOL);
    MethodDeclaration methodDeclaration = cu.findAll(MethodDeclaration.class).get(1);
    Javadoc javadoc = new Javadoc(JavadocDescription.parseText("Change Javadoc"));
    methodDeclaration.setJavadocComment("", javadoc);
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("public class MyClass {\n" + "\n" + "  /**\n" + "   * Comment A\n" + "   */\n" + "  public void oneMethod() {\n" + "  }\n" + "\n" + "  /**\n" + "   * Change Javadoc\n" + "   */\n" + "  public void anotherMethod() {\n" + "  }\n" + "}\n", result);
  }

  @Ignore(value = "Comments not supported yet") @Test public void removingDuplicateComment() {
    considerCode("public class MyClass {" + EOL + EOL + "  /*" + EOL + "   * Comment A" + EOL + "   */" + EOL + "  public void oneMethod() {" + EOL + "  }" + EOL + EOL + "  /*" + EOL + "   * Comment A" + EOL + "   */" + EOL + "  public void anotherMethod() {" + EOL + "  }" + EOL + "}" + EOL);
    MethodDeclaration methodDeclaration = cu.findAll(MethodDeclaration.class).get(1);
    methodDeclaration.removeComment();
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("public class MyClass {\n" + "\n" + "  /*\n" + "   * Comment A\n" + "   */\n" + "  public void oneMethod() {\n" + "  }\n" + "\n" + "  public void anotherMethod() {\n" + "  }\n" + "}\n", result);
  }

  @Test public void addingModifiers() {
    MethodDeclaration it = consider("void A(){}");
    it.setModifiers(createModifierList(PUBLIC));
    assertTransformedToString("public void A(){}", it);
  }

  @Test public void removingModifiers() {
    MethodDeclaration it = consider("public void A(){}");
    it.setModifiers(new NodeList<>());
    assertTransformedToString("void A(){}", it);
  }

  @Test public void removingModifiersWithExistingAnnotationsShort() {
    MethodDeclaration it = consider("@Override public void A(){}");
    it.setModifiers(new NodeList<>());
    assertTransformedToString("@Override void A(){}", it);
  }

  @Test public void removingPublicModifierFromPublicStaticMethod() {
    MethodDeclaration it = consider("public static void a(){}");
    it.removeModifier(Modifier.PUBLIC);
    assertTransformedToString("static void a(){}", it);
  }

  @Test public void removingModifiersWithExistingAnnotations() {
    considerCode("class X {" + EOL + "  @Test" + EOL + "  public void testCase() {" + EOL + "  }" + EOL + "}" + EOL);
    cu.getType(0).getMethods().get(0).setModifiers(new NodeList<>());
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("class X {\n" + "  @Test\n" + "  void testCase() {\n" + "  }\n" + "}\n", result);
  }

  @Test public void replacingModifiers() {
    MethodDeclaration it = consider("public void A(){}");
    it.setModifiers(createModifierList(PROTECTED));
    assertTransformedToString("protected void A(){}", it);
  }

  @Test public void replacingModifiersWithExistingAnnotationsShort() {
    MethodDeclaration it = consider("@Override public void A(){}");
    it.setModifiers(createModifierList(PROTECTED));
    assertTransformedToString("@Override protected void A(){}", it);
  }

  @Test public void replacingModifiersWithExistingAnnotations() {
    considerCode("class X {" + EOL + "  @Test" + EOL + "  public void testCase() {" + EOL + "  }" + EOL + "}" + EOL);
    cu.getType(0).getMethods().get(0).setModifiers(createModifierList(PROTECTED));
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("class X {\n" + "  @Test\n" + "  protected void testCase() {\n" + "  }\n" + "}\n", result);
  }

  @Test public void addingParameters() {
    MethodDeclaration it = consider("void foo(){}");
    it.addParameter(PrimitiveType.doubleType(), "d");
    assertTransformedToString("void foo(double d){}", it);
  }

  @Test public void removingOnlyParameter() {
    MethodDeclaration it = consider("public void foo(double d){}");
    it.getParameters().remove(0);
    assertTransformedToString("public void foo(){}", it);
  }

  @Test public void removingFirstParameterOfMany() {
    MethodDeclaration it = consider("public void foo(double d, float f){}");
    it.getParameters().remove(0);
    assertTransformedToString("public void foo(float f){}", it);
  }

  @Test public void removingLastParameterOfMany() {
    MethodDeclaration it = consider("public void foo(double d, float f){}");
    it.getParameters().remove(1);
    assertTransformedToString("public void foo(double d){}", it);
  }

  @Test public void replacingOnlyParameter() {
    MethodDeclaration it = consider("public void foo(float f){}");
    it.getParameters().set(0, new Parameter(new ArrayType(PrimitiveType.intType()), new SimpleName("foo")));
    assertTransformedToString("public void foo(int[] foo){}", it);
  }

  @Test public void addingToExistingAnnotations() {
    considerCode("class X {" + EOL + "  @Test" + EOL + "  public void testCase() {" + EOL + "  }" + EOL + "}" + EOL);
    cu.getType(0).getMethods().get(0).addSingleMemberAnnotation("org.junit.Ignore", new StringLiteralExpr("flaky test"));
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("class X {\n" + "  @Test\n" + "  @org.junit.Ignore(\"flaky test\")\n" + "  public void testCase() {\n" + "  }\n" + "}\n", result);
  }

  @Test public void addingAnnotationsNoModifiers() {
    considerCode("class X {" + EOL + "  void testCase() {" + EOL + "  }" + EOL + "}" + EOL);
    cu.getType(0).getMethods().get(0).addMarkerAnnotation("Test");
    cu.getType(0).getMethods().get(0).addMarkerAnnotation("Override");
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("class X {\n" + "  @Test\n" + "  @Override\n" + "  void testCase() {\n" + "  }\n" + "}\n", result);
  }

  @Test public void replacingAnnotations() {
    considerCode("class X {" + EOL + "  @Override" + EOL + "  public void testCase() {" + EOL + "  }" + EOL + "}" + EOL);
    cu.getType(0).getMethods().get(0).setAnnotations(new NodeList<>(new MarkerAnnotationExpr("Test")));
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("class X {\n" + "  @Test\n" + "  public void testCase() {\n" + "  }\n" + "}\n", result);
  }

  @Test public void addingAnnotationsShort() {
    MethodDeclaration it = consider("void testMethod(){}");
    it.addMarkerAnnotation("Override");
    assertTransformedToString("@Override" + EOL + "void testMethod(){}", it);
  }

  @Test public void removingAnnotations() {
    considerCode("class X {" + EOL + "  @Override" + EOL + "  public void testCase() {" + EOL + "  }" + EOL + "}" + EOL);
    cu.getType(0).getMethods().get(0).getAnnotationByName("Override").get().remove();
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("class X {\n" + "  public void testCase() {\n" + "  }\n" + "}\n", result);
  }

  @Ignore @Test public void removingAnnotationsWithSpaces() {
    considerCode("class X {" + EOL + "  @Override " + EOL + "  public void testCase() {" + EOL + "  }" + EOL + "}" + EOL);
    cu.getType(0).getMethods().get(0).getAnnotationByName("Override").get().remove();
    String result = LexicalPreservingPrinter.print(cu.findCompilationUnit().get());
    assertEqualsNoEol("class X {\n" + "  public void testCase() {\n" + "  }\n" + "}\n", result);
  }
}