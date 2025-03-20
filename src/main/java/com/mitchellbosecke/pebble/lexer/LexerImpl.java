package com.mitchellbosecke.pebble.lexer;
import java.io.IOException;
import com.mitchellbosecke.pebble.error.ParserException;
import java.io.Reader;
import com.mitchellbosecke.pebble.lexer.Token.Type;
import java.util.ArrayList;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import java.util.Collection;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import java.util.Collections;
import com.mitchellbosecke.pebble.utils.Pair;
import java.util.HashMap;
import com.mitchellbosecke.pebble.utils.StringLengthComparator;
import java.util.LinkedList;
import com.mitchellbosecke.pebble.utils.StringUtils;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads the template input and builds single items out of it.
 * <p>
 * This class is not thread safe.
 */
public final class LexerImpl implements Lexer {
  /**
     * Syntax
     */
  private final Syntax syntax;

  /**
     * Unary operators
     */
  private final Collection<UnaryOperator> unaryOperators;

  /**
     * Binary operators
     */
  private final Collection<BinaryOperator> binaryOperators;

  /**
     * As we progress through the source we maintain a string which is the text
     * that has yet to be tokenized.
     */
  private TemplateSource source;

  /**
     * The list of tokens that we find and use to create a TokenStream
     */
  private ArrayList<Token> tokens;

  /**
     * Represents the brackets we are currently inside ordered by how recently
     * we encountered them. (i.e. peek() will return the most innermost bracket,
     * getLast() will return the outermost). Brackets in this case includes
     * double quotes. The String value of the pair is the bracket
     * representation, and the Integer is the line number.
     */
  private LinkedList<Pair<String, Integer>> brackets;

  /**
     * The state of the lexer is important so that we know what to expect next
     * and to help discover errors in the template (ex. unclosed comments).
     */
  private State state;

  private LinkedList<State> states;

  private enum State {
    DATA,
    EXECUTE,
    PRINT,
    COMMENT,
    STRING,
    STRING_INTERPOLATION
  }

  /**
     * If we encountered an END delimiter that was preceded with a whitespace
     * trim character (ex. {{ foo -}}) then this boolean is toggled to "true"
     * which tells the lexData() method to trim leading whitespace from the next
     * text token.
     */
  private boolean trimLeadingWhitespaceFromNextData = false;

  /**
     * Static regular expressions for names, numbers, and punctuation.
     */
  private static final Pattern REGEX_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*");

  private static final Pattern REGEX_NUMBER = Pattern.compile("^[0-9]+(\\.[0-9]+)?");

  /**
     * Matches a double quote
     */
  private static final Pattern REGEX_DOUBLEQUOTE = Pattern.compile("^\"");

  /**
     * Matches everything up to the first interpolation in a double quoted
     * string
     */
  private static final Pattern REGEX_STRING_NON_INTERPOLATED_PART = Pattern.compile("^[^#\"\\\\]*(?:(?:\\\\.|#(?!\\{))[^#\"\\\\]*)*", Pattern.DOTALL);

  /**
     * Matches single quoted strings and double quoted strings without
     * interpolation. Extra complexity is due to ignoring escaped quotation
     * marks.
     */
  private static final Pattern REGEX_STRING_PLAIN = Pattern.compile("^\"([^#\"\\\\]*(?:\\\\.[^#\"\\\\]*)*)\"|\'([^\'\\\\]*(?:\\\\.[^\'\\\\]*)*)\'", Pattern.DOTALL);

  private static final String PUNCTUATION = "()[]{}?:.,|=";

  /**
     * Regular expression to find operators
     */
  private Pattern regexOperators;

  /**
     * Constructor
     *
     * @param syntax
     *            The primary syntax
     * @param unaryOperators
     *            The available unary operators
     * @param binaryOperators
     *            The available binary operators
     */
  public LexerImpl(Syntax syntax, Collection<UnaryOperator> unaryOperators, Collection<BinaryOperator> binaryOperators) {
    this.syntax = syntax;
    this.unaryOperators = unaryOperators;
    this.binaryOperators = binaryOperators;
  }

