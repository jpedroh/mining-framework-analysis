/**
 * Copyright (C) 2009-2013 enstratius, Inc.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.dasein.cloud.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.dasein.cloud.Taggable;


/**
 * Represents a virtual load balancer operating in a cloud. Load balancers have an address/virtual IP (VIP) to which
 * public traffic is routed over one or more public ports. The address or VIP is based on what kind of addressing
 * the cloud has (DNS/CNAME-based vs IP based).  Endpoints (aka real IPs) may
 * be either IP addresses or virtual machines. One or more listeners indicate how the traffic on the public port
 * is routed over to the endpoints.
 *
 * @author George Reese
 */
public class LoadBalancer implements Networkable , Taggable {
    /**
     * Constructs a load balancer with the minimally acceptable data set.
     *
     * @param ownerId
     * 		the account number that owns this load balancer
     * @param regionId
     * 		the region ID of the region in which the load balancer operates
     * @param lbId
     * 		the unique ID of the load balancer in the target cloud
     * @param state
     * 		the current operational state for the load balancer
     * @param name
     * 		the name of the load balancer
     * @param description
     * 		a user-friendly description of the load balancer
     * @param addressType
     * 		what kind of address is represented by the load balancer address
     * @param address
     * 		the load balancer CNAME, IPv4, or IPv6 address
     * @param publicPorts
     * 		one or more public ports on which the load balancer is listening
     * @return a load balancer instance representing the specified state
     */
    public static LoadBalancer getInstance(@Nonnull
    String ownerId, @Nonnull
    String regionId, @Nonnull
    String lbId, @Nonnull
    LoadBalancerState state, @Nonnull
    String name, @Nonnull
    String description, @Nonnull
    LoadBalancerAddressType addressType, @Nonnull
    String address, @Nonnull
    int... publicPorts) {
        assert publicPorts.length > 0;
        return new LoadBalancer(ownerId, regionId, lbId, state, name, description, addressType, address, publicPorts);
    }

    private String                  address;

    private LoadBalancerAddressType addressType;

    private long                    creationTimestamp;

    private LoadBalancerState       currentState;

    private String                  description;

    private ArrayList<LbListener> listeners;

    private String                  name;

    private String[]                providerDataCenterIds;

    private String                  providerLoadBalancerId;

    private String                  providerOwnerId;

    private String                  providerRegionId;

    private String[]                providerServerIds;

    private int[]                   publicPorts;

    private IPVersion[]             supportedTraffic;

    private Map<String, String> tags;

    /**
     * Constructs a load balancer object with no data.
     *
     * @deprecated Use the factory methods that construct fully-formed load balancers
     */
    public LoadBalancer() {
    }

    private LoadBalancer(@Nonnull
    String ownerId, @Nonnull
    String regionId, @Nonnull
    String lbId, @Nonnull
    LoadBalancerState state, @Nonnull
    String name, @Nonnull
    String description, @Nonnull
    LoadBalancerAddressType addressType, @Nonnull
    String address, @Nonnull
    int... publicPorts) {
        this.providerOwnerId = ownerId;
        this.providerRegionId = regionId;
        this.providerLoadBalancerId = lbId;
        this.currentState = state;
        this.name = name;
        this.description = description;
        this.address = address;
        this.addressType = addressType;
        this.publicPorts = publicPorts;
        this.creationTimestamp = 0L;
        this.supportedTraffic = new IPVersion[]{ IPVersion.IPV4 };
    }

    /**
     * Indicates the Unix timestamp in milliseconds when this load balancer was created in the cloud.
     *
     * @param timestamp
     * 		the unix timestamp in milliseconds when the load balancer was initially provisioned
     * @return this
     */
    public LoadBalancer createdAt(@Nonnegative
    long timestamp) {
        assert timestamp > 0L;
        this.creationTimestamp = timestamp;
        return this;
    }

    @Override
    public boolean equals(@Nullable
    Object ob) {
        if (ob == null) {
            return false;
        }
        if (ob == this) {
            return true;
        }
        if (!getClass().getName().equals(ob.getClass().getName())) {
            return false;
        }
        LoadBalancer other = ((LoadBalancer) (ob));
        return (providerOwnerId.equals(other.providerOwnerId) && providerRegionId.equals(other.providerRegionId)) && providerLoadBalancerId.equals(other.providerLoadBalancerId);
    }

    /**
     * A load balancer address may be a CNAME or an IPv4 or IPv6 IP address depending on what the {@link #getAddressType()}
     * is for the load balancer and what kind of network traffic it supports.
     *
     * @return the address associated with this load balancer
     */
    @Nonnull
    public String getAddress() {
        return address;
    }

