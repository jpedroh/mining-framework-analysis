package swagger

import (
    "strings"
    "fmt"
    "encoding/json"
    "errors"
)

type UserApi struct {
    Configuration Configuration
}

func NewUserApi() *UserApi{
    configuration := NewConfiguration()
    return &UserApi {
        Configuration: *configuration,
    }
}

func NewUserApiWithBasePath(basePath string) *UserApi{
    configuration := NewConfiguration()
    configuration.BasePath = basePath
    
    return &UserApi {
        Configuration: *configuration,
    }
}

/**
 * Create user
 * This can only be done by the logged in user.
 * @param body Created user object
 * @return void
 */
func (a UserApi) CreateUser (body User) (error) {

    var httpMethod = "Post"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user"

    // verify the required parameter 'body' is set
    if &body == nil {
        return errors.New("Missing required parameter 'body' when calling UserApi->CreateUser")
    }

    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }

// body params
    _sling = _sling.BodyJSON(body)



  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(nil, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return err
}
/**
 * Creates list of users with given input array
 * 
 * @param body List of user object
 * @return void
 */
func (a UserApi) CreateUsersWithArrayInput (body []User) (error) {

    var httpMethod = "Post"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user/createWithArray"

    // verify the required parameter 'body' is set
    if &body == nil {
        return errors.New("Missing required parameter 'body' when calling UserApi->CreateUsersWithArrayInput")
    }

    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }

// body params
    _sling = _sling.BodyJSON(body)



  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(nil, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return err
}
/**
 * Creates list of users with given input array
 * 
 * @param body List of user object
 * @return void
 */
func (a UserApi) CreateUsersWithListInput (body []User) (error) {

    var httpMethod = "Post"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user/createWithList"

    // verify the required parameter 'body' is set
    if &body == nil {
        return errors.New("Missing required parameter 'body' when calling UserApi->CreateUsersWithListInput")
    }

    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }

// body params
    _sling = _sling.BodyJSON(body)



  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(nil, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return err
}
/**
 * Delete user
 * This can only be done by the logged in user.
 * @param username The name that needs to be deleted
 * @return void
 */
func (a UserApi) DeleteUser (username string) (error) {

    var httpMethod = "Delete"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user/{username}"
    path = strings.Replace(path, "{" + "username" + "}", fmt.Sprintf("%v", username), -1)

    // verify the required parameter 'username' is set
    if &username == nil {
        return errors.New("Missing required parameter 'username' when calling UserApi->DeleteUser")
    }

    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }




  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(nil, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return err
}
/**
 * Get user by user name
 * 
 * @param username The name that needs to be fetched. Use user1 for testing. 
 * @return User
 */
func (a UserApi) GetUserByName (username string) (User, error) {

    var httpMethod = "Get"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user/{username}"
    path = strings.Replace(path, "{" + "username" + "}", fmt.Sprintf("%v", username), -1)

    // verify the required parameter 'username' is set
    if &username == nil {
        return *new(User), errors.New("Missing required parameter 'username' when calling UserApi->GetUserByName")
    }

    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }


  var successPayload = new(User)

  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(successPayload, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return *successPayload, err
}
/**
 * Logs user into the system
 * 
 * @param username The user name for login
 * @param password The password for login in clear text
 * @return string
 */
func (a UserApi) LoginUser (username string, password string) (string, error) {

    var httpMethod = "Get"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user/login"

    // verify the required parameter 'username' is set
    if &username == nil {
        return *new(string), errors.New("Missing required parameter 'username' when calling UserApi->LoginUser")
    }
    // verify the required parameter 'password' is set
    if &password == nil {
        return *new(string), errors.New("Missing required parameter 'password' when calling UserApi->LoginUser")
    }

    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    
    queryParams["Username"] =  username
    queryParams["Password"] =  password

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }


  var successPayload = new(string)

  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(successPayload, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return *successPayload, err
}
/**
 * Logs out current logged in user session
 * 
 * @return void
 */
func (a UserApi) LogoutUser () (error) {

    var httpMethod = "Get"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user/logout"


    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }




  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(nil, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return err
}
/**
 * Updated user
 * This can only be done by the logged in user.
 * @param username name that need to be deleted
 * @param body Updated user object
 * @return void
 */
func (a UserApi) UpdateUser (username string, body User) (error) {

    var httpMethod = "Put"
        // create path and map variables
    path := c.Configuration.BasePath + "/v2/user/{username}"
    path = strings.Replace(path, "{" + "username" + "}", fmt.Sprintf("%v", username), -1)

    // verify the required parameter 'username' is set
    if &username == nil {
        return errors.New("Missing required parameter 'username' when calling UserApi->UpdateUser")
    }
    // verify the required parameter 'body' is set
    if &body == nil {
        return errors.New("Missing required parameter 'body' when calling UserApi->UpdateUser")
    }

    headerParams := make(map[string]string)
    queryParams := make(map[string]string)
    formParams := make(map[string]string)
    fileParams := make(map[string]string)
    formBody := make(interface{})

    

    // add default headers if any
    for key := range a.Configuration.DefaultHeader {
        headerParams[key] = a.Configuration.DefaultHeader[key]
    }
    

    // to determine the Content-Type header
    localVarHttpContentTypes := []string {
    }
    //set Content-Type header
    localVarHttpContentType := a.Configuration.ApiClient.SelectHeaderContentType(localVarHttpContentTypes)
    if localVarHttpContentType != "" {    
        headerParams["Content-Type"] = localVarHttpContentType
    }

    // to determine the Accept header
    localVarHttpHeaderAccepts := []string {
        "application/xml", 
        "application/json", 
    }
    //set Accept header
    localVarHttpHeaderAccept := a.Configuration.ApiClient.SelectHeaderAccept(localVarHttpHeaderAccepts)
    if localVarHttpHeaderAccept != "" {  
        headerParams["Accept"] = localVarHttpHeaderAccept
    }

// body params
    _sling = _sling.BodyJSON(body)



  // We use this map (below) so that any arbitrary error JSON can be handled.
  // FIXME: This is in the absence of this Go generator honoring the non-2xx
  // response (error) models, which needs to be implemented at some point.
  var failurePayload map[string]interface{}

  httpResponse, err := a.Configuration.ApiClient.CallApi(path, method, postBody, headerParams, queryParams, formParams, fileParams)
  //httpResponse, err := _sling.Receive(nil, &failurePayload)

  if err == nil {
    // err == nil only means that there wasn't a sub-application-layer error (e.g. no network error)
    if failurePayload != nil {
      // If the failurePayload is present, there likely was some kind of non-2xx status
      // returned (and a JSON payload error present)
      var str []byte
      str, err = json.Marshal(failurePayload)
      if err == nil { // For safety, check for an error marshalling... probably superfluous
        // This will return the JSON error body as a string
        err = errors.New(string(str))
      }
  } else {
    // So, there was no network-type error, and nothing in the failure payload,
    // but we should still check the status code
    if httpResponse == nil {
      // This should never happen...
      err = errors.New("No HTTP Response received.")
    } else if code := httpResponse.StatusCode; 200 > code || code > 299 {
        err = errors.New("HTTP Error: " + string(httpResponse.StatusCode))
      }
    }
  }

  return err
}