  /**
     * This is the main method used to tokenize the raw contents of a template.
     *
     * @param reader
     *            The reader provided from the Loader
     * @param name
     *            The name of the template (used for meaningful error messages)
     * @throws ParserException
     *             Thrown from the Reader object
     */
  @Override public TokenStream tokenize(Reader reader, String name) {
    buildOperatorRegex();
    try {
      this.source = new TemplateSource(reader, name);
    } catch (IOException e) {
      throw new ParserException(e, "Can not convert template Reader into a String", 0, name);
    }
    this.state = State.DATA;
    this.tokens = new ArrayList<>();
    this.states = new LinkedList<>();
    this.brackets = new LinkedList<>();
    while (this.source.length() > 0) {
      switch (this.state) {
        case DATA:
        lexData();
        break;
        case EXECUTE:
        lexExecute();
        break;
        case PRINT:
        lexPrint();
        break;
        case COMMENT:
        lexComment();
        break;
        case STRING:
        lexString();
        break;
        case STRING_INTERPOLATION:
        lexStringInterpolation();
        break;
        default:
        break;
      }
    }
    pushToken(Token.Type.EOF);
    if (!this.brackets.isEmpty()) {
      String expected = brackets.pop().getLeft();
      throw new ParserException(null, String.format("Unclosed \"%s\"", expected), source.getLineNumber(), source.getFilename());
    }
    return new TokenStream(tokens, source.getFilename());
  }

  private void lexStringInterpolation() throws ParserException {
    String lastBracket = brackets.peek().getLeft();
    Matcher matcher = syntax.getRegexInterpolationClose().matcher(source);
    if (syntax.getInterpolationOpenDelimiter().equals(lastBracket) && matcher.lookingAt()) {
      brackets.pop();
      pushToken(Token.Type.STRING_INTERPOLATION_END);
      source.advance(matcher.end());
      popState();
    } else {
      lexExpression();
    }
  }

  private void lexString() throws ParserException {
    Matcher matcher = this.syntax.getRegexInterpolationOpen().matcher(source);
    if (matcher.lookingAt()) {
      brackets.push(new Pair<>(syntax.getInterpolationOpenDelimiter(), source.getLineNumber()));
      pushToken(Token.Type.STRING_INTERPOLATION_START);
      source.advance(matcher.end());
      pushState(State.STRING_INTERPOLATION);
      return;
    }
    matcher = REGEX_STRING_NON_INTERPOLATED_PART.matcher(source);
    if (matcher.lookingAt() && matcher.end() > 0) {
      String token = source.substring(matcher.end());
      source.advance(matcher.end());
      pushToken(Token.Type.STRING, token);
      return;
    }
    matcher = REGEX_DOUBLEQUOTE.matcher(source);
    if (matcher.lookingAt()) {
      String expected = brackets.pop().getLeft();
      if (source.charAt(0) != '\"') {
        throw new ParserException(null, String.format("Unclosed \"%s\"", expected), source.getLineNumber(), source.getFilename());
      }
      popState();
      source.advance(matcher.end());
    }
  }

  /**
     * The DATA state assumes that we are current NOT in between any pair of
     * meaningful delimiters. We are currently looking for the next "open" or
     * "start" delimiter, ex. the opening comment delimiter, or the opening
     * variable delimiter.
     *
     * @throws ParserException
     */
  private void lexData() {
    Matcher matcher = this.syntax.getRegexStartDelimiters().matcher(source);
    boolean match = matcher.find();
    String text;
    String startDelimiterToken = null;
    if (!match) {
      text = source.toString();
      source.advance(source.length());
    } else {
      text = source.substring(matcher.start());
      startDelimiterToken = source.substring(matcher.start(), matcher.end());
      source.advance(matcher.end());
    }
    if (trimLeadingWhitespaceFromNextData) {
      text = StringUtils.ltrim(text);
      trimLeadingWhitespaceFromNextData = false;
    }
    Token textToken = pushToken(Type.TEXT, text);
    if (match) {
      checkForLeadingWhitespaceTrim(textToken);
      if (this.syntax.getCommentOpenDelimiter().equals(startDelimiterToken)) {
        pushState(State.COMMENT);
      } else {
        if (this.syntax.getPrintOpenDelimiter().equals(startDelimiterToken)) {
          pushToken(Token.Type.PRINT_START);
          pushState(State.PRINT);
        } else {
          if ((this.syntax.getExecuteOpenDelimiter().equals(startDelimiterToken))) {
            Matcher verbatimStartMatcher = this.syntax.getRegexVerbatimStart().matcher(source);
            if (verbatimStartMatcher.lookingAt()) {
              lexVerbatimData(verbatimStartMatcher);
              pushState(State.DATA);
            } else {
              pushToken(Token.Type.EXECUTE_START);
              pushState(State.EXECUTE);
            }
          }
        }
      }
    }
  }

