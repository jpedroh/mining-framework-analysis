package com.premiumminds.billy.portugal.persistence.entities;
import java.net.URL;
import com.premiumminds.billy.core.persistence.entities.ApplicationEntity;
import com.premiumminds.billy.portugal.services.entities.PTApplication;

public interface PTApplicationEntity extends ApplicationEntity, PTApplication {
  public void setSoftwareCertificateNum(Integer number);

  public void setApplicationKeysPath(URL path);
}