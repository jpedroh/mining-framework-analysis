package org.openapitools.api;
import java.util.Map;
import org.openapitools.model.Order;
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
import java.util.Map;
import javax.annotation.Generated;

@Validated @Api(value = "store", description = "Access to Petstore orders") public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface StoreApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiOperation(tags = { "store" }, value = "Delete purchase order by ID", nickname = "deleteOrder", notes = "For valid response try integer IDs with value < 1000. Anything above 1000 or nonintegers will generate API errors") @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Order not found") }) @RequestMapping(method = RequestMethod.DELETE, value = "/store/order/{order_id}") default ResponseEntity<Void> deleteOrder(@ApiParam(value = "ID of the order that needs to be deleted", required = true) @PathVariable(value = "order_id") String orderId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "store" }, value = "Returns pet inventories by status", nickname = "getInventory", notes = "Returns a map of status codes to quantities", response = Integer.class, responseContainer = "Map", authorizations = { @Authorization(value = "api_key") }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Map.class, responseContainer = "Map") }) @RequestMapping(method = RequestMethod.GET, value = "/store/inventory", produces = { "application/json" }) default ResponseEntity<Map<String, Integer>> getInventory() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "store" }, value = "Find purchase order by ID", nickname = "getOrderById", notes = "For valid response try integer IDs with value <= 5 or > 10. Other values will generated exceptions", response = Order.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Order.class), @ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Order not found") }) @RequestMapping(method = RequestMethod.GET, value = "/store/order/{order_id}", produces = { "application/xml", "application/json" }) default ResponseEntity<Order> getOrderById(@Min(value = 1L) @Max(value = 5L) @ApiParam(value = "ID of pet that needs to be fetched", required = true) @PathVariable(value = "order_id") Long orderId) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"petId\" : 6, \"quantity\" : 1, \"id\" : 0, \"shipDate\" : \"2000-01-23T04:56:07.000+00:00\", \"complete\" : false, \"status\" : \"placed\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
          String exampleString = "<Order> <id>123456789</id> <petId>123456789</petId> <quantity>123</quantity> <shipDate>2000-01-23T04:56:07.000Z</shipDate> <status>aeiou</status> <complete>true</complete> </Order>";
          ApiUtil.setExampleResponse(request, "application/xml", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiOperation(tags = { "store" }, value = "Place an order for a pet", nickname = "placeOrder", notes = "", response = Order.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Order.class), @ApiResponse(code = 400, message = "Invalid Order") }) @RequestMapping(method = RequestMethod.POST, value = "/store/order", produces = { "application/xml", "application/json" }) default ResponseEntity<Order> placeOrder(@ApiParam(value = "order placed for purchasing the pet", required = true) @Valid @RequestBody Order body) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"petId\" : 6, \"quantity\" : 1, \"id\" : 0, \"shipDate\" : \"2000-01-23T04:56:07.000+00:00\", \"complete\" : false, \"status\" : \"placed\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
          String exampleString = "<Order> <id>123456789</id> <petId>123456789</petId> <quantity>123</quantity> <shipDate>2000-01-23T04:56:07.000Z</shipDate> <status>aeiou</status> <complete>true</complete> </Order>";
          ApiUtil.setExampleResponse(request, "application/xml", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}