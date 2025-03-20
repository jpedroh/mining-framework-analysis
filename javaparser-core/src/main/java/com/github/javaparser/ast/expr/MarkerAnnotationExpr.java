package com.github.javaparser.ast.expr;
import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.MarkerAnnotationExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import static com.github.javaparser.JavaParser.parseName;

/**
 * An annotation that uses only the annotation type name.
 * <br/><code>@Override</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class MarkerAnnotationExpr extends AnnotationExpr {
  public MarkerAnnotationExpr() {
    this(null, new Name());
  }

  public MarkerAnnotationExpr(final String name) {
    this(null, parseName(name));
  }

  @AllFieldsConstructor public MarkerAnnotationExpr(final Name name) {
    this(null, name);
  }

  public MarkerAnnotationExpr(final Range range, final Name name) {
    super(range, name);
  }

  @Override public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Override public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override public MarkerAnnotationExpr clone() {
    return (MarkerAnnotationExpr) accept(new CloneVisitor(), null);
  }

  @Override public MarkerAnnotationExprMetaModel getMetaModel() {
    return JavaParserMetaModel.markerAnnotationExprMetaModel;
  }
}