    /**
     *
     *
     * @return the type of address in the {@link #getAddress()} field
     */
    public LoadBalancerAddressType getAddressType() {
        return addressType;
    }

    /**
     *
     *
     * @return the Unix timestamp in milliseconds when this load balancer was first created
     */
    @Nonnegative
    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    /**
     *
     *
     * @return the current operational state of the load balancer
     */
    @Nonnull
    public LoadBalancerState getCurrentState() {
        return currentState;
    }

    /**
     *
     *
     * @return a user-friendly description for the load balancer
     */
    @Nonnull
    public String getDescription() {
        return description;
    }

    /**
     *
     *
     * @return the set of listeners associated with the load balancer
     */
    @Nonnull
    public LbListener[] getListeners() {
        return listeners == null ? new LbListener[0] : listeners.toArray(new LbListener[listeners.size()]);
    }

    /**
     *
     *
     * @return the name of the load balancer
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * For load balancers that are data center-aware, this method indicates the list of data centers across which
     * the load balancer will operate. If this method has no data centers, then the load balancer is NOT data center-aware.
     *
     * @return the list of IDs of data centers across which the load balancer is operating
     */
    @Nonnull
    public String[] getProviderDataCenterIds() {
        return providerDataCenterIds == null ? new String[0] : providerDataCenterIds;
    }

    /**
     *
     *
     * @return the unique ID for this load balancer in the cloud in which it operates
     */
    @Nonnull
    public String getProviderLoadBalancerId() {
        return providerLoadBalancerId;
    }

    /**
     * @return the account number for the account that owns this load balancer
     */
    @Nonnull
    public String getProviderOwnerId() {
        return providerOwnerId;
    }

    /**
     * @return the ID of the region in which this load balancer is provisioned
     */
    @Nonnull
    public String getProviderRegionId() {
        return providerRegionId;
    }

    /**
     * @return the list of public ports associated with this load balancer
     */
    @Nonnull
    public int[] getPublicPorts() {
        return publicPorts == null ? new int[0] : publicPorts;
    }

    /**
     * @return the IP version of the traffic allowed through this load balancer
     */
    @Nonnull
    public IPVersion[] getSupportedTraffic() {
        return supportedTraffic == null ? new IPVersion[]{ IPVersion.IPV4 } : supportedTraffic;
    }

    @Override
    public int hashCode() {
        return ((((providerOwnerId + ":") + providerRegionId) + ":") + providerLoadBalancerId).hashCode();
    }

    /**
     * Indicates the data centers across which this load balancer is operating for data-center aware load balancers. This
     * method should not be called for load balancers that are not data-center aware.
     *
     * @param dataCenterIds
     * 		one or more data center IDs indicating the data centers across which this load balancer is operating
     * @return this
     */
    @Nonnull
    public LoadBalancer operatingIn(@Nonnull
    String... dataCenterIds) {
        assert dataCenterIds.length > 0;
        this.providerDataCenterIds = dataCenterIds;
        return this;
    }

    /**
     * Fetches a single tag value.
     *
     * @param key
     * 		the key of the desired tag
     * @return the value for the desired tag if it exists
     */
    @Nullable
    public String getTag(@Nonnull
    String key) {
        return getTags().get(key);
    }

    @Override
    @Nonnull
    public Map<String, String> getTags() {
        if (tags == null) {
            tags = new HashMap<String, String>();
        }
        return tags;
    }

    @Override
    public void setTag(@Nonnull
    String key, @Nonnull
    String value) {
        if (tags == null) {
            tags = new HashMap<String, String>();
        }
        tags.put(key, value);
    }

    /**
     * Sets the tag set to the specified values.
     *
     * @param tags
     * 		the new tag set
     */
    public void setTags(@Nonnull
    Map<String, String> tags) {
        this.tags = tags;
    }

    /**
     * Indicates that this load balancer will support the specified kind of traffic.
     * @param traffic the traffic supported in this load balancer
     * @return this
     */
    public LoadBalancer supportingTraffic(@Nonnull
    IPVersion... traffic) {
        supportedTraffic = traffic;
        return this;
    }

    @Override
    @Nonnull
    public String toString() {
        return ((((name + " (") + address) + ") [#") + providerLoadBalancerId) + "]";
    }

