package com.premiumminds.billy.core.persistence.entities;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import com.premiumminds.billy.core.services.builders.GenericInvoiceEntryBuilder.AmountType;
import com.premiumminds.billy.core.services.entities.Product;
import com.premiumminds.billy.core.services.entities.ShippingPoint;
import com.premiumminds.billy.core.services.entities.Tax;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice.CreditOrDebit;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoiceEntry;

public interface GenericInvoiceEntryEntity extends GenericInvoiceEntry, BaseEntity {
  public void setEntryNumber(Integer number);

  public <T extends ShippingPoint> void setShippingOrigin(T origin);

  public <T extends ShippingPoint> void setShippingDestination(T destination);

  public <T extends Product> void setProduct(T product);

  public void setQuantity(BigDecimal quantity);

  public void setUnitOfMeasure(String unit);

  public void setUnitAmountWithTax(BigDecimal amount);

  public void setUnitAmountWithoutTax(BigDecimal amount);

  public void setUnitTaxAmount(BigDecimal amount);

  public void setUnitDiscountAmount(BigDecimal amount);

  public void setAmountWithTax(BigDecimal amount);

  public void setAmountWithoutTax(BigDecimal amount);

  public void setTaxAmount(BigDecimal amount);

  public void setDiscountAmount(BigDecimal amount);

  public void setTaxPointDate(Date date);

  @Override public <T extends GenericInvoice> List<T> getDocumentReferences();

  public void setDescription(String description);

  public void setCreditOrDebit(CreditOrDebit creditOrDebit);

  public void setShippingCostsAmount(BigDecimal amount);

  public void setCurrency(Currency currency);

  public void setExchangeRateToDocumentCurrency(BigDecimal rate);

  @Override public <T extends Tax> List<T> getTaxes();

  public void setTaxExemptionReason(String exemptionReason);

  public void setAmountType(AmountType type);
}