package org.openapitools.codegen.languages;
import static org.openapitools.codegen.utils.StringUtils.camelize;
import static org.openapitools.codegen.utils.StringUtils.escape;
import static org.openapitools.codegen.utils.StringUtils.underscore;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenDiscriminator;
import org.openapitools.codegen.CodegenMediaType;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.GeneratorLanguage;
import org.openapitools.codegen.IJsonSchemaValidationProperties;
import org.openapitools.codegen.meta.features.SecurityFeature;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.curiousoddman.rgxgen.RgxGen;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;

public abstract class AbstractPythonCodegen extends DefaultCodegen implements CodegenConfig {
  private final Logger LOGGER = LoggerFactory.getLogger(AbstractPythonCodegen.class);

  public static final String MAP_NUMBER_TO = "mapNumberTo";

  protected String packageName = "openapi_client";

  protected String packageVersion = "1.0.0";

  protected String projectName;

  protected boolean hasModelsToImport = Boolean.FALSE;

  protected String mapNumberTo = "Union[StrictFloat, StrictInt]";

  protected Map<Character, String> regexModifiers;

  private Map<String, String> schemaKeyToModelNameCache = new HashMap<>();

  private HashMap<String, HashSet<String>> circularImports = new HashMap<>();

  private HashMap<String, CodegenModel> codegenModelMap = new HashMap<>();

  public AbstractPythonCodegen() {
    super();
    modifyFeatureSet((features) -> features.securityFeatures(EnumSet.of(SecurityFeature.BasicAuth, SecurityFeature.BearerToken, SecurityFeature.ApiKey, SecurityFeature.OAuth2_Implicit)));
    setReservedWordsLowerCase(Arrays.asList("all_params", "resource_path", "path_params", "query_params", "header_params", "form_params", "local_var_files", "body_params", "auth_settings", "property", "schema", "base64", "json", "date", "float", "and", "del", "from", "not", "while", "as", "elif", "global", "or", "with", "assert", "else", "if", "pass", "yield", "break", "except", "import", "print", "class", "exec", "in", "raise", "continue", "finally", "is", "return", "def", "for", "lambda", "try", "self", "nonlocal", "None", "True", "False", "async", "await"));
    languageSpecificPrimitives.clear();
    languageSpecificPrimitives.add("int");
    languageSpecificPrimitives.add("float");
    languageSpecificPrimitives.add("list");
    languageSpecificPrimitives.add("dict");
    languageSpecificPrimitives.add("List");
    languageSpecificPrimitives.add("Dict");
    languageSpecificPrimitives.add("bool");
    languageSpecificPrimitives.add("str");
    languageSpecificPrimitives.add("datetime");
    languageSpecificPrimitives.add("date");
    languageSpecificPrimitives.add("object");
    languageSpecificPrimitives.add("file");
    languageSpecificPrimitives.add("bytes");
    typeMapping.clear();
    typeMapping.put("integer", "int");
    typeMapping.put("float", "float");
    typeMapping.put("number", "float");
    typeMapping.put("long", "int");
    typeMapping.put("double", "float");
    typeMapping.put("array", "list");
    typeMapping.put("set", "list");
    typeMapping.put("map", "dict");
    typeMapping.put("boolean", "bool");
    typeMapping.put("string", "str");
    typeMapping.put("date", "date");
    typeMapping.put("DateTime", "datetime");
    typeMapping.put("object", "object");
    typeMapping.put("AnyType", "object");
    typeMapping.put("file", "file");
    typeMapping.put("binary", "str");
    typeMapping.put("ByteArray", "str");
    typeMapping.put("UUID", "str");
    typeMapping.put("URI", "str");
    typeMapping.put("null", "none_type");
    regexModifiers = new HashMap<Character, String>();
    regexModifiers.put('i', "IGNORECASE");
    regexModifiers.put('l', "LOCALE");
    regexModifiers.put('m', "MULTILINE");
    regexModifiers.put('s', "DOTALL");
    regexModifiers.put('u', "UNICODE");
    regexModifiers.put('x', "VERBOSE");
  }

  @Override public void processOpts() {
    super.processOpts();
    if (StringUtils.isEmpty(System.getenv("PYTHON_POST_PROCESS_FILE"))) {
      LOGGER.info("Environment variable PYTHON_POST_PROCESS_FILE not defined so the Python code may not be properly formatted. To define it, try \'export PYTHON_POST_PROCESS_FILE=\"/usr/local/bin/yapf -i\"\' (Linux/Mac)");
      LOGGER.info("NOTE: To enable file post-processing, \'enablePostProcessFile\' must be set to `true` (--enable-post-process-file for CLI).");
    }
  }

  @Override public String escapeReservedWord(String name) {
    if (this.reservedWordsMappings().containsKey(name)) {
      return this.reservedWordsMappings().get(name);
    }
    return "_" + name;
  }

