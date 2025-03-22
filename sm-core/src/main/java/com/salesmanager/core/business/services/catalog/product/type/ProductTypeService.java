package com.salesmanager.core.business.services.catalog.product.type;

import java.util.List;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityService;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

public interface ProductTypeService extends SalesManagerEntityService<Long, ProductType> {

	ProductType getProductType(String productTypeCode) throws ServiceException;
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/services/catalog/product/type/ProductTypeService.java/left.java
	
	List<ProductType> getByMerchant(String merchant, Language language) throws ServiceException;

    public List<ProductType> getByMerchant(MerchantStore store, Language language) throws ServiceException;
||||||| /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/services/catalog/product/type/ProductTypeService.java/base.java
=======
	
	List<ProductType> getByMerchant(String merchant, Language language) throws ServiceException;
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/services/catalog/product/type/ProductTypeService.java/right.java

}
