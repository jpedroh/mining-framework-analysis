package com.ning.billing.recurly.model;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

@JsonIgnoreProperties(ignoreUnknown = true) public abstract class RecurlyObject {
  public static final String NIL_STR = "nil";

  public static final List<String> NIL_VAL = Arrays.asList("nil", "true");

  public static XmlMapper newXmlMapper() {
    final XmlMapper xmlMapper = new XmlMapper();
    final AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
    final AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
    final AnnotationIntrospector pair = new AnnotationIntrospectorPair(primary, secondary);
    xmlMapper.setAnnotationIntrospector(pair);
    xmlMapper.registerModule(new JodaModule());
    xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return xmlMapper;
  }

  public static Boolean booleanOrNull(@Nullable final Object object) {
    if (isNull(object)) {
      return null;
    }
    if (object instanceof Map) {
      final Map map = (Map) object;
      if (map.keySet().size() >= 2 && "boolean".equals(map.get("type"))) {
        return Boolean.valueOf((String) map.get(""));
      }
    }
    return Boolean.valueOf(object.toString());
  }

  public static String stringOrNull(@Nullable final Object object) {
    if (isNull(object)) {
      return null;
    }
    return object.toString();
  }

  public static Integer integerOrNull(@Nullable final Object object) {
    if (isNull(object)) {
      return null;
    }
    if (object instanceof Map) {
      final Map map = (Map) object;
      if (map.keySet().size() == 2 && "integer".equals(map.get("type"))) {
        return Integer.valueOf((String) map.get(""));
      }
    }
    return Integer.valueOf(object.toString());
  }

  public static DateTime dateTimeOrNull(@Nullable final Object object) {
    if (isNull(object)) {
      return null;
    }
    if (object instanceof Map) {
      final Map map = (Map) object;
      if (map.keySet().size() == 2 && "datetime".equals(map.get("type"))) {
        return new DateTime(map.get(""));
      }
    }
    return new DateTime(object.toString());
  }

  public static boolean isNull(@Nullable final Object object) {
    if (object == null) {
      return true;
    }
    if (object instanceof Map) {
      final Map map = (Map) object;
      if (map.keySet().size() >= 1 && map.get(NIL_STR) != null && NIL_VAL.contains(map.get(NIL_STR).toString())) {
        return true;
      }
    }
    return false;
  }
}