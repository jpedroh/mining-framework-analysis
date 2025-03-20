package io.motown.operatorapi.json.commands;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.domain.api.chargingstation.RequestStartTransactionCommand;
import io.motown.domain.api.chargingstation.CorrelationToken;
import io.motown.operatorapi.viewmodel.model.RequestStartTransactionApiCommand;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;

class RequestStartTransactionJsonCommandHandler implements JsonCommandHandler {
  private static final String COMMAND_NAME = "RequestStartTransaction";

  private DomainCommandGateway commandGateway;

  private ChargingStationRepository repository;

  private Gson gson;

  @Override public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override public void handle(String chargingStationId, JsonObject commandObject) {
    try {
      ChargingStation chargingStation = repository.findOne(chargingStationId);
      if (chargingStation != null && chargingStation.isAccepted()) {
        RequestStartTransactionApiCommand command = gson.fromJson(commandObject, RequestStartTransactionApiCommand.class);
        commandGateway.send(new RequestStartTransactionCommand(new ChargingStationId(chargingStationId), command.getIdentifyingToken(), command.getEvseId()), new CorrelationToken());
      } else {
        throw new IllegalStateException("It is not possible to request a start transaction on a charging station that is not registered");
      }
    } catch (JsonSyntaxException ex) {
      throw new IllegalArgumentException("Start transaction command is not able to parse the payload, is your json correctly formatted?", ex);
    }
  }

  public void setCommandGateway(DomainCommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  public void setRepository(ChargingStationRepository repository) {
    this.repository = repository;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }
}