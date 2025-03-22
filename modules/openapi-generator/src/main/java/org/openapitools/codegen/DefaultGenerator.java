package org.openapitools.codegen;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.tags.Tag;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.PathFileComparator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.api.TemplateDefinition;
import org.openapitools.codegen.api.TemplatePathLocator;
import org.openapitools.codegen.api.TemplateProcessor;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.api.TemplatingEngineAdapter;
import org.openapitools.codegen.api.TemplateFileType;
import org.openapitools.codegen.ignore.CodegenIgnoreProcessor;
import org.openapitools.codegen.languages.PythonPriorClientCodegen;
import org.openapitools.codegen.languages.PythonClientCodegen;
import org.openapitools.codegen.meta.GeneratorMetadata;
import org.openapitools.codegen.meta.Stability;
import org.openapitools.codegen.model.ApiInfoMap;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.serializer.SerializerUtils;
import org.openapitools.codegen.templating.CommonTemplateContentLocator;
import org.openapitools.codegen.templating.GeneratorTemplateContentLocator;
import org.openapitools.codegen.templating.MustacheEngineAdapter;
import org.openapitools.codegen.templating.TemplateManagerOptions;
import org.openapitools.codegen.utils.ImplementationVersion;
import org.openapitools.codegen.utils.ModelUtils;
import org.openapitools.codegen.utils.ProcessUtils;
import org.openapitools.codegen.utils.URLPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.openapitools.codegen.utils.OnceLogger.once;

@SuppressWarnings(value = { "rawtypes" }) public class DefaultGenerator implements Generator {
  private static final String METADATA_DIR = ".openapi-generator";

  protected final Logger LOGGER = LoggerFactory.getLogger(DefaultGenerator.class);

  private final boolean dryRun;

  protected CodegenConfig config;

  protected ClientOptInput opts;

  protected OpenAPI openAPI;

  protected CodegenIgnoreProcessor ignoreProcessor;

  private Boolean generateApis = null;

  private Boolean generateModels = null;

  private Boolean generateSupportingFiles = null;

  private Boolean generateApiTests = null;

  private Boolean generateApiDocumentation = null;

  private Boolean generateModelTests = null;

  private Boolean generateModelDocumentation = null;

  private Boolean generateMetadata = true;

  private String basePath;

  private String basePathWithoutHost;

  private String contextPath;

  private Map<String, String> generatorPropertyDefaults = new HashMap<>();

  protected TemplateProcessor templateProcessor = null;

  private List<TemplateDefinition> userDefinedTemplates = new ArrayList<>();

  public DefaultGenerator() {
    this(false);
  }

  public DefaultGenerator(Boolean dryRun) {
    this.dryRun = Boolean.TRUE.equals(dryRun);
    LOGGER.info("Generating with dryRun={}", this.dryRun);
  }

  @SuppressWarnings(value = { "deprecation" }) @Override public Generator opts(ClientOptInput opts) {
    this.opts = opts;
    this.openAPI = opts.getOpenAPI();
    this.config = opts.getConfig();
    List<TemplateDefinition> userFiles = opts.getUserDefinedTemplates();
    if (userFiles != null) {
      this.userDefinedTemplates = Collections.unmodifiableList(userFiles);
    }
    TemplateManagerOptions templateManagerOptions = new TemplateManagerOptions(this.config.isEnableMinimalUpdate(), this.config.isSkipOverwrite());
    if (this.dryRun) {
      this.templateProcessor = new DryRunTemplateManager(templateManagerOptions);
    } else {
      TemplatingEngineAdapter templatingEngine = this.config.getTemplatingEngine();
      if (templatingEngine instanceof MustacheEngineAdapter) {
        MustacheEngineAdapter mustacheEngineAdapter = (MustacheEngineAdapter) templatingEngine;
        mustacheEngineAdapter.setCompiler(this.config.processCompiler(mustacheEngineAdapter.getCompiler()));
      }
      TemplatePathLocator commonTemplateLocator = new CommonTemplateContentLocator();
      TemplatePathLocator generatorTemplateLocator = new GeneratorTemplateContentLocator(this.config);
      this.templateProcessor = new TemplateManager(templateManagerOptions, templatingEngine, new TemplatePathLocator[] { generatorTemplateLocator, commonTemplateLocator });
    }
    String ignoreFileLocation = this.config.getIgnoreFilePathOverride();
    if (ignoreFileLocation != null) {
      final File ignoreFile = new File(ignoreFileLocation);
      if (ignoreFile.exists() && ignoreFile.canRead()) {
        this.ignoreProcessor = new CodegenIgnoreProcessor(ignoreFile);
      } else {
        LOGGER.warn("Ignore file specified at {} is not valid. This will fall back to an existing ignore file if present in the output directory.", ignoreFileLocation);
      }
    }
    if (this.ignoreProcessor == null) {
      this.ignoreProcessor = new CodegenIgnoreProcessor(this.config.getOutputDir());
    }
    return this;
  }

  /**
     * Retrieves an instance to the configured template processor, available after user-defined options are
     * applied via {@link DefaultGenerator#opts(ClientOptInput)}.
     *
     * @return A configured {@link TemplateProcessor}, or null.
     */
  public TemplateProcessor getTemplateProcessor() {
    return templateProcessor;
  }

  /**
     * Programmatically disable the output of .openapi-generator/VERSION, .openapi-generator-ignore,
     * or other metadata files used by OpenAPI Generator.
     *
     * @param generateMetadata true: enable outputs, false: disable outputs
     */
  @SuppressWarnings(value = { "WeakerAccess" }) public void setGenerateMetadata(Boolean generateMetadata) {
    this.generateMetadata = generateMetadata;
  }

  /**
     * Set generator properties otherwise pulled from system properties.
     * Useful for running tests in parallel without relying on System.properties.
     *
     * @param key   The system property key
     * @param value The system property value
     */
  @SuppressWarnings(value = { "WeakerAccess" }) public void setGeneratorPropertyDefault(final String key, final String value) {
    this.generatorPropertyDefaults.put(key, value);
  }

  private Boolean getGeneratorPropertyDefaultSwitch(final String key, final Boolean defaultValue) {
    String result = null;
    if (this.generatorPropertyDefaults.containsKey(key)) {
      result = this.generatorPropertyDefaults.get(key);
    }
    if (result != null) {
      return Boolean.valueOf(result);
    }
    return defaultValue;
  }

