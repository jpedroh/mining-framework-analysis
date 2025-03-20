package io.motown.operatorapi.json.commands;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.motown.domain.api.chargingstation.*;
import io.motown.operatorapi.viewmodel.model.RequestStopTransactionApiCommand;
import io.motown.operatorapi.viewmodel.persistence.entities.ChargingStation;
import io.motown.operatorapi.viewmodel.persistence.repositories.ChargingStationRepository;

class RequestStopTransactionJsonCommandHandler implements JsonCommandHandler {
  private static final String COMMAND_NAME = "RequestStopTransaction";

  private DomainCommandGateway commandGateway;

  private ChargingStationRepository repository;

  private Gson gson;

  @Override public String getCommandName() {
    return COMMAND_NAME;
  }

  @Override public void handle(String chargingStationId, JsonObject commandObject) {
    try {
      RequestStopTransactionApiCommand command = gson.fromJson(commandObject, RequestStopTransactionApiCommand.class);
      ChargingStation chargingStation = repository.findOne(chargingStationId);
      ChargingStationId chargingStationIdObject = new ChargingStationId(chargingStationId);
      TransactionId transactionId = new NumberedTransactionId(chargingStationIdObject, chargingStation.getProtocol(), Integer.parseInt(command.getId()));
      commandGateway.send(new RequestStopTransactionCommand(chargingStationIdObject, transactionId), new CorrelationToken());
    } catch (JsonSyntaxException ex) {
      throw new IllegalArgumentException("Configure command not able to parse the payload, is your json correctly formatted ?", ex);
    }
  }

  public void setCommandGateway(DomainCommandGateway commandGateway) {
    this.commandGateway = commandGateway;
  }

  public void setGson(Gson gson) {
    this.gson = gson;
  }

  public void setRepository(ChargingStationRepository repository) {
    this.repository = repository;
  }
}