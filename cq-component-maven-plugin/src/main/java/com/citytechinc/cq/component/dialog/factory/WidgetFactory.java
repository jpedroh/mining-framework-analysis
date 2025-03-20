package com.citytechinc.cq.component.dialog.factory;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import org.codehaus.plexus.util.StringUtils;
import com.citytechinc.cq.component.annotations.DialogField;
import com.citytechinc.cq.component.dialog.DialogElement;
import com.citytechinc.cq.component.dialog.exception.InvalidComponentFieldException;
import com.citytechinc.cq.component.dialog.maker.WidgetMaker;
import com.citytechinc.cq.component.dialog.impl.Html5SmartImageWidget;
import com.citytechinc.cq.component.dialog.impl.MultiValueWidget;
import com.citytechinc.cq.component.dialog.impl.Option;
import com.citytechinc.cq.component.dialog.impl.SelectionWidget;

public class WidgetFactory {
  public static final String TEXTFIELD_XTYPE = "textfield";

  public static final String NUMBERFIELD_XTYPE = "numberfield";

  public static final String PATHFIELD_XTYPE = "pathfield";

  public static final String SELECTION_XTYPE = "selection";

  public static final String MULTIFIELD_XTYPE = "multifield";

  public static final String HTML5SMARTIMAGE_XTYPE = "html5smartimage";

