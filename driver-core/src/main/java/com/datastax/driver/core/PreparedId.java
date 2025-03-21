package com.datastax.driver.core;

/**
 * Identifies a PreparedStatement.
 */
public class PreparedId {
  final MD5Digest id;

  final ColumnDefinitions metadata;

  final ColumnDefinitions resultSetMetadata;

  final int[] routingKeyIndexes;

  final ProtocolVersion protocolVersion;

  PreparedId(MD5Digest id, ColumnDefinitions metadata, ColumnDefinitions resultSetMetadata, int[] routingKeyIndexes, ProtocolVersion protocolVersion) {
    this.id = id;
    this.metadata = metadata;
    this.resultSetMetadata = resultSetMetadata;
    this.routingKeyIndexes = routingKeyIndexes;
    this.protocolVersion = protocolVersion;
  }
}