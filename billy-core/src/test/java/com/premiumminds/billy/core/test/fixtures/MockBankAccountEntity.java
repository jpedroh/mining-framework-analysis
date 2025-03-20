package com.premiumminds.billy.core.test.fixtures;
import com.premiumminds.billy.core.persistence.entities.BankAccountEntity;

public class MockBankAccountEntity extends MockBaseEntity implements BankAccountEntity {
  private static final long serialVersionUID = 1L;

  public String iban;

  public String bankId;

  public String accountNumber;

  public String ownerName;

  public MockBankAccountEntity() {
  }

  @Override public String getIBANNumber() {
    return this.iban;
  }

  @Override public String getBankIdentifier() {
    return this.bankId;
  }

  @Override public String getBankAccountNumber() {
    return this.accountNumber;
  }

  @Override public String getOwnerName() {
    return this.ownerName;
  }

  @Override public void setIBANNumber(String iban) {
    this.iban = iban;
  }

  @Override public void setBankIdentifier(String bankId) {
    this.bankId = bankId;
  }

  @Override public void setBankAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  @Override public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }
}