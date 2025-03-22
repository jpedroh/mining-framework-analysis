<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop-model/src/main/java/com/salesmanager/shop/model/shop/PersistableMerchantStore.java/left.java
fatal: path 'sm-shop-model/src/main/java/com/salesmanager/shop/model/shop/PersistableMerchantStore.java' exists on disk, but not in 'd59b6c7f9651433f407ad646636e2f80bc34a93f'
||||||| /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop-model/src/main/java/com/salesmanager/shop/model/shop/PersistableMerchantStore.java/base.java
package com.salesmanager.shop.model.shop;

import com.salesmanager.shop.model.references.PersistableAddress;

public class PersistableMerchantStore extends MerchantStoreEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PersistableAddress address;

	public PersistableAddress getAddress() {
		return address;
	}

	public void setAddress(PersistableAddress address) {
		this.address = address;
	}

}
=======
package com.salesmanager.shop.model.shop;

import com.salesmanager.shop.model.references.PersistableAddress;

public class PersistableMerchantStore extends MerchantStoreEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PersistableAddress address;
	//code of parent store (can be null if retailer)
	private String retailerStore;

	public PersistableAddress getAddress() {
		return address;
	}

	public void setAddress(PersistableAddress address) {
		this.address = address;
	}

  public String getRetailerStore() {
    return retailerStore;
  }

  public void setRetailerStore(String retailerStore) {
    this.retailerStore = retailerStore;
  }

}
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-shop-model/src/main/java/com/salesmanager/shop/model/shop/PersistableMerchantStore.java/right.java
