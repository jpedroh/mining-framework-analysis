package com.github.javaparser.ast.validator.language_level_validations;

/**
 * This validator validates according to Java 14 syntax rules -- including incubator/preview/second preview features.
 *
 * @see <a href="https://openjdk.java.net/projects/jdk/14/">https://openjdk.java.net/projects/jdk/14/</a>
 */
public class Java14PreviewValidator extends Java14Validator {
  public Java14PreviewValidator() {
    super();
    remove(noPatternMatchingInstanceOf);
    remove(noRecordDeclaration);
    add(recordAsTypeIdentifierNotAllowed);
    remove(noTextBlockLiteral);
  }
}