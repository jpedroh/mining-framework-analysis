package com.fasterxml.jackson.module.jaxb;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.module.jaxb.deser.DataHandlerJsonDeserializer;
import com.fasterxml.jackson.module.jaxb.ser.DataHandlerJsonSerializer;

/**
 * Annotation introspector that leverages JAXB annotations where applicable to JSON mapping.
 * As of Jackson 2.0, most JAXB annotations are supported at least to some degree.
 * Ones that are NOT yet supported are:
 * <ul>
 * <li>{@link XmlAnyAttribute} not yet used (as of 1.5) but may be in future (as an alias for @JsonAnySetter?)
 * <li>{@link XmlAnyElement} not yet used, may be as per [JACKSON-253]
 * <li>{@link javax.xml.bind.annotation.XmlAttachmentRef}: JSON does not support external attachments
 * <li>{@link XmlElementDecl}
 * <li>{@link XmlElementRefs} because Jackson doesn't have any support for 'named' collection items -- however,
 *    this may become partially supported as per [JACKSON-253].
 * <li>{@link javax.xml.bind.annotation.XmlInlineBinaryData} since the underlying concepts
 *    (like XOP) do not exist in JSON -- Jackson will always use inline base64 encoding as the method
 * <li>{@link javax.xml.bind.annotation.XmlList} because JSON does have (or necessarily need)
 *    method of serializing list of values as space-separated Strings
 * <li>{@link javax.xml.bind.annotation.XmlMimeType}
 * <li>{@link javax.xml.bind.annotation.XmlMixed} since JSON has no concept of mixed content
 * <li>{@link XmlRegistry}
 * <li>{@link XmlSchema} not used, unlikely to be used
 * <li>{@link XmlSchemaType} not used, unlikely to be used
 * <li>{@link XmlSchemaTypes} not used, unlikely to be used
 * <li>{@link XmlSeeAlso} not yet supported, but [ISSUE-1] filed to use it, so may be supported.
 * </ul>
 *
 * Note also the following limitations:
 *
 * <ul>
 * <li>Any property annotated with {@link XmlValue} will have implicit property named 'value' on
 *    its JSON object; although (as of 2.4) it should be possible to override this name
 *   </li>
 * </ul>
 *<p>
 * A note on compatibility with Jackson XML module: since this module does not depend
 * on Jackson XML module, it is bit difficult to make sure we will properly expose
 * all information. But effort is made (as of version 2.3.3) to expose this information,
 * even without using a specific sub-class from that project.
 *
 * @author Ryan Heaton
 * @author Tatu Saloranta
 */
public class JaxbAnnotationIntrospector extends AnnotationIntrospector implements Versioned {
  private static final long serialVersionUID = -1L;

  protected final static String DEFAULT_NAME_FOR_XML_VALUE = "value";

  protected final static boolean DEFAULT_IGNORE_XMLIDREF = false;

  protected final static String MARKER_FOR_DEFAULT = "##default";

  protected final static JsonFormat.Value FORMAT_STRING = new JsonFormat.Value().withShape(JsonFormat.Shape.STRING);

  protected final static JsonFormat.Value FORMAT_INT = new JsonFormat.Value().withShape(JsonFormat.Shape.NUMBER_INT);

  protected final String _jaxbPackageName;

  protected final JsonSerializer<?> _dataHandlerSerializer;

  protected final JsonDeserializer<?> _dataHandlerDeserializer;

  protected final TypeFactory _typeFactory;

  protected final boolean _ignoreXmlIDREF;

  /**
     * When using {@link XmlValue} annotation, a placeholder name is assigned
     * to property (unless overridden by explicit name); this configuration
     * value specified what that name is.
     */
  protected String _xmlValueName = DEFAULT_NAME_FOR_XML_VALUE;

  /**
     * Inclusion value to return for properties annotated with 
     * {@link XmlElement} and {@link XmlElementWrapper}, in case <code>nillable</code>
     * property is left as <code>false</false>. Default setting is
     * <code>null</code>; this is typically changed to either
     * {@link com.fasterxml.jackson.annotation.JsonInclude.Include#NON_NULL}
     * or {@link com.fasterxml.jackson.annotation.JsonInclude.Include#NON_EMPTY}.
     *
     * @since 2.7
     */
  protected JsonInclude.Include _nonNillableInclusion = null;

  /**
     * @deprecated Since 2.1, use constructor that takes TypeFactory.
     */
  @Deprecated public JaxbAnnotationIntrospector() {
    this(TypeFactory.defaultInstance());
  }

  public JaxbAnnotationIntrospector(MapperConfig<?> config) {
    this(config.getTypeFactory());
  }

  public JaxbAnnotationIntrospector(TypeFactory typeFactory) {
    this(typeFactory, DEFAULT_IGNORE_XMLIDREF);
  }

