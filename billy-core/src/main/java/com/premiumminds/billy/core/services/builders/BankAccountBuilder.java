package com.premiumminds.billy.core.services.builders;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.entities.BankAccount;

public interface BankAccountBuilder<TBuilder extends BankAccountBuilder<TBuilder, TBankAccount>, TBankAccount extends BankAccount> extends Builder<TBankAccount> {
  public TBuilder setIBANNumber(String iban);

  public TBuilder setBankIdentifier(String bankId);

  public TBuilder setBankAccountNumber(String accountNumber);

  public TBuilder setOwnerName(String ownerName);
}