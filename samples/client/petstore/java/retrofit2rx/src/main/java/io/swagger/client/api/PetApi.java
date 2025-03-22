package io.swagger.client.api;
import io.swagger.client.CollectionFormats.*;
import rx.Observable;
import retrofit2.http.*;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import java.io.File;
import io.swagger.client.model.ModelApiResponse;
import io.swagger.client.model.Pet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PetApi {
  /**
   * Add a new pet to the store
   * 
   * @param body Pet object that needs to be added to the store (required)
   * @return Call&lt;Void&gt;
   */
  @Headers(value = { "Content-Type:application/json" }) @POST(value = "pet") Observable<Void> addPet(@retrofit2.http.Body Pet body);

  /**
   * Deletes a pet
   * 
   * @param petId Pet id to delete (required)
   * @param apiKey  (optional)
   * @return Call&lt;Void&gt;
   */
  @DELETE(value = "pet/{petId}") Observable<Void> deletePet(@retrofit2.http.Path(value = "petId") Long petId, @retrofit2.http.Header(value = "api_key") String apiKey);

  /**
   * Finds Pets by status
   * Multiple status values can be provided with comma separated strings
   * @param status Status values that need to be considered for filter (required)
   * @return Call&lt;List&lt;Pet&gt;&gt;
   */
  @GET(value = "pet/findByStatus") Observable<List<Pet>> findPetsByStatus(@retrofit2.http.Query(value = "status") CSVParams status);

  /**
   * Finds Pets by tags
   * Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.
   * @param tags Tags to filter by (required)
   * @return Call&lt;List&lt;Pet&gt;&gt;
   * @deprecated
   */
  @Deprecated @GET(value = "pet/findByTags") Observable<List<Pet>> findPetsByTags(@retrofit2.http.Query(value = "tags") CSVParams tags);

  /**
   * Find pet by ID
   * Returns a single pet
   * @param petId ID of pet to return (required)
   * @return Call&lt;Pet&gt;
   */
  @GET(value = "pet/{petId}") Observable<Pet> getPetById(@retrofit2.http.Path(value = "petId") Long petId);

  /**
   * Update an existing pet
   * 
   * @param body Pet object that needs to be added to the store (required)
   * @return Call&lt;Void&gt;
   */
  @Headers(value = { "Content-Type:application/json" }) @PUT(value = "pet") Observable<Void> updatePet(@retrofit2.http.Body Pet body);

  /**
   * Updates a pet in the store with form data
   * 
   * @param petId ID of pet that needs to be updated (required)
   * @param name Updated name of the pet (optional)
   * @param status Updated status of the pet (optional)
   * @return Call&lt;Void&gt;
   */
  @retrofit2.http.FormUrlEncoded @POST(value = "pet/{petId}") Observable<Void> updatePetWithForm(@retrofit2.http.Path(value = "petId") Long petId, @retrofit2.http.Field(value = "name") String name, @retrofit2.http.Field(value = "status") String status);

  /**
   * uploads an image
   * 
   * @param petId ID of pet to update (required)
   * @param additionalMetadata Additional data to pass to server (optional)
   * @param file file to upload (optional)
   * @return Call&lt;ModelApiResponse&gt;
   */
  @retrofit2.http.Multipart @POST(value = "pet/{petId}/uploadImage") Observable<ModelApiResponse> uploadFile(@retrofit2.http.Path(value = "petId") Long petId, @retrofit2.http.Part(value = "additionalMetadata") String additionalMetadata, @retrofit2.http.Part(value = "file\"; filename=\"file") RequestBody file);
}