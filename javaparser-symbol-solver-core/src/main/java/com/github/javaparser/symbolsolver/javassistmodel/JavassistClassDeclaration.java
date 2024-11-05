package com.github.javaparser.symbolsolver.javassistmodel;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.core.resolution.Context;
import com.github.javaparser.symbolsolver.core.resolution.MethodUsageResolutionCapability;
import com.github.javaparser.symbolsolver.javaparsermodel.LambdaArgumentTypePlaceholder;
import com.github.javaparser.symbolsolver.logic.AbstractClassDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class JavassistClassDeclaration extends AbstractClassDeclaration implements MethodUsageResolutionCapability {
  private CtClass ctClass;

  private TypeSolver typeSolver;

  private JavassistTypeDeclarationAdapter javassistTypeDeclarationAdapter;

  public JavassistClassDeclaration(CtClass ctClass, TypeSolver typeSolver) {
    if (ctClass == null) {
      throw new IllegalArgumentException();
    }
    if (ctClass.isInterface() || ctClass.isAnnotation() || ctClass.isPrimitive() || ctClass.isEnum()) {
      throw new IllegalArgumentException("Trying to instantiate a JavassistClassDeclaration with something which is not a class: " + ctClass.toString());
    }
    this.ctClass = ctClass;
    this.typeSolver = typeSolver;
    this.javassistTypeDeclarationAdapter = new JavassistTypeDeclarationAdapter(ctClass, typeSolver, this);
  }

  @Override protected ResolvedReferenceType object() {
    return new ReferenceTypeImpl(typeSolver.getSolvedJavaLangObject(), typeSolver);
  }

  @Override public boolean hasDirectlyAnnotation(String canonicalName) {
    return ctClass.hasAnnotation(canonicalName);
  }

  @Override public Set<ResolvedMethodDeclaration> getDeclaredMethods() {
    return javassistTypeDeclarationAdapter.getDeclaredMethods();
  }

  @Override public boolean isAssignableBy(ResolvedReferenceTypeDeclaration other) {
    return isAssignableBy(new ReferenceTypeImpl(other, typeSolver));
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JavassistClassDeclaration that = (JavassistClassDeclaration) o;
    return ctClass.equals(that.ctClass);
  }

  @Override public int hashCode() {
    return ctClass.hashCode();
  }

  @Override public String getPackageName() {
    return ctClass.getPackageName();
  }

  @Override public String getClassName() {
    String className = ctClass.getName().replace('$', '.');
    if (getPackageName() != null) {
      return className.substring(getPackageName().length() + 1);
    }
    return className;
  }

  @Override public String getQualifiedName() {
    return ctClass.getName().replace('$', '.');
  }

  @Deprecated public Optional<MethodUsage> solveMethodAsUsage(String name, List<ResolvedType> argumentsTypes, Context invokationContext, List<ResolvedType> typeParameterValues) {
    return JavassistUtils.solveMethodAsUsage(name, argumentsTypes, typeSolver, invokationContext, typeParameterValues, this, ctClass);
  }

  @Deprecated public SymbolReference<? extends ResolvedValueDeclaration> solveSymbol(String name, TypeSolver typeSolver) {
    for (CtField field : ctClass.getDeclaredFields()) {
      if (field.getName().equals(name)) {
        return SymbolReference.solved(new JavassistFieldDeclaration(field, typeSolver));
      }
    }
    final String superclassFQN = getSuperclassFQN();
    SymbolReference<? extends ResolvedValueDeclaration> ref = solveSymbolForFQN(name, superclassFQN);
    if (ref.isSolved()) {
      return ref;
    }
    String[] interfaceFQNs = getInterfaceFQNs();
    for (String interfaceFQN : interfaceFQNs) {
      SymbolReference<? extends ResolvedValueDeclaration> interfaceRef = solveSymbolForFQN(name, interfaceFQN);
      if (interfaceRef.isSolved()) {
        return interfaceRef;
      }
    }
    return SymbolReference.unsolved(ResolvedValueDeclaration.class);
  }

  private SymbolReference<? extends ResolvedValueDeclaration> solveSymbolForFQN(String symbolName, String fqn) {
    if (fqn == null) {
      return SymbolReference.unsolved(ResolvedValueDeclaration.class);
    }
    ResolvedReferenceTypeDeclaration fqnTypeDeclaration = typeSolver.solveType(fqn);
    return new SymbolSolver(typeSolver).solveSymbolInType(fqnTypeDeclaration, symbolName);
  }

  private String[] getInterfaceFQNs() {
    return ctClass.getClassFile().getInterfaces();
  }

  private String getSuperclassFQN() {
    return ctClass.getClassFile().getSuperclass();
  }

  @Override public List<ResolvedReferenceType> getAncestors(boolean acceptIncompleteList) {
    return javassistTypeDeclarationAdapter.getAncestors(this, acceptIncompleteList);
  }

  @Override @Deprecated public SymbolReference<ResolvedMethodDeclaration> solveMethod(String name, List<ResolvedType> argumentsTypes, boolean staticOnly) {
    return JavassistUtils.solveMethod(name, argumentsTypes, staticOnly, typeSolver, this, ctClass);
  }

  public ResolvedType getUsage(Node node) {
    return new ReferenceTypeImpl(this, typeSolver);
  }

  @Override public boolean isAssignableBy(ResolvedType type) {
    if (type.isNull()) {
      return true;
    }
    if (type instanceof LambdaArgumentTypePlaceholder) {
      return isFunctionalInterface();
    }
    if (type.describe().equals(this.getQualifiedName())) {
      return true;
    }
    Optional<ResolvedReferenceType> superClassOpt = getSuperClass();
    if (superClassOpt.isPresent()) {
      ResolvedReferenceType superClass = superClassOpt.get();
      if (superClass.isAssignableBy(type)) {
        return true;
      }
    }
    for (ResolvedReferenceType interfaceType : getInterfaces()) {
      if (interfaceType.isAssignableBy(type)) {
        return true;
      }
    }
    return false;
  }

  @Override public boolean isTypeParameter() {
    return false;
  }

  @Override public List<ResolvedFieldDeclaration> getAllFields() {
    return javassistTypeDeclarationAdapter.getDeclaredFields();
  }

  @Override public String getName() {
    String[] nameElements = ctClass.getSimpleName().replace('$', '.').split("\\.");
    return nameElements[nameElements.length - 1];
  }

  @Override public boolean isField() {
    return false;
  }

  @Override public boolean isParameter() {
    return false;
  }

  @Override public boolean isType() {
    return true;
  }

  @Override public boolean isClass() {
    return !ctClass.isInterface();
  }

  @Override public Optional<ResolvedReferenceType> getSuperClass() {
    return javassistTypeDeclarationAdapter.getSuperClass();
  }

  @Override public List<ResolvedReferenceType> getInterfaces() {
    return javassistTypeDeclarationAdapter.getInterfaces();
  }

  @Override public boolean isInterface() {
    return ctClass.isInterface();
  }

  @Override public String toString() {
    return "JavassistClassDeclaration {" + ctClass.getName() + '}';
  }

  @Override public List<ResolvedTypeParameterDeclaration> getTypeParameters() {
    return javassistTypeDeclarationAdapter.getTypeParameters();
  }

  @Override public AccessSpecifier accessSpecifier() {
    return JavassistFactory.modifiersToAccessLevel(ctClass.getModifiers());
  }

  @Override public List<ResolvedConstructorDeclaration> getConstructors() {
    return javassistTypeDeclarationAdapter.getConstructors();
  }

  @Override public Optional<ResolvedReferenceTypeDeclaration> containerType() {
    return javassistTypeDeclarationAdapter.containerType();
  }

  @Override public Set<ResolvedReferenceTypeDeclaration> internalTypes() {
    try {
      return Arrays.stream(ctClass.getDeclaredClasses()).map((itype) -> JavassistFactory.toTypeDeclaration(itype, typeSolver)).collect(Collectors.toSet());
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override public ResolvedReferenceTypeDeclaration getInternalType(String name) {
    Optional<ResolvedReferenceTypeDeclaration> type = this.internalTypes().stream().filter((f) -> f.getName().endsWith(name)).findFirst();
    return type.orElseThrow(() -> new UnsolvedSymbolException("Internal type not found: " + name));
  }

  @Override public boolean hasInternalType(String name) {
    return this.internalTypes().stream().anyMatch((f) -> f.getName().endsWith(name));
  }

  @Override public Optional<Node> toAst() {
    return Optional.empty();
  }
}