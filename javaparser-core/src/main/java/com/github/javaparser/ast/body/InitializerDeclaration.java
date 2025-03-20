package com.github.javaparser.ast.body;
import com.github.javaparser.Range;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.nodeTypes.NodeWithBlockStmt;
import com.github.javaparser.ast.nodeTypes.NodeWithJavaDoc;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.6">JLS</a>
 * A (possibly static) initializer body. "static { a=3; }" in this example: <code>class X { static { a=3; }  } </code> 
 * @author Julio Vilmar Gesser
 */
public final class InitializerDeclaration extends BodyDeclaration<InitializerDeclaration> implements NodeWithJavaDoc<InitializerDeclaration>, NodeWithBlockStmt<InitializerDeclaration> {
  private boolean isStatic;

  private BlockStmt body;

  public InitializerDeclaration() {
    this(null, false, new BlockStmt());
  }

  public InitializerDeclaration(boolean isStatic, BlockStmt body) {
    this(null, isStatic, body);
  }

  public InitializerDeclaration(Range range, boolean isStatic, BlockStmt body) {
    super(range, new NodeList<>());
    setStatic(isStatic);
    setBody(body);
  }

  @Override public <R extends java.lang.Object, A extends java.lang.Object> R accept(GenericVisitor<R, A> v, A arg) {
    return v.visit(this, arg);
  }

  @Override public <A extends java.lang.Object> void accept(VoidVisitor<A> v, A arg) {
    v.visit(this, arg);
  }

  public BlockStmt getBody() {
    return body;
  }

  public boolean isStatic() {
    return isStatic;
  }

  public InitializerDeclaration setBody(BlockStmt body) {
    notifyPropertyChange(ObservableProperty.BLOCK, this.body, body);
    this.body = assertNotNull(body);
    setAsParentNodeOf(this.body);
    return this;
  }

  public InitializerDeclaration setStatic(boolean isStatic) {
    notifyPropertyChange(ObservableProperty.STATIC, this.isStatic, isStatic);
    this.isStatic = isStatic;
    return this;
  }

  @Override public JavadocComment getJavaDoc() {
    if (getComment() instanceof JavadocComment) {
      return (JavadocComment) getComment();
    }
    return null;
  }
}