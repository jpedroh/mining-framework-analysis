/**
 * Copyright (C) 2009-2014 Dell, Inc.
 * See annotations for authorship information
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

import java.util.HashMap;
import java.util.Locale;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.dasein.cloud.*;
import org.dasein.cloud.identity.ServiceAction;


/**
 * Implements support for cloud load balancing services.
 * @author George Reese
 * @author Cameron Stokes
 * @version 2013.04 added Javadoc and did some refactoring
 * @version 2013.02 added support for health checks
 * @since unknown
 */
public interface LoadBalancerSupport extends AccessControlledService {
    static public final ServiceAction ANY                  = new ServiceAction("LB:ANY");

    static public final ServiceAction ADD_DATA_CENTERS     = new ServiceAction("LB:ADD_DC");

    static public final ServiceAction ADD_VMS              = new ServiceAction("LB:ADD_VM");

    static public final ServiceAction CREATE_LOAD_BALANCER = new ServiceAction("LB:CREATE_LOAD_BALANCER");

    static public final ServiceAction GET_LOAD_BALANCER    = new ServiceAction("LB:GET_LOAD_BALANCER");

    static public final ServiceAction LIST_LOAD_BALANCER   = new ServiceAction("LB:LIST_LOAD_BALANCER");

    static public final ServiceAction GET_LOAD_BALANCER_SERVER_HEALTH   = new ServiceAction("LB:GET_LOAD_BALANCER_SERVER_HEALTH");

    static public final ServiceAction REMOVE_DATA_CENTERS  = new ServiceAction("LB:REMOVE_DC");

    static public final ServiceAction REMOVE_VMS           = new ServiceAction("LB:REMOVE_VM");

    static public final ServiceAction REMOVE_LOAD_BALANCER = new ServiceAction("LB:REMOVE_LOAD_BALANCER");

    static public final ServiceAction CONFIGURE_HEALTH_CHECK = new ServiceAction("LB:CONFIGURE_HEALTH_CHECK");

    static public final ServiceAction SET_FIREWALLS        = new ServiceAction("LB:SET_FIREWALLS");

    static public final ServiceAction LIST_SSL_CERTIFICATES     = new ServiceAction("LB:LIST_SSL_CERTIFICATES");

    static public final ServiceAction GET_SSL_CERTIFICATE       = new ServiceAction("LB:GET_SSL_CERTIFICATE");

    static public final ServiceAction CREATE_SSL_CERTIFICATE    = new ServiceAction("LB:CREATE_SSL_CERTIFICATE");

    static public final ServiceAction DELETE_SSL_CERTIFICATE    = new ServiceAction("LB:DELETE_SSL_CERTIFICATE");

    static public final ServiceAction SET_LB_SSL_CERTIFICATE    = new ServiceAction("LB:SET_SSL_CERTIFICATE");

    /**
     * Adds one or more data centers to the list of data centers associated with the specified load balancer. This method
     * makes sense only if load balancers are not data center aware
     * @param toLoadBalancerId the load balancer to which data centers are being added
     * @param dataCenterIdsToAdd one or more data centers to add
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @throws OperationNotSupportedException this load balancer is not data-center aware
     */
    public abstract void addDataCenters(@Nonnull
    String toLoadBalancerId, @Nonnull
    String... dataCenterIdsToAdd) throws CloudException, InternalException;

    /**
     * Adds one or more IP address endpoints to the load balancer resource pool.
     * @param toLoadBalancerId the load balancer to which the endpoints are being added
     * @param ipAddresses the addresses to be added
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @throws OperationNotSupportedException this load balancer does not support IP endpoints, or does not support adding them post-create
     */
    public abstract void addIPEndpoints(@Nonnull
    String toLoadBalancerId, @Nonnull
    String... ipAddresses) throws CloudException, InternalException;

