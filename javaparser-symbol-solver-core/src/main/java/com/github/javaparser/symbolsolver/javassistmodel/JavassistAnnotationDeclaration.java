package com.github.javaparser.symbolsolver.javassistmodel;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.logic.AbstractTypeDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import javassist.CtClass;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavassistAnnotationDeclaration extends AbstractTypeDeclaration implements ResolvedAnnotationDeclaration {
  private CtClass ctClass;

  private TypeSolver typeSolver;

  private JavassistTypeDeclarationAdapter javassistTypeDeclarationAdapter;

  @Override public String toString() {
    return getClass().getSimpleName() + "{" + "ctClass=" + ctClass.getName() + ", typeSolver=" + typeSolver + '}';
  }

  public JavassistAnnotationDeclaration(CtClass ctClass, TypeSolver typeSolver) {
    if (!ctClass.isAnnotation()) {
      throw new IllegalArgumentException("Not an annotation: " + ctClass.getName());
    }
    this.ctClass = ctClass;
    this.typeSolver = typeSolver;
    this.javassistTypeDeclarationAdapter = new JavassistTypeDeclarationAdapter(ctClass, typeSolver, this);
  }

  @Override public String getPackageName() {
    return ctClass.getPackageName();
  }

  @Override public String getClassName() {
    String qualifiedName = getQualifiedName();
    if (qualifiedName.contains(".")) {
      return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1, qualifiedName.length());
    } else {
      return qualifiedName;
    }
  }

  @Override public String getQualifiedName() {
    return ctClass.getName().replace('$', '.');
  }

  @Override public boolean isAssignableBy(ResolvedType type) {
    throw new UnsupportedOperationException();
  }

  @Override public List<ResolvedFieldDeclaration> getAllFields() {
    return javassistTypeDeclarationAdapter.getDeclaredFields();
  }

  @Override public boolean isAssignableBy(ResolvedReferenceTypeDeclaration other) {
    throw new UnsupportedOperationException();
  }

  @Override public List<ResolvedReferenceType> getAncestors(boolean acceptIncompleteList) {
    return javassistTypeDeclarationAdapter.getAncestors(this, acceptIncompleteList);
  }

  @Override public Set<ResolvedMethodDeclaration> getDeclaredMethods() {
    throw new UnsupportedOperationException();
  }

  @Override public boolean hasDirectlyAnnotation(String canonicalName) {
    return ctClass.hasAnnotation(canonicalName);
  }

  @Override public String getName() {
    return getClassName();
  }

  @Override public List<ResolvedTypeParameterDeclaration> getTypeParameters() {
    return Collections.emptyList();
  }

  @Override public Optional<ResolvedReferenceTypeDeclaration> containerType() {
    throw new UnsupportedOperationException("containerType() is not supported for " + this.getClass().getCanonicalName());
  }

  @Override public List<ResolvedConstructorDeclaration> getConstructors() {
    return Collections.emptyList();
  }

  @Override public List<ResolvedAnnotationMemberDeclaration> getAnnotationMembers() {
    return Stream.of(ctClass.getDeclaredMethods()).map((m) -> new JavassistAnnotationMemberDeclaration(m, typeSolver)).collect(Collectors.toList());
  }

  @Override public Optional<AnnotationDeclaration> toAst() {
    return Optional.empty();
  }
}