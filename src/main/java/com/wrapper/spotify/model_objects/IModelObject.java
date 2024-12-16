package com.wrapper.spotify.model_objects;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PagingCursorbased;
import java.text.SimpleDateFormat;


<<<<<<< LEFT
/**
 * Interface with methods used in model objects.
 */
=======
/**
 * Interface with methods used in model objects.
 */
>>>>>>> RIGHT

//  interface IJsonUtilPaging {
//    <X> Paging<X> createModelObject(JsonObject jsonObject, TypeToken<X> typeToken);
//  }
public interface IModelObject {
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

  /**
   * Create a builder for building an instance of a model object.<br>
   * The type of the builder and its methods depend on its corresponding implementation.
   *
   * @return A builder object.
   */
  public abstract Builder builder();

  /**
   * Interface with methods used in builder classes of model objects.
   */
  interface Builder {
    /**
     * Build a model object with the information set in the builder object. <br>
     * The type of the model object and its methods depend on its corresponding implementation.
     *
     * @return A model object.
     */
    public abstract IModelObject build();
  }

  /**
   * Interface with methods used in JsonUtil classes of model objects.
   *
   * @param <T>
   * 		Type of the corresponding model object.
   */
  interface IJsonUtil<T> {
    /**
     * Build a model object with the information given in a json object. <br>
     * The type of the model object and its methods depend on its corresponding implementation.
     *
     * @param jsonObject
     * 		A json object.
     * @return A model object. The type depends on this methods implementation.
     */
    public abstract T createModelObject(JsonObject jsonObject);

    T[] createModelObjectArray(JsonArray jsonArray);

    T[] createModelObjectArray(String json);

    T[] createModelObjectArray(String json, String key);

    <X> X[] createModelObjectArray(JsonArray jsonArray, TypeToken<X> typeToken);

    Paging<T> createModelObjectPaging(JsonObject jsonObject);

    Paging<T> createModelObjectPaging(String json);

    Paging<T> createModelObjectPaging(String json, String key);

    PagingCursorbased<T> createModelObjectPagingCursorbased(JsonObject jsonObject);

    PagingCursorbased<T> createModelObjectPagingCursorbased(String json);

    PagingCursorbased<T> createModelObjectPagingCursorbased(String json, String key);
  }
}