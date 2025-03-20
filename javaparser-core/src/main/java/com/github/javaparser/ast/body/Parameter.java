package com.github.javaparser.ast.body;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithFinalModifier;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.ParameterMetaModel;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;

/**
 * The parameters to a method or lambda. Lambda parameters may have inferred types, in that case "type" is UnknownType.
 * <br>Note that <a href="https://en.wikipedia.org/wiki/Parameter_(computer_programming)#Parameters_and_arguments">parameters
 * are different from arguments.</a> <br>"String x" and "float y" are the parameters in {@code int abc(String x, float
 * y) {...}}
 *
 * <br>All annotations preceding the type will be set on this object, not on the type.
 * JavaParser doesn't know if it they are applicable to the parameter or the type.
 *
 * @author Julio Vilmar Gesser
 */
public class Parameter extends Node implements NodeWithType<Parameter, Type>, NodeWithAnnotations<Parameter>, NodeWithSimpleName<Parameter>, NodeWithFinalModifier<Parameter>, Resolvable<ResolvedParameterDeclaration> {
  private Type type;

  private boolean isVarArgs;

  private NodeList<AnnotationExpr> varArgsAnnotations;

  private NodeList<Modifier> modifiers;

  private NodeList<AnnotationExpr> annotations;

  private SimpleName name;

  public Parameter() {
    this(null, new NodeList<>(), new NodeList<>(), new ClassOrInterfaceType(), false, new NodeList<>(), new SimpleName());
  }

  public Parameter(Type type, SimpleName name) {
    this(null, new NodeList<>(), new NodeList<>(), type, false, new NodeList<>(), name);
  }

  /**
     * Creates a new {@link Parameter}.
     *
     * @param type type of the parameter
     * @param name name of the parameter
     */
  public Parameter(Type type, String name) {
    this(null, new NodeList<>(), new NodeList<>(), type, false, new NodeList<>(), new SimpleName(name));
  }

  public Parameter(NodeList<Modifier> modifiers, Type type, SimpleName name) {
    this(null, modifiers, new NodeList<>(), type, false, new NodeList<>(), name);
  }

