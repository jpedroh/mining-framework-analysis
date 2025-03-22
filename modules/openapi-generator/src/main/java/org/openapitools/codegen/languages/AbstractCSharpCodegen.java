package org.openapitools.codegen.languages;
import com.google.common.collect.ImmutableMap.Builder;
import com.samskivert.mustache.Mustache.Lambda;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.*;
import org.openapitools.codegen.templating.mustache.*;
import org.openapitools.codegen.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;
import static org.openapitools.codegen.utils.StringUtils.camelize;

public abstract class AbstractCSharpCodegen extends DefaultCodegen implements CodegenConfig {
  protected boolean optionalAssemblyInfoFlag = true;

  protected boolean optionalEmitDefaultValuesFlag = false;

  protected boolean conditionalSerialization = false;

  protected boolean optionalProjectFileFlag = true;

  protected boolean optionalMethodArgumentFlag = true;

  protected boolean useDateTimeOffsetFlag = false;

  protected boolean useCollection = false;

  protected boolean returnICollection = false;

  protected boolean netCoreProjectFileFlag = false;

  protected boolean nullReferenceTypesFlag = false;

  protected String modelPropertyNaming = CodegenConstants.MODEL_PROPERTY_NAMING_TYPE.PascalCase.name();

  protected String licenseUrl = "http://localhost";

  protected String licenseName = "NoLicense";

  protected String packageVersion = "1.0.0";

  protected String packageName = "Org.OpenAPITools";

  protected String packageTitle = "OpenAPI Library";

  protected String packageProductName = "OpenAPILibrary";

  protected String packageDescription = "A library generated from a OpenAPI doc";

  protected String packageCompany = "OpenAPI";

  protected String packageCopyright = "No Copyright";

  protected String packageAuthors = "OpenAPI";

  protected String interfacePrefix = "I";

  protected String enumNameSuffix = "Enum";

  protected String enumValueSuffix = "Enum";

  protected String sourceFolder = "src";

  protected String testFolder = sourceFolder;

  protected Set<String> collectionTypes;

  protected Set<String> mapTypes;

  protected boolean supportNullable = Boolean.FALSE;

  protected Set<String> nullableType = new HashSet<String>();

  protected Set<String> valueTypes = new HashSet<String>();

  private final Logger LOGGER = LoggerFactory.getLogger(AbstractCSharpCodegen.class);

  public AbstractCSharpCodegen() {
    super();
    supportsInheritance = true;
    importMapping.clear();
    outputFolder = "generated-code" + File.separator + this.getName();
    embeddedTemplateDir = templateDir = this.getName();
    collectionTypes = new HashSet<String>(Arrays.asList("IList", "List", "ICollection", "Collection", "IEnumerable"));
    mapTypes = new HashSet<String>(Arrays.asList("IDictionary"));
    reservedWords.addAll(Arrays.asList("Client", "client", "parameter", "localVarPath", "localVarPathParams", "localVarQueryParams", "localVarHeaderParams", "localVarFormParams", "localVarFileParams", "localVarStatusCode", "localVarResponse", "localVarPostBody", "localVarHttpHeaderAccepts", "localVarHttpHeaderAccept", "localVarHttpContentTypes", "localVarHttpContentType", "localVarStatusCode", "abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char", "checked", "class", "const", "continue", "decimal", "default", "delegate", "do", "double", "else", "enum", "event", "explicit", "extern", "false", "finally", "fixed", "float", "for", "foreach", "goto", "if", "implicit", "in", "int", "interface", "internal", "is", "lock", "long", "namespace", "new", "null", "object", "operator", "out", "override", "params", "private", "protected", "public", "readonly", "ref", "return", "sbyte", "sealed", "short", "sizeof", "stackalloc", "static", "string", "struct", "switch", "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe", "ushort", "using", "virtual", "void", "volatile", "while"));
    languageSpecificPrimitives = new HashSet<String>(Arrays.asList("String", "string", "bool?", "bool", "double?", "double", "decimal?", "decimal", "int?", "int", "long?", "long", "float?", "float", "byte[]", "ICollection", "Collection", "List", "Dictionary", "DateTime?", "DateTime", "DateTimeOffset?", "DateTimeOffset", "Boolean", "Double", "Decimal", "Int32", "Int64", "Float", "Guid?", "Guid", "System.IO.Stream", "Object"));
    instantiationTypes.put("array", "List");
    instantiationTypes.put("list", "List");
    instantiationTypes.put("map", "Dictionary");
    typeMapping = new HashMap<String, String>();
    typeMapping.put("string", "string");
    typeMapping.put("binary", "byte[]");
    typeMapping.put("ByteArray", "byte[]");
    typeMapping.put("boolean", "bool?");
    typeMapping.put("integer", "int?");
    typeMapping.put("float", "float?");
    typeMapping.put("long", "long?");
    typeMapping.put("double", "double?");
    typeMapping.put("number", "decimal?");
    typeMapping.put("BigDecimal", "decimal?");
    typeMapping.put("DateTime", "DateTime?");
    typeMapping.put("date", "DateTime?");
    typeMapping.put("file", "System.IO.Stream");
    typeMapping.put("array", "List");
    typeMapping.put("list", "List");
    typeMapping.put("map", "Dictionary");
    typeMapping.put("object", "Object");
    typeMapping.put("UUID", "Guid?");
    typeMapping.put("URI", "string");
    typeMapping.put("AnyType", "Object");
    nullableType = new HashSet<String>(Arrays.asList("decimal", "bool", "int", "float", "long", "double", "DateTime", "DateTimeOffset", "Guid"));
    valueTypes = new HashSet<String>(Arrays.asList("decimal", "bool", "int", "float", "long", "double"));
  }

