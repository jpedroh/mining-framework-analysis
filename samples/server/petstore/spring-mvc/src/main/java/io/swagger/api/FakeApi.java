package io.swagger.api;

import io.swagger.annotations.*;
import io.swagger.model.Client;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;


@Api(value = "fake", description = "the fake API")
public interface FakeApi {
    @ApiOperation(value = "To test \"client\" model", notes = "To test \"client\" model", response = Client.class, tags = { "fake" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = Client.class) })
    @RequestMapping(value = "/fake", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PATCH)
    public abstract ResponseEntity<Client> testClientModel(@ApiParam(value = "client model", required = true)
    @Valid
    @RequestBody
    Client body, @RequestHeader("Accept")
    String accept) throws IOException;

    @ApiOperation(value = "Fake endpoint for testing various parameters 假端點 偽のエンドポイント 가짜 엔드 포인트 ", notes = "Fake endpoint for testing various parameters 假端點 偽のエンドポイント 가짜 엔드 포인트 ", response = java.lang.Void.class, authorizations = { @Authorization("http_basic_test") }, tags = { "fake" })
    @ApiResponses({ @ApiResponse(code = 400, message = "Invalid username supplied", response = java.lang.Void.class), @ApiResponse(code = 404, message = "User not found", response = java.lang.Void.class) })
    @RequestMapping(value = "/fake", produces = { "application/xml; charset=utf-8", "application/json; charset=utf-8" }, consumes = { "application/xml; charset=utf-8", "application/json; charset=utf-8" }, method = RequestMethod.POST)
    public abstract ResponseEntity<Void> testEndpointParameters(@ApiParam(value = "None", required = true)
    @RequestPart(value = "number", required = true)
    BigDecimal number, @ApiParam(value = "None", required = true)
    @RequestPart(value = "double", required = true)
    Double _double, @ApiParam(value = "None", required = true)
    @RequestPart(value = "pattern_without_delimiter", required = true)
    String patternWithoutDelimiter, @ApiParam(value = "None", required = true)
    @RequestPart(value = "byte", required = true)
    byte[] _byte, @ApiParam("None")
    @RequestPart(value = "integer", required = false)
    Integer integer, @ApiParam("None")
    @RequestPart(value = "int32", required = false)
    Integer int32, @ApiParam("None")
    @RequestPart(value = "int64", required = false)
    Long int64, @ApiParam("None")
    @RequestPart(value = "float", required = false)
    Float _float, @ApiParam("None")
    @RequestPart(value = "string", required = false)
    String string, @ApiParam("None")
    @RequestPart(value = "binary", required = false)
    byte[] binary, @ApiParam("None")
    @RequestPart(value = "date", required = false)
    LocalDate date, @ApiParam("None")
    @RequestPart(value = "dateTime", required = false)
    OffsetDateTime dateTime, @ApiParam("None")
    @RequestPart(value = "password", required = false)
    String password, @ApiParam("None")
    @RequestPart(value = "callback", required = false)
    String paramCallback, @RequestHeader("Accept")
    String accept);

    @ApiOperation(value = "To test enum parameters", notes = "To test enum parameters", response = Void.class, tags={ "fake", })
    @ApiResponses(value = { 
        @ApiResponse(code = 400, message = "Invalid request", response = Void.class),
        @ApiResponse(code = 404, message = "Not found", response = Void.class) })
    @RequestMapping(value = "/fake",
        produces = { "*/*" }, 
        consumes = { "*/*" },
        method = RequestMethod.GET)
    ResponseEntity<Void> testEnumParameters(@ApiParam(value = "Form parameter enum test (string array)", allowableValues=">, $") @RequestPart(value="enum_form_string_array", required=false)  List<String> enumFormStringArray,@ApiParam(value = "Form parameter enum test (string)", allowableValues="_abc, -efg, (xyz)", defaultValue="-efg") @RequestPart(value="enum_form_string", required=false)  String enumFormString,@ApiParam(value = "Header parameter enum test (string array)" , allowableValues=">, $") @RequestHeader(value="enum_header_string_array", required=false) List<String> enumHeaderStringArray,@ApiParam(value = "Header parameter enum test (string)" , allowableValues="_abc, -efg, (xyz)", defaultValue="-efg") @RequestHeader(value="enum_header_string", required=false) String enumHeaderString, @ApiParam(value = "Query parameter enum test (string array)", allowableValues = ">, $") @RequestParam(value = "enum_query_string_array", required = false) List<String> enumQueryStringArray, @ApiParam(value = "Query parameter enum test (string)", allowableValues = "_abc, -efg, (xyz)", defaultValue = "-efg") @RequestParam(value = "enum_query_string", required = false, defaultValue="-efg") String enumQueryString, @ApiParam(value = "Query parameter enum test (double)", allowableValues = "1, -2") @RequestParam(value = "enum_query_integer", required = false) Integer enumQueryInteger,@ApiParam(value = "Query parameter enum test (double)", allowableValues="1.1, -1.2") @RequestPart(value="enum_query_double", required=false)  Double enumQueryDouble, @RequestHeader("Accept") String accept);
}