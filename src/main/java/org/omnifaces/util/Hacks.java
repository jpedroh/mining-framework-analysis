package org.omnifaces.util;
import static org.omnifaces.util.Faces.getApplication;
import static org.omnifaces.util.Faces.getELContext;
import static org.omnifaces.util.FacesLocal.getContextAttribute;
import static org.omnifaces.util.FacesLocal.getInitParameter;
import static org.omnifaces.util.FacesLocal.setContextAttribute;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;
import org.omnifaces.resourcehandler.ResourceIdentifier;
import static org.omnifaces.util.Utils.unmodifiableSet;

public final class Hacks {
  private static final boolean RICHFACES_INSTALLED = initRichFacesInstalled();

  private static final String RICHFACES_RLR_RENDERER_TYPE = "org.richfaces.renderkit.ResourceLibraryRenderer";

  private static final String RICHFACES_RLF_CLASS_NAME = "org.richfaces.resource.ResourceLibraryFactoryImpl";

  private static final boolean JUEL_SUPPORTS_METHOD_EXPRESSION = initJUELSupportsMethodExpression();

  private static final String JUEL_EF_CLASS_NAME = "de.odysseus.el.ExpressionFactoryImpl";

  private static final String JUEL_MINIMUM_METHOD_EXPRESSION_VERSION = "2.2.6";

  private static final String MYFACES_PACKAGE_PREFIX = "org.apache.myfaces.";

  private static final String MYFACES_RENDERED_SCRIPT_RESOURCES_KEY = "org.apache.myfaces.RENDERED_SCRIPT_RESOURCES_SET";

  private static final String MYFACES_RENDERED_STYLESHEET_RESOURCES_KEY = "org.apache.myfaces.RENDERED_STYLESHEET_RESOURCES_SET";

  private static final Set<String> MOJARRA_MYFACES_RESOURCE_DEPENDENCY_KEYS = unmodifiableSet("com.sun.faces.PROCESSED_RESOURCE_DEPENDENCIES", MYFACES_RENDERED_SCRIPT_RESOURCES_KEY, MYFACES_RENDERED_STYLESHEET_RESOURCES_KEY);

  private static final String MOJARRA_DEFAULT_RESOURCE_MAX_AGE = "com.sun.faces.defaultResourceMaxAge";

  private static final String MYFACES_DEFAULT_RESOURCE_MAX_AGE = "org.apache.myfaces.RESOURCE_MAX_TIME_EXPIRES";

  private static final long DEFAULT_RESOURCE_MAX_AGE = 604800000L;

  private static final String[] PARAM_NAMES_RESOURCE_MAX_AGE = { MOJARRA_DEFAULT_RESOURCE_MAX_AGE, MYFACES_DEFAULT_RESOURCE_MAX_AGE };

  private static final String ERROR_MAX_AGE = "The \'%s\' init param must be a number. Encountered an invalid value of \'%s\'.";

  private static final String ERROR_CREATE_INSTANCE = "Cannot create instance of class \'%s\'.";

  private static final String ERROR_ACCESS_FIELD = "Cannot access field \'%s\' of class \'%s\'.";

  private static final String ERROR_INVOKE_METHOD = "Cannot invoke method \'%s\' of class \'%s\' with arguments %s.";

  private static final Object[] EMPTY_PARAMETERS = new Object[0];

  private static Boolean myFacesUsed;

  private static Long defaultResourceMaxAge;

  private Hacks() {
  }

  private static boolean initRichFacesInstalled() {
    for (String richFacesPvcClassName : RICHFACES_PVC_CLASS_NAMES) {
      try {
        Class.forName(richFacesPvcClassName);
        return true;
      } catch (ClassNotFoundException ignore) {
        continue;
      }
    }
    return false;
  }

  private static boolean initJUELSupportsMethodExpression() {
    Package juelPackage = Package.getPackage("de.odysseus.el");
    if (juelPackage == null) {
      return false;
    }
    String juelVersion = juelPackage.getImplementationVersion();
    if (juelVersion == null) {
      return false;
    }
    return isSameOrHigherVersion(juelVersion, JUEL_MINIMUM_METHOD_EXPRESSION_VERSION);
  }

  public static boolean isRichFacesInstalled() {
    return RICHFACES_INSTALLED;
  }

  public static PartialViewContext getRichFacesPartialViewContext() {
    PartialViewContext context = Ajax.getContext();
    while (!RICHFACES_PVC_CLASS_NAMES.contains(context.getClass().getName()) && context instanceof PartialViewContextWrapper) {
      context = ((PartialViewContextWrapper) context).getWrapped();
    }
    if (RICHFACES_PVC_CLASS_NAMES.contains(context.getClass().getName())) {
      return context;
    } else {
      return null;
    }
  }

