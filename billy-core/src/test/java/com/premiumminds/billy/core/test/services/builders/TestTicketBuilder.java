package com.premiumminds.billy.core.test.services.builders;
import org.junit.Test;
import junit.framework.Assert;
import org.mockito.Mockito;
import com.premiumminds.billy.core.persistence.dao.DAOTicket;
import com.premiumminds.billy.core.services.entities.Ticket;
import com.premiumminds.billy.core.test.AbstractTest;
import com.premiumminds.billy.core.test.fixtures.MockTicketEntity;

public class TestTicketBuilder extends AbstractTest {
  private static final String TICKET_YML = AbstractTest.YML_CONFIGS_DIR + "Ticket.yml";

  @Test public void doTest() {
    MockTicketEntity mockTicket = this.createMockEntity(MockTicketEntity.class, TestTicketBuilder.TICKET_YML);
    Mockito.when(this.getInstance(DAOTicket.class).getEntityInstance()).thenReturn(new MockTicketEntity());
    Ticket.Builder builder = this.getInstance(Ticket.Builder.class);
    builder.setCreationDate(mockTicket.getCreationDate()).setProcessDate(mockTicket.getProcessDate()).setObjectUID(mockTicket.getObjectUID());
    Ticket ticket = builder.build();
    ticket.setUID(mockTicket.getUID());
    Assert.assertTrue(ticket != null);
    Assert.assertEquals(mockTicket.getCreationDate(), ticket.getCreationDate());
    Assert.assertEquals(mockTicket.getProcessDate(), ticket.getProcessDate());
    Assert.assertEquals(mockTicket.getObjectUID(), ticket.getObjectUID());
    Assert.assertEquals(mockTicket.getUID(), ticket.getUID());
  }
}