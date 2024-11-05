package org.openapitools.virtualan.api;
import java.util.List;
import org.openapitools.virtualan.model.User;
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
import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.annotation.Generated;

@Validated @VirtualService public @Generated(value = { "org.openapitools.codegen.languages.SpringCodegen" }) @Tag(name = "user", description = "Operations about user") @RequestMapping(value = "${openapi.openAPIPetstore.base-path:/v2}") interface UserApi {
  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/user") default @Operation(operationId = "createUser", summary = "Create user", tags = { "user" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation") }) ResponseEntity<Void> createUser(@Valid @RequestBody @Parameter(name = "body", description = "Created user object", required = true) User body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/user/createWithArray") default @Operation(operationId = "createUsersWithArrayInput", summary = "Creates list of users with given input array", tags = { "user" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation") }) ResponseEntity<Void> createUsersWithArrayInput(@Valid @RequestBody @Parameter(name = "body", description = "List of user object", required = true) List<User> body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.POST, value = "/user/createWithList") default @Operation(operationId = "createUsersWithListInput", summary = "Creates list of users with given input array", tags = { "user" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation") }) ResponseEntity<Void> createUsersWithListInput(@Valid @RequestBody @Parameter(name = "body", description = "List of user object", required = true) List<User> body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.DELETE, value = "/user/{username}") default @Operation(operationId = "deleteUser", summary = "Delete user", tags = { "user" }, responses = { @ApiResponse(responseCode = "400", description = "Invalid username supplied"), @ApiResponse(responseCode = "404", description = "User not found") }) ResponseEntity<Void> deleteUser(@PathVariable(value = "username") @Parameter(name = "username", description = "The name that needs to be deleted", required = true) String username) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/user/{username}", produces = { "application/xml", "application/json" }) default @Operation(operationId = "getUserByName", summary = "Get user by user name", tags = { "user" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/xml", schema = @Schema(implementation = User.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }), @ApiResponse(responseCode = "400", description = "Invalid username supplied"), @ApiResponse(responseCode = "404", description = "User not found") }) ResponseEntity<User> getUserByName(@PathVariable(value = "username") @Parameter(name = "username", description = "The name that needs to be fetched. Use user1 for testing.", required = true) String username) {
    getRequest().ifPresent((request) -> {
      for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
          String exampleString = "{ \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"password\" : \"password\", \"userStatus\" : 6, \"phone\" : \"phone\", \"id\" : 0, \"email\" : \"email\", \"username\" : \"username\" }";
          ApiUtil.setExampleResponse(request, "application/json", exampleString);
          break;
        }
        if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
          String exampleString = "<User> <id>123456789</id> <username>aeiou</username> <firstName>aeiou</firstName> <lastName>aeiou</lastName> <email>aeiou</email> <password>aeiou</password> <phone>aeiou</phone> <userStatus>123</userStatus> </User>";
          ApiUtil.setExampleResponse(request, "application/xml", exampleString);
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/user/login", produces = { "application/xml", "application/json" }) default @Operation(operationId = "loginUser", summary = "Logs user into the system", tags = { "user" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation", content = { @Content(mediaType = "application/xml", schema = @Schema(implementation = String.class)), @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }), @ApiResponse(responseCode = "400", description = "Invalid username/password supplied") }) ResponseEntity<String> loginUser(@NotNull @Valid @RequestParam(value = "username", required = true) @Parameter(name = "username", description = "The user name for login", required = true) String username, @NotNull @Valid @RequestParam(value = "password", required = true) @Parameter(name = "password", description = "The password for login in clear text", required = true) String password) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.GET, value = "/user/logout") default @Operation(operationId = "logoutUser", summary = "Logs out current logged in user session", tags = { "user" }, responses = { @ApiResponse(responseCode = "200", description = "successful operation") }) ResponseEntity<Void> logoutUser() {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @ApiVirtual @RequestMapping(method = RequestMethod.PUT, value = "/user/{username}") default @Operation(operationId = "updateUser", summary = "Updated user", tags = { "user" }, responses = { @ApiResponse(responseCode = "400", description = "Invalid user supplied"), @ApiResponse(responseCode = "404", description = "User not found") }) ResponseEntity<Void> updateUser(@PathVariable(value = "username") @Parameter(name = "username", description = "name that need to be deleted", required = true) String username, @Valid @RequestBody @Parameter(name = "body", description = "Updated user object", required = true) User body) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}