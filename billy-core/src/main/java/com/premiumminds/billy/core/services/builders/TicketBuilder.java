package com.premiumminds.billy.core.services.builders;
import java.util.Date;
import com.premiumminds.billy.core.services.Builder;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.Ticket;

public interface TicketBuilder<TBuilder extends TicketBuilder<TBuilder, TTicket>, TTicket extends Ticket> extends Builder<TTicket> {
  public TBuilder setObjectUID(UID objectUID);

  public TBuilder setCreationDate(Date creationDate);

  public TBuilder setProcessDate(Date processDate);
}