  void configureGeneratorProperties() {
    generateApis = GlobalSettings.getProperty(CodegenConstants.APIS) != null ? Boolean.TRUE : getGeneratorPropertyDefaultSwitch(CodegenConstants.APIS, null);
    generateModels = GlobalSettings.getProperty(CodegenConstants.MODELS) != null ? Boolean.TRUE : getGeneratorPropertyDefaultSwitch(CodegenConstants.MODELS, null);
    generateSupportingFiles = GlobalSettings.getProperty(CodegenConstants.SUPPORTING_FILES) != null ? Boolean.TRUE : getGeneratorPropertyDefaultSwitch(CodegenConstants.SUPPORTING_FILES, null);
    if (generateApis == null && generateModels == null && generateSupportingFiles == null) {
      generateApis = generateModels = generateSupportingFiles = true;
    } else {
      if (generateApis == null) {
        generateApis = false;
      }
      if (generateModels == null) {
        generateModels = false;
      }
      if (generateSupportingFiles == null) {
        generateSupportingFiles = false;
      }
    }
    generateModelTests = GlobalSettings.getProperty(CodegenConstants.MODEL_TESTS) != null ? Boolean.valueOf(GlobalSettings.getProperty(CodegenConstants.MODEL_TESTS)) : getGeneratorPropertyDefaultSwitch(CodegenConstants.MODEL_TESTS, true);
    generateModelDocumentation = GlobalSettings.getProperty(CodegenConstants.MODEL_DOCS) != null ? Boolean.valueOf(GlobalSettings.getProperty(CodegenConstants.MODEL_DOCS)) : getGeneratorPropertyDefaultSwitch(CodegenConstants.MODEL_DOCS, true);
    generateApiTests = GlobalSettings.getProperty(CodegenConstants.API_TESTS) != null ? Boolean.valueOf(GlobalSettings.getProperty(CodegenConstants.API_TESTS)) : getGeneratorPropertyDefaultSwitch(CodegenConstants.API_TESTS, true);
    generateApiDocumentation = GlobalSettings.getProperty(CodegenConstants.API_DOCS) != null ? Boolean.valueOf(GlobalSettings.getProperty(CodegenConstants.API_DOCS)) : getGeneratorPropertyDefaultSwitch(CodegenConstants.API_DOCS, true);
    config.additionalProperties().put(CodegenConstants.GENERATE_API_TESTS, generateApiTests);
    config.additionalProperties().put(CodegenConstants.GENERATE_MODEL_TESTS, generateModelTests);
    config.additionalProperties().put(CodegenConstants.GENERATE_API_DOCS, generateApiDocumentation);
    config.additionalProperties().put(CodegenConstants.GENERATE_MODEL_DOCS, generateModelDocumentation);
    config.additionalProperties().put(CodegenConstants.GENERATE_APIS, generateApis);
    config.additionalProperties().put(CodegenConstants.GENERATE_MODELS, generateModels);
    if (!generateApiTests && !generateModelTests) {
      config.additionalProperties().put(CodegenConstants.EXCLUDE_TESTS, true);
    }
    if (GlobalSettings.getProperty("debugOpenAPI") != null) {
      System.out.println(SerializerUtils.toJsonString(openAPI));
    } else {
      if (GlobalSettings.getProperty("debugSwagger") != null) {
        LOGGER.info("Please use system property \'debugOpenAPI\' instead of \'debugSwagger\'.");
        System.out.println(SerializerUtils.toJsonString(openAPI));
      }
    }
    config.processOpts();
    if (config.getUseOpenAPINormalizer()) {
      OpenAPINormalizer openapiNormalizer = new OpenAPINormalizer(openAPI, config.openapiNormalizer());
      openapiNormalizer.normalize();
    }
    if (config.getUseInlineModelResolver()) {
      InlineModelResolver inlineModelResolver = new InlineModelResolver();
      inlineModelResolver.setInlineSchemaNameMapping(config.inlineSchemaNameMapping());
      inlineModelResolver.setInlineSchemaNameDefaults(config.inlineSchemaNameDefault());
      inlineModelResolver.flatten(openAPI);
    }
    config.preprocessOpenAPI(openAPI);
    config.setOpenAPI(openAPI);
    config.additionalProperties().put("generatorVersion", ImplementationVersion.read());
    config.additionalProperties().put("generatedDate", ZonedDateTime.now().toString());
    config.additionalProperties().put("generatedYear", String.valueOf(ZonedDateTime.now().getYear()));
    config.additionalProperties().put("generatorClass", config.getClass().getName());
    config.additionalProperties().put("inputSpec", config.getInputSpec());
    if (openAPI.getExtensions() != null) {
      config.vendorExtensions().putAll(openAPI.getExtensions());
    }
    URL url = URLPathUtils.getServerURL(openAPI, config.serverVariableOverrides());
    contextPath = removeTrailingSlash(config.escapeText(url.getPath()));
    basePathWithoutHost = contextPath;
    if (URLPathUtils.isRelativeUrl(openAPI.getServers())) {
      basePath = removeTrailingSlash(basePathWithoutHost);
    } else {
      basePath = removeTrailingSlash(config.escapeText(URLPathUtils.getHost(openAPI, config.serverVariableOverrides())));
    }
  }

  private void configureOpenAPIInfo() {
    Info info = this.openAPI.getInfo();
    if (info == null) {
      return;
    }
    if (info.getTitle() != null) {
      config.additionalProperties().put("appName", config.escapeText(info.getTitle()));
    }
    if (info.getVersion() != null) {
      config.additionalProperties().put("appVersion", config.escapeText(info.getVersion()));
    } else {
      LOGGER.error("Missing required field info version. Default appVersion set to 1.0.0");
      config.additionalProperties().put("appVersion", "1.0.0");
    }
    if (StringUtils.isEmpty(info.getDescription())) {
      config.additionalProperties().put("appDescription", "No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)");
      config.additionalProperties().put("appDescriptionWithNewLines", config.additionalProperties().get("appDescription"));
      config.additionalProperties().put("unescapedAppDescription", "No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)");
    } else {
      config.additionalProperties().put("appDescription", config.escapeText(info.getDescription()));
      config.additionalProperties().put("appDescriptionWithNewLines", config.escapeTextWhileAllowingNewLines(info.getDescription()));
      config.additionalProperties().put("unescapedAppDescription", info.getDescription());
    }
    if (info.getContact() != null) {
      Contact contact = info.getContact();
      if (contact.getEmail() != null) {
        config.additionalProperties().put("infoEmail", config.escapeText(contact.getEmail()));
      }
      if (contact.getName() != null) {
        config.additionalProperties().put("infoName", config.escapeText(contact.getName()));
      }
      if (contact.getUrl() != null) {
        config.additionalProperties().put("infoUrl", config.escapeText(contact.getUrl()));
      }
    }
    if (info.getLicense() != null) {
      License license = info.getLicense();
      if (license.getName() != null) {
        config.additionalProperties().put("licenseInfo", config.escapeText(license.getName()));
      }
      if (license.getUrl() != null) {
        config.additionalProperties().put("licenseUrl", config.escapeText(license.getUrl()));
      }
    }
    if (info.getVersion() != null) {
      config.additionalProperties().put("version", config.escapeText(info.getVersion()));
    } else {
      LOGGER.error("Missing required field info version. Default version set to 1.0.0");
      config.additionalProperties().put("version", "1.0.0");
    }
    if (info.getTermsOfService() != null) {
      config.additionalProperties().put("termsOfService", config.escapeText(info.getTermsOfService()));
    }
  }

