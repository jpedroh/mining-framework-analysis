package com.github.javaparser.symbolsolver.javassistmodel;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.AbstractSymbolResolutionTest;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;

class JavassistInterfaceDeclarationTest extends AbstractSymbolResolutionTest {
  private TypeSolver typeSolver;

  private TypeSolver anotherTypeSolver;

  @BeforeEach void setup() throws IOException {
    Path pathToJar = adaptPath("src/test/resources/javaparser-core-3.0.0-alpha.2.jar");
    typeSolver = new CombinedTypeSolver(new JarTypeSolver(pathToJar), new ReflectionTypeSolver());
    Path anotherPathToJar = adaptPath("src/test/resources/test-artifact-1.0.0.jar");
    anotherTypeSolver = new CombinedTypeSolver(new JarTypeSolver(anotherPathToJar), new ReflectionTypeSolver());
  }

  @Test void testIsClass() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals(false, nodeWithAnnotations.isClass());
  }

  @Test void testIsInterface() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals(true, nodeWithAnnotations.isInterface());
  }

  @Test void testIsEnum() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals(false, nodeWithAnnotations.isEnum());
  }

  @Test void testIsTypeVariable() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals(false, nodeWithAnnotations.isTypeParameter());
  }

  @Test void testIsType() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals(true, nodeWithAnnotations.isType());
  }

  @Test void testAsType() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals(nodeWithAnnotations, nodeWithAnnotations.asType());
  }

  @Test void testAsClass() {
    assertThrows(UnsupportedOperationException.class, () -> {
      JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
      nodeWithAnnotations.asClass();
    });
  }

  @Test void testAsInterface() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals(nodeWithAnnotations, nodeWithAnnotations.asInterface());
  }

  @Test void testAsEnum() {
    assertThrows(UnsupportedOperationException.class, () -> {
      JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
      nodeWithAnnotations.asEnum();
    });
  }

  @Test void testGetPackageName() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals("com.github.javaparser.ast.nodeTypes", nodeWithAnnotations.getPackageName());
  }

  @Test void testGetClassName() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals("NodeWithAnnotations", nodeWithAnnotations.getClassName());
  }

  @Test void testGetQualifiedName() {
    JavassistInterfaceDeclaration nodeWithAnnotations = (JavassistInterfaceDeclaration) typeSolver.solveType("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations");
    assertEquals("com.github.javaparser.ast.nodeTypes.NodeWithAnnotations", nodeWithAnnotations.getQualifiedName());
  }

  @Test void testHasDirectlyAnnotation() {
    JavassistInterfaceDeclaration compilationUnit = (JavassistInterfaceDeclaration) anotherTypeSolver.solveType("com.github.javaparser.test.TestInterface");
    assertTrue(compilationUnit.hasDirectlyAnnotation("com.github.javaparser.test.TestAnnotation"));
  }

  @Test void testHasAnnotation() {
    JavassistInterfaceDeclaration compilationUnit = (JavassistInterfaceDeclaration) anotherTypeSolver.solveType("com.github.javaparser.test.TestChildInterface");
    assertTrue(compilationUnit.hasAnnotation("com.github.javaparser.test.TestAnnotation"));
  }

  @Test void testGetAncestorsWithGenericAncestors() {
    JavassistInterfaceDeclaration compilationUnit = (JavassistInterfaceDeclaration) anotherTypeSolver.solveType("com.github.javaparser.test.GenericChildInterface");
    List<ResolvedReferenceType> ancestors = compilationUnit.getAncestors();
    ancestors.sort(new Comparator<ResolvedReferenceType>() {
      @Override public int compare(ResolvedReferenceType o1, ResolvedReferenceType o2) {
        return o1.describe().compareTo(o2.describe());
      }
    });
    assertEquals(2, ancestors.size());
    assertEquals("java.lang.Object", ancestors.get(0).describe());
    assertEquals("com.github.javaparser.test.GenericInterface<S>", ancestors.get(1).describe());
    List<Pair<ResolvedTypeParameterDeclaration, ResolvedType>> typePamatersMap = ancestors.get(1).getTypeParametersMap();
    assertEquals(1, typePamatersMap.size());
    ResolvedTypeParameterDeclaration genericTypeParameterDeclaration = typePamatersMap.get(0).a;
    assertEquals("com.github.javaparser.test.GenericInterface.T", genericTypeParameterDeclaration.getQualifiedName());
    ResolvedType genericResolvedType = typePamatersMap.get(0).b;
    assertEquals("com.github.javaparser.test.GenericChildInterface.S", genericResolvedType.asTypeParameter().getQualifiedName());
  }
}