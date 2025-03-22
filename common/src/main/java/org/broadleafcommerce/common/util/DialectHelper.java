package org.broadleafcommerce.common.util;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * @author Jeff Fischer
 */
@Transactional @Repository(value = "blDialectHelper") public class DialectHelper {
  @PersistenceContext(unitName = "blPU") protected EntityManager defaultEntityManager;

  public Dialect getHibernateDialect() {
    return getHibernateDialect(defaultEntityManager);
  }

  public Dialect getHibernateDialect(EntityManager em) {
    SessionFactoryImplementor factory = (SessionFactoryImplementor) em.unwrap(Session.class).getSessionFactory();
    return factory.getServiceRegistry().getService(JdbcServices.class).getDialect();
  }

  public boolean isOracle() {
    return getHibernateDialect(defaultEntityManager) instanceof Oracle8iDialect;
  }

  public boolean isOracle(EntityManager em) {
    return getHibernateDialect(em) instanceof Oracle8iDialect;
  }

  public boolean isPostgreSql() {
    return getHibernateDialect(defaultEntityManager) instanceof PostgreSQL81Dialect;
  }

  public boolean isPostgreSql(EntityManager em) {
    return getHibernateDialect(em) instanceof PostgreSQL81Dialect;
  }

  public boolean isSqlServer() {
    return getHibernateDialect(defaultEntityManager) instanceof SQLServerDialect;
  }

  public boolean isSqlServer(EntityManager em) {
    return getHibernateDialect(em) instanceof SQLServerDialect;
  }

  public boolean isMySql() {
    return getHibernateDialect(defaultEntityManager) instanceof MySQLDialect;
  }

  public boolean isMySql(EntityManager em) {
    return getHibernateDialect(em) instanceof MySQLDialect;
  }
}