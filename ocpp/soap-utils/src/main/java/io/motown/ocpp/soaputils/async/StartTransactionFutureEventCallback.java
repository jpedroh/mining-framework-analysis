package io.motown.ocpp.soaputils.async;
import io.motown.domain.api.chargingstation.*;
import io.motown.domain.api.security.AddOnIdentity;
import io.motown.ocpp.viewmodel.domain.DomainService;
import io.motown.ocpp.viewmodel.domain.FutureEventCallback;
import io.motown.ocpp.viewmodel.persistence.entities.Transaction;
import org.apache.cxf.continuations.Continuation;
import org.axonframework.domain.EventMessage;
import java.util.Date;

/**
 * Future event callback for the start transaction action. Before starting a transaction the identification that starts the
 * transaction needs to be validated. It could be that the charging station has an outdated local list of identifications.
 */
public class StartTransactionFutureEventCallback extends FutureEventCallback<StartTransactionFutureResult> implements ContinuationFutureCallback {
  private Continuation continuation;

  private DomainService domainService;

  private EvseId evseId;

  private ChargingStationId chargingStationId;

  private String protocolIdentifier;

  private ReservationId reservationId;

  private AddOnIdentity addOnIdentity;

  private IdentifyingToken identifyingToken;

  private int meterStart;

  private Date timestamp;

  public StartTransactionFutureEventCallback(DomainService domainService, EvseId evseId, ChargingStationId chargingStationId, String protocolIdentifier, ReservationId reservationId, AddOnIdentity addOnIdentity, IdentifyingToken identifyingToken, int meterStart, Date timestamp) {
    this.domainService = domainService;
    this.evseId = evseId;
    this.chargingStationId = chargingStationId;
    this.protocolIdentifier = protocolIdentifier;
    this.reservationId = reservationId;
    this.addOnIdentity = addOnIdentity;
    this.identifyingToken = identifyingToken;
    this.meterStart = meterStart;
    this.timestamp = timestamp != null ? new Date(timestamp.getTime()) : null;
  }

  /**
     * Handles the {@code AuthorizationResultEvent} (other events will directly result in 'false' return value). In the
     * flow of handling a start transaction message the first step is to authorize the identification used to start
     * the transaction. This method handles that event and uses the domain service to start the transaction and sets the
     * result of the start transaction using 'setResult'. The continuation, if it exists, is resumed.
     *
     * @param event Authorizaton result event.
     * @return true if the event has been handled, false if the event was the wrong type.
     */
  @Override public boolean onEvent(EventMessage<?> event) {
    AuthorizationResultEvent resultEvent;
    if (!(event.getPayload() instanceof AuthorizationResultEvent)) {
      return false;
    }
    resultEvent = (AuthorizationResultEvent) event.getPayload();
    Transaction transaction = domainService.createTransaction(evseId);
    NumberedTransactionId transactionId = new NumberedTransactionId(chargingStationId, protocolIdentifier, transaction.getId().intValue());
    domainService.startTransactionNoAuthorize(transactionId, evseId, chargingStationId, reservationId, addOnIdentity, identifyingToken, meterStart, timestamp);
    StartTransactionFutureResult futureResult = new StartTransactionFutureResult();
    futureResult.setAuthorizationResultStatus(resultEvent.getAuthenticationStatus());
    futureResult.setTransactionId(transactionId.getNumber());
    this.setResult(futureResult);
    this.countDownLatch();
    if (continuation != null) {
      continuation.resume();
    }
    return true;
  }

  @Override public void setContinuation(Continuation continuation) {
    this.continuation = continuation;
  }
}