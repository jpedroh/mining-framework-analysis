package com.github.javaparser.ast.validator.language_level_validations;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.validator.SingleNodeTypeValidator;
import com.github.javaparser.ast.validator.Validator;
import com.github.javaparser.ast.validator.language_level_validations.chunks.RecordDeclarationValidator;

/**
 * This validator validates according to Java 16 syntax rules.
 *
 * @see <a href="https://openjdk.java.net/projects/jdk/16/">https://openjdk.java.net/projects/jdk/16/</a>
 */
public class Java16Validator extends Java15Validator {
  final Validator recordDeclarationValidator = new SingleNodeTypeValidator<>(RecordDeclaration.class, new RecordDeclarationValidator());

  public Java16Validator() {
    super();
    remove(noPatternMatchingInstanceOf);
    remove(noRecordDeclaration);
    add(recordAsTypeIdentifierNotAllowed);
    add(recordDeclarationValidator);
  }
}