package org.openapitools.virtualan.api;
import org.openapitools.virtualan.model.Client;
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
import javax.annotation.Generated;

@Validated @VirtualService public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @Tag(name = "another-fake", description = "the another-fake API") @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface AnotherFakeApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.PATCH, value = "/another-fake/dummy", produces = { "application/json" }, consumes = { "application/json" }) default @Operation(operationId = "call123testSpecialTags", summary = "To test special tags", tags = { "$another-fake?" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Client.class)) }) }) ResponseEntity<Client> call123testSpecialTags(@Valid @RequestBody @Parameter(name = "body", description = "client model", required = true) Client body) {
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