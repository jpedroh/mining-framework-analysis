package com.github.javaparser.symbolsolver.javassistmodel;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.model.typesystem.ReferenceTypeImpl;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.SignatureAttribute;
import java.util.*;
import java.util.stream.Collectors;
import javassist.CtField;

public class JavassistTypeDeclarationAdapter {
  private final CtClass ctClass;

  private final TypeSolver typeSolver;

  public Set<ResolvedMethodDeclaration> getDeclaredMethods() {
    return Arrays.stream(ctClass.getDeclaredMethods()).filter((m) -> ((m.getMethodInfo().getAccessFlags() & AccessFlag.BRIDGE) == 0) && ((m.getMethodInfo().getAccessFlags() & AccessFlag.SYNTHETIC) == 0)).map((m) -> new JavassistMethodDeclaration(m, typeSolver)).collect(Collectors.toSet());
  }

  public List<ResolvedConstructorDeclaration> getConstructors() {
    return Arrays.stream(ctClass.getConstructors()).filter((m) -> (m.getMethodInfo().getAccessFlags() & AccessFlag.SYNTHETIC) == 0).map((m) -> new JavassistConstructorDeclaration(m, typeSolver)).collect(Collectors.toList());
  }

  public List<ResolvedFieldDeclaration> getDeclaredFields() {
    List<ResolvedFieldDeclaration> fields = new ArrayList<>();
    for (CtField field : ctClass.getDeclaredFields()) {
      fields.add(new JavassistFieldDeclaration(field, typeSolver));
    }
    for (ResolvedReferenceType ancestor : typeDeclaration.getAllAncestors()) {
      ancestor.getTypeDeclaration().ifPresent((ancestorTypeDeclaration) -> {
        fields.addAll(ancestorTypeDeclaration.getAllFields());
      });
    }
    return fields;
  }

  public List<ResolvedTypeParameterDeclaration> getTypeParameters() {
    if (null == ctClass.getGenericSignature()) {
      return Collections.emptyList();
    } else {
      try {
        SignatureAttribute.ClassSignature classSignature = SignatureAttribute.toClassSignature(ctClass.getGenericSignature());
        return Arrays.stream(classSignature.getParameters()).map((tp) -> new JavassistTypeParameter(tp, JavassistFactory.toTypeDeclaration(ctClass, typeSolver), typeSolver)).collect(Collectors.toList());
      } catch (BadBytecode badBytecode) {
        throw new RuntimeException(badBytecode);
      }
    }
  }

