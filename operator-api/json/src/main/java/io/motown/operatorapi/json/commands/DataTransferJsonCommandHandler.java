package io.motown.operatorapi.json.commands;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.motown.domain.api.chargingstation.ChargingStationId;
import io.motown.domain.api.chargingstation.DataTransferCommand;
import io.motown.domain.api.chargingstation.CorrelationToken;
import io.motown.operatorapi.viewmodel.model.DataTransferApiCommand;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;

class DataTransferJsonCommandHandler implements JsonCommandHandler {
  private static final String COMMAND_NAME = "DataTransfer";

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
        DataTransferApiCommand command = gson.fromJson(commandObject, DataTransferApiCommand.class);
        commandGateway.send(new DataTransferCommand(new ChargingStationId(chargingStationId), command.getVendorId(), command.getMessageId(), command.getData()), new CorrelationToken());
      }
    } catch (JsonSyntaxException ex) {
      throw new IllegalArgumentException("Data transfer command not able to parse the payload, is your json correctly formatted?", ex);
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