  private void generateModelTests(List<File> files, Map<String, Object> models, String modelName) throws IOException {
    for (Map.Entry<String, String> configModelTestTemplateFilesEntry : config.modelTestTemplateFiles().entrySet()) {
      String templateName = configModelTestTemplateFilesEntry.getKey();
      String suffix = configModelTestTemplateFilesEntry.getValue();
      String filename = config.modelTestFileFolder() + File.separator + config.toModelTestFilename(modelName) + suffix;
      if (generateModelTests) {
        File modelTestFile = new File(filename);
        if (modelTestFile.exists()) {
          this.templateProcessor.skip(modelTestFile.toPath(), "Test files never overwrite an existing file of the same name.");
        } else {
          File written = processTemplateToFile(models, templateName, filename, generateModelTests, CodegenConstants.MODEL_TESTS, config.modelTestFileFolder());
          if (written != null) {
            files.add(written);
            if (config.isEnablePostProcessFile() && !dryRun) {
              config.postProcessFile(written, "model-test");
            }
          }
        }
      } else {
        if (dryRun) {
          Path skippedPath = java.nio.file.Paths.get(filename);
          this.templateProcessor.skip(skippedPath, "Skipped by modelTests option supplied by user.");
        }
      }
    }
  }

  private void generateModelDocumentation(List<File> files, Map<String, Object> models, String modelName) throws IOException {
    for (String templateName : config.modelDocTemplateFiles().keySet()) {
      String docExtension = config.getDocExtension();
      String suffix = docExtension != null ? docExtension : config.modelDocTemplateFiles().get(templateName);
      String filename = config.modelDocFileFolder() + File.separator + config.toModelDocFilename(modelName) + suffix;
      File written = processTemplateToFile(models, templateName, filename, generateModelDocumentation, CodegenConstants.MODEL_DOCS);
      if (written != null) {
        files.add(written);
        if (config.isEnablePostProcessFile() && !dryRun) {
          config.postProcessFile(written, "model-doc");
        }
      }
    }
  }

  private void generateModel(List<File> files, Map<String, Object> models, String modelName) throws IOException {
    for (String templateName : config.modelTemplateFiles().keySet()) {
      String filename = config.modelFilename(templateName, modelName);
      File written = processTemplateToFile(models, templateName, filename, generateModels, CodegenConstants.MODELS);
      if (written != null) {
        files.add(written);
        if (config.isEnablePostProcessFile() && !dryRun) {
          config.postProcessFile(written, "model");
        }
      }
    }
  }

  void generateModels(List<File> files, List<ModelMap> allModels, List<String> unusedModels) {
    if (!generateModels) {
      LOGGER.info("Skipping generation of models.");
      return;
    }
    final Map<String, Schema> schemas = ModelUtils.getSchemas(this.openAPI);
    if (schemas == null) {
      LOGGER.warn("Skipping generation of models because specification document has no schemas.");
      return;
    }
    String modelNames = GlobalSettings.getProperty("models");
    Set<String> modelsToGenerate = null;
    if (modelNames != null && !modelNames.isEmpty()) {
      modelsToGenerate = new HashSet<>(Arrays.asList(modelNames.split(",")));
    }
    Set<String> modelKeys = schemas.keySet();
    if (modelsToGenerate != null && !modelsToGenerate.isEmpty()) {
      Set<String> updatedKeys = new HashSet<>();
      for (String m : modelKeys) {
        if (modelsToGenerate.contains(m)) {
          updatedKeys.add(m);
        }
      }
      modelKeys = updatedKeys;
    }
    Map<String, ModelsMap> allProcessedModels = new TreeMap<>((o1, o2) -> ObjectUtils.compare(config.toModelName(o1), config.toModelName(o2)));
    Boolean skipFormModel = GlobalSettings.getProperty(CodegenConstants.SKIP_FORM_MODEL) != null ? Boolean.valueOf(GlobalSettings.getProperty(CodegenConstants.SKIP_FORM_MODEL)) : getGeneratorPropertyDefaultSwitch(CodegenConstants.SKIP_FORM_MODEL, true);
    for (String name : modelKeys) {
      try {
        if (config.schemaMapping().containsKey(name)) {
          LOGGER.debug("Model {} not imported due to schema mapping", name);
          for (String templateName : config.modelTemplateFiles().keySet()) {
            String filename = config.modelFilename(templateName, name);
            Path path = java.nio.file.Paths.get(filename);
            this.templateProcessor.skip(path, "Skipped prior to model processing due to schema mapping.");
          }
          continue;
        }
        if (unusedModels.contains(name)) {
          if (Boolean.FALSE.equals(skipFormModel)) {
            LOGGER.info("Model {} (marked as unused due to form parameters) is generated due to the global property `skipFormModel` set to false", name);
          } else {
            LOGGER.info("Model {} not generated since it\'s marked as unused (due to form parameters) and `skipFormModel` (global property) set to true (default)", name);
            continue;
          }
        }
        Schema schema = schemas.get(name);
        if (ModelUtils.isFreeFormObject(this.openAPI, schema)) {
          Schema refSchema = new Schema();
          refSchema.set$ref("#/components/schemas/" + name);
          Schema unaliasedSchema = config.unaliasSchema(refSchema);
          if (unaliasedSchema.get$ref() == null) {
            LOGGER.info("Model {} not generated since it\'s a free-form object", name);
            continue;
          }
        } else {
          if (ModelUtils.isMapSchema(schema)) {
            if (!ModelUtils.isGenerateAliasAsModel(schema) && !ModelUtils.isComposedSchema(schema) && (schema.getProperties() == null || schema.getProperties().isEmpty())) {
              LOGGER.info("Model {} not generated since it\'s an alias to map (without property) and `generateAliasAsModel` is set to false (default)", name);
              continue;
            }
          } else {
            if (ModelUtils.isArraySchema(schema)) {
              if (!ModelUtils.isGenerateAliasAsModel(schema) && (schema.getProperties() == null || schema.getProperties().isEmpty())) {
                LOGGER.info("Model {} not generated since it\'s an alias to array (without property) and `generateAliasAsModel` is set to false (default)", name);
                continue;
              }
            }
          }
        }
        Map<String, Schema> schemaMap = new HashMap<>();
        schemaMap.put(name, schema);
        ModelsMap models = processModels(config, schemaMap);
        models.put("classname", config.toModelName(name));
        models.putAll(config.additionalProperties());
        allProcessedModels.put(name, models);
      } catch (Exception e) {
        throw new RuntimeException("Could not process model \'" + name + "\'" + ".Please make sure that your schema is correct!", e);
      }
    }
    allProcessedModels = config.updateAllModels(allProcessedModels);
    allProcessedModels = config.postProcessAllModels(allProcessedModels);
    for (String modelName : allProcessedModels.keySet()) {
      ModelsMap models = allProcessedModels.get(modelName);
      models.put("modelPackage", config.modelPackage());
      try {
        if (config.schemaMapping().containsKey(modelName)) {
          continue;
        }
        List<ModelMap> modelList = models.getModels();
        if (modelList != null && !modelList.isEmpty()) {
          ModelMap modelTemplate = modelList.get(0);
          if (modelTemplate != null && modelTemplate.getModel() != null) {
            CodegenModel m = modelTemplate.getModel();
            if (m.isAlias && !((config instanceof PythonPriorClientCodegen) || (config instanceof PythonClientCodegen))) {
              continue;
            }
            if (Boolean.valueOf(String.valueOf(m.vendorExtensions.get("x-oag-internal-only")))) {
              LOGGER.info("Model {} not generated since it\'s marked as internal only (x-oag-internal-only: true).", modelName);
              continue;
            }
          }
          allModels.add(modelTemplate);
        }
        generateModel(files, models, modelName);
        generateModelTests(files, models, modelName);
        generateModelDocumentation(files, models, modelName);
      } catch (Exception e) {
        throw new RuntimeException("Could not generate model \'" + modelName + "\'", e);
      }
    }
    if (GlobalSettings.getProperty("debugModels") != null) {
      LOGGER.info("############ Model info ############");
      Json.prettyPrint(allModels);
    }
  }

