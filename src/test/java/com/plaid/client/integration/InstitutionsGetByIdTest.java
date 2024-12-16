package com.plaid.client.integration;

import com.plaid.client.model.CountryCode;
import com.plaid.client.model.Error;
import com.plaid.client.model.Institution;
import com.plaid.client.model.InstitutionsGetByIdRequest;
import com.plaid.client.model.InstitutionsGetByIdRequestOptions;
import com.plaid.client.model.InstitutionsGetByIdResponse;
import com.plaid.client.model.Products;
import java.util.Arrays;
import org.junit.Test;
import retrofit2.Response;
import static org.junit.Assert.*;


public class InstitutionsGetByIdTest extends AbstractIntegrationTest {
  @Test
  public void testSuccess() throws Exception {
    InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest().institutionId(TARTAN_BANK_INSTITUTION_ID).addCountryCodesItem(CountryCode.US);
    Response<InstitutionsGetByIdResponse> response = client().institutionsGetById(request).execute();
    assertSuccessResponse(response);
    Institution institution = response.body().getInstitution();
    assertIsTartanBank(institution);
  }

  @Test
  public void testSuccessWithIncludeOptionalMetadataTrue() throws Exception {
    InstitutionsGetByIdRequestOptions options = new InstitutionsGetByIdRequestOptions();
    options.includeOptionalMetadata(true);
    InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest().institutionId(TARTAN_BANK_INSTITUTION_ID).addCountryCodesItem(CountryCode.US).options(options);
    Response<InstitutionsGetByIdResponse> response = client().institutionsGetById(request).execute();
    assertSuccessResponse(response);
    Institution institution = response.body().getInstitution();
    assertIsTartanBank(institution);
    assertEquals("https://www.plaid.com/", institution.getUrl());
    assertNotNull(institution.getLogo());
    assertEquals("#174e7c", institution.getPrimaryColor());
  }

  @Test
  public void testSuccessWithIncludeOptionalMetadataFalse() throws Exception {
    InstitutionsGetByIdRequestOptions options = new InstitutionsGetByIdRequestOptions();
    options.includeOptionalMetadata(false);
    InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest().institutionId(TARTAN_BANK_INSTITUTION_ID).addCountryCodesItem(CountryCode.US).options(options);
    Response<InstitutionsGetByIdResponse> response = client().institutionsGetById(request).execute();
    assertSuccessResponse(response);
    Institution institution = response.body().getInstitution();
    assertIsTartanBank(institution);
    assertEquals(null, institution.getUrl());
    assertEquals(null, institution.getLogo());
    assertEquals(null, institution.getPrimaryColor());
  }

  @Test
  public void testSuccessWithIncludeStatusTrue() throws Exception {
    InstitutionsGetByIdRequestOptions options = new InstitutionsGetByIdRequestOptions();
    options.includeStatus(true);
    InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest().institutionId(FIRST_PLATYPUS_BANK_INSTITUTION_ID).addCountryCodesItem(CountryCode.US).options(options);
    Response<InstitutionsGetByIdResponse> response = client().institutionsGetById(request).execute();
    assertSuccessResponse(response);
    Institution institution = response.body().getInstitution();
    assertNotNull(institution.getStatus());
    assertNotNull(institution.getStatus().getTransactionsUpdates());
    assertNotNull(institution.getStatus().getAuth());
    assertNotNull(institution.getStatus().getBalance());
    assertNotNull(institution.getStatus().getIdentity());
    assertIsFirstPlatypusBank(institution);
  }

  @Test
  public void testSuccessWithIncludeStatusFalse() throws Exception {
    InstitutionsGetByIdRequestOptions options = new InstitutionsGetByIdRequestOptions();
    options.includeStatus(false);
    InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest().institutionId(TARTAN_BANK_INSTITUTION_ID).addCountryCodesItem(CountryCode.US).options(options);
    Response<InstitutionsGetByIdResponse> response = client().institutionsGetById(request).execute();
    assertSuccessResponse(response);
    Institution institution = response.body().getInstitution();
    assertNull(institution.getStatus());
    assertIsTartanBank(institution);
  }

  @Test
  public void testSuccessWithIncludePaymentInitiationMetadataTrue() throws Exception {
    Response<InstitutionsGetByIdResponse> response =
      client().service().institutionsGetById(
        new InstitutionsGetByIdRequest(ROYAL_BANK_OF_PLAID_INSTITUTION_ID, Arrays.asList("GB")).
        withIncludePaymentInitiationMetadata(true))
        .execute();

    assertSuccessResponse(response);

    Institution institution = response.body().getInstitution();
    assertIsRoyalBankOfPlaid(institution);

    assertNotNull(institution.getPaymentInitiationMetadata());
  }

  @Test
  public void testSuccessWithIncludePaymentInitiationMetadataFalse() throws Exception {
    Response<InstitutionsGetByIdResponse> response = client().service().
      institutionsGetById(new InstitutionsGetByIdRequest(ROYAL_BANK_OF_PLAID_INSTITUTION_ID, Arrays.asList("GB")).
        withIncludePaymentInitiationMetadata(false))
      .execute();

    assertSuccessResponse(response);

    Institution institution = response.body().getInstitution();
    assertIsRoyalBankOfPlaid(institution);

    assertNull(institution.getPaymentInitiationMetadata());
  }

  private void assertIsRoyalBankOfPlaid(Institution institution) {
    assertEquals(ROYAL_BANK_OF_PLAID_INSTITUTION_ID, institution.getInstitutionId());
    assertEquals("Royal Bank of Plaid", institution.getName());
    assertEquals(Arrays.asList(
      Product.ASSETS,
      Product.AUTH,
      Product.BALANCE,
      Product.TRANSACTIONS,
      Product.IDENTITY,
      Product.PAYMENT_INITIATION
      ),
      institution.getProducts());
    assertTrue(institution.getCountryCodes().contains("GB"));
  }

  private void assertIsTartanBank(Institution institution) {
    assertEquals(TARTAN_BANK_INSTITUTION_ID, institution.getInstitutionId());
    assertEquals("Tartan Bank", institution.getName());
    assertEquals(Arrays.asList(Products.ASSETS, Products.AUTH, Products.BALANCE, Products.TRANSACTIONS, Products.CREDIT_DETAILS, Products.INCOME, Products.IDENTITY, Products.INVESTMENTS, Products.LIABILITIES), institution.getProducts());
    assertTrue(institution.getCountryCodes().contains(CountryCode.US));
  }

  private void assertIsFirstPlatypusBank(Institution institution) {
    assertEquals(FIRST_PLATYPUS_BANK_INSTITUTION_ID, institution.getInstitutionId());
    assertEquals("First Platypus Bank", institution.getName());
    assertEquals(Arrays.asList(Products.ASSETS, Products.AUTH, Products.BALANCE, Products.TRANSACTIONS, Products.CREDIT_DETAILS, Products.INCOME, Products.IDENTITY, Products.INVESTMENTS, Products.LIABILITIES), institution.getProducts());
    assertTrue(institution.getCountryCodes().contains(CountryCode.US));
  }

  @Test
  public void testInvalidInstitution() throws Exception {
    InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest().institutionId("notreal").addCountryCodesItem(CountryCode.US);
    Response<InstitutionsGetByIdResponse> response = client().institutionsGetById(request).execute();
    assertErrorResponse(response, java.lang.Error.ErrorTypeEnum, "INVALID_INSTITUTION");
  }
}