  /**
     * Tokenizes between execute delimiters.
     *
     * @throws ParserException
     */
  private void lexExecute() {
    checkForTrailingWhitespaceTrim();
    Matcher matcher = this.syntax.getRegexExecuteClose().matcher(source);
    if (brackets.isEmpty() && matcher.lookingAt()) {
      pushToken(Token.Type.EXECUTE_END, this.syntax.getExecuteCloseDelimiter());
      source.advance(matcher.end());
      popState();
    } else {
      lexExpression();
    }
  }

  /**
     * Tokenizes between print delimiters.
     *
     * @throws ParserException
     */
  private void lexPrint() {
    checkForTrailingWhitespaceTrim();
    Matcher matcher = this.syntax.getRegexPrintClose().matcher(source);
    if (brackets.isEmpty() && matcher.lookingAt()) {
      pushToken(Token.Type.PRINT_END, this.syntax.getPrintCloseDelimiter());
      source.advance(matcher.end());
      popState();
    } else {
      lexExpression();
    }
  }

  /**
     * Tokenizes between comment delimiters.
     * <p>
     * Simply find the closing delimiter for the comment and move the cursor to
     * that point.
     *
     * @throws ParserException
     */
  private void lexComment() {
    Matcher matcher = this.syntax.getRegexCommentClose().matcher(source);
    boolean match = matcher.find(0);
    if (!match) {
      throw new ParserException(null, "Unclosed comment.", source.getLineNumber(), source.getFilename());
    }
    String comment = source.substring(matcher.start());
    String reversedComment = new StringBuilder(comment).reverse().toString();
    Matcher whitespaceTrimMatcher = this.syntax.getRegexLeadingWhitespaceTrim().matcher(reversedComment);
    if (whitespaceTrimMatcher.lookingAt()) {
      this.trimLeadingWhitespaceFromNextData = true;
    }
    source.advance(matcher.end());
    popState();
  }

  /**
     * Tokenizing an expression which can be found within both execute and print
     * regions.
     *
     * @throws ParserException
     */
  private void lexExpression() {
    String token;
    source.advanceThroughWhitespace();
    Matcher matcher = regexOperators.matcher(source);
    if (matcher.lookingAt()) {
      token = source.substring(matcher.end());
      pushToken(Token.Type.OPERATOR, token);
      source.advance(matcher.end());
      return;
    }
    matcher = REGEX_NAME.matcher(source);
    if (matcher.lookingAt()) {
      token = source.substring(matcher.end());
      pushToken(Token.Type.NAME, token);
      source.advance(matcher.end());
      return;
    }
    matcher = REGEX_NUMBER.matcher(source);
    if (matcher.lookingAt()) {
      token = source.substring(matcher.end());
      pushToken(Token.Type.NUMBER, token);
      source.advance(matcher.end());
      return;
    }
    if (PUNCTUATION.indexOf(source.charAt(0)) >= 0) {
      String character = String.valueOf(source.charAt(0));
      if ("([{".indexOf(character) >= 0) {
        brackets.push(new Pair<>(character, source.getLineNumber()));
      } else {
        if (")]}".indexOf(character) >= 0) {
          if (brackets.isEmpty()) {
            throw new ParserException(null, "Unexpected \"" + character + "\"", source.getLineNumber(), source.getFilename());
          } else {
            HashMap<String, String> validPairs = new HashMap<>();
            validPairs.put("(", ")");
            validPairs.put("[", "]");
            validPairs.put("{", "}");
            String lastBracket = brackets.pop().getLeft();
            String expected = validPairs.get(lastBracket);
            if (!expected.equals(character)) {
              throw new ParserException(null, "Unclosed \"" + expected + "\"", source.getLineNumber(), source.getFilename());
            }
          }
        }
      }
      pushToken(Token.Type.PUNCTUATION, character);
      source.advance(1);
      return;
    }
    matcher = REGEX_STRING_PLAIN.matcher(source);
    if (matcher.lookingAt()) {
      token = source.substring(matcher.end());
      source.advance(matcher.end());
      token = unquoteAndUnescape(token);
      pushToken(Token.Type.STRING, token);
      return;
    }
    matcher = REGEX_DOUBLEQUOTE.matcher(source);
    if (matcher.lookingAt()) {
      brackets.push(new Pair<>("\"", source.getLineNumber()));
      pushState(State.STRING);
      source.advance(matcher.end());
      return;
    }
    throw new ParserException(null, String.format("Unexpected character [%s]", source.charAt(0)), source.getLineNumber(), source.getFilename());
  }

