package io.swagger.client.auth;
import io.swagger.client.Pair;
import java.util.Map;
import java.util.List;


<<<<<<< Unknown file: This is a bug in JDime.
=======
@javax.annotation.Generated(value = { "class io.swagger.codegen.languages.JavaClientCodegen" }, date = "2016-06-09T08:56:08.812+02:00")
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/bc42d941ec8c184829816c05236cf58f5c59a3ff/samples/client/petstore/java/okhttp-gson/src/main/java/io/swagger/client/auth/ApiKeyAuth.java/right.java
 public class ApiKeyAuth implements Authentication {
  private final String location;

  private final String paramName;

  private String apiKey;

  private String apiKeyPrefix;

  public ApiKeyAuth(String location, String paramName) {
    this.location = location;
    this.paramName = paramName;
  }

  public String getLocation() {
    return location;
  }

  public String getParamName() {
    return paramName;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getApiKeyPrefix() {
    return apiKeyPrefix;
  }

  public void setApiKeyPrefix(String apiKeyPrefix) {
    this.apiKeyPrefix = apiKeyPrefix;
  }

  @Override public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams) {
    if (apiKey == null) {
      return;
    }
    String value;
    if (apiKeyPrefix != null) {
      value = apiKeyPrefix + " " + apiKey;
    } else {
      value = apiKey;
    }
    if (location == "query") {
      queryParams.add(new Pair(paramName, value));
    } else {
      if (location == "header") {
        headerParams.put(paramName, value);
      }
    }
  }
}