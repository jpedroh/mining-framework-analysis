package com.github.javaparser.ast.validator.language_level_validations;

/**
 * This validator validates according to Java 15 syntax rules -- including incubator/preview/second preview features.
 *
 * @see <a href="https://openjdk.java.net/projects/jdk/15/">https://openjdk.java.net/projects/jdk/15/</a>
 */
public class Java15PreviewValidator extends Java15Validator {
  public Java15PreviewValidator() {
    super();
    remove(noPatternMatchingInstanceOf);
    remove(noRecordDeclaration);
    add(recordAsTypeIdentifierNotAllowed);
  }
}