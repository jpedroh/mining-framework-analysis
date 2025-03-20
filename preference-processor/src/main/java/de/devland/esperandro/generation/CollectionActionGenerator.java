package de.devland.esperandro.generation;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import de.devland.esperandro.Constants;
import de.devland.esperandro.Utils;
import de.devland.esperandro.annotations.Cached;
import de.devland.esperandro.base.preferences.EsperandroType;
import de.devland.esperandro.base.preferences.MethodInformation;
import de.devland.esperandro.base.preferences.MethodOperation;
import de.devland.esperandro.base.preferences.TypeInformation;
import de.devland.esperandro.base.processing.Environment;

public class CollectionActionGenerator implements MethodGenerator {
  private final String action;

  public CollectionActionGenerator(String action) {
    this.action = action;
  }

  @Override public void generateMethod(TypeSpec.Builder type, MethodInformation methodInformation, Cached cacheAnnotation) {
    String prefName = methodInformation.associatedPreference;
    String setterName = null;
    String getterName = null;
    List<MethodInformation> methods = Environment.currentPreferenceInterface.getMethodsForPreference(prefName);
    for (MethodInformation method : methods) {
      if (method.operation == MethodOperation.GET) {
        getterName = method.methodName;
      }
      if (method.operation == MethodOperation.PUT) {
        setterName = method.methodName;
      }
    }
    TypeInformation preferenceType = Environment.currentPreferenceInterface.getTypeOfPreference(prefName);
    MethodSpec.Builder 
<<<<<<< /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/left.java
    adder = MethodSpec.methodBuilder(methodInformation.getMethodName()).addAnnotation(Override.class).addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(methodInformation.parameterType.getType(), "value").addStatement("$T __pref = this.$L()", preferenceType.getObjectType(), prefName)
=======
    action = MethodSpec.methodBuilder(methodInformation.getMethodName()).addAnnotation(Override.class).addModifiers(Modifier.PUBLIC).returns(methodInformation.returnType.getType()).addParameter(methodInformation.parameterType.getType(), "value").addStatement("$T __pref = this.$L()", preferenceType.getObjectType(), getterName)
>>>>>>> /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/right.java
    ;
    if (preferenceType.getEsperandroType() == EsperandroType.STRINGSET) {

<<<<<<< /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/left.java
      adder
=======
      action
>>>>>>> /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/right.java
      .addStatement("__pref = new java.util.HashSet<String>(__pref)");
    }

<<<<<<< /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/left.java
    adder
=======
    action
>>>>>>> /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/right.java
    .addStatement(
<<<<<<< /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/left.java
    "__pref.$L(value)"
=======
    "boolean result = __pref.$L(value)"
>>>>>>> /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/right.java
    , 
<<<<<<< /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/left.java
    action
=======
    this.action
>>>>>>> /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/right.java
    ).addStatement("this.$L(__pref)", 
<<<<<<< /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/left.java
    prefName
=======
    setterName
>>>>>>> /usr/src/app/output/dkunzler/esperandro/a504784178f32a425ca7bd38f97b9a77a2eb6dfa/preference-processor/src/main/java/de/devland/esperandro/generation/CollectionActionGenerator.java/right.java
    );
    if (methodInformation.returnType.getEsperandroType() == EsperandroType.BOOLEAN) {
      action.addStatement("return result");
    }
    type.addMethod(action.build());
  }
}