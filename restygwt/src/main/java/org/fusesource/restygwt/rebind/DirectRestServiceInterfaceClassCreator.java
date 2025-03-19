package org.fusesource.restygwt.rebind;
import java.lang.annotation.Annotation;
import com.google.gwt.core.ext.GeneratorContext;
import org.fusesource.restygwt.client.RestService;
import com.google.gwt.core.ext.TreeLogger;
import org.fusesource.restygwt.rebind.util.AnnotationCopyUtil;
import com.google.gwt.core.ext.UnableToCompleteException;
import org.fusesource.restygwt.rebind.util.AnnotationUtils;
import com.google.gwt.core.ext.typeinfo.JClassType;
import org.fusesource.restygwt.rebind.util.OnceFirstIterator;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author <a href="mailto:bogdan.mustiata@gmail.com">Bogdan Mustiata</a>
 */
public class DirectRestServiceInterfaceClassCreator extends DirectRestBaseSourceCreator {
  public static final String DIRECT_REST_SERVICE_SUFFIX = "_DirectRestService";

  public DirectRestServiceInterfaceClassCreator(TreeLogger logger, GeneratorContext context, JClassType source) {
    super(logger, context, source, DIRECT_REST_SERVICE_SUFFIX);
  }

  @Override protected ClassSourceFileComposerFactory createComposerFactory() throws UnableToCompleteException {
    Annotation[] annotations = AnnotationUtils.getAnnotationsInTypeHierarchy(source);
    return createClassSourceComposerFactory(JavaSourceCategory.INTERFACE, getAnnotationsAsStringArray(annotations), new String[] { RestService.class.getCanonicalName() });
  }

  @Override protected void generate() throws UnableToCompleteException {
    super.generate();
    for (JMethod method : source.getInheritableMethods()) {
      p(getAnnotationsAsString(method.getAnnotations()));
      p("void " + method.getName() + "(" + getMethodParameters(method) + getMethodCallback(method) + ");");
    }
  }

  private String getMethodParameters(JMethod method) {
    StringBuilder result = new StringBuilder("");
    for (JParameter parameter : method.getParameters()) {
      result.append(getAnnotationsAsString(parameter.getAnnotations())).append(" ").append(parameter.getType().getParameterizedQualifiedSourceName()).append(" ").append(parameter.getName()).append(", ");
    }
    return result.toString();
  }

  private String getMethodCallback(JMethod method) {
    final String returnType = method.getReturnType().getParameterizedQualifiedSourceName();

<<<<<<< /usr/src/app/output/resty-gwt/resty-gwt/867b917c43c32acbdcac55767e7f04334006c866/restygwt/src/main/java/org/fusesource/restygwt/rebind/DirectRestServiceInterfaceClassCreator.java/left.java
    if (method.getReturnType().isPrimitive() != null) {
      JPrimitiveType primitiveType = method.getReturnType().isPrimitive();
      return "org.fusesource.restygwt.client.MethodCallback<" + primitiveType.getQualifiedBoxedSourceName() + "> callback";
    }
=======
    if (isOverlayMethod(method)) {
      return "org.fusesource.restygwt.client.OverlayCallback<" + returnType + "> callback";
    } else {
      return "org.fusesource.restygwt.client.MethodCallback<" + returnType + "> callback";
    }
>>>>>>> /usr/src/app/output/resty-gwt/resty-gwt/867b917c43c32acbdcac55767e7f04334006c866/restygwt/src/main/java/org/fusesource/restygwt/rebind/DirectRestServiceInterfaceClassCreator.java/right.java
  }

  private String getAnnotationsAsString(Annotation[] annotations) {
    StringBuilder result = new StringBuilder("");
    OnceFirstIterator<String> space = new OnceFirstIterator<String>("", " ");
    for (String annotation : getAnnotationsAsStringArray(annotations)) {
      result.append(space.next()).append(annotation);
    }
    return result.toString();
  }

  private String[] getAnnotationsAsStringArray(Annotation[] annotations) {
    String[] result = new String[annotations.length];
    for (int i = 0; i < annotations.length; i++) {
      Annotation annotation = annotations[i];
      result[i] = AnnotationCopyUtil.getAnnotationAsString(annotation);
    }
    return result;
  }
}