package org.dasein.cloud.compute;
import org.dasein.cloud.*;
import org.dasein.cloud.identity.ServiceAction;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Locale;

@SuppressWarnings(value = { "UnusedDeclaration" }) public interface VirtualMachineSupport extends AccessControlledService {
  static public final ServiceAction ANY = new ServiceAction("VM:ANY");

  static public final ServiceAction BOOT = new ServiceAction("VM:BOOT");

  static public final ServiceAction CLONE = new ServiceAction("VM:CLONE");

  static public final ServiceAction CREATE_VM = new ServiceAction("VM:CREATE_VM");

  static public final ServiceAction GET_VM = new ServiceAction("VM:GET_VM");

  static public final ServiceAction LIST_VM = new ServiceAction("VM:LIST_VM");

  static public final ServiceAction PAUSE = new ServiceAction("VM:PAUSE");

  static public final ServiceAction REBOOT = new ServiceAction("VM:REBOOT");

  static public final ServiceAction REMOVE_VM = new ServiceAction("VM:REMOVE_VM");

  static public final ServiceAction TOGGLE_ANALYTICS = new ServiceAction("VM:TOGGLE_ANALYTICS");

  static public final ServiceAction VIEW_ANALYTICS = new ServiceAction("VM:VIEW_ANALYTICS");

  static public final ServiceAction VIEW_CONSOLE = new ServiceAction("VM:VIEW_CONSOLE");

  public VirtualMachine alterVirtualMachine(@Nonnull String vmId, @Nonnull VMScalingOptions options) throws InternalException, CloudException;

  public abstract VirtualMachine modifyInstance(@Nonnull String vmId, @Nonnull String[] firewalls) throws InternalException, CloudException;

  public void cancelSpotDataFeedSubscription() throws CloudException, InternalException;

  public @Nonnull VirtualMachine clone(@Nonnull String vmId, @Nonnull String intoDcId, @Nonnull String name, @Nonnull String description, boolean powerOn, @Nullable String... firewallIds) throws InternalException, CloudException;

  public void disableAnalytics(@Nonnull String vmId) throws InternalException, CloudException;

  public void enableAnalytics(@Nonnull String vmId) throws InternalException, CloudException;

  public void enableSpotDataFeedSubscription(String bucketName) throws CloudException, InternalException;

  public @Nonnull VirtualMachineCapabilities getCapabilities() throws InternalException, CloudException;

  public @Nullable String getPassword(@Nonnull String vmId) throws InternalException, CloudException;

  public @Nullable String getUserData(@Nonnull String vmId) throws InternalException, CloudException;

  public @Nonnull String getConsoleOutput(@Nonnull String vmId) throws InternalException, CloudException;

  public @Nullable VirtualMachineProduct getProduct(@Nonnull String productId) throws InternalException, CloudException;

  public @Nullable VirtualMachine getVirtualMachine(@Nonnull String vmId) throws InternalException, CloudException;

  public @Nonnull VmStatistics getVMStatistics(@Nonnull String vmId, @Nonnegative long from, @Nonnegative long to) throws InternalException, CloudException;

  public @Nonnull Iterable<VmStatistics> getVMStatisticsForPeriod(@Nonnull String vmId, @Nonnegative long from, @Nonnegative long to) throws InternalException, CloudException;

  public @Nullable Iterable<VirtualMachineStatus> getVMStatus(@Nullable String... vmIds) throws InternalException, CloudException;

  public @Nullable Iterable<VirtualMachineStatus> getVMStatus(@Nullable VmStatusFilterOptions filterOptions) throws InternalException, CloudException;

  public boolean isSubscribed() throws CloudException, InternalException;

  public @Nonnull VirtualMachine launch(@Nonnull VMLaunchOptions withLaunchOptions) throws CloudException, InternalException;

  public @Nonnull VirtualMachine launch(@Nonnull String fromMachineImageId, @Nonnull VirtualMachineProduct product, @Nonnull String dataCenterId, @Nonnull String name, @Nonnull String description, @Nullable String withKeypairId, @Nullable String inVlanId, boolean withAnalytics, boolean asSandbox, @Nullable String... firewallIds) throws InternalException, CloudException;

  public @Nonnull VirtualMachine launch(@Nonnull String fromMachineImageId, @Nonnull VirtualMachineProduct product, @Nonnull String dataCenterId, @Nonnull String name, @Nonnull String description, @Nullable String withKeypairId, @Nullable String inVlanId, boolean withAnalytics, boolean asSandbox, @Nullable String[] firewallIds, @Nullable Tag... tags) throws InternalException, CloudException;

  public @Nonnull Iterable<String> launchMany(@Nonnull VMLaunchOptions withLaunchOptions, @Nonnegative int count) throws CloudException, InternalException;

  public @Nonnull Iterable<String> listFirewalls(@Nonnull String vmId) throws InternalException, CloudException;

