package org.openapitools.virtualan.api;
import org.openapitools.virtualan.model.ModelApiResponse;
import org.openapitools.virtualan.model.Pet;
import java.util.Set;
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

@Validated @VirtualService public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @Tag(name = "pet", description = "Everything about your Pets") @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface PetApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/pet", consumes = { "application/json", "application/xml" }) default @Operation(operationId = "addPet", summary = "Add a new pet to the store", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation"), @ApiResponse(responseCode = "405", description = "Invalid input") }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<Void> addPet(@Valid @RequestBody @Parameter(name = "body", description = "Pet object that needs to be added to the store", required = true) Pet body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.DELETE, value = "/pet/{petId}") default @Operation(operationId = "deletePet", summary = "Deletes a pet", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation"), @ApiResponse(responseCode = "400", description = "Invalid pet value") }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<Void> deletePet(@PathVariable(value = "petId") @Parameter(name = "petId", description = "Pet id to delete", required = true) Long petId, @RequestHeader(value = "api_key", required = false) @Parameter(name = "api_key", description = "") String apiKey) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/pet/findByStatus", produces = { "application/xml", "application/json" }) default @Operation(operationId = "findPetsByStatus", summary = "Finds Pets by status", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/xml", schema = @Schema(implementation = Pet.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class)) }), @ApiResponse(responseCode = "400", description = "Invalid status value") }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<List<Pet>> findPetsByStatus(@NotNull @Valid @RequestParam(value = "status", required = true) @Parameter(name = "status", description = "Status values that need to be considered for filter", required = true) List<String> status) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"default-name\", \"id\" : 6 }, \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
          String exampleString = "<Pet> <id>123456789</id> <name>doggie</name> <photoUrls> <photoUrls>aeiou</photoUrls> </photoUrls> <tags> </tags> <status>aeiou</status> </Pet>";
          ApiUtil.setExampleResponse(request, "application/xml", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/pet/findByTags", produces = { "application/xml", "application/json" }) default @Deprecated @Operation(operationId = "findPetsByTags", summary = "Finds Pets by tags", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/xml", schema = @Schema(implementation = Pet.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class)) }), @ApiResponse(responseCode = "400", description = "Invalid tag value") }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<Set<Pet>> findPetsByTags(@NotNull @Valid @RequestParam(value = "tags", required = true) @Parameter(name = "tags", description = "Tags to filter by", required = true) Set<String> tags) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"default-name\", \"id\" : 6 }, \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
          String exampleString = "<Pet> <id>123456789</id> <name>doggie</name> <photoUrls> <photoUrls>aeiou</photoUrls> </photoUrls> <tags> </tags> <status>aeiou</status> </Pet>";
          ApiUtil.setExampleResponse(request, "application/xml", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/pet/{petId}", produces = { "application/xml", "application/json" }) default @Operation(operationId = "getPetById", summary = "Find pet by ID", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/xml", schema = @Schema(implementation = Pet.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class)) }), @ApiResponse(responseCode = "400", description = "Invalid ID supplied"), @ApiResponse(responseCode = "404", description = "Pet not found") }, security = { @SecurityRequirement(name = "api_key") }) ResponseEntity<Pet> getPetById(@PathVariable(value = "petId") @Parameter(name = "petId", description = "ID of pet to return", required = true) Long petId) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ], \"name\" : \"doggie\", \"id\" : 0, \"category\" : { \"name\" : \"default-name\", \"id\" : 6 }, \"tags\" : [ { \"name\" : \"name\", \"id\" : 1 }, { \"name\" : \"name\", \"id\" : 1 } ], \"status\" : \"available\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
          String exampleString = "<Pet> <id>123456789</id> <name>doggie</name> <photoUrls> <photoUrls>aeiou</photoUrls> </photoUrls> <tags> </tags> <status>aeiou</status> </Pet>";
          ApiUtil.setExampleResponse(request, "application/xml", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.PUT, value = "/pet", consumes = { "application/json", "application/xml" }) default @Operation(operationId = "updatePet", summary = "Update an existing pet", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation"), @ApiResponse(responseCode = "400", description = "Invalid ID supplied"), @ApiResponse(responseCode = "404", description = "Pet not found"), @ApiResponse(responseCode = "405", description = "Validation exception") }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<Void> updatePet(@Valid @RequestBody @Parameter(name = "body", description = "Pet object that needs to be added to the store", required = true) Pet body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/pet/{petId}", consumes = { "application/x-www-form-urlencoded" }) default @Operation(operationId = "updatePetWithForm", summary = "Updates a pet in the store with form data", tags = { "pet" }, responses = { @ApiResponse(responseCode = "405", description = "Invalid input") }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<Void> updatePetWithForm(@PathVariable(value = "petId") @Parameter(name = "petId", description = "ID of pet that needs to be updated", required = true) Long petId, @Valid @Parameter(name = "name", description = "Updated name of the pet") @RequestParam(value = "name", required = false) String name, @Valid @Parameter(name = "status", description = "Updated status of the pet") @RequestParam(value = "status", required = false) String status) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/pet/{petId}/uploadImage", produces = { "application/json" }, consumes = { "multipart/form-data" }) default @Operation(operationId = "uploadFile", summary = "uploads an image", tags = { "pet" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ModelApiResponse.class)) }) }, security = { @SecurityRequirement(name = "petstore_auth", scopes = { "write:pets", "read:pets" }) }) ResponseEntity<ModelApiResponse> uploadFile(@PathVariable(value = "petId") @Parameter(name = "petId", description = "ID of pet to update", required = true) Long petId, @Valid @Parameter(name = "additionalMetadata", description = "Additional data to pass to server") @RequestParam(value = "additionalMetadata", required = false) String additionalMetadata, @RequestPart(value = "file", required = false) @Parameter(name = "file", description = "file to upload") MultipartFile file) {
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