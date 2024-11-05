package org.dasein.cloud.network;
import org.dasein.cloud.Taggable;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import org.dasein.cloud.VisibleScope;

public class LoadBalancer implements Networkable, Taggable {
  static public LoadBalancer getInstance(@Nonnull String ownerId, @Nonnull String regionId, @Nonnull String lbId, @Nonnull LoadBalancerState state, @Nonnull String name, @Nonnull String description, @Nonnull LoadBalancerAddressType addressType, @Nonnull String address, @Nonnull int... publicPorts) {
    return new LoadBalancer(ownerId, regionId, lbId, state, name, description, addressType, address, publicPorts);
  }

  static public LoadBalancer getInstance(@Nonnull String ownerId, @Nonnull String regionId, @Nonnull String lbId, @Nonnull LoadBalancerState state, @Nonnull String name, @Nonnull String description, @Nonnull LbType type, @Nonnull LoadBalancerAddressType addressType, @Nonnull String address, @Nonnull int... publicPorts) {
    LoadBalancer lb = new LoadBalancer(ownerId, regionId, lbId, state, name, description, addressType, address, publicPorts);
    lb.setType(type);
    return lb;
  }

  static public LoadBalancer getInstance(@Nonnull String ownerId, @Nonnull String regionId, @Nonnull String lbId, @Nonnull LoadBalancerState state, @Nonnull String name, @Nonnull String description, @Nonnull LbType type, @Nonnull LoadBalancerAddressType addressType, @Nonnull String address, @Nonnull String providerLBHealthCheckId, @Nonnull int... publicPorts) {
    LoadBalancer lb = new LoadBalancer(ownerId, regionId, lbId, state, name, description, addressType, address, publicPorts);
    lb.setType(type);
    lb.setProviderLBHealthCheckId(providerLBHealthCheckId);
    return lb;
  }

  private String address;

  private LoadBalancerAddressType addressType;

  private long creationTimestamp;

  private LoadBalancerState currentState;

  private String description;

  private LbType type;

  private ArrayList<LbListener> listeners;

  private String name;

  private String[] providerDataCenterIds;

  private String providerLoadBalancerId;

  private String providerOwnerId;

  private String providerRegionId;

  private String[] providerServerIds;

  private ArrayList<String> providerSubnetIds;

  private int[] publicPorts;

  private IPVersion[] supportedTraffic;

  private Map<String, String> tags;

  private String providerLBHealthCheckId;

  private String[] providerFirewallIds;

  private LoadBalancerHealthCheck healthCheck;

  public LoadBalancer() {
  }

  private LoadBalancer(@Nonnull String ownerId, @Nonnull String regionId, @Nonnull String lbId, @Nonnull LoadBalancerState state, @Nonnull String name, @Nonnull String description, @Nonnull LoadBalancerAddressType addressType, @Nonnull String address, @Nonnull int... publicPorts) {
    this.providerOwnerId = ownerId;
    this.providerRegionId = regionId;
    this.providerLoadBalancerId = lbId;
    this.currentState = state;
    this.name = name;
    this.description = description;
    this.type = type;
    this.address = address;
    this.addressType = addressType;
    this.publicPorts = publicPorts;
    this.creationTimestamp = 0L;
    this.supportedTraffic = new IPVersion[] { IPVersion.IPV4 };
  }

  public LoadBalancer createdAt(@Nonnegative long timestamp) {
    assert (timestamp > 0L);
    this.creationTimestamp = timestamp;
    return this;
  }

  @Override public boolean equals(@Nullable Object ob) {
    if (ob == null) {
      return false;
    }
    if (ob == this) {
      return true;
    }
    if (!getClass().getName().equals(ob.getClass().getName())) {
      return false;
    }
    LoadBalancer other = (LoadBalancer) ob;
    return (providerOwnerId.equals(other.providerOwnerId) && providerRegionId.equals(other.providerRegionId) && providerLoadBalancerId.equals(other.providerLoadBalancerId));
  }

  public @Nonnull String getAddress() {
    return address;
  }

  public LoadBalancerAddressType getAddressType() {
    return addressType;
  }

  public @Nonnegative long getCreationTimestamp() {
    return creationTimestamp;
  }

  public @Nonnull LoadBalancerState getCurrentState() {
    return currentState;
  }

  public @Nonnull String getDescription() {
    return description;
  }

  public LbType getType() {
    return type;
  }

  public @Nonnull LbListener[] getListeners() {
    return (listeners == null ? new LbListener[0] : listeners.toArray(new LbListener[listeners.size()]));
  }

  public @Nonnull String getName() {
    return name;
  }

  public @Nonnull String[] getProviderDataCenterIds() {
    return (providerDataCenterIds == null ? new String[0] : providerDataCenterIds);
  }

  public @Nonnull String getProviderLoadBalancerId() {
    return providerLoadBalancerId;
  }

  public @Nonnull String getProviderOwnerId() {
    return providerOwnerId;
  }

  public @Nonnull String getProviderRegionId() {
    return providerRegionId;
  }

  public @Nonnull int[] getPublicPorts() {
    return (publicPorts == null ? new int[0] : publicPorts);
  }

  public @Nonnull IPVersion[] getSupportedTraffic() {
    return (supportedTraffic == null ? new IPVersion[] { IPVersion.IPV4 } : supportedTraffic);
  }

  public @Nullable String getProviderLBHealthCheckId() {
    return providerLBHealthCheckId;
  }

  @Override public int hashCode() {
    return (providerOwnerId + ":" + providerRegionId + ":" + providerLoadBalancerId).hashCode();
  }

