package controllers;
import java.util.HashMap;
import java.util.Map;
import models.Contact;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Router;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.validation.Required;
import ninja.validation.Validation;
import org.slf4j.Logger;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton public class ApplicationController {
  /**
     * This is the system wide logger. You can still use any config you like. Or
     * create your own custom logger.
     * 
     * But often this is just a simple solution:
     */
  @Inject public Logger logger;

  @Inject Lang lang;

  @Inject Messages messages;

  @Inject Router router;

  public Result examples(Context context) {
    logger.info("In example ");
    return Results.html();
  }

  public Result testPage() {
    return Results.html();
  }

  public Result index(Context context) {
    return Results.html();
  }

  public Result userDashboard(@PathParam(value = "email") String email, @PathParam(value = "id") Integer id, Context context) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", Integer.toString(id));
    map.put("email", email);
    String reverseRoute = router.getReverseRoute(ApplicationController.class, "userDashboard", map);
    map.put("reverseRoute", reverseRoute);
    return Results.html().render(map);
  }

  public Result validation(Validation validation, @Param(value = "email") @Required String email) {
    if (validation.hasViolations()) {
      return Results.json().render(validation.getFieldViolations("email"));
    } else {
      return Results.json().render(email);
    }
  }

  public Result redirect(Context context) {
    return Results.redirect("/");
  }

  public Result session(Context context) {
    context.getSessionCookie().put("username", "kevin");
    return Results.html();
  }

  public Result flashSuccess(Context context) {
    Result result = Results.html();
    Optional<String> flashMessage = messages.get("flashSuccess", context, Optional.of(result), "PLACEHOLDER");
    if (flashMessage.isPresent()) {
      context.getFlashCookie().success(flashMessage.get());
    }
    return result;
  }

  public Result flashError(Context context) {
    Result result = Results.html();
    Optional<String> flashMessage = messages.get("flashError", context, Optional.of(result), "PLACEHOLDER");
    if (flashMessage.isPresent()) {
      context.getFlashCookie().error(flashMessage.get());
    }
    return result;
  }

  public Result flashAny(Context context) {
    Result result = Results.html();
    Optional<String> flashMessage = messages.get("flashAny", context, Optional.of(result), "PLACEHOLDER");
    if (flashMessage.isPresent()) {
      context.getFlashCookie().put("any", flashMessage.get());
    }
    return result;
  }

  public Result contactForm(Context context) {
    return Results.html();
  }

  public Result postContactForm(Context context, Contact contact) {
    return Results.html().render(contact);
  }

  public Result htmlEscaping(Context context) {
    String maliciousJavascript = "<script>alert(\'Hello\');</script>";
    return Results.html().render("maliciousJavascript", maliciousJavascript);
  }
}