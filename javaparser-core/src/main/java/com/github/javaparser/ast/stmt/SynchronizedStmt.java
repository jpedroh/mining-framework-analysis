package com.github.javaparser.ast.stmt;
import com.github.javaparser.Range;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBlockStmt;
import com.github.javaparser.ast.nodeTypes.NodeWithExpression;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * @author Julio Vilmar Gesser
 */
public final class SynchronizedStmt extends Statement implements NodeWithBlockStmt<SynchronizedStmt>, NodeWithExpression<SynchronizedStmt> {
  private Expression expression;

  private BlockStmt body;

  public SynchronizedStmt() {
    this(null, new NameExpr(), new BlockStmt());
  }

  public SynchronizedStmt(final Expression expression, final BlockStmt body) {
    this(null, expression, body);
  }

  public SynchronizedStmt(Range range, final Expression expression, final BlockStmt body) {
    super(range);
    setExpression(expression);
    setBody(body);
  }

  @Override public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  public Expression getExpression() {
    return expression;
  }

  public SynchronizedStmt setExpression(final Expression expression) {
    notifyPropertyChange(ObservableProperty.EXPRESSION, this.expression, expression);
    this.expression = assertNotNull(expression);
    setAsParentNodeOf(this.expression);
    return this;
  }

  @Override public BlockStmt getBody() {
    return body;
  }

  @Override public SynchronizedStmt setBody(BlockStmt body) {
    notifyPropertyChange(ObservableProperty.BODY, this.body, body);
    this.body = assertNotNull(body);
    setAsParentNodeOf(this.body);
    return this;
  }
}