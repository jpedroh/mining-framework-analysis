/**
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy portugal (PT Pack).
 *
 * billy portugal (PT Pack) is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy portugal (PT Pack) is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy portugal (PT Pack). If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.portugal.services.documents;

import java.util.Date;

import javax.inject.Inject;
import javax.persistence.LockModeType;

import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.persistence.entities.InvoiceSeriesEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAInvoiceSeriesEntity;
import com.premiumminds.billy.core.services.documents.DocumentIssuingHandler;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.portugal.persistence.dao.AbstractDAOPTGenericInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTGenericInvoiceEntity;
import com.premiumminds.billy.portugal.services.documents.exceptions.InvalidInvoiceDateException;
import com.premiumminds.billy.portugal.services.documents.exceptions.InvalidInvoiceTypeException;
import com.premiumminds.billy.portugal.services.documents.exceptions.InvalidSourceBillingException;
import com.premiumminds.billy.portugal.services.documents.util.PTIssuingParams;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;
import com.premiumminds.billy.portugal.util.GenerateHash;

public abstract class PTGenericInvoiceIssuingHandler<T extends PTGenericInvoiceEntity, P extends PTIssuingParams> 
implements DocumentIssuingHandler<T, P>	 {

    protected DAOInvoiceSeries daoInvoiceSeries;

    @Inject
    public PTGenericInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries) {
        this.daoInvoiceSeries = daoInvoiceSeries;
    }

    protected void validateDocumentType(TYPE documentType, TYPE expectedType, String series)
            throws InvalidInvoiceTypeException {
        if (documentType != expectedType) {
            throw new InvalidInvoiceTypeException(series, documentType.toString(), expectedType.toString());
        }
    }

	protected <D extends AbstractDAOPTGenericInvoice<T>> T issue(final T document,
	        final PTIssuingParams parametersPT, final D daoInvoice, final TYPE invoiceType)
	        throws DocumentIssuingException {

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/left.java
		String series = parametersPT.getInvoiceSeries();
		
		InvoiceSeriesEntity invoiceSeriesEntity = getInvoiceSeries(document,
				series, LockModeType.PESSIMISTIC_WRITE);
		
		SourceBilling sourceBilling = ((PTGenericInvoice) document)
				.getSourceBilling();
		
		document.initializeEntityDates();
		
		//If the date is null then the invoice date is the current date
		Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();
		Date systemDate = document.getCreateTimestamp();
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/base.java
		String series = parametersPT.getInvoiceSeries();
		
		InvoiceSeriesEntity invoiceSeriesEntity = getInvoiceSeries(document,
				series, LockModeType.PESSIMISTIC_WRITE);
		
		PTGenericInvoiceEntity documentEntity = (PTGenericInvoiceEntity) document;
		SourceBilling sourceBilling = ((PTGenericInvoice) document)
				.getSourceBilling();
		
		((BaseEntity)document).initializeEntityDates();
		
		//If the date is null then the invoice date is the current date
		Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();
		Date systemDate = document.getCreateTimestamp();
=======
	    String series = parametersPT.getInvoiceSeries();
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/left.java
		Integer seriesNumber = 1;
		String previousHash = null;
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/base.java
	//		if (systemDate..after(invoiceDate)) {
	//			throw new InvalidInvoiceDateException();
	//		}

		Integer seriesNumber = 1;
		String previousHash = null;
=======
	    InvoiceSeriesEntity invoiceSeriesEntity =
	            this.getInvoiceSeries(document, series, LockModeType.PESSIMISTIC_WRITE);

	    PTGenericInvoiceEntity documentEntity = (PTGenericInvoiceEntity) document;
	    SourceBilling sourceBilling = ((PTGenericInvoice) document).getSourceBilling();
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/left.java
		T latestInvoice = daoInvoice
				.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(), document.getBusiness()
						.getUID().toString());
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/base.java
		PTGenericInvoiceEntity latestInvoice = daoInvoice
				.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(), document.getBusiness()
						.getUID().toString());
=======
	    ((BaseEntity) document).initializeEntityDates();
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/left.java
		if (null != latestInvoice) {
			seriesNumber = latestInvoice.getSeriesNumber() + 1;
			previousHash = latestInvoice.getHash();
			Date latestInvoiceDate = latestInvoice.getDate();
			
			validateDocumentType(invoiceType, latestInvoice.getType(), invoiceSeriesEntity.getSeries());
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/base.java
		if (null != latestInvoice) {
			seriesNumber = latestInvoice.getSeriesNumber() + 1;
			previousHash = latestInvoice.getHash();
			Date latestInvoiceDate = latestInvoice.getDate();
			PTGenericInvoiceIssuingHandler.this.validateDocumentType(
					invoiceType, latestInvoice.getType(), invoiceSeriesEntity.getSeries());
=======
	    // If the date is null then the invoice date is the current date
	    Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();
	    Date systemDate = document.getCreateTimestamp();
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/right.java

	    // if (systemDate..after(invoiceDate)) {
	    // throw new InvalidInvoiceDateException();
	    // }

	    Integer seriesNumber = 1;
	    String previousHash = null;

	    PTGenericInvoiceEntity latestInvoice = daoInvoice.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(),
	            document.getBusiness().getUID().toString());

	    if (null != latestInvoice) {
	        seriesNumber = latestInvoice.getSeriesNumber() + 1;
	        previousHash = latestInvoice.getHash();
	        Date latestInvoiceDate = latestInvoice.getDate();
	        PTGenericInvoiceIssuingHandler.this.validateDocumentType(invoiceType, latestInvoice.getType(),
	                invoiceSeriesEntity.getSeries());

	        if (!latestInvoice.getSourceBilling().equals(sourceBilling)) {
	            throw new InvalidSourceBillingException(invoiceSeriesEntity.getSeries(), sourceBilling.toString(),
	                    latestInvoice.getSourceBilling().toString());
	        }

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/left.java
		document.setDate(invoiceDate);
		document.setNumber(formatedNumber);
		document.setSeries(invoiceSeriesEntity.getSeries());
		document.setSeriesNumber(seriesNumber);
		document.setHash(newHash);
		document.setBilled(false);
		document.setCancelled(false);
		document.setType(invoiceType);
		document.setSourceHash(sourceHash);
		document.setHashControl(parametersPT.getPrivateKeyVersion());
		document.setEACCode(parametersPT.getEACCode());
		document.setCurrency(document.getCurrency());
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/base.java
		documentEntity.setDate(invoiceDate);
		documentEntity.setNumber(formatedNumber);
		documentEntity.setSeries(invoiceSeriesEntity.getSeries());
		documentEntity.setSeriesNumber(seriesNumber);
		documentEntity.setHash(newHash);
		documentEntity.setBilled(false);
		documentEntity.setCancelled(false);
		documentEntity.setType(invoiceType);
		documentEntity.setSourceHash(sourceHash);
		documentEntity.setHashControl(parametersPT.getPrivateKeyVersion());
		documentEntity.setEACCode(parametersPT.getEACCode());
		documentEntity.setCurrency(document.getCurrency());
=======
	        if (latestInvoiceDate.compareTo(invoiceDate) > 0) {
	            throw new InvalidInvoiceDateException();
	        }
	    }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/left.java
		daoInvoice.create(document);
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/base.java
		daoInvoice.create(documentEntity);
=======
	    String formatedNumber = invoiceType.toString() + " " + parametersPT.getInvoiceSeries() + "/" + seriesNumber;
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/left.java
		return document;
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/base.java
		return (T) documentEntity;
=======
	    String newHash = GenerateHash.generateHash(parametersPT.getPrivateKey(), parametersPT.getPublicKey(),
	            invoiceDate, systemDate, formatedNumber, document.getAmountWithTax(), previousHash);

	    String sourceHash = GenerateHash.generateSourceHash(invoiceDate, systemDate, formatedNumber,
	            document.getAmountWithTax(), previousHash);

	    documentEntity.setDate(invoiceDate);
	    documentEntity.setNumber(formatedNumber);
	    documentEntity.setSeries(invoiceSeriesEntity.getSeries());
	    documentEntity.setSeriesNumber(seriesNumber);
	    documentEntity.setHash(newHash);
	    documentEntity.setBilled(false);
	    documentEntity.setCancelled(false);
	    documentEntity.setType(invoiceType);
	    documentEntity.setSourceHash(sourceHash);
	    documentEntity.setHashControl(parametersPT.getPrivateKeyVersion());
	    documentEntity.setEACCode(parametersPT.getEACCode());
	    documentEntity.setCurrency(document.getCurrency());

	    daoInvoice.create(documentEntity);

	    return (T) documentEntity;
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/services/documents/PTGenericInvoiceIssuingHandler.java/right.java

	}

    private InvoiceSeriesEntity getInvoiceSeries(final T document, String series,
            LockModeType lockMode) {
        InvoiceSeriesEntity invoiceSeriesEntity =
                this.daoInvoiceSeries.getSeries(series, document.getBusiness().getUID().toString(), lockMode);

        if (null == invoiceSeriesEntity) {
            InvoiceSeriesEntity entity = new JPAInvoiceSeriesEntity();
            entity.setBusiness(document.getBusiness());
            entity.setSeries(series);

            invoiceSeriesEntity = this.daoInvoiceSeries.create(entity);
        }
        return invoiceSeriesEntity;
    }
}
