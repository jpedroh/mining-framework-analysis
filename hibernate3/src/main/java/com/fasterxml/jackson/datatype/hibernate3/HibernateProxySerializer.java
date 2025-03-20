package com.fasterxml.jackson.datatype.hibernate3;
import java.io.IOException;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

/**
 * Serializer to use for values proxied using {@link HibernateProxy}.
 *<p>
 * TODO: should try to make this work more like Jackson
 * <code>BeanPropertyWriter</code>, possibly sub-classing
 * it -- it handles much of functionality we need, and has
 * access to more information than value serializers (like
 * this one) have.
 */
public class HibernateProxySerializer extends JsonSerializer<HibernateProxy> implements ContextualSerializer {
  /**
     * Property that has proxy value to handle
     */
  protected final BeanProperty _property;

  protected final boolean _forceLazyLoading;

  /**
     * For efficient serializer lookup, let's use this; most
     * of the time, there's just one type and one serializer.
     */
  protected PropertySerializerMap _dynamicSerializers;

  public HibernateProxySerializer(boolean forceLazyLoading) {
    _forceLazyLoading = forceLazyLoading;
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _property = null;
  }

  public HibernateProxySerializer(boolean forceLazyLoading, BeanProperty property) {
    _forceLazyLoading = forceLazyLoading;
    _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    _property = property;
  }

  @Override public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
    return new HibernateProxySerializer(this._forceLazyLoading);
  }

  @Override public boolean isEmpty(SerializerProvider provider, HibernateProxy value) {
    return (value == null) || (findProxied(value) == null);
  }

  @Override public void serialize(HibernateProxy value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    Object proxiedValue = findProxied(value);
    if (proxiedValue == null) {
      provider.defaultSerializeNull(jgen);
      return;
    }
    findSerializer(provider, proxiedValue).serialize(proxiedValue, jgen, provider);
  }

  @Override public void serializeWithType(HibernateProxy value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
    Object proxiedValue = findProxied(value);
    if (proxiedValue == null) {
      provider.defaultSerializeNull(jgen);
      return;
    }
    findSerializer(provider, proxiedValue).serializeWithType(proxiedValue, jgen, provider, typeSer);
  }

  protected JsonSerializer<Object> findSerializer(SerializerProvider provider, Object value) throws IOException {
    Class<?> type = value.getClass();
    PropertySerializerMap.SerializerAndMapResult result = _dynamicSerializers.findAndAddPrimarySerializer(type, provider, _property);
    if (_dynamicSerializers != result.map) {
      _dynamicSerializers = result.map;
    }
    return result.serializer;
  }

  /**
     * Helper method for finding value being proxied, if it is available
     * or if it is to be forced to be loaded.
     */
  protected Object findProxied(HibernateProxy proxy) {
    LazyInitializer init = proxy.getHibernateLazyInitializer();
    if (!_forceLazyLoading && init.isUninitialized()) {
      return null;
    }
    return init.getImplementation();
  }
}