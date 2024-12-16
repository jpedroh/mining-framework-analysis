package io.swagger.api;

import io.swagger.annotations.*;
import io.swagger.model.Pet;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;


@Api(value = "Pet", description = "the Pet API")
public interface PetApi {
    @ApiOperation(value = "Add a new pet to the store", notes = "", response = java.lang.Void.class, authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }, tags = { "pet" })
    @ApiResponses({ @ApiResponse(code = 405, message = "Invalid input", response = java.lang.Void.class) })
    @RequestMapping(value = "/pet", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> addPet(@ApiParam("Pet object that needs to be added to the store")
    @Valid
    @RequestBody
    Pet body) {
    }

    @ApiOperation(value = "Deletes a pet", notes = "", response = Void.class, authorizations = {
        @Authorization(value = "petstore_auth", scopes = {
            @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"),
            @AuthorizationScope(scope = "read:pets", description = "read your pets")
            })
    }, tags={ "pet", })
    @ApiResponses(value = { 
        @ApiResponse(code = 400, message = "Invalid pet value", response = Void.class) })
    @RequestMapping(value = "/pet/{petId}",
        produces = "application/json",
        consumes = "application/json",
        method = RequestMethod.DELETE)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> deletePet(@ApiParam(value = "Pet id to delete",required=true ) @PathVariable("petId") Long petId,@ApiParam(value = "" ) @RequestHeader(value="api_key", required=false) String apiKey);

    @ApiOperation(value = "Finds Pets by status", notes = "Multiple status values can be provided with comma separated strings", response = Pet.class, responseContainer = "List", authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }, tags = { "pet" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = Pet.class), @ApiResponse(code = 400, message = "Invalid status value", response = Pet.class) })
    @RequestMapping(value = "/pet/findByStatus", produces = "application/json", consumes = "application/json", method = RequestMethod.GET)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<List<Pet>>> findPetsByStatus(@ApiParam(value = "Status values that need to be considered for filter", required = "available", allowableValues = "available, pending, sold")
    @RequestParam(defaultValue = "available", value = "status", required = false)
    List<String> status) {
    }

    @ApiOperation(value = "Finds Pets by tags", notes = "Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.", response = Pet.class, responseContainer = "List", authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }, tags = { "pet" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = Pet.class), @ApiResponse(code = 400, message = "Invalid tag value", response = Pet.class) })
    @RequestMapping(value = "/pet/findByTags", produces = "application/json", consumes = "application/json", method = RequestMethod.GET)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<List<Pet>>> findPetsByTags(@ApiParam("Tags to filter by")
    @RequestParam(value = "tags", required = false)
    List<String> tags) {
    }

    @ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10.  ID > 10 or nonintegers will simulate API error conditions", response = Pet.class, authorizations = { @Authorization(authorizations = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }, value = "petstore_auth"), @Authorization("api_key") }, tags = { "pet" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = Pet.class), @ApiResponse(code = 400, message = "Invalid ID supplied", response = Pet.class), @ApiResponse(code = 404, message = "Pet not found", response = Pet.class) })
    @RequestMapping(value = "/pet/{petId}", produces = "application/json", consumes = "application/json", method = RequestMethod.GET)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Pet>> getPetById(@ApiParam(value = "ID of pet that needs to be fetched", required = true)
    @PathVariable("petId")
    Long petId) {
    }

    @ApiOperation(value = "Update an existing pet", notes = "", response = java.lang.Void.class, authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }, tags = { "pet" })
    @ApiResponses({ @ApiResponse(code = 400, message = "Invalid ID supplied", response = java.lang.Void.class), @ApiResponse(code = 404, message = "Pet not found", response = java.lang.Void.class), @ApiResponse(code = 405, message = "Validation exception", response = java.lang.Void.class) })
    @RequestMapping(value = "/pet", produces = "application/json", consumes = "application/json", method = RequestMethod.PUT)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> updatePet(@ApiParam("Pet object that needs to be added to the store")
    @Valid
    @RequestBody
    Pet body) {
    }

    @ApiOperation(value = "Updates a pet in the store with form data", notes = "", response = java.lang.Void.class, authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }, tags = { "pet" })
    @ApiResponses({ @ApiResponse(code = 405, message = "Invalid input", response = java.lang.Void.class) })
    @RequestMapping(value = "/pet/{petId}", produces = "application/json", consumes = "application/x-www-form-urlencoded", method = RequestMethod.POST)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> updatePetWithForm(@ApiParam(value = "ID of pet that needs to be updated", required = true)
    @PathVariable("petId")
    String petId, @ApiParam("Updated name of the pet")
    @RequestParam(value = "name", required = false)
    String name, @ApiParam("Updated status of the pet")
    @RequestParam(value = "status", required = false)
    String status) {
    }

    @ApiOperation(value = "uploads an image", notes = "", response = java.lang.Void.class, authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }, tags = { "pet" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = java.lang.Void.class) })
    @RequestMapping(value = "/pet/{petId}/uploadImage", produces = "application/json", consumes = "multipart/form-data", method = RequestMethod.POST)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> uploadFile(@ApiParam(value = "ID of pet to update", required = true)
    @PathVariable("petId")
    Long petId, @ApiParam("Additional data to pass to server")
    @RequestParam(value = "additionalMetadata", required = false)
    String additionalMetadata, @ApiParam("file detail")
    @RequestParam("file")
    MultipartFile file) {
    }
}