  @SuppressWarnings(value = { "unchecked" }) void generateApis(List<File> files, List<OperationsMap> allOperations, List<ModelMap> allModels) {
    if (!generateApis) {
      LOGGER.info("Skipping generation of APIs.");
      return;
    }
    Map<String, List<CodegenOperation>> paths = processPaths(this.openAPI.getPaths());
    Set<String> apisToGenerate = null;
    String apiNames = GlobalSettings.getProperty("apis");
    if (apiNames != null && !apiNames.isEmpty()) {
      apisToGenerate = new HashSet<>(Arrays.asList(apiNames.split(",")));
    }
    if (apisToGenerate != null && !apisToGenerate.isEmpty()) {
      Map<String, List<CodegenOperation>> updatedPaths = new TreeMap<>();
      for (String m : paths.keySet()) {
        if (apisToGenerate.contains(m)) {
          updatedPaths.put(m, paths.get(m));
        }
      }
      paths = updatedPaths;
    }
    for (String tag : paths.keySet()) {
      try {
        List<CodegenOperation> ops = paths.get(tag);
        ops.sort((one, another) -> ObjectUtils.compare(one.operationId, another.operationId));
        OperationsMap operation = processOperations(config, tag, ops, allModels);
        URL url = URLPathUtils.getServerURL(openAPI, config.serverVariableOverrides());
        operation.put("basePath", basePath);
        operation.put("basePathWithoutHost", removeTrailingSlash(config.encodePath(url.getPath())));
        operation.put("contextPath", contextPath);
        operation.put("baseName", tag);
        operation.put("apiPackage", config.apiPackage());
        operation.put("modelPackage", config.modelPackage());
        operation.putAll(config.additionalProperties());
        operation.put("classname", config.toApiName(tag));
        operation.put("classVarName", config.toApiVarName(tag));
        operation.put("importPath", config.toApiImport(tag));
        operation.put("classFilename", config.toApiFilename(tag));
        operation.put("strictSpecBehavior", config.isStrictSpecBehavior());
        if (allModels == null || allModels.isEmpty()) {
          operation.put("hasModel", false);
        } else {
          operation.put("hasModel", true);
        }
        if (!config.vendorExtensions().isEmpty()) {
          operation.put("vendorExtensions", config.vendorExtensions());
        }
        if (config.vendorExtensions().containsKey("x-group-parameters")) {
          boolean isGroupParameters = Boolean.parseBoolean(config.vendorExtensions().get("x-group-parameters").toString());
          OperationMap objectMap = operation.getOperations();
          List<CodegenOperation> operations = objectMap.getOperation();
          for (CodegenOperation op : operations) {
            if (isGroupParameters && !op.vendorExtensions.containsKey("x-group-parameters")) {
              op.vendorExtensions.put("x-group-parameters", Boolean.TRUE);
            }
          }
        }
        boolean sortParamsByRequiredFlag = true;
        if (this.config.additionalProperties().containsKey(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG)) {
          sortParamsByRequiredFlag = Boolean.parseBoolean(this.config.additionalProperties().get(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG).toString());
        }
        operation.put("sortParamsByRequiredFlag", sortParamsByRequiredFlag);
        allOperations.add(operation);
        addAuthenticationSwitches(operation);
        for (String templateName : config.apiTemplateFiles().keySet()) {
          String filename = config.apiFilename(templateName, tag);
          File written = processTemplateToFile(operation, templateName, filename, generateApis, CodegenConstants.APIS);
          if (written != null) {
            files.add(written);
            if (config.isEnablePostProcessFile() && !dryRun) {
              config.postProcessFile(written, "api");
            }
          }
        }
        for (String templateName : config.apiTestTemplateFiles().keySet()) {
          String filename = config.apiTestFilename(templateName, tag);
          File apiTestFile = new File(filename);
          if (apiTestFile.exists()) {
            this.templateProcessor.skip(apiTestFile.toPath(), "Test files never overwrite an existing file of the same name.");
          } else {
            File written = processTemplateToFile(operation, templateName, filename, generateApiTests, CodegenConstants.API_TESTS, config.apiTestFileFolder());
            if (written != null) {
              files.add(written);
              if (config.isEnablePostProcessFile() && !dryRun) {
                config.postProcessFile(written, "api-test");
              }
            }
          }
        }
        for (String templateName : config.apiDocTemplateFiles().keySet()) {
          String filename = config.apiDocFilename(templateName, tag);
          File written = processTemplateToFile(operation, templateName, filename, generateApiDocumentation, CodegenConstants.API_DOCS);
          if (written != null) {
            files.add(written);
            if (config.isEnablePostProcessFile() && !dryRun) {
              config.postProcessFile(written, "api-doc");
            }
          }
        }
      } catch (Exception e) {
        throw new RuntimeException("Could not generate api file for \'" + tag + "\'", e);
      }
    }
    if (GlobalSettings.getProperty("debugOperations") != null) {
      LOGGER.info("############ Operation info ############");
      Json.prettyPrint(allOperations);
    }
  }

  private void generateSupportingFiles(List<File> files, Map<String, Object> bundle) {
    if (!generateSupportingFiles) {
      LOGGER.info("Skipping generation of supporting files.");
      return;
    }
    Set<String> supportingFilesToGenerate = null;
    String supportingFiles = GlobalSettings.getProperty(CodegenConstants.SUPPORTING_FILES);
    if (supportingFiles != null && !supportingFiles.isEmpty()) {
      supportingFilesToGenerate = new HashSet<>(Arrays.asList(supportingFiles.split(",")));
    }
    for (SupportingFile support : config.supportingFiles()) {
      try {
        String outputFolder = config.outputFolder();
        if (StringUtils.isNotEmpty(support.getFolder())) {
          outputFolder += File.separator + support.getFolder();
        }
        File of = new File(outputFolder);
        String outputFilename = new File(support.getDestinationFilename()).isAbsolute() ? support.getDestinationFilename() : outputFolder + File.separator + support.getDestinationFilename().replace('/', File.separatorChar);
        if (!of.isDirectory()) {
          if (!dryRun && ignoreProcessor.allowsFile(new File(outputFilename)) && !of.mkdirs()) {
            once(LOGGER).debug("Output directory {} not created. It {}.", outputFolder, of.exists() ? "already exists." : "may not have appropriate permissions.");
          }
        }
        boolean shouldGenerate = true;
        if (supportingFilesToGenerate != null && !supportingFilesToGenerate.isEmpty()) {
          shouldGenerate = supportingFilesToGenerate.contains(support.getDestinationFilename());
        }
        File written = processTemplateToFile(bundle, support.getTemplateFile(), outputFilename, shouldGenerate, CodegenConstants.SUPPORTING_FILES);
        if (written != null) {
          files.add(written);
          if (config.isEnablePostProcessFile() && !dryRun) {
            config.postProcessFile(written, "supporting-file");
          }
        }
      } catch (Exception e) {
        throw new RuntimeException("Could not generate supporting file \'" + support + "\'", e);
      }
    }
    final String openapiGeneratorIgnore = ".openapi-generator-ignore";
    String ignoreFileNameTarget = config.outputFolder() + File.separator + openapiGeneratorIgnore;
    File ignoreFile = new File(ignoreFileNameTarget);
    if (generateMetadata) {
      try {
        boolean shouldGenerate = !ignoreFile.exists();
        if (shouldGenerate && supportingFilesToGenerate != null && !supportingFilesToGenerate.isEmpty()) {
          shouldGenerate = supportingFilesToGenerate.contains(openapiGeneratorIgnore);
        }
        File written = processTemplateToFile(bundle, openapiGeneratorIgnore, ignoreFileNameTarget, shouldGenerate, CodegenConstants.SUPPORTING_FILES);
        if (written != null) {
          files.add(written);
          if (config.isEnablePostProcessFile() && !dryRun) {
            config.postProcessFile(written, "openapi-generator-ignore");
          }
        }
      } catch (Exception e) {
        throw new RuntimeException("Could not generate supporting file \'" + ignoreFileNameTarget + "\'", e);
      }
    } else {
      this.templateProcessor.skip(ignoreFile.toPath(), "Skipped by generateMetadata option supplied by user.");
    }
    generateVersionMetadata(files);
  }

