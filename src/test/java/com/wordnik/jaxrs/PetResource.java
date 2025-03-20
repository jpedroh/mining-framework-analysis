package com.wordnik.jaxrs;
import com.sun.jersey.api.core.InjectParam;
import com.wordnik.sample.JavaRestResourceUtil;
import com.wordnik.sample.data.PetData;
import com.wordnik.sample.model.Pet;
import com.wordnik.sample.model.PetName;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = "/pet") @Api(value = "/pet", description = "Operations about pets", authorizations = { @Authorization(value = "petstore_auth", scopes = { @AuthorizationScope(scope = "write:pets", description = "modify pets in your account"), @AuthorizationScope(scope = "read:pets", description = "read your pets") }) }) @Produces(value = { "application/json", "application/xml" }) public class PetResource {
  static PetData petData = new PetData();

  static JavaRestResourceUtil ru = new JavaRestResourceUtil();

  @GET @Path(value = "/{petId : [0-9]}") @ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10.  ID > 10 or nonintegers will simulate API error conditions", response = Pet.class, authorizations = @Authorization(value = "api_key")) @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Pet not found") }) public Response getPetById(@ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true) @PathParam(value = "petId") Long petId) throws com.wordnik.sample.exception.NotFoundException {
    Pet pet = petData.getPetbyId(petId);
    if (null != pet) {
      return Response.ok().entity(pet).build();
    } else {
      throw new com.wordnik.sample.exception.NotFoundException(404, "Pet not found");
    }
  }

  @DELETE @Path(value = "/{petId}") @ApiOperation(value = "Deletes a pet", nickname = "removePet") @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid pet value") }) public Response deletePet(@ApiParam @HeaderParam(value = "api_key") String apiKey, @ApiParam(value = "Pet id to delete", required = true) @PathParam(value = "petId") Long petId) {
    petData.deletePet(petId);
    return Response.ok().build();
  }

  @POST @Consumes(value = { "application/json", "application/xml" }) @ApiOperation(value = "Add a new pet to the store") @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") }) public Response addPet(@ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    Pet updatedPet = petData.addPet(pet);
    return Response.ok().entity(updatedPet).build();
  }

  @PUT @Consumes(value = { "application/json", "application/xml" }) @ApiOperation(value = "Update an existing pet") @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Pet not found"), @ApiResponse(code = 405, message = "Validation exception") }) public Response updatePet(@ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    Pet updatedPet = petData.addPet(pet);
    return Response.ok().entity(updatedPet).build();
  }

  @GET @Path(value = "/pets/{petName}") @ApiOperation(value = "Finds Pets by name", response = Pet.class, responseContainer = "List") @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value") }) public Response findPetByPetName(@ApiParam(value = "petName", required = true) @PathParam(value = "petName") PetName petName) {
    return Response.ok(petData.getPetbyId(1)).build();
  }

  @GET @Path(value = "/findByStatus") @ApiOperation(value = "Finds Pets by status", notes = "Multiple status values can be provided with comma seperated strings", response = Pet.class, responseContainer = "List") @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value") }) public Response findPetsByStatus(@ApiParam(value = "Status values that need to be considered for filter", required = true, defaultValue = "available", allowableValues = "available,pending,sold", allowMultiple = true) @QueryParam(value = "status") String status) {
    return Response.ok(petData.findPetByStatus(status)).build();
  }

  @GET @Path(value = "/findByTags") @ApiOperation(value = "Finds Pets by tags", notes = "Muliple tags can be provided with comma seperated strings. Use tag1, tag2, tag3 for testing.", response = Pet.class, responseContainer = "List") @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid tag value") }) @Deprecated public Response findPetsByTags(@ApiParam(value = "Tags to filter by", required = true, allowMultiple = true) @QueryParam(value = "tags") String tags) {
    return Response.ok(petData.findPetByTags(tags)).build();
  }

  @POST @Path(value = "/{petId}") @Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED }) @ApiOperation(value = "Updates a pet in the store with form data", consumes = MediaType.APPLICATION_FORM_URLENCODED) @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") }) public Response updatePetWithForm(@BeanParam MyBean myBean) {
    System.out.println(myBean.getName());
    System.out.println(myBean.getStatus());
    return Response.ok().entity(new com.wordnik.sample.model.ApiResponse(200, "SUCCESS")).build();
  }

  @POST @Path(value = "/{petId}/testInjectParam") @Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED }) @ApiOperation(value = "Updates a pet in the store with form data", consumes = MediaType.APPLICATION_FORM_URLENCODED) @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") }) public Response updatePetWithFormViaInjectParam(@InjectParam MyBean myBean) {
    System.out.println(myBean.getName());
    System.out.println(myBean.getStatus());
    return Response.ok().entity(new com.wordnik.sample.model.ApiResponse(200, "SUCCESS")).build();
  }

  @ApiOperation(value = "Returns pet", response = Pet.class) @GET @Produces(value = "application/json") public Pet get(@ApiParam(hidden = true, name = "hiddenParameter") @QueryParam(value = "hiddenParameter") String hiddenParameter) {
    return new Pet();
  }

  @ApiOperation(value = "Test pet as json string in query", response = Pet.class) @GET @Path(value = "/test") @Produces(value = "application/json") public Pet test(@ApiParam(value = "describe Pet in json here") @QueryParam(value = "pet") Pet pet) {
    return new Pet();
  }

  @GET @Path(value = "/test/extensions") @Produces(value = "text/plain") @ApiOperation(value = "testExtensions", extensions = { @Extension(name = "firstExtension", properties = { @ExtensionProperty(name = "extensionName1", value = "extensionValue1"), @ExtensionProperty(name = "extensionName2", value = "extensionValue2") }), @Extension(properties = { @ExtensionProperty(name = "extensionName3", value = "extensionValue3") }) }) public Pet testingExtensions() {
    return new Pet();
  }

  @ApiOperation(value = "Test apiimplicitparams", response = Pet.class) @GET @Path(value = "/test/apiimplicitparams") @Produces(value = "application/json") @ApiImplicitParams(value = { @ApiImplicitParam(name = "header-test-name", value = "header-test-value", required = true, dataType = "string", paramType = "header", defaultValue = "z"), @ApiImplicitParam(name = "path-test-name", value = "path-test-value", required = true, dataType = "string", paramType = "path", defaultValue = "path-test-defaultValue"), @ApiImplicitParam(name = "body-test-name", value = "body-test-value", required = true, dataType = "com.wordnik.sample.model.Pet", paramType = "body"), @ApiImplicitParam(name = "form-test-name", value = "form-test-value", allowMultiple = true, required = true, dataType = "string", paramType = "form", defaultValue = "form-test-defaultValue") }) public Pet testapiimplicitparams() {
    return new Pet();
  }

  @ApiOperation(value = "testingHiddenApiOperation", hidden = true) @GET @Produces(value = "application/json") public String testingHiddenApiOperation() {
    return "testingHiddenApiOperation";
  }
}