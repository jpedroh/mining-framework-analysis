package com.github.javaparser.symbolsolver.reflectionmodel;
import com.github.javaparser.ast.Node;
import java.lang.annotation.Inherited;
import com.github.javaparser.resolution.Context;
import java.util.Arrays;
import com.github.javaparser.resolution.MethodUsage;
import java.util.Collections;
import com.github.javaparser.resolution.TypeSolver;
import java.util.LinkedList;
import com.github.javaparser.resolution.declarations.ResolvedAnnotationDeclaration;
import java.util.List;
import com.github.javaparser.resolution.declarations.ResolvedAnnotationMemberDeclaration;
import java.util.Optional;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import java.util.Set;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import java.util.stream.Collectors;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import java.util.stream.Stream;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.logic.ConfilictingGenericTypesException;
import com.github.javaparser.resolution.logic.InferenceContext;
import com.github.javaparser.resolution.logic.MethodResolutionCapability;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.core.resolution.MethodUsageResolutionCapability;
import com.github.javaparser.symbolsolver.logic.AbstractTypeDeclaration;

/**
 * @author Malte Skoruppa
 */
public class ReflectionAnnotationDeclaration extends AbstractTypeDeclaration implements ResolvedAnnotationDeclaration, MethodUsageResolutionCapability, MethodResolutionCapability {
  private Class<?> clazz;

  private TypeSolver typeSolver;

  private ReflectionClassAdapter reflectionClassAdapter;

  public ReflectionAnnotationDeclaration(Class<?> clazz, TypeSolver typeSolver) {
    if (!clazz.isAnnotation()) {
      throw new IllegalArgumentException("The given type is not an annotation.");
    }
    this.clazz = clazz;
    this.typeSolver = typeSolver;
    this.reflectionClassAdapter = new ReflectionClassAdapter(clazz, typeSolver, this);
  }

  @Override public String getPackageName() {
    if (clazz.getPackage() != null) {
      return clazz.getPackage().getName();
    }
    return "";
  }

  @Override public String getClassName() {
    String qualifiedName = getQualifiedName();
    if (qualifiedName.contains(".")) {
      return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
    } else {
      return qualifiedName;
    }
  }

  @Override public String getQualifiedName() {
    return clazz.getCanonicalName();
  }

  @Override public String toString() {
    return getClass().getSimpleName() + "{" + "clazz=" + clazz.getCanonicalName() + '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ReflectionAnnotationDeclaration)) {
      return false;
    }
    ReflectionAnnotationDeclaration that = (ReflectionAnnotationDeclaration) o;
    return clazz.getCanonicalName().equals(that.clazz.getCanonicalName());
  }

  @Override public int hashCode() {
    return clazz.getCanonicalName().hashCode();
  }

  @Override public boolean isAssignableBy(ResolvedType type) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean isAssignableBy(ResolvedReferenceTypeDeclaration other) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean hasDirectlyAnnotation(String canonicalName) {
    return reflectionClassAdapter.hasDirectlyAnnotation(canonicalName);
  }

  @Override public List<ResolvedFieldDeclaration> getAllFields() {
    return reflectionClassAdapter.getAllFields();
  }

  @Override public List<ResolvedReferenceType> getAncestors(boolean acceptIncompleteList) {
    return reflectionClassAdapter.getAncestors();
  }

  @Override public Set<ResolvedMethodDeclaration> getDeclaredMethods() {
    throw new UnsupportedOperationException();
  }

  @Override public String getName() {
    return clazz.getSimpleName();
  }

  @Override public Optional<ResolvedReferenceTypeDeclaration> containerType() {
    throw new UnsupportedOperationException("containerType() is not supported for " + this.getClass().getCanonicalName());
  }

  /**
     * Annotation declarations cannot have type parameters and hence this method always returns an empty list.
     *
     * @return An empty list.
     */
  @Override public List<ResolvedTypeParameterDeclaration> getTypeParameters() {
    return Collections.emptyList();
  }

  @Override public Set<ResolvedReferenceTypeDeclaration> internalTypes() {
    return Arrays.stream(this.clazz.getDeclaredClasses()).map((ic) -> ReflectionFactory.typeDeclarationFor(ic, typeSolver)).collect(Collectors.toSet());
  }

  @Override public List<ResolvedConstructorDeclaration> getConstructors() {
    return Collections.emptyList();
  }

  @Override public List<ResolvedAnnotationMemberDeclaration> getAnnotationMembers() {
    return Stream.of(clazz.getDeclaredMethods()).map((m) -> new ReflectionAnnotationMemberDeclaration(m, typeSolver)).collect(Collectors.toList());
  }

  @Override public Optional<MethodUsage> solveMethodAsUsage(final String name, final List<ResolvedType> parameterTypes, final Context invokationContext, final List<ResolvedType> typeParameterValues) {
    Optional<MethodUsage> res = ReflectionMethodResolutionLogic.solveMethodAsUsage(name, parameterTypes, typeSolver, invokationContext, typeParameterValues, this, clazz);
    if (res.isPresent()) {
      InferenceContext inferenceContext = new InferenceContext(typeSolver);
      MethodUsage methodUsage = res.get();
      int i = 0;
      List<ResolvedType> parameters = new LinkedList<>();
      for (ResolvedType actualType : parameterTypes) {
        ResolvedType formalType = methodUsage.getParamType(i);
        parameters.add(inferenceContext.addPair(formalType, actualType));
        i++;
      }
      try {
        ResolvedType returnType = inferenceContext.addSingle(methodUsage.returnType());
        for (int j = 0; j < parameters.size(); j++) {
          methodUsage = methodUsage.replaceParamType(j, inferenceContext.resolve(parameters.get(j)));
        }
        methodUsage = methodUsage.replaceReturnType(inferenceContext.resolve(returnType));
        return Optional.of(methodUsage);
      } catch (ConfilictingGenericTypesException e) {
        return Optional.empty();
      }
    } else {
      return res;
    }
  }

  @Override public SymbolReference<ResolvedMethodDeclaration> solveMethod(final String name, final List<ResolvedType> argumentsTypes, final boolean staticOnly) {
    return ReflectionMethodResolutionLogic.solveMethod(name, argumentsTypes, staticOnly, typeSolver, this, clazz);
  }

  @Override public boolean isInheritable() {
    return clazz.getAnnotation(Inherited.class) != null;
  }
}