package ninja.template.directives;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import ninja.Context;
import ninja.utils.NinjaConstant;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@SuppressWarnings(value = { "rawtypes" }) public class TemplateEngineFreemarkerAuthenticityFormDirective implements TemplateDirectiveModel {
  private Context context;

  public TemplateEngineFreemarkerAuthenticityFormDirective(Context context) {
    this.context = context;
  }

  @Override public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
    if (!params.isEmpty()) {
      throw new TemplateModelException("This directive doesn\'t allow parameters.");
    }
    if (loopVars.length != 0) {
      throw new TemplateModelException("This directive doesn\'t allow loop variables.");
    }
    Writer out = env.getOut();
    out.append("<input type=\"hidden\" value=\"" + context.
<<<<<<< /usr/src/app/output/ninjaframework/ninja/fe1237f21fd065e4a834be262563600e8da2f8b4/ninja-core/src/main/java/ninja/template/directives/TemplateEngineFreemarkerAuthenticityFormDirective.java/left.java
    getSession().getAuthenticityToken()
=======
    context.getSession().getAuthenticityToken()
>>>>>>> /usr/src/app/output/ninjaframework/ninja/fe1237f21fd065e4a834be262563600e8da2f8b4/ninja-core/src/main/java/ninja/template/directives/TemplateEngineFreemarkerAuthenticityFormDirective.java/right.java
     + "\" name=\"" + NinjaConstant.AUTHENTICITY_TOKEN + "\" />");
  }
}