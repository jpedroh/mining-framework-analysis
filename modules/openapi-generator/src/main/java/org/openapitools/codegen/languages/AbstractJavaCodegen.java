package org.openapitools.codegen.languages;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.inject.Injector;
import es.us.isa.idl.IDLStandaloneSetupGenerated;
import es.us.isa.idl.idl.*;
import es.us.isa.idl.idl.impl.*;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.util.SchemaTypeUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Code;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.features.*;
import org.openapitools.codegen.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import static org.openapitools.codegen.utils.StringUtils.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.v3.oas.models.media.*;
import org.openapitools.codegen.languages.features.DocumentationProviderFeatures;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractJavaCodegen extends DefaultCodegen implements CodegenConfig, DocumentationProviderFeatures {
  private final Logger LOGGER = LoggerFactory.getLogger(AbstractJavaCodegen.class);

  private static final String ARTIFACT_VERSION_DEFAULT_VALUE = "1.0.0";

  public static final String FULL_JAVA_UTIL = "fullJavaUtil";

  public static final String DEFAULT_LIBRARY = "<default>";

  public static final String DATE_LIBRARY = "dateLibrary";

  public static final String SUPPORT_ASYNC = "supportAsync";

  public static final String WITH_XML = "withXml";

  public static final String SUPPORT_JAVA6 = "supportJava6";

  public static final String DISABLE_HTML_ESCAPING = "disableHtmlEscaping";

  public static final String BOOLEAN_GETTER_PREFIX = "booleanGetterPrefix";

  public static final String IGNORE_ANYOF_IN_ENUM = "ignoreAnyOfInEnum";

  public static final String ADDITIONAL_MODEL_TYPE_ANNOTATIONS = "additionalModelTypeAnnotations";

  public static final String ADDITIONAL_ENUM_TYPE_ANNOTATIONS = "additionalEnumTypeAnnotations";

  public static final String DISCRIMINATOR_CASE_SENSITIVE = "discriminatorCaseSensitive";

  public static final String OPENAPI_NULLABLE = "openApiNullable";

  public static final String JACKSON = "jackson";

  protected String dateLibrary = "java8";

  protected boolean supportAsync = false;

  protected boolean withXml = false;

  protected String invokerPackage = "org.openapitools";

  protected String groupId = "org.openapitools";

  protected String artifactId = "openapi-java";

  protected String artifactVersion = null;

  protected String artifactUrl = "https://github.com/openapitools/openapi-generator";

  protected String artifactDescription = "OpenAPI Java";

  protected String developerName = "OpenAPI-Generator Contributors";

  protected String developerEmail = "team@openapitools.org";

  protected String developerOrganization = "OpenAPITools.org";

  protected String developerOrganizationUrl = "http://openapitools.org";

  protected String scmConnection = "scm:git:git@github.com:openapitools/openapi-generator.git";

  protected String scmDeveloperConnection = "scm:git:git@github.com:openapitools/openapi-generator.git";

  protected String scmUrl = "https://github.com/openapitools/openapi-generator";

  protected String licenseName = "Unlicense";

  protected String licenseUrl = "http://unlicense.org";

  protected String projectFolder = "src/main";

  protected String projectTestFolder = "src/test";

  protected String sourceFolder = projectFolder + "/java";

  protected String testFolder = projectTestFolder + "/java";

  protected boolean fullJavaUtil;

  protected boolean discriminatorCaseSensitive = true;

  protected String javaUtilPrefix = "";

  protected Boolean serializableModel = false;

  protected boolean serializeBigDecimalAsString = false;

  protected String apiDocPath = "docs/";

  protected String modelDocPath = "docs/";

  protected boolean supportJava6 = false;

  protected boolean disableHtmlEscaping = false;

  protected String booleanGetterPrefix = "get";

  protected boolean ignoreAnyOfInEnum = false;

  protected String parentGroupId = "";

  protected String parentArtifactId = "";

  protected String parentVersion = "";

  protected boolean parentOverridden = false;

  protected List<String> additionalModelTypeAnnotations = new LinkedList<>();

  protected List<String> additionalEnumTypeAnnotations = new LinkedList<>();

  protected boolean openApiNullable = true;

  protected String assertOperation;

  protected CodegenOperation currentOperation;

  public AbstractJavaCodegen() {
    super();
    modifyFeatureSet((features) -> features.includeDocumentationFeatures(DocumentationFeature.Readme).wireFormatFeatures(EnumSet.of(WireFormatFeature.JSON, WireFormatFeature.XML)).securityFeatures(EnumSet.noneOf(SecurityFeature.class)).excludeGlobalFeatures(GlobalFeature.XMLStructureDefinitions, GlobalFeature.Callbacks, GlobalFeature.LinkObjects, GlobalFeature.ParameterStyling).excludeSchemaSupportFeatures(SchemaSupportFeature.Polymorphism).includeClientModificationFeatures(ClientModificationFeature.BasePath));
    supportsInheritance = true;
    modelTemplateFiles.put("model.mustache", ".java");
    apiTemplateFiles.put("api.mustache", ".java");
    apiTestTemplateFiles.put("api_test.mustache", ".java");
    modelDocTemplateFiles.put("model_doc.mustache", ".md");
    apiDocTemplateFiles.put("api_doc.mustache", ".md");
    hideGenerationTimestamp = false;
    setReservedWordsLowerCase(Arrays.asList("object", "list", "file", "localVarPath", "localVarQueryParams", "localVarCollectionQueryParams", "localVarHeaderParams", "localVarCookieParams", "localVarFormParams", "localVarPostBody", "localVarAccepts", "localVarAccept", "localVarContentTypes", "localVarContentType", "localVarAuthNames", "localReturnType", "ApiClient", "ApiException", "ApiResponse", "Configuration", "StringUtil", "abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package", "synchronized", "boolean", "do", "goto", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while", "null"));
    languageSpecificPrimitives = Sets.newHashSet("String", "boolean", "Boolean", "Double", "Integer", "Long", "Float", "Object", "byte[]");
    instantiationTypes.put("array", "ArrayList");
    instantiationTypes.put("set", "LinkedHashSet");
    instantiationTypes.put("map", "HashMap");
    typeMapping.put("date", "Date");
    typeMapping.put("file", "File");
    typeMapping.put("AnyType", "Object");
    importMapping.put("BigDecimal", "java.math.BigDecimal");
    importMapping.put("UUID", "java.util.UUID");
    importMapping.put("URI", "java.net.URI");
    importMapping.put("File", "java.io.File");
    importMapping.put("Date", "java.util.Date");
    importMapping.put("Timestamp", "java.sql.Timestamp");
    importMapping.put("Map", "java.util.Map");
    importMapping.put("HashMap", "java.util.HashMap");
    importMapping.put("Array", "java.util.List");
    importMapping.put("ArrayList", "java.util.ArrayList");
    importMapping.put("List", "java.util.*");
    importMapping.put("Set", "java.util.*");
    importMapping.put("LinkedHashSet", "java.util.LinkedHashSet");
    importMapping.put("DateTime", "org.joda.time.*");
    importMapping.put("LocalDateTime", "org.joda.time.*");
    importMapping.put("LocalDate", "org.joda.time.*");
    importMapping.put("LocalTime", "org.joda.time.*");
    cliOptions.add(new CliOption(CodegenConstants.MODEL_PACKAGE, CodegenConstants.MODEL_PACKAGE_DESC));
    cliOptions.add(new CliOption(CodegenConstants.API_PACKAGE, CodegenConstants.API_PACKAGE_DESC));
    cliOptions.add(new CliOption(CodegenConstants.INVOKER_PACKAGE, CodegenConstants.INVOKER_PACKAGE_DESC).defaultValue(this.getInvokerPackage()));
    cliOptions.add(new CliOption(CodegenConstants.GROUP_ID, CodegenConstants.GROUP_ID_DESC).defaultValue(this.getGroupId()));
    cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_ID, CodegenConstants.ARTIFACT_ID_DESC).defaultValue(this.getArtifactId()));
    cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_VERSION, CodegenConstants.ARTIFACT_VERSION_DESC).defaultValue(ARTIFACT_VERSION_DEFAULT_VALUE));
    cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_URL, CodegenConstants.ARTIFACT_URL_DESC).defaultValue(this.getArtifactUrl()));
    cliOptions.add(new CliOption(CodegenConstants.ARTIFACT_DESCRIPTION, CodegenConstants.ARTIFACT_DESCRIPTION_DESC).defaultValue(this.getArtifactDescription()));
    cliOptions.add(new CliOption(CodegenConstants.SCM_CONNECTION, CodegenConstants.SCM_CONNECTION_DESC).defaultValue(this.getScmConnection()));
    cliOptions.add(new CliOption(CodegenConstants.SCM_DEVELOPER_CONNECTION, CodegenConstants.SCM_DEVELOPER_CONNECTION_DESC).defaultValue(this.getScmDeveloperConnection()));
    cliOptions.add(new CliOption(CodegenConstants.SCM_URL, CodegenConstants.SCM_URL_DESC).defaultValue(this.getScmUrl()));
    cliOptions.add(new CliOption(CodegenConstants.DEVELOPER_NAME, CodegenConstants.DEVELOPER_NAME_DESC).defaultValue(this.getDeveloperName()));
    cliOptions.add(new CliOption(CodegenConstants.DEVELOPER_EMAIL, CodegenConstants.DEVELOPER_EMAIL_DESC).defaultValue(this.getDeveloperEmail()));
    cliOptions.add(new CliOption(CodegenConstants.DEVELOPER_ORGANIZATION, CodegenConstants.DEVELOPER_ORGANIZATION_DESC).defaultValue(this.getDeveloperOrganization()));
    cliOptions.add(new CliOption(CodegenConstants.DEVELOPER_ORGANIZATION_URL, CodegenConstants.DEVELOPER_ORGANIZATION_URL_DESC).defaultValue(this.getDeveloperOrganizationUrl()));
    cliOptions.add(new CliOption(CodegenConstants.LICENSE_NAME, CodegenConstants.LICENSE_NAME_DESC).defaultValue(this.getLicenseName()));
    cliOptions.add(new CliOption(CodegenConstants.LICENSE_URL, CodegenConstants.LICENSE_URL_DESC).defaultValue(this.getLicenseUrl()));
    cliOptions.add(new CliOption(CodegenConstants.SOURCE_FOLDER, CodegenConstants.SOURCE_FOLDER_DESC).defaultValue(this.getSourceFolder()));
    cliOptions.add(CliOption.newBoolean(CodegenConstants.SERIALIZABLE_MODEL, CodegenConstants.SERIALIZABLE_MODEL_DESC, this.getSerializableModel()));
    cliOptions.add(CliOption.newBoolean(CodegenConstants.SERIALIZE_BIG_DECIMAL_AS_STRING, CodegenConstants.SERIALIZE_BIG_DECIMAL_AS_STRING_DESC, serializeBigDecimalAsString));
    cliOptions.add(CliOption.newBoolean(FULL_JAVA_UTIL, "whether to use fully qualified name for classes under java.util. This option only works for Java API client", fullJavaUtil));
    cliOptions.add(CliOption.newBoolean(DISCRIMINATOR_CASE_SENSITIVE, "Whether the discriminator value lookup should be case-sensitive or not. This option only works for Java API client", discriminatorCaseSensitive));
    cliOptions.add(CliOption.newBoolean(CodegenConstants.HIDE_GENERATION_TIMESTAMP, CodegenConstants.HIDE_GENERATION_TIMESTAMP_DESC, this.isHideGenerationTimestamp()));
    cliOptions.add(CliOption.newBoolean(WITH_XML, "whether to include support for application/xml content type and include XML annotations in the model (works with libraries that provide support for JSON and XML)"));
    CliOption dateLibrary = new CliOption(DATE_LIBRARY, "Option. Date library to use").defaultValue(this.getDateLibrary());
    Map<String, String> dateOptions = new HashMap<>();
    dateOptions.put("java8", "Java 8 native JSR310 (preferred for jdk 1.8+)");
    dateOptions.put("java8-localdatetime", "Java 8 using LocalDateTime (for legacy app only)");
    dateOptions.put("joda", "Joda (for legacy app only)");
    dateOptions.put("legacy", "Legacy java.util.Date");
    dateLibrary.setEnum(dateOptions);
    cliOptions.add(dateLibrary);
    cliOptions.add(CliOption.newBoolean(DISABLE_HTML_ESCAPING, "Disable HTML escaping of JSON strings when using gson (needed to avoid problems with byte[] fields)", disableHtmlEscaping));
    cliOptions.add(CliOption.newString(BOOLEAN_GETTER_PREFIX, "Set booleanGetterPrefix").defaultValue(this.getBooleanGetterPrefix()));
    cliOptions.add(CliOption.newBoolean(IGNORE_ANYOF_IN_ENUM, "Ignore anyOf keyword in enum", ignoreAnyOfInEnum));
    cliOptions.add(CliOption.newString(ADDITIONAL_ENUM_TYPE_ANNOTATIONS, "Additional annotations for enum type(class level annotations)"));
    cliOptions.add(CliOption.newString(ADDITIONAL_MODEL_TYPE_ANNOTATIONS, "Additional annotations for model type(class level annotations). List separated by semicolon(;) or new line (Linux or Windows)"));
    cliOptions.add(CliOption.newBoolean(OPENAPI_NULLABLE, "Enable OpenAPI Jackson Nullable library", this.openApiNullable));
    cliOptions.add(CliOption.newBoolean(IMPLICIT_HEADERS, "Skip header parameters in the generated API methods using @ApiImplicitParams annotation.", implicitHeaders));
    cliOptions.add(CliOption.newString(IMPLICIT_HEADERS_REGEX, "Skip header parameters that matches given regex in the generated API methods using @ApiImplicitParams annotation. Note: this parameter is ignored when implicitHeaders=true"));
    cliOptions.add(CliOption.newString(CodegenConstants.PARENT_GROUP_ID, CodegenConstants.PARENT_GROUP_ID_DESC));
    cliOptions.add(CliOption.newString(CodegenConstants.PARENT_ARTIFACT_ID, CodegenConstants.PARENT_ARTIFACT_ID_DESC));
    cliOptions.add(CliOption.newString(CodegenConstants.PARENT_VERSION, CodegenConstants.PARENT_VERSION_DESC));
    CliOption snapShotVersion = CliOption.newString(CodegenConstants.SNAPSHOT_VERSION, CodegenConstants.SNAPSHOT_VERSION_DESC);
    Map<String, String> snapShotVersionOptions = new HashMap<>();
    snapShotVersionOptions.put("true", "Use a SnapShot Version");
    snapShotVersionOptions.put("false", "Use a Release Version");
    snapShotVersion.setEnum(snapShotVersionOptions);
    cliOptions.add(snapShotVersion);
    cliOptions.add(CliOption.newString(TEST_OUTPUT, "Set output folder for models and APIs tests").defaultValue(DEFAULT_TEST_FOLDER));
    if (null != defaultDocumentationProvider()) {
      CliOption documentationProviderCliOption = new CliOption(DOCUMENTATION_PROVIDER, "Select the OpenAPI documentation provider.").defaultValue(defaultDocumentationProvider().toCliOptValue());
      supportedDocumentationProvider().forEach((dp) -> documentationProviderCliOption.addEnum(dp.toCliOptValue(), dp.getDescription()));
      cliOptions.add(documentationProviderCliOption);
      CliOption annotationLibraryCliOption = new CliOption(ANNOTATION_LIBRARY, "Select the complementary documentation annotation library.").defaultValue(defaultDocumentationProvider().getPreferredAnnotationLibrary().toCliOptValue());
      supportedAnnotationLibraries().forEach((al) -> annotationLibraryCliOption.addEnum(al.toCliOptValue(), al.getDescription()));
      cliOptions.add(annotationLibraryCliOption);
    }
  }

  @Override public void processOpts() {
    super.processOpts();
    if (null != defaultDocumentationProvider()) {
      documentationProvider = DocumentationProvider.ofCliOption((String) additionalProperties.getOrDefault(DOCUMENTATION_PROVIDER, defaultDocumentationProvider().toCliOptValue()));
      if (!supportedDocumentationProvider().contains(documentationProvider)) {
        String msg = String.format(Locale.ROOT, "The [%s] Documentation Provider is not supported by this generator", documentationProvider.toCliOptValue());
        throw new IllegalArgumentException(msg);
      }
      annotationLibrary = AnnotationLibrary.ofCliOption((String) additionalProperties.getOrDefault(ANNOTATION_LIBRARY, documentationProvider.getPreferredAnnotationLibrary().toCliOptValue()));
      if (!supportedAnnotationLibraries().contains(annotationLibrary)) {
        String msg = String.format(Locale.ROOT, "The Annotation Library [%s] is not supported by this generator", annotationLibrary.toCliOptValue());
        throw new IllegalArgumentException(msg);
      }
      if (!documentationProvider.supportedAnnotationLibraries().contains(annotationLibrary)) {
        String msg = String.format(Locale.ROOT, "The [%s] documentation provider does not support [%s] as complementary annotation library", documentationProvider.toCliOptValue(), annotationLibrary.toCliOptValue());
        throw new IllegalArgumentException(msg);
      }
      additionalProperties.put(DOCUMENTATION_PROVIDER, documentationProvider.toCliOptValue());
      additionalProperties.put(documentationProvider.getPropertyName(), true);
      additionalProperties.put(ANNOTATION_LIBRARY, annotationLibrary.toCliOptValue());
      additionalProperties.put(annotationLibrary.getPropertyName(), true);
    } else {
      additionalProperties.put(DOCUMENTATION_PROVIDER, DocumentationProvider.NONE);
      additionalProperties.put(ANNOTATION_LIBRARY, AnnotationLibrary.NONE);
    }
    if (StringUtils.isEmpty(System.getenv("JAVA_POST_PROCESS_FILE"))) {
      LOGGER.info("Environment variable JAVA_POST_PROCESS_FILE not defined so the Java code may not be properly formatted. To define it, try \'export JAVA_POST_PROCESS_FILE=\"/usr/local/bin/clang-format -i\"\' (Linux/Mac)");
      LOGGER.info("NOTE: To enable file post-processing, \'enablePostProcessFile\' must be set to `true` (--enable-post-process-file for CLI).");
    }
    if (additionalProperties.containsKey(SUPPORT_JAVA6)) {
      this.setSupportJava6(Boolean.parseBoolean(additionalProperties.get(SUPPORT_JAVA6).toString()));
    }
    additionalProperties.put(SUPPORT_JAVA6, supportJava6);
    if (additionalProperties.containsKey(DISABLE_HTML_ESCAPING)) {
      this.setDisableHtmlEscaping(Boolean.parseBoolean(additionalProperties.get(DISABLE_HTML_ESCAPING).toString()));
    }
    additionalProperties.put(DISABLE_HTML_ESCAPING, disableHtmlEscaping);
    if (additionalProperties.containsKey(BOOLEAN_GETTER_PREFIX)) {
      this.setBooleanGetterPrefix(additionalProperties.get(BOOLEAN_GETTER_PREFIX).toString());
    }
    additionalProperties.put(BOOLEAN_GETTER_PREFIX, booleanGetterPrefix);
    if (additionalProperties.containsKey(IGNORE_ANYOF_IN_ENUM)) {
      this.setIgnoreAnyOfInEnum(Boolean.parseBoolean(additionalProperties.get(IGNORE_ANYOF_IN_ENUM).toString()));
    }
    additionalProperties.put(IGNORE_ANYOF_IN_ENUM, ignoreAnyOfInEnum);
    if (additionalProperties.containsKey(ADDITIONAL_MODEL_TYPE_ANNOTATIONS)) {
      String additionalAnnotationsList = additionalProperties.get(ADDITIONAL_MODEL_TYPE_ANNOTATIONS).toString();
      this.setAdditionalModelTypeAnnotations(Arrays.asList(additionalAnnotationsList.trim().split("\\s*(;|\\r?\\n)\\s*")));
    }
    if (additionalProperties.containsKey(ADDITIONAL_ENUM_TYPE_ANNOTATIONS)) {
      String additionalAnnotationsList = additionalProperties.get(ADDITIONAL_ENUM_TYPE_ANNOTATIONS).toString();
      this.setAdditionalEnumTypeAnnotations(Arrays.asList(additionalAnnotationsList.split(";")));
    }
    if (additionalProperties.containsKey(CodegenConstants.INVOKER_PACKAGE)) {
      this.setInvokerPackage((String) additionalProperties.get(CodegenConstants.INVOKER_PACKAGE));
    } else {
      if (additionalProperties.containsKey(CodegenConstants.API_PACKAGE)) {
        String derivedInvokerPackage = deriveInvokerPackageName((String) additionalProperties.get(CodegenConstants.API_PACKAGE));
        this.additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, derivedInvokerPackage);
        this.setInvokerPackage((String) additionalProperties.get(CodegenConstants.INVOKER_PACKAGE));
        LOGGER.info("Invoker Package Name, originally not set, is now derived from api package name: {}", derivedInvokerPackage);
      } else {
        if (additionalProperties.containsKey(CodegenConstants.MODEL_PACKAGE)) {
          String derivedInvokerPackage = deriveInvokerPackageName((String) additionalProperties.get(CodegenConstants.MODEL_PACKAGE));
          this.additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, derivedInvokerPackage);
          this.setInvokerPackage((String) additionalProperties.get(CodegenConstants.INVOKER_PACKAGE));
          LOGGER.info("Invoker Package Name, originally not set, is now derived from model package name: {}", derivedInvokerPackage);
        } else {
          additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        }
      }
    }
    if (!additionalProperties.containsKey(CodegenConstants.MODEL_PACKAGE)) {
      additionalProperties.put(CodegenConstants.MODEL_PACKAGE, modelPackage);
    }
    if (!additionalProperties.containsKey(CodegenConstants.API_PACKAGE)) {
      additionalProperties.put(CodegenConstants.API_PACKAGE, apiPackage);
    }
    if (additionalProperties.containsKey(CodegenConstants.GROUP_ID)) {
      this.setGroupId((String) additionalProperties.get(CodegenConstants.GROUP_ID));
    } else {
      additionalProperties.put(CodegenConstants.GROUP_ID, groupId);
    }
    if (additionalProperties.containsKey(CodegenConstants.ARTIFACT_ID)) {
      this.setArtifactId((String) additionalProperties.get(CodegenConstants.ARTIFACT_ID));
    } else {
      additionalProperties.put(CodegenConstants.ARTIFACT_ID, artifactId);
    }
    if (additionalProperties.containsKey(CodegenConstants.ARTIFACT_URL)) {
      this.setArtifactUrl((String) additionalProperties.get(CodegenConstants.ARTIFACT_URL));
    } else {
      additionalProperties.put(CodegenConstants.ARTIFACT_URL, artifactUrl);
    }
    if (additionalProperties.containsKey(CodegenConstants.ARTIFACT_DESCRIPTION)) {
      this.setArtifactDescription((String) additionalProperties.get(CodegenConstants.ARTIFACT_DESCRIPTION));
    } else {
      additionalProperties.put(CodegenConstants.ARTIFACT_DESCRIPTION, artifactDescription);
    }
    if (additionalProperties.containsKey(CodegenConstants.SCM_CONNECTION)) {
      this.setScmConnection((String) additionalProperties.get(CodegenConstants.SCM_CONNECTION));
    } else {
      additionalProperties.put(CodegenConstants.SCM_CONNECTION, scmConnection);
    }
    if (additionalProperties.containsKey(CodegenConstants.SCM_DEVELOPER_CONNECTION)) {
      this.setScmDeveloperConnection((String) additionalProperties.get(CodegenConstants.SCM_DEVELOPER_CONNECTION));
    } else {
      additionalProperties.put(CodegenConstants.SCM_DEVELOPER_CONNECTION, scmDeveloperConnection);
    }
    if (additionalProperties.containsKey(CodegenConstants.SCM_URL)) {
      this.setScmUrl((String) additionalProperties.get(CodegenConstants.SCM_URL));
    } else {
      additionalProperties.put(CodegenConstants.SCM_URL, scmUrl);
    }
    if (additionalProperties.containsKey(CodegenConstants.DEVELOPER_NAME)) {
      this.setDeveloperName((String) additionalProperties.get(CodegenConstants.DEVELOPER_NAME));
    } else {
      additionalProperties.put(CodegenConstants.DEVELOPER_NAME, developerName);
    }
    if (additionalProperties.containsKey(CodegenConstants.DEVELOPER_EMAIL)) {
      this.setDeveloperEmail((String) additionalProperties.get(CodegenConstants.DEVELOPER_EMAIL));
    } else {
      additionalProperties.put(CodegenConstants.DEVELOPER_EMAIL, developerEmail);
    }
    if (additionalProperties.containsKey(CodegenConstants.DEVELOPER_ORGANIZATION)) {
      this.setDeveloperOrganization((String) additionalProperties.get(CodegenConstants.DEVELOPER_ORGANIZATION));
    } else {
      additionalProperties.put(CodegenConstants.DEVELOPER_ORGANIZATION, developerOrganization);
    }
    if (additionalProperties.containsKey(CodegenConstants.DEVELOPER_ORGANIZATION_URL)) {
      this.setDeveloperOrganizationUrl((String) additionalProperties.get(CodegenConstants.DEVELOPER_ORGANIZATION_URL));
    } else {
      additionalProperties.put(CodegenConstants.DEVELOPER_ORGANIZATION_URL, developerOrganizationUrl);
    }
    if (additionalProperties.containsKey(CodegenConstants.LICENSE_NAME)) {
      this.setLicenseName((String) additionalProperties.get(CodegenConstants.LICENSE_NAME));
    } else {
      additionalProperties.put(CodegenConstants.LICENSE_NAME, licenseName);
    }
    if (additionalProperties.containsKey(CodegenConstants.LICENSE_URL)) {
      this.setLicenseUrl((String) additionalProperties.get(CodegenConstants.LICENSE_URL));
    } else {
      additionalProperties.put(CodegenConstants.LICENSE_URL, licenseUrl);
    }
    if (additionalProperties.containsKey(CodegenConstants.SOURCE_FOLDER)) {
      this.setSourceFolder((String) additionalProperties.get(CodegenConstants.SOURCE_FOLDER));
    }
    additionalProperties.put(CodegenConstants.SOURCE_FOLDER, sourceFolder);
    if (additionalProperties.containsKey(CodegenConstants.SERIALIZABLE_MODEL)) {
      this.setSerializableModel(Boolean.valueOf(additionalProperties.get(CodegenConstants.SERIALIZABLE_MODEL).toString()));
    }
    if (additionalProperties.containsKey(CodegenConstants.LIBRARY)) {
      this.setLibrary((String) additionalProperties.get(CodegenConstants.LIBRARY));
    }
    if (additionalProperties.containsKey(CodegenConstants.SERIALIZE_BIG_DECIMAL_AS_STRING)) {
      this.setSerializeBigDecimalAsString(Boolean.parseBoolean(additionalProperties.get(CodegenConstants.SERIALIZE_BIG_DECIMAL_AS_STRING).toString()));
    }
    additionalProperties.put(CodegenConstants.SERIALIZABLE_MODEL, serializableModel);
    if (additionalProperties.containsKey(FULL_JAVA_UTIL)) {
      this.setFullJavaUtil(Boolean.parseBoolean(additionalProperties.get(FULL_JAVA_UTIL).toString()));
    }
    if (additionalProperties.containsKey(DISCRIMINATOR_CASE_SENSITIVE)) {
      this.setDiscriminatorCaseSensitive(Boolean.parseBoolean(additionalProperties.get(DISCRIMINATOR_CASE_SENSITIVE).toString()));
    } else {
      this.setDiscriminatorCaseSensitive(Boolean.TRUE);
    }
    additionalProperties.put(DISCRIMINATOR_CASE_SENSITIVE, this.discriminatorCaseSensitive);
    if (fullJavaUtil) {
      javaUtilPrefix = "java.util.";
    }
    additionalProperties.put(FULL_JAVA_UTIL, fullJavaUtil);
    additionalProperties.put("javaUtilPrefix", javaUtilPrefix);
    if (additionalProperties.containsKey(WITH_XML)) {
      this.setWithXml(Boolean.parseBoolean(additionalProperties.get(WITH_XML).toString()));
    }
    additionalProperties.put(WITH_XML, withXml);
    if (additionalProperties.containsKey(OPENAPI_NULLABLE)) {
      this.setOpenApiNullable(Boolean.parseBoolean(additionalProperties.get(OPENAPI_NULLABLE).toString()));
    }
    additionalProperties.put(OPENAPI_NULLABLE, openApiNullable);
    if (additionalProperties.containsKey(CodegenConstants.PARENT_GROUP_ID)) {
      this.setParentGroupId((String) additionalProperties.get(CodegenConstants.PARENT_GROUP_ID));
    }
    if (additionalProperties.containsKey(CodegenConstants.PARENT_ARTIFACT_ID)) {
      this.setParentArtifactId((String) additionalProperties.get(CodegenConstants.PARENT_ARTIFACT_ID));
    }
    if (additionalProperties.containsKey(CodegenConstants.PARENT_VERSION)) {
      this.setParentVersion((String) additionalProperties.get(CodegenConstants.PARENT_VERSION));
    }
    if (additionalProperties.containsKey(IMPLICIT_HEADERS)) {
      this.setImplicitHeaders(Boolean.parseBoolean(additionalProperties.get(IMPLICIT_HEADERS).toString()));
    }
    if (additionalProperties.containsKey(IMPLICIT_HEADERS_REGEX)) {
      this.setImplicitHeadersRegex(additionalProperties.get(IMPLICIT_HEADERS_REGEX).toString());
    }
    if (!StringUtils.isEmpty(parentGroupId) && !StringUtils.isEmpty(parentArtifactId) && !StringUtils.isEmpty(parentVersion)) {
      additionalProperties.put("parentOverridden", true);
    }
    additionalProperties.put("apiDocPath", apiDocPath);
    additionalProperties.put("modelDocPath", modelDocPath);
    importMapping.put("List", "java.util.List");
    importMapping.put("Set", "java.util.Set");
    if (fullJavaUtil) {
      typeMapping.put("array", "java.util.List");
      typeMapping.put("set", "java.util.Set");
      typeMapping.put("map", "java.util.Map");
      typeMapping.put("DateTime", "java.util.Date");
      typeMapping.put("UUID", "java.util.UUID");
      typeMapping.remove("List");
      importMapping.remove("Date");
      importMapping.remove("Map");
      importMapping.remove("HashMap");
      importMapping.remove("Array");
      importMapping.remove("ArrayList");
      importMapping.remove("List");
      importMapping.remove("Set");
      importMapping.remove("DateTime");
      importMapping.remove("UUID");
      instantiationTypes.put("array", "java.util.ArrayList");
      instantiationTypes.put("set", "java.util.LinkedHashSet");
      instantiationTypes.put("map", "java.util.HashMap");
    }
    this.sanitizeConfig();
    importMapping.put("ToStringSerializer", "com.fasterxml.jackson.databind.ser.std.ToStringSerializer");
    importMapping.put("JsonSerialize", "com.fasterxml.jackson.databind.annotation.JsonSerialize");
    importMapping.put("JsonDeserialize", "com.fasterxml.jackson.databind.annotation.JsonDeserialize");
    importMapping.put("ApiModelProperty", "io.swagger.annotations.ApiModelProperty");
    importMapping.put("ApiModel", "io.swagger.annotations.ApiModel");
    importMapping.put("BigDecimal", "java.math.BigDecimal");
    importMapping.put("JsonProperty", "com.fasterxml.jackson.annotation.JsonProperty");
    importMapping.put("JsonSubTypes", "com.fasterxml.jackson.annotation.JsonSubTypes");
    importMapping.put("JsonTypeInfo", "com.fasterxml.jackson.annotation.JsonTypeInfo");
    importMapping.put("JsonTypeName", "com.fasterxml.jackson.annotation.JsonTypeName");
    importMapping.put("JsonCreator", "com.fasterxml.jackson.annotation.JsonCreator");
    importMapping.put("JsonValue", "com.fasterxml.jackson.annotation.JsonValue");
    importMapping.put("JsonIgnore", "com.fasterxml.jackson.annotation.JsonIgnore");
    importMapping.put("JsonIgnoreProperties", "com.fasterxml.jackson.annotation.JsonIgnoreProperties");
    importMapping.put("JsonInclude", "com.fasterxml.jackson.annotation.JsonInclude");
    importMapping.put("JsonNullable", "org.openapitools.jackson.nullable.JsonNullable");
    importMapping.put("SerializedName", "com.google.gson.annotations.SerializedName");
    importMapping.put("TypeAdapter", "com.google.gson.TypeAdapter");
    importMapping.put("JsonAdapter", "com.google.gson.annotations.JsonAdapter");
    importMapping.put("JsonReader", "com.google.gson.stream.JsonReader");
    importMapping.put("JsonWriter", "com.google.gson.stream.JsonWriter");
    importMapping.put("IOException", "java.io.IOException");
    importMapping.put("Arrays", "java.util.Arrays");
    importMapping.put("Objects", "java.util.Objects");
    importMapping.put("StringUtil", invokerPackage + ".StringUtil");
    importMapping.put("DependencyUtil", invokerPackage + ".DependencyUtil");
    importMapping.put("com.fasterxml.jackson.annotation.JsonProperty", "com.fasterxml.jackson.annotation.JsonCreator");
    if (additionalProperties.containsKey(SUPPORT_ASYNC)) {
      setSupportAsync(Boolean.parseBoolean(additionalProperties.get(SUPPORT_ASYNC).toString()));
      if (supportAsync) {
        additionalProperties.put(SUPPORT_ASYNC, "true");
      }
    }
    if (additionalProperties.containsKey(DATE_LIBRARY)) {
      setDateLibrary(additionalProperties.get("dateLibrary").toString());
    }
    if ("joda".equals(dateLibrary)) {
      additionalProperties.put("joda", "true");
      typeMapping.put("date", "LocalDate");
      typeMapping.put("DateTime", "DateTime");
      importMapping.put("LocalDate", "org.joda.time.LocalDate");
      importMapping.put("DateTime", "org.joda.time.DateTime");
    } else {
      if (dateLibrary.startsWith("java8")) {
        additionalProperties.put("java8", "true");
        additionalProperties.put("jsr310", "true");
        typeMapping.put("date", "LocalDate");
        importMapping.put("LocalDate", "java.time.LocalDate");
        if ("java8-localdatetime".equals(dateLibrary)) {
          typeMapping.put("DateTime", "LocalDateTime");
          importMapping.put("LocalDateTime", "java.time.LocalDateTime");
        } else {
          typeMapping.put("DateTime", "OffsetDateTime");
          importMapping.put("OffsetDateTime", "java.time.OffsetDateTime");
        }
      } else {
        if (dateLibrary.equals("legacy")) {
          additionalProperties.put("legacyDates", "true");
        } else {
          if (dateLibrary.equals("legacy")) {
            additionalProperties.put("legacyDates", "true");
          }
        }
      }
    }
    if (additionalProperties.containsKey(TEST_OUTPUT)) {
      setOutputTestFolder(additionalProperties.get(TEST_OUTPUT).toString());
    }
  }

  private void sanitizeConfig() {
    this.setApiPackage(sanitizePackageName(apiPackage));
    if (additionalProperties.containsKey(CodegenConstants.API_PACKAGE)) {
      this.additionalProperties.put(CodegenConstants.API_PACKAGE, apiPackage);
    }
    this.setModelPackage(sanitizePackageName(modelPackage));
    if (additionalProperties.containsKey(CodegenConstants.MODEL_PACKAGE)) {
      this.additionalProperties.put(CodegenConstants.MODEL_PACKAGE, modelPackage);
    }
    this.setInvokerPackage(sanitizePackageName(invokerPackage));
    if (additionalProperties.containsKey(CodegenConstants.INVOKER_PACKAGE)) {
      this.additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
    }
  }

  @Override public String escapeReservedWord(String name) {
    if (this.reservedWordsMappings().containsKey(name)) {
      return this.reservedWordsMappings().get(name);
    }
    return "_" + name;
  }

  @Override public String apiFileFolder() {
    return (outputFolder + File.separator + sourceFolder + File.separator + apiPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
  }

  @Override public String apiTestFileFolder() {
    return (outputTestFolder + File.separator + testFolder + File.separator + apiPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
  }

  @Override public String modelTestFileFolder() {
    return (outputTestFolder + File.separator + testFolder + File.separator + modelPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
  }

  @Override public String modelFileFolder() {
    return (outputFolder + File.separator + sourceFolder + File.separator + modelPackage().replace('.', File.separatorChar)).replace('/', File.separatorChar);
  }

  @Override public String apiDocFileFolder() {
    return (outputFolder + File.separator + apiDocPath).replace('/', File.separatorChar);
  }

  @Override public String modelDocFileFolder() {
    return (outputFolder + File.separator + modelDocPath).replace('/', File.separatorChar);
  }

  @Override public String toApiDocFilename(String name) {
    return toApiName(name);
  }

  @Override public String toModelDocFilename(String name) {
    return toModelName(name);
  }

  @Override public String toApiTestFilename(String name) {
    return toApiName(name) + "Test";
  }

  @Override public String toModelTestFilename(String name) {
    return toModelName(name) + "Test";
  }

  @Override public String toApiFilename(String name) {
    return toApiName(name);
  }

  @Override public String toVarName(String name) {
    name = sanitizeName(name, "\\W-[\\$]");
    if (name.toLowerCase(Locale.ROOT).matches("^_*class$")) {
      return "propertyClass";
    }
    if ("_".equals(name)) {
      name = "_u";
    }
    if (name.matches("^\\d.*")) {
      name = "_" + name;
    }
    if (name.matches("^[A-Z0-9_]*$")) {
      return name;
    }
    if (startsWithTwoUppercaseLetters(name)) {
      name = name.substring(0, 2).toLowerCase(Locale.ROOT) + name.substring(2);
    }
    if ((((CharSequence) name).chars().anyMatch((character) -> specialCharReplacements.containsKey(String.valueOf((char) character))))) {
      List<String> allowedCharacters = new ArrayList<>();
      allowedCharacters.add("_");
      allowedCharacters.add("$");
      name = escape(name, specialCharReplacements, allowedCharacters, "_");
    }
    name = camelize(name, true);
    if (isReservedWord(name) || name.matches("^\\d.*")) {
      name = escapeReservedWord(name);
    }
    return name;
  }

  private boolean startsWithTwoUppercaseLetters(String name) {
    boolean startsWithTwoUppercaseLetters = false;
    if (name.length() > 1) {
      startsWithTwoUppercaseLetters = name.substring(0, 2).equals(name.substring(0, 2).toUpperCase(Locale.ROOT));
    }
    return startsWithTwoUppercaseLetters;
  }

  @Override public String toParamName(String name) {
    if ("callback".equals(name)) {
      return "paramCallback";
    }
    return toVarName(name);
  }

  @Override public String toModelName(final String name) {
    if (schemaMapping.containsKey(name)) {
      return schemaMapping.get(name);
    }
    String origName = name;
    if (schemaKeyToModelNameCache.containsKey(origName)) {
      return schemaKeyToModelNameCache.get(origName);
    }
    final String sanitizedName = sanitizeName(name);
    String nameWithPrefixSuffix = sanitizedName;
    if (!StringUtils.isEmpty(modelNamePrefix)) {
      nameWithPrefixSuffix = modelNamePrefix + "_" + nameWithPrefixSuffix;
    }
    if (!StringUtils.isEmpty(modelNameSuffix)) {
      nameWithPrefixSuffix = nameWithPrefixSuffix + "_" + modelNameSuffix;
    }
    final String camelizedName = camelize(nameWithPrefixSuffix);
    if (isReservedWord(camelizedName)) {
      final String modelName = "Model" + camelizedName;
      schemaKeyToModelNameCache.put(origName, modelName);
      LOGGER.warn("{} (reserved word) cannot be used as model name. Renamed to {}", camelizedName, modelName);
      return modelName;
    }
    if (camelizedName.matches("^\\d.*")) {
      final String modelName = "Model" + camelizedName;
      schemaKeyToModelNameCache.put(origName, modelName);
      LOGGER.warn("{} (model name starts with number) cannot be used as model name. Renamed to {}", name, modelName);
      return modelName;
    }
    schemaKeyToModelNameCache.put(origName, camelizedName);
    return camelizedName;
  }

  @Override public String toModelFilename(String name) {
    return toModelName(name);
  }

  @Override public String getTypeDeclaration(Schema p) {
    Schema<?> schema = unaliasSchema(p);
    Schema<?> target = ModelUtils.isGenerateAliasAsModel() ? p : schema;
    if (ModelUtils.isArraySchema(target)) {
      Schema<?> items = getSchemaItems((ArraySchema) schema);
      return getSchemaType(target) + "<" + getTypeDeclaration(items) + ">";
    } else {
      if (ModelUtils.isMapSchema(target)) {
        Schema<?> inner = getAdditionalProperties(target);
        if (inner == null) {
          LOGGER.error("`{}` (map property) does not have a proper inner type defined. Default to type:string", p.getName());
          inner = new StringSchema().description("TODO default missing map inner type to string");
          p.setAdditionalProperties(inner);
        }
        return getSchemaType(target) + "<String, " + getTypeDeclaration(inner) + ">";
      }
    }
    return super.getTypeDeclaration(target);
  }

  @Override public String getAlias(String name) {
    if (typeAliases != null && typeAliases.containsKey(name)) {
      return typeAliases.get(name);
    }
    return name;
  }

  @Override public String toDefaultValue(Schema schema) {
    schema = ModelUtils.getReferencedSchema(this.openAPI, schema);
    if (ModelUtils.isArraySchema(schema)) {
      final String pattern;
      if (ModelUtils.isSet(schema)) {
        String mapInstantiationType = instantiationTypes().getOrDefault("set", "LinkedHashSet");
        pattern = "new " + mapInstantiationType + "<%s>()";
      } else {
        String arrInstantiationType = instantiationTypes().getOrDefault("array", "ArrayList");
        pattern = "new " + arrInstantiationType + "<%s>()";
      }
      return String.format(Locale.ROOT, pattern, "");
    } else {
      if (ModelUtils.isMapSchema(schema) && !(schema instanceof ComposedSchema)) {
        if (schema.getProperties() != null && schema.getProperties().size() > 0) {
          if (schema.getDefault() != null) {
            return super.toDefaultValue(schema);
          }
          return null;
        }
        String mapInstantiationType = instantiationTypes().getOrDefault("map", "HashMap");
        final String pattern = "new " + mapInstantiationType + "<%s>()";
        if (getAdditionalProperties(schema) == null) {
          return null;
        }
        return String.format(Locale.ROOT, pattern, "");
      } else {
        if (ModelUtils.isIntegerSchema(schema)) {
          if (schema.getDefault() != null) {
            if (SchemaTypeUtil.INTEGER64_FORMAT.equals(schema.getFormat())) {
              return schema.getDefault().toString() + "l";
            } else {
              return schema.getDefault().toString();
            }
          }
          return null;
        } else {
          if (ModelUtils.isNumberSchema(schema)) {
            if (schema.getDefault() != null) {
              if (SchemaTypeUtil.FLOAT_FORMAT.equals(schema.getFormat())) {
                return schema.getDefault().toString() + "f";
              } else {
                if (SchemaTypeUtil.DOUBLE_FORMAT.equals(schema.getFormat())) {
                  return schema.getDefault().toString() + "d";
                } else {
                  return "new BigDecimal(\"" + schema.getDefault().toString() + "\")";
                }
              }
            }
            return null;
          } else {
            if (ModelUtils.isBooleanSchema(schema)) {
              if (schema.getDefault() != null) {
                return schema.getDefault().toString();
              }
              return null;
            } else {
              if (ModelUtils.isURISchema(schema)) {
                if (schema.getDefault() != null) {
                  return "URI.create(\"" + escapeText((String) schema.getDefault()) + "\")";
                }
                return null;
              } else {
                if (ModelUtils.isStringSchema(schema)) {
                  if (schema.getDefault() != null) {
                    String _default;
                    if (schema.getDefault() instanceof Date) {
                      if ("java8".equals(getDateLibrary())) {
                        Date date = (Date) schema.getDefault();
                        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return String.format(Locale.ROOT, "LocalDate.parse(\"%s\")", localDate.toString());
                      } else {
                        return null;
                      }
                    } else {
                      if (schema.getDefault() instanceof java.time.OffsetDateTime) {
                        if ("java8".equals(getDateLibrary())) {
                          return String.format(Locale.ROOT, "OffsetDateTime.parse(\"%s\", %s)", ((java.time.OffsetDateTime) schema.getDefault()).atZoneSameInstant(ZoneId.systemDefault()), "java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(java.time.ZoneId.systemDefault())");
                        } else {
                          return null;
                        }
                      } else {
                        _default = (String) schema.getDefault();
                      }
                    }
                    if (schema.getEnum() == null) {
                      return "\"" + escapeText(_default) + "\"";
                    } else {
                      return _default;
                    }
                  }
                  return null;
                } else {
                  if (ModelUtils.isObjectSchema(schema)) {
                    if (schema.getDefault() != null) {
                      return super.toDefaultValue(schema);
                    }
                    return null;
                  } else {
                    if (ModelUtils.isComposedSchema(schema)) {
                      if (schema.getDefault() != null) {
                        return super.toDefaultValue(schema);
                      }
                      return null;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return super.toDefaultValue(schema);
  }

  @Override public String toDefaultParameterValue(final Schema<?> schema) {
    Object defaultValue = schema.get$ref() != null ? ModelUtils.getReferencedSchema(openAPI, schema).getDefault() : schema.getDefault();
    if (defaultValue == null) {
      return null;
    }
    if (defaultValue instanceof Date) {
      Date date = (Date) schema.getDefault();
      LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      return localDate.toString();
    }
    if (ModelUtils.isArraySchema(schema)) {
      if (defaultValue instanceof ArrayNode) {
        ArrayNode array = (ArrayNode) defaultValue;
        return StreamSupport.stream(array.spliterator(), false).map(JsonNode::toString).map((item) -> StringUtils.removeStart(item, "\"")).map((item) -> StringUtils.removeEnd(item, "\"")).collect(Collectors.joining(","));
      }
    }
    return defaultValue.toString().replace("\"", "\\\"");
  }

  @Override public void setParameterExampleValue(CodegenParameter codegenParameter, Parameter parameter) {
    if (parameter.getExample() != null) {
      codegenParameter.example = parameter.getExample().toString();
    }
    if (parameter.getExamples() != null && !parameter.getExamples().isEmpty()) {
      Example example = parameter.getExamples().values().iterator().next();
      if (example.getValue() != null) {
        codegenParameter.example = example.getValue().toString();
      }
    }
    Schema schema = parameter.getSchema();
    if (schema != null && schema.getExample() != null) {
      codegenParameter.example = schema.getExample().toString();
    }
    setParameterExampleValue(codegenParameter);
  }

  @Override public void setParameterExampleValue(CodegenParameter codegenParameter, RequestBody requestBody) {
    boolean isModel = (codegenParameter.isModel || (codegenParameter.isContainer && codegenParameter.getItems().isModel));
    Content content = requestBody.getContent();
    if (content.size() > 1) {
      LOGGER.warn("Multiple MediaTypes found, using only the first one");
    }
    MediaType mediaType = content.values().iterator().next();
    if (mediaType.getExample() != null) {
      if (isModel) {
        LOGGER.warn("Ignoring complex example on request body");
      } else {
        codegenParameter.example = mediaType.getExample().toString();
        return;
      }
    }
    if (mediaType.getExamples() != null && !mediaType.getExamples().isEmpty()) {
      Example example = mediaType.getExamples().values().iterator().next();
      if (example.getValue() != null) {
        if (isModel) {
          LOGGER.warn("Ignoring complex example on request body");
        } else {
          codegenParameter.example = example.getValue().toString();
          return;
        }
      }
    }
    setParameterExampleValue(codegenParameter);
  }

  @Override public void setParameterExampleValue(CodegenParameter p) {
    String example;
    boolean hasAllowableValues = p.allowableValues != null && !p.allowableValues.isEmpty();
    if (hasAllowableValues) {
      final List<Object> values = (List<Object>) p.allowableValues.get("values");
      example = String.valueOf(values.get(0));
    } else {
      if (p.defaultValue == null) {
        example = p.example;
      } else {
        example = p.defaultValue;
      }
    }
    String type = p.baseType;
    if (type == null) {
      type = p.dataType;
    }
    if ("String".equals(type)) {
      if (example == null) {
        example = p.paramName + "_example";
      }
      example = "\"" + escapeText(example) + "\"";
    } else {
      if ("Integer".equals(type) || "Short".equals(type)) {
        if (example == null) {
          example = "56";
        }
      } else {
        if ("Long".equals(type)) {
          if (example == null) {
            example = "56";
          }
          example = StringUtils.appendIfMissingIgnoreCase(example, "L");
        } else {
          if ("Float".equals(type)) {
            if (example == null) {
              example = "3.4";
            }
            example = StringUtils.appendIfMissingIgnoreCase(example, "F");
          } else {
            if ("Double".equals(type)) {
              if (example == null) {
                example = "3.4";
              }
              example = StringUtils.appendIfMissingIgnoreCase(example, "D");
            } else {
              if ("Boolean".equals(type)) {
                if (example == null) {
                  example = "true";
                }
              } else {
                if ("File".equals(type)) {
                  if (example == null) {
                    example = "/path/to/file";
                  }
                  example = "new File(\"" + escapeText(example) + "\")";
                } else {
                  if ("Date".equals(type)) {
                    example = "new Date()";
                  } else {
                    if ("LocalDate".equals(type)) {
                      if (example == null) {
                        example = "LocalDate.now()";
                      } else {
                        example = "LocalDate.parse(\"" + example + "\")";
                      }
                    } else {
                      if ("OffsetDateTime".equals(type)) {
                        if (example == null) {
                          example = "OffsetDateTime.now()";
                        } else {
                          example = "OffsetDateTime.parse(\"" + example + "\")";
                        }
                      } else {
                        if ("BigDecimal".equals(type)) {
                          if (example == null) {
                            example = "new BigDecimal(78)";
                          } else {
                            example = "new BigDecimal(\"" + example + "\")";
                          }
                        } else {
                          if ("UUID".equals(type)) {
                            if (example == null) {
                              example = "UUID.randomUUID()";
                            } else {
                              example = "UUID.fromString(\"" + example + "\")";
                            }
                          } else {
                            if (hasAllowableValues) {
                              example = type + ".fromValue(\"" + example + "\")";
                            } else {
                              if (!languageSpecificPrimitives.contains(type)) {
                                example = "new " + type + "()";
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
    if (example == null) {
      example = "null";
    } else {
      if (Boolean.TRUE.equals(p.isArray)) {
        if (p.items.defaultValue != null) {
          String innerExample;
          if ("String".equals(p.items.dataType)) {
            innerExample = "\"" + p.items.defaultValue + "\"";
          } else {
            innerExample = p.items.defaultValue;
          }
          example = "Arrays.asList(" + innerExample + ")";
        } else {
          example = "Arrays.asList()";
        }
      } else {
        if (Boolean.TRUE.equals(p.isMap)) {
          example = "new HashMap()";
        }
      }
    }
    p.example = example;
  }

  @Override public String toExampleValue(Schema p) {
    if (p.getExample() != null) {
      return escapeText(p.getExample().toString());
    } else {
      return null;
    }
  }

  @Override public String getSchemaType(Schema p) {
    String openAPIType = super.getSchemaType(p);
    if (typeMapping.containsKey(openAPIType)) {
      return typeMapping.get(openAPIType);
    }
    if (null == openAPIType) {
      LOGGER.error("No Type defined for Schema {}", p);
    }
    return toModelName(openAPIType);
  }

  @Override public String toOperationId(String operationId) {
    if (StringUtils.isEmpty(operationId)) {
      throw new RuntimeException("Empty method/operation name (operationId) not allowed");
    }
    operationId = camelize(sanitizeName(operationId), true);
    if (isReservedWord(operationId)) {
      String newOperationId = camelize("call_" + operationId, true);
      LOGGER.warn("{} (reserved word) cannot be used as method name. Renamed to {}", operationId, newOperationId);
      return newOperationId;
    }
    if (operationId.matches("^\\d.*")) {
      LOGGER.warn(operationId + " (starting with a number) cannot be used as method name. Renamed to " + camelize("call_" + operationId), true);
      operationId = camelize("call_" + operationId, true);
    }
    return operationId;
  }

  @Override public CodegenModel fromModel(String name, Schema model) {
    Map<String, Schema> allDefinitions = ModelUtils.getSchemas(this.openAPI);
    CodegenModel codegenModel = super.fromModel(name, model);
    if (codegenModel.description != null) {
      codegenModel.imports.add("ApiModel");
    }
    if (codegenModel.discriminator != null && additionalProperties.containsKey(JACKSON)) {
      codegenModel.imports.add("JsonSubTypes");
      codegenModel.imports.add("JsonTypeInfo");
      codegenModel.imports.add("JsonIgnoreProperties");
    }
    if (allDefinitions != null && codegenModel.parentSchema != null && codegenModel.hasEnums) {
      final Schema parentModel = allDefinitions.get(codegenModel.parentSchema);
      final CodegenModel parentCodegenModel = super.fromModel(codegenModel.parent, parentModel);
      codegenModel = AbstractJavaCodegen.reconcileInlineEnums(codegenModel, parentCodegenModel);
    }
    if ("BigDecimal".equals(codegenModel.dataType)) {
      codegenModel.imports.add("BigDecimal");
    }
    return codegenModel;
  }

  @Override public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
    if (model.getIsClassnameSanitized() && additionalProperties.containsKey(JACKSON)) {
      model.imports.add("JsonTypeName");
    }
    if (serializeBigDecimalAsString) {
      if ("decimal".equals(property.baseType)) {
        property.vendorExtensions.put("x-extra-annotation", "@JsonSerialize(using = ToStringSerializer.class)");
        model.imports.add("ToStringSerializer");
        model.imports.add("JsonSerialize");
      }
    }
    if (!fullJavaUtil) {
      if ("array".equals(property.containerType)) {
        model.imports.add("ArrayList");
      } else {
        if ("set".equals(property.containerType)) {
          model.imports.add("LinkedHashSet");
          boolean canNotBeWrappedToNullable = !openApiNullable || !property.isNullable;
          if (canNotBeWrappedToNullable) {
            model.imports.add("JsonDeserialize");
            property.vendorExtensions.put("x-setter-extra-annotation", "@JsonDeserialize(as = LinkedHashSet.class)");
          }
        } else {
          if ("map".equals(property.containerType)) {
            model.imports.add("HashMap");
          }
        }
      }
    }
    if (!BooleanUtils.toBoolean(model.isEnum)) {
      model.imports.add("ApiModelProperty");
      model.imports.add("ApiModel");
    }
    if (openApiNullable) {
      if (Boolean.FALSE.equals(property.required) && Boolean.TRUE.equals(property.isNullable)) {
        model.imports.add("JsonNullable");
        model.getVendorExtensions().put("x-jackson-optional-nullable-helpers", true);
      }
    }
    if (property.isReadOnly) {
      model.getVendorExtensions().put("x-has-readonly-properties", true);
    }
    if (property.dataType != null && property.dataType.equals(property.name) && property.dataType.toUpperCase(Locale.ROOT).equals(property.name)) {
      property.name = property.name.toLowerCase(Locale.ROOT);
    }
  }

  @Override public void preprocessOpenAPI(OpenAPI openAPI) {
    super.preprocessOpenAPI(openAPI);
    if (openAPI == null) {
      return;
    }
    if (openAPI.getPaths() != null) {
      for (Map.Entry<String, PathItem> openAPIGetPathsEntry : openAPI.getPaths().entrySet()) {
        String pathname = openAPIGetPathsEntry.getKey();
        PathItem path = openAPIGetPathsEntry.getValue();
        if (path.readOperations() == null) {
          continue;
        }
        for (Operation operation : path.readOperations()) {
          LOGGER.info("Processing operation {}", operation.getOperationId());
          if (hasBodyParameter(openAPI, operation) || hasFormParameter(openAPI, operation)) {
            String defaultContentType = hasFormParameter(openAPI, operation) ? "application/x-www-form-urlencoded" : "application/json";
            List<String> consumes = new ArrayList<>(getConsumesInfo(openAPI, operation));
            String contentType = consumes.isEmpty() ? defaultContentType : consumes.get(0);
            operation.addExtension("x-content-type", contentType);
          }
          String accepts = getAccept(openAPI, operation);
          operation.addExtension("x-accepts", accepts);
        }
      }
    }
    if (artifactVersion == null) {
      if (additionalProperties.containsKey(CodegenConstants.ARTIFACT_VERSION) && additionalProperties.get(CodegenConstants.ARTIFACT_VERSION) != null) {
        this.setArtifactVersion((String) additionalProperties.get(CodegenConstants.ARTIFACT_VERSION));
      } else {
        if (openAPI.getInfo() != null && openAPI.getInfo().getVersion() != null) {
          this.setArtifactVersion(openAPI.getInfo().getVersion());
        } else {
          this.setArtifactVersion(ARTIFACT_VERSION_DEFAULT_VALUE);
        }
      }
    }
    additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);
    if (additionalProperties.containsKey(CodegenConstants.SNAPSHOT_VERSION)) {
      if (convertPropertyToBooleanAndWriteBack(CodegenConstants.SNAPSHOT_VERSION)) {
        this.setArtifactVersion(this.buildSnapshotVersion(this.getArtifactVersion()));
      }
    }
    additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);
    if (ignoreAnyOfInEnum) {
      Stream.concat(Stream.of(openAPI.getComponents().getSchemas()), openAPI.getComponents().getSchemas().values().stream().filter((schema) -> schema.getProperties() != null).map(Schema::getProperties)).forEach((schemas) -> schemas.replaceAll((name, s) -> Stream.of(s).filter((schema) -> schema instanceof ComposedSchema).map((schema) -> (ComposedSchema) schema).filter((schema) -> Objects.nonNull(schema.getAnyOf())).flatMap((schema) -> schema.getAnyOf().stream()).filter((schema) -> Objects.nonNull(schema.getEnum())).findFirst().orElse((Schema) s)));
    }
  }

  private static String getAccept(OpenAPI openAPI, Operation operation) {
    String accepts = null;
    String defaultContentType = "application/json";
    Set<String> producesInfo = getProducesInfo(openAPI, operation);
    if (producesInfo != null && !producesInfo.isEmpty()) {
      ArrayList<String> produces = new ArrayList<>(producesInfo);
      StringBuilder sb = new StringBuilder();
      for (String produce : produces) {
        if (defaultContentType.equalsIgnoreCase(produce)) {
          accepts = defaultContentType;
          break;
        } else {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append(produce);
        }
      }
      if (accepts == null) {
        accepts = sb.toString();
      }
    } else {
      accepts = defaultContentType;
    }
    return accepts;
  }

  @Override protected boolean needToImport(String type) {
    return super.needToImport(type) && !type.contains(".");
  }

  @Override public String toEnumName(CodegenProperty property) {
    return sanitizeName(camelize(property.name)) + "Enum";
  }

  @Override public String toEnumVarName(String value, String datatype) {
    if (value.length() == 0) {
      return "EMPTY";
    }
    if (getSymbolName(value) != null) {
      return getSymbolName(value).toUpperCase(Locale.ROOT);
    }
    if (" ".equals(value)) {
      return "SPACE";
    }
    if ("Integer".equals(datatype) || "Long".equals(datatype) || "Float".equals(datatype) || "Double".equals(datatype) || "BigDecimal".equals(datatype)) {
      String varName = "NUMBER_" + value;
      varName = varName.replaceAll("-", "MINUS_");
      varName = varName.replaceAll("\\+", "PLUS_");
      varName = varName.replaceAll("\\.", "_DOT_");
      return varName;
    }
    String var = value.replaceAll("\\W+", "_").toUpperCase(Locale.ROOT);
    if (var.matches("\\d.*")) {
      return "_" + var;
    } else {
      return var;
    }
  }

  @Override public String toEnumValue(String value, String datatype) {
    if ("Integer".equals(datatype) || "Double".equals(datatype)) {
      return value;
    } else {
      if ("Long".equals(datatype)) {
        return value + "l";
      } else {
        if ("Float".equals(datatype)) {
          return value + "f";
        } else {
          if ("BigDecimal".equals(datatype)) {
            return "new BigDecimal(\"" + value + "\")";
          } else {
            return "\"" + escapeText(value) + "\"";
          }
        }
      }
    }
  }

  @Override public CodegenOperation fromOperation(String path, String httpMethod, Operation operation, List<Server> servers) {
    CodegenOperation op = super.fromOperation(path, httpMethod, operation, servers);
    op.path = sanitizePath(op.path);
    if (operation.getExtensions() != null && operation.getExtensions().containsKey("x-dependencies")) {
      List<String> dependencies = (List<String>) operation.getExtensions().get("x-dependencies");
      List<CodegenDependency> dependencyList = new ArrayList<>();
      try {
        Injector injector = new IDLStandaloneSetupGenerated().createInjectorAndDoEMFRegistration();
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
        Resource resource = resourceSet.createResource(URI.createURI("dummy:/example.idl"));
        currentOperation = op;
        for (String dep : dependencies) {
          resource.load(new ByteArrayInputStream(dep.getBytes()), resourceSet.getLoadOptions());
          String assertion = writeDependency((Dependency) resource.getContents().get(0).eContents().get(0));
          CodegenDependency dependency = new CodegenDependency();
          dependency.idlDependency = dep;
          dependency.assertOperation = assertion;
          dependencyList.add(dependency);
          resource.unload();
        }
        op.vendorExtensions.put("x-dependencies", dependencyList);
        op.imports.add("DependencyUtil");
      } catch (IOException e) {
        LOGGER.error("Error while processing IDL dependencies for operation: " + op.operationId + ". They will not be included");
        op.vendorExtensions.remove("x-dependencies");
      } catch (IllegalArgumentException e) {
        LOGGER.error("Error while processing IDL dependencies for operation: " + op.operationId + ": " + e.getMessage());
        op.vendorExtensions.remove("x-dependencies");
      }
    }
    return op;
  }

  public String writeDependency(Dependency dep) {
    assertOperation = "!";
    if (dep.getDep() instanceof ConditionalDependencyImpl) {
      writeConditionalDependency((ConditionalDependency) dep.getDep());
    } else {
      if (dep.getDep() instanceof ArithmeticDependencyImpl) {
        writeArithmeticDependency((ArithmeticDependency) dep.getDep(), true);
      } else {
        if (dep.getDep() instanceof RelationalDependencyImpl) {
          writeRelationalDependency((RelationalDependency) dep.getDep(), true);
        } else {
          if (dep.getDep() instanceof GeneralPredefinedDependencyImpl) {
            assertOperation = "";
            writePredefinedDependency((GeneralPredefinedDependency) dep.getDep(), true);
          }
        }
      }
    }
    return assertOperation;
  }

  private String writeParamName(String paramName) {
    return getParameter(paramName).paramName;
  }

  private CodegenParameter getParameter(String paramName) {
    return currentOperation.queryParams.stream().filter((p) -> p.baseName.equals(paramName)).findFirst().orElseThrow(() -> new IllegalArgumentException("IDL parameter \"" + paramName + "\" not found in query params for operation \"" + currentOperation.operationId + "\""));
  }

  private boolean isParamValueRelation(Param param) {
    return param.getStringValues().size() != 0 || param.getPatternString() != null || param.getBooleanValue() != null || param.getDoubleValue() != null;
  }

  private void writeClause(GeneralClause clause) {
    if (clause.getPredicate() != null) {
      if (clause.getNot() != null) {
        assertOperation += "!";
      }
      assertOperation += "(";
      writePredicate(clause.getPredicate());
      assertOperation += ")";
    }
    if (clause.getFirstElement() != null) {
      if (clause.getFirstElement() instanceof RelationalDependencyImpl) {
        writeRelationalDependency((RelationalDependency) clause.getFirstElement(), false);
      } else {
        if (clause.getFirstElement() instanceof GeneralTermImpl) {
          GeneralTerm term = (GeneralTerm) clause.getFirstElement();
          Param param = (Param) term.getParam();
          CodegenParameter parameter = getParameter(param.getName());
          if (term.getNot() != null) {
            assertOperation += "!";
          }
          assertOperation += "(";
          assertOperation += parameter.paramName + " != null";
          if (parameter.isArray) {
            assertOperation += " && !" + parameter.paramName + ".isEmpty()";
          }
          if (isParamValueRelation(param)) {
            assertOperation += " && ";
            if (param.getBooleanValue() != null) {
              if (param.getBooleanValue().equals("false")) {
                assertOperation += "!";
              }
              assertOperation += parameter.paramName;
            } else {
              if (param.getDoubleValue() != null) {
                assertOperation += parameter.paramName + param.getRelationalOp() + Double.parseDouble(param.getDoubleValue());
              } else {
                if (param.getStringValues().size() != 0) {
                  assertOperation += "(";
                  if (parameter.isArray) {
                    for (String s : param.getStringValues()) {
                      assertOperation += parameter.paramName + ".contains(" + "\"" + s + "\") || ";
                    }
                  } else {
                    for (String s : param.getStringValues()) {
                      assertOperation += parameter.paramName + ".equals(" + "\"" + s + "\") || ";
                    }
                  }
                  assertOperation = assertOperation.substring(0, assertOperation.length() - 4);
                  assertOperation += ")";
                } else {
                  if (param.getPatternString() != null) {
                  }
                }
              }
            }
          }
          assertOperation += ")";
        } else {
          if (clause.getFirstElement() instanceof ArithmeticDependencyImpl) {
            writeArithmeticDependency((ArithmeticDependency) clause.getFirstElement(), false);
          } else {
            if (clause.getFirstElement() instanceof GeneralPredefinedDependencyImpl) {
              writePredefinedDependency((GeneralPredefinedDependency) clause.getFirstElement(), false);
            }
          }
        }
      }
    }
  }

  private void writePredicate(GeneralPredicate predicate) {
    writeClause(predicate.getFirstClause());
    if (predicate.getClauseContinuation() != null) {
      if (predicate.getClauseContinuation().getLogicalOp().equals("AND")) {
        assertOperation += " && ";
      } else {
        if (predicate.getClauseContinuation().getLogicalOp().equals("OR")) {
          assertOperation += " || ";
        }
      }
      writePredicate(predicate.getClauseContinuation().getAdditionalElements());
    }
  }

  private void writeConditionalDependency(ConditionalDependency dep) {
    assertOperation += "(!";
    writePredicate(dep.getCondition());
    assertOperation += " || ";
    writePredicate(dep.getConsequence());
    assertOperation += ")";
  }

  private void writeRelationalDependency(RelationalDependency dep, boolean alone) {
    if (alone) {
      assertOperation += "(!(" + writeParamName(dep.getParam1().getName()) + " != null && " + writeParamName(dep.getParam2().getName()) + " != null) || (" + writeParamName(dep.getParam1().getName()) + dep.getRelationalOp() + writeParamName(dep.getParam2().getName()) + "))";
    } else {
      assertOperation += "(" + writeParamName(dep.getParam1().getName()) + " != null && " + writeParamName(dep.getParam2().getName()) + " != null && " + writeParamName(dep.getParam1().getName()) + dep.getRelationalOp() + writeParamName(dep.getParam2().getName()) + ")";
    }
  }

  private void writeArithmeticDependency(ArithmeticDependency dep, boolean alone) {
    assertOperation += "(";
    if (alone) {
      assertOperation += "!(";
    }
    Iterator params = IteratorExtensions.toIterable(Iterators.filter(dep.eAllContents(), Param.class)).iterator();
    while (params.hasNext()) {
      Param param = (Param) params.next();
      assertOperation += writeParamName(param.getName()) + " != null && ";
    }
    if (alone) {
      assertOperation = assertOperation.substring(0, assertOperation.length() - 4);
      assertOperation += ") || (";
    }
    writeOperation(dep.getOperation());
    assertOperation += dep.getRelationalOp();
    assertOperation += Double.parseDouble(dep.getResult());
    assertOperation += ")";
    if (alone) {
      assertOperation += ")";
    }
  }

  private void writeOperation(es.us.isa.idl.idl.Operation operation) {
    if (operation.getOpeningParenthesis() == null) {
      assertOperation += writeParamName(operation.getFirstParam().getName());
      writeOperationContinuation(operation.getOperationContinuation());
    } else {
      assertOperation += "(";
      writeOperation(operation.getOperation());
      assertOperation += ")";
      if (operation.getOperationContinuation() != null) {
        writeOperationContinuation(operation.getOperationContinuation());
      }
    }
  }

  private void writeOperationContinuation(OperationContinuation opCont) {
    assertOperation += opCont.getArithOp();
    if (opCont.getAdditionalParams() instanceof ParamImpl) {
      Param param = (Param) opCont.getAdditionalParams();
      assertOperation += writeParamName(param.getName());
    } else {
      writeOperation((es.us.isa.idl.idl.Operation) opCont.getAdditionalParams());
    }
  }

  private void writePredefinedDependency(GeneralPredefinedDependency dep, boolean alone) {
    if (!alone ^ dep.getNot() != null) {
      assertOperation += "!";
    }
    assertOperation += "DependencyUtil.doNotSatisfy" + dep.getPredefDepType() + "Dependency(";
    for (GeneralPredicate depElement : dep.getPredefDepElements()) {
      writePredicate(depElement);
      assertOperation += ",";
    }
    assertOperation = assertOperation.substring(0, assertOperation.length() - 1);
    assertOperation += ")";
  }

  private static CodegenModel reconcileInlineEnums(CodegenModel codegenModel, CodegenModel parentCodegenModel) {
    if (!parentCodegenModel.hasEnums) {
      return codegenModel;
    }
    final List<CodegenProperty> parentModelCodegenProperties = parentCodegenModel.vars;
    List<CodegenProperty> codegenProperties = codegenModel.vars;
    boolean removedChildEnum = false;
    for (CodegenProperty parentModelCodegenProperty : parentModelCodegenProperties) {
      if (parentModelCodegenProperty.isEnum) {
        Iterator<CodegenProperty> iterator = codegenProperties.iterator();
        while (iterator.hasNext()) {
          CodegenProperty codegenProperty = iterator.next();
          if (codegenProperty.isEnum && codegenProperty.equals(parentModelCodegenProperty)) {
            iterator.remove();
            removedChildEnum = true;
          }
        }
      }
    }
    if (removedChildEnum) {
      codegenModel.vars = codegenProperties;
    }
    return codegenModel;
  }

  private static String sanitizePackageName(String packageName) {
    packageName = packageName.trim();
    packageName = packageName.replaceAll("[^a-zA-Z0-9_\\.]", "_");
    if (Strings.isNullOrEmpty(packageName)) {
      return "invalidPackageName";
    }
    return packageName;
  }

  public String getInvokerPackage() {
    return invokerPackage;
  }

  public void setInvokerPackage(String invokerPackage) {
    this.invokerPackage = invokerPackage;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getArtifactVersion() {
    return artifactVersion;
  }

  public void setArtifactVersion(String artifactVersion) {
    this.artifactVersion = artifactVersion;
  }

  public String getArtifactUrl() {
    return artifactUrl;
  }

  public void setArtifactUrl(String artifactUrl) {
    this.artifactUrl = artifactUrl;
  }

  public String getArtifactDescription() {
    return artifactDescription;
  }

  public void setArtifactDescription(String artifactDescription) {
    this.artifactDescription = artifactDescription;
  }

  public String getScmConnection() {
    return scmConnection;
  }

  public void setScmConnection(String scmConnection) {
    this.scmConnection = scmConnection;
  }

  public String getScmDeveloperConnection() {
    return scmDeveloperConnection;
  }

  public void setScmDeveloperConnection(String scmDeveloperConnection) {
    this.scmDeveloperConnection = scmDeveloperConnection;
  }

  public String getScmUrl() {
    return scmUrl;
  }

  public void setScmUrl(String scmUrl) {
    this.scmUrl = scmUrl;
  }

  public String getDeveloperName() {
    return developerName;
  }

  public void setDeveloperName(String developerName) {
    this.developerName = developerName;
  }

  public String getDeveloperEmail() {
    return developerEmail;
  }

  public void setDeveloperEmail(String developerEmail) {
    this.developerEmail = developerEmail;
  }

  public String getDeveloperOrganization() {
    return developerOrganization;
  }

  public void setDeveloperOrganization(String developerOrganization) {
    this.developerOrganization = developerOrganization;
  }

  public String getDeveloperOrganizationUrl() {
    return developerOrganizationUrl;
  }

  public void setDeveloperOrganizationUrl(String developerOrganizationUrl) {
    this.developerOrganizationUrl = developerOrganizationUrl;
  }

  public String getLicenseName() {
    return licenseName;
  }

  public void setLicenseName(String licenseName) {
    this.licenseName = licenseName;
  }

  public String getLicenseUrl() {
    return licenseUrl;
  }

  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

  public String getSourceFolder() {
    return sourceFolder;
  }

  public void setSourceFolder(String sourceFolder) {
    this.sourceFolder = sourceFolder;
  }

  public String getTestFolder() {
    return testFolder;
  }

  public void setTestFolder(String testFolder) {
    this.testFolder = testFolder;
  }

  public void setSerializeBigDecimalAsString(boolean s) {
    this.serializeBigDecimalAsString = s;
  }

  public Boolean getSerializableModel() {
    return serializableModel;
  }

  public void setSerializableModel(Boolean serializableModel) {
    this.serializableModel = serializableModel;
  }

  private String sanitizePath(String p) {
    return p.replaceAll("\"", "%22");
  }

  public void setFullJavaUtil(boolean fullJavaUtil) {
    this.fullJavaUtil = fullJavaUtil;
  }

  public void setDiscriminatorCaseSensitive(boolean discriminatorCaseSensitive) {
    this.discriminatorCaseSensitive = discriminatorCaseSensitive;
  }

  public void setWithXml(boolean withXml) {
    this.withXml = withXml;
  }

  public String getDateLibrary() {
    return dateLibrary;
  }

  public void setDateLibrary(String library) {
    this.dateLibrary = library;
  }

  public void setSupportAsync(boolean enabled) {
    this.supportAsync = enabled;
  }

  public void setDisableHtmlEscaping(boolean disabled) {
    this.disableHtmlEscaping = disabled;
  }

  public String getBooleanGetterPrefix() {
    return booleanGetterPrefix;
  }

  public void setBooleanGetterPrefix(String booleanGetterPrefix) {
    this.booleanGetterPrefix = booleanGetterPrefix;
  }

  public void setIgnoreAnyOfInEnum(boolean ignoreAnyOfInEnum) {
    this.ignoreAnyOfInEnum = ignoreAnyOfInEnum;
  }

  public boolean isOpenApiNullable() {
    return openApiNullable;
  }

  public void setOpenApiNullable(final boolean openApiNullable) {
    this.openApiNullable = openApiNullable;
  }

  @Override public String escapeQuotationMark(String input) {
    return input.replace("\"", "");
  }

  @Override public String escapeUnsafeCharacters(String input) {
    return input.replace("*/", "*_/").replace("/*", "/_*");
  }

  private String deriveInvokerPackageName(String input) {
    String[] parts = input.split(Pattern.quote("."));
    StringBuilder sb = new StringBuilder();
    String delim = "";
    for (String p : Arrays.copyOf(parts, parts.length - 1)) {
      sb.append(delim).append(p);
      delim = ".";
    }
    return sb.toString();
  }

  private String buildSnapshotVersion(String version) {
    if (version.endsWith("-SNAPSHOT")) {
      return version;
    }
    return version + "-SNAPSHOT";
  }

  public void setSupportJava6(boolean value) {
    this.supportJava6 = value;
  }

  @Override public String toRegularExpression(String pattern) {
    return escapeText(pattern);
  }

  @Override public String toBooleanGetter(String name) {
    return booleanGetterPrefix + getterAndSetterCapitalize(name);
  }

  @Override public String sanitizeTag(String tag) {
    tag = camelize(underscore(sanitizeName(tag)));
    if (tag.matches("^\\d.*")) {
      tag = "Class" + tag;
    }
    return tag;
  }

  @Override public String getterAndSetterCapitalize(String name) {
    boolean lowercaseFirstLetter = false;
    if (name == null || name.length() == 0) {
      return name;
    }
    name = toVarName(name);
    if (name.length() > 1 && Character.isLowerCase(name.charAt(0)) && Character.isUpperCase(name.charAt(1))) {
      lowercaseFirstLetter = true;
    }
    return camelize(name, lowercaseFirstLetter);
  }

  @Override public void postProcessFile(File file, String fileType) {
    if (file == null) {
      return;
    }
    String javaPostProcessFile = System.getenv("JAVA_POST_PROCESS_FILE");
    if (StringUtils.isEmpty(javaPostProcessFile)) {
      return;
    }
    if ("java".equals(FilenameUtils.getExtension(file.toString()))) {
      String command = javaPostProcessFile + " " + file;
      try {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        int exitValue = p.exitValue();
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

  public void setParentGroupId(final String parentGroupId) {
    this.parentGroupId = parentGroupId;
  }

  public void setParentArtifactId(final String parentArtifactId) {
    this.parentArtifactId = parentArtifactId;
  }

  public void setParentVersion(final String parentVersion) {
    this.parentVersion = parentVersion;
  }

  public void setParentOverridden(final boolean parentOverridden) {
    this.parentOverridden = parentOverridden;
  }

  public List<String> getAdditionalModelTypeAnnotations() {
    return additionalModelTypeAnnotations;
  }

  public void setAdditionalModelTypeAnnotations(final List<String> additionalModelTypeAnnotations) {
    this.additionalModelTypeAnnotations = additionalModelTypeAnnotations;
  }

  public void setAdditionalEnumTypeAnnotations(final List<String> additionalEnumTypeAnnotations) {
    this.additionalEnumTypeAnnotations = additionalEnumTypeAnnotations;
  }

  @Override protected void addAdditionPropertiesToCodeGenModel(CodegenModel codegenModel, Schema schema) {
    if (!supportsAdditionalPropertiesWithComposedSchema) {
      super.addAdditionPropertiesToCodeGenModel(codegenModel, schema);
    }
    Schema s = getAdditionalProperties(schema);
    if (s != null) {
      codegenModel.additionalPropertiesType = getSchemaType(s);
      addImport(codegenModel, codegenModel.additionalPropertiesType);
    }
  }

  public static final String TEST_OUTPUT = "testOutput";

  public static final String IMPLICIT_HEADERS = "implicitHeaders";

  public static final String IMPLICIT_HEADERS_REGEX = "implicitHeadersRegex";

  public static final String DEFAULT_TEST_FOLDER = "${project.build.directory}/generated-test-sources/openapi";

  protected String outputTestFolder = "";

  protected DocumentationProvider documentationProvider;

  protected AnnotationLibrary annotationLibrary;

  protected boolean implicitHeaders = false;

  protected String implicitHeadersRegex = null;

  private Map<String, String> schemaKeyToModelNameCache = new HashMap<>();

  @Override public Map<String, ModelsMap> postProcessAllModels(Map<String, ModelsMap> objs) {
    objs = super.postProcessAllModels(objs);
    objs = super.updateAllModels(objs);
    if (!additionalModelTypeAnnotations.isEmpty()) {
      for (String modelName : objs.keySet()) {
        Map<String, Object> models = (Map<String, Object>) objs.get(modelName);
        models.put(ADDITIONAL_MODEL_TYPE_ANNOTATIONS, additionalModelTypeAnnotations);
      }
    }
    if (!additionalEnumTypeAnnotations.isEmpty()) {
      for (String modelName : objs.keySet()) {
        Map<String, Object> models = (Map<String, Object>) objs.get(modelName);
        models.put(ADDITIONAL_ENUM_TYPE_ANNOTATIONS, additionalEnumTypeAnnotations);
      }
    }
    return objs;
  }

  @Override public ModelsMap postProcessModels(ModelsMap objs) {
    List<Map<String, String>> recursiveImports = objs.getImports();
    if (recursiveImports == null) {
      return objs;
    }
    ListIterator<Map<String, String>> listIterator = recursiveImports.listIterator();
    while (listIterator.hasNext()) {
      String _import = listIterator.next().get("import");
      if (importMapping.containsKey(_import)) {
        Map<String, String> newImportMap = new HashMap<>();
        newImportMap.put("import", importMapping.get(_import));
        listIterator.add(newImportMap);
      }
    }
    for (ModelMap mo : objs.getModels()) {
      CodegenModel cm = mo.getModel();
      if (this.serializableModel) {
        cm.getVendorExtensions().putIfAbsent("x-implements", new ArrayList<String>());
        ((ArrayList<String>) cm.getVendorExtensions().get("x-implements")).add("Serializable");
      }
    }
    return postProcessModelsEnum(objs);
  }

  @Override public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
    List<Map<String, String>> imports = objs.getImports();
    Pattern pattern = Pattern.compile("java\\.util\\.(List|ArrayList|Map|HashMap)");
    for (Iterator<Map<String, String>> itr = imports.iterator(); itr.hasNext(); ) {
      String itrImport = itr.next().get("import");
      if (pattern.matcher(itrImport).matches()) {
        itr.remove();
      }
    }
    OperationMap operations = objs.getOperations();
    List<CodegenOperation> operationList = operations.getOperation();
    for (CodegenOperation op : operationList) {
      Collection<String> operationImports = new ConcurrentSkipListSet<>();
      for (CodegenParameter p : op.allParams) {
        if (importMapping.containsKey(p.dataType)) {
          operationImports.add(importMapping.get(p.dataType));
        }
      }
      op.vendorExtensions.put("x-java-import", operationImports);
      handleImplicitHeaders(op);
    }
    return objs;
  }

  @Override public void setOutputDir(String dir) {
    super.setOutputDir(dir);
    if (this.outputTestFolder.isEmpty()) {
      setOutputTestFolder(dir);
    }
  }

  public String getOutputTestFolder() {
    if (outputTestFolder.isEmpty()) {
      return DEFAULT_TEST_FOLDER;
    }
    return outputTestFolder;
  }

  public void setOutputTestFolder(String outputTestFolder) {
    this.outputTestFolder = outputTestFolder;
  }

  @Override public DocumentationProvider getDocumentationProvider() {
    return documentationProvider;
  }

  @Override public void setDocumentationProvider(DocumentationProvider documentationProvider) {
    this.documentationProvider = documentationProvider;
  }

  @Override public AnnotationLibrary getAnnotationLibrary() {
    return annotationLibrary;
  }

  @Override public void setAnnotationLibrary(AnnotationLibrary annotationLibrary) {
    this.annotationLibrary = annotationLibrary;
  }

  public void setImplicitHeaders(boolean implicitHeaders) {
    this.implicitHeaders = implicitHeaders;
  }

  public void setImplicitHeadersRegex(String implicitHeadersRegex) {
    this.implicitHeadersRegex = implicitHeadersRegex;
  }

  protected Optional<CodegenProperty> findByName(String name, List<CodegenProperty> properties) {
    if (properties == null || properties.isEmpty()) {
      return Optional.empty();
    }
    return properties.stream().filter((p) -> p.name.equals(name)).findFirst();
  }

  protected void handleImplicitHeaders(CodegenOperation operation) {
    if (operation.allParams.isEmpty()) {
      return;
    }
    final ArrayList<CodegenParameter> copy = new ArrayList<>(operation.allParams);
    operation.allParams.clear();
    for (CodegenParameter p : copy) {
      if (p.isHeaderParam && (implicitHeaders || shouldBeImplicitHeader(p))) {
        operation.implicitHeadersParams.add(p);
        operation.headerParams.removeIf((header) -> header.baseName.equals(p.baseName));
        LOGGER.info("Update operation [{}]. Remove header [{}] because it\'s marked to be implicit", operation.operationId, p.baseName);
      } else {
        operation.allParams.add(p);
      }
    }
  }

  private boolean shouldBeImplicitHeader(CodegenParameter parameter) {
    return StringUtils.isNotBlank(implicitHeadersRegex) && parameter.baseName.matches(implicitHeadersRegex);
  }

  @Override public void addImportsToOneOfInterface(List<Map<String, String>> imports) {
    if (additionalProperties.containsKey(JACKSON)) {
      for (String i : Arrays.asList("JsonSubTypes", "JsonTypeInfo")) {
        Map<String, String> oneImport = new HashMap<>();
        oneImport.put("import", importMapping.get(i));
        if (!imports.contains(oneImport)) {
          imports.add(oneImport);
        }
      }
    }
  }

  @Override public List<VendorExtension> getSupportedVendorExtensions() {
    List<VendorExtension> extensions = super.getSupportedVendorExtensions();
    extensions.add(VendorExtension.X_DISCRIMINATOR_VALUE);
    extensions.add(VendorExtension.X_IMPLEMENTS);
    extensions.add(VendorExtension.X_SETTER_EXTRA_ANNOTATION);
    extensions.add(VendorExtension.X_TAGS);
    extensions.add(VendorExtension.X_ACCEPTS);
    extensions.add(VendorExtension.X_CONTENT_TYPE);
    extensions.add(VendorExtension.X_CLASS_EXTRA_ANNOTATION);
    extensions.add(VendorExtension.X_FIELD_EXTRA_ANNOTATION);
    return extensions;
  }
}