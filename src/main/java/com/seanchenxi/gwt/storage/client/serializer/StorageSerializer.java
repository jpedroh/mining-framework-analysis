package com.seanchenxi.gwt.storage.client.serializer;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Interface for deferred binding implementation.
 * <p>
 *   Realize: <br/>
 *   <b>Object</b> to <b>String</b> serialization, <br/>
 *   <b>String</b> to <b>Object</b> deserialization
 * </p>
 */
public interface StorageSerializer {
  /**
   * Deserialize string to object
   *
   * <p>
   *   The <b>clazz</b> only used by serializer to distinguish primitive type and object type.<br/>
   *   For all primitive types, the <b>clazz</b> should equals to <b><T></b>.<br/>
   *   Otherwise, use {@link java.io.Serializable} as <b>clazz</b>, and real class type for <b><T></b>.
   * </p>
   *
   * @param clazz the type of the given value to deserialize with
   * @param serializedString the serialized string which will be deserialize to its original class type
   * @param <T> the real class type wanted to return
   * @return the original class type value of the the given string
   * @throws SerializationException
   */
  <T extends java.lang.Object> T deserialize(Class<? super T> clazz, String serializedString) throws SerializationException;

  /**
   * Serialize object to string
   *
   * <p>
   *   The <b>clazz</b> only used to distinguish primitive type and object type.<br/>
   *   For all primitive types, the <b>clazz</b> should equals to <b><T></b>.<br/>
   *   Otherwise, use {@link java.io.Serializable} as <b>clazz</b>, and real class type for <b><T></b>
   * </p>
   *
   * @param clazz the type of the given value to serialize with
   * @param instance the value which will be serialized to string
   * @param <T> the real class type of the given value
   * @return the serialized string
   * @throws SerializationException
   */
  <T extends java.lang.Object> String serialize(Class<? super T> clazz, T instance) throws SerializationException;
}