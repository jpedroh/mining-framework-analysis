package com.wrapper.spotify.model_objects;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PagingCursorbased;
import java.text.SimpleDateFormat;

/**
 * Interface with methods used in model objects.
 */
public interface IModelObject {
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:mm:ss");

  /**
   * Create a builder for building an instance of a model object.<br>
   * The type of the builder and its methods depend on its corresponding implementation.
   *
   * @return A builder object.
   */
  Builder builder();

  interface Builder {
    /**
     * Build a model object with the information set in the builder object. <br>
     * The type of the model object and its methods depend on its corresponding implementation.
     *
     * @return A model object.
     */
    IModelObject build();
  }

  interface IJsonUtil<T extends java.lang.Object> {
    /**
     * Build a model object with the information given in a json object. <br>
     * The type of the model object and its methods depend on its corresponding implementation.
     *
     * @param jsonObject A json object.
     * @return A model object. The type depends on this methods implementation.
     */
    T createModelObject(JsonObject jsonObject);

    T[] createModelObjectArray(JsonArray jsonArray);

    T[] createModelObjectArray(String json);

    T[] createModelObjectArray(String json, String key);

    <X extends java.lang.Object> X[] createModelObjectArray(JsonArray jsonArray, TypeToken<X> typeToken);

    Paging<T> createModelObjectPaging(JsonObject jsonObject);

    Paging<T> createModelObjectPaging(String json);

    Paging<T> createModelObjectPaging(String json, String key);

    PagingCursorbased<T> createModelObjectPagingCursorbased(JsonObject jsonObject);

    PagingCursorbased<T> createModelObjectPagingCursorbased(String json);

    PagingCursorbased<T> createModelObjectPagingCursorbased(String json, String key);
  }
}