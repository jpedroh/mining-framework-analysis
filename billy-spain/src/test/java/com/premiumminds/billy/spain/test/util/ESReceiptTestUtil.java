package com.premiumminds.billy.spain.test.util;
import java.math.BigDecimal;
import java.util.Date;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder.AmountType;
import com.premiumminds.billy.spain.persistence.entities.ESBusinessEntity;
import com.premiumminds.billy.spain.persistence.entities.ESReceiptEntity;
import com.premiumminds.billy.spain.services.entities.ESReceipt;
import com.premiumminds.billy.spain.services.entities.ESReceiptEntry;

public class ESReceiptTestUtil {
  protected static final Boolean BILLED = false;

  protected static final Boolean CANCELLED = false;

  protected static final Boolean SELFBILL = false;

  protected static final String SOURCE_ID = "SOURCE";

  protected static final String SERIES = "A";

  protected static final Integer SERIES_NUMBER = 1;

  protected static final int MAX_PRODUCTS = 5;

  protected final Injector injector;

  protected final ESReceiptEntryTestUtil receiptEntry;

  protected final ESBusinessTestUtil businesses;

  protected final ESPaymentTestUtil payments;

  public ESReceiptTestUtil(Injector injector) {
    this.injector = injector;
    this.receiptEntry = new ESReceiptEntryTestUtil(injector);
    this.businesses = new ESBusinessTestUtil(injector);
    this.payments = new ESPaymentTestUtil(injector);
  }

  public ESReceiptEntity getReceiptEntity() {
    return (ESReceiptEntity) this.getReceiptBuilder(this.businesses.getBusinessEntity()).build();
  }

  public ESReceipt.Builder getReceiptBuilder(ESBusinessEntity business) {
    ESReceipt.Builder receiptBuilder = this.injector.getInstance(ESReceipt.Builder.class);
    ESReceiptEntry.Builder entryBuilder = this.receiptEntry.getReceiptEntryBuilder().setUnitAmount(AmountType.WITH_TAX, new BigDecimal("0.45"));
    return receiptBuilder.setBilled(ESReceiptTestUtil.BILLED).setCancelled(ESReceiptTestUtil.CANCELLED).setSelfBilled(ESReceiptTestUtil.SELFBILL).setSourceId(ESReceiptTestUtil.SOURCE_ID).setDate(new Date()).setBusinessUID(business.getUID()).addPayment(this.payments.getPaymentBuilder()).addEntry(entryBuilder);
  }
}