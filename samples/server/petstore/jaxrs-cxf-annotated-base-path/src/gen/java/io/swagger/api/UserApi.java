package io.swagger.api;
import java.util.List;
import io.swagger.model.User;
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

@Path(value = "/v2") @Api(value = "/", description = "") public interface UserApi {
  @POST @Path(value = "/user") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Create user", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") }) public void createUser(@Valid User body);

  @POST @Path(value = "/user/createWithArray") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Creates list of users with given input array", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") }) public void createUsersWithArrayInput(@Valid List<User> body);

  @POST @Path(value = "/user/createWithList") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Creates list of users with given input array", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") }) public void createUsersWithListInput(@Valid List<User> body);

  @DELETE @Path(value = "/user/{username}") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Delete user", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username supplied"), @ApiResponse(code = 404, message = "User not found") }) public void deleteUser(@PathParam(value = "username") String username);

  @GET @Path(value = "/user/{username}") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Get user by user name", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = User.class), @ApiResponse(code = 400, message = "Invalid username supplied"), @ApiResponse(code = 404, message = "User not found") }) public User getUserByName(@PathParam(value = "username") String username);

  @GET @Path(value = "/user/login") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Logs user into the system", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation", response = String.class), @ApiResponse(code = 400, message = "Invalid username/password supplied") }) public String loginUser(@QueryParam(value = "username") @NotNull String username, @QueryParam(value = "password") @NotNull String password);

  @GET @Path(value = "/user/logout") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Logs out current logged in user session", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 200, message = "successful operation") }) public void logoutUser();

  @PUT @Path(value = "/user/{username}") @Produces(value = { "application/xml", "application/json" }) @ApiOperation(value = "Updated user", tags = { "user" }) @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid user supplied"), @ApiResponse(code = 404, message = "User not found") }) public void updateUser(@PathParam(value = "username") String username, @Valid User body);
}