    /**
     * Adds one or more virtual machine endpoints to the load balancer resource pool.
     * @param toLoadBalancerId the load balancer to which the endpoints are being added
     * @param serverIdsToAdd the IDs of the virtual machines to be added
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @throws OperationNotSupportedException this load balancer does not support VM endpoints, or does not support adding them post-create
     */
    public abstract void addServers(@Nonnull
    String toLoadBalancerId, @Nonnull
    String... serverIdsToAdd) throws CloudException, InternalException;

    /**
     * Provisions a new cloud load balancer in the target region based on the specified creation options.
     * @param options the options for creating the new load balancer
     * @return the unique ID of the new load balancer
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     */
    @Nonnull
    public abstract String createLoadBalancer(@Nonnull
    LoadBalancerCreateOptions options) throws CloudException, InternalException;

    /**
     * Indicates the type of load balancer supported by this cloud.
     * @return the load balancer type
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated use {@link LoadBalancerCapabilities#getAddressType()}
     */
    @Deprecated
    @Nonnull
    public abstract LoadBalancerAddressType getAddressType() throws CloudException, InternalException;

    /**
     * Provides access to meta-data about load balancer capabilities in the current region of this cloud.
     * @return a description of the features supported by this region of this cloud
     * @throws InternalException an error occurred within the Dasein Cloud API implementation
     * @throws CloudException an error occurred within the cloud provider
     */
    @Nonnull
    public abstract LoadBalancerCapabilities getCapabilities() throws CloudException, InternalException;

    /**
     * Fetches the details for the load balancer associated with the specified load balancer ID from the cloud.
     * @param loadBalancerId the unique ID of the desired load balancer
     * @return the matching load balancer details if there is a matching load balancer in the cloud
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     */
    @Nullable
    public abstract LoadBalancer getLoadBalancer(@Nonnull
    String loadBalancerId) throws CloudException, InternalException;

    /**
     * Lists all servers along with their load balancer health status for the specified load balancer.
     * @param loadBalancerId the unique ID of the load balancer being checked
     * @return all servers with their current health status
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated Use {@link #listEndpoints(String)}
     */
    @Nonnull
    public abstract Iterable<LoadBalancerServer> getLoadBalancerServerHealth(@Nonnull
    String loadBalancerId) throws CloudException, InternalException;

    /**
     * Lists the desired servers along with their load balancer health status for the specified load balancer.
     * @param loadBalancerId the unique ID of the load balancer being checked
     * @param serverIdsToCheck a list of server IDs for which the current health is being sought
     * @return all servers matching the specified server IDs with their current health status
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated Use {@link #listEndpoints(String, LbEndpointType, String...)}
     */
    @Nonnull
    public abstract Iterable<LoadBalancerServer> getLoadBalancerServerHealth(@Nonnull
    String loadBalancerId, @Nonnull
    String... serverIdsToCheck) throws CloudException, InternalException;

    /**
     * @return the maximum number of public ports on which the load balancer can listen
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated use {@link LoadBalancerCapabilities#getMaxPublicPorts()}
     */
    @Deprecated
    @Nonnegative
    public abstract int getMaxPublicPorts() throws CloudException, InternalException;

    /**
     * Gives the cloud provider's term for a load balancer (for example, "ELB" in AWS).
     * @param locale the locale for which the term should be translated
     * @return the provider term for a load balancer
     * @deprecated use {@link LoadBalancerCapabilities#getProviderTermForLoadBalancer(java.util.Locale)}
     */
    @Deprecated
    public @Nonnull String getProviderTermForLoadBalancer(@Nonnull Locale locale);

    /**
     * Fetched the details of an SSL certificate associated with the given name.
     * @param certificateName the certificate name to search for.
     * @return server certificate name or null if no certificate exists with the given name.
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     */
    public @Nullable SSLCertificate getSSLCertificate(@Nonnull String certificateName) throws CloudException, InternalException;

