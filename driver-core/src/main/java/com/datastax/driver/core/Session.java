/*
 *      Copyright (C) 2012 DataStax Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.datastax.driver.core;

import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


/**
 * A session holds connections to a Cassandra cluster, allowing it to be queried.
 *
 * Each session maintains multiple connections to the cluster nodes,
 * provides policies to choose which node to use for each query (round-robin on
 * all nodes of the cluster by default), and handles retries for failed query (when
 * it makes sense), etc...
 * <p>
 * Session instances are thread-safe and usually a single instance is enough
 * per application. However, a given session can only be set to one keyspace
 * at a time, so one instance per keyspace is necessary.
 */
public interface Session {
    /**
     * Executes the provided query.
     *
     * This is a convenience method for {@code execute(new SimpleStatement(query))}.
     *
     * @param query the CQL query to execute.
     * @return the result of the query. That result will never be null but can
     * be empty (and will be for any non SELECT query).
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to execute this query.
     * @throws QueryExecutionException if the query triggered an execution
     * exception, i.e. an exception thrown by Cassandra when it cannot execute
     * the query with the requested consistency level successfully.
     * @throws QueryValidationException if the query if invalid (syntax error,
     * unauthorized or any other validation problem).
     */
    public ResultSet execute(String query);

    /**
     * Executes the provided query using the provided value.
     *
     * This is a convenience method for {@code execute(new SimpleStatement(query, values))}.
     *
     * @param query
     * 		the CQL query to execute (that can be either a {@code Statement} or a {@code BoundStatement}). If it is a {@code BoundStatement}, all variables must have been bound (the statement must
     * 		be ready).
     * @return the result of the query. That result will never be null but can
    be empty (and will be for any non SELECT query).
     * @throws NoHostAvailableException
     * 		if no host in the cluster can be
     * 		contacted successfully to execute this query.
     * @throws QueryExecutionException
     * 		if the query triggered an execution
     * 		exception, i.e. an exception thrown by Cassandra when it cannot execute
     * 		the query with the requested consistency level successfully.
     * @throws QueryValidationException
     * 		if the query if invalid (syntax error,
     * 		unauthorized or any other validation problem).
     * @throws IllegalStateException
     * 		if {@code query} is a {@code BoundStatement}
     * 		but {@code !query.isReady()}.
     */
    public abstract ResultSet execute(String query, Object... values);

    /**
     * Executes the provided query.
     *
     * This method blocks until at least some result has been received from the
     * database. However, for SELECT queries, it does not guarantee that the
     * result has been received in full. But it does guarantee that some
     * response has been received from the database, and in particular
     * guarantee that if the request is invalid, an exception will be thrown
     * by this method.
     *
     * @param query
     * 		the CQL query to execute (that can be either a {@code Statement} or a {@code BoundStatement}). If it is a {@code BoundStatement}, all variables must have been bound (the statement must
     * 		be ready).
     * @return a future on the result of the query.
     * @throws IllegalStateException
     * 		if {@code query} is a {@code BoundStatement}
     * 		but {@code !query.isReady()}.
     * @throws QueryExecutionException
     * 		if the query triggered an execution
     * 		exception, i.e. an exception thrown by Cassandra when it cannot execute
     * 		the query with the requested consistency level successfully.
     * @throws QueryValidationException
     * 		if the query if invalid (syntax error,
     * 		unauthorized or any other validation problem).
     */
    public abstract ResultSet execute(Statement statement);

    /**
     * Executes the provided query asynchronously.
     *
     * This is a convenience method for {@code executeAsync(new SimpleStatement(query))}.
     *
     * @param query the CQL query to execute.
     * @return a future on the result of the query.
     */
    public ResultSetFuture executeAsync(String query);

    /**
     * Executes the provided query asynchronously using the provided values.
     *
     * This is a convenience method for {@code executeAsync(new SimpleStatement(query, values))}.
     *
     * @param statement
     * 		the statement to prepare
     * @return the prepared statement corresponding to {@code statement}.
     * @throws NoHostAvailableException
     * 		if no host in the cluster can be
     * 		contacted successfully to prepare this statement.
     */
    public abstract ResultSetFuture executeAsync(String query, Object... values);

    /**
     * Executes the provided query asynchronously.
     *
     * This method does not block. It returns as soon as the query has been
     * passed to the underlying network stack. In particular, returning from
     * this method does not guarantee that the query is valid or has even been
     * submitted to a live node. Any exception pertaining to the failure of the
     * query will be thrown when accessing the {@link ResultSetFuture}.
     *
     * Note that for queries that doesn't return a result (INSERT, UPDATE and
     * DELETE), you will need to access the ResultSetFuture (that is call one of
     * its get method to make sure the query was successful.
     *
     * @param statement
     * 		the CQL query to execute (that can be either any {@code Statement}.
     * @return a future on the result of the query.
     */
    public abstract ResultSetFuture executeAsync(Statement statement);

    /**
     * Prepares the provided query string.
     *
     * @param query the CQL query string to prepare
     * @return the prepared statement corresponding to {@code query}.
     *
     * @throws NoHostAvailableException if no host in the cluster can be
     * contacted successfully to prepare this query.
     */
    public PreparedStatement prepare(String query);

    /**
     * Prepares the provided query.
     * <p>
     * This method is essentially a shortcut for {@code prepare(statement.getQueryString())},
     * but note that the resulting {@code PreparedStamenent} will inherit the query properties
     * set on {@code statement}. Concretely, this means that in the following code:
     * <pre>
     * RegularStatement toPrepare = new SimpleStatement("SELECT * FROM test WHERE k=?").setConsistencyLevel(ConsistencyLevel.QUORUM);
     * PreparedStatement prepared = session.prepare(toPrepare);
     * session.execute(prepared.bind("someValue"));
     * </pre>
     * the final execution will be performed with Quorum consistency.
     *
     * @param timeout
     * 		how long to wait for the session to shutdown.
     * @param unit
     * 		the unit for the timeout.
     * @return {@code true} if the session has been properly shutdown within
    the {@code timeout}, {@code false} otherwise.
     * @throws IllegalArgumentException
     * 		if {@code statement.getValues() != null}
     * 		(values for executing a prepared statement should be provided after preparation
     * 		though the {@link PreparedStatement#bind} method or through a corresponding
     * 		{@link BoundStatement}).
     */
    public abstract PreparedStatement prepare(RegularStatement statement);

    /**
     * Initiates a shutdown of this session instance.
     *
     * This method is asynchronous and return a future on the completion
     * of the shutdown process. As soon a the session is shutdown, no
     * new request will be accepted, but already submitted queries are
     * allowed to complete. Shutdown closes all connections of this
     * session  and reclaims all resources used by it.
     * <p>
     * If for some reason you wish to expedite this process, the
     * {@link ShutdownFuture#force} can be called on the result future.
     * <p>
     * This method has no particular effect if the session was already shut
     * down (in which case the returned future will return immediately).
     * <p>
     * Note that if you want to shut down the full {@code Cluster} instance
     * this session is part of, you should use {@link Cluster#shutdown} instead
     * (which will call this method for all sessions but also release some
     * additional resources).
     *
     * @return a future on the completion of the shutdown process.
     */
    public ShutdownFuture shutdown();

    /**
     * Returns the {@code Cluster} object this session is part of.
     *
     * @return the {@code Cluster} object this session is part of.
     */
    public Cluster getCluster();
}