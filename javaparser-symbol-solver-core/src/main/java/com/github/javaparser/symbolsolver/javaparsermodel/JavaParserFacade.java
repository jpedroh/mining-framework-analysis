package com.github.javaparser.symbolsolver.javaparsermodel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.resolution.MethodAmbiguityException;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.resolution.types.*;
import com.github.javaparser.symbolsolver.core.resolution.Context;
import com.github.javaparser.symbolsolver.javaparsermodel.contexts.FieldAccessContext;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserAnonymousClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserEnumDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserTypeVariableDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionAnnotationDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionClassDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionEnumDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionInterfaceDeclaration;
import com.github.javaparser.symbolsolver.resolution.ConstructorResolutionLogic;
import com.github.javaparser.symbolsolver.resolution.MethodResolutionLogic;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import com.github.javaparser.utils.Log;
import java.util.*;
import java.util.stream.Collectors;
import static com.github.javaparser.symbolsolver.javaparser.Navigator.demandParentNode;
import static com.github.javaparser.symbolsolver.model.resolution.SymbolReference.solved;
import static com.github.javaparser.symbolsolver.model.resolution.SymbolReference.unsolved;

public class JavaParserFacade {
  private static final DataKey<ResolvedType> TYPE_WITH_LAMBDAS_RESOLVED = new DataKey<ResolvedType>() { };

  private static final DataKey<ResolvedType> TYPE_WITHOUT_LAMBDAS_RESOLVED = new DataKey<ResolvedType>() { };

  private static final Map<TypeSolver, JavaParserFacade> instances = new WeakHashMap<>();

  private final TypeSolver typeSolver;

  private final TypeExtractor typeExtractor;

  private final SymbolSolver symbolSolver;

  private JavaParserFacade(TypeSolver typeSolver) {
    this.typeSolver = typeSolver.getRoot();
    this.symbolSolver = new SymbolSolver(typeSolver);
    this.typeExtractor = new TypeExtractor(typeSolver, this);
  }

  public TypeSolver getTypeSolver() {
    return typeSolver;
  }

  public SymbolSolver getSymbolSolver() {
    return symbolSolver;
  }

  public synchronized static JavaParserFacade get(TypeSolver typeSolver) {
    return instances.computeIfAbsent(typeSolver, JavaParserFacade::new);
  }

  public static void clearInstances() {
    instances.clear();
  }

  protected static ResolvedType solveGenericTypes(ResolvedType type, Context context) {
    if (type.isTypeVariable()) {
      return context.solveGenericType(type.describe()).orElse(type);
    }
    if (type.isWildcard()) {
      if (type.asWildcard().isExtends() || type.asWildcard().isSuper()) {
        ResolvedWildcard wildcardUsage = type.asWildcard();
        ResolvedType boundResolved = solveGenericTypes(wildcardUsage.getBoundedType(), context);
        if (wildcardUsage.isExtends()) {
          return ResolvedWildcard.extendsBound(boundResolved);
        } else {
          return ResolvedWildcard.superBound(boundResolved);
        }
      }
    }
    return type;
  }

  public SymbolReference<? extends ResolvedValueDeclaration> solve(NameExpr nameExpr) {
    return symbolSolver.solveSymbol(nameExpr.getName().getId(), nameExpr);
  }

  public SymbolReference<? extends ResolvedValueDeclaration> solve(SimpleName nameExpr) {
    return symbolSolver.solveSymbol(nameExpr.getId(), nameExpr);
  }

  public SymbolReference<? extends ResolvedValueDeclaration> solve(Expression expr) {
    return expr.toNameExpr().map(this::solve).orElseThrow(() -> new IllegalArgumentException(expr.getClass().getCanonicalName()));
  }

  public SymbolReference<ResolvedMethodDeclaration> solve(MethodCallExpr methodCallExpr) {
    return solve(methodCallExpr, true);
  }

  public SymbolReference<ResolvedMethodDeclaration> solve(MethodReferenceExpr methodReferenceExpr) {
    return solve(methodReferenceExpr, true);
  }

