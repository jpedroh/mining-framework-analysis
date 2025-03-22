package io.swagger.configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@javax.annotation.Generated(value = { "class io.swagger.codegen.languages.SpringMVCServerCodegen" }, date = 
<<<<<<< /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/configuration/WebApplication.java/left.java
"2016-04-15T00:38:43.027+08:00"
=======
"2016-04-14T23:14:04.836+08:00"
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/configuration/WebApplication.java/right.java
) public class WebApplication extends AbstractAnnotationConfigDispatcherServletInitializer {
  @Override protected Class<?>[] getRootConfigClasses() {
    return new Class[] { SwaggerConfig.class };
  }

  @Override protected Class<?>[] getServletConfigClasses() {
    return new Class<?>[] { WebMvcConfiguration.class };
  }

  @Override protected String[] getServletMappings() {
    return new String[] { "/" };
  }
}