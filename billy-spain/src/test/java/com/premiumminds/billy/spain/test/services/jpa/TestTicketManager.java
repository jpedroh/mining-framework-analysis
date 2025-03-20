package com.premiumminds.billy.spain.test.services.jpa;
import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.premiumminds.billy.core.services.TicketManager;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.Ticket;

public class TestTicketManager extends ESJPAAbstractTest {
  private static final String OBJECT_UID = "object_uid";

  private static final Date CREATION_DATE = new Date();

  private static final Date PROCESS_DATE = new Date();

  private TicketManager manager = null;

  String ticket = null;

  @Before public void setUp() {
    this.manager = this.getInstance(TicketManager.class);
  }

  @Test public void generateTicketTest() {
    this.ticket = this.manager.generateTicket(this.getInstance(Ticket.Builder.class));
    Assert.assertTrue(this.ticket != null);
  }

  @Test public void ticketExistsTest() {
    this.ticket = this.manager.generateTicket(this.getInstance(Ticket.Builder.class));
    Assert.assertTrue(this.manager.ticketExists(this.ticket));
  }

  @Test public void updateTicketTest() {
    this.ticket = this.manager.generateTicket(this.getInstance(Ticket.Builder.class));
    this.manager.updateTicket(new UID(this.ticket), new UID(TestTicketManager.OBJECT_UID), TestTicketManager.CREATION_DATE, TestTicketManager.PROCESS_DATE);
    Assert.assertTrue(this.manager.ticketExists(this.ticket));
  }
}