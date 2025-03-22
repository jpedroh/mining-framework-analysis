package org.openapitools.codegen.utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.util.ClasspathHelper;
import io.swagger.v3.parser.ObjectMapperFactory;
import io.swagger.v3.parser.util.RemoteUrl;
import io.swagger.v3.parser.util.SchemaTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.IJsonSchemaValidationProperties;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.openapitools.codegen.utils.OnceLogger.once;

public class ModelUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModelUtils.class);

  private static final String URI_FORMAT = "uri";

  private static final String generateAliasAsModelKey = "generateAliasAsModel";

  private static final String openapiDocVersion = "x-original-swagger-version";

  private static final String disallowAdditionalPropertiesIfNotPresent = "x-disallow-additional-properties-if-not-present";

  private static final String freeFormExplicit = "x-is-free-form";

  private static ObjectMapper JSON_MAPPER, YAML_MAPPER;

  static {
    JSON_MAPPER = ObjectMapperFactory.createJson();
    YAML_MAPPER = ObjectMapperFactory.createYaml();
  }

  public static void setDisallowAdditionalPropertiesIfNotPresent(boolean value) {
    GlobalSettings.setProperty(disallowAdditionalPropertiesIfNotPresent, Boolean.toString(value));
  }

  public static boolean isDisallowAdditionalPropertiesIfNotPresent() {
    return Boolean.parseBoolean(GlobalSettings.getProperty(disallowAdditionalPropertiesIfNotPresent, "true"));
  }

  public static void setGenerateAliasAsModel(boolean value) {
    GlobalSettings.setProperty(generateAliasAsModelKey, Boolean.toString(value));
  }

  public static boolean isGenerateAliasAsModel() {
    return Boolean.parseBoolean(GlobalSettings.getProperty(generateAliasAsModelKey, "false"));
  }

  public static boolean isGenerateAliasAsModel(Schema schema) {
    return isGenerateAliasAsModel() || (schema.getExtensions() != null && schema.getExtensions().getOrDefault("x-generate-alias-as-model", false).equals(true));
  }

  /**
     * Searches for the model by name in the map of models and returns it
     *
     * @param name   Name of the model
     * @param models Map of models
     * @return model
     */
  public static CodegenModel getModelByName(final String name, final Map<String, ModelsMap> models) {
    final ModelsMap data = models.get(name);
    if (data != null) {
      final List<ModelMap> dataModelsList = data.getModels();
      if (dataModelsList != null) {
        for (final ModelMap entryMap : dataModelsList) {
          final CodegenModel model = entryMap.getModel();
          if (model != null) {
            return model;
          }
        }
      }
    }
    return null;
  }

  /**
     * Return the list of all schemas in the 'components/schemas' section used in the openAPI specification
     *
     * @param openAPI specification
     * @return schemas a list of used schemas
     */
  public static List<String> getAllUsedSchemas(OpenAPI openAPI) {
    Map<String, List<String>> childrenMap = getChildrenMap(openAPI);
    List<String> allUsedSchemas = new ArrayList<String>();
    visitOpenAPI(openAPI, (s, t) -> {
      if (s.get$ref() != null) {
        String ref = getSimpleRef(s.get$ref());
        if (!allUsedSchemas.contains(ref)) {
          allUsedSchemas.add(ref);
        }
        if (childrenMap.containsKey(ref)) {
          for (String child : childrenMap.get(ref)) {
            if (!allUsedSchemas.contains(child)) {
              allUsedSchemas.add(child);
            }
          }
        }
      }
    });
    return allUsedSchemas;
  }

  /**
     * Return the list of unused schemas in the 'components/schemas' section of an openAPI specification
     *
     * @param openAPI specification
     * @return schemas a list of unused schemas
     */
  public static List<String> getUnusedSchemas(OpenAPI openAPI) {
    final Map<String, List<String>> childrenMap;
    Map<String, List<String>> tmpChildrenMap;
    try {
      tmpChildrenMap = getChildrenMap(openAPI);
    } catch (NullPointerException npe) {
      tmpChildrenMap = new HashMap<>();
    }
    childrenMap = tmpChildrenMap;
    List<String> unusedSchemas = new ArrayList<String>();
    if (openAPI != null) {
      Map<String, Schema> schemas = getSchemas(openAPI);
      unusedSchemas.addAll(schemas.keySet());
      visitOpenAPI(openAPI, (s, t) -> {
        if (s.get$ref() != null) {
          String ref = getSimpleRef(s.get$ref());
          unusedSchemas.remove(ref);
          if (childrenMap.containsKey(ref)) {
            unusedSchemas.removeAll(childrenMap.get(ref));
          }
        }
      });
    }
    return unusedSchemas;
  }

  /**
     * Return the list of schemas in the 'components/schemas' used only in a 'application/x-www-form-urlencoded' or 'multipart/form-data' mime time
     *
     * @param openAPI specification
     * @return schemas a list of schemas
     */
  public static List<String> getSchemasUsedOnlyInFormParam(OpenAPI openAPI) {
    List<String> schemasUsedInFormParam = new ArrayList<String>();
    List<String> schemasUsedInOtherCases = new ArrayList<String>();
    visitOpenAPI(openAPI, (s, t) -> {
      if (s.get$ref() != null) {
        String ref = getSimpleRef(s.get$ref());
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(t) || "multipart/form-data".equalsIgnoreCase(t)) {
          schemasUsedInFormParam.add(ref);
        } else {
          schemasUsedInOtherCases.add(ref);
        }
      }
    });
    return schemasUsedInFormParam.stream().filter((n) -> !schemasUsedInOtherCases.contains(n)).collect(Collectors.toList());
  }

  /**
     * Private method used by several methods ({@link #getAllUsedSchemas(OpenAPI)},
     * {@link #getUnusedSchemas(OpenAPI)},
     * {@link #getSchemasUsedOnlyInFormParam(OpenAPI)}, ...) to traverse all paths of an
     * OpenAPI instance and call the visitor functional interface when a schema is found.
     *
     * @param openAPI specification
     * @param visitor functional interface (can be defined as a lambda) called each time a schema is found.
     */
  private static void visitOpenAPI(OpenAPI openAPI, OpenAPISchemaVisitor visitor) {
    Map<String, PathItem> paths = openAPI.getPaths();
    List<String> visitedSchemas = new ArrayList<>();
    if (paths != null) {
      for (PathItem path : paths.values()) {
        visitPathItem(path, openAPI, visitor, visitedSchemas);
      }
    }
  }

  private static void visitPathItem(PathItem pathItem, OpenAPI openAPI, OpenAPISchemaVisitor visitor, List<String> visitedSchemas) {
    List<Operation> allOperations = pathItem.readOperations();
    if (allOperations != null) {
      for (Operation operation : allOperations) {
        visitParameters(openAPI, operation.getParameters(), visitor, visitedSchemas);
        RequestBody requestBody = getReferencedRequestBody(openAPI, operation.getRequestBody());
        if (requestBody != null) {
          visitContent(openAPI, requestBody.getContent(), visitor, visitedSchemas);
        }
        if (operation.getResponses() != null) {
          for (ApiResponse r : operation.getResponses().values()) {
            ApiResponse apiResponse = getReferencedApiResponse(openAPI, r);
            if (apiResponse != null) {
              visitContent(openAPI, apiResponse.getContent(), visitor, visitedSchemas);
              if (apiResponse.getHeaders() != null) {
                for (Entry<String, Header> e : apiResponse.getHeaders().entrySet()) {
                  Header header = getReferencedHeader(openAPI, e.getValue());
                  if (header.getSchema() != null) {
                    visitSchema(openAPI, header.getSchema(), e.getKey(), visitedSchemas, visitor);
                  }
                  visitContent(openAPI, header.getContent(), visitor, visitedSchemas);
                }
              }
            }
          }
        }
        if (operation.getCallbacks() != null) {
          for (Callback c : operation.getCallbacks().values()) {
            Callback callback = getReferencedCallback(openAPI, c);
            if (callback != null) {
              for (PathItem p : callback.values()) {
                visitPathItem(p, openAPI, visitor, visitedSchemas);
              }
            }
          }
        }
      }
    }
    visitParameters(openAPI, pathItem.getParameters(), visitor, visitedSchemas);
  }

  private static void visitParameters(OpenAPI openAPI, List<Parameter> parameters, OpenAPISchemaVisitor visitor, List<String> visitedSchemas) {
    if (parameters != null) {
      for (Parameter p : parameters) {
        Parameter parameter = getReferencedParameter(openAPI, p);
        if (parameter != null) {
          if (parameter.getSchema() != null) {
            visitSchema(openAPI, parameter.getSchema(), null, visitedSchemas, visitor);
          }
          visitContent(openAPI, parameter.getContent(), visitor, visitedSchemas);
        } else {
          once(LOGGER).warn("Unreferenced parameter(s) found.");
        }
      }
    }
  }

  private static void visitContent(OpenAPI openAPI, Content content, OpenAPISchemaVisitor visitor, List<String> visitedSchemas) {
    if (content != null) {
      for (Entry<String, MediaType> e : content.entrySet()) {
        if (e.getValue().getSchema() != null) {
          visitSchema(openAPI, e.getValue().getSchema(), e.getKey(), visitedSchemas, visitor);
        }
      }
    }
  }

  /**
     * Invoke the specified visitor function for every schema that matches mimeType in the OpenAPI document.
     * <p>
     * To avoid infinite recursion, referenced schemas are visited only once. When a referenced schema is visited,
     * it is added to visitedSchemas.
     *
     * @param openAPI        the OpenAPI document that contains schema objects.
     * @param schema         the root schema object to be visited.
     * @param mimeType       the mime type. TODO: does not seem to be used in a meaningful way.
     * @param visitedSchemas the list of referenced schemas that have been visited.
     * @param visitor        the visitor function which is invoked for every visited schema.
     */
  private static void visitSchema(OpenAPI openAPI, Schema schema, String mimeType, List<String> visitedSchemas, OpenAPISchemaVisitor visitor) {
    visitor.visit(schema, mimeType);
    if (schema.get$ref() != null) {
      String ref = getSimpleRef(schema.get$ref());
      if (!visitedSchemas.contains(ref)) {
        visitedSchemas.add(ref);
        Schema referencedSchema = getSchemas(openAPI).get(ref);
        if (referencedSchema != null) {
          visitSchema(openAPI, referencedSchema, mimeType, visitedSchemas, visitor);
        }
      }
    }
    if (schema instanceof ComposedSchema) {
      List<Schema> oneOf = ((ComposedSchema) schema).getOneOf();
      if (oneOf != null) {
        for (Schema s : oneOf) {
          visitSchema(openAPI, s, mimeType, visitedSchemas, visitor);
        }
      }
      List<Schema> allOf = ((ComposedSchema) schema).getAllOf();
      if (allOf != null) {
        for (Schema s : allOf) {
          visitSchema(openAPI, s, mimeType, visitedSchemas, visitor);
        }
      }
      List<Schema> anyOf = ((ComposedSchema) schema).getAnyOf();
      if (anyOf != null) {
        for (Schema s : anyOf) {
          visitSchema(openAPI, s, mimeType, visitedSchemas, visitor);
        }
      }
    } else {
      if (schema instanceof ArraySchema) {
        Schema itemsSchema = ((ArraySchema) schema).getItems();
        if (itemsSchema != null) {
          visitSchema(openAPI, itemsSchema, mimeType, visitedSchemas, visitor);
        }
      } else {
        if (isMapSchema(schema)) {
          Object additionalProperties = schema.getAdditionalProperties();
          if (additionalProperties instanceof Schema) {
            visitSchema(openAPI, (Schema) additionalProperties, mimeType, visitedSchemas, visitor);
          }
        }
      }
    }
    if (schema.getNot() != null) {
      visitSchema(openAPI, schema.getNot(), mimeType, visitedSchemas, visitor);
    }
    Map<String, Schema> properties = schema.getProperties();
    if (properties != null) {
      for (Schema property : properties.values()) {
        visitSchema(openAPI, property, mimeType, visitedSchemas, visitor);
      }
    }
  }

  @FunctionalInterface private static interface OpenAPISchemaVisitor {
    public void visit(Schema schema, String mimeType);
  }

  public static String getSimpleRef(String ref) {
    if (ref == null) {
      once(LOGGER).warn("Failed to get the schema name: null");
      return null;
    } else {
      if (ref.startsWith("#/components/")) {
        ref = ref.substring(ref.lastIndexOf("/") + 1);
      } else {
        if (ref.startsWith("#/definitions/")) {
          ref = ref.substring(ref.lastIndexOf("/") + 1);
        } else {
          once(LOGGER).warn("Failed to get the schema name: {}", ref);
          return null;
        }
      }
    }
    try {
      ref = URLDecoder.decode(ref, "UTF-8");
    } catch (UnsupportedEncodingException ignored) {
      once(LOGGER).warn("Found UnsupportedEncodingException: {}", ref);
    }
    ref = ref.replace("~1", "/").replace("~0", "~");
    return ref;
  }

  /**
     * Return true if the specified schema is type object
     * We can't use isObjectSchema because it requires properties to exist which is not required
     * We can't use isMap because it is true for AnyType use cases
     *
     * @param schema the OAS schema
     * @return true if the specified schema is an Object schema.
     */
  public static boolean isTypeObjectSchema(Schema schema) {
    if (SchemaTypeUtil.OBJECT_TYPE.equals(schema.getType())) {
      return true;
    }
    return false;
  }

  /**
     * Return true if the specified schema is an object with a fixed number of properties.
     * <p>
     * A ObjectSchema differs from a MapSchema in the following way:
     * - An ObjectSchema is not extensible, i.e. it has a fixed number of properties.
     * - A MapSchema is an object that can be extended with an arbitrary set of properties.
     *   The payload may include dynamic properties.
     * <p>
     * For example, an OpenAPI schema is considered an ObjectSchema in the following scenarios:
     * <p>
     *
     *   type: object
     *   additionalProperties: false
     *   properties:
     *     name:
     *       type: string
     *     address:
     *       type: string
     *
     * @param schema the OAS schema
     * @return true if the specified schema is an Object schema.
     */
  public static boolean isObjectSchema(Schema schema) {
    if (schema instanceof ObjectSchema) {
      return true;
    }
    if (SchemaTypeUtil.OBJECT_TYPE.equals(schema.getType()) && !(schema instanceof MapSchema)) {
      return true;
    }
    if (schema.getType() == null && schema.getProperties() != null && !schema.getProperties().isEmpty()) {
      return true;
    }
    return false;
  }

  /**
     * Return true if the specified schema is composed, i.e. if it uses
     * 'oneOf', 'anyOf' or 'allOf'.
     *
     * @param schema the OAS schema
     * @return true if the specified schema is a Composed schema.
     */
  public static boolean isComposedSchema(Schema schema) {
    if (schema instanceof ComposedSchema) {
      return true;
    }
    return false;
  }

  /**
     * Return true if the specified schema is composed with more than one of the following:
     * 'oneOf', 'anyOf' or 'allOf'.
     *
     * @param schema the OAS schema
     * @return true if the specified schema is a Composed schema.
     */
  public static boolean isComplexComposedSchema(Schema schema) {
    if (!(schema instanceof ComposedSchema)) {
      return false;
    }
    int count = 0;
    if (schema.getAllOf() != null && !schema.getAllOf().isEmpty()) {
      count++;
    }
    if (schema.getOneOf() != null && !schema.getOneOf().isEmpty()) {
      count++;
    }
    if (schema.getAnyOf() != null && !schema.getAnyOf().isEmpty()) {
      count++;
    }
    if (schema.getProperties() != null && !schema.getProperties().isEmpty()) {
      count++;
    }
    if (count > 1) {
      return true;
    }
    return false;
  }

  /**
     * Return true if the specified 'schema' is an object that can be extended with additional properties.
     * Additional properties means a Schema should support all explicitly defined properties plus any
     * undeclared properties.
     * <p>
     * A MapSchema differs from an ObjectSchema in the following way:
     * - An ObjectSchema is not extensible, i.e. it has a fixed number of properties.
     * - A MapSchema is an object that can be extended with an arbitrary set of properties.
     * The payload may include dynamic properties.
     * <p>
     * Note that isMapSchema returns true for a composed schema (allOf, anyOf, oneOf) that also defines
     * additionalproperties.
     * <p>
     * For example, an OpenAPI schema is considered a MapSchema in the following scenarios:
     * <p>
     *
     *   type: object
     *   additionalProperties: true
     *
     *   type: object
     *   additionalProperties:
     *     type: object
     *     properties:
     *       code:
     *         type: integer
     *
     *   allOf:
     *     - $ref: '#/components/schemas/Class1'
     *     - $ref: '#/components/schemas/Class2'
     *   additionalProperties: true
     *
     * @param schema the OAS schema
     * @return true if the specified schema is a Map schema.
     */
  public static boolean isMapSchema(Schema schema) {
    if (schema instanceof MapSchema) {
      return true;
    }
    if (schema == null) {
      return false;
    }
    if (schema.getAdditionalProperties() instanceof Schema) {
      return true;
    }
    if (schema.getAdditionalProperties() instanceof Boolean && (Boolean) schema.getAdditionalProperties()) {
      return true;
    }
    return false;
  }

  /**
     * Return true if the specified schema is an array of items.
     *
     * @param schema the OAS schema
     * @return true if the specified schema is an Array schema.
     */
  public static boolean isArraySchema(Schema schema) {
    return (schema instanceof ArraySchema);
  }

  public static boolean isSet(Schema schema) {
    return ModelUtils.isArraySchema(schema) && Boolean.TRUE.equals(schema.getUniqueItems());
  }

  public static boolean isStringSchema(Schema schema) {
    if (schema instanceof StringSchema || SchemaTypeUtil.STRING_TYPE.equals(schema.getType())) {
      return true;
    }
    return false;
  }

  public static boolean isIntegerSchema(Schema schema) {
    if (schema instanceof IntegerSchema) {
      return true;
    }
    if (SchemaTypeUtil.INTEGER_TYPE.equals(schema.getType())) {
      return true;
    }
    return false;
  }

  public static boolean isShortSchema(Schema schema) {
    if (SchemaTypeUtil.INTEGER_TYPE.equals(schema.getType()) && SchemaTypeUtil.INTEGER32_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isUnsignedIntegerSchema(Schema schema) {
    if (SchemaTypeUtil.INTEGER_TYPE.equals(schema.getType()) && ("int32".equals(schema.getFormat()) || schema.getFormat() == null) && (schema.getExtensions() != null && (Boolean) schema.getExtensions().getOrDefault("x-unsigned", Boolean.FALSE))) {
      return true;
    }
    return false;
  }

  public static boolean isLongSchema(Schema schema) {
    if (SchemaTypeUtil.INTEGER_TYPE.equals(schema.getType()) && SchemaTypeUtil.INTEGER64_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isUnsignedLongSchema(Schema schema) {
    if (SchemaTypeUtil.INTEGER_TYPE.equals(schema.getType()) && "int64".equals(schema.getFormat()) && (schema.getExtensions() != null && (Boolean) schema.getExtensions().getOrDefault("x-unsigned", Boolean.FALSE))) {
      return true;
    }
    return false;
  }

  public static boolean isBooleanSchema(Schema schema) {
    if (schema instanceof BooleanSchema) {
      return true;
    }
    if (SchemaTypeUtil.BOOLEAN_TYPE.equals(schema.getType())) {
      return true;
    }
    return false;
  }

  public static boolean isNumberSchema(Schema schema) {
    if (schema instanceof NumberSchema) {
      return true;
    }
    if (SchemaTypeUtil.NUMBER_TYPE.equals(schema.getType())) {
      return true;
    }
    return false;
  }

  public static boolean isFloatSchema(Schema schema) {
    if (SchemaTypeUtil.NUMBER_TYPE.equals(schema.getType()) && SchemaTypeUtil.FLOAT_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isDoubleSchema(Schema schema) {
    if (SchemaTypeUtil.NUMBER_TYPE.equals(schema.getType()) && SchemaTypeUtil.DOUBLE_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isDateSchema(Schema schema) {
    if (schema instanceof DateSchema) {
      return true;
    }
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && SchemaTypeUtil.DATE_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isDateTimeSchema(Schema schema) {
    if (schema instanceof DateTimeSchema) {
      return true;
    }
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && SchemaTypeUtil.DATE_TIME_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isPasswordSchema(Schema schema) {
    if (schema instanceof PasswordSchema) {
      return true;
    }
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && SchemaTypeUtil.PASSWORD_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isByteArraySchema(Schema schema) {
    if (schema instanceof ByteArraySchema) {
      return true;
    }
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && SchemaTypeUtil.BYTE_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isBinarySchema(Schema schema) {
    if (schema instanceof BinarySchema) {
      return true;
    }
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && SchemaTypeUtil.BINARY_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isFileSchema(Schema schema) {
    if (schema instanceof FileSchema) {
      return true;
    }
    return isBinarySchema(schema);
  }

  public static boolean isUUIDSchema(Schema schema) {
    if (schema instanceof UUIDSchema) {
      return true;
    }
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && SchemaTypeUtil.UUID_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isURISchema(Schema schema) {
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && URI_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isEmailSchema(Schema schema) {
    if (schema instanceof EmailSchema) {
      return true;
    }
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && SchemaTypeUtil.EMAIL_FORMAT.equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  public static boolean isDecimalSchema(Schema schema) {
    if (SchemaTypeUtil.STRING_TYPE.equals(schema.getType()) && "number".equals(schema.getFormat())) {
      return true;
    }
    return false;
  }

  /**
     * Check to see if the schema is a model
     *
     * @param schema potentially containing a '$ref'
     * @return true if it's a model with at least one properties
     */
  public static boolean isModel(Schema schema) {
    if (schema == null) {
      return false;
    }
    if (null != schema.getProperties() && !schema.getProperties().isEmpty()) {
      return true;
    }
    return schema instanceof ComposedSchema || schema instanceof ObjectSchema;
  }

  /**
     * Check to see if the schema is a model with properties only (non-composed model)
     *
     * @param schema potentially containing a '$ref'
     * @return true if it's a model with at least one properties
     */
  public static boolean isModelWithPropertiesOnly(Schema schema) {
    if (schema == null) {
      return false;
    }
    if (null != schema.getProperties() && !schema.getProperties().isEmpty() && (schema.getAdditionalProperties() == null || (schema.getAdditionalProperties() instanceof Boolean && !(Boolean) schema.getAdditionalProperties()))) {
      return true;
    }
    return false;
  }

  public static boolean hasValidation(Schema sc) {
    return (sc.getMaxItems() != null || sc.getMinProperties() != null || sc.getMaxProperties() != null || sc.getMinLength() != null || sc.getMinItems() != null || sc.getMultipleOf() != null || sc.getPattern() != null || sc.getMaxLength() != null || sc.getMinimum() != null || sc.getMaximum() != null || sc.getExclusiveMaximum() != null || sc.getExclusiveMinimum() != null || sc.getUniqueItems() != null);
  }

  /**
     * Check to see if the schema is a free form object.
     * <p>
     * A free form object is an object (i.e. 'type: object' in a OAS document) that:
     * 1) Does not define properties, and
     * 2) Is not a composed schema (no anyOf, oneOf, allOf), and
     * 3) additionalproperties is not defined, or additionalproperties: true, or additionalproperties: {}.
     * <p>
     * Examples:
     * <p>
     * components:
     *   schemas:
     *     arbitraryObject:
     *       type: object
     *       description: This is a free-form object.
     *         The value must be a map of strings to values. The value cannot be 'null'.
     *         It cannot be array, string, integer, number.
     *     arbitraryNullableObject:
     *       type: object
     *       description: This is a free-form object.
     *         The value must be a map of strings to values. The value can be 'null',
     *         It cannot be array, string, integer, number.
     *       nullable: true
     *     arbitraryTypeValue:
     *       description: This is NOT a free-form object.
     *         The value can be any type except the 'null' value.
     *
     * @param openAPI the object that encapsulates the OAS document.
     * @param schema  potentially containing a '$ref'
     * @return true if it's a free-form object
     */
  public static boolean isFreeFormObject(OpenAPI openAPI, Schema schema) {
    if (schema == null) {
      once(LOGGER).error("Schema cannot be null in isFreeFormObject check");
      return false;
    }
    if (schema instanceof ComposedSchema) {
      ComposedSchema cs = (ComposedSchema) schema;
      List<Schema> interfaces = ModelUtils.getInterfaces(cs);
      if (interfaces != null && !interfaces.isEmpty()) {
        return false;
      }
    }
    if ("object".equals(schema.getType())) {
      if ((schema.getProperties() == null || schema.getProperties().isEmpty())) {
        Schema addlProps = getAdditionalProperties(openAPI, schema);
        if (schema.getExtensions() != null && schema.getExtensions().containsKey(freeFormExplicit)) {
          boolean isFreeFormExplicit = Boolean.parseBoolean(String.valueOf(schema.getExtensions().get(freeFormExplicit)));
          if (!isFreeFormExplicit && addlProps != null && addlProps.getProperties() != null && !addlProps.getProperties().isEmpty()) {
            once(LOGGER).error(String.format(Locale.ROOT, "Potentially confusing usage of %s within model which defines additional properties", freeFormExplicit));
          }
          return isFreeFormExplicit;
        }
        if (addlProps == null) {
          return true;
        } else {
          if (addlProps instanceof ObjectSchema) {
            ObjectSchema objSchema = (ObjectSchema) addlProps;
            if (objSchema.getProperties() == null || objSchema.getProperties().isEmpty()) {
              return true;
            }
          } else {
            if (addlProps instanceof Schema) {
              if (addlProps.getType() == null && addlProps.get$ref() == null && (addlProps.getProperties() == null || addlProps.getProperties().isEmpty())) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }

  /**
     * If a Schema contains a reference to another Schema with '$ref', returns the referenced Schema if it is found or the actual Schema in the other cases.
     *
     * @param openAPI specification being checked
     * @param schema  potentially containing a '$ref'
     * @return schema without '$ref'
     */
  public static Schema getReferencedSchema(OpenAPI openAPI, Schema schema) {
    if (schema != null && StringUtils.isNotEmpty(schema.get$ref())) {
      String name = getSimpleRef(schema.get$ref());
      Schema referencedSchema = getSchema(openAPI, name);
      if (referencedSchema != null) {
        return referencedSchema;
      }
    }
    return schema;
  }

  public static Schema getSchema(OpenAPI openAPI, String name) {
    if (name == null) {
      return null;
    }
    return getSchemas(openAPI).get(name);
  }

  /**
     * Return a Map of the schemas defined under /components/schemas in the OAS document.
     * The returned Map only includes the direct children of /components/schemas in the OAS document; the Map
     * does not include inlined schemas.
     *
     * @param openAPI the OpenAPI document.
     * @return a map of schemas in the OAS document.
     */
  public static Map<String, Schema> getSchemas(OpenAPI openAPI) {
    if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getSchemas() != null) {
      return openAPI.getComponents().getSchemas();
    }
    return Collections.emptyMap();
  }

  /**
     * Return the list of all schemas in the 'components/schemas' section of an openAPI specification,
     * including inlined schemas and children of composed schemas.
     *
     * @param openAPI OpenAPI document
     * @return a list of schemas
     */
  public static List<Schema> getAllSchemas(OpenAPI openAPI) {
    List<Schema> allSchemas = new ArrayList<Schema>();
    List<String> refSchemas = new ArrayList<String>();
    getSchemas(openAPI).forEach((key, schema) -> {
      visitSchema(openAPI, schema, null, refSchemas, (s, mimetype) -> {
        allSchemas.add(s);
      });
    });
    return allSchemas;
  }

  /**
     * If a RequestBody contains a reference to another RequestBody with '$ref', returns the referenced RequestBody if it is found or the actual RequestBody in the other cases.
     *
     * @param openAPI     specification being checked
     * @param requestBody potentially containing a '$ref'
     * @return requestBody without '$ref'
     */
  public static RequestBody getReferencedRequestBody(OpenAPI openAPI, RequestBody requestBody) {
    if (requestBody != null && StringUtils.isNotEmpty(requestBody.get$ref())) {
      String name = getSimpleRef(requestBody.get$ref());
      RequestBody referencedRequestBody = getRequestBody(openAPI, name);
      if (referencedRequestBody != null) {
        return referencedRequestBody;
      }
    }
    return requestBody;
  }

  public static RequestBody getRequestBody(OpenAPI openAPI, String name) {
    if (name == null) {
      return null;
    }
    if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getRequestBodies() != null) {
      return openAPI.getComponents().getRequestBodies().get(name);
    }
    return null;
  }

  /**
     * If a ApiResponse contains a reference to another ApiResponse with '$ref', returns the referenced ApiResponse if it is found or the actual ApiResponse in the other cases.
     *
     * @param openAPI     specification being checked
     * @param apiResponse potentially containing a '$ref'
     * @return apiResponse without '$ref'
     */
  public static ApiResponse getReferencedApiResponse(OpenAPI openAPI, ApiResponse apiResponse) {
    if (apiResponse != null && StringUtils.isNotEmpty(apiResponse.get$ref())) {
      String name = getSimpleRef(apiResponse.get$ref());
      ApiResponse referencedApiResponse = getApiResponse(openAPI, name);
      if (referencedApiResponse != null) {
        return referencedApiResponse;
      }
    }
    return apiResponse;
  }

  public static ApiResponse getApiResponse(OpenAPI openAPI, String name) {
    if (name == null) {
      return null;
    }
    if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getResponses() != null) {
      return openAPI.getComponents().getResponses().get(name);
    }
    return null;
  }

  /**
     * If a Parameter contains a reference to another Parameter with '$ref', returns the referenced Parameter if it is found or the actual Parameter in the other cases.
     *
     * @param openAPI   specification being checked
     * @param parameter potentially containing a '$ref'
     * @return parameter without '$ref'
     */
  public static Parameter getReferencedParameter(OpenAPI openAPI, Parameter parameter) {
    if (parameter != null && StringUtils.isNotEmpty(parameter.get$ref())) {
      String name = getSimpleRef(parameter.get$ref());
      Parameter referencedParameter = getParameter(openAPI, name);
      if (referencedParameter != null) {
        return referencedParameter;
      }
    }
    return parameter;
  }

  public static Parameter getParameter(OpenAPI openAPI, String name) {
    if (name == null) {
      return null;
    }
    if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getParameters() != null) {
      return openAPI.getComponents().getParameters().get(name);
    }
    return null;
  }

  /**
     * If a Callback contains a reference to another Callback with '$ref', returns the referenced Callback if it is found or the actual Callback in the other cases.
     *
     * @param openAPI  specification being checked
     * @param callback potentially containing a '$ref'
     * @return callback without '$ref'
     */
  public static Callback getReferencedCallback(OpenAPI openAPI, Callback callback) {
    if (callback != null && StringUtils.isNotEmpty(callback.get$ref())) {
      String name = getSimpleRef(callback.get$ref());
      Callback referencedCallback = getCallback(openAPI, name);
      if (referencedCallback != null) {
        return referencedCallback;
      }
    }
    return callback;
  }

  public static Callback getCallback(OpenAPI openAPI, String name) {
    if (name == null) {
      return null;
    }
    if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getCallbacks() != null) {
      return openAPI.getComponents().getCallbacks().get(name);
    }
    return null;
  }

  /**
     * Return the first defined Schema for a RequestBody
     *
     * @param requestBody request body of the operation
     * @return first schema
     */
  public static Schema getSchemaFromRequestBody(RequestBody requestBody) {
    return getSchemaFromContent(requestBody.getContent());
  }

  /**
     * Return the first defined Schema for a ApiResponse
     *
     * @param response api response of the operation
     * @return firstSchema
     */
  public static Schema getSchemaFromResponse(ApiResponse response) {
    return getSchemaFromContent(response.getContent());
  }

  /**
     * Return the first Schema from a specified OAS 'content' section.
     * <p>
     * For example, given the following OAS, this method returns the schema
     * for the 'application/json' content type because it is listed first in the OAS.
     * <p>
     * responses:
     *   '200':
     *     content:
     *       application/json:
     *         schema:
     *           $ref: '#/components/schemas/XYZ'
     *       application/xml:
     *          ...
     *
     * @param content a 'content' section in the OAS specification.
     * @return the Schema.
     */
  private static Schema getSchemaFromContent(Content content) {
    if (content == null || content.isEmpty()) {
      return null;
    }
    Map.Entry<String, MediaType> entry = content.entrySet().iterator().next();
    if (content.size() > 1) {
      once(LOGGER).warn("Multiple schemas found in the OAS \'content\' section, returning only the first one ({})", entry.getKey());
    }
    return entry.getValue().getSchema();
  }

  /**
     * Has self reference?
     *
     * @param openAPI OpenAPI spec.
     * @param schema  Schema
     * @return boolean true if it has at least one self reference
     */
  public static boolean hasSelfReference(OpenAPI openAPI, Schema schema) {
    return hasSelfReference(openAPI, schema, null);
  }

  /**
     * Has self reference?
     *
     * @param openAPI            OpenAPI spec.
     * @param schema             Schema
     * @param visitedSchemaNames A set of visited schema names
     * @return boolean true if it has at least one self reference
     */
  public static boolean hasSelfReference(OpenAPI openAPI, Schema schema, Set<String> visitedSchemaNames) {
    if (visitedSchemaNames == null) {
      visitedSchemaNames = new HashSet<String>();
    }
    if (schema.get$ref() != null) {
      String ref = getSimpleRef(schema.get$ref());
      if (!visitedSchemaNames.contains(ref)) {
        visitedSchemaNames.add(ref);
        Schema referencedSchema = getSchemas(openAPI).get(ref);
        if (referencedSchema != null) {
          return hasSelfReference(openAPI, referencedSchema, visitedSchemaNames);
        } else {
          LOGGER.error("Failed to obtain schema from `{}` in self reference check", ref);
          return false;
        }
      } else {
        return true;
      }
    }
    if (schema instanceof ComposedSchema) {
      List<Schema> oneOf = ((ComposedSchema) schema).getOneOf();
      if (oneOf != null) {
        for (Schema s : oneOf) {
          if (hasSelfReference(openAPI, s, visitedSchemaNames)) {
            return true;
          }
        }
      }
      List<Schema> allOf = ((ComposedSchema) schema).getAllOf();
      if (allOf != null) {
        for (Schema s : allOf) {
          if (hasSelfReference(openAPI, s, visitedSchemaNames)) {
            return true;
          }
        }
      }
      List<Schema> anyOf = ((ComposedSchema) schema).getAnyOf();
      if (anyOf != null) {
        for (Schema s : anyOf) {
          if (hasSelfReference(openAPI, s, visitedSchemaNames)) {
            return true;
          }
        }
      }
    } else {
      if (isArraySchema(schema)) {
        Schema itemsSchema = ((ArraySchema) schema).getItems();
        if (itemsSchema != null) {
          return hasSelfReference(openAPI, itemsSchema, visitedSchemaNames);
        }
      } else {
        if (isMapSchema(schema)) {
          Object additionalProperties = schema.getAdditionalProperties();
          if (additionalProperties instanceof Schema) {
            return hasSelfReference(openAPI, (Schema) additionalProperties, visitedSchemaNames);
          }
        } else {
          if (schema.getNot() != null) {
            return hasSelfReference(openAPI, schema.getNot(), visitedSchemaNames);
          } else {
            if (schema.getProperties() != null && !schema.getProperties().isEmpty()) {
              for (Schema property : ((Map<String, Schema>) schema.getProperties()).values()) {
                if (hasSelfReference(openAPI, property, visitedSchemaNames)) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  /**
     * Get the actual schema from aliases. If the provided schema is not an alias, the schema itself will be returned.
     *
     * @param openAPI specification being checked
     * @param schema  schema (alias or direct reference)
     * @return actual schema
     */
  public static Schema unaliasSchema(OpenAPI openAPI, Schema schema) {
    return unaliasSchema(openAPI, schema, Collections.emptyMap());
  }

  /**
     * Get the actual schema from aliases. If the provided schema is not an alias, the schema itself will be returned.
     *
     * @param openAPI        OpenAPI document containing the schemas.
     * @param schema         schema (alias or direct reference)
     * @param schemaMappings mappings of external types to be omitted by unaliasing
     * @return actual schema
     */
  public static Schema unaliasSchema(OpenAPI openAPI, Schema schema, Map<String, String> schemaMappings) {
    Map<String, Schema> allSchemas = getSchemas(openAPI);
    if (allSchemas == null || allSchemas.isEmpty()) {
      return schema;
    }
    if (schema != null && StringUtils.isNotEmpty(schema.get$ref())) {
      String simpleRef = ModelUtils.getSimpleRef(schema.get$ref());
      if (schemaMappings.containsKey(simpleRef)) {
        LOGGER.debug("Schema unaliasing of {} omitted because aliased class is to be mapped to {}", simpleRef, schemaMappings.get(simpleRef));
        return schema;
      }
      Schema ref = allSchemas.get(simpleRef);
      if (ref == null) {
        once(LOGGER).warn("{} is not defined", schema.get$ref());
        return schema;
      } else {
        if (ref.getEnum() != null && !ref.getEnum().isEmpty()) {
          return schema;
        } else {
          if (isArraySchema(ref)) {
            if (isGenerateAliasAsModel(ref)) {
              return schema;
            } else {
              return unaliasSchema(openAPI, allSchemas.get(ModelUtils.getSimpleRef(schema.get$ref())), schemaMappings);
            }
          } else {
            if (isComposedSchema(ref)) {
              return schema;
            } else {
              if (isMapSchema(ref)) {
                if (ref.getProperties() != null && !ref.getProperties().isEmpty()) {
                  return schema;
                } else {
                  if (isGenerateAliasAsModel(ref)) {
                    return schema;
                  } else {
                    return unaliasSchema(openAPI, allSchemas.get(ModelUtils.getSimpleRef(schema.get$ref())), schemaMappings);
                  }
                }
              } else {
                if (isObjectSchema(ref)) {
                  if (ref.getProperties() != null && !ref.getProperties().isEmpty()) {
                    return schema;
                  } else {
                    return unaliasSchema(openAPI, allSchemas.get(ModelUtils.getSimpleRef(schema.get$ref())), schemaMappings);
                  }
                } else {
                  return unaliasSchema(openAPI, allSchemas.get(ModelUtils.getSimpleRef(schema.get$ref())), schemaMappings);
                }
              }
            }
          }
        }
      }
    }
    return schema;
  }

  /**
     * Returns the additionalProperties Schema for the specified input schema.
     * <p>
     * The additionalProperties keyword is used to control the handling of additional, undeclared
     * properties, that is, properties whose names are not listed in the properties keyword.
     * The additionalProperties keyword may be either a boolean or an object.
     * If additionalProperties is a boolean and set to false, no additional properties are allowed.
     * By default when the additionalProperties keyword is not specified in the input schema,
     * any additional properties are allowed. This is equivalent to setting additionalProperties
     * to the boolean value True or setting additionalProperties: {}
     *
     * @param openAPI the object that encapsulates the OAS document.
     * @param schema  the input schema that may or may not have the additionalProperties keyword.
     * @return the Schema of the additionalProperties. The null value is returned if no additional
     * properties are allowed.
     */
  public static Schema getAdditionalProperties(OpenAPI openAPI, Schema schema) {
    Object addProps = schema.getAdditionalProperties();
    if (addProps instanceof Schema) {
      return (Schema) addProps;
    }
    if (addProps == null) {
      if (isDisallowAdditionalPropertiesIfNotPresent()) {
        return null;
      }
    }
    if (addProps == null || (addProps instanceof Boolean && (Boolean) addProps)) {
      return new Schema();
    }
    return null;
  }

  public static Header getReferencedHeader(OpenAPI openAPI, Header header) {
    if (header != null && StringUtils.isNotEmpty(header.get$ref())) {
      String name = getSimpleRef(header.get$ref());
      Header referencedheader = getHeader(openAPI, name);
      if (referencedheader != null) {
        return referencedheader;
      }
    }
    return header;
  }

  public static Header getHeader(OpenAPI openAPI, String name) {
    if (name == null) {
      return null;
    }
    if (openAPI != null && openAPI.getComponents() != null && openAPI.getComponents().getHeaders() != null) {
      return openAPI.getComponents().getHeaders().get(name);
    }
    return null;
  }

  public static Map<String, List<String>> getChildrenMap(OpenAPI openAPI) {
    Map<String, Schema> allSchemas = getSchemas(openAPI);
    Map<String, List<Entry<String, Schema>>> groupedByParent = allSchemas.entrySet().stream().filter((entry) -> isComposedSchema(entry.getValue())).filter((entry) -> getParentName((ComposedSchema) entry.getValue(), allSchemas) != null).collect(Collectors.groupingBy((entry) -> getParentName((ComposedSchema) entry.getValue(), allSchemas)));
    return groupedByParent.entrySet().stream().collect(Collectors.toMap((entry) -> entry.getKey(), (entry) -> entry.getValue().stream().map((e) -> e.getKey()).collect(Collectors.toList())));
  }

  /**
     * Get the interfaces from the schema (composed)
     *
     * @param composed schema (alias or direct reference)
     * @return a list of schema defined in allOf, anyOf or oneOf
     */
  public static List<Schema> getInterfaces(ComposedSchema composed) {
    if (composed.getAllOf() != null && !composed.getAllOf().isEmpty()) {
      return composed.getAllOf();
    } else {
      if (composed.getAnyOf() != null && !composed.getAnyOf().isEmpty()) {
        return composed.getAnyOf();
      } else {
        if (composed.getOneOf() != null && !composed.getOneOf().isEmpty()) {
          return composed.getOneOf();
        } else {
          return Collections.emptyList();
        }
      }
    }
  }

  /**
     * Get the parent model name from the composed schema (allOf, anyOf, oneOf).
     * It traverses the OAS model (possibly resolving $ref) to determine schemas
     * that specify a determinator.
     * If there are multiple elements in the composed schema and it is not clear
     * which one should be the parent, return null.
     * <p>
     * For example, given the following OAS spec, the parent of 'Dog' is Animal
     * because 'Animal' specifies a discriminator.
     * <p>
     * animal:
     *   type: object
     *   discriminator:
     *     propertyName: type
     *   properties:
     *     type: string
     *
     * <p>
     * dog:
     *   allOf:
     *      - $ref: '#/components/schemas/animal'
     *      - type: object
     *        properties:
     *          breed: string
     *
     * @param composedSchema schema (alias or direct reference)
     * @param allSchemas     all schemas
     * @return the name of the parent model
     */
  public static String getParentName(ComposedSchema composedSchema, Map<String, Schema> allSchemas) {
    List<Schema> interfaces = getInterfaces(composedSchema);
    int nullSchemaChildrenCount = 0;
    boolean hasAmbiguousParents = false;
    List<String> refedWithoutDiscriminator = new ArrayList<>();
    if (interfaces != null && !interfaces.isEmpty()) {
      for (Schema schema : interfaces) {
        if (StringUtils.isNotEmpty(schema.get$ref())) {
          String parentName = getSimpleRef(schema.get$ref());
          Schema s = allSchemas.get(parentName);
          if (s == null) {
            LOGGER.error("Failed to obtain schema from {}", parentName);
            return "UNKNOWN_PARENT_NAME";
          } else {
            if (hasOrInheritsDiscriminator(s, allSchemas)) {
              return parentName;
            } else {
              hasAmbiguousParents = true;
              refedWithoutDiscriminator.add(parentName);
            }
          }
        } else {
          if (ModelUtils.isNullType(schema)) {
            nullSchemaChildrenCount++;
          }
        }
      }
      if (refedWithoutDiscriminator.size() == 1 && nullSchemaChildrenCount == 1) {
        hasAmbiguousParents = false;
      }
    }
    if (refedWithoutDiscriminator.size() == 1 && hasAmbiguousParents) {
      LOGGER.info("[deprecated] inheritance without use of \'discriminator.propertyName\' has been deprecated" + " in the 5.x release. Composed schema name: {}. Title: {}", composedSchema.getName(), composedSchema.getTitle());
    }
    return null;
  }

  /**
     * Get the list of parent model names from the schemas (allOf, anyOf, oneOf).
     *
     * @param composedSchema   schema (alias or direct reference)
     * @param allSchemas       all schemas
     * @param includeAncestors if true, include the indirect ancestors in the return value. If false, return the direct parents.
     * @return the name of the parent model
     */
  public static List<String> getAllParentsName(ComposedSchema composedSchema, Map<String, Schema> allSchemas, boolean includeAncestors) {
    List<Schema> interfaces = getInterfaces(composedSchema);
    List<String> names = new ArrayList<String>();
    if (interfaces != null && !interfaces.isEmpty()) {
      for (Schema schema : interfaces) {
        if (StringUtils.isNotEmpty(schema.get$ref())) {
          String parentName = getSimpleRef(schema.get$ref());
          Schema s = allSchemas.get(parentName);
          if (s == null) {
            LOGGER.error("Failed to obtain schema from {}", parentName);
            names.add("UNKNOWN_PARENT_NAME");
          } else {
            if (hasOrInheritsDiscriminator(s, allSchemas)) {
              names.add(parentName);
              if (includeAncestors && s instanceof ComposedSchema) {
                names.addAll(getAllParentsName((ComposedSchema) s, allSchemas, true));
              }
            } else {
            }
          }
        } else {
        }
      }
    }
    String parentName = getParentName(composedSchema, allSchemas);
    if (parentName != null && !names.contains(parentName)) {
      names.add(parentName);
    }
    return names;
  }

  private static boolean hasOrInheritsDiscriminator(Schema schema, Map<String, Schema> allSchemas) {
    if ((schema.getDiscriminator() != null && StringUtils.isNotEmpty(schema.getDiscriminator().getPropertyName())) || (isExtensionParent(schema))) {
      return true;
    } else {
      if (StringUtils.isNotEmpty(schema.get$ref())) {
        String parentName = getSimpleRef(schema.get$ref());
        Schema s = allSchemas.get(parentName);
        if (s != null) {
          return hasOrInheritsDiscriminator(s, allSchemas);
        } else {
          LOGGER.error("Failed to obtain schema from {}", parentName);
        }
      } else {
        if (schema instanceof ComposedSchema) {
          final ComposedSchema composed = (ComposedSchema) schema;
          final List<Schema> interfaces = getInterfaces(composed);
          for (Schema i : interfaces) {
            if (hasOrInheritsDiscriminator(i, allSchemas)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
     * If it's a boolean, returns the value of the extension `x-parent`.
     * If it's string, return true if it's non-empty.
     * If the return value is `true`, the schema is a parent.
     *
     * @param schema    Schema
     * @return boolean
     */
  public static boolean isExtensionParent(Schema schema) {
    if (schema.getExtensions() == null) {
      return false;
    } else {
      Object xParent = schema.getExtensions().get("x-parent");
      if (xParent == null) {
        return false;
      } else {
        if (xParent instanceof Boolean) {
          return (Boolean) xParent;
        } else {
          if (xParent instanceof String) {
            return StringUtils.isNotEmpty((String) xParent);
          } else {
            return false;
          }
        }
      }
    }
  }

  /**
     * Return true if the 'nullable' attribute is set to true in the schema, i.e. if the value
     * of the property can be the null value.
     * <p>
     * In addition, if the OAS document is 3.1 or above, isNullable returns true if the input
     * schema is a 'oneOf' composed document with at most two children, and one of the children
     * is the 'null' type.
     * <p>
     * The caller is responsible for resolving schema references before invoking isNullable.
     * If the input schema is a $ref and the referenced schema has 'nullable: true', this method
     * returns false (because the nullable attribute is defined in the referenced schema).
     * <p>
     * The 'nullable' attribute was introduced in OAS 3.0.
     * The 'nullable' attribute is deprecated in OAS 3.1. In a OAS 3.1 document, the preferred way
     * to specify nullable properties is to use the 'null' type.
     *
     * @param schema the OAS schema.
     * @return true if the schema is nullable.
     */
  public static boolean isNullable(Schema schema) {
    if (schema == null) {
      return false;
    }
    if (Boolean.TRUE.equals(schema.getNullable())) {
      return true;
    }
    if (schema.getExtensions() != null && schema.getExtensions().get("x-nullable") != null) {
      return Boolean.parseBoolean(schema.getExtensions().get("x-nullable").toString());
    }
    if (schema instanceof ComposedSchema) {
      return isNullableComposedSchema(((ComposedSchema) schema));
    }
    return false;
  }

  /**
     * Return true if the specified composed schema is 'oneOf', contains one or two elements,
     * and at least one of the elements is the 'null' type.
     * <p>
     * The 'null' type is supported in OAS 3.1 and above.
     * In the example below, the 'OptionalOrder' can have the null value because the 'null'
     * type is one of the elements under 'oneOf'.
     * <p>
     * OptionalOrder:
     *   oneOf:
     *     - type: 'null'
     *     - $ref: '#/components/schemas/Order'
     *
     * @param schema the OAS composed schema.
     * @return true if the composed schema is nullable.
     */
  public static boolean isNullableComposedSchema(ComposedSchema schema) {
    List<Schema> oneOf = schema.getOneOf();
    if (oneOf != null && oneOf.size() <= 2) {
      for (Schema s : oneOf) {
        if (isNullType(s)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
     * isNullType returns true if the input schema is the 'null' type.
     * <p>
     * The 'null' type is supported in OAS 3.1 and above. It is not supported
     * in OAS 2.0 and OAS 3.0.x.
     * <p>
     * For example, the "null" type could be used to specify that a value must
     * either be null or a specified type:
     * <p>
     * OptionalOrder:
     *   oneOf:
     *     - type: 'null'
     *     - $ref: '#/components/schemas/Order'
     *
     * @param schema the OpenAPI schema
     * @return true if the schema is the 'null' type
     */
  public static boolean isNullType(Schema schema) {
    if ("null".equals(schema.getType())) {
      return true;
    }
    return false;
  }

  /**
     * For when a type is not defined on a schema
     * Note: properties, additionalProperties, enums, validations, items, and composed schemas (oneOf/anyOf/allOf)
     * can be defined or omitted on these any type schemas
     *
     * @param schema the schema that we are checking
     * @return boolean
     */
  public static boolean isAnyType(Schema schema) {
    return (schema.get$ref() == null && schema.getType() == null);
  }

  public static void syncValidationProperties(Schema schema, IJsonSchemaValidationProperties target) {
    if (schema != null && target != null) {
      if (isNullType(schema) || schema.get$ref() != null || isBooleanSchema(schema)) {
        return;
      }
      Integer minItems = schema.getMinItems();
      Integer maxItems = schema.getMaxItems();
      Boolean uniqueItems = schema.getUniqueItems();
      Integer minProperties = schema.getMinProperties();
      Integer maxProperties = schema.getMaxProperties();
      Integer minLength = schema.getMinLength();
      Integer maxLength = schema.getMaxLength();
      String pattern = schema.getPattern();
      BigDecimal multipleOf = schema.getMultipleOf();
      BigDecimal minimum = schema.getMinimum();
      BigDecimal maximum = schema.getMaximum();
      Boolean exclusiveMinimum = schema.getExclusiveMinimum();
      Boolean exclusiveMaximum = schema.getExclusiveMaximum();
      if (isArraySchema(schema)) {
        setArrayValidations(minItems, maxItems, uniqueItems, target);
      } else {
        if (isTypeObjectSchema(schema)) {
          setObjectValidations(minProperties, maxProperties, target);
        } else {
          if (isStringSchema(schema)) {
            setStringValidations(minLength, maxLength, pattern, target);
            if (isDecimalSchema(schema)) {
              setNumericValidations(schema, multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum, target);
            }
          } else {
            if (isNumberSchema(schema) || isIntegerSchema(schema)) {
              setNumericValidations(schema, multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum, target);
            } else {
              if (isAnyType(schema)) {
                setArrayValidations(minItems, maxItems, uniqueItems, target);
                setObjectValidations(minProperties, maxProperties, target);
                setStringValidations(minLength, maxLength, pattern, target);
                setNumericValidations(schema, multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum, target);
              }
            }
          }
        }
      }
      if (maxItems != null || minItems != null || minProperties != null || maxProperties != null || minLength != null || maxLength != null || multipleOf != null || pattern != null || minimum != null || maximum != null || exclusiveMinimum != null || exclusiveMaximum != null || uniqueItems != null) {
        target.setHasValidation(true);
      }
    }
  }

  private static void setArrayValidations(Integer minItems, Integer maxItems, Boolean uniqueItems, IJsonSchemaValidationProperties target) {
    if (minItems != null) {
      target.setMinItems(minItems);
    }
    if (maxItems != null) {
      target.setMaxItems(maxItems);
    }
    if (uniqueItems != null) {
      target.setUniqueItems(uniqueItems);
    }
    if (uniqueItems != null) {
      target.setUniqueItemsBoolean(uniqueItems);
    }
  }

  private static void setObjectValidations(Integer minProperties, Integer maxProperties, IJsonSchemaValidationProperties target) {
    if (minProperties != null) {
      target.setMinProperties(minProperties);
    }
    if (maxProperties != null) {
      target.setMaxProperties(maxProperties);
    }
  }

  private static void setStringValidations(Integer minLength, Integer maxLength, String pattern, IJsonSchemaValidationProperties target) {
    if (minLength != null) {
      target.setMinLength(minLength);
    }
    if (maxLength != null) {
      target.setMaxLength(maxLength);
    }
    if (pattern != null) {
      target.setPattern(pattern);
    }
  }

  private static void setNumericValidations(Schema schema, BigDecimal multipleOf, BigDecimal minimum, BigDecimal maximum, Boolean exclusiveMinimum, Boolean exclusiveMaximum, IJsonSchemaValidationProperties target) {
    if (multipleOf != null) {
      target.setMultipleOf(multipleOf);
    }
    if (minimum != null) {
      if (isIntegerSchema(schema)) {
        target.setMinimum(String.valueOf(minimum.longValue()));
      } else {
        target.setMinimum(String.valueOf(minimum));
      }
      if (exclusiveMinimum != null) {
        target.setExclusiveMinimum(exclusiveMinimum);
      }
    }
    if (maximum != null) {
      if (isIntegerSchema(schema)) {
        target.setMaximum(String.valueOf(maximum.longValue()));
      } else {
        target.setMaximum(String.valueOf(maximum));
      }
      if (exclusiveMaximum != null) {
        target.setExclusiveMaximum(exclusiveMaximum);
      }
    }
  }

  private static ObjectMapper getRightMapper(String data) {
    ObjectMapper mapper;
    if (data.trim().startsWith("{")) {
      mapper = JSON_MAPPER;
    } else {
      mapper = YAML_MAPPER;
    }
    return mapper;
  }

  /**
     * Parse and return a JsonNode representation of the input OAS document.
     *
     * @param location the URL of the OAS document.
     * @param auths    the list of authorization values to access the remote URL.
     * @return A JsonNode representation of the input OAS document.
     * @throws java.lang.Exception if an error occurs while retrieving the OpenAPI document.
     */
  public static JsonNode readWithInfo(String location, List<AuthorizationValue> auths) throws Exception {
    String data;
    location = location.replaceAll("\\\\", "/");
    if (location.toLowerCase(Locale.ROOT).startsWith("http")) {
      data = RemoteUrl.urlToString(location, auths);
    } else {
      final String fileScheme = "file:";
      Path path;
      if (location.toLowerCase(Locale.ROOT).startsWith(fileScheme)) {
        path = Paths.get(URI.create(location));
      } else {
        path = Paths.get(location);
      }
      if (Files.exists(path)) {
        data = FileUtils.readFileToString(path.toFile(), "UTF-8");
      } else {
        data = ClasspathHelper.loadFileFromClasspath(location);
      }
    }
    return getRightMapper(data).readTree(data);
  }

  /**
     * Parse the OAS document at the specified location, get the swagger or openapi version
     * as specified in the source document, and return the version.
     * <p>
     * For OAS 2.0 documents, return the value of the 'swagger' attribute.
     * For OAS 3.x documents, return the value of the 'openapi' attribute.
     *
     * @param openAPI  the object that encapsulates the OAS document.
     * @param location the URL of the OAS document.
     * @param auths    the list of authorization values to access the remote URL.
     * @return the version of the OpenAPI document.
     */
  public static SemVer getOpenApiVersion(OpenAPI openAPI, String location, List<AuthorizationValue> auths) {
    String version;
    try {
      JsonNode document = readWithInfo(location, auths);
      JsonNode value = document.findValue("swagger");
      if (value == null) {
        value = document.findValue("openapi");
      }
      version = value.asText();
    } catch (Exception ex) {
      LOGGER.warn("Unable to read swagger/openapi attribute");
      version = openAPI.getOpenapi();
    }
    return new SemVer(version);
  }

  /**
     * Returns true if the schema contains allOf but
     * no properties/oneOf/anyOf defined.
     *
     * @param schema the schema
     * @return true if the schema contains allOf but no properties/oneOf/anyOf defined.
     */
  public static boolean isAllOf(Schema schema) {
    if (hasAllOf(schema) && (schema.getProperties() == null || schema.getProperties().isEmpty()) && (schema.getOneOf() == null || schema.getOneOf().isEmpty()) && (schema.getAnyOf() == null || schema.getAnyOf().isEmpty())) {
      return true;
    }
    return false;
  }

  /**
     * Returns true if the schema contains allOf and may or may not have
     * properties/oneOf/anyOf defined.
     *
     * @param schema the schema
     * @return true if allOf is not empty
     */
  public static boolean hasAllOf(Schema schema) {
    if (schema.getAllOf() != null && !schema.getAllOf().isEmpty()) {
      return true;
    }
    return false;
  }

  /**
     * Returns true if the schema contains oneOf but
     * no properties/allOf/anyOf defined.
     *
     * @param schema the schema
     * @return true if the schema contains oneOf but no properties/allOf/anyOf defined.
     */
  public static boolean isOneOf(Schema schema) {
    if (hasOneOf(schema) && (schema.getProperties() == null || schema.getProperties().isEmpty()) && (schema.getAllOf() == null || schema.getAllOf().isEmpty()) && (schema.getAnyOf() == null || schema.getAnyOf().isEmpty())) {
      return true;
    }
    return false;
  }

  /**
     * Returns true if the schema contains oneOf and may or may not have
     * properties/allOf/anyOf defined.
     *
     * @param schema the schema
     * @return true if allOf is not empty
     */
  public static boolean hasOneOf(Schema schema) {
    if (schema.getOneOf() != null && !schema.getOneOf().isEmpty()) {
      return true;
    }
    return false;
  }

  /**
     * Returns true if the schema contains anyOf but
     * no properties/allOf/anyOf defined.
     *
     * @param schema the schema
     * @return true if the schema contains oneOf but no properties/allOf/anyOf defined.
     */
  public static boolean isAnyOf(Schema schema) {
    if (hasAnyOf(schema) && (schema.getProperties() == null || schema.getProperties().isEmpty()) && (schema.getAllOf() == null || schema.getAllOf().isEmpty()) && (schema.getOneOf() == null || schema.getOneOf().isEmpty())) {
      return true;
    }
    return false;
  }

  /**
     * Returns true if the schema contains anyOf and may or may not have
     * properties/allOf/oneOf defined.
     *
     * @param schema the schema
     * @return true if anyOf is not empty
     */
  public static boolean hasAnyOf(Schema schema) {
    if (schema.getAnyOf() != null && !schema.getAnyOf().isEmpty()) {
      return true;
    }
    return false;
  }

  /**
     * Returns true if any of the common attributes of the schema (e.g. readOnly, default, maximum, etc) is defined.
     *
     * @param schema the schema
     * @return true if allOf is not empty
     */
  public static boolean hasCommonAttributesDefined(Schema schema) {
    if (schema.getNullable() != null || schema.getDefault() != null || schema.getMinimum() != null || schema.getMinimum() != null || schema.getExclusiveMaximum() != null || schema.getExclusiveMinimum() != null || schema.getMinLength() != null || schema.getMaxLength() != null || schema.getMinItems() != null || schema.getMaxItems() != null || schema.getReadOnly() != null || schema.getWriteOnly() != null) {
      return true;
    }
    return false;
  }
}