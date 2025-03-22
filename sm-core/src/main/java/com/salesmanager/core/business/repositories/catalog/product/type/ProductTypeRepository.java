package com.salesmanager.core.business.repositories.catalog.product.type;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.salesmanager.core.model.catalog.product.type.ProductType;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

	ProductType findByCode(String code);
	
<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/repositories/catalog/product/type/ProductTypeRepository.java/left.java
	//List<ProductType> findByMerchant(int id);
||||||| /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/repositories/catalog/product/type/ProductTypeRepository.java/base.java
=======
	//List<ProductType> findByMerchant();
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/repositories/catalog/product/type/ProductTypeRepository.java/right.java
}