    /**
     * Indicates one or more listeners to be associated with this load balancer. Calls to this method are additive.
     *
     * @param listeners
     * 		one or more listeners to associate with the load balancer
     * @return this
     */
    @Nonnull
    public LoadBalancer withListeners(@Nonnull
    LbListener... listeners) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<LbListener>();
        }
        Collections.addAll(this.listeners, listeners);
        return this;
    }

    /**
     * ***************************** DEPRECATED METHODS **********************************
     */
    /**
     * Lists the IDs of the virtual machine endpoints associated with this load balancer.
     *
     * @return the virtual machine IDs for the VM endpoints for the load balancer
     * @deprecated Use {@link LoadBalancerSupport#listEndpoints(String)} to get all endpoints
     */
    @Nonnull
    public String[] getProviderServerIds() {
        return providerServerIds == null ? new String[0] : providerServerIds;
    }

    /**
     * Sets the address associated with this load balancer.
     *
     * @param address
     * 		the address associated with the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setAddress(@Nonnull
    String address) {
        this.address = address;
    }

    /**
     * Sets the address type for the address associated with the load balancer
     *
     * @param addressType
     * 		the address type for the address associated with the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setAddressType(@Nonnull
    LoadBalancerAddressType addressType) {
        this.addressType = addressType;
    }

    /**
     * Sets the creation timestamp.
     * @param creationTimestamp the Unix timestamp in milliseconds when this load balancer was first created in the cloud
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setCreationTimestamp(@Nonnegative
    long creationTimestamp) {
        assert creationTimestamp > (-1L);
        this.creationTimestamp = creationTimestamp;
    }

    /**
     * Sets the current operational state of the load balancer.
     * @param currentState the current load balancer state
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setCurrentState(@Nonnull
    LoadBalancerState currentState) {
        this.currentState = currentState;
    }

    /**
     * Sets a user-friendly description for the load balancer.
     *
     * @param description
     * 		a user-friendly description of the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the listeners associated with this load balancer.
     *
     * @param listeners
     * 		the list of listeners associated with the load balancer
     * @deprecated Use the factory methods to construct a load balancer and the {@link #withListeners(LbListener...)} method
     */
    public void setListeners(@Nonnull
    LbListener... listeners) {
        withListeners(listeners);
    }

    /**
     * Sets the load balancer name.
     *
     * @param name
     * 		a name for the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setName(@Nonnull
    String name) {
        this.name = name;
    }

    /**
     * Sets the data centers across which this load balancer operates.
     *
     * @param providerDataCenterIds
     * 		the data centers across which this load balancer operates
     * @deprecated Use the factory methods to construct a load balancer along with the {@link #operatingIn(String...)} method
     */
    public void setProviderDataCenterIds(@Nonnull
    String[] providerDataCenterIds) {
        if (providerDataCenterIds.length > 0) {
            this.operatingIn(providerDataCenterIds);
        }
    }

    /**
     * Sets the unique ID for this load balancer.
     * @param providerLoadBalancerId the unique ID of the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setProviderLoadBalancerId(@Nonnull
    String providerLoadBalancerId) {
        this.providerLoadBalancerId = providerLoadBalancerId;
    }

    /**
     * Sets the account owner for this load balancer.
     * @param providerOwnerId the account owner of the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setProviderOwnerId(@Nonnull
    String providerOwnerId) {
        this.providerOwnerId = providerOwnerId;
    }

    /**
     * Sets the region for this load balancer.
     *
     * @param providerRegionId
     * 		the region of the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setProviderRegionId(@Nonnull
    String providerRegionId) {
        this.providerRegionId = providerRegionId;
    }

    /**
     * Sets the list of servers to which load balancer is routing traffic.
     *
     * @param providerServerIds
     * 		the list of server IDs to which the load balancer is routing traffic
     * @deprecated No equivalent method (load balancer does not store its endpoints any more)
     */
    public void setProviderServerIds(@Nonnull
    String[] providerServerIds) {
        this.providerServerIds = providerServerIds;
    }

    /**
     * Sets the public ports on which the load balancer is listening.
     *
     * @param publicPorts
     * 		the public ports associated with the load balancer
     * @deprecated Use the factory methods to construct a load balancer
     */
    public void setPublicPorts(@Nonnull
    int[] publicPorts) {
        this.publicPorts = publicPorts;
    }

    /**
     * Sets the supported traffic.
     *
     * @param supportedTraffic
     * 		the supported traffic containing at least one IP version
     * @deprecated Use the factory methods to construct an LB
     */
    public void setSupportedTraffic(@Nonnull
    IPVersion[] supportedTraffic) {
        assert supportedTraffic.length > 0;
        this.supportedTraffic = supportedTraffic;
    }
}