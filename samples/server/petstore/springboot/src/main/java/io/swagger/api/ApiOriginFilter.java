package io.swagger.api;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

@javax.annotation.Generated(value = { "class io.swagger.codegen.languages.SpringBootServerCodegen" }, date = 
<<<<<<< /usr/src/app/output/wordnik/swagger-codegen/6ab6d1fb349203823ae26f36c6e4bc838cf7941b/samples/server/petstore/springboot/src/main/java/io/swagger/api/ApiOriginFilter.java/left.java
"2016-05-05T15:10:34.669+08:00"
=======
"2016-05-05T15:30:42.322+08:00"
>>>>>>> /usr/src/app/output/wordnik/swagger-codegen/6ab6d1fb349203823ae26f36c6e4bc838cf7941b/samples/server/petstore/springboot/src/main/java/io/swagger/api/ApiOriginFilter.java/right.java
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