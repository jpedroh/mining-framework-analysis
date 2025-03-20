package com.github.javaparser.ast.type;
import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.IntersectionTypeMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import java.util.Arrays;
import com.github.javaparser.metamodel.NonEmptyProperty;
import java.util.List;
import static com.github.javaparser.utils.Utils.assertNotNull;
import static java.util.stream.Collectors.joining;
import javax.annotation.Generated;
import com.github.javaparser.TokenRange;

/**
 * Represents a set of types. A given value of this type has to be assignable to at all of the element types.
 * As of Java 8 it is used in casts or while expressing bounds for generic types.
 * <p>
 * For example:
 * <code>public class A&lt;T extends Serializable &amp; Cloneable&gt; { }</code>
 * <p>
 * Or:
 * <code>void foo((Serializable &amp; Cloneable)myObject);</code>
 *
 * @since 3.0.0
 */
public class IntersectionType extends Type implements NodeWithAnnotations<IntersectionType> {
  @NonEmptyProperty private NodeList<ReferenceType> elements;

  @AllFieldsConstructor public IntersectionType(NodeList<ReferenceType> elements) {
    this(null, elements);
  }

  /**This constructor is used by the parser and is considered private.*/
  @Generated(value = { "com.github.javaparser.generator.core.node.MainConstructorGenerator" }) public IntersectionType(TokenRange tokenRange, NodeList<ReferenceType> elements) {
    super(tokenRange);
    setElements(elements);
    customInitialization();
  }

  @Override public <R extends java.lang.Object, A extends java.lang.Object> R accept(GenericVisitor<R, A> v, A arg) {
    return v.visit(this, arg);
  }

  @Override public <A extends java.lang.Object> void accept(VoidVisitor<A> v, A arg) {
    v.visit(this, arg);
  }

  @Generated(value = { "com.github.javaparser.generator.core.node.PropertyGenerator" }) public NodeList<ReferenceType> getElements() {
    return elements;
  }

  @Generated(value = { "com.github.javaparser.generator.core.node.PropertyGenerator" }) public IntersectionType setElements(final NodeList<ReferenceType> elements) {
    assertNotNull(elements);
    if (elements == this.elements) {
      return (IntersectionType) this;
    }
    notifyPropertyChange(ObservableProperty.ELEMENTS, this.elements, elements);
    if (this.elements != null) {
      this.elements.setParentNode(null);
    }
    this.elements = elements;
    setAsParentNodeOf(elements);
    return this;
  }

  @Override public IntersectionType setAnnotations(NodeList<AnnotationExpr> annotations) {
    return (IntersectionType) super.setAnnotations(annotations);
  }

  @Override @Generated(value = { "com.github.javaparser.generator.core.node.GetNodeListsGenerator" }) public List<NodeList<?>> getNodeLists() {
    return Arrays.asList(getElements(), getAnnotations());
  }

  @Override @Generated(value = { "com.github.javaparser.generator.core.node.RemoveMethodGenerator" }) public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    for (int i = 0; i < elements.size(); i++) {
      if (elements.get(i) == node) {
        elements.remove(i);
        return true;
      }
    }
    return super.remove(node);
  }

  @Override public String asString() {
    return elements.stream().map(Type::asString).collect(joining("&"));
  }

  @Override @Generated(value = { "com.github.javaparser.generator.core.node.CloneGenerator" }) public IntersectionType clone() {
    return (IntersectionType) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = { "com.github.javaparser.generator.core.node.GetMetaModelGenerator" }) public IntersectionTypeMetaModel getMetaModel() {
    return JavaParserMetaModel.intersectionTypeMetaModel;
  }
}