  public SymbolReference<ResolvedConstructorDeclaration> solve(ObjectCreationExpr objectCreationExpr) {
    return solve(objectCreationExpr, true);
  }

  public SymbolReference<ResolvedConstructorDeclaration> solve(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt) {
    return solve(explicitConstructorInvocationStmt, true);
  }

  public SymbolReference<ResolvedConstructorDeclaration> solve(ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt, boolean solveLambdas) {
    Optional<ClassOrInterfaceDeclaration> optAncestorClassOrInterfaceNode = explicitConstructorInvocationStmt.findAncestor(ClassOrInterfaceDeclaration.class);
    if (!optAncestorClassOrInterfaceNode.isPresent()) {
      return unsolved(ResolvedConstructorDeclaration.class);
    }
    ClassOrInterfaceDeclaration classOrInterfaceNode = optAncestorClassOrInterfaceNode.get();
    ResolvedReferenceTypeDeclaration resolvedClassNode = classOrInterfaceNode.resolve();
    if (!resolvedClassNode.isClass()) {
      throw new IllegalStateException("Expected to be a class -- cannot call this() or super() within an interface.");
    }
    ResolvedTypeDeclaration typeDecl = null;
    if (explicitConstructorInvocationStmt.isThis()) {
      typeDecl = resolvedClassNode.asReferenceType();
    } else {
      Optional<ResolvedReferenceType> superClass = resolvedClassNode.asClass().getSuperClass();
      if (superClass.isPresent() && superClass.get().getTypeDeclaration().isPresent()) {
        typeDecl = superClass.get().getTypeDeclaration().get();
      }
    }
    if (typeDecl == null) {
      return unsolved(ResolvedConstructorDeclaration.class);
    }
    List<ResolvedType> argumentTypes = new LinkedList<>();
    List<LambdaArgumentTypePlaceholder> placeholders = new LinkedList<>();
    solveArguments(explicitConstructorInvocationStmt, explicitConstructorInvocationStmt.getArguments(), solveLambdas, argumentTypes, placeholders);
    SymbolReference<ResolvedConstructorDeclaration> res = ConstructorResolutionLogic.findMostApplicable(((ResolvedClassDeclaration) typeDecl).getConstructors(), argumentTypes, typeSolver);
    for (LambdaArgumentTypePlaceholder placeholder : placeholders) {
      placeholder.setMethod(res);
    }
    return res;
  }

  public SymbolReference<ResolvedTypeDeclaration> solve(ThisExpr node) {
    if (node.getTypeName().isPresent()) {
      String className = node.getTypeName().get().asString();
      SymbolReference<ResolvedReferenceTypeDeclaration> clazz = typeSolver.tryToSolveType(className);
      if (clazz.isSolved()) {
        return solved(clazz.getCorrespondingDeclaration());
      }
      Optional<CompilationUnit> cu = node.findAncestor(CompilationUnit.class);
      if (cu.isPresent()) {
        Optional<ClassOrInterfaceDeclaration> classByName = cu.get().getClassByName(className);
        if (classByName.isPresent()) {
          return solved(getTypeDeclaration(classByName.get()));
        }
      }
    }
    return solved(getTypeDeclaration(findContainingTypeDeclOrObjectCreationExpr(node)));
  }

  public SymbolReference<ResolvedConstructorDeclaration> solve(ObjectCreationExpr objectCreationExpr, boolean solveLambdas) {
    List<ResolvedType> argumentTypes = new LinkedList<>();
    List<LambdaArgumentTypePlaceholder> placeholders = new LinkedList<>();
    solveArguments(objectCreationExpr, objectCreationExpr.getArguments(), solveLambdas, argumentTypes, placeholders);
    ResolvedReferenceTypeDeclaration typeDecl = null;
    if (objectCreationExpr.getAnonymousClassBody().isPresent()) {
      typeDecl = new JavaParserAnonymousClassDeclaration(objectCreationExpr, typeSolver);
    } else {
      ResolvedType classDecl = JavaParserFacade.get(typeSolver).convert(objectCreationExpr.getType(), objectCreationExpr);
      if (classDecl.isReferenceType() && classDecl.asReferenceType().getTypeDeclaration().isPresent()) {
        typeDecl = classDecl.asReferenceType().getTypeDeclaration().get();
      }
    }
    if (typeDecl == null) {
      return unsolved(ResolvedConstructorDeclaration.class);
    }
    SymbolReference<ResolvedConstructorDeclaration> res = ConstructorResolutionLogic.findMostApplicable(typeDecl.getConstructors(), argumentTypes, typeSolver);
    for (LambdaArgumentTypePlaceholder placeholder : placeholders) {
      placeholder.setMethod(res);
    }
    return res;
  }