  Map<String, Object> buildSupportFileBundle(List<OperationsMap> allOperations, List<ModelMap> allModels) {
    Map<String, Object> bundle = new HashMap<>(config.additionalProperties());
    bundle.put("apiPackage", config.apiPackage());
    ApiInfoMap apis = new ApiInfoMap();
    apis.setApis(allOperations);
    URL url = URLPathUtils.getServerURL(openAPI, config.serverVariableOverrides());
    bundle.put("openAPI", openAPI);
    bundle.put("basePath", basePath);
    bundle.put("basePathWithoutHost", basePathWithoutHost);
    bundle.put("scheme", URLPathUtils.getScheme(url, config));
    bundle.put("host", url.getHost());
    if (url.getPort() != 80 && url.getPort() != 443 && url.getPort() != -1) {
      bundle.put("port", url.getPort());
    }
    bundle.put("contextPath", contextPath);
    bundle.put("apiInfo", apis);
    bundle.put("models", allModels);
    bundle.put("apiFolder", config.apiPackage().replace('.', File.separatorChar));
    bundle.put("modelPackage", config.modelPackage());
    bundle.put("library", config.getLibrary());
    bundle.put("generatorLanguageVersion", config.generatorLanguageVersion());
    addAuthenticationSwitches(bundle);
    List<CodegenServer> servers = config.fromServers(openAPI.getServers());
    if (servers != null && !servers.isEmpty()) {
      servers.forEach((server) -> server.url = removeTrailingSlash(server.url));
      bundle.put("servers", servers);
      bundle.put("hasServers", true);
    }
    if (openAPI.getExternalDocs() != null) {
      bundle.put("externalDocs", openAPI.getExternalDocs());
    }
    for (int i = 0; i < allModels.size() - 1; i++) {
      CodegenModel m = allModels.get(i).getModel();
      m.hasMoreModels = true;
    }
    config.postProcessSupportingFileData(bundle);
    if (GlobalSettings.getProperty("debugSupportingFiles") != null) {
      LOGGER.info("############ Supporting file info ############");
      Json.prettyPrint(bundle);
    }
    return bundle;
  }

  /**
     * Add authentication methods to the given map
     * This adds a boolean and a collection for each authentication type to the map.
     * <p>
     * Examples:
     * <p>
     *   boolean hasOAuthMethods
     * <p>
     *   List&lt;CodegenSecurity&gt; oauthMethods
     *
     * @param bundle the map which the booleans and collections will be added
     */
  void addAuthenticationSwitches(Map<String, Object> bundle) {
    Map<String, SecurityScheme> securitySchemeMap = openAPI.getComponents() != null ? openAPI.getComponents().getSecuritySchemes() : null;
    List<CodegenSecurity> authMethods = config.fromSecurity(securitySchemeMap);
    if (authMethods != null && !authMethods.isEmpty()) {
      bundle.put("authMethods", authMethods);
      bundle.put("hasAuthMethods", true);
      if (ProcessUtils.hasOAuthMethods(authMethods)) {
        bundle.put("hasOAuthMethods", true);
        bundle.put("oauthMethods", ProcessUtils.getOAuthMethods(authMethods));
      }
      if (ProcessUtils.hasHttpBearerMethods(authMethods)) {
        bundle.put("hasHttpBearerMethods", true);
        bundle.put("httpBearerMethods", ProcessUtils.getHttpBearerMethods(authMethods));
      }
      if (ProcessUtils.hasHttpSignatureMethods(authMethods)) {
        bundle.put("hasHttpSignatureMethods", true);
        bundle.put("httpSignatureMethods", ProcessUtils.getHttpSignatureMethods(authMethods));
      }
      if (ProcessUtils.hasHttpBasicMethods(authMethods)) {
        bundle.put("hasHttpBasicMethods", true);
        bundle.put("httpBasicMethods", ProcessUtils.getHttpBasicMethods(authMethods));
      }
      if (ProcessUtils.hasApiKeyMethods(authMethods)) {
        bundle.put("hasApiKeyMethods", true);
        bundle.put("apiKeyMethods", ProcessUtils.getApiKeyMethods(authMethods));
      }
    }
  }

