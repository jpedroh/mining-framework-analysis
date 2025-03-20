package com.premiumminds.billy.portugal.services.export.pdf.simpleinvoice;
import java.io.InputStream;
import com.premiumminds.billy.core.util.PaymentMechanism;
import com.premiumminds.billy.gin.services.impl.pdf.AbstractTemplateBundle;
import com.premiumminds.billy.portugal.services.export.pdf.PTTemplateBundle;

public class PTSimpleInvoiceTemplateBundle extends AbstractTemplateBundle implements PTTemplateBundle {
  private static final String GENERIC_CUSTOMER_TEXT = "Consumidor Final";

  private static final String BANK_TRANSFER_TEXT = "Transfer\u00eancia Banc\u00e1ria";

  private static final String CASH_TEXT = "Numer\u00e1rio";

  private static final String CREDIT_CARD_TEXT = "Cart\u00e3o Cr\u00e9dito";

  private static final String CHECK_TEXT = "Cheque";

  private static final String DEBIT_CARD_TEXT = "Cart\u00e3o D\u00e9bito";

  private static final String COMPENSATION_TEXT = "Compensa\u00e7\u00e3o de saldos em conta corrente";

  private static final String COMMERCIAL_LETTER_TEXT = "Letra Comercial";

  private static final String RESTAURANT_TICKET_TEXT = "Ticket Restaurante";

  private static final String ATM_TEXT = "Multibanco";

  private static final String EXCHANGE_TEXT = "Permuta";

  private static final String ELECTRONIC_MONEY = "Dinheiro Eletr\u00f3nico";

  private final String softwareCertificationId;

  public PTSimpleInvoiceTemplateBundle(String logoImagePath, InputStream xsltFileStream, String softwareCertificationId) {
    super(logoImagePath, xsltFileStream);
    this.softwareCertificationId = softwareCertificationId;
  }

  @Override public String getGenericCustomer() {
    return PTSimpleInvoiceTemplateBundle.GENERIC_CUSTOMER_TEXT;
  }

  @Override public String getSoftwareCertificationId() {
    return this.softwareCertificationId;
  }

  @Override public String getPaymentMechanismTranslation(Enum<?> pmc) {
    if (null == pmc) {
      return null;
    }
    PaymentMechanism payment = (PaymentMechanism) pmc;
    switch (payment) {
      case BANK_TRANSFER:
      return PTSimpleInvoiceTemplateBundle.BANK_TRANSFER_TEXT;
      case CASH:
      return PTSimpleInvoiceTemplateBundle.CASH_TEXT;
      case CREDIT_CARD:
      return PTSimpleInvoiceTemplateBundle.CREDIT_CARD_TEXT;
      case CHECK:
      return PTSimpleInvoiceTemplateBundle.CHECK_TEXT;
      case DEBIT_CARD:
      return PTSimpleInvoiceTemplateBundle.DEBIT_CARD_TEXT;
      case COMPENSATION:
      return PTSimpleInvoiceTemplateBundle.COMPENSATION_TEXT;
      case COMMERCIAL_LETTER:
      return PTSimpleInvoiceTemplateBundle.COMMERCIAL_LETTER_TEXT;
      case ATM:
      return PTSimpleInvoiceTemplateBundle.ATM_TEXT;
      case RESTAURANT_TICKET:
      return PTSimpleInvoiceTemplateBundle.RESTAURANT_TICKET_TEXT;
      case EXCHANGE:
      return PTSimpleInvoiceTemplateBundle.EXCHANGE_TEXT;
      case ELECTRONIC_MONEY:
      return PTSimpleInvoiceTemplateBundle.ELECTRONIC_MONEY;
      default:
      return null;
    }
  }
}