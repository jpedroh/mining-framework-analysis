package com.premiumminds.billy.core.persistence.entities;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import com.premiumminds.billy.core.services.entities.Tax;

public interface TaxEntity extends Tax, BaseEntity {
  public <T extends ContextEntity> void setContext(T context);

  public void setDesignation(String designation);

  public void setDescription(String description);

  public void setCode(String code);

  public void setValidFrom(Date from);

  public void setValidTo(Date to);

  public void setTaxRateType(TaxRateType type);

  public void setPercentageRateValue(BigDecimal value);

  public void setFlatRateAmount(BigDecimal amount);

  public void setCurrency(Currency currency);

  public void setValue(BigDecimal value);
}