  public static Collection<String> getRichFacesRenderIds() {
    PartialViewContext richFacesContext = getRichFacesPartialViewContext();
    if (richFacesContext != null) {
      Collection<String> renderIds = accessField(richFacesContext, "componentRenderIds");
      if (renderIds != null) {
        return renderIds;
      }
    }
    return Collections.emptyList();
  }

  public static PartialViewContext getRichFacesWrappedPartialViewContext() {
    PartialViewContext richFacesContext = getRichFacesPartialViewContext();
    if (richFacesContext != null) {
      return accessField(richFacesContext, "wrappedViewContext");
    }
    return null;
  }

  public static boolean isRichFacesResourceLibraryRenderer(String rendererType) {
    return RICHFACES_RLR_RENDERER_TYPE.equals(rendererType);
  }

  @SuppressWarnings(value = { "rawtypes" }) public static Set<ResourceIdentifier> getRichFacesResourceLibraryResources(ResourceIdentifier id) {
    Object resourceFactory = createInstance(RICHFACES_RLF_CLASS_NAME);
    String name = id.getName().split("\\.")[0];
    Object resourceLibrary = invokeMethod(resourceFactory, "getResourceLibrary", name, id.getLibrary());
    Iterable resources = invokeMethod(resourceLibrary, "getResources");
    Set<ResourceIdentifier> resourceIdentifiers = new LinkedHashSet<ResourceIdentifier>();
    for (Object resource : resources) {
      String libraryName = invokeMethod(resource, "getLibraryName");
      String resourceName = invokeMethod(resource, "getResourceName");
      resourceIdentifiers.add(new ResourceIdentifier(libraryName, resourceName));
    }
    return resourceIdentifiers;
  }

  public static boolean isJUELUsed() {
    return isJUELUsed(getApplication().getExpressionFactory());
  }

  public static boolean isJUELUsed(ExpressionFactory factory) {
    return factory.getClass().getName().equals(JUEL_EF_CLASS_NAME);
  }

  public static boolean isJUELSupportingMethodExpression() {
    return JUEL_SUPPORTS_METHOD_EXPRESSION;
  }

  private static boolean isSameOrHigherVersion(String version1, String version2) {
    List<Integer> version1Elements = toVersionElements(version1);
    List<Integer> version2Elements = toVersionElements(version2);
    int maxLength = Math.max(version1Elements.size(), version2Elements.size());
    for (int i = 0; i < maxLength; i++) {
      int version1Element = getVersionElement(version1Elements, i);
      int version2Element = getVersionElement(version2Elements, i);
      if (version1Element > version2Element) {
        return true;
      }
      if (version1Element < version2Element) {
        return false;
      }
    }
    return true;
  }

  private static List<Integer> toVersionElements(String version) {
    List<Integer> versionElements = new ArrayList<Integer>();
    for (String string : version.split("\\.")) {
      versionElements.add(Integer.valueOf(string));
    }
    return versionElements;
  }

  private static int getVersionElement(List<Integer> versionElements, int index) {
    if (index < versionElements.size()) {
      return versionElements.get(index);
    }
    return 0;
  }

