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
package com.premiumminds.billy.portugal.persistence.dao.jpa;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.premiumminds.billy.portugal.persistence.dao.DAOPTGenericInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTGenericInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTGenericInvoiceEntity;

public class DAOPTGenericInvoiceImpl
    extends AbstractDAOPTGenericInvoiceImpl<PTGenericInvoiceEntity, JPAPTGenericInvoiceEntity>
    implements DAOPTGenericInvoice {

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/left.java
  @Inject
  public DAOPTGenericInvoiceImpl(Provider<EntityManager> emProvider) {
    super(emProvider);
  }
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/base.java
	@Inject
	public DAOPTGenericInvoiceImpl(Provider<EntityManager> emProvider) {
		super(emProvider);
	}
=======
    @Inject
    public DAOPTGenericInvoiceImpl(Provider<EntityManager> emProvider) {
        super(emProvider);
    }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/left.java
  @Override
  public PTGenericInvoiceEntity getEntityInstance() {
    return new JPAPTGenericInvoiceEntity();
  }
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/base.java
	@Override
	public PTGenericInvoiceEntity getEntityInstance() {
		return new JPAPTGenericInvoiceEntity();
	}
=======
    @Override
    public PTGenericInvoiceEntity getEntityInstance() {
        return new JPAPTGenericInvoiceEntity();
    }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/left.java
  @Override
  protected Class<? extends JPAPTGenericInvoiceEntity> getEntityClass() {
    return JPAPTGenericInvoiceEntity.class;
  }
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/base.java
	@Override
	protected Class<? extends JPAPTGenericInvoiceEntity> getEntityClass() {
		return JPAPTGenericInvoiceEntity.class;
	}
=======
    @Override
    protected Class<? extends JPAPTGenericInvoiceEntity> getEntityClass() {
        return JPAPTGenericInvoiceEntity.class;
    }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/right.java

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/left.java
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/base.java
	protected PTBusinessEntity getBusinessEntity(UID uid) {

		QJPAPTBusinessEntity business = QJPAPTBusinessEntity.jPAPTBusinessEntity;
		JPAQuery query = new JPAQuery(this.getEntityManager());

		query.from(business).where(business.uid.eq(uid.getValue()));

		return this.checkEntity(query.singleResult(business),
				PTBusinessEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends PTGenericInvoiceEntity> T findByNumber(UID uidBusiness, String number) {
		QJPAPTGenericInvoiceEntity invoice = QJPAPTGenericInvoiceEntity.jPAPTGenericInvoiceEntity;

		return (T) this.checkEntity(createQuery()
				.from(invoice)
				.where(
						toDSL(invoice.business, QJPAPTBusinessEntity.class).uid.eq(uidBusiness.toString())
						.and(invoice.number.eq(number)))
				.singleResult(invoice), PTGenericInvoiceEntity.class);
	}
=======
    protected PTBusinessEntity getBusinessEntity(UID uid) {

        QJPAPTBusinessEntity business = QJPAPTBusinessEntity.jPAPTBusinessEntity;
        JPAQuery query = new JPAQuery(this.getEntityManager());

        query.from(business).where(business.uid.eq(uid.getValue()));

        return this.checkEntity(query.singleResult(business), PTBusinessEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends PTGenericInvoiceEntity> T findByNumber(UID uidBusiness, String number) {
        QJPAPTGenericInvoiceEntity invoice = QJPAPTGenericInvoiceEntity.jPAPTGenericInvoiceEntity;

        return (T) this.checkEntity(
                this.createQuery()
                        .from(invoice).where(this.toDSL(invoice.business, QJPAPTBusinessEntity.class).uid
                                .eq(uidBusiness.toString()).and(invoice.number.eq(number)))
                        .singleResult(invoice),
                PTGenericInvoiceEntity.class);
    }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-portugal/src/main/java/com/premiumminds/billy/portugal/persistence/dao/jpa/DAOPTGenericInvoiceImpl.java/right.java
}