    /**
     * @return the degree to which endpoints should or must be part of the load balancer creation process
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated use {@link LoadBalancerCapabilities#identifyEndpointsOnCreateRequirement()}
     */
    @Deprecated
    @Nonnull
    public abstract Requirement identifyEndpointsOnCreateRequirement() throws CloudException, InternalException;

    /**
     * Indicates the degree to which listeners should or must be specified when creating a load balancer.
     * @return the degree to which listeners must be specified during load balancer creation
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated use {@link LoadBalancerCapabilities#identifyListenersOnCreateRequirement()}
     */
    @Deprecated
    @Nonnull
    public abstract Requirement identifyListenersOnCreateRequirement() throws CloudException, InternalException;

    /**
     * @return whether or not you are expected to provide an address as part of the create process or one gets assigned by the provider
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated use {@link LoadBalancerCapabilities#isAddressAssignedByProvider()}
     */
    @Deprecated
    public abstract boolean isAddressAssignedByProvider() throws CloudException, InternalException;

    /**
     * Indicates whether or not VM endpoints for this load balancer should be constrained to specific data centers in
     * its region. It should be false for load balancers handling non-VM endpoints or load balancers that are free
     * to balance across any data center. When a load balancer is data-center limited, the load balancer tries to balance
     * traffic equally across the data centers. It is therefore up to you to try to keep the data centers configured
     * with equal capacity.
     * @return whether or not VM endpoints are constrained to specific data centers associated with the load balancer
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated use {@link LoadBalancerCapabilities#isDataCenterLimited()}
     */
    @Deprecated
    public abstract boolean isDataCenterLimited() throws CloudException, InternalException;

    /**
     * Indicates whether the current account has access to load balancer services in the current region.
     * @return true if the current account has access to load balancer services in the current region
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     */
    public abstract boolean isSubscribed() throws CloudException, InternalException;

    /**
     * Lists the endpoints associated with the specified load balancer.
     * @param forLoadBalancerId the load balancer for which you are listing the balanced endpoints
     * @return a list of balanced endpoints
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     */
    @Nonnull
    public abstract Iterable<LoadBalancerEndpoint> listEndpoints(@Nonnull
    String forLoadBalancerId) throws CloudException, InternalException;

    /**
     * Lists the endpoints associated with the specified load balancer that match the desired values.
     * @param forLoadBalancerId the load balancer for which you are listing the balanced endpoints
     * @param type the type of endpoint being sought
     * @param endpoints the VM ID or addresses of the endpoints being sought
     * @return a list of balanced endpoints matching the desired criteria
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     */
    @Nonnull
    public abstract Iterable<LoadBalancerEndpoint> listEndpoints(@Nonnull
    String forLoadBalancerId, @Nonnull
    LbEndpointType type, @Nonnull
    String... endpoints) throws CloudException, InternalException;

    /**
     * Lists the load balancers in the current region.
     * @return a list of load balancers in the current region
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     */
    @Nonnull
    public abstract Iterable<LoadBalancer> listLoadBalancers() throws CloudException, InternalException;

    /**
     * Lists the current status of all load balancers associated with the account in the current region.
     * @return the status of all load balancers associated with the account in the current region
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     */
    @Nonnull
    public abstract Iterable<ResourceStatus> listLoadBalancerStatus() throws CloudException, InternalException;

    /**
     * Lists all available server certificates associated with the account in the current region.
     * @return all server certificates associated with the account in the current region. Certificates may not contain
     * all fields, e.g. a body. To get all information use {@link #getSSLCertificate(String)}.
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     */
    public @Nonnull Iterable<SSLCertificate> listSSLCertificates() throws CloudException, InternalException;

    /**
     * Lists the load balancing algorithms from which you can choose when setting up a load balancer listener.
     * @return a list of one or more supported load balancing algorithms
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated use {@link LoadBalancerCapabilities#listSupportedAlgorithms()}
     */
    @Deprecated
    @Nonnull
    public abstract Iterable<LbAlgorithm> listSupportedAlgorithms() throws CloudException, InternalException;

