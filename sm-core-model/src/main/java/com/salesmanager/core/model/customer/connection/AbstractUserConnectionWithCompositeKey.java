<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/b80f72c11fb19cc7ad447095953e7e7885b64ab9/sm-core-model/src/main/java/com/salesmanager/core/model/customer/connection/AbstractUserConnectionWithCompositeKey.java/left.java
package com.salesmanager.core.model.customer.connection;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.salesmanager.core.constants.SchemaConstant;

@Deprecated
@MappedSuperclass
@Table(name="USERCONNECTION", schema=SchemaConstant.SALESMANAGER_SCHEMA, uniqueConstraints = { @UniqueConstraint(columnNames = { "userId",
		"providerId", "userRank" }) })
public abstract class AbstractUserConnectionWithCompositeKey extends
		AbstractUserConnection<UserConnectionPK> {

	@Id
	private UserConnectionPK primaryKey = new UserConnectionPK();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getProviderId() {
		return primaryKey.getProviderId();
	}

	@Override
	public void setProviderId(String providerId) {
		primaryKey.setProviderId(providerId);
	}

	@Override
	public String getProviderUserId() {
		return primaryKey.getProviderUserId();
	}

	@Override
	public void setProviderUserId(String providerUserId) {
		primaryKey.setProviderUserId(providerUserId);
	}

	@Override
	public String getUserId() {
		return primaryKey.getUserId();
	}

	@Override
	public void setUserId(String userId) {
		primaryKey.setUserId(userId);
	}

	@Override
	protected UserConnectionPK getId() {
		return primaryKey;
	}

}
||||||| /usr/src/app/output/shopizer-ecommerce/shopizer/b80f72c11fb19cc7ad447095953e7e7885b64ab9/sm-core-model/src/main/java/com/salesmanager/core/model/customer/connection/AbstractUserConnectionWithCompositeKey.java/base.java
package com.salesmanager.core.model.customer.connection;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.salesmanager.core.constants.SchemaConstant;

@MappedSuperclass
@Table(name="USERCONNECTION", schema=SchemaConstant.SALESMANAGER_SCHEMA, uniqueConstraints = { @UniqueConstraint(columnNames = { "userId",
		"providerId", "userRank" }) })
public abstract class AbstractUserConnectionWithCompositeKey extends
		AbstractUserConnection<UserConnectionPK> {

	@Id
	private UserConnectionPK primaryKey = new UserConnectionPK();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getProviderId() {
		return primaryKey.getProviderId();
	}

	@Override
	public void setProviderId(String providerId) {
		primaryKey.setProviderId(providerId);
	}

	@Override
	public String getProviderUserId() {
		return primaryKey.getProviderUserId();
	}

	@Override
	public void setProviderUserId(String providerUserId) {
		primaryKey.setProviderUserId(providerUserId);
	}

	@Override
	public String getUserId() {
		return primaryKey.getUserId();
	}

	@Override
	public void setUserId(String userId) {
		primaryKey.setUserId(userId);
	}

	@Override
	protected UserConnectionPK getId() {
		return primaryKey;
	}

}
=======
fatal: path 'sm-core-model/src/main/java/com/salesmanager/core/model/customer/connection/AbstractUserConnectionWithCompositeKey.java' exists on disk, but not in '4ce8226c52271034398dc68fdc137b6f230ac31e'
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/b80f72c11fb19cc7ad447095953e7e7885b64ab9/sm-core-model/src/main/java/com/salesmanager/core/model/customer/connection/AbstractUserConnectionWithCompositeKey.java/right.java
