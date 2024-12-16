package com.mercadopago.serialization;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import com.mercadopago.net.MPResource;
import com.mercadopago.net.MPResourceList;
import com.mercadopago.resources.ResultsResourcesPage;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import static com.google.gson.stream.JsonToken.END_DOCUMENT;


/** Serializer class, responsible for objects serialization and deserialization. */
public class Serializer {
  private static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  private static final Gson GSON =
      new GsonBuilder()
          .setDateFormat(DATE_FORMAT_ISO8601)
          .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
          .create();

  /**
   * Method responsible for deserialize objects.
   *
   * @param clazz class.
   * @param jsonObject json object.
   * @param <T> class type.
   * @return object.
   */
  public static <T extends MPResource> T deserializeFromJson(Class<T> clazz, String jsonObject) {
    return GSON.fromJson(jsonObject, clazz);
  }

  /**
   * Method responsible for deserialize objects to ResultsResourcesPage.
   *
   * @param clazz class.
   * @param jsonObject json object.
   * @param <T> class type.
   * @return object.
   */
  public static <T extends MPResource>
      ResultsResourcesPage<T> deserializeFromJsonToResultsResources(
          Class<T> clazz, String jsonObject) {
    Type collectionType = new TypeToken<ResultsResourcesPage<T>>() {}.getType();
    return GSON.fromJson(jsonObject, collectionType);
  }

  /**
   * Method responsible for deserialize objects.
   *
   * @param clazz class.
   * @param jsonObject json object.
   * @param <T> class type.
   * @return object.
   */
  public static <T extends MPResource> MPResourceList<T> deserializeListFromJson(Class<T> clazz, String jsonObject) {
    MPResourceList<T> resourceList = new MPResourceList<>();
    JsonObject rootObject = JsonParser.parseString(jsonObject).getAsJsonObject();
    JsonArray jsonArray = getArrayFromJsonElement(rootObject);
    for(int i=0; i < jsonArray.size(); i++) {
      T resource = GSON.fromJson(jsonArray.get(i), clazz);
      resourceList.add(resource);
    }

    return resourceList;
  }

  /**
   * Method for getting a json array from a json element
   *
   * @param jsonElement the jsonElement to be analized
   * @return
   */
  static JsonArray getArrayFromJsonElement(JsonElement jsonElement) {
    JsonArray jsonArray = null;
    if (jsonElement.isJsonArray()) {
      jsonArray = jsonElement.getAsJsonArray();
    } else if (jsonElement.isJsonObject() &&
        ((JsonObject) jsonElement).get("results") != null &&
        ((JsonObject) jsonElement).get("results").isJsonArray()) {
      jsonArray = ((JsonObject) jsonElement).get("results").getAsJsonArray();
    }
    return jsonArray;
  }

  /**
   * Method responsible for serialize objects.
   *
   * @param resource resource.
   * @param <T> class type.
   * @return JsonObject.
   */
  public static <T> JsonObject serializeToJson(T resource) {
    return (JsonObject) GSON.toJsonTree(resource);
  }

  public static boolean isJsonValid(String json) throws IOException {
    try {
      JsonReader jsonReader = new JsonReader(new StringReader(json));
      JsonToken token;
      loop:
      while ((token = jsonReader.peek()) != END_DOCUMENT && token != null) {
        switch (token) {
          case BEGIN_ARRAY:
            jsonReader.beginArray();
            break;
          case END_ARRAY:
            jsonReader.endArray();
            break;
          case BEGIN_OBJECT:
            jsonReader.beginObject();
            break;
          case END_OBJECT:
            jsonReader.endObject();
            break;
          case NAME:
            jsonReader.nextName();
            break;
          case STRING:
          case NUMBER:
          case BOOLEAN:
          case NULL:
            jsonReader.skipValue();
            break;
          case END_DOCUMENT:
            break loop;
          default:
            throw new AssertionError(token);
        }
      }
      return true;
    } catch (final MalformedJsonException ignored) {
      return false;
    }
  }
}