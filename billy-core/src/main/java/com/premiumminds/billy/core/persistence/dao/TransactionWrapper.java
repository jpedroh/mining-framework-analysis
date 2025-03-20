package com.premiumminds.billy.core.persistence.dao;

/**
 * @author Francisco Vargas
 *
 *         A class that ensures that ONE transaction is active. If a transaction
 *         is not already active, then a new one is created. Transaction
 *         management is ignored otherwise.
 * @param <T>
 *        The transaction return type
 */
public abstract class TransactionWrapper<T extends java.lang.Object> {
  private DAO<?> dao;

  private boolean wasActive;

  /**
     * The TransactionWrapper constructor
     *
     * @param dao
     *        The {@link DAO} managing the transaction.
     */
  public TransactionWrapper(DAO<?> dao) {
    this.dao = dao;
  }

  private void setupTransaction() {
    this.wasActive = this.dao.isTransactionActive();
    if (!this.wasActive) {
      this.dao.beginTransaction();
    }
  }

  /**
     * Runs the transaction instructions
     *
     * @return The transaction return value
     * @throws Exception
     *         an exception wrapping all thrown exceptions in the
     *         runTransaction block
     */
  public abstract T runTransaction() throws Exception;

  /**
     * Executes the transaction wrapping steps
     *
     * @return The transaction return value
     * @throws Exception
     */
  public T execute() throws Exception {
    this.setupTransaction();
    T result = null;
    try {
      result = this.runTransaction();
    } catch (Exception e) {
      this.rollback();
      this.finalizeTransaction();
      throw e;
    }
    this.finalizeTransaction();
    return result;
  }

  /**
     * Sets the transaction to be commited
     */
  public void commit() {
  }

  /**
     * Sets the transaction for rollback
     */
  public void rollback() {
    this.dao.setForRollback();
  }

  private void finalizeTransaction() {
    if (!this.wasActive) {
      if (this.dao.isSetForRollback()) {
        this.dao.rollback();
      } else {
        this.dao.commit();
      }
    }
  }
}