  public Optional<ResolvedReferenceTypeDeclaration> containerType() {
    try {
      return ctClass.getDeclaringClass() == null ? Optional.empty() : Optional.of(JavassistFactory.toTypeDeclaration(ctClass.getDeclaringClass(), typeSolver));
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public List<ResolvedReferenceType> getAncestors(ResolvedReferenceTypeDeclaration referenceTypeDeclaration, boolean acceptIncompleteList) {
    List<ResolvedReferenceType> ancestors = new ArrayList<>();
    if (ctClass.getGenericSignature() == null) {
      ClassFile classFile = ctClass.getClassFile();
      try {
        ResolvedReferenceTypeDeclaration superType = typeSolver.solveType(JavassistUtils.internalNameToCanonicalName(classFile.getSuperclass()));
        ancestors.add(new ReferenceTypeImpl(superType, typeSolver));
      } catch (UnsolvedSymbolException e) {
        if (!acceptIncompleteList) {
          throw e;
        }
      }
      for (String superInterface : classFile.getInterfaces()) {
        try {
          ancestors.add(new ReferenceTypeImpl(typeSolver.solveType(JavassistUtils.internalNameToCanonicalName(superInterface)), typeSolver));
        } catch (UnsolvedSymbolException e) {
          if (!acceptIncompleteList) {
            throw e;
          }
        }
      }
    } else {
      try {
        SignatureAttribute.ClassSignature classSignature = SignatureAttribute.toClassSignature(ctClass.getGenericSignature());
        try {
          ancestors.add(JavassistUtils.signatureTypeToType(classSignature.getSuperClass(), typeSolver, referenceTypeDeclaration).asReferenceType());
        } catch (UnsolvedSymbolException e) {
          if (!acceptIncompleteList) {
            throw e;
          }
        }
        for (SignatureAttribute.ClassType superInterface : classSignature.getInterfaces()) {
          try {
            ancestors.add(JavassistUtils.signatureTypeToType(superInterface, typeSolver, referenceTypeDeclaration).asReferenceType());
          } catch (UnsolvedSymbolException e) {
            if (!acceptIncompleteList) {
              throw e;
            }
          }
        }
      } catch (BadBytecode e) {
        throw new RuntimeException(e);
      }
    }
    return ancestors;
  }

  private ResolvedReferenceTypeDeclaration typeDeclaration;

  public JavassistTypeDeclarationAdapter(CtClass ctClass, TypeSolver typeSolver, ResolvedReferenceTypeDeclaration typeDeclaration) {
    this.ctClass = ctClass;
    this.typeSolver = typeSolver;
    this.typeDeclaration = typeDeclaration;
  }

  public Optional<ResolvedReferenceType> getSuperClass() {
    try {
      if ("java.lang.Object".equals(ctClass.getClassFile().getName())) {
        return Optional.empty();
      }
      if (ctClass.getGenericSignature() == null) {
        return Optional.of(new ReferenceTypeImpl(typeSolver.solveType(JavassistUtils.internalNameToCanonicalName(ctClass.getClassFile().getSuperclass())), typeSolver));
      } else {
        SignatureAttribute.ClassSignature classSignature = SignatureAttribute.toClassSignature(ctClass.getGenericSignature());
        return Optional.ofNullable(JavassistUtils.signatureTypeToType(classSignature.getSuperClass(), typeSolver, typeDeclaration).asReferenceType());
      }
    } catch (BadBytecode e) {
      throw new RuntimeException(e);
    }
  }

  public List<ResolvedReferenceType> getInterfaces() {
    return getInterfaces(false);
  }

  private List<ResolvedReferenceType> getInterfaces(boolean acceptIncompleteList) {
    List<ResolvedReferenceType> interfaces = new ArrayList<>();
    try {
      if (ctClass.getGenericSignature() == null) {
        for (String interfaceType : ctClass.getClassFile().getInterfaces()) {
          try {
            ResolvedReferenceTypeDeclaration declaration = typeSolver.solveType(JavassistUtils.internalNameToCanonicalName(interfaceType));
            interfaces.add(new ReferenceTypeImpl(declaration, typeSolver));
          } catch (UnsolvedSymbolException e) {
            if (!acceptIncompleteList) {
              throw e;
            }
          }
        }
      } else {
        SignatureAttribute.ClassSignature classSignature = SignatureAttribute.toClassSignature(ctClass.getGenericSignature());
        for (SignatureAttribute.ClassType interfaceType : classSignature.getInterfaces()) {
          try {
            interfaces.add(JavassistUtils.signatureTypeToType(interfaceType, typeSolver, typeDeclaration).asReferenceType());
          } catch (UnsolvedSymbolException e) {
            if (!acceptIncompleteList) {
              throw e;
            }
          }
        }
      }
    } catch (BadBytecode e) {
      throw new RuntimeException(e);
    }
    return interfaces;
  }

  public List<ResolvedReferenceType> getAncestors(boolean acceptIncompleteList) {
    List<ResolvedReferenceType> ancestors = new ArrayList<>();
    try {
      getSuperClass().ifPresent((superClass) -> ancestors.add(superClass));
    } catch (UnsolvedSymbolException e) {
      if (!acceptIncompleteList) {
        throw e;
      }
    }
    ancestors.addAll(getInterfaces(acceptIncompleteList));
    return ancestors;
  }
}