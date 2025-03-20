package com.premiumminds.billy.core.persistence.dao.jpa;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import com.mysema.query.jpa.impl.JPAQuery;
import com.premiumminds.billy.core.persistence.dao.DAOInvoiceSeries;
import com.premiumminds.billy.core.persistence.entities.InvoiceSeriesEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.JPAInvoiceSeriesEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.QJPABusinessEntity;
import com.premiumminds.billy.core.persistence.entities.jpa.QJPAInvoiceSeriesEntity;
import com.premiumminds.billy.core.services.entities.InvoiceSeries;

public class DAOInvoiceSeriesImpl extends AbstractDAO<InvoiceSeriesEntity, JPAInvoiceSeriesEntity> implements DAOInvoiceSeries {
  @Inject public DAOInvoiceSeriesImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }

  @Override public InvoiceSeriesEntity getEntityInstance() {
    return new JPAInvoiceSeriesEntity();
  }

  @Override protected Class<? extends JPAInvoiceSeriesEntity> getEntityClass() {
    return JPAInvoiceSeriesEntity.class;
  }

  @Override public InvoiceSeriesEntity getSeries(String series, String businessUID, LockModeType lockMode) {
    QJPAInvoiceSeriesEntity entity = QJPAInvoiceSeriesEntity.jPAInvoiceSeriesEntity;
    JPAQuery query = new JPAQuery(this.getEntityManager());
    query = new JPAQuery(this.getEntityManager());
    query.from(entity);
    query.where(entity.series.eq(series));
    query.where(this.toDSL(entity.business, QJPABusinessEntity.class).uid.eq(businessUID));
    InvoiceSeries seriesEntity = query.setLockMode(lockMode).singleResult(entity);
    return (InvoiceSeriesEntity) seriesEntity;
  }
}