  public void setReturnICollection(boolean returnICollection) {
    this.returnICollection = returnICollection;
  }

  public void setUseCollection(boolean useCollection) {
    this.useCollection = useCollection;
    if (useCollection) {
      typeMapping.put("array", "Collection");
      typeMapping.put("list", "Collection");
      instantiationTypes.put("array", "Collection");
      instantiationTypes.put("list", "Collection");
    }
  }

  public void setOptionalMethodArgumentFlag(boolean flag) {
    this.optionalMethodArgumentFlag = flag;
  }

  public void setNetCoreProjectFileFlag(boolean flag) {
    this.netCoreProjectFileFlag = flag;
  }

  public void useDateTimeOffset(boolean flag) {
    this.useDateTimeOffsetFlag = flag;
    if (flag) {
      typeMapping.put("DateTime", "DateTimeOffset");
    } else {
      typeMapping.put("DateTime", "DateTime");
    }
  }

  @Override public void processOpts() {
    super.processOpts();
    if (StringUtils.isEmpty(System.getenv("CSHARP_POST_PROCESS_FILE"))) {
      LOGGER.info("Environment variable CSHARP_POST_PROCESS_FILE not defined so the C# code may not be properly formatted by uncrustify (0.66 or later) or other code formatter. To define it, try `export CSHARP_POST_PROCESS_FILE=\"/usr/local/bin/uncrustify --no-backup\" && export UNCRUSTIFY_CONFIG=/path/to/uncrustify-rules.cfg` (Linux/Mac). Note: replace /path/to with the location of uncrustify-rules.cfg");
      LOGGER.info("NOTE: To enable file post-processing, \'enablePostProcessFile\' must be set to `true` (--enable-post-process-file for CLI).");
    }
    if (additionalProperties.containsKey(CodegenConstants.LICENSE_URL)) {
      setLicenseUrl((String) additionalProperties.get(CodegenConstants.LICENSE_URL));
    } else {
      additionalProperties.put(CodegenConstants.LICENSE_URL, this.licenseUrl);
    }
    if (additionalProperties.containsKey(CodegenConstants.LICENSE_NAME)) {
      setLicenseName((String) additionalProperties.get(CodegenConstants.LICENSE_NAME));
    } else {
      additionalProperties.put(CodegenConstants.LICENSE_NAME, this.licenseName);
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_VERSION)) {
      setPackageVersion((String) additionalProperties.get(CodegenConstants.PACKAGE_VERSION));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_VERSION, packageVersion);
    }
    if (additionalProperties.containsKey(CodegenConstants.SOURCE_FOLDER)) {
      setSourceFolder((String) additionalProperties.get(CodegenConstants.SOURCE_FOLDER));
    } else {
      additionalProperties.put(CodegenConstants.SOURCE_FOLDER, this.sourceFolder);
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_NAME)) {
      setPackageName((String) additionalProperties.get(CodegenConstants.PACKAGE_NAME));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_NAME, packageName);
    }
    if (additionalProperties.containsKey(CodegenConstants.INVOKER_PACKAGE)) {
      LOGGER.warn(String.format(Locale.ROOT, "%s is not used by C# generators. Please use %s", CodegenConstants.INVOKER_PACKAGE, CodegenConstants.PACKAGE_NAME));
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_TITLE)) {
      setPackageTitle((String) additionalProperties.get(CodegenConstants.PACKAGE_TITLE));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_TITLE, packageTitle);
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_PRODUCTNAME)) {
      setPackageProductName((String) additionalProperties.get(CodegenConstants.PACKAGE_PRODUCTNAME));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_PRODUCTNAME, packageProductName);
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_DESCRIPTION)) {
      setPackageDescription((String) additionalProperties.get(CodegenConstants.PACKAGE_DESCRIPTION));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_DESCRIPTION, packageDescription);
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_COMPANY)) {
      setPackageCompany((String) additionalProperties.get(CodegenConstants.PACKAGE_COMPANY));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_COMPANY, packageCompany);
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_COPYRIGHT)) {
      setPackageCopyright((String) additionalProperties.get(CodegenConstants.PACKAGE_COPYRIGHT));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_COPYRIGHT, packageCopyright);
    }
    if (additionalProperties.containsKey(CodegenConstants.PACKAGE_AUTHORS)) {
      setPackageAuthors((String) additionalProperties.get(CodegenConstants.PACKAGE_AUTHORS));
    } else {
      additionalProperties.put(CodegenConstants.PACKAGE_AUTHORS, packageAuthors);
    }
    if (additionalProperties.containsKey(CodegenConstants.USE_DATETIME_OFFSET)) {
      useDateTimeOffset(convertPropertyToBooleanAndWriteBack(CodegenConstants.USE_DATETIME_OFFSET));
    } else {
      additionalProperties.put(CodegenConstants.USE_DATETIME_OFFSET, useDateTimeOffsetFlag);
    }
    if (additionalProperties.containsKey(CodegenConstants.USE_COLLECTION)) {
      setUseCollection(convertPropertyToBooleanAndWriteBack(CodegenConstants.USE_COLLECTION));
    } else {
      additionalProperties.put(CodegenConstants.USE_COLLECTION, useCollection);
    }
    if (additionalProperties.containsKey(CodegenConstants.RETURN_ICOLLECTION)) {
      setReturnICollection(convertPropertyToBooleanAndWriteBack(CodegenConstants.RETURN_ICOLLECTION));
    } else {
      additionalProperties.put(CodegenConstants.RETURN_ICOLLECTION, returnICollection);
    }
    if (additionalProperties.containsKey(CodegenConstants.NETCORE_PROJECT_FILE)) {
      setNetCoreProjectFileFlag(convertPropertyToBooleanAndWriteBack(CodegenConstants.NETCORE_PROJECT_FILE));
    } else {
      additionalProperties.put(CodegenConstants.NETCORE_PROJECT_FILE, netCoreProjectFileFlag);
    }
    if (additionalProperties.containsKey(CodegenConstants.NULLABLE_REFERENCE_TYPES)) {
      setNullableReferenceTypes(convertPropertyToBooleanAndWriteBack(CodegenConstants.NULLABLE_REFERENCE_TYPES));
    } else {
      additionalProperties.put(CodegenConstants.NULLABLE_REFERENCE_TYPES, nullReferenceTypesFlag);
    }
    if (additionalProperties.containsKey(CodegenConstants.INTERFACE_PREFIX)) {
      String useInterfacePrefix = additionalProperties.get(CodegenConstants.INTERFACE_PREFIX).toString();
      if ("false".equals(useInterfacePrefix.toLowerCase(Locale.ROOT))) {
        setInterfacePrefix("");
      } else {
        if (!"true".equals(useInterfacePrefix.toLowerCase(Locale.ROOT))) {
          setInterfacePrefix(sanitizeName(useInterfacePrefix));
        }
      }
    }
    if (additionalProperties().containsKey(CodegenConstants.ENUM_NAME_SUFFIX)) {
      setEnumNameSuffix(additionalProperties.get(CodegenConstants.ENUM_NAME_SUFFIX).toString());
    }
    if (additionalProperties().containsKey(CodegenConstants.ENUM_VALUE_SUFFIX)) {
      setEnumValueSuffix(additionalProperties.get(CodegenConstants.ENUM_VALUE_SUFFIX).toString());
    }
    additionalProperties.put(CodegenConstants.INTERFACE_PREFIX, interfacePrefix);
  }

  @Override protected Builder<String, Lambda> addMustacheLambdas() {
    return super.addMustacheLambdas().put("camelcase_param", new CamelCaseLambda().generator(this).escapeAsParamName(true));
  }

  @Override public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
    super.postProcessModelProperty(model, property);
  }

  @Override public Map<String, Object> postProcessModels(Map<String, Object> objs) {
    List<Object> models = (List<Object>) objs.get("models");
    for (Object _mo : models) {
      Map<String, Object> mo = (Map<String, Object>) _mo;
      CodegenModel cm = (CodegenModel) mo.get("model");
      for (CodegenProperty var : cm.vars) {
        if (var.name.equalsIgnoreCase(cm.classname)) {
          var.name = "_" + var.name;
        }
      }
    }
    return postProcessModelsEnum(objs);
  }

  /**
     * Invoked by {@link DefaultGenerator} after all models have been post-processed, allowing for a last pass of codegen-specific model cleanup.
     *
     * @param objs Current state of codegen object model.
     * @return An in-place modified state of the codegen object model.
     */
  @Override public Map<String, Object> postProcessAllModels(Map<String, Object> objs) {
    final Map<String, Object> processed = super.postProcessAllModels(objs);
    postProcessEnumRefs(processed);
    updateValueTypeProperty(processed);
    updateNullableTypeProperty(processed);
    return processed;
  }

  @Override protected List<Map<String, Object>> buildEnumVars(List<Object> values, String dataType) {
    List<Map<String, Object>> enumVars = super.buildEnumVars(values, dataType);
    if ("string?".equals(dataType)) {
      enumVars.forEach((enumVar) -> {
        enumVar.put("isString", true);
      });
    }
    return enumVars;
  }

  /**
     * C# differs from other languages in that Enums are not _true_ objects; enums are compiled to integral types.
     * So, in C#, an enum is considers more like a user-defined primitive.
     * <p>
     * When working with enums, we can't always assume a RefModel is a nullable type (where default(YourType) == null),
     * so this post processing runs through all models to find RefModel'd enums. Then, it runs through all vars and modifies
     * those vars referencing RefModel'd enums to work the same as inlined enums rather than as objects.
     *
     * @param models processed models to be further processed for enum references
     */
  @SuppressWarnings(value = { "unchecked" }) private void postProcessEnumRefs(final Map<String, Object> models) {
    Map<String, CodegenModel> enumRefs = new HashMap<String, CodegenModel>();
    for (Map.Entry<String, Object> entry : models.entrySet()) {
      CodegenModel model = ModelUtils.getModelByName(entry.getKey(), models);
      if (model.isEnum) {
        enumRefs.put(entry.getKey(), model);
      }
    }
    for (Map.Entry<String, Object> entry : models.entrySet()) {
      String openAPIName = entry.getKey();
      CodegenModel model = ModelUtils.getModelByName(openAPIName, models);
      if (model != null) {
        for (CodegenProperty var : model.allVars) {
          if (enumRefs.containsKey(var.dataType)) {
            CodegenModel refModel = enumRefs.get(var.dataType);
            var.allowableValues = refModel.allowableValues;
            var.isEnum = true;
            var.isPrimitiveType = true;
          }
        }
        for (CodegenProperty var : model.vars) {
          if (enumRefs.containsKey(var.dataType)) {
            CodegenModel refModel = enumRefs.get(var.dataType);
            var.allowableValues = refModel.allowableValues;
            var.isEnum = true;
            var.isPrimitiveType = true;
          }
        }
        for (CodegenProperty var : model.readWriteVars) {
          if (enumRefs.containsKey(var.dataType)) {
            CodegenModel refModel = enumRefs.get(var.dataType);
            var.allowableValues = refModel.allowableValues;
            var.isEnum = true;
            var.isPrimitiveType = true;
          }
        }
        for (CodegenProperty var : model.readOnlyVars) {
          if (enumRefs.containsKey(var.dataType)) {
            CodegenModel refModel = enumRefs.get(var.dataType);
            var.allowableValues = refModel.allowableValues;
            var.isEnum = true;
            var.isPrimitiveType = true;
          }
        }
      } else {
        LOGGER.warn("Expected to retrieve model %s by name, but no model was found. Check your -Dmodels inclusions.", openAPIName);
      }
    }
  }

  /**
     * Update codegen property's enum by adding "enumVars" (with name and value)
     *
     * @param var list of CodegenProperty
     */
  @Override public void updateCodegenPropertyEnum(CodegenProperty var) {
    if (var.vendorExtensions == null) {
      var.vendorExtensions = new HashMap<>();
    }
    super.updateCodegenPropertyEnum(var);
    if (var.isEnum) {
      if ("byte".equals(var.dataFormat)) {
        var.vendorExtensions.put("x-enum-byte", true);
        var.isString = false;
        var.isLong = false;
        var.isInteger = false;
      } else {
        if ("int".equals(var.dataType) || "int32".equals(var.dataFormat)) {
          var.isInteger = true;
          var.isString = false;
          var.isLong = false;
        } else {
          if ("int64".equals(var.dataFormat)) {
            var.isLong = true;
            var.isString = false;
            var.isInteger = false;
          } else {
            var.isString = true;
            var.isInteger = false;
            var.isLong = false;
          }
        }
      }
    }
  }

  /**
     * Update property if it is a C# value type
     *
     * @param models list of all models
     */
  protected void updateValueTypeProperty(Map<String, Object> models) {
    for (Map.Entry<String, Object> entry : models.entrySet()) {
      String openAPIName = entry.getKey();
      CodegenModel model = ModelUtils.getModelByName(openAPIName, models);
      if (model != null) {
        for (CodegenProperty var : model.vars) {
          var.vendorExtensions.put("x-is-value-type", isValueType(var));
        }
      }
    }
  }

  /**
     * Update property if it is a C# nullable type
     *
     * @param models list of all models
     */
  protected void updateNullableTypeProperty(Map<String, Object> models) {
    for (Map.Entry<String, Object> entry : models.entrySet()) {
      String openAPIName = entry.getKey();
      CodegenModel model = ModelUtils.getModelByName(openAPIName, models);
      if (model != null) {
        for (CodegenProperty var : model.vars) {
          if (!var.isContainer && (nullableType.contains(var.dataType) || var.isEnum)) {
            var.vendorExtensions.put("x-csharp-value-type", true);
          }
        }
      }
    }
  }

  @Override public Map<String, Object> postProcessOperationsWithModels(Map<String, Object> objs, List<Object> allModels) {
    super.postProcessOperationsWithModels(objs, allModels);
    if (objs != null) {
      Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
      if (operations != null) {
        List<CodegenOperation> ops = (List<CodegenOperation>) operations.get("operation");
        for (CodegenOperation operation : ops) {
          if (operation.returnType != null) {
            String typeMapping;
            int namespaceEnd = operation.returnType.lastIndexOf(".");
            if (namespaceEnd > 0) {
              typeMapping = operation.returnType.substring(namespaceEnd);
            } else {
              typeMapping = operation.returnType;
            }
            if (this.collectionTypes.contains(typeMapping)) {
              operation.isArray = true;
              operation.returnContainer = operation.returnType;
              if (this.returnICollection && (typeMapping.startsWith("List") || typeMapping.startsWith("Collection"))) {
                int genericStart = typeMapping.indexOf("<");
                if (genericStart > 0) {
                  operation.returnType = "ICollection" + typeMapping.substring(genericStart);
                }
              }
            } else {
              operation.returnContainer = operation.returnType;
              operation.isMap = this.mapTypes.contains(typeMapping);
            }
          }
          if (operation.consumes != null) {
            for (Map<String, String> consume : operation.consumes) {
              if (consume.containsKey("mediaType")) {
                if (isJsonMimeType(consume.get("mediaType"))) {
                  operation.vendorExtensions.put("x-is-json", true);
                  break;
                }
              }
            }
          }
          if (operation.examples != null) {
            for (Map<String, String> example : operation.examples) {
              for (Map.Entry<String, String> entry : example.entrySet()) {
                String val = entry.getValue().replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                entry.setValue(val);
              }
            }
          }
          if (!isSupportNullable()) {
            for (CodegenParameter parameter : operation.allParams) {
              CodegenModel model = null;
              for (Object modelHashMap : allModels) {
                CodegenModel codegenModel = ((HashMap<String, CodegenModel>) modelHashMap).get("model");
                if (codegenModel.getClassname().equals(parameter.dataType)) {
                  model = codegenModel;
                  break;
                }
              }
              if (model == null) {
                parameter.isNullable = true;
              } else {
                if (model.isEnum) {
                  parameter.isEnum = true;
                  parameter.allowableValues = model.allowableValues;
                  parameter.isPrimitiveType = true;
                  parameter.isNullable = false;
                } else {
                  parameter.isNullable = true;
                }
              }
            }
          } else {
            updateCodegenParametersEnum(operation.allParams, allModels);
          }
          processOperation(operation);
        }
      }
    }
    return objs;
  }

  protected void processOperation(CodegenOperation operation) {
  }

  private void updateCodegenParametersEnum(List<CodegenParameter> parameters, List<Object> allModels) {
    for (CodegenParameter parameter : parameters) {
      CodegenModel model = null;
      for (Object modelHashMap : allModels) {
        CodegenModel codegenModel = ((HashMap<String, CodegenModel>) modelHashMap).get("model");
        if (codegenModel.getClassname().equals(parameter.dataType)) {
          model = codegenModel;
          break;
        }
      }
      if (model != null) {
        if (model.isEnum) {
          parameter.isEnum = true;
          parameter.allowableValues = model.allowableValues;
          parameter.isPrimitiveType = true;
          parameter.vendorExtensions.put("x-csharp-value-type", true);
        }
      }
      if (!parameter.isContainer && nullableType.contains(parameter.dataType)) {
        parameter.vendorExtensions.put("x-csharp-value-type", true);
      }
      if (!parameter.required && parameter.vendorExtensions.get("x-csharp-value-type") != null) {
        parameter.dataType = parameter.dataType + "?";
      }
    }
  }

  @Override public String apiFileFolder() {
    return outputFolder + File.separator + sourceFolder + File.separator + packageName + File.separator + apiPackage();
  }

  @Override public String modelFileFolder() {
    return outputFolder + File.separator + sourceFolder + File.separator + packageName + File.separator + modelPackage();
  }

  @Override public String toModelFilename(String name) {
    return toModelName(name);
  }

  @Override public String toOperationId(String operationId) {
    if (StringUtils.isEmpty(operationId)) {
      throw new RuntimeException("Empty method name (operationId) not allowed");
    }
    if (isReservedWord(operationId)) {
      LOGGER.warn("{} (reserved word) cannot be used as method name. Renamed to {}", operationId, camelize(sanitizeName("call_" + operationId)));
      operationId = "call_" + operationId;
    }
    if (operationId.matches("^\\d.*")) {
      LOGGER.warn("{} (starting with a number) cannot be used as method name. Renamed to {}", operationId, camelize(sanitizeName("call_" + operationId)));
      operationId = "call_" + operationId;
    }
    return camelize(sanitizeName(operationId));
  }

  @Override public String toVarName(String name) {
    name = sanitizeName(name);
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
    name = sanitizeName(name);
    name = name.replaceAll("-", "_");
    if (name.matches("^[A-Z_]*$")) {
      return name;
    }
    name = camelize(name, true);
    if (isReservedWord(name) || name.matches("^\\d.*")) {
      name = escapeReservedWord(name);
    }
    return name;
  }

  @Override public String escapeReservedWord(String name) {
    if (this.reservedWordsMappings().containsKey(name)) {
      return this.reservedWordsMappings().get(name);
    }
    return "_" + name;
  }

  /**
     * Return the example value of the property
     *
     * @param p OpenAPI property object
     * @return string presentation of the example value of the property
     */
  @Override public String toExampleValue(Schema p) {
    if (ModelUtils.isStringSchema(p)) {
      if (p.getExample() != null) {
        return "\"" + p.getExample().toString() + "\"";
      }
    } else {
      if (ModelUtils.isBooleanSchema(p)) {
        if (p.getExample() != null) {
          return p.getExample().toString();
        }
      } else {
        if (ModelUtils.isDateSchema(p)) {
        } else {
          if (ModelUtils.isDateTimeSchema(p)) {
          } else {
            if (ModelUtils.isNumberSchema(p)) {
              if (p.getExample() != null) {
                return p.getExample().toString();
              }
            } else {
              if (ModelUtils.isIntegerSchema(p)) {
                if (p.getExample() != null) {
                  return p.getExample().toString();
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

  /**
     * Return the default value of the property
     * @param p OpenAPI property object
     * @return string presentation of the default value of the property
     */
  @Override public String toDefaultValue(Schema p) {
    if (ModelUtils.isBooleanSchema(p)) {
      if (p.getDefault() != null) {
        return p.getDefault().toString();
      }
    } else {
      if (ModelUtils.isDateSchema(p)) {
        if (p.getDefault() != null) {
          return "\"" + p.getDefault().toString() + "\"";
        }
      } else {
        if (ModelUtils.isDateTimeSchema(p)) {
          if (p.getDefault() != null) {
            return "\"" + p.getDefault().toString() + "\"";
          }
        } else {
          if (ModelUtils.isNumberSchema(p)) {
            if (p.getDefault() != null) {
              if (ModelUtils.isFloatSchema(p)) {
                return p.getDefault().toString() + "F";
              } else {
                if (ModelUtils.isDoubleSchema(p)) {
                  return p.getDefault().toString() + "D";
                } else {
                  return p.getDefault().toString() + "M";
                }
              }
            }
          } else {
            if (ModelUtils.isIntegerSchema(p)) {
              if (p.getDefault() != null) {
                return p.getDefault().toString();
              }
            } else {
              if (ModelUtils.isStringSchema(p)) {
                if (p.getDefault() != null) {
                  String _default = (String) p.getDefault();
                  if (p.getEnum() == null) {
                    return "\"" + _default + "\"";
                  } else {
                    return _default;
                  }
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

  @Override protected boolean isReservedWord(String word) {
    return reservedWords.contains(word);
  }

  public String getNullableType(Schema p, String type) {
    if (languageSpecificPrimitives.contains(type)) {
      return type;
    } else {
      return null;
    }
  }

  @Override public String getSchemaType(Schema p) {
    String openAPIType = super.getSchemaType(p);
    String type;
    if (openAPIType == null) {
      LOGGER.error("OpenAPI Type for {} is null. Default to UNKNOWN_OPENAPI_TYPE instead.", p.getName());
      openAPIType = "UNKNOWN_OPENAPI_TYPE";
    }
    if (typeMapping.containsKey(openAPIType)) {
      type = typeMapping.get(openAPIType);
      String languageType = getNullableType(p, type);
      if (languageType != null) {
        return languageType;
      }
    } else {
      type = openAPIType;
    }
    return toModelName(type);
  }

  /**
     * Provides C# strongly typed declaration for simple arrays of some type and arrays of arrays of some type.
     *
     * @param arr The input array property
     * @return The type declaration when the type is an array of arrays.
     */
  private String getArrayTypeDeclaration(ArraySchema arr) {
    String arrayType = typeMapping.get("array");
    StringBuilder instantiationType = new StringBuilder(arrayType);
    Schema items = arr.getItems();
    String nestedType = getTypeDeclaration(items);
    instantiationType.append("<").append(nestedType).append(">");
    return instantiationType.toString();
  }

  @Override public String toInstantiationType(Schema p) {
    if (ModelUtils.isArraySchema(p)) {
      return getArrayTypeDeclaration((ArraySchema) p);
    }
    return super.toInstantiationType(p);
  }

  @Override public String getTypeDeclaration(Schema p) {
    if (ModelUtils.isArraySchema(p)) {
      return getArrayTypeDeclaration((ArraySchema) p);
    } else {
      if (ModelUtils.isMapSchema(p)) {
        Schema inner = getAdditionalProperties(p);
        return getSchemaType(p) + "<string, " + getTypeDeclaration(inner) + ">";
      }
    }
    return super.getTypeDeclaration(p);
  }

  @Override public String toModelName(String name) {
    if (importMapping.containsKey(name)) {
      return importMapping.get(name);
    }
    if (!StringUtils.isEmpty(modelNamePrefix)) {
      name = modelNamePrefix + "_" + name;
    }
    if (!StringUtils.isEmpty(modelNameSuffix)) {
      name = name + "_" + modelNameSuffix;
    }
    name = sanitizeName(name);
    if (isReservedWord(name)) {
      LOGGER.warn("{} (reserved word) cannot be used as model name. Renamed to {}", name, camelize("model_" + name));
      name = "model_" + name;
    }
    if (name.matches("^\\d.*")) {
      LOGGER.warn("{} (model name starts with number) cannot be used as model name. Renamed to {}", name, camelize("model_" + name));
      name = "model_" + name;
    }
    return camelize(name);
  }

  @Override public String apiTestFileFolder() {
    return outputFolder + ".Test";
  }

  @Override public String modelTestFileFolder() {
    return outputFolder + ".Test";
  }

  @Override public String toApiTestFilename(String name) {
    return toApiName(name) + "Tests";
  }

  @Override public String toModelTestFilename(String name) {
    return toModelName(name) + "Tests";
  }

  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

  public void setLicenseName(String licenseName) {
    this.licenseName = licenseName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public void setPackageVersion(String packageVersion) {
    this.packageVersion = packageVersion;
  }

  public void setPackageTitle(String packageTitle) {
    this.packageTitle = packageTitle;
  }

  public void setPackageProductName(String packageProductName) {
    this.packageProductName = packageProductName;
  }

  public void setPackageDescription(String packageDescription) {
    this.packageDescription = packageDescription;
  }

  public void setPackageCompany(String packageCompany) {
    this.packageCompany = packageCompany;
  }

  public void setPackageCopyright(String packageCopyright) {
    this.packageCopyright = packageCopyright;
  }

  public void setPackageAuthors(String packageAuthors) {
    this.packageAuthors = packageAuthors;
  }

  public void setSourceFolder(String sourceFolder) {
    this.sourceFolder = sourceFolder;
  }

  public String getInterfacePrefix() {
    return interfacePrefix;
  }

  public void setNullableReferenceTypes(final Boolean nullReferenceTypesFlag) {
    this.nullReferenceTypesFlag = nullReferenceTypesFlag;
    if (nullReferenceTypesFlag == true) {
      this.nullableType.add("string");
    }
  }

  public void setInterfacePrefix(final String interfacePrefix) {
    this.interfacePrefix = interfacePrefix;
  }

  public void setEnumNameSuffix(final String enumNameSuffix) {
    this.enumNameSuffix = enumNameSuffix;
  }

  public void setEnumValueSuffix(final String enumValueSuffix) {
    this.enumValueSuffix = enumValueSuffix;
  }

  public boolean isSupportNullable() {
    return supportNullable;
  }

  public void setSupportNullable(final boolean supportNullable) {
    this.supportNullable = supportNullable;
  }

  @Override public String toEnumValue(String value, String datatype) {
    if (datatype.startsWith("int") || datatype.startsWith("long") || datatype.startsWith("byte")) {
      return value;
    }
    return escapeText(value);
  }

  @Override public String toEnumVarName(String name, String datatype) {
    if (name.length() == 0) {
      return "Empty";
    }
    if (getSymbolName(name) != null) {
      return camelize(getSymbolName(name));
    }
    String enumName = sanitizeName(name);
    enumName = enumName.replaceFirst("^_", "");
    enumName = enumName.replaceFirst("_$", "");
    enumName = camelize(enumName) + this.enumValueSuffix;
    if (enumName.matches("\\d.*")) {
      return "_" + enumName;
    } else {
      return enumName;
    }
  }

  @Override public String toEnumName(CodegenProperty property) {
    return sanitizeName(camelize(property.name)) + this.enumNameSuffix;
  }

  public String testPackageName() {
    return this.packageName + ".Test";
  }

  @Override public String escapeQuotationMark(String input) {
    return input.replace("\"", "");
  }

  @Override public String escapeUnsafeCharacters(String input) {
    return input.replace("*/", "*_/").replace("/*", "/_*").replace("--", "- -");
  }

  @Override public boolean isDataTypeString(String dataType) {
    return "String".equalsIgnoreCase(dataType) || "double?".equals(dataType) || "decimal?".equals(dataType) || "float?".equals(dataType) || "double".equals(dataType) || "decimal".equals(dataType) || "float".equals(dataType);
  }

  /**
     * Return true if the property being passed is a C# value type
     *
     * @param var property
     * @return true if property is a value type
     */
  protected boolean isValueType(CodegenProperty var) {
    return (valueTypes.contains(var.dataType) || var.isEnum);
  }

  @Override public void setParameterExampleValue(CodegenParameter codegenParameter) {
    if (codegenParameter.vendorExtensions != null && codegenParameter.vendorExtensions.containsKey("x-example")) {
      codegenParameter.example = Json.pretty(codegenParameter.vendorExtensions.get("x-example"));
    } else {
      if (Boolean.TRUE.equals(codegenParameter.isBoolean)) {
        codegenParameter.example = "true";
      } else {
        if (Boolean.TRUE.equals(codegenParameter.isLong)) {
          codegenParameter.example = "789";
        } else {
          if (Boolean.TRUE.equals(codegenParameter.isInteger)) {
            codegenParameter.example = "56";
          } else {
            if (Boolean.TRUE.equals(codegenParameter.isFloat)) {
              codegenParameter.example = "3.4F";
            } else {
              if (Boolean.TRUE.equals(codegenParameter.isDouble)) {
                codegenParameter.example = "1.2D";
              } else {
                if (Boolean.TRUE.equals(codegenParameter.isNumber)) {
                  codegenParameter.example = "8.14";
                } else {
                  if (Boolean.TRUE.equals(codegenParameter.isBinary)) {
                    codegenParameter.example = "BINARY_DATA_HERE";
                  } else {
                    if (Boolean.TRUE.equals(codegenParameter.isByteArray)) {
                      codegenParameter.example = "BYTE_ARRAY_DATA_HERE";
                    } else {
                      if (Boolean.TRUE.equals(codegenParameter.isFile)) {
                        codegenParameter.example = "/path/to/file.txt";
                      } else {
                        if (Boolean.TRUE.equals(codegenParameter.isDate)) {
                          codegenParameter.example = "2013-10-20";
                        } else {
                          if (Boolean.TRUE.equals(codegenParameter.isDateTime)) {
                            codegenParameter.example = "2013-10-20T19:20:30+01:00";
                          } else {
                            if (Boolean.TRUE.equals(codegenParameter.isUuid)) {
                              codegenParameter.example = "38400000-8cf0-11bd-b23e-10b96e4ef00d";
                            } else {
                              if (Boolean.TRUE.equals(codegenParameter.isUri)) {
                                codegenParameter.example = "https://openapi-generator.tech";
                              } else {
                                if (Boolean.TRUE.equals(codegenParameter.isString)) {
                                  codegenParameter.example = codegenParameter.paramName + "_example";
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  @Override public void postProcessParameter(CodegenParameter parameter) {
    super.postProcessParameter(parameter);
    if (!parameter.required && (nullReferenceTypesFlag || nullableType.contains(parameter.dataType))) {
      parameter.dataType = parameter.dataType.endsWith("?") ? parameter.dataType : parameter.dataType + "?";
    }
  }

  @Override public void postProcessFile(File file, String fileType) {
    if (file == null) {
      return;
    }
    String csharpPostProcessFile = System.getenv("CSHARP_POST_PROCESS_FILE");
    if (StringUtils.isEmpty(csharpPostProcessFile)) {
      return;
    }
    if ("cs".equals(FilenameUtils.getExtension(file.toString()))) {
      String command = csharpPostProcessFile + " " + file.toString();
      try {
        Process p = Runtime.getRuntime().exec(command);
        int exitValue = p.waitFor();
        if (exitValue != 0) {
          LOGGER.error("Error running the command ({}). Exit code: {}", command, exitValue);
        } else {
          LOGGER.info("Successfully executed: {}", command);
        }
      } catch (InterruptedException | IOException e) {
        LOGGER.error("Error running the command ({}). Exception: {}", command, e.getMessage());
        Thread.currentThread().interrupt();
      }
    }
  }
}