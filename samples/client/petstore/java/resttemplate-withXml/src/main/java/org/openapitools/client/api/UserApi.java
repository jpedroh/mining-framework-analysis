package org.openapitools.client.api;
import org.openapitools.client.ApiClient;
import org.openapitools.client.model.User;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.time.OffsetDateTime;

@javax.annotation.Generated(value = { "org.openapitools.codegen.languages.JavaClientCodegen" }) @Component(value = "org.openapitools.client.api.UserApi") public class UserApi {
  private ApiClient apiClient;

  public UserApi() {
    this(new ApiClient());
  }

  @Autowired public UserApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void createUser(User body) throws RestClientException {
    createUserWithHttpInfo(body);
  }

  public ResponseEntity<Void> createUserWithHttpInfo(User body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling createUser");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/user", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void createUsersWithArrayInput(List<User> body) throws RestClientException {
    createUsersWithArrayInputWithHttpInfo(body);
  }

  public ResponseEntity<Void> createUsersWithArrayInputWithHttpInfo(List<User> body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling createUsersWithArrayInput");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/user/createWithArray", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void createUsersWithListInput(List<User> body) throws RestClientException {
    createUsersWithListInputWithHttpInfo(body);
  }

  public ResponseEntity<Void> createUsersWithListInputWithHttpInfo(List<User> body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling createUsersWithListInput");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/user/createWithList", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void deleteUser(String username) throws RestClientException {
    deleteUserWithHttpInfo(username);
  }

  public ResponseEntity<Void> deleteUserWithHttpInfo(String username) throws RestClientException {
    Object localVarPostBody = null;
    if (username == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'username\' when calling deleteUser");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("username", username);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/user/{username}", HttpMethod.DELETE, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public User getUserByName(String username) throws RestClientException {
    return getUserByNameWithHttpInfo(username).getBody();
  }

  public ResponseEntity<User> getUserByNameWithHttpInfo(String username) throws RestClientException {
    Object localVarPostBody = null;
    if (username == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'username\' when calling getUserByName");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("username", username);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<User> localReturnType = new ParameterizedTypeReference<User>() { };
    return apiClient.invokeAPI("/user/{username}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public String loginUser(String username, String password) throws RestClientException {
    return loginUserWithHttpInfo(username, password).getBody();
  }

  public ResponseEntity<String> loginUserWithHttpInfo(String username, String password) throws RestClientException {
    Object localVarPostBody = null;
    if (username == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'username\' when calling loginUser");
    }
    if (password == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'password\' when calling loginUser");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "username", username));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "password", password));
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<String> localReturnType = new ParameterizedTypeReference<String>() { };
    return apiClient.invokeAPI("/user/login", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void logoutUser() throws RestClientException {
    logoutUserWithHttpInfo();
  }

  public ResponseEntity<Void> logoutUserWithHttpInfo() throws RestClientException {
    Object localVarPostBody = null;
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/user/logout", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void updateUser(String username, User body) throws RestClientException {
    updateUserWithHttpInfo(username, body);
  }

  public ResponseEntity<Void> updateUserWithHttpInfo(String username, User body) throws RestClientException {
    Object localVarPostBody = body;
    if (username == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'username\' when calling updateUser");
    }
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling updateUser");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("username", username);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/user/{username}", HttpMethod.PUT, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }
}