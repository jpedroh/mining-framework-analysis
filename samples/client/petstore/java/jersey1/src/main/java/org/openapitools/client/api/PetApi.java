package org.openapitools.client.api;
import com.sun.jersey.api.client.GenericType;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;
import org.openapitools.client.model.*;
import org.openapitools.client.Pair;
import java.io.File;
import org.openapitools.client.model.ModelApiResponse;
import org.openapitools.client.model.Pet;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = { "org.openapitools.codegen.languages.JavaClientCodegen" }) public class PetApi {
  private ApiClient apiClient;

  public PetApi() {
    this(Configuration.getDefaultApiClient());
  }

  public PetApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void addPet(Pet body) throws ApiException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new ApiException(400, "Missing the required parameter \'body\' when calling addPet");
    }
    String localVarPath = "/pet";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json", "application/xml" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void deletePet(Long petId, String apiKey) throws ApiException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new ApiException(400, "Missing the required parameter \'petId\' when calling deletePet");
    }
    String localVarPath = "/pet/{petId}".replaceAll("\\{" + "petId" + "\\}", apiClient.escapeString(petId.toString()));
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    if (apiKey != null) {
      localVarHeaderParams.put("api_key", apiClient.parameterToString(apiKey));
    }
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public List<Pet> findPetsByStatus(List<String> status) throws ApiException {
    Object localVarPostBody = null;
    if (status == null) {
      throw new ApiException(400, "Missing the required parameter \'status\' when calling findPetsByStatus");
    }
    String localVarPath = "/pet/findByStatus";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("csv", "status", status));
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    GenericType<List<Pet>> localVarReturnType = new GenericType<List<Pet>>() { };
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  @Deprecated public Set<Pet> findPetsByTags(Set<String> tags) throws ApiException {
    Object localVarPostBody = null;
    if (tags == null) {
      throw new ApiException(400, "Missing the required parameter \'tags\' when calling findPetsByTags");
    }
    String localVarPath = "/pet/findByTags";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("csv", "tags", tags));
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    GenericType<Set<Pet>> localVarReturnType = new GenericType<Set<Pet>>() { };
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public Pet getPetById(Long petId) throws ApiException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new ApiException(400, "Missing the required parameter \'petId\' when calling getPetById");
    }
    String localVarPath = "/pet/{petId}".replaceAll("\\{" + "petId" + "\\}", apiClient.escapeString(petId.toString()));
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "api_key" };
    GenericType<Pet> localVarReturnType = new GenericType<Pet>() { };
    return apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public void updatePet(Pet body) throws ApiException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new ApiException(400, "Missing the required parameter \'body\' when calling updatePet");
    }
    String localVarPath = "/pet";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json", "application/xml" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    apiClient.invokeAPI(localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void updatePetWithForm(Long petId, String name, String status) throws ApiException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new ApiException(400, "Missing the required parameter \'petId\' when calling updatePetWithForm");
    }
    String localVarPath = "/pet/{petId}".replaceAll("\\{" + "petId" + "\\}", apiClient.escapeString(petId.toString()));
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    if (name != null) {
      localVarFormParams.put("name", name);
    }
    if (status != null) {
      localVarFormParams.put("status", status);
    }
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public ModelApiResponse uploadFile(Long petId, String additionalMetadata, File _file) throws ApiException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new ApiException(400, "Missing the required parameter \'petId\' when calling uploadFile");
    }
    String localVarPath = "/pet/{petId}/uploadImage".replaceAll("\\{" + "petId" + "\\}", apiClient.escapeString(petId.toString()));
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    if (additionalMetadata != null) {
      localVarFormParams.put("additionalMetadata", additionalMetadata);
    }
    if (_file != null) {
      localVarFormParams.put("file", _file);
    }
    final String[] localVarAccepts = { "application/json" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "multipart/form-data" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    GenericType<ModelApiResponse> localVarReturnType = new GenericType<ModelApiResponse>() { };
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public ModelApiResponse uploadFileWithRequiredFile(Long petId, File requiredFile, String additionalMetadata) throws ApiException {
    Object localVarPostBody = null;
    if (petId == null) {
      throw new ApiException(400, "Missing the required parameter \'petId\' when calling uploadFileWithRequiredFile");
    }
    if (requiredFile == null) {
      throw new ApiException(400, "Missing the required parameter \'requiredFile\' when calling uploadFileWithRequiredFile");
    }
    String localVarPath = "/fake/{petId}/uploadImageWithRequiredFile".replaceAll("\\{" + "petId" + "\\}", apiClient.escapeString(petId.toString()));
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    if (additionalMetadata != null) {
      localVarFormParams.put("additionalMetadata", additionalMetadata);
    }
    if (requiredFile != null) {
      localVarFormParams.put("requiredFile", requiredFile);
    }
    final String[] localVarAccepts = { "application/json" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "multipart/form-data" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "petstore_auth" };
    GenericType<ModelApiResponse> localVarReturnType = new GenericType<ModelApiResponse>() { };
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }
}