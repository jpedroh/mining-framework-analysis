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
package com.premiumminds.billy.spain.persistence.dao.jpa;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.premiumminds.billy.spain.persistence.dao.DAOESGenericInvoice;
import com.premiumminds.billy.spain.persistence.entities.ESGenericInvoiceEntity;
import com.premiumminds.billy.spain.persistence.entities.jpa.JPAESGenericInvoiceEntity;

public class DAOESGenericInvoiceImpl extends AbstractDAOESGenericInvoiceImpl<ESGenericInvoiceEntity, JPAESGenericInvoiceEntity> 
implements DAOESGenericInvoice {

    @Inject
    public DAOESGenericInvoiceImpl(Provider<EntityManager> emProvider) {
        super(emProvider);
    }

    @Override
    public ESGenericInvoiceEntity getEntityInstance() {
        return new JPAESGenericInvoiceEntity();
    }

    @Override
    protected Class<? extends JPAESGenericInvoiceEntity> getEntityClass() {
        return JPAESGenericInvoiceEntity.class;
    }

<<<<<<< /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/persistence/dao/jpa/DAOESGenericInvoiceImpl.java/left.java
||||||| /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/persistence/dao/jpa/DAOESGenericInvoiceImpl.java/base.java
	protected ESBusinessEntity getBusinessEntity(UID uid) {

		QJPAESBusinessEntity business = QJPAESBusinessEntity.jPAESBusinessEntity;
		JPAQuery query = new JPAQuery(this.getEntityManager());

		query.from(business).where(business.uid.eq(uid.getValue()));

		return this.checkEntity(query.singleResult(business),
				ESBusinessEntity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ESGenericInvoiceEntity> T findByNumber(UID uidBusiness, String number) {
		QJPAESGenericInvoiceEntity invoice = QJPAESGenericInvoiceEntity.jPAESGenericInvoiceEntity;

		return (T) this.checkEntity(createQuery()
				.from(invoice)
				.where(
						toDSL(invoice.business, QJPAESBusinessEntity.class).uid.eq(uidBusiness.toString())
						.and(invoice.number.eq(number)))
				.singleResult(invoice), ESGenericInvoiceEntity.class);
	}
=======
    protected ESBusinessEntity getBusinessEntity(UID uid) {

        QJPAESBusinessEntity business = QJPAESBusinessEntity.jPAESBusinessEntity;
        JPAQuery query = new JPAQuery(this.getEntityManager());

        query.from(business).where(business.uid.eq(uid.getValue()));

        return this.checkEntity(query.singleResult(business), ESBusinessEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ESGenericInvoiceEntity> T findByNumber(UID uidBusiness, String number) {
        QJPAESGenericInvoiceEntity invoice = QJPAESGenericInvoiceEntity.jPAESGenericInvoiceEntity;

        return (T) this.checkEntity(
                this.createQuery()
                        .from(invoice).where(this.toDSL(invoice.business, QJPAESBusinessEntity.class).uid
                                .eq(uidBusiness.toString()).and(invoice.number.eq(number)))
                        .singleResult(invoice),
                ESGenericInvoiceEntity.class);
    }
>>>>>>> /usr/src/app/output/premium-minds/billy/e481179c9fa61835d2db73bd8cdcb9f47123f892/billy-spain/src/main/java/com/premiumminds/billy/spain/persistence/dao/jpa/DAOESGenericInvoiceImpl.java/right.java
}
