package com.premiumminds.billy.core.services.documents.impl;
import java.util.HashMap;
import java.util.Map;
import com.premiumminds.billy.core.services.documents.IssuingParams;
import com.premiumminds.billy.core.services.exceptions.ParameterNotFoundException;

public class IssuingParamsImpl implements IssuingParams {
  Map<String, Object> parameters;

  public IssuingParamsImpl() {
    this.parameters = new HashMap<>();
  }

  @Override public Object getParameter(String key) throws ParameterNotFoundException {
    if (this.parameters.containsKey(key)) {
      return this.parameters.get(key);
    } else {
      return null;
    }
  }

  @Override public void setParameter(String key, Object obj) {
    this.parameters.put(key, obj);
  }
}