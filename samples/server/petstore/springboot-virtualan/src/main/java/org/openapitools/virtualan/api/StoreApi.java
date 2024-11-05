package org.openapitools.virtualan.api;
import java.util.Map;
import org.openapitools.virtualan.model.Order;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import javax.annotation.Generated;

@Validated @VirtualService public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @Tag(name = "store", description = "Access to Petstore orders") @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface StoreApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.DELETE, value = "/store/order/{order_id}") default @Operation(operationId = "deleteOrder", summary = "Delete purchase order by ID", tags = { "store" }, responses = { @ApiResponse(responseCode = "400", description = "Invalid ID supplied"), @ApiResponse(responseCode = "404", description = "Order not found") }) ResponseEntity<Void> deleteOrder(@PathVariable(value = "order_id") @Parameter(name = "order_id", description = "ID of the order that needs to be deleted", required = true) String orderId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/store/inventory", produces = { "application/json" }) default @Operation(operationId = "getInventory", summary = "Returns pet inventories by status", tags = { "store" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)) }) }, security = { @SecurityRequirement(name = "api_key") }) ResponseEntity<Map<String, Integer>> getInventory() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/store/order/{order_id}", produces = { "application/xml", "application/json" }) default @Operation(operationId = "getOrderById", summary = "Find purchase order by ID", tags = { "store" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/xml", schema = @Schema(implementation = Order.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)) }), @ApiResponse(responseCode = "400", description = "Invalid ID supplied"), @ApiResponse(responseCode = "404", description = "Order not found") }) ResponseEntity<Order> getOrderById(@Min(value = 1L) @Max(value = 5L) @PathVariable(value = "order_id") @Parameter(name = "order_id", description = "ID of pet that needs to be fetched", required = true) Long orderId) {
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

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/store/order", produces = { "application/xml", "application/json" }) default @Operation(operationId = "placeOrder", summary = "Place an order for a pet", tags = { "store" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/xml", schema = @Schema(implementation = Order.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class)) }), @ApiResponse(responseCode = "400", description = "Invalid Order") }) ResponseEntity<Order> placeOrder(@Valid @RequestBody @Parameter(name = "body", description = "order placed for purchasing the pet", required = true) Order body) {
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