  public @Nonnull LoadBalancer operatingIn(@Nonnull String... dataCenterIds) {
    assert (dataCenterIds.length > 0);
    this.providerDataCenterIds = dataCenterIds;
    return this;
  }

  public ArrayList<String> getProviderSubnetIds() {
    return providerSubnetIds;
  }

  public LoadBalancer withProviderSubnetIds(String... providerSubnetIds) {
    if (this.providerSubnetIds == null) {
      this.providerSubnetIds = new ArrayList<String>();
    }
    Collections.addAll(this.providerSubnetIds, providerSubnetIds);
    return this;
  }

  public @Nullable String getTag(@Nonnull String key) {
    return getTags().get(key);
  }

  @Override public @Nonnull Map<String, String> getTags() {
    if (tags == null) {
      tags = new HashMap<String, String>();
    }
    return tags;
  }

  @Override public void setTag(@Nonnull String key, @Nonnull String value) {
    if (tags == null) {
      tags = new HashMap<String, String>();
    }
    tags.put(key, value);
  }

  public void setTags(@Nonnull Map<String, String> tags) {
    this.tags = tags;
  }

  public String[] getProviderFirewallIds() {
    return providerFirewallIds;
  }

  public void setProviderFirewallIds(String[] providerFirewallIds) {
    this.providerFirewallIds = providerFirewallIds;
  }

  public LoadBalancer supportingTraffic(@Nonnull IPVersion... traffic) {
    supportedTraffic = traffic;
    return this;
  }

  @Override public @Nonnull String toString() {
    return (name + " (" + address + ") [#" + providerLoadBalancerId + "]");
  }

  public @Nonnull LoadBalancer withListeners(@Nonnull LbListener... listeners) {
    if (this.listeners == null) {
      this.listeners = new ArrayList<LbListener>();
    }
    Collections.addAll(this.listeners, listeners);
    return this;
  }

  public LoadBalancerHealthCheck getHealthCheck() {
    return healthCheck;
  }

  public void setHealthCheck(LoadBalancerHealthCheck healthCheck) {
    this.healthCheck = healthCheck;
  }

  public @Nonnull String[] getProviderServerIds() {
    return (providerServerIds == null ? new String[0] : providerServerIds);
  }

  public void setAddress(@Nonnull String address) {
    this.address = address;
  }

  public void setAddressType(@Nonnull LoadBalancerAddressType addressType) {
    this.addressType = addressType;
  }

  public void setCreationTimestamp(@Nonnegative long creationTimestamp) {
    assert (creationTimestamp > -1L);
    this.creationTimestamp = creationTimestamp;
  }

  public void setCurrentState(@Nonnull LoadBalancerState currentState) {
    this.currentState = currentState;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setType(LbType type) {
    this.type = type;
  }

  public void setProviderLBHealthCheckId(@Nonnull String providerLBHealthCheckId) {
    this.providerLBHealthCheckId = providerLBHealthCheckId;
  }

  public void setListeners(@Nonnull LbListener... listeners) {
    withListeners(listeners);
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  public void setProviderDataCenterIds(@Nonnull String[] providerDataCenterIds) {
    if (providerDataCenterIds.length > 0) {
      this.operatingIn(providerDataCenterIds);
    }
  }

  public void setProviderLoadBalancerId(@Nonnull String providerLoadBalancerId) {
    this.providerLoadBalancerId = providerLoadBalancerId;
  }

  public void setProviderOwnerId(@Nonnull String providerOwnerId) {
    this.providerOwnerId = providerOwnerId;
  }

  public void setProviderRegionId(@Nonnull String providerRegionId) {
    this.providerRegionId = providerRegionId;
  }

  public void setProviderServerIds(@Nonnull String[] providerServerIds) {
    this.providerServerIds = providerServerIds;
  }

  public void setPublicPorts(@Nonnull int[] publicPorts) {
    this.publicPorts = publicPorts;
  }

  public void setSupportedTraffic(@Nonnull IPVersion[] supportedTraffic) {
    assert (supportedTraffic.length > 0);
    this.supportedTraffic = supportedTraffic;
  }

  static public LoadBalancer getInstance(@Nonnull String ownerId, @Nonnull String regionId, @Nonnull String lbId, @Nonnull LoadBalancerState state, @Nonnull String name, @Nonnull String description, @Nonnull LbType type, @Nonnull LoadBalancerAddressType addressType, @Nonnull String address, @Nonnull String providerLBHealthCheckId, @Nonnull VisibleScope visibleScope, @Nonnull int... publicPorts) {
    LoadBalancer lb = new LoadBalancer(ownerId, regionId, lbId, state, name, description, addressType, address, visibleScope, publicPorts);
    lb.setType(type);
    lb.setProviderLBHealthCheckId(providerLBHealthCheckId);
    return lb;
  }

  private VisibleScope visibleScope;

  private LoadBalancer(@Nonnull String ownerId, @Nonnull String regionId, @Nonnull String lbId, @Nonnull LoadBalancerState state, @Nonnull String name, @Nonnull String description, @Nonnull LoadBalancerAddressType addressType, @Nonnull String address, @Nullable VisibleScope visibleScope, @Nonnull int... publicPorts) {
    this.providerOwnerId = ownerId;
    this.providerRegionId = regionId;
    this.providerLoadBalancerId = lbId;
    this.currentState = state;
    this.name = name;
    this.description = description;
    this.type = type;
    this.address = address;
    this.addressType = addressType;
    this.publicPorts = publicPorts;
    this.creationTimestamp = 0L;
    this.supportedTraffic = new IPVersion[] { IPVersion.IPV4 };
    this.visibleScope = visibleScope;
  }

  public @Nullable VisibleScope getVisibleScope() {
    return visibleScope;
  }
}