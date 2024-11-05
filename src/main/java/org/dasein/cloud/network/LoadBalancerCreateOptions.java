package org.dasein.cloud.network;
import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.OperationNotSupportedException;
import org.dasein.cloud.Requirement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class LoadBalancerCreateOptions {
  static public LoadBalancerCreateOptions getInstance(@Nonnull String name, @Nonnull String description) {
    LoadBalancerCreateOptions options = new LoadBalancerCreateOptions();
    options.name = name;
    options.description = description;
    return options;
  }

  static public LoadBalancerCreateOptions getInstance(@Nonnull String name, @Nonnull String description, @Nullable String atIpAddressId) {
    LoadBalancerCreateOptions options = new LoadBalancerCreateOptions();
    options.name = name;
    options.description = description;
    options.providerIpAddressId = atIpAddressId;
    return options;
  }

  private List<LoadBalancerEndpoint> endpoints;

  private List<String> providerDataCenterIds;

  private List<String> providerSubnetIds;

  private ArrayList<String> firewallIds;

  private String providerIpAddressId;

  private String description;

  private List<LbListener> listeners;

  private Map<String, Object> metaData;

  private String name;

  private LbType type;

  private HealthCheckOptions healthCheckOptions;

  private Boolean crossDataCenter;

  private Boolean connectionDraining;

  private Integer connectionDrainingTimeout;

  private Integer idleConnectionTimeout;

  private LoadBalancerCreateOptions() {
  }

  public @Nonnull String build(@Nonnull CloudProvider provider) throws CloudException, InternalException {
    NetworkServices services = provider.getNetworkServices();
    if (services == null) {
      throw new OperationNotSupportedException("Network services are not supported in " + provider.getCloudName());
    }
    LoadBalancerSupport support = services.getLoadBalancerSupport();
    if (support == null) {
      throw new OperationNotSupportedException("Load balancers are not supported in " + provider.getCloudName());
    }
    if (support.getCapabilities().identifyListenersOnCreateRequirement().equals(Requirement.REQUIRED) && (listeners == null || listeners.isEmpty())) {
      throw new CloudException("You must specify at least one listener when creating a load balancer in " + provider.getCloudName());
    }
    if (support.getCapabilities().identifyEndpointsOnCreateRequirement().equals(Requirement.REQUIRED) && (endpoints == null || endpoints.isEmpty())) {
      throw new CloudException("You must specify at least one endpoint when creating a load balancer in " + provider.getCloudName());
    }
    if (support.getCapabilities().isDataCenterLimited() && (providerDataCenterIds == null || providerDataCenterIds.isEmpty())) {
      throw new CloudException("You must specify at least one data center when creating a load balancer in " + provider.getCloudName());
    }
    if (!support.getCapabilities().isAddressAssignedByProvider() && providerIpAddressId == null) {
      IpAddressSupport as = services.getIpAddressSupport();
      if (as != null) {
        for (IPVersion version : support.getCapabilities().listSupportedIPVersions()) {
          Iterator<IpAddress> addresses = as.listIpPool(version, true).iterator();
          if (addresses.hasNext()) {
            providerIpAddressId = addresses.next().getProviderIpAddressId();
            break;
          }
        }
        if (providerIpAddressId == null) {
          for (IPVersion version : support.getCapabilities().listSupportedIPVersions()) {
            if (as.getCapabilities().isRequestable(version)) {
              providerIpAddressId = as.request(version);
            }
          }
        }
      }
    }
    return support.createLoadBalancer(this);
  }

  public @Nonnull String getDescription() {
    return description;
  }

  public @Nonnull LoadBalancerEndpoint[] getEndpoints() {
    return (endpoints == null ? new LoadBalancerEndpoint[0] : endpoints.toArray(new LoadBalancerEndpoint[endpoints.size()]));
  }

  public @Nonnull LbListener[] getListeners() {
    return (listeners == null ? new LbListener[0] : listeners.toArray(new LbListener[listeners.size()]));
  }

  public @Nonnull Map<String, Object> getMetaData() {
    return (metaData == null ? new HashMap<String, Object>() : metaData);
  }

  public @Nonnull String getName() {
    return name;
  }

  public @Nonnull String[] getProviderDataCenterIds() {
    if (providerDataCenterIds == null) {
      return new String[0];
    }
    return providerDataCenterIds.toArray(new String[providerDataCenterIds.size()]);
  }

  public @Nonnull String[] getProviderSubnetIds() {
    if (providerSubnetIds == null) {
      return new String[0];
    }
    return providerSubnetIds.toArray(new String[providerSubnetIds.size()]);
  }

  public String[] getFirewallIds() {
    if (firewallIds == null) {
      return new String[0];
    }
    return firewallIds.toArray(new String[firewallIds.size()]);
  }

  public @Nullable String getProviderIpAddressId() {
    return providerIpAddressId;
  }

  public @Nullable LbType getType() {
    return type;
  }

  public @Nullable HealthCheckOptions getHealthCheckOptions() {
    return this.healthCheckOptions;
  }

  public Boolean getCrossDataCenter() {
    return crossDataCenter;
  }

  public Boolean getConnectionDraining() {
    return connectionDraining;
  }

  public Integer getConnectionDrainingTimeout() {
    return connectionDrainingTimeout;
  }

  public Integer getIdleConnectionTimeout() {
    return idleConnectionTimeout;
  }

  public @Nonnull LoadBalancerCreateOptions havingListeners(@Nonnull LbListener... listeners) {
    if (this.listeners == null) {
      this.listeners = new ArrayList<LbListener>();
    }
    Collections.addAll(this.listeners, listeners);
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions limitedTo(@Nonnull String... dataCenterIds) {
    if (providerDataCenterIds == null) {
      providerDataCenterIds = new ArrayList<String>();
    }
    Collections.addAll(providerDataCenterIds, dataCenterIds);
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withProviderSubnetIds(@Nonnull String... providerSubnetIds) {
    if (this.providerSubnetIds == null) {
      this.providerSubnetIds = new ArrayList<String>();
    }
    Collections.addAll(this.providerSubnetIds, providerSubnetIds);
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withFirewalls(@Nonnull String... firewallIds) {
    if (this.firewallIds == null) {
      this.firewallIds = new ArrayList<String>();
    }
    Collections.addAll(this.firewallIds, firewallIds);
    return this;
  }

  @Override public @Nonnull String toString() {
    return ("[" + name + " - " + providerIpAddressId + " - " + listeners + "-" + endpoints + "]");
  }

  public @Nonnull LoadBalancerCreateOptions withIpAddresses(@Nonnull String... ipAddresses) {
    if (endpoints == null) {
      endpoints = new ArrayList<LoadBalancerEndpoint>();
    }
    for (String ipAddress : ipAddresses) {
      endpoints.add(LoadBalancerEndpoint.getInstance(LbEndpointType.IP, ipAddress, LbEndpointState.ACTIVE));
    }
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withVirtualMachines(@Nonnull String... virtualMachineIds) {
    if (endpoints == null) {
      endpoints = new ArrayList<LoadBalancerEndpoint>();
    }
    for (String virtualMachineId : virtualMachineIds) {
      endpoints.add(LoadBalancerEndpoint.getInstance(LbEndpointType.VM, virtualMachineId, LbEndpointState.ACTIVE));
    }
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withMetaData(@Nonnull Map<String, Object> metaData) {
    if (this.metaData == null) {
      this.metaData = new HashMap<String, Object>();
    }
    this.metaData.putAll(metaData);
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions asType(@Nullable LbType type) {
    this.type = type;
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withHealthCheckOptions(@Nullable HealthCheckOptions options) {
    this.healthCheckOptions = options;
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withConnectionDrainingTimeout(Integer connectionDrainingTimeout) {
    this.connectionDrainingTimeout = connectionDrainingTimeout;
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withIdleConnectionTimeout(Integer idleConnectionTimeout) {
    this.idleConnectionTimeout = idleConnectionTimeout;
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withConnectionDraining(Boolean connectionDraining) {
    this.connectionDraining = connectionDraining;
    return this;
  }

  public @Nonnull LoadBalancerCreateOptions withCrossDataCenter(Boolean crossDataCenter) {
    this.crossDataCenter = crossDataCenter;
    return this;
  }
}