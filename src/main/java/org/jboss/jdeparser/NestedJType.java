package org.jboss.jdeparser;
import static org.jboss.jdeparser.Tokens.$PUNCT;
import java.io.IOException;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
class NestedJType extends AbstractJType {
  private final AbstractJType enclosingType;

  private final String name;

  private StaticRefJExpr classExpr;

  private StaticRefJExpr thisExpr;

  private StaticRefJExpr superExpr;

  private CachingLinkedHashMap<String, NestedJType> nestedTypes;

  NestedJType(final AbstractJType enclosingType, final String name) {
    this.enclosingType = enclosingType;
    this.name = name;
  }

  String qualifiedName() {
    return enclosingType.qualifiedName() + "." + name;
  }

  public JExpr _class() {
    StaticRefJExpr expr = classExpr;
    if (expr == null) {
      expr = classExpr = new StaticRefJExpr(this, "class");
    }
    return expr;
  }

  public JExpr _this() {
    StaticRefJExpr expr = thisExpr;
    if (expr == null) {
      expr = thisExpr = new StaticRefJExpr(this, "this");
    }
    return expr;
  }

  public JExpr _super() {
    StaticRefJExpr expr = superExpr;
    if (expr == null) {
      expr = superExpr = new StaticRefJExpr(this, "super");
    }
    return expr;
  }

  public JCall _new(final JExpr dim) {
    return new NewJCall(this);
  }

  public JAnonymousClassDef _newAnon() {
    return new ImplJAnonymousClassDef(this);
  }

  boolean equals(final AbstractJType other) {
    return other instanceof NestedJType && equals((NestedJType) other);
  }

  private boolean equals(final NestedJType other) {
    return enclosingType.equals(other.enclosingType) && name.equals(other.name);
  }

  public int hashCode() {
    return enclosingType.hashCode() * 17 + name.hashCode();
  }

  public String simpleName() {
    return name;
  }

  public JType typeArg(final JType... args) {
    return new NarrowedJType(this, args);
  }

  public JType nestedType(final String name) {
    CachingLinkedHashMap<String, NestedJType> nestedTypes = this.nestedTypes;
    if (nestedTypes == null) {
      nestedTypes = this.nestedTypes = new CachingLinkedHashMap<>();
    }
    NestedJType nestedType = nestedTypes.get(name);
    if (nestedType == null) {
      nestedTypes.put(name, nestedType = new NestedJType(this, name));
    }
    return nestedType;
  }

  public String toString() {
    return "Nested type " + name + " of " + enclosingType;
  }

  void writeDirect(final SourceFileWriter writer) throws IOException {
    if (!writer.getClassFile().hasStaticImport(name)) {
      enclosingType.writeDirect(writer);
      writer.write($PUNCT.DOT);
    }
    writer.writeClass(name);
  }
}