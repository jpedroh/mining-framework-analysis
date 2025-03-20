package com.github.javaparser.ast.type;
import java.util.Optional;
import com.github.javaparser.TokenRange;
import java.util.function.Consumer;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.ReferenceTypeMetaModel;

/**
 * Base class for reference types.
 *
 * @author Julio Vilmar Gesser
 */
public abstract class ReferenceType extends Type {
  public ReferenceType() {
    this(null, new NodeList<>());
  }

  @AllFieldsConstructor public ReferenceType(NodeList<AnnotationExpr> annotations) {
    this(null, annotations);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public ReferenceType(TokenRange tokenRange, NodeList<AnnotationExpr> annotations) {
    super(tokenRange, annotations);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public ReferenceType clone() {
    return (ReferenceType) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public ReferenceTypeMetaModel getMetaModel() {
    return JavaParserMetaModel.referenceTypeMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    return super.replace(node, replacementNode);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isReferenceType() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ReferenceType asReferenceType() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifReferenceType(Consumer<ReferenceType> action) {
    action.accept(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ReferenceType> toReferenceType() {
    return Optional.of(this);
  }

  public abstract String toDescriptor();
}