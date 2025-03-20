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
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.UnknownTypeMetaModel;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

/**
 * An unknown parameter type object. It plays the role of a null object for
 * lambda parameters that have no explicit type declared. As such, it has no
 * lexical representation and hence gets no comment attributed.
 * <p>
 * <br>In {@code DoubleToIntFunction d = }<b>{@code x}</b> {@code -> (int)x + 1;} the x parameter in bold has type UnknownType.
 *
 * @author Didier Villevalois
 */
public class UnknownType extends Type {
  @AllFieldsConstructor public UnknownType() {
    this(null);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public UnknownType(TokenRange tokenRange) {
    super(tokenRange);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Override public UnknownType setAnnotations(NodeList<AnnotationExpr> annotations) {
    if (annotations.size() > 0) {
      throw new IllegalStateException("Inferred lambda types cannot be annotated.");
    }
    return (UnknownType) super.setAnnotations(annotations);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override public String asString() {
    return "";
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public UnknownType clone() {
    return (UnknownType) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public UnknownTypeMetaModel getMetaModel() {
    return JavaParserMetaModel.unknownTypeMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    return super.replace(node, replacementNode);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isUnknownType() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public UnknownType asUnknownType() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifUnknownType(Consumer<UnknownType> action) {
    action.accept(this);
  }

  @Override public ResolvedType resolve() {
    return getSymbolResolver().toResolvedType(this, ResolvedReferenceType.class);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<UnknownType> toUnknownType() {
    return Optional.of(this);
  }

  @Override public boolean isPhantom() {
    return true;
  }
}