  /**
     * @param typeFactory Type factory used for resolving type information
     * @param ignoreXmlIDREF Whether {@link XmlIDREF} annotation should be processed
     *   JAXB style (meaning that references are always serialized using id), or
     *   not (first reference as full POJO, others as ids)
     */
  public JaxbAnnotationIntrospector(TypeFactory typeFactory, boolean ignoreXmlIDREF) {
    _typeFactory = (typeFactory == null) ? TypeFactory.defaultInstance() : typeFactory;
    _ignoreXmlIDREF = ignoreXmlIDREF;
    _jaxbPackageName = XmlElement.class.getPackage().getName();
    JsonSerializer<?> dataHandlerSerializer = null;
    JsonDeserializer<?> dataHandlerDeserializer = null;
    try {
      dataHandlerSerializer = (JsonSerializer<?>) DataHandlerJsonSerializer.class.newInstance();
      dataHandlerDeserializer = (JsonDeserializer<?>) DataHandlerJsonDeserializer.class.newInstance();
    } catch (Throwable e) {
    }
    _dataHandlerSerializer = dataHandlerSerializer;
    _dataHandlerDeserializer = dataHandlerDeserializer;
  }

  /**
     * Method that will return version information stored in and read from jar
     * that contains this class.
     */
  @Override public Version version() {
    return PackageVersion.VERSION;
  }

  /**
     * Configuration method that can be used to change default name
     * ("value") used for properties annotated with {@link XmlValue};
     * note that setting it to <code>null</code> will actually avoid
     * name override, and name will instead be derived from underlying
     * method name using standard bean name introspection.
     * 
     * @since 2.5
     */
  public void setNameUsedForXmlValue(String name) {
    _xmlValueName = name;
  }

  /**
     * Accessor for getting currently configured placeholder named
     * used for property annotated with {@link XmlValue}.
     */
  public String getNameUsedForXmlValue() {
    return _xmlValueName;
  }

  /**
     * Method to call to change inclusion criteria used for property annotated
     * with {@link XmlElement} or {@link XmlElementWrapper}, with <code>nillable</code>
     * set as <code>false</code>.
     *
     * @since 2.7
     */
  public JaxbAnnotationIntrospector setNonNillableInclusion(JsonInclude.Include incl) {
    _nonNillableInclusion = incl;
    return this;
  }

  /**
     * @since 2.7
     */
  public JsonInclude.Include getNonNillableInclusion() {
    return _nonNillableInclusion;
  }

  public String findNamespace(Annotated ann) {
    String ns = null;
    if (ann instanceof AnnotatedClass) {
      XmlRootElement elem = findRootElementAnnotation((AnnotatedClass) ann);
      if (elem != null) {
        ns = elem.namespace();
      }
    } else {
      XmlElement elem = findAnnotation(XmlElement.class, ann, false, false, false);
      if (elem != null) {
        ns = elem.namespace();
      }
      if (ns == null || MARKER_FOR_DEFAULT.equals(ns)) {
        XmlAttribute attr = findAnnotation(XmlAttribute.class, ann, false, false, false);
        if (attr != null) {
          ns = attr.namespace();
        }
      }
    }
    if (MARKER_FOR_DEFAULT.equals(ns)) {
      ns = null;
    }
    return ns;
  }

  /**
     * Here we assume fairly simple logic; if there is <code>XmlAttribute</code> to be found,
     * we consider it an attribute; if <code>XmlElement</code>, not-an-attribute; and otherwise
     * we will consider there to be no information.
     * Caller is likely to default to considering things as elements.
     */
  public Boolean isOutputAsAttribute(Annotated ann) {
    XmlAttribute attr = findAnnotation(XmlAttribute.class, ann, false, false, false);
    if (attr != null) {
      return Boolean.TRUE;
    }
    XmlElement elem = findAnnotation(XmlElement.class, ann, false, false, false);
    if (elem != null) {
      return Boolean.FALSE;
    }
    return null;
  }

  public Boolean isOutputAsText(Annotated ann) {
    XmlValue attr = findAnnotation(XmlValue.class, ann, false, false, false);
    if (attr != null) {
      return Boolean.TRUE;
    }
    return null;
  }

  @Override public ObjectIdInfo findObjectIdInfo(Annotated ann) {
    if (!(ann instanceof AnnotatedClass)) {
      return null;
    }
    AnnotatedClass ac = (AnnotatedClass) ann;
    PropertyName idPropName = null;
    method_loop:
    for (AnnotatedMethod m : ac.memberMethods()) {
      XmlID idProp = m.getAnnotation(XmlID.class);
      if (idProp == null) {
        continue;
      }
      switch (m.getParameterCount()) {
        case 0:
        idPropName = findJaxbPropertyName(m, m.getRawType(), BeanUtil.okNameForGetter(m, true));
        break method_loop;
        case 1:
        idPropName = findJaxbPropertyName(m, m.getRawType(), BeanUtil.okNameForSetter(m, true));
        break method_loop;
      }
    }
    if (idPropName == null) {
      for (AnnotatedField f : ac.fields()) {
        XmlID idProp = f.getAnnotation(XmlID.class);
        if (idProp != null) {
          idPropName = findJaxbPropertyName(f, f.getRawType(), f.getName());
          break;
        }
      }
    }
    if (idPropName != null) {
      Class<?> scope = Object.class;
      return new ObjectIdInfo(idPropName, scope, ObjectIdGenerators.PropertyGenerator.class, SimpleObjectIdResolver.class);
    }
    return null;
  }