  private void solveArguments(Node node, NodeList<Expression> args, boolean solveLambdas, List<ResolvedType> argumentTypes, List<LambdaArgumentTypePlaceholder> placeholders) {
    int i = 0;
    for (Expression parameterValue : args) {
      if (parameterValue instanceof LambdaExpr || parameterValue instanceof MethodReferenceExpr) {
        LambdaArgumentTypePlaceholder placeholder = new LambdaArgumentTypePlaceholder(i);
        argumentTypes.add(placeholder);
        placeholders.add(placeholder);
      } else {
        try {
          argumentTypes.add(JavaParserFacade.get(typeSolver).getType(parameterValue, solveLambdas));
        } catch (UnsolvedSymbolException e) {
          throw e;
        } catch (Exception e) {
          throw new RuntimeException(String.format("Unable to calculate the type of a parameter of a method call. Method call: %s, Parameter: %s", node, parameterValue), e);
        }
      }
      i++;
    }
  }

  public SymbolReference<ResolvedMethodDeclaration> solve(MethodCallExpr methodCallExpr, boolean solveLambdas) {
    List<ResolvedType> argumentTypes = new LinkedList<>();
    List<LambdaArgumentTypePlaceholder> placeholders = new LinkedList<>();
    solveArguments(methodCallExpr, methodCallExpr.getArguments(), solveLambdas, argumentTypes, placeholders);
    SymbolReference<ResolvedMethodDeclaration> res = JavaParserFactory.getContext(methodCallExpr, typeSolver).solveMethod(methodCallExpr.getName().getId(), argumentTypes, false);
    for (LambdaArgumentTypePlaceholder placeholder : placeholders) {
      placeholder.setMethod(res);
    }
    return res;
  }

  public SymbolReference<ResolvedMethodDeclaration> solve(MethodReferenceExpr methodReferenceExpr, boolean solveLambdas) {
    List<ResolvedType> argumentTypes = new LinkedList<>();
    return JavaParserFactory.getContext(methodReferenceExpr, typeSolver).solveMethod(methodReferenceExpr.getIdentifier(), argumentTypes, false);
  }

  public SymbolReference<ResolvedAnnotationDeclaration> solve(AnnotationExpr annotationExpr) {
    Context context = JavaParserFactory.getContext(annotationExpr, typeSolver);
    SymbolReference<ResolvedTypeDeclaration> typeDeclarationSymbolReference = context.solveType(annotationExpr.getNameAsString());
    if (typeDeclarationSymbolReference.isSolved()) {
      ResolvedAnnotationDeclaration annotationDeclaration = (ResolvedAnnotationDeclaration) typeDeclarationSymbolReference.getCorrespondingDeclaration();
      return solved(annotationDeclaration);
    } else {
      return unsolved(ResolvedAnnotationDeclaration.class);
    }
  }

  public SymbolReference<ResolvedValueDeclaration> solve(FieldAccessExpr fieldAccessExpr) {
    return ((FieldAccessContext) JavaParserFactory.getContext(fieldAccessExpr, typeSolver)).solveField(fieldAccessExpr.getName().getId());
  }

