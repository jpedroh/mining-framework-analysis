package com.arcbees.analytics.server;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.arcbees.analytics.server.options.ServerOptionsCallback;
import com.arcbees.analytics.server.options.TrackerNameOptionsCallback;
import com.arcbees.analytics.shared.AnalyticsImpl;
import com.arcbees.analytics.shared.AnalyticsPlugin;
import com.arcbees.analytics.shared.GaAccount;
import com.arcbees.analytics.shared.HitType;
import com.arcbees.analytics.shared.options.AnalyticsOptions;
import com.arcbees.analytics.shared.options.CreateOptions;
import com.arcbees.analytics.shared.options.GeneralOptions;
import com.arcbees.analytics.shared.options.TimingOptions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton public class ServerAnalytics extends AnalyticsImpl {
  private static final Logger LOGGER = Logger.getLogger(ServerAnalytics.class.getName());

  private final Provider<ServerOptionsCallback> serverOptionsCallbackProvider;

  private final Map<String, Long> timingEvents = new HashMap<>();

  private final Map<String, String> trackerNames = new HashMap<>();

  @Inject ServerAnalytics(Provider<ServerOptionsCallback> serverOptionsCallbackProvider, @GaAccount String userAccount) {
    super(userAccount);
    this.serverOptionsCallbackProvider = serverOptionsCallbackProvider;
  }

  @Override public CreateOptions create(final String userAccount) {
    return new AnalyticsOptions(new TrackerNameOptionsCallback() {
      @Override public void onCallback(String trackerName) {
        trackerNames.put(trackerName, userAccount);
      }
    }).createOptions();
  }

  @Override public void enablePlugin(AnalyticsPlugin plugin) {
  }

  @Override public TimingOptions endTimingEvent(String trackerName, String timingCategory, String timingVariableName) {
    final String key = getTimingKey(timingCategory, timingVariableName);
    if (timingEvents.containsKey(key)) {
      return sendTiming(trackerName, timingCategory, timingVariableName, (int) (System.currentTimeMillis() - timingEvents.remove(key)));
    }
    LOGGER.severe("Timing Event Ended before it was started: " + key);
    return new AnalyticsOptions(new TrackerNameOptionsCallback() {
      @Override public void onCallback(String trackerName) {
      }
    }).timingOptions(timingCategory, timingVariableName, 0);
  }

  @Override public AnalyticsOptions send(String trackerName, HitType hitType) {
    final ServerOptionsCallback options = serverOptionsCallbackProvider.get();
    if (trackerName != null) {
      options.putText("tid", trackerNames.get(trackerName));
    }
    options.putText("hitType", hitType.getFieldName());
    return new AnalyticsOptions(options);
  }

  @Override public GeneralOptions setGlobalSettings() {
    return new AnalyticsOptions(new TrackerNameOptionsCallback() {
      @Override public void onCallback(String trackerName) {
      }
    }).generalOptions();
  }

  @Override public void startTimingEvent(String timingCategory, String timingVariableName) {
    timingEvents.put(getTimingKey(timingCategory, timingVariableName), System.currentTimeMillis());
  }
}