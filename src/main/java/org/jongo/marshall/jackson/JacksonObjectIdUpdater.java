package org.jongo.marshall.jackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.bson.types.ObjectId;
import org.jongo.ObjectIdUpdater;

/**
 * An ObjectIdUpdater based on Jackson's view of on object.
 *
 * @author Christian Trimble
 */
@SuppressWarnings(value = { "deprecation" }) public class JacksonObjectIdUpdater implements ObjectIdUpdater {
  private final ObjectMapper mapper;

  private final IdSelector<BeanPropertyDefinition> idSelector;

  public JacksonObjectIdUpdater(ObjectMapper mapper) {
    this(mapper, new JacksonObjectIdSelector());
  }

  public JacksonObjectIdUpdater(ObjectMapper mapper, IdSelector<BeanPropertyDefinition> idSelector) {
    this.mapper = mapper;
    this.idSelector = idSelector;
  }

  public boolean mustGenerateObjectId(Object pojo) {
    for (BeanPropertyDefinition def : beanDescription(pojo.getClass()).findProperties()) {
      if (idSelector.isId(def)) {
        AnnotatedMember accessor = def.getAccessor();
        accessor.fixAccess(true);
        return idSelector.isObjectId(def) && accessor.getValue(pojo) == null;
      }
    }
    return false;
  }

  public Object getId(Object pojo) {
    BasicBeanDescription beanDescription = beanDescription(pojo.getClass());
    for (BeanPropertyDefinition def : beanDescription.findProperties()) {
      if (idSelector.isId(def)) {
        AnnotatedMember accessor = def.getAccessor();
        accessor.fixAccess(true);
        Object id = accessor.getValue(pojo);
        if (id instanceof String && idSelector.isObjectId(def)) {
          return new ObjectId(id.toString());
        } else {
          return id;
        }
      }
    }
    return null;
  }

  public void setObjectId(Object target, ObjectId id) {
    for (BeanPropertyDefinition def : beanDescription(target.getClass()).findProperties()) {
      if (idSelector.isId(def)) {
        AnnotatedMember accessor = def.getAccessor();
        accessor.fixAccess(true);
        if (accessor.getValue(target) != null) {
          throw new IllegalArgumentException("Unable to set objectid on class: " + target.getClass());
        }
        AnnotatedMember field = def.getField();
        field.fixAccess(true);
        Class<?> type = field.getRawType();
        if (ObjectId.class.isAssignableFrom(type)) {
          field.setValue(target, id);
        } else {
          if (type.equals(String.class) && idSelector.isObjectId(def)) {
            field.setValue(target, id.toString());
          }
        }
        return;
      }
    }
  }

  private BasicBeanDescription beanDescription(Class<?> cls) {
    BasicClassIntrospector bci = new BasicClassIntrospector();
    return bci.forSerialization(mapper.getSerializationConfig(), mapper.constructType(cls), mapper.getSerializationConfig());
  }
}