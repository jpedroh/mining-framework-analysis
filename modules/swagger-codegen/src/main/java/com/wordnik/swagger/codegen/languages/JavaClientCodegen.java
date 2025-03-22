package com.wordnik.swagger.codegen.languages;
import com.wordnik.swagger.codegen.*;
import com.wordnik.swagger.models.properties.*;
import java.util.*;
import java.io.File;

public class JavaClientCodegen extends DefaultCodegen implements CodegenConfig {
  protected String invokerPackage = "io.swagger.client";

  protected String groupId = "io.swagger";

  protected String artifactId = "swagger-java-client";

  protected String artifactVersion = "1.0.0";

  protected String sourceFolder = "src/main/java";

  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  public String getName() {
    return "java";
  }

  public String getHelp() {
    return "Generates a Java client library.";
  }

  public JavaClientCodegen() {
    super();
    outputFolder = "generated-code/java";
    modelTemplateFiles.put("model.mustache", ".java");
    apiTemplateFiles.put("api.mustache", ".java");
    templateDir = "Java";
    apiPackage = "io.swagger.client.api";
    modelPackage = "io.swagger.client.model";
    reservedWords = new HashSet<String>(Arrays.asList("abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package", "synchronized", "boolean", "do", "goto", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"));
    languageSpecificPrimitives = new HashSet<String>(Arrays.asList("String", "boolean", "Boolean", "Double", "Integer", "Long", "Float", "Object"));
    instantiationTypes.put("array", "ArrayList");
    instantiationTypes.put("map", "HashMap");
    final String invokerFolder = (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator);
    final String authFolder = (sourceFolder + File.separator + invokerPackage + ".auth").replace(".", java.io.File.separator);

<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles.add(new SupportingFile("apiInvoker.mustache", invokerFolder, "ApiInvoker.java"));
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles.add(new SupportingFile("JsonUtil.mustache", invokerFolder, "JsonUtil.java"));
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles.add(new SupportingFile("apiException.mustache", invokerFolder, "ApiException.java"));
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles
=======
    cliOptions
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    .add(new 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    SupportingFile
=======
    CliOption
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    (
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    "configuration.mustache"
=======
    "invokerPackage"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    invokerFolder
=======
    "root package for generated code"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , "Configuration.java"));

<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles
=======
    cliOptions
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    .add(new 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    SupportingFile
=======
    CliOption
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    (
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    "auth/Authentication.mustache"
=======
    "groupId"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    authFolder
=======
    "groupId in generated pom.xml"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , "Authentication.java"));

<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles
=======
    cliOptions
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    .add(new 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    SupportingFile
=======
    CliOption
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    (
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    "auth/HttpBasicAuth.mustache"
=======
    "artifactId"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    authFolder
=======
    "artifactId in generated pom.xml"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , "HttpBasicAuth.java"));

<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles
=======
    cliOptions
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    .add(new 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    SupportingFile
=======
    CliOption
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    (
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    "auth/ApiKeyAuth.mustache"
=======
    "artifactVersion"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    authFolder
=======
    "artifact version in generated pom.xml"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , "ApiKeyAuth.java"));

<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    supportingFiles
=======
    cliOptions
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    .add(new 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    SupportingFile
=======
    CliOption
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    (
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    "auth/OAuth.mustache"
=======
    "sourceFolder"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/left.java
    authFolder
=======
    "source folder for generated code"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/947935f3d9d27f942ce540500ca58c3b13891909/modules/swagger-codegen/src/main/java/com/wordnik/swagger/codegen/languages/JavaClientCodegen.java/right.java
    , "OAuth.java"));
  }

  @Override public void processOpts() {
    super.processOpts();
    if (additionalProperties.containsKey("invokerPackage")) {
      this.setInvokerPackage((String) additionalProperties.get("invokerPackage"));
    } else {
      additionalProperties.put("invokerPackage", invokerPackage);
    }
    if (additionalProperties.containsKey("groupId")) {
      this.setGroupId((String) additionalProperties.get("groupId"));
    } else {
      additionalProperties.put("groupId", groupId);
    }
    if (additionalProperties.containsKey("artifactId")) {
      this.setArtifactId((String) additionalProperties.get("artifactId"));
    } else {
      additionalProperties.put("artifactId", artifactId);
    }
    if (additionalProperties.containsKey("artifactVersion")) {
      this.setArtifactVersion((String) additionalProperties.get("artifactVersion"));
    } else {
      additionalProperties.put("artifactVersion", artifactVersion);
    }
    if (additionalProperties.containsKey("sourceFolder")) {
      this.setSourceFolder((String) additionalProperties.get("sourceFolder"));
    }
    supportingFiles.add(new SupportingFile("pom.mustache", "", "pom.xml"));
    supportingFiles.add(new SupportingFile("ApiClient.mustache", (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator), "ApiClient.java"));
    supportingFiles.add(new SupportingFile("Configuration.mustache", (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator), "Configuration.java"));
    supportingFiles.add(new SupportingFile("JsonUtil.mustache", (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator), "JsonUtil.java"));
    supportingFiles.add(new SupportingFile("apiException.mustache", (sourceFolder + File.separator + invokerPackage).replace(".", java.io.File.separator), "ApiException.java"));
  }

  @Override public String escapeReservedWord(String name) {
    return "_" + name;
  }

  @Override public String apiFileFolder() {
    return outputFolder + "/" + sourceFolder + "/" + apiPackage().replace('.', File.separatorChar);
  }

  public String modelFileFolder() {
    return outputFolder + "/" + sourceFolder + "/" + modelPackage().replace('.', File.separatorChar);
  }

  @Override public String toVarName(String name) {
    name = name.replaceAll("-", "_");
    if (name.matches("^[A-Z_]*$")) {
      return name;
    }
    name = camelize(name, true);
    if (reservedWords.contains(name) || name.matches("^\\d.*")) {
      name = escapeReservedWord(name);
    }
    return name;
  }

  @Override public String toParamName(String name) {
    return toVarName(name);
  }

  @Override public String toModelName(String name) {
    if (reservedWords.contains(name)) {
      throw new RuntimeException(name + " (reserved word) cannot be used as a model name");
    }
    return camelize(name);
  }

  @Override public String toModelFilename(String name) {
    return toModelName(name);
  }

  @Override public String getTypeDeclaration(Property p) {
    if (p instanceof ArrayProperty) {
      ArrayProperty ap = (ArrayProperty) p;
      Property inner = ap.getItems();
      return getSwaggerType(p) + "<" + getTypeDeclaration(inner) + ">";
    } else {
      if (p instanceof MapProperty) {
        MapProperty mp = (MapProperty) p;
        Property inner = mp.getAdditionalProperties();
        return getSwaggerType(p) + "<String, " + getTypeDeclaration(inner) + ">";
      }
    }
    return super.getTypeDeclaration(p);
  }

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

  @Override public String toOperationId(String operationId) {
    if (reservedWords.contains(operationId)) {
      throw new RuntimeException(operationId + " (reserved word) cannot be used as method name");
    }
    return camelize(operationId, true);
  }

  public void setInvokerPackage(String invokerPackage) {
    this.invokerPackage = invokerPackage;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public void setArtifactVersion(String artifactVersion) {
    this.artifactVersion = artifactVersion;
  }

  public void setSourceFolder(String sourceFolder) {
    this.sourceFolder = sourceFolder;
  }
}