  @AllFieldsConstructor public Parameter(NodeList<Modifier> modifiers, NodeList<AnnotationExpr> annotations, Type type, boolean isVarArgs, NodeList<AnnotationExpr> varArgsAnnotations, SimpleName name) {
    this(null, modifiers, annotations, type, isVarArgs, varArgsAnnotations, name);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public Parameter(TokenRange tokenRange, NodeList<Modifier> modifiers, NodeList<AnnotationExpr> annotations, Type type, boolean isVarArgs, NodeList<AnnotationExpr> varArgsAnnotations, SimpleName name) {
    super(tokenRange);
    setModifiers(modifiers);
    setAnnotations(annotations);
    setType(type);
    setVarArgs(isVarArgs);
    setVarArgsAnnotations(varArgsAnnotations);
    setName(name);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Type getType() {
    return type;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public boolean isVarArgs() {
    return isVarArgs;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Parameter setType(final Type type) {
    assertNotNull(type);
    if (type == this.type) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.TYPE, this.type, type);
    if (this.type != null) {
      this.type.setParentNode(null);
    }
    this.type = type;
    setAsParentNodeOf(type);
    return this;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Parameter setVarArgs(final boolean isVarArgs) {
    if (isVarArgs == this.isVarArgs) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.VAR_ARGS, this.isVarArgs, isVarArgs);
    this.isVarArgs = isVarArgs;
    return this;
  }

  /**
     * @return the list returned could be immutable (in that case it will be empty)
     */
  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public NodeList<AnnotationExpr> getAnnotations() {
    return annotations;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public SimpleName getName() {
    return name;
  }

  /**
     * Return the modifiers of this parameter declaration.
     *
     * @return modifiers
     * @see Modifier
     */
  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public NodeList<Modifier> getModifiers() {
    return modifiers;
  }

  /**
     * @param annotations a null value is currently treated as an empty list. This behavior could change in the future,
     * so please avoid passing null
     */
  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Parameter setAnnotations(final NodeList<AnnotationExpr> annotations) {
    assertNotNull(annotations);
    if (annotations == this.annotations) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.ANNOTATIONS, this.annotations, annotations);
    if (this.annotations != null) {
      this.annotations.setParentNode(null);
    }
    this.annotations = annotations;
    setAsParentNodeOf(annotations);
    return this;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Parameter setName(final SimpleName name) {
    assertNotNull(name);
    if (name == this.name) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.NAME, this.name, name);
    if (this.name != null) {
      this.name.setParentNode(null);
    }
    this.name = name;
    setAsParentNodeOf(name);
    return this;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Parameter setModifiers(final NodeList<Modifier> modifiers) {
    assertNotNull(modifiers);
    if (modifiers == this.modifiers) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.MODIFIERS, this.modifiers, modifiers);
    if (this.modifiers != null) {
      this.modifiers.setParentNode(null);
    }
    this.modifiers = modifiers;
    setAsParentNodeOf(modifiers);
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    for (int i = 0; i < annotations.size(); i++) {
      if (annotations.get(i) == node) {
        annotations.remove(i);
        return true;
      }
    }
    for (int i = 0; i < modifiers.size(); i++) {
      if (modifiers.get(i) == node) {
        modifiers.remove(i);
        return true;
      }
    }
    for (int i = 0; i < varArgsAnnotations.size(); i++) {
      if (varArgsAnnotations.get(i) == node) {
        varArgsAnnotations.remove(i);
        return true;
      }
    }
    return super.remove(node);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public NodeList<AnnotationExpr> getVarArgsAnnotations() {
    return varArgsAnnotations;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Parameter setVarArgsAnnotations(final NodeList<AnnotationExpr> varArgsAnnotations) {
    assertNotNull(varArgsAnnotations);
    if (varArgsAnnotations == this.varArgsAnnotations) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.VAR_ARGS_ANNOTATIONS, this.varArgsAnnotations, varArgsAnnotations);
    if (this.varArgsAnnotations != null) {
      this.varArgsAnnotations.setParentNode(null);
    }
    this.varArgsAnnotations = varArgsAnnotations;
    setAsParentNodeOf(varArgsAnnotations);
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public Parameter clone() {
    return (Parameter) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public ParameterMetaModel getMetaModel() {
    return JavaParserMetaModel.parameterMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    for (int i = 0; i < annotations.size(); i++) {
      if (annotations.get(i) == node) {
        annotations.set(i, (AnnotationExpr) replacementNode);
        return true;
      }
    }
    for (int i = 0; i < modifiers.size(); i++) {
      if (modifiers.get(i) == node) {
        modifiers.set(i, (Modifier) replacementNode);
        return true;
      }
    }
    if (node == name) {
      setName((SimpleName) replacementNode);
      return true;
    }
    if (node == type) {
      setType((Type) replacementNode);
      return true;
    }
    for (int i = 0; i < varArgsAnnotations.size(); i++) {
      if (varArgsAnnotations.get(i) == node) {
        varArgsAnnotations.set(i, (AnnotationExpr) replacementNode);
        return true;
      }
    }
    return super.replace(node, replacementNode);
  }

  @Override public ResolvedParameterDeclaration resolve() {
    return getSymbolResolver().resolveDeclaration(this, ResolvedParameterDeclaration.class);
  }

  /**
     * Record components are implicitly final, even without the explicit modifier.
     * https://openjdk.java.net/jeps/359#Restrictions-on-records
     * @return If the parent is present and it is a record declaration, return true - otherwise use default method implementation.
     */
  @Override public boolean isFinal() {
    if (getParentNode().isPresent()) {
      Node parentNode = getParentNode().get();
      if (parentNode instanceof RecordDeclaration) {
        return true;
      }
    }
    return NodeWithFinalModifier.super.isFinal();
  }
}