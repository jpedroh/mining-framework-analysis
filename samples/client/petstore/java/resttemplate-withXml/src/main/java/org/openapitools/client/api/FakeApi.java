package org.openapitools.client.api;
import org.openapitools.client.ApiClient;
import java.math.BigDecimal;
import org.openapitools.client.model.Client;
import java.io.File;
import org.openapitools.client.model.FileSchemaTestClass;
import org.openapitools.client.model.OuterComposite;
import org.openapitools.client.model.User;
import org.openapitools.client.model.XmlItem;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;

@javax.annotation.Generated(value = { "org.openapitools.codegen.languages.JavaClientCodegen" }) @Component(value = "org.openapitools.client.api.FakeApi") public class FakeApi {
  private ApiClient apiClient;

  public FakeApi() {
    this(new ApiClient());
  }

  @Autowired public FakeApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void createXmlItem(XmlItem xmlItem) throws RestClientException {
    createXmlItemWithHttpInfo(xmlItem);
  }

  public ResponseEntity<Void> createXmlItemWithHttpInfo(XmlItem xmlItem) throws RestClientException {
    Object localVarPostBody = xmlItem;
    if (xmlItem == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'xmlItem\' when calling createXmlItem");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/xml", "application/xml; charset=utf-8", "application/xml; charset=utf-16", "text/xml", "text/xml; charset=utf-8", "text/xml; charset=utf-16" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake/create_xml_item", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public Boolean fakeOuterBooleanSerialize(Boolean body) throws RestClientException {
    return fakeOuterBooleanSerializeWithHttpInfo(body).getBody();
  }

  public ResponseEntity<Boolean> fakeOuterBooleanSerializeWithHttpInfo(Boolean body) throws RestClientException {
    Object localVarPostBody = body;
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Boolean> localReturnType = new ParameterizedTypeReference<Boolean>() { };
    return apiClient.invokeAPI("/fake/outer/boolean", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public OuterComposite fakeOuterCompositeSerialize(OuterComposite body) throws RestClientException {
    return fakeOuterCompositeSerializeWithHttpInfo(body).getBody();
  }

  public ResponseEntity<OuterComposite> fakeOuterCompositeSerializeWithHttpInfo(OuterComposite body) throws RestClientException {
    Object localVarPostBody = body;
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<OuterComposite> localReturnType = new ParameterizedTypeReference<OuterComposite>() { };
    return apiClient.invokeAPI("/fake/outer/composite", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public BigDecimal fakeOuterNumberSerialize(BigDecimal body) throws RestClientException {
    return fakeOuterNumberSerializeWithHttpInfo(body).getBody();
  }

  public ResponseEntity<BigDecimal> fakeOuterNumberSerializeWithHttpInfo(BigDecimal body) throws RestClientException {
    Object localVarPostBody = body;
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<BigDecimal> localReturnType = new ParameterizedTypeReference<BigDecimal>() { };
    return apiClient.invokeAPI("/fake/outer/number", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public String fakeOuterStringSerialize(String body) throws RestClientException {
    return fakeOuterStringSerializeWithHttpInfo(body).getBody();
  }

  public ResponseEntity<String> fakeOuterStringSerializeWithHttpInfo(String body) throws RestClientException {
    Object localVarPostBody = body;
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<String> localReturnType = new ParameterizedTypeReference<String>() { };
    return apiClient.invokeAPI("/fake/outer/string", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testBodyWithFileSchema(FileSchemaTestClass body) throws RestClientException {
    testBodyWithFileSchemaWithHttpInfo(body);
  }

  public ResponseEntity<Void> testBodyWithFileSchemaWithHttpInfo(FileSchemaTestClass body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling testBodyWithFileSchema");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake/body-with-file-schema", HttpMethod.PUT, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testBodyWithQueryParams(String query, User body) throws RestClientException {
    testBodyWithQueryParamsWithHttpInfo(query, body);
  }

  public ResponseEntity<Void> testBodyWithQueryParamsWithHttpInfo(String query, User body) throws RestClientException {
    Object localVarPostBody = body;
    if (query == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'query\' when calling testBodyWithQueryParams");
    }
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling testBodyWithQueryParams");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "query", query));
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake/body-with-query-params", HttpMethod.PUT, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public Client testClientModel(Client body) throws RestClientException {
    return testClientModelWithHttpInfo(body).getBody();
  }

  public ResponseEntity<Client> testClientModelWithHttpInfo(Client body) throws RestClientException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'body\' when calling testClientModel");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = { "application/json" };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Client> localReturnType = new ParameterizedTypeReference<Client>() { };
    return apiClient.invokeAPI("/fake", HttpMethod.PATCH, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testEndpointParameters(BigDecimal number, Double _double, String patternWithoutDelimiter, byte[] _byte, Integer integer, Integer int32, Long int64, Float _float, String string, File binary, LocalDate date, OffsetDateTime dateTime, String password, String paramCallback) throws RestClientException {
    testEndpointParametersWithHttpInfo(number, _double, patternWithoutDelimiter, _byte, integer, int32, int64, _float, string, binary, date, dateTime, password, paramCallback);
  }

  public ResponseEntity<Void> testEndpointParametersWithHttpInfo(BigDecimal number, Double _double, String patternWithoutDelimiter, byte[] _byte, Integer integer, Integer int32, Long int64, Float _float, String string, File binary, LocalDate date, OffsetDateTime dateTime, String password, String paramCallback) throws RestClientException {
    Object localVarPostBody = null;
    if (number == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'number\' when calling testEndpointParameters");
    }
    if (_double == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'_double\' when calling testEndpointParameters");
    }
    if (patternWithoutDelimiter == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'patternWithoutDelimiter\' when calling testEndpointParameters");
    }
    if (_byte == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'_byte\' when calling testEndpointParameters");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    if (integer != null) {
      localVarFormParams.add("integer", integer);
    }
    if (int32 != null) {
      localVarFormParams.add("int32", int32);
    }
    if (int64 != null) {
      localVarFormParams.add("int64", int64);
    }
    if (number != null) {
      localVarFormParams.add("number", number);
    }
    if (_float != null) {
      localVarFormParams.add("float", _float);
    }
    if (_double != null) {
      localVarFormParams.add("double", _double);
    }
    if (string != null) {
      localVarFormParams.add("string", string);
    }
    if (patternWithoutDelimiter != null) {
      localVarFormParams.add("pattern_without_delimiter", patternWithoutDelimiter);
    }
    if (_byte != null) {
      localVarFormParams.add("byte", _byte);
    }
    if (binary != null) {
      localVarFormParams.add("binary", new FileSystemResource(binary));
    }
    if (date != null) {
      localVarFormParams.add("date", date);
    }
    if (dateTime != null) {
      localVarFormParams.add("dateTime", dateTime);
    }
    if (password != null) {
      localVarFormParams.add("password", password);
    }
    if (paramCallback != null) {
      localVarFormParams.add("callback", paramCallback);
    }
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "http_basic_test" };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testEnumParameters(List<String> enumHeaderStringArray, String enumHeaderString, List<String> enumQueryStringArray, String enumQueryString, Integer enumQueryInteger, Double enumQueryDouble, List<String> enumFormStringArray, String enumFormString) throws RestClientException {
    testEnumParametersWithHttpInfo(enumHeaderStringArray, enumHeaderString, enumQueryStringArray, enumQueryString, enumQueryInteger, enumQueryDouble, enumFormStringArray, enumFormString);
  }

  public ResponseEntity<Void> testEnumParametersWithHttpInfo(List<String> enumHeaderStringArray, String enumHeaderString, List<String> enumQueryStringArray, String enumQueryString, Integer enumQueryInteger, Double enumQueryDouble, List<String> enumFormStringArray, String enumFormString) throws RestClientException {
    Object localVarPostBody = null;
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "enum_query_string_array", enumQueryStringArray));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "enum_query_string", enumQueryString));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "enum_query_integer", enumQueryInteger));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "enum_query_double", enumQueryDouble));
    if (enumHeaderStringArray != null) {
      localVarHeaderParams.add("enum_header_string_array", apiClient.parameterToString(enumHeaderStringArray));
    }
    if (enumHeaderString != null) {
      localVarHeaderParams.add("enum_header_string", apiClient.parameterToString(enumHeaderString));
    }
    if (enumFormStringArray != null) {
      localVarFormParams.addAll("enum_form_string_array", enumFormStringArray);
    }
    if (enumFormString != null) {
      localVarFormParams.add("enum_form_string", enumFormString);
    }
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testGroupParameters(Integer requiredStringGroup, Boolean requiredBooleanGroup, Long requiredInt64Group, Integer stringGroup, Boolean booleanGroup, Long int64Group) throws RestClientException {
    testGroupParametersWithHttpInfo(requiredStringGroup, requiredBooleanGroup, requiredInt64Group, stringGroup, booleanGroup, int64Group);
  }

  public ResponseEntity<Void> testGroupParametersWithHttpInfo(Integer requiredStringGroup, Boolean requiredBooleanGroup, Long requiredInt64Group, Integer stringGroup, Boolean booleanGroup, Long int64Group) throws RestClientException {
    Object localVarPostBody = null;
    if (requiredStringGroup == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'requiredStringGroup\' when calling testGroupParameters");
    }
    if (requiredBooleanGroup == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'requiredBooleanGroup\' when calling testGroupParameters");
    }
    if (requiredInt64Group == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'requiredInt64Group\' when calling testGroupParameters");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "required_string_group", requiredStringGroup));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "required_int64_group", requiredInt64Group));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "string_group", stringGroup));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "int64_group", int64Group));
    if (requiredBooleanGroup != null) {
      localVarHeaderParams.add("required_boolean_group", apiClient.parameterToString(requiredBooleanGroup));
    }
    if (booleanGroup != null) {
      localVarHeaderParams.add("boolean_group", apiClient.parameterToString(booleanGroup));
    }
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake", HttpMethod.DELETE, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testInlineAdditionalProperties(Map<String, String> param) throws RestClientException {
    testInlineAdditionalPropertiesWithHttpInfo(param);
  }

  public ResponseEntity<Void> testInlineAdditionalPropertiesWithHttpInfo(Map<String, String> param) throws RestClientException {
    Object localVarPostBody = param;
    if (param == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'param\' when calling testInlineAdditionalProperties");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake/inline-additionalProperties", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testJsonFormData(String param, String param2) throws RestClientException {
    testJsonFormDataWithHttpInfo(param, param2);
  }

  public ResponseEntity<Void> testJsonFormDataWithHttpInfo(String param, String param2) throws RestClientException {
    Object localVarPostBody = null;
    if (param == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'param\' when calling testJsonFormData");
    }
    if (param2 == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'param2\' when calling testJsonFormData");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    if (param != null) {
      localVarFormParams.add("param", param);
    }
    if (param2 != null) {
      localVarFormParams.add("param2", param2);
    }
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake/jsonFormData", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }

  public void testQueryParameterCollectionFormat(List<String> pipe, List<String> ioutil, List<String> http, List<String> url, List<String> context) throws RestClientException {
    testQueryParameterCollectionFormatWithHttpInfo(pipe, ioutil, http, url, context);
  }

  public ResponseEntity<Void> testQueryParameterCollectionFormatWithHttpInfo(List<String> pipe, List<String> ioutil, List<String> http, List<String> url, List<String> context) throws RestClientException {
    Object localVarPostBody = null;
    if (pipe == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'pipe\' when calling testQueryParameterCollectionFormat");
    }
    if (ioutil == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'ioutil\' when calling testQueryParameterCollectionFormat");
    }
    if (http == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'http\' when calling testQueryParameterCollectionFormat");
    }
    if (url == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'url\' when calling testQueryParameterCollectionFormat");
    }
    if (context == null) {
      throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter \'context\' when calling testQueryParameterCollectionFormat");
    }
    final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
    final HttpHeaders localVarHeaderParams = new HttpHeaders();
    final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
    final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "pipe", pipe));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "ioutil", ioutil));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("ssv".toUpperCase(Locale.ROOT)), "http", http));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("csv".toUpperCase(Locale.ROOT)), "url", url));
    localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("multi".toUpperCase(Locale.ROOT)), "context", context));
    final String[] localVarAccepts = {  };
    final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() { };
    return apiClient.invokeAPI("/fake/test-query-parameters", HttpMethod.PUT, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
  }
}