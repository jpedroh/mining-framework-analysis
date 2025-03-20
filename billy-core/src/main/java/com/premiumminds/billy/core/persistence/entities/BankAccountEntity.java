package com.premiumminds.billy.core.persistence.entities;
import com.premiumminds.billy.core.services.entities.BankAccount;

public interface BankAccountEntity extends BankAccount, BaseEntity {
  public void setIBANNumber(String iban);

  public void setBankIdentifier(String bankId);

  public void setBankAccountNumber(String accountNumber);

  public void setOwnerName(String ownerName);
}