package org.openapitools.codegen.languages;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.*;
import org.openapitools.codegen.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.*;
import static org.openapitools.codegen.utils.StringUtils.camelize;
import static org.openapitools.codegen.utils.StringUtils.underscore;

public abstract class AbstractGoCodegen extends DefaultCodegen implements CodegenConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGoCodegen.class);

  private static final String NUMERIC_ENUM_PREFIX = "_";

  protected boolean withGoCodegenComment = false;

  protected boolean withXml = false;

  protected boolean enumClassPrefix = false;

  protected boolean structPrefix = false;

  protected String packageName = "openapi";

  protected Set<String> numberTypes;

  public AbstractGoCodegen() {
    super();
    supportsInheritance = true;
    hideGenerationTimestamp = Boolean.FALSE;
    defaultIncludes = new HashSet<String>(Arrays.asList("map", "array"));
    setReservedWordsLowerCase(Arrays.asList("string", "bool", "uint", "uint8", "uint16", "uint32", "uint64", "int", "int8", "int16", "int32", "int64", "float32", "float64", "complex64", "complex128", "rune", "byte", "uintptr", "break", "default", "func", "interface", "select", "case", "defer", "go", "map", "struct", "chan", "else", "goto", "package", "switch", "const", "fallthrough", "if", "range", "type", "continue", "for", "import", "return", "var", "error", "nil"));
    languageSpecificPrimitives = new HashSet<String>(Arrays.asList("string", "bool", "uint", "uint32", "uint64", "int", "int32", "int64", "float32", "float64", "complex64", "complex128", "rune", "byte"));
    instantiationTypes.clear();
    typeMapping.clear();
    typeMapping.put("integer", "int32");
    typeMapping.put("long", "int64");
    typeMapping.put("number", "float32");
    typeMapping.put("float", "float32");
    typeMapping.put("double", "float64");
    typeMapping.put("BigDecimal", "float64");
    typeMapping.put("boolean", "bool");
    typeMapping.put("string", "string");
    typeMapping.put("UUID", "string");
    typeMapping.put("URI", "string");
    typeMapping.put("date", "string");
    typeMapping.put("DateTime", "time.Time");
    typeMapping.put("password", "string");
    typeMapping.put("File", "*os.File");
    typeMapping.put("file", "*os.File");
    typeMapping.put("binary", "*os.File");
    typeMapping.put("ByteArray", "string");
    typeMapping.put("object", "map[string]interface{}");
    numberTypes = new HashSet<String>(Arrays.asList("uint", "uint8", "uint16", "uint32", "uint64", "int", "int8", "int16", "int32", "int64", "float32", "float64"));
    importMapping = new HashMap<String, String>();
    cliOptions.clear();
    cliOptions.add(new CliOption(CodegenConstants.PACKAGE_NAME, "Go package name (convention: lowercase).").defaultValue("openapi"));
    cliOptions.add(new CliOption(CodegenConstants.PACKAGE_VERSION, "Go package version.").defaultValue("1.0.0"));
    cliOptions.add(new CliOption(CodegenConstants.HIDE_GENERATION_TIMESTAMP, CodegenConstants.HIDE_GENERATION_TIMESTAMP_DESC).defaultValue(Boolean.TRUE.toString()));
  }

  @Override public void processOpts() {
    super.processOpts();
    if (StringUtils.isEmpty(System.getenv("GO_POST_PROCESS_FILE"))) {
      LOGGER.info("Environment variable GO_POST_PROCESS_FILE not defined so Go code may not be properly formatted. To define it, try `export GO_POST_PROCESS_FILE=\"/usr/local/bin/gofmt -w\"` (Linux/Mac)");
      LOGGER.info("NOTE: To enable file post-processing, \'enablePostProcessFile\' must be set to `true` (--enable-post-process-file for CLI).");
    }
  }

  /**
     * Escapes a reserved word as defined in the `reservedWords` array. Handle escaping
     * those terms here.  This logic is only called if a variable matches the reserved words
     *
     * @return the escaped term
     */
  @Override public String escapeReservedWord(String name) {
    if (this.reservedWordsMappings().containsKey(name)) {
      return this.reservedWordsMappings().get(name);
    }
    return camelize(name) + '_';
  }

  @Override public String toVarName(String name) {
    name = sanitizeName(name);
    if (name.matches("^[A-Z_]*$")) {
      return name;
    }
    name = camelize(name);
    if (isReservedWord(name)) {
      LOGGER.warn(name + " (reserved word) cannot be used as variable name. Renamed to " + escapeReservedWord(name));
      name = escapeReservedWord(name);
    }
    if (name.matches("^\\d.*")) {
      name = "Var" + name;
    }
    return name;
  }

  @Override protected boolean isReservedWord(String word) {
    return word != null && reservedWords.contains(word);
  }

  @Override public String toParamName(String name) {
    name = camelize(toVarName(name), true);
    if (isReservedWord(name)) {
      LOGGER.warn(name + " (reserved word) cannot be used as parameter name. Renamed to " + name + "_");
      name = name + "_";
    }
    return name;
  }

  @Override public String toModelName(String name) {
    return camelize(toModel(name));
  }

  @Override public String toModelFilename(String name) {
    name = toModel("model_" + name);
    if (name.endsWith("_test")) {
      LOGGER.warn(name + ".go with `_test.go` suffix (reserved word) cannot be used as filename. Renamed to " + name + "_.go");
      name += "_";
    }
    return name;
  }

  public String toModel(String name) {
    if (!StringUtils.isEmpty(modelNamePrefix)) {
      name = modelNamePrefix + "_" + name;
    }
    if (!StringUtils.isEmpty(modelNameSuffix)) {
      name = name + "_" + modelNameSuffix;
    }
    name = sanitizeName(name);
    if (isReservedWord(name)) {
      LOGGER.warn(name + " (reserved word) cannot be used as model name. Renamed to " + ("model_" + name));
      name = "model_" + name;
    }
    if (name.matches("^\\d.*")) {
      LOGGER.warn(name + " (model name starts with number) cannot be used as model name. Renamed to " + ("model_" + name));
      name = "model_" + name;
    }
    return underscore(name);
  }

  @Override public String toApiFilename(String name) {
    name = name.replaceAll("-", "_");
    name = "api_" + underscore(name);
    if (name.endsWith("_test")) {
      LOGGER.warn(name + ".go with `_test.go` suffix (reserved word) cannot be used as filename. Renamed to " + name + "_.go");
      name += "_";
    }
    return name;
  }

  @Override public String getTypeDeclaration(Schema p) {
    if (ModelUtils.isArraySchema(p)) {
      ArraySchema ap = (ArraySchema) p;
      Schema inner = ap.getItems();
      return "[]" + getTypeDeclaration(ModelUtils.unaliasSchema(this.openAPI, inner));
    } else {
      if (ModelUtils.isMapSchema(p)) {
        Schema inner = ModelUtils.getAdditionalProperties(p);
        return getSchemaType(p) + "[string]" + getTypeDeclaration(ModelUtils.unaliasSchema(this.openAPI, inner));
      }
    }
    String openAPIType = getSchemaType(p);
    String ref = p.get$ref();
    if (ref != null && !ref.isEmpty()) {
      String tryRefV2 = "#/definitions/" + openAPIType;
      String tryRefV3 = "#/components/schemas/" + openAPIType;
      if (ref.equals(tryRefV2) || ref.equals(tryRefV3)) {
        return toModelName(openAPIType);
      }
    }
    if (typeMapping.containsKey(openAPIType)) {
      return typeMapping.get(openAPIType);
    }
    if (typeMapping.containsValue(openAPIType)) {
      return openAPIType;
    }
    if (languageSpecificPrimitives.contains(openAPIType)) {
      return openAPIType;
    }
    return toModelName(openAPIType);
  }

  @Override public String getSchemaType(Schema p) {
    String openAPIType = super.getSchemaType(p);
    String ref = p.get$ref();
    String type = null;
    if (ref != null && !ref.isEmpty()) {
      type = openAPIType;
    } else {
      if (typeMapping.containsKey(openAPIType)) {
        type = typeMapping.get(openAPIType);
        if (languageSpecificPrimitives.contains(type)) {
          return (type);
        }
      } else {
        type = openAPIType;
      }
    }
    return type;
  }

  @Override public String toOperationId(String operationId) {
    String sanitizedOperationId = sanitizeName(operationId);
    if (isReservedWord(sanitizedOperationId)) {
      LOGGER.warn(operationId + " (reserved word) cannot be used as method name. Renamed to " + camelize("call_" + sanitizedOperationId));
      sanitizedOperationId = "call_" + sanitizedOperationId;
    }
    if (sanitizedOperationId.matches("^\\d.*")) {
      LOGGER.warn(operationId + " (starting with a number) cannot be used as method name. Renamed to " + camelize("call_" + sanitizedOperationId));
      sanitizedOperationId = "call_" + sanitizedOperationId;
    }
    return camelize(sanitizedOperationId);
  }

  @Override public Map<String, Object> postProcessOperationsWithModels(Map<String, Object> objs, List<Object> allModels) {
    @SuppressWarnings(value = { "unchecked" }) Map<String, Object> objectMap = (Map<String, Object>) objs.get("operations");
    @SuppressWarnings(value = { "unchecked" }) List<CodegenOperation> operations = (List<CodegenOperation>) objectMap.get("operation");
    for (CodegenOperation operation : operations) {
      operation.httpMethod = camelize(operation.httpMethod.toLowerCase(Locale.ROOT));
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
    for (CodegenOperation operation : operations) {
      if (operation.pathParams != null && operation.pathParams.size() > 0) {
        imports.add(createMapping("import", "fmt"));
        imports.add(createMapping("import", "strings"));
        break;
      }
    }
    boolean addedOptionalImport = false;
    boolean addedTimeImport = false;
    boolean addedOSImport = false;
    boolean addedReflectImport = false;
    for (CodegenOperation operation : operations) {
      if (!addedOSImport && "*os.File".equals(operation.returnType)) {
        imports.add(createMapping("import", "os"));
        addedOSImport = true;
      }
      for (CodegenParameter param : operation.allParams) {
        if (!addedOSImport && "*os.File".equals(param.dataType)) {
          imports.add(createMapping("import", "os"));
          addedOSImport = true;
        }
        if (param.required) {
          if (!addedTimeImport && "time.Time".equals(param.dataType)) {
            imports.add(createMapping("import", "time"));
            addedTimeImport = true;
          }
        }
        if (!addedReflectImport && param.isCollectionFormatMulti) {
          imports.add(createMapping("import", "reflect"));
          addedReflectImport = true;
        }
        if (!param.required) {
          if (!addedOptionalImport) {
            imports.add(createMapping("import", "github.com/antihax/optional"));
            addedOptionalImport = true;
          }
          if ("time.Time".equals(param.dataType)) {
            param.vendorExtensions.put("x-optionalDataType", "Time");
          } else {
            param.vendorExtensions.put("x-optionalDataType", param.dataType.substring(0, 1).toUpperCase(Locale.ROOT) + param.dataType.substring(1));
          }
        }
        char nameFirstChar = param.paramName.charAt(0);
        if (Character.isUpperCase(nameFirstChar)) {
          param.vendorExtensions.put("x-exportParamName", param.paramName);
        } else {
          StringBuilder sb = new StringBuilder(param.paramName);
          sb.setCharAt(0, Character.toUpperCase(nameFirstChar));
          param.vendorExtensions.put("x-exportParamName", sb.toString());
        }
      }
      setExportParameterName(operation.queryParams);
      setExportParameterName(operation.formParams);
      setExportParameterName(operation.headerParams);
      setExportParameterName(operation.bodyParams);
      setExportParameterName(operation.cookieParams);
      setExportParameterName(operation.optionalParams);
      setExportParameterName(operation.requiredParams);
    }
    List<Map<String, String>> recursiveImports = (List<Map<String, String>>) objs.get("imports");
    if (recursiveImports == null) {
      return objs;
    }
    ListIterator<Map<String, String>> listIterator = imports.listIterator();
    while (listIterator.hasNext()) {
      String _import = listIterator.next().get("import");
      if (importMapping.containsKey(_import)) {
        listIterator.add(createMapping("import", importMapping.get(_import)));
      }
    }
    return objs;
  }

  private void setExportParameterName(List<CodegenParameter> codegenParameters) {
    for (CodegenParameter param : codegenParameters) {
      char nameFirstChar = param.paramName.charAt(0);
      if (Character.isUpperCase(nameFirstChar)) {
        param.vendorExtensions.put("x-exportParamName", param.paramName);
      } else {
        StringBuilder sb = new StringBuilder(param.paramName);
        sb.setCharAt(0, Character.toUpperCase(nameFirstChar));
        param.vendorExtensions.put("x-exportParamName", sb.toString());
      }
    }
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
    boolean addedTimeImport = false;
    boolean addedOSImport = false;
    List<Map<String, Object>> models = (List<Map<String, Object>>) objs.get("models");
    for (Map<String, Object> m : models) {
      Object v = m.get("model");
      if (v instanceof CodegenModel) {
        CodegenModel model = (CodegenModel) v;
        for (CodegenProperty param : model.vars) {
          if (!addedTimeImport && ("time.Time".equals(param.dataType) || ("[]time.Time".equals(param.dataType)))) {
            imports.add(createMapping("import", "time"));
            addedTimeImport = true;
          }
          if (!addedOSImport && "*os.File".equals(param.baseType)) {
            imports.add(createMapping("import", "os"));
            addedOSImport = true;
          }
        }
      }
    }
    List<Map<String, String>> recursiveImports = (List<Map<String, String>>) objs.get("imports");
    if (recursiveImports == null) {
      return objs;
    }
    ListIterator<Map<String, String>> listIterator = imports.listIterator();
    while (listIterator.hasNext()) {
      String _import = listIterator.next().get("import");
      if (importMapping.containsKey(_import)) {
        listIterator.add(createMapping("import", importMapping.get(_import)));
      }
    }
    return postProcessModelsEnum(objs);
  }

  @Override public Map<String, Object> postProcessSupportingFileData(Map<String, Object> objs) {
    generateYAMLSpecFile(objs);
    return super.postProcessSupportingFileData(objs);
  }

  @Override protected boolean needToImport(String type) {
    return !defaultIncludes.contains(type) && !languageSpecificPrimitives.contains(type);
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  @Override public String escapeQuotationMark(String input) {
    return input.replace("\"", "");
  }

  @Override public String escapeUnsafeCharacters(String input) {
    return input.replace("*/", "*_/").replace("/*", "/_*");
  }

  public Map<String, String> createMapping(String key, String value) {
    Map<String, String> customImport = new HashMap<String, String>();
    customImport.put(key, value);
    return customImport;
  }

  @Override public String toEnumValue(String value, String datatype) {
    if (isNumberType(datatype) || "bool".equals(datatype)) {
      return value;
    } else {
      return "\"" + escapeText(value) + "\"";
    }
  }

  @Override public String toEnumDefaultValue(String value, String datatype) {
    return datatype + "_" + value;
  }

  @Override public String toEnumVarName(String name, String datatype) {
    if (name.length() == 0) {
      return "EMPTY";
    }
    if (isNumberType(datatype)) {
      String varName = name;
      varName = varName.replaceAll("-", "MINUS_");
      varName = varName.replaceAll("\\+", "PLUS_");
      varName = varName.replaceAll("\\.", "_DOT_");
      return NUMERIC_ENUM_PREFIX + varName;
    }
    if (getSymbolName(name) != null) {
      return getSymbolName(name).toUpperCase(Locale.ROOT);
    }
    String enumName = sanitizeName(underscore(name).toUpperCase(Locale.ROOT));
    enumName = enumName.replaceFirst("^_", "");
    enumName = enumName.replaceFirst("_$", "");
    if (isReservedWord(enumName)) {
      return escapeReservedWord(enumName);
    } else {
      if (enumName.matches("\\d.*")) {
        return NUMERIC_ENUM_PREFIX + enumName;
      } else {
        return enumName;
      }
    }
  }

  @Override public String toEnumName(CodegenProperty property) {
    String enumName = underscore(toModelName(property.name)).toUpperCase(Locale.ROOT);
    enumName = enumName.replace("[]", "");
    if (enumName.matches("\\d.*")) {
      return NUMERIC_ENUM_PREFIX + enumName;
    } else {
      return enumName;
    }
  }

  public void setWithGoCodegenComment(boolean withGoCodegenComment) {
    this.withGoCodegenComment = withGoCodegenComment;
  }

  public void setWithXml(boolean withXml) {
    this.withXml = withXml;
  }

  public void setEnumClassPrefix(boolean enumClassPrefix) {
    this.enumClassPrefix = enumClassPrefix;
  }

  public void setStructPrefix(boolean structPrefix) {
    this.structPrefix = structPrefix;
  }

  @Override public String toDefaultValue(Schema schema) {
    if (schema.getDefault() != null) {
      return schema.getDefault().toString();
    } else {
      return null;
    }
  }

  @Override public void postProcessFile(File file, String fileType) {
    if (file == null) {
      return;
    }
    String goPostProcessFile = System.getenv("GO_POST_PROCESS_FILE");
    if (StringUtils.isEmpty(goPostProcessFile)) {
      return;
    }
    Set<String> supportedFileType = new HashSet<String>(Arrays.asList("supporting-mustache", "model-test", "model", "api-test", "api"));
    if (!supportedFileType.contains(fileType)) {
      return;
    }
    if ("go".equals(FilenameUtils.getExtension(file.toString()))) {
      String command = goPostProcessFile + " " + file.toString();
      try {
        Process p = Runtime.getRuntime().exec(command);
        int exitValue = p.waitFor();
        if (exitValue != 0) {
          LOGGER.error("Error running the command ({}). Exit code: {}", command, exitValue);
        } else {
          LOGGER.info("Successfully executed: " + command);
        }
      } catch (Exception e) {
        LOGGER.error("Error running the command ({}). Exception: {}", command, e.getMessage());
      }
    }
  }

  protected boolean isNumberType(String datatype) {
    return numberTypes.contains(datatype);
  }
}