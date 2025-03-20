package com.premiumminds.billy.core.services.builders.impl;
import java.util.Date;
import javax.inject.Inject;
import com.premiumminds.billy.core.persistence.dao.DAOTicket;
import com.premiumminds.billy.core.persistence.entities.TicketEntity;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.builders.TicketBuilder;
import com.premiumminds.billy.core.services.entities.Ticket;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.core.util.Localizer;

public class TicketBuilderImpl<TBuilder extends TicketBuilderImpl<TBuilder, TTicket>, TTicket extends Ticket> extends AbstractBuilder<TBuilder, TTicket> implements TicketBuilder<TBuilder, TTicket> {
  protected static final Localizer LOCALIZER = new Localizer("com/premiumminds/billy/core/i18n/FieldNames");

  protected DAOTicket daoTicket;

  @Inject public TicketBuilderImpl(DAOTicket daoTicket) {
    super(daoTicket);
    this.daoTicket = daoTicket;
  }

  @Override public TBuilder setObjectUID(UID objectUID) {
    BillyValidator.mandatory(objectUID, ContactBuilderImpl.LOCALIZER.getString("field.uid"));
    this.getTypeInstance().setObjectUID(objectUID);
    return this.getBuilder();
  }

  @Override protected void validateInstance() throws javax.validation.ValidationException {
  }

  @SuppressWarnings(value = { "unchecked" }) @Override protected TicketEntity getTypeInstance() {
    return (TicketEntity) super.getTypeInstance();
  }

  @Override public TBuilder setCreationDate(Date creationDate) {
    BillyValidator.mandatory(creationDate, ContactBuilderImpl.LOCALIZER.getString("field.date"));
    this.getTypeInstance().setCreationDate(creationDate);
    return this.getBuilder();
  }

  @Override public TBuilder setProcessDate(Date processDate) {
    BillyValidator.mandatory(processDate, ContactBuilderImpl.LOCALIZER.getString("field.date"));
    this.getTypeInstance().setProcessDate(processDate);
    return this.getBuilder();
  }
}