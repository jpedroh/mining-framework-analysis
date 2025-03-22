package io.swagger.configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration @ComponentScan(basePackages = "io.swagger.api") @EnableWebMvc @EnableSwagger2 @PropertySource(value = "classpath:swagger.properties") @Import(value = SwaggerUiConfiguration.class) @javax.annotation.Generated(value = { "class io.swagger.codegen.languages.SpringMVCServerCodegen" }, date = 
<<<<<<< /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/configuration/SwaggerConfig.java/left.java
"2016-04-15T00:38:43.027+08:00"
=======
"2016-04-14T23:14:04.836+08:00"
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/configuration/SwaggerConfig.java/right.java
) public class SwaggerConfig {
  @Bean ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfo("Swagger Petstore", "This is a sample server Petstore server.  You can find out more about Swagger at [http://swagger.io](http://swagger.io) or on [irc.freenode.net, #swagger](http://swagger.io/irc/).  For this sample, you can use the api key `special-key` to test the authorization filters.", "1.0.0", "", "apiteam@swagger.io", "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0.html");
    return apiInfo;
  }

  @Bean public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
  }
}