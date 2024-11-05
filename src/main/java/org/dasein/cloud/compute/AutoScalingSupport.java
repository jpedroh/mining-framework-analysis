package org.dasein.cloud.compute;
import org.dasein.cloud.*;
import org.dasein.cloud.identity.ServiceAction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface AutoScalingSupport extends AccessControlledService {
  static public final ServiceAction ANY = new ServiceAction("SCALING:ANY");

  static public final ServiceAction CREATE_LAUNCH_CONFIGURATION = new ServiceAction("SCALING:CREATE_LAUNCH_CONFIGURATION");

  static public final ServiceAction CREATE_SCALING_GROUP = new ServiceAction("SCALING:CREATE_SCALING_GROUP");

  static public final ServiceAction GET_LAUNCH_CONFIGURATION = new ServiceAction("SCALING:GET_LAUNCH_CONFIGURATION");

  static public final ServiceAction GET_SCALING_GROUP = new ServiceAction("SCALING:GET_SCALING_GROUP");

  static public final ServiceAction LIST_LAUNCH_CONFIGURATION = new ServiceAction("SCALING:LIST_LAUNCH_CONFIGURATION");

  static public final ServiceAction LIST_SCALING_GROUP = new ServiceAction("SCALING:LIST_SCALING_GROUP");

  static public final ServiceAction REMOVE_LAUNCH_CONFIGURATION = new ServiceAction("SCALING:REMOVE_LAUNCH_CONFIGURATION");

  static public final ServiceAction REMOVE_SCALING_GROUP = new ServiceAction("SCALING:REMOVE_SCALING_GROUP");

  static public final ServiceAction SET_CAPACITY = new ServiceAction("SCALING:SET_CAPACITY");

  static public final ServiceAction SET_SCALING_TRIGGER = new ServiceAction("SCALING:SET_SCALING_TRIGGER");

  static public final ServiceAction UPDATE_SCALING_GROUP = new ServiceAction("SCALING:UPDATE_SCALING_GROUP");

  static public final ServiceAction SUSPEND_AUTO_SCALING_GROUP = new ServiceAction("SCALING:SUSPEND_AUTO_SCALING_GROUP");

  static public final ServiceAction RESUME_AUTO_SCALING_GROUP = new ServiceAction("SCALING:RESUME_AUTO_SCALING_GROUP");

  static public final ServiceAction PUT_SCALING_POLICY = new ServiceAction("SCALING:PUT_SCALING_POLICY");

  static public final ServiceAction DELETE_SCALING_POLICY = new ServiceAction("SCALING:DELETE_SCALING_POLICY");

  static public final ServiceAction LIST_SCALING_POLICIES = new ServiceAction("SCALING:LIST_SCALING_POLICIES");

  public String createAutoScalingGroup(@Nonnull AutoScalingGroupOptions options) throws InternalException, CloudException;

  public @Deprecated String createAutoScalingGroup(@Nonnull String name, @Nonnull String launchConfigurationId, @Nonnull Integer minServers, @Nonnull Integer maxServers, @Nullable Integer cooldown, @Nullable String[] loadBalancerIds, @Nullable Integer desiredCapacity, @Nullable Integer healthCheckGracePeriod, @Nullable String healthCheckType, @Nullable String vpcZones, @Nullable String... dataCenterIds) throws InternalException, CloudException;

  public void updateAutoScalingGroup(String scalingGroupId, @Nullable String launchConfigurationId, @Nullable Integer minServers, @Nullable Integer maxServers, @Nullable Integer cooldown, @Nullable Integer desiredCapacity, @Nullable Integer healthCheckGracePeriod, @Nullable String healthCheckType, @Nullable String vpcZones, @Nullable String... zoneIds) throws InternalException, CloudException;

  public String createLaunchConfiguration(String name, String imageId, VirtualMachineProduct size, String keyPairName, String userData, String providerRoleId, Boolean detailedMonitoring, String... firewalls) throws InternalException, CloudException;

  public String createLaunchConfiguration(@Nonnull LaunchConfigurationCreateOptions options) throws InternalException, CloudException;

  public void deleteAutoScalingGroup(String providerAutoScalingGroupId) throws CloudException, InternalException;

  public void deleteAutoScalingGroup(@Nonnull AutoScalingGroupDeleteOptions options) throws CloudException, InternalException;

  public void deleteLaunchConfiguration(String providerLaunchConfigurationId) throws CloudException, InternalException;

  public LaunchConfiguration getLaunchConfiguration(String providerLaunchConfigurationId) throws CloudException, InternalException;

  public ScalingGroup getScalingGroup(String providerScalingGroupId) throws CloudException, InternalException;

  public boolean isSubscribed() throws CloudException, InternalException;

  public void suspendAutoScaling(String providerScalingGroupId, String[] processesToSuspend) throws CloudException, InternalException;

  public void resumeAutoScaling(String providerScalingGroupId, String[] processesToResume) throws CloudException, InternalException;

  public String updateScalingPolicy(String policyName, String adjustmentType, String autoScalingGroupName, Integer cooldown, Integer minAdjustmentStep, Integer scalingAdjustment) throws CloudException, InternalException;

  public void deleteScalingPolicy(String policyName, String autoScalingGroupName) throws CloudException, InternalException;

  public Collection<ScalingPolicy> listScalingPolicies(@Nullable String autoScalingGroupName) throws CloudException, InternalException;

  public ScalingPolicy getScalingPolicy(@Nonnull String policyName) throws CloudException, InternalException;

  public Iterable<ResourceStatus> listScalingGroupStatus() throws CloudException, InternalException;

  public Collection<ScalingGroup> listScalingGroups(AutoScalingGroupFilterOptions options) throws CloudException, InternalException;

  public Collection<ScalingGroup> listScalingGroups() throws CloudException, InternalException;

  public Iterable<ResourceStatus> listLaunchConfigurationStatus() throws CloudException, InternalException;

  public Collection<LaunchConfiguration> listLaunchConfigurations() throws CloudException, InternalException;

  public void setDesiredCapacity(String scalingGroupId, int capacity) throws CloudException, InternalException;

  public String setTrigger(String name, String scalingGroupId, String statistic, String unitOfMeasure, String metric, int periodInSeconds, double lowerThreshold, double upperThreshold, int lowerIncrement, boolean lowerIncrementAbsolute, int upperIncrement, boolean upperIncrementAbsolute, int breachDuration) throws InternalException, CloudException;

  public void setNotificationConfig(@Nonnull String scalingGroupId, @Nonnull String topic, @Nonnull String[] notificationTypes) throws CloudException, InternalException;

  public Collection<AutoScalingGroupNotificationConfig> listNotificationConfigs(final String[] scalingGroupIds) throws CloudException, InternalException;

  public void updateTags(@Nonnull String[] providerScalingGroupIds, @Nonnull AutoScalingTag... tags) throws CloudException, InternalException;

  public void removeTags(@Nonnull String[] providerScalingGroupIds, @Nonnull AutoScalingTag... tags) throws CloudException, InternalException;

  public void setTags(@Nonnull String providerScalingGroupId, @Nonnull AutoScalingTag... tags) throws CloudException, InternalException;

  public void setTags(@Nonnull String[] providerScalingGroupIds, @Nonnull AutoScalingTag... tags) throws CloudException, InternalException;
}