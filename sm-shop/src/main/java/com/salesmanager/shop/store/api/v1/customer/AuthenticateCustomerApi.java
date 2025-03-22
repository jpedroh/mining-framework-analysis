package com.salesmanager.shop.store.api.v1.customer;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.lang.Validate;
import org.apache.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;
import com.salesmanager.shop.store.controller.store.facade.StoreFacade;
import com.salesmanager.shop.store.security.AuthenticationRequest;
import com.salesmanager.shop.store.security.AuthenticationResponse;
import com.salesmanager.shop.store.security.JWTTokenUtil;
import com.salesmanager.shop.store.security.PasswordRequest;
import com.salesmanager.shop.store.security.user.JWTUser;
import com.salesmanager.shop.utils.LanguageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@RestController @RequestMapping(value = "/api/v1") @Api(tags = { "Customer authentication resource (Customer Authentication Api)" }) @SwaggerDefinition(tags = { @Tag(name = "Customer authentication resource", description = "Authenticates customer, register customer and reset customer password") }) public class AuthenticateCustomerApi {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateCustomerApi.class);

  @Value(value = "${authToken.header}") private String tokenHeader;

  @Inject private AuthenticationManager jwtCustomerAuthenticationManager;

  @Inject private JWTTokenUtil jwtTokenUtil;

  @Inject private UserDetailsService jwtCustomerDetailsService;

  @Inject private CustomerFacade customerFacade;

  @Inject private StoreFacade storeFacade;

  @Inject private LanguageUtils languageUtils;

  /**
     * Create new customer for a given MerchantStore, then authenticate that customer
     */
  @RequestMapping(value = { "/auth/register" }, method = RequestMethod.POST, produces = { "application/json" }) @ResponseStatus(value = HttpStatus.CREATED) @ApiOperation(httpMethod = "POST", value = "Registers a customer to the application", notes = "Used as self-served operation", response = AuthenticationResponse.class) @ResponseBody public ResponseEntity<?> register(@Valid @RequestBody PersistableCustomer customer, HttpServletRequest request, HttpServletResponse response, Device device) throws Exception {
    MerchantStore merchantStore = storeFacade.getByCode(request);
    Language language = languageUtils.getRESTLanguage(request, merchantStore);
    customer.setUserName(customer.getEmailAddress());
    Validate.notNull(customer.getUserName(), "Username cannot be null");
    Validate.notNull(customer.getBilling(), "Requires customer Country code");
    Validate.notNull(customer.getBilling().getCountry(), "Requires customer Country code");
    customerFacade.registerCustomer(customer, merchantStore, language);
    Authentication authentication = null;
    try {
      authentication = jwtCustomerAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(customer.getUserName(), customer.getPassword()));
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (authentication == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final JWTUser userDetails = (JWTUser) jwtCustomerDetailsService.loadUserByUsername(customer.getUserName());
    final String token = jwtTokenUtil.generateToken(userDetails, device);
    return ResponseEntity.ok(new AuthenticationResponse(customer.getId(), token));
  }

  /**
     * Authenticate a customer using username & password
     * @param authenticationRequest
     * @param device
     * @return
     * @throws AuthenticationException
     */
  @RequestMapping(value = "/auth/login", method = RequestMethod.POST, produces = { "application/json" }) @ApiOperation(httpMethod = "POST", value = "Authenticates a customer to the application", notes = "Customer can authenticate after registration, request is {\"username\":\"admin\",\"password\":\"password\"}", response = ResponseEntity.class) @ResponseBody public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest, Device device) throws AuthenticationException {
    Authentication authentication = null;
    try {
      authentication = jwtCustomerAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
    } catch (BadCredentialsException unn) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (authentication == null) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final JWTUser userDetails = (JWTUser) jwtCustomerDetailsService.loadUserByUsername(authenticationRequest.getUsername());
    final String token = jwtTokenUtil.generateToken(userDetails, device);
    return ResponseEntity.ok(new AuthenticationResponse(userDetails.getId(), token));
  }

  @RequestMapping(value = "/auth/customer/refresh", method = RequestMethod.GET, produces = { "application/json" }) public ResponseEntity<?> refreshToken(HttpServletRequest request) {
    String token = request.getHeader(tokenHeader);
    String username = jwtTokenUtil.getUsernameFromToken(token);
    JWTUser user = (JWTUser) jwtCustomerDetailsService.loadUserByUsername(username);
    if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
      String refreshedToken = jwtTokenUtil.refreshToken(token);
      return ResponseEntity.ok(new AuthenticationResponse(user.getId(), refreshedToken));
    } else {
      return ResponseEntity.badRequest().body(null);
    }
  }

  @RequestMapping(value = "/auth/customer/password", method = RequestMethod.PUT, produces = { "application/json" }) @ApiOperation(httpMethod = "POST", value = "Change customer password", notes = "Change password request object is {\"username\":\"test@email.com\"}", response = ResponseEntity.class) public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordRequest passwordRequest, HttpServletRequest request) {
    try {
      MerchantStore merchantStore = storeFacade.getByCode(request);
      Customer customer = customerFacade.getCustomerByUserName(passwordRequest.getUsername(), merchantStore);
      if (customer == null) {
        return ResponseEntity.notFound().build();
      }
      if (!customerFacade.passwordMatch(passwordRequest.getCurrent(), customer)) {
        throw new ResourceNotFoundException("Username or password does not match");
      }
      if (!passwordRequest.getPassword().equals(passwordRequest.getRepeatPassword())) {
        throw new ResourceNotFoundException("Both passwords do not match");
      }
      customerFacade.changePassword(customer, passwordRequest.getPassword());
      return ResponseEntity.ok(Void.class);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Exception when reseting password " + e.getMessage());
    }
  }

  @RequestMapping(value = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/left.java
  "/customer/password"
=======
  "/auth/customer/password/reset"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/right.java
  , method = RequestMethod.
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/left.java
  POST
=======
  PUT
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/right.java
  , produces = { "application/json" }) @ApiOperation(httpMethod = "PUT", value = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/left.java
  "Sends a request to reset password"
=======
  "Change customer password"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/right.java
  , notes = 
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/left.java
  "Password reset request is {\"username\":\"test@email.com\"}"
=======
  "Change password request object is {\"username\":\"test@email.com\"}"
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/right.java
  , response = ResponseEntity.class) public ResponseEntity<?> changePassword(
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/left.java
  @RequestBody @Valid PasswordRequest passwordRequest
=======
  @RequestBody @Valid AuthenticationRequest authenticationRequest
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/right.java
  , HttpServletRequest request) {
    try {
      MerchantStore merchantStore = storeFacade.getByCode(request);
      Language language = languageUtils.getRESTLanguage(request, merchantStore);
      Customer customer = customerFacade.getCustomerByUserName(
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/left.java
      passwordRequest
=======
      authenticationRequest
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/right.java
      .getUsername(), merchantStore);
      if (customer == null) {
        return ResponseEntity.notFound().build();
      }
      if (!customerFacade.passwordMatch(passwordRequest.getCurrent(), customer)) {
        throw new ResourceNotFoundException("Username or password does not match");
      }
      if (!passwordRequest.getPassword().equals(passwordRequest.getRepeatPassword())) {
        throw new ResourceNotFoundException("Both passwords do not match");
      }
      customerFacade.
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/left.java
      changePassword(customer, passwordRequest.getPassword())
=======
      resetPassword(customer, merchantStore, language)
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop/src/main/java/com/salesmanager/shop/store/api/v1/customer/AuthenticateCustomerApi.java/right.java
      ;
      return ResponseEntity.ok(Void.class);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Exception when reseting password " + e.getMessage());
    }
  }
}