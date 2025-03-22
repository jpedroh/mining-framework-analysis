package com.salesmanager.core.business.services.merchant;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;


import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityService;
import com.salesmanager.core.model.common.GenericEntityList;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.merchant.MerchantStoreCriteria;

public interface MerchantStoreService extends SalesManagerEntityService<Integer, MerchantStore>{
	

	MerchantStore getMerchantStore(String merchantStoreCode)
			throws ServiceException;
	
	MerchantStore getByCode(String code) throws ServiceException;
	
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/services/merchant/MerchantStoreService.java/left.java
	List<MerchantStore> findAllStoreNames() throws ServiceException;

	List<MerchantStore> findAllStoreCodeNameEmail() throws ServiceException;

	Page<MerchantStore> listAll(Optional<String> storeName, int page, int count) throws ServiceException;
	
	Page<MerchantStore> listAllRetailers(Optional<String> storeName, int page, int count) throws ServiceException;
	
	Page<MerchantStore> listChildren(String code, int page, int count) throws ServiceException;
||||||| /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/services/merchant/MerchantStoreService.java/base.java
=======
	List<MerchantStore> listChildren(String code) throws ServiceException;
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/services/merchant/MerchantStoreService.java/right.java

	boolean existByCode(String code);

	void saveOrUpdate(MerchantStore store) throws ServiceException;
	
	GenericEntityList<MerchantStore> getByCriteria(MerchantStoreCriteria criteria) throws ServiceException;

}
