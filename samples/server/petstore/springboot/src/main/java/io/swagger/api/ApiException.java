package io.swagger.api;

@javax.annotation.Generated(value = { "class io.swagger.codegen.languages.SpringBootServerCodegen" }, date = 
<<<<<<< /usr/src/app/output/swagger-api/swagger-codegen/5a7e0fd1816abb4f59a185685d5c4d53eac1efb3/samples/server/petstore/springboot/src/main/java/io/swagger/api/ApiException.java/left.java
"2016-05-05T15:10:34.669+08:00"
=======
"2016-05-05T15:30:42.322+08:00"
>>>>>>> /usr/src/app/output/swagger-api/swagger-codegen/5a7e0fd1816abb4f59a185685d5c4d53eac1efb3/samples/server/petstore/springboot/src/main/java/io/swagger/api/ApiException.java/right.java
) public class ApiException extends Exception {
  private int code;

  public ApiException(int code, String msg) {
    super(msg);
    this.code = code;
  }
}