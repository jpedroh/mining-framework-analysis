package com.premiumminds.billy.core.persistence.dao;
import javax.persistence.LockModeType;
import com.premiumminds.billy.core.persistence.entities.BaseEntity;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.util.EntityFactory;

/**
 * @author Francisco Vargas
 *
 * @param <T>
 *        The entity type being managed by the DAO implementation
 */
public interface DAO<T extends BaseEntity> extends EntityFactory<T> {
  /**
     * Initialized a transaction context
     */
  public void beginTransaction();

  /**
     * Rolls back the active transaction context, discarding all transaction
     * changes.
     */
  public void rollback();

  /**
     * Sets the active transaction for rollback. Meaning that the transaction
     * will remain active but all changes will be discarded.
     */
  public void setForRollback();

  /**
     * Tells whether a transaction is set for rollback or not
     *
     * @return true if the active transaction if set to rollback. Returns false
     *         otherwise.
     */
  public boolean isSetForRollback();

  /**
     * Commits the active transaction
     */
  public void commit();

  /**
     * Lock the entity type being managed by the DAO.
     */
  public void lock(T entity, LockModeType type);

  /**
     * Tells whether a transaction is currently active or not
     *
     * @return true if a transaction is currently active. Returns false
     *         otherwise.
     */
  public boolean isTransactionActive();

  /**
     * Gets a persisted instance of type T
     *
     * @param uid
     *        The {@link UID} identifying the wanted instance
     * @return The requested instance of type T
     */
  public T get(UID uid);

  /**
     * Persists a new instance of type T
     *
     * @param entity
     *        The entity to be persisted
     * @return The entity instance after it's been persisted
     */
  public T create(T entity);

  /**
     * Persists an updated version of type T
     *
     * @param entity
     *        The updated entity instance
     * @return The entity instance after it's been updated
     */
  public T update(T entity);

  /**
     * Tells whether an instance of type T is persisted.
     *
     * @param uid
     *        The unique identifier of the requested instance.
     * @return true if the instance is persisted. Returns false otherwise.
     */
  public boolean exists(UID uid);
}