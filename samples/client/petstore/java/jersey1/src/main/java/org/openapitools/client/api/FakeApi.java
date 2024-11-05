package org.openapitools.client.api;
import com.sun.jersey.api.client.GenericType;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiClient;
import org.openapitools.client.Configuration;
import org.openapitools.client.model.*;
import org.openapitools.client.Pair;
import java.math.BigDecimal;
import org.openapitools.client.model.Client;
import java.io.File;
import org.openapitools.client.model.FileSchemaTestClass;
import org.openapitools.client.model.OuterComposite;
import org.openapitools.client.model.User;
import org.openapitools.client.model.XmlItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@javax.annotation.Generated(value = { "org.openapitools.codegen.languages.JavaClientCodegen" }) public class FakeApi {
  private ApiClient apiClient;

  public FakeApi() {
    this(Configuration.getDefaultApiClient());
  }

  public FakeApi(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void createXmlItem(XmlItem xmlItem) throws ApiException {
    Object localVarPostBody = xmlItem;
    if (xmlItem == null) {
      throw new ApiException(400, "Missing the required parameter \'xmlItem\' when calling createXmlItem");
    }
    String localVarPath = "/fake/create_xml_item";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/xml", "application/xml; charset=utf-8", "application/xml; charset=utf-16", "text/xml", "text/xml; charset=utf-8", "text/xml; charset=utf-16" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public Boolean fakeOuterBooleanSerialize(Boolean body) throws ApiException {
    Object localVarPostBody = body;
    String localVarPath = "/fake/outer/boolean";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    GenericType<Boolean> localVarReturnType = new GenericType<Boolean>() { };
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public OuterComposite fakeOuterCompositeSerialize(OuterComposite body) throws ApiException {
    Object localVarPostBody = body;
    String localVarPath = "/fake/outer/composite";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    GenericType<OuterComposite> localVarReturnType = new GenericType<OuterComposite>() { };
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public BigDecimal fakeOuterNumberSerialize(BigDecimal body) throws ApiException {
    Object localVarPostBody = body;
    String localVarPath = "/fake/outer/number";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    GenericType<BigDecimal> localVarReturnType = new GenericType<BigDecimal>() { };
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public String fakeOuterStringSerialize(String body) throws ApiException {
    Object localVarPostBody = body;
    String localVarPath = "/fake/outer/string";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = { "*/*" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    GenericType<String> localVarReturnType = new GenericType<String>() { };
    return apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public void testBodyWithFileSchema(FileSchemaTestClass body) throws ApiException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new ApiException(400, "Missing the required parameter \'body\' when calling testBodyWithFileSchema");
    }
    String localVarPath = "/fake/body-with-file-schema";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void testBodyWithQueryParams(String query, User body) throws ApiException {
    Object localVarPostBody = body;
    if (query == null) {
      throw new ApiException(400, "Missing the required parameter \'query\' when calling testBodyWithQueryParams");
    }
    if (body == null) {
      throw new ApiException(400, "Missing the required parameter \'body\' when calling testBodyWithQueryParams");
    }
    String localVarPath = "/fake/body-with-query-params";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    localVarQueryParams.addAll(apiClient.parameterToPair("query", query));
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public Client testClientModel(Client body) throws ApiException {
    Object localVarPostBody = body;
    if (body == null) {
      throw new ApiException(400, "Missing the required parameter \'body\' when calling testClientModel");
    }
    String localVarPath = "/fake";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = { "application/json" };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    GenericType<Client> localVarReturnType = new GenericType<Client>() { };
    return apiClient.invokeAPI(localVarPath, "PATCH", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localVarReturnType);
  }

  public void testEndpointParameters(BigDecimal number, Double _double, String patternWithoutDelimiter, byte[] _byte, Integer integer, Integer int32, Long int64, Float _float, String string, File binary, LocalDate date, OffsetDateTime dateTime, String password, String paramCallback) throws ApiException {
    Object localVarPostBody = null;
    if (number == null) {
      throw new ApiException(400, "Missing the required parameter \'number\' when calling testEndpointParameters");
    }
    if (_double == null) {
      throw new ApiException(400, "Missing the required parameter \'_double\' when calling testEndpointParameters");
    }
    if (patternWithoutDelimiter == null) {
      throw new ApiException(400, "Missing the required parameter \'patternWithoutDelimiter\' when calling testEndpointParameters");
    }
    if (_byte == null) {
      throw new ApiException(400, "Missing the required parameter \'_byte\' when calling testEndpointParameters");
    }
    String localVarPath = "/fake";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    if (integer != null) {
      localVarFormParams.put("integer", integer);
    }
    if (int32 != null) {
      localVarFormParams.put("int32", int32);
    }
    if (int64 != null) {
      localVarFormParams.put("int64", int64);
    }
    if (number != null) {
      localVarFormParams.put("number", number);
    }
    if (_float != null) {
      localVarFormParams.put("float", _float);
    }
    if (_double != null) {
      localVarFormParams.put("double", _double);
    }
    if (string != null) {
      localVarFormParams.put("string", string);
    }
    if (patternWithoutDelimiter != null) {
      localVarFormParams.put("pattern_without_delimiter", patternWithoutDelimiter);
    }
    if (_byte != null) {
      localVarFormParams.put("byte", _byte);
    }
    if (binary != null) {
      localVarFormParams.put("binary", binary);
    }
    if (date != null) {
      localVarFormParams.put("date", date);
    }
    if (dateTime != null) {
      localVarFormParams.put("dateTime", dateTime);
    }
    if (password != null) {
      localVarFormParams.put("password", password);
    }
    if (paramCallback != null) {
      localVarFormParams.put("callback", paramCallback);
    }
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] { "http_basic_test" };
    apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void testEnumParameters(List<String> enumHeaderStringArray, String enumHeaderString, List<String> enumQueryStringArray, String enumQueryString, Integer enumQueryInteger, Double enumQueryDouble, List<String> enumFormStringArray, String enumFormString) throws ApiException {
    Object localVarPostBody = null;
    String localVarPath = "/fake";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("csv", "enum_query_string_array", enumQueryStringArray));
    localVarQueryParams.addAll(apiClient.parameterToPair("enum_query_string", enumQueryString));
    localVarQueryParams.addAll(apiClient.parameterToPair("enum_query_integer", enumQueryInteger));
    localVarQueryParams.addAll(apiClient.parameterToPair("enum_query_double", enumQueryDouble));
    if (enumHeaderStringArray != null) {
      localVarHeaderParams.put("enum_header_string_array", apiClient.parameterToString(enumHeaderStringArray));
    }
    if (enumHeaderString != null) {
      localVarHeaderParams.put("enum_header_string", apiClient.parameterToString(enumHeaderString));
    }
    if (enumFormStringArray != null) {
      localVarFormParams.put("enum_form_string_array", enumFormStringArray);
    }
    if (enumFormString != null) {
      localVarFormParams.put("enum_form_string", enumFormString);
    }
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void testGroupParameters(Integer requiredStringGroup, Boolean requiredBooleanGroup, Long requiredInt64Group, Integer stringGroup, Boolean booleanGroup, Long int64Group) throws ApiException {
    Object localVarPostBody = null;
    if (requiredStringGroup == null) {
      throw new ApiException(400, "Missing the required parameter \'requiredStringGroup\' when calling testGroupParameters");
    }
    if (requiredBooleanGroup == null) {
      throw new ApiException(400, "Missing the required parameter \'requiredBooleanGroup\' when calling testGroupParameters");
    }
    if (requiredInt64Group == null) {
      throw new ApiException(400, "Missing the required parameter \'requiredInt64Group\' when calling testGroupParameters");
    }
    String localVarPath = "/fake";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    localVarQueryParams.addAll(apiClient.parameterToPair("required_string_group", requiredStringGroup));
    localVarQueryParams.addAll(apiClient.parameterToPair("required_int64_group", requiredInt64Group));
    localVarQueryParams.addAll(apiClient.parameterToPair("string_group", stringGroup));
    localVarQueryParams.addAll(apiClient.parameterToPair("int64_group", int64Group));
    if (requiredBooleanGroup != null) {
      localVarHeaderParams.put("required_boolean_group", apiClient.parameterToString(requiredBooleanGroup));
    }
    if (booleanGroup != null) {
      localVarHeaderParams.put("boolean_group", apiClient.parameterToString(booleanGroup));
    }
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void testInlineAdditionalProperties(Map<String, String> param) throws ApiException {
    Object localVarPostBody = param;
    if (param == null) {
      throw new ApiException(400, "Missing the required parameter \'param\' when calling testInlineAdditionalProperties");
    }
    String localVarPath = "/fake/inline-additionalProperties";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/json" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void testJsonFormData(String param, String param2) throws ApiException {
    Object localVarPostBody = null;
    if (param == null) {
      throw new ApiException(400, "Missing the required parameter \'param\' when calling testJsonFormData");
    }
    if (param2 == null) {
      throw new ApiException(400, "Missing the required parameter \'param2\' when calling testJsonFormData");
    }
    String localVarPath = "/fake/jsonFormData";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    if (param != null) {
      localVarFormParams.put("param", param);
    }
    if (param2 != null) {
      localVarFormParams.put("param2", param2);
    }
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = { "application/x-www-form-urlencoded" };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }

  public void testQueryParameterCollectionFormat(List<String> pipe, List<String> ioutil, List<String> http, List<String> url, List<String> context) throws ApiException {
    Object localVarPostBody = null;
    if (pipe == null) {
      throw new ApiException(400, "Missing the required parameter \'pipe\' when calling testQueryParameterCollectionFormat");
    }
    if (ioutil == null) {
      throw new ApiException(400, "Missing the required parameter \'ioutil\' when calling testQueryParameterCollectionFormat");
    }
    if (http == null) {
      throw new ApiException(400, "Missing the required parameter \'http\' when calling testQueryParameterCollectionFormat");
    }
    if (url == null) {
      throw new ApiException(400, "Missing the required parameter \'url\' when calling testQueryParameterCollectionFormat");
    }
    if (context == null) {
      throw new ApiException(400, "Missing the required parameter \'context\' when calling testQueryParameterCollectionFormat");
    }
    String localVarPath = "/fake/test-query-parameters";
    List<Pair> localVarQueryParams = new ArrayList<Pair>();
    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
    Map<String, String> localVarCookieParams = new HashMap<String, String>();
    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("csv", "pipe", pipe));
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("csv", "ioutil", ioutil));
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("ssv", "http", http));
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("csv", "url", url));
    localVarCollectionQueryParams.addAll(apiClient.parameterToPairs("multi", "context", context));
    final String[] localVarAccepts = {  };
    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
    final String[] localVarContentTypes = {  };
    final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
    String[] localVarAuthNames = new String[] {  };
    apiClient.invokeAPI(localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, null);
  }
}