  @Override public List<File> generate() {
    if (openAPI == null) {
      throw new RuntimeException("Issues with the OpenAPI input. Possible causes: invalid/missing spec, malformed JSON/YAML files, etc.");
    }
    if (config == null) {
      throw new RuntimeException("missing config!");
    }
    if (config.getGeneratorMetadata() == null) {
      LOGGER.warn("Generator \'{}\' is missing generator metadata!", config.getName());
    } else {
      GeneratorMetadata generatorMetadata = config.getGeneratorMetadata();
      if (StringUtils.isNotEmpty(generatorMetadata.getGenerationMessage())) {
        LOGGER.info(generatorMetadata.getGenerationMessage());
      }
      Stability stability = generatorMetadata.getStability();
      String stabilityMessage = String.format(Locale.ROOT, "Generator \'%s\' is considered %s.", config.getName(), stability.value());
      if (stability == Stability.DEPRECATED) {
        LOGGER.warn(stabilityMessage);
      } else {
        LOGGER.info(stabilityMessage);
      }
    }
    configureGeneratorProperties();
    configureOpenAPIInfo();
    config.processOpenAPI(openAPI);
    processUserDefinedTemplates();
    List<File> files = new ArrayList<>();
    List<String> filteredSchemas = ModelUtils.getSchemasUsedOnlyInFormParam(openAPI);
    List<ModelMap> allModels = new ArrayList<>();
    generateModels(files, allModels, filteredSchemas);
    List<OperationsMap> allOperations = new ArrayList<>();
    generateApis(files, allOperations, allModels);
    Map<String, Object> bundle = buildSupportFileBundle(allOperations, allModels);
    generateSupportingFiles(files, bundle);
    if (dryRun) {
      boolean verbose = Boolean.parseBoolean(GlobalSettings.getProperty("verbose"));
      StringBuilder sb = new StringBuilder();
      sb.append(System.lineSeparator()).append(System.lineSeparator());
      sb.append("Dry Run Results:");
      sb.append(System.lineSeparator()).append(System.lineSeparator());
      Map<String, DryRunStatus> dryRunStatusMap = ((DryRunTemplateManager) this.templateProcessor).getDryRunStatusMap();
      dryRunStatusMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((entry) -> {
        DryRunStatus status = entry.getValue();
        try {
          status.appendTo(sb);
          sb.append(System.lineSeparator());
          if (verbose) {
            sb.append("  ").append(StringUtils.rightPad(status.getState().getDescription(), 20, ".")).append(" ").append(status.getReason()).append(System.lineSeparator());
          }
        } catch (IOException e) {
          LOGGER.debug("Unable to document dry run status for {}.", entry.getKey());
        }
      });
      sb.append(System.lineSeparator()).append(System.lineSeparator());
      sb.append("States:");
      sb.append(System.lineSeparator()).append(System.lineSeparator());
      for (DryRunStatus.State state : DryRunStatus.State.values()) {
        sb.append("  - ").append(state.getShortDisplay()).append(" ").append(state.getDescription()).append(System.lineSeparator());
      }
      sb.append(System.lineSeparator());
      LOGGER.error(sb.toString());
    } else {
      if (generateSupportingFiles) {
        generateFilesMetadata(files);
      }
    }
    config.postProcess();
    GlobalSettings.reset();
    return files;
  }

  private void processUserDefinedTemplates() {
    if (userDefinedTemplates != null && !userDefinedTemplates.isEmpty()) {
      Map<String, SupportingFile> supportingFilesMap = config.supportingFiles().stream().collect(Collectors.toMap(TemplateDefinition::getTemplateFile, Function.identity(), (oldValue, newValue) -> oldValue));
      userDefinedTemplates.stream().filter((i) -> i.getTemplateType().equals(TemplateFileType.SupportingFiles)).forEach((userDefinedTemplate) -> {
        SupportingFile newFile = new SupportingFile(userDefinedTemplate.getTemplateFile(), userDefinedTemplate.getFolder(), userDefinedTemplate.getDestinationFilename());
        if (supportingFilesMap.containsKey(userDefinedTemplate.getTemplateFile())) {
          SupportingFile f = supportingFilesMap.get(userDefinedTemplate.getTemplateFile());
          config.supportingFiles().remove(f);
          if (!f.isCanOverwrite()) {
            newFile.doNotOverwrite();
          }
        }
        config.supportingFiles().add(newFile);
      });
      userDefinedTemplates.stream().filter((i) -> !i.getTemplateType().equals(TemplateFileType.SupportingFiles)).forEach((userDefinedTemplate) -> {
        String templateFile = userDefinedTemplate.getTemplateFile();
        int lastSeparator = templateFile.lastIndexOf('.');
        String templateExt = FilenameUtils.getExtension(templateFile.substring(0, lastSeparator));
        if (StringUtils.isBlank(templateExt)) {
          templateExt = userDefinedTemplate.getDestinationFilename();
        } else {
          templateExt = StringUtils.prependIfMissing(templateExt, ".");
        }
        switch (userDefinedTemplate.getTemplateType()) {
          case API:
          config.apiTemplateFiles().put(templateFile, templateExt);
          break;
          case Model:
          config.modelTemplateFiles().put(templateFile, templateExt);
          break;
          case APIDocs:
          config.apiDocTemplateFiles().put(templateFile, templateExt);
          break;
          case ModelDocs:
          config.modelDocTemplateFiles().put(templateFile, templateExt);
          break;
          case APITests:
          config.apiTestTemplateFiles().put(templateFile, templateExt);
          break;
          case ModelTests:
          config.modelTestTemplateFiles().put(templateFile, templateExt);
          break;
          case SupportingFiles:
          break;
        }
      });
    }
  }

  protected File processTemplateToFile(Map<String, Object> templateData, String templateName, String outputFilename, boolean shouldGenerate, String skippedByOption) throws IOException {
    return processTemplateToFile(templateData, templateName, outputFilename, shouldGenerate, skippedByOption, this.config.getOutputDir());
  }

  private File processTemplateToFile(Map<String, Object> templateData, String templateName, String outputFilename, boolean shouldGenerate, String skippedByOption, String intendedOutputDir) throws IOException {
    String adjustedOutputFilename = outputFilename.replaceAll("//", "/").replace('/', File.separatorChar);
    File target = new File(adjustedOutputFilename);
    if (ignoreProcessor.allowsFile(target)) {
      if (shouldGenerate) {
        Path outDir = java.nio.file.Paths.get(intendedOutputDir).toAbsolutePath();
        Path absoluteTarget = target.toPath().toAbsolutePath();
        if (!absoluteTarget.startsWith(outDir)) {
          throw new RuntimeException(String.format(Locale.ROOT, "Target files must be generated within the output directory; absoluteTarget=%s outDir=%s", absoluteTarget, outDir));
        }
        return this.templateProcessor.write(templateData, templateName, target);
      } else {
        this.templateProcessor.skip(target.toPath(), String.format(Locale.ROOT, "Skipped by %s options supplied by user.", skippedByOption));
        return null;
      }
    } else {
      this.templateProcessor.ignore(target.toPath(), "Ignored by rule in ignore file.");
      return null;
    }
  }

  public Map<String, List<CodegenOperation>> processPaths(Paths paths) {
    Map<String, List<CodegenOperation>> ops = new TreeMap<>();
    if (paths == null) {
      return ops;
    }
    for (Map.Entry<String, PathItem> pathsEntry : paths.entrySet()) {
      String resourcePath = pathsEntry.getKey();
      PathItem path = pathsEntry.getValue();
      processOperation(resourcePath, "get", path.getGet(), ops, path);
      processOperation(resourcePath, "head", path.getHead(), ops, path);
      processOperation(resourcePath, "put", path.getPut(), ops, path);
      processOperation(resourcePath, "post", path.getPost(), ops, path);
      processOperation(resourcePath, "delete", path.getDelete(), ops, path);
      processOperation(resourcePath, "patch", path.getPatch(), ops, path);
      processOperation(resourcePath, "options", path.getOptions(), ops, path);
      processOperation(resourcePath, "trace", path.getTrace(), ops, path);
    }
    return ops;
  }

