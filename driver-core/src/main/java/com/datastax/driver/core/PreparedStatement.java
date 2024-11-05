package com.datastax.driver.core;
import java.nio.ByteBuffer;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import com.datastax.driver.core.policies.RetryPolicy;

public interface PreparedStatement {
  public ColumnDefinitions getVariables();

  public BoundStatement bind(Object... values);

  public BoundStatement bind();

  public PreparedStatement setRoutingKey(ByteBuffer routingKey);

  public PreparedStatement setRoutingKey(ByteBuffer... routingKeyComponents);

  public ByteBuffer getRoutingKey();

  public PreparedStatement setConsistencyLevel(ConsistencyLevel consistency);

  public ConsistencyLevel getConsistencyLevel();

  public PreparedStatement setSerialConsistencyLevel(ConsistencyLevel serialConsistency);

  public ConsistencyLevel getSerialConsistencyLevel();

  public String getQueryString();

  public String getQueryKeyspace();

  public PreparedStatement enableTracing();

  public PreparedStatement disableTracing();

  public boolean isTracing();

  public PreparedStatement setRetryPolicy(RetryPolicy policy);

  public RetryPolicy getRetryPolicy();

  public PreparedId getPreparedId();
}