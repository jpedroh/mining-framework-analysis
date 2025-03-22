package io.swagger.api;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

@javax.annotation.Generated(value = { "class io.swagger.codegen.languages.SpringMVCServerCodegen" }, date = 
<<<<<<< /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/api/ApiOriginFilter.java/left.java
"2016-04-15T00:38:43.027+08:00"
=======
"2016-04-14T23:14:04.836+08:00"
>>>>>>> /usr/src/app/output/openapitools/openapi-generator/a0b429d69050b57653cad214daaa352685e8c449/samples/server/petstore/spring-mvc/src/main/java/io/swagger/api/ApiOriginFilter.java/right.java
) public class ApiOriginFilter implements javax.servlet.Filter {
  @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse res = (HttpServletResponse) response;
    res.addHeader("Access-Control-Allow-Origin", "*");
    res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    res.addHeader("Access-Control-Allow-Headers", "Content-Type");
    chain.doFilter(request, response);
  }

  @Override public void destroy() {
  }

  @Override public void init(FilterConfig filterConfig) throws ServletException {
  }
}