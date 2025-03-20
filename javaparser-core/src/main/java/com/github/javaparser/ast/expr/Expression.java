package com.github.javaparser.ast.expr;
import static com.github.javaparser.utils.CodeGenerationUtils.f;
import com.github.javaparser.TokenRange;
import java.util.Optional;
import com.github.javaparser.ast.AllFieldsConstructor;
import java.util.function.Consumer;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.ExpressionMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.resolution.types.ResolvedType;

/**
 * A base class for all expressions.
 *
 * @author Julio Vilmar Gesser
 */
public abstract class Expression extends Node {
  @AllFieldsConstructor public Expression() {
    this(null);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public Expression(TokenRange tokenRange) {
    super(tokenRange);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.RemoveMethodGenerator") public boolean remove(Node node) {
    if (node == null) {
      return false;
    }
    return super.remove(node);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public Expression clone() {
    return (Expression) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public ExpressionMetaModel getMetaModel() {
    return JavaParserMetaModel.expressionMetaModel;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.ReplaceMethodGenerator") public boolean replace(Node node, Node replacementNode) {
    if (node == null) {
      return false;
    }
    return super.replace(node, replacementNode);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isAnnotationExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public AnnotationExpr asAnnotationExpr() {
    throw new IllegalStateException(f("%s is not AnnotationExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isArrayAccessExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ArrayAccessExpr asArrayAccessExpr() {
    throw new IllegalStateException(f("%s is not ArrayAccessExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isArrayCreationExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ArrayCreationExpr asArrayCreationExpr() {
    throw new IllegalStateException(f("%s is not ArrayCreationExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isArrayInitializerExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ArrayInitializerExpr asArrayInitializerExpr() {
    throw new IllegalStateException(f("%s is not ArrayInitializerExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isAssignExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public AssignExpr asAssignExpr() {
    throw new IllegalStateException(f("%s is not AssignExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isBinaryExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public BinaryExpr asBinaryExpr() {
    throw new IllegalStateException(f("%s is not BinaryExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isBooleanLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public BooleanLiteralExpr asBooleanLiteralExpr() {
    throw new IllegalStateException(f("%s is not BooleanLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isCastExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public CastExpr asCastExpr() {
    throw new IllegalStateException(f("%s is not CastExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isCharLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public CharLiteralExpr asCharLiteralExpr() {
    throw new IllegalStateException(f("%s is not CharLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isClassExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ClassExpr asClassExpr() {
    throw new IllegalStateException(f("%s is not ClassExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isConditionalExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ConditionalExpr asConditionalExpr() {
    throw new IllegalStateException(f("%s is not ConditionalExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isDoubleLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public DoubleLiteralExpr asDoubleLiteralExpr() {
    throw new IllegalStateException(f("%s is not DoubleLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isEnclosedExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public EnclosedExpr asEnclosedExpr() {
    throw new IllegalStateException(f("%s is not EnclosedExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isFieldAccessExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public FieldAccessExpr asFieldAccessExpr() {
    throw new IllegalStateException(f("%s is not FieldAccessExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isInstanceOfExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public InstanceOfExpr asInstanceOfExpr() {
    throw new IllegalStateException(f("%s is not InstanceOfExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isIntegerLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public IntegerLiteralExpr asIntegerLiteralExpr() {
    throw new IllegalStateException(f("%s is not IntegerLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isLambdaExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public LambdaExpr asLambdaExpr() {
    throw new IllegalStateException(f("%s is not LambdaExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public LiteralExpr asLiteralExpr() {
    throw new IllegalStateException(f("%s is not LiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isLiteralStringValueExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public LiteralStringValueExpr asLiteralStringValueExpr() {
    throw new IllegalStateException(f("%s is not LiteralStringValueExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isLongLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public LongLiteralExpr asLongLiteralExpr() {
    throw new IllegalStateException(f("%s is not LongLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isMarkerAnnotationExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public MarkerAnnotationExpr asMarkerAnnotationExpr() {
    throw new IllegalStateException(f("%s is not MarkerAnnotationExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isMethodCallExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public MethodCallExpr asMethodCallExpr() {
    throw new IllegalStateException(f("%s is not MethodCallExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isMethodReferenceExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public MethodReferenceExpr asMethodReferenceExpr() {
    throw new IllegalStateException(f("%s is not MethodReferenceExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isNameExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public NameExpr asNameExpr() {
    throw new IllegalStateException(f("%s is not NameExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isNormalAnnotationExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public NormalAnnotationExpr asNormalAnnotationExpr() {
    throw new IllegalStateException(f("%s is not NormalAnnotationExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isNullLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public NullLiteralExpr asNullLiteralExpr() {
    throw new IllegalStateException(f("%s is not NullLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isObjectCreationExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ObjectCreationExpr asObjectCreationExpr() {
    throw new IllegalStateException(f("%s is not ObjectCreationExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isSingleMemberAnnotationExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public SingleMemberAnnotationExpr asSingleMemberAnnotationExpr() {
    throw new IllegalStateException(f("%s is not SingleMemberAnnotationExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isStringLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public StringLiteralExpr asStringLiteralExpr() {
    throw new IllegalStateException(f("%s is not StringLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isSuperExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public SuperExpr asSuperExpr() {
    throw new IllegalStateException(f("%s is not SuperExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isThisExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public ThisExpr asThisExpr() {
    throw new IllegalStateException(f("%s is not ThisExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isTypeExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public TypeExpr asTypeExpr() {
    throw new IllegalStateException(f("%s is not TypeExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isUnaryExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public UnaryExpr asUnaryExpr() {
    throw new IllegalStateException(f("%s is not UnaryExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isVariableDeclarationExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public VariableDeclarationExpr asVariableDeclarationExpr() {
    throw new IllegalStateException(f("%s is not VariableDeclarationExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifAnnotationExpr(Consumer<AnnotationExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifArrayAccessExpr(Consumer<ArrayAccessExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifArrayCreationExpr(Consumer<ArrayCreationExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifArrayInitializerExpr(Consumer<ArrayInitializerExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifAssignExpr(Consumer<AssignExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifBinaryExpr(Consumer<BinaryExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifBooleanLiteralExpr(Consumer<BooleanLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifCastExpr(Consumer<CastExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifCharLiteralExpr(Consumer<CharLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifClassExpr(Consumer<ClassExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifConditionalExpr(Consumer<ConditionalExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifDoubleLiteralExpr(Consumer<DoubleLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifEnclosedExpr(Consumer<EnclosedExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifFieldAccessExpr(Consumer<FieldAccessExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifInstanceOfExpr(Consumer<InstanceOfExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifIntegerLiteralExpr(Consumer<IntegerLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifLambdaExpr(Consumer<LambdaExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifLiteralExpr(Consumer<LiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifLiteralStringValueExpr(Consumer<LiteralStringValueExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifLongLiteralExpr(Consumer<LongLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifMarkerAnnotationExpr(Consumer<MarkerAnnotationExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifMethodCallExpr(Consumer<MethodCallExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifMethodReferenceExpr(Consumer<MethodReferenceExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifNameExpr(Consumer<NameExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifNormalAnnotationExpr(Consumer<NormalAnnotationExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifNullLiteralExpr(Consumer<NullLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifObjectCreationExpr(Consumer<ObjectCreationExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifSingleMemberAnnotationExpr(Consumer<SingleMemberAnnotationExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifStringLiteralExpr(Consumer<StringLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifSuperExpr(Consumer<SuperExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifThisExpr(Consumer<ThisExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifTypeExpr(Consumer<TypeExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifUnaryExpr(Consumer<UnaryExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifVariableDeclarationExpr(Consumer<VariableDeclarationExpr> action) {
  }

  /**
     * returns the type associated with the node.
     */
  public ResolvedType calculateResolvedType() {
    return getSymbolResolver().calculateType(this);
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<AnnotationExpr> toAnnotationExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ArrayAccessExpr> toArrayAccessExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ArrayCreationExpr> toArrayCreationExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ArrayInitializerExpr> toArrayInitializerExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<AssignExpr> toAssignExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<BinaryExpr> toBinaryExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<BooleanLiteralExpr> toBooleanLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<CastExpr> toCastExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<CharLiteralExpr> toCharLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ClassExpr> toClassExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ConditionalExpr> toConditionalExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<DoubleLiteralExpr> toDoubleLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<EnclosedExpr> toEnclosedExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<FieldAccessExpr> toFieldAccessExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<InstanceOfExpr> toInstanceOfExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<IntegerLiteralExpr> toIntegerLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<LambdaExpr> toLambdaExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<LiteralExpr> toLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<LiteralStringValueExpr> toLiteralStringValueExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<LongLiteralExpr> toLongLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<MarkerAnnotationExpr> toMarkerAnnotationExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<MethodCallExpr> toMethodCallExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<MethodReferenceExpr> toMethodReferenceExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<NameExpr> toNameExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<NormalAnnotationExpr> toNormalAnnotationExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<NullLiteralExpr> toNullLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ObjectCreationExpr> toObjectCreationExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<SingleMemberAnnotationExpr> toSingleMemberAnnotationExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<StringLiteralExpr> toStringLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<SuperExpr> toSuperExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<ThisExpr> toThisExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<TypeExpr> toTypeExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<UnaryExpr> toUnaryExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<VariableDeclarationExpr> toVariableDeclarationExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isSwitchExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public SwitchExpr asSwitchExpr() {
    throw new IllegalStateException(f("%s is not SwitchExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<SwitchExpr> toSwitchExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifSwitchExpr(Consumer<SwitchExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isTextBlockLiteralExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public TextBlockLiteralExpr asTextBlockLiteralExpr() {
    throw new IllegalStateException(f("%s is not TextBlockLiteralExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<TextBlockLiteralExpr> toTextBlockLiteralExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifTextBlockLiteralExpr(Consumer<TextBlockLiteralExpr> action) {
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isPatternExpr() {
    return false;
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public PatternExpr asPatternExpr() {
    throw new IllegalStateException(f("%s is not PatternExpr, it is %s", this, this.getClass().getSimpleName()));
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<PatternExpr> toPatternExpr() {
    return Optional.empty();
  }

  @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifPatternExpr(Consumer<PatternExpr> action) {
  }

  /**
     * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.2
     * @return true if the expression is a standalone expression
     */
  public boolean isStandaloneExpression() {
    return !isPolyExpression();
  }

  /**
     * See https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.2
     * @return true if the expression is a poly expression
     */
  public boolean isPolyExpression() {
    return false;
  }

  public boolean isQualified() {
    return hasScope();
  }

  public final boolean appearsInAssignmentContext() {
    if (getParentNode().isPresent() && getParentNode().get() instanceof Expression) {
      return ((Expression) getParentNode().get()).isAssignmentContext();
    }
    return false;
  }

  protected boolean isAssignmentContext() {
    return false;
  }

  public final boolean appearsInInvocationContext() {
    if (getParentNode().isPresent() && getParentNode().get() instanceof Expression) {
      return ((Expression) getParentNode().get()).isInvocationContext();
    }
    return false;
  }

  protected boolean isInvocationContext() {
    return false;
  }

  public final boolean elidesTypeArguments() {
    if (!(hasScope() && this instanceof NodeWithTypeArguments)) {
      return true;
    }
    Expression scope = (Expression) ((NodeWithOptionalScope) this).getScope().get();
    NodeWithTypeArguments nwta = (NodeWithTypeArguments) this;
    return scope.elidesTypeArguments() && (!nwta.getTypeArguments().isPresent() || nwta.isUsingDiamondOperator());
  }
}