    /**
     * Describes what kind of endpoints may be added to a load balancer.
     * @return a list of one or more supported endpoint types
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated use {@link LoadBalancerCapabilities#listSupportedEndpointTypes()}
     */
    @Nonnull
    public abstract Iterable<LbEndpointType> listSupportedEndpointTypes() throws CloudException, InternalException;

    /**
     * Lists all IP protocol versions supported for load balancers in this cloud.
     * @return a list of supported versions
     * @throws CloudException an error occurred checking support for IP versions with the cloud provider
     * @throws InternalException a local error occurred preparing the supported version
     * @deprecated use {@link LoadBalancerCapabilities#listSupportedIPVersions()}
     */
    @Deprecated
    @Nonnull
    public abstract Iterable<IPVersion> listSupportedIPVersions() throws CloudException, InternalException;

    /**
     * Lists the various options for session stickiness with load balancers in this cloud.
     * @return a list of one or more load balancer persistence options for session stickiness
     * @throws CloudException an error occurred checking support for IP versions with the cloud provider
     * @throws InternalException a local error occurred preparing the supported version
     * @deprecated use {@link LoadBalancerCapabilities#listSupportedPersistenceOptions()}
     */
    @Deprecated
    @Nonnull
    public abstract Iterable<LbPersistence> listSupportedPersistenceOptions() throws CloudException, InternalException;

    /**
     * Lists the network protocols supported for load balancer listeners.
     * @return a list of one or more supported network protocols for load balancing
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @deprecated use {@link LoadBalancerCapabilities#listSupportedProtocols()}
     */
    @Deprecated
    @Nonnull
    public abstract Iterable<LbProtocol> listSupportedProtocols() throws CloudException, InternalException;

    /**
     * Removes one or more data centers from the rotation behind this load balancer
     * @param fromLoadBalancerId the load balancer to remove data centers from
     * @param dataCenterIdsToRemove the data centers to be removed
     * @throws CloudException an error occurred while communicating with the cloud provider
     * @throws InternalException an error occurred within the Dasein Cloud implementation
     * @throws OperationNotSupportedException this load balancer is not data-center aware
     */
    public abstract void removeDataCenters(@Nonnull
    String fromLoadBalancerId, @Nonnull
    String... dataCenterIdsToRemove) throws CloudException, InternalException;

    /**
     * Removes one or more IP endpoints from the load balancer resource pool.
     * @param fromLoadBalancerId the load balancer from which the endpoints are being removed
     * @param addresses the IP addresses to be removed
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @throws OperationNotSupportedException this load balancer does not support IP endpoints, or does not support removing them post-create
     */
    public abstract void removeIPEndpoints(@Nonnull
    String fromLoadBalancerId, @Nonnull
    String... addresses) throws CloudException, InternalException;

    /**
     * Removes the specified load balancer from the cloud.
     * @param loadBalancerId the load balancer to be removed
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     */
    public abstract void removeLoadBalancer(@Nonnull
    String loadBalancerId) throws CloudException, InternalException;

    /**
     * Removes a given server certificate from the account in current region.
     * <strong>Note:</strong> be sure to first unset this certificate from any load balancer it is used by.
     * @param certificateName name of the certificate to remove
     * @throws CloudException an error occurred with the cloud provider, certificate does not exist by given name etc
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     */
    public void removeSSLCertificate(@Nonnull String certificateName) throws CloudException, InternalException;

    /**
     * Removes one or more virtual machine endpoints from the load balancer resource pool.
     * @param fromLoadBalancerId the load balancer from which the endpoints are being removed
     * @param serverIdsToRemove the IDs of the virtual machines to be removed
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @throws OperationNotSupportedException this load balancer does not support VM endpoints, or does not support removing them post-create
     */
    public abstract void removeServers(@Nonnull
    String fromLoadBalancerId, @Nonnull
    String... serverIdsToRemove) throws CloudException, InternalException;

