package com.github.javaparser.ast.expr;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.TextBlockLiteralExprMetaModel;
import com.github.javaparser.utils.Pair;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static com.github.javaparser.utils.StringEscapeUtils.unescapeJavaTextBlock;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

/**
 * <h1>A text block</h1>
 * <h2>Java 13-</h2>
 * A text block is a multi-line string. It was introduced in JEP 355.
 * The content of "value" is byte-for-byte exactly what is in the source code.
 */
public class TextBlockLiteralExpr extends LiteralStringValueExpr {
  public TextBlockLiteralExpr() {
    this(null, "empty");
  }

  /**
     * Creates a text block literal expression from given string.
     *
     * @param value the value of the literal
     */
  @AllFieldsConstructor public TextBlockLiteralExpr(final String value) {
    this(null, value);
  }

  /**
     * This constructor is used by the parser and is considered private.
     */
  @Generated(value = "com.github.javaparser.generator.core.node.MainConstructorGenerator") public TextBlockLiteralExpr(TokenRange tokenRange, String value) {
    super(tokenRange, value);
    customInitialization();
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <R extends java.lang.Object, A extends java.lang.Object> R accept(final GenericVisitor<R, A> v, final A arg) {
    return v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.AcceptGenerator") public <A extends java.lang.Object> void accept(final VoidVisitor<A> v, final A arg) {
    v.visit(this, arg);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public boolean isTextBlockLiteralExpr() {
    return true;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public TextBlockLiteralExpr asTextBlockLiteralExpr() {
    return this;
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public Optional<TextBlockLiteralExpr> toTextBlockLiteralExpr() {
    return Optional.of(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.TypeCastingGenerator") public void ifTextBlockLiteralExpr(Consumer<TextBlockLiteralExpr> action) {
    action.accept(this);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.CloneGenerator") public TextBlockLiteralExpr clone() {
    return (TextBlockLiteralExpr) accept(new CloneVisitor(), null);
  }

  @Override @Generated(value = "com.github.javaparser.generator.core.node.GetMetaModelGenerator") public TextBlockLiteralExprMetaModel getMetaModel() {
    return JavaParserMetaModel.textBlockLiteralExprMetaModel;
  }

  /**
     * Most of the algorithm for stripIndent, stopping just before concatenating all the lines into a single string.
     * Useful for tools.
     */
  public Stream<String> stripIndentOfLines() {
    String[] rawLines = getValue().split("\\R", -1);
    int commonWhiteSpacePrefixSize = range(0, rawLines.length).mapToObj((nr) -> new Pair<>(nr, rawLines[nr])).filter((l) -> !emptyOrWhitespace(l.b) || isLastLine(rawLines, l.a)).map((l) -> indentSize(l.b)).min(Integer::compare).orElse(0);
    return Arrays.stream(rawLines).map((l) -> l.length() < commonWhiteSpacePrefixSize ? l : l.substring(commonWhiteSpacePrefixSize)).map(this::trimTrailing);
  }

  /**
     * @return The algorithm from String::stripIndent in JDK 13.
     */
  public String stripIndent() {
    return stripIndentOfLines().collect(joining("\n"));
  }

  /**
     * @return The algorithm from String::translateEscapes in JDK 13.
     */
  public String translateEscapes() {
    return unescapeJavaTextBlock(stripIndent());
  }

  /**
     * @return the final string value of this text block after all processing.
     */
  public String asString() {
    return translateEscapes();
  }

  /**
     * @return is the line with index lineNr the last line in rawLines?
     */
  private boolean isLastLine(String[] rawLines, Integer lineNr) {
    return lineNr == rawLines.length - 1;
  }

  /**
     * @return is this string empty or filled only with whitespace?
     */
  private boolean emptyOrWhitespace(String rawLine) {
    return rawLine.trim().isEmpty();
  }

  /**
     * @return the amount of leading whitespaces.
     */
  private int indentSize(String s) {
    String content = s.trim();
    if (content.isEmpty()) {
      return s.length();
    }
    return s.indexOf(content);
  }

  /**
     * Can be replaced when moving to JDK 11
     */
  private String trimTrailing(String source) {
    int pos = source.length() - 1;
    while ((pos >= 0) && Character.isWhitespace(source.charAt(pos))) {
      pos--;
    }
    pos++;
    return (pos < source.length()) ? source.substring(0, pos) : source;
  }
}