package io.swagger.client.auth;

import io.swagger.client.Pair;

import java.util.Map;
import java.util.List;

<<<<<<< /usr/src/app/output/wordnik/swagger-codegen/55bab1f6b6dd20f44c7d16e1a5f74d1ceef0134c/samples/client/petstore/java/okhttp-gson/src/main/java/io/swagger/client/auth/ApiKeyAuth.java/left.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2015-11-25T15:16:28.364+08:00")
||||||| /usr/src/app/output/wordnik/swagger-codegen/55bab1f6b6dd20f44c7d16e1a5f74d1ceef0134c/samples/client/petstore/java/okhttp-gson/src/main/java/io/swagger/client/auth/ApiKeyAuth.java/base.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2015-10-20T11:42:25.339-07:00")
=======
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2015-11-20T17:28:54.086+08:00")
>>>>>>> /usr/src/app/output/wordnik/swagger-codegen/55bab1f6b6dd20f44c7d16e1a5f74d1ceef0134c/samples/client/petstore/java/okhttp-gson/src/main/java/io/swagger/client/auth/ApiKeyAuth.java/right.java
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

  @Override
  public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams) {
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
    } else if (location == "header") {
      headerParams.put(paramName, value);
    }
  }
}
