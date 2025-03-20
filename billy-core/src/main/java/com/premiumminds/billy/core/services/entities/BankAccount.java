package com.premiumminds.billy.core.services.entities;
import com.google.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOBankAccount;
import com.premiumminds.billy.core.services.builders.impl.BankAccountBuilderImpl;

public interface BankAccount {
  public static class Builder extends BankAccountBuilderImpl<Builder, BankAccount> {
    @Inject public Builder(DAOBankAccount daoBankAccount) {
      super(daoBankAccount);
    }
  }

  public String getIBANNumber();

  public String getBankIdentifier();

  public String getBankAccountNumber();

  public String getOwnerName();
}