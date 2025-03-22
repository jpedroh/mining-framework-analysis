package de.uni_koblenz.jgralab.codegenerator;
import de.uni_koblenz.jgralab.schema.EnumDomain;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EnumCodeGenerator extends CodeGenerator {
  private EnumDomain enumDomain;

  /**
	 * Creates a new EnumCodeGenerator which creates code for the given
	 * enumDomain object
	 */
  public EnumCodeGenerator(EnumDomain enumDomain, String schemaPackageName) {
    super(schemaPackageName, enumDomain.getPackageName(), new CodeGeneratorConfiguration());
    rootBlock.setVariable("simpleClassName", enumDomain.getSimpleName());
    rootBlock.setVariable("isClassOnly", "true");
    this.enumDomain = enumDomain;
  }

  @Override protected CodeBlock createBody() {
    CodeList result = new CodeList();
    if (currentCycle.isClassOnly()) {
      CodeSnippet constCode = new CodeSnippet(true);
      String delim = "";
      StringBuilder constants = new StringBuilder();
      for (String s : enumDomain.getConsts()) {
        constants.append(delim);
        constants.append(s);
        delim = ", ";
      }
      constants.append(";");
      constCode.add(constants.toString());
      CodeSnippet valueOfCode = new CodeSnippet(true);
      valueOfCode.add("public static #simpleClassName# valueOfPermitNull(String val) {", "\tif (val.equals(de.uni_koblenz.jgralab.GraphIO.NULL_LITERAL)) {", "\t\treturn null;", "\t}", "\treturn valueOf(val);", "}");
      result.add(constCode);
      result.add(valueOfCode);
    }
    return result;
  }

  @Override protected CodeBlock createHeader() {
    if (currentCycle.isClassOnly()) {
      return new CodeSnippet(true, "public enum #simpleClassName# {");
    }
    return new CodeSnippet();
  }
}