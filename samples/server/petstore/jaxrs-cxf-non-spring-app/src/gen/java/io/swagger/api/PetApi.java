package io.swagger.api;
import java.io.File;
import io.swagger.model.ModelApiResponse;
import io.swagger.model.Pet;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.jaxrs.PATCH;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Path(value = "/") @Api(value = "/", description = "") public interface PetApi {
  @POST @Path(value = "/pet") @Consumes(value = { "application/json", "application/xml" }) @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Add a new pet to the store", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") }) public void addPet(@Valid Pet body);

  @DELETE @Path(value = "/pet/{petId}") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Deletes a pet", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid pet value") }) public void deletePet(@PathParam(value = "petId") Long petId, @HeaderParam(value = "api_key") String apiKey);

  @GET @Path(value = "/pet/findByStatus") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Finds Pets by status", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Pet.class, responseContainer = "List"), @ApiResponse(code = 400, message = "Invalid status value") }) public List<Pet> findPetsByStatus(@QueryParam(value = "status") @NotNull List<String> status);

  @GET @Path(value = "/pet/findByTags") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Finds Pets by tags", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Pet.class, responseContainer = "List"), @ApiResponse(code = 400, message = "Invalid tag value") }) public List<Pet> findPetsByTags(@QueryParam(value = "tags") @NotNull List<String> tags);

  @GET @Path(value = "/pet/{petId}") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Find pet by ID", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = Pet.class), @ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Pet not found") }) public Pet getPetById(@PathParam(value = "petId") Long petId);

  @PUT @Path(value = "/pet") @Consumes(value = { "application/json", "application/xml" }) @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Update an existing pet", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"), @ApiResponse(code = 404, message = "Pet not found"), @ApiResponse(code = 405, message = "Validation exception") }) public void updatePet(@Valid Pet body);

  @POST @Path(value = "/pet/{petId}") @Consumes(value = { "application/x-www-form-urlencoded" }) @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Updates a pet in the store with form data", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") }) public void updatePetWithForm(@PathParam(value = "petId") Long petId, @Multipart(value = "name", required = false) String name, @Multipart(value = "status", required = false) String status);

  @POST @Path(value = "/pet/{petId}/uploadImage") @Consumes(value = { "multipart/form-data" }) @Produces(value = { "application/json" }) @ApiOperation(value = "uploads an image", tags = { "pet" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = ModelApiResponse.class) }) public ModelApiResponse uploadFile(@PathParam(value = "petId") Long petId, @Multipart(value = "additionalMetadata", required = false) String additionalMetadata, @Multipart(value = "file", required = false) Attachment fileDetail);
}