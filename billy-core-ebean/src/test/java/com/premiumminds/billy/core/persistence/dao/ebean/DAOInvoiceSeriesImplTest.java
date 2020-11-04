/*
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy core Ebean.
 *
 * billy core Ebean is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy core Ebean is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy core Ebean. If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.core.persistence.dao.ebean;

import javax.persistence.LockModeType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.premiumminds.billy.core.persistence.entities.InvoiceSeriesEntity;
import com.premiumminds.billy.core.persistence.entities.ebean.JPABusinessEntity;
import com.premiumminds.billy.core.persistence.entities.ebean.JPAInvoiceSeriesEntity;
import com.premiumminds.billy.core.services.UID;

import io.ebean.Ebean;

public class DAOInvoiceSeriesImplTest extends BaseH2Test {

    private static final String rightSeries = "Right Series";

    private static final String wrongSeries = "Wrong Series";

    private static final UID rightBusinessUid = new UID("f01970a9-c004-4f29-a3e1-bf2183248d76");

    private static final UID wrongBusinessUid = new UID("cde197fd-a866-4959-a2bc-6750360947d4");

    private static final UID invoiceSeriesUid = new UID("1796dc4d-462c-468c-9f0f-170b65944341");

    private static DAOInvoiceSeriesImpl daoInvoiceSeriesImpl;

    @BeforeEach
    public void prepare() {
        Ebean.beginTransaction();
        DAOBusinessImpl businessDAO = new DAOBusinessImpl();
        JPABusinessEntity business = new JPABusinessEntity();
        business.setUID(rightBusinessUid);
        business.setName("Test Business");
        businessDAO.create(business);
        Ebean.commitTransaction();

        Ebean.beginTransaction();
        daoInvoiceSeriesImpl = new DAOInvoiceSeriesImpl();
        JPAInvoiceSeriesEntity series = new JPAInvoiceSeriesEntity();
        series.setUID(invoiceSeriesUid);
        series.setBusiness(business);
        series.setSeries(rightSeries);
        daoInvoiceSeriesImpl.create(series);
        Ebean.commitTransaction();
    }

    @Test
    public void getSeries() {
        InvoiceSeriesEntity series = daoInvoiceSeriesImpl.getSeries(
                rightSeries, rightBusinessUid.toString(), null);

        Assertions.assertEquals(series.getUID(), invoiceSeriesUid);
    }

    @Test
    public void getSeries_wrongSeriesId() {
        InvoiceSeriesEntity series = daoInvoiceSeriesImpl.getSeries(
                wrongSeries, rightBusinessUid.toString(), null);

        Assertions.assertNull(series);
    }

    @Test
    public void getSeries_wrongBusinessId() {
        InvoiceSeriesEntity series = daoInvoiceSeriesImpl.getSeries(
                rightSeries, wrongBusinessUid.toString(), null);

        Assertions.assertNull(series);
    }

    @Test
    public void getSeries_concurrent_deadlock() {
        Ebean.beginTransaction();
        InvoiceSeriesEntity series =
                daoInvoiceSeriesImpl.getSeries(rightSeries,
                        rightBusinessUid.toString(), LockModeType.PESSIMISTIC_WRITE);

        long threadTimeout = 1000;
        long millis = System.currentTimeMillis();
        Thread concurrentThread = new Thread() {

            @Override
            public void run() {
                Ebean.beginTransaction();
                InvoiceSeriesEntity series =
                        daoInvoiceSeriesImpl.getSeries(rightSeries,
                                rightBusinessUid.toString(), LockModeType.PESSIMISTIC_WRITE);
                Ebean.commitTransaction();
            }
        };
        concurrentThread.start();
        try {
            concurrentThread.join(threadTimeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long diff = System.currentTimeMillis() - millis;

        Ebean.commitTransaction();

        Assertions.assertTrue(diff >= threadTimeout);
    }
}
