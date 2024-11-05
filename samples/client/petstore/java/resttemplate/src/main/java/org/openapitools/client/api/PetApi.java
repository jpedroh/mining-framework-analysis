package org.openapitools.client.api;
import org.openapitools.client.ApiClient;
import java.io.File;
import org.openapitools.client.model.ModelApiResponse;
import org.openapitools.client.model.Pet;
import java.util.Set;
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

@javax.annotation.Generated(value = { "org.openapitools.codegen.languages.JavaClientCodegen" }) @Component(value = "org.openapitools.client.api.PetApi") public class PetApi {
  private ApiClient apiClient;

  public PetApi() {
    this(new ApiClient());
  }

  @Autowired public PetApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void addPet(Pet body) throws RestClientException {
    addPetWithHttpInfo(body);
  }

  public ResponseEntity<Void> addPetWithHttpInfo(Pet body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling addPet");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json", "application/xml" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/pet", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void deletePet(Long petId, String apiKey) throws RestClientException {
    deletePetWithHttpInfo(petId, apiKey);
  }

  public ResponseEntity<Void> deletePetWithHttpInfo(Long petId, String apiKey) throws RestClientException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'petId\' when calling deletePet");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("petId", petId);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    if (apiKey != null) {
      localVarHeaderParams.add("api_key", apiClient.parameterToString(apiKey));
    }
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/pet/{petId}", HttpMethod.DELETE, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public List<Pet> findPetsByStatus(List<String> status) throws RestClientException {
    return findPetsByStatusWithHttpInfo(status).getBody();
  }

  public ResponseEntity<List<Pet>> findPetsByStatusWithHttpInfo(List<String> status) throws RestClientException {
    Object localVarPostBody = null;
    if (status == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'status\' when calling findPetsByStatus");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "status", status));
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<List<Pet>> localReturnType = new ParameterizedTypeReference<List<Pet>>() { };
    return apiClient.invokeAPI("/pet/findByStatus", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  @Deprecated public Set<Pet> findPetsByTags(Set<String> tags) throws RestClientException {
    return findPetsByTagsWithHttpInfo(tags).getBody();
  }

  @Deprecated public ResponseEntity<Set<Pet>> findPetsByTagsWithHttpInfo(Set<String> tags) throws RestClientException {
    Object localVarPostBody = null;
    if (tags == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'tags\' when calling findPetsByTags");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "tags", tags));
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<Set<Pet>> localReturnType = new ParameterizedTypeReference<Set<Pet>>() { };
    return apiClient.invokeAPI("/pet/findByTags", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public Pet getPetById(Long petId) throws RestClientException {
    return getPetByIdWithHttpInfo(petId).getBody();
  }

  public ResponseEntity<Pet> getPetByIdWithHttpInfo(Long petId) throws RestClientException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'petId\' when calling getPetById");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("petId", petId);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "api_key" };
    ParameterizedTypeReference<Pet> localReturnType = new ParameterizedTypeReference<Pet>() { };
    return apiClient.invokeAPI("/pet/{petId}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void updatePet(Pet body) throws RestClientException {
    updatePetWithHttpInfo(body);
  }

  public ResponseEntity<Void> updatePetWithHttpInfo(Pet body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling updatePet");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json", "application/xml" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/pet", HttpMethod.PUT, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void updatePetWithForm(Long petId, String name, String status) throws RestClientException {
    updatePetWithFormWithHttpInfo(petId, name, status);
  }

  public ResponseEntity<Void> updatePetWithFormWithHttpInfo(Long petId, String name, String status) throws RestClientException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'petId\' when calling updatePetWithForm");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("petId", petId);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    if (name != null) {
      localVarFormParams.add("name", name);
    }
    if (status != null) {
      localVarFormParams.add("status", status);
    }
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/pet/{petId}", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public ModelApiResponse uploadFile(Long petId, String additionalMetadata, File _file) throws RestClientException {
    return uploadFileWithHttpInfo(petId, additionalMetadata, _file).getBody();
  }

  public ResponseEntity<ModelApiResponse> uploadFileWithHttpInfo(Long petId, String additionalMetadata, File _file) throws RestClientException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'petId\' when calling uploadFile");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("petId", petId);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    if (additionalMetadata != null) {
      localVarFormParams.add("additionalMetadata", additionalMetadata);
    }
    if (_file != null) {
      localVarFormParams.add("file", new FileSystemResource(_file));
    }
    final String[] localVarAccepts = { "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "multipart/form-data" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<ModelApiResponse> localReturnType = new ParameterizedTypeReference<ModelApiResponse>() { };
    return apiClient.invokeAPI("/pet/{petId}/uploadImage", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public ModelApiResponse uploadFileWithRequiredFile(Long petId, File requiredFile, String additionalMetadata) throws RestClientException {
    return uploadFileWithRequiredFileWithHttpInfo(petId, requiredFile, additionalMetadata).getBody();
  }

  public ResponseEntity<ModelApiResponse> uploadFileWithRequiredFileWithHttpInfo(Long petId, File requiredFile, String additionalMetadata) throws RestClientException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'petId\' when calling uploadFileWithRequiredFile");
    }
    if (requiredFile == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'requiredFile\' when calling uploadFileWithRequiredFile");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("petId", petId);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    if (additionalMetadata != null) {
      localVarFormParams.add("additionalMetadata", additionalMetadata);
    }
    if (requiredFile != null) {
      localVarFormParams.add("requiredFile", new FileSystemResource(requiredFile));
    }
    final String[] localVarAccepts = { "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "multipart/form-data" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    ParameterizedTypeReference<ModelApiResponse> localReturnType = new ParameterizedTypeReference<ModelApiResponse>() { };
    return apiClient.invokeAPI("/fake/{petId}/uploadImageWithRequiredFile", HttpMethod.POST, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }
}