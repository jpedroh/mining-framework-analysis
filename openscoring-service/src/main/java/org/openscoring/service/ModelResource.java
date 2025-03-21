package org.openscoring.service;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.dmg.pmml.FieldName;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jpmml.evaluator.EvaluationException;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.HasGroupFields;
import org.jpmml.evaluator.InputField;
import org.openscoring.common.BatchEvaluationRequest;
import org.openscoring.common.BatchEvaluationResponse;
import org.openscoring.common.BatchModelResponse;
import org.openscoring.common.EvaluationRequest;
import org.openscoring.common.EvaluationResponse;
import org.openscoring.common.ModelResponse;
import org.openscoring.common.SimpleResponse;
import org.openscoring.common.TableEvaluationRequest;
import org.openscoring.common.TableEvaluationResponse;
import org.openscoring.service.annotations.Endpoint;
import org.openscoring.service.exceptions.ModelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value = "model") @Consumes(value = MediaType.APPLICATION_JSON) @Produces(value = MediaType.APPLICATION_JSON) @PermitAll public class ModelResource {
  @Context private UriInfo uriInfo = null;

  private ModelRegistry modelRegistry = null;

  @Inject public ModelResource(ModelRegistry modelRegistry) {
    this.modelRegistry = modelRegistry;
  }

  @GET @Endpoint(family = Endpoint.Family.INFORMATION) public BatchModelResponse queryBatch(@Context SecurityContext securityContext) {
    Principal owner = securityContext.getUserPrincipal();
    List<ModelResponse> responses = new ArrayList<>();
    Map<String, Model> models = this.modelRegistry.getModels(owner);
    Collection<Map.Entry<String, Model>> entries = models.entrySet();
    for (Map.Entry<String, Model> entry : entries) {
      ModelResponse response = createModelResponse(entry.getKey(), entry.getValue(), false);
      responses.add(response);
    }
    Comparator<ModelResponse> comparator = new Comparator<ModelResponse>() {
      @Override public int compare(ModelResponse left, ModelResponse right) {
        return (left.getId()).compareToIgnoreCase(right.getId());
      }
    };
    Collections.sort(responses, comparator);
    BatchModelResponse batchResponse = new BatchModelResponse().setResponses(responses);
    return batchResponse;
  }

  @GET @Path(value = ModelRef.PATH_VALUE_ID) @Endpoint(family = Endpoint.Family.INFORMATION) public ModelResponse query(@PathParam(value = "id") ModelRef modelRef) {
    Model model = this.modelRegistry.get(modelRef);
    if (model == null) {
      throw new ModelNotFoundException(modelRef);
    }
    return createModelResponse(modelRef.getId(), model, true);
  }

  @PUT @Path(value = ModelRef.PATH_VALUE_ID) @Consumes(value = { MediaType.APPLICATION_XML, MediaType.TEXT_XML }) @RolesAllowed(value = { "admin" }) @Endpoint(family = Endpoint.Family.MANAGEMENT) public Response deploy(@PathParam(value = "id") ModelRef modelRef, Model model) {
    return doDeploy(modelRef, model);
  }

  @PUT @Path(value = ModelRef.PATH_VALUE_ID) @RolesAllowed(value = { "admin" }) @Consumes(value = MediaType.MULTIPART_FORM_DATA) @Endpoint(family = Endpoint.Family.MANAGEMENT) public Response deployForm(@PathParam(value = "id") ModelRef modelRef, @FormDataParam(value = "pmml") Model model) {
    return doDeploy(modelRef, model);
  }

  private Response doDeploy(ModelRef modelRef, Model model) {
    boolean success;
    Model oldModel = this.modelRegistry.get(modelRef);
    if (oldModel != null) {
      success = this.modelRegistry.replace(modelRef, oldModel, model);
    } else {
      success = this.modelRegistry.put(modelRef, model);
    }
    if (!success) {
      throw new InternalServerErrorException("Concurrent modification");
    }
    ModelResponse entity = createModelResponse(modelRef.getId(), model, true);
    if (oldModel != null) {
      return (Response.ok().entity(entity)).build();
    } else {
      UriBuilder uriBuilder = (this.uriInfo.getBaseUriBuilder()).path(ModelResource.class).path(modelRef.getId());
      URI uri = uriBuilder.build();
      return (Response.created(uri).entity(entity)).build();
    }
  }

  @GET @Path(value = ModelRef.PATH_VALUE_ID + "/pmml") @Produces(value = MediaType.APPLICATION_XML) @RolesAllowed(value = { "admin" }) @Endpoint(family = Endpoint.Family.MANAGEMENT) public Model download(@PathParam(value = "id") ModelRef modelRef) {
    Model model = this.modelRegistry.get(modelRef, true);
    if (model == null) {
      throw new ModelNotFoundException(modelRef);
    }
    return model;
  }

  @POST @Path(value = ModelRef.PATH_VALUE_ID) @Endpoint(family = Endpoint.Family.EVALUATION) public EvaluationResponse evaluate(@PathParam(value = "id") ModelRef modelRef, EvaluationRequest request) {
    List<EvaluationRequest> requests = Collections.singletonList(request);
    List<EvaluationResponse> responses = doEvaluate(modelRef, requests, true);
    return responses.get(0);
  }

  @POST @Path(value = ModelRef.PATH_VALUE_ID + "/batch") @Endpoint(family = Endpoint.Family.EVALUATION) public BatchEvaluationResponse evaluateBatch(@PathParam(value = "id") ModelRef modelRef, BatchEvaluationRequest batchRequest) {
    List<EvaluationRequest> requests = batchRequest.getRequests();
    List<EvaluationResponse> responses = doEvaluate(modelRef, requests, false);
    BatchEvaluationResponse batchResponse = new BatchEvaluationResponse(batchRequest.getId()).setResponses(responses);
    return batchResponse;
  }

  @POST @Path(value = ModelRef.PATH_VALUE_ID + "/csv") @Consumes(value = { "application/csv", "text/csv", MediaType.TEXT_PLAIN }) @Produces(value = MediaType.TEXT_PLAIN) @Endpoint(family = Endpoint.Family.EVALUATION) public TableEvaluationResponse evaluateCsv(@PathParam(value = "id") ModelRef modelRef, TableEvaluationRequest tableRequest) {
    return doEvaluateCsv(modelRef, tableRequest);
  }

  @POST @Path(value = ModelRef.PATH_VALUE_ID + "/csv") @Consumes(value = MediaType.MULTIPART_FORM_DATA) @Produces(value = MediaType.TEXT_PLAIN) @Endpoint(family = Endpoint.Family.EVALUATION) public TableEvaluationResponse evaluateCsvForm(@PathParam(value = "id") ModelRef modelRef, @FormDataParam(value = "csv") TableEvaluationRequest tableRequest) {
    return doEvaluateCsv(modelRef, tableRequest);
  }

  private TableEvaluationResponse doEvaluateCsv(ModelRef modelRef, TableEvaluationRequest tableRequest) {
    List<EvaluationRequest> requests = tableRequest.getRequests();
    List<EvaluationResponse> responses = doEvaluate(modelRef, requests, true);
    List<String> columns = new ArrayList<>();
    String idColumn = tableRequest.getIdColumn();
    if (idColumn != null) {
      columns.add(idColumn);
    }
    responses:
    for (EvaluationResponse response : responses) {
      String message = response.getMessage();
      if (message != null) {
        continue;
      }
      Map<String, ?> results = response.getResults();
      columns.addAll(results.keySet());
      break responses;
    }
    TableEvaluationResponse tableResponse = new TableEvaluationResponse().setFormat(tableRequest.getFormat()).setColumns(columns).setResponses(responses);
    return tableResponse;
  }

  private List<EvaluationResponse> doEvaluate(ModelRef modelRef, List<EvaluationRequest> requests, boolean allOrNothing) {
    Model model = this.modelRegistry.get(modelRef, true);
    if (model == null) {
      throw new ModelNotFoundException(modelRef);
    }
    List<EvaluationResponse> responses = new ArrayList<>();
    try {
      Evaluator evaluator = model.getEvaluator();
      if (evaluator instanceof HasGroupFields) {
        HasGroupFields hasGroupFields = (HasGroupFields) evaluator;
        List<InputField> groupFields = hasGroupFields.getGroupFields();
        if (groupFields.size() == 1) {
          InputField groupField = groupFields.get(0);
          requests = aggregateRequests(groupField.getName(), requests);
        } else {
          if (groupFields.size() > 1) {
            throw new EvaluationException("Too many group fields");
          }
        }
      }
      for (EvaluationRequest request : requests) {
        EvaluationResponse response;
        try {
          response = evaluate(evaluator, request);
        } catch (Exception e) {
          if (allOrNothing) {
            throw e;
          }
          response = new EvaluationResponse(request.getId());
          response.setMessage(e.toString());
        }
        responses.add(response);
      }
    } catch (Exception e) {
      logger.error("Failed to evaluate", e);
      throw new BadRequestException(e);
    }
    return responses;
  }

  @DELETE @Path(value = ModelRef.PATH_VALUE_ID) @RolesAllowed(value = { "admin" }) @Endpoint(family = Endpoint.Family.MANAGEMENT) public SimpleResponse undeploy(@PathParam(value = "id") ModelRef modelRef) {
    return doUndeploy(modelRef);
  }

  @DELETE @Path(value = ModelRef.PATH_VALUE_ID) @Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA }) @RolesAllowed(value = { "admin" }) @Endpoint(family = Endpoint.Family.MANAGEMENT) public SimpleResponse undeployForm(@PathParam(value = "id") ModelRef modelRef) {
    return doUndeploy(modelRef);
  }

  private SimpleResponse doUndeploy(ModelRef modelRef) {
    Model model = this.modelRegistry.get(modelRef);
    if (model == null) {
      throw new ModelNotFoundException(modelRef);
    }
    boolean success = this.modelRegistry.remove(modelRef, model);
    if (!success) {
      throw new InternalServerErrorException("Concurrent modification");
    }
    SimpleResponse response = new SimpleResponse();
    return response;
  }

  static protected List<EvaluationRequest> aggregateRequests(FieldName groupName, List<EvaluationRequest> requests) {
    Map<Object, ListMultimap<String, Object>> groupedArguments = new LinkedHashMap<>();
    String key = groupName.getValue();
    for (EvaluationRequest request : requests) {
      Map<String, ?> requestArguments = request.getArguments();
      Object value = requestArguments.get(key);
      if (value == null && !requestArguments.containsKey(key)) {
        logger.warn("Evaluation request {} does not specify a group field {}", request.getId(), key);
      }
      ListMultimap<String, Object> groupedArgumentMap = groupedArguments.get(value);
      if (groupedArgumentMap == null) {
        groupedArgumentMap = ArrayListMultimap.create();
        groupedArguments.put(value, groupedArgumentMap);
      }
      Collection<? extends Map.Entry<String, ?>> entries = requestArguments.entrySet();
      for (Map.Entry<String, ?> entry : entries) {
        groupedArgumentMap.put(entry.getKey(), entry.getValue());
      }
    }
    if (groupedArguments.size() == requests.size()) {
      return requests;
    }
    List<EvaluationRequest> resultRequests = new ArrayList<>();
    Collection<Map.Entry<Object, ListMultimap<String, Object>>> entries = groupedArguments.entrySet();
    for (Map.Entry<Object, ListMultimap<String, Object>> entry : entries) {
      Map<String, Object> arguments = new LinkedHashMap<>();
      arguments.putAll((entry.getValue()).asMap());
      arguments.put(key, entry.getKey());
      EvaluationRequest resultRequest = new EvaluationRequest().setArguments(arguments);
      resultRequests.add(resultRequest);
    }
    return resultRequests;
  }

  static protected EvaluationResponse evaluate(Evaluator evaluator, EvaluationRequest request) {
    logger.info("Received {}", request);
    Map<String, ?> requestArguments = request.getArguments();
    EvaluationResponse response = new EvaluationResponse(request.getId());
    Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();
    List<InputField> inputFields = evaluator.getInputFields();
    for (InputField inputField : inputFields) {
      FieldName inputName = inputField.getName();
      String key = inputName.getValue();
      Object value = requestArguments.get(key);
      if (value == null && !requestArguments.containsKey(key)) {
        logger.warn("Evaluation request {} does not specify an input field {}", request.getId(), key);
      }
      FieldValue inputValue = inputField.prepare(value);
      arguments.put(inputName, inputValue);
    }
    logger.debug("Evaluation request {} has prepared arguments: {}", request.getId(), arguments);
    Map<FieldName, ?> results = evaluator.evaluate(arguments);
    logger.debug("Evaluation response {} has result: {}", response.getId(), results);
    response.
<<<<<<< /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelResource.java/left.java
    setResults(EvaluatorUtil.decodeAll(results))
=======
    setResult(EvaluatorUtil.decodeAll(results))
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelResource.java/right.java
    ;
    logger.info("Returned {}", response);
    return response;
  }

  static private ModelResponse createModelResponse(String id, Model model, boolean expand) {
    ModelResponse response = new ModelResponse(id).setMiningFunction(model.getMiningFunction()).setSummary(model.getSummary()).setProperties(model.getProperties());
    if (expand) {
      response.setSchema(model.getSchema());
    }
    return response;
  }

  private static final Logger logger = LoggerFactory.getLogger(ModelResource.class);
}