package com.github.javaparser.ast.stmt;
import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithCondition;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.WhileStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import javax.annotation.Generated;
import com.github.javaparser.TokenRange;

/**
 * A while statement.
 * <br/><code>while(true) { ... }</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class WhileStmt extends Statement implements NodeWithBody<WhileStmt>, NodeWithCondition<WhileStmt> {
  private Expression condition;

  private Statement body;

  public WhileStmt() {
    this(null, new BooleanLiteralExpr(), new ReturnStmt());
  }

  @AllFieldsConstructor public WhileStmt(final Expression condition, final Statement body) {
    this(null, condition, body);
  }

  /**This constructor is used by the parser and is considered private.*/
  @Generated(value = { "com.github.javaparser.generator.core.node.MainConstructorGenerator" }) public WhileStmt(TokenRange tokenRange, Expression condition, Statement body) {
    super(tokenRange);
    setCondition(condition);
    setBody(body);
    customInitialization();
  }

  @Override public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Generated(value = { "com.github.javaparser.generator.core.node.PropertyGenerator" }) public Statement getBody() {
    return body;
  }

  @Generated(value = { "com.github.javaparser.generator.core.node.PropertyGenerator" }) public Expression getCondition() {
    return condition;
  }

  @Generated(value = { "com.github.javaparser.generator.core.node.PropertyGenerator" }) public WhileStmt setBody(final Statement body) {
    assertNotNull(body);
    if (body == this.body) {
      return (WhileStmt) this;
    }
    notifyPropertyChange(ObservableProperty.BODY, this.body, body);
    if (this.body != null) {
      this.body.setParentNode(null);
    }
    this.body = body;
    setAsParentNodeOf(body);
    return this;
  }

  @Generated(value = { "com.github.javaparser.generator.core.node.PropertyGenerator" }) public WhileStmt setCondition(final Expression condition) {
    assertNotNull(condition);
    if (condition == this.condition) {
      return (WhileStmt) this;
    }
    notifyPropertyChange(ObservableProperty.CONDITION, this.condition, condition);
    if (this.condition != null) {
      this.condition.setParentNode(null);
    }
    this.condition = condition;
    setAsParentNodeOf(condition);
    return this;
  }

  @Override @Generated(value = { "com.github.javaparser.generator.core.node.RemoveMethodGenerator" }) public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override @Generated(value = { "com.github.javaparser.generator.core.node.CloneGenerator" }) public WhileStmt clone() {
    return (WhileStmt) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = { "com.github.javaparser.generator.core.node.GetMetaModelGenerator" }) public WhileStmtMetaModel getMetaModel() {
    return JavaParserMetaModel.whileStmtMetaModel;
  }
}