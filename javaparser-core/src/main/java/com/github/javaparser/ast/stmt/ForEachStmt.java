package com.github.javaparser.ast.stmt;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Optional;
import java.util.function.Consumer;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.metamodel.ForEachStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.TokenRange;
import java.util.function.Consumer;
import java.util.Optional;
import com.github.javaparser.ast.Generated;

/**
 * A for-each statement.
 * <br/><code>for(Object o: objects) { ... }</code>
 * It was introduced in Java 5.
 *
 * @author Julio Vilmar Gesser
 */
public final class ForEachStmt extends Statement implements NodeWithBody<ForEachStmt> {
  private VariableDeclarationExpr variable;

  private Expression iterable;

  private Statement body;

  public ForEachStmt() {
    this(null, new VariableDeclarationExpr(), new NameExpr(), new ReturnStmt());
  }

  @AllFieldsConstructor public ForEachStmt(final VariableDeclarationExpr variable, final Expression iterable, final Statement body) {
    this(null, variable, iterable, body);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public ForEachStmt(TokenRange tokenRange, VariableDeclarationExpr variable, Expression iterable, Statement body) {
    super(tokenRange);
    setVariable(variable);
    setIterable(iterable);
    setBody(body);
    customInitialization();
  }

  public ForEachStmt(VariableDeclarationExpr variable, String iterable, BlockStmt body) {
    this(null, variable, new NameExpr(iterable), body);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Statement getBody() {
    return body;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Expression getIterable() {
    return iterable;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public VariableDeclarationExpr getVariable() {
    return variable;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public ForEachStmt setBody(final Statement body) {
    assertNotNull(body);
    if (body == this.body) {
      return (ForEachStmt) this;
    }
    notifyPropertyChange(ObservableProperty.BODY, this.body, body);
    if (this.body != null) {
      this.body.setParentNode(null);
    }
    this.body = body;
    setAsParentNodeOf(body);
    return this;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public ForEachStmt setIterable(final Expression iterable) {
    assertNotNull(iterable);
    if (iterable == this.iterable) {
      return (ForEachStmt) this;
    }
    notifyPropertyChange(ObservableProperty.ITERABLE, this.iterable, iterable);
    if (this.iterable != null) {
      this.iterable.setParentNode(null);
    }
    this.iterable = iterable;
    setAsParentNodeOf(iterable);
    return this;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public ForEachStmt setVariable(final VariableDeclarationExpr variable) {
    assertNotNull(variable);
    if (variable == this.variable) {
      return (ForEachStmt) this;
    }
    notifyPropertyChange(ObservableProperty.VARIABLE, this.variable, variable);
    if (this.variable != null) {
      this.variable.setParentNode(null);
    }
    this.variable = variable;
    setAsParentNodeOf(variable);
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public ForEachStmt clone() {
    return (ForEachStmt) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    if (node == body) {
      setBody((Statement) replacementNode);
      return true;
    }
    if (node == iterable) {
      setIterable((Expression) replacementNode);
      return true;
    }
    if (node == variable) {
      setVariable((VariableDeclarationExpr) replacementNode);
      return true;
    }
    return super.replace(node, replacementNode);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isForeachStmt() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ForEachStmt asForeachStmt() {
    return this;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifForeachStmt(Consumer<ForEachStmt> action) {
    action.accept(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ForEachStmt> toForeachStmt() {
    return Optional.of(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isForEachStmt() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ForEachStmt asForEachStmt() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ForEachStmt> toForEachStmt() {
    return Optional.of(this);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifForEachStmt(Consumer<ForEachStmt> action) {
    action.accept(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public ForEachStmtMetaModel getMetaModel() {
    return JavaParserMetaModel.forEachStmtMetaModel;
  }
}