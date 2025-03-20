package com.premiumminds.billy.portugal.util;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.core.util.BillyMathContext;
import com.premiumminds.billy.portugal.services.certification.CertificationManager;

public class GenerateHash {
  public static String generateHash(@NotNull PrivateKey privateKey, @NotNull PublicKey publicKey, @NotNull Date invoiceDate, @NotNull Date systemEntryDate, @NotNull String invoiceNumber, @NotNull BigDecimal grossTotal, String previousInvoiceHash) throws DocumentIssuingException {
    try {
      String sourceString = GenerateHash.generateSourceHash(invoiceDate, systemEntryDate, invoiceNumber, grossTotal, previousInvoiceHash);
      CertificationManager certificationManager = new CertificationManager();
      certificationManager.setAutoVerifyHash(true);
      certificationManager.setPrivateKey(privateKey);
      certificationManager.setPublicKey(publicKey);
      return certificationManager.getHashBase64(sourceString);
    } catch (Throwable e) {
      throw new DocumentIssuingException(e);
    }
  }

  public static String generateSourceHash(Date invoiceDate, Date systemEntryDate, String invoiceNumber, BigDecimal grossTotal, String previousInvoiceHash) {
    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss");
    StringBuilder builder = new StringBuilder();
    builder.append(date.format(invoiceDate)).append(';').append(dateTime.format(systemEntryDate)).append(';').append(invoiceNumber).append(';').append(grossTotal.setScale(BillyMathContext.SCALE, BillyMathContext.get().getRoundingMode())).append(';').append(previousInvoiceHash == null ? "" : previousInvoiceHash);
    String sourceString = builder.toString();
    return sourceString;
  }
}