  public ResolvedType getType(Node node) {
    try {
      return getType(node, true);
    } catch (UnsolvedSymbolException e) {
      if (node instanceof NameExpr) {
        NameExpr nameExpr = (NameExpr) node;
        SymbolReference<ResolvedTypeDeclaration> typeDeclaration = JavaParserFactory.getContext(node, typeSolver).solveType(nameExpr.getNameAsString());
        if (typeDeclaration.isSolved() && typeDeclaration.getCorrespondingDeclaration() instanceof ResolvedReferenceTypeDeclaration) {
          ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration = (ResolvedReferenceTypeDeclaration) typeDeclaration.getCorrespondingDeclaration();
          return ReferenceTypeImpl.undeterminedParameters(resolvedReferenceTypeDeclaration, typeSolver);
        }
      }
      throw e;
    }
  }

  public ResolvedType getType(Node node, boolean solveLambdas) {
    if (solveLambdas) {
      if (!node.containsData(TYPE_WITH_LAMBDAS_RESOLVED)) {
        ResolvedType res = getTypeConcrete(node, solveLambdas);
        node.setData(TYPE_WITH_LAMBDAS_RESOLVED, res);
        boolean secondPassNecessary = false;
        if (node instanceof MethodCallExpr) {
          MethodCallExpr methodCallExpr = (MethodCallExpr) node;
          for (Node arg : methodCallExpr.getArguments()) {
            if (!arg.containsData(TYPE_WITH_LAMBDAS_RESOLVED)) {
              getType(arg, true);
              secondPassNecessary = true;
            }
          }
        }
        if (secondPassNecessary) {
          node.removeData(TYPE_WITH_LAMBDAS_RESOLVED);
          ResolvedType type = getType(node, true);
          node.setData(TYPE_WITH_LAMBDAS_RESOLVED, type);
        }
        Log.trace("getType on %s  -> %s", () -> node, () -> res);
      }
      return node.getData(TYPE_WITH_LAMBDAS_RESOLVED);
    } else {
      Optional<ResolvedType> res = find(TYPE_WITH_LAMBDAS_RESOLVED, node);
      if (res.isPresent()) {
        return res.get();
      }
      res = find(TYPE_WITHOUT_LAMBDAS_RESOLVED, node);
      if (!res.isPresent()) {
        ResolvedType resType = getTypeConcrete(node, solveLambdas);
        node.setData(TYPE_WITHOUT_LAMBDAS_RESOLVED, resType);
        Optional<ResolvedType> finalRes = res;
        Log.trace("getType on %s (no solveLambdas) -> %s", () -> node, () -> finalRes);
        return resType;
      }
      return res.get();
    }
  }

  private Optional<ResolvedType> find(DataKey<ResolvedType> dataKey, Node node) {
    if (node.containsData(dataKey)) {
      return Optional.of(node.getData(dataKey));
    }
    return Optional.empty();
  }

  protected MethodUsage toMethodUsage(MethodReferenceExpr methodReferenceExpr, List<ResolvedType> paramTypes) {
    Expression scope = methodReferenceExpr.getScope();
    ResolvedType typeOfScope = getType(methodReferenceExpr.getScope());
    if (!typeOfScope.isReferenceType()) {
      throw new UnsupportedOperationException(typeOfScope.getClass().getCanonicalName());
    }
    Optional<MethodUsage> result;
    Set<MethodUsage> allMethods = typeOfScope.asReferenceType().getTypeDeclaration().orElseThrow(() -> new RuntimeException("TypeDeclaration unexpectedly empty.")).getAllMethods();
    if (scope instanceof TypeExpr) {
      List<MethodUsage> staticMethodUsages = allMethods.stream().filter((it) -> it.getDeclaration().isStatic()).collect(Collectors.toList());
      result = MethodResolutionLogic.findMostApplicableUsage(staticMethodUsages, methodReferenceExpr.getIdentifier(), paramTypes, typeSolver);
      if (!paramTypes.isEmpty()) {
        List<MethodUsage> instanceMethodUsages = allMethods.stream().filter((it) -> !it.getDeclaration().isStatic()).collect(Collectors.toList());
        List<ResolvedType> instanceMethodParamTypes = new ArrayList<>(paramTypes);
        instanceMethodParamTypes.remove(0);
        Optional<MethodUsage> instanceResult = MethodResolutionLogic.findMostApplicableUsage(instanceMethodUsages, methodReferenceExpr.getIdentifier(), instanceMethodParamTypes, typeSolver);
        if (result.isPresent() && instanceResult.isPresent()) {
          throw new MethodAmbiguityException("Ambiguous method call: cannot find a most applicable method for " + methodReferenceExpr.getIdentifier());
        }
        if (instanceResult.isPresent()) {
          result = instanceResult;
        }
      }
    } else {
      result = MethodResolutionLogic.findMostApplicableUsage(new ArrayList<>(allMethods), methodReferenceExpr.getIdentifier(), paramTypes, typeSolver);
      if (result.isPresent() && result.get().getDeclaration().isStatic()) {
        throw new RuntimeException("Invalid static method reference " + methodReferenceExpr.getIdentifier());
      }
    }
    if (!result.isPresent()) {
      throw new UnsupportedOperationException();
    }
    return result.get();
  }

