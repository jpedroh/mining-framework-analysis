package com.github.javaparser.ast.body;
import com.github.javaparser.Range;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.EnumSet;
import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * A constructor declaration: <code>class X { X() { } }</code> where X(){} is the constructor declaration.
 *
 * @author Julio Vilmar Gesser
 */
public final class ConstructorDeclaration extends CallableDeclaration<ConstructorDeclaration> implements NodeWithBlockStmt<ConstructorDeclaration>, NodeWithModifiers<ConstructorDeclaration>, NodeWithJavadoc<ConstructorDeclaration>, NodeWithDeclaration, NodeWithSimpleName<ConstructorDeclaration>, NodeWithParameters<ConstructorDeclaration>, NodeWithThrownExceptions<ConstructorDeclaration>, NodeWithTypeParameters<ConstructorDeclaration> {
  public ConstructorDeclaration() {
    this(null, EnumSet.noneOf(Modifier.class), new NodeList<>(), new NodeList<>(), new SimpleName(), new NodeList<>(), new NodeList<>(), new BlockStmt());
  }

  public ConstructorDeclaration(EnumSet<Modifier> modifiers, String name) {
    this(null, modifiers, new NodeList<>(), new NodeList<>(), new SimpleName(name), new NodeList<>(), new NodeList<>(), new BlockStmt());
  }

  @AllFieldsConstructor public ConstructorDeclaration(EnumSet<Modifier> modifiers, NodeList<AnnotationExpr> annotations, NodeList<TypeParameter> typeParameters, SimpleName name, NodeList<Parameter> parameters, NodeList<ReferenceType> thrownExceptions, BlockStmt body) {
    this(null, modifiers, annotations, typeParameters, name, parameters, thrownExceptions, body);
  }

  public ConstructorDeclaration(Range range, EnumSet<Modifier> modifiers, NodeList<AnnotationExpr> annotations, NodeList<TypeParameter> typeParameters, SimpleName name, NodeList<Parameter> parameters, NodeList<ReferenceType> thrownExceptions, BlockStmt body) {
    super(range, modifiers, annotations, typeParameters, name, parameters, thrownExceptions, body);
  }

  @Override public <R extends java.lang.Object, A extends java.lang.Object> R accept(GenericVisitor<R, A> v, A arg) {
    return v.visit(this, arg);
  }

  @Override public <A extends java.lang.Object> void accept(VoidVisitor<A> v, A arg) {
    v.visit(this, arg);
  }

  @Override public BlockStmt getBody() {
    return body;
  }

  @Override public ConstructorDeclaration setModifiers(final EnumSet<Modifier> modifiers) {
    return (ConstructorDeclaration) super.setModifiers(modifiers);
  }

  /**
     * Sets the body
     *
     * @param body the body, can not be null
     * @return this, the ConstructorDeclaration
     */
  @Override public ConstructorDeclaration setBody(final BlockStmt body) {
    assertNotNull(body);
    return (ConstructorDeclaration) super.setBody(body);
  }

  @Override public ConstructorDeclaration setName(final SimpleName name) {
    return (ConstructorDeclaration) super.setName(name);
  }

  @Override public ConstructorDeclaration setParameters(final NodeList<Parameter> parameters) {
    return (ConstructorDeclaration) super.setParameters(parameters);
  }

  @Override public ConstructorDeclaration setThrownExceptions(final NodeList<ReferenceType> thrownExceptions) {
    return (ConstructorDeclaration) super.setThrownExceptions(thrownExceptions);
  }

  @Override public ConstructorDeclaration setTypeParameters(final NodeList<TypeParameter> typeParameters) {
    return (ConstructorDeclaration) super.setTypeParameters(typeParameters);
  }

  /**
     * The declaration returned has this schema:
     * <p>
     * [accessSpecifier] className ([paramType [paramName]])
     * [throws exceptionsList]
     */
  @Override public String getDeclarationAsString(boolean includingModifiers, boolean includingThrows, boolean includingParameterName) {
    StringBuilder sb = new StringBuilder();
    if (includingModifiers) {
      AccessSpecifier accessSpecifier = Modifier.getAccessSpecifier(getModifiers());
      sb.append(accessSpecifier.asString());
      sb.append(accessSpecifier == AccessSpecifier.DEFAULT ? "" : " ");
    }
    sb.append(getName());
    sb.append("(");
    boolean firstParam = true;
    for (Parameter param : getParameters()) {
      if (firstParam) {
        firstParam = false;
      } else {
        sb.append(", ");
      }
      if (includingParameterName) {
        sb.append(param.toString(prettyPrinterNoCommentsConfiguration));
      } else {
        sb.append(param.getType().toString(prettyPrinterNoCommentsConfiguration));
      }
    }
    sb.append(")");
    sb.append(appendThrowsIfRequested(includingThrows));
    return sb.toString();
  }
}