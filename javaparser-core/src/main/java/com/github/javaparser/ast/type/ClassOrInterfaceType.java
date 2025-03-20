package com.github.javaparser.ast.type;
import java.util.List;
import com.github.javaparser.Range;
import com.github.javaparser.ast.TypeArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class ClassOrInterfaceType extends ReferenceType<ClassOrInterfaceType> implements NodeWithName<ClassOrInterfaceType>, NodeWithAnnotations<ClassOrInterfaceType> {
  private ClassOrInterfaceType scope;

  private String name;

  private TypeArguments typeArguments = TypeArguments.EMPTY;

  public ClassOrInterfaceType() {
  }

  public ClassOrInterfaceType(final String name) {
    setName(name);
  }

  public ClassOrInterfaceType(final ClassOrInterfaceType scope, final String name) {
    setScope(scope);
    setName(name);
  }

  public ClassOrInterfaceType(final Range range, final ClassOrInterfaceType scope, final String name, final TypeArguments typeArguments) {
    super(range);
    setScope(scope);
    setName(name);
    setTypeArguments(typeArguments);
  }

  @Override public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Override public String getName() {
    return name;
  }

  public ClassOrInterfaceType getScope() {
    return scope;
  }

  public List<Type<?>> getTypeArgs() {
    return typeArguments.getTypeArguments();
  }

  public TypeArguments getTypeArguments() {
    return typeArguments;
  }

  public boolean isUsingDiamondOperator() {
    return typeArguments.isUsingDiamondOperator();
  }

  public boolean isBoxedType() {
    return PrimitiveType.unboxMap.containsKey(name);
  }

  public PrimitiveType toUnboxedType() throws UnsupportedOperationException {
    if (!isBoxedType()) {
      throw new UnsupportedOperationException(name + " isn\'t a boxed type.");
    }
    return new PrimitiveType(PrimitiveType.unboxMap.get(name));
  }

  @Override public ClassOrInterfaceType setName(final String name) {
    this.name = name;
    return this;
  }

  public void setScope(final ClassOrInterfaceType scope) {
    this.scope = scope;
    setAsParentNodeOf(this.scope);
  }

  /**
     * Allows you to set the generic arguments
     * @param typeArgs The list of types of the generics
     */
  public void setTypeArgs(final List<Type<?>> typeArgs) {
    setTypeArguments(TypeArguments.withArguments(typeArgs));
  }

  public void setTypeArguments(TypeArguments typeArguments) {
    this.typeArguments = typeArguments;
    setAsParentNodeOf(this.typeArguments.getTypeArguments());
  }
}