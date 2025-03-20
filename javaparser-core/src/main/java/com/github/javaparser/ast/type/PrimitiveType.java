package com.github.javaparser.ast.type;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import com.github.javaparser.TokenRange;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.AllFieldsConstructor;
import java.util.HashMap;
import com.github.javaparser.ast.Generated;
import java.util.Optional;
import com.github.javaparser.ast.Node;
import java.util.function.Consumer;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.PrimitiveTypeMetaModel;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;

/**
 * A primitive type.
 * <br>{@code int}
 * <br>{@code boolean}
 * <br>{@code short}
 *
 * @author Julio Vilmar Gesser
 */
public class PrimitiveType extends Type implements NodeWithAnnotations<PrimitiveType> {
  public static PrimitiveType booleanType() {
    return new PrimitiveType(Primitive.BOOLEAN);
  }

  public static PrimitiveType charType() {
    return new PrimitiveType(Primitive.CHAR);
  }

  public static PrimitiveType byteType() {
    return new PrimitiveType(Primitive.BYTE);
  }

  public static PrimitiveType shortType() {
    return new PrimitiveType(Primitive.SHORT);
  }

  public static PrimitiveType intType() {
    return new PrimitiveType(Primitive.INT);
  }

  public static PrimitiveType longType() {
    return new PrimitiveType(Primitive.LONG);
  }

  public static PrimitiveType floatType() {
    return new PrimitiveType(Primitive.FLOAT);
  }

  public static PrimitiveType doubleType() {
    return new PrimitiveType(Primitive.DOUBLE);
  }

  public enum Primitive {
    BOOLEAN("Boolean", "Z"),
    CHAR("Character", "C"),
    BYTE("Byte", "B"),
    SHORT("Short", "S"),
    INT("Integer", "I"),
    LONG("Long", "J"),
    FLOAT("Float", "F"),
    DOUBLE("Double", "D")
    ;

    final String nameOfBoxedType;

    final String descriptor;

    private String codeRepresentation;

    public ClassOrInterfaceType toBoxedType() {
      return parseClassOrInterfaceType(nameOfBoxedType);
    }

    public String asString() {
      return codeRepresentation;
    }

    Primitive(String nameOfBoxedType, String descriptor) {
      this.nameOfBoxedType = nameOfBoxedType;
      this.codeRepresentation = name().toLowerCase();
      this.descriptor = descriptor;
    }
  }

  static final HashMap<String, Primitive> unboxMap = new HashMap<>();

  static {
    for (Primitive unboxedType : Primitive.values()) {
      unboxMap.put(unboxedType.nameOfBoxedType, unboxedType);
    }
  }

  private Primitive type;

  public PrimitiveType() {
    this(null, Primitive.INT, new NodeList<>());
  }

  public PrimitiveType(final Primitive type) {
    this(null, type, new NodeList<>());
  }

  @AllFieldsConstructor public PrimitiveType(final Primitive type, NodeList<AnnotationExpr> annotations) {
    this(null, type, annotations);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public PrimitiveType(TokenRange tokenRange, Primitive type, NodeList<AnnotationExpr> annotations) {
    super(tokenRange, annotations);
    setType(type);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public Primitive getType() {
    return type;
  }

  public ClassOrInterfaceType toBoxedType() {
    return type.toBoxedType();
  }

  @Override public String toDescriptor() {
    return type.descriptor;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.PropertyGenerator") public PrimitiveType setType(final Primitive type) {
    assertNotNull(type);
    if (type == this.type) {
      return this;
    }
    notifyPropertyChange(ObservableProperty.TYPE, this.type, type);
    this.type = type;
    return this;
  }

  @Override public String asString() {
    return type.asString();
  }

  @Override public PrimitiveType setAnnotations(NodeList<AnnotationExpr> annotations) {
    return (PrimitiveType) super.setAnnotations(annotations);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public PrimitiveType clone() {
    return (PrimitiveType) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public PrimitiveTypeMetaModel getMetaModel() {
    return JavaParserMetaModel.primitiveTypeMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    return super.replace(node, replacementNode);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isPrimitiveType() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public PrimitiveType asPrimitiveType() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifPrimitiveType(Consumer<PrimitiveType> action) {
    action.accept(this);
  }

  @Override public ResolvedPrimitiveType resolve() {
    return getSymbolResolver().toResolvedType(this, ResolvedPrimitiveType.class);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<PrimitiveType> toPrimitiveType() {
    return Optional.of(this);
  }
}