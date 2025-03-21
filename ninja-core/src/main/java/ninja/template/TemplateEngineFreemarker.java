package ninja.template;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import ninja.Context;
import javax.inject.Singleton;
import ninja.Result;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaConstant;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;
import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.Version;

@Singleton public class TemplateEngineFreemarker implements TemplateEngine {
  static {
    try {
      freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_SLF4J);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private final Version INCOMPATIBLE_IMPROVEMENTS_VERSION = new Version(2, 3, 21);

  private final String FILE_SUFFIX = ".ftl.html";

  private final Configuration cfg;

  private final Messages messages;

  private final Lang lang;

  private final TemplateEngineHelper templateEngineHelper;

  private final Logger logger;

  private final TemplateEngineFreemarkerExceptionHandler templateEngineFreemarkerExceptionHandler;

  private final TemplateEngineFreemarkerReverseRouteMethod templateEngineFreemarkerReverseRouteMethod;

  private final TemplateEngineFreemarkerAssetsAtMethod templateEngineFreemarkerAssetsAtMethod;

  private final TemplateEngineFreemarkerWebJarsAtMethod templateEngineFreemarkerWebJarsAtMethod;

  @Inject public TemplateEngineFreemarker(Messages messages, Lang lang, Logger logger, TemplateEngineFreemarkerExceptionHandler templateEngineFreemarkerExceptionHandler, TemplateEngineHelper templateEngineHelper, TemplateEngineManager templateEngineManager, TemplateEngineFreemarkerReverseRouteMethod templateEngineFreemarkerReverseRouteMethod, TemplateEngineFreemarkerAssetsAtMethod templateEngineFreemarkerAssetsAtMethod, TemplateEngineFreemarkerWebJarsAtMethod templateEngineFreemarkerWebJarsAtMethod, NinjaProperties ninjaProperties) throws Exception {
    this.messages = messages;
    this.lang = lang;
    this.logger = logger;
    this.templateEngineFreemarkerExceptionHandler = templateEngineFreemarkerExceptionHandler;
    this.templateEngineHelper = templateEngineHelper;
    this.templateEngineFreemarkerReverseRouteMethod = templateEngineFreemarkerReverseRouteMethod;
    this.templateEngineFreemarkerAssetsAtMethod = templateEngineFreemarkerAssetsAtMethod;
    this.templateEngineFreemarkerWebJarsAtMethod = templateEngineFreemarkerWebJarsAtMethod;
    cfg = new Configuration(INCOMPATIBLE_IMPROVEMENTS_VERSION);
    cfg.setDefaultEncoding(NinjaConstant.UTF_8);
    cfg.setOutputEncoding(NinjaConstant.UTF_8);
    cfg.setLocalizedLookup(false);
    cfg.setTemplateExceptionHandler(templateEngineFreemarkerExceptionHandler);
    String srcDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "java";
    if (ninjaProperties.isDev() && new File(srcDir).exists()) {
      try {
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(srcDir));
        ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(this.getClass(), "/");
        TemplateLoader[] templateLoader = new TemplateLoader[] { fileTemplateLoader, classTemplateLoader };
        MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoader);
        cfg.setTemplateLoader(multiTemplateLoader);
      } catch (IOException e) {
        logger.error("Error Loading Freemarker Template " + srcDir, e);
      }
      cfg.setTemplateUpdateDelay(1);
    } else {
      cfg.setClassForTemplateLoading(this.getClass(), "/");
      cfg.setTemplateUpdateDelay(Integer.MAX_VALUE);
      cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, Integer.MAX_VALUE));
    }
    cfg.setTemplateLoader(new TemplateEngineFreemarkerEscapedLoader(cfg.getTemplateLoader()));
    cfg.setNumberFormat("0.######");
    cfg.setObjectWrapper(createBeansWrapperWithExposedFields());
  }

  @Override public void invoke(Context context, Result result) {
    Object object = result.getRenderable();
    Map map;
    if (object == null) {
      map = Maps.newHashMap();
    } else {
      if (object instanceof Map) {
        map = (Map) object;
      } else {
        String realClassNameLowerCamelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, object.getClass().getSimpleName());
        map = Maps.newHashMap();
        map.put(realClassNameLowerCamelCase, object);
      }
    }
    Optional<String> language = lang.getLanguage(context, Optional.of(result));
    if (language.isPresent()) {
      map.put("lang", language.get());
    }
    if (!context.getSession().isEmpty()) {
      map.put("session", context.getSession().getData());
    }
    map.put("contextPath", context.getContextPath());
    map.put("i18n", new TemplateEngineFreemarkerI18nMethod(messages, context, result));
    Optional<String> requestLang = lang.getLanguage(context, Optional.of(result));
    Locale locale = lang.getLocaleFromStringOrDefault(requestLang);
    map.put("prettyTime", new TemplateEngineFreemarkerPrettyTimeMethod(locale));
    map.put("reverseRoute", templateEngineFreemarkerReverseRouteMethod);
    map.put("assetsAt", templateEngineFreemarkerAssetsAtMethod);
    map.put("webJarsAt", templateEngineFreemarkerWebJarsAtMethod);
    Map<String, String> translatedFlashCookieMap = Maps.newHashMap();
    for (Entry<String, String> entry : context.getFlashScope().getCurrentFlashCookieData().entrySet()) {
      String messageValue = null;
      Optional<String> messageValueOptional = messages.get(entry.getValue(), context, Optional.of(result));
      if (!messageValueOptional.isPresent()) {
        messageValue = entry.getValue();
      } else {
        messageValue = messageValueOptional.get();
      }
      translatedFlashCookieMap.put(entry.getKey(), messageValue);
    }
    map.put("flash", translatedFlashCookieMap);
    String templateName = templateEngineHelper.getTemplateForResult(context.getRoute(), result, FILE_SUFFIX);
    Template freemarkerTemplate = null;
    try {
      freemarkerTemplate = cfg.getTemplate(templateName);
    } catch (IOException iOException) {
      logger.error("Error reading Freemarker Template {} ", templateName, iOException);
      throw new RuntimeException(iOException);
    }
    ResponseStreams responseStreams = context.finalizeHeaders(result);
    try (Writer writer = responseStreams.getWriter()) {
      freemarkerTemplate.process(map, writer);
    } catch (Exception e) {
      logger.error("Error processing Freemarker Template {} ", templateName, e);
      throw new RuntimeException(e);
    }
  }

  @Override public String getContentType() {
    return "text/html";
  }

  @Override public String getSuffixOfTemplatingEngine() {
    return FILE_SUFFIX;
  }

  private BeansWrapper createBeansWrapperWithExposedFields() {
    DefaultObjectWrapperBuilder defaultObjectWrapperBuilder = new DefaultObjectWrapperBuilder(INCOMPATIBLE_IMPROVEMENTS_VERSION);
    defaultObjectWrapperBuilder.setExposeFields(true);
    DefaultObjectWrapper defaultObjectWrapper = defaultObjectWrapperBuilder.build();
    return defaultObjectWrapper;
  }
}