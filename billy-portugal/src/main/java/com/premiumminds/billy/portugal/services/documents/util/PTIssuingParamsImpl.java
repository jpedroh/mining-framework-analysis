package com.premiumminds.billy.portugal.services.documents.util;
import java.security.PrivateKey;
import java.security.PublicKey;
import com.premiumminds.billy.core.services.documents.impl.IssuingParamsImpl;
import com.premiumminds.billy.core.services.exceptions.ParameterNotFoundException;

public class PTIssuingParamsImpl extends IssuingParamsImpl implements PTIssuingParams {
  public PTIssuingParamsImpl() {
    super();
  }

  @Override public String getInvoiceSeries() throws ParameterNotFoundException {
    return (String) this.getParameter(Keys.INVOICE_SERIES);
  }

  @Override public PrivateKey getPrivateKey() throws ParameterNotFoundException {
    return (PrivateKey) this.getParameter(Keys.PRIVATE_KEY);
  }

  @Override public PublicKey getPublicKey() throws ParameterNotFoundException {
    return (PublicKey) this.getParameter(Keys.PUBLIC_KEY);
  }

  @Override public String getPrivateKeyVersion() throws ParameterNotFoundException {
    return (String) this.getParameter(Keys.PRIVATE_KEY_VERSION);
  }

  @Override public String getEACCode() throws ParameterNotFoundException {
    return (String) this.getParameter(Keys.EAC_CODE);
  }

  @Override public void setPublicKey(PublicKey publicKey) {
    this.setParameter(Keys.PUBLIC_KEY, publicKey);
  }

  @Override public void setPrivateKey(PrivateKey privateKey) {
    this.setParameter(Keys.PRIVATE_KEY, privateKey);
  }

  @Override public void setInvoiceSeries(String series) {
    this.setParameter(Keys.INVOICE_SERIES, series);
  }

  @Override public void setPrivateKeyVersion(String version) {
    this.setParameter(Keys.PRIVATE_KEY_VERSION, version);
  }

  @Override public void setEACCode(String eacCode) {
    this.setParameter(Keys.EAC_CODE, eacCode);
  }
}