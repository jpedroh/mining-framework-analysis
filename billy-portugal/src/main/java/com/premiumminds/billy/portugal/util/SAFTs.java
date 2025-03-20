package com.premiumminds.billy.portugal.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import com.google.inject.Injector;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTApplication;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTBusiness;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;
import com.premiumminds.billy.portugal.services.export.saftpt.PTSAFTFileGenerator;
import com.premiumminds.billy.portugal.services.export.saftpt.PTSAFTFileGenerator.SAFTVersion;

public class SAFTs {
  private static final String TMP_SAFT = System.getProperty("java.io.tmpdir") + "/saft.xml";

  private final Injector injector;

  private final PTSAFTFileGenerator generator;

  public SAFTs(Injector injector) {
    this.injector = injector;
    this.generator = this.getInstance(PTSAFTFileGenerator.class);
  }

  public InputStream export(UID uidApplication, UID uidBusiness, String certificateNumber, Date from, Date to, SAFTVersion version) throws SAFTPTExportException, IOException {
    return this.export(uidApplication, uidBusiness, certificateNumber, from, to, SAFTs.TMP_SAFT, version);
  }

  public InputStream export(UID uidApplication, UID uidBusiness, String certificateNumber, Date from, Date to, String resultPath, SAFTVersion version) throws SAFTPTExportException, IOException {
    File outputFile = new File(resultPath);
    OutputStream oStream = new FileOutputStream(outputFile);
    this.generator.generateSAFTFile(oStream, this.getInstance(DAOPTBusiness.class).get(uidBusiness), this.getInstance(DAOPTApplication.class).get(uidApplication), certificateNumber, from, to, version);
    IOUtils.closeQuietly(oStream);
    return new FileInputStream(outputFile);
  }

  private <T extends java.lang.Object> T getInstance(Class<T> clazz) {
    return this.injector.getInstance(clazz);
  }
}