  public static DialogElement make(CtClass componentClass, CtField annotatedWidgetField, Field widgetField, Map<Class<?>, String> classToXTypeMap, Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool) throws InvalidComponentFieldException, ClassNotFoundException, CannotCompileException, NotFoundException {
    DialogField propertyAnnotation = (DialogField) annotatedWidgetField.getAnnotation(DialogField.class);
    if (propertyAnnotation == null) {
      throw new InvalidComponentFieldException();
    }
    String xtype = getXTypeForField(widgetField, annotatedWidgetField, propertyAnnotation, classToXTypeMap, classLoader, classPool);
    if (!xTypeToWidgetMakerMap.containsKey(xtype)) {
      throw new InvalidComponentFieldException("xType determined to be " + xtype + " but no Class implementing WidgetMaker is specified for this xtype");
    }
    Class<?> containingClass = classLoader.loadClass(componentClass.getName());

<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (annotatedWidgetField.hasAnnotation(Html5SmartImage.class)) {
      return buildHtml5SmartImageWidget(fieldName, fieldLabel, fieldDescription, isRequired, (Html5SmartImage) annotatedWidgetField.getAnnotation(Html5SmartImage.class), propertyAnnotation);
    }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/dialog/factory/WidgetFactory.java/right.java

    return xTypeToWidgetMakerMap.get(xtype).make(xtype, widgetField, annotatedWidgetField, containingClass, componentClass, classToXTypeMap);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static final MultiValueWidget buildMultiFieldWidget(CtClass componentClass, Field widgetField, DialogField fieldAnnotation, String name, String fieldName, String fieldLabel, String fieldDescription, Boolean isRequired, String defaultValue, Map<String, String> additionalProperties, Map<Class<?>, String> xtypeMap) throws InvalidComponentFieldException {
    String innerXType = getInnerXTypeForMultiField(widgetField, fieldAnnotation, xtypeMap);
    if (innerXType == null) {
      throw new InvalidComponentFieldException("Invalid or unsupported field annotation on a multi valued field");
    }
    BasicFieldConfig fieldConfig = new BasicFieldConfig(innerXType, null);
    return new MultiValueWidget(MULTIFIELD_XTYPE, name, fieldName, fieldLabel, fieldDescription, isRequired, defaultValue, additionalProperties, fieldConfig);
  }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/dialog/factory/WidgetFactory.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static final Html5SmartImageWidget buildHtml5SmartImageWidget(String fieldName, String fieldLabel, String fieldDescription, boolean required, Html5SmartImage smartImage, DialogField dialogField) {
    boolean disableFlush = smartImage.disableFlush();
    boolean disableInfo = smartImage.disableInfo();
    boolean disableZoom = smartImage.disableZoom();
    boolean allowUpload = smartImage.allowUpload();
    String cropParameter = null;
    String fileNameParameter = null;
    String fileReferenceParameter = null;
    String mapParameter = null;
    String rotateParameter = null;
    String uploadUrl = null;
    String ddGroups = null;
    String name = null;
    Integer height = null;
    if (!StringUtils.isEmpty(smartImage.name())) {
      name = smartImage.name();
    }
    if (!StringUtils.isEmpty(smartImage.cropParameter())) {
      cropParameter = smartImage.cropParameter();
    }
    if (!StringUtils.isEmpty(smartImage.fileNameParameter())) {
      fileNameParameter = smartImage.fileNameParameter();
    }
    if (!StringUtils.isEmpty(smartImage.fileReferenceParameter())) {
      fileReferenceParameter = smartImage.fileReferenceParameter();
    }
    if (!StringUtils.isEmpty(smartImage.mapParameter())) {
      mapParameter = smartImage.mapParameter();
    }
    if (!StringUtils.isEmpty(smartImage.rotateParameter())) {
      rotateParameter = smartImage.rotateParameter();
    }
    if (!StringUtils.isEmpty(smartImage.uploadUrl())) {
      uploadUrl = smartImage.uploadUrl();
    }
    if (!StringUtils.isEmpty(smartImage.ddGroups())) {
      ddGroups = smartImage.ddGroups();
    }
    if (smartImage.height() != 0) {
      height = smartImage.height();
    }
    return new Html5SmartImageWidget(name, disableFlush, disableInfo, disableZoom, cropParameter, fileNameParameter, fileReferenceParameter, mapParameter, rotateParameter, uploadUrl, ddGroups, allowUpload, required, fieldLabel, fieldName, fieldDescription, height, smartImage.tab());
  }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/dialog/factory/WidgetFactory.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static final SelectionWidget buildSelectionWidget(CtClass componentClass, CtField widgetField, DialogField fieldAnnotation, String name, String fieldName, String fieldLabel, String fieldDescription, Boolean isRequired, String defaultValue, Map<String, String> additionalProperties, ClassLoader classLoader, ClassPool classPool, Selection selectionAnnotation) throws InvalidComponentFieldException, CannotCompileException, NotFoundException, ClassNotFoundException {
    List<DialogElement> options = buildSelectionOptionsForField(widgetField, selectionAnnotation, classLoader, classPool);
    String selectionType = getSelectionTypeForField(widgetField, selectionAnnotation);
    return new SelectionWidget(selectionType, name, fieldLabel, fieldName, fieldDescription, isRequired, defaultValue, additionalProperties, options);
  }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/dialog/factory/WidgetFactory.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static final List<DialogElement> buildSelectionOptionsForField(CtField widgetField, Selection fieldAnnotation, ClassLoader classLoader, ClassPool classPool) throws InvalidComponentFieldException, CannotCompileException, NotFoundException, ClassNotFoundException {
    List<DialogElement> options = new ArrayList<DialogElement>();
    if (fieldAnnotation != null && fieldAnnotation.options().length > 0) {
      for (com.citytechinc.cq.component.annotations.Option curOptionAnnotation : fieldAnnotation.options()) {
        if (StringUtils.isEmpty(curOptionAnnotation.text()) || StringUtils.isEmpty(curOptionAnnotation.value())) {
          throw new InvalidComponentFieldException("Selection Options specified in the selectionOptions Annotation property must include a non-empty text and value attribute");
        }
        options.add(new Option(curOptionAnnotation.text(), curOptionAnnotation.value()));
      }
    } else {
      if (widgetField.getType().isEnum()) {
        for (Object curEnumObject : classLoader.loadClass(widgetField.getType().getName()).getEnumConstants()) {
          Enum<?> curEnum = (Enum<?>) curEnumObject;
          try {
            options.add(buildSelectionOptionForEnum(curEnum, classPool));
          } catch (SecurityException e) {
            throw new InvalidComponentFieldException("Invalid Enum Field", e);
          } catch (NoSuchFieldException e) {
            throw new InvalidComponentFieldException("Invalid Enum Field", e);
          }
        }
      }
    }
    return options;
  }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/dialog/factory/WidgetFactory.java/right.java



<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static final Option buildSelectionOptionForEnum(Enum<?> optionEnum, ClassPool classPool) throws SecurityException, NoSuchFieldException, NotFoundException, ClassNotFoundException {
    String text = optionEnum.name();
    String value = optionEnum.name();
    CtClass annotatedEnumClass = classPool.getCtClass(optionEnum.getDeclaringClass().getName());
    CtField annotatedEnumField = annotatedEnumClass.getField(optionEnum.name());
    com.citytechinc.cq.component.annotations.Option optionAnnotation = (com.citytechinc.cq.component.annotations.Option) annotatedEnumField.getAnnotation(com.citytechinc.cq.component.annotations.Option.class);
    if (optionAnnotation != null) {
      if (StringUtils.isNotEmpty(optionAnnotation.text())) {
        text = optionAnnotation.text();
      }
      if (StringUtils.isNotEmpty(optionAnnotation.value())) {
        value = optionAnnotation.value();
      }
    }
    return new Option(text, value);
  }
>>>>>>> /usr/src/app/output/citytechinc/cq-component-maven-plugin/5c6c9e155afc295d410d1cc804d79ceb3d3f0027/cq-component-maven-plugin/src/main/java/com/citytechinc/cq/component/dialog/factory/WidgetFactory.java/right.java


  private static final String getXTypeForField(Field widgetField, CtField ctWidgetField, DialogField propertyAnnotation, Map<Class<?>, String> classToXTypeMap, ClassLoader classLoader, ClassPool classPool) throws InvalidComponentFieldException, CannotCompileException, NotFoundException, ClassNotFoundException {
    String overrideXType = propertyAnnotation.xtype();
    if (StringUtils.isNotEmpty(overrideXType)) {
      return overrideXType;
    }
    Class<?> fieldClass = widgetField.getType();
    for (Class<?> curCustomClass : classToXTypeMap.keySet()) {
      if (curCustomClass.isAnnotation()) {
        if (ctWidgetField.hasAnnotation(curCustomClass)) {
          return classToXTypeMap.get(curCustomClass);
        }
      } else {
        if (curCustomClass.isAssignableFrom(fieldClass)) {
          return classToXTypeMap.get(curCustomClass);
        }
      }
    }
    if (Number.class.isAssignableFrom(fieldClass) || fieldClass.equals(int.class) || fieldClass.equals(double.class) || fieldClass.equals(float.class)) {
      return NUMBERFIELD_XTYPE;
    }
    if (fieldClass.equals(String.class)) {
      return TEXTFIELD_XTYPE;
    }
    if (URI.class.isAssignableFrom(fieldClass) || URL.class.isAssignableFrom(fieldClass)) {
      return PATHFIELD_XTYPE;
    }
    if (fieldClass.isEnum()) {
      return SELECTION_XTYPE;
    }
    if (List.class.isAssignableFrom(fieldClass) || fieldClass.isArray()) {
      String simpleXtype = getInnerXTypeForField(widgetField, classToXTypeMap);
      if (simpleXtype == null) {
        throw new InvalidComponentFieldException("Parameterized class for List is not of a supported type.  Currently supported types are numbers, strings, and links");
      }
      return MULTIFIELD_XTYPE;
    }
    return TEXTFIELD_XTYPE;
  }

  private static final String getInnerXTypeForField(Field widgetField, Map<Class<?>, String> xtypeMap) throws InvalidComponentFieldException {
    Class<?> fieldClass = widgetField.getType();
    if (List.class.isAssignableFrom(fieldClass)) {
      return getInnerXTypeForListField(widgetField, xtypeMap);
    }
    if (fieldClass.isArray()) {
      return getInnerXTypeForArrayField(widgetField, xtypeMap);
    }
    throw new InvalidComponentFieldException("List dialog property found with a paramaterized type count not equal to 1");
  }

  private static final String getInnerXTypeForListField(Field widgetField, Map<Class<?>, String> xtypeMap) throws InvalidComponentFieldException {
    ParameterizedType parameterizedType = (ParameterizedType) widgetField.getGenericType();
    if (parameterizedType.getActualTypeArguments().length == 0 || parameterizedType.getActualTypeArguments().length > 1) {
      throw new InvalidComponentFieldException("List dialog property found with a paramaterized type count not equal to 1");
    }
    String simpleXtype = getSimpleXTypeForClass((Class<?>) parameterizedType.getActualTypeArguments()[0], xtypeMap);
    return simpleXtype;
  }

  private static final String getInnerXTypeForArrayField(Field widgetField, Map<Class<?>, String> xtypeMap) {
    Class<?> fieldClass = widgetField.getType();
    return getSimpleXTypeForClass(fieldClass.getComponentType(), xtypeMap);
  }

  private static final String getSimpleXTypeForClass(Class<?> fieldClass, Map<Class<?>, String> xtypeMap) {
    for (Class<?> curCustomClass : xtypeMap.keySet()) {
      if (curCustomClass.isAssignableFrom(fieldClass)) {
        return xtypeMap.get(curCustomClass);
      }
    }
    if (Number.class.isAssignableFrom(fieldClass) || fieldClass.equals(int.class) || fieldClass.equals(double.class) || fieldClass.equals(float.class)) {
      return NUMBERFIELD_XTYPE;
    }
    if (fieldClass.equals(String.class)) {
      return TEXTFIELD_XTYPE;
    }
    if (URI.class.isAssignableFrom(fieldClass) || URL.class.isAssignableFrom(fieldClass)) {
      return PATHFIELD_XTYPE;
    }
    return null;
  }
}