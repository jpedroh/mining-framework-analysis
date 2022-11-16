/*
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
package com.premiumminds.billy.portugal.test.services.documents.handler;

import com.google.inject.Guice;
import com.premiumminds.billy.core.exceptions.InvalidTicketException;
import com.premiumminds.billy.core.exceptions.SeriesUniqueCodeNotFilled;
import com.premiumminds.billy.core.services.StringID;
import com.premiumminds.billy.core.services.TicketManager;
import com.premiumminds.billy.core.services.entities.Ticket;
import com.premiumminds.billy.core.services.entities.documents.GenericInvoice;
import com.premiumminds.billy.core.services.exceptions.DocumentIssuingException;
import com.premiumminds.billy.core.services.exceptions.DocumentSeriesDoesNotExistException;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSimpleInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTSimpleInvoiceEntity;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.TYPE;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice;
import com.premiumminds.billy.portugal.services.entities.PTSimpleInvoice.CLIENTTYPE;
import com.premiumminds.billy.portugal.services.persistence.PTSimpleInvoicePersistenceService;
import com.premiumminds.billy.portugal.test.PTAbstractTest;
import com.premiumminds.billy.portugal.test.PTMockDependencyModule;
import com.premiumminds.billy.portugal.test.PTPersistencyAbstractTest;
import com.premiumminds.billy.portugal.test.services.documents.PTDocumentAbstractTest;
import com.premiumminds.billy.portugal.test.util.PTBusinessTestUtil;
import com.premiumminds.billy.portugal.test.util.PTSimpleInvoiceTestUtil;
import com.premiumminds.billy.portugal.util.Services;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPTSimpleInvoiceIssuingHandlerWithTicket extends PTDocumentAbstractTest {

    private static final TYPE DEFAULT_TYPE = TYPE.FS;
    private static final SourceBilling SOURCE_BILLING = SourceBilling.P;

    private StringID<GenericInvoice> issuedInvoiceUID;
    private StringID<Ticket> ticketUID;
    private TicketManager ticketManager;

    @BeforeEach
    public void setUpNewSimpleInvoice() {

        try {
            this.setUpParamenters();
            this.parameters.setInvoiceSeries(PTPersistencyAbstractTest.DEFAULT_SERIES);

            final String uid = "business";
            this.createSeries(uid);
            PTBusinessEntity business = new PTBusinessTestUtil(PTAbstractTest.injector).getBusinessEntity(uid);
            PTSimpleInvoice.Builder simpleInvoiceBuilder =
                    new PTSimpleInvoiceTestUtil(PTAbstractTest.injector).getSimpleInvoiceBuilder(business,
                            TestPTSimpleInvoiceIssuingHandlerWithTicket.SOURCE_BILLING, CLIENTTYPE.CUSTOMER);

            this.ticketManager = this.getInstance(TicketManager.class);

            this.ticketUID = this.ticketManager.generateTicket(this.getInstance(Ticket.Builder.class));

            Services services = new Services(PTAbstractTest.injector);
            services.issueDocument(simpleInvoiceBuilder, this.parameters, ticketUID);

            PTSimpleInvoice simpleInvoice = simpleInvoiceBuilder.build();
            this.issuedInvoiceUID = simpleInvoice.getUID();

        } catch (InvalidTicketException e) {
            e.printStackTrace();
        } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIssuedInvoiceSimpleWithTicket() throws DocumentIssuingException {
        PTSimpleInvoice issuedInvoice = this.getInstance(DAOPTSimpleInvoice.class).get(this.issuedInvoiceUID);

        PTSimpleInvoicePersistenceService service =
                PTAbstractTest.injector.getInstance(PTSimpleInvoicePersistenceService.class);

        PTSimpleInvoiceEntity ticketEntity = (PTSimpleInvoiceEntity) service.getWithTicket(this.ticketUID);

        Assertions.assertTrue(issuedInvoice != null);
        Assertions.assertEquals(PTPersistencyAbstractTest.DEFAULT_SERIES, issuedInvoice.getSeries());
        Assertions.assertTrue(1 == issuedInvoice.getSeriesNumber());
        String formatedNumber = TestPTSimpleInvoiceIssuingHandlerWithTicket.DEFAULT_TYPE + " " +
                PTPersistencyAbstractTest.DEFAULT_SERIES + "/1";
        Assertions.assertEquals(formatedNumber, issuedInvoice.getNumber());
        Assertions.assertEquals(TestPTSimpleInvoiceIssuingHandlerWithTicket.SOURCE_BILLING,
                issuedInvoice.getSourceBilling());

        Assertions.assertTrue(this.ticketManager.ticketExists(this.ticketUID) == true);
        Assertions.assertTrue(ticketEntity != null);
        Assertions.assertTrue(ticketEntity.getUID().getIdentifier().equals(issuedInvoice.getUID().getIdentifier()));
        Assertions.assertTrue(ticketEntity.getHash().equals(issuedInvoice.getHash()));
        Assertions.assertTrue(ticketEntity.getNumber().equals(issuedInvoice.getNumber()));
        Assertions.assertTrue(ticketEntity.getSeries().equals(issuedInvoice.getSeries()));

    }

    @Test
    public void testTicketAssociateEntity() throws DocumentIssuingException {

        PTSimpleInvoicePersistenceService service =
                PTAbstractTest.injector.getInstance(PTSimpleInvoicePersistenceService.class);
        PTSimpleInvoiceEntity ticketEntity = null;
        StringID<Ticket> noResultUID = StringID.fromValue("noresult");
        StringID<Ticket> notIssuedUID = this.ticketManager.generateTicket(this.getInstance(Ticket.Builder.class));

        try {
            ticketEntity = (PTSimpleInvoiceEntity) service.getWithTicket(noResultUID);
        } catch (NoResultException e) {

        }

        Assertions.assertTrue(this.ticketManager.ticketExists(noResultUID) == false);
        Assertions.assertTrue(ticketEntity == null);

        try {
            ticketEntity = (PTSimpleInvoiceEntity) service.getWithTicket(notIssuedUID);
        } catch (NoResultException e) {
        }

        Assertions.assertTrue(this.ticketManager.ticketExists(notIssuedUID) == true);
        Assertions.assertFalse(this.ticketManager.ticketIssued(notIssuedUID) == false);
        Assertions.assertTrue(ticketEntity == null);
    }

    @Test
    public void testIssueWithUsedTicket() {
        Services services = new Services(PTAbstractTest.injector);
        PTSimpleInvoiceEntity entity = null;
        this.parameters.setInvoiceSeries(PTPersistencyAbstractTest.DEFAULT_SERIES);

        PTBusinessEntity business = new PTBusinessTestUtil(PTAbstractTest.injector).getBusinessEntity("business");
        PTSimpleInvoice.Builder builder = new PTSimpleInvoiceTestUtil(PTAbstractTest.injector).getSimpleInvoiceBuilder(
                business, TestPTSimpleInvoiceIssuingHandlerWithTicket.SOURCE_BILLING, CLIENTTYPE.CUSTOMER);

        try {

            entity = (PTSimpleInvoiceEntity) services.issueDocument(builder, this.parameters,
                    StringID.fromValue(this.issuedInvoiceUID.getIdentifier()));
        } catch (InvalidTicketException e) {

        } catch (DocumentIssuingException | SeriesUniqueCodeNotFilled | DocumentSeriesDoesNotExistException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(entity == null);
    }

    @Test
    public void testOpenCloseConnections() {
        PTSimpleInvoicePersistenceService persistenceService =
                PTAbstractTest.injector.getInstance(PTSimpleInvoicePersistenceService.class);
        PTBusinessEntity business = new PTBusinessTestUtil(PTAbstractTest.injector).getBusinessEntity("business");
        PTSimpleInvoice.Builder testinvoice =
                new PTSimpleInvoiceTestUtil(PTAbstractTest.injector).getSimpleInvoiceBuilder(business,
                        TestPTSimpleInvoiceIssuingHandlerWithTicket.SOURCE_BILLING, CLIENTTYPE.CUSTOMER);

        EntityManager em = PTAbstractTest.injector.getInstance(EntityManager.class);
        em.getTransaction().begin();

        TicketManager newTicketManager = PTAbstractTest.injector.getInstance(TicketManager.class);
        StringID<Ticket> testValue = newTicketManager.generateTicket(this.getInstance(Ticket.Builder.class));
        em.getTransaction().commit();

        em.clear();

        Services services = new Services(Guice.createInjector(new PTMockDependencyModule()));

        try {
            services.issueDocument(testinvoice, this.parameters, testValue);
        } catch (Exception e) {
        }

        PTSimpleInvoiceEntity ticketEntity = null;
        try {
            ticketEntity = (PTSimpleInvoiceEntity) persistenceService.getWithTicket(testValue);
        } catch (Exception e) {
        }
        Assertions.assertTrue(ticketEntity == null);
    }

}
