package com.datastax.driver.core;
import java.net.InetSocketAddress;

/**
 * Indicates that we've attempted to connect to a 1.2 C* node with version 2 of
 * the protocol.
 */
class UnsupportedProtocolVersionException extends Exception {
  private static final long serialVersionUID = 0;

  public final InetSocketAddress address;

  public final ProtocolVersion unsupportedVersion;

  public final ProtocolVersion serverVersion;

  public UnsupportedProtocolVersionException(InetSocketAddress address, ProtocolVersion unsupportedVersion, ProtocolVersion serverVersion) {
    super(String.format("[%s] Host %s does not support protocol version %s but %s", address, address, unsupportedVersion, serverVersion));
    this.address = address;
    this.unsupportedVersion = unsupportedVersion;
    this.serverVersion = serverVersion;
  }
}