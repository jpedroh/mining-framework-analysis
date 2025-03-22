package io.swagger.api;
import io.swagger.model.*;
import io.swagger.api.FakeApiService;
import io.swagger.api.factories.FakeApiServiceFactory;
import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;
import io.swagger.model.Client;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.api.NotFoundException;
import java.io.InputStream;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;

@Path(value = "/fake") @io.swagger.annotations.Api(description = "the fake API") public class FakeApi {
  private final FakeApiService delegate = FakeApiServiceFactory.getFakeApi();

  @PATCH @Consumes(value = { "application/json" }) @Produces(value = { "application/json" }) @io.swagger.annotations.ApiOperation(value = "To test \"client\" model", notes = "", response = Client.class, tags = { "fake" }) @io.swagger.annotations.ApiResponses(value = { @io.swagger.annotations.ApiResponse(code = 200, message = "successful operation", response = Client.class) }) public Response testClientModel(@ApiParam(value = "client model", required = true) Client body, @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.testClientModel(body, securityContext);
  }

  @POST @Consumes(value = { "application/xml; charset=utf-8", "application/json; charset=utf-8" }) @Produces(value = { "application/xml; charset=utf-8", "application/json; charset=utf-8" }) @io.swagger.annotations.ApiOperation(value = "Fake endpoint for testing various parameters \u5047\u7aef\u9ede \u507d\u306e\u30a8\u30f3\u30c9\u30dd\u30a4\u30f3\u30c8 \uac00\uc9dc \uc5d4\ub4dc \ud3ec\uc778\ud2b8 ", notes = "Fake endpoint for testing various parameters \u5047\u7aef\u9ede \u507d\u306e\u30a8\u30f3\u30c9\u30dd\u30a4\u30f3\u30c8 \uac00\uc9dc \uc5d4\ub4dc \ud3ec\uc778\ud2b8 ", response = void.class, authorizations = { @io.swagger.annotations.Authorization(value = "http_basic_test") }, tags = { "fake" }) @io.swagger.annotations.ApiResponses(value = { @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid username supplied", response = void.class), @io.swagger.annotations.ApiResponse(code = 404, message = "User not found", response = void.class) }) public Response testEndpointParameters(@ApiParam(value = "None", required = true) @FormParam(value = "number") BigDecimal number, @ApiParam(value = "None", required = true) @FormParam(value = "double") Double _double, @ApiParam(value = "None", required = true) @FormParam(value = "pattern_without_delimiter") String patternWithoutDelimiter, @ApiParam(value = "None", required = true) @FormParam(value = "byte") byte[] _byte, @ApiParam(value = "None") @FormParam(value = "integer") Integer integer, @ApiParam(value = "None") @FormParam(value = "int32") Integer int32, @ApiParam(value = "None") @FormParam(value = "int64") Long int64, @ApiParam(value = "None") @FormParam(value = "float") Float _float, @ApiParam(value = "None") @FormParam(value = "string") String string, @ApiParam(value = "None") @FormParam(value = "binary") byte[] binary, @ApiParam(value = "None") @FormParam(value = "date") Date date, @ApiParam(value = "None") @FormParam(value = "dateTime") Date dateTime, @ApiParam(value = "None") @FormParam(value = "password") String password, @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.testEndpointParameters(number, _double, patternWithoutDelimiter, _byte, integer, int32, int64, _float, string, binary, date, dateTime, password, securityContext);
  }

  @GET @Consumes(value = { "application/json" }) @Produces(value = { "application/json" }) @io.swagger.annotations.ApiOperation(value = "To test enum parameters", notes = "", response = void.class, tags = { "fake" }) @io.swagger.annotations.ApiResponses(value = { @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid request", response = void.class), @io.swagger.annotations.ApiResponse(code = 404, message = "Not found", response = void.class) }) public Response testEnumParameters(@ApiParam(value = "Form parameter enum test (string array)", allowableValues = ">, $") @FormParam(value = "enum_form_string_array") List<String> enumFormStringArray, @ApiParam(value = "Form parameter enum test (string)", allowableValues = "_abc, -efg, (xyz)", defaultValue = "-efg") @DefaultValue(value = "-efg") @FormParam(value = "enum_form_string") String enumFormString, @ApiParam(value = "Header parameter enum test (string array)", allowableValues = ">, $") @HeaderParam(value = "enum_header_string_array") List<String> enumHeaderStringArray, @ApiParam(value = "Header parameter enum test (string)", allowableValues = "_abc, -efg, (xyz)", defaultValue = "-efg") @HeaderParam(value = "enum_header_string") String enumHeaderString, @ApiParam(value = "Query parameter enum test (string array)", allowableValues = ">, $") @QueryParam(value = "enum_query_string_array") List<String> enumQueryStringArray, @ApiParam(value = "Query parameter enum test (string)", allowableValues = "_abc, -efg, (xyz)", defaultValue = "-efg") @DefaultValue(value = "-efg") @QueryParam(value = "enum_query_string") String enumQueryString, @ApiParam(value = "Query parameter enum test (double)") @QueryParam(value = "enum_query_integer") BigDecimal enumQueryInteger, @ApiParam(value = "Query parameter enum test (double)") @FormParam(value = "enum_query_double") Double enumQueryDouble, @Context SecurityContext securityContext) throws NotFoundException {
    return delegate.testEnumParameters(enumFormStringArray, enumFormString, enumHeaderStringArray, enumHeaderString, enumQueryStringArray, enumQueryString, enumQueryInteger, enumQueryDouble, securityContext);
  }
}