package com.plaid.client.integration;

import com.plaid.client.integration.RecipientCreateTest;
import com.plaid.client.model.PaymentAmount;
import com.plaid.client.model.PaymentInitiationPaymentCreateRequest;
import com.plaid.client.model.PaymentInitiationPaymentCreateResponse;
import com.plaid.client.model.PaymentInitiationRecipientCreateResponse;
import com.plaid.client.model.paymentinitiation.Bacs;
import com.plaid.client.model.paymentinitiation.PaymentCreateOptions;
import com.plaid.client.request.PlaidApi;
import com.plaid.client.request.paymentinitiation.PaymentGetRequest;
import com.plaid.client.response.paymentinitiation.PaymentGetResponse;
import org.junit.Test;
import retrofit2.Response;
import static org.junit.Assert.assertNotNull;


public class PaymentCreateTest extends AbstractIntegrationTest {
  /**
   * Utility method that creates a paymentinitiation.
   * Used by other integration tests to set up.
   */
  public static Response<PaymentInitiationPaymentCreateResponse> createPayment(PlaidApi client) throws Exception {
    Response<PaymentInitiationRecipientCreateResponse> createRecipientResponse = RecipientCreateTest.createRecipientWithIban(client);
    String recipientId = createRecipientResponse.body().getRecipientId();
    assertNotNull(recipientId);
    PaymentAmount amount = new PaymentAmount().currency(PaymentAmount.CurrencyEnum.GBP).value(999.99);
    PaymentInitiationPaymentCreateRequest paymentCreateRequest = new PaymentInitiationPaymentCreateRequest().recipientId(recipientId).reference("reference").amount(amount);
    Response<PaymentInitiationPaymentCreateResponse> response = client.paymentInitiationPaymentCreate(paymentCreateRequest).execute();
    return response;
  }

  @Test
  public void testPaymentCreateSuccess() throws Exception {
    Response<PaymentInitiationPaymentCreateResponse> response = createPayment(client());
    assertSuccessResponse(response);
    assertNotNull(response.body().getPaymentId());
    assertNotNull(response.body().getStatus());
  }
}