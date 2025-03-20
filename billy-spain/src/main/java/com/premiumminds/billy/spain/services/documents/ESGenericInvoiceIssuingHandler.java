/**
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy spain (ES Pack).
 *
 * billy spain (ES Pack) is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy spain (ES Pack) is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy spain (ES Pack). If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.spain.services.documents;

import java.util.Date;

import javax.inject.Inject;
import javax.persistence.LockModeType;

import com.premiumminds.billy.core.persistence.dao.AbstractDAOGenericInvoice;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.persistence.entities.InvoiceSeriesEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAInvoiceSeriesEntity;
import com.premiumminds.billy.core.services.documents.DocumentIssuingHandler;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.spain.persistence.entities.ESGenericInvoiceEntity;
import com.premiumminds.billy.spain.services.documents.exceptions.InvalidInvoiceDateException;
import com.premiumminds.billy.spain.services.documents.util.ESIssuingParams;

public abstract class ESGenericInvoiceIssuingHandler<T extends ESGenericInvoiceEntity, P extends ESIssuingParams> implements DocumentIssuingHandler<T, P> {

    protected DAOInvoiceSeries daoInvoiceSeries;

    @Inject
    public ESGenericInvoiceIssuingHandler(DAOInvoiceSeries daoInvoiceSeries) {
        this.daoInvoiceSeries = daoInvoiceSeries;
    }
	
	protected <D extends AbstractDAOGenericInvoice<T>> T issue(final T document,
	        final ESIssuingParams parametersES, final D daoInvoice) throws DocumentIssuingException {

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/left.java
		document.initializeEntityDates();
		
		//If the date is null then the invoice date is the current date
		Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/base.java
		ESGenericInvoiceEntity documentEntity = (ESGenericInvoiceEntity) document;
		
		((BaseEntity)document).initializeEntityDates();
		
		//If the date is null then the invoice date is the current date
		Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();
=======
	    String series = parametersES.getInvoiceSeries();
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/left.java
		Integer seriesNumber = 1;
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/base.java
	//		if (systemDate..after(invoiceDate)) {
	//			throw new InvalidInvoiceDateException();
	//		}

		Integer seriesNumber = 1;
=======
	    InvoiceSeriesEntity invoiceSeriesEntity =
	            this.getInvoiceSeries(document, series, LockModeType.PESSIMISTIC_WRITE);

	    ESGenericInvoiceEntity documentEntity = (ESGenericInvoiceEntity) document;
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/left.java
		T latestInvoice = daoInvoice
				.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(), document.getBusiness()
						.getUID().toString());
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/base.java
		ESGenericInvoiceEntity latestInvoice = daoInvoice
				.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(), document.getBusiness()
						.getUID().toString());
=======
	    ((BaseEntity) document).initializeEntityDates();
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/right.java

	    // If the date is null then the invoice date is the current date
	    Date invoiceDate = document.getDate() == null ? new Date() : document.getDate();

	    // if (systemDate..after(invoiceDate)) {
	    // throw new InvalidInvoiceDateException();
	    // }

	    Integer seriesNumber = 1;

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/left.java
		document.setDate(invoiceDate);
		document.setNumber(formatedNumber);
		document.setSeries(invoiceSeriesEntity.getSeries());
		document.setSeriesNumber(seriesNumber);
		document.setBilled(false);
		document.setCancelled(false);
		document.setEACCode(parametersES.getEACCode());
		document.setCurrency(document.getCurrency());
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/base.java
		documentEntity.setDate(invoiceDate);
		documentEntity.setNumber(formatedNumber);
		documentEntity.setSeries(invoiceSeriesEntity.getSeries());
		documentEntity.setSeriesNumber(seriesNumber);
		documentEntity.setBilled(false);
		documentEntity.setCancelled(false);
		documentEntity.setEACCode(parametersES.getEACCode());
		documentEntity.setCurrency(document.getCurrency());
=======
	    ESGenericInvoiceEntity latestInvoice = daoInvoice.getLatestInvoiceFromSeries(invoiceSeriesEntity.getSeries(),
	            document.getBusiness().getUID().toString());
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/left.java
		daoInvoice.create(document);
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/base.java
		daoInvoice.create(documentEntity);
=======
	    if (null != latestInvoice) {
	        seriesNumber = latestInvoice.getSeriesNumber() + 1;
	        Date latestInvoiceDate = latestInvoice.getDate();
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/left.java
		return document;
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/base.java
		return (T) documentEntity;
=======
	        if (latestInvoiceDate.compareTo(invoiceDate) > 0) {
	            throw new InvalidInvoiceDateException();
	        }
	    }

	    String formatedNumber = parametersES.getInvoiceSeries() + "/" + seriesNumber;

	    documentEntity.setDate(invoiceDate);
	    documentEntity.setNumber(formatedNumber);
	    documentEntity.setSeries(invoiceSeriesEntity.getSeries());
	    documentEntity.setSeriesNumber(seriesNumber);
	    documentEntity.setBilled(false);
	    documentEntity.setCancelled(false);
	    documentEntity.setEACCode(parametersES.getEACCode());
	    documentEntity.setCurrency(document.getCurrency());

	    daoInvoice.create(documentEntity);

	    return (T) documentEntity;
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/services/documents/ESGenericInvoiceIssuingHandler.java/right.java
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
