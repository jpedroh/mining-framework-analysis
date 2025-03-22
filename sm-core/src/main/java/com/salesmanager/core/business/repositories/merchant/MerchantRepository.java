package com.salesmanager.core.business.repositories.merchant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salesmanager.core.model.merchant.MerchantStore;

public interface MerchantRepository extends JpaRepository<MerchantStore, Integer>, MerchantRepositoryCustom {

<<<<<<< /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/repositories/merchant/MerchantRepository.java/left.java
	@Query("select m from MerchantStore m "
			+ "left join fetch m.parent mp"
			+ "left join fetch m.country mc "
			+ "left join fetch m.currency mc "
			+ "left join fetch m.zone mz "
			+ "left join fetch m.defaultLanguage md "
			+ "left join fetch m.languages mls where m.code = ?1")
||||||| /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/repositories/merchant/MerchantRepository.java/base.java
	@Query("select m from MerchantStore m left join fetch m.country mc left join fetch m.currency mc left join fetch m.zone mz left join fetch m.defaultLanguage md left join fetch m.languages mls where m.code = ?1")
=======
	@Query("select m from MerchantStore m left join fetch m.parent mp left join fetch m.country mc left join fetch m.currency mc left join fetch m.zone mz left join fetch m.defaultLanguage md left join fetch m.languages mls where m.code = ?1")
>>>>>>> /usr/src/app/output/shopizer-ecommerce/shopizer/e992f347e0b4f6502966d46fffa4c60cb4c89211/sm-core/src/main/java/com/salesmanager/core/business/repositories/merchant/MerchantRepository.java/right.java
	MerchantStore findByCode(String code);
	
	@Query("select m from MerchantStore m left join fetch m.parent mp left join fetch m.country mc left join fetch m.currency mc left join fetch m.zone mz left join fetch m.defaultLanguage md left join fetch m.languages mls where m.id = ?1")
	MerchantStore getById(int id);
	
	
	@Query("select distinct m from MerchantStore m left join fetch m.parent mp left join fetch m.country mc left join fetch m.currency mc left join fetch m.zone mz left join fetch m.defaultLanguage md left join fetch m.languages mls where mp.code = ?1")
	List<MerchantStore> getByParent(String code);

	@Query("SELECT COUNT(m) > 0 FROM MerchantStore m WHERE m.code = :code")
	boolean existsByCode(String code);
	
	@Query("select new com.salesmanager.core.model.merchant.MerchantStore(m.id, m.code, m.storename) from MerchantStore m")
	List<MerchantStore> findAllStoreNames();

	@Query("select new com.salesmanager.core.model.merchant.MerchantStore(m.id, m.code, m.storename, m.storeEmailAddress) from MerchantStore m")
	List<MerchantStore> findAllStoreCodeNameEmail();
}
