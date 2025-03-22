package io.swagger.codegen.languages;
import io.swagger.codegen.*;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.parameters.Parameter;
import java.io.File;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoClientCodegen extends DefaultCodegen implements CodegenConfig {
  static Logger LOGGER = LoggerFactory.getLogger(GoClientCodegen.class);

  protected String packageName = "swagger";

  protected String packageVersion = "1.0.0";

  protected String apiDocPath = "docs/";

  protected String modelDocPath = "docs/";

  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  public String getName() {
    return "go";
  }

  public String getHelp() {
    return "Generates a Go client library (beta).";
  }

  public GoClientCodegen() {
    super();
    outputFolder = "generated-code/go";
    modelTemplateFiles.put("model.mustache", ".go");
    apiTemplateFiles.put("api.mustache", ".go");
    modelDocTemplateFiles.put("model_doc.mustache", ".md");
    apiDocTemplateFiles.put("api_doc.mustache", ".md");
    templateDir = "go";
    setReservedWordsLowerCase(Arrays.asList("break", "default", "func", "interface", "select", "case", "defer", "go", "map", "struct", "chan", "else", "goto", "package", "switch", "const", "fallthrough", "if", "range", "type", "continue", "for", "import", "return", "var", "error"));
    defaultIncludes = new HashSet<String>(Arrays.asList("map", "array"));
    languageSpecificPrimitives = new HashSet<String>(Arrays.asList("string", "bool", "uint", "uint32", "uint64", "int", "int32", "int64", "float32", "float64", "complex64", "complex128", "rune", "byte"));
    instantiationTypes.clear();
    typeMapping.clear();
    typeMapping.put("integer", "int32");
    typeMapping.put("long", "int64");
    typeMapping.put("number", "float32");
    typeMapping.put("float", "float32");
    typeMapping.put("double", "float64");
    typeMapping.put("boolean", "bool");
    typeMapping.put("string", "string");
    typeMapping.put("date", "time.Time");
    typeMapping.put("DateTime", "time.Time");
    typeMapping.put("password", "string");
    typeMapping.put("File", "*os.File");
    typeMapping.put("file", "*os.File");
    typeMapping.put("binary", "string");
    typeMapping.put("ByteArray", "string");
    importMapping = new HashMap<String, String>();
    importMapping.put("time.Time", "time");
    importMapping.put("*os.File", "os");
    cliOptions.clear();
    cliOptions.add(new CliOption(CodegenConstants.PACKAGE_NAME, "Go package name (convention: lowercase).").defaultValue("swagger"));
    cliOptions.add(new CliOption(CodegenConstants.PACKAGE_VERSION, "Go package version.").defaultValue("1.0.0"));
  }

  @Override public void processOpts() {
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_NAME)) {
      setPackageName((String) additionalProperties.get(CodegenConstants.PACKAGE_NAME));
    } else {
      setPackageName("swagger");
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_VERSION)) {
      setPackageVersion((String) additionalProperties.get(CodegenConstants.PACKAGE_VERSION));
    } else {
      setPackageVersion("1.0.0");
    }
    additionalProperties.put(CodegenConstants.PACKAGE_NAME, packageName);
    additionalProperties.put(CodegenConstants.PACKAGE_VERSION, packageVersion);
    additionalProperties.put("apiDocPath", apiDocPath);
    additionalProperties.put("modelDocPath", modelDocPath);
    modelPackage = packageName;
    apiPackage = packageName;
    supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
    supportingFiles.add(new SupportingFile("git_push.sh.mustache", "", "git_push.sh"));
    supportingFiles.add(new SupportingFile("gitignore.mustache", "", ".gitignore"));
    supportingFiles.add(new SupportingFile("configuration.mustache", "", "configuration.go"));
    supportingFiles.add(new SupportingFile("api_client.mustache", "", "api_client.go"));
    supportingFiles.add(new SupportingFile("pom.mustache", "", "pom.xml"));
  }

  @Override public String escapeReservedWord(String name) {
    return camelize(name) + '_';
  }

  @Override public String apiFileFolder() {
    return outputFolder + File.separator;
  }

  public String modelFileFolder() {
    return outputFolder + File.separator;
  }

  @Override public String toVarName(String name) {
    name = name.replaceAll("-", "_");
    if (name.matches("^[A-Z_]*$")) {
      return name;
    }
    name = camelize(name);
    if (isReservedWord(name) || name.matches("^\\d.*")) {
      name = escapeReservedWord(name);
    }
    return name;
  }

  @Override public String toParamName(String name) {
    return camelize(toVarName(name), true);
  }

  @Override public String toModelName(String name) {
    return camelize(toModelFilename(name));
  }

  @Override public String toModelFilename(String name) {
    if (!StringUtils.isEmpty(modelNamePrefix)) {
      name = modelNamePrefix + "_" + name;
    }
    if (!StringUtils.isEmpty(modelNameSuffix)) {
      name = name + "_" + modelNameSuffix;
    }
    name = sanitizeName(name);
    if (isReservedWord(name)) {
      LOGGER.warn(name + " (reserved word) cannot be used as model name. Renamed to " + camelize("model_" + name));
      name = "model_" + name;
    }
    return underscore(name);
  }

  @Override public String toApiFilename(String name) {
    name = name.replaceAll("-", "_");
    return underscore(name) + "_api";
  }

  /**
     * Overrides postProcessParameter to add a vendor extension "x-exportParamName".
     * This is useful when paramName starts with a lowercase letter, but we need that
     * param to be exportable (starts with an Uppercase letter).
     *
     * @param parameter CodegenParameter object to be processed.
     */
  @Override public void postProcessParameter(CodegenParameter parameter) {
    super.postProcessParameter(parameter);
    char firstChar = parameter.paramName.charAt(0);
    if (Character.isUpperCase(firstChar)) {
      parameter.vendorExtensions.put("x-exportParamName", parameter.paramName);
    }
    StringBuilder sb = new StringBuilder(parameter.paramName);
    sb.setCharAt(0, Character.toUpperCase(firstChar));
    parameter.vendorExtensions.put("x-exportParamName", sb.toString());
  }

  @Override public String apiDocFileFolder() {
    return (outputFolder + "/" + apiDocPath).replace('/', File.separatorChar);
  }

  @Override public String modelDocFileFolder() {
    return (outputFolder + "/" + modelDocPath).replace('/', File.separatorChar);
  }

  @Override public String toModelDocFilename(String name) {
    return toModelName(name);
  }

  @Override public String toApiDocFilename(String name) {
    return toApiName(name);
  }

  @Override public String getTypeDeclaration(Property p) {
    if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return "[]" + getTypeDeclaration(inner);
    } else {
      if (p instanceof MapProperty) {
        MapProperty mp = (MapProperty) p;
        Property inner = mp.getAdditionalProperties();
        return getSwaggerType(p) + "[string]" + getTypeDeclaration(inner);
      }
    }
    String swaggerType = getSwaggerType(p);
    if (typeMapping.containsKey(swaggerType)) {
      return typeMapping.get(swaggerType);
    }
    if (typeMapping.containsValue(swaggerType)) {
      return swaggerType;
    }
    if (languageSpecificPrimitives.contains(swaggerType)) {
      return swaggerType;
    }
    return camelize(swaggerType, false);
  }

  @Override public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if (typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if (languageSpecificPrimitives.contains(type)) {
        return (type);
      }
    } else {
      type = swaggerType;
    }
    return type;
  }

  @Override public String toOperationId(String operationId) {
    if (isReservedWord(operationId)) {
      LOGGER.warn(operationId + " (reserved word) cannot be used as method name. Renamed to " + camelize(sanitizeName("call_" + operationId)));
      operationId = "call_" + operationId;
    }
    return camelize(operationId);
  }

  @Override public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
    @SuppressWarnings(value = { "unchecked" }) Map<String, Object> objectMap = (Map<String, Object>) objs.get("operations");
    @SuppressWarnings(value = { "unchecked" }) List<CodegenOperation> operations = (List<CodegenOperation>) objectMap.get("operation");
    for (CodegenOperation operation : operations) {
      operation.httpMethod = camelize(operation.httpMethod.toLowerCase());
    }
    List<Map<String, String>> imports = (List<Map<String, String>>) objs.get("imports");
    if (imports == null) {
      return objs;
    }
    Iterator<Map<String, String>> iterator = imports.iterator();
    while (iterator.hasNext()) {
      String _import = iterator.next().get("import");
      if (_import.startsWith(apiPackage())) {
        iterator.remove();
      }
    }
    return objs;
  }

  @Override public Map<String, Object> postProcessModels(Map<String, Object> objs) {
    List<Map<String, String>> imports = (List<Map<String, String>>) objs.get("imports");
    final String prefix = modelPackage();
    Iterator<Map<String, String>> iterator = imports.iterator();
    while (iterator.hasNext()) {
      String _import = iterator.next().get("import");
      if (_import.startsWith(prefix)) {
        iterator.remove();
      }
    }
    return objs;
  }

  @Override protected boolean needToImport(String type) {
    return !defaultIncludes.contains(type) && !languageSpecificPrimitives.contains(type);
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public void setPackageVersion(String packageVersion) {
    this.packageVersion = packageVersion;
  }
}