package ninja.params;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import ninja.Context;
import ninja.RoutingException;
import ninja.params.ParamParsers.ArrayParamParser;
import ninja.validation.Validator;
import ninja.validation.WithValidator;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import java.lang.reflect.Type;
import java.util.Optional;
import ninja.exceptions.BadRequestException;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invokes methods on the controller, extracting arguments out
 *
 * @author James Roper
 */
public class ControllerMethodInvoker {
  private final static Logger logger = LoggerFactory.getLogger(ControllerMethodInvoker.class);

  private final Method method;

  private final ArgumentExtractor<?>[] argumentExtractors;

  private final boolean useStrictArgumentExtractors;

  private static boolean nonStrictModeWarningLoggedAlready = false;

  ControllerMethodInvoker(Method method, ArgumentExtractor<?>[] argumentExtractors, boolean useStrictArgumentExtractors) {
    this.method = method;
    this.argumentExtractors = argumentExtractors;
    this.useStrictArgumentExtractors = useStrictArgumentExtractors;
  }

  public Object invoke(Object controller, Context context) {
    Object[] arguments = new Object[argumentExtractors.length];
    for (int i = 0; i < argumentExtractors.length; i++) {
      arguments[i] = argumentExtractors[i].extract(context);
    }
    checkNullArgumentsAndThrowBadRequestExceptionIfConfigured(arguments);
    try {
      return method.invoke(controller, arguments);
    } catch (IllegalAccessException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new RuntimeException(e.getCause());
      }
    }
  }

  private void checkNullArgumentsAndThrowBadRequestExceptionIfConfigured(Object[] arguments) {
    if (!useStrictArgumentExtractors) {
      return;
    }
    for (Object object : arguments) {
      if (object == null) {
        throw new BadRequestException();
      }
    }
  }

  /**
     * Builds an invoker for a functional method.  Understands what parameters
     * to inject and extract based on type and annotations.
     * @param functionalMethod The method to be invoked
     * @param implementationMethod The method to use for determining what
     *      actual parameters and annotations to use for each argument.  Useful
     *      when type/lambda erasure makes the functional interface not reliable
     *      for reflecting.
     * @param injector The guice injector
     * @return An invoker
     */
  public static ControllerMethodInvoker build(Method functionalMethod, Method implementationMethod, Injector injector, NinjaProperties ninjaProperties) {
    final Type[] genericParameterTypes = implementationMethod.getGenericParameterTypes();
    final MethodParameter[] methodParameters = MethodParameter.convertIntoMethodParameters(genericParameterTypes);
    final Annotation[][] paramAnnotations = implementationMethod.getParameterAnnotations();
    ArgumentExtractor<?>[] argumentExtractors = new ArgumentExtractor<?>[methodParameters.length];
    for (int i = 0; i < methodParameters.length; i++) {
      try {
        argumentExtractors[i] = getArgumentExtractor(methodParameters[i], paramAnnotations[i], injector);
      } catch (RoutingException e) {
        throw new RoutingException("Error building argument extractor for parameter " + i + " in method " + implementationMethod.getDeclaringClass().getName() + "." + implementationMethod.getName() + "()", e);
      }
    }
    int bodyAsFound = -1;
    for (int i = 0; i < argumentExtractors.length; i++) {
      if (argumentExtractors[i] == null) {
        if (bodyAsFound > -1) {
          throw new RoutingException("Only one parameter may be deserialised as the body " + implementationMethod.getDeclaringClass().getName() + "." + implementationMethod.getName() + "()\n" + "Extracted parameter is type: " + methodParameters[bodyAsFound].parameterClass.getName() + "\n" + "Extra parmeter is type: " + methodParameters[i].parameterClass.getName());
        } else {
          argumentExtractors[i] = new ArgumentExtractors.BodyAsExtractor(methodParameters[i].parameterClass);
          bodyAsFound = i;
        }
      }
    }
    for (int i = 0; i < argumentExtractors.length; i++) {
      argumentExtractors[i] = validateArgumentWithExtractor(methodParameters[i], paramAnnotations[i], injector, argumentExtractors[i]);
    }
    boolean useStrictArgumentExtractors = determineWhetherToUseStrictArgumentExtractorMode(ninjaProperties);
    return new ControllerMethodInvoker(functionalMethod, argumentExtractors, useStrictArgumentExtractors);
  }

  private static boolean determineWhetherToUseStrictArgumentExtractorMode(NinjaProperties ninjaProperties) {
    boolean useStrictArgumentExtractors = ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false);
    if (useStrictArgumentExtractors == false && nonStrictModeWarningLoggedAlready == false) {
      String message = "Using deprecated non-strict mode for injection of parameters into controller " + "(" + NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS + " = false). " + "This mode will soon be removed from Ninja. Make sure you upgrade your application as soon as possible. " + "More: http://www.ninjaframework.org/documentation/basic_concepts/controllers.html \'A note about null and Optional\'.";
      logger.warn(message);
      nonStrictModeWarningLoggedAlready = true;
    }
    return useStrictArgumentExtractors;
  }

  private static ArgumentExtractor<?> getArgumentExtractor(MethodParameter methodParameter, Annotation[] annotations, Injector injector) {
    ArgumentExtractor<?> extractor = ArgumentExtractors.getExtractorForType(methodParameter.parameterClass);
    if (extractor == null) {
      for (Annotation annotation : annotations) {
        WithArgumentExtractors withArgumentExtractors = annotation.annotationType().getAnnotation(WithArgumentExtractors.class);
        if (withArgumentExtractors != null) {
          for (Class<? extends ArgumentExtractor<?>> argumentExtractor : withArgumentExtractors.value()) {
            Class<?> extractedType = (Class<?>) ((ParameterizedType) (argumentExtractor.getGenericInterfaces()[0])).getActualTypeArguments()[0];
            if (methodParameter.parameterClass.isAssignableFrom(extractedType)) {
              extractor = instantiateComponent(argumentExtractor, annotation, methodParameter.parameterClass, injector);
              break;
            }
          }
        }
      }
    }
    if (extractor == null) {
      for (Annotation annotation : annotations) {
        WithArgumentExtractor withArgumentExtractor = annotation.annotationType().getAnnotation(WithArgumentExtractor.class);
        if (withArgumentExtractor != null) {
          extractor = instantiateComponent(withArgumentExtractor.value(), annotation, methodParameter.parameterClass, injector);
          break;
        }
      }
    }
    return extractor;
  }

  private static ArgumentExtractor<?> validateArgumentWithExtractor(MethodParameter methodParameter, Annotation[] annotations, Injector injector, ArgumentExtractor<?> extractor) {
    List<Validator<?>> preParseValidators = new ArrayList<>();
    List<Validator<?>> postParseValidators = new ArrayList<>();
    Class<?> boxedParamType = methodParameter.parameterClass;
    if (methodParameter.parameterClass.isPrimitive()) {
      boxedParamType = box(methodParameter.parameterClass);
    }
    for (Annotation annotation : annotations) {
      WithValidator withValidator = annotation.annotationType().getAnnotation(WithValidator.class);
      if (withValidator != null) {
        Validator<?> validator = instantiateComponent(withValidator.value(), annotation, methodParameter.parameterClass, injector);
        if (validator.getValidatedType().isAssignableFrom(extractor.getExtractedType())) {
          preParseValidators.add(validator);
        } else {
          if (validator.getValidatedType().isAssignableFrom(boxedParamType)) {
            postParseValidators.add(validator);
          } else {
            throw new RoutingException("Validator for field " + extractor.getFieldName() + " validates type " + validator.getValidatedType() + ", which doesn\'t match extracted type " + extractor.getExtractedType() + " or parameter type " + methodParameter.parameterClass);
          }
        }
      }
    }
    if (!preParseValidators.isEmpty()) {
      extractor = new ValidatingArgumentExtractor(extractor, preParseValidators);
    }
    if (!boxedParamType.isAssignableFrom(extractor.getExtractedType())) {
      if (extractor.getFieldName() != null) {
        if (String.class.isAssignableFrom(extractor.getExtractedType())) {
          ParamParser<?> parser = injector.getInstance(ParamParsers.class).getParamParser(methodParameter.parameterClass);
          if (parser == null) {
            throw new RoutingException("Can\'t find parameter parser for type " + extractor.getExtractedType() + " on field " + extractor.getFieldName());
          } else {
            extractor = new ParsingArgumentExtractor(extractor, parser);
          }
        } else {
          if (String[].class.isAssignableFrom(extractor.getExtractedType())) {
            ArrayParamParser<?> parser = injector.getInstance(ParamParsers.class).getArrayParser(methodParameter.parameterClass);
            if (parser == null) {
              throw new RoutingException("Can\'t find parameter array parser for type " + extractor.getExtractedType() + " on field " + extractor.getFieldName());
            } else {
              extractor = new ParsingArrayExtractor(extractor, parser);
            }
          } else {
            throw new RoutingException("Extracted type " + extractor.getExtractedType() + " for field " + extractor.getFieldName() + " doesn\'t match parameter type " + methodParameter.parameterClass);
          }
        }
      }
    }
    if (!postParseValidators.isEmpty()) {
      extractor = new ValidatingArgumentExtractor(extractor, postParseValidators);
    }
    if (methodParameter.isOptional) {
      extractor = new OptionalArgumentExtractor(extractor);
    }
    return extractor;
  }

  private static <T extends java.lang.Object> T instantiateComponent(Class<? extends T> argumentExtractor, final Annotation annotation, final Class<?> paramType, Injector injector) {
    Constructor noarg = getNoArgConstructor(argumentExtractor);
    if (noarg != null) {
      try {
        return (T) noarg.newInstance();
      } catch (Exception e) {
        throw new RoutingException(e);
      }
    }
    Constructor simple = getSingleArgConstructor(argumentExtractor, annotation.annotationType());
    if (simple != null) {
      try {
        return (T) simple.newInstance(annotation);
      } catch (Exception e) {
        throw new RoutingException(e);
      }
    }
    Constructor simpleClass = getSingleArgConstructor(argumentExtractor, Class.class);
    if (simpleClass != null) {
      try {
        return (T) simpleClass.newInstance(paramType);
      } catch (Exception e) {
        throw new RoutingException(e);
      }
    }
    return injector.createChildInjector(new AbstractModule() {
      @Override protected void configure() {
        bind((Class<Annotation>) annotation.annotationType()).toInstance(annotation);
        bind(ArgumentClassHolder.class).toInstance(new ArgumentClassHolder(paramType));
      }
    }).getInstance(argumentExtractor);
  }

  private static Constructor getNoArgConstructor(Class<?> clazz) {
    for (Constructor constructor : clazz.getConstructors()) {
      if (constructor.getParameterTypes().length == 0) {
        return constructor;
      }
    }
    return null;
  }

  private static Constructor getSingleArgConstructor(Class<?> clazz, Class<?> arg) {
    for (Constructor constructor : clazz.getConstructors()) {
      if (constructor.getParameterTypes().length == 1) {
        if (constructor.getParameterTypes()[0].isAssignableFrom(arg)) {
          return constructor;
        }
      }
    }
    return null;
  }

  private static Class<?> box(Class<?> typeToBox) {
    if (typeToBox == int.class) {
      return Integer.class;
    } else {
      if (typeToBox == boolean.class) {
        return Boolean.class;
      } else {
        if (typeToBox == long.class) {
          return Long.class;
        } else {
          if (typeToBox == float.class) {
            return Float.class;
          } else {
            if (typeToBox == double.class) {
              return Double.class;
            } else {
              if (typeToBox == byte.class) {
                return Byte.class;
              } else {
                if (typeToBox == short.class) {
                  return Short.class;
                } else {
                  if (typeToBox == char.class) {
                    return Character.class;
                  }
                }
              }
            }
          }
        }
      }
    }
    throw new IllegalArgumentException("Don\'t know how to box type of " + typeToBox);
  }

  private static class MethodParameter {
    public boolean isOptional;

    public Class<?> parameterClass;

    private MethodParameter(Type genericType) {
      try {
        if (genericType instanceof ParameterizedType) {
          ParameterizedType parameterizedType = (ParameterizedType) genericType;
          Class<?> maybeOptional = getClass(parameterizedType.getRawType());
          if (maybeOptional.isAssignableFrom(Optional.class)) {
            isOptional = true;
            parameterClass = getClass(parameterizedType.getActualTypeArguments()[0]);
          }
        }
        if (parameterClass == null) {
          isOptional = false;
          parameterClass = getClass(genericType);
        }
      } catch (Exception e) {
        throw new RuntimeException("Oops. Something went wrong while investigating method parameters for controller class invocation", e);
      }
    }

    public static MethodParameter[] convertIntoMethodParameters(Type[] genericParameterTypes) {
      MethodParameter[] methodParameters = new MethodParameter[genericParameterTypes.length];
      for (int i = 0; i < genericParameterTypes.length; i++) {
        methodParameters[i] = new MethodParameter(genericParameterTypes[i]);
      }
      return methodParameters;
    }

    private Class<?> getClass(Type type) {
      if (type instanceof Class) {
        return (Class<?>) type;
      } else {
        throw new RuntimeException("Oops. That\'s a strange internal Ninja error.\n" + "Seems someone tried to convert a type into a class that is not a real class. ( " + type.getTypeName() + ")");
      }
    }
  }
}