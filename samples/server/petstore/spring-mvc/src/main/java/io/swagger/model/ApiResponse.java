package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.swagger.annotations.*;

import java.util.Objects;


@ApiModel(description = "")
<<<<<<< /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/model/ApiResponse.java/left.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringMVCServerCodegen", date = "2016-04-15T00:38:43.027+08:00")
||||||| /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/model/ApiResponse.java/base.java
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringMVCServerCodegen", date = "2016-01-22T15:27:38.634-06:00")
=======
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringMVCServerCodegen", date = "2016-04-14T23:14:04.836+08:00")
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/model/ApiResponse.java/right.java
public class ApiResponse  {
  
  private Integer code = null;
  private String type = null;
  private String message = null;

  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("code")
  public Integer getCode() {
    return code;
  }
  public void setCode(Integer code) {
    this.code = code;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("type")
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiResponse apiResponse = (ApiResponse) o;
    return Objects.equals(code, apiResponse.code) &&
        Objects.equals(type, apiResponse.type) &&
        Objects.equals(message, apiResponse.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, type, message);
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiResponse {\n");
    
    sb.append("  code: ").append(code).append("\n");
    sb.append("  type: ").append(type).append("\n");
    sb.append("  message: ").append(message).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
