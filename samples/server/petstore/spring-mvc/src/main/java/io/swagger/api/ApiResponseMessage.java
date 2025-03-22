package io.swagger.api;
import javax.xml.bind.annotation.XmlTransient;

@javax.xml.bind.annotation.XmlRootElement @javax.annotation.Generated(value = { "class io.swagger.codegen.languages.SpringMVCServerCodegen" }, date = 
<<<<<<< /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/api/ApiResponseMessage.java/left.java
"2016-04-15T00:38:43.027+08:00"
=======
"2016-04-14T23:14:04.836+08:00"
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/api/ApiResponseMessage.java/right.java
) public class ApiResponseMessage {
  public static final int ERROR = 1;

  public static final int WARNING = 2;

  public static final int INFO = 3;

  public static final int OK = 4;

  public static final int TOO_BUSY = 5;

  int code;

  String type;

  String message;

  public ApiResponseMessage() {
  }

  public ApiResponseMessage(int code, String message) {
    this.code = code;
    switch (code) {
      case ERROR:
      setType("error");
      break;
      case WARNING:
      setType("warning");
      break;
      case INFO:
      setType("info");
      break;
      case OK:
      setType("ok");
      break;
      case TOO_BUSY:
      setType("too busy");
      break;
      default:
      setType("unknown");
      break;
    }
    this.message = message;
  }

  @XmlTransient public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}