package io.swagger.api;

import io.swagger.annotations.*;
import io.swagger.model.User;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;


@Api(value = "User", description = "the User API")
public interface UserApi {
    @ApiOperation(value = "Create user", notes = "This can only be done by the logged in user.", response = java.lang.Void.class, tags = { "user" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = java.lang.Void.class) })
    @RequestMapping(value = "/user", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> createUser(@ApiParam("Created user object")
    @Valid
    @RequestBody
    User body) {
    }

    @ApiOperation(value = "Creates list of users with given input array", notes = "", response = java.lang.Void.class, tags = { "user" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = java.lang.Void.class) })
    @RequestMapping(value = "/user/createWithArray", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> createUsersWithArrayInput(@ApiParam("List of user object")
    @Valid
    @RequestBody
    List<User> body) {
    }

    @ApiOperation(value = "Creates list of users with given input array", notes = "", response = java.lang.Void.class, tags = { "user" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = java.lang.Void.class) })
    @RequestMapping(value = "/user/createWithList", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> createUsersWithListInput(@ApiParam("List of user object")
    @Valid
    @RequestBody
    List<User> body) {
    }

    @ApiOperation(value = "Delete user", notes = "This can only be done by the logged in user.", response = Void.class, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 400, message = "Invalid username supplied", response = Void.class),
        @ApiResponse(code = 404, message = "User not found", response = Void.class) })
    @RequestMapping(value = "/user/{username}",
        produces = "application/json",
        consumes = "application/json",
        method = RequestMethod.DELETE)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> deleteUser(@ApiParam(value = "The name that needs to be deleted",required=true ) @PathVariable("username") String username);

    @ApiOperation(value = "Get user by user name", notes = "", response = User.class, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = User.class),
        @ApiResponse(code = 400, message = "Invalid username supplied", response = User.class),
        @ApiResponse(code = 404, message = "User not found", response = User.class) })
    @RequestMapping(value = "/user/{username}",
        produces = "application/json",
        consumes = "application/json",
        method = RequestMethod.GET)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<User>> getUserByName(@ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ",required=true ) @PathVariable("username") String username);

    @ApiOperation(value = "Logs user into the system", notes = "", response = java.lang.String.class, tags = { "user" })
    @ApiResponses({ @ApiResponse(code = 200, message = "successful operation", response = java.lang.String.class), @ApiResponse(code = 400, message = "Invalid username/password supplied", response = java.lang.String.class) })
    @RequestMapping(value = "/user/login", produces = "application/json", consumes = "application/json", method = RequestMethod.GET)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<String>> loginUser(@ApiParam("The user name for login")
    @RequestParam(value = "username", required = false)
    String username, @ApiParam("The password for login in clear text")
    @RequestParam(value = "password", required = false)
    String password) {
    }

    @ApiOperation(value = "Logs out current logged in user session", notes = "", response = Void.class, tags={ "user", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful operation", response = Void.class) })
    @RequestMapping(value = "/user/logout",
        produces = "application/json",
        consumes = "application/json",
        method = RequestMethod.GET)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> logoutUser();

    @ApiOperation(value = "Updated user", notes = "This can only be done by the logged in user.", response = java.lang.Void.class, tags = { "user" })
    @ApiResponses({ @ApiResponse(code = 400, message = "Invalid user supplied", response = java.lang.Void.class), @ApiResponse(code = 404, message = "User not found", response = java.lang.Void.class) })
    @RequestMapping(value = "/user/{username}", produces = "application/json", consumes = "application/json", method = RequestMethod.PUT)
    com.netflix.hystrix.HystrixCommand<ResponseEntity<Void>> updateUser(@ApiParam(value = "name that need to be deleted", required = true)
    @PathVariable("username")
    String username, @ApiParam("Updated user object")
    @Valid
    @RequestBody
    User body) {
    }
}