  @Override public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo base) {
    if (!_ignoreXmlIDREF) {
      XmlIDREF idref = ann.getAnnotation(XmlIDREF.class);
      if (idref != null) {
        base = base.withAlwaysAsId(true);
      }
    }
    return base;
  }

  @Override public PropertyName findRootName(AnnotatedClass ac) {
    XmlRootElement elem = findRootElementAnnotation(ac);
    if (elem != null) {
      return _combineNames(elem.name(), elem.namespace(), "");
    }
    return null;
  }

  @Override public Boolean isIgnorableType(AnnotatedClass ac) {
    return null;
  }

  @Override public boolean hasIgnoreMarker(AnnotatedMember m) {
    return m.getAnnotation(XmlTransient.class) != null;
  }

  @Override public Boolean hasRequiredMarker(AnnotatedMember m) {
    XmlElement elem = m.getAnnotation(XmlElement.class);
    if ((elem != null) && elem.required()) {
      return Boolean.TRUE;
    }
    XmlAttribute attr = m.getAnnotation(XmlAttribute.class);
    if ((attr != null) && attr.required()) {
      return Boolean.TRUE;
    }
    if ((elem != null) || (attr != null)) {
      return null;
    }
    return Boolean.FALSE;
  }

  @Override public PropertyName findWrapperName(Annotated ann) {
    XmlElementWrapper w = findAnnotation(XmlElementWrapper.class, ann, false, false, false);
    if (w != null) {
      PropertyName name = _combineNames(w.name(), w.namespace(), "");
      if (!name.hasSimpleName()) {
        if (ann instanceof AnnotatedMethod) {
          AnnotatedMethod am = (AnnotatedMethod) ann;
          String str;
          if (am.getParameterCount() == 0) {
            str = BeanUtil.okNameForGetter(am, true);
          } else {
            str = BeanUtil.okNameForSetter(am, true);
          }
          if (str != null) {
            return name.withSimpleName(str);
          }
        }
        return name.withSimpleName(ann.getName());
      }
      return name;
    }
    return null;
  }

  @Override public String findImplicitPropertyName(AnnotatedMember m) {
    XmlValue valueInfo = m.getAnnotation(XmlValue.class);
    if (valueInfo != null) {
      return _xmlValueName;
    }
    return null;
  }

  @Override public JsonFormat.Value findFormat(Annotated m) {
    if (m instanceof AnnotatedClass) {
      XmlEnum ann = m.getAnnotation(XmlEnum.class);
      if (ann != null) {
        Class<?> type = ann.value();
        if (type == String.class || type.isEnum()) {
          return FORMAT_STRING;
        }
        if (Number.class.isAssignableFrom(type)) {
          return FORMAT_INT;
        }
      }
    }
    return null;
  }

  @Override public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
    XmlAccessType at = findAccessType(ac);
    if (at == null) {
      return checker;
    }
    switch (at) {
      case FIELD:
      return checker.withFieldVisibility(Visibility.ANY).withSetterVisibility(Visibility.NONE).withGetterVisibility(Visibility.NONE).withIsGetterVisibility(Visibility.NONE);
      case NONE:
      return checker.withFieldVisibility(Visibility.NONE).withSetterVisibility(Visibility.NONE).withGetterVisibility(Visibility.NONE).withIsGetterVisibility(Visibility.NONE);
      case PROPERTY:
      return checker.withFieldVisibility(Visibility.NONE).withSetterVisibility(Visibility.PUBLIC_ONLY).withGetterVisibility(Visibility.PUBLIC_ONLY).withIsGetterVisibility(Visibility.PUBLIC_ONLY);
      case PUBLIC_MEMBER:
      return checker.withFieldVisibility(Visibility.PUBLIC_ONLY).withSetterVisibility(Visibility.PUBLIC_ONLY).withGetterVisibility(Visibility.PUBLIC_ONLY).withIsGetterVisibility(Visibility.PUBLIC_ONLY);
    }
    return checker;
  }

  /**
     * Method for locating JAXB {@link XmlAccessType} annotation value
     * for given annotated entity, if it has one, or inherits one from
     * its ancestors (in JAXB sense, package etc). Returns null if
     * nothing has been explicitly defined.
     */
  protected XmlAccessType findAccessType(Annotated ac) {
    XmlAccessorType at = findAnnotation(XmlAccessorType.class, ac, true, true, true);
    return (at == null) ? null : at.value();
  }

  @Override public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
    return null;
  }

  @Override public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
    if (baseType.isContainerType()) {
      return null;
    }
    return _typeResolverFromXmlElements(am);
  }

  @Override public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
    if (containerType.getContentType() == null) {
      throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
    }
    return _typeResolverFromXmlElements(am);
  }

  protected TypeResolverBuilder<?> _typeResolverFromXmlElements(AnnotatedMember am) {
    XmlElements elems = findAnnotation(XmlElements.class, am, false, false, false);
    XmlElementRefs elemRefs = findAnnotation(XmlElementRefs.class, am, false, false, false);
    if (elems == null && elemRefs == null) {
      return null;
    }
    TypeResolverBuilder<?> b = new StdTypeResolverBuilder();
    b = b.init(JsonTypeInfo.Id.NAME, null);
    b = b.inclusion(JsonTypeInfo.As.WRAPPER_OBJECT);
    return b;
  }

  @Override public List<NamedType> findSubtypes(Annotated a) {
    XmlElements elems = findAnnotation(XmlElements.class, a, false, false, false);
    ArrayList<NamedType> result = null;
    if (elems != null) {
      result = new ArrayList<NamedType>();
      for (XmlElement elem : elems.value()) {
        String name = elem.name();
        if (MARKER_FOR_DEFAULT.equals(name)) {
          name = null;
        }
        result.add(new NamedType(elem.type(), name));
      }
    } else {
      XmlElementRefs elemRefs = findAnnotation(XmlElementRefs.class, a, false, false, false);
      if (elemRefs != null) {
        result = new ArrayList<NamedType>();
        for (XmlElementRef elemRef : elemRefs.value()) {
          Class<?> refType = elemRef.type();
          if (!JAXBElement.class.isAssignableFrom(refType)) {
            String name = elemRef.name();
            if (name == null || MARKER_FOR_DEFAULT.equals(name)) {
              XmlRootElement rootElement = (XmlRootElement) refType.getAnnotation(XmlRootElement.class);
              if (rootElement != null) {
                name = rootElement.name();
              }
            }
            if (name == null || MARKER_FOR_DEFAULT.equals(name)) {
              name = Introspector.decapitalize(refType.getSimpleName());
            }
            result.add(new NamedType(refType, name));
          }
        }
      }
    }
    XmlSeeAlso ann = a.getAnnotation(XmlSeeAlso.class);
    if (ann != null) {
      if (result == null) {
        result = new ArrayList<NamedType>();
      }
      for (Class<?> cls : ann.value()) {
        result.add(new NamedType(cls));
      }
    }
    return result;
  }

  @Override public String findTypeName(AnnotatedClass ac) {
    XmlType type = findAnnotation(XmlType.class, ac, false, false, false);
    if (type != null) {
      String name = type.name();
      if (!MARKER_FOR_DEFAULT.equals(name)) {
        return name;
      }
    }
    return null;
  }

  @Override public JsonSerializer<?> findSerializer(Annotated am) {
    final Class<?> type = _rawSerializationType(am);
    if (type != null) {
      if (_dataHandlerSerializer != null && isDataHandler(type)) {
        return _dataHandlerSerializer;
      }
    }
    return null;
  }

  /**
     * Determines whether the type is assignable to class javax.activation.DataHandler without requiring that class
     * to be on the classpath.
     *
     * @param type The type.
     * @return Whether the type is assignable to class javax.activation.DataHandler
     */
  private boolean isDataHandler(Class<?> type) {
    return type != null && (Object.class != type) && (("javax.activation.DataHandler".equals(type.getName()) || isDataHandler(type.getSuperclass())));
  }

  @Override public Object findContentSerializer(Annotated a) {
    return null;
  }

  @Override @Deprecated public Class<?> findSerializationType(Annotated a) {
    Class<?> allegedType = _getTypeFromXmlElement(a);
    if (allegedType != null) {
      Class<?> rawPropType = _rawSerializationType(a);
      if (!isContainerType(rawPropType)) {
        return allegedType;
      }
    }
    return null;
  }

  /**
     * Implementation of this method is slightly tricky, given that JAXB defaults differ
     * from Jackson defaults. As of version 1.5 and above, this is resolved by honoring
     * Jackson defaults (which are configurable), and only using JAXB explicit annotations.
     */
  @Override @Deprecated public JsonInclude.Include findSerializationInclusion(Annotated a, JsonInclude.Include defValue) {
    XmlElementWrapper w = a.getAnnotation(XmlElementWrapper.class);
    if (w != null) {
      if (w.nillable()) {
        return JsonInclude.Include.ALWAYS;
      }
      if (_nonNillableInclusion != null) {
        return _nonNillableInclusion;
      }
    }
    XmlElement e = a.getAnnotation(XmlElement.class);
    if (e != null) {
      if (e.nillable()) {
        return JsonInclude.Include.ALWAYS;
      }
      if (_nonNillableInclusion != null) {
        return _nonNillableInclusion;
      }
    }
    return defValue;
  }

  @Override public JsonInclude.Value findPropertyInclusion(Annotated a) {
    JsonInclude.Include incl = findSerializationInclusion(a, null);
    if (incl == null) {
      return JsonInclude.Value.empty();
    }
    return JsonInclude.Value.construct(incl, null);
  }

  @Override public JavaType refineSerializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
    Class<?> serClass = _getTypeFromXmlElement(a);
    if (serClass == null) {
      return baseType;
    }
    final TypeFactory tf = config.getTypeFactory();
    if (baseType.getContentType() == null) {
      if (!serClass.isAssignableFrom(baseType.getRawClass())) {
        return baseType;
      }
      if (baseType.hasRawClass(serClass)) {
        return baseType.withStaticTyping();
      }
      try {
        return tf.constructGeneralizedType(baseType, serClass);
      } catch (IllegalArgumentException iae) {
        throw new JsonMappingException(null, String.format("Failed to widen type %s with annotation (value %s), from \'%s\': %s", baseType, serClass.getName(), a.getName(), iae.getMessage()), iae);
      }
    } else {
      JavaType contentType = baseType.getContentType();
      if (contentType != null) {
        if (!serClass.isAssignableFrom(contentType.getRawClass())) {
          return baseType;
        }
        if (contentType.hasRawClass(serClass)) {
          contentType = contentType.withStaticTyping();
        } else {
          try {
            contentType = tf.constructGeneralizedType(contentType, serClass);
          } catch (IllegalArgumentException iae) {
            throw new JsonMappingException(null, String.format("Failed to widen value type of %s with concrete-type annotation (value %s), from \'%s\': %s", baseType, serClass.getName(), a.getName(), iae.getMessage()), iae);
          }
        }
        return baseType.withContentType(contentType);
      }
    }
    return baseType;
  }

  @Override public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
    XmlType type = findAnnotation(XmlType.class, ac, true, true, true);
    if (type == null) {
      return null;
    }
    String[] order = type.propOrder();
    if (order == null || order.length == 0) {
      return null;
    }
    return order;
  }

  @Override public Boolean findSerializationSortAlphabetically(Annotated ann) {
    return _findAlpha(ann);
  }

  private final Boolean _findAlpha(Annotated ann) {
    XmlAccessorOrder order = findAnnotation(XmlAccessorOrder.class, ann, true, true, true);
    return (order == null) ? null : (order.value() == XmlAccessOrder.ALPHABETICAL);
  }

  @Override public Object findSerializationConverter(Annotated a) {
    Class<?> serType = _rawSerializationType(a);
    XmlAdapter<?, ?> adapter = findAdapter(a, true, serType);
    if (adapter != null) {
      return _converter(adapter, true);
    }
    return null;
  }

  @Override public Object findSerializationContentConverter(AnnotatedMember a) {
    Class<?> serType = _rawSerializationType(a);
    if (isContainerType(serType)) {
      XmlAdapter<?, ?> adapter = _findContentAdapter(a, true);
      if (adapter != null) {
        return _converter(adapter, true);
      }
    }
    return null;
  }

  @Override public PropertyName findNameForSerialization(Annotated a) {
    if (a instanceof AnnotatedMethod) {
      AnnotatedMethod am = (AnnotatedMethod) a;
      if (!isVisible(am)) {
        return null;
      }
      return findJaxbPropertyName(am, am.getRawType(), BeanUtil.okNameForGetter(am, true));
    }
    if (a instanceof AnnotatedField) {
      AnnotatedField af = (AnnotatedField) a;
      if (!isVisible(af)) {
        return null;
      }
      if (af.isTransient()) {
        return null;
      }
      PropertyName name = findJaxbPropertyName(af, af.getRawType(), null);
      if (name == null) {
        return PropertyName.USE_DEFAULT;
      }
      return name;
    }
    return null;
  }

  @Override public boolean hasAsValueAnnotation(AnnotatedMethod am) {
    return false;
  }

  /**
     *<p>
     * This is very slow implementation, but as of Jackson 2.7, should not be called any more;
     * instead, {@link #findEnumValues} should be called which has less overhead.
     */
  @Deprecated @Override public String findEnumValue(Enum<?> e) {
    Class<?> enumClass = e.getDeclaringClass();
    String enumValue = e.name();
    try {
      XmlEnumValue xmlEnumValue = enumClass.getDeclaredField(enumValue).getAnnotation(XmlEnumValue.class);
      return (xmlEnumValue != null) ? xmlEnumValue.value() : enumValue;
    } catch (NoSuchFieldException e1) {
      throw new IllegalStateException("Could not locate Enum entry \'" + enumValue + "\' (Enum class " + enumClass.getName() + ")", e1);
    }
  }

  @Override public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
    HashMap<String, String> expl = null;
    for (Field f : ClassUtil.getDeclaredFields(enumType)) {
      if (!f.isEnumConstant()) {
        continue;
      }
      XmlEnumValue enumValue = f.getAnnotation(XmlEnumValue.class);
      if (enumValue == null) {
        continue;
      }
      String n = enumValue.value();
      if (n.isEmpty()) {
        continue;
      }
      if (expl == null) {
        expl = new HashMap<String, String>();
      }
      expl.put(f.getName(), n);
    }
    if (expl != null) {
      for (int i = 0, end = enumValues.length; i < end; ++i) {
        String defName = enumValues[i].name();
        String explValue = expl.get(defName);
        if (explValue != null) {
          names[i] = explValue;
        }
      }
    }
    return names;
  }

  @Override public Object findDeserializer(Annotated am) {
    final Class<?> type = _rawDeserializationType(am);
    if (type != null) {
      if (_dataHandlerDeserializer != null && isDataHandler(type)) {
        return _dataHandlerDeserializer;
      }
    }
    return null;
  }

  @Override public Object findKeyDeserializer(Annotated am) {
    return null;
  }

  @Override public Object findContentDeserializer(Annotated a) {
    return null;
  }

  /**
     * JAXB does allow specifying (more) concrete class for
     * deserialization by using \@XmlElement annotation.
     */
  @Override @Deprecated public Class<?> findDeserializationType(Annotated a, JavaType baseType) {
    if (!baseType.isContainerType()) {
      return _doFindDeserializationType(a, baseType);
    }
    return null;
  }

  @Override @Deprecated public Class<?> findDeserializationContentType(Annotated a, JavaType baseContentType) {
    return _doFindDeserializationType(a, baseContentType);
  }

  protected Class<?> _doFindDeserializationType(Annotated a, JavaType baseType) {
    if (a.hasAnnotation(XmlJavaTypeAdapter.class)) {
      return null;
    }
    XmlElement annotation = findAnnotation(XmlElement.class, a, false, false, false);
    if (annotation != null) {
      Class<?> type = annotation.type();
      if (type != XmlElement.DEFAULT.class) {
        return type;
      }
    }
    return null;
  }

  @Override public JavaType refineDeserializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
    Class<?> deserClass = _getTypeFromXmlElement(a);
    if (deserClass == null) {
      return baseType;
    }
    final TypeFactory tf = config.getTypeFactory();
    if (baseType.getContentType() == null) {
      if (baseType.hasRawClass(deserClass)) {
        return baseType;
      }
      if (!baseType.getRawClass().isAssignableFrom(deserClass)) {
        return baseType;
      }
      try {
        return tf.constructSpecializedType(baseType, deserClass);
      } catch (IllegalArgumentException iae) {
        throw new JsonMappingException(null, String.format("Failed to narrow type %s with annotation (value %s), from \'%s\': %s", baseType, deserClass.getName(), a.getName(), iae.getMessage()), iae);
      }
    } else {
      JavaType contentType = baseType.getContentType();
      if (contentType != null) {
        if (!contentType.getRawClass().isAssignableFrom(deserClass)) {
          return baseType;
        }
        try {
          contentType = tf.constructSpecializedType(contentType, deserClass);
          return baseType.withContentType(contentType);
        } catch (IllegalArgumentException iae) {
          throw new JsonMappingException(null, String.format("Failed to narrow type %s with annotation (value %s), from \'%s\': %s", baseType, deserClass.getName(), a.getName(), iae.getMessage()), iae);
        }
      }
    }
    return baseType;
  }

  @Override public PropertyName findNameForDeserialization(Annotated a) {
    if (a instanceof AnnotatedMethod) {
      AnnotatedMethod am = (AnnotatedMethod) a;
      if (!isVisible((AnnotatedMethod) a)) {
        return null;
      }
      Class<?> rawType = am.getRawParameterType(0);
      return findJaxbPropertyName(am, rawType, BeanUtil.okNameForSetter(am, true));
    }
    if (a instanceof AnnotatedField) {
      AnnotatedField af = (AnnotatedField) a;
      if (!isVisible(af)) {
        return null;
      }
      if (af.isTransient()) {
        return null;
      }
      PropertyName name = findJaxbPropertyName(af, af.getRawType(), null);
      if (name == null) {
        return PropertyName.USE_DEFAULT;
      }
      return name;
    }
    return null;
  }

  @Override public boolean hasCreatorAnnotation(Annotated am) {
    return false;
  }

  @Override public Object findDeserializationConverter(Annotated a) {
    Class<?> deserType = _rawDeserializationType(a);
    if (isContainerType(deserType)) {
      XmlAdapter<?, ?> adapter = findAdapter(a, true, deserType);
      if (adapter != null) {
        return _converter(adapter, false);
      }
    } else {
      XmlAdapter<?, ?> adapter = findAdapter(a, true, deserType);
      if (adapter != null) {
        return _converter(adapter, false);
      }
    }
    return null;
  }

  @Override public Object findDeserializationContentConverter(AnnotatedMember a) {
    Class<?> deserType = _rawDeserializationType(a);
    if (isContainerType(deserType)) {
      XmlAdapter<?, ?> adapter = _findContentAdapter(a, false);
      if (adapter != null) {
        return _converter(adapter, false);
      }
    }
    return null;
  }

  /**
     * Whether the specified field is invisible, per the JAXB visibility rules.
     *
     * @param f The field.
     * @return Whether the field is invisible.
     */
  private boolean isVisible(AnnotatedField f) {
    for (Annotation annotation : f.getAnnotated().getDeclaredAnnotations()) {
      if (isJAXBAnnotation(annotation)) {
        return true;
      }
    }
    XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
    XmlAccessorType at = findAnnotation(XmlAccessorType.class, f, true, true, true);
    if (at != null) {
      accessType = at.value();
    }
    if (accessType == XmlAccessType.FIELD) {
      return true;
    }
    if (accessType == XmlAccessType.PUBLIC_MEMBER) {
      return Modifier.isPublic(f.getAnnotated().getModifiers());
    }
    return false;
  }

  private boolean isVisible(AnnotatedMethod m) {
    for (Annotation annotation : m.getAnnotated().getDeclaredAnnotations()) {
      if (isJAXBAnnotation(annotation)) {
        return true;
      }
    }
    XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
    XmlAccessorType at = findAnnotation(XmlAccessorType.class, m, true, true, true);
    if (at != null) {
      accessType = at.value();
    }
    if (accessType == XmlAccessType.PROPERTY || accessType == XmlAccessType.PUBLIC_MEMBER) {
      return Modifier.isPublic(m.getModifiers());
    }
    return false;
  }

  /**
     * Finds an annotation associated with given annotatable thing; or if
     * not found, a default annotation it may have (from super class, package
     * and so on)
     *
     * @param annotationClass the annotation class.
     * @param annotated The annotated element.
     * @param includePackage Whether the annotation can be found on the package of the annotated element.
     * @param includeClass Whether the annotation can be found on the class of the annotated element.
     * @param includeSuperclasses Whether the annotation can be found on any superclasses of the class of the annotated element.
     * @return The annotation, or null if not found.
     */
  private <A extends Annotation> A findAnnotation(Class<A> annotationClass, Annotated annotated, boolean includePackage, boolean includeClass, boolean includeSuperclasses) {
    A annotation = annotated.getAnnotation(annotationClass);
    if (annotation != null) {
      return annotation;
    }
    Class<?> memberClass = null;
    if (annotated instanceof AnnotatedParameter) {
      memberClass = ((AnnotatedParameter) annotated).getDeclaringClass();
    } else {
      AnnotatedElement annType = annotated.getAnnotated();
      if (annType instanceof Member) {
        memberClass = ((Member) annType).getDeclaringClass();
        if (includeClass) {
          annotation = (A) memberClass.getAnnotation(annotationClass);
          if (annotation != null) {
            return annotation;
          }
        }
      } else {
        if (annType instanceof Class<?>) {
          memberClass = (Class<?>) annType;
        } else {
          throw new IllegalStateException("Unsupported annotated member: " + annotated.getClass().getName());
        }
      }
    }
    if (memberClass != null) {
      if (includeSuperclasses) {
        Class<?> superclass = memberClass.getSuperclass();
        while (superclass != null && superclass != Object.class) {
          annotation = (A) superclass.getAnnotation(annotationClass);
          if (annotation != null) {
            return annotation;
          }
          superclass = superclass.getSuperclass();
        }
      }
      if (includePackage) {
        Package pkg = memberClass.getPackage();
        if (pkg != null) {
          return memberClass.getPackage().getAnnotation(annotationClass);
        }
      }
    }
    return null;
  }

  /**
     * An annotation is handled if it's in the same package as @XmlElement, including subpackages.
     *
     * @param ann The annotation.
     * @return Whether the annotation is in the JAXB package.
     */
  protected boolean isJAXBAnnotation(Annotation ann) {
    Class<?> cls = ann.annotationType();
    Package pkg = cls.getPackage();
    String pkgName = (pkg != null) ? pkg.getName() : cls.getName();
    if (pkgName.startsWith(_jaxbPackageName)) {
      return true;
    }
    return false;
  }

  private static PropertyName findJaxbPropertyName(Annotated ae, Class<?> aeType, String defaultName) {
    XmlAttribute attribute = ae.getAnnotation(XmlAttribute.class);
    if (attribute != null) {
      return _combineNames(attribute.name(), attribute.namespace(), defaultName);
    }
    XmlElement element = ae.getAnnotation(XmlElement.class);
    if (element != null) {
      return _combineNames(element.name(), element.namespace(), defaultName);
    }
    XmlElementRef elementRef = ae.getAnnotation(XmlElementRef.class);
    boolean hasAName = (elementRef != null);
    if (hasAName) {
      if (!MARKER_FOR_DEFAULT.equals(elementRef.name())) {
        return _combineNames(elementRef.name(), elementRef.namespace(), defaultName);
      }
      if (aeType != null) {
        XmlRootElement rootElement = (XmlRootElement) aeType.getAnnotation(XmlRootElement.class);
        if (rootElement != null) {
          String name = rootElement.name();
          if (!MARKER_FOR_DEFAULT.equals(name)) {
            return _combineNames(name, rootElement.namespace(), defaultName);
          }
          return new PropertyName(Introspector.decapitalize(aeType.getSimpleName()));
        }
      }
    }
    if (!hasAName) {
      hasAName = ae.hasAnnotation(XmlElementWrapper.class);
    }
    return hasAName ? PropertyName.USE_DEFAULT : null;
  }

  private static PropertyName _combineNames(String localName, String namespace, String defaultName) {
    if (MARKER_FOR_DEFAULT.equals(localName)) {
      if (MARKER_FOR_DEFAULT.equals(namespace)) {
        return new PropertyName(defaultName);
      }
      return new PropertyName(defaultName, namespace);
    }
    if (MARKER_FOR_DEFAULT.equals(namespace)) {
      return new PropertyName(localName);
    }
    return new PropertyName(localName, namespace);
  }

  private XmlRootElement findRootElementAnnotation(AnnotatedClass ac) {
    return findAnnotation(XmlRootElement.class, ac, true, false, true);
  }

  /**
     * Finds the XmlAdapter for the specified annotation.
     *
     * @param am The annotated element.
     * @param forSerialization If true, adapter for serialization; if false, for deserialization
     * @param type
     * 
     * @return The adapter, or null if none.
     */
  private XmlAdapter<Object, Object> findAdapter(Annotated am, boolean forSerialization, Class<?> type) {
    if (am instanceof AnnotatedClass) {
      return findAdapterForClass((AnnotatedClass) am, forSerialization);
    }
    XmlJavaTypeAdapter adapterInfo = findAnnotation(XmlJavaTypeAdapter.class, am, true, false, false);
    if (adapterInfo != null) {
      XmlAdapter<Object, Object> adapter = checkAdapter(adapterInfo, type, forSerialization);
      if (adapter != null) {
        return adapter;
      }
    }
    XmlJavaTypeAdapters adapters = findAnnotation(XmlJavaTypeAdapters.class, am, true, false, false);
    if (adapters != null) {
      for (XmlJavaTypeAdapter info : adapters.value()) {
        XmlAdapter<Object, Object> adapter = checkAdapter(info, type, forSerialization);
        if (adapter != null) {
          return adapter;
        }
      }
    }
    return null;
  }

  @SuppressWarnings(value = { "unchecked" }) private final XmlAdapter<Object, Object> checkAdapter(XmlJavaTypeAdapter adapterInfo, Class<?> typeNeeded, boolean forSerialization) {
    Class<?> adaptedType = adapterInfo.type();
    if (adaptedType == XmlJavaTypeAdapter.DEFAULT.class) {
      JavaType type = _typeFactory.constructType(adapterInfo.value());
      JavaType[] params = _typeFactory.findTypeParameters(type, XmlAdapter.class);
      adaptedType = params[1].getRawClass();
    }
    if (adaptedType.isAssignableFrom(typeNeeded)) {
      @SuppressWarnings(value = { "rawtypes" }) Class<? extends XmlAdapter> cls = adapterInfo.value();
      return ClassUtil.createInstance(cls, true);
    }
    return null;
  }

  @SuppressWarnings(value = { "unchecked" }) private XmlAdapter<Object, Object> findAdapterForClass(AnnotatedClass ac, boolean forSerialization) {
    XmlJavaTypeAdapter adapterInfo = ac.getAnnotated().getAnnotation(XmlJavaTypeAdapter.class);
    if (adapterInfo != null) {
      @SuppressWarnings(value = { "rawtypes" }) Class<? extends XmlAdapter> cls = adapterInfo.value();
      return ClassUtil.createInstance(cls, true);
    }
    return null;
  }

  protected final TypeFactory getTypeFactory() {
    return _typeFactory;
  }

  /**
     * Helper method used to distinguish structured types (arrays, Lists, Maps),
     * which with JAXB use different rules for defining content types.
     */
  private boolean isContainerType(Class<?> raw) {
    return raw.isArray() || Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw);
  }

  private boolean adapterTypeMatches(XmlAdapter<?, ?> adapter, Class<?> targetType) {
    return findAdapterBoundType(adapter).isAssignableFrom(targetType);
  }

  private Class<?> findAdapterBoundType(XmlAdapter<?, ?> adapter) {
    TypeFactory tf = getTypeFactory();
    JavaType adapterType = tf.constructType(adapter.getClass());
    JavaType[] params = tf.findTypeParameters(adapterType, XmlAdapter.class);
    if (params == null || params.length < 2) {
      return Object.class;
    }
    return params[1].getRawClass();
  }

  protected XmlAdapter<?, ?> _findContentAdapter(Annotated ann, boolean forSerialization) {
    Class<?> rawType = forSerialization ? _rawSerializationType(ann) : _rawDeserializationType(ann);
    if (isContainerType(rawType) && (ann instanceof AnnotatedMember)) {
      AnnotatedMember member = (AnnotatedMember) ann;
      JavaType fullType = forSerialization ? _fullSerializationType(member) : _fullDeserializationType(member);
      Class<?> contentType = fullType.getContentType().getRawClass();
      XmlAdapter<Object, Object> adapter = findAdapter(member, forSerialization, contentType);
      if (adapter != null && adapterTypeMatches(adapter, contentType)) {
        return adapter;
      }
    }
    return null;
  }

  protected String _propertyNameToString(PropertyName n) {
    return (n == null) ? null : n.getSimpleName();
  }

  protected Class<?> _rawDeserializationType(Annotated a) {
    if (a instanceof AnnotatedMethod) {
      AnnotatedMethod am = (AnnotatedMethod) a;
      if (am.getParameterCount() == 1) {
        return am.getRawParameterType(0);
      }
    }
    return a.getRawType();
  }

  protected JavaType _fullDeserializationType(AnnotatedMember am) {
    if (am instanceof AnnotatedMethod) {
      AnnotatedMethod method = (AnnotatedMethod) am;
      if (method.getParameterCount() == 1) {
        return ((AnnotatedMethod) am).getParameterType(0);
      }
    }
    return am.getType();
  }

  protected Class<?> _rawSerializationType(Annotated a) {
    return a.getRawType();
  }

  protected JavaType _fullSerializationType(AnnotatedMember am) {
    return am.getType();
  }

  protected Converter<Object, Object> _converter(XmlAdapter<?, ?> adapter, boolean forSerialization) {
    TypeFactory tf = getTypeFactory();
    JavaType adapterType = tf.constructType(adapter.getClass());
    JavaType[] pt = tf.findTypeParameters(adapterType, XmlAdapter.class);
    if (forSerialization) {
      return new AdapterConverter(adapter, pt[1], pt[0], forSerialization);
    }
    return new AdapterConverter(adapter, pt[0], pt[1], forSerialization);
  }

  protected Class<?> _getTypeFromXmlElement(Annotated a) {
    XmlElement annotation = findAnnotation(XmlElement.class, a, false, false, false);
    if (annotation != null) {
      if (a.getAnnotation(XmlJavaTypeAdapter.class) != null) {
        return null;
      }
      Class<?> type = annotation.type();
      if (type != XmlElement.DEFAULT.class) {
        return type;
      }
    }
    return null;
  }
}