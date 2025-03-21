package io.swagger.client.auth;
import io.swagger.client.Pair;
import java.util.Map;
import java.util.List;


<<<<<<< Unknown file: This is a bug in JDime.
=======
@javax.annotation.Generated(value = { "class io.swagger.codegen.languages.JavaClientCodegen" }, date = "2016-06-09T08:56:08.812+02:00")
>>>>>>> /usr/src/app/output/wordnik/swagger-codegen/dbadd9a831cb04e211266b2b1538e8f2c0a13376/samples/client/petstore/java/okhttp-gson/src/main/java/io/swagger/client/auth/OAuth.java/right.java
 public class OAuth implements Authentication {
  private String accessToken;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @Override public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams) {
    if (accessToken != null) {
      headerParams.put("Authorization", "Bearer " + accessToken);
    }
  }
}