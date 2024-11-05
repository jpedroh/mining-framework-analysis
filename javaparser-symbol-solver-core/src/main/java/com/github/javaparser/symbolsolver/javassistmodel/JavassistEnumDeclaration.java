package com.github.javaparser.symbolsolver.javassistmodel;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.core.resolution.Context;
import com.github.javaparser.symbolsolver.core.resolution.MethodUsageResolutionCapability;
import com.github.javaparser.symbolsolver.logic.AbstractTypeDeclaration;
import com.github.javaparser.symbolsolver.logic.MethodResolutionCapability;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import java.util.*;
import java.util.stream.Collectors;

public class JavassistEnumDeclaration extends AbstractTypeDeclaration implements ResolvedEnumDeclaration, MethodResolutionCapability, MethodUsageResolutionCapability {
  private CtClass ctClass;

  private TypeSolver typeSolver;

  private JavassistTypeDeclarationAdapter javassistTypeDeclarationAdapter;

  public JavassistEnumDeclaration(CtClass ctClass, TypeSolver typeSolver) {
    if (ctClass == null) {
      throw new IllegalArgumentException();
    }
    if (!ctClass.isEnum()) {
      throw new IllegalArgumentException("Trying to instantiate a JavassistEnumDeclaration with something which is not an enum: " + ctClass.toString());
    }
    this.ctClass = ctClass;
    this.typeSolver = typeSolver;
    this.javassistTypeDeclarationAdapter = new JavassistTypeDeclarationAdapter(ctClass, typeSolver, this);
  }

  @Override public AccessSpecifier accessSpecifier() {
    return JavassistFactory.modifiersToAccessLevel(ctClass.getModifiers());
  }

  @Override public String getPackageName() {
    return ctClass.getPackageName();
  }

  @Override public String getClassName() {
    String name = ctClass.getName().replace('$', '.');
    if (getPackageName() != null) {
      return name.substring(getPackageName().length() + 1);
    }
    return name;
  }

  @Override public String getQualifiedName() {
    return ctClass.getName().replace('$', '.');
  }

  @Override public List<ResolvedReferenceType> getAncestors(boolean acceptIncompleteList) {
    return javassistTypeDeclarationAdapter.getAncestors(this, acceptIncompleteList);
  }

  @Override public ResolvedFieldDeclaration getField(String name) {
    Optional<ResolvedFieldDeclaration> field = javassistTypeDeclarationAdapter.getDeclaredFields().stream().filter((f) -> f.getName().equals(name)).findFirst();
    return field.orElseThrow(() -> new RuntimeException("Field " + name + " does not exist in " + ctClass.getName() + "."));
  }

  @Override public boolean hasField(String name) {
    return javassistTypeDeclarationAdapter.getDeclaredFields().stream().anyMatch((f) -> f.getName().equals(name));
  }

  @Override public List<ResolvedFieldDeclaration> getAllFields() {
    return javassistTypeDeclarationAdapter.getDeclaredFields();
  }

  @Override public Set<ResolvedMethodDeclaration> getDeclaredMethods() {
    return javassistTypeDeclarationAdapter.getDeclaredMethods();
  }

  @Override public boolean isAssignableBy(ResolvedType type) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean isAssignableBy(ResolvedReferenceTypeDeclaration other) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean hasDirectlyAnnotation(String canonicalName) {
    return ctClass.hasAnnotation(canonicalName);
  }

  @Override public String getName() {
    String[] nameElements = ctClass.getSimpleName().replace('$', '.').split("\\.");
    return nameElements[nameElements.length - 1];
  }

  @Override public List<ResolvedTypeParameterDeclaration> getTypeParameters() {
    return javassistTypeDeclarationAdapter.getTypeParameters();
  }

  @Override public Optional<ResolvedReferenceTypeDeclaration> containerType() {
    return javassistTypeDeclarationAdapter.containerType();
  }

  @Override public SymbolReference<ResolvedMethodDeclaration> solveMethod(String name, List<ResolvedType> argumentsTypes, boolean staticOnly) {
    return JavassistUtils.solveMethod(name, argumentsTypes, staticOnly, typeSolver, this, ctClass);
  }

  public Optional<MethodUsage> solveMethodAsUsage(String name, List<ResolvedType> argumentsTypes, Context invokationContext, List<ResolvedType> typeParameterValues) {
    return JavassistUtils.solveMethodAsUsage(name, argumentsTypes, typeSolver, invokationContext, typeParameterValues, this, ctClass);
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

  public SymbolReference<? extends ResolvedValueDeclaration> solveSymbol(String name, TypeSolver typeSolver) {
    for (CtField field : ctClass.getDeclaredFields()) {
      if (field.getName().equals(name)) {
        return SymbolReference.solved(new JavassistFieldDeclaration(field, typeSolver));
      }
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

  @Override public List<ResolvedEnumConstantDeclaration> getEnumConstants() {
    return Arrays.stream(ctClass.getFields()).filter((f) -> (f.getFieldInfo2().getAccessFlags() & AccessFlag.ENUM) != 0).map((f) -> new JavassistEnumConstantDeclaration(f, typeSolver)).collect(Collectors.toList());
  }

  @Override public List<ResolvedConstructorDeclaration> getConstructors() {
    return javassistTypeDeclarationAdapter.getConstructors();
  }

  @Override public String toString() {
    return getClass().getSimpleName() + "{" + "ctClass=" + ctClass.getName() + ", typeSolver=" + typeSolver + '}';
  }
}