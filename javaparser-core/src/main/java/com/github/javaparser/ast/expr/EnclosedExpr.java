package com.github.javaparser.ast.expr;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.TokenRange;
import java.util.Optional;
import com.github.javaparser.ast.AllFieldsConstructor;
import java.util.function.Consumer;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.EnclosedExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;

/**
 * An expression between ( ).
 * <br>{@code (1+1)}
 *
 * @author Julio Vilmar Gesser
 */
public class EnclosedExpr extends Expression {
  private Expression inner;

  public EnclosedExpr() {
    this(null, new StringLiteralExpr());
  }

  @AllFieldsConstructor public EnclosedExpr(final Expression inner) {
    this(null, inner);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public EnclosedExpr(TokenRange tokenRange, Expression inner) {
    super(tokenRange);
    setInner(inner);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Expression getInner() {
    return inner;
  }

  /**
     * Sets the inner expression
     *
     * @param inner the inner expression, can be null
     * @return this, the EnclosedExpr
     */
  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public EnclosedExpr setInner(final Expression inner) {
    assertNotNull(inner);
    if (inner == this.inner) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.INNER, this.inner, inner);
    if (this.inner != null) {
      this.inner.setParentNode(null);
    }
    this.inner = inner;
    setAsParentNodeOf(inner);
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public EnclosedExpr clone() {
    return (EnclosedExpr) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public EnclosedExprMetaModel getMetaModel() {
    return JavaParserMetaModel.enclosedExprMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    if (node == inner) {
      setInner((Expression) replacementNode);
      return true;
    }
    return super.replace(node, replacementNode);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isEnclosedExpr() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public EnclosedExpr asEnclosedExpr() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifEnclosedExpr(Consumer<EnclosedExpr> action) {
    action.accept(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<EnclosedExpr> toEnclosedExpr() {
    return Optional.of(this);
  }

  @Override public boolean isPolyExpression() {
    return getInner().isPolyExpression();
  }
}