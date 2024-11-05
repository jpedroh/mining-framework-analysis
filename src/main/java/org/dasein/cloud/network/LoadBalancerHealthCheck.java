package org.dasein.cloud.network;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancerHealthCheck implements Networkable {
  private String providerLBHealthCheckId;

  private String name;

  private String description;

  private List<String> providerLoadBalancerIds = new ArrayList<String>();

  private String host;

  private HCProtocol protocol;

  private int port;

  private String path;

  private int interval;

  private int timeout;

  private int unhealthyCount = 0;

  private int healthyCount = 0;

  public enum HCProtocol {
    HTTP,
    HTTPS,
    SSL,
    TCP
  }

  public static LoadBalancerHealthCheck getInstance(@Nonnull HCProtocol protocol, int port, @Nullable String path, int interval, int timeout, int healthyCount, int unhealthyCount) {
    return new LoadBalancerHealthCheck(null, null, null, null, protocol, port, path, interval, timeout, healthyCount, unhealthyCount);
  }

  public static LoadBalancerHealthCheck getInstance(@Nullable String providerLBHealthCheckId, @Nonnull HCProtocol protocol, int port, @Nullable String path, int interval, int timeout, int healthyCount, int unhealthyCount) {
    return new LoadBalancerHealthCheck(providerLBHealthCheckId, null, null, null, protocol, port, path, interval, timeout, healthyCount, unhealthyCount);
  }

  public static LoadBalancerHealthCheck getInstance(@Nullable String providerLBHealthCheckId, @Nonnull String name, @Nullable String description, @Nullable String host, @Nullable HCProtocol protocol, int port, @Nullable String path, int interval, int timeout, int healthyCount, int unhealthyCount) {
    return new LoadBalancerHealthCheck(providerLBHealthCheckId, name, description, host, protocol, port, path, interval, timeout, healthyCount, unhealthyCount);
  }

  private LoadBalancerHealthCheck(@Nullable String providerLBHealthCheckId, @Nullable String name, @Nullable String description, @Nullable String host, @Nullable HCProtocol protocol, int port, @Nullable String path, int interval, int timeout, int healthyCount, int unhealthyCount) {
    this.providerLBHealthCheckId = providerLBHealthCheckId;
    this.name = name;
    this.description = description;
    this.host = host;
    this.protocol = protocol;
    this.port = port;
    this.path = path;
    this.interval = interval;
    this.timeout = timeout;
    this.unhealthyCount = unhealthyCount;
    this.healthyCount = healthyCount;
  }

  public String getProviderLBHealthCheckId() {
    return providerLBHealthCheckId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getProviderLoadBalancerIds() {
    return providerLoadBalancerIds;
  }

  public void addProviderLoadBalancerId(String providerLoadBalancerId) {
    this.providerLoadBalancerIds.add(providerLoadBalancerId);
  }

  public void removeProviderLoadBalancerId(String providerLoadBalancerId) {
    this.providerLoadBalancerIds.remove(providerLoadBalancerId);
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public HCProtocol getProtocol() {
    return protocol;
  }

  public void setProtocol(HCProtocol protocol) {
    this.protocol = protocol;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getUnhealthyCount() {
    return unhealthyCount;
  }

  public void setUnhealthyCount(int unhealthyCount) {
    this.unhealthyCount = unhealthyCount;
  }

  public int getHealthyCount() {
    return healthyCount;
  }

  public void setHealthyCount(int healthyCount) {
    this.healthyCount = healthyCount;
  }
}