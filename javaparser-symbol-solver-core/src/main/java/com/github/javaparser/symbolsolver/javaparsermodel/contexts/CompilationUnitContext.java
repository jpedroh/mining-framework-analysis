package com.github.javaparser.symbolsolver.javaparsermodel.contexts;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.logic.MethodResolutionLogic;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserAnnotationDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserEnumDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserInterfaceDeclaration;
import com.github.javaparser.symbolsolver.resolution.SymbolSolver;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Federico Tomassetti
 */
public class CompilationUnitContext extends AbstractJavaParserContext<CompilationUnit> {
  private static final String DEFAULT_PACKAGE = "java.lang";

  public CompilationUnitContext(CompilationUnit wrappedNode, TypeSolver typeSolver) {
    super(wrappedNode, typeSolver);
  }

  @Override public SymbolReference<? extends ResolvedValueDeclaration> solveSymbol(String name) {
    String itName = name;
    while (itName.contains(".")) {
      String typeName = getType(itName);
      String memberName = getMember(itName);
      SymbolReference<ResolvedTypeDeclaration> type = this.solveType(typeName);
      if (type.isSolved()) {
        return new SymbolSolver(typeSolver).solveSymbolInType(type.getCorrespondingDeclaration(), memberName);
      } else {
        itName = typeName;
      }
    }
    for (ImportDeclaration importDecl : wrappedNode.getImports()) {
      if (importDecl.isStatic()) {
        if (importDecl.isAsterisk()) {
          String qName = importDecl.getNameAsString();
          ResolvedTypeDeclaration importedType = typeSolver.solveType(qName);
          if (!isAncestorOf(importedType)) {
            SymbolReference<? extends ResolvedValueDeclaration> ref = new SymbolSolver(typeSolver).solveSymbolInType(importedType, name);
            if (ref.isSolved()) {
              return ref;
            }
          }
        } else {
          String whole = importDecl.getNameAsString();
          String memberName = getMember(whole);
          String typeName = getType(whole);
          if (memberName.equals(name)) {
            ResolvedTypeDeclaration importedType = typeSolver.solveType(typeName);
            return new SymbolSolver(typeSolver).solveSymbolInType(importedType, memberName);
          }
        }
      }
    }
    return SymbolReference.unsolved();
  }

  @Override public SymbolReference<ResolvedTypeDeclaration> solveType(String name, List<ResolvedType> typeArguments) {
    if (wrappedNode.getTypes() != null) {
      for (TypeDeclaration<?> type : wrappedNode.getTypes()) {
        if (type.getName().getId().equals(name) || type.getFullyQualifiedName().map((qualified) -> qualified.equals(name)).orElse(false)) {
          if (type instanceof ClassOrInterfaceDeclaration) {
            return SymbolReference.solved(JavaParserFacade.get(typeSolver).getTypeDeclaration((ClassOrInterfaceDeclaration) type));
          } else {
            if (type instanceof AnnotationDeclaration) {
              return SymbolReference.solved(new JavaParserAnnotationDeclaration((AnnotationDeclaration) type, typeSolver));
            } else {
              if (type instanceof EnumDeclaration) {
                return SymbolReference.solved(new JavaParserEnumDeclaration((EnumDeclaration) type, typeSolver));
              } else {
                throw new UnsupportedOperationException(type.getClass().getCanonicalName());
              }
            }
          }
        }
      }
      if (name.indexOf('.') > -1) {
        SymbolReference<ResolvedTypeDeclaration> ref = null;
        SymbolReference<ResolvedTypeDeclaration> outerMostRef = solveType(name.substring(0, name.indexOf(".")));
        if (outerMostRef != null && outerMostRef.isSolved() && outerMostRef.getCorrespondingDeclaration() instanceof JavaParserClassDeclaration) {
          ref = ((JavaParserClassDeclaration) outerMostRef.getCorrespondingDeclaration()).solveType(name.substring(name.indexOf(".") + 1));
        } else {
          if (outerMostRef != null && outerMostRef.isSolved() && outerMostRef.getCorrespondingDeclaration() instanceof JavaParserInterfaceDeclaration) {
            ref = ((JavaParserInterfaceDeclaration) outerMostRef.getCorrespondingDeclaration()).solveType(name.substring(name.indexOf(".") + 1));
          }
        }
        if (ref != null && ref.isSolved()) {
          return ref;
        }
      }
    }
    int dotPos = name.indexOf('.');
    String prefix = null;
    if (dotPos > -1) {
      prefix = name.substring(0, dotPos);
    }
    for (ImportDeclaration importDecl : wrappedNode.getImports()) {
      if (!importDecl.isAsterisk()) {
        String qName = importDecl.getNameAsString();
        boolean defaultPackage = !importDecl.getName().getQualifier().isPresent();
        boolean found = !defaultPackage && importDecl.getName().getIdentifier().equals(name);
        if (!found && prefix != null) {
          found = qName.endsWith("." + prefix);
          if (found) {
            qName = qName + name.substring(dotPos);
          }
        }
        if (found) {
          SymbolReference<ResolvedReferenceTypeDeclaration> ref = typeSolver.tryToSolveType(qName);
          if (ref != null && ref.isSolved()) {
            return SymbolReference.adapt(ref, ResolvedTypeDeclaration.class);
          }
        }
      }
    }
    if (this.wrappedNode.getPackageDeclaration().isPresent()) {
      String qName = this.wrappedNode.getPackageDeclaration().get().getNameAsString() + "." + name;
      SymbolReference<ResolvedReferenceTypeDeclaration> ref = typeSolver.tryToSolveType(qName);
      if (ref != null && ref.isSolved()) {
        return SymbolReference.adapt(ref, ResolvedTypeDeclaration.class);
      }
    } else {
      String qName = name;
      SymbolReference<ResolvedReferenceTypeDeclaration> ref = typeSolver.tryToSolveType(qName);
      if (ref != null && ref.isSolved()) {
        return SymbolReference.adapt(ref, ResolvedTypeDeclaration.class);
      }
    }
    for (ImportDeclaration importDecl : wrappedNode.getImports()) {
      if (importDecl.isAsterisk()) {
        String qName = importDecl.getNameAsString() + "." + name;
        SymbolReference<ResolvedReferenceTypeDeclaration> ref = typeSolver.tryToSolveType(qName);
        if (ref != null && ref.isSolved()) {
          return SymbolReference.adapt(ref, ResolvedTypeDeclaration.class);
        }
      }
    }
    SymbolReference<ResolvedReferenceTypeDeclaration> ref = typeSolver.tryToSolveType(DEFAULT_PACKAGE + "." + name);
    if (ref != null && ref.isSolved()) {
      return SymbolReference.adapt(ref, ResolvedTypeDeclaration.class);
    }
    if (isQualifiedName(name)) {
      return SymbolReference.adapt(typeSolver.tryToSolveType(name), ResolvedTypeDeclaration.class);
    } else {
      return SymbolReference.unsolved();
    }
  }

