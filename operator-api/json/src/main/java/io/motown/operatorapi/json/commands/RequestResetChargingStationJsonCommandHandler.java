package io.motown.operatorapi.json.commands;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.domain.api.chargingstation.CorrelationToken;
import io.motown.domain.api.chargingstation.RequestHardResetChargingStationCommand;
import io.motown.domain.api.chargingstation.RequestSoftResetChargingStationCommand;
import io.motown.operatorapi.viewmodel.model.RequestResetChargingStationApiCommand;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;

class RequestResetChargingStationJsonCommandHandler implements JsonCommandHandler {
  private static final String COMMAND_NAME = "ResetChargingStation";

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
        RequestResetChargingStationApiCommand command = gson.fromJson(commandObject, RequestResetChargingStationApiCommand.class);
        if ("hard".equalsIgnoreCase(command.getType())) {
          commandGateway.send(new RequestHardResetChargingStationCommand(new ChargingStationId(chargingStationId)), new CorrelationToken());
        } else {
          commandGateway.send(new RequestSoftResetChargingStationCommand(new ChargingStationId(chargingStationId)), new CorrelationToken());
        }
      }
    } catch (JsonSyntaxException ex) {
      throw new IllegalArgumentException("Configure command not able to parse the payload, is your json correctly formatted ?", ex);
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