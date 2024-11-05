package org.openapitools.client.api;
import org.openapitools.client.ApiClient;
import org.openapitools.client.model.Order;
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

@javax.annotation.Generated(value = { "org.openapitools.codegen.languages.JavaClientCodegen" }) @Component(value = "org.openapitools.client.api.StoreApi") public class StoreApi {
  private ApiClient apiClient;

  public StoreApi() {
    this(new ApiClient());
  }

  @Autowired public StoreApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void deleteOrder(String orderId) throws RestClientException {
    deleteOrderWithHttpInfo(orderId);
  }

  public ResponseEntity<Void> deleteOrderWithHttpInfo(String orderId) throws RestClientException {
    Object localVarPostBody = null;
    if (orderId == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'orderId\' when calling deleteOrder");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("order_id", orderId);
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
    return apiClient.invokeAPI("/store/order/{order_id}", HttpMethod.DELETE, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public Map<String, Integer> getInventory() throws RestClientException {
    return getInventoryWithHttpInfo().getBody();
  }

  public ResponseEntity<Map<String, Integer>> getInventoryWithHttpInfo() throws RestClientException {
    Object localVarPostBody = null;
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "api_key" };
    ParameterizedTypeReference<Map<String, Integer>> localReturnType = new ParameterizedTypeReference<Map<String, Integer>>() { };
    return apiClient.invokeAPI("/store/inventory", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public Order getOrderById(Long orderId) throws RestClientException {
    return getOrderByIdWithHttpInfo(orderId).getBody();
  }

  public ResponseEntity<Order> getOrderByIdWithHttpInfo(Long orderId) throws RestClientException {
    Object localVarPostBody = null;
    if (orderId == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'orderId\' when calling getOrderById");
    }
    final Map<String, Object> uriVariables = new HashMap<String, Object>();
    uriVariables.put("order_id", orderId);
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Order> localReturnType = new ParameterizedTypeReference<Order>() { };
    return apiClient.invokeAPI("/store/order/{order_id}", HttpMethod.GET, uriVariables, localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public Order placeOrder(Order body) throws RestClientException {
    return placeOrderWithHttpInfo(body).getBody();
  }

  public ResponseEntity<Order> placeOrderWithHttpInfo(Order body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling placeOrder");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "application/xml", "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Order> localReturnType = new ParameterizedTypeReference<Order>() { };
    return apiClient.invokeAPI("/store/order", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }
}