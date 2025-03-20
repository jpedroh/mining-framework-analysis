package io.motown.operatorapi.json.commands;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.domain.api.chargingstation.CorrelationToken;
import io.motown.domain.api.chargingstation.RequestReserveNowCommand;
import io.motown.operatorapi.viewmodel.model.RequestReserveNowApiCommand;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;

class RequestReserveNowJsonCommandHandler implements JsonCommandHandler {
  private static final String COMMAND_NAME = "RequestReserveNow";

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
        RequestReserveNowApiCommand command = gson.fromJson(commandObject, RequestReserveNowApiCommand.class);
        commandGateway.send(new RequestReserveNowCommand(new ChargingStationId(chargingStationId), command.getEvseId(), command.getIdentifyingToken(), command.getExpiryDate(), null), new CorrelationToken());
      } else {
        throw new IllegalStateException("It is not possible to request a reservation on a charging station that is not registered");
      }
    } catch (JsonSyntaxException ex) {
      throw new IllegalArgumentException("Reserve now command is not able to parse the payload, is your json correctly formatted?", ex);
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