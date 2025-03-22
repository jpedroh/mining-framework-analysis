package org.broadleafcommerce.common.config.dao;
import org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.time.SystemTime;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository(value = "blModuleConfigurationDao") public class ModuleConfigurationDaoImpl implements ModuleConfigurationDao {
  @PersistenceContext(unitName = "blPU") protected EntityManager em;

  @Resource(name = "blEntityConfiguration") protected EntityConfiguration entityConfiguration;

  protected Long currentDateResolution = 10000L;

  protected Date cachedDate = SystemTime.asDate();

  protected Date getCurrentDateAfterFactoringInDateResolution() {
    Date returnDate = SystemTime.getCurrentDateWithinTimeResolution(cachedDate, getCurrentDateResolution());
    if (returnDate != cachedDate) {
      if (SystemTime.shouldCacheDate()) {
        cachedDate = returnDate;
      }
    }
    return returnDate;
  }

  @Override public ModuleConfiguration readById(Long id) {
    return em.find(AbstractModuleConfiguration.class, id);
  }

  @Override public ModuleConfiguration save(ModuleConfiguration config) {
    if (config.getIsDefault()) {
      Query batchUpdate = em.createNamedQuery("BC_BATCH_UPDATE_MODULE_CONFIG_DEFAULT");
      batchUpdate.setParameter("configType", config.getModuleConfigurationType().getType());
      batchUpdate.executeUpdate();
    }
    return em.merge(config);
  }

  @Override public void delete(ModuleConfiguration config) {
    ((Status) config).setArchived('Y');
    em.merge(config);
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ModuleConfiguration> readAllByType(ModuleConfigurationType type) {
    Query query = em.createNamedQuery("BC_READ_MODULE_CONFIG_BY_TYPE");
    query.setParameter("configType", type.getType());
    query.setHint(QueryHints.HINT_CACHEABLE, true);
    query.setHint(QueryHints.HINT_CACHE_REGION, "query.ConfigurationModuleElements");
    return query.getResultList();
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ModuleConfiguration> readActiveByType(ModuleConfigurationType type) {
    Query query = em.createNamedQuery("BC_READ_ACTIVE_MODULE_CONFIG_BY_TYPE");
    query.setParameter("configType", type.getType());
    Date myDate = getCurrentDateAfterFactoringInDateResolution();
    query.setParameter("currentDate", myDate);
    query.setHint(QueryHints.HINT_CACHEABLE, true);
    query.setHint(QueryHints.HINT_CACHE_REGION, "query.ConfigurationModuleElements");
    return query.getResultList();
  }

  @SuppressWarnings(value = { "unchecked" }) @Override public List<ModuleConfiguration> readByType(Class<? extends ModuleConfiguration> type) {
    Query query = em.createQuery("SELECT config FROM " + type.getName() + " config");
    query.setHint(QueryHints.HINT_CACHEABLE, true);
    query.setHint(QueryHints.HINT_CACHE_REGION, "query.ConfigurationModuleElements");
    return query.getResultList();
  }

  @Override public Long getCurrentDateResolution() {
    return currentDateResolution;
  }

  @Override public void setCurrentDateResolution(Long currentDateResolution) {
    this.currentDateResolution = currentDateResolution;
  }
}