package org.ez18n.apt.processor;
import static javax.lang.model.SourceVersion.RELEASE_6;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import org.ez18n.apt.Label;
import org.ez18n.apt.LabelBundle;
import org.ez18n.apt.LabelTemplateMethod;
import org.ez18n.apt.base.TemplateAnnotation;
import org.ez18n.apt.base.TemplateParam;

@SupportedAnnotationTypes(value = { "org.ez18n.apt.LabelBundle" }) @SupportedSourceVersion(value = RELEASE_6) public final class CSVReportProcessor extends AbstractProcessor {
  @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) {
      return true;
    }
    final Map<TypeElement, List<LabelTemplateMethod>> labelBundles = new HashMap<TypeElement, List<LabelTemplateMethod>>();
    for (Element element : roundEnv.getElementsAnnotatedWith(LabelBundle.class)) {
      if (element.getKind() != ElementKind.INTERFACE) {
        continue;
      }
      final TypeElement bundleType = (TypeElement) element;
      final List<LabelTemplateMethod> bundleMethods = new ArrayList<LabelTemplateMethod>();
      for (Element enclosedElement : bundleType.getEnclosedElements()) {
        if (enclosedElement.getKind() != ElementKind.METHOD) {
          continue;
        }
        final ExecutableElement method = (ExecutableElement) enclosedElement;
        Label labelAnnotation = enclosedElement.getAnnotation(Label.class);
        final TemplateParam returnType = new TemplateParam(method.getReturnType().toString());
        final boolean deprecated = method.getAnnotation(Deprecated.class) != null;
        final LabelTemplateMethod labelMethod;
        try {
          labelMethod = new LabelTemplateMethod(method.getSimpleName().toString(), deprecated, returnType, labelAnnotation.value(), labelAnnotation.mobile());
        } catch (Throwable t) {
          processingEnv.getMessager().printMessage(Kind.WARNING, t.getMessage(), enclosedElement);
          continue;
        }
        for (VariableElement variable : method.getParameters()) {
          final String paramName = variable.getSimpleName().toString();
          final String paramType = variable.asType().toString();
          final List<TemplateAnnotation> methodeAnnotations = new ArrayList<TemplateAnnotation>();
          for (AnnotationMirror annotationMirrors : variable.getAnnotationMirrors()) {
            methodeAnnotations.add(new TemplateAnnotation(annotationMirrors.getAnnotationType().toString()));
          }
          labelMethod.getParams().add(new TemplateParam(paramType, paramName, methodeAnnotations));
        }
        bundleMethods.add(labelMethod);
      }
      if (bundleMethods.isEmpty()) {
        continue;
      }
      labelBundles.put(bundleType, bundleMethods);
    }
    try {
      final FileObject file = processingEnv.getFiler().createResource(SOURCE_OUTPUT, "", "i18n_report.csv");
      final Writer writer = file.openWriter();
      for (TypeElement bundleType : labelBundles.keySet()) {
        for (LabelTemplateMethod templateMethod : labelBundles.get(bundleType)) {
          writer.write('\"');
          writer.write(bundleType.getQualifiedName().toString());
          writer.write("\";\"");
          writer.write(templateMethod.getName());
          writer.write("\";\"");
          writer.write(new MessageFormat(templateMethod.getBase()).format(new Object[] {  }).replace("\"", "\'\'"));
          writer.write("\";\"");
          writer.write(new MessageFormat(templateMethod.getMobile()).format(new Object[] {  }).replace("\"", "\'\'"));
          writer.write("\"\n");
        }
      }
      writer.close();
      processingEnv.getMessager().printMessage(Kind.NOTE, "written " + file.toUri());
    } catch (FilerException e) {
      return false;
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
    }
    return false;
  }
}