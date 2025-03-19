package com.plaid.client.integration.banktransfer;
import static org.junit.Assert.assertEquals;
import com.plaid.client.model.BankTransferEventListRequest;
import com.plaid.client.model.BankTransferEventListResponse;
import com.plaid.client.model.SandboxBankTransferSimulateRequest;
import com.plaid.client.model.SandboxBankTransferSimulateResponse;
import com.plaid.client.model.BankTransferEvent;
import org.junit.Before;
import java.util.List;
import retrofit2.Response;

public class BankTransferEventListTest extends AbstractBankTransferTest {
  @Before public void simulatePosted() throws AssertionError, Exception {
    Response<SandboxBankTransferSimulateResponse> simulateResponse = client().service().sandboxBankTransferSimulate(new SandboxBankTransferSimulateRequest(getBankTransfer().getId(), "posted")).execute();
    assertSuccessResponse(simulateResponse);
  }

  @Override protected void bankTransferTest() throws AssertionError, Exception {
    SandboxBankTransferSimulateRequest request = new SandboxBankTransferSimulateRequest().bankTransferId(getBankTransfer().getId()).eventType("posted");

<<<<<<< /usr/src/app/output/plaid/plaid-java/ee81fb7afb875ac137baad5b9a30d55d39e2f60e/src/test/java/com/plaid/client/integration/banktransfer/BankTransferEventListTest.java/left.java
    Response<SandboxBankTransferSimulateResponse> simulateResponse = client().sandboxBankTransferSimulate(request).execute();
=======
>>>>>>> Unknown file: This is a bug in JDime.

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