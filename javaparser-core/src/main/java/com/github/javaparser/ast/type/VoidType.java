package com.github.javaparser.ast.type;
import java.util.Optional;
import com.github.javaparser.TokenRange;
import java.util.function.Consumer;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.VoidTypeMetaModel;
import com.github.javaparser.resolution.types.ResolvedVoidType;

/**
 * The return type of a {@link com.github.javaparser.ast.body.MethodDeclaration}
 * when it returns void.
 * <br><code><b>void</b> helloWorld() { ... }</code>
 *
 * @author Julio Vilmar Gesser
 */
public class VoidType extends Type implements NodeWithAnnotations<VoidType> {
  @AllFieldsConstructor public VoidType() {
    this(null);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public VoidType(TokenRange tokenRange) {
    super(tokenRange);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Override public VoidType setAnnotations(NodeList<AnnotationExpr> annotations) {
    return (VoidType) super.setAnnotations(annotations);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override public String asString() {
    return "void";
  }

  @Override public String toDescriptor() {
    return "V";
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public VoidType clone() {
    return (VoidType) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public VoidTypeMetaModel getMetaModel() {
    return JavaParserMetaModel.voidTypeMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    return super.replace(node, replacementNode);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isVoidType() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public VoidType asVoidType() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifVoidType(Consumer<VoidType> action) {
    action.accept(this);
  }

  @Override public ResolvedVoidType resolve() {
    return getSymbolResolver().toResolvedType(this, ResolvedVoidType.class);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<VoidType> toVoidType() {
    return Optional.of(this);
  }
}