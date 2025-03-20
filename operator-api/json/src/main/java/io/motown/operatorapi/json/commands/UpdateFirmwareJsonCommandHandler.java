package io.motown.operatorapi.json.commands;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.domain.api.chargingstation.CorrelationToken;
import io.motown.domain.api.chargingstation.RequestFirmwareUpdateCommand;
import io.motown.operatorapi.viewmodel.model.UpdateFirmwareApiCommand;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;
import java.util.HashMap;

class UpdateFirmwareJsonCommandHandler implements JsonCommandHandler {
  private static final String COMMAND_NAME = "UpdateFirmware";

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
        UpdateFirmwareApiCommand command = gson.fromJson(commandObject, UpdateFirmwareApiCommand.class);
        commandGateway.send(new RequestFirmwareUpdateCommand(new ChargingStationId(chargingStationId), command.getLocation(), command.getRetrieveDate(), new HashMap<String, String>()), new CorrelationToken());
      }
    } catch (JsonSyntaxException ex) {
      throw new IllegalArgumentException("Change configuration command not able to parse the payload, is your json correctly formatted?", ex);
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