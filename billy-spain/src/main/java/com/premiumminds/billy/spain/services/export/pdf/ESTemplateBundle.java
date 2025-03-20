package com.premiumminds.billy.spain.services.export.pdf;
import com.premiumminds.billy.gin.services.export.BillyTemplateBundle;

public interface ESTemplateBundle extends BillyTemplateBundle {
  static final String BANK_TRANSFER_TEXT = "Transferencia Bancaria";

  static final String CASH_TEXT = "Met\u00e1lico";

  static final String CREDIT_CARD_TEXT = "Tarjeta Cr\u00e9dito";

  static final String CHECK_TEXT = "Cheque";

  static final String DEBIT_CARD_TEXT = "Tarjeta D\u00e9bito";

  static final String COMPENSATION_TEXT = "Compensaci\u00f3n de saldos en cuenta corriente";

  static final String COMMERCIAL_LETTER_TEXT = "Letra Comercial";

  static final String RESTAURANT_TICKET_TEXT = "Ticket Restaurante";

  static final String ATM_TEXT = "Dat\u00e1fono";

  static final String EXCHANGE_TEXT = "Permuta";

  static final String ELECTRONIC_MONEY_TEXT = "Dinero Electr\u00f3nico";
}