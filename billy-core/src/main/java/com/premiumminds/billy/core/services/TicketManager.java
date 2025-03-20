package com.premiumminds.billy.core.services;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.inject.Inject;
import com.premiumminds.billy.core.Config;
import com.premiumminds.billy.core.exceptions.InvalidTicketException;
import com.premiumminds.billy.core.persistence.dao.DAOTicket;
import com.premiumminds.billy.core.persistence.entities.TicketEntity;

public class TicketManager implements Serializable {
  private static final long serialVersionUID = Config.SERIAL_VERSION;

  private DAOTicket daoTicket = null;

  @Inject public TicketManager(DAOTicket daoTicket) {
    this.daoTicket = daoTicket;
  }

  public String generateTicket(Builder<?> ticketBuilder) throws InvalidTicketException {
    TicketEntity newTicket = (TicketEntity) ticketBuilder.build();
    UID uid = new UID(UUID.randomUUID().toString());
    newTicket.setUID(uid);
    if (this.ticketExists(newTicket.getUID().getValue())) {
      throw new InvalidTicketException();
    }
    this.daoTicket.create(newTicket);
    return newTicket.getUID().getValue();
  }

  public boolean ticketExists(String ticketUID) {
    return this.daoTicket.exists(new UID(ticketUID));
  }

  public boolean ticketIssued(String ticketUID) throws InvalidTicketException {
    if (!this.ticketExists(ticketUID)) {
      throw new InvalidTicketException();
    }
    TicketEntity ticket = this.daoTicket.get(new UID(ticketUID));
    return ticket.getObjectUID() != null;
  }

  public void updateTicket(UID ticketUID, UID objectUID, Date creationDate, Date processDate) throws InvalidTicketException {
    if (!this.ticketExists(ticketUID.getValue())) {
      throw new InvalidTicketException();
    }
    TicketEntity ticket = this.daoTicket.get(ticketUID);
    ticket.setCreationDate(creationDate);
    ticket.setProcessDate(processDate);
    ticket.setObjectUID(objectUID);
    this.daoTicket.update(ticket);
  }
}