  private void processOperation(String resourcePath, String httpMethod, Operation operation, Map<String, List<CodegenOperation>> operations, PathItem path) {
    if (operation == null) {
      return;
    }
    if (GlobalSettings.getProperty("debugOperations") != null) {
      LOGGER.info("processOperation: resourcePath=  {}\t;{} {}\n", resourcePath, httpMethod, operation);
    }
    List<Tag> tags = new ArrayList<>();
    List<String> tagNames = operation.getTags();
    List<Tag> swaggerTags = openAPI.getTags();
    if (tagNames != null) {
      if (swaggerTags == null) {
        for (String tagName : tagNames) {
          tags.add(new Tag().name(tagName));
        }
      } else {
        for (String tagName : tagNames) {
          boolean foundTag = false;
          for (Tag tag : swaggerTags) {
            if (tag.getName().equals(tagName)) {
              tags.add(tag);
              foundTag = true;
              break;
            }
          }
          if (!foundTag) {
            tags.add(new Tag().name(tagName));
          }
        }
      }
    }
    if (tags.isEmpty()) {
      tags.add(new Tag().name("default"));
    }
    Set<String> operationParameters = new HashSet<>();
    if (operation.getParameters() != null) {
      for (Parameter parameter : operation.getParameters()) {
        operationParameters.add(generateParameterId(parameter));
      }
    }
    if (path.getParameters() != null) {
      for (Parameter parameter : path.getParameters()) {
        if (!operationParameters.contains(generateParameterId(parameter))) {
          operation.addParametersItem(parameter);
        }
      }
    }
    final Map<String, SecurityScheme> securitySchemes = openAPI.getComponents() != null ? openAPI.getComponents().getSecuritySchemes() : null;
    final List<SecurityRequirement> globalSecurities = openAPI.getSecurity();
    for (Tag tag : tags) {
      try {
        CodegenOperation codegenOperation = config.fromOperation(resourcePath, httpMethod, operation, path.getServers());
        codegenOperation.tags = new ArrayList<>(tags);
        config.addOperationToGroup(config.sanitizeTag(tag.getName()), resourcePath, operation, codegenOperation, operations);
        List<SecurityRequirement> securities = operation.getSecurity();
        if (securities != null && securities.isEmpty()) {
          continue;
        }
        Map<String, SecurityScheme> authMethods = getAuthMethods(securities, securitySchemes);
        if (authMethods != null && !authMethods.isEmpty()) {
          List<CodegenSecurity> fullAuthMethods = config.fromSecurity(authMethods);
          codegenOperation.authMethods = filterAuthMethods(fullAuthMethods, securities);
          codegenOperation.hasAuthMethods = true;
        } else {
          authMethods = getAuthMethods(globalSecurities, securitySchemes);
          if (authMethods != null && !authMethods.isEmpty()) {
            List<CodegenSecurity> fullAuthMethods = config.fromSecurity(authMethods);
            codegenOperation.authMethods = filterAuthMethods(fullAuthMethods, globalSecurities);
            codegenOperation.hasAuthMethods = true;
          }
        }
      } catch (Exception ex) {
        String msg = "Could not process operation:\n" + "  Tag: " + tag + "\n" + "  Operation: " + operation.getOperationId() + "\n" + "  Resource: " + httpMethod + " " + resourcePath + "\n" + "  Schemas: " + openAPI.getComponents().getSchemas() + "\n" + "  Exception: " + ex.getMessage();
        throw new RuntimeException(msg, ex);
      }
    }
  }

  private static String generateParameterId(Parameter parameter) {
    return parameter.getName() + ":" + parameter.getIn();
  }

  private OperationsMap processOperations(CodegenConfig config, String tag, List<CodegenOperation> ops, List<ModelMap> allModels) {
    OperationsMap operations = new OperationsMap();
    OperationMap objs = new OperationMap();
    objs.setClassname(config.toApiName(tag));
    objs.setPathPrefix(config.toApiVarName(tag));
    if (config.getAddSuffixToDuplicateOperationNicknames()) {
      Set<String> opIds = new HashSet<>();
      int counter = 0;
      for (CodegenOperation op : ops) {
        String opId = op.nickname;
        if (opIds.contains(opId)) {
          counter++;
          op.nickname += "_" + counter;
        }
        opIds.add(opId);
      }
    }
    objs.setOperation(ops);
    operations.setOperation(objs);
    operations.put("package", config.apiPackage());
    Set<String> allImports = new ConcurrentSkipListSet<>();
    for (CodegenOperation op : ops) {
      allImports.addAll(op.imports);
    }
    Map<String, String> mappings = getAllImportsMappings(allImports);
    Set<Map<String, String>> imports = toImportsObjects(mappings);
    operations.setImports(new ArrayList<>(imports));
    if (!imports.isEmpty()) {
      operations.put("hasImport", true);
    }
    config.postProcessOperationsWithModels(operations, allModels);
    return operations;
  }

  /**
     * Transforms a set of imports to a map with key config.toModelImport(import) and value the import string.
     *
     * @param allImports - Set of imports
     * @return Map of fully qualified import path and initial import.
     */
  private Map<String, String> getAllImportsMappings(Set<String> allImports) {
    Map<String, String> result = new HashMap<>();
    allImports.forEach((nextImport) -> {
      String mapping = config.importMapping().get(nextImport);
      if (mapping != null) {
        result.put(mapping, nextImport);
      } else {
        result.putAll(config.toModelImportMap(nextImport));
      }
    });
    return result;
  }

  /**
     * Using an import map created via {@link #getAllImportsMappings(Set)} to build a list import objects.
     * The import objects have two keys: import and classname which hold the key and value of the initial map entry.
     *
     * @param mappedImports Map of fully qualified import and import
     * @return The set of unique imports
     */
  private Set<Map<String, String>> toImportsObjects(Map<String, String> mappedImports) {
    Set<Map<String, String>> result = new TreeSet<>(Comparator.comparing((o) -> o.get("classname")));
    mappedImports.forEach((key, value) -> {
      Map<String, String> im = new LinkedHashMap<>();
      im.put("import", key);
      im.put("classname", value);
      result.add(im);
    });
    return result;
  }

  private ModelsMap processModels(CodegenConfig config, Map<String, Schema> definitions) {
    ModelsMap objs = new ModelsMap();
    objs.put("package", config.modelPackage());
    List<ModelMap> modelMaps = new ArrayList<>();
    Set<String> allImports = new LinkedHashSet<>();
    for (Map.Entry<String, Schema> definitionsEntry : definitions.entrySet()) {
      String key = definitionsEntry.getKey();
      Schema schema = definitionsEntry.getValue();
      if (schema == null) {
        throw new RuntimeException("schema cannot be null in processModels");
      }
      CodegenModel cm = config.fromModel(key, schema);
      ModelMap mo = new ModelMap();
      mo.setModel(cm);
      mo.put("importPath", config.toModelImport(cm.classname));
      modelMaps.add(mo);
      cm.removeSelfReferenceImport();
      allImports.addAll(cm.imports);
    }
    objs.setModels(modelMaps);
    Set<String> importSet = new ConcurrentSkipListSet<>();
    for (String nextImport : allImports) {
      String mapping = config.importMapping().get(nextImport);
      if (mapping == null) {
        mapping = config.toModelImport(nextImport);
      }
      if (mapping != null && !config.defaultIncludes().contains(mapping)) {
        importSet.add(mapping);
      }
      mapping = config.instantiationTypes().get(nextImport);
      if (mapping != null && !config.defaultIncludes().contains(mapping)) {
        importSet.add(mapping);
      }
    }
    List<Map<String, String>> imports = new ArrayList<>();
    for (String s : importSet) {
      Map<String, String> item = new HashMap<>();
      item.put("import", s);
      imports.add(item);
    }
    objs.setImports(imports);
    config.postProcessModels(objs);
    return objs;
  }

