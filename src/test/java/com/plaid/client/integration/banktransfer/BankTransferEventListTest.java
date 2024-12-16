package com.plaid.client.integration.banktransfer;

import com.plaid.client.model.BankTransferEvent;
import com.plaid.client.model.BankTransferEventListRequest;
import com.plaid.client.model.BankTransferEventListResponse;
import com.plaid.client.model.SandboxBankTransferSimulateRequest;
import com.plaid.client.model.SandboxBankTransferSimulateResponse;
import java.util.List;
import org.junit.Before;
import retrofit2.Response;
import static org.junit.Assert.assertEquals;


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
    SandboxBankTransferSimulateRequest request = new SandboxBankTransferSimulateRequest().bankTransferId(getBankTransfer().getId()).eventType("posted");
    Response<SandboxBankTransferSimulateResponse> simulateResponse = client().sandboxBankTransferSimulate(request).execute();
    assertSuccessResponse(simulateResponse);
    BankTransferEventListRequest listRequest = new BankTransferEventListRequest().bankTransferId(getBankTransfer().getId());
    Response<BankTransferEventListResponse> eventListResponse = client().bankTransferEventList(listRequest).execute();
    assertSuccessResponse(eventListResponse);
    List<BankTransferEvent> bankTransferEvents = eventListResponse.body().getBankTransferEvents();
    assertEquals(1, bankTransferEvents.size());
    for (BankTransferEvent e : bankTransferEvents) {
      assertEquals(getBankTransfer().getId(), e.getBankTransferId());
    }
  }
}