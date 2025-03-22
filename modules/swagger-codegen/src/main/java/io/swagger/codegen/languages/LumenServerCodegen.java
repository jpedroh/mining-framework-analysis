package io.swagger.codegen.languages;
import io.swagger.codegen.*;
import io.swagger.models.properties.*;
import java.util.*;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LumenServerCodegen extends DefaultCodegen implements CodegenConfig {
  protected String sourceFolder = "";

  protected String apiVersion = "1.0.0";

  /**
     * Configures the type of generator.
     * 
     * @return  the CodegenType for this generator
     * @see     io.swagger.codegen.CodegenType
     */
  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  /**
     * Configures a friendly name for the generator.  This will be used by the generator
     * to select the library with the -l flag.
     * 
     * @return the friendly name for the generator
     */
  public String getName() {
    return "lumen";
  }

  /**
     * Returns human-friendly help for the generator.  Provide the consumer with help
     * tips, parameters here
     * 
     * @return A string value for the help message
     */
  public String getHelp() {
    return "Generates a LumenServerCodegen client library.";
  }

  public LumenServerCodegen() {
    super();
    outputFolder = "lumen";
    String packagePath = "";
    apiTemplateFiles.put("api.mustache", ".php");
    templateDir = "lumen";
    apiPackage = "app.Http.Controllers";
    modelPackage = "models";
    reservedWords = new HashSet<String>(Arrays.asList("sample1", "sample2"));
    additionalProperties.put("apiVersion", apiVersion);
    supportingFiles.add(new SupportingFile("composer.mustache", packagePath, "composer.json"));
    supportingFiles.add(new SupportingFile("readme.md", packagePath, "readme.md"));
    supportingFiles.add(new SupportingFile("app.php", packagePath + File.separator + "bootstrap", "app.php"));
    supportingFiles.add(new SupportingFile("index.php", packagePath + File.separator + "public", "index.php"));
    supportingFiles.add(new SupportingFile("User.php", packagePath + File.separator + "app", "User.php"));
    supportingFiles.add(new SupportingFile("Kernel.php", packagePath + File.separator + "app" + File.separator + "Console", "Kernel.php"));
    supportingFiles.add(new SupportingFile("Handler.php", packagePath + File.separator + "app" + File.separator + "Exceptions", "Handler.php"));
    supportingFiles.add(new SupportingFile("routes.mustache", packagePath + File.separator + "app" + File.separator + "Http", "routes.php"));
    supportingFiles.add(new SupportingFile("Controller.php", packagePath + File.separator + "app" + File.separator + "Http" + File.separator + "Controllers" + File.separator, "Controller.php"));
    supportingFiles.add(new SupportingFile("Authenticate.php", packagePath + File.separator + "app" + File.separator + "Http" + File.separator + "Middleware" + File.separator, "Authenticate.php"));
    languageSpecificPrimitives = new HashSet<String>(Arrays.asList("Type1", "Type2"));
  }

  /**
     * Escapes a reserved word as defined in the `reservedWords` array. Handle escaping
     * those terms here.  This logic is only called if a variable matches the reseved words
     * 
     * @return the escaped term
     */
  @Override public String escapeReservedWord(String name) {
    return "_" + name;
  }

  /**
     * Location to write model files.  You can use the modelPackage() as defined when the class is
     * instantiated
     */
  public String modelFileFolder() {
    return outputFolder + "/" + modelPackage().replace('.', File.separatorChar);
  }

  /**
     * Location to write api files.  You can use the apiPackage() as defined when the class is
     * instantiated
     */
  @Override public String apiFileFolder() {
    return outputFolder + "/" + apiPackage().replace('.', File.separatorChar);
  }

  @Override public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
    @SuppressWarnings(value = { "unchecked" }) Map<String, Object> objectMap = (Map<String, Object>) objs.get("operations");
    @SuppressWarnings(value = { "unchecked" }) List<CodegenOperation> operations = (List<CodegenOperation>) objectMap.get("operation");
    Collections.sort(operations, new Comparator<CodegenOperation>() {
      @Override public int compare(CodegenOperation lhs, CodegenOperation rhs) {
        return lhs.path.compareTo(rhs.path);
      }
    });
    return objs;
  }

  /**
     * Optional - type declaration.  This is a String which is used by the templates to instantiate your
     * types.  There is typically special handling for different property types
     *
     * @return a string value used as the `dataType` field for model templates, `returnType` for api templates
     */
  @Override public String getTypeDeclaration(Property p) {
    if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return getSwaggerType(p) + "[" + getTypeDeclaration(inner) + "]";
    } else {
      if (p instanceof MapProperty) {
        MapProperty mp = (MapProperty) p;
        Property inner = mp.getAdditionalProperties();
        return getSwaggerType(p) + "[String, " + getTypeDeclaration(inner) + "]";
      }
    }
    return super.getTypeDeclaration(p);
  }

  /**
     * Optional - swagger type conversion.  This is used to map swagger types in a `Property` into 
     * either language specific types via `typeMapping` or into complex models if there is not a mapping.
     *
     * @return a string value of the type or complex model for this property
     * @see io.swagger.models.properties.Property
     */
  @Override public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if (typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if (languageSpecificPrimitives.contains(type)) {
        return toModelName(type);
      }
    } else {
      type = swaggerType;
    }
    return toModelName(type);
  }

  @Override public String escapeQuotationMark(String input) {
    return input.replace("\'", "");
  }

  @Override public String escapeUnsafeCharacters(String input) {
    return input.replace("*/", "");
  }
}