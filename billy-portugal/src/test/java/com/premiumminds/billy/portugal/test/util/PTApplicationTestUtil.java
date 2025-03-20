package com.premiumminds.billy.portugal.test.util;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.inject.Injector;
import com.premiumminds.billy.portugal.services.entities.PTApplication;
import com.premiumminds.billy.portugal.services.entities.PTContact;

public class PTApplicationTestUtil {
  private static final String KEYS_PATH = "http://url";

  private static final String COMPANY_NAME = "company_name";

  private static final String COMPANY_TAX_ID = "12432353426435";

  private static final String APP_NAME = "APP";

  private static final Integer SW_CERTIFICATE_NUMBER = 1;

  private static final String VERSION = "1";

  private static final String WEBSITE = "http://app.ex";

  private Injector injector;

  private PTContactTestUtil contact;

  public PTApplicationTestUtil(Injector injector) {
    this.injector = injector;
    this.contact = new PTContactTestUtil(injector);
  }

  public PTApplication.Builder getApplicationBuilder(String appName, String version, String companyName, String companyTaxId, String website, Integer swCertificateNumber, String keysPath, PTContact.Builder contactBuilder) throws MalformedURLException {
    PTApplication.Builder applicationBuilder = this.injector.getInstance(PTApplication.Builder.class);
    applicationBuilder.addContact(contactBuilder).setApplicationKeysPath(new URL(keysPath)).setDeveloperCompanyName(companyName).setDeveloperCompanyTaxIdentifier(companyTaxId).setName(appName).setSoftwareCertificationNumber(swCertificateNumber).setVersion(version).setWebsiteAddress(website);
    return applicationBuilder;
  }

  public PTApplication.Builder getApplicationBuilder() throws MalformedURLException {
    PTContact.Builder contactBuilder = this.contact.getContactBuilder();
    return this.getApplicationBuilder(PTApplicationTestUtil.APP_NAME, PTApplicationTestUtil.VERSION, PTApplicationTestUtil.COMPANY_NAME, PTApplicationTestUtil.COMPANY_TAX_ID, PTApplicationTestUtil.WEBSITE, PTApplicationTestUtil.SW_CERTIFICATE_NUMBER, PTApplicationTestUtil.KEYS_PATH, contactBuilder);
  }
}