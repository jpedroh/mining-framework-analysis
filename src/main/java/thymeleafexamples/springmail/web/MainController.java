package thymeleafexamples.springmail.web;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.thymeleaf.util.ClassLoaderUtils;
import static thymeleafexamples.springmail.config.SpringWebInitializer.ENCODING;

@Controller public class MainController {
  private static final String EDITABLE_TEMPLATE = "mail/email-editable.html";

  @RequestMapping(value = { "/", "/index.html" }, method = GET) public String index() {
    return "index";
  }

  @RequestMapping(value = "/text.html", method = GET) public String text() {
    return "text";
  }

  @RequestMapping(value = "/simple.html", method = GET) public String simple() {
    return "simple";
  }

  @RequestMapping(value = "/attachment.html", method = GET) public String attachment() {
    return "attachment";
  }

  @RequestMapping(value = "/inline.html", method = GET) public String inline() {
    return "inline";
  }

  @RequestMapping(value = "/editable.html", method = GET) public String editable(Model model) throws IOException {
    final ClassLoader 
<<<<<<< /usr/src/app/output/thymeleaf/thymeleafexamples-springmail/158fb6406b10311f114054c66f6206dc7e915917/src/main/java/thymeleafexamples/springmail/web/MainController.java/left.java
    classLoader = ClassLoaderUtils.getClassLoader(MainController.class)
=======
    cl = ClassLoaderUtils.getClassLoader(MainController.class)
>>>>>>> /usr/src/app/output/thymeleaf/thymeleafexamples-springmail/158fb6406b10311f114054c66f6206dc7e915917/src/main/java/thymeleafexamples/springmail/web/MainController.java/right.java
    ;
    InputStream inputStream = 
<<<<<<< /usr/src/app/output/thymeleaf/thymeleafexamples-springmail/158fb6406b10311f114054c66f6206dc7e915917/src/main/java/thymeleafexamples/springmail/web/MainController.java/left.java
    classLoader
=======
    cl
>>>>>>> /usr/src/app/output/thymeleaf/thymeleafexamples-springmail/158fb6406b10311f114054c66f6206dc7e915917/src/main/java/thymeleafexamples/springmail/web/MainController.java/right.java
    .getResourceAsStream(EDITABLE_TEMPLATE);
    String baseTemplate = IOUtils.toString(inputStream, ENCODING);
    model.addAttribute("baseTemplate", baseTemplate);
    return "editable";
  }

  @RequestMapping(value = "/sent.html", method = GET) public String sent() {
    return "sent";
  }
}