  public static Method methodExpressionToStaticMethod(final ELContext context, final MethodExpression methodExpression) {
    MethodInfo methodInfo = methodExpression.getMethodInfo(getELContext());
    try {
      Constructor<Method> methodConstructor = Method.class.getDeclaredConstructor(Class.class, String.class, Class[].class, Class.class, Class[].class, int.class, int.class, String.class, byte[].class, byte[].class, byte[].class);
      methodConstructor.setAccessible(true);
      Method staticMethod = methodConstructor.newInstance(null, methodInfo.getName(), methodInfo.getParamTypes(), methodInfo.getReturnType(), null, 0, 0, null, null, null, null);
      Class<?> methodAccessorClass = Class.forName("sun.reflect.MethodAccessor");
      Object methodAccessor = Proxy.newProxyInstance(Method.class.getClassLoader(), new Class[] { methodAccessorClass }, new InvocationHandler() {
        @Override public Object invoke(Object proxy, Method method, Object[] args) {
          Object[] params = null;
          if (args != null && args.length > 1) {
            params = (Object[]) args[1];
          } else {
            params = EMPTY_PARAMETERS;
          }
          return methodExpression.invoke(context, params);
        }
      });
      Method setMethodAccessor = Method.class.getDeclaredMethod("setMethodAccessor", methodAccessorClass);
      setMethodAccessor.setAccessible(true);
      setMethodAccessor.invoke(staticMethod, methodAccessor);
      Field override = AccessibleObject.class.getDeclaredField("override");
      override.setAccessible(true);
      override.set(staticMethod, true);
      return staticMethod;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static boolean isMyFacesUsed() {
    if (myFacesUsed == null) {
      FacesContext context = FacesContext.getCurrentInstance();
      if (context != null) {
        myFacesUsed = context.getClass().getPackage().getName().startsWith(MYFACES_PACKAGE_PREFIX);
      } else {
        return false;
      }
    }
    return myFacesUsed;
  }

  public static void setScriptResourceRendered(FacesContext context, ResourceIdentifier id) {
    setMojarraResourceRendered(context, id);
    if (isMyFacesUsed()) {
      setMyFacesResourceRendered(context, MYFACES_RENDERED_SCRIPT_RESOURCES_KEY, id);
    }
  }

  public static boolean isScriptResourceRendered(FacesContext context, ResourceIdentifier id) {
    boolean rendered = isMojarraResourceRendered(context, id);
    if (!rendered && isMyFacesUsed()) {
      return isMyFacesResourceRendered(context, MYFACES_RENDERED_SCRIPT_RESOURCES_KEY, id);
    } else {
      return rendered;
    }
  }

  public static void setStylesheetResourceRendered(FacesContext context, ResourceIdentifier id) {
    setMojarraResourceRendered(context, id);
    if (isMyFacesUsed()) {
      setMyFacesResourceRendered(context, MYFACES_RENDERED_STYLESHEET_RESOURCES_KEY, id);
    }
  }

  private static void setMojarraResourceRendered(FacesContext context, ResourceIdentifier id) {
    context.getAttributes().put(id.getName() + id.getLibrary(), true);
  }

  private static boolean isMojarraResourceRendered(FacesContext context, ResourceIdentifier id) {
    return context.getAttributes().containsKey(id.getName() + id.getLibrary());
  }

  private static void setMyFacesResourceRendered(FacesContext context, String key, ResourceIdentifier id) {
    getMyFacesResourceMap(context, key).put(getMyFacesResourceKey(id), true);
  }

  private static boolean isMyFacesResourceRendered(FacesContext context, String key, ResourceIdentifier id) {
    return getMyFacesResourceMap(context, key).containsKey(getMyFacesResourceKey(id));
  }

  private static Map<String, Boolean> getMyFacesResourceMap(FacesContext context, String key) {
    Map<String, Boolean> map = getContextAttribute(context, key);
    if (map == null) {
      map = new HashMap<String, Boolean>();
      setContextAttribute(context, key, map);
    }
    return map;
  }

  private static String getMyFacesResourceKey(ResourceIdentifier id) {
    String library = id.getLibrary();
    String name = id.getName();
    return (library != null) ? (library + '/' + name) : name;
  }

  public static void removeResourceDependencyState(FacesContext context) {
    context.getAttributes().keySet().removeAll(MOJARRA_MYFACES_RESOURCE_DEPENDENCY_KEYS);
    context.getAttributes().values().removeAll(Collections.singleton(true));
  }

  public static long getDefaultResourceMaxAge() {
    if (defaultResourceMaxAge != null) {
      return defaultResourceMaxAge;
    }
    FacesContext context = FacesContext.getCurrentInstance();
    if (context == null) {
      return DEFAULT_RESOURCE_MAX_AGE;
    }
    for (String name : PARAM_NAMES_RESOURCE_MAX_AGE) {
      String value = getInitParameter(context, name);
      if (value != null) {
        try {
          defaultResourceMaxAge = Long.valueOf(value);
          return defaultResourceMaxAge;
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(String.format(ERROR_MAX_AGE, name, value), e);
        }
      }
    }
    defaultResourceMaxAge = DEFAULT_RESOURCE_MAX_AGE;
    return defaultResourceMaxAge;
  }

  public static boolean isPrimeFacesDynamicResourceRequest(FacesContext context) {
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();
    return "primefaces".equals(params.get("ln")) && params.get("pfdrid") != null;
  }

  private static Object createInstance(String className) {
    try {
      return Class.forName(className).newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(ERROR_CREATE_INSTANCE, className), e);
    }
  }

  @SuppressWarnings(value = { "unchecked" }) private static <T extends java.lang.Object> T accessField(Object instance, String fieldName) {
    try {
      Field field = instance.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return (T) field.get(instance);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(ERROR_ACCESS_FIELD, fieldName, instance.getClass()), e);
    }
  }

  @SuppressWarnings(value = { "rawtypes", "unchecked" }) private static <T extends java.lang.Object> T invokeMethod(Object instance, String methodName, Object... parameters) {
    Class[] parameterTypes = new Class[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      parameterTypes[i] = parameters[i].getClass();
    }
    try {
      Method method = instance.getClass().getMethod(methodName, parameterTypes);
      method.setAccessible(true);
      return (T) method.invoke(instance, parameters);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(ERROR_INVOKE_METHOD, methodName, instance.getClass(), Arrays.toString(parameters)), e);
    }
  }

  private static final Set<String> RICHFACES_PVC_CLASS_NAMES = unmodifiableSet("org.richfaces.context.ExtendedPartialViewContextImpl", "org.richfaces.context.ExtendedPartialViewContext");
}