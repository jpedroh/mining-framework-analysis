package org.openapitools.api;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.apache.camel.LoggingLevel;
import org.openapitools.model.*;
import org.apache.camel.model.dataformat.JsonLibrary;

@Component public class UserApiRoutesImpl extends RouteBuilder {
  @Override public void configure() throws Exception {
    from("direct:createUser").id("createUser").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}");
    from("direct:createUsersWithArrayInput").id("createUsersWithArrayInput").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}");
    from("direct:createUsersWithListInput").id("createUsersWithListInput").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}");
    from("direct:deleteUser").id("deleteUser").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}");
    from("direct:getUserByName").id("getUserByName").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}").setBody(constant("{ \"firstName\" : \"firstName\", \"lastName\" : \"lastName\", \"password\" : \"password\", \"userStatus\" : 6, \"phone\" : \"phone\", \"id\" : 0, \"email\" : \"email\", \"username\" : \"username\" }")).unmarshal().json(JsonLibrary.Jackson, User.class);
    from("direct:loginUser").id("loginUser").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}");
    from("direct:logoutUser").id("logoutUser").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}");
    from("direct:updateUser").id("updateUser").choice().when(simple("${body} != null")).log(LoggingLevel.INFO, "BODY TYPE: ${body.getClass().getName()}").end().log(LoggingLevel.INFO, "HEADERS: ${headers}");
  }
}