    /**
     * Assigns an SSL certificate to specified port of a load balancer.
     * @param options request options: load balancer name, port number and certificate ID.
     * @throws CloudException thrown if load balancer or certificate do not exist or other error occurs in the cloud.
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     */
    public void setSSLCertificate(@Nonnull SetLoadBalancerSSLCertificateOptions options) throws CloudException, InternalException;

    /**
     * Indicates whether or not endpoints may be added to or removed from a load balancer once the load balancer has been created.
     * @return true if you can modify the endpoints post-create
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated use {@link LoadBalancerCapabilities#supportsAddingEndpoints()}
     */
    @Deprecated
    public abstract boolean supportsAddingEndpoints() throws CloudException, InternalException;

    /**
     * Indicates whether or not the underlying cloud monitors the balanced endpoints and provides health status information.
     * @return true if monitoring is supported
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated use {@link LoadBalancerCapabilities#supportsMonitoring()}
     */
    @Deprecated
    public abstract boolean supportsMonitoring() throws CloudException, InternalException;

    /**
     * Indicates whether a single load balancer is limited to either IPv4 or IPv6 (false) or can support both IPv4 and
     * IPv6 traffic (true)
     * @return true if a load balancer can be configured to support simultaneous IPv4 and IPv6 traffic
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated use {@link LoadBalancerCapabilities#supportsMultipleTrafficTypes()}
     */
    @Deprecated
    public abstract boolean supportsMultipleTrafficTypes() throws CloudException, InternalException;

    /**
     * Creates a standalone LoadBalancerHealthCheck that can be attached to a LoadBalancer either at a later time
     * or on create of the LB.
     * @param name the name of the Health Check if required
     * @param description a friendly name for the Health Check
     * @param host an optional hostname that can be set as the target for the health check monitoring
     * @param protocol the protocol to be used for the health check monitoring
     * @param port the port to be used for the health check monitoring
     * @param path the path which is the target for the health check monitoring
     * @param interval how often to perform the health check
     * @param timeout timeout after which the health check request is considered a failure
     * @param healthyCount the number of consecutive successful requests before an unhealthy instance is marked as healthy
     * @param unhealthyCount the number of consecutive failed requests before a healthy instance is marked as unhealthy
     * @return the unique ID of the health check
     * @throws CloudException
     * @throws InternalException
     */
    public abstract LoadBalancerHealthCheck createLoadBalancerHealthCheck(@Nullable
    String name, @Nullable
    String description, @Nullable
    String host, @Nullable
    LoadBalancerHealthCheck.HCProtocol protocol, int port, @Nullable
    String path, @Nullable
    Double interval, @Nullable
    Double timeout, int healthyCount, int unhealthyCount) throws CloudException, InternalException;

    /**
     * Uploads a new server certificate associated with the account and current region.
     * @param options the details of the certificate to upload
     * @return details of created server certificate
     * @throws CloudException an error occurred with the cloud provider or request parameters were incorrect
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     */
    public SSLCertificate createSSLCertificate(@Nonnull SSLCertificateCreateOptions options) throws CloudException, InternalException;

    /**
     * Creates a standalone LoadBalancerHealthCheck that can be attached to a LoadBalancer either at a later time
     * or on create of the LB.
     * @param options the options for creating the health check
     * @return the unique ID of the health check
     */
    public abstract LoadBalancerHealthCheck createLoadBalancerHealthCheck(@Nonnull
    LBHealthCheckCreateOptions options) throws CloudException, InternalException;

    /**
     * Attaches an existing Health Check to an existing Load Balancer
     * @param providerLoadBalancerId the load balancer ID
     * @param providerLBHealthCheckId the health check ID
     * @throws CloudException
     * @throws InternalException
     */
    public abstract void attachHealthCheckToLoadBalancer(@Nonnull
    String providerLoadBalancerId, @Nonnull
    String providerLBHealthCheckId) throws CloudException, InternalException;