  protected ResolvedType getBinaryTypeConcrete(Node left, Node right, boolean solveLambdas, BinaryExpr.Operator operator) {
    ResolvedType leftType = getTypeConcrete(left, solveLambdas);
    ResolvedType rightType = getTypeConcrete(right, solveLambdas);
    if (operator == BinaryExpr.Operator.PLUS) {
      boolean isLeftString = leftType.isReferenceType() && leftType.asReferenceType().getQualifiedName().equals(JAVA_LANG_STRING);
      boolean isRightString = rightType.isReferenceType() && rightType.asReferenceType().getQualifiedName().equals(JAVA_LANG_STRING);
      if (isLeftString || isRightString) {
        return isLeftString ? leftType : rightType;
      }
    }
    boolean isLeftNumeric = leftType.isPrimitive() && leftType.asPrimitive().isNumeric();
    boolean isRightNumeric = rightType.isPrimitive() && rightType.asPrimitive().isNumeric();
    if (isLeftNumeric && isRightNumeric) {
      return leftType.asPrimitive().bnp(rightType.asPrimitive());
    }
    if (rightType.isAssignableBy(leftType)) {
      return rightType;
    }
    return leftType;
  }

  private ResolvedType getTypeConcrete(Node node, boolean solveLambdas) {
    if (node == null) {
      throw new IllegalArgumentException();
    }
    return node.accept(typeExtractor, solveLambdas);
  }

  protected TypeDeclaration<?> findContainingTypeDecl(Node node) {
    if (node instanceof ClassOrInterfaceDeclaration) {
      return (ClassOrInterfaceDeclaration) node;
    }
    if (node instanceof EnumDeclaration) {
      return (EnumDeclaration) node;
    }
    return findContainingTypeDecl(demandParentNode(node));
  }

  protected Node findContainingTypeDeclOrObjectCreationExpr(Node node) {
    if (node instanceof ClassOrInterfaceDeclaration) {
      return node;
    }
    if (node instanceof EnumDeclaration) {
      return node;
    }
    Node parent = demandParentNode(node);
    if (parent instanceof ObjectCreationExpr && !((ObjectCreationExpr) parent).getArguments().contains(node)) {
      return parent;
    }
    return findContainingTypeDeclOrObjectCreationExpr(parent);
  }

