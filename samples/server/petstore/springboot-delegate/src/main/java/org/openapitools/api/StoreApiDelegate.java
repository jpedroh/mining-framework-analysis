package org.openapitools.api;
import java.util.Map;
import org.openapitools.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) interface StoreApiDelegate {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  default ResponseEntity<Void> deleteOrder(String orderId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  default ResponseEntity<Map<String, Integer>> getInventory() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  default ResponseEntity<Order> getOrderById(Long orderId) {
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

  default ResponseEntity<Order> placeOrder(Order body) {
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