package com.premiumminds.billy.core.services.builders;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import com.premiumminds.billy.core.persistence.entities.ShippingPointEntity;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.Payment;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoiceEntry;
import com.premiumminds.billy.core.util.DiscountType;

public interface GenericInvoiceBuilder<TBuilder extends GenericInvoiceBuilder<TBuilder, TEntry, TDocument>, TEntry extends GenericInvoiceEntry, TDocument extends GenericInvoice> extends Builder<TDocument> {
  public TBuilder setBusinessUID(UID businessUID);

  public TBuilder setCustomerUID(UID customerUID);

  public TBuilder setSupplierUID(UID supplier);

  public TBuilder setOfficeNumber(String number);

  public TBuilder setDate(Date date);

  public <T extends ShippingPointEntity> TBuilder setShippingOrigin(Builder<T> originBuilder);

  public <T extends ShippingPointEntity> TBuilder setShippingDestination(Builder<T> destinationBuilder);

  public TBuilder setPaymentTerms(String terms);

  public TBuilder setSelfBilled(boolean selfBilled);

  public TBuilder setSourceId(String source);

  public TBuilder setGeneralLedgerDate(Date date);

  public TBuilder setBatchId(String id);

  public TBuilder setTransactionId(String id);

  public TBuilder addReceiptNumber(String number);

  public <T extends GenericInvoiceEntry> TBuilder addEntry(Builder<T> entryBuilder);

  public TBuilder setSettlementDescription(String description);

  public TBuilder setSettlementDiscount(BigDecimal discount);

  public TBuilder setSettlementDate(Date date);

  public <T extends Payment> TBuilder addPayment(Builder<T> paymentBuilder);

  public TBuilder setDiscounts(DiscountType type, BigDecimal... discounts);

  public TBuilder setCurrency(Currency currency);

  public TBuilder setScale(int scale);
}