    /**
     * Gets the health state of the virtual machine(s) being monitored by the health check
     * @param providerLoadBalancerId the ID of the specific loadbalancer under which the instance(s) belong(s)
     * @param providerVirtualMachineId the ID of a specific virtual machine if required
     * @return Hashmap containing the virtual machine ID and it's corresponding health state
     */
    public abstract HashMap<String, String> getInstanceHealth(@Nonnull
    String providerLoadBalancerId, @Nullable
    String providerVirtualMachineId) throws CloudException, InternalException;

    /**
     * Removes a health check associated with a particular Load Balancer. Only certain clouds allow this operation
     * @param providerLoadBalancerId the ID of the Load Balancer that has the health check being removed
     * @throws CloudException
     * @throws InternalException
     */
    public abstract void removeLoadBalancerHealthCheck(@Nonnull
    String providerLoadBalancerId) throws CloudException, InternalException;

    /**
     * Indicates whether a health check can be created independantly of a load balancer
     * @return false if a health check can exist without having been assigned to a load balancer
     * @throws CloudException
     * @throws InternalException
     * @deprecated use {@link LoadBalancerCapabilities#healthCheckRequiresLoadBalancer()}
     */
    @Deprecated
    public abstract boolean healthCheckRequiresLoadBalancer() throws CloudException, InternalException;

    /********************************** DEPRECATED METHODS *************************************/
    /**
     * Creates a new load balancer matching the specified characteristics in the cloud.
     * @param name the name of the new load balancer
     * @param description a description for the new load balancer
     * @param addressId the address ID of the IP address, if any, for the new load balancer
     * @param dataCenterIds a list of data center IDs across which the load balancer will manage traffic
     * @param listeners the listeners to be established on create
     * @param serverIds a list of servers to be attached to the load balancer on create
     * @param subnetIds a list of subnets to be attached to the load balancer on create
     * @param type the load balancer type
     * @return the unique ID for the new load balancer
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated Use {@link #createLoadBalancer(LoadBalancerCreateOptions)}
     */
    @Nonnull
    public abstract String create(@Nonnull
    String name, @Nonnull
    String description, @Nullable
    String addressId, @Nullable
    String[] dataCenterIds, @Nullable
    LbListener[] listeners, @Nullable
    String[] serverIds, @Nullable
    String[] subnetIds, @Nullable
    LbType type) throws CloudException, InternalException;

    /**
     * Removes a load balancer.
     * @param loadBalancerId  the load balancer to be removed
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated Use {@link #removeLoadBalancer(String)}
     */
    public abstract void remove(@Nonnull
    String loadBalancerId) throws CloudException, InternalException;

    /**
     * @return <code>true</code> if at least one endpoint must be part of the load balancer creation process
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated Use {@link #identifyEndpointsOnCreateRequirement()}
     */
    public abstract boolean requiresListenerOnCreate() throws CloudException, InternalException;

    /**
     * @return <code>true</code> if you must provide at least one listener when creating a load balancer
     * @throws CloudException an error occurred with the cloud provider while performing this action
     * @throws InternalException an error occurred within the Dasein Cloud implementation while performing this action
     * @deprecated Use {@link #identifyListenersOnCreateRequirement()}
     */
    public abstract boolean requiresServerOnCreate() throws CloudException, InternalException;

    /**
     * Attaches an existing Load Balancer to an existing firewalls
     * @param providerLoadBalancerId the load balancer ID
     * @param firewallIds the firewalls
     * @throws CloudException
     * @throws InternalException
     */
    public void setFirewalls(@Nonnull String providerLoadBalancerId, @Nonnull String... firewallIds) throws CloudException, InternalException;
}