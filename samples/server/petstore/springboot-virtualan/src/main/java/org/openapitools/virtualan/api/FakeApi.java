package org.openapitools.virtualan.api;
import java.math.BigDecimal;
import org.openapitools.virtualan.model.Client;
import org.openapitools.virtualan.model.FileSchemaTestClass;
import java.time.LocalDate;
import java.util.Map;
import org.openapitools.virtualan.model.ModelApiResponse;
import java.time.OffsetDateTime;
import org.openapitools.virtualan.model.OuterComposite;
import org.openapitools.virtualan.model.User;
import org.openapitools.virtualan.model.XmlItem;
import io.virtualan.annotation.ApiVirtual;
import io.virtualan.annotation.VirtualService;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Generated;

@Validated @VirtualService public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @Tag(name = "fake", description = "the fake API") @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface FakeApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake/create_xml_item", consumes = { "application/xml", "application/xml; charset=utf-8", "application/xml; charset=utf-16", "text/xml", "text/xml; charset=utf-8", "text/xml; charset=utf-16" }) default @Operation(operationId = "createXmlItem", summary = "creates an XmlItem", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation") }) ResponseEntity<Void> createXmlItem(@Valid @RequestBody @Parameter(name = "XmlItem", description = "XmlItem Body", required = true) XmlItem xmlItem) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/boolean", produces = { "*/*" }) default @Operation(operationId = "fakeOuterBooleanSerialize", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "Output boolean", content = { @Content(mediaType = "*/*", schema = @Schema(implementation = Boolean.class)) }) }) ResponseEntity<Boolean> fakeOuterBooleanSerialize(@Valid @RequestBody(required = false) @Parameter(name = "body", description = "Input boolean as post body") Boolean body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/composite", produces = { "*/*" }) default @Operation(operationId = "fakeOuterCompositeSerialize", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "Output composite", content = { @Content(mediaType = "*/*", schema = @Schema(implementation = OuterComposite.class)) }) }) ResponseEntity<OuterComposite> fakeOuterCompositeSerialize(@Valid @RequestBody(required = false) @Parameter(name = "body", description = "Input composite as post body") OuterComposite body) {
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

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/number", produces = { "*/*" }) default @Operation(operationId = "fakeOuterNumberSerialize", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "Output number", content = { @Content(mediaType = "*/*", schema = @Schema(implementation = BigDecimal.class)) }) }) ResponseEntity<BigDecimal> fakeOuterNumberSerialize(@Valid @RequestBody(required = false) @Parameter(name = "body", description = "Input number as post body") BigDecimal body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake/outer/string", produces = { "*/*" }) default @Operation(operationId = "fakeOuterStringSerialize", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "Output string", content = { @Content(mediaType = "*/*", schema = @Schema(implementation = String.class)) }) }) ResponseEntity<String> fakeOuterStringSerialize(@Valid @RequestBody(required = false) @Parameter(name = "body", description = "Input string as post body") String body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.PUT, value = "/fake/body-with-file-schema", consumes = { "application/json" }) default @Operation(operationId = "testBodyWithFileSchema", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "Success") }) ResponseEntity<Void> testBodyWithFileSchema(@Valid @RequestBody @Parameter(name = "body", description = "", required = true) FileSchemaTestClass body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.PUT, value = "/fake/body-with-query-params", consumes = { "application/json" }) default @Operation(operationId = "testBodyWithQueryParams", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "Success") }) ResponseEntity<Void> testBodyWithQueryParams(@NotNull @Valid @RequestParam(value = "query", required = true) @Parameter(name = "query", description = "", required = true) String query, @Valid @RequestBody @Parameter(name = "body", description = "", required = true) User body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.PATCH, value = "/fake", produces = { "application/json" }, consumes = { "application/json" }) default @Operation(operationId = "testClientModel", summary = "To test \"client\" model", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class)) }) }) ResponseEntity<Client> testClientModel(@Valid @RequestBody @Parameter(name = "body", description = "client model", required = true) Client body) {
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

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake", consumes = { "application/x-www-form-urlencoded" }) default @Operation(operationId = "testEndpointParameters", summary = "Fake endpoint for testing various parameters  \u5047\u7aef\u9ede  \u507d\u306e\u30a8\u30f3\u30c9\u30dd\u30a4\u30f3\u30c8  \uac00\uc9dc \uc5d4\ub4dc \ud3ec\uc778\ud2b8", tags = { "fake" }, responses = { @ApiResponse(responseCode = "400", description = "Invalid username supplied"), @ApiResponse(responseCode = "404", description = "User not found") }, security = { @SecurityRequirement(name = "http_basic_test") }) ResponseEntity<Void> testEndpointParameters(@Valid @Parameter(name = "number", description = "None", required = true) @RequestParam(value = "number", required = true) BigDecimal number, @Valid @Parameter(name = "double", description = "None", required = true) @RequestParam(value = "double", required = true) Double _double, @Valid @Parameter(name = "pattern_without_delimiter", description = "None", required = true) @RequestParam(value = "pattern_without_delimiter", required = true) String patternWithoutDelimiter, @Valid @Parameter(name = "byte", description = "None", required = true) @RequestParam(value = "byte", required = true) byte[] _byte, @Valid @Parameter(name = "integer", description = "None") @RequestParam(value = "integer", required = false) Integer integer, @Valid @Parameter(name = "int32", description = "None") @RequestParam(value = "int32", required = false) Integer int32, @Valid @Parameter(name = "int64", description = "None") @RequestParam(value = "int64", required = false) Long int64, @Valid @Parameter(name = "float", description = "None") @RequestParam(value = "float", required = false) Float _float, @Valid @Parameter(name = "string", description = "None") @RequestParam(value = "string", required = false) String string, @RequestPart(value = "binary", required = false) @Parameter(name = "binary", description = "None") MultipartFile binary, @Valid @Parameter(name = "date", description = "None") @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @Valid @Parameter(name = "dateTime", description = "None") @RequestParam(value = "dateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTime, @Valid @Parameter(name = "password", description = "None") @RequestParam(value = "password", required = false) String password, @Valid @Parameter(name = "callback", description = "None") @RequestParam(value = "callback", required = false) String paramCallback) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/fake", consumes = { "application/x-www-form-urlencoded" }) default @Operation(operationId = "testEnumParameters", summary = "To test enum parameters", tags = { "fake" }, responses = { @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "404", description = "Not found") }) ResponseEntity<Void> testEnumParameters(@RequestHeader(value = "enum_header_string_array", required = false) @Parameter(name = "enum_header_string_array", description = "Header parameter enum test (string array)") List<String> enumHeaderStringArray, @RequestHeader(value = "enum_header_string", required = false, defaultValue = "-efg") @Parameter(name = "enum_header_string", description = "Header parameter enum test (string)") String enumHeaderString, @Valid @RequestParam(value = "enum_query_string_array", required = false) @Parameter(name = "enum_query_string_array", description = "Query parameter enum test (string array)") List<String> enumQueryStringArray, @Valid @RequestParam(value = "enum_query_string", required = false, defaultValue = "-efg") @Parameter(name = "enum_query_string", description = "Query parameter enum test (string)") String enumQueryString, @Valid @RequestParam(value = "enum_query_integer", required = false) @Parameter(name = "enum_query_integer", description = "Query parameter enum test (double)") Integer enumQueryInteger, @Valid @RequestParam(value = "enum_query_double", required = false) @Parameter(name = "enum_query_double", description = "Query parameter enum test (double)") Double enumQueryDouble, @Valid @Parameter(name = "enum_form_string_array", description = "Form parameter enum test (string array)") @RequestParam(value = "enum_form_string_array", required = false) List<String> enumFormStringArray, @Valid @Parameter(name = "enum_form_string", description = "Form parameter enum test (string)") @RequestParam(value = "enum_form_string", required = false) String enumFormString) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.DELETE, value = "/fake") default @Operation(operationId = "testGroupParameters", summary = "Fake endpoint to test group parameters (optional)", tags = { "fake" }, responses = { @ApiResponse(responseCode = "400", description = "Someting wrong") }) ResponseEntity<Void> testGroupParameters(@NotNull @Valid @RequestParam(value = "required_string_group", required = true) @Parameter(name = "required_string_group", description = "Required String in group parameters", required = true) Integer requiredStringGroup, @RequestHeader(value = "required_boolean_group", required = true) @NotNull @Parameter(name = "required_boolean_group", description = "Required Boolean in group parameters", required = true) Boolean requiredBooleanGroup, @NotNull @Valid @RequestParam(value = "required_int64_group", required = true) @Parameter(name = "required_int64_group", description = "Required Integer in group parameters", required = true) Long requiredInt64Group, @Valid @RequestParam(value = "string_group", required = false) @Parameter(name = "string_group", description = "String in group parameters") Integer stringGroup, @RequestHeader(value = "boolean_group", required = false) @Parameter(name = "boolean_group", description = "Boolean in group parameters") Boolean booleanGroup, @Valid @RequestParam(value = "int64_group", required = false) @Parameter(name = "int64_group", description = "Integer in group parameters") Long int64Group) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake/inline-additionalProperties", consumes = { "application/json" }) default @Operation(operationId = "testInlineAdditionalProperties", summary = "test inline additionalProperties", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation") }) ResponseEntity<Void> testInlineAdditionalProperties(@Valid @RequestBody @Parameter(name = "param", description = "request body", required = true) Map<String, String> param) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/fake/jsonFormData", consumes = { "application/x-www-form-urlencoded" }) default @Operation(operationId = "testJsonFormData", summary = "test json serialization of form data", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation") }) ResponseEntity<Void> testJsonFormData(@Valid @Parameter(name = "param", description = "field1", required = true) @RequestParam(value = "param", required = true) String param, @Valid @Parameter(name = "param2", description = "field2", required = true) @RequestParam(value = "param2", required = true) String param2) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.PUT, value = "/fake/test-query-parameters") default @Operation(operationId = "testQueryParameterCollectionFormat", tags = { "fake" }, responses = { @ApiResponse(responseCode = "200", description = "Success") }) ResponseEntity<Void> testQueryParameterCollectionFormat(@NotNull @Valid @RequestParam(value = "pipe", required = true) @Parameter(name = "pipe", description = "", required = true) List<String> pipe, @NotNull @Valid @RequestParam(value = "ioutil", required = true) @Parameter(name = "ioutil", description = "", required = true) List<String> ioutil, @NotNull @Valid @RequestParam(value = "http", required = true) @Parameter(name = "http", description = "", required = true) List<String> http, @NotNull @Valid @RequestParam(value = "url", required = true) @Parameter(name = "url", description = "", required = true) List<String> url, @NotNull @Valid @RequestParam(value = "context", required = true) @Parameter(name = "context", description = "", required = true) List<String> context) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/fake/{petId}/uploadImageWithRequiredFile", produces = { "application/json" }, consumes = { "multipart/form-data" }) default @Operation(operationId = "uploadFileWithRequiredFile", summary = "uploads an image (required)", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ModelApiResponse.class)) }) }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<ModelApiResponse> uploadFileWithRequiredFile(@PathVariable(value = "petId") @Parameter(name = "petId", description = "ID of pet to update", required = true) Long petId, @RequestPart(value = "requiredFile", required = true) @Parameter(name = "requiredFile", description = "file to upload", required = true) MultipartFile requiredFile, @Valid @Parameter(name = "additionalMetadata", description = "Additional data to pass to server") @RequestParam(value = "additionalMetadata", required = false) String additionalMetadata) {
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