  public Iterable<VirtualMachineProduct> listProducts(Architecture architecture) throws InternalException, CloudException;

  public @Nonnull Iterable<ResourceStatus> listVirtualMachineStatus() throws InternalException, CloudException;

  public @Nonnull Iterable<VirtualMachine> listVirtualMachines() throws InternalException, CloudException;

  public @Nonnull Iterable<VirtualMachine> listVirtualMachines(@Nullable VMFilterOptions options) throws InternalException, CloudException;

  public void pause(@Nonnull String vmId) throws InternalException, CloudException;

  public void reboot(@Nonnull String vmId) throws CloudException, InternalException;

  public void resume(@Nonnull String vmId) throws CloudException, InternalException;

  public void start(@Nonnull String vmId) throws InternalException, CloudException;

  public void stop(@Nonnull String vmId) throws InternalException, CloudException;

  public void stop(@Nonnull String vmId, boolean force) throws InternalException, CloudException;

  public void suspend(@Nonnull String vmId) throws CloudException, InternalException;

  public void terminate(@Nonnull String vmId) throws InternalException, CloudException;

  public void terminate(@Nonnull String vmId, @Nullable String explanation) throws InternalException, CloudException;

  public void unpause(@Nonnull String vmId) throws CloudException, InternalException;

  public void updateTags(@Nonnull String vmId, @Nonnull Tag... tags) throws CloudException, InternalException;

  public void updateTags(@Nonnull String[] vmIds, @Nonnull Tag... tags) throws CloudException, InternalException;

  public void updateTags(@Nonnull String vmId, boolean asynchronous, @Nonnull Tag... tags) throws CloudException, InternalException;

  public void updateTags(@Nonnull String[] vmIds, boolean asynchronous, @Nonnull Tag... tags) throws CloudException, InternalException;

  public void setTags(@Nonnull String vmId, @Nonnull Tag... tags) throws CloudException, InternalException;

  public void setTags(@Nonnull String[] vmIds, @Nonnull Tag... tags) throws CloudException, InternalException;

  public void removeTags(@Nonnull String vmId, @Nonnull Tag... tags) throws CloudException, InternalException;

  public void removeTags(@Nonnull String[] vmIds, @Nonnull Tag... tags) throws CloudException, InternalException;

  @Deprecated public @Nullable VMScalingCapabilities describeVerticalScalingCapabilities() throws CloudException, InternalException;

  @Deprecated public @Nonnegative int getCostFactor(@Nonnull VmState state) throws InternalException, CloudException;

  @Deprecated public int getMaximumVirtualMachineCount() throws CloudException, InternalException;

  @Deprecated public @Nonnull String getProviderTermForServer(@Nonnull Locale locale);

  @Deprecated public @Nonnull Requirement identifyImageRequirement(@Nonnull ImageClass cls) throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyPasswordRequirement() throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyPasswordRequirement(Platform platform) throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyRootVolumeRequirement() throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyShellKeyRequirement() throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyShellKeyRequirement(Platform platform) throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyStaticIPRequirement() throws CloudException, InternalException;

  @Deprecated public @Nonnull Requirement identifyVlanRequirement() throws CloudException, InternalException;

  @Deprecated public boolean isAPITerminationPreventable() throws CloudException, InternalException;

  @Deprecated public boolean isBasicAnalyticsSupported() throws CloudException, InternalException;

  @Deprecated public boolean isExtendedAnalyticsSupported() throws CloudException, InternalException;

  @Deprecated public boolean isUserDataSupported() throws CloudException, InternalException;

  @Deprecated public Iterable<Architecture> listSupportedArchitectures() throws InternalException, CloudException;

  @Deprecated public boolean supportsAnalytics() throws CloudException, InternalException;

  @Deprecated public boolean supportsPauseUnpause(@Nonnull VirtualMachine vm);

  @Deprecated public boolean supportsStartStop(@Nonnull VirtualMachine vm);

  @Deprecated public boolean supportsSuspendResume(@Nonnull VirtualMachine vm);

  public void cancelSpotVirtualMachineRequest(String providerSpotVirtualMachineRequestID) throws CloudException, InternalException;

  public @Nonnull SpotVirtualMachineRequest createSpotVirtualMachineRequest(SpotVirtualMachineRequestCreateOptions options) throws CloudException, InternalException;

  public Iterable<VirtualMachineProduct> listProducts(VirtualMachineProductFilterOptions options) throws InternalException, CloudException;

  public Iterable<VirtualMachineProduct> listProducts(VirtualMachineProductFilterOptions options, Architecture architecture) throws InternalException, CloudException;

  public Iterable<SpotPriceHistory> listSpotPriceHistories(@Nullable SpotPriceHistoryFilterOptions options) throws CloudException, InternalException;

  public Iterable<SpotVirtualMachineRequest> listSpotVirtualMachineRequests(@Nullable SpotVirtualMachineRequestFilterOptions options) throws CloudException, InternalException;
}