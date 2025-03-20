package com.fasterxml.jackson.datatype.hibernate3;
import java.util.*;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentMap;
import org.hibernate.proxy.HibernateProxy;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.*;
import com.fasterxml.jackson.databind.type.*;
import com.fasterxml.jackson.datatype.hibernate3.Hibernate3Module.Feature;

public class HibernateSerializers extends Serializers.Base {
  protected final int _moduleFeatures;

  public HibernateSerializers(int features) {
    _moduleFeatures = features;
  }

  @Override public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    Class<?> raw = type.getRawClass();
    if (Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw)) {
      return null;
    }
    if (PersistentCollection.class.isAssignableFrom(raw)) {
      JavaType elementType = _figureFallbackType(config, type);
      return new PersistentCollectionSerializer(elementType, isEnabled(Feature.FORCE_LAZY_LOADING));
    }
    if (HibernateProxy.class.isAssignableFrom(raw)) {
      return new HibernateProxySerializer(isEnabled(Feature.FORCE_LAZY_LOADING), isEnabled(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS));
    }
    return null;
  }

  @Override public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
    Class<?> raw = type.getRawClass();
    if (PersistentCollection.class.isAssignableFrom(raw)) {
      JavaType elementType = _figureFallbackType(config, type);
      return new PersistentCollectionSerializer(elementType, isEnabled(Feature.FORCE_LAZY_LOADING));
    }
    return null;
  }

  @Override public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
    Class<?> raw = type.getRawClass();
    if (PersistentMap.class.isAssignableFrom(raw)) {
      return new PersistentCollectionSerializer(_figureFallbackType(config, type), isEnabled(Feature.FORCE_LAZY_LOADING));
    }
    return null;
  }

  public final boolean isEnabled(Hibernate3Module.Feature f) {
    return (_moduleFeatures & f.getMask()) != 0;
  }

  protected JavaType _figureFallbackType(SerializationConfig config, JavaType persistentType) {
    Class<?> raw = persistentType.getRawClass();
    TypeFactory tf = config.getTypeFactory();
    final int paramCount = persistentType.containedTypeCount();
    if (Map.class.isAssignableFrom(raw)) {
      if (paramCount >= 2) {
        return tf.constructMapType(Map.class, persistentType.containedType(0), persistentType.containedType(1));
      }
      return tf.constructMapType(Map.class, Object.class, Object.class);
    }
    if (List.class.isAssignableFrom(raw)) {
      if (paramCount == 1) {
        return tf.constructCollectionType(List.class, persistentType.containedType(0));
      }
      return tf.constructCollectionType(List.class, Object.class);
    }
    if (Set.class.isAssignableFrom(raw)) {
      if (paramCount == 1) {
        return tf.constructCollectionType(Set.class, persistentType.containedType(0));
      }
      return tf.constructCollectionType(Set.class, Object.class);
    }
    if (paramCount == 1) {
      return tf.constructCollectionType(Collection.class, persistentType.containedType(0));
    }
    return tf.constructCollectionType(Collection.class, Object.class);
  }
}