  private String qName(ClassOrInterfaceType type) {
    if (type.getScope().isPresent()) {
      return qName(type.getScope().get()) + "." + type.getName().getId();
    } else {
      return type.getName().getId();
    }
  }

  private String qName(Name name) {
    if (name.getQualifier().isPresent()) {
      return qName(name.getQualifier().get()) + "." + name.getId();
    } else {
      return name.getId();
    }
  }

  private String toSimpleName(String qName) {
    String[] parts = qName.split("\\.");
    return parts[parts.length - 1];
  }

  private String packageName(String qName) {
    int lastDot = qName.lastIndexOf('.');
    if (lastDot == -1) {
      throw new UnsupportedOperationException();
    } else {
      return qName.substring(0, lastDot);
    }
  }

  @Override public SymbolReference<ResolvedMethodDeclaration> solveMethod(String name, List<ResolvedType> argumentsTypes, boolean staticOnly) {
    for (ImportDeclaration importDecl : wrappedNode.getImports()) {
      if (importDecl.isStatic()) {
        if (importDecl.isAsterisk()) {
          String importString = importDecl.getNameAsString();
          if (this.wrappedNode.getPackageDeclaration().isPresent() && this.wrappedNode.getPackageDeclaration().get().getName().getIdentifier().equals(packageName(importString)) && this.wrappedNode.getTypes().stream().anyMatch((it) -> it.getName().getIdentifier().equals(toSimpleName(importString)))) {
            return SymbolReference.unsolved();
          }
          ResolvedTypeDeclaration ref = typeSolver.solveType(importString);
          if (!isAncestorOf(ref)) {
            SymbolReference<ResolvedMethodDeclaration> method = MethodResolutionLogic.solveMethodInType(ref, name, argumentsTypes, true);
            if (method.isSolved()) {
              return method;
            }
          }
        } else {
          String qName = importDecl.getNameAsString();
          if (qName.equals(name) || qName.endsWith("." + name)) {
            String typeName = getType(qName);
            ResolvedTypeDeclaration ref = typeSolver.solveType(typeName);
            SymbolReference<ResolvedMethodDeclaration> method = MethodResolutionLogic.solveMethodInType(ref, name, argumentsTypes, true);
            if (method.isSolved()) {
              return method;
            } else {
              return SymbolReference.unsolved();
            }
          }
        }
      }
    }
    return SymbolReference.unsolved();
  }

  @Override public List<ResolvedFieldDeclaration> fieldsExposedToChild(Node child) {
    List<ResolvedFieldDeclaration> res = new LinkedList<>();
    for (ImportDeclaration importDeclaration : wrappedNode.getImports()) {
      if (importDeclaration.isStatic()) {
        Name typeNameAsNode = importDeclaration.isAsterisk() ? importDeclaration.getName() : importDeclaration.getName().getQualifier().get();
        String typeName = typeNameAsNode.asString();
        ResolvedReferenceTypeDeclaration typeDeclaration = typeSolver.solveType(typeName);
        res.addAll(typeDeclaration.getAllFields().stream().filter((f) -> f.isStatic()).filter((f) -> importDeclaration.isAsterisk() || importDeclaration.getName().getIdentifier().equals(f.getName())).collect(Collectors.toList()));
      }
    }
    return res;
  }

  private String getType(String qName) {
    int index = qName.lastIndexOf('.');
    if (index == -1) {
      throw new UnsupportedOperationException();
    }
    String typeName = qName.substring(0, index);
    return typeName;
  }

  private String getMember(String qName) {
    int index = qName.lastIndexOf('.');
    if (index == -1) {
      throw new UnsupportedOperationException();
    }
    String memberName = qName.substring(index + 1);
    return memberName;
  }

  private boolean isAncestorOf(ResolvedTypeDeclaration descendant) {
    return descendant.toAst().filter((node) -> wrappedNode.isAncestorOf(node)).isPresent();
  }
}