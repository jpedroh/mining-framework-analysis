package com.plaid.client.integration.banktransfer;

import static org.junit.Assert.assertEquals;

import com.plaid.client.model.BankTransferEventListRequest;
import com.plaid.client.model.BankTransferEventListResponse;
import com.plaid.client.model.SandboxBankTransferSimulateRequest;
import com.plaid.client.model.SandboxBankTransferSimulateResponse;
import com.plaid.client.model.BankTransferEvent;
import retrofit2.Response;
public class BankTransferEventListTest extends AbstractBankTransferTest {
  @Before
  public void simulatePosted() throws AssertionError, Exception {
    Response<SandboxBankTransferSimulateResponse> simulateResponse = client().service().sandboxBankTransferSimulate(
      new SandboxBankTransferSimulateRequest(getBankTransfer().getId(), "posted")
    ).execute();
    assertSuccessResponse(simulateResponse);
  }

  @Override
  protected void bankTransferTest() throws AssertionError, Exception {
<<<<<<< /usr/src/app/output/plaid/plaid-java/ee81fb7afb875ac137baad5b9a30d55d39e2f60e/src/test/java/com/plaid/client/integration/banktransfer/BankTransferEventListTest.java/left.java
    SandboxBankTransferSimulateRequest request = new SandboxBankTransferSimulateRequest()
      .bankTransferId(getBankTransfer().getId())
      .eventType("posted");

    Response<SandboxBankTransferSimulateResponse> simulateResponse = client()
      .sandboxBankTransferSimulate(request)
      .execute();
    assertSuccessResponse(simulateResponse);

    BankTransferEventListRequest listRequest = new BankTransferEventListRequest()
      .bankTransferId(getBankTransfer().getId());

    Response<BankTransferEventListResponse> eventListResponse = client()
      .bankTransferEventList(listRequest)
      .execute();
||||||| /usr/src/app/output/plaid/plaid-java/ee81fb7afb875ac137baad5b9a30d55d39e2f60e/src/test/java/com/plaid/client/integration/banktransfer/BankTransferEventListTest.java/base.java
    Response<SandboxBankTransferSimulateResponse> simulateResponse = client().service().sandboxBankTransferSimulate(
      new SandboxBankTransferSimulateRequest(getBankTransfer().getId(), "posted")
    ).execute();
    assertSuccessResponse(simulateResponse);

    Response<BankTransferEventListResponse> eventListResponse = client().service().bankTransferEventList(
      new BankTransferEventListRequest().withBankTransferId(getBankTransfer().getId())
    ).execute();
=======
    Response<BankTransferEventListResponse> eventListResponse = client().service().bankTransferEventList(
      new BankTransferEventListRequest().withBankTransferId(getBankTransfer().getId())
    ).execute();
>>>>>>> /usr/src/app/output/plaid/plaid-java/ee81fb7afb875ac137baad5b9a30d55d39e2f60e/src/test/java/com/plaid/client/integration/banktransfer/BankTransferEventListTest.java/right.java
    assertSuccessResponse(eventListResponse);
    List<BankTransferEvent> bankTransferEvents = eventListResponse
      .body()
      .getBankTransferEvents();
    assertEquals(1, bankTransferEvents.size());
    for (BankTransferEvent e : bankTransferEvents) {
      assertEquals(getBankTransfer().getId(), e.getBankTransferId());
    }
  }
}
import org.junit.Before;
import java.util.List;
