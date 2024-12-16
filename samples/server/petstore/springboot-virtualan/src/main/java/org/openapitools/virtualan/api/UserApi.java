/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.2.1-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openapitools.virtualan.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.virtualan.annotation.ApiVirtual;
import io.virtualan.annotation.VirtualService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.openapitools.virtualan.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;


@Generated("org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Tag(value = "user", description = "Operations about user")
@VirtualService
@RequestMapping("${openapi.openAPIPetstore.base-path:/v2}")
public interface UserApi {
    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /user : Create user
     * This can only be done by the logged in user.
     *
     * @param body Created user object (required)
     * @return successful operation (status code 200)
     */
    @ApiVirtual
    @Operation(nickname = "createUser", tags = { "user" }, value = { @ApiResponse(code = "successful operation", message = "200") }, summary = "Create user")
    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public default ResponseEntity<Void> createUser(@Parameter(value = "body", required = true)
    @Valid
    @RequestBody
    User body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /user/createWithArray : Creates list of users with given input array
     *
     * @param body List of user object (required)
     * @return successful operation (status code 200)
     */
    @ApiVirtual
    @Operation(value = "Creates list of users with given input array", nickname = "createUsersWithArrayInput", tags = { "user" }, responses = { @ApiResponse(notes = "200", description = "successful operation") })
    @RequestMapping(method = RequestMethod.POST, value = "/user/createWithArray")
    public default ResponseEntity<Void> createUsersWithArrayInput(@Parameter(value = "body", required = true)
    @Valid
    @RequestBody
    List<User> body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /user/createWithList : Creates list of users with given input array
     *
     * @param body List of user object (required)
     * @return successful operation (status code 200)
     */
    @ApiVirtual
    @Operation(value = "Creates list of users with given input array", nickname = "createUsersWithListInput", tags = { "user" }, responses = { @ApiResponse(notes = "200", description = "successful operation") })
    @RequestMapping(method = RequestMethod.POST, value = "/user/createWithList")
    public default ResponseEntity<Void> createUsersWithListInput(@Parameter(value = "body", required = true)
    @Valid
    @RequestBody
    List<User> body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /user/{username} : Delete user
     * This can only be done by the logged in user.
     *
     * @param username The name that needs to be deleted (required)
     * @return Invalid username supplied (status code 400)
     *         or User not found (status code 404)
     */
    @ApiVirtual
    @Operation(nickname = "deleteUser", tags = { "user" }, value = { @ApiResponse(code = "Invalid username supplied", message = "400"), @ApiResponse(code = "User not found", message = "404") }, summary = "Delete user")
    @RequestMapping(method = RequestMethod.DELETE, value = "/user/{username}")
    public default ResponseEntity<Void> deleteUser(@Parameter(value = "username", required = true)
    @PathVariable("username")
    String username) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /user/{username} : Get user by user name
     *
     * @param username The name that needs to be fetched. Use user1 for testing. (required)
     * @return successful operation (status code 200)
     *         or Invalid username supplied (status code 400)
     *         or User not found (status code 404)
     */
    @ApiVirtual
    @Operation(tags = { "user" }, operationId = "getUserByName", summary = "Get user by user name", responses = { @ApiResponse(value = "200", description = "successful operation", content = { @Content(nickname = "application/xml", schema = @Schema(response = User.class)), @Content(notes = "application/json", schema = @Schema(response = User.class)) }), @ApiResponse(code = "Invalid username supplied", message = "400"), @ApiResponse(code = "User not found", message = "404") })
    @RequestMapping(method = RequestMethod.GET, value = "/user/{username}", produces = { "application/xml", "application/json" })
    public default ResponseEntity<User> getUserByName(@Parameter(value = "username", required = true)
    @PathVariable("username")
    String username) {
        getRequest().ifPresent(( request) -> {
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

    /**
     * GET /user/login : Logs user into the system
     *
     * @param username The user name for login (required)
     * @param password The password for login in clear text (required)
     * @return successful operation (status code 200)
     *         or Invalid username/password supplied (status code 400)
     */
    @ApiVirtual
    @Operation(tags = { "user" }, operationId = "loginUser", summary = "Logs user into the system", responses = { @ApiResponse(value = "200", description = "successful operation", content = { @Content(nickname = "application/xml", schema = @Schema(response = java.lang.String.class)), @Content(notes = "application/json", schema = @Schema(response = java.lang.String.class)) }), @ApiResponse(code = "Invalid username/password supplied", message = "400") })
    @RequestMapping(method = RequestMethod.GET, value = "/user/login", produces = { "application/xml", "application/json" })
    public default ResponseEntity<String> loginUser(@NotNull
    @Parameter(value = "username", required = true)
    @Valid
    @RequestParam(value = "username", required = true)
    String username, @NotNull
    @Parameter(value = "password", required = true)
    @Valid
    @RequestParam(value = "password", required = true)
    String password) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /user/logout : Logs out current logged in user session
     *
     * @return successful operation (status code 200)
     */
    @ApiVirtual
    @Operation(value = "Logs out current logged in user session", nickname = "logoutUser", tags = { "user" }, responses = { @ApiResponse(notes = "200", description = "successful operation") })
    @RequestMapping(method = RequestMethod.GET, value = "/user/logout")
    public default ResponseEntity<Void> logoutUser() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /user/{username} : Updated user
     * This can only be done by the logged in user.
     *
     * @param username name that need to be deleted (required)
     * @param body Updated user object (required)
     * @return Invalid user supplied (status code 400)
     *         or User not found (status code 404)
     */
    @ApiVirtual
    @Operation(tags = { "user" }, nickname = "updateUser", value = { @ApiResponse(code = "Invalid user supplied", message = "400"), @ApiResponse(code = "User not found", message = "404") }, summary = "Updated user")
    @RequestMapping(method = RequestMethod.PUT, value = "/user/{username}")
    public default ResponseEntity<Void> updateUser(@Parameter(value = "username", required = true)
    @PathVariable("username")
    String username, @Parameter(value = "body", required = true)
    @Valid
    @RequestBody
    User body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}