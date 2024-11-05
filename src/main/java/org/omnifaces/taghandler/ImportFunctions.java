package org.omnifaces.taghandler;
import static org.omnifaces.taghandler.ImportConstants.toClass;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.el.FunctionMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class ImportFunctions extends TagHandler {
  private static final Map<String, Method> FUNCTIONS_CACHE = new ConcurrentHashMap<String, Method>();

  private static final String ERROR_INVALID_VAR = "The \'var\' attribute may not be an EL expression.";

  private String varValue;

  private TagAttribute typeAttribute;

  public ImportFunctions(TagConfig config) {
    super(config);
    TagAttribute var = getAttribute("var");
    if (var != null) {
      if (var.isLiteral()) {
        varValue = var.getValue();
      } else {
        throw new IllegalArgumentException(ERROR_INVALID_VAR);
      }
    }
    typeAttribute = getRequiredAttribute("type");
  }

  @Override public void apply(FaceletContext context, UIComponent parent) throws IOException {
    final String type = typeAttribute.getValue(context);
    final Class<?> cls = toClass(type);
    final String var = (varValue != null) ? varValue : type.substring(type.lastIndexOf('.') + 1);
    final FunctionMapper originalFunctionMapper = context.getFunctionMapper();
    context.setFunctionMapper(new FunctionMapper() {
      @Override public Method resolveFunction(String prefix, String name) {
        if (var.equals(prefix)) {
          String key = cls + "." + name;
          Method function = FUNCTIONS_CACHE.get(key);
          if (function == null) {
            function = findMethod(cls, name);
            if (function == null) {
              throw new IllegalArgumentException(String.format(ERROR_INVALID_FUNCTION, type, name));
            }
            FUNCTIONS_CACHE.put(key, function);
          }
          return function;
        } else {
          return originalFunctionMapper.resolveFunction(prefix, name);
        }
      }
    });
  }

  private static Method findMethod(Class<?> cls, String name) {
    Set<Method> methods = new TreeSet<Method>(new Comparator<Method>() {
      @Override public int compare(Method m1, Method m2) {
        return Integer.valueOf(m1.getParameterTypes().length).compareTo(m2.getParameterTypes().length);
      }
    });
    for (Method method : cls.getDeclaredMethods()) {
      if (method.getName().equals(name) && isPublicStaticNonVoid(method)) {
        methods.add(method);
      }
    }
    return methods.isEmpty() ? null : methods.iterator().next();
  }

  private static boolean isPublicStaticNonVoid(Method method) {
    int modifiers = method.getModifiers();
    return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && method.getReturnType() != void.class;
  }

  private static final String ERROR_INVALID_FUNCTION = "Type \'%s\' does not have the function \'%s\'.";
}