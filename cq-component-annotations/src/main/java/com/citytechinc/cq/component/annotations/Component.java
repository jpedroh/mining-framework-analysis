package com.citytechinc.cq.component.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.citytechinc.cq.component.annotations.editconfig.ActionConfig;
import com.citytechinc.cq.component.annotations.editconfig.DropTarget;
import com.citytechinc.cq.component.annotations.editconfig.FormParameter;

@Retention(value = RetentionPolicy.CLASS) @Target(value = { ElementType.TYPE }) public @interface Component {
  String basePath() default "";

  String path() default "";

  String name() default "";

  String value();

  String group() default "";

  boolean isContainer() default false;

  String[] tabs() default {  };

  String[] actions() default {  };

  String dialogMode() default "floating";

  String layout() default "editbar";

  Listener[] listeners() default {  };

  String resourceSuperType() default "";

  String emptyText() default "Drag components or assets here";

  boolean editConfigInherit() default false;

  String fileName() default "dialog";

  boolean editConfig() default true;

  int dialogWidth() default -1;

  int dialogHeight() default -1;

  ActionConfig[] actionConfigs() default {  };

  ContentProperty[] contentAdditionalProperties() default {  };

  boolean inPlaceEditingActive() default true;

  boolean disableTargeting() default false;

  String inPlaceEditingConfigPath() default "";

  String inPlaceEditingEditorType() default "";

  FormParameter[] formParameters() default {  };

  DropTarget[] dropTargets() default {  };
}