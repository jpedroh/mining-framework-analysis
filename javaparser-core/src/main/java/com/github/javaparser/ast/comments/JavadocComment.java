package com.github.javaparser.ast.comments;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.JavadocCommentMetaModel;
import java.util.Optional;
import java.util.function.Consumer;
import static com.github.javaparser.StaticJavaParser.parseJavadoc;

/**
 * A Javadoc comment. {@code /&#42;&#42; a comment &#42;/}
 *
 * @author Julio Vilmar Gesser
 */
public class JavadocComment extends Comment {
  public JavadocComment() {
    this(null, "empty");
  }

  @AllFieldsConstructor public JavadocComment(String content) {
    this(null, content);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public JavadocComment(TokenRange tokenRange, String content) {
    super(tokenRange, content);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  public Javadoc parse() {
    return parseJavadoc(getContent());
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public JavadocComment clone() {
    return (JavadocComment) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public JavadocCommentMetaModel getMetaModel() {
    return JavaParserMetaModel.javadocCommentMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isJavadocComment() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public JavadocComment asJavadocComment() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifJavadocComment(Consumer<JavadocComment> action) {
    action.accept(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<JavadocComment> toJavadocComment() {
    return Optional.of(this);
  }
}