  /**
     * This method assumes the provided {@code str} starts with a single or
     * double quote. It removes the wrapping quotes, and un-escapes any quotes
     * within the string.
     */
  private String unquoteAndUnescape(String str) {
    char quotationType = str.charAt(0);
    str = str.substring(1, str.length() - 1);
    if (quotationType == '\'') {
      str = str.replaceAll("\\\\(\')", "$1");
    } else {
      if (quotationType == '\"') {
        str = str.replaceAll("\\\\(\")", "$1");
      }
    }
    return str;
  }

  private void checkForLeadingWhitespaceTrim(Token leadingToken) {
    Matcher whitespaceTrimMatcher = this.syntax.getRegexLeadingWhitespaceTrim().matcher(source);
    if (whitespaceTrimMatcher.lookingAt()) {
      if (leadingToken != null) {
        leadingToken.setValue(StringUtils.rtrim(leadingToken.getValue()));
      }
      source.advance(whitespaceTrimMatcher.end());
    }
  }

  private void checkForTrailingWhitespaceTrim() {
    Matcher whitespaceTrimMatcher = this.syntax.getRegexTrailingWhitespaceTrim().matcher(source);
    if (whitespaceTrimMatcher.lookingAt()) {
      this.trimLeadingWhitespaceFromNextData = true;
    }
  }

  /**
     * Implementation of the "verbatim" tag
     *
     * @throws ParserException
     */
  private void lexVerbatimData(Matcher verbatimStartMatcher) {
    source.advance(verbatimStartMatcher.end());
    Matcher verbatimEndMatcher = this.syntax.getRegexVerbatimEnd().matcher(source);
    if (!verbatimEndMatcher.find()) {
      throw new ParserException(null, "Unclosed verbatim tag.", source.getLineNumber(), source.getFilename());
    }
    String verbatimText = source.substring(verbatimEndMatcher.start());
    if (verbatimStartMatcher.group(0) != null) {
      verbatimText = StringUtils.ltrim(verbatimText);
    }
    if (verbatimEndMatcher.group(1) != null) {
      verbatimText = StringUtils.rtrim(verbatimText);
    }
    if (verbatimEndMatcher.group(2) != null) {
      trimLeadingWhitespaceFromNextData = true;
    }
    source.advance(verbatimEndMatcher.end());
    pushToken(Type.TEXT, verbatimText);
  }

  /**
     * Create a Token of a certain type but has no particular value. This will
     * pass control to the overloaded method that will push this token into a
     * list of tokens that we are maintaining.
     *
     * @param type
     *            The type of Token we are creating
     */
  private Token pushToken(Token.Type type) {
    return pushToken(type, null);
  }

  /**
     * Create a Token of a certain type and value and push it into the list of
     * tokens that we are maintaining. `
     *
     * @param type
     *            The type of token we are creating
     * @param value
     *            The value of the new token
     */
  private Token pushToken(Token.Type type, String value) {
    if (type.equals(Token.Type.TEXT) && (value == null || "".equals(value))) {
      return null;
    }
    Token result = new Token(type, value, source.getLineNumber());
    this.tokens.add(result);
    return result;
  }

  /**
     * Pushes the current state onto the stack and then updates the current
     * state to the new state.
     *
     * @param state
     *            The new state to use as the current state
     */
  private void pushState(State state) {
    this.states.push(this.state);
    this.state = state;
  }

  /**
     * Pop state from the stack
     */
  private void popState() {
    this.state = this.states.pop();
  }

  /**
     * Retrieves the operators (both unary and binary) from the PebbleEngine and
     * then dynamically creates one giant regular expression to detect for the
     * existence of one of these operators.
     *
     * @return Pattern The regular expression used to find an operator
     */
  private void buildOperatorRegex() {
    List<String> operators = new ArrayList<>();
    for (UnaryOperator operator : unaryOperators) {
      operators.add(operator.getSymbol());
    }
    for (BinaryOperator operator : binaryOperators) {
      operators.add(operator.getSymbol());
    }
    Collections.sort(operators, new StringLengthComparator());
    StringBuilder regex = new StringBuilder("^");
    boolean isFirst = true;
    for (String operator : operators) {
      if (isFirst) {
        isFirst = false;
      } else {
        regex.append("|");
      }
      regex.append(Pattern.quote(operator));
      char nextChar = operator.charAt(operator.length() - 1);
      if (Character.isLetter(nextChar) || Character.getType(nextChar) == Character.LETTER_NUMBER) {
        regex.append("(?![a-zA-Z])");
      }
    }
    this.regexOperators = Pattern.compile(regex.toString());
  }
}