  protected Node findContainingTypeDeclOrObjectCreationExpr(Node node, String className) {
    if (node instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration) node).getFullyQualifiedName().get().endsWith(className)) {
      return node;
    }
    if (node instanceof EnumDeclaration) {
      return node;
    }
    Node parent = demandParentNode(node);
    if (parent instanceof ObjectCreationExpr && !((ObjectCreationExpr) parent).getArguments().contains(node)) {
      return parent;
    }
    return findContainingTypeDeclOrObjectCreationExpr(parent, className);
  }

  public ResolvedType convertToUsageVariableType(VariableDeclarator var) {
    return get(typeSolver).convertToUsage(var.getType(), var);
  }

  public ResolvedType convertToUsage(Type type, Node context) {
    if (type.isUnknownType()) {
      throw new IllegalArgumentException("Inferred lambda parameter type");
    }
    return convertToUsage(type, JavaParserFactory.getContext(context, typeSolver));
  }

  public ResolvedType convertToUsage(Type type) {
    return convertToUsage(type, type);
  }

  private String qName(ClassOrInterfaceType classOrInterfaceType) {
    String name = classOrInterfaceType.getName().getId();
    if (classOrInterfaceType.getScope().isPresent()) {
      return qName(classOrInterfaceType.getScope().get()) + "." + name;
    }
    return name;
  }

  protected ResolvedType convertToUsage(Type type, Context context) {
    if (context == null) {
      throw new NullPointerException("Context should not be null");
    }
    if (type instanceof ClassOrInterfaceType) {
      ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) type;
      String name = qName(classOrInterfaceType);
      SymbolReference<ResolvedTypeDeclaration> ref = context.solveType(name);
      if (!ref.isSolved()) {
        throw new UnsolvedSymbolException(name);
      }
      ResolvedTypeDeclaration typeDeclaration = ref.getCorrespondingDeclaration();
      List<ResolvedType> typeParameters = Collections.emptyList();
      if (classOrInterfaceType.getTypeArguments().isPresent()) {
        typeParameters = classOrInterfaceType.getTypeArguments().get().stream().map((pt) -> convertToUsage(pt, context)).collect(Collectors.toList());
      }
      if (typeDeclaration.isTypeParameter()) {
        if (typeDeclaration instanceof ResolvedTypeParameterDeclaration) {
          return new ResolvedTypeVariable((ResolvedTypeParameterDeclaration) typeDeclaration);
        } else {
          JavaParserTypeVariableDeclaration javaParserTypeVariableDeclaration = (JavaParserTypeVariableDeclaration) typeDeclaration;
          return new ResolvedTypeVariable(javaParserTypeVariableDeclaration.asTypeParameter());
        }
      } else {
        return new ReferenceTypeImpl((ResolvedReferenceTypeDeclaration) typeDeclaration, typeParameters, typeSolver);
      }
    } else {
      if (type instanceof PrimitiveType) {
        return ResolvedPrimitiveType.byName(((PrimitiveType) type).getType().name());
      } else {
        if (type instanceof WildcardType) {
          WildcardType wildcardType = (WildcardType) type;
          if (wildcardType.getExtendedType().isPresent() && !wildcardType.getSuperType().isPresent()) {
            return ResolvedWildcard.extendsBound(convertToUsage(wildcardType.getExtendedType().get(), context));
          } else {
            if (!wildcardType.getExtendedType().isPresent() && wildcardType.getSuperType().isPresent()) {
              return ResolvedWildcard.superBound(convertToUsage(wildcardType.getSuperType().get(), context));
            } else {
              if (!wildcardType.getExtendedType().isPresent() && !wildcardType.getSuperType().isPresent()) {
                return ResolvedWildcard.UNBOUNDED;
              } else {
                throw new UnsupportedOperationException(wildcardType.toString());
              }
            }
          }
        } else {
          if (type instanceof VoidType) {
            return ResolvedVoidType.INSTANCE;
          } else {
            if (type instanceof ArrayType) {
              ArrayType jpArrayType = (ArrayType) type;
              return new ResolvedArrayType(convertToUsage(jpArrayType.getComponentType(), context));
            } else {
              if (type instanceof UnionType) {
                UnionType unionType = (UnionType) type;
                return new ResolvedUnionType(unionType.getElements().stream().map((el) -> convertToUsage(el, context)).collect(Collectors.toList()));
              } else {
                if (type instanceof VarType) {
                  Node parent = type.getParentNode().get();
                  if (!(parent instanceof VariableDeclarator)) {
                    throw new IllegalStateException("Trying to resolve a `var` which is not in a variable declaration.");
                  }
                  final VariableDeclarator variableDeclarator = (VariableDeclarator) parent;
                  return variableDeclarator.getInitializer().map(Expression::calculateResolvedType).orElseThrow(() -> new IllegalStateException("Cannot resolve `var` which has no initializer."));
                } else {
                  throw new UnsupportedOperationException(type.getClass().getCanonicalName());
                }
              }
            }
          }
        }
      }
    }
  }

  public ResolvedType convert(Type type, Node node) {
    return convert(type, JavaParserFactory.getContext(node, typeSolver));
  }

  public ResolvedType convert(Type type, Context context) {
    return convertToUsage(type, context);
  }

  public MethodUsage solveMethodAsUsage(MethodCallExpr call) {
    List<ResolvedType> params = new ArrayList<>();
    if (call.getArguments() != null) {
      for (Expression param : call.getArguments()) {
        try {
          params.add(getType(param, false));
        } catch (Exception e) {
          throw new RuntimeException(String.format("Error calculating the type of parameter %s of method call %s", param, call), e);
        }
      }
    }
    Context context = JavaParserFactory.getContext(call, typeSolver);
    Optional<MethodUsage> methodUsage = context.solveMethodAsUsage(call.getName().getId(), params);
    if (!methodUsage.isPresent()) {
      throw new RuntimeException("Method \'" + call.getName() + "\' cannot be resolved in context " + call + " (line: " + call.getRange().map((r) -> "" + r.begin.line).orElse("??") + ") " + context + ". Parameter types: " + params);
    }
    return methodUsage.get();
  }

  public ResolvedReferenceTypeDeclaration getTypeDeclaration(Node node) {
    if (node instanceof TypeDeclaration) {
      return getTypeDeclaration((TypeDeclaration) node);
    } else {
      if (node instanceof ObjectCreationExpr) {
        return new JavaParserAnonymousClassDeclaration((ObjectCreationExpr) node, typeSolver);
      } else {
        throw new IllegalArgumentException();
      }
    }
  }

  public ResolvedReferenceTypeDeclaration getTypeDeclaration(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
    return JavaParserFactory.toTypeDeclaration(classOrInterfaceDeclaration, typeSolver);
  }

  public ResolvedType getTypeOfThisIn(Node node) {
    if (node instanceof ClassOrInterfaceDeclaration) {
      return new ReferenceTypeImpl(getTypeDeclaration((ClassOrInterfaceDeclaration) node), typeSolver);
    } else {
      if (node instanceof EnumDeclaration) {
        JavaParserEnumDeclaration enumDeclaration = new JavaParserEnumDeclaration((EnumDeclaration) node, typeSolver);
        return new ReferenceTypeImpl(enumDeclaration, typeSolver);
      } else {
        if (node instanceof ObjectCreationExpr && ((ObjectCreationExpr) node).getAnonymousClassBody().isPresent()) {
          JavaParserAnonymousClassDeclaration anonymousDeclaration = new JavaParserAnonymousClassDeclaration((ObjectCreationExpr) node, typeSolver);
          return new ReferenceTypeImpl(anonymousDeclaration, typeSolver);
        }
      }
    }
    return getTypeOfThisIn(demandParentNode(node));
  }

  public ResolvedReferenceTypeDeclaration getTypeDeclaration(TypeDeclaration<?> typeDeclaration) {
    return JavaParserFactory.toTypeDeclaration(typeDeclaration, typeSolver);
  }

  public ResolvedType classToResolvedType(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      return ResolvedPrimitiveType.byName(clazz.getName());
    }
    ResolvedReferenceTypeDeclaration declaration;
    if (clazz.isAnnotation()) {
      declaration = new ReflectionAnnotationDeclaration(clazz, typeSolver);
    } else {
      if (clazz.isEnum()) {
        declaration = new ReflectionEnumDeclaration(clazz, typeSolver);
      } else {
        if (clazz.isInterface()) {
          declaration = new ReflectionInterfaceDeclaration(clazz, typeSolver);
        } else {
          declaration = new ReflectionClassDeclaration(clazz, typeSolver);
        }
      }
    }
    return new ReferenceTypeImpl(declaration, typeSolver);
  }

  private static String JAVA_LANG_STRING = String.class.getCanonicalName();
}