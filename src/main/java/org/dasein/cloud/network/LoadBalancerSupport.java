package org.dasein.cloud.network;
import org.dasein.cloud.*;
import org.dasein.cloud.identity.ServiceAction;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;

public interface LoadBalancerSupport extends AccessControlledService {
  static public final ServiceAction ANY = new ServiceAction("LB:ANY");

  static public final ServiceAction ADD_DATA_CENTERS = new ServiceAction("LB:ADD_DC");

  static public final ServiceAction ADD_VMS = new ServiceAction("LB:ADD_VM");

  static public final ServiceAction CREATE_LOAD_BALANCER = new ServiceAction("LB:CREATE_LOAD_BALANCER");

  static public final ServiceAction GET_LOAD_BALANCER = new ServiceAction("LB:GET_LOAD_BALANCER");

  static public final ServiceAction LIST_LOAD_BALANCER = new ServiceAction("LB:LIST_LOAD_BALANCER");

  static public final ServiceAction GET_LOAD_BALANCER_SERVER_HEALTH = new ServiceAction("LB:GET_LOAD_BALANCER_SERVER_HEALTH");

  static public final ServiceAction REMOVE_DATA_CENTERS = new ServiceAction("LB:REMOVE_DC");

  static public final ServiceAction REMOVE_VMS = new ServiceAction("LB:REMOVE_VM");

  static public final ServiceAction REMOVE_LOAD_BALANCER = new ServiceAction("LB:REMOVE_LOAD_BALANCER");

  static public final ServiceAction CONFIGURE_HEALTH_CHECK = new ServiceAction("LB:CONFIGURE_HEALTH_CHECK");

  static public final ServiceAction LIST_SSL_CERTIFICATES = new ServiceAction("LB:LIST_SSL_CERTIFICATES");

  static public final ServiceAction GET_SSL_CERTIFICATE = new ServiceAction("LB:GET_SSL_CERTIFICATE");

  static public final ServiceAction CREATE_SSL_CERTIFICATE = new ServiceAction("LB:CREATE_SSL_CERTIFICATE");

  static public final ServiceAction DELETE_SSL_CERTIFICATE = new ServiceAction("LB:DELETE_SSL_CERTIFICATE");

  static public final ServiceAction SET_LB_SSL_CERTIFICATE = new ServiceAction("LB:SET_SSL_CERTIFICATE");

  static public final ServiceAction CLEATE_LOAD_BALANCER_LISTENERS = new ServiceAction("LB:CLEATE_LOAD_BALANCER_LISTENERS");

  static public final ServiceAction DELETE_LOAD_BALANCER_LISTENERS = new ServiceAction("LB:DELETE_LOAD_BALANCER_LISTENERS");

  static public final ServiceAction SET_FIREWALLS = new ServiceAction("LB:SET_FIREWALLS");

  static public final ServiceAction ATTACH_LB_TO_SUBNETS = new ServiceAction("LB:ATTACH_LB_TO_SUBNETS");

  static public final ServiceAction DETACH_LB_FROM_SUBNETS = new ServiceAction("LB:DETACH_LB_FROM_SUBNETS");

  static public final ServiceAction MODIFY_LB_ATTRIBUTES = new ServiceAction("LB:MODIFY_LB_ATTRIBUTES");

  static public final ServiceAction DESCRIBE_LOADBALANCER_ATTRIBUTES = new ServiceAction("LB:DESCRIBE_LOADBALANCER_ATTRIBUTES");

  public void addListeners(@Nonnull String toLoadBalancerId, @Nullable LbListener[] listeners) throws CloudException, InternalException;

  public void removeListeners(@Nonnull String toLoadBalancerId, @Nullable LbListener[] listeners) throws CloudException, InternalException;

  public void addDataCenters(@Nonnull String toLoadBalancerId, @Nonnull String... dataCenterIdsToAdd) throws CloudException, InternalException;

  public void addIPEndpoints(@Nonnull String toLoadBalancerId, @Nonnull String... ipAddresses) throws CloudException, InternalException;

  public void addServers(@Nonnull String toLoadBalancerId, @Nonnull String... serverIdsToAdd) throws CloudException, InternalException;

  public @Nonnull String createLoadBalancer(@Nonnull LoadBalancerCreateOptions options) throws CloudException, InternalException;

  public @Nonnull LoadBalancerCapabilities getCapabilities() throws CloudException, InternalException;

  public @Nullable LoadBalancer getLoadBalancer(@Nonnull String loadBalancerId) throws CloudException, InternalException;

  public boolean isSubscribed() throws CloudException, InternalException;

  public @Nonnull Iterable<LoadBalancerEndpoint> listEndpoints(@Nonnull String forLoadBalancerId) throws CloudException, InternalException;

  public @Nonnull Iterable<LoadBalancerEndpoint> listEndpoints(@Nonnull String forLoadBalancerId, @Nonnull LbEndpointType type, @Nonnull String... endpoints) throws CloudException, InternalException;

  public @Nonnull Iterable<LoadBalancer> listLoadBalancers() throws CloudException, InternalException;

  public @Nonnull Iterable<ResourceStatus> listLoadBalancerStatus() throws CloudException, InternalException;

  public void removeDataCenters(@Nonnull String fromLoadBalancerId, @Nonnull String... dataCenterIdsToRemove) throws CloudException, InternalException;

  public void removeIPEndpoints(@Nonnull String fromLoadBalancerId, @Nonnull String... addresses) throws CloudException, InternalException;

  public void removeLoadBalancer(@Nonnull String loadBalancerId) throws CloudException, InternalException;

  public void removeServers(@Nonnull String fromLoadBalancerId, @Nonnull String... serverIdsToRemove) throws CloudException, InternalException;

