package org.openapitools.codegen.languages;
import io.swagger.models.Path;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.*;
import org.openapitools.codegen.meta.features.DocumentationFeature;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;

public class JavaMSF4JServerCodegen extends AbstractJavaJAXRSServerCodegen {
  protected static final String LIBRARY_JERSEY1 = "jersey1";

  protected static final String LIBRARY_JERSEY2 = "jersey2";

  public static final String DEFAULT_MSF4J_LIBRARY = LIBRARY_JERSEY2;

  public JavaMSF4JServerCodegen() {
    super();
    modifyFeatureSet((features) -> features.includeDocumentationFeatures(DocumentationFeature.Readme));
    outputFolder = "generated-code/JavaJaxRS-MSF4J";
    apiTemplateFiles.put("apiService.mustache", ".java");
    apiTemplateFiles.put("apiServiceImpl.mustache", ".java");
    apiTemplateFiles.put("apiServiceFactory.mustache", ".java");
    apiTestTemplateFiles.clear();
    modelDocTemplateFiles.remove("model_doc.mustache");
    apiDocTemplateFiles.remove("api_doc.mustache");
    embeddedTemplateDir = templateDir = "java-msf4j-server";
    CliOption library = new CliOption(CodegenConstants.LIBRARY, CodegenConstants.LIBRARY_DESC);
    supportedLibraries.put(LIBRARY_JERSEY1, "Jersey core 1.x");
    supportedLibraries.put(LIBRARY_JERSEY2, "Jersey core 2.x");
    library.setEnum(supportedLibraries);
    library.setDefault(DEFAULT_MSF4J_LIBRARY);
    cliOptions.add(library);
  }

  @Override public String getName() {
    return "java-msf4j";
  }

  @Override public String getHelp() {
    return "Generates a Java Micro Service based on WSO2 Microservices Framework for Java (MSF4J)";
  }

  @Override public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
    super.postProcessModelProperty(model, property);
    if ("null".equals(property.example)) {
      property.example = null;
    }
    if (!BooleanUtils.toBoolean(model.isEnum)) {
      model.imports.add("JsonProperty");
      if (BooleanUtils.toBoolean(model.hasEnums)) {
        model.imports.add("JsonValue");
      }
    }
  }

  @Override public void processOpts() {
    super.processOpts();
    if (StringUtils.isEmpty(library)) {
      setLibrary(DEFAULT_MSF4J_LIBRARY);
    }
    if (additionalProperties.containsKey(CodegenConstants.IMPL_FOLDER)) {
      implFolder = (String) additionalProperties.get(CodegenConstants.IMPL_FOLDER);
    }
    if ("joda".equals(dateLibrary)) {
      supportingFiles.add(new SupportingFile("JodaDateTimeProvider.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "JodaDateTimeProvider.java"));
      supportingFiles.add(new SupportingFile("JodaLocalDateProvider.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "JodaLocalDateProvider.java"));
    } else {
      if (dateLibrary.startsWith("java8")) {
        supportingFiles.add(new SupportingFile("OffsetDateTimeProvider.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "OffsetDateTimeProvider.java"));
        supportingFiles.add(new SupportingFile("LocalDateProvider.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "LocalDateProvider.java"));
      }
    }
    supportingFiles.add(new SupportingFile("pom.mustache", "", "pom.xml").doNotOverwrite());
    supportingFiles.add(new SupportingFile("README.mustache", "", "README.md").doNotOverwrite());
    supportingFiles.add(new SupportingFile("ApiException.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "ApiException.java"));
    supportingFiles.add(new SupportingFile("ApiOriginFilter.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "ApiOriginFilter.java"));
    supportingFiles.add(new SupportingFile("ApiResponseMessage.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "ApiResponseMessage.java"));
    supportingFiles.add(new SupportingFile("NotFoundException.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "NotFoundException.java"));
    supportingFiles.add(new SupportingFile("jacksonJsonProvider.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "JacksonJsonProvider.java"));
    supportingFiles.add(new SupportingFile("RFC3339DateFormat.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "RFC3339DateFormat.java"));
    supportingFiles.add(new SupportingFile("StringUtil.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "StringUtil.java"));
    supportingFiles.add(new SupportingFile("Application.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "Application.java"));
  }

  @Override public void preprocessOpenAPI(OpenAPI openAPI) {
    super.preprocessOpenAPI(openAPI);
    if (openAPI.getPaths() != null) {
      for (String pathname : openAPI.getPaths().keySet()) {
        boolean dependencies = false;
        PathItem path = openAPI.getPaths().get(pathname);
        if (path.readOperations() != null) {
          for (Operation operation : path.readOperations()) {
            if (operation.getExtensions() != null && operation.getExtensions().containsKey("x-dependencies")) {
              supportingFiles.add(new SupportingFile("DependencyUtil.mustache", (sourceFolder + '/' + apiPackage).replace(".", "/"), "DependencyUtil.java"));
              dependencies = true;
              break;
            }
          }
        }
        if (dependencies) {
          break;
        }
      }
    }
  }

  @Override public ModelsMap postProcessModelsEnum(ModelsMap objs) {
    objs = super.postProcessModelsEnum(objs);
    List<Map<String, String>> imports = objs.getImports();
    for (ModelMap mo : objs.getModels()) {
      CodegenModel cm = mo.getModel();
      if (Boolean.TRUE.equals(cm.isEnum) && cm.allowableValues != null) {
        cm.imports.add(importMapping.get("JsonValue"));
        Map<String, String> item = new HashMap<String, String>();
        item.put("import", importMapping.get("JsonValue"));
        imports.add(item);
      }
    }
    return objs;
  }
}