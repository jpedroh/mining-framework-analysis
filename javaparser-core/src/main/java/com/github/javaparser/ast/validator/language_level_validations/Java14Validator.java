package com.github.javaparser.ast.validator.language_level_validations;
import com.github.javaparser.ast.validator.ReservedKeywordValidator;
import com.github.javaparser.ast.validator.Validator;

/**
 * This validator validates according to Java 14 syntax rules.
 *
 * @see <a href="https://openjdk.java.net/projects/jdk/14/">https://openjdk.java.net/projects/jdk/14/</a>
 */
public class Java14Validator extends Java13Validator {
  /**
     * <blockquote>
     *     A type identifier is any identifier other than the character sequences var, yield, and record.<br/>
     *     <br/>
     *     Type identifiers are used in certain contexts involving the declaration or use of types. For example,
     *     the name of a class must be a TypeIdentifier, so it is illegal to declare a class named var,
     *     yield, or record (8.1).
     * </blockquote>
     * https://docs.oracle.com/javase/specs/jls/se15/preview/specs/records-jls.html#jls-3.8
     */
  final Validator recordAsTypeIdentifierNotAllowed = new ReservedKeywordValidator("record");

  public Java14Validator() {
    super();
    {
      remove(noSwitchExpressions);
      remove(onlyOneLabelInSwitchCase);
      remove(noYield);
    }
  }
}