  public LoadBalancerHealthCheck createLoadBalancerHealthCheck(@Nullable String name, @Nullable String description, @Nullable String host, @Nullable LoadBalancerHealthCheck.HCProtocol protocol, int port, @Nullable String path, int interval, int timeout, int healthyCount, int unhealthyCount) throws CloudException, InternalException;

  public LoadBalancerHealthCheck createLoadBalancerHealthCheck(@Nonnull HealthCheckOptions options) throws CloudException, InternalException;

  public LoadBalancerHealthCheck getLoadBalancerHealthCheck(@Nonnull String providerLBHealthCheckId, @Nullable String providerLoadBalancerId) throws CloudException, InternalException;

  public Iterable<LoadBalancerHealthCheck> listLBHealthChecks(@Nullable HealthCheckFilterOptions options) throws CloudException, InternalException;

  public void attachHealthCheckToLoadBalancer(@Nonnull String providerLoadBalancerId, @Nonnull String providerLBHealthCheckId) throws CloudException, InternalException;

  public LoadBalancerHealthCheck modifyHealthCheck(@Nonnull String providerLBHealthCheckId, @Nonnull HealthCheckOptions options) throws InternalException, CloudException;

  public void removeLoadBalancerHealthCheck(@Nonnull String providerLoadBalancerId) throws CloudException, InternalException;

  public SSLCertificate createSSLCertificate(@Nonnull SSLCertificateCreateOptions options) throws CloudException, InternalException;

  public @Nonnull Iterable<SSLCertificate> listSSLCertificates() throws CloudException, InternalException;

  public void removeSSLCertificate(@Nonnull String certificateName) throws CloudException, InternalException;

  public void setSSLCertificate(@Nonnull SetLoadBalancerSSLCertificateOptions options) throws CloudException, InternalException;

  public @Nullable SSLCertificate getSSLCertificate(@Nonnull String certificateName) throws CloudException, InternalException;

  public void setFirewalls(@Nonnull String providerLoadBalancerId, @Nonnull String... firewallIds) throws CloudException, InternalException;

  public void attachLoadBalancerToSubnets(@Nonnull String toLoadBalancerId, @Nonnull String... subnetIdsToAdd) throws CloudException, InternalException;

  public void detachLoadBalancerFromSubnets(@Nonnull String fromLoadBalancerId, @Nonnull String... subnetIdsToDelete) throws CloudException, InternalException;

  public void modifyLoadBalancerAttributes(@Nonnull String id, @Nonnull LbAttributesOptions options) throws CloudException, InternalException;

  public LbAttributesOptions getLoadBalancerAttributes(@Nonnull String id) throws CloudException, InternalException;

  @Deprecated public @Nonnull LoadBalancerAddressType getAddressType() throws CloudException, InternalException;

  @Deprecated public @Nonnull Iterable<LoadBalancerServer> getLoadBalancerServerHealth(@Nonnull String loadBalancerId) throws CloudException, InternalException;

  @Deprecated public @Nonnull Iterable<LoadBalancerServer> getLoadBalancerServerHealth(@Nonnull String loadBalancerId, @Nonnull String... serverIdsToCheck) throws CloudException, InternalException;

  @Deprecated public @Nonnegative int getMaxPublicPorts() throws CloudException, InternalException;

  @Deprecated public @Nonnull String getProviderTermForLoadBalancer(@Nonnull Locale locale);

  @Deprecated public @Nonnull Requirement identifyEndpointsOnCreateRequirement() throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyListenersOnCreateRequirement() throws CloudException, InternalException;

  @Deprecated public boolean isAddressAssignedByProvider() throws CloudException, InternalException;

  @Deprecated public boolean isDataCenterLimited() throws CloudException, InternalException;

  @Deprecated public @Nonnull Iterable<LbAlgorithm> listSupportedAlgorithms() throws CloudException, InternalException;

  @Deprecated public @Nonnull Iterable<LbEndpointType> listSupportedEndpointTypes() throws CloudException, InternalException;

  @Deprecated public @Nonnull Iterable<IPVersion> listSupportedIPVersions() throws CloudException, InternalException;

  @Deprecated public @Nonnull Iterable<LbPersistence> listSupportedPersistenceOptions() throws CloudException, InternalException;

  @Deprecated public @Nonnull Iterable<LbProtocol> listSupportedProtocols() throws CloudException, InternalException;

  @Deprecated public boolean supportsAddingEndpoints() throws CloudException, InternalException;

  @Deprecated public boolean supportsMonitoring() throws CloudException, InternalException;

  @Deprecated public boolean supportsMultipleTrafficTypes() throws CloudException, InternalException;

  @Deprecated public HashMap<String, String> getInstanceHealth(@Nonnull String providerLoadBalancerId, @Nullable String providerVirtualMachineId) throws CloudException, InternalException;

  @Deprecated public boolean healthCheckRequiresLoadBalancer() throws CloudException, InternalException;

  @Deprecated public @Nonnull String create(@Nonnull String name, @Nonnull String description, @Nullable String addressId, @Nullable String[] dataCenterIds, @Nullable LbListener[] listeners, @Nullable String[] serverIds, @Nullable String[] subnetIds, @Nullable LbType type) throws CloudException, InternalException;

  @Deprecated public void remove(@Nonnull String loadBalancerId) throws CloudException, InternalException;

  @Deprecated public boolean requiresListenerOnCreate() throws CloudException, InternalException;

  @Deprecated public boolean requiresServerOnCreate() throws CloudException, InternalException;

  public void detatchHealthCheck(String loadBalancerId, String heathcheckId) throws CloudException, InternalException;
}