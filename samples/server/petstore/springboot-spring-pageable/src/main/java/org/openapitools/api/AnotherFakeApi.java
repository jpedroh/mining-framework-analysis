package org.openapitools.api;
import org.openapitools.model.Client;
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
import javax.annotation.Generated;

@Validated @Api(value = "another-fake", description = "the another-fake API") public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface AnotherFakeApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiOperation(tags = { "$another-fake?" }, value = "To test special tags", nickname = "call123testSpecialTags", notes = "To test special tags and operation ID starting with number", response = Client.class) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Client.class) }) @RequestMapping(method = RequestMethod.PATCH, value = "/another-fake/dummy", produces = { "application/json" }, consumes = { "application/json" }) default ResponseEntity<Client> call123testSpecialTags(@ApiParam(value = "client model", required = true) @Valid @RequestBody Client body) {
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
}