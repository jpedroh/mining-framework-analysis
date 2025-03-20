package com.premiumminds.billy.portugal.persistence.entities.jpa;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAApplicationEntity;
import com.premiumminds.billy.core.services.entities.Contact;
import com.premiumminds.billy.portugal.Config;
import com.premiumminds.billy.portugal.persistence.entities.PTApplicationEntity;

@Entity @Audited @Table(name = Config.TABLE_PREFIX + "APPLICATION") public class JPAPTApplicationEntity extends JPAApplicationEntity implements PTApplicationEntity {
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  @Basic(optional = false) @Column(name = "NUMBER") protected Integer number;

  @Basic(optional = true) @Column(name = "KEYS_PATH") protected String path;

  public JPAPTApplicationEntity() {
  }

  @Override public List<Contact> getContacts() {
    return super.getContacts();
  }

  @Override public Integer getSoftwareCertificationNumber() {
    return this.number;
  }

  @Override public void setSoftwareCertificateNum(Integer number) {
    this.number = number;
  }

  @Override public URL getApplicationKeysPath() throws MalformedURLException {
    return new URL(this.path);
  }

  @Override public void setApplicationKeysPath(URL path) {
    this.path = path.toExternalForm();
  }
}