  private Map<String, SecurityScheme> getAuthMethods(List<SecurityRequirement> securities, Map<String, SecurityScheme> securitySchemes) {
    if (securities == null || (securitySchemes == null || securitySchemes.isEmpty())) {
      return null;
    }
    final Map<String, SecurityScheme> authMethods = new HashMap<>();
    for (SecurityRequirement requirement : securities) {
      for (Map.Entry<String, List<String>> entry : requirement.entrySet()) {
        final String key = entry.getKey();
        SecurityScheme securityScheme = securitySchemes.get(key);
        if (securityScheme != null) {
          if (securityScheme.getType().equals(SecurityScheme.Type.OAUTH2)) {
            OAuthFlows oauthUpdatedFlows = new OAuthFlows();
            oauthUpdatedFlows.extensions(securityScheme.getFlows().getExtensions());
            SecurityScheme oauthUpdatedScheme = new SecurityScheme().type(securityScheme.getType()).description(securityScheme.getDescription()).name(securityScheme.getName()).$ref(securityScheme.get$ref()).in(securityScheme.getIn()).scheme(securityScheme.getScheme()).bearerFormat(securityScheme.getBearerFormat()).openIdConnectUrl(securityScheme.getOpenIdConnectUrl()).extensions(securityScheme.getExtensions()).flows(oauthUpdatedFlows);
            final OAuthFlows securitySchemeFlows = securityScheme.getFlows();
            if (securitySchemeFlows.getAuthorizationCode() != null) {
              OAuthFlow updatedFlow = cloneOAuthFlow(securitySchemeFlows.getAuthorizationCode(), entry.getValue());
              oauthUpdatedFlows.setAuthorizationCode(updatedFlow);
            }
            if (securitySchemeFlows.getImplicit() != null) {
              OAuthFlow updatedFlow = cloneOAuthFlow(securitySchemeFlows.getImplicit(), entry.getValue());
              oauthUpdatedFlows.setImplicit(updatedFlow);
            }
            if (securitySchemeFlows.getPassword() != null) {
              OAuthFlow updatedFlow = cloneOAuthFlow(securitySchemeFlows.getPassword(), entry.getValue());
              oauthUpdatedFlows.setPassword(updatedFlow);
            }
            if (securitySchemeFlows.getClientCredentials() != null) {
              OAuthFlow updatedFlow = cloneOAuthFlow(securitySchemeFlows.getClientCredentials(), entry.getValue());
              oauthUpdatedFlows.setClientCredentials(updatedFlow);
            }
            authMethods.put(key, oauthUpdatedScheme);
          } else {
            authMethods.put(key, securityScheme);
          }
        }
      }
    }
    return authMethods;
  }

  private static OAuthFlow cloneOAuthFlow(OAuthFlow originFlow, List<String> operationScopes) {
    Scopes newScopes = new Scopes();
    for (String operationScope : operationScopes) {
      if (originFlow.getScopes().containsKey(operationScope)) {
        newScopes.put(operationScope, originFlow.getScopes().get(operationScope));
      }
    }
    return new OAuthFlow().authorizationUrl(originFlow.getAuthorizationUrl()).tokenUrl(originFlow.getTokenUrl()).refreshUrl(originFlow.getRefreshUrl()).extensions(originFlow.getExtensions()).scopes(newScopes);
  }

  private List<CodegenSecurity> filterAuthMethods(List<CodegenSecurity> authMethods, List<SecurityRequirement> securities) {
    if (securities == null || securities.isEmpty() || authMethods == null) {
      return authMethods;
    }
    List<CodegenSecurity> result = new ArrayList<>();
    for (CodegenSecurity security : authMethods) {
      boolean filtered = false;
      if (security != null && security.scopes != null) {
        for (SecurityRequirement requirement : securities) {
          List<String> opScopes = requirement.get(security.name);
          if (opScopes != null) {
            CodegenSecurity opSecurity = security.filterByScopeNames(opScopes);
            result.add(opSecurity);
            filtered = true;
            break;
          }
        }
      }
      if (!filtered) {
        result.add(security);
      }
    }
    return result;
  }

  /**
     * Generates a file at .openapi-generator/VERSION to track the version of user's latest run.
     *
     * @param files The list tracking generated files
     */
  private void generateVersionMetadata(List<File> files) {
    String versionMetadata = config.outputFolder() + File.separator + METADATA_DIR + File.separator + config.getVersionMetadataFilename();
    if (generateMetadata) {
      File versionMetadataFile = new File(versionMetadata);
      try {
        File written = this.templateProcessor.writeToFile(versionMetadata, ImplementationVersion.read().getBytes(StandardCharsets.UTF_8));
        if (written != null) {
          files.add(versionMetadataFile);
          if (config.isEnablePostProcessFile() && !dryRun) {
            config.postProcessFile(written, "openapi-generator-version");
          }
        }
      } catch (IOException e) {
        throw new RuntimeException("Could not generate supporting file \'" + versionMetadata + "\'", e);
      }
    } else {
      Path metadata = java.nio.file.Paths.get(versionMetadata);
      this.templateProcessor.skip(metadata, "Skipped by generateMetadata option supplied by user.");
    }
  }

  private Path absPath(File input) {
    return java.nio.file.Paths.get(input.getAbsolutePath());
  }

  /**
     * Generates a file at .openapi-generator/FILES to track the files created by the user's latest run.
     * This is ideal for CI and regeneration of code without stale/unused files from older generations.
     *
     * @param files The list tracking generated files
     */
  private void generateFilesMetadata(List<File> files) {
    if (generateMetadata) {
      try {
        StringBuilder sb = new StringBuilder();
        Path outDir = absPath(new File(this.config.getOutputDir()));
        List<File> filesToSort = new ArrayList<>();
        files.forEach((f) -> {
          if (f != null && f.getPath() != null) {
            filesToSort.add(outDir.relativize(absPath(f)).normalize().toFile());
          }
        });
        String relativeMeta = METADATA_DIR + "/VERSION";
        filesToSort.sort(PathFileComparator.PATH_COMPARATOR);
        filesToSort.forEach((f) -> {
          String relativePath = removeStart(removeStart(f.toString(), "." + File.separator), File.separator);
          if (File.separator.equals("\\")) {
            relativePath = relativePath.replace(File.separator, "/");
          }
          if (!relativePath.equals(relativeMeta)) {
            sb.append(relativePath).append(System.lineSeparator());
          }
        });
        String targetFile = config.outputFolder() + File.separator + METADATA_DIR + File.separator + config.getFilesMetadataFilename();
        File filesFile = this.templateProcessor.writeToFile(targetFile, sb.toString().getBytes(StandardCharsets.UTF_8));
        if (filesFile != null) {
          files.add(filesFile);
        }
      } catch (Exception e) {
        LOGGER.warn("Failed to write FILES metadata to track generated files.");
      }
    }
  }

  private String removeTrailingSlash(String value) {
    return StringUtils.removeEnd(value, "/");
  }
}