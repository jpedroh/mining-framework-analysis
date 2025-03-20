package com.premiumminds.billy.portugal.services.export.saftpt;
import java.io.OutputStream;
import java.util.Date;
import javax.inject.Inject;
import com.premiumminds.billy.portugal.persistence.entities.PTApplicationEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.services.export.exceptions.SAFTPTExportException;

public class PTSAFTFileGenerator {
  @Inject private com.premiumminds.billy.portugal.services.export.saftpt.v1_02_01.PTSAFTFileGenerator saftGenV1_02_01;

  @Inject private com.premiumminds.billy.portugal.services.export.saftpt.v1_03_01.PTSAFTFileGenerator saftGenV1_03_01;

  @Inject private com.premiumminds.billy.portugal.services.export.saftpt.v1_04_01.PTSAFTFileGenerator saftGenV1_04_01;

  public static enum SAFTVersion {
    CURRENT,
    V10201,
    V10301,
    V10401
  }

  /**
	 * Constructs a new SAFT a.k.a. AuditFile
	 * 
	 * @param targetStream
	 * 
	 * @param businessEntity
	 *            - the company
	 * @param application
	 * @param certificateNumber
	 * @param fromDate
	 * @param toDate
	 * @param daoCustomer
	 * @param daoSupplier
	 * @param daoProduct
	 * @param daoPTTax
	 * @param daoPTRegionContext
	 * @param daoPTInvoice
	 * @param daoPTSimpleInvoice
	 * @param daoPTCreditNote
	 * @return the SAFT for that business entity, given lists of customers,
	 *         products, taxes and financial documents; depends on a period of
	 *         time
	 * @throws SAFTPTExportException
	 */
  public void generateSAFTFile(final OutputStream targetStream, final PTBusinessEntity businessEntity, final PTApplicationEntity application, final String certificateNumber, final Date fromDate, final Date toDate, final SAFTVersion version) throws SAFTPTExportException {
    switch (version) {
      case V10201:
      saftGenV1_02_01.generateSAFTFile(targetStream, businessEntity, application, certificateNumber, fromDate, toDate);
      return;
      case V10301:
      saftGenV1_03_01.generateSAFTFile(targetStream, businessEntity, application, certificateNumber, fromDate, toDate);
      return;
      case V10401:
      case CURRENT:
      default:
      saftGenV1_04_01.generateSAFTFile(targetStream, businessEntity, application, certificateNumber, fromDate, toDate);
      return;
    }
  }
}