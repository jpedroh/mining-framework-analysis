package org.openapitools.api;
import java.math.BigDecimal;
import org.openapitools.model.Client;
import org.openapitools.model.FileSchemaTestClass;
import java.time.LocalDate;
import java.util.Map;
import org.openapitools.model.ModelApiResponse;
import java.time.OffsetDateTime;
import org.openapitools.model.OuterComposite;
import org.openapitools.model.User;
import org.openapitools.model.XmlItem;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Map;
import javax.annotation.Generated;

@Validated @Api(value = "fake", description = "the fake API") public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface FakeApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiOperation(tags = { "fake" }, value = "creates an XmlItem", nickname = "createXmlItem", notes = "this route creates an XmlItem") @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") }) @RequestMapping(method = RequestMethod.POST, value = "/fake/create_xml_item", consumes = { "application/xml", "application/xml; charset=utf-8", "application/xml; charset=utf-16", "text/xml", "text/xml; charset=utf-8", "text/xml; charset=utf-16" }) default ResponseEntity<Void> createXmlItem(@ApiParam(value = "XmlItem Body", required = true) @Valid @RequestBody XmlItem xmlItem) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "", nickname = "fakeOuterBooleanSerialize", notes = "Test serialization of outer boolean types", response = Boolean.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "Output boolean", response = Boolean.class) }) @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/boolean", produces = { "*/*" }) default ResponseEntity<Boolean> fakeOuterBooleanSerialize(@ApiParam(value = "Input boolean as post body") @Valid @RequestBody(required = false) Boolean body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "", nickname = "fakeOuterCompositeSerialize", notes = "Test serialization of object with outer number type", response = OuterComposite.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "Output composite", response = OuterComposite.class) }) @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/composite", produces = { "*/*" }) default ResponseEntity<OuterComposite> fakeOuterCompositeSerialize(@ApiParam(value = "Input composite as post body") @Valid @RequestBody(required = false) OuterComposite body) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("*/*"))) {
          String exampleString = "{ \"my_string\" : \"my_string\", \"my_number\" : 0.8008281904610115, \"my_boolean\" : true }";
          ApiUtil.setExampleResponse(request, "*/*", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "", nickname = "fakeOuterNumberSerialize", notes = "Test serialization of outer number types", response = BigDecimal.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "Output number", response = BigDecimal.class) }) @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/number", produces = { "*/*" }) default ResponseEntity<BigDecimal> fakeOuterNumberSerialize(@ApiParam(value = "Input number as post body") @Valid @RequestBody(required = false) BigDecimal body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "", nickname = "fakeOuterStringSerialize", notes = "Test serialization of outer string types", response = String.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "Output string", response = String.class) }) @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/string", produces = { "*/*" }) default ResponseEntity<String> fakeOuterStringSerialize(@ApiParam(value = "Input string as post body") @Valid @RequestBody(required = false) String body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "", nickname = "testBodyWithFileSchema", notes = "For this test, the body for this request much reference a schema named `File`.") @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") }) @RequestMapping(method = RequestMethod.PUT, value = "/fake/body-with-file-schema", consumes = { "application/json" }) default ResponseEntity<Void> testBodyWithFileSchema(@ApiParam(value = "", required = true) @Valid @RequestBody FileSchemaTestClass body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "", nickname = "testBodyWithQueryParams", notes = "") @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") }) @RequestMapping(method = RequestMethod.PUT, value = "/fake/body-with-query-params", consumes = { "application/json" }) default ResponseEntity<Void> testBodyWithQueryParams(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "query", required = true) String query, @ApiParam(value = "", required = true) @Valid @RequestBody User body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "To test \"client\" model", nickname = "testClientModel", notes = "To test \"client\" model", response = Client.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Client.class) }) @RequestMapping(method = RequestMethod.PATCH, value = "/fake", produces = { "application/json" }, consumes = { "application/json" }) default ResponseEntity<Client> testClientModel(@ApiParam(value = "client model", required = true) @Valid @RequestBody Client body) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"client\" : \"client\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "Fake endpoint for testing various parameters \u5047\u7aef\u9ede \u507d\u306e\u30a8\u30f3\u30c9\u30dd\u30a4\u30f3\u30c8 \uac00\uc9dc \uc5d4\ub4dc \ud3ec\uc778\ud2b8 ", nickname = "testEndpointParameters", notes = "Fake endpoint for testing various parameters \u5047\u7aef\u9ede \u507d\u306e\u30a8\u30f3\u30c9\u30dd\u30a4\u30f3\u30c8 \uac00\uc9dc \uc5d4\ub4dc \ud3ec\uc778\ud2b8 ", authorizations = { @Authorization(value = "http_basic_test") }) @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username supplied"), @ApiResponse(code = 404, message = "User not found") }) @RequestMapping(method = RequestMethod.POST, value = "/fake", consumes = { "application/x-www-form-urlencoded" }) default ResponseEntity<Void> testEndpointParameters(@ApiParam(value = "None", required = true) @Valid @RequestParam(value = "number", required = true) BigDecimal number, @ApiParam(value = "None", required = true) @Valid @RequestParam(value = "double", required = true) Double _double, @ApiParam(value = "None", required = true) @Valid @RequestParam(value = "pattern_without_delimiter", required = true) String patternWithoutDelimiter, @ApiParam(value = "None", required = true) @Valid @RequestParam(value = "byte", required = true) byte[] _byte, @ApiParam(value = "None") @Valid @RequestParam(value = "integer", required = false) Integer integer, @ApiParam(value = "None") @Valid @RequestParam(value = "int32", required = false) Integer int32, @ApiParam(value = "None") @Valid @RequestParam(value = "int64", required = false) Long int64, @ApiParam(value = "None") @Valid @RequestParam(value = "float", required = false) Float _float, @ApiParam(value = "None") @Valid @RequestParam(value = "string", required = false) String string, @ApiParam(value = "None") @RequestPart(value = "binary", required = false) MultipartFile binary, @ApiParam(value = "None") @Valid @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @ApiParam(value = "None") @Valid @RequestParam(value = "dateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTime, @ApiParam(value = "None") @Valid @RequestParam(value = "password", required = false) String password, @ApiParam(value = "None") @Valid @RequestParam(value = "callback", required = false) String paramCallback) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "To test enum parameters", nickname = "testEnumParameters", notes = "To test enum parameters") @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid request"), @ApiResponse(code = 404, message = "Not found") }) @RequestMapping(method = RequestMethod.GET, value = "/fake", consumes = { "application/x-www-form-urlencoded" }) default ResponseEntity<Void> testEnumParameters(@ApiParam(value = "Header parameter enum test (string array)", allowableValues = ">, $") @RequestHeader(value = "enum_header_string_array", required = false) List<String> enumHeaderStringArray, @ApiParam(value = "Header parameter enum test (string)", allowableValues = "_abc, -efg, (xyz)", defaultValue = "-efg") @RequestHeader(value = "enum_header_string", required = false, defaultValue = "-efg") String enumHeaderString, @ApiParam(value = "Query parameter enum test (string array)", allowableValues = ">, $") @Valid @RequestParam(value = "enum_query_string_array", required = false) List<String> enumQueryStringArray, @ApiParam(value = "Query parameter enum test (string)", allowableValues = "_abc, -efg, (xyz)", defaultValue = "-efg") @Valid @RequestParam(value = "enum_query_string", required = false, defaultValue = "-efg") String enumQueryString, @ApiParam(value = "Query parameter enum test (double)", allowableValues = "1, -2") @Valid @RequestParam(value = "enum_query_integer", required = false) Integer enumQueryInteger, @ApiParam(value = "Query parameter enum test (double)", allowableValues = "1.1, -1.2") @Valid @RequestParam(value = "enum_query_double", required = false) Double enumQueryDouble, @ApiParam(value = "Form parameter enum test (string array)", allowableValues = ">, $", defaultValue = "$") @Valid @RequestParam(value = "enum_form_string_array", required = false) List<String> enumFormStringArray, @ApiParam(value = "Form parameter enum test (string)", allowableValues = "_abc, -efg, (xyz)", defaultValue = "-efg") @Valid @RequestParam(value = "enum_form_string", required = false) String enumFormString) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "Fake endpoint to test group parameters (optional)", nickname = "testGroupParameters", notes = "Fake endpoint to test group parameters (optional)") @ApiResponses(value = { @ApiResponse(code = 400, message = "Someting wrong") }) @RequestMapping(method = RequestMethod.DELETE, value = "/fake") default ResponseEntity<Void> testGroupParameters(@NotNull @ApiParam(value = "Required String in group parameters", required = true) @Valid @RequestParam(value = "required_string_group", required = true) Integer requiredStringGroup, @ApiParam(value = "Required Boolean in group parameters", required = true) @RequestHeader(value = "required_boolean_group", required = true) @NotNull Boolean requiredBooleanGroup, @NotNull @ApiParam(value = "Required Integer in group parameters", required = true) @Valid @RequestParam(value = "required_int64_group", required = true) Long requiredInt64Group, @ApiParam(value = "String in group parameters") @Valid @RequestParam(value = "string_group", required = false) Integer stringGroup, @ApiParam(value = "Boolean in group parameters") @RequestHeader(value = "boolean_group", required = false) Boolean booleanGroup, @ApiParam(value = "Integer in group parameters") @Valid @RequestParam(value = "int64_group", required = false) Long int64Group) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "test inline additionalProperties", nickname = "testInlineAdditionalProperties", notes = "") @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") }) @RequestMapping(method = RequestMethod.POST, value = "/fake/inline-additionalProperties", consumes = { "application/json" }) default ResponseEntity<Void> testInlineAdditionalProperties(@ApiParam(value = "request body", required = true) @Valid @RequestBody Map<String, String> param) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "test json serialization of form data", nickname = "testJsonFormData", notes = "") @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") }) @RequestMapping(method = RequestMethod.GET, value = "/fake/jsonFormData", consumes = { "application/x-www-form-urlencoded" }) default ResponseEntity<Void> testJsonFormData(@ApiParam(value = "field1", required = true) @Valid @RequestParam(value = "param", required = true) String param, @ApiParam(value = "field2", required = true) @Valid @RequestParam(value = "param2", required = true) String param2) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "fake" }, value = "", nickname = "testQueryParameterCollectionFormat", notes = "To test the collection format in query parameters") @ApiResponses(value = { @ApiResponse(code = 200, message = "Success") }) @RequestMapping(method = RequestMethod.PUT, value = "/fake/test-query-parameters") default ResponseEntity<Void> testQueryParameterCollectionFormat(@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "pipe", required = true) List<String> pipe, @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "ioutil", required = true) List<String> ioutil, @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "http", required = true) List<String> http, @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "url", required = true) List<String> url, @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "context", required = true) List<String> context) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "pet" }, value = "uploads an image (required)", nickname = "uploadFileWithRequiredFile", notes = "", response = ModelApiResponse.class, authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = ModelApiResponse.class) }) @RequestMapping(method = RequestMethod.POST, value = "/fake/{petId}/uploadImageWithRequiredFile", produces = { "application/json" }, consumes = { "multipart/form-data" }) default ResponseEntity<ModelApiResponse> uploadFileWithRequiredFile(@ApiParam(value = "ID of pet to update", required = true) @PathVariable(value = "petId") Long petId, @ApiParam(value = "file to upload", required = true) @RequestPart(value = "requiredFile", required = true) MultipartFile requiredFile, @ApiParam(value = "Additional data to pass to server") @Valid @RequestParam(value = "additionalMetadata", required = false) String additionalMetadata) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"code\" : 0, \"type\" : \"type\", \"message\" : \"message\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}