  /**
     * Return the default value of the property
     *
     * @param p OpenAPI property object
     * @return string presentation of the default value of the property
     */
  @Override public String toDefaultValue(Schema p) {
    if (ModelUtils.isBooleanSchema(p)) {
      if (p.getDefault() != null) {
        if (!Boolean.valueOf(p.getDefault().toString())) {
          return "False";
        } else {
          return "True";
        }
      }
    } else {
      if (ModelUtils.isDateSchema(p)) {
      } else {
        if (ModelUtils.isDateTimeSchema(p)) {
        } else {
          if (ModelUtils.isNumberSchema(p)) {
            if (p.getDefault() != null) {
              return p.getDefault().toString();
            }
          } else {
            if (ModelUtils.isIntegerSchema(p)) {
              if (p.getDefault() != null) {
                return p.getDefault().toString();
              }
            } else {
              if (ModelUtils.isStringSchema(p)) {
                if (p.getDefault() != null) {
                  String defaultValue = String.valueOf(p.getDefault());
                  if (defaultValue != null) {
                    defaultValue = defaultValue.replace("\\", "\\\\").replace("\'", "\'");
                    if (Pattern.compile("\r\n|\r|\n").matcher(defaultValue).find()) {
                      return "\'\'\'" + defaultValue + "\'\'\'";
                    } else {
                      return "\'" + defaultValue + "\'";
                    }
                  }
                }
              } else {
                if (ModelUtils.isArraySchema(p)) {
                  if (p.getDefault() != null) {
                    return p.getDefault().toString();
                  } else {
                    return null;
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

  @Override public String toVarName(String name) {
    if (nameMapping.containsKey(name)) {
      return nameMapping.get(name);
    }
    name = sanitizeName(name);
    name = name.replace("$", "");
    if (name.matches("^[A-Z_]*$")) {
      name = name.toLowerCase(Locale.ROOT);
    }
    name = underscore(name);
    name = name.replaceAll("^_*", "");
    if (isReservedWord(name) || name.matches("^\\d.*")) {
      name = escapeReservedWord(name);
    }
    return name;
  }

  @Override public String toRegularExpression(String pattern) {
    return addRegularExpressionDelimiter(pattern);
  }

  @Override public String toParamName(String name) {
    if (parameterNameMapping.containsKey(name)) {
      return parameterNameMapping.get(name);
    }
    if ("callback".equals(name)) {
      return "param_callback";
    }
    return toVarName(name);
  }

  @Override public String toOperationId(String operationId) {
    if (StringUtils.isEmpty(operationId)) {
      throw new RuntimeException("Empty method name (operationId) not allowed");
    }
    if (isReservedWord(operationId)) {
      LOGGER.warn("{} (reserved word) cannot be used as method name. Renamed to {}", operationId, underscore(sanitizeName("call_" + operationId)));
      operationId = "call_" + operationId;
    }
    if (operationId.matches("^\\d.*")) {
      LOGGER.warn("{} (starting with a number) cannot be used as method name. Renamed to {}", operationId, underscore(sanitizeName("call_" + operationId)));
      operationId = "call_" + operationId;
    }
    return underscore(sanitizeName(operationId));
  }

  @Override public String escapeQuotationMark(String input) {
    return input.replace("\'", "");
  }

  @Override public String escapeUnsafeCharacters(String input) {
    return input.replace("\'\'\'", "\'_\'_\'");
  }

  @Override public void postProcessFile(File file, String fileType) {
    if (file == null) {
      return;
    }
    String pythonPostProcessFile = System.getenv("PYTHON_POST_PROCESS_FILE");
    if (StringUtils.isEmpty(pythonPostProcessFile)) {
      return;
    }
    if ("py".equals(FilenameUtils.getExtension(file.toString()))) {
      String command = pythonPostProcessFile + " " + file;
      try {
        Process p = Runtime.getRuntime().exec(command);
        int exitValue = p.waitFor();
        if (exitValue != 0) {
          LOGGER.error("Error running the command ({}). Exit value: {}", command, exitValue);
        } else {
          LOGGER.info("Successfully executed: {}", command);
        }
      } catch (InterruptedException | IOException e) {
        LOGGER.error("Error running the command ({}). Exception: {}", command, e.getMessage());
        Thread.currentThread().interrupt();
      }
    }
  }

  @Override public String toExampleValue(Schema schema) {
    return toExampleValueRecursive(schema, new ArrayList<>(), 5);
  }

  private String toExampleValueRecursive(Schema schema, List<Schema> includedSchemas, int indentation) {
    boolean cycleFound = includedSchemas.stream().filter((s) -> schema.equals(s)).count() > 1;
    if (cycleFound) {
      return "";
    }
    String indentationString = "";
    for (int i = 0; i < indentation; i++) {
      indentationString += "    ";
    }
    String example = null;
    if (schema.getExample() != null) {
      example = schema.getExample().toString();
    }
    if (ModelUtils.isNullType(schema) && null != example) {
      return "None";
    }
    if (ModelUtils.isBooleanSchema(schema) && null != example) {
      if ("false".equalsIgnoreCase(example)) {
        example = "False";
      } else {
        example = "True";
      }
    }
    if (ModelUtils.isStringSchema(schema) && schema.getDefault() != null && !ModelUtils.isDateSchema(schema) && !ModelUtils.isDateTimeSchema(schema)) {
      example = String.valueOf(schema.getDefault());
    }
    if (StringUtils.isNotBlank(example) && !"null".equals(example)) {
      if (ModelUtils.isStringSchema(schema)) {
        example = "\'" + example + "\'";
      }
      return example;
    }
    if (schema.getEnum() != null && !schema.getEnum().isEmpty()) {
      example = schema.getEnum().get(0).toString();
      if (ModelUtils.isStringSchema(schema)) {
        example = "\'" + escapeText(example) + "\'";
      }
      if (null == example) {
        LOGGER.warn("Empty enum. Cannot built an example!");
      }
      return example;
    } else {
      if (null != schema.get$ref()) {
        Map<String, Schema> allDefinitions = ModelUtils.getSchemas(this.openAPI);
        String ref = ModelUtils.getSimpleRef(schema.get$ref());
        if (allDefinitions != null) {
          Schema refSchema = allDefinitions.get(ref);
          if (null == refSchema) {
            return "None";
          } else {
            String refTitle = refSchema.getTitle();
            if (StringUtils.isBlank(refTitle) || "null".equals(refTitle)) {
              refSchema.setTitle(ref);
            }
            if (StringUtils.isNotBlank(schema.getTitle()) && !"null".equals(schema.getTitle())) {
              includedSchemas.add(schema);
            }
            return toExampleValueRecursive(refSchema, includedSchemas, indentation);
          }
        } else {
          LOGGER.warn("allDefinitions not defined in toExampleValue!\n");
        }
      }
    }
    if (ModelUtils.isDateSchema(schema)) {
      example = "datetime.datetime.strptime(\'1975-12-30\', \'%Y-%m-%d\').date()";
      return example;
    } else {
      if (ModelUtils.isDateTimeSchema(schema)) {
        example = "datetime.datetime.strptime(\'2013-10-20 19:20:30.00\', \'%Y-%m-%d %H:%M:%S.%f\')";
        return example;
      } else {
        if (ModelUtils.isBinarySchema(schema)) {
          example = "bytes(b\'blah\')";
          return example;
        } else {
          if (ModelUtils.isByteArraySchema(schema)) {
            example = "YQ==";
          } else {
            if (ModelUtils.isStringSchema(schema)) {
              if ("Number".equalsIgnoreCase(schema.getFormat())) {
                return "1";
              }
              if (StringUtils.isNotBlank(schema.getPattern())) {
                String pattern = schema.getPattern();
                RgxGen rgxGen = new RgxGen(patternCorrection(pattern));
                Random random = new Random(18);
                String sample = rgxGen.generate(random);
                Pattern valueExtractor = Pattern.compile("^/\\^?(.+?)\\$?/.?$");
                Matcher m = valueExtractor.matcher(sample);
                if (m.find()) {
                  example = m.group(m.groupCount());
                } else {
                  example = sample;
                }
              }
              if (example == null) {
                example = "";
              }
              int len = 0;
              if (null != schema.getMinLength()) {
                len = schema.getMinLength().intValue();
                if (len < 1) {
                  example = "";
                } else {
                  for (int i = 0; i < len; i++) {
                    example += i;
                  }
                }
              }
            } else {
              if (ModelUtils.isIntegerSchema(schema)) {
                if (schema.getMinimum() != null) {
                  example = schema.getMinimum().toString();
                } else {
                  example = "56";
                }
              } else {
                if (ModelUtils.isNumberSchema(schema)) {
                  if (schema.getMinimum() != null) {
                    example = schema.getMinimum().toString();
                  } else {
                    example = "1.337";
                  }
                } else {
                  if (ModelUtils.isBooleanSchema(schema)) {
                    example = "True";
                  } else {
                    if (ModelUtils.isArraySchema(schema)) {
                      if (StringUtils.isNotBlank(schema.getTitle()) && !"null".equals(schema.getTitle())) {
                        includedSchemas.add(schema);
                      }
                      ArraySchema arrayschema = (ArraySchema) schema;
                      example = "[\n" + indentationString + toExampleValueRecursive(arrayschema.getItems(), includedSchemas, indentation + 1) + "\n" + indentationString + "]";
                    } else {
                      if (ModelUtils.isMapSchema(schema)) {
                        if (StringUtils.isNotBlank(schema.getTitle()) && !"null".equals(schema.getTitle())) {
                          includedSchemas.add(schema);
                        }
                        Object additionalObject = schema.getAdditionalProperties();
                        if (additionalObject instanceof Schema) {
                          Schema additional = (Schema) additionalObject;
                          String theKey = "\'key\'";
                          if (additional.getEnum() != null && !additional.getEnum().isEmpty()) {
                            theKey = additional.getEnum().get(0).toString();
                            if (ModelUtils.isStringSchema(additional)) {
                              theKey = "\'" + escapeText(theKey) + "\'";
                            }
                          }
                          example = "{\n" + indentationString + theKey + " : " + toExampleValueRecursive(additional, includedSchemas, indentation + 1) + "\n" + indentationString + "}";
                        } else {
                          example = "{ }";
                        }
                      } else {
                        if (ModelUtils.isObjectSchema(schema)) {
                          if (StringUtils.isBlank(schema.getTitle())) {
                            example = "None";
                            return example;
                          }
                          String toExclude = null;
                          if (schema.getDiscriminator() != null) {
                            toExclude = schema.getDiscriminator().getPropertyName();
                          }
                          example = packageName + ".models." + underscore(schema.getTitle()) + "." + schema.getTitle() + "(";
                          List<String> reqs = new ArrayList<>();
                          if (schema.getProperties() != null && !schema.getProperties().isEmpty()) {
                            for (Object toAdd : schema.getProperties().keySet()) {
                              reqs.add((String) toAdd);
                            }
                            Map<String, Schema> properties = schema.getProperties();
                            Set<String> propkeys = null;
                            if (properties != null) {
                              propkeys = properties.keySet();
                            }
                            if (toExclude != null && reqs.contains(toExclude)) {
                              reqs.remove(toExclude);
                            }
                            for (String toRemove : includedSchemas.stream().map(Schema::getTitle).collect(Collectors.toList())) {
                              if (reqs.contains(toRemove)) {
                                reqs.remove(toRemove);
                              }
                            }
                            if (StringUtils.isNotBlank(schema.getTitle()) && !"null".equals(schema.getTitle())) {
                              includedSchemas.add(schema);
                            }
                            if (null != schema.getRequired()) {
                              for (Object toAdd : schema.getRequired()) {
                                reqs.add((String) toAdd);
                              }
                            }
                            if (null != propkeys) {
                              for (String propname : propkeys) {
                                Schema schema2 = properties.get(propname);
                                if (reqs.contains(propname)) {
                                  String refTitle = schema2.getTitle();
                                  if (StringUtils.isBlank(refTitle) || "null".equals(refTitle)) {
                                    schema2.setTitle(propname);
                                  }
                                  example += "\n" + indentationString + underscore(propname) + " = " + toExampleValueRecursive(schema2, includedSchemas, indentation + 1) + ", ";
                                }
                              }
                            }
                          }
                          example += ")";
                        } else {
                          LOGGER.debug("Type {} not handled properly in toExampleValue", schema.getType());
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
    if (ModelUtils.isStringSchema(schema)) {
      example = "\'" + escapeText(example) + "\'";
    }
    return example;
  }

  @Override public void setParameterExampleValue(CodegenParameter p) {
    String example;
    if (p.defaultValue == null) {
      example = p.example;
    } else {
      p.example = p.defaultValue;
      return;
    }
    String type = p.baseType;
    if (type == null) {
      type = p.dataType;
    }
    if ("String".equalsIgnoreCase(type) || "str".equalsIgnoreCase(type)) {
      if (example == null) {
        example = p.paramName + "_example";
      }
      example = "\'" + escapeText(example) + "\'";
    } else {
      if ("Integer".equals(type) || "int".equals(type)) {
        if (example == null) {
          example = "56";
        }
      } else {
        if ("Float".equalsIgnoreCase(type) || "Double".equalsIgnoreCase(type)) {
          if (example == null) {
            example = "3.4";
          }
        } else {
          if ("BOOLEAN".equalsIgnoreCase(type) || "bool".equalsIgnoreCase(type)) {
            if (example == null) {
              example = "True";
            }
          } else {
            if ("file".equalsIgnoreCase(type)) {
              if (example == null) {
                example = "/path/to/file";
              }
              example = "\'" + escapeText(example) + "\'";
            } else {
              if ("Date".equalsIgnoreCase(type)) {
                if (example == null) {
                  example = "2013-10-20";
                }
                example = "\'" + escapeText(example) + "\'";
              } else {
                if ("DateTime".equalsIgnoreCase(type)) {
                  if (example == null) {
                    example = "2013-10-20T19:20:30+01:00";
                  }
                  example = "\'" + escapeText(example) + "\'";
                } else {
                  if (!languageSpecificPrimitives.contains(type)) {
                    example = this.packageName + "." + type + "()";
                  } else {
                    LOGGER.debug("Type {} not handled properly in setParameterExampleValue", type);
                  }
                }
              }
            }
          }
        }
      }
    }
    if (example == null) {
      example = "None";
    } else {
      if (Boolean.TRUE.equals(p.isArray)) {
        example = "[" + example + "]";
      } else {
        if (Boolean.TRUE.equals(p.isMap)) {
          example = "{\'key\': " + example + "}";
        }
      }
    }
    p.example = example;
  }

  @Override public void setParameterExampleValue(CodegenParameter codegenParameter, Parameter parameter) {
    Schema schema = parameter.getSchema();
    if (parameter.getExample() != null) {
      codegenParameter.example = parameter.getExample().toString();
    } else {
      if (parameter.getExamples() != null && !parameter.getExamples().isEmpty()) {
        Example example = parameter.getExamples().values().iterator().next();
        if (example.getValue() != null) {
          codegenParameter.example = example.getValue().toString();
        }
      } else {
        if (schema != null && schema.getExample() != null) {
          codegenParameter.example = schema.getExample().toString();
        }
      }
    }
    setParameterExampleValue(codegenParameter);
  }

  @Override public String sanitizeTag(String tag) {
    return sanitizeName(tag);
  }

  public String patternCorrection(String pattern) {
    boolean checkEnding = pattern.endsWith("/i") || pattern.endsWith("/g") || pattern.endsWith("/m");
    if (checkEnding) {
      pattern = pattern.substring(0, pattern.length() - 2);
    }
    if (pattern.endsWith("/")) {
      pattern = pattern.substring(0, pattern.length() - 1);
    }
    if (pattern.startsWith("/")) {
      pattern = pattern.substring(1);
    }
    return pattern;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
    additionalProperties.put(CodegenConstants.PACKAGE_NAME, this.packageName);
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public void setPackageVersion(String packageVersion) {
    this.packageVersion = packageVersion;
  }

  @Override public String getTypeDeclaration(Schema p) {
    p = ModelUtils.unaliasSchema(openAPI, p);
    if (ModelUtils.isArraySchema(p)) {
      ArraySchema ap = (ArraySchema) p;
      Schema inner = ap.getItems();
      return getSchemaType(p) + "[" + getTypeDeclaration(inner) + "]";
    } else {
      if (ModelUtils.isMapSchema(p)) {
        Schema inner = ModelUtils.getAdditionalProperties(p);
        return getSchemaType(p) + "[str, " + getTypeDeclaration(inner) + "]";
      }
    }
    String openAPIType = getSchemaType(p);
    if (typeMapping.containsKey(openAPIType)) {
      return typeMapping.get(openAPIType);
    }
    if (languageSpecificPrimitives.contains(openAPIType)) {
      return openAPIType;
    }
    return toModelName(openAPIType);
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
      if (type != null) {
        return type;
      }
    } else {
      type = openAPIType;
    }
    return toModelName(type);
  }

  @Override public String toModelName(String name) {
    if (modelNameMapping.containsKey(name)) {
      return modelNameMapping.get(name);
    }
    if (schemaMapping.containsKey(name)) {
      return schemaMapping.get(name);
    }
    String origName = name;
    if (schemaKeyToModelNameCache.containsKey(origName)) {
      return schemaKeyToModelNameCache.get(origName);
    }
    String sanitizedName = sanitizeName(name);
    sanitizedName = sanitizedName.replace("$", "");
    sanitizedName = sanitizedName.replaceAll("\\s+", "");
    String nameWithPrefixSuffix = sanitizedName;
    if (!StringUtils.isEmpty(modelNamePrefix)) {
      nameWithPrefixSuffix = modelNamePrefix + "_" + nameWithPrefixSuffix;
    }
    if (!StringUtils.isEmpty(modelNameSuffix)) {
      nameWithPrefixSuffix = nameWithPrefixSuffix + "_" + modelNameSuffix;
    }
    String camelizedName = camelize(nameWithPrefixSuffix);
    if (isReservedWord(camelizedName)) {
      String modelName = "Model" + camelizedName;
      LOGGER.warn("{} (reserved word) cannot be used as model name. Renamed to {}", camelizedName, modelName);
      schemaKeyToModelNameCache.put(origName, modelName);
      return modelName;
    }
    if (camelizedName.matches("^\\d.*")) {
      String modelName = "Model" + camelizedName;
      LOGGER.warn("{} (model name starts with number) cannot be used as model name. Renamed to {}", camelizedName, modelName);
      schemaKeyToModelNameCache.put(origName, modelName);
      return modelName;
    }
    schemaKeyToModelNameCache.put(origName, camelizedName);
    return camelizedName;
  }

  @Override public String toModelFilename(String name) {
    return underscore(dropDots(toModelName(name)));
  }

  @Override public String toModelTestFilename(String name) {
    return "test_" + toModelFilename(name);
  }

  @Override public String toApiFilename(String name) {
    return underscore(toApiName(name));
  }

  @Override public String toApiTestFilename(String name) {
    return "test_" + toApiFilename(name);
  }

  @Override public String toApiName(String name) {
    return super.toApiName(name);
  }

  @Override public String toApiVarName(String name) {
    return underscore(toApiName(name));
  }

  protected static String dropDots(String str) {
    return str.replaceAll("\\.", "_");
  }

  @Override public GeneratorLanguage generatorLanguage() {
    return GeneratorLanguage.PYTHON;
  }

  @Override public Map<String, ModelsMap> postProcessAllModels(Map<String, ModelsMap> objs) {
    final Map<String, ModelsMap> processed = super.postProcessAllModels(objs);
    for (Map.Entry<String, ModelsMap> entry : objs.entrySet()) {
      CodegenModel cm = ModelUtils.getModelByName(entry.getKey(), objs);
      codegenModelMap.put(cm.classname, ModelUtils.getModelByName(entry.getKey(), objs));
    }
    for (String m : codegenModelMap.keySet()) {
      createImportMapOfSet(m, codegenModelMap);
    }
    for (Map.Entry<String, ModelsMap> entry : processed.entrySet()) {
      entry.setValue(postProcessModelsMap(entry.getValue()));
    }
    return processed;
  }

  private ModelsMap postProcessModelsMap(ModelsMap objs) {
    objs = postProcessModelsEnum(objs);
    TreeSet<String> typingImports = new TreeSet<>();
    TreeSet<String> pydanticImports = new TreeSet<>();
    TreeSet<String> datetimeImports = new TreeSet<>();
    TreeSet<String> modelImports = new TreeSet<>();
    TreeSet<String> postponedModelImports = new TreeSet<>();
    for (ModelMap m : objs.getModels()) {
      TreeSet<String> exampleImports = new TreeSet<>();
      TreeSet<String> postponedExampleImports = new TreeSet<>();
      List<String> readOnlyFields = new ArrayList<>();
      hasModelsToImport = false;
      int property_count = 1;
      typingImports.clear();
      pydanticImports.clear();
      datetimeImports.clear();
      Imports otherImports = new Imports();
      CodegenModel model = m.getModel();
      PydanticType pydantic = new PydanticType(typingImports, pydanticImports, datetimeImports, modelImports, exampleImports, postponedModelImports, postponedExampleImports, otherImports, model.classname);
      if (model.getComposedSchemas() != null && model.getComposedSchemas().getOneOf() != null && !model.getComposedSchemas().getOneOf().isEmpty()) {
        int index = 0;
        List<CodegenProperty> oneOfs = model.getComposedSchemas().getOneOf();
        for (CodegenProperty oneOf : oneOfs) {
          if ("none_type".equals(oneOf.dataType)) {
            oneOfs.remove(index);
            break;
          }
          index++;
        }
      }
      List<CodegenProperty> codegenProperties = null;
      if (!model.oneOf.isEmpty()) {
        codegenProperties = model.getComposedSchemas().getOneOf();
        typingImports.add("Any");
        typingImports.add("List");
        pydanticImports.add("Field");
        pydanticImports.add("StrictStr");
        pydanticImports.add("ValidationError");
        pydanticImports.add("field_validator");
      } else {
        if (!model.anyOf.isEmpty()) {
          codegenProperties = model.getComposedSchemas().getAnyOf();
          pydanticImports.add("Field");
          pydanticImports.add("StrictStr");
          pydanticImports.add("ValidationError");
          pydanticImports.add("field_validator");
        } else {
          codegenProperties = model.vars;
          if (model.getDiscriminator() != null && model.getDiscriminator().getMappedModels() != null) {
            typingImports.add("Union");
            Set<CodegenDiscriminator.MappedModel> discriminator = model.getDiscriminator().getMappedModels();
            for (CodegenDiscriminator.MappedModel mappedModel : discriminator) {
              postponedModelImports.add(mappedModel.getMappingName());
            }
          }
        }
      }
      if (!model.allOf.isEmpty()) {
        for (CodegenProperty cp : model.allVars) {
          if (!cp.isPrimitiveType || cp.isModel) {
            if (cp.isArray) {
              modelImports.add(cp.items.dataType);
            } else {
              modelImports.add(cp.getDataType());
            }
          }
        }
      }
      if (model.oneOf.isEmpty() && model.anyOf.isEmpty() && !model.isEnum && !this.disallowAdditionalPropertiesIfNotPresent) {
        typingImports.add("Dict");
        typingImports.add("List");
        typingImports.add("Any");
        typingImports.add("ClassVar");
      }
      for (CodegenProperty cp : codegenProperties) {
        if (cp.isReadOnly) {
          readOnlyFields.add(cp.name);
        }
        String typing = pydantic.generatePythonType(cp);
        cp.vendorExtensions.put("x-py-typing", typing);
        if (!model.oneOf.isEmpty()) {
          cp.vendorExtensions.put("x-py-name", String.format(Locale.ROOT, "oneof_schema_%d_validator", property_count++));
        } else {
          if (!model.anyOf.isEmpty()) {
            cp.vendorExtensions.put("x-py-name", String.format(Locale.ROOT, "anyof_schema_%d_validator", property_count++));
          }
        }
      }
      if (!StringUtils.isEmpty(model.parent)) {
        modelImports.add(model.parent);
      } else {
        if (!model.isEnum) {
          pydanticImports.add("BaseModel");
        }
      }
      if (model.isEnum) {
        for (Map<String, Object> enumVars : (List<Map<String, Object>>) model.getAllowableValues().get("enumVars")) {
          if ((Boolean) enumVars.get("isString")) {
            model.vendorExtensions.putIfAbsent("x-py-enum-type", "str");
            enumVars.put("name", toEnumVariableName((String) enumVars.get("value"), "str"));
          } else {
            model.vendorExtensions.putIfAbsent("x-py-enum-type", "int");
            enumVars.put("name", toEnumVariableName((String) enumVars.get("value"), "int"));
          }
        }
      }
      model.getVendorExtensions().putIfAbsent("x-py-typing-imports", typingImports);
      model.getVendorExtensions().putIfAbsent("x-py-pydantic-imports", pydanticImports);
      model.getVendorExtensions().putIfAbsent("x-py-datetime-imports", datetimeImports);
      model.getVendorExtensions().putIfAbsent("x-py-readonly", readOnlyFields);
      if (!modelImports.isEmpty()) {
        Set<String> modelsToImport = new TreeSet<>();
        for (String modelImport : modelImports) {
          if (modelImport.equals(model.classname)) {
            continue;
          }
          modelsToImport.add("from " + packageName + ".models." + underscore(modelImport) + " import " + modelImport);
        }
        if (!modelsToImport.isEmpty()) {
          model.getVendorExtensions().putIfAbsent("x-py-model-imports", modelsToImport);
        }
      }
      if (!otherImports.isEmpty()) {
        model.getVendorExtensions().putIfAbsent("x-py-other-imports", otherImports.exports());
      }
      if (!postponedModelImports.isEmpty()) {
        Set<String> modelsToImport = new TreeSet<>();
        for (String modelImport : postponedModelImports) {
          if (modelImport.equals(model.classname)) {
            continue;
          }
          modelsToImport.add("from " + packageName + ".models." + underscore(modelImport) + " import " + modelImport);
        }
        model.getVendorExtensions().putIfAbsent("x-py-postponed-model-imports", modelsToImport);
      }
    }
    return objs;
  }

  private PythonType getPydanticType(CodegenProperty cp, Set<String> typingImports, Set<String> pydanticImports, Set<String> datetimeImports, Set<String> modelImports, Set<String> exampleImports, Set<String> postponedModelImports, Set<String> postponedExampleImports, String classname) {
    Imports otherImports = new Imports();
    PydanticType pt = new PydanticType(typingImports, pydanticImports, datetimeImports, modelImports, exampleImports, postponedModelImports, postponedExampleImports, otherImports, classname);
    return pt.getType(cp);
  }

  public void setMapNumberTo(String mapNumberTo) {
    if ("Union[StrictFloat, StrictInt]".equals(mapNumberTo) || "StrictFloat".equals(mapNumberTo) || "float".equals(mapNumberTo)) {
      this.mapNumberTo = mapNumberTo;
    } else {
      throw new IllegalArgumentException("mapNumberTo value must be Union[StrictFloat, StrictInt], StrictStr or float");
    }
  }

  public String toEnumVariableName(String name, String datatype) {
    if ("int".equals(datatype)) {
      return "NUMBER_" + name.replace("-", "MINUS_");
    }
    name = name.substring(1, name.length() - 1);
    if (name.length() == 0) {
      return "EMPTY";
    }
    if (" ".equals(name)) {
      return "SPACE";
    }
    if ("_".equals(name)) {
      return "UNDERSCORE";
    }
    if (reservedWords.contains(name)) {
      name = name.toUpperCase(Locale.ROOT);
    } else {
      if (((CharSequence) name).chars().anyMatch((character) -> specialCharReplacements.keySet().contains(String.valueOf((char) character)))) {
        name = underscore(escape(name, specialCharReplacements, Collections.singletonList("_"), "_")).toUpperCase(Locale.ROOT);
      } else {
        name = name.toUpperCase(Locale.ROOT);
      }
    }
    name = name.replace(" ", "_");
    name = name.replaceFirst("^_", "");
    name = name.replaceFirst("_$", "");
    if (name.matches("\\d.*")) {
      name = "ENUM_" + name.toUpperCase(Locale.ROOT);
    }
    return name;
  }

  /**
     * Update circularImports with the model name (key) and its imports gathered recursively
     *
     * @param modelName       model name
     * @param codegenModelMap a map of CodegenModel
     */
  void createImportMapOfSet(String modelName, Map<String, CodegenModel> codegenModelMap) {
    HashSet<String> imports = new HashSet<>();
    circularImports.put(modelName, imports);
    CodegenModel cm = codegenModelMap.get(modelName);
    if (cm == null) {
      LOGGER.warn("Failed to lookup model in createImportMapOfSet: " + modelName);
      return;
    }
    List<CodegenProperty> codegenProperties = null;
    if (cm.oneOf != null && !cm.oneOf.isEmpty()) {
      codegenProperties = cm.getComposedSchemas().getOneOf();
    } else {
      if (cm.anyOf != null && !cm.anyOf.isEmpty()) {
        codegenProperties = cm.getComposedSchemas().getAnyOf();
      } else {
        codegenProperties = cm.vars;
      }
    }
    for (CodegenProperty cp : codegenProperties) {
      String modelNameFromDataType = getModelNameFromDataType(cp);
      if (modelNameFromDataType != null) {
        imports.add(modelNameFromDataType);
        updateImportsFromCodegenModel(modelNameFromDataType, codegenModelMap.get(modelNameFromDataType), imports);
      }
    }
  }

  /**
     * Returns the model name (if any) from data type of codegen property.
     * Returns null if it's not a model.
     *
     * @param cp Codegen property
     * @return model name
     */
  private String getModelNameFromDataType(CodegenProperty cp) {
    if (cp.isArray) {
      return getModelNameFromDataType(cp.items);
    } else {
      if (cp.isMap) {
        return getModelNameFromDataType(cp.items);
      } else {
        if (!cp.isPrimitiveType || cp.isModel) {
          return cp.getDataType();
        } else {
          return null;
        }
      }
    }
  }

  /**
     * Update set of imports from codegen model recursivly
     *
     * @param modelName model name
     * @param cm        codegen model
     * @param imports   set of imports
     */
  public void updateImportsFromCodegenModel(String modelName, CodegenModel cm, Set<String> imports) {
    if (cm == null) {
      LOGGER.warn("Failed to lookup model in createImportMapOfSet " + modelName);
      return;
    }
    List<CodegenProperty> codegenProperties = null;
    if (cm.oneOf != null && !cm.oneOf.isEmpty()) {
      codegenProperties = cm.getComposedSchemas().getOneOf();
    } else {
      if (cm.anyOf != null && !cm.anyOf.isEmpty()) {
        codegenProperties = cm.getComposedSchemas().getAnyOf();
      } else {
        codegenProperties = cm.vars;
      }
    }
    for (CodegenProperty cp : codegenProperties) {
      String modelNameFromDataType = getModelNameFromDataType(cp);
      if (modelNameFromDataType != null) {
        if (modelName.equals(modelNameFromDataType)) {
          continue;
        } else {
          if (imports.contains(modelNameFromDataType)) {
            continue;
          } else {
            imports.add(modelNameFromDataType);
            updateImportsFromCodegenModel(modelNameFromDataType, codegenModelMap.get(modelNameFromDataType), imports);
          }
        }
      }
    }
  }

  @Override public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
    hasModelsToImport = false;
    TreeSet<String> typingImports = new TreeSet<>();
    TreeSet<String> pydanticImports = new TreeSet<>();
    TreeSet<String> datetimeImports = new TreeSet<>();
    TreeSet<String> modelImports = new TreeSet<>();
    TreeSet<String> postponedModelImports = new TreeSet<>();
    OperationMap objectMap = objs.getOperations();
    List<CodegenOperation> operations = objectMap.getOperation();
    Imports otherImports = new Imports();
    for (CodegenOperation operation : operations) {
      TreeSet<String> exampleImports = new TreeSet<>();
      TreeSet<String> postponedExampleImports = new TreeSet<>();
      List<CodegenParameter> params = operation.allParams;
      for (CodegenParameter cp : params) {
        PydanticType pydantic = new PydanticType(typingImports, pydanticImports, datetimeImports, modelImports, exampleImports, postponedModelImports, postponedExampleImports, otherImports, null);
        String typing = pydantic.generatePythonType(cp);
        cp.vendorExtensions.put("x-py-typing", typing);
      }
      if (!StringUtils.isEmpty(operation.returnType)) {
        getPydanticType(operation.returnProperty, typingImports, new TreeSet<>(), datetimeImports, modelImports, exampleImports, postponedModelImports, postponedExampleImports, null);
      }
      if (!exampleImports.isEmpty()) {
        List<String> imports = new ArrayList<>();
        for (String exampleImport : exampleImports) {
          imports.add("from " + packageName + ".models." + underscore(exampleImport) + " import " + exampleImport);
        }
        operation.vendorExtensions.put("x-py-example-import", imports);
      }
      if (!postponedExampleImports.isEmpty()) {
        List<String> imports = new ArrayList<>();
        for (String exampleImport : postponedExampleImports) {
          imports.add("from " + packageName + ".models." + underscore(exampleImport) + " import " + exampleImport);
        }
        operation.vendorExtensions.put("x-py-example-import", imports);
      }
    }
    List<Map<String, String>> newImports = new ArrayList<>();
    for (String importLine : otherImports.exports()) {
      Map<String, String> item = new HashMap<>();
      item.put("import", importLine);
      newImports.add(item);
    }
    if (!datetimeImports.isEmpty()) {
      Map<String, String> item = new HashMap<>();
      item.put("import", String.format(Locale.ROOT, "from datetime import %s\n", StringUtils.join(datetimeImports, ", ")));
      newImports.add(item);
    }
    if (!pydanticImports.isEmpty()) {
      Map<String, String> item = new HashMap<>();
      item.put("import", String.format(Locale.ROOT, "from pydantic import %s\n", StringUtils.join(pydanticImports, ", ")));
      newImports.add(item);
    }
    if (!typingImports.isEmpty()) {
      Map<String, String> item = new HashMap<>();
      item.put("import", String.format(Locale.ROOT, "from typing import %s\n", StringUtils.join(typingImports, ", ")));
      newImports.add(item);
    }
    if (!modelImports.isEmpty()) {
      for (String modelImport : modelImports) {
        Map<String, String> item = new HashMap<>();
        item.put("import", "from " + packageName + ".models." + underscore(modelImport) + " import " + modelImport);
        newImports.add(item);
      }
    }
    if (!postponedModelImports.isEmpty()) {
      for (String modelImport : postponedModelImports) {
        Map<String, String> item = new HashMap<>();
        item.put("import", "from " + packageName + ".models." + underscore(modelImport) + " import " + modelImport);
        newImports.add(item);
      }
    }
    objs.setImports(newImports);
    return objs;
  }

  @Override public void postProcessParameter(CodegenParameter parameter) {
    postProcessPattern(parameter.pattern, parameter.vendorExtensions);
  }

  @Override public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
    postProcessPattern(property.pattern, property.vendorExtensions);
  }

  public void postProcessPattern(String pattern, Map<String, Object> vendorExtensions) {
    if (pattern != null) {
      int i = pattern.lastIndexOf('/');
      if (pattern.charAt(0) != '/' || i < 2) {
        throw new IllegalArgumentException("Pattern must follow the Perl " + "/pattern/modifiers convention. " + pattern + " is not valid.");
      }
      String regex = pattern.substring(1, i).replace("\'", "\\\'");
      List<String> modifiers = new ArrayList<String>();
      for (char c : pattern.substring(i).toCharArray()) {
        if (regexModifiers.containsKey(c)) {
          String modifier = regexModifiers.get(c);
          modifiers.add(modifier);
        }
      }
      vendorExtensions.put("x-regex", regex.replace("\"", "\\\""));
      vendorExtensions.put("x-pattern", pattern.replace("\"", "\\\""));
      vendorExtensions.put("x-modifiers", modifiers);
    }
  }

  @Override public String addRegularExpressionDelimiter(String pattern) {
    if (StringUtils.isEmpty(pattern)) {
      return pattern;
    }
    if (!pattern.matches("^/.*")) {
      return "/" + pattern.replaceAll("(?<!\\\\)\\/", "\\\\/") + "/";
    }
    return pattern;
  }

  @Override public ModelsMap postProcessModels(ModelsMap objs) {
    return postProcessModelsEnum(objs);
  }

  @Override public String toEnumVarName(String name, String datatype) {
    if ("int".equals(datatype) || "float".equals(datatype)) {
      return name;
    } else {
      return "\'" + name + "\'";
    }
  }

  @Override public String toEnumValue(String value, String datatype) {
    if ("int".equals(datatype) || "float".equals(datatype)) {
      return value;
    } else {
      return "\'" + escapeText(value) + "\'";
    }
  }

  @Override public String toEnumDefaultValue(String value, String datatype) {
    return value;
  }

  /**
     * checks if the data should be classified as "string" in enum
     * e.g. double in C# needs to be double-quoted (e.g. "2.8") by treating it as a string
     * In the future, we may rename this function to "isEnumString"
     *
     * @param dataType data type
     * @return true if it's a enum string
     */
  @Override public boolean isDataTypeString(String dataType) {
    return "str".equals(dataType);
  }

  class PythonType {
    private String type;

    private List<PythonType> typeParams;

    private Map<String, Object> annotations;

    private Map<String, Object> constraints;

    private String defaultValue;

    public PythonType() {
      this(null);
    }

    public PythonType(String type) {
      this.setType(type);
      this.defaultValue = null;
      this.typeParams = new ArrayList<>();
      this.annotations = new HashMap<>();
      this.constraints = new HashMap<>();
    }

    public PythonType setType(String type) {
      this.type = type;
      return this;
    }

    public PythonType setDefaultValue(boolean value) {
      if (value) {
        defaultValue = "True";
      } else {
        defaultValue = "False";
      }
      return this;
    }

    public PythonType setDefaultValue(Object value) {
      defaultValue = value.toString();
      return this;
    }

    public PythonType constrain(String name, String value) {
      return constrain(name, value, true);
    }

    public PythonType constrain(String name, String value, boolean quote) {
      if (quote) {
        value = "\"" + value + "\"";
      }
      constraints.put(name, value);
      return this;
    }

    public PythonType constrain(String name, boolean value) {
      if (value) {
        constraints.put(name, "True");
      } else {
        constraints.put(name, "False");
      }
      return this;
    }

    public PythonType constrain(String name, Object value) {
      constraints.put(name, value);
      return this;
    }

    public PythonType annotate(String name, String value) {
      return annotate(name, value, true);
    }

    public PythonType annotate(String name, String value, boolean quote) {
      if (quote) {
        value = "\"" + value + "\"";
      }
      annotations.put(name, value);
      return this;
    }

    public PythonType annotate(String name, boolean value) {
      if (value) {
        annotations.put(name, "True");
      } else {
        annotations.put(name, "False");
      }
      return this;
    }

    public PythonType annotate(String name, Object value) {
      annotations.put(name, value);
      return this;
    }

    public PythonType addTypeParam(PythonType typeParam) {
      this.typeParams.add(typeParam);
      return this;
    }

    public String asTypeConstraint(Imports imports) {
      return asTypeConstraint(imports, false);
    }

    public String asTypeConstraintWithAnnotations(Imports imports) {
      return asTypeConstraint(imports, true);
    }

    private String asTypeConstraint(Imports imports, boolean withAnnotations) {
      String typeParam = "";
      if (this.typeParams.size() > 0) {
        List<String> types = new ArrayList<>();
        for (PythonType t : this.typeParams) {
          types.add(t.asTypeConstraint(imports));
        }
        typeParam = "[" + StringUtils.join(types, ", ") + "]";
      }
      String currentType = this.type + typeParam;
      List<String> fieldParams = new ArrayList<>();
      for (Map.Entry<String, Object> entry : this.constraints.entrySet()) {
        String ans = entry.getKey() + "=";
        ans += entry.getValue().toString();
        fieldParams.add(ans);
      }
      if (withAnnotations) {
        for (Map.Entry<String, Object> entry : this.annotations.entrySet()) {
          String ans = entry.getKey() + "=";
          ans += entry.getValue().toString();
          fieldParams.add(ans);
        }
      }
      if (fieldParams.size() > 0) {
        imports.add("pydantic", "Field");
        imports.add("typing_extensions", "Annotated");
        currentType = "Annotated[" + currentType + ", Field(" + StringUtils.join(fieldParams, ", ") + ")]";
      }
      return currentType;
    }

    @Nullable public String asTypeValue(Imports imports) {
      String defaultValue = this.defaultValue;
      if (this.annotations.size() > 0) {
        String typeValue = "";
        List<String> ants = new ArrayList<>();
        if (defaultValue != null) {
          ants.add("default=" + defaultValue);
        }
        for (Map.Entry<String, Object> entry : this.annotations.entrySet()) {
          String ans = entry.getKey() + "=";
          ans += entry.getValue().toString();
          ants.add(ans);
        }
        imports.add("pydantic", "Field");
        typeValue = "Field(" + StringUtils.join(ants, ", ") + ")";
        return typeValue;
      }
      return defaultValue;
    }
  }

  class Imports {
    private Map<String, Set<String>> imports;

    public Imports() {
      imports = new HashMap<>();
    }

    private void add(String from, String what) {
      Set<String> allImportsFrom = imports.get(from);
      if (allImportsFrom == null) {
        allImportsFrom = new TreeSet<>();
      }
      allImportsFrom.add(what);
      imports.put(from, allImportsFrom);
    }

    public Set<String> exports() {
      Set<String> results = new TreeSet<>();
      for (Map.Entry<String, Set<String>> entry : imports.entrySet()) {
        String importLine = String.format(Locale.ROOT, "from %s import %s", entry.getKey(), StringUtils.join(entry.getValue(), ", "));
        results.add(importLine);
      }
      return results;
    }

    public boolean isEmpty() {
      return imports.isEmpty();
    }
  }

  class PydanticType {
    private Set<String> typingImports;

    private Set<String> pydanticImports;

    private Set<String> datetimeImports;

    private Set<String> modelImports;

    private Set<String> exampleImports;

    private Set<String> postponedModelImports;

    private Set<String> postponedExampleImports;

    private Imports otherImports;

    private String classname;

    public PydanticType(Set<String> typingImports, Set<String> pydanticImports, Set<String> datetimeImports, Set<String> modelImports, Set<String> exampleImports, Set<String> postponedModelImports, Set<String> postponedExampleImports, Imports otherImports, String classname) {
      this.typingImports = typingImports;
      this.pydanticImports = pydanticImports;
      this.datetimeImports = datetimeImports;
      this.modelImports = modelImports;
      this.exampleImports = exampleImports;
      this.postponedModelImports = postponedModelImports;
      this.postponedExampleImports = postponedExampleImports;
      this.otherImports = otherImports;
      this.classname = classname;
    }

    private PythonType arrayType(IJsonSchemaValidationProperties cp) {
      PythonType pt = new PythonType();
      if (cp.getMaxItems() != null) {
        pt.constrain("max_length", cp.getMaxItems());
      }
      if (cp.getMinItems() != null) {
        pt.constrain("min_length", cp.getMinItems());
      }
      if (cp.getUniqueItems()) {
        pt.setType("List");
        typingImports.add("List");
      } else {
        pt.setType("List");
        typingImports.add("List");
      }
      pt.addTypeParam(getType(cp.getItems()));
      return pt;
    }

    private PythonType stringType(IJsonSchemaValidationProperties cp) {
      if (cp.getHasValidation()) {
        PythonType pt = new PythonType("str");
        pt.constrain("strict", true);
        if (cp.getMaxLength() != null) {
          pt.constrain("max_length", cp.getMaxLength());
        }
        if (cp.getMinLength() != null) {
          pt.constrain("min_length", cp.getMinLength());
        }
        if (cp.getPattern() != null) {
          pydanticImports.add("field_validator");
        }
        return pt;
      } else {
        if ("password".equals(cp.getFormat())) {
          pydanticImports.add("SecretStr");
          return new PythonType("SecretStr");
        } else {
          pydanticImports.add("StrictStr");
          return new PythonType("StrictStr");
        }
      }
    }

    private PythonType mapType(IJsonSchemaValidationProperties cp) {
      typingImports.add("Dict");
      PythonType pt = new PythonType("Dict");
      pt.addTypeParam(new PythonType("str"));
      pt.addTypeParam(getType(cp.getItems()));
      return pt;
    }

    private PythonType numberType(IJsonSchemaValidationProperties cp) {
      if (cp.getHasValidation()) {
        PythonType floatt = new PythonType("float");
        PythonType intt = new PythonType("int");
        if (cp.getMaximum() != null) {
          if (cp.getExclusiveMaximum()) {
            floatt.constrain("lt", cp.getMaximum(), false);
            intt.constrain("lt", (int) Math.ceil(Double.valueOf(cp.getMaximum())));
          } else {
            floatt.constrain("le", cp.getMaximum(), false);
            intt.constrain("le", (int) Math.floor(Double.valueOf(cp.getMaximum())));
          }
        }
        if (cp.getMinimum() != null) {
          if (cp.getExclusiveMinimum()) {
            floatt.constrain("gt", cp.getMinimum(), false);
            intt.constrain("gt", (int) Math.floor(Double.valueOf(cp.getMinimum())));
          } else {
            floatt.constrain("ge", cp.getMinimum(), false);
            intt.constrain("ge", (int) Math.ceil(Double.valueOf(cp.getMinimum())));
          }
        }
        if (cp.getMultipleOf() != null) {
          floatt.constrain("multiple_of", cp.getMultipleOf());
        }
        if ("Union[StrictFloat, StrictInt]".equals(mapNumberTo)) {
          floatt.constrain("strict", true);
          intt.constrain("strict", true);
          typingImports.add("Union");
          PythonType pt = new PythonType("Union");
          pt.addTypeParam(floatt);
          pt.addTypeParam(intt);
          return pt;
        } else {
          if ("StrictFloat".equals(mapNumberTo)) {
            floatt.constrain("strict", true);
            return floatt;
          } else {
            return floatt;
          }
        }
      } else {
        if ("Union[StrictFloat, StrictInt]".equals(mapNumberTo)) {
          typingImports.add("Union");
          pydanticImports.add("StrictFloat");
          pydanticImports.add("StrictInt");
          PythonType pt = new PythonType("Union");
          pt.addTypeParam(new PythonType("StrictFloat"));
          pt.addTypeParam(new PythonType("StrictInt"));
          return pt;
        } else {
          if ("StrictFloat".equals(mapNumberTo)) {
            pydanticImports.add("StrictFloat");
            return new PythonType("StrictFloat");
          } else {
            return new PythonType("float");
          }
        }
      }
    }

    private PythonType intType(IJsonSchemaValidationProperties cp) {
      if (cp.getHasValidation()) {
        PythonType pt = new PythonType("int");
        pt.constrain("strict", true);
        if (cp.getMaximum() != null) {
          if (cp.getExclusiveMaximum()) {
            pt.constrain("lt", cp.getMaximum(), false);
          } else {
            pt.constrain("le", cp.getMaximum(), false);
          }
        }
        if (cp.getMinimum() != null) {
          if (cp.getExclusiveMinimum()) {
            pt.constrain("gt", cp.getMinimum(), false);
          } else {
            pt.constrain("ge", cp.getMinimum(), false);
          }
        }
        if (cp.getMultipleOf() != null) {
          pt.constrain("multiple_of", cp.getMultipleOf());
        }
        return pt;
      } else {
        pydanticImports.add("StrictInt");
        return new PythonType("StrictInt");
      }
    }

    private PythonType binaryType(IJsonSchemaValidationProperties cp) {
      if (cp.getHasValidation()) {
        PythonType bytest = new PythonType("bytes");
        PythonType strt = new PythonType("str");
        bytest.constrain("strict", true);
        strt.constrain("strict", true);
        if (cp.getMaxLength() != null) {
          bytest.constrain("max_length", cp.getMaxLength());
          strt.constrain("max_length", cp.getMaxLength());
        }
        if (cp.getMinLength() != null) {
          bytest.constrain("min_length", cp.getMinLength());
          strt.constrain("min_length", cp.getMinLength());
        }
        if (cp.getPattern() != null) {
          pydanticImports.add("field_validator");
        }
        typingImports.add("Union");
        PythonType pt = new PythonType("Union");
        pt.addTypeParam(bytest);
        pt.addTypeParam(strt);
        return pt;
      } else {
        pydanticImports.add("StrictBytes");
        pydanticImports.add("StrictStr");
        typingImports.add("Union");
        PythonType pt = new PythonType("Union");
        pt.addTypeParam(new PythonType("StrictBytes"));
        pt.addTypeParam(new PythonType("StrictStr"));
        return pt;
      }
    }

    private PythonType boolType(IJsonSchemaValidationProperties cp) {
      pydanticImports.add("StrictBool");
      return new PythonType("StrictBool");
    }

    private PythonType decimalType(IJsonSchemaValidationProperties cp) {
      PythonType pt = new PythonType("Decimal");
      otherImports.add("decimal", "Decimal");
      if (cp.getHasValidation()) {
        pt.constrain("strict", true);
        if (cp.getMaximum() != null) {
          if (cp.getExclusiveMaximum()) {
            pt.constrain("gt", cp.getMaximum(), false);
          } else {
            pt.constrain("ge", cp.getMaximum(), false);
          }
        }
        if (cp.getMinimum() != null) {
          if (cp.getExclusiveMinimum()) {
            pt.constrain("lt", cp.getMinimum(), false);
          } else {
            pt.constrain("le", cp.getMinimum(), false);
          }
        }
        if (cp.getMultipleOf() != null) {
          pt.constrain("multiple_of", cp.getMultipleOf());
        }
      }
      return pt;
    }

    private PythonType anyType(IJsonSchemaValidationProperties cp) {
      typingImports.add("Any");
      return new PythonType("Any");
    }

    private PythonType dateType(IJsonSchemaValidationProperties cp) {
      if (cp.getIsDate()) {
        datetimeImports.add("date");
      }
      if (cp.getIsDateTime()) {
        datetimeImports.add("datetime");
      }
      return new PythonType(cp.getDataType());
    }

    private PythonType uuidType(IJsonSchemaValidationProperties cp) {
      return new PythonType(cp.getDataType());
    }

    private PythonType freeFormType(IJsonSchemaValidationProperties cp) {
      typingImports.add("Dict");
      typingImports.add("Any");
      typingImports.add("Union");
      PythonType pt = new PythonType("Union");
      pt.addTypeParam(new PythonType("str"));
      pt.addTypeParam(new PythonType("Any"));
      return pt;
    }

    private PythonType modelType(IJsonSchemaValidationProperties cp) {
      hasModelsToImport = true;
      modelImports.add(cp.getDataType());
      exampleImports.add(cp.getDataType());
      return new PythonType(cp.getDataType());
    }

    private PythonType fromCommon(IJsonSchemaValidationProperties cp) {
      if (cp == null) {
        LOGGER.warn("Codegen property is null (e.g. map/dict of undefined type). Default to typing.Any.");
        typingImports.add("Any");
        return new PythonType("Any");
      }
      if (cp.getIsEnum()) {
        pydanticImports.add("field_validator");
      }
      if (cp.getIsArray()) {
        return arrayType(cp);
      } else {
        if (cp.getIsMap()) {
          return mapType(cp);
        } else {
          if (cp.getIsString()) {
            return stringType(cp);
          } else {
            if (cp.getIsNumber() || cp.getIsFloat() || cp.getIsDouble()) {
              return numberType(cp);
            } else {
              if (cp.getIsInteger() || cp.getIsLong() || cp.getIsShort() || cp.getIsUnboundedInteger()) {
                return intType(cp);
              } else {
                if (cp.getIsBinary() || cp.getIsByteArray()) {
                  return binaryType(cp);
                } else {
                  if (cp.getIsBoolean()) {
                    return boolType(cp);
                  } else {
                    if (cp.getIsDecimal()) {
                      return decimalType(cp);
                    } else {
                      if (cp.getIsAnyType()) {
                        return anyType(cp);
                      } else {
                        if (cp.getIsDate() || cp.getIsDateTime()) {
                          return dateType(cp);
                        } else {
                          if (cp.getIsUuid()) {
                            return uuidType(cp);
                          } else {
                            if (cp.getIsFreeFormObject()) {
                              return freeFormType(cp);
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
      return null;
    }

    public String generatePythonType(CodegenProperty cp) {
      PythonType pt = this.getType(cp);
      return this.finalizeType(cp, pt);
    }

    private PythonType getType(CodegenProperty cp) {
      PythonType result = fromCommon(cp);
      if (result == null) {
        if (!cp.isPrimitiveType || cp.isModel) {
          if (classname == null) {
            hasModelsToImport = true;
            modelImports.add(cp.getDataType());
            exampleImports.add(cp.getDataType());
          } else {
            if (circularImports.containsKey(cp.getDataType())) {
              if (circularImports.get(cp.getDataType()).contains(classname)) {
                hasModelsToImport = true;
                postponedModelImports.add(cp.getDataType());
                postponedExampleImports.add(cp.getDataType());
                LOGGER.debug("Skipped importing {} in {} due to circular import.", cp.getDataType(), classname);
              } else {
                hasModelsToImport = true;
                modelImports.add(cp.getDataType());
                exampleImports.add(cp.getDataType());
              }
            } else {
              LOGGER.error("Failed to look up {} from the imports (map of set) of models.", cp.getDataType());
            }
          }
          result = new PythonType(cp.getDataType());
        } else {
          throw new RuntimeException("Error! Codegen Property not yet supported in getPydanticType: " + cp);
        }
      }
      return result;
    }

    private String finalizeType(CodegenProperty cp, PythonType pt) {
      if (!cp.required || cp.isNullable) {
        typingImports.add("Optional");
        PythonType opt = new PythonType("Optional");
        opt.addTypeParam(pt);
        pt = opt;
      }
      if (!StringUtils.isEmpty(cp.description)) {
        pt.annotate("description", cp.description);
      }
      if (cp.baseName != null && !cp.baseName.equals(cp.name)) {
        pt.annotate("alias", cp.baseName);
      }
      if (!cp.required) {
        if (cp.defaultValue == null) {
          pt.setDefaultValue("None");
        } else {
          if (cp.isArray || cp.isMap) {
            pt.setDefaultValue("None");
          } else {
            pt.setDefaultValue(cp.defaultValue);
          }
        }
      }
      String typeConstraint = pt.asTypeConstraint(otherImports);
      String typeValue = pt.asTypeValue(otherImports);
      if (typeValue == null) {
        return typeConstraint;
      } else {
        return typeConstraint + " = " + typeValue;
      }
    }

    public String generatePythonType(CodegenParameter cp) {
      PythonType pt = this.getType(cp);
      return this.finalizeType(cp, pt);
    }

    private PythonType getType(CodegenParameter cp) {
      PythonType result = fromCommon(cp);
      if (result == null) {
        if (!cp.isPrimitiveType) {
          hasModelsToImport = true;
          modelImports.add(cp.getDataType());
          exampleImports.add(cp.getDataType());
          result = new PythonType(cp.getDataType());
        } else {
          if (cp.getContent() != null) {
            LinkedHashMap<String, CodegenMediaType> contents = cp.getContent();
            for (String key : contents.keySet()) {
              CodegenMediaType cmt = contents.get(key);
              if (cmt != null) {
                result = getPydanticType(cmt.getSchema(), typingImports, pydanticImports, datetimeImports, modelImports, exampleImports, postponedModelImports, postponedExampleImports, classname);
              }
            }
            throw new RuntimeException("Error! Failed to process getPydanticType when getting the content: " + cp);
          } else {
            throw new RuntimeException("Error! Codegen Parameter not yet supported in getPydanticType: " + cp);
          }
        }
      }
      return result;
    }

    private String finalizeType(CodegenParameter cp, PythonType pt) {
      if (!cp.required || cp.isNullable) {
        typingImports.add("Optional");
        PythonType opt = new PythonType("Optional");
        opt.addTypeParam(pt);
        pt = opt;
      }
      if (!StringUtils.isEmpty(cp.description)) {
        pt.annotate("description", cp.description);
      }
      return pt.